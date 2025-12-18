package com.neo.neopayplus.api;

import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * EMV Configuration API Service Interface
 * 
 * Defines the contract for backend EMV configuration services.
 * This allows loading AIDs, CAPKs, and terminal parameters from backend.
 */
public interface EmvConfigApiService {
    
    /**
     * AID Configuration Data
     */
    class AidConfig {
        public String aidHex;          // AID in hex string format
        public String label;           // AID label (e.g., "VISA CREDIT", "MASTERCARD PAYPASS") - optional
        public String kernel;          // Kernel type: "EMV", "PAYPASS", "PAYWAVE", etc.
        public String tacDefault;      // TAC Default in hex
        public String tacDenial;       // TAC Denial in hex
        public String tacOnline;       // TAC Online in hex
        public String threshold;       // Threshold in hex
        public String floorLimit;      // Floor Limit in hex (12-digit BCD hex)
        public int selFlag;            // Selection flag: 0=contact, 1=contactless (CRITICAL for NFC)
        public int priority;           // AID priority (1=highest)
        public String version;         // AID version (e.g., "008C", required by PayLib v2.0.32)
        public int targetPer;          // Target percentage
        public int maxTargetPer;       // Max target percentage
        
        // Contactless-specific parameters
        public String ttq;             // Terminal Transaction Qualifiers (9F66) - 4 bytes hex
        public String ctq;             // Card Transaction Qualifiers (9F6C) - 2-4 bytes hex
        public String noCvmLimit;      // No-CVM limit (12-digit BCD hex, e.g., "000000060000" = 600.00)
        public String cvmLimit;        // CVM required limit (12-digit BCD hex)
        public String contactlessFloorLimit; // Contactless floor limit (12-digit BCD hex)
        
        // DOL fields
        public String ddol;            // Default DDOL (hex)
        public String tdol;            // Terminal DOL (hex)
        public String udol;            // Unpredictable Number DOL (hex)
        
        @Override
        public String toString() {
            return "AidConfig{aidHex='" + (aidHex != null && aidHex.length() > 10 ? 
                aidHex.substring(0, 10) + "..." : aidHex) + 
                "', selFlag=" + selFlag + ", kernel='" + kernel + "'}";
        }
    }
    
    /**
     * CAPK Configuration Data
     */
    class CapkConfig {
        public String ridHex;          // RID (Registry Identifier) in hex
        public String indexHex;        // CAPK index in hex
        public String modulusHex;      // Public key modulus in hex
        public String exponentHex;     // Public key exponent in hex
        public String hashIndHex;      // Hash indicator (SHA-1, SHA-256, etc.)
        public String arithIndHex;     // Algorithm indicator (RSA, ECC, etc.)
        public String expiryDate;      // Expiry date (YYMMDD)
        
        @Override
        public String toString() {
            return "CapkConfig{ridHex='" + (ridHex != null && ridHex.length() > 10 ? 
                ridHex.substring(0, 10) + "..." : ridHex) + 
                "', indexHex='" + indexHex + "'}";
        }
    }
    
    /**
     * EMV Configuration Response
     */
    class EmvConfigResponse {
        public List<AidConfig> aids = new ArrayList<>();
        public List<CapkConfig> capks = new ArrayList<>();
        public String terminalCountryCode;
        public String currencyCode;
        public String terminalType;
        public String terminalCapabilities;
        public String additionalTerminalCapabilities;
        public String transactionCategoryCode;
        public Throwable error;
        public boolean success;
        
        public static EmvConfigResponse error(Throwable error) {
            EmvConfigResponse response = new EmvConfigResponse();
            response.success = false;
            response.error = error;
            return response;
        }
        
        public static EmvConfigResponse success() {
            EmvConfigResponse response = new EmvConfigResponse();
            response.success = true;
            return response;
        }
        
        @Override
        public String toString() {
            return "EmvConfigResponse{" +
                    "success=" + success +
                    ", aidsCount=" + aids.size() +
                    ", capksCount=" + capks.size() +
                    ", error=" + (error != null ? error.getMessage() : "null") +
                    '}';
        }
    }
    
    /**
     * Callback interface for configuration requests
     */
    interface EmvConfigCallback {
        /**
         * Called when configuration is loaded successfully
         * 
         * @param response Configuration response with AIDs, CAPKs, and terminal parameters
         */
        void onConfigLoaded(EmvConfigResponse response);
        
        /**
         * Called when configuration loading fails
         * 
         * @param error Error that occurred
         */
        void onConfigError(Throwable error);
    }
    
    /**
     * Load EMV configuration from backend
     * This includes AIDs, CAPKs, and terminal parameters
     * 
     * @param callback Callback to receive configuration data
     */
    void loadEmvConfiguration(EmvConfigCallback callback);
    
    /**
     * Check if service is available/configured
     * 
     * @return true if service is ready to process requests
     */
    boolean isAvailable();
}

