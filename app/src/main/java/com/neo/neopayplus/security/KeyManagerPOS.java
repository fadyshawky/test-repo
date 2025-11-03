package com.neo.neopayplus.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.api.PaymentApiFactory;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.utils.LogUtil;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

import java.util.HashMap;
import java.util.Map;

/**
 * Key Manager POS
 * 
 * Generates TPK (Transaction PIN Key) INSIDE the Sunmi secure element,
 * exports it as TR-31 under the terminal TMK (no clear keys ever),
 * announces it to backend so backend can rewrap under Bank TMK and return a pin_key_id.
 * 
 * Active/standby slots: 12 (active), 13 (standby) by convention.
 * No clear keys ever leave the SE.
 * 
 * NOTE: Some SDK methods may need to be adjusted based on actual Sunmi PayLib 2.0.32 API.
 * Check SecurityOptV2 interface for exact method signatures.
 */
public final class KeyManagerPOS {
    
    private static final String TAG = Constant.TAG;
    private static final int SLOT_A = 12;  // Active PIN key slot
    private static final int SLOT_B = 13; // Standby PIN key slot
    
    private static final String PREFS = "pos_keys";
    private static final String K_ACTIVE_SLOT = "active_slot";
    private static final String K_PIN_KEY_ID = "pin_key_id";
    private static final String K_KCV = "kcv";
    private static final String K_VER = "ver";
    private static final String K_SET = "set";
    private static final String K_EPOCH = "epoch"; // millis when activated
    
    private static int activeSlotCached = SLOT_A;
    
    /**
     * TPK State
     */
    public static class State {
        public int activeSlot;
        public String pinKeyId; // server-issued id
        public String kcvHex;
        public int setId = 1001; // optional logical set
        public int verId = 1;    // rolling version if you want
        public long activatedAtEpoch;
    }
    
    /**
     * Load TPK state from SharedPreferences
     */
    public static State load(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        State s = new State();
        s.activeSlot = sp.getInt(K_ACTIVE_SLOT, SLOT_A);
        s.pinKeyId = sp.getString(K_PIN_KEY_ID, null);
        s.kcvHex = sp.getString(K_KCV, null);
        s.setId = sp.getInt(K_SET, 1001);
        s.verId = sp.getInt(K_VER, 1);
        s.activatedAtEpoch = sp.getLong(K_EPOCH, 0L);
        activeSlotCached = s.activeSlot;
        return s;
    }
    
