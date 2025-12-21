package com.neo.neopayplus.api;

import android.os.Handler;
import android.os.Looper;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.keys.KeyRegistry;
import com.neo.neopayplus.iso.Iso8583Packer;
import com.neo.neopayplus.iso.IsoLogger;
import com.neo.neopayplus.iso.Iso8583SocketClient;
import com.neo.neopayplus.iso.Iso8583MessageBuilder;
import com.neo.neopayplus.iso.Iso8583ResponseParser;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Payment API Service Implementation
 * 
 * Unified implementation that uses production APIs when baseUrl is configured,
 * otherwise falls back to mock responses for testing/development.
 */
public class PaymentApiServiceImpl implements PaymentApiService {

    private static final String TAG = Constant.TAG;

    // API routes/endpoints (matching actual server endpoints)
    private static final String ROUTE_AUTHORIZE = "/tx/authorize";
    private static final String ROUTE_KEY_ROTATION = "/keys/rotate";
    private static final String ROUTE_KEY_ANNOUNCE = "/keys/announce";
    private static final String ROUTE_REGISTER_TERMINAL = "/keys/register";
    private static final String ROUTE_PROVISION_TMK = "/keys/provision/tmk";
    private static final String ROUTE_PROVISION_TPK = "/keys/provision/tpk";
    private static final String ROUTE_DUKPT_KEYS = "/keys/provision/dukpt";
    private static final String ROUTE_REVERSAL = "/tx/reverse";

    // Configuration
    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient httpClient;

    // Mock mode helpers (used when baseUrl not configured)
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private boolean simulateNetworkDelay = true;
    private long networkDelayMs = 150; // 100-300ms per spec
    private double approvalRate = 0.95;
    private int transactionCounter = 0; // For tracking every 10th transaction (key sync)

    private static final MediaType JSON = MediaType.parse("application/json");

    /**
     * Create service with default configuration (mock mode)
     */
    public PaymentApiServiceImpl() {
        this(null, null);
    }

    /**
     * Create service with baseUrl and apiKey
     * If baseUrl is null or placeholder, uses mock responses
     * 
     * @param baseUrl Backend base URL (e.g., "https://api.backend.com")
     * @param apiKey  API key for authentication
     */
    public PaymentApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.apiKey = apiKey;

        // Initialize HTTP client only if baseUrl is configured
        if (isProductionMode()) {
            this.httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            LogUtil.e(TAG, "Payment API Service initialized - PRODUCTION mode");
            LogUtil.e(TAG, "  Base URL: " + baseUrl);
            LogUtil.e(TAG,
                    "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
        } else {
            this.httpClient = null;
            LogUtil.e(TAG, "Payment API Service initialized - MOCK mode (baseUrl not configured)");
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
    public void setMockConfiguration(boolean simulateDelay, long delayMs, double approvalRate) {
        this.simulateNetworkDelay = simulateDelay;
        this.networkDelayMs = delayMs;
        this.approvalRate = Math.max(0.0, Math.min(1.0, approvalRate));
    }

    @Override
    public void authorizeTransaction(AuthorizationRequest request, AuthorizationCallback callback) {
        if (isProductionMode()) {
            callProductionApi(request, callback);
        } else {
            callMockApi(request, callback);
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
     * Sends real data from terminal (PIN block, KSN, EMV data) to backend
     * Backend will decrypt PIN block using DUKPT keys (IPEK + KSN) and verify PIN
     */
    private void callProductionApi(AuthorizationRequest request, AuthorizationCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION API: Authorization Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + PaymentConfig.getTerminalId());
        LogUtil.e(TAG, "  PAN: " + (request.pan != null ? maskCardNumber(request.pan) : "null"));
        LogUtil.e(TAG, "  Amount: " + request.amount);
        LogUtil.e(TAG, "  Has PIN Block: " + (request.pinBlock != null && request.pinBlock.length > 0));
        LogUtil.e(TAG, "  KSN: " + (request.ksn != null ? request.ksn : "null"));
        LogUtil.e(TAG, "  Has EMV Data: " + (request.field55 != null && !request.field55.isEmpty()));

        // Check if ISO socket mode is enabled
        boolean isIsoMode = PaymentConfig.isIsoSocketMode();
        LogUtil.e(TAG, "=== ISO Socket Mode Check ===");
        LogUtil.e(TAG, "  ISO_SOCKET_HOST: "
                + (PaymentConfig.ISO_SOCKET_HOST != null ? PaymentConfig.ISO_SOCKET_HOST : "null"));
        LogUtil.e(TAG, "  ISO_SOCKET_PORT: " + PaymentConfig.ISO_SOCKET_PORT);
        LogUtil.e(TAG, "  isIsoSocketMode(): " + isIsoMode);

        if (isIsoMode) {
            LogUtil.e(TAG, "✓ Using ISO 8583 Socket Mode");
            callProductionApiIsoSocket(request, callback);
            return;
        } else {
            LogUtil.e(TAG, "⚠️ ISO Socket Mode disabled - using HTTP/JSON mode");
        }

        // Fall back to HTTP/JSON mode
        try {
            JsonObject requestJson = buildRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);

            String url = buildUrl(ROUTE_AUTHORIZE);
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .post(requestBody)
                    .build();

            LogUtil.e(TAG, "Calling production API: " + url);
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                LogUtil.e(TAG, "Request body (masked PIN/KSN): " + maskSensitiveData(requestBodyStr));
            }

            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ Production API Error: " + e.getMessage());
                    callback.onAuthorizationError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            LogUtil.e(TAG, "❌ Production API Error: HTTP " + response.code() + " - " + errorBody);
                            callback.onAuthorizationError(
                                    new IOException("HTTP " + response.code() + ": " + errorBody));
                            return;
                        }

                        String responseBody = response.body() != null ? response.body().string() : "{}";
                        LogUtil.e(TAG, "=== Production API Response ===");
                        LogUtil.e(TAG, responseBody);

                        // Parse real response from backend (includes PIN verification result)
                        AuthorizationResponse authResponse = parseResponse(responseBody);

                        // Log PIN verification result (if applicable)
                        if (request.pinBlock != null && request.pinBlock.length > 0) {
                            if (authResponse.approved) {
                                LogUtil.e(TAG, "✓ PIN verified successfully by backend");
                            } else if ("55".equals(authResponse.responseCode)
                                    || "63".equals(authResponse.responseCode)) {
                                LogUtil.e(TAG, "⚠️ PIN verification failed - incorrect PIN");
                            }
                        }

                        callback.onAuthorizationComplete(authResponse);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing production API response: " + e.getMessage());
                        callback.onAuthorizationError(e);
                    } finally {
                        if (response.body() != null)
                            response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building production API request: " + e.getMessage());
            callback.onAuthorizationError(e);
        }
    }

    /**
     * Call production API using ISO 8583 socket communication (MsgSpec v341)
     */
    private void callProductionApiIsoSocket(AuthorizationRequest request, AuthorizationCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION API: ISO 8583 Socket Mode ===");
        LogUtil.e(TAG, "  Host: " + PaymentConfig.ISO_SOCKET_HOST);
        LogUtil.e(TAG, "  Port: " + PaymentConfig.ISO_SOCKET_PORT);

        new Thread(() -> {
            Iso8583SocketClient socketClient = null;
            try {
                // Create and connect socket client
                socketClient = new Iso8583SocketClient(
                        PaymentConfig.ISO_SOCKET_HOST,
                        PaymentConfig.ISO_SOCKET_PORT);
                socketClient.connect();

                // Build ISO 8583 application data (0100)
                // PAN must be sent in full (unmasked) exactly as extracted from EMV tag
                // No masking, no truncation, no modifications - send exactly as extracted
                String pan = request.pan != null ? request.pan : "";
                LogUtil.e(TAG, "✓ PAN for ISO 8583 (full, unmasked, as extracted from EMV): " + pan);
                LogUtil.e(TAG, "✓ PAN length: " + (pan != null ? pan.length() : 0) + " characters");
                String processingCode = "000000"; // Purchase
                String amount = request.amount;
                String stan = generateStan(request.date, request.time);
                String posEntryMode = determinePosEntryMode(request);
                String currencyCode = request.currencyCode != null ? request.currencyCode
                        : PaymentConfig.getCurrencyCode();
                String field55 = request.field55 != null ? request.field55 : "";
                String terminalId = PaymentConfig.getTerminalId();
                String merchantId = PaymentConfig.getMerchantId();
                String pinBlock = null;
                if (request.pinBlock != null && request.pinBlock.length > 0) {
                    pinBlock = bytesToHex(request.pinBlock);
                }

                byte[] applicationData = Iso8583Packer.pack0100(
                        pan, processingCode, amount, stan, posEntryMode,
                        currencyCode, field55, terminalId, merchantId, pinBlock);

                if (applicationData == null || applicationData.length == 0) {
                    throw new IOException("Failed to pack ISO 8583 message");
                }

                // Build complete message with header and CRC
                byte[] destinationAddress = PaymentConfig.ISO_DESTINATION_ADDRESS;
                byte[] originatorAddress = terminalIdToBytes(terminalId);
                byte[] completeMessage = Iso8583MessageBuilder.buildCompleteMessage(
                        applicationData, destinationAddress, originatorAddress);

                if (completeMessage == null || completeMessage.length == 0) {
                    throw new IOException("Failed to build complete ISO 8583 message");
                }

                // Log ISO message (for debugging)
                IsoLogger.save(completeMessage, "0100");

                // Send and receive
                byte[] responseMessage = socketClient.sendAndReceive(completeMessage, 30000);

                // Parse response
                byte[] responseApplicationData = Iso8583MessageBuilder.parseResponse(responseMessage);
                Iso8583ResponseParser.ParsedResponse parsedResponse = Iso8583ResponseParser
                        .parse0110(responseApplicationData);

                if (parsedResponse == null) {
                    throw new IOException("Failed to parse ISO 8583 response");
                }

                // Convert to AuthorizationResponse
                AuthorizationResponse authResponse = convertIsoResponseToAuthorizationResponse(parsedResponse, request);

                // Log PIN verification result
                if (request.pinBlock != null && request.pinBlock.length > 0) {
                    if (authResponse.approved) {
                        LogUtil.e(TAG, "✓ PIN verified successfully by backend");
                    } else if ("55".equals(authResponse.responseCode) || "63".equals(authResponse.responseCode)) {
                        LogUtil.e(TAG, "⚠️ PIN verification failed - incorrect PIN");
                    }
                }

                mainHandler.post(() -> callback.onAuthorizationComplete(authResponse));

            } catch (Exception e) {
                LogUtil.e(TAG, "❌ ISO 8583 Socket Error: " + e.getMessage());
                mainHandler.post(() -> callback.onAuthorizationError(e));
            } finally {
                if (socketClient != null) {
                    socketClient.close();
                }
            }
        }).start();
    }

