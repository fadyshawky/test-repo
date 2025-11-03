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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Settlement API Service Implementation
 * 
 * Unified implementation that uses production APIs when baseUrl is configured,
 * otherwise falls back to mock responses for testing/development.
 */
public class SettlementApiServiceImpl implements SettlementApiService {
    
    private static final String TAG = Constant.TAG;
    
    // API routes/endpoints
    private static final String ROUTE_SETTLEMENT_UPLOAD = "/settlement/upload";
    
    private static final MediaType JSON = MediaType.parse("application/json");
    
    // Configuration
    private final String baseUrl;
    private final String apiKey;
    private final OkHttpClient httpClient;
    
    // Mock mode helpers
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    /**
     * Create service with default configuration (mock mode)
     */
    public SettlementApiServiceImpl() {
        this(null, null);
    }
    
    /**
     * Create service with baseUrl and apiKey
     * If baseUrl is null or placeholder, uses mock responses
     * 
     * @param baseUrl Backend base URL (e.g., "http://192.168.100.176:8080/v1")
     * @param apiKey API key for authentication
     */
    public SettlementApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        
        // Initialize HTTP client only if baseUrl is configured
        if (isProductionMode()) {
            this.httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            LogUtil.e(TAG, "Settlement API Service initialized - PRODUCTION mode");
            LogUtil.e(TAG, "  Base URL: " + baseUrl);
            LogUtil.e(TAG, "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
        } else {
            this.httpClient = null;
            LogUtil.e(TAG, "Settlement API Service initialized - MOCK mode (baseUrl not configured)");
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
    
    @Override
    public void uploadBatch(BatchUploadRequest request, BatchUploadCallback callback) {
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
     */
    private void callProductionApi(BatchUploadRequest request, BatchUploadCallback callback) {
        LogUtil.e(TAG, "=== PRODUCTION API: Settlement Batch Upload ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  Batch Date: " + request.batchDate);
        LogUtil.e(TAG, "  Transaction Count: " + (request.transactions != null ? request.transactions.size() : 0));
        
        try {
            JsonObject requestJson = buildRequestJson(request);
            Gson gson = new Gson();
            String requestBodyStr = gson.toJson(requestJson);
            
            // Log request payload (mask sensitive data)
            LogUtil.e(TAG, "  Request JSON (masked): " + maskSensitiveData(requestBodyStr));
            
            RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);
            
            String url = baseUrl + ROUTE_SETTLEMENT_UPLOAD;
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                    .build();
            
            LogUtil.e(TAG, "  POST " + url);
            LogUtil.e(TAG, "  Sending batch upload request...");
            
            httpClient.newCall(httpRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "❌ Settlement batch upload failed: " + e.getMessage());
                    e.printStackTrace();
                    mainHandler.post(() -> callback.onBatchUploadError(e));
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    int statusCode = response.code();
                    
                    LogUtil.e(TAG, "  Response Status: " + statusCode);
                    LogUtil.e(TAG, "  Response Body: " + maskSensitiveData(responseBody));
                    
                    if (statusCode == 200 || statusCode == 201) {
                        BatchUploadResponse uploadResponse = parseResponse(responseBody);
                        mainHandler.post(() -> callback.onBatchUploadComplete(uploadResponse));
                    } else {
                        String errorMessage = "Settlement batch upload failed with status " + statusCode;
                        if (!responseBody.isEmpty()) {
                            errorMessage += ": " + responseBody;
                        }
                        LogUtil.e(TAG, "❌ " + errorMessage);
                        IOException error = new IOException(errorMessage);
                        mainHandler.post(() -> callback.onBatchUploadError(error));
                    }
                }
            });
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error building settlement batch upload request: " + e.getMessage());
            e.printStackTrace();
            mainHandler.post(() -> callback.onBatchUploadError(e));
        }
    }
    
    /**
     * Build request JSON from batch upload request
     */
    private JsonObject buildRequestJson(BatchUploadRequest request) {
        JsonObject json = new JsonObject();
        json.addProperty("terminal_id", request.terminalId);
        json.addProperty("batch_date", request.batchDate);
        json.addProperty("batch_time", request.batchTime != null ? request.batchTime : "");
        
        JsonArray transactionsArray = new JsonArray();
        if (request.transactions != null) {
            for (SettlementTransaction tx : request.transactions) {
                JsonObject txJson = new JsonObject();
                txJson.addProperty("rrn", tx.rrn);
                txJson.addProperty("auth_code", tx.authCode != null ? tx.authCode : "");
                txJson.addProperty("pan", tx.pan != null ? tx.pan : "");
                txJson.addProperty("amount", tx.amount);
                txJson.addProperty("currency_code", tx.currencyCode);
                txJson.addProperty("transaction_type", tx.transactionType != null ? tx.transactionType : "00");
                txJson.addProperty("date", tx.date != null ? tx.date : "");
                txJson.addProperty("time", tx.time != null ? tx.time : "");
                txJson.addProperty("field55", tx.field55 != null ? tx.field55 : "");
                txJson.addProperty("response_code", tx.responseCode != null ? tx.responseCode : "");
                txJson.addProperty("status", tx.status != null ? tx.status : "");
                transactionsArray.add(txJson);
            }
        }
        json.add("transactions", transactionsArray);
        
        return json;
    }
    
    /**
     * Parse response JSON from backend
     */
    private BatchUploadResponse parseResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            
            String status = json.has("status") ? json.get("status").getAsString() : "";
            boolean success = "success".equals(status) || json.has("batch_id");
            
            if (success) {
                String batchId = json.has("batch_id") ? json.get("batch_id").getAsString() : "";
                int totalCount = json.has("total_count") ? json.get("total_count").getAsInt() : 0;
                int acceptedCount = json.has("accepted_count") ? json.get("accepted_count").getAsInt() : 0;
                int rejectedCount = json.has("rejected_count") ? json.get("rejected_count").getAsInt() : 0;
                
                List<String> acceptedRrns = new ArrayList<>();
                List<String> rejectedRrns = new ArrayList<>();
                
                if (json.has("accepted_rrns")) {
                    JsonArray acceptedArray = json.getAsJsonArray("accepted_rrns");
                    for (int i = 0; i < acceptedArray.size(); i++) {
                        acceptedRrns.add(acceptedArray.get(i).getAsString());
                    }
                }
                
                if (json.has("rejected_rrns")) {
                    JsonArray rejectedArray = json.getAsJsonArray("rejected_rrns");
                    for (int i = 0; i < rejectedArray.size(); i++) {
                        rejectedRrns.add(rejectedArray.get(i).getAsString());
                    }
                }
                
                String message = json.has("message") ? json.get("message").getAsString() : "Batch upload completed successfully";
                
                LogUtil.e(TAG, "✓ Settlement batch upload successful");
                LogUtil.e(TAG, "  Batch ID: " + batchId);
                LogUtil.e(TAG, "  Total: " + totalCount + ", Accepted: " + acceptedCount + ", Rejected: " + rejectedCount);
                
                return BatchUploadResponse.success(batchId, totalCount, acceptedCount, rejectedCount, acceptedRrns, rejectedRrns);
            } else {
                String message = json.has("message") ? json.get("message").getAsString() : "Batch upload failed";
                LogUtil.e(TAG, "❌ Settlement batch upload failed: " + message);
                return BatchUploadResponse.error(new IOException(message), message);
            }
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error parsing settlement batch upload response: " + e.getMessage());
            e.printStackTrace();
            return BatchUploadResponse.error(e, "Failed to parse response: " + e.getMessage());
        }
    }
    
    /**
     * Call mock API (for testing without backend)
     */
    private void callMockApi(BatchUploadRequest request, BatchUploadCallback callback) {
        LogUtil.e(TAG, "=== MOCK API: Settlement Batch Upload ===");
        LogUtil.e(TAG, "  Terminal ID: " + request.terminalId);
        LogUtil.e(TAG, "  Transaction Count: " + (request.transactions != null ? request.transactions.size() : 0));
        
        // Simulate network delay
        mainHandler.postDelayed(() -> {
            int totalCount = request.transactions != null ? request.transactions.size() : 0;
            int acceptedCount = (int) (totalCount * 0.95); // 95% acceptance rate
            int rejectedCount = totalCount - acceptedCount;
            
            List<String> acceptedRrns = new ArrayList<>();
            List<String> rejectedRrns = new ArrayList<>();
            
            if (request.transactions != null) {
                for (int i = 0; i < request.transactions.size(); i++) {
                    SettlementTransaction tx = request.transactions.get(i);
                    if (i < acceptedCount) {
                        acceptedRrns.add(tx.rrn);
                    } else {
                        rejectedRrns.add(tx.rrn);
                    }
                }
            }
            
            String batchId = "BATCH-" + System.currentTimeMillis();
            BatchUploadResponse response = BatchUploadResponse.success(
                batchId, totalCount, acceptedCount, rejectedCount, acceptedRrns, rejectedRrns
            );
            
            LogUtil.e(TAG, "✓ Mock settlement batch upload successful");
            LogUtil.e(TAG, "  Batch ID: " + batchId);
            LogUtil.e(TAG, "  Accepted: " + acceptedCount + ", Rejected: " + rejectedCount);
            
            callback.onBatchUploadComplete(response);
        }, 500); // 500ms delay
    }
    
    /**
     * Mask sensitive data in JSON string for logging
     */
    private String maskSensitiveData(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        // Mask PAN (pan field)
        json = json.replaceAll("\"pan\"\\s*:\\s*\"([^\"]{4})([^\"]*)([^\"]{4})\"", "\"pan\":\"$1****$3\"");
        
        // Mask Field 55 if present (first and last 8 chars)
        json = json.replaceAll("\"field55\"\\s*:\\s*\"([^\"]{8})([^\"]*)([^\"]{8})\"", "\"field55\":\"$1****$3\"");
        
        return json;
    }
}