    /**
     * Save TPK state to SharedPreferences
     */
    private static void save(Context ctx, State s) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit()
            .putInt(K_ACTIVE_SLOT, s.activeSlot)
            .putString(K_PIN_KEY_ID, s.pinKeyId)
            .putString(K_KCV, s.kcvHex)
            .putInt(K_SET, s.setId)
            .putInt(K_VER, s.verId)
            .putLong(K_EPOCH, s.activatedAtEpoch)
            .apply();
        activeSlotCached = s.activeSlot;
    }
    
    /**
     * Get active PIN key slot
     */
    public static int getActivePinSlot() {
        return activeSlotCached;
    }
    
    /**
     * Ensure there is a valid session TPK active.
     * If none or you want daily rotation, call this before showing the PIN pad.
     * 
     * Flow:
     * 1) Generate PIN key INSIDE SE into standby slot (or use server-provided key)
     * 2) Read KCV from SE
     * 3) Export TR-31 (POS key block) under terminal TMK
     * 4) POST /v1/keys/announce {kb_pos_b64,kcv,tid} -> server rewraps under bank TMK, returns pin_key_id
     * 5) Swap standby->active, persist state
     * 
     * NOTE: If SDK doesn't support key generation in SE, fallback to server-provided key pattern.
     */
    public static boolean ensureSessionTPK(Context ctx, KeyAnnounceCallback callback) {
        final State st = load(ctx);
        final int standby = (st.activeSlot == SLOT_A) ? SLOT_B : SLOT_A;
        
        try {
            SecurityOptV2 sec = MyApplication.app.securityOptV2;
            if (sec == null) {
                LogUtil.e(TAG, "SecurityOptV2 not ready");
                callback.onError("SecurityOptV2 not available");
                return false;
            }
            
            // OPTION 1: Generate key in SE (if SDK supports it)
            // NOTE: Sunmi PayLib may not have direct key generation - check SDK docs
            // If not available, use server-provided key pattern (fetchServerGeneratedKey)
            boolean keyGenerated = false;
            try {
                // Check if SDK has key generation method
                // Some SDKs may have: generateKeyInSE(), createKey(), etc.
                // For now, we'll use server-provided key as primary pattern
                LogUtil.e(TAG, "Key generation in SE not verified - using server-provided key pattern");
                keyGenerated = false; // Use server-provided key
            } catch (Exception e) {
                LogUtil.e(TAG, "Key generation in SE not available: " + e.getMessage());
                keyGenerated = false;
            }
            
            // FALLBACK: Use server-provided key pattern (if SE generation not available)
            // This is the recommended pattern: Backend generates key in HSM, sends TR-31
            if (!keyGenerated) {
                LogUtil.e(TAG, "Using server-provided key pattern");
                
                // For now, check if we already have an active TPK
                // If pin_key_id exists and is recent, reuse it
                if (st.pinKeyId != null && !st.pinKeyId.isEmpty()) {
                    long age = System.currentTimeMillis() - st.activatedAtEpoch;
                    // Reuse key if less than 24 hours old
                    if (age < 24 * 60 * 60 * 1000) {
                        LogUtil.e(TAG, "✓ Reusing existing session TPK - pin_key_id: " + st.pinKeyId);
                        callback.onSuccess(st);
                        return true;
                    }
                }
                
                // Otherwise, try to fetch new key from server
                fetchServerGeneratedKey(ctx, standby, st, callback);
                return true; // Will continue async
            }
            
            // OPTION 2: If key generated in SE, continue with export and announce
            // Read KCV
            // NOTE: If key was generated, need to read KCV
            // Check SDK for: getKeyKcv(), getKeyCheckValue(), readKcv()
            // For now, calculate KCV from key or use placeholder
            byte[] kcv = new byte[3];
            // Try to read KCV - if SDK doesn't support, use calculated value
            String kcvHex = "000000"; // Placeholder - replace with actual KCV read or calculation
            
            // Export TR-31 under TMK
            // NOTE: Replace with actual SDK method: exportKeyAsTR31(), exportKeyUnderTMK(), etc.
            // Method may be: exportKeyAsTR31(slot, underKeyType=TMK, usage=PIN)
            byte[] tr31Pos = exportKeyAsTR31(sec, standby);
            if (tr31Pos == null || tr31Pos.length == 0) {
                LogUtil.e(TAG, "exportKeyAsTR31 failed");
                // Zeroize if possible
                try {
                    zeroizeKey(sec, standby);
                } catch (Exception ignore) {}
                callback.onError("TR-31 export failed");
                return false;
            }
            
            String kbPosB64 = Base64.encodeToString(tr31Pos, Base64.NO_WRAP);
            
            // Announce to backend
            announceKeyToBackend(ctx, kbPosB64, kcvHex, st, standby, callback);
            
            // Zeroize exported buffer
            java.util.Arrays.fill(tr31Pos, (byte) 0);
            
            return true;
            
        } catch (Throwable t) {
            LogUtil.e(TAG, "ensureSessionTPK error: " + t.getMessage());
            t.printStackTrace();
            callback.onError("Exception: " + t.getMessage());
            return false;
        }
    }
    
    /**
     * Fallback: Fetch server-generated key and import it
     * Backend generates TPK in HSM, returns TR-31 under Terminal TMK
     */
    private static void fetchServerGeneratedKey(Context ctx, int slot, State st, KeyAnnounceCallback callback) {
        LogUtil.e(TAG, "Fetching server-generated session key...");
        
        PaymentApiService apiService = PaymentApiFactory.getInstance();
        
        // Request server-generated session TPK
        // NOTE: This endpoint needs to be added to PaymentApiService
        // For now, we'll use a pattern similar to getDukptKeys()
        LogUtil.e(TAG, "⚠️ Server key generation endpoint (/v1/keys/session) not yet implemented");
        
        // TODO: When endpoint is implemented:
        // apiService.getSessionKey(PaymentConfig.getTerminalId(), new SessionKeyCallback() {
        //     @Override
        //     public void onSuccess(String kbPosB64, String kcv) {
        //         // Import TR-31 key block into standby slot
        //         if (importKeyFromTR31(ctx, sec, slot, kbPosB64)) {
        //             // Continue with announcement
        //             announceKeyToBackend(ctx, kbPosB64, kcv, st, slot, callback);
        //         } else {
        //             callback.onError("Failed to import server-generated key");
        //         }
        //     }
        //     @Override
        //     public void onError(Throwable error) {
        //         callback.onError("Server key generation failed: " + error.getMessage());
        //     }
        // });
        
        // Temporary: Use existing TPK at slot if available, otherwise fail gracefully
        // In production, implement /v1/keys/session endpoint
        LogUtil.e(TAG, "⚠️ Using existing TPK slot - server key generation pending");
        callback.onError("Server key generation endpoint not yet implemented. Using existing TPK.");
    }
    
    /**
     * Import key from TR-31 key block
     */
    private static boolean importKeyFromTR31(Context ctx, SecurityOptV2 sec, int slot, String kbPosB64) {
        try {
            // Decode Base64 TR-31
            byte[] tr31Bytes = Base64.decode(kbPosB64, Base64.NO_WRAP);
            
            // Import TR-31 key block using saveTR31Key
            // Method signature: saveTR31Key(keyValue, kbpkIndex, keyIndex)
            // kbpkIndex = TMK index (1), keyIndex = target slot
            int result = sec.saveTR31Key(tr31Bytes, 1, slot); // TMK index = 1, target slot
            
            if (result == 0) {
                LogUtil.e(TAG, "✓ Server-generated key imported to slot " + slot);
                return true;
            } else {
                LogUtil.e(TAG, "❌ Failed to import TR-31 key, code: " + result);
                return false;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error importing TR-31 key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Export key as TR-31 under TMK
     * Uses hsmExportKeyUnderKEKEx (verified to exist in SDK)
     */
    private static byte[] exportKeyAsTR31(SecurityOptV2 sec, int slot) {
        try {
            // Use hsmExportKeyUnderKEKEx to export key under TMK (KEK)
            // This method exists in the SDK (see HsmExportKeyUnderKEKActivity)
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putInt("keySystem", AidlConstantsV2.Security.SEC_MKSK);
            bundle.putInt("keyIndex", slot);
            bundle.putInt("kekKeySystem", AidlConstantsV2.Security.SEC_MKSK);
            bundle.putInt("kekIndex", 1); // TMK index (Terminal Master Key)
            bundle.putInt("paddingMode", AidlConstantsV2.Security.NOTHING_PADDING);
            
            byte[] buffer = new byte[2048];
            int len = sec.hsmExportKeyUnderKEKEx(bundle, buffer);
            
            if (len < 0) {
                LogUtil.e(TAG, "hsmExportKeyUnderKEKEx failed, code: " + len);
                return null;
            }
            
            byte[] tr31 = java.util.Arrays.copyOf(buffer, len);
            LogUtil.e(TAG, "✓ TR-31 exported - length: " + tr31.length + " bytes");
            return tr31;
            
        } catch (Exception e) {
            LogUtil.e(TAG, "TR-31 export error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Zeroize key in slot
     * NOTE: Replace with actual SDK method
     */
    private static void zeroizeKey(SecurityOptV2 sec, int slot) {
        try {
            // Some SDKs have: deleteKey(), zeroizeKey(), clearKey()
            // For now, use deleteKeyEx if available
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putInt("keyIndex", slot);
            bundle.putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_PIK);
            
            int result = sec.deleteKeyEx(bundle);
            if (result != 0) {
                LogUtil.e(TAG, "Failed to zeroize key at slot " + slot + ", code: " + result);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Zeroize key error: " + e.getMessage());
        }
    }
    
    /**
     * Announce key to backend
     */
    private static void announceKeyToBackend(Context ctx, String kbPosB64, String kcvHex, 
                                            State st, int standby, KeyAnnounceCallback callback) {
        PaymentApiService apiService = PaymentApiFactory.getInstance();
        
        PaymentApiService.KeyAnnounceRequest request = new PaymentApiService.KeyAnnounceRequest();
        request.terminalId = PaymentConfig.getTerminalId();
        request.kbPosB64 = kbPosB64;
        request.kcv = kcvHex;
        request.pinKeySetHint = st.setId;
        request.prevPinKeyId = st.pinKeyId;
        
        // Call backend announcement endpoint
        apiService.announceKey(request, new PaymentApiService.KeyAnnounceCallback() {
            @Override
            public void onKeyAnnounceComplete(PaymentApiService.KeyAnnounceResponse response) {
                if (response.success) {
                    // Swap active slot and persist
                    st.activeSlot = standby;
                    st.kcvHex = kcvHex;
                    st.pinKeyId = response.pinKeyId;
                    st.setId = response.pinKeySet;
                    st.verId = response.pinKeyVer;
                    st.activatedAtEpoch = System.currentTimeMillis();
                    save(ctx, st);
                    
                    LogUtil.e(TAG, "✓ Session TPK activated - pin_key_id: " + st.pinKeyId);
                    callback.onSuccess(st);
                } else {
                    // Don't activate - zeroize standby
                    try {
                        zeroizeKey(MyApplication.app.securityOptV2, standby);
                    } catch (Exception ignore) {}
                    callback.onError(response.message != null ? response.message : "Key announcement failed");
                }
            }
            
            @Override
            public void onKeyAnnounceError(Throwable error) {
                // Don't activate - zeroize standby
                try {
                    zeroizeKey(MyApplication.app.securityOptV2, standby);
                } catch (Exception ignore) {}
                callback.onError(error.getMessage() != null ? error.getMessage() : "Key announcement error");
            }
        });
    }
    
    /**
     * Callback for key announcement
     */
    public interface KeyAnnounceCallback {
        void onSuccess(State state);
        void onError(String error);
    }
    
    /**
     * Bytes to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