    /**
     * Convert ISO 8583 parsed response to AuthorizationResponse
     */
    private AuthorizationResponse convertIsoResponseToAuthorizationResponse(
            Iso8583ResponseParser.ParsedResponse parsedResponse,
            AuthorizationRequest request) {

        boolean approved = parsedResponse.approved;
        String responseCode = parsedResponse.responseCode != null ? parsedResponse.responseCode : "XX";
        String authCode = parsedResponse.authCode != null ? parsedResponse.authCode : "";
        String rrn = parsedResponse.rrn != null ? parsedResponse.rrn : "";

        // Parse Field 55 (EMV response tags) if present
        List<String> responseTagList = new ArrayList<>();
        List<String> responseValueList = new ArrayList<>();

        if (approved) {
            // Tag 8A: Authorization Response Code
            responseTagList.add("8A");
            responseValueList.add("3000"); // Approved

            // Tag 91: Issuer Authentication Data (if present in Field 55)
            // Note: Field 55 may contain tag 91, we'd need to parse TLV here
            // For now, add empty tag 91
            responseTagList.add("91");
            responseValueList.add("");

            // Parse Field 55 for additional EMV tags if needed
            if (parsedResponse.field55 != null && !parsedResponse.field55.isEmpty()) {
                // Field 55 contains TLV data - parse it
                // This is a simplified version - full TLV parsing would be needed
                LogUtil.e(TAG, "Field 55 present in response: " + parsedResponse.field55.length() + " hex chars");
            }
        } else {
            // Tag 8A: Decline Code
            String emvResponseCode = convertIsoResponseToEmv8A(responseCode);
            responseTagList.add("8A");
            responseValueList.add(emvResponseCode);
        }

        String[] responseTags = responseTagList.toArray(new String[0]);
        String[] responseValues = responseValueList.toArray(new String[0]);

        if (approved) {
            return AuthorizationResponse.success(authCode, rrn, responseTags, responseValues);
        } else {
            String message = getDeclineMessage(responseCode);
            return AuthorizationResponse.declined(responseCode, message, true); // Bank decline
        }
    }

