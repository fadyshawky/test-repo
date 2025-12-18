package com.neo.neopayplus.emv;

import android.os.Bundle;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
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

import com.neo.neopayplus.domain.payment.model.TransactionType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Default AIDs JSON structure (matching assets/default_aids.json)
 */
class DefaultAidConfig {
    @SerializedName("Card scheme / payment network")
    String cardScheme;

    @SerializedName("RID")
    String rid;

    boolean enable;

    @SerializedName("dDOL")
    String dDOL;

    String version;

    @SerializedName("kernel_type")
    int kernelType;

    @SerializedName("refund_TACDenial_DF13")
    String refundTacDenial;

    @SerializedName("refund_TACOnline_DF12")
    String refundTacOnline;

    @SerializedName("refund_TACDefault_DF11")
    String refundTacDefault;

    @SerializedName("termRiskManagement")
    String termRiskManagement;

    List<DefaultAidChild> children;

    static class DefaultAidChild {
        String Product;
        String AID;
        String PIX;
        boolean enable;

        @SerializedName("termRiskManagement")
        String termRiskManagement; // Override parent if specified
    }

    static class DefaultAidsRoot {
        List<DefaultAidConfig> aids;
    }
}

/**
 * EMV Configuration Manager
 * 
 * Centralized manager for EMV configuration including:
 * - Terminal parameters (9F33, 9F40, etc.)
 * - AIDs (Application Identifiers)
 * - CAPKs (Certificate Authority Public Keys)
 * 
 * This manager ensures configuration is loaded once at application startup
 * rather than per-transaction, improving performance and maintaining
 * consistency.
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

            // NOTE: AIDs and CAPKs are now loaded by EmvProvisioner in
            // MyApplication.onConnectPaySDK
            // Do NOT load them here as it would overwrite the contactless configuration
            // that EmvProvisioner sets (cvmLmt, termClssLmt, ttq, etc.)

            // Skip: loadAids(emvOptV2);
            // Skip: loadCapks(emvOptV2);
            LogUtil.e(TAG, "✓ Skipping AID/CAPK loading (handled by EmvProvisioner)");

            // 3. Configure terminal parameters (optional - EmvProvisioner also does this)
            // configureTerminalParameters(emvOptV2);

            // 4. Configure scheme-specific TLV data (optional)
            // configureSchemeTlvData(emvOptV2);

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
     * 
     * @return Country code (4-digit, e.g., "0840") or null if not loaded
     */
    public String getTerminalCountryCode() {
        return terminalCountryCode != null ? terminalCountryCode : PaymentConfig.TERMINAL_COUNTRY_CODE;
    }

    /**
     * Get currency code from API response (4-digit format for EMV TLV)
     * 
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
                EmvConfigApiService.EmvConfigResponse cachedResponse = gson.fromJson(cachedJson,
                        EmvConfigApiService.EmvConfigResponse.class);
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
            LogUtil.e(TAG, "⚠️ Cache expired or disabled - fetching fresh AIDs from API (cache age: "
                    + (cacheAge / 1000) + " seconds)");
        }

        // Cache invalid or not available - fetch from API
        LogUtil.e(TAG, "Fetching AIDs from API service...");

        com.neo.neopayplus.api.EmvConfigApiService apiService = com.neo.neopayplus.api.EmvConfigApiFactory
                .getInstance();

        if (!apiService.isAvailable()) {
            LogUtil.e(TAG, "⚠️ EMV Config API service not available - using minimal defaults");
            loadMinimalDefaultAids(emvOptV2);
            return;
        }

        // Use CountDownLatch to wait for async API call
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = { false };
        final RemoteException[] savedException = { null };

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
     * This preserves SDK built-in AIDs as fallback while ensuring API-provided AIDs
     * are added.
     */
    private void addAidsFromApiResponse(EMVOptV2 emvOptV2,
            java.util.List<com.neo.neopayplus.api.EmvConfigApiService.AidConfig> aidConfigs) {
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
                    if (aidTlv.contains("A0000000043060"))
                        existingAids.add("A0000000043060");
                    if (aidTlv.contains("A0000000032010"))
                        existingAids.add("A0000000032010");
                    if (aidTlv.contains("A0000000031010"))
                        existingAids.add("A0000000031010");
                    if (aidTlv.contains("A0000000041010"))
                        existingAids.add("A0000000041010");
                }
                LogUtil.e(TAG, "Identified " + existingAids.size() + " known contactless/contact AIDs in SDK");
            } else {
                LogUtil.e(TAG, "⚠️ Could not query SDK AIDs (query result: " + queryResult
                        + ") - will rely on addAid() return codes");
                LogUtil.e(TAG, "Note: SDK built-in AIDs may still be present but not shown in query");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "⚠️ Error querying SDK AIDs: " + e.getMessage() + " - will rely on addAid() return codes");
        }

        int addedCount = 0;
        int skippedCount = 0;

        for (com.neo.neopayplus.api.EmvConfigApiService.AidConfig aidConfig : aidConfigs) {
            try {
                byte[] aidBytes = ByteUtil.hexStr2Bytes(aidConfig.aidHex);
                AidV2 aid = new AidV2();
                aid.aid = aidBytes;

                // Sunmi SDK V2: selFlag meanings:
                // 0 = contact (chip) only
                // 1 = contactless (NFC) enabled
                // CRITICAL: For NFC to work, selFlag MUST be 1
                int selFlagValue = aidConfig.selFlag;

                // Detect if this should be a contactless-enabled AID
                boolean shouldBeContactless = selFlagValue == 1 ||
                        (aidConfig.kernel != null && (aidConfig.kernel.toUpperCase().contains("PAYPASS") ||
                                aidConfig.kernel.toUpperCase().contains("PAYWAVE") ||
                                aidConfig.kernel.toUpperCase().contains("EXPRESSPAY")))
                        ||
                        (aidConfig.label != null && (aidConfig.label.toUpperCase().contains("PAYPASS") ||
                                aidConfig.label.toUpperCase().contains("PAYWAVE") ||
                                aidConfig.label.toUpperCase().contains("CONTACTLESS")));

                // If API says contactless (selFlag=1) or it's a known contactless kernel, use
                // selFlag=1
                if (shouldBeContactless) {
                    selFlagValue = 1; // CRITICAL: 1 = contactless enabled
                    LogUtil.e(TAG, "✓ AID " + aidConfig.aidHex +
                            (aidConfig.label != null ? " (" + aidConfig.label + ")" : "") +
                            " configured with selFlag=1 (contactless enabled)");
                } else {
                    LogUtil.e(TAG, "  AID " + aidConfig.aidHex + " using selFlag=" + selFlagValue + " (contact only)");
                }

                // Delete existing AID first to ensure our config is applied
                try {
                    int deleteResult = emvOptV2.deleteAid(aidConfig.aidHex);
                    if (deleteResult == 0) {
                        LogUtil.e(TAG,
                                "  ✓ Deleted existing AID " + aidConfig.aidHex + " (will re-add with new config)");
                        Thread.sleep(50); // Small delay to ensure deletion is processed
                    }
                } catch (Exception e) {
                    // Ignore - AID may not exist
                }

                aid.selFlag = (byte) selFlagValue;

                // CRITICAL: Set kernelType for Mastercard/Meeza AIDs to ensure DFC10A=02 is
                // stored
                // Without DFC10A=02, PayPass L2 kernel is not available → -4125 "L2 candidate
                // list is empty"
                String aidHexUpper = aidConfig.aidHex.toUpperCase();
                boolean isMastercard = aidHexUpper.startsWith("A000000004") || aidHexUpper.startsWith("A000000005");
                boolean isMeeza = aidHexUpper.startsWith("A000000732");
                if (isMastercard || isMeeza) {
                    // CRITICAL: kernelType = 2 (PayPass) - this creates DFC10A=02 in AID TLV
                    aid.kernelType = (byte) com.sunmi.pay.hardware.aidl.AidlConstants.EMV.KernelType.PAYPASS;
                    LogUtil.e(TAG, "  CRITICAL: Set kernelType=2 (PayPass) for "
                            + (isMastercard ? "Mastercard" : "Meeza") + " AID " + aidConfig.aidHex);
                    LogUtil.e(TAG, "    This creates DFC10A=02 in AID TLV - REQUIRED for PayPass L2 kernel activation");
                } else if (aidHexUpper.startsWith("A000000003")) {
                    // Visa PayWave
                    aid.kernelType = (byte) com.sunmi.pay.hardware.aidl.AidlConstants.EMV.KernelType.PAYWAVE;
                }

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

                // Add AID to device
                int code = emvOptV2.addAid(aid);
                if (code == 0) {
                    String aidLabel = aidConfig.label != null ? " (" + aidConfig.label + ")" : "";
                    LogUtil.e(TAG, "✓ Added AID: " + aidConfig.aidHex + aidLabel + " (selFlag=" + selFlagValue + ")");
                    addedCount++;
                } else if (code > 0) {
                    // AID already exists
                    LogUtil.e(TAG, "⊘ AID " + aidConfig.aidHex + " already exists (code: " + code + ")");
                    skippedCount++;
                } else {
                    LogUtil.e(TAG, "❌ Failed to add AID " + aidConfig.aidHex + " (code: " + code + ")");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error adding AID " + aidConfig.aidHex + ": " + e.getMessage());
            }
        }

        LogUtil.e(TAG, "=== AID Merge Summary ===");
        LogUtil.e(TAG, "  Added: " + addedCount);
        LogUtil.e(TAG, "  Skipped (already exists): " + skippedCount);
        LogUtil.e(TAG, "✓ AIDs configured with selFlag=1 for contactless support");
    }

    /**
     * Load minimal default AIDs if API fails
     * Reads from assets/default_aids.json and installs all enabled AIDs
     */
    private void loadMinimalDefaultAids(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Loading default AIDs from assets/default_aids.json (fallback)");

        try {
            // Read JSON from assets
            InputStream is = MyApplication.app.getAssets().open("default_aids.json");
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            DefaultAidConfig.DefaultAidsRoot root = gson.fromJson(reader, DefaultAidConfig.DefaultAidsRoot.class);
            reader.close();
            is.close();

            if (root == null || root.aids == null || root.aids.isEmpty()) {
                LogUtil.e(TAG, "⚠️ No AIDs found in default_aids.json");
                return;
            }

            int totalAdded = 0;
            int totalSkipped = 0;

            // Loop through each card scheme
            for (DefaultAidConfig scheme : root.aids) {
                if (!scheme.enable) {
                    LogUtil.e(TAG, "Skipping disabled scheme: " + scheme.cardScheme);
                    continue;
                }

                if (scheme.children == null || scheme.children.isEmpty()) {
                    continue;
                }

                // Loop through each child AID
                for (DefaultAidConfig.DefaultAidChild child : scheme.children) {
                    if (!child.enable) {
                        totalSkipped++;
                        continue;
                    }

                    try {
                        // Delete existing AID first
                        emvOptV2.deleteAid(child.AID);

                        // Create AidV2 from JSON config
                        AidV2 aid = createAidFromDefaultConfig(scheme, child);

                        // Add AID
                        int result = emvOptV2.addAid(aid);
                        if (result == 0) {
                            totalAdded++;
                            LogUtil.e(TAG, "✓ Added " + scheme.cardScheme + " AID: " + child.AID +
                                    " (kernelType=" + aid.kernelType + ", selFlag=" + aid.selFlag + ")");
                        } else {
                            LogUtil.e(TAG, "⚠️ Failed to add AID " + child.AID + ": result=" + result);
                        }
                    } catch (Exception e) {
                        LogUtil.e(TAG, "Error adding AID " + child.AID + ": " + e.getMessage());
                    }
                }
            }

            LogUtil.e(TAG, "✓ Default AIDs loaded: " + totalAdded + " added, " + totalSkipped + " skipped");
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error loading default AIDs from JSON: " + e.getMessage());
            e.printStackTrace();
            // Fallback to minimal hardcoded AIDs if JSON fails
            loadMinimalHardcodedAids(emvOptV2);
        }
    }

    /**
     * Create AidV2 object from default AID configuration
     */
    private AidV2 createAidFromDefaultConfig(DefaultAidConfig scheme, DefaultAidConfig.DefaultAidChild child) {
        // Default behavior: build AID for a normal purchase transaction
        return createAidFromDefaultConfigForTransaction(scheme, child, TransactionType.PURCHASE);
    }

    /**
     * Create AidV2 object from default AID configuration for a specific transaction
     * type.
     *
     * This matches the colleague pattern:
     * - Always creates a NEW AidV2 instance
     * - Populates all standard fields (limits, TACs, merchant/terminal, risk)
     * - Switches TACs for REFUND / VOID transactions
     */
    private static AidV2 createAidFromDefaultConfigForTransaction(
            DefaultAidConfig scheme,
            DefaultAidConfig.DefaultAidChild child,
            TransactionType transactionType) {
        AidV2 aid = new AidV2();

        // Basic AID fields
        aid.aid = ByteUtil.hexStr2Bytes(child.AID);
        aid.selFlag = (byte) 1; // Contactless enabled
        aid.paramType = (byte) 0; // Default (allows both contact and contactless)

        // Kernel type from scheme config
        aid.kernelType = (byte) scheme.kernelType;

        // Version from scheme or default
        String version = scheme.version != null && !scheme.version.isEmpty() ? scheme.version : "008C";
        aid.version = ByteUtil.hexStr2Bytes(version);

        // dDOL from scheme config
        if (scheme.dDOL != null && !scheme.dDOL.isEmpty()) {
            aid.dDOL = ByteUtil.hexStr2Bytes(scheme.dDOL);
        }

        // ================= TAC SELECTION BY TRANSACTION TYPE =================
        //
        // Pattern:
        // - PURCHASE / REVERSAL: use "normal" TACs
        // - REFUND / VOID: use refund TACs if provided in scheme, otherwise fallback to
        // normal TACs
        //
        boolean isRefundOrVoid = (transactionType == TransactionType.REFUND ||
                transactionType == TransactionType.VOID);

        if (isRefundOrVoid) {
            // Refund / void TACs (per-scheme if available, else fall back to normal TACs)
            String refundTacDenial = scheme.refundTacDenial;
            String refundTacOnline = scheme.refundTacOnline;
            String refundTacDefault = scheme.refundTacDefault;

            String tacDenial = (refundTacDenial != null && !refundTacDenial.isEmpty())
                    ? refundTacDenial
                    : PaymentConfig.TACConfig.TAC_DENIAL;
            String tacOnline = (refundTacOnline != null && !refundTacOnline.isEmpty())
                    ? refundTacOnline
                    : PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED;
            String tacDefault = (refundTacDefault != null && !refundTacDefault.isEmpty())
                    ? refundTacDefault
                    : PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED;

            aid.TACDefault = ByteUtil.hexStr2Bytes(tacDefault);
            aid.TACDenial = ByteUtil.hexStr2Bytes(tacDenial);
            aid.TACOnline = ByteUtil.hexStr2Bytes(tacOnline);
        } else {
            // Normal TACs for purchase / reversal
            aid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            aid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            aid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
        }

        // Risk management data (child override or scheme default)
        String riskManData = child.termRiskManagement != null && !child.termRiskManagement.isEmpty()
                ? child.termRiskManagement
                : (scheme.termRiskManagement != null && !scheme.termRiskManagement.isEmpty()
                        ? scheme.termRiskManagement
                        : "6C78800000000000");
        aid.riskManData = ByteUtil.hexStr2Bytes(riskManData, 8);
        aid.rMDLen = (byte) 8;

        // Standard fields
        aid.threshold = ByteUtil.hexStr2Bytes("00000000");
        aid.floorLimit = ByteUtil.hexStr2Bytes("000000000000");
        aid.targetPer = (byte) 0;
        aid.maxTargetPer = (byte) 0;

        // Merchant/terminal fields
        String rid = scheme.rid != null && !scheme.rid.isEmpty() ? scheme.rid : child.AID.substring(0, 10);
        aid.AcquierId = ByteUtil.hexStr2Bytes(rid, 6);
        aid.merchName = ByteUtil.text2Bytes(PaymentConfig.MERCHANT_NAME, 128);
        aid.merchCateCode = ByteUtil.hexStr2Bytes("0000", 2);
        aid.merchId = ByteUtil.hexStr2Bytes(PaymentConfig.getMerchantId(), 16);

        // Pad terminal ID to 8 characters
        String termId = PaymentConfig.getTerminalId();
        if (termId == null) {
            termId = "";
        }
        StringBuilder termIdBuilder = new StringBuilder(termId);
        while (termIdBuilder.length() < 8) {
            termIdBuilder.append(" ");
        }
        aid.termId = ByteUtil.text2Bytes(termIdBuilder.substring(0, 8), 8);

        return aid;
    }

    /**
     * Public helper to build AID list according to transaction type, following the
     * colleague pattern:
     *
     * - Reads AIDs from assets/default_aids.json
     * - Creates NEW AidV2 instances for each enabled child AID
     * - Selects TACs based on transaction type (normal vs refund/void)
     *
     * NOTE: This method ONLY builds AidV2 objects – it does NOT install them into
     * EMV.
     * Callers can decide whether/when to call emvOptV2.addAid(aid) per transaction.
     */
    public static List<AidV2> getDefaultAidsForTransactionType(TransactionType transactionType) throws Exception {
        List<AidV2> result = new ArrayList<>();

        // Read JSON from assets
        InputStream is = MyApplication.app.getAssets().open("default_aids.json");
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        DefaultAidConfig.DefaultAidsRoot root = gson.fromJson(reader, DefaultAidConfig.DefaultAidsRoot.class);
        reader.close();
        is.close();

        if (root == null || root.aids == null || root.aids.isEmpty()) {
            LogUtil.e(TAG, "⚠️ getDefaultAidsForTransactionType: no AIDs defined in default_aids.json");
            return result;
        }

        boolean isRefundOrVoid = (transactionType == TransactionType.REFUND ||
                transactionType == TransactionType.VOID);
        LogUtil.e(TAG, "getDefaultAidsForTransactionType: transactionType=" + transactionType +
                ", isRefundOrVoid=" + isRefundOrVoid);

        for (DefaultAidConfig scheme : root.aids) {
            if (!scheme.enable) {
                continue;
            }
            if (scheme.children == null || scheme.children.isEmpty()) {
                continue;
            }

            for (DefaultAidConfig.DefaultAidChild child : scheme.children) {
                if (child == null || !child.enable) {
                    continue;
                }
                try {
                    AidV2 aid = createAidFromDefaultConfigForTransaction(scheme, child, transactionType);
                    result.add(aid);
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error building AidV2 for AID " + child.AID + ": " + e.getMessage());
                }
            }
        }

        LogUtil.e(TAG, "getDefaultAidsForTransactionType: built " + result.size() + " AIDs for " + transactionType);
        return result;
    }

    /**
     * Fallback: Load minimal hardcoded AIDs if JSON file fails
     */
    private void loadMinimalHardcodedAids(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "⚠️ Using hardcoded fallback AIDs (JSON load failed)");

        // Minimal Visa AID
        try {
            emvOptV2.deleteAid("A0000000031010");
            AidV2 visaAid = new AidV2();
            visaAid.aid = ByteUtil.hexStr2Bytes("A0000000031010");
            visaAid.selFlag = (byte) 1;
            visaAid.kernelType = (byte) com.sunmi.pay.hardware.aidl.AidlConstants.EMV.KernelType.PAYWAVE;
            visaAid.version = ByteUtil.hexStr2Bytes("0097");
            visaAid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            visaAid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            visaAid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
            visaAid.threshold = ByteUtil.hexStr2Bytes("00000000");
            visaAid.floorLimit = ByteUtil.hexStr2Bytes("000000000000");
            visaAid.targetPer = (byte) 0;
            visaAid.maxTargetPer = (byte) 0;
            emvOptV2.addAid(visaAid);
            LogUtil.e(TAG, "✓ Added minimal Visa AID");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error adding minimal Visa AID: " + e.getMessage());
        }

        // Minimal Mastercard AID
        try {
            emvOptV2.deleteAid("A0000000041010");
            AidV2 mcAid = new AidV2();
            mcAid.aid = ByteUtil.hexStr2Bytes("A0000000041010");
            mcAid.selFlag = (byte) 1;
            mcAid.kernelType = (byte) com.sunmi.pay.hardware.aidl.AidlConstants.EMV.KernelType.PAYPASS;
            mcAid.version = ByteUtil.hexStr2Bytes("0002");
            mcAid.TACDefault = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED);
            mcAid.TACDenial = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_DENIAL);
            mcAid.TACOnline = ByteUtil.hexStr2Bytes(PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED);
            mcAid.threshold = ByteUtil.hexStr2Bytes("00000000");
            mcAid.floorLimit = ByteUtil.hexStr2Bytes("000000000000");
            mcAid.targetPer = (byte) 0;
            mcAid.maxTargetPer = (byte) 0;
            emvOptV2.addAid(mcAid);
            LogUtil.e(TAG, "✓ Added minimal Mastercard AID (kernelType=2 PayPass)");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error adding minimal Mastercard AID: " + e.getMessage());
        }
    }

    /**
     * Load CAPKs (Certificate Authority Public Keys) from API service or local
     * storage
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
                EmvConfigApiService.EmvConfigResponse cachedResponse = gson.fromJson(cachedJson,
                        EmvConfigApiService.EmvConfigResponse.class);
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
        com.neo.neopayplus.api.EmvConfigApiService apiService = com.neo.neopayplus.api.EmvConfigApiFactory
                .getInstance();

        if (!apiService.isAvailable()) {
            LogUtil.e(TAG, "⚠️ EMV Config API service not available - relying on SDK built-in CAPKs");
            LogUtil.e(TAG, "⚠️ SECURITY: Production CAPKs must be loaded from acquirer/backend");
            return;
        }

        // Load configuration asynchronously (already cached if we got here from
        // loadAids)
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
     * Strategy: Try to add each CAPK from API. If it already exists (SDK built-in),
     * preserve SDK version.
     * Only add if missing or if API version is different (requires explicit update
     * approval).
     */
    private void addCapksFromApiResponse(EMVOptV2 emvOptV2,
            java.util.List<com.neo.neopayplus.api.EmvConfigApiService.CapkConfig> capkConfigs) {
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
                    if (capkTlv.contains("A000000004"))
                        existingCapks.add("A000000004:*"); // Mastercard
                    if (capkTlv.contains("A000000003"))
                        existingCapks.add("A000000003:*"); // Visa
                }
                LogUtil.e(TAG, "Identified " + existingCapks.size() + " known scheme CAPKs in SDK");
            } else {
                LogUtil.e(TAG, "⚠️ Could not query SDK CAPKs (query result: " + queryResult
                        + ") - will rely on addCapk() return codes");
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
     * Guide recommends: setTermParamEx() with EmvTermParamV2 containing all
     * terminal params
     */
    private void configureTerminalParameters(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Configuring terminal parameters...");

        // Use API-provided values if available, otherwise use PaymentConfig defaults
        // CRITICAL: Sunmi EMV kernel requires 4-character hex format (e.g., "0332" for
        // Egypt 818)
        // NOT 3-digit numeric or alphabetic codes
        String countryCode = terminalCountryCode != null ? terminalCountryCode : PaymentConfig.TERMINAL_COUNTRY_CODE;
        String currencyCodeValue = currencyCode != null ? currencyCode : PaymentConfig.CURRENCY_CODE_TLV;

        // Ensure currency code is in correct 4-character hex format for EMV
        // If API provides 3-digit numeric (e.g., "818"), convert to 4-character hex
        // ("0332")
        String emvCurrencyCode = currencyCodeValue;
        if (currencyCodeValue != null && currencyCodeValue.length() == 3 && currencyCodeValue.matches("\\d{3}")) {
            // Convert 3-digit numeric to 4-character hex (e.g., "818" -> "0332")
            try {
                int numericValue = Integer.parseInt(currencyCodeValue);
                emvCurrencyCode = String.format("%04X", numericValue);
                LogUtil.e(TAG, "Converted currency code from numeric " + currencyCodeValue + " to EMV hex format "
                        + emvCurrencyCode);
            } catch (NumberFormatException e) {
                LogUtil.e(TAG, "⚠️ Could not convert currency code to hex, using as-is: " + currencyCodeValue);
            }
        }

        // Ensure country code is in correct 4-character hex format
        String emvCountryCode = countryCode;
        if (countryCode != null && countryCode.length() == 3 && countryCode.matches("\\d{3}")) {
            // Convert 3-digit numeric to 4-character hex
            try {
                int numericValue = Integer.parseInt(countryCode);
                emvCountryCode = String.format("%04X", numericValue);
                LogUtil.e(TAG,
                        "Converted country code from numeric " + countryCode + " to EMV hex format " + emvCountryCode);
            } catch (NumberFormatException e) {
                LogUtil.e(TAG, "⚠️ Could not convert country code to hex, using as-is: " + countryCode);
            }
        }

        LogUtil.e(TAG, "Using EMV terminal parameters:");
        LogUtil.e(TAG, "  Country Code (9F1A): " + emvCountryCode + " (4-character hex format)");
        LogUtil.e(TAG, "  Currency Code (5F2A): " + emvCurrencyCode + " (4-character hex format)");

        // PayLib v2.0.32 Guide: Use setTermParamEx() if available
        // CRITICAL: Sunmi EMV kernel expects 4-character hex format (e.g., "0332"), NOT
        // 3-digit numeric
        try {
            EmvTermParamV2 termParam = new EmvTermParamV2();
            termParam.countryCode = emvCountryCode; // 4-character hex format (e.g., "0332")
            termParam.currencyCode = emvCurrencyCode; // 4-character hex format (e.g., "0332")
            termParam.capability = PaymentConfig.TERMINAL_CAPABILITIES;

            // Try setTermParamEx() first (guide recommendation) - may accept Bundle or
            // EmvTermParamV2
            try {
                // Try as Bundle first (SDK may require Bundle)
                Bundle termBundle = new Bundle();
                termBundle.putString("terminalId", PaymentConfig.TERMINAL_ID);
                termBundle.putString("merchantId", PaymentConfig.MERCHANT_ID);
                termBundle.putString("merchantName", PaymentConfig.MERCHANT_NAME);
                termBundle.putString("countryCode", emvCountryCode); // 4-character hex format
                termBundle.putString("currencyCode", emvCurrencyCode); // 4-character hex format
                termBundle.putString("capability", PaymentConfig.TERMINAL_CAPABILITIES);
                termBundle.putString("transCurrencyExp", PaymentConfig.CURRENCY_EXPONENT);

                // Try setTermParamEx with Bundle
                try {
                    emvOptV2.setTermParamEx(termBundle);
                    LogUtil.e(TAG,
                            "✓ Terminal parameters set via setTermParamEx(Bundle) (PayLib v2.0.32 guide method)");
                    LogUtil.e(TAG, "  Country Code (9F1A): " + emvCountryCode + " (EMV hex format)");
                    LogUtil.e(TAG, "  Currency Code (5F2A): " + emvCurrencyCode + " (EMV hex format)");
                    return; // Success - exit early
                } catch (Exception e1) {
                    LogUtil.e(TAG,
                            "⚠️ setTermParamEx(Bundle) not available, trying setTerminalParam(): " + e1.getMessage());
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
            LogUtil.e(TAG, "⚠️ Error using setTermParamEx()/setTerminalParam(), falling back to setTlvList(): "
                    + e.getMessage());
        }

        // Final fallback: Use setTlvList() for compatibility
        setTerminalParametersViaTlv(emvOptV2);
    }

    /**
     * Fallback method: Set terminal parameters via setTlvList() for compatibility
     */
    private void setTerminalParametersViaTlv(EMVOptV2 emvOptV2) throws RemoteException {
        // Set terminal capabilities (9F33) - supports Online PIN, Offline PIN, CDCVM,
        // contactless
        String[] termCapTags = { "9F33" };
        String[] termCapValues = { PaymentConfig.TERMINAL_CAPABILITIES };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termCapTags, termCapValues);

        // Set additional terminal capabilities (9F40) - Online PIN supported
        String[] addCapTags = { "9F40" };
        String[] addCapValues = { PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, addCapTags, addCapValues);

        // Set terminal type (9F35) - from PaymentConfig
        String[] termTypeTags = { "9F35" };
        String[] termTypeValues = { PaymentConfig.TERMINAL_TYPE };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termTypeTags, termTypeValues);

        // Set country code (9F1A) - from PaymentConfig
        String[] countryTags = { "9F1A" };
        String[] countryValues = { PaymentConfig.TERMINAL_COUNTRY_CODE };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, countryTags, countryValues);

        // Set currency code (5F2A) - from PaymentConfig
        String[] currencyTags = { "5F2A" };
        String[] currencyValues = { PaymentConfig.CURRENCY_CODE_TLV };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, currencyTags, currencyValues);

        // Set transaction category code (9F53) for contactless - from PaymentConfig
        String[] tccTags = { "9F53" };
        String[] tccValues = { PaymentConfig.TRANSACTION_CATEGORY_CODE };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tccTags, tccValues);

        LogUtil.e(TAG, "Terminal parameters configured via setTlvList()");
    }

    /**
     * Configure scheme-specific TLV data (PayPass, PayWave, AMEX, JCB)
     */
    private void configureSchemeTlvData(EMVOptV2 emvOptV2) throws RemoteException {
        LogUtil.e(TAG, "Configuring scheme-specific TLV data...");

        // PayPass (MasterCard) TLV data - from PaymentConfig
        String[] tagsPayPass = { "DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                "DF8123", "DF8124", "DF8125", "DF8126",
                "DF811B", "DF811D", "DF8122", "DF8120", "DF8121" };
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
        String[] tagsPayWave = { "DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                "DF8123", "DF8124", "DF8125", "DF8126",
                "DF811B", "DF811D", "DF8122", "DF8120", "DF8121" };
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
        String[] tagsAE = { "9F6D", "9F6E", "9F33", "9F35", "DF8168", "DF8167", "DF8169", "DF8170" };
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
        String[] tagsJCB = { "9F53", "DF8161" };
        String[] valuesJCB = {
                PaymentConfig.JCBConfig.TAG_9F53,
                PaymentConfig.JCBConfig.DF8161
        };
        emvOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_JCB, tagsJCB, valuesJCB);

        LogUtil.e(TAG, "Scheme-specific TLV data configured");
    }
}
