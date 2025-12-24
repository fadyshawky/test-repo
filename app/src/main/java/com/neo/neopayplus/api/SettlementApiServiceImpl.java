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
    private static final String ROUTE_SETTLEMENT_UPLOAD = "/tx/settlement";
    private static final String ROUTE_CLEAR_TRANSACTIONS = "/tx/clear-transactions";
    
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
     * @param apiKey  API key for authentication
     */
    public SettlementApiServiceImpl(String baseUrl, String apiKey) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
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
            LogUtil.e(TAG,
                    "  API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
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
            
            String url = buildUrl(ROUTE_SETTLEMENT_UPLOAD);
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
                    com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Settlement batch upload", e);
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
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Building settlement batch upload request", e);
            mainHandler.post(() -> callback.onBatchUploadError(e));
        }
    }
    
    /**
     * Clear all transactions from backend
     */
    public void clearBackendTransactions(ClearTransactionsCallback callback) {
        if (!isProductionMode()) {
            // Mock mode - simulate success
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onClearComplete(true, "Mock: Transactions cleared (mock mode)");
                }
            });
            return;
        }
        
        new Thread(() -> {
            try {
                String url = buildUrl(ROUTE_CLEAR_TRANSACTIONS);
                LogUtil.e(TAG, "=== Clearing backend transactions ===");
                LogUtil.e(TAG, "  POST " + url);
                
                JsonObject requestJson = new JsonObject();
                String requestBodyStr = new Gson().toJson(requestJson);
                RequestBody requestBody = RequestBody.create(JSON, requestBodyStr);
                
                Request httpRequest = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + (apiKey != null ? apiKey : "test-token"))
                        .build();
                
                try (Response response = httpClient.newCall(httpRequest).execute()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    boolean success = response.isSuccessful();
                    
                    if (success) {
                        LogUtil.e(TAG, "✓ Backend transactions cleared successfully");
                    } else {
                        LogUtil.e(TAG, "✗ Failed to clear backend transactions: HTTP " + response.code());
                    }
                    
                    final boolean finalSuccess = success;
                    final String finalMessage = success ? "Backend transactions cleared" : "Failed to clear backend transactions";
                    
                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onClearComplete(finalSuccess, finalMessage);
                        }
                    });
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "✗ Error clearing backend transactions: " + e.getMessage());
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onClearComplete(false, "Error: " + e.getMessage());
                    }
                });
            }
        }).start();
    }
    
    /**
     * Callback interface for clear transactions operation
     */
    public interface ClearTransactionsCallback {
        void onClearComplete(boolean success, String message);
    }
    
    /**
     * Build request JSON from batch upload request
     */
    private JsonObject buildRequestJson(BatchUploadRequest request) {
        JsonObject json = new JsonObject();
        json.addProperty("terminal_id", request.terminalId);
        json.addProperty("batch_number", request.batchNumber != null ? request.batchNumber : "");
        json.addProperty("batch_date", request.batchDate);
        json.addProperty("batch_time", request.batchTime != null ? request.batchTime : "");
        
        // Send transactions for validation - backend will compare with transactions found by batch number
        JsonArray transactionsArray = new JsonArray();
        if (request.transactions != null) {
            for (SettlementTransaction tx : request.transactions) {
                JsonObject txJson = new JsonObject();
                txJson.addProperty("transaction_id", tx.transactionId != null ? tx.transactionId : "");
                txJson.addProperty("rrn", tx.rrn != null ? tx.rrn : "");
                txJson.addProperty("status", tx.status != null ? tx.status : "");
                // Only send minimal data needed for matching
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
                String batchNumber = json.has("batch_number") ? json.get("batch_number").getAsString() : batchId;
                String batchDate = json.has("batch_date") ? json.get("batch_date").getAsString() : "";
                String batchTime = json.has("batch_time") ? json.get("batch_time").getAsString() : "";
                String terminalId = json.has("terminal_id") ? json.get("terminal_id").getAsString() : "";
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
                
                // Parse totals (all calculated by backend)
                SettlementTotals totals = new SettlementTotals();
                if (json.has("totals")) {
                    JsonObject totalsObj = json.getAsJsonObject("totals");
                    // Sales
                    totals.countSales = totalsObj.has("count_sales") ? totalsObj.get("count_sales").getAsInt() : 0;
                    totals.totalSales = totalsObj.has("total_sales") ? totalsObj.get("total_sales").getAsString() : "0.00";
                    // Refunds
                    totals.countRefund = totalsObj.has("count_refund") ? totalsObj.get("count_refund").getAsInt() : 0;
                    totals.totalRefund = totalsObj.has("total_refund") ? totalsObj.get("total_refund").getAsString() : "0.00";
                    // Voids
                    totals.countVoid = totalsObj.has("count_void") ? totalsObj.get("count_void").getAsInt() : 0;
                    totals.totalVoid = totalsObj.has("total_void") ? totalsObj.get("total_void").getAsString() : "0.00";
                    // Declined
                    totals.countDeclined = totalsObj.has("count_declined") ? totalsObj.get("count_declined").getAsInt() : 0;
                    totals.totalDeclined = totalsObj.has("total_declined") ? totalsObj.get("total_declined").getAsString() : "0.00";
                    // Summary
                    totals.grandTotal = totalsObj.has("grand_total") ? totalsObj.get("grand_total").getAsString() : "0.00";
                    totals.currency = totalsObj.has("currency") ? totalsObj.get("currency").getAsString() : "EGP";
                    
                    // Parse brand-specific totals (calculated by backend)
                    if (totalsObj.has("brands")) {
                        JsonObject brandsObj = totalsObj.getAsJsonObject("brands");
                        
                        // VISA
                        if (brandsObj.has("visa")) {
                            JsonObject visaObj = brandsObj.getAsJsonObject("visa");
                            totals.visa = new SettlementApiService.BrandTotalsData();
                            totals.visa.sales = parseBrandTransactionTotals(visaObj, "sales");
                            totals.visa.voids = parseBrandTransactionTotals(visaObj, "voids");
                            totals.visa.refunds = parseBrandTransactionTotals(visaObj, "refunds");
                            totals.visa.total = visaObj.has("total") ? visaObj.get("total").getAsString() : "0.00";
                        }
                        
                        // MASTERCARD
                        if (brandsObj.has("mastercard")) {
                            JsonObject mastercardObj = brandsObj.getAsJsonObject("mastercard");
                            totals.mastercard = new SettlementApiService.BrandTotalsData();
                            totals.mastercard.sales = parseBrandTransactionTotals(mastercardObj, "sales");
                            totals.mastercard.voids = parseBrandTransactionTotals(mastercardObj, "voids");
                            totals.mastercard.refunds = parseBrandTransactionTotals(mastercardObj, "refunds");
                            totals.mastercard.total = mastercardObj.has("total") ? mastercardObj.get("total").getAsString() : "0.00";
                        }
                        
                        // MEEZA
                        if (brandsObj.has("meeza")) {
                            JsonObject meezaObj = brandsObj.getAsJsonObject("meeza");
                            totals.meeza = new SettlementApiService.BrandTotalsData();
                            totals.meeza.sales = parseBrandTransactionTotals(meezaObj, "sales");
                            totals.meeza.voids = parseBrandTransactionTotals(meezaObj, "voids");
                            totals.meeza.refunds = parseBrandTransactionTotals(meezaObj, "refunds");
                            totals.meeza.total = meezaObj.has("total") ? meezaObj.get("total").getAsString() : "0.00";
                        }
                    }
                }
                
                // Parse settled transactions
                List<SettlementTransaction> settledTransactions = new ArrayList<>();
                if (json.has("transactions")) {
                    JsonArray txArray = json.getAsJsonArray("transactions");
                    for (int i = 0; i < txArray.size(); i++) {
                        JsonObject txJson = txArray.get(i).getAsJsonObject();
                        SettlementTransaction tx = new SettlementTransaction();
                        tx.transactionId = txJson.has("transaction_id") ? txJson.get("transaction_id").getAsString() : "";
                        tx.rrn = txJson.has("rrn") ? txJson.get("rrn").getAsString() : "";
                        tx.amount = txJson.has("amount") ? String.valueOf((int)(txJson.get("amount").getAsDouble() * 100)) : "0";
                        tx.pan = txJson.has("pan") ? txJson.get("pan").getAsString() : "";
                        tx.authCode = txJson.has("auth_code") ? txJson.get("auth_code").getAsString() : "";
                        tx.transactionType = txJson.has("transaction_type") ? txJson.get("transaction_type").getAsString() : "00";
                        tx.status = txJson.has("status") ? txJson.get("status").getAsString() : "";
                        settledTransactions.add(tx);
                    }
                }
                
                String message = json.has("message") ? json.get("message").getAsString()
                        : "Batch upload completed successfully";
                
                LogUtil.e(TAG, "✓ Settlement batch upload successful");
                LogUtil.e(TAG, "  Batch ID: " + batchId);
                LogUtil.e(TAG,
                        "  Total: " + totalCount + ", Accepted: " + acceptedCount + ", Rejected: " + rejectedCount);
                LogUtil.e(TAG, "  Sales: " + totals.countSales + " transactions, Total: " + totals.totalSales + " " + totals.currency);
                LogUtil.e(TAG, "  Refunds: " + totals.countRefund + " transactions, Total: " + totals.totalRefund + " " + totals.currency);
                LogUtil.e(TAG, "  Voids: " + totals.countVoid + " transactions, Total: " + totals.totalVoid + " " + totals.currency);
                LogUtil.e(TAG, "  Declined: " + totals.countDeclined + " transactions, Total: " + totals.totalDeclined + " " + totals.currency);
                LogUtil.e(TAG, "  Grand Total (Sales - Refunds - Voids): " + totals.grandTotal + " " + totals.currency);
                
                BatchUploadResponse response = BatchUploadResponse.success(batchId, totalCount, acceptedCount, rejectedCount, acceptedRrns, rejectedRrns);
                response.batchNumber = batchNumber;
                response.batchDate = batchDate;
                response.batchTime = batchTime;
                response.terminalId = terminalId;
                response.totals = totals;
                response.transactions = settledTransactions;
                return response;
            } else {
                String message = json.has("message") ? json.get("message").getAsString() : "Batch upload failed";
                LogUtil.e(TAG, "❌ Settlement batch upload failed: " + message);
                return BatchUploadResponse.error(new IOException(message), message);
            }
            
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Parsing settlement batch upload response", e);
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
                    batchId, totalCount, acceptedCount, rejectedCount, acceptedRrns, rejectedRrns);
            
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
    
    /**
     * Helper method to parse brand transaction totals from JSON
     */
    private SettlementApiService.BrandTransactionTotals parseBrandTransactionTotals(JsonObject brandObj, String type) {
        SettlementApiService.BrandTransactionTotals totals = new SettlementApiService.BrandTransactionTotals();
        if (brandObj.has(type)) {
            JsonObject typeObj = brandObj.getAsJsonObject(type);
            totals.count = typeObj.has("count") ? typeObj.get("count").getAsInt() : 0;
            totals.total = typeObj.has("total") ? typeObj.get("total").getAsString() : "0.00";
        } else {
            totals.count = 0;
            totals.total = "0.00";
        }
        return totals;
    }
}
