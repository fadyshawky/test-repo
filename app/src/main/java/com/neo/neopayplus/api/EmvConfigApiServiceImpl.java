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
    private static final String ROUTE_EMV_CONFIG = "/emv/bundle";

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
     * @param apiKey  API key for authentication
     */
    public EmvConfigApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
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
            LogUtil.e(TAG,
                    "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
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

    private static String normalizeBaseUrl(String raw) {
        if (raw == null)
            return null;
        String trimmed = raw.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private String buildUrl(String route) {
        if (baseUrl == null || route == null)
            return null;
        String normalizedRoute = route;
        if (baseUrl.endsWith("/v1") && route.startsWith("/v1")) {
            normalizedRoute = route.substring(3);
        }
        return baseUrl + normalizedRoute;
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
            String url = buildUrl(ROUTE_EMV_CONFIG);
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
                        if (response.body() != null)
                            response.body().close();
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

        // Check for "ok" field in new API format
        if (json.has("ok") && !json.get("ok").getAsBoolean()) {
            throw new IOException("API returned ok=false");
        }

        EmvConfigResponse response = EmvConfigResponse.success();

        // New API format: /emv/bundle returns aids_flat (flat list) or aids
        // (hierarchical)
        // Prefer aids_flat as it matches /emv/aids structure
        JsonArray aidsArray = null;
        if (json.has("aids_flat") && json.get("aids_flat").isJsonArray()) {
            aidsArray = json.getAsJsonArray("aids_flat");
            LogUtil.e(TAG, "Using aids_flat from bundle response");
        } else if (json.has("aids") && json.get("aids").isJsonArray()) {
            aidsArray = json.getAsJsonArray("aids");
            LogUtil.e(TAG, "Using aids (hierarchical) from bundle response");
        }

        if (aidsArray != null) {
            response.aids = parseAids(aidsArray);
        }

        if (json.has("capks") && json.get("capks").isJsonArray()) {
            response.capks = parseCapks(json.getAsJsonArray("capks"));
        }

        // Terminal parameters (from spec: currency_code, country_code, merchant_name,
        // merchant_id at root)
        if (json.has("currency_code")) {
            String currencyCode = json.get("currency_code").getAsString();
            // Store full 4-digit code for EMV TLV (e.g., "0840")
            // Extract 3-digit for setTermParamEx if needed (done in
            // EmvConfigurationManager)
            response.currencyCode = currencyCode; // Keep full value: "0840"
            LogUtil.e(TAG, "Parsed currency_code from API: " + currencyCode);
        }
        if (json.has("country_code")) {
            String countryCode = json.get("country_code").getAsString();
            response.terminalCountryCode = countryCode; // Keep full value: "0840"
            LogUtil.e(TAG, "Parsed country_code from API: " + countryCode);
        }
        // Note: terminalType, terminalCapabilities etc. not in spec, using defaults
        // from PaymentConfig
        response.terminalType = PaymentConfig.TERMINAL_TYPE;
        response.terminalCapabilities = PaymentConfig.TERMINAL_CAPABILITIES;
        response.additionalTerminalCapabilities = PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES;
        response.transactionCategoryCode = PaymentConfig.TRANSACTION_CATEGORY_CODE;

        // Parse ISO 8583 Socket Configuration
        if (json.has("iso_socket") && json.get("iso_socket").isJsonObject()) {
            com.google.gson.JsonObject isoSocket = json.getAsJsonObject("iso_socket");
            if (isoSocket.has("host") && isoSocket.has("port")) {
                String isoHost = isoSocket.get("host").getAsString();
                int isoPort = isoSocket.get("port").getAsInt();
                boolean isoEnabled = isoSocket.has("enabled") && isoSocket.get("enabled").getAsBoolean();

                if (isoEnabled && isoHost != null && !isoHost.isEmpty() && isoPort > 0) {
                    // Set ISO socket configuration in PaymentConfig
                    PaymentConfig.ISO_SOCKET_HOST = isoHost;
                    PaymentConfig.ISO_SOCKET_PORT = isoPort;
                    // Save to cache for persistence
                    PaymentConfig.saveIsoSocketConfigToCache(isoHost, isoPort);
                    LogUtil.e(TAG, "✓ ISO 8583 Socket Configuration loaded:");
                    LogUtil.e(TAG, "  Host: " + isoHost);
                    LogUtil.e(TAG, "  Port: " + isoPort);
                    LogUtil.e(TAG, "  Mode: ENABLED");
                } else {
                    // Disable ISO socket mode
                    PaymentConfig.ISO_SOCKET_HOST = null;
                    PaymentConfig.ISO_SOCKET_PORT = 0;
                    PaymentConfig.saveIsoSocketConfigToCache(null, 0);
                    LogUtil.e(TAG, "⚠️ ISO 8583 Socket Configuration disabled or invalid");
                }
            }
        } else {
            // No ISO socket config - disable
            PaymentConfig.ISO_SOCKET_HOST = null;
            PaymentConfig.ISO_SOCKET_PORT = 0;
            LogUtil.e(TAG, "⚠️ No ISO 8583 Socket Configuration in response - using HTTP/JSON mode");
        }

        return response;
    }

    private List<AidConfig> parseAids(JsonArray aidsArray) throws Exception {
        List<AidConfig> aids = new ArrayList<>();
        for (int i = 0; i < aidsArray.size(); i++) {
            JsonObject aidJson = aidsArray.get(i).getAsJsonObject();
            AidConfig aid = new AidConfig();

            // New API format: "aid", "issuer", "label", "description", "kernel", "pix",
            // "rid"
            aid.aidHex = aidJson.has("aid") ? aidJson.get("aid").getAsString()
                    : (aidJson.has("aidHex") ? aidJson.get("aidHex").getAsString() : "");

            // Label and kernel from new API format
            aid.label = aidJson.has("label") ? aidJson.get("label").getAsString() : null;
            String kernelStr = aidJson.has("kernel") ? aidJson.get("kernel").getAsString() : "EMV";
            // Map kernel string to internal format: "VISA" -> "VISA", "MC" -> "MC", etc.
            aid.kernel = kernelStr;

            // Determine selFlag based on kernel type (VISA/MC contactless kernels = 1, EMV
            // = 0)
            // For new API, assume contactless-enabled if kernel is VISA or MC
            if (kernelStr.equals("VISA") || kernelStr.equals("MC")) {
                aid.selFlag = 1; // Contactless enabled
            } else {
                aid.selFlag = aidJson.has("selFlag") ? aidJson.get("selFlag").getAsInt() : 0;
            }

            aid.priority = aidJson.has("priority") ? aidJson.get("priority").getAsInt() : 1;

            // Version: use scheme-specific defaults if not provided
            // Mastercard PayPass: Use API-provided version, or "0002" as default
            // Visa payWave typically uses "0097"
            String defaultVersion = "008C"; // Generic EMV default
            if (aid.aidHex != null) {
                if (aid.aidHex.startsWith("A000000004") || aid.aidHex.startsWith("A000000005") ||
                        aid.aidHex.startsWith("A000000732")) {
                    defaultVersion = "0002"; // Mastercard PayPass / Meeza
                } else if (aid.aidHex.startsWith("A000000003")) {
                    defaultVersion = "0097"; // Visa payWave
                }
            }
            aid.version = aidJson.has("version") ? aidJson.get("version").getAsString() : defaultVersion;

            if (!aidJson.has("version")) {
                LogUtil.e(TAG, "  Using default version: " + defaultVersion + " for AID " + aid.aidHex);
            }

            // TAC fields (may not be in new API, use defaults)
            aid.tacDefault = aidJson.has("tac_default") ? aidJson.get("tac_default").getAsString()
                    : (aidJson.has("tacDefault") ? aidJson.get("tacDefault").getAsString() : "0010000000");
            aid.tacDenial = aidJson.has("tac_denial") ? aidJson.get("tac_denial").getAsString()
                    : (aidJson.has("tacDenial") ? aidJson.get("tacDenial").getAsString() : "0000000000");
            aid.tacOnline = aidJson.has("tac_online") ? aidJson.get("tac_online").getAsString()
                    : (aidJson.has("tacOnline") ? aidJson.get("tacOnline").getAsString() : "0010000000");

            // Floor limit and threshold
            aid.threshold = aidJson.has("threshold") ? aidJson.get("threshold").getAsString()
                    : PaymentConfig.TACConfig.THRESHOLD_ZERO;
            aid.floorLimit = aidJson.has("floorLimit") ? aidJson.get("floorLimit").getAsString()
                    : "000000000000";
            aid.targetPer = aidJson.has("targetPer") ? aidJson.get("targetPer").getAsInt() : 0;
            aid.maxTargetPer = aidJson.has("maxTargetPer") ? aidJson.get("maxTargetPer").getAsInt() : 0;

            // Contactless-specific parameters (may not be in new API format)
            aid.ttq = aidJson.has("ttq") ? aidJson.get("ttq").getAsString() : "2600C080";
            aid.ctq = aidJson.has("ctq") ? aidJson.get("ctq").getAsString() : "00000000";
            aid.noCvmLimit = aidJson.has("noCvmLimit") ? aidJson.get("noCvmLimit").getAsString() : "000000000000";
            aid.cvmLimit = aidJson.has("cvmLimit") ? aidJson.get("cvmLimit").getAsString() : "000000000000";
            aid.contactlessFloorLimit = aidJson.has("contactlessFloorLimit")
                    ? aidJson.get("contactlessFloorLimit").getAsString()
                    : "000000000000";

            // Parse nested contactless object if present (for backward compatibility)
            if (aidJson.has("contactless") && aidJson.get("contactless").isJsonObject()) {
                JsonObject ctls = aidJson.getAsJsonObject("contactless");
                if (ctls.has("ttq"))
                    aid.ttq = ctls.get("ttq").getAsString();
                if (ctls.has("ctq"))
                    aid.ctq = ctls.get("ctq").getAsString();

                // Parse merchantRiskParameters if present
                if (ctls.has("merchantRiskParameters") && ctls.get("merchantRiskParameters").isJsonObject()) {
                    JsonObject mrp = ctls.getAsJsonObject("merchantRiskParameters");
                    if (mrp.has("contactless_no_cvm_limit"))
                        aid.noCvmLimit = mrp.get("contactless_no_cvm_limit").getAsString();
                    if (mrp.has("contactless_cvm_limit"))
                        aid.cvmLimit = mrp.get("contactless_cvm_limit").getAsString();
                    if (mrp.has("reader_contactless_floor_limit"))
                        aid.contactlessFloorLimit = mrp.get("reader_contactless_floor_limit").getAsString();
                }
            }

            // DOL fields (may not be in new API)
            // Handle both lowercase and camelCase (backend may send "dDOL")
            aid.ddol = aidJson.has("dDOL") ? aidJson.get("dDOL").getAsString()
                    : (aidJson.has("ddol") ? aidJson.get("ddol").getAsString() : "");
            aid.tdol = aidJson.has("tDOL") ? aidJson.get("tDOL").getAsString()
                    : (aidJson.has("tdol") ? aidJson.get("tdol").getAsString() : "");
            aid.udol = aidJson.has("uDOL") ? aidJson.get("uDOL").getAsString()
                    : (aidJson.has("udol") ? aidJson.get("udol").getAsString() : "");

            if (!aid.ddol.isEmpty()) {
                LogUtil.e(TAG, "  Parsed dDOL: " + aid.ddol + " (length=" + aid.ddol.length() + ")");
            }

            LogUtil.e(TAG, "Parsed AID: " + aid.aidHex + " issuer=" +
                    (aidJson.has("issuer") ? aidJson.get("issuer").getAsString() : "N/A") +
                    " selFlag=" + aid.selFlag + " kernel=" + aid.kernel);
            aids.add(aid);
        }
        return aids;
    }

    private List<CapkConfig> parseCapks(JsonArray capksArray) throws Exception {
        List<CapkConfig> capks = new ArrayList<>();
        for (int i = 0; i < capksArray.size(); i++) {
            JsonObject capkJson = capksArray.get(i).getAsJsonObject();
            CapkConfig capk = new CapkConfig();

            // New API format: "brand", "exponent", "index", "rid", "modulus", "checksum",
            // "sha1", "status", "expiry"
            capk.ridHex = capkJson.has("rid") ? capkJson.get("rid").getAsString()
                    : (capkJson.has("ridHex") ? capkJson.get("ridHex").getAsString() : "");
            capk.indexHex = capkJson.has("index") ? capkJson.get("index").getAsString()
                    : (capkJson.has("indexHex") ? capkJson.get("indexHex").getAsString() : "");
            capk.modulusHex = capkJson.has("modulus") ? capkJson.get("modulus").getAsString()
                    : (capkJson.has("modulusHex") ? capkJson.get("modulusHex").getAsString() : "");
            capk.exponentHex = capkJson.has("exponent") ? capkJson.get("exponent").getAsString()
                    : (capkJson.has("exponentHex") ? capkJson.get("exponentHex").getAsString() : "");

            // New API uses "expiry" as ISO 8601 date (e.g., "2028-12-31")
            // Convert to YYMMDD format if needed, or use as-is if already in correct format
            String expiryStr = capkJson.has("expiry") ? capkJson.get("expiry").getAsString()
                    : (capkJson.has("expiryDate") ? capkJson.get("expiryDate").getAsString() : "");
            if (expiryStr != null && !expiryStr.isEmpty()) {
                // If ISO format (YYYY-MM-DD), convert to YYMMDD
                if (expiryStr.length() == 10 && expiryStr.contains("-")) {
                    String year = expiryStr.substring(2, 4); // YY
                    String month = expiryStr.substring(5, 7); // MM
                    String day = expiryStr.substring(8, 10); // DD
                    capk.expiryDate = year + month + day;
                } else {
                    capk.expiryDate = expiryStr; // Assume already in YYMMDD format
                }
            } else {
                capk.expiryDate = "";
            }

            // Optional fields with defaults
            capk.hashIndHex = capkJson.has("hashIndHex") ? capkJson.get("hashIndHex").getAsString() : "01"; // Default
                                                                                                            // SHA-1
            capk.arithIndHex = capkJson.has("arithIndHex") ? capkJson.get("arithIndHex").getAsString() : "01"; // Default
                                                                                                               // RSA

            // Log brand and status for debugging
            String brand = capkJson.has("brand") ? capkJson.get("brand").getAsString() : "N/A";
            String status = capkJson.has("status") ? capkJson.get("status").getAsString() : "N/A";
            LogUtil.e(TAG, "Parsed CAPK: brand=" + brand + " rid=" + capk.ridHex +
                    " index=" + capk.indexHex + " status=" + status);

            capks.add(capk);
        }
        return capks;
    }

    private List<AidConfig> loadStandardAids() {
        List<AidConfig> aids = new ArrayList<>();

        // VISA Credit/Debit - selFlag=1 enables contactless
        AidConfig visaAid = new AidConfig();
        visaAid.aidHex = "A0000000031010";
        visaAid.label = "VISA Credit/Debit";
        visaAid.kernel = "EMV";
        visaAid.selFlag = 1; // CRITICAL: 1 = contactless enabled
        visaAid.priority = 1;
        visaAid.version = "008C";
        visaAid.tacDefault = "0010000000";
        visaAid.tacDenial = "0000000000";
        visaAid.tacOnline = "0010000000";
        visaAid.threshold = "00000000";
        visaAid.floorLimit = "000000000000";
        visaAid.ttq = "2600C080";
        visaAid.ctq = "00000000";
        visaAid.noCvmLimit = "000000000000";
        visaAid.cvmLimit = "000000000000";
        visaAid.contactlessFloorLimit = "000000000000";
        aids.add(visaAid);

        // Mastercard Credit/Debit - selFlag=1 enables contactless
        AidConfig mcAid = new AidConfig();
        mcAid.aidHex = "A0000000041010";
        mcAid.label = "Mastercard Credit/Debit";
        mcAid.kernel = "EMV";
        mcAid.selFlag = 1; // CRITICAL: 1 = contactless enabled
        mcAid.priority = 1;
        mcAid.version = "008C";
        mcAid.tacDefault = "0010000000";
        mcAid.tacDenial = "0000000000";
        mcAid.tacOnline = "0010000000";
        mcAid.threshold = "00000000";
        mcAid.floorLimit = "000000000000";
        mcAid.ttq = "2600C080";
        mcAid.ctq = "00000000";
        mcAid.noCvmLimit = "000000000000";
        mcAid.cvmLimit = "000000000000";
        mcAid.contactlessFloorLimit = "000000000000";
        aids.add(mcAid);

        // AMEX - selFlag=1 enables contactless
        AidConfig amexAid = new AidConfig();
        amexAid.aidHex = "A0000000250108"; // AMEX ExpressPay
        amexAid.label = "American Express";
        amexAid.kernel = "EMV";
        amexAid.selFlag = 1; // CRITICAL: 1 = contactless enabled
        amexAid.priority = 1;
        amexAid.version = "008C";
        amexAid.tacDefault = "0010000000";
        amexAid.tacDenial = "0000000000";
        amexAid.tacOnline = "0010000000";
        amexAid.threshold = "00000000";
        amexAid.floorLimit = "000000000000";
        amexAid.ttq = "2600C080";
        amexAid.ctq = "00000000";
        amexAid.noCvmLimit = "000000000000";
        amexAid.cvmLimit = "000000000000";
        amexAid.contactlessFloorLimit = "000000000000";
        aids.add(amexAid);

        return aids;
    }
}