    /**
     * Convert terminal ID string to 2 bytes for TPDU originator address
     */
    private byte[] terminalIdToBytes(String terminalId) {
        if (terminalId == null || terminalId.isEmpty()) {
            return new byte[] { 0x00, 0x01 };
        }
        String digits = terminalId.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return new byte[] { 0x00, 0x01 };
        }
        if (digits.length() > 4) {
            digits = digits.substring(digits.length() - 4);
        } else {
            digits = String.format("%04d", Integer.parseInt(digits));
        }
        int value = Integer.parseInt(digits);
        return new byte[] {
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Call mock API (returns mock responses per specification)
     * Mock behavior rules:
     * - Approve amount ≤ 200 → response_code = 00
     * - Decline amount 201-500 → 05
     * - Timeout for amount > 500 → HTTP 504
     * - Send 97 once every 10th transaction to force key re-download
     */
    private void callMockApi(AuthorizationRequest request, AuthorizationCallback callback) {
        LogUtil.e(TAG, "=== MOCK: Authorization Request ===");
        LogUtil.e(TAG, request.toString());

        transactionCounter++;
        final int currentTxCount = transactionCounter;

        new Thread(() -> {
            if (simulateNetworkDelay) {
                // Random delay between 100-300ms per spec
                long delay = 100 + random.nextInt(200);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Parse amount in main currency unit
            double amount = parseAmount(request.amount);

            // Check for timeout condition (> 500)
            if (amount > 500.0) {
                LogUtil.e(TAG, "=== MOCK: TIMEOUT (amount > 500) ===");
                mainHandler.post(() -> {
                    AuthorizationResponse timeoutResponse = AuthorizationResponse.error(
                            new IOException("HOST_UNAVAILABLE"),
                            "Host timeout - amount exceeds threshold");
                    callback.onAuthorizationError(new IOException("HOST_UNAVAILABLE"));
                });
                return;
            }

            // Check for key sync requirement (every 10th transaction)
            if (currentTxCount % 10 == 0) {
                LogUtil.e(TAG, "=== MOCK: Key sync required (97) ===");
                AuthorizationResponse keySyncResponse = AuthorizationResponse.declined("97",
                        "Key sync required - trigger /terminal/config");
                mainHandler.post(() -> callback.onAuthorizationComplete(keySyncResponse));
                return;
            }

            // Determine approval based on amount
            boolean shouldApprove = amount <= 200.0;
            AuthorizationResponse response = shouldApprove ? createMockSuccessResponse(request)
                    : createMockDeclinedResponse(amount);

            LogUtil.e(TAG, "=== MOCK: Authorization " + (shouldApprove ? "APPROVED" : "DECLINED") + " ===");
            LogUtil.e(TAG, "  Amount: " + amount + " " + PaymentConfig.CURRENCY_NAME);
            LogUtil.e(TAG, response.toString());

            final AuthorizationResponse finalResponse = response;
            mainHandler.post(() -> {
                if (finalResponse.error != null) {
                    callback.onAuthorizationError(finalResponse.error);
                } else {
                    callback.onAuthorizationComplete(finalResponse);
                }
            });
        }).start();
    }

    private JsonObject buildRequestJson(AuthorizationRequest request) {
        JsonObject json = new JsonObject();

        // Backend API spec format: terminal_id, merchant_id, amount, currency, etc.
        // Send both camelCase and snake_case for compatibility
        String terminalId = PaymentConfig.getTerminalId();
        String merchantId = PaymentConfig.getMerchantId();
        json.addProperty("terminalId", terminalId);
        json.addProperty("terminal_id", terminalId);
        json.addProperty("merchantId", merchantId);
        json.addProperty("merchant_id", merchantId);

        // Convert amount from piasters (smallest unit) to main currency unit
        double amountInMainUnit = parseAmount(request.amount);
        json.addProperty("amount", amountInMainUnit);
        json.addProperty("currency", PaymentConfig.CURRENCY_NAME); // "EGP"
        json.addProperty("transaction_type", "SALE");

        // Mask PAN for security (real data from terminal)
        String panMasked = request.pan != null ? maskCardNumber(request.pan) : "";
        json.addProperty("pan_masked", panMasked);

        // PIN key ID (managed via KeyRegistry - server-issued key identifier)
        // Backend uses this to look up the TPK that was rewrapped under Bank TMK
        try {
            KeyRegistry.KeyState keyState = KeyRegistry.current();
            if (keyState != null && keyState.getPinKeyId() != null && !keyState.getPinKeyId().isEmpty()) {
                json.addProperty("pin_key_id", keyState.getPinKeyId());
                LogUtil.e(TAG, "✓ PIN key ID included: " + keyState.getPinKeyId());
            } else {
                LogUtil.e(TAG, "⚠️ No pin_key_id available - transaction may fail if backend requires it");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading PIN key ID: " + e.getMessage());
        }

        // EMV data structure (from field55 - real data from terminal)
        // Field 55 contains unmasked PAN in tags 5A, 57, or 9F6B (required for bank
        // communication)
        // Field 55 is built directly from EMV kernel and passed as-is without masking
        if (request.field55 != null && !request.field55.isEmpty()) {
            JsonObject emvData = new JsonObject();
            // Pass field55 as-is for backend to parse (PAN is unmasked in tags 5A, 57,
            // 9F6B)
            json.addProperty("track2_encrypted", ""); // Would be encrypted track2 if available
            json.addProperty("emv_data_raw", request.field55);
            json.addProperty("icc_data", request.field55);
            json.addProperty("field_55", request.field55);

            // Parse common EMV tags from field55 (for structured data)
            try {
                parseEmvTagsFromField55(request.field55, emvData);
                json.add("emv_data", emvData);
            } catch (Exception e) {
                LogUtil.e(TAG, "Could not parse EMV tags from field55: " + e.getMessage());
                json.add("emv_data", emvData);
            }
        }

        // PIN block encrypted with TPK (Terminal PIN Key) at slot 12
        // Backend will decrypt PIN block using TPK (identified by pin_key_id) and
        // verify PIN
        // Note: TPK uses MKSK (Master Key / Session Key) system, NOT DUKPT
        // KSN is NOT used with TPK - only pin_key_id is needed
        if (request.pinBlock != null && request.pinBlock.length > 0) {
            String pinBlockHex = bytesToHex(request.pinBlock);
            json.addProperty("pin_block", pinBlockHex);
            LogUtil.e(TAG, "✓ Sending PIN block encrypted with TPK (masked: " +
                    (pinBlockHex.length() > 8
                            ? pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4)
                            : "****")
                    + ")");
        } else {
            LogUtil.e(TAG, "⚠️ No PIN block in request - transaction may be offline PIN or no PIN required");
        }

        // KSN is only used for DUKPT - not needed for TPK (MKSK system)
        // If KSN is present, it means the system might be using DUKPT (should not
        // happen with current config)
        if (request.ksn != null && !request.ksn.isEmpty()) {
            json.addProperty("ksn", request.ksn);
            LogUtil.e(TAG, "⚠️ KSN provided but system uses TPK (MKSK) - KSN should be null for TPK");
        } else {
            LogUtil.e(TAG, "✓ No KSN (expected for TPK/MKSK system - backend uses pin_key_id to identify TPK)");
        }

        // Datetime in ISO8601 format (real transaction date/time)
        String datetime = formatDateTime(request.date, request.time);
        json.addProperty("datetime", datetime);
        json.addProperty("pos_entry_mode", determinePosEntryMode(request));

        // ISO8583 Fields (simulated for backend integration)
        JsonObject isoFields = buildIsoFields(request);
        json.add("iso_fields", isoFields);

        // Pack raw ISO8583 binary frame (0100 = Authorization Request)
        // Extract PIN block if present (for DE52)
        String pinBlockHex = null;
        if (request.pinBlock != null && request.pinBlock.length > 0) {
            pinBlockHex = bytesToHex(request.pinBlock);
            // PIN block should be 8 bytes (16 hex chars)
            if (pinBlockHex.length() == 16) {
                // Verify ISO 9564 format (Format 0 or Format 1)
                String formatNibble = pinBlockHex.substring(0, 1);
                String pinBlockFormat = "Unknown";
                boolean isValidFormat = false;

                if ("0".equals(formatNibble)) {
                    pinBlockFormat = "ISO 9564 Format 0";
                    isValidFormat = true;
                } else if ("1".equals(formatNibble)) {
                    pinBlockFormat = "ISO 9564 Format 1";
                    isValidFormat = true;
                } else {
                    pinBlockFormat = "Format " + formatNibble + " (not ISO 9564 Format 0 or 1)";
                }

                // Extract PIN length (second nibble)
                int pinLength = -1;
                try {
                    pinLength = Integer.parseInt(pinBlockHex.substring(1, 2), 16);
                } catch (Exception e) {
                    // Ignore
                }

                LogUtil.e(TAG, "=== Field 52 (PIN Block) Details ===");
                LogUtil.e(TAG, "✓ PIN Block (DE52): " + pinBlockHex.substring(0, 4) + "****"
                        + pinBlockHex.substring(pinBlockHex.length() - 4));
                LogUtil.e(TAG, "✓ PIN Block Format: " + pinBlockFormat + (isValidFormat ? " ✓" : " ⚠️"));
                LogUtil.e(TAG, "✓ PIN Block Length: 8 bytes (16 hex chars)");
                if (pinLength >= 0 && pinLength <= 12) {
                    LogUtil.e(TAG, "✓ PIN Length (from block): " + pinLength + " digits");
                }

                // Log TPK information (for testing/decryption)
                try {
                    KeyRegistry.KeyState keyState = KeyRegistry.current();
                    if (keyState != null) {
                        String pinKeyId = keyState.getPinKeyId();
                        String tpkKcv = keyState.getTpkKcv();
                        String wrappedTpk = keyState.getWrappedTpk();
                        LogUtil.e(TAG, "=== TPK (Terminal PIN Key) Details ===");
                        LogUtil.e(TAG, "✓ TPK Slot: 12 (TPK_INDEX)");
                        LogUtil.e(TAG, "✓ TPK KCV: " + (tpkKcv != null ? tpkKcv : "N/A"));
                        LogUtil.e(TAG, "✓ PIN Key ID: " + (pinKeyId != null ? pinKeyId : "N/A"));
                        LogUtil.e(TAG, "✓ Key System: MKSK (Master Key / Session Key)");
                        if (wrappedTpk != null && !wrappedTpk.isEmpty()) {
                            LogUtil.e(TAG, "✓ Wrapped TPK (encrypted under TMK): " + wrappedTpk);
                            LogUtil.e(TAG, "  Note: This is the TPK encrypted under TMK.");
                            LogUtil.e(TAG,
                                    "  To decrypt PIN blocks: Unwrap TPK using TMK, then decrypt PIN block using TPK.");
                        } else {
                            LogUtil.e(TAG, "⚠️ Wrapped TPK: Not stored (may need to re-provision TPK)");
                        }
                    } else {
                        LogUtil.e(TAG, "⚠️ TPK Key State: Not available");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "⚠️ Failed to retrieve TPK information: " + e.getMessage());
                }
            } else {
                LogUtil.e(TAG, "⚠️ PIN Block length invalid: " + pinBlockHex.length() + " hex chars (expected 16)");
            }
        } else {
            LogUtil.e(TAG, "⚠️ No PIN block in request - Field 52 (DE52) will not be included");
        }

        // Send actual PAN (unmasked) to ISO message - masking is only for
        // logging/display
        byte[] isoFrame = Iso8583Packer.pack0100(
                request.pan != null ? request.pan : "",
                "000000", // Processing Code
                request.amount,
                generateStan(request.date, request.time),
                determinePosEntryMode(request),
                request.currencyCode != null ? request.currencyCode : "818",
                request.field55 != null ? request.field55 : "",
                PaymentConfig.getTerminalId(),
                PaymentConfig.getMerchantId(),
                pinBlockHex // DE52: PIN Block (optional, only for online PIN)
        );

        // Base64 encode raw ISO frame
        if (isoFrame != null && isoFrame.length > 0) {
            String isoRawB64 = Base64.encodeToString(isoFrame, Base64.NO_WRAP);
            json.addProperty("iso_raw_b64", isoRawB64);
            LogUtil.e(TAG, "✓ Raw ISO8583 frame (0100) attached - length: " + isoFrame.length + " bytes");

            // Save ISO frame to disk for debugging (only in DEBUG builds)
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                IsoLogger.save(isoFrame, "0100");
            }
        } else {
            LogUtil.e(TAG, "⚠️ Failed to pack ISO8583 frame");
        }

        return json;
    }

    /**
     * Build ISO8583 iso_fields object
     * Includes standard ISO8583 data elements plus DE55 (ICC Data) from EMV Field
     * 55
     * 
     * @param request Authorization request with transaction data
     * @return JsonObject containing ISO8583 fields
     */
    private JsonObject buildIsoFields(AuthorizationRequest request) {
        JsonObject isoFields = new JsonObject();

        // DE2: PAN (Primary Account Number) - masked
        if (request.pan != null && !request.pan.isEmpty()) {
            String panMasked = maskCardNumber(request.pan);
            isoFields.addProperty("2", panMasked);
        }

        // DE3: Processing Code (000000 = Purchase)
        isoFields.addProperty("3", "000000");

        // DE4: Amount, Authorized (in minor currency units)
        if (request.amount != null && !request.amount.isEmpty()) {
            isoFields.addProperty("4", request.amount);
        }

        // DE11: STAN (Systems Trace Audit Number) - generated from timestamp
        String stan = generateStan(request.date, request.time);
        isoFields.addProperty("11", stan);

        // DE22: POS Entry Mode
        // 051 = Chip + PIN
        // 021 = Chip (no PIN)
        // 071 = Contactless (NFC) + PIN
        // 072 = Contactless (NFC) - no PIN
        String posEntryMode = determinePosEntryMode(request);
        isoFields.addProperty("22", posEntryMode);

        // DE35: Track 2 Data (if available from mag stripe)
        // Note: For EMV chip cards, Track 2 equivalent comes from EMV data

        // DE49: Currency Code, Transaction (e.g., "818" for EGP)
        if (request.currencyCode != null && !request.currencyCode.isEmpty()) {
            isoFields.addProperty("49", request.currencyCode);
        } else {
            isoFields.addProperty("49", "818"); // Default EGP
        }

        // DE55: ICC Data (EMV Field 55 - Integrated Circuit Card Data)
        // This is the most critical field for EMV transactions
        // Contains all EMV tags (ARQC, CVM, AIP, etc.)
        if (request.field55 != null && !request.field55.isEmpty()) {
            isoFields.addProperty("55", request.field55);
            LogUtil.e(TAG,
                    "✓ DE55 (ICC Data) included in ISO fields - length: " + request.field55.length() + " hex chars");
        } else {
            LogUtil.e(TAG, "⚠️ No Field 55 available - DE55 will be empty");
        }

        // DE60: Additional Data (optional - can include terminal ID, merchant ID, etc.)
        // Format varies by acquirer

        // DE61: POS Data (optional - terminal capabilities)

        // DE63: Network Management Information Code (optional)

        // Transaction Type Code (derived from MTI in full ISO8583)
        // 0100 = Authorization Request
        // 0200 = Financial Transaction Request
        isoFields.addProperty("mti", "0200"); // Financial Transaction

        return isoFields;
    }

    /**
     * Generate STAN (Systems Trace Audit Number) from date/time
     * Format: 6 digits (000000-999999), typically incremented per transaction
     * 
     * @param date Transaction date (YYMMDD)
     * @param time Transaction time (HHMMSS)
     * @return STAN as 6-digit string
     */
    private String generateStan(String date, String time) {
        try {
            // Use seconds from time + milliseconds to create unique STAN
            if (time != null && time.length() >= 6) {
                String seconds = time.substring(4, 6); // Last 2 digits (SS)
                // Add milliseconds (0-9) to seconds for uniqueness
                int millis = (int) (System.currentTimeMillis() % 1000) / 100; // 0-9
                int stanValue = Integer.parseInt(seconds) * 10 + millis;
                // Ensure 6 digits by padding
                return String.format(Locale.US, "%06d", stanValue % 1000000);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error generating STAN: " + e.getMessage());
        }
        // Fallback: use last 6 digits of timestamp
        long timestamp = System.currentTimeMillis();
        return String.format(Locale.US, "%06d", timestamp % 1000000);
    }

    /**
     * Determine POS Entry Mode (DE22) based on transaction context
     * 
     * @param request Authorization request
     * @return POS Entry Mode code (3 digits)
     */
    private String determinePosEntryMode(AuthorizationRequest request) {
        // Default: Chip + PIN (051)
        // This should be determined from actual transaction flow
        // - Chip + PIN = 051
        // - Chip only (no PIN) = 021
        // - Contactless + PIN = 071
        // - Contactless (no PIN) = 072
        // - Signature = 050

        // Detect contactless from card type or EMV field55
        boolean hasPin = (request.pinBlock != null && request.pinBlock.length > 0);
        boolean isContactless = false;

        // First try: detect from cardType field (if provided)
        if (request.cardType != null) {
            int nfcValue = com.sunmi.pay.hardware.aidl.AidlConstants.CardType.NFC.getValue();
            isContactless = (request.cardType == nfcValue);
        }

        // Fallback: detect from EMV field55 (tag 9F6E contains POS entry mode)
        if (request.field55 != null && !request.field55.isEmpty()) {
            // Check if field55 contains indicators of contactless transaction
            // Tag 9F6E (POS Entry Mode) contains entry mode information
            // Contactless transactions typically have 9F6E values starting with 07
            String field55Upper = request.field55.toUpperCase();
            if (field55Upper.contains("9F6E") || field55Upper.contains("071") || field55Upper.contains("072")) {
                isContactless = true;
            }
        }

        if (isContactless) {
            return hasPin ? "071" : "072"; // Contactless + PIN : Contactless only
        } else {
            return hasPin ? "051" : "021"; // Chip + PIN : Chip only
        }
    }

    private void parseEmvTagsFromField55(String field55, JsonObject emvData) {
        // Simplified parsing - extract common tags like 9F26, 9F27, 9F10, 5A
        // This is a simplified version - actual TLV parsing would be more robust
        if (field55.contains("9F26")) {
            // Extract 9F26 value (ARQC)
            emvData.addProperty("9F26", extractTagValue(field55, "9F26"));
        }
        if (field55.contains("9F27")) {
            emvData.addProperty("9F27", extractTagValue(field55, "9F27"));
        }
        if (field55.contains("9F10")) {
            emvData.addProperty("9F10", extractTagValue(field55, "9F10"));
        }
        if (field55.contains("5A")) {
            emvData.addProperty("5A", extractTagValue(field55, "5A"));
        }
    }

    private String extractTagValue(String tlv, String tag) {
        // Simplified - find tag and extract value
        // In production, use proper TLV parser
        int tagIndex = tlv.indexOf(tag);
        if (tagIndex >= 0) {
            // Skip tag (4 hex chars) and length (2 hex chars)
            int valueStart = tagIndex + 6;
            if (valueStart < tlv.length()) {
                // Assume 16 hex chars for value (simplified)
                int valueEnd = Math.min(valueStart + 16, tlv.length());
                return tlv.substring(valueStart, valueEnd);
            }
        }
        return "";
    }

    private double parseAmount(String amountStr) {
        try {
            // Amount is in smallest currency unit (piasters for EGP)
            long amount = Long.parseLong(amountStr);
            return amount / 100.0; // Convert to main unit
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String formatDateTime(String date, String time) {
        // Convert YYMMDD and HHMMSS to ISO8601 format
        // Example: "251101" + "143221" -> "2025-11-01T14:32:21Z"
        try {
            if (date != null && date.length() == 6 && time != null && time.length() == 6) {
                String year = "20" + date.substring(0, 2);
                String month = date.substring(2, 4);
                String day = date.substring(4, 6);
                String hour = time.substring(0, 2);
                String minute = time.substring(2, 4);
                String second = time.substring(4, 6);
                return String.format("%s-%s-%sT%s:%s:%sZ", year, month, day, hour, minute, second);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error formatting datetime: " + e.getMessage());
        }
        // Fallback to current time (ISO 8601 format, compatible with API 24+)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * Get current time as ISO 8601 UTC string (compatible with API 24+)
     * 
     * @return ISO 8601 formatted time string (e.g., "2025-01-15T10:30:45Z")
     */
    private String getCurrentTimeIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 10) {
            return "****";
        }
        // Always show first 6 digits and last 4 digits
        // Format: "400000****7899"
        return cardNumber.substring(0, 6) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * Parse production API response
     * Backend returns PIN verification result and transaction authorization result
     * Response codes:
     * - 00 = APPROVED (PIN correct, transaction approved)
     * - 55 = INCORRECT PIN (terminal will retry)
     * - 63 = SECURITY VIOLATION (PIN attempts exceeded)
     * - Other = Transaction declined for other reasons
     */
    private AuthorizationResponse parseResponse(String responseBody) throws Exception {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);

        // Backend API spec format: response_code, response_message, auth_code,
        // issuer_auth_data, field_55, rrn
        String responseCode = json.has("response_code") ? json.get("response_code").getAsString() : "XX";
        String responseMessage = json.has("response_message") ? json.get("response_message").getAsString() : "";
        String authCode = json.has("auth_code") ? json.get("auth_code").getAsString() : "";
        String rrn = json.has("rrn") ? json.get("rrn").getAsString() : "";
        // Handle issuer_auth_data - may be undefined, null, or empty in JSON
        String issuerAuthData = "";
        if (json.has("issuer_auth_data") && !json.get("issuer_auth_data").isJsonNull()) {
            try {
                issuerAuthData = json.get("issuer_auth_data").getAsString();
            } catch (Exception e) {
                LogUtil.e(TAG, "⚠️ Error reading issuer_auth_data: " + e.getMessage());
                issuerAuthData = "";
            }
        }
        String field55 = json.has("field_55") ? json.get("field_55").getAsString() : "";

        boolean approved = "00".equals(responseCode);

        // Always generate EMV response tags locally based on backend response
        // Backend only provides: response_code, auth_code, rrn
        // Terminal generates: 8A, 91, issuer scripts
        List<String> responseTagList = new ArrayList<>();
        List<String> responseValueList = new ArrayList<>();

        if (approved) {
            // Tag 8A: Authorization Response Code (required for second GENERATE AC)
            // Convert ISO8583 response_code "00" to EMV format "3000" (approved)
            String emvResponseCode = convertIsoResponseToEmv8A(responseCode);
            responseTagList.add("8A");
            responseValueList.add(emvResponseCode);
            LogUtil.e(TAG, "✓ Generated tag 8A locally (Authorization Response Code): " + emvResponseCode);

            // Check if issuer scripts are present first (affects tag 91 requirement)
            boolean hasIssuerScripts = false;
            if (json.has("issuer_scripts") && json.get("issuer_scripts").isJsonArray()) {
                JsonArray scripts = json.getAsJsonArray("issuer_scripts");
                if (scripts != null && scripts.size() > 0) {
                    hasIssuerScripts = true;
                }
            }

            // Tag 91: Issuer Authentication Data (REQUIRED for second GENERATE AC)
            // If backend provides issuer_auth_data, validate and use it
            // If backend does not provide it, add tag 91 as empty string
            if (issuerAuthData != null && !issuerAuthData.isEmpty()) {
                // Validate tag 91 format (should be 20 hex chars = 10 bytes)
                String iad = issuerAuthData.trim();
                if (iad.length() < 16 || iad.length() > 40) {
                    LogUtil.e(TAG, "⚠️ WARNING: Tag 91 length may be incorrect: " + iad.length()
                            + " chars (expected 20 hex chars = 10 bytes)");
                }

                // Validate hex format
                if (!iad.matches("[0-9A-Fa-f]+")) {
                    LogUtil.e(TAG, "❌ ERROR: Tag 91 contains invalid hex characters: " + iad);
                    return AuthorizationResponse.declined("96",
                            "System error: Invalid issuer authentication data format (tag 91)");
                }

                responseTagList.add("91");
                responseValueList.add(iad.toUpperCase()); // Normalize to uppercase
                LogUtil.e(TAG, "✓ Using tag 91 from backend (Issuer Authentication Data from bank): "
                        + iad.substring(0, Math.min(8, iad.length())) + "... (length: " + iad.length() + " hex chars)");
            } else {
                // Backend did not provide tag 91 - add it as empty string
                LogUtil.e(TAG, "⚠️ Backend did not provide issuer_auth_data (tag 91) - adding as empty string");
                responseTagList.add("91");
                responseValueList.add(""); // Empty string for tag 91
            }

            // Optional: Parse issuer scripts from backend if provided (but not required)
            if (hasIssuerScripts) {
                JsonArray scripts = json.getAsJsonArray("issuer_scripts");
                appendScriptArray(scripts, responseTagList, responseValueList);
                LogUtil.e(TAG, "✓ Parsed issuer_scripts from backend");
            }
        } else {
            // For declined transactions, set 8A to decline code
            String emvResponseCode = convertIsoResponseToEmv8A(responseCode);
            responseTagList.add("8A");
            responseValueList.add(emvResponseCode);
            LogUtil.e(TAG, "✓ Generated tag 8A (Decline Code): " + emvResponseCode);
        }

        String[] responseTags = responseTagList.toArray(new String[0]);
        String[] responseValues = responseValueList.toArray(new String[0]);

        // Log PIN verification result
        if ("55".equals(responseCode)) {
            LogUtil.e(TAG, "⚠️ Backend PIN verification: INCORRECT PIN (response code 55)");
        } else if ("63".equals(responseCode)) {
            LogUtil.e(TAG, "⚠️ Backend PIN verification: SECURITY VIOLATION (response code 63)");
        }

        if (approved) {
            LogUtil.e(TAG, "✓ Backend authorization: APPROVED");
            LogUtil.e(TAG, "  Auth Code: " + authCode);
            LogUtil.e(TAG, "  RRN: " + rrn);
            return AuthorizationResponse.success(authCode, rrn, responseTags, responseValues);
        } else {
            LogUtil.e(TAG, "⚠️ Backend authorization: DECLINED (from bank)");
            LogUtil.e(TAG, "  Response Code: " + responseCode);
            LogUtil.e(TAG, "  Message: " + responseMessage);
            // Production API decline = bank decline
            return AuthorizationResponse.declined(responseCode, responseMessage, true);
        }
    }

    private void appendScriptArray(JsonArray scripts, List<String> tags, List<String> values) {
        if (scripts == null) {
            return;
        }
        for (int i = 0; i < scripts.size(); i++) {
            try {
                JsonObject script = scripts.get(i).getAsJsonObject();
                if (script == null)
                    continue;
                String tag = script.has("tag") ? script.get("tag").getAsString() : null;
                String value = script.has("value") ? script.get("value").getAsString() : null;
                if (tag != null && !tag.isEmpty() && value != null && !value.isEmpty()) {
                    tags.add(tag);
                    values.add(value);
                }
            } catch (Exception ignored) {
            }
        }
    }

    private AuthorizationResponse createMockSuccessResponse(AuthorizationRequest request) {
        // Per spec format: response_code="00", response_message="APPROVED", auth_code,
        // issuer_auth_data, rrn
        String authCode = String.format(Locale.US, "%06d", random.nextInt(1000000));
        String rrn = generateRRN(request.date, request.time);
        String issuerAuthData = generateMockIAD(); // Tag 91 value

        // EMV response tags for importOnlineProcStatus
        String[] responseTags = { "91" }; // Issuer Authentication Data
        String[] responseValues = { issuerAuthData };

        return AuthorizationResponse.success(authCode, rrn, responseTags, responseValues);
    }

    private String generateRRN(String date, String time) {
        // Generate RRN: format "YYMMDDHHMMSS" or similar
        if (date != null && time != null && date.length() == 6 && time.length() == 6) {
            return date + time;
        }
        // Fallback to random 12-digit number
        return String.format(Locale.US, "%012d", Math.abs(random.nextLong() % 1000000000000L));
    }

    private AuthorizationResponse createMockDeclinedResponse(double amount) {
        // Per spec: decline amount 201-500 → 05
        String responseCode = "05"; // "Do not honor"
        String message = "DECLINED - Do not honor";
        return AuthorizationResponse.declined(responseCode, message);
    }

    private AuthorizationResponse createMockDeclinedResponse() {
        return createMockDeclinedResponse(0.0);
    }

    private String getDeclineMessage(String responseCode) {
        switch (responseCode) {
            case "05":
                return "Transaction declined - Do not honor";
            case "14":
                return "Transaction declined - Invalid card number";
            case "51":
                return "Transaction declined - Insufficient funds";
            case "54":
                return "Transaction declined - Expired card";
            case "61":
                return "Transaction declined - Exceeds withdrawal limit";
            case "91":
                return "Transaction declined - Issuer unavailable";
            default:
                return "Transaction declined - Code: " + responseCode;
        }
    }

    /**
     * Convert ISO8583 response code to EMV tag 8A format.
     * ISO8583 "00" = Approved → EMV "3000"
     * Other codes → EMV decline codes (e.g., "5A00" for "05")
     */
    private String convertIsoResponseToEmv8A(String isoResponseCode) {
        if (isoResponseCode == null || isoResponseCode.isEmpty()) {
            return "5A00"; // Default decline
        }

        switch (isoResponseCode) {
            case "00":
                return "3000"; // Approved
            case "05":
                return "5A00"; // Do not honor
            case "14":
                return "5A00"; // Invalid card number
            case "51":
                return "5100"; // Insufficient funds
            case "54":
                return "5400"; // Expired card
            case "55":
                return "5500"; // Incorrect PIN
            case "61":
                return "6100"; // Exceeds withdrawal limit
            case "63":
                return "6300"; // Security violation
            case "91":
                return "9100"; // Issuer unavailable
            default:
                // Convert 2-digit ISO code to 4-digit EMV format
                if (isoResponseCode.length() == 2) {
                    return isoResponseCode + "00";
                }
                return "5A00"; // Default decline
        }
    }

    private String generateMockIAD() {
        // Generate Issuer Authentication Data (tag 91) - typically 10 bytes (20 hex
        // chars)
        // Per spec: "910A112233445566" format (91 = tag, 0A = length, then data)
        StringBuilder iad = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            iad.append(String.format("%x", random.nextInt(16)));
        }
        return iad.toString().toUpperCase();
    }

    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return "";
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }

    /**
     * Mask sensitive data in request body for logging (PIN blocks, KSN, etc.)
     */
    private String maskSensitiveData(String jsonString) {
        if (jsonString == null || jsonString.isEmpty())
            return "";

        try {
            // Replace PIN block values with masked version
            jsonString = jsonString.replaceAll("\"pin_block\"\\s*:\\s*\"[^\"]+\"", "\"pin_block\":\"****\"");

            // Replace KSN values with masked version
            java.util.regex.Pattern ksnPattern = java.util.regex.Pattern.compile("\"ksn\"\\s*:\\s*\"([^\"]+)\"");
            java.util.regex.Matcher ksnMatcher = ksnPattern.matcher(jsonString);
            StringBuffer sb = new StringBuffer();
            while (ksnMatcher.find()) {
                String ksn = ksnMatcher.group(1);
                String maskedKsn = ksn.length() > 8
                        ? ksn.substring(0, 4) + "****" + ksn.substring(ksn.length() - 4)
                        : "****";
                ksnMatcher.appendReplacement(sb, "\"ksn\":\"" + maskedKsn + "\"");
            }
            ksnMatcher.appendTail(sb);
            jsonString = sb.toString();

            return jsonString;
        } catch (Exception e) {
            return jsonString; // Return original on error
        }
    }

    @Override
    public void rotateKeys(KeyRotationRequest request, KeyRotationCallback callback) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            // Mock mode: Return mock key rotation response
            callMockKeyRotation(request, callback);
        } else {
            // Production mode: Call actual API
            callProductionKeyRotation(request, callback);
        }
    }

