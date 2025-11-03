package com.neo.neopayplus.security;

import android.os.Bundle;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

/**
 * Sunmi PayLib P2 Key Management Utility
 * 
 * This class provides comprehensive key management for Sunmi PayLib v2.0.32
 * following best practices for secure storage and EMV configuration.
 * 
 * Key Index Reference:
 * - MKSK Master Key (TMK): 0-9
 * - MKSK Working Keys (TPK/TAK/TDK): 0-199
 * - DUKPT Keys: 1100-1199
 * 
 * Initialization Order:
 * 1. securityOptV2.saveKeyDukpt() or saveKeyEx() - inject keys
 * 2. emvOptV2.setTermParamEx() - set terminal parameters
 * 3. emvOptV2.addAid() - load all scheme AIDs
 * 4. emvOptV2.addCapk() - load CAPKs
 * 5. Run transaction via emvOptV2.transactProcessEx()
 */
public class SunmiPayLibKeyManager {
    private static final String TAG = "SunmiPayLibKeyManager";
    
    // Key Index Constants (following reference guide)
    private static final int TMK_INDEX = 1;           // Terminal Master Key (MKSK system)
    private static final int TPK_INDEX = 12;          // Terminal PIN Key (for online PIN)
    private static final int TAK_INDEX = 13;          // Terminal MAC Key
    private static final int TDK_INDEX = 14;          // Terminal Data Key
    private static final int DUKPT_KEY_INDEX = 1100;  // DUKPT IPEK (for online PIN with DUKPT)
    
    private SecurityOptV2 securityOptV2;
    
    public SunmiPayLibKeyManager() {
        this.securityOptV2 = MyApplication.app.securityOptV2;
    }
    
    /**
     * Initialize MKSK (Master Key Set Key) system keys
     * This is the standard key system for most terminals
     * 
     * @param tmk Terminal Master Key (32 bytes for 3DES, typically provided by acquirer)
     * @param tpk Terminal PIN Key (32 bytes for 3DES, derived from TMK)
     * @param tak Terminal MAC Key (32 bytes for 3DES, derived from TMK)
     * @param tdk Terminal Data Key (32 bytes for 3DES, derived from TMK, optional)
     * @return true if all keys saved successfully
     */
    public boolean initMKSKKeys(byte[] tmk, byte[] tpk, byte[] tak, byte[] tdk) {
        try {
            if (securityOptV2 == null) {
                LogUtil.e(Constant.TAG, TAG + ": SecurityOptV2 not initialized");
                return false;
            }
            
            // Save Terminal Master Key (TMK)
            // TMK is typically encrypted under KEK for secure injection
            Bundle tmkBundle = new Bundle();
            tmkBundle.putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_TMK);
            tmkBundle.putInt("keyIndex", TMK_INDEX);
            tmkBundle.putByteArray("keyValue", tmk);
            // KCV (Key Check Value) - 3 bytes, typically calculated from key
            // If not provided, SDK will calculate automatically (null = auto-calculate)
            tmkBundle.putByteArray("checkValue", null);
            tmkBundle.putInt("encryptIndex", 0); // 0 = plaintext (or encrypt under KEK if needed)
            tmkBundle.putInt("keyAlgType", AidlConstantsV2.Security.KEY_ALG_TYPE_3DES);
            
            int result = securityOptV2.saveKeyEx(tmkBundle);
            if (result != 0) {
                LogUtil.e(Constant.TAG, TAG + ": Failed to save TMK, code: " + result);
                return false;
            }
            LogUtil.e(Constant.TAG, TAG + ": TMK saved successfully at index " + TMK_INDEX);
            
            // Save Terminal PIN Key (PIK/TPK) - encrypted under TMK
            Bundle tpkBundle = new Bundle();
            tpkBundle.putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_PIK);
            tpkBundle.putInt("keyIndex", TPK_INDEX);
            tpkBundle.putByteArray("keyValue", tpk);
            tpkBundle.putByteArray("checkValue", null); // Auto-calculate KCV
            tpkBundle.putInt("encryptIndex", TMK_INDEX); // Encrypted under TMK
            tpkBundle.putInt("keyAlgType", AidlConstantsV2.Security.KEY_ALG_TYPE_3DES);
            
            result = securityOptV2.saveKeyEx(tpkBundle);
            if (result != 0) {
                LogUtil.e(Constant.TAG, TAG + ": Failed to save PIK, code: " + result);
                return false;
            }
            LogUtil.e(Constant.TAG, TAG + ": PIK saved successfully at index " + TPK_INDEX);
            
