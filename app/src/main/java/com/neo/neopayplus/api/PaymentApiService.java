package com.neo.neopayplus.api;

/**
 * Payment API Service Interface
 * 
 * Defines the contract for backend payment authorization services.
 * This interface allows for easy swapping between mock and production implementations.
 */
public interface PaymentApiService {
    
    /**
     * Authorization Request Data
     */
    class AuthorizationRequest {
        public String field55;           // EMV Field 55 (TLV data)
        public String pan;               // Primary Account Number (masked)
        public String amount;            // Transaction amount in smallest currency unit
        public String currencyCode;      // Currency code (e.g., "818" for EGP)
        public String transactionType;   // Transaction type (e.g., "00" for purchase)
        public String date;              // Transaction date (YYMMDD)
        public String time;              // Transaction time (HHMMSS)
        public byte[] pinBlock;          // Encrypted PIN block (if online PIN)
        public String ksn;               // Key Serial Number (for DUKPT)
        
        @Override
        public String toString() {
            return "AuthorizationRequest{" +
                    "pan='" + (pan != null ? maskCardNumber(pan) : "null") + '\'' +
                    ", amount='" + amount + '\'' +
                    ", currencyCode='" + currencyCode + '\'' +
                    ", transactionType='" + transactionType + '\'' +
                    ", date='" + date + '\'' +
                    ", time='" + time + '\'' +
                    ", hasPinBlock=" + (pinBlock != null) +
                    ", field55Length=" + (field55 != null ? field55.length() : 0) +
                    '}';
        }
        
        private String maskCardNumber(String cardNumber) {
            if (cardNumber == null || cardNumber.length() < 6) {
                return "****";
            }
            return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
        }
    }
    
    /**
     * Authorization Response Data
     */
    class AuthorizationResponse {
        public boolean approved;         // true if transaction approved
        public String responseCode;      // ISO 8583 response code (e.g., "00" = approved)
        public String authCode;          // Authorization code
        public String rrn;               // Retrieval Reference Number
        public String[] responseTags;    // EMV response tags (for importOnlineProcStatus)
        public String[] responseValues;  // EMV response values (for importOnlineProcStatus)
        public String message;           // Response message/description
        public Throwable error;          // Error if request failed
        
        public static AuthorizationResponse success(String authCode, String rrn, String[] tags, String[] values) {
            AuthorizationResponse response = new AuthorizationResponse();
            response.approved = true;
            response.responseCode = "00";
            response.authCode = authCode;
            response.rrn = rrn;
            response.responseTags = tags;
            response.responseValues = values;
            response.message = "Transaction approved";
            return response;
        }
        
        public static AuthorizationResponse declined(String responseCode, String message) {
            AuthorizationResponse response = new AuthorizationResponse();
            response.approved = false;
            response.responseCode = responseCode;
            response.message = message;
            response.responseTags = new String[0];
            response.responseValues = new String[0];
            return response;
        }
        
        public static AuthorizationResponse error(Throwable error, String message) {
            AuthorizationResponse response = new AuthorizationResponse();
            response.approved = false;
            response.responseCode = "XX";
            response.error = error;
            response.message = message;
            response.responseTags = new String[0];
            response.responseValues = new String[0];
            return response;
        }
        
        @Override
        public String toString() {
            return "AuthorizationResponse{" +
                    "approved=" + approved +
                    ", responseCode='" + responseCode + '\'' +
                    ", authCode='" + authCode + '\'' +
                    ", rrn='" + rrn + '\'' +
                    ", message='" + message + '\'' +
                    ", error=" + (error != null ? error.getMessage() : "null") +
                    '}';
        }
    }
    
    /**
     * Callback interface for authorization requests
     */
    interface AuthorizationCallback {
        /**
         * Called when authorization completes
         * 
         * @param response Authorization response
         */
        void onAuthorizationComplete(AuthorizationResponse response);
        
        /**
         * Called when authorization fails (network error, etc.)
         * 
         * @param error Error that occurred
         */
        void onAuthorizationError(Throwable error);
    }
    
    /**
     * Request authorization for a transaction
     * 
     * @param request Authorization request data
     * @param callback Callback to receive response
     */
    void authorizeTransaction(AuthorizationRequest request, AuthorizationCallback callback);
    
    /**
     * Check if service is available/configured
     * 
     * @return true if service is ready to process requests
     */
    boolean isAvailable();
    
