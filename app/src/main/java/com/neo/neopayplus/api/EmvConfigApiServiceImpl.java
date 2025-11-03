package com.neo.neopayplus.api;

import android.os.Handler;
import android.os.Looper;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.utils.LogUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * EMV Configuration API Service Implementation
 * 
 * Unified implementation that uses production APIs when baseUrl is configured,
 * otherwise falls back to mock responses for testing/development.
 */
public class EmvConfigApiServiceImpl implements EmvConfigApiService {
    
    private static final String TAG = Constant.TAG;
    
    // API routes/endpoints (matching actual server endpoints)
    // Note: Base URL already includes /v1, so route is just /terminal/config
    private static final String ROUTE_EMV_CONFIG = "/terminal/config";
    
    // Configuration
    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient httpClient;
    
    // Mock mode helpers (used when baseUrl not configured)
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean simulateNetworkDelay = true;
    private long networkDelayMs = 500;
    
    /**
     * Create service with default configuration (mock mode)
     */
    public EmvConfigApiServiceImpl() {
        this(null, null);
    }
    
    /**
     * Create service with baseUrl and apiKey
     * If baseUrl is null or placeholder, uses mock responses
     * 
     * @param baseUrl Backend base URL (e.g., "https://api.backend.com")
     * @param apiKey API key for authentication
     */
    public EmvConfigApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        
        // Initialize HTTP client only if baseUrl is configured
        if (isProductionMode()) {
            this.httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            LogUtil.e(TAG, "EMV Config API Service initialized - PRODUCTION mode");
            LogUtil.e(TAG, "  Base URL: " + baseUrl);
            LogUtil.e(TAG, "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
        } else {
            this.httpClient = null;
            LogUtil.e(TAG, "EMV Config API Service initialized - MOCK mode (baseUrl not configured)");
        }
    }
    
    /**
     * Check if service is in production mode (baseUrl configured)
     */
    private boolean isProductionMode() {
        return baseUrl != null && !baseUrl.isEmpty() && 
               !baseUrl.contains("yourbackend.com") &&
               !baseUrl.equals("https://api.yourbackend.com");
    }
    
    /**
     * Set mock configuration (only used in mock mode)
     */
    public void setMockConfiguration(boolean simulateDelay, long delayMs) {
        this.simulateNetworkDelay = simulateDelay;
        this.networkDelayMs = delayMs;
    }
    
    @Override
    public void loadEmvConfiguration(EmvConfigCallback callback) {
        if (isProductionMode()) {
            callProductionApi(callback);
        } else {
            callMockApi(callback);
        }
    }
    
    @Override
    public boolean isAvailable() {
        if (isProductionMode()) {
            // Production: check if API key is configured
            return apiKey != null && !apiKey.isEmpty() && !apiKey.contains("YOUR_API_KEY");
        } else {
            // Mock: always available
            return true;
        }
    }
    
