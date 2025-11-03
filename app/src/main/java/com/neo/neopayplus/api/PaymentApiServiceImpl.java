package com.neo.neopayplus.api;

import android.os.Handler;
import android.os.Looper;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.iso.Iso8583Packer;
import com.neo.neopayplus.iso.IsoLogger;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Random;
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
    // Note: Base URL already includes /v1, so route is just /transactions/authorize
    private static final String ROUTE_AUTHORIZE = "/transactions/authorize";
    private static final String ROUTE_KEY_ROTATION = "/keys/rotate";
    private static final String ROUTE_DUKPT_KEYS = "/terminal/dukpt";
    private static final String ROUTE_REVERSAL = "/transactions/reverse";
    
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
     * @param apiKey API key for authentication
     */
    public PaymentApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
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
            LogUtil.e(TAG, "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
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
        
        try {
            JsonObject requestJson = buildRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);
            
            String url = baseUrl + ROUTE_AUTHORIZE;
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
                            callback.onAuthorizationError(new IOException("HTTP " + response.code() + ": " + errorBody));
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
                            } else if ("55".equals(authResponse.responseCode) || "63".equals(authResponse.responseCode)) {
                                LogUtil.e(TAG, "⚠️ PIN verification failed - incorrect PIN");
                            }
                        }
                        
                        callback.onAuthorizationComplete(authResponse);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "❌ Error parsing production API response: " + e.getMessage());
                        callback.onAuthorizationError(e);
                    } finally {
                        if (response.body() != null) response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building production API request: " + e.getMessage());
            callback.onAuthorizationError(e);
        }
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
                        "Host timeout - amount exceeds threshold"
                    );
                    callback.onAuthorizationError(new IOException("HOST_UNAVAILABLE"));
                });
                return;
            }
            
            // Check for key sync requirement (every 10th transaction)
            if (currentTxCount % 10 == 0) {
                LogUtil.e(TAG, "=== MOCK: Key sync required (97) ===");
                AuthorizationResponse keySyncResponse = AuthorizationResponse.declined("97", "Key sync required - trigger /terminal/config");
                mainHandler.post(() -> callback.onAuthorizationComplete(keySyncResponse));
                return;
            }
            
            // Determine approval based on amount
            boolean shouldApprove = amount <= 200.0;
            AuthorizationResponse response = shouldApprove ? 
                createMockSuccessResponse(request) : createMockDeclinedResponse(amount);
            
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
        json.addProperty("terminal_id", PaymentConfig.getTerminalId());
        json.addProperty("merchant_id", PaymentConfig.getMerchantId());
        
        // Convert amount from piasters (smallest unit) to main currency unit
        double amountInMainUnit = parseAmount(request.amount);
        json.addProperty("amount", amountInMainUnit);
        json.addProperty("currency", PaymentConfig.CURRENCY_NAME); // "EGP"
        json.addProperty("transaction_type", "SALE");
        
        // Mask PAN for security (real data from terminal)
        String panMasked = request.pan != null ? maskCardNumber(request.pan) : "";
        json.addProperty("pan_masked", panMasked);
        
        // PIN key ID (from KeyManagerPOS - server-issued key identifier)
        // Backend uses this to look up the TPK that was rewrapped under Bank TMK
        try {
            com.neo.neopayplus.security.KeyManagerPOS.State keyState = 
                com.neo.neopayplus.security.KeyManagerPOS.load(MyApplication.app);
            if (keyState != null && keyState.pinKeyId != null && !keyState.pinKeyId.isEmpty()) {
                json.addProperty("pin_key_id", keyState.pinKeyId);
                LogUtil.e(TAG, "✓ PIN key ID included: " + keyState.pinKeyId);
            } else {
                LogUtil.e(TAG, "⚠️ No pin_key_id available - transaction may fail if backend requires it");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading PIN key ID: " + e.getMessage());
        }
        
        // EMV data structure (from field55 - real data from terminal)
        if (request.field55 != null && !request.field55.isEmpty()) {
            JsonObject emvData = new JsonObject();
            // Pass field55 as-is for backend to parse
            json.addProperty("track2_encrypted", ""); // Would be encrypted track2 if available
            json.addProperty("emv_data_raw", request.field55);
            
            // Parse common EMV tags from field55 (for structured data)
            try {
                parseEmvTagsFromField55(request.field55, emvData);
                json.add("emv_data", emvData);
            } catch (Exception e) {
                LogUtil.e(TAG, "Could not parse EMV tags from field55: " + e.getMessage());
                json.add("emv_data", emvData);
            }
        }
        
        // PIN block and KSN (real data from terminal - required for online PIN with DUKPT)
        // Backend will decrypt PIN block using IPEK + KSN and verify PIN
        if (request.pinBlock != null && request.pinBlock.length > 0) {
            String pinBlockHex = bytesToHex(request.pinBlock);
            json.addProperty("pin_block", pinBlockHex);
            LogUtil.e(TAG, "✓ Sending real PIN block to backend (masked: " + 
                (pinBlockHex.length() > 8 ? pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4) : "****") + ")");
        } else {
            LogUtil.e(TAG, "⚠️ No PIN block in request - transaction may be offline PIN or no PIN required");
        }
        
        if (request.ksn != null && !request.ksn.isEmpty()) {
            json.addProperty("ksn", request.ksn);
            LogUtil.e(TAG, "✓ Sending real KSN to backend: " + request.ksn);
        } else {
            LogUtil.e(TAG, "⚠️ No KSN in request - required for DUKPT PIN decryption");
        }
        
        // Datetime in ISO8601 format (real transaction date/time)
        String datetime = formatDateTime(request.date, request.time);
        json.addProperty("datetime", datetime);
        
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
                   LogUtil.e(TAG, "✓ PIN block extracted for DE52: " + pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4));
               }
           }
           
           byte[] isoFrame = Iso8583Packer.pack0100(
               request.pan != null ? maskCardNumber(request.pan) : "",
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
            
            // Save ISO frame to disk for debugging
            IsoLogger.save(isoFrame, "0100");
        } else {
            LogUtil.e(TAG, "⚠️ Failed to pack ISO8583 frame");
        }
        
        return json;
    }
    
    /**
     * Build ISO8583 iso_fields object
     * Includes standard ISO8583 data elements plus DE55 (ICC Data) from EMV Field 55
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
            LogUtil.e(TAG, "✓ DE55 (ICC Data) included in ISO fields - length: " + request.field55.length() + " hex chars");
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
                return String.format("%06d", stanValue % 1000000);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error generating STAN: " + e.getMessage());
        }
        // Fallback: use last 6 digits of timestamp
        long timestamp = System.currentTimeMillis();
        return String.format("%06d", timestamp % 1000000);
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
        
        // For now, default to Chip + PIN (most common for EMV)
        // In production, this should come from EMV kernel or transaction context
        boolean hasPin = (request.pinBlock != null && request.pinBlock.length > 0);
        boolean isContactless = false; // TODO: Detect from card type
        
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
        // Fallback to current time
        return java.time.Instant.now().toString();
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return "****";
        }
        // Format: "400000******7899"
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(cardNumber.length() - 4);
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
        
        // Backend API spec format: response_code, response_message, auth_code, issuer_auth_data, field_55, rrn
        String responseCode = json.has("response_code") ? json.get("response_code").getAsString() : "XX";
        String responseMessage = json.has("response_message") ? json.get("response_message").getAsString() : "";
        String authCode = json.has("auth_code") ? json.get("auth_code").getAsString() : "";
        String rrn = json.has("rrn") ? json.get("rrn").getAsString() : "";
        String issuerAuthData = json.has("issuer_auth_data") ? json.get("issuer_auth_data").getAsString() : "";
        String field55 = json.has("field_55") ? json.get("field_55").getAsString() : "";
        
        boolean approved = "00".equals(responseCode);
        
        // Parse EMV response tags from field_55 or issuer_auth_data (real data from backend)
        String[] responseTags = new String[0];
        String[] responseValues = new String[0];
        
        if (approved && issuerAuthData != null && !issuerAuthData.isEmpty()) {
            // Parse issuer_auth_data (tag 91) from response
            responseTags = new String[]{"91"};
            responseValues = new String[]{issuerAuthData};
            LogUtil.e(TAG, "✓ Received issuer_auth_data (tag 91) from backend");
        } else if (approved && field55 != null && !field55.isEmpty()) {
            // Parse field_55 TLV data (full EMV response)
            responseTags = new String[]{"91"};
            responseValues = new String[]{issuerAuthData};
            LogUtil.e(TAG, "✓ Received field_55 EMV data from backend");
        }
        
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
            LogUtil.e(TAG, "⚠️ Backend authorization: DECLINED");
            LogUtil.e(TAG, "  Response Code: " + responseCode);
            LogUtil.e(TAG, "  Message: " + responseMessage);
            return AuthorizationResponse.declined(responseCode, responseMessage);
        }
    }
    
    private AuthorizationResponse createMockSuccessResponse(AuthorizationRequest request) {
        // Per spec format: response_code="00", response_message="APPROVED", auth_code, issuer_auth_data, rrn
        String authCode = String.format("%06d", random.nextInt(1000000));
        String rrn = generateRRN(request.date, request.time);
        String issuerAuthData = generateMockIAD(); // Tag 91 value
        
        // EMV response tags for importOnlineProcStatus
        String[] responseTags = {"91"}; // Issuer Authentication Data
        String[] responseValues = {issuerAuthData};
        
        return AuthorizationResponse.success(authCode, rrn, responseTags, responseValues);
    }
    
    private String generateRRN(String date, String time) {
        // Generate RRN: format "YYMMDDHHMMSS" or similar
        if (date != null && time != null && date.length() == 6 && time.length() == 6) {
            return date + time;
        }
        // Fallback to random 12-digit number
        return String.format("%012d", Math.abs(random.nextLong() % 1000000000000L));
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
            case "05": return "Transaction declined - Do not honor";
            case "14": return "Transaction declined - Invalid card number";
            case "51": return "Transaction declined - Insufficient funds";
            case "54": return "Transaction declined - Expired card";
            case "61": return "Transaction declined - Exceeds withdrawal limit";
            case "91": return "Transaction declined - Issuer unavailable";
            default: return "Transaction declined - Code: " + responseCode;
        }
    }
    
    private String generateMockIAD() {
        // Generate Issuer Authentication Data (tag 91) - typically 10 bytes (20 hex chars)
        // Per spec: "910A112233445566" format (91 = tag, 0A = length, then data)
        StringBuilder iad = new StringBuilder(20);
        for (int i = 0; i < 20; i++) {
            iad.append(String.format("%x", random.nextInt(16)));
        }
        return iad.toString().toUpperCase();
    }
    
    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
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
        if (jsonString == null || jsonString.isEmpty()) return "";
        
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
            String mockKsn = generateMockHexKey(20);  // 20 hex chars = 10 bytes
            String mockEffectiveDate = java.time.Instant.now().toString();
            String mockCiphertext = generateMockCiphertext(mockIpek, mockKsn);
            
            KeyRotationResponse response = KeyRotationResponse.success(
                request.terminalId,
                request.keyType,
                mockKeyIndex,
                mockIpek,
                mockKsn,
                mockEffectiveDate,
                mockCiphertext
            );
            
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
        LogUtil.e(TAG, "  Endpoint: " + baseUrl + ROUTE_KEY_ROTATION);
        
        try {
            // Build JSON request
            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("terminal_id", request.terminalId);
            requestJson.addProperty("key_type", request.keyType);
            
            String jsonBody = new Gson().toJson(requestJson);
            LogUtil.e(TAG, "  Request body: " + jsonBody);
            
            // Build HTTP request
            Request httpRequest = new Request.Builder()
                .url(baseUrl + ROUTE_KEY_ROTATION)
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
                            new IOException("Key rotation failed: HTTP " + response.code())
                        ));
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
            String mockKsn = generateMockHexKey(20);  // 20 hex chars = 10 bytes
            String mockEffectiveDate = java.time.Instant.now().toString();
            String mockCiphertext = generateMockCiphertext(mockIpek, mockKsn);
            
            DukptKeysResponse response = DukptKeysResponse.success(
                terminalId,
                "DUKPT",
                mockKeyIndex,
                mockIpek,
                mockKsn,
                mockEffectiveDate,
                mockCiphertext
            );
            
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
        LogUtil.e(TAG, "  Endpoint: " + baseUrl + ROUTE_DUKPT_KEYS);
        
        try {
            // Build URL with terminal_id query parameter
            String url = baseUrl + ROUTE_DUKPT_KEYS + "?terminal_id=" + 
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
                            new IOException("DUKPT keys fetch failed: HTTP " + response.code())
                        ));
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
     *   "status": "success",
     *   "terminal_id": "...",
     *   "key_type": "DUKPT",
     *   "key_index": 1100,
     *   "ipek": "...",
     *   "ksn": "...",
     *   "effective_date": "...",
     *   "ciphertext": "..." (optional)
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
        String effectiveDate = jsonObject.has("effective_date") ? jsonObject.get("effective_date").getAsString() : 
                               java.time.Instant.now().toString();
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
            
            String url = baseUrl + ROUTE_REVERSAL;
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
                    LogUtil.e(TAG, "❌ Production reversal API error: " + e.getMessage());
                    e.printStackTrace();
                    mainHandler.post(() -> callback.onReversalError(e));
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            LogUtil.e(TAG, "❌ Production reversal API error: HTTP " + response.code() + " - " + errorBody);
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
                        if (response.body() != null) response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building production reversal API request: " + e.getMessage());
            e.printStackTrace();
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
            request.reversalReason != null ? request.reversalReason : "USER_REQUEST"
        );
        
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
        return String.format("%06d", timestamp % 1000000);
    }
    
    /**
     * Parse reversal response JSON from backend
     */
    private ReversalResponse parseReversalResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            
            String responseCode = json.has("response_code") ? json.get("response_code").getAsString() : "";
            String responseMessage = json.has("response_message") ? json.get("response_message").getAsString() : 
                    (json.has("message") ? json.get("message").getAsString() : "");
            
            if ("00".equals(responseCode)) {
                LogUtil.e(TAG, "✓ Reversal approved: " + responseMessage);
                return ReversalResponse.success(responseCode, responseMessage);
            } else {
                LogUtil.e(TAG, "❌ Reversal declined: " + responseCode + " - " + responseMessage);
                return ReversalResponse.declined(responseCode, responseMessage);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error parsing reversal response: " + e.getMessage());
            e.printStackTrace();
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
            ReversalResponse response = shouldApprove ?
                ReversalResponse.success("00", "REVERSAL_APPROVED") :
                ReversalResponse.declined("94", "NO_MATCH");
            
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
        LogUtil.e(TAG, "  Key Block Length: " + (request.kbPosB64 != null ? request.kbPosB64.length() : 0) + " (base64)");
        
        try {
            JsonObject requestJson = buildKeyAnnounceRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);
            
            String url = baseUrl + "/keys/announce";
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
                    String masked = kb.length() > 20 ? kb.substring(0, 10) + "..." + kb.substring(kb.length() - 10) : "***";
                    maskedJson.addProperty("kb_pos_b64", masked);
                }
                LogUtil.e(TAG, "Request body (masked): " + gson.toJson(maskedJson));
            }
            
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ Production key announcement API error: " + e.getMessage());
                    e.printStackTrace();
                    mainHandler.post(() -> callback.onKeyAnnounceError(e));
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                            LogUtil.e(TAG, "❌ Production key announcement API error: HTTP " + response.code() + " - " + errorBody);
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
                        if (response.body() != null) response.body().close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building production key announcement API request: " + e.getMessage());
            e.printStackTrace();
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
            LogUtil.e(TAG, "❌ Error parsing key announcement response: " + e.getMessage());
            e.printStackTrace();
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
                String.format("%06d", random.nextInt(999999));
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