    /**
     * Key Rotation Request Data
     */
    class KeyRotationRequest {
        public String terminalId;    // Terminal identifier
        public String keyType;       // Key type (e.g., "DUKPT")
        
        public KeyRotationRequest(String terminalId, String keyType) {
            this.terminalId = terminalId;
            this.keyType = keyType != null ? keyType : "DUKPT";
        }
    }
    
    /**
     * Key Rotation Response Data
     */
    class KeyRotationResponse {
        public boolean success;
        public String terminalId;
        public String keyType;
        public int keyIndex;
        public String ipek;          // Initial PIN Encryption Key (hex)
        public String ksn;           // Key Serial Number (hex)
        public String effectiveDate; // ISO 8601 timestamp
        public String ciphertext;    // Base64-encoded encrypted key material
        public String message;
        public Throwable error;
        
        public static KeyRotationResponse success(String terminalId, String keyType, 
                int keyIndex, String ipek, String ksn, String effectiveDate, String ciphertext) {
            KeyRotationResponse response = new KeyRotationResponse();
            response.success = true;
            response.terminalId = terminalId;
            response.keyType = keyType;
            response.keyIndex = keyIndex;
            response.ipek = ipek;
            response.ksn = ksn;
            response.effectiveDate = effectiveDate;
            response.ciphertext = ciphertext;
            response.message = "Key rotation successful";
            return response;
        }
        
        public static KeyRotationResponse error(Throwable error, String message) {
            KeyRotationResponse response = new KeyRotationResponse();
            response.success = false;
            response.error = error;
            response.message = message;
            return response;
        }
    }
    
    /**
     * Callback interface for key rotation requests
     */
    interface KeyRotationCallback {
        /**
         * Called when key rotation completes successfully
         * 
         * @param response Key rotation response with new keys
         */
        void onKeyRotationComplete(KeyRotationResponse response);
        
        /**
         * Called when key rotation fails
         * 
         * @param error Error that occurred
         */
        void onKeyRotationError(Throwable error);
    }
    
    /**
     * Request key rotation from backend
     * 
     * @param request Key rotation request data
     * @param callback Callback to receive response
     */
    void rotateKeys(KeyRotationRequest request, KeyRotationCallback callback);
    
    /**
     * DUKPT Keys Response Data (for initial key fetch at startup)
     * Reuses same structure as KeyRotationResponse since data is identical
     */
    class DukptKeysResponse {
        public boolean success;
        public String terminalId;
        public String keyType;
        public int keyIndex;
        public String ipek;          // Initial PIN Encryption Key (hex)
        public String ksn;           // Key Serial Number (hex)
        public String effectiveDate; // ISO 8601 timestamp
        public String ciphertext;    // Base64-encoded encrypted key material (optional)
        public String message;
        public Throwable error;
        
        public static DukptKeysResponse success(String terminalId, String keyType, 
                int keyIndex, String ipek, String ksn, String effectiveDate, String ciphertext) {
            DukptKeysResponse response = new DukptKeysResponse();
            response.success = true;
            response.terminalId = terminalId;
            response.keyType = keyType;
            response.keyIndex = keyIndex;
            response.ipek = ipek;
            response.ksn = ksn;
            response.effectiveDate = effectiveDate;
            response.ciphertext = ciphertext;
            response.message = "DUKPT keys fetched successfully";
            return response;
        }
        
        public static DukptKeysResponse error(Throwable error, String message) {
            DukptKeysResponse response = new DukptKeysResponse();
            response.success = false;
            response.error = error;
            response.message = message;
            return response;
        }
    }
    
    /**
     * Callback interface for DUKPT keys fetch requests
     */
    interface DukptKeysCallback {
        /**
         * Called when DUKPT keys fetch completes successfully
         * 
         * @param response DUKPT keys response with IPEK and KSN
         */
        void onDukptKeysComplete(DukptKeysResponse response);
        
        /**
         * Called when DUKPT keys fetch fails
         * 
         * @param error Error that occurred
         */
        void onDukptKeysError(Throwable error);
    }
    
    /**
     * Fetch initial DUKPT keys from backend at terminal startup
     * This is called once at boot to initialize DUKPT keys (IPEK + KSN)
     * 
     * @param terminalId Terminal identifier
     * @param callback Callback to receive response
     */
    void getDukptKeys(String terminalId, DukptKeysCallback callback);
    
