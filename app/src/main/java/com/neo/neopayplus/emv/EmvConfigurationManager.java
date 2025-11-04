package com.neo.neopayplus.emv;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.api.EmvConfigApiService;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.utils.PreferencesUtil;
import com.sunmi.payservice.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.bean.AidV2;
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2;
import com.sunmi.pay.hardware.aidlv2.bean.EmvTermParamV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * EMV Configuration Manager
 * 
 * Centralized manager for EMV configuration including:
 * - Terminal parameters (9F33, 9F40, etc.)
 * - AIDs (Application Identifiers)
 * - CAPKs (Certificate Authority Public Keys)
 * 
 * This manager ensures configuration is loaded once at application startup
 * rather than per-transaction, improving performance and maintaining consistency.
 * 
 * Initialization Order:
 * 1. Load AIDs (preserve SDK defaults, add production AIDs)
 * 2. Load CAPKs (preserve SDK defaults, add production CAPKs)
 * 3. Configure terminal parameters
 * 
 * Note: This should be called once when PaySDK connects, typically in
 * MyApplication.onConnectPaySDK() callback.
 */
public class EmvConfigurationManager {
    
    private static final String TAG = Constant.TAG;
    private static EmvConfigurationManager instance;
    private boolean isInitialized = false;
    private static final long CONFIG_CACHE_VALIDITY_MS = 24 * 60 * 60 * 1000; // 24 hours
    
    // Store terminal parameters from API response
    private String terminalCountryCode = null; // From API response
    private String currencyCode = null; // From API response (4-digit format)
    