            // Save Terminal MAC Key (MAK/TAK) - encrypted under TMK
            Bundle takBundle = new Bundle();
            takBundle.putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_MAK);
            takBundle.putInt("keyIndex", TAK_INDEX);
            takBundle.putByteArray("keyValue", tak);
            takBundle.putByteArray("checkValue", null);
            takBundle.putInt("encryptIndex", TMK_INDEX);
            takBundle.putInt("keyAlgType", AidlConstantsV2.Security.KEY_ALG_TYPE_3DES);
            
            result = securityOptV2.saveKeyEx(takBundle);
            if (result != 0) {
                LogUtil.e(Constant.TAG, TAG + ": Failed to save MAK, code: " + result);
                return false;
            }
            LogUtil.e(Constant.TAG, TAG + ": MAK saved successfully at index " + TAK_INDEX);
            
            // Save Terminal Data Key (TDK) - optional, for data encryption
            if (tdk != null && tdk.length == 32) {
                Bundle tdkBundle = new Bundle();
                tdkBundle.putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_TDK);
                tdkBundle.putInt("keyIndex", TDK_INDEX);
                tdkBundle.putByteArray("keyValue", tdk);
                tdkBundle.putByteArray("checkValue", null);
                tdkBundle.putInt("encryptIndex", TMK_INDEX);
                tdkBundle.putInt("keyAlgType", AidlConstantsV2.Security.KEY_ALG_TYPE_3DES);
                
                result = securityOptV2.saveKeyEx(tdkBundle);
                if (result != 0) {
                    LogUtil.e(Constant.TAG, TAG + ": Failed to save TDK, code: " + result);
                } else {
                    LogUtil.e(Constant.TAG, TAG + ": TDK saved successfully at index " + TDK_INDEX);
                }
            }
            
            LogUtil.e(Constant.TAG, TAG + ": MKSK key system initialized successfully");
            return true;
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, TAG + ": Exception initializing MKSK keys: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Initialize DUKPT (Derived Unique Key Per Transaction) keys for online PIN
     * DUKPT provides unique encryption keys for each transaction (required in some regions)
     * 
     * @param ipek Initial PIN Encryption Key (32 bytes for 3DES)
     * @param ksn Key Serial Number (10 bytes, unique per terminal)
     * @return true if DUKPT key saved successfully
     */
    public boolean initDUKPTKeys(byte[] ipek, byte[] ksn) {
        try {
            if (securityOptV2 == null) {
                LogUtil.e(Constant.TAG, TAG + ": SecurityOptV2 not initialized");
                return false;
            }
            
            if (ipek == null || ipek.length != 32) {
                LogUtil.e(Constant.TAG, TAG + ": Invalid IPEK length (expected 32 bytes)");
                return false;
            }
            
            if (ksn == null || ksn.length != 10) {
                LogUtil.e(Constant.TAG, TAG + ": Invalid KSN length (expected 10 bytes)");
                return false;
            }
            
            // Save DUKPT IPEK (Initial PIN Encryption Key)
            // Method signature: saveKeyDukpt(keyType, keyValue, checkValue, ksn, algType, keyIndex)
            int result = securityOptV2.saveKeyDukpt(
                AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK,  // keyType
                ipek,                                           // keyValue
                null,                                           // checkValue (auto-calculate)
                ksn,                                            // ksn
                AidlConstantsV2.Security.KEY_ALG_TYPE_3DES,     // algType
                DUKPT_KEY_INDEX                                 // keyIndex
            );
            if (result != 0) {
                LogUtil.e(Constant.TAG, TAG + ": Failed to save DUKPT IPEK, code: " + result);
                return false;
            }
            
            LogUtil.e(Constant.TAG, TAG + ": DUKPT IPEK saved successfully at index " + DUKPT_KEY_INDEX);
            
            // Get current KSN to verify
            byte[] currentKSN = new byte[10];
            result = securityOptV2.dukptCurrentKSN(DUKPT_KEY_INDEX, currentKSN);
            if (result == 0) {
                LogUtil.e(Constant.TAG, TAG + ": Current KSN: " + ByteUtil.bytes2HexStr(currentKSN));
            }
            
            return true;
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, TAG + ": Exception initializing DUKPT keys: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get current DUKPT KSN (Key Serial Number)
     * 
     * @param keyIndex DUKPT key index (1100-1199)
     * @return Current KSN (10 bytes) or null if error
     */
    public byte[] getCurrentKSN(int keyIndex) {
        try {
            if (securityOptV2 == null) {
                LogUtil.e(Constant.TAG, TAG + ": SecurityOptV2 not initialized");
                return null;
            }
            
            byte[] ksn = new byte[10];
            int result = securityOptV2.dukptCurrentKSN(keyIndex, ksn);
            if (result == 0) {
                return ksn;
            } else {
                LogUtil.e(Constant.TAG, TAG + ": Failed to get KSN, code: " + result);
                return null;
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, TAG + ": Exception getting KSN: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Encrypt data using DUKPT key
     * 
     * @param keyIndex DUKPT key index
     * @param dataIn Data to encrypt (8-byte blocks for 3DES)
     * @param mode Encryption mode (ECB, CBC, etc.)
     * @param iv Initialization vector (for CBC mode, 8 bytes for 3DES)
     * @return Encrypted data or null if error
     */
    public byte[] encryptWithDUKPT(int keyIndex, byte[] dataIn, int mode, byte[] iv) {
        try {
            if (securityOptV2 == null) {
                LogUtil.e(Constant.TAG, TAG + ": SecurityOptV2 not initialized");
                return null;
            }
            
            byte[] dataOut = new byte[dataIn.length];
            int result = securityOptV2.dataEncryptDukpt(keyIndex, dataIn, mode, iv, dataOut);
            if (result == 0) {
                return dataOut;
            } else {
                LogUtil.e(Constant.TAG, TAG + ": DUKPT encryption failed, code: " + result);
                return null;
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, TAG + ": Exception encrypting with DUKPT: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get PIN key index based on key system
     * 
     * @param useDUKPT true for DUKPT, false for MKSK
     * @return Key index for PIN operations
     */
    public static int getPINKeyIndex(boolean useDUKPT) {
        return useDUKPT ? DUKPT_KEY_INDEX : TPK_INDEX;
    }
    
}