    /**
     * Reversal Request Data
     */
    class ReversalRequest {
        public String terminalId;
        public String merchantId;
        public String rrn;               // Retrieval Reference Number to reverse
        public String amount;            // Original transaction amount
        public String currencyCode;      // Currency code (e.g., "818" for EGP)
        public String reversalReason;    // Reason for reversal (e.g., "HOST_TIMEOUT", "USER_REQUEST")
        
        @Override
        public String toString() {
            return "ReversalRequest{" +
                    "rrn='" + rrn + '\'' +
                    ", amount='" + amount + '\'' +
                    ", terminalId='" + terminalId + '\'' +
                    ", reason='" + reversalReason + '\'' +
                    '}';
        }
    }
    
    /**
     * Reversal Response Data
     */
    class ReversalResponse {
        public boolean approved;         // true if reversal approved
        public String responseCode;      // ISO 8583 response code (e.g., "00" = approved)
        public String responseMessage;   // Response message/description
        public Throwable error;          // Error if request failed
        
        public static ReversalResponse success(String responseCode, String message) {
            ReversalResponse response = new ReversalResponse();
            response.approved = true;
            response.responseCode = responseCode;
            response.responseMessage = message;
            return response;
        }
        
        public static ReversalResponse declined(String responseCode, String message) {
            ReversalResponse response = new ReversalResponse();
            response.approved = false;
            response.responseCode = responseCode;
            response.responseMessage = message;
            return response;
        }
        
        public static ReversalResponse error(Throwable error, String message) {
            ReversalResponse response = new ReversalResponse();
            response.approved = false;
            response.error = error;
            response.responseMessage = message;
            return response;
        }
    }
    
    /**
     * Callback interface for reversal requests
     */
    interface ReversalCallback {
        /**
         * Called when reversal completes successfully
         * 
         * @param response Reversal response with approval/decline status
         */
        void onReversalComplete(ReversalResponse response);
        
        /**
         * Called when reversal fails (network error, timeout, etc.)
         * 
         * @param error Error that occurred
         */
        void onReversalError(Throwable error);
    }
    
    /**
     * Reverse a transaction
     * 
     * @param request Reversal request with RRN and transaction details
     * @param callback Callback to receive response
     */
    void reverseTransaction(ReversalRequest request, ReversalCallback callback);
    
    /**
     * Key Announce Request Data
     */
    class KeyAnnounceRequest {
        public String terminalId;
        public String kbPosB64;        // TR-31 key block under Terminal TMK (Base64)
        public String kcv;            // Key Check Value (hex)
        public int pinKeySetHint;      // Optional logical set hint
        public String prevPinKeyId;    // Optional previous key ID
    }
    
    /**
     * Key Announce Response Data
     */
    class KeyAnnounceResponse {
        public boolean success;
        public String pinKeyId;        // Server-issued PIN key ID
        public int pinKeySet;          // Key set ID
        public int pinKeyVer;          // Key version
        public String message;
        public Throwable error;
        
        public static KeyAnnounceResponse success(String pinKeyId, int setId, int verId) {
            KeyAnnounceResponse response = new KeyAnnounceResponse();
            response.success = true;
            response.pinKeyId = pinKeyId;
            response.pinKeySet = setId;
            response.pinKeyVer = verId;
            response.message = "Key announced successfully";
            return response;
        }
        
        public static KeyAnnounceResponse error(Throwable error, String message) {
            KeyAnnounceResponse response = new KeyAnnounceResponse();
            response.success = false;
            response.error = error;
            response.message = message;
            return response;
        }
    }
    
    /**
     * Callback interface for key announcement requests
     */
    interface KeyAnnounceCallback {
        /**
         * Called when key announcement completes successfully
         * 
         * @param response Key announcement response with pin_key_id
         */
        void onKeyAnnounceComplete(KeyAnnounceResponse response);
        
        /**
         * Called when key announcement fails
         * 
         * @param error Error that occurred
         */
        void onKeyAnnounceError(Throwable error);
    }
    
    /**
     * Announce TPK (Transaction PIN Key) to backend
     * Backend will rewrap under Bank TMK and return pin_key_id
     * 
     * @param request Key announcement request with TR-31 key block
     * @param callback Callback to receive response
     */
    void announceKey(KeyAnnounceRequest request, KeyAnnounceCallback callback);
}