    @Override
    public TerminalRegisterResponse registerTerminalKeySync(TerminalRegisterRequest request) throws IOException {
        TerminalRegisterResponse response = new TerminalRegisterResponse();
        if (request == null || request.terminalId == null || request.terminalId.isEmpty()) {
            response.success = false;
            response.message = "terminalId required";
            return response;
        }

        if (!isProductionMode()) {
            response.success = true;
            response.terminalId = request.terminalId;
            response.message = "Mock register";
            return response;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("terminalId", request.terminalId);
        payload.addProperty("terminal_id", request.terminalId);
        if (request.publicKeyPem != null) {
            payload.addProperty("publicKeyPem", request.publicKeyPem);
            payload.addProperty("public_key_pem", request.publicKeyPem);
        }

        String resp = executePost(buildUrl(ROUTE_REGISTER_TERMINAL), payload);
        return parseTerminalRegisterResponse(resp);
    }

    @Override
    public TmkProvisionResponse provisionTmkSync(TmkProvisionRequest request) throws IOException {
        TmkProvisionResponse response = new TmkProvisionResponse();
        if (request == null || request.terminalId == null || request.terminalId.isEmpty()) {
            response.success = false;
            response.message = "terminalId required";
            return response;
        }

        if (!isProductionMode()) {
            response.success = true;
            response.terminalId = request.terminalId;
            response.wrappedTmk = generateMockWrappedKey();
            response.kcv = generateMockHexKey(6);
            response.message = "Mock TMK provisioning";
            return response;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("terminalId", request.terminalId);
        payload.addProperty("terminal_id", request.terminalId);

        String resp = executePost(buildUrl(ROUTE_PROVISION_TMK), payload);
        return parseTmkProvisionResponse(resp);
    }

    @Override
    public TpkProvisionResponse provisionTpkSync(TpkProvisionRequest request) throws IOException {
        TpkProvisionResponse response = new TpkProvisionResponse();
        if (request == null || request.terminalId == null || request.terminalId.isEmpty()) {
            response.success = false;
            response.message = "terminalId required";
            return response;
        }

        if (!isProductionMode()) {
            response.success = true;
            response.terminalId = request.terminalId;
            response.wrappedTpk = generateMockWrappedKey();
            response.wrappedTak = generateMockWrappedKey();
            response.pinKeyId = "MOCK_PIN_KEY";
            response.message = "Mock TPK provisioning";
            return response;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("terminalId", request.terminalId);
        payload.addProperty("terminal_id", request.terminalId);

        String resp = executePost(buildUrl(ROUTE_PROVISION_TPK), payload);
        return parseTpkProvisionResponse(resp);
    }

    /**
     * Mock key rotation (returns mock keys for testing)
     */
    private void callMockKeyRotation(KeyRotationRequest request, KeyRotationCallback callback) {
        LogUtil.e(TAG, "=== MOCK: Key Rotation Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  Key Type: " + request.keyType);

        new Thread(() -> {
            if (simulateNetworkDelay) {
                try {
                    Thread.sleep(100 + random.nextInt(200)); // 100-300ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Generate mock key rotation response
            // Note: These are MOCK keys - never use in production!
            int mockKeyIndex = 1101 + random.nextInt(10); // Random index 1101-1110
            String mockIpek = generateMockHexKey(32); // 32 hex chars = 16 bytes
            String mockKsn = generateMockHexKey(20); // 20 hex chars = 10 bytes
            String mockEffectiveDate = getCurrentTimeIso();
            String mockCiphertext = generateMockCiphertext(mockIpek, mockKsn);

            KeyRotationResponse response = KeyRotationResponse.success(
                    request.terminalId,
                    request.keyType,
                    mockKeyIndex,
                    mockIpek,
                    mockKsn,
                    mockEffectiveDate,
                    mockCiphertext);

            LogUtil.e(TAG, "=== MOCK: Key Rotation Success ===");
            LogUtil.e(TAG, "  Key Index: " + mockKeyIndex);
            LogUtil.e(TAG, "  IPEK: " + mockIpek.substring(0, 8) + "****");
            LogUtil.e(TAG, "  KSN: " + mockKsn);
            LogUtil.e(TAG, "  ⚠️ WARNING: These are MOCK keys - never use in production!");

            final KeyRotationResponse finalResponse = response;
            mainHandler.post(() -> callback.onKeyRotationComplete(finalResponse));
        }).start();
    }

    /**
     * Production key rotation (calls actual backend API)
     */
    private void callProductionKeyRotation(KeyRotationRequest request, KeyRotationCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION: Key Rotation Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  Key Type: " + request.keyType);
        LogUtil.e(TAG, "  Endpoint: " + buildUrl(ROUTE_KEY_ROTATION));

        try {
            // Build JSON request
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("terminal_id", request.terminalId);
            requestJson.addProperty("key_type", request.keyType);

            String jsonBody = new Gson().toJson(requestJson);
            LogUtil.e(TAG, "  Request body: " + jsonBody);

            // Build HTTP request
            Request httpRequest = new Request.Builder()
                    .url(buildUrl(ROUTE_KEY_ROTATION))
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .build();

            // Execute request asynchronously
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ Key rotation request failed: " + e.getMessage());
                    mainHandler.post(() -> callback.onKeyRotationError(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        LogUtil.e(TAG, "❌ Key rotation failed: HTTP " + response.code() + " - " + errorBody);
                        mainHandler.post(() -> callback.onKeyRotationError(
                                new IOException("Key rotation failed: HTTP " + response.code())));
                        return;
                    }

                    String responseBody = response.body().string();
                    LogUtil.e(TAG, "✓ Key rotation response received");

                    try {
                        KeyRotationResponse rotationResponse = parseKeyRotationResponse(responseBody);
                        mainHandler.post(() -> callback.onKeyRotationComplete(rotationResponse));
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing key rotation response: " + e.getMessage());
                        mainHandler.post(() -> callback.onKeyRotationError(e));
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building key rotation request: " + e.getMessage());
            mainHandler.post(() -> callback.onKeyRotationError(e));
        }
    }

    /**
     * Parse key rotation response from JSON
     */
    private KeyRotationResponse parseKeyRotationResponse(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        if (!"success".equals(jsonObject.get("status").getAsString())) {
            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Unknown error";
            return KeyRotationResponse.error(new Exception(message), message);
        }

        JsonObject rotation = jsonObject.getAsJsonObject("rotation");
        String terminalId = rotation.get("terminal_id").getAsString();
        String keyType = rotation.get("key_type").getAsString();
        int keyIndex = rotation.get("key_index").getAsInt();
        String ipek = rotation.get("ipek").getAsString();
        String ksn = rotation.get("ksn").getAsString();
        String effectiveDate = rotation.get("effective_date").getAsString();
        String ciphertext = jsonObject.get("ciphertext").getAsString();

        return KeyRotationResponse.success(terminalId, keyType, keyIndex, ipek, ksn, effectiveDate, ciphertext);
    }

    private String executePost(String url, JsonObject payload) throws IOException {
        if (httpClient == null) {
            throw new IOException("HTTP client not initialized");
        }
        String bodyString = new Gson().toJson(payload);
        LogUtil.e(TAG, "POST " + url + " body=" + maskSensitiveData(bodyString));
        RequestBody requestBody = RequestBody.create(JSON, bodyString);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json");
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + apiKey);
        }
        try (Response response = httpClient.newCall(builder.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code() + ": " + responseBody);
            }
            return responseBody;
        }
    }

    private TerminalRegisterResponse parseTerminalRegisterResponse(String body) {
        TerminalRegisterResponse response = new TerminalRegisterResponse();
        try {
            JsonObject obj = new Gson().fromJson(body, JsonObject.class);
            response.success = isSuccessStatus(obj);
            response.terminalId = firstString(obj, "terminalId", "terminal_id");
            response.message = firstString(obj, "message", "detail", "status");
            if (response.message == null) {
                response.message = response.success ? "Terminal registered" : "Registration failed";
            }
        } catch (Exception e) {
            response.success = false;
            response.message = "Invalid response: " + e.getMessage();
        }
        return response;
    }

    private TmkProvisionResponse parseTmkProvisionResponse(String body) {
        TmkProvisionResponse response = new TmkProvisionResponse();
        try {
            JsonObject obj = new Gson().fromJson(body, JsonObject.class);
            response.success = isSuccessStatus(obj);
            response.terminalId = firstString(obj, "terminalId", "terminal_id");
            response.wrappedTmk = firstString(obj, "tmkCipher", "wrapped_tmk", "wrappedTmk", "tmk", "tmk_b64",
                    "wrapped_tmk_b64");
            // Parse KCV - backend returns as tmkKcv
            String kcvValue = firstString(obj, "tmkKcv", "kcv", "tmk_kcv");
            response.kcv = kcvValue;
            response.tmkKcv = kcvValue;
            response.message = firstString(obj, "message", "detail", "status");
            if (response.message == null) {
                response.message = response.success ? "TMK provisioned" : "TMK provisioning failed";
            }
        } catch (Exception e) {
            response.success = false;
            response.message = "Invalid response: " + e.getMessage();
        }
        return response;
    }

    private TpkProvisionResponse parseTpkProvisionResponse(String body) {
        TpkProvisionResponse response = new TpkProvisionResponse();
        try {
            JsonObject obj = new Gson().fromJson(body, JsonObject.class);
            response.success = isSuccessStatus(obj);
            response.terminalId = firstString(obj, "terminalId", "terminal_id");
            response.wrappedTpk = firstString(obj, "wrapped_tpk", "tpk", "tpk_b64", "wrappedTpk", "tpkCipher");
            response.wrappedTak = firstString(obj, "wrapped_tak", "tak", "tak_b64", "wrappedTak", "takCipher");
            response.tpkKcv = firstString(obj, "tpkKcv", "tpk_kcv");
            response.takKcv = firstString(obj, "takKcv", "tak_kcv");
            response.pinKeyId = firstString(obj, "pin_key_id", "pinKeyId", "key_set_id", "keySetId");
            response.message = firstString(obj, "message", "detail", "status");
            if (response.message == null) {
                response.message = response.success ? "TPK provisioned" : "TPK provisioning failed";
            }
        } catch (Exception e) {
            response.success = false;
            response.message = "Invalid response: " + e.getMessage();
        }
        return response;
    }

    private boolean isSuccessStatus(JsonObject obj) {
        if (obj == null)
            return false;
        if (obj.has("status")) {
            String status = obj.get("status").getAsString();
            if ("success".equalsIgnoreCase(status) || "ok".equalsIgnoreCase(status)) {
                return true;
            }
        }
        if (obj.has("ok") && obj.get("ok").getAsBoolean()) {
            return true;
        }
        if (obj.has("ok") && obj.get("ok").getAsBoolean()) {
            return true;
        }
        return obj.has("tmkCipher") || obj.has("wrapped_tmk") || obj.has("wrappedTmk") || obj.has("wrapped_tpk")
                || obj.has("wrappedTpk");
    }

    private String firstString(JsonObject obj, String... keys) {
        if (obj == null)
            return null;
        for (String key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                return obj.get(key).getAsString();
            }
        }
        return null;
    }

    /**
     * Generate mock hexadecimal key (for testing only)
     */
    private String generateMockHexKey(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%X", random.nextInt(16)));
        }
        return sb.toString();
    }

    private String generateMockWrappedKey() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    /**
     * Generate mock ciphertext (TR-31 stub format for testing)
     */
    private String generateMockCiphertext(String ipek, String ksn) {
        // Mock TR-31 stub: Base64-encoded JSON containing IPEK and KSN
        JsonObject stub = new JsonObject();
        stub.addProperty("type", "TR-31-stub");
        stub.addProperty("ipek", ipek);
        stub.addProperty("ksn", ksn);
        String json = new Gson().toJson(stub);
        return android.util.Base64.encodeToString(json.getBytes(), android.util.Base64.NO_WRAP);
    }

    @Override
    public void getDukptKeys(String terminalId, DukptKeysCallback callback) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            // Mock mode: Return mock DUKPT keys for testing
            callMockDukptKeys(terminalId, callback);
        } else {
            // Production mode: Call actual API
            callProductionDukptKeys(terminalId, callback);
        }
    }

