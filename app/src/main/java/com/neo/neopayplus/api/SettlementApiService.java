package com.neo.neopayplus.api;

import java.util.List;

/**
 * Settlement API Service Interface
 * 
 * Defines the contract for settlement/batch upload services.
 * This allows uploading transaction batches for settlement processing.
 */
public interface SettlementApiService {
    
    /**
     * Settlement Transaction Data
     */
    class SettlementTransaction {
        public String rrn;               // Retrieval Reference Number (unique transaction ID)
        public String authCode;          // Authorization code
        public String pan;               // Primary Account Number (masked)
        public String amount;            // Transaction amount in smallest currency unit
        public String currencyCode;      // Currency code (e.g., "818" for EGP)
        public String transactionType;   // Transaction type (e.g., "00" for purchase)
        public String date;              // Transaction date (YYMMDD)
        public String time;              // Transaction time (HHMMSS)
        public String field55;          // EMV Field 55 (TLV data)
        public String responseCode;      // ISO 8583 response code (e.g., "00" = approved)
        public String status;            // Transaction status (e.g., "APPROVED", "DECLINED")
        
        @Override
        public String toString() {
            return "SettlementTransaction{" +
                    "rrn='" + rrn + '\'' +
                    ", amount='" + amount + '\'' +
                    ", currencyCode='" + currencyCode + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
    
    /**
     * Batch Upload Request
     */
    class BatchUploadRequest {
        public String terminalId;                // Terminal ID
        public String batchDate;                  // Batch date (YYYYMMDD)
        public String batchTime;                  // Batch time (HHMMSS)
        public List<SettlementTransaction> transactions;  // List of transactions to upload
        
        @Override
        public String toString() {
            return "BatchUploadRequest{" +
                    "terminalId='" + terminalId + '\'' +
                    ", batchDate='" + batchDate + '\'' +
                    ", transactionCount=" + (transactions != null ? transactions.size() : 0) +
                    '}';
        }
    }
    
    /**
     * Batch Upload Response
     */
    class BatchUploadResponse {
        public boolean success;                   // true if batch upload succeeded
        public String batchId;                   // Unique batch ID assigned by backend
        public int totalCount;                    // Total number of transactions in batch
        public int acceptedCount;                 // Number of accepted transactions
        public int rejectedCount;                 // Number of rejected transactions
        public List<String> acceptedRrns;         // List of accepted RRNs
        public List<String> rejectedRrns;        // List of rejected RRNs
        public String message;                    // Response message
        public Throwable error;                   // Error if upload failed
        
        public static BatchUploadResponse success(String batchId, int totalCount, 
                                                   int acceptedCount, int rejectedCount,
                                                   List<String> acceptedRrns, List<String> rejectedRrns) {
            BatchUploadResponse response = new BatchUploadResponse();
            response.success = true;
            response.batchId = batchId;
            response.totalCount = totalCount;
            response.acceptedCount = acceptedCount;
            response.rejectedCount = rejectedCount;
            response.acceptedRrns = acceptedRrns;
            response.rejectedRrns = rejectedRrns;
            response.message = "Batch upload completed successfully";
            return response;
        }
        
        public static BatchUploadResponse error(Throwable error, String message) {
            BatchUploadResponse response = new BatchUploadResponse();
            response.success = false;
            response.error = error;
            response.message = message;
            response.acceptedRrns = new java.util.ArrayList<>();
            response.rejectedRrns = new java.util.ArrayList<>();
            return response;
        }
        
        @Override
        public String toString() {
            return "BatchUploadResponse{" +
                    "success=" + success +
                    ", batchId='" + batchId + '\'' +
                    ", totalCount=" + totalCount +
                    ", acceptedCount=" + acceptedCount +
                    ", rejectedCount=" + rejectedCount +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    
    /**
     * Callback interface for batch upload requests
     */
    interface BatchUploadCallback {
        /**
         * Called when batch upload completes successfully
         * 
         * @param response Batch upload response with accepted/rejected transactions
         */
        void onBatchUploadComplete(BatchUploadResponse response);
        
        /**
         * Called when batch upload fails
         * 
         * @param error Error that occurred
         */
        void onBatchUploadError(Throwable error);
    }
    
    /**
     * Upload transaction batch for settlement
     * 
     * @param request Batch upload request with transactions
     * @param callback Callback to receive upload result
     */
    void uploadBatch(BatchUploadRequest request, BatchUploadCallback callback);
    
    /**
     * Check if service is available/configured
     * 
     * @return true if service is ready to process requests
     */
    boolean isAvailable();
}