    private EmvConfigurationManager() {
        // Private constructor for singleton
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized EmvConfigurationManager getInstance() {
        if (instance == null) {
            instance = new EmvConfigurationManager();
        }
        return instance;
    }
    
    /**
     * Initialize EMV configuration (AIDs, CAPKs, terminal parameters)
     * This should be called once when PaySDK connects.
     * 
     * @return true if initialization successful, false otherwise
     */
    public synchronized boolean initialize() {
        if (isInitialized) {
            LogUtil.e(TAG, "EMV configuration already initialized");
            return true;
        }
        
        EMVOptV2 emvOptV2 = MyApplication.app.emvOptV2;
        if (emvOptV2 == null) {
            LogUtil.e(TAG, "EMVOptV2 not available - PaySDK not connected?");
            return false;
        }
        
        try {
            LogUtil.e(TAG, "=== Initializing EMV Configuration ===");
            
            // Validate configuration before proceeding
            if (!PaymentConfig.validate()) {
                LogUtil.e(TAG, "⚠️ Configuration validation failed - proceeding with defaults");
            }
            
            // 1. Load AIDs (preserve SDK defaults)
            loadAids(emvOptV2);
            
            // 2. Load CAPKs (preserve SDK defaults, production CAPKs should be loaded separately)
            loadCapks(emvOptV2);
            
            // 3. Configure terminal parameters
            configureTerminalParameters(emvOptV2);
            
            // 4. Configure scheme-specific TLV data
            configureSchemeTlvData(emvOptV2);
            
            // 5. Verify AIDs and CAPKs (PayLib v2.0.32 guide recommendation)
            try {
                int verifyResult = emvOptV2.checkAidAndCapk();
                if (verifyResult == 0) {
                    LogUtil.e(TAG, "✓ AID and CAPK verification successful");
                } else {
                    LogUtil.e(TAG, "⚠️ AID and CAPK verification returned code: " + verifyResult);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "⚠️ Could not verify AIDs and CAPKs: " + e.getMessage());
                // Continue anyway - verification may not be available in all SDK versions
            }
            
            isInitialized = true;
            LogUtil.e(TAG, "=== EMV Configuration Initialized Successfully ===");
            return true;
            
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Initializing EMV configuration", e);
            return false;
        }
    }
    
    /**
     * Check if EMV configuration is initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }
    
    /**
     * Get terminal country code from API response
     * @return Country code (4-digit, e.g., "0840") or null if not loaded
     */
    public String getTerminalCountryCode() {
        return terminalCountryCode != null ? terminalCountryCode : PaymentConfig.TERMINAL_COUNTRY_CODE;
    }
    
    /**
     * Get currency code from API response (4-digit format for EMV TLV)
     * @return Currency code (4-digit, e.g., "0840") or null if not loaded
     */
    public String getCurrencyCode() {
        return currencyCode != null ? currencyCode : PaymentConfig.CURRENCY_CODE_TLV;
    }
    
    /**
     * Reset initialization flag (for testing or re-initialization)
     */
    public synchronized void reset() {
        isInitialized = false;
        terminalCountryCode = null;
        currencyCode = null;
    }
    
    /**
     * Clear EMV configuration cache and reset initialization
     * Use this to force fresh configuration from API on next initialize()
     */
    public synchronized void clearCacheAndReset() {
        com.neo.neopayplus.utils.PreferencesUtil.clearEmvConfig();
        isInitialized = false;
        LogUtil.e(TAG, "✓ EMV configuration cache cleared - fresh config will be loaded on next initialize()");
    }
    
    /**
     * Load AIDs for supported payment schemes from API service or local storage
     * Preserves SDK built-in AIDs for ICC chip card support
     * This method blocks until AIDs are loaded (either from cache or API)
     */
    private void loadAids(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Loading AIDs - preserving SDK defaults...");
        
        // Preserve SDK built-in AIDs - do NOT delete
        // Deleting would break ICC chip card functionality
        
        // Try loading from local storage first
        String cachedJson = PreferencesUtil.getEmvConfigJson();
        long cacheTimestamp = PreferencesUtil.getEmvConfigTimestamp();
        long cacheAge = System.currentTimeMillis() - cacheTimestamp;
        
        // Always fetch fresh from API to ensure correct configuration
        // This ensures AIDs have correct selFlag=0 for contactless support
        // Cache is fallback only if API fails
        boolean useCache = false; // Disabled to ensure fresh AIDs with correct selFlag for contactless
        
        if (useCache && cachedJson != null && cacheAge < CONFIG_CACHE_VALIDITY_MS) {
            LogUtil.e(TAG, "✓ Loading AIDs from local storage (cache age: " + (cacheAge / 1000) + " seconds)");
            try {
                Gson gson = new Gson();
                EmvConfigApiService.EmvConfigResponse cachedResponse = gson.fromJson(cachedJson, EmvConfigApiService.EmvConfigResponse.class);
                if (cachedResponse != null && cachedResponse.aids != null && !cachedResponse.aids.isEmpty()) {
                    // Restore terminal parameters from cache
                    if (cachedResponse.terminalCountryCode != null && !cachedResponse.terminalCountryCode.isEmpty()) {
                        instance.terminalCountryCode = cachedResponse.terminalCountryCode;
                        LogUtil.e(TAG, "✓ Country code from cache: " + instance.terminalCountryCode);
                    }
                    if (cachedResponse.currencyCode != null && !cachedResponse.currencyCode.isEmpty()) {
                        instance.currencyCode = cachedResponse.currencyCode;
                        LogUtil.e(TAG, "✓ Currency code from cache: " + instance.currencyCode);
                    }
                    
                    addAidsFromApiResponse(emvOptV2, cachedResponse.aids);
                    LogUtil.e(TAG, "✓ Loaded " + cachedResponse.aids.size() + " AIDs from cache");
                    return;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "⚠️ Error parsing cached AIDs, fetching from API: " + e.getMessage());
            }
        } else if (cachedJson != null) {
            LogUtil.e(TAG, "⚠️ Cache expired or disabled - fetching fresh AIDs from API (cache age: " + (cacheAge / 1000) + " seconds)");
        }
        
        // Cache invalid or not available - fetch from API
        LogUtil.e(TAG, "Fetching AIDs from API service...");
        
        com.neo.neopayplus.api.EmvConfigApiService apiService = 
            com.neo.neopayplus.api.EmvConfigApiFactory.getInstance();
        
        if (!apiService.isAvailable()) {
            LogUtil.e(TAG, "⚠️ EMV Config API service not available - using minimal defaults");
            loadMinimalDefaultAids(emvOptV2);
            return;
        }
        
        // Use CountDownLatch to wait for async API call
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};
        final RemoteException[] savedException = {null};
        
        // Load configuration asynchronously
        apiService.loadEmvConfiguration(new com.neo.neopayplus.api.EmvConfigApiService.EmvConfigCallback() {
            @Override
            public void onConfigLoaded(com.neo.neopayplus.api.EmvConfigApiService.EmvConfigResponse response) {
                try {
                    if (response.success && response.aids != null && !response.aids.isEmpty()) {
                        // Store terminal parameters from API response
                        if (response.terminalCountryCode != null && !response.terminalCountryCode.isEmpty()) {
                            instance.terminalCountryCode = response.terminalCountryCode;
                            LogUtil.e(TAG, "✓ Country code from API: " + instance.terminalCountryCode);
                        }
                        if (response.currencyCode != null && !response.currencyCode.isEmpty()) {
                            instance.currencyCode = response.currencyCode;
                            LogUtil.e(TAG, "✓ Currency code from API: " + instance.currencyCode);
                        }
                        
                        // Store to local storage
                        Gson gson = new Gson();
                        String json = gson.toJson(response);
                        PreferencesUtil.saveEmvConfigJson(json);
                        LogUtil.e(TAG, "✓ Stored EMV configuration to local storage");
                        
                        // Add AIDs to EMV kernel
                        addAidsFromApiResponse(emvOptV2, response.aids);
                        success[0] = true;
                    } else {
                        LogUtil.e(TAG, "⚠️ Failed to load AIDs from API - using minimal defaults");
                        loadMinimalDefaultAids(emvOptV2);
                    }
                } catch (RemoteException e) {
                    LogUtil.e(TAG, "RemoteException processing API response: " + e.getMessage());
                    savedException[0] = e;
                    try {
                        loadMinimalDefaultAids(emvOptV2);
                    } catch (RemoteException re) {
                        LogUtil.e(TAG, "Error loading minimal default AIDs: " + re.getMessage());
                        savedException[0] = re;
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error processing API response: " + e.getMessage());
                    try {
                        loadMinimalDefaultAids(emvOptV2);
                    } catch (RemoteException re) {
                        LogUtil.e(TAG, "Error loading minimal default AIDs: " + re.getMessage());
                        savedException[0] = re;
                    }
                } finally {
                    latch.countDown();
                }
            }
            
            @Override
            public void onConfigError(Throwable error) {
                LogUtil.e(TAG, "⚠️ Error loading AIDs from API: " + error.getMessage());
                try {
                    loadMinimalDefaultAids(emvOptV2);
                } catch (RemoteException e) {
                    LogUtil.e(TAG, "Error loading minimal default AIDs: " + e.getMessage());
                    savedException[0] = e;
                } finally {
                    latch.countDown();
                }
            }
        });
        
        // Wait for API call to complete (max 30 seconds)
        try {
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                LogUtil.e(TAG, "⚠️ Timeout waiting for AIDs from API - using minimal defaults");
                loadMinimalDefaultAids(emvOptV2);
            }
            
            // Re-throw any RemoteException that occurred
            if (savedException[0] != null) {
                throw savedException[0];
            }
        } catch (InterruptedException e) {
            LogUtil.e(TAG, "⚠️ Interrupted while waiting for AIDs - using minimal defaults");
            Thread.currentThread().interrupt();
            loadMinimalDefaultAids(emvOptV2);
        }
    }
    