    /**
     * Mock DUKPT keys fetch (returns mock keys for testing)
     */
    private void callMockDukptKeys(String terminalId, DukptKeysCallback callback) {
        LogUtil.e(TAG, "=== MOCK: DUKPT Keys Fetch Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + terminalId);

        new Thread(() -> {
            if (simulateNetworkDelay) {
                try {
                    Thread.sleep(100 + random.nextInt(200)); // 100-300ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Generate mock DUKPT keys response
            // Note: These are MOCK keys - never use in production!
            int mockKeyIndex = 1100; // Default DUKPT key index
            String mockIpek = generateMockHexKey(32); // 32 hex chars = 16 bytes
            String mockKsn = generateMockHexKey(20); // 20 hex chars = 10 bytes
            String mockEffectiveDate = getCurrentTimeIso();
            String mockCiphertext = generateMockCiphertext(mockIpek, mockKsn);

            DukptKeysResponse response = DukptKeysResponse.success(
                    terminalId,
                    "DUKPT",
                    mockKeyIndex,
                    mockIpek,
                    mockKsn,
                    mockEffectiveDate,
                    mockCiphertext);

            LogUtil.e(TAG, "=== MOCK: DUKPT Keys Fetch Success ===");
            LogUtil.e(TAG, "  Key Index: " + mockKeyIndex);
            LogUtil.e(TAG, "  IPEK: " + mockIpek.substring(0, 8) + "****");
            LogUtil.e(TAG, "  KSN: " + mockKsn);
            LogUtil.e(TAG, "  ⚠️ WARNING: These are MOCK keys - never use in production!");

            final DukptKeysResponse finalResponse = response;
            mainHandler.post(() -> callback.onDukptKeysComplete(finalResponse));
        }).start();
    }

    /**
     * Production DUKPT keys fetch (calls actual backend API)
     */
    private void callProductionDukptKeys(String terminalId, DukptKeysCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION: DUKPT Keys Fetch Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + terminalId);
        LogUtil.e(TAG, "  Endpoint: " + buildUrl(ROUTE_DUKPT_KEYS));

        try {
            // Build URL with terminal_id query parameter
            String url = buildUrl(ROUTE_DUKPT_KEYS) + "?terminal_id=" +
                    java.net.URLEncoder.encode(terminalId, "UTF-8");

            // Build HTTP GET request
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .build();

            LogUtil.e(TAG, "  Request URL: " + url);

            // Execute request asynchronously
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ DUKPT keys fetch request failed: " + e.getMessage());
                    mainHandler.post(() -> callback.onDukptKeysError(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "";
                        LogUtil.e(TAG, "❌ DUKPT keys fetch failed: HTTP " + response.code() + " - " + errorBody);
                        mainHandler.post(() -> callback.onDukptKeysError(
                                new IOException("DUKPT keys fetch failed: HTTP " + response.code())));
                        return;
                    }

                    String responseBody = response.body() != null ? response.body().string() : "{}";
                    LogUtil.e(TAG, "✓ DUKPT keys response received");
                    LogUtil.e(TAG, responseBody);

                    try {
                        DukptKeysResponse dukptResponse = parseDukptKeysResponse(responseBody);
                        mainHandler.post(() -> callback.onDukptKeysComplete(dukptResponse));
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing DUKPT keys response: " + e.getMessage());
                        mainHandler.post(() -> callback.onDukptKeysError(e));
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building DUKPT keys request: " + e.getMessage());
            mainHandler.post(() -> callback.onDukptKeysError(e));
        }
    }

    /**
     * Parse DUKPT keys response from JSON
     * Response format should match key rotation response structure:
     * {
     * "status": "success",
     * "terminal_id": "...",
     * "key_type": "DUKPT",
     * "key_index": 1100,
     * "ipek": "...",
     * "ksn": "...",
     * "effective_date": "...",
     * "ciphertext": "..." (optional)
     * }
     */
    private DukptKeysResponse parseDukptKeysResponse(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        // Check if response indicates success
        String status = jsonObject.has("status") ? jsonObject.get("status").getAsString() : "success";
        if (!"success".equals(status)) {
            String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Unknown error";
            return DukptKeysResponse.error(new Exception(message), message);
        }

        // Parse DUKPT keys data
        // Backend may return keys at root level or in a nested object
        String terminalId = jsonObject.has("terminal_id") ? jsonObject.get("terminal_id").getAsString() : "";
        String keyType = jsonObject.has("key_type") ? jsonObject.get("key_type").getAsString() : "DUKPT";
        int keyIndex = jsonObject.has("key_index") ? jsonObject.get("key_index").getAsInt() : 1100;
        String ipek = jsonObject.has("ipek") ? jsonObject.get("ipek").getAsString() : "";
        String ksn = jsonObject.has("ksn") ? jsonObject.get("ksn").getAsString() : "";
        String effectiveDate = jsonObject.has("effective_date") ? jsonObject.get("effective_date").getAsString()
                : getCurrentTimeIso();
        String ciphertext = jsonObject.has("ciphertext") ? jsonObject.get("ciphertext").getAsString() : "";

        // Validate required fields
        if (ipek.isEmpty() || ksn.isEmpty()) {
            return DukptKeysResponse.error(new Exception("Missing IPEK or KSN in response"),
                    "DUKPT keys response missing required fields");
        }

        return DukptKeysResponse.success(terminalId, keyType, keyIndex, ipek, ksn, effectiveDate, ciphertext);
    }

    @Override
    public void reverseTransaction(ReversalRequest request, ReversalCallback callback) {
        if (isProductionMode()) {
            callProductionReversalApi(request, callback);
        } else {
            callMockReversalApi(request, callback);
        }
    }

    /**
     * Call production reversal API endpoint
     */
    private void callProductionReversalApi(ReversalRequest request, ReversalCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION API: Reversal Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  RRN: " + request.rrn);
        LogUtil.e(TAG, "  Amount: " + request.amount);
        LogUtil.e(TAG, "  Reason: " + request.reversalReason);

        try {
            JsonObject requestJson = buildReversalRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);

            String url = buildUrl(ROUTE_REVERSAL);
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .build();

            LogUtil.e(TAG, "Calling production reversal API: " + url);
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                LogUtil.e(TAG, "Request body: " + requestBodyStr);
            }

            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Production reversal API", e);
                    mainHandler.post(() -> callback.onReversalError(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            LogUtil.e(TAG,
                                    "❌ Production reversal API error: HTTP " + response.code() + " - " + errorBody);
                            IOException error = new IOException("HTTP " + response.code() + ": " + errorBody);
                            mainHandler.post(() -> callback.onReversalError(error));
                            return;
                        }

                        String responseBody = response.body() != null ? response.body().string() : "{}";
                        LogUtil.e(TAG, "=== Production Reversal API Response ===");
                        LogUtil.e(TAG, responseBody);

                        ReversalResponse reversalResponse = parseReversalResponse(responseBody);
                        mainHandler.post(() -> callback.onReversalComplete(reversalResponse));
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing production reversal API response: " + e.getMessage());
                        mainHandler.post(() -> callback.onReversalError(e));
                    } finally {
                        if (response.body() != null)
                            response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Building production reversal API request", e);
            mainHandler.post(() -> callback.onReversalError(e));
        }
    }

    /**
     * Build reversal request JSON
     */
    private JsonObject buildReversalRequestJson(ReversalRequest request) {
        JsonObject json = new JsonObject();
        json.addProperty("terminal_id", request.terminalId);
        json.addProperty("merchant_id", request.merchantId);
        json.addProperty("rrn", request.rrn);
        json.addProperty("amount", request.amount);
        json.addProperty("currency", request.currencyCode != null ? request.currencyCode : "818");
        json.addProperty("reversal_reason", request.reversalReason != null ? request.reversalReason : "USER_REQUEST");

        // Pack raw ISO8583 binary frame (0400 = Reversal Request)
        String stan = generateStanForReversal();
        byte[] isoFrame = Iso8583Packer.pack0400(
                request.rrn,
                request.amount,
                stan,
                request.currencyCode != null ? request.currencyCode : "818",
                request.terminalId,
                request.merchantId,
                request.reversalReason != null ? request.reversalReason : "USER_REQUEST");

        // Base64 encode raw ISO frame
        if (isoFrame != null && isoFrame.length > 0) {
            String isoRawB64 = Base64.encodeToString(isoFrame, Base64.NO_WRAP);
            json.addProperty("iso_raw_b64", isoRawB64);
            LogUtil.e(TAG, "✓ Raw ISO8583 frame (0400) attached - length: " + isoFrame.length + " bytes");

            // Save ISO frame to disk for debugging
            IsoLogger.save(isoFrame, "0400");
        } else {
            LogUtil.e(TAG, "⚠️ Failed to pack ISO8583 reversal frame");
        }

        return json;
    }

    /**
     * Generate STAN for reversal
     */
    private String generateStanForReversal() {
        long timestamp = System.currentTimeMillis();
        return String.format(Locale.US, "%06d", timestamp % 1000000);
    }

    /**
     * Parse reversal response JSON from backend
     */
    private ReversalResponse parseReversalResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            String responseCode = json.has("response_code") ? json.get("response_code").getAsString() : "";
            String responseMessage = json.has("response_message") ? json.get("response_message").getAsString()
                    : (json.has("message") ? json.get("message").getAsString() : "");

            if ("00".equals(responseCode)) {
                LogUtil.e(TAG, "✓ Reversal approved: " + responseMessage);
                return ReversalResponse.success(responseCode, responseMessage);
            } else {
                LogUtil.e(TAG, "❌ Reversal declined: " + responseCode + " - " + responseMessage);
                return ReversalResponse.declined(responseCode, responseMessage);
            }
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Parsing reversal response", e);
            return ReversalResponse.error(e, "Failed to parse response: " + e.getMessage());
        }
    }

    /**
     * Call mock reversal API (for testing without backend)
     */
    private void callMockReversalApi(ReversalRequest request, ReversalCallback callback) {
        LogUtil.e(TAG, "=== MOCK: Reversal Request ===");
        LogUtil.e(TAG, request.toString());

        new Thread(() -> {
            if (simulateNetworkDelay) {
                long delay = 100 + random.nextInt(200); // 100-300ms delay
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Mock: Approve reversal if RRN exists
            boolean shouldApprove = request.rrn != null && !request.rrn.isEmpty();
            ReversalResponse response = shouldApprove ? ReversalResponse.success("00", "REVERSAL_APPROVED")
                    : ReversalResponse.declined("94", "NO_MATCH");

            LogUtil.e(TAG, "=== MOCK: Reversal " + (shouldApprove ? "APPROVED" : "DECLINED") + " ===");
            LogUtil.e(TAG, response.toString());

            final ReversalResponse finalResponse = response;
            mainHandler.post(() -> callback.onReversalComplete(finalResponse));
        }).start();
    }

    @Override
    public void announceKey(KeyAnnounceRequest request, KeyAnnounceCallback callback) {
        if (isProductionMode()) {
            callProductionKeyAnnounceApi(request, callback);
        } else {
            callMockKeyAnnounceApi(request, callback);
        }
    }

    /**
     * Call production key announcement API endpoint
     */
    private void callProductionKeyAnnounceApi(KeyAnnounceRequest request, KeyAnnounceCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION API: Key Announce Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  KCV: " + request.kcv);
        LogUtil.e(TAG,
                "  Key Block Length: " + (request.kbPosB64 != null ? request.kbPosB64.length() : 0) + " (base64)");

        try {
            JsonObject requestJson = buildKeyAnnounceRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);

            String url = buildUrl(ROUTE_KEY_ANNOUNCE);
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .build();

            LogUtil.e(TAG, "Calling production key announcement API: " + url);
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                // Don't log full key block in production
                JsonObject maskedJson = requestJson.deepCopy();
                if (maskedJson.has("kb_pos_b64")) {
                    String kb = maskedJson.get("kb_pos_b64").getAsString();
                    String masked = kb.length() > 20 ? kb.substring(0, 10) + "..." + kb.substring(kb.length() - 10)
                            : "***";
                    maskedJson.addProperty("kb_pos_b64", masked);
                }
                LogUtil.e(TAG, "Request body (masked): " + gson.toJson(maskedJson));
            }

            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Production key announcement API", e);
                    mainHandler.post(() -> callback.onKeyAnnounceError(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            LogUtil.e(TAG, "❌ Production key announcement API error: HTTP " + response.code() + " - "
                                    + errorBody);
                            IOException error = new IOException("HTTP " + response.code() + ": " + errorBody);
                            mainHandler.post(() -> callback.onKeyAnnounceError(error));
                            return;
                        }

                        String responseBody = response.body() != null ? response.body().string() : "{}";
                        LogUtil.e(TAG, "=== Production Key Announce API Response ===");
                        LogUtil.e(TAG, responseBody);

                        KeyAnnounceResponse announceResponse = parseKeyAnnounceResponse(responseBody);
                        mainHandler.post(() -> callback.onKeyAnnounceComplete(announceResponse));
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing production key announcement API response: " + e.getMessage());
                        mainHandler.post(() -> callback.onKeyAnnounceError(e));
                    } finally {
                        if (response.body() != null)
                            response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Building production key announcement API request", e);
            mainHandler.post(() -> callback.onKeyAnnounceError(e));
        }
    }

    /**
     * Build key announcement request JSON
     */
    private JsonObject buildKeyAnnounceRequestJson(KeyAnnounceRequest request) {
        JsonObject json = new JsonObject();
        json.addProperty("terminal_id", request.terminalId);
        json.addProperty("kb_pos_b64", request.kbPosB64);
        json.addProperty("kcv", request.kcv);
        json.addProperty("pin_key_set_hint", request.pinKeySetHint);
        if (request.prevPinKeyId != null && !request.prevPinKeyId.isEmpty()) {
            json.addProperty("prev_pin_key_id", request.prevPinKeyId);
        }
        return json;
    }

    /**
     * Parse key announcement response JSON from backend
     */
    private KeyAnnounceResponse parseKeyAnnounceResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            String status = json.has("status") ? json.get("status").getAsString() : "";
            if ("ok".equals(status) || json.has("pin_key_id")) {
                String pinKeyId = json.has("pin_key_id") ? json.get("pin_key_id").getAsString() : "";
                int setId = json.has("pin_key_set") ? json.get("pin_key_set").getAsInt() : 1001;
                int verId = json.has("pin_key_ver") ? json.get("pin_key_ver").getAsInt() : 1;

                LogUtil.e(TAG, "✓ Key announced successfully - pin_key_id: " + pinKeyId);
                return KeyAnnounceResponse.success(pinKeyId, setId, verId);
            } else {
                String message = json.has("message") ? json.get("message").getAsString() : "Unknown error";
                LogUtil.e(TAG, "❌ Key announcement failed: " + message);
                return KeyAnnounceResponse.error(new Exception(message), message);
            }
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Parsing key announcement response", e);
            return KeyAnnounceResponse.error(e, "Failed to parse response: " + e.getMessage());
        }
    }

    /**
     * Call mock key announcement API (for testing without backend)
     */
    private void callMockKeyAnnounceApi(KeyAnnounceRequest request, KeyAnnounceCallback callback) {
        LogUtil.e(TAG, "=== MOCK: Key Announce Request ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  KCV: " + request.kcv);

        new Thread(() -> {
            if (simulateNetworkDelay) {
                long delay = 100 + random.nextInt(200); // 100-300ms delay
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Mock: Generate pin_key_id
            String mockPinKeyId = "S" + new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US)
                    .format(new java.util.Date()) + "-" +
                    String.format(Locale.US, "%06d", random.nextInt(999999));
            int mockSetId = request.pinKeySetHint > 0 ? request.pinKeySetHint : 1001;
            int mockVerId = random.nextInt(100) + 1;

            KeyAnnounceResponse response = KeyAnnounceResponse.success(mockPinKeyId, mockSetId, mockVerId);

            LogUtil.e(TAG, "=== MOCK: Key Announce SUCCESS ===");
            LogUtil.e(TAG, "  pin_key_id: " + mockPinKeyId);
            LogUtil.e(TAG, "  pin_key_set: " + mockSetId);
            LogUtil.e(TAG, "  pin_key_ver: " + mockVerId);

            final KeyAnnounceResponse finalResponse = response;
            mainHandler.post(() -> callback.onKeyAnnounceComplete(finalResponse));
        }).start();
    }
}