    /**
     * Call production API endpoint
     */
    private void callProductionApi(EmvConfigCallback callback) {
        LogUtil.e(TAG, "=== API: Loading EMV Configuration ===");
        
        try {
            String url = baseUrl + ROUTE_EMV_CONFIG;
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .get()
                    .build();
            
            LogUtil.e(TAG, "Calling API: " + url);
            
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ API Error: " + e.getMessage());
                    callback.onConfigError(e);
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            callback.onConfigError(new IOException("HTTP " + response.code() + ": " + errorBody));
                            return;
                        }
                        
                        String responseBody = response.body() != null ? response.body().string() : "{}";
                        LogUtil.e(TAG, "=== API Response ===");
                        LogUtil.e(TAG, responseBody);
                        
                        EmvConfigResponse configResponse = parseConfigResponse(responseBody);
                        callback.onConfigLoaded(configResponse);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing API response: " + e.getMessage());
                        callback.onConfigError(e);
                    } finally {
                        if (response.body() != null) response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building API request: " + e.getMessage());
            callback.onConfigError(e);
        }
    }
    
    /**
     * Call mock API (returns mock responses)
     */
    private void callMockApi(EmvConfigCallback callback) {
        LogUtil.e(TAG, "=== MOCK: Loading EMV Configuration ===");
        
        new Thread(() -> {
            if (simulateNetworkDelay) {
                try {
                    Thread.sleep(networkDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            EmvConfigResponse response = EmvConfigResponse.success();
            response.aids = loadStandardAids();
            response.capks = new ArrayList<>(); // Empty - rely on SDK built-in CAPKs
            response.terminalCountryCode = PaymentConfig.TERMINAL_COUNTRY_CODE;
            response.currencyCode = PaymentConfig.CURRENCY_CODE;
            response.terminalType = PaymentConfig.TERMINAL_TYPE;
            response.terminalCapabilities = PaymentConfig.TERMINAL_CAPABILITIES;
            response.additionalTerminalCapabilities = PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES;
            response.transactionCategoryCode = PaymentConfig.TRANSACTION_CATEGORY_CODE;
            
            LogUtil.e(TAG, "=== MOCK: EMV Configuration Loaded ===");
            LogUtil.e(TAG, "  AIDs: " + response.aids.size());
            LogUtil.e(TAG, "  CAPKs: " + response.capks.size() + " (using SDK built-in)");
            
            final EmvConfigResponse finalResponse = response;
            mainHandler.post(() -> {
                if (finalResponse.error != null) {
                    callback.onConfigError(finalResponse.error);
                } else {
                    callback.onConfigLoaded(finalResponse);
                }
            });
        }).start();
    }
    
    private EmvConfigResponse parseConfigResponse(String responseBody) throws Exception {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);
        
        EmvConfigResponse response = EmvConfigResponse.success();
        
        // Backend API spec format: /terminal/config returns aids, capks, terminal params at root level
        if (json.has("aids") && json.get("aids").isJsonArray()) {
            response.aids = parseAids(json.getAsJsonArray("aids"));
        }
        
        if (json.has("capks") && json.get("capks").isJsonArray()) {
            response.capks = parseCapks(json.getAsJsonArray("capks"));
        }
        
        // Terminal parameters (from spec: currency_code, country_code, merchant_name, merchant_id at root)
        if (json.has("currency_code")) {
            String currencyCode = json.get("currency_code").getAsString();
            // Store full 4-digit code for EMV TLV (e.g., "0840")
            // Extract 3-digit for setTermParamEx if needed (done in EmvConfigurationManager)
            response.currencyCode = currencyCode; // Keep full value: "0840"
            LogUtil.e(TAG, "Parsed currency_code from API: " + currencyCode);
        }
        if (json.has("country_code")) {
            String countryCode = json.get("country_code").getAsString();
            response.terminalCountryCode = countryCode; // Keep full value: "0840"
            LogUtil.e(TAG, "Parsed country_code from API: " + countryCode);
        }
        // Note: terminalType, terminalCapabilities etc. not in spec, using defaults from PaymentConfig
        response.terminalType = PaymentConfig.TERMINAL_TYPE;
        response.terminalCapabilities = PaymentConfig.TERMINAL_CAPABILITIES;
        response.additionalTerminalCapabilities = PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES;
        response.transactionCategoryCode = PaymentConfig.TRANSACTION_CATEGORY_CODE;
        
        return response;
    }
    
    private List<AidConfig> parseAids(JsonArray aidsArray) throws Exception {
        List<AidConfig> aids = new ArrayList<>();
        for (int i = 0; i < aidsArray.size(); i++) {
            JsonObject aidJson = aidsArray.get(i).getAsJsonObject();
            AidConfig aid = new AidConfig();
            
            // Backend API spec format: "aid", "version", "tac_default", "tac_online", "tac_denial"
            aid.aidHex = aidJson.has("aid") ? aidJson.get("aid").getAsString() : 
                        (aidJson.has("aidHex") ? aidJson.get("aidHex").getAsString() : "");
            aid.version = aidJson.has("version") ? aidJson.get("version").getAsString() : "008C";
            
            // Label for identification (optional)
            aid.label = aidJson.has("label") ? aidJson.get("label").getAsString() : null;
            
            // selFlag defaults to 0 (required for contactless - partial name matching)
            // If API provides selFlag, use it, but it will be overridden to 0 for contactless AIDs
            aid.selFlag = aidJson.has("selFlag") ? aidJson.get("selFlag").getAsInt() : 0;
            
            // TAC fields (spec uses snake_case)
            aid.tacDefault = aidJson.has("tac_default") ? aidJson.get("tac_default").getAsString() : 
                            (aidJson.has("tacDefault") ? aidJson.get("tacDefault").getAsString() : "");
            aid.tacDenial = aidJson.has("tac_denial") ? aidJson.get("tac_denial").getAsString() : 
                           (aidJson.has("tacDenial") ? aidJson.get("tacDenial").getAsString() : "");
            aid.tacOnline = aidJson.has("tac_online") ? aidJson.get("tac_online").getAsString() : 
                           (aidJson.has("tacOnline") ? aidJson.get("tacOnline").getAsString() : "");
            
            // Optional fields with defaults
            aid.threshold = aidJson.has("threshold") ? aidJson.get("threshold").getAsString() : PaymentConfig.TACConfig.THRESHOLD_ZERO;
            aid.floorLimit = aidJson.has("floorLimit") ? aidJson.get("floorLimit").getAsString() : PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO;
            aid.targetPer = aidJson.has("targetPer") ? aidJson.get("targetPer").getAsInt() : 0;
            aid.maxTargetPer = aidJson.has("maxTargetPer") ? aidJson.get("maxTargetPer").getAsInt() : 0;
            
            aids.add(aid);
        }
        return aids;
    }
    
    private List<CapkConfig> parseCapks(JsonArray capksArray) throws Exception {
        List<CapkConfig> capks = new ArrayList<>();
        for (int i = 0; i < capksArray.size(); i++) {
            JsonObject capkJson = capksArray.get(i).getAsJsonObject();
            CapkConfig capk = new CapkConfig();
            
            // Backend API spec format: "rid", "index", "modulus", "exponent", "expiry"
            capk.ridHex = capkJson.has("rid") ? capkJson.get("rid").getAsString() : 
                         (capkJson.has("ridHex") ? capkJson.get("ridHex").getAsString() : "");
            capk.indexHex = capkJson.has("index") ? capkJson.get("index").getAsString() : 
                           (capkJson.has("indexHex") ? capkJson.get("indexHex").getAsString() : "");
            capk.modulusHex = capkJson.has("modulus") ? capkJson.get("modulus").getAsString() : 
                             (capkJson.has("modulusHex") ? capkJson.get("modulusHex").getAsString() : "");
            capk.exponentHex = capkJson.has("exponent") ? capkJson.get("exponent").getAsString() : 
                              (capkJson.has("exponentHex") ? capkJson.get("exponentHex").getAsString() : "");
            capk.expiryDate = capkJson.has("expiry") ? capkJson.get("expiry").getAsString() : 
                             (capkJson.has("expiryDate") ? capkJson.get("expiryDate").getAsString() : "");
            
            // Optional fields with defaults
            capk.hashIndHex = capkJson.has("hashIndHex") ? capkJson.get("hashIndHex").getAsString() : "01"; // Default SHA-1
            capk.arithIndHex = capkJson.has("arithIndHex") ? capkJson.get("arithIndHex").getAsString() : "01"; // Default RSA
            capks.add(capk);
        }
        return capks;
    }
    
    private List<AidConfig> loadStandardAids() {
        List<AidConfig> aids = new ArrayList<>();
        
        AidConfig visaAid = new AidConfig();
        visaAid.aidHex = "A0000000031010";
        visaAid.selFlag = 0;
        visaAid.version = "008C"; // Required by PayLib v2.0.32
        visaAid.tacDefault = PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED;
        visaAid.tacDenial = PaymentConfig.TACConfig.TAC_DENIAL;
        visaAid.tacOnline = PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED;
        visaAid.threshold = PaymentConfig.TACConfig.THRESHOLD_ZERO;
        visaAid.floorLimit = PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO;
        aids.add(visaAid);
        
        AidConfig mcAid = new AidConfig();
        mcAid.aidHex = "A0000000041010";
        mcAid.selFlag = 0;
        mcAid.version = "008C"; // Required by PayLib v2.0.32
        mcAid.tacDefault = PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED;
        mcAid.tacDenial = PaymentConfig.TACConfig.TAC_DENIAL;
        mcAid.tacOnline = PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED;
        mcAid.threshold = PaymentConfig.TACConfig.THRESHOLD_ZERO;
        mcAid.floorLimit = PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO;
        aids.add(mcAid);
        
        AidConfig amexAid = new AidConfig();
        amexAid.aidHex = "A0000000043060";
        amexAid.selFlag = 0;
        amexAid.version = "008C"; // Required by PayLib v2.0.32
        amexAid.tacDefault = PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED;
        amexAid.tacDenial = PaymentConfig.TACConfig.TAC_DENIAL;
        amexAid.tacOnline = PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED;
        amexAid.threshold = PaymentConfig.TACConfig.THRESHOLD_ZERO;
        amexAid.floorLimit = PaymentConfig.TACConfig.FLOOR_LIMIT_ZERO;
        aids.add(amexAid);
        
        return aids;
    }
}