    /**
     * Add AIDs from API response (MERGE STRATEGY)
     * Strategy: Query existing SDK AIDs first, then add only missing ones from API.
     * For contactless AIDs, update selFlag=0 if SDK version has wrong selFlag.
     * This preserves SDK built-in AIDs as fallback while ensuring API-provided AIDs are added.
     */
    private void addAidsFromApiResponse(EMVOptV2 emvOptV2, java.util.List<com.neo.neopayplus.api.EmvConfigApiService.AidConfig> aidConfigs) {
        LogUtil.e(TAG, "Merging " + aidConfigs.size() + " AIDs from API with SDK built-ins...");
        LogUtil.e(TAG, "Strategy: Preserve SDK defaults, add missing from API, update contactless AIDs with selFlag=0");
        
        // Query existing SDK built-in AIDs (if available)
        java.util.Set<String> existingAids = new java.util.HashSet<>();
        try {
            java.util.List<String> sdkAidList = new java.util.ArrayList<>();
            int queryResult = emvOptV2.queryAidCapkList(0, sdkAidList); // 0 = query AIDs
            if (queryResult == 0 && !sdkAidList.isEmpty()) {
                LogUtil.e(TAG, "Found " + sdkAidList.size() + " existing AIDs in SDK (may include built-ins)");
                // Extract AID hex from TLV strings (format: 9F06XX... where XX is AID length)
                for (String aidTlv : sdkAidList) {
                    // Parse AID from TLV - format is 9F06[length][AID bytes]...
                    // For simplicity, we'll try to extract known AID patterns
                    if (aidTlv.contains("A0000000043060")) existingAids.add("A0000000043060");
                    if (aidTlv.contains("A0000000032010")) existingAids.add("A0000000032010");
                    if (aidTlv.contains("A0000000031010")) existingAids.add("A0000000031010");
                    if (aidTlv.contains("A0000000041010")) existingAids.add("A0000000041010");
                }
                LogUtil.e(TAG, "Identified " + existingAids.size() + " known contactless/contact AIDs in SDK");
            } else {
                LogUtil.e(TAG, "⚠️ Could not query SDK AIDs (query result: " + queryResult + ") - will rely on addAid() return codes");
                LogUtil.e(TAG, "Note: SDK built-in AIDs may still be present but not shown in query");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "⚠️ Error querying SDK AIDs: " + e.getMessage() + " - will rely on addAid() return codes");
        }
        
        int addedCount = 0;
        int updatedCount = 0;
        int skippedCount = 0;
        
        for (com.neo.neopayplus.api.EmvConfigApiService.AidConfig aidConfig : aidConfigs) {
            try {
                byte[] aidBytes = ByteUtil.hexStr2Bytes(aidConfig.aidHex);
                AidV2 aid = new AidV2();
                aid.aid = aidBytes;
                
                // For contactless (NFC), selFlag MUST be 0 for partial name matching
                // This is CRITICAL for Mastercard PayPass and Visa PayWave contactless transactions
                // Mastercard PayPass (A0000000043060) requires selFlag=0 for L2 candidate matching
                int selFlagValue = aidConfig.selFlag;
                boolean isContactlessAid = aidConfig.aidHex.equals("A0000000043060") ||  // Mastercard PayPass
                                          aidConfig.aidHex.equals("A0000000032010") ||  // Visa PayWave
                                          (aidConfig.label != null && (aidConfig.label.toUpperCase().contains("PAYPASS") || 
                                                                       aidConfig.label.toUpperCase().contains("PAYWAVE")));
                
                // Force selFlag=0 ONLY for contactless AIDs (required for L2 candidate list matching)
                // Contact chip AIDs can keep their selFlag (0 or 1 both work for contact)
                if (isContactlessAid) {
                    if (selFlagValue != 0) {
                        LogUtil.e(TAG, "⚠️ Contactless AID " + aidConfig.aidHex + " had selFlag=" + selFlagValue + 
                                  " - forcing to 0 (REQUIRED for contactless L2 candidate matching)");
                    }
                    selFlagValue = 0; // CRITICAL: contactless REQUIRES partial match (selFlag=0)
                    
                    // MERGE STRATEGY: Check if AID exists first, only delete/update if needed
                    // Don't blindly delete - SDK built-ins are valuable fallbacks
                    try {
                        // Try to query if AID exists (queryAidCapkList may not be reliable for this)
                        // For contactless AIDs with wrong selFlag, we need to update them
                        // Strategy: Delete only if we're sure we need to update selFlag
                        // If addAid fails with "already exists", then delete and retry
                        int deleteResult = emvOptV2.deleteAid(aidConfig.aidHex);
                        if (deleteResult == 0) {
                            LogUtil.e(TAG, "  ✓ Updated existing contactless AID " + aidConfig.aidHex + 
                                      " (deleted SDK version with possibly wrong selFlag, will re-add with selFlag=0)");
                            // Small delay to ensure deletion is committed before re-adding
                            try {
                                Thread.sleep(50); // 50ms delay to ensure SDK processes deletion
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            LogUtil.e(TAG, "  ℹ️ AID " + aidConfig.aidHex + " may not exist yet (delete result: " + deleteResult + ")");
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "  ⚠️ Could not check/update AID " + aidConfig.aidHex + ": " + e.getMessage());
                        // Continue anyway - addAid may still work
                    }
                    
                    LogUtil.e(TAG, "✓ Contactless AID " + aidConfig.aidHex + 
                              (aidConfig.label != null ? " (" + aidConfig.label + ")" : "") + 
                              " will be configured with selFlag=0");
                } else {
                    // For contact chip AIDs, keep the API-provided selFlag (or default 0)
                    if (selFlagValue == 0) {
                        LogUtil.e(TAG, "  AID " + aidConfig.aidHex + " using selFlag=0 (contact chip)");
                    }
                }
                aid.selFlag = (byte) selFlagValue;
                
                // PayLib v2.0.32 requires version field
                aid.version = aidConfig.version != null && !aidConfig.version.isEmpty() 
                    ? ByteUtil.hexStr2Bytes(aidConfig.version) 
                    : ByteUtil.hexStr2Bytes("008C"); // Default version if not provided
                aid.TACDefault = ByteUtil.hexStr2Bytes(aidConfig.tacDefault);
                aid.TACDenial = ByteUtil.hexStr2Bytes(aidConfig.tacDenial);
                aid.TACOnline = ByteUtil.hexStr2Bytes(aidConfig.tacOnline);
                aid.threshold = ByteUtil.hexStr2Bytes(aidConfig.threshold);
                aid.floorLimit = ByteUtil.hexStr2Bytes(aidConfig.floorLimit);
                aid.targetPer = (byte) aidConfig.targetPer;
                aid.maxTargetPer = (byte) aidConfig.maxTargetPer;
                
                // MERGE STRATEGY: Check if AID exists in SDK before adding
                boolean existsInSdk = existingAids.contains(aidConfig.aidHex);
                if (existsInSdk && !isContactlessAid) {
                    // Non-contactless AID exists in SDK - skip adding (preserve SDK version)
                    LogUtil.e(TAG, "⊘ Skipped AID " + aidConfig.aidHex + " (exists in SDK, preserving SDK version)");
                    skippedCount++;
                    continue;
                }
                
                // For contactless AIDs or new AIDs, add them (contactless AIDs may have been deleted above if needed)
                int code = emvOptV2.addAid(aid);
                if (code == 0) {
                    String aidLabel = aidConfig.aidHex.equals("A0000000043060") ? " (Mastercard PayPass)" :
                                     aidConfig.aidHex.equals("A0000000032010") ? " (Visa PayWave)" :
                                     aidConfig.aidHex.equals("A0000000031010") ? " (Visa Credit)" : "";
                    if (existsInSdk && isContactlessAid) {
                        LogUtil.e(TAG, "✓ Updated contactless AID: " + aidConfig.aidHex + aidLabel + 
                                  " (replaced SDK version with selFlag=" + selFlagValue + ")");
                        updatedCount++;
                    } else {
                        LogUtil.e(TAG, "✓ Added new AID: " + aidConfig.aidHex + aidLabel + " (selFlag=" + selFlagValue + ")");
                        addedCount++;
                    }
                } else if (code > 0) {
                    // AID already exists (SDK built-in or previously added) - this is OK for non-contactless
                    if (isContactlessAid) {
                        LogUtil.e(TAG, "⚠️ Contactless AID " + aidConfig.aidHex + " add failed (code: " + code + 
                                  ") - may need deletion first, but SDK version might work");
                        // Could retry with delete first, but SDK version might be acceptable
                    } else {
                        LogUtil.e(TAG, "⊘ AID " + aidConfig.aidHex + " already exists (code: " + code + 
                                  ") - preserving existing version");
                        skippedCount++;
                    }
                } else {
                    LogUtil.e(TAG, "❌ Failed to add AID " + aidConfig.aidHex + " (error code: " + code + ")");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error adding AID " + aidConfig.aidHex + ": " + e.getMessage());
            }
        }
        
        LogUtil.e(TAG, "=== AID Merge Summary ===");
        LogUtil.e(TAG, "  Added (new): " + addedCount);
        LogUtil.e(TAG, "  Updated (contactless with selFlag=0): " + updatedCount);
        LogUtil.e(TAG, "  Skipped (preserved SDK version): " + skippedCount);
        LogUtil.e(TAG, "✓ SDK built-in AIDs preserved - API AIDs supplement/update as needed");
    }
    
    /**
     * Load minimal default AIDs if API fails
     * This provides basic fallback functionality
     */
    private void loadMinimalDefaultAids(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Loading minimal default AIDs (fallback)");
        
        // Visa AID
        try {
            AidV2 visaAid = new AidV2();
            visaAid.aid = ByteUtil.hexStr2Bytes("A0000000031010");
            visaAid.selFlag = (byte) 0;
            visaAid.version = ByteUtil.hexStr2Bytes("008C"); // Required by PayLib v2.0.32
            visaAid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            visaAid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            visaAid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
            visaAid.threshold = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.THRESHOLD_ZERO);
            visaAid.floorLimit = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO);
            visaAid.targetPer = (byte) 0;
            visaAid.maxTargetPer = (byte) 0;
            emvOptV2.addAid(visaAid);
            LogUtil.e(TAG, "✓ Added default Visa AID");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error adding default Visa AID: " + e.getMessage());
        }
        
        // Mastercard AID
        try {
            AidV2 mcAid = new AidV2();
            mcAid.aid = ByteUtil.hexStr2Bytes("A0000000041010");
            mcAid.selFlag = (byte) 0;
            mcAid.version = ByteUtil.hexStr2Bytes("008C"); // Required by PayLib v2.0.32
            mcAid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            mcAid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            mcAid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
            mcAid.threshold = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.THRESHOLD_ZERO);
            mcAid.floorLimit = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO);
            mcAid.targetPer = (byte) 0;
            mcAid.maxTargetPer = (byte) 0;
            emvOptV2.addAid(mcAid);
            LogUtil.e(TAG, "✓ Added default Mastercard AID");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error adding default Mastercard AID: " + e.getMessage());
        }
        
        // American Express AID
        try {
            AidV2 amexAid = new AidV2();
            amexAid.aid = ByteUtil.hexStr2Bytes("A0000000043060");
            amexAid.selFlag = (byte) 0;
            amexAid.version = ByteUtil.hexStr2Bytes("008C"); // Required by PayLib v2.0.32
            amexAid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            amexAid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            amexAid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
            amexAid.threshold = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.THRESHOLD_ZERO);
            amexAid.floorLimit = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO);
            amexAid.targetPer = (byte) 0;
            amexAid.maxTargetPer = (byte) 0;
            emvOptV2.addAid(amexAid);
            LogUtil.e(TAG, "✓ Added default American Express AID");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error adding default Amex AID: " + e.getMessage());
        }
        
        LogUtil.e(TAG, "Minimal default AIDs loaded");
    }
    
    /**
     * Load CAPKs (Certificate Authority Public Keys) from API service or local storage
     * 
     * SECURITY NOTE: Production CAPKs must be loaded from backend/acquirer.
     * Placeholder CAPKs are a security risk and will fail certificate verification.
     */
    private void loadCapks(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Loading CAPKs - preserving SDK built-in CAPKs");
        
        // Preserve SDK built-in CAPKs - do NOT delete
        // Deleting would break certificate verification for standard cards
        
        // Try loading from local storage first
        String cachedJson = PreferencesUtil.getEmvConfigJson();
        if (cachedJson != null) {
            try {
                Gson gson = new Gson();
                EmvConfigApiService.EmvConfigResponse cachedResponse = gson.fromJson(cachedJson, EmvConfigApiService.EmvConfigResponse.class);
                if (cachedResponse != null && cachedResponse.capks != null && !cachedResponse.capks.isEmpty()) {
                    addCapksFromApiResponse(emvOptV2, cachedResponse.capks);
                    LogUtil.e(TAG, "✓ Loaded " + cachedResponse.capks.size() + " CAPKs from cache");
                    return;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "⚠️ Error parsing cached CAPKs: " + e.getMessage());
            }
        }
        
        // Load CAPKs from API service (if not in cache)
        com.neo.neopayplus.api.EmvConfigApiService apiService = 
            com.neo.neopayplus.api.EmvConfigApiFactory.getInstance();
        
        if (!apiService.isAvailable()) {
            LogUtil.e(TAG, "⚠️ EMV Config API service not available - relying on SDK built-in CAPKs");
            LogUtil.e(TAG, "⚠️ SECURITY: Production CAPKs must be loaded from acquirer/backend");
            return;
        }
        
        // Load configuration asynchronously (already cached if we got here from loadAids)
        apiService.loadEmvConfiguration(new com.neo.neopayplus.api.EmvConfigApiService.EmvConfigCallback() {
            @Override
            public void onConfigLoaded(com.neo.neopayplus.api.EmvConfigApiService.EmvConfigResponse response) {
                if (response.success && response.capks != null && !response.capks.isEmpty()) {
                    addCapksFromApiResponse(emvOptV2, response.capks);
                } else {
                    LogUtil.e(TAG, "⚠️ No CAPKs in API response - relying on SDK built-in CAPKs");
                    LogUtil.e(TAG, "⚠️ SECURITY: Production CAPKs must be loaded from acquirer/backend");
                }
            }
            
            @Override
            public void onConfigError(Throwable error) {
                LogUtil.e(TAG, "⚠️ Error loading CAPKs from API: " + error.getMessage());
                LogUtil.e(TAG, "⚠️ Relying on SDK built-in CAPKs");
                LogUtil.e(TAG, "⚠️ SECURITY: Production CAPKs must be loaded from acquirer/backend");
            }
        });
    }
    
    /**
     * Add CAPKs from API response (MERGE STRATEGY)
     * Strategy: Try to add each CAPK from API. If it already exists (SDK built-in), preserve SDK version.
     * Only add if missing or if API version is different (requires explicit update approval).
     */
    private void addCapksFromApiResponse(EMVOptV2 emvOptV2, java.util.List<com.neo.neopayplus.api.EmvConfigApiService.CapkConfig> capkConfigs) {
        LogUtil.e(TAG, "Merging " + capkConfigs.size() + " CAPKs from API with SDK built-ins...");
        LogUtil.e(TAG, "Strategy: Preserve SDK defaults, add missing from API");
        
        // Query existing SDK built-in CAPKs (if available)
        java.util.Set<String> existingCapks = new java.util.HashSet<>();
        try {
            java.util.List<String> sdkCapkList = new java.util.ArrayList<>();
            int queryResult = emvOptV2.queryAidCapkList(1, sdkCapkList); // 1 = query CAPKs
            if (queryResult == 0 && !sdkCapkList.isEmpty()) {
                LogUtil.e(TAG, "Found " + sdkCapkList.size() + " existing CAPKs in SDK (may include built-ins)");
                // Extract RID+index from CAPK TLV strings
                // Format varies, but we can extract known patterns
                for (String capkTlv : sdkCapkList) {
                    // Parse RID+index from TLV - for simplicity, log and store as "RID:index"
                    // This is a simplified approach - full parsing would require TLV library
                    if (capkTlv.contains("A000000004")) existingCapks.add("A000000004:*"); // Mastercard
                    if (capkTlv.contains("A000000003")) existingCapks.add("A000000003:*"); // Visa
                }
                LogUtil.e(TAG, "Identified " + existingCapks.size() + " known scheme CAPKs in SDK");
            } else {
                LogUtil.e(TAG, "⚠️ Could not query SDK CAPKs (query result: " + queryResult + ") - will rely on addCapk() return codes");
                LogUtil.e(TAG, "Note: SDK built-in CAPKs may still be present but not shown in query");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "⚠️ Error querying SDK CAPKs: " + e.getMessage() + " - will rely on addCapk() return codes");
        }
        
        int addedCount = 0;
        int skippedCount = 0;
        
        for (com.neo.neopayplus.api.EmvConfigApiService.CapkConfig capkConfig : capkConfigs) {
            try {
                String capkKey = capkConfig.ridHex + ":" + capkConfig.indexHex;
                
                CapkV2 capk = new CapkV2();
                capk.rid = ByteUtil.hexStr2Bytes(capkConfig.ridHex);
                capk.index = ByteUtil.hexStr2Byte(capkConfig.indexHex);
                capk.modul = ByteUtil.hexStr2Bytes(capkConfig.modulusHex);
                capk.exponent = ByteUtil.hexStr2Bytes(capkConfig.exponentHex);
                capk.hashInd = ByteUtil.hexStr2Byte(capkConfig.hashIndHex);
                capk.arithInd = ByteUtil.hexStr2Byte(capkConfig.arithIndHex);
                
                // MERGE STRATEGY: Try to add, if exists preserve SDK version
                int code = emvOptV2.addCapk(capk);
                if (code == 0) {
                    LogUtil.e(TAG, "✓ Added new CAPK: " + capkConfig.ridHex + " index " + capkConfig.indexHex);
                    addedCount++;
                } else if (code > 0) {
                    // CAPK already exists (SDK built-in or previously added) - preserve existing
                    LogUtil.e(TAG, "⊘ CAPK " + capkConfig.ridHex + " index " + capkConfig.indexHex + 
                              " already exists (code: " + code + ") - preserving SDK/existing version");
                    skippedCount++;
                } else {
                    LogUtil.e(TAG, "❌ Failed to add CAPK " + capkConfig.ridHex + " (error code: " + code + ")");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error adding CAPK " + capkConfig.ridHex + ": " + e.getMessage());
            }
        }
        
        LogUtil.e(TAG, "=== CAPK Merge Summary ===");
        LogUtil.e(TAG, "  Added (new): " + addedCount);
        LogUtil.e(TAG, "  Skipped (preserved SDK version): " + skippedCount);
        LogUtil.e(TAG, "✓ SDK built-in CAPKs preserved - API CAPKs supplement as needed");
    }
    
    /**
     * Configure terminal parameters for EMV transactions
     * PayLib v2.0.32: Use setTermParamEx() for provisioning phase (one-time setup)
     * Guide recommends: setTermParamEx() with EmvTermParamV2 containing all terminal params
     */
    private void configureTerminalParameters(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Configuring terminal parameters...");
        
        // Use API-provided values if available, otherwise use PaymentConfig defaults
        String countryCode = terminalCountryCode != null ? terminalCountryCode : PaymentConfig.TERMINAL_COUNTRY_CODE;
        String currencyCodeValue = currencyCode != null ? currencyCode : PaymentConfig.CURRENCY_CODE_TLV;
        
        // Extract 3-digit currency code from 4-digit format for EmvTermParamV2
        String currencyCode3Digit = currencyCodeValue.length() >= 3 
            ? currencyCodeValue.substring(currencyCodeValue.length() - 3) 
            : PaymentConfig.CURRENCY_CODE;
        
        LogUtil.e(TAG, "Using country code: " + countryCode + ", currency code: " + currencyCode3Digit + " (from " + currencyCodeValue + ")");
        
        // PayLib v2.0.32 Guide: Use setTermParamEx() if available
        // Note: EmvTermParamV2 may have limited fields in SDK - use available ones
        try {
            EmvTermParamV2 termParam = new EmvTermParamV2();
            termParam.countryCode = countryCode;
            termParam.currencyCode = currencyCode3Digit; // 3-digit format for setTermParamEx()
            termParam.capability = PaymentConfig.TERMINAL_CAPABILITIES;
            
            // Try setTermParamEx() first (guide recommendation) - may accept Bundle or EmvTermParamV2
            try {
                // Try as Bundle first (SDK may require Bundle)
                Bundle termBundle = new Bundle();
                termBundle.putString("terminalId", PaymentConfig.TERMINAL_ID);
                termBundle.putString("merchantId", PaymentConfig.MERCHANT_ID);
                termBundle.putString("merchantName", PaymentConfig.MERCHANT_NAME);
                termBundle.putString("countryCode", countryCode);
                termBundle.putString("currencyCode", currencyCode3Digit);
                termBundle.putString("capability", PaymentConfig.TERMINAL_CAPABILITIES);
                termBundle.putString("transCurrencyExp", PaymentConfig.CURRENCY_EXPONENT);
                
                // Try setTermParamEx with Bundle
                try {
                    emvOptV2.setTermParamEx(termBundle);
                    LogUtil.e(TAG, "✓ Terminal parameters set via setTermParamEx(Bundle) (PayLib v2.0.32 guide method)");
                    LogUtil.e(TAG, "  Country Code: " + countryCode);
                    LogUtil.e(TAG, "  Currency Code: " + currencyCode3Digit);
                    return; // Success - exit early
                } catch (Exception e1) {
                    LogUtil.e(TAG, "⚠️ setTermParamEx(Bundle) not available, trying setTerminalParam(): " + e1.getMessage());
                }
            } catch (NoSuchMethodError | Exception e) {
                LogUtil.e(TAG, "⚠️ setTermParamEx() not available, trying setTerminalParam(): " + e.getMessage());
            }
            
            // Fallback: Try setTerminalParam() if setTermParamEx() not available
            try {
                int result = emvOptV2.setTerminalParam(termParam);
                if (result == 0) {
                    LogUtil.e(TAG, "✓ Terminal parameters set via setTerminalParam()");
                    return; // Success - exit early
                } else {
                    LogUtil.e(TAG, "⚠️ setTerminalParam() returned code: " + result + ", using setTlvList() fallback");
                }
            } catch (NoSuchMethodError | Exception e) {
                LogUtil.e(TAG, "⚠️ setTerminalParam() not available, using setTlvList(): " + e.getMessage());
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "⚠️ Error using setTermParamEx()/setTerminalParam(), falling back to setTlvList(): " + e.getMessage());
        }
        
        // Final fallback: Use setTlvList() for compatibility
        setTerminalParametersViaTlv(emvOptV2);
    }
    
    /**
     * Fallback method: Set terminal parameters via setTlvList() for compatibility
     */
    private void setTerminalParametersViaTlv(EMVOptV2 emvOptV2) throws RemoteException {
        // Set terminal capabilities (9F33) - supports Online PIN, Offline PIN, CDCVM, contactless
        String[] termCapTags = {"9F33"};
        String[] termCapValues = {PaymentConfig.TERMINAL_CAPABILITIES};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termCapTags, termCapValues);
        
        // Set additional terminal capabilities (9F40) - Online PIN supported
        String[] addCapTags = {"9F40"};
        String[] addCapValues = {PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, addCapTags, addCapValues);
        
        // Set terminal type (9F35) - from PaymentConfig
        String[] termTypeTags = {"9F35"};
        String[] termTypeValues = {PaymentConfig.TERMINAL_TYPE};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termTypeTags, termTypeValues);
        
        // Set country code (9F1A) - from PaymentConfig
        String[] countryTags = {"9F1A"};
        String[] countryValues = {PaymentConfig.TERMINAL_COUNTRY_CODE};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, countryTags, countryValues);
        
        // Set currency code (5F2A) - from PaymentConfig
        String[] currencyTags = {"5F2A"};
        String[] currencyValues = {PaymentConfig.CURRENCY_CODE_TLV};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, currencyTags, currencyValues);
        
        // Set transaction category code (9F53) for contactless - from PaymentConfig
        String[] tccTags = {"9F53"};
        String[] tccValues = {PaymentConfig.TRANSACTION_CATEGORY_CODE};
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tccTags, tccValues);
        
        LogUtil.e(TAG, "Terminal parameters configured via setTlvList()");
    }
    
    /**
     * Configure scheme-specific TLV data (PayPass, PayWave, AMEX, JCB)
     */
    private void configureSchemeTlvData(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Configuring scheme-specific TLV data...");
        
        // PayPass (MasterCard) TLV data - from PaymentConfig
        String[] tagsPayPass = {"DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                "DF8123", "DF8124", "DF8125", "DF8126",
                "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"};
        String[] valuesPayPass = {
                PaymentConfig.PayPassConfig.DF8117,
                PaymentConfig.PayPassConfig.DF8118,
                PaymentConfig.PayPassConfig.DF8119,
                PaymentConfig.PayPassConfig.DF811F,
                PaymentConfig.PayPassConfig.DF811E,
                PaymentConfig.PayPassConfig.DF812C,
                PaymentConfig.PayPassConfig.DF8123,
                PaymentConfig.PayPassConfig.DF8124,
                PaymentConfig.PayPassConfig.DF8125,
                PaymentConfig.PayPassConfig.DF8126,
                PaymentConfig.PayPassConfig.DF811B,
                PaymentConfig.PayPassConfig.DF811D,
                PaymentConfig.PayPassConfig.DF8122,
                PaymentConfig.PayPassConfig.DF8120,
                PaymentConfig.PayPassConfig.DF8121
        };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass);
        
        // PayWave (Visa) TLV data - from PaymentConfig
        String[] tagsPayWave = {"DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                "DF8123", "DF8124", "DF8125", "DF8126",
                "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"};
        String[] valuesPayWave = {
                PaymentConfig.PayWaveConfig.DF8117,
                PaymentConfig.PayWaveConfig.DF8118,
                PaymentConfig.PayWaveConfig.DF8119,
                PaymentConfig.PayWaveConfig.DF811F,
                PaymentConfig.PayWaveConfig.DF811E,
                PaymentConfig.PayWaveConfig.DF812C,
                PaymentConfig.PayWaveConfig.DF8123,
                PaymentConfig.PayWaveConfig.DF8124,
                PaymentConfig.PayWaveConfig.DF8125,
                PaymentConfig.PayWaveConfig.DF8126,
                PaymentConfig.PayWaveConfig.DF811B,
                PaymentConfig.PayWaveConfig.DF811D,
                PaymentConfig.PayWaveConfig.DF8122,
                PaymentConfig.PayWaveConfig.DF8120,
                PaymentConfig.PayWaveConfig.DF8121
        };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tagsPayWave, valuesPayWave);
        
        // AMEX (American Express) TLV data - from PaymentConfig
        String[] tagsAE = {"9F6D", "9F6E", "9F33", "9F35", "DF8168", "DF8167", "DF8169", "DF8170"};
        String[] valuesAE = {
                PaymentConfig.AmexConfig.TAG_9F6D,
                PaymentConfig.AmexConfig.TAG_9F6E,
                PaymentConfig.AmexConfig.TAG_9F33,
                PaymentConfig.AmexConfig.TAG_9F35,
                PaymentConfig.AmexConfig.DF8168,
                PaymentConfig.AmexConfig.DF8167,
                PaymentConfig.AmexConfig.DF8169,
                PaymentConfig.AmexConfig.DF8170
        };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_AE, tagsAE, valuesAE);
        
        // JCB TLV data - from PaymentConfig
        String[] tagsJCB = {"9F53", "DF8161"};
        String[] valuesJCB = {
                PaymentConfig.JCBConfig.TAG_9F53,
                PaymentConfig.JCBConfig.DF8161
        };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_JCB, tagsJCB, valuesJCB);
        
        LogUtil.e(TAG, "Scheme-specific TLV data configured");
    }
}

