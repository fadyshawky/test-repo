package com.neo.neopayplus.config;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

/**
 * Payment Configuration Manager
 * 
 * Centralized configuration for payment-related parameters.
 * These values should be set based on your deployment region and acquirer requirements.
 * 
 * SECURITY NOTE: Never hardcode sensitive values like keys or certificates here.
 * Sensitive data should be loaded securely from acquirer/payment processor.
 */
public class PaymentConfig {
    
    private static final String TAG = Constant.TAG;
    
    // ==================== TERMINAL CONFIGURATION ====================
    
    /**
     * Merchant Name
     * Used in setTermParamEx() for terminal configuration
     * TODO: Load from backend/acquirer configuration
     */
    public static final String MERCHANT_NAME = "NeoPayPlus";
    
    /**
     * Terminal ID
     * Format: Up to 8 characters (typically numeric)
     * Used in setTermParamEx() for terminal configuration
     * 
     * DYNAMIC: Loaded from backend on boot via MyApplication.terminalConfig
     * Falls back to this default if backend unavailable
     */
    public static final String TERMINAL_ID = "00000001";
    
    /**
     * Get current terminal ID (from dynamic config if available, else default)
     */
    public static String getTerminalId() {
        if (MyApplication.terminalConfig != null && MyApplication.terminalConfig.terminalId != null) {
            return MyApplication.terminalConfig.terminalId;
        }
        return TERMINAL_ID;
    }
    
    /**
     * Merchant ID
     * Format: Up to 15 characters (typically numeric)
     * Used in setTermParamEx() for terminal configuration
     * 
     * DYNAMIC: Loaded from backend on boot via MyApplication.terminalConfig
     * Falls back to this default if backend unavailable
     */
    public static final String MERCHANT_ID = "00000001";
    
    /**
     * Get current merchant ID (from dynamic config if available, else default)
     */
    public static String getMerchantId() {
        if (MyApplication.terminalConfig != null && MyApplication.terminalConfig.merchantId != null) {
            return MyApplication.terminalConfig.merchantId;
        }
        return MERCHANT_ID;
    }
    
    /**
     * Terminal Country Code (ISO 3166-1 numeric)
     * Format: 4-digit string (e.g., "0818" for Egypt, "0156" for China)
     * This is the EMV tag 9F1A value.
     */
    public static final String TERMINAL_COUNTRY_CODE = "0818"; // Egypt
    
    /**
     * Transaction Currency Code (ISO 4217 numeric)
     * Format: 3-digit string (e.g., "818" for EGP, "156" for CNY)
     * This is the EMV tag 5F2A value (without leading zero for bundle).
     * 
     * DYNAMIC: Loaded from backend on boot via MyApplication.terminalConfig
     * Falls back to this default if backend unavailable
     */
    public static final String CURRENCY_CODE = "818"; // EGP (Egyptian Pound)
    
    /**
     * Get current currency code (from dynamic config if available, else default)
     */
    public static String getCurrencyCode() {
        if (MyApplication.terminalConfig != null && MyApplication.terminalConfig.currencyCode != null) {
            return MyApplication.terminalConfig.currencyCode;
        }
        return CURRENCY_CODE;
    }
    
    /**
     * Currency Code for EMV TLV (with leading zero if needed)
     * Format: 4-digit string (e.g., "0818" for EGP, "0156" for CNY)
     */
    public static final String CURRENCY_CODE_TLV = "0818"; // EGP
    
    /**
     * Currency Code Exponent
     * Format: 2-digit string (e.g., "00" for currencies with 2 decimal places, "02" for 0 decimal places)
     * This is the EMV tag 5F36 value.
     */
    public static final String CURRENCY_EXPONENT = "00"; // EGP has 2 decimal places
    
    /**
     * Currency Display Name
     * Used for UI display purposes
     */
    public static final String CURRENCY_NAME = "EGP";
    
    /**
     * Terminal Type
     * Format: 2-digit hex string (e.g., "22" = attended POS with contactless)
     * This is the EMV tag 9F35 value.
     * 
     * Common values:
     * - 22: Attended POS with contactless
     * - 21: Attended POS without contactless
     * - 42: Unattended POS with contactless
     */
    public static final String TERMINAL_TYPE = "22"; // Attended POS with contactless
    
    // ==================== TERMINAL CAPABILITIES ====================
    
    /**
     * Terminal Capabilities (9F33)
     * Format: 6-digit hex string
     * Byte 1: Card data input, CVM, security
     * Byte 2: Cardholder verification methods
     * Byte 3: Cardholder verification methods (continued)
     * 
     * Current: "E0F8C8" - supports Online PIN, Offline PIN, CDCVM, contactless
     * 
     * Byte 2 breakdown (F8 = 11111000):
     * - Bit 3 = 1: Online PIN supported
     * - Bit 4 = 1: Signature supported
     * - Bit 5 = 1: No CVM supported
     * - Bit 6 = 1: Offline PIN supported
     * 
     * The terminal should support both online and offline PIN capabilities.
     * The EMV kernel will select the appropriate CVM based on the card's CVM List
     * and transaction conditions. We don't force a preference - we let the card's
     * CVM List and conditions determine which CVM is used.
     */
    public static final String TERMINAL_CAPABILITIES = "E0F8C8";
    
    /**
     * Additional Terminal Capabilities (9F40)
     * Format: 10-digit hex string (5 bytes)
     * 
     * This tag controls CVM selection preferences and priorities.
     * Byte 5-6 (last 2 bytes) control CVM selection order/preference.
     * 
     * Current: "F000F0F001" - supports both Online PIN and Offline PIN
     * 
     * Byte breakdown:
     * - Byte 1: F0 = Card data input, security capabilities
     * - Byte 2: 00 = Reserved
     * - Byte 3: F0 = CVM capabilities (online PIN, signature, offline PIN)
     * - Byte 4: F0 = CVM capabilities continued
     * - Byte 5: F0 = CVM preferences - supports both online and offline PIN
     * - Byte 6: 01 = Additional preferences
     * 
     * The terminal reports its capabilities (supports both PIN types).
     * The EMV kernel selects CVM based on card's CVM List order and conditions,
     * matching terminal capabilities with card requirements.
     */
    public static final String ADDITIONAL_TERMINAL_CAPABILITIES = "F000F0F001";
    
    /**
     * Transaction Category Code (9F53) for contactless
     * Format: 6-digit hex string
     * 
     * Default: "708000" - Contactless transaction category code
     */
    public static final String TRANSACTION_CATEGORY_CODE = "708000";
    
    // ==================== SCHEME-SPECIFIC TLV DATA ====================
    
    /**
     * PayPass (Mastercard) configuration values
     */
    public static class PayPassConfig {
        public static final String DF8117 = "E0";
        public static final String DF8118 = "F8";
        public static final String DF8119 = "F8";
        public static final String DF811F = "E8";
        public static final String DF811E = "00";
        public static final String DF812C = "00";
        public static final String DF8123 = "000000000000";
        public static final String DF8124 = "000000100000";
        public static final String DF8125 = "999999999999";
        public static final String DF8126 = "000000100000";
        public static final String DF811B = "30";
        public static final String DF811D = "02";
        public static final String DF8122 = "0000000000";
        public static final String DF8120 = "000000000000";
        public static final String DF8121 = "000000000000";
    }
    
    /**
     * PayWave (Visa) configuration values
     * (Same as PayPass for standard configuration)
     */
    public static class PayWaveConfig {
        public static final String DF8117 = "E0";
        public static final String DF8118 = "F8";
        public static final String DF8119 = "F8";
        public static final String DF811F = "E8";
        public static final String DF811E = "00";
        public static final String DF812C = "00";
        public static final String DF8123 = "000000000000";
        public static final String DF8124 = "000000100000";
        public static final String DF8125 = "999999999999";
        public static final String DF8126 = "000000100000";
        public static final String DF811B = "30";
        public static final String DF811D = "02";
        public static final String DF8122 = "0000000000";
        public static final String DF8120 = "000000000000";
        public static final String DF8121 = "000000000000";
    }
    
    /**
     * AMEX (American Express) configuration values
     */
    public static class AmexConfig {
        public static final String TAG_9F6D = "C0";
        public static final String TAG_9F6E = "D8E00000";
        public static final String TAG_9F33 = "E0E888";
        public static final String TAG_9F35 = "22";
        public static final String DF8168 = "00";
        public static final String DF8167 = "00";
        public static final String DF8169 = "00";
        public static final String DF8170 = "60";
    }
    
    /**
     * JCB configuration values
     */
    public static class JCBConfig {
        public static final String TAG_9F53 = "708000";
        public static final String DF8161 = "7F00";
    }
    
    // ==================== AID CONFIGURATION ====================
    
    /**
     * TAC (Terminal Action Code) values for AID configuration
     */
    public static class TACConfig {
        /**
         * TAC Default - Prefer online authorization
         * Format: 4-byte hex string
         */
        public static final String TAC_DEFAULT_ONLINE_PREFERRED = "DC4000A800";
        
        /**
         * TAC Denial - Deny offline when possible
         * Format: 4-byte hex string
         */
        public static final String TAC_DENIAL = "0010000000";
        
        /**
         * TAC Online - Online PIN required
         * Format: 4-byte hex string
         */
        public static final String TAC_ONLINE_PIN_REQUIRED = "DC4004F800";
        
        /**
         * Threshold - No threshold (force online decision)
         * Format: 6-byte hex string
         */
        public static final String THRESHOLD_ZERO = "000000000000";
        
        /**
         * Floor Limit - Force online for all amounts
         * Format: 6-byte hex string
         */
        public static final String FLOOR_LIMIT_ZERO = "000000000000";
    }
    
    // ==================== KEY MODEL ====================
    
    /**
     * Key Management Model
     * Controls whether POS uses DUKPT or Master/Session (TMK→TPK/TAK) key model
     */
    public enum KeyModel {
        DUKPT,
        MASTER_SESSION   // TMK → TPK/TAK session keys
    }
    
    /**
     * Active Key Model (default: MASTER_SESSION for production)
     */
    private static KeyModel KEY_MODEL = KeyModel.MASTER_SESSION;
    
    /**
     * Get current key model
     */
    public static KeyModel getKeyModel() {
        return KEY_MODEL;
    }
    
    /**
     * Check if using Master/Session key model
     */
    public static boolean isMasterSession() {
        return KEY_MODEL == KeyModel.MASTER_SESSION;
    }
    
    // ==================== VALIDATION ====================
    
    /**
     * Validate configuration values
     * 
     * @return true if configuration is valid, false otherwise
     */
    public static boolean validate() {
        boolean valid = true;
        
        if (TERMINAL_COUNTRY_CODE == null || TERMINAL_COUNTRY_CODE.length() != 4) {
            LogUtil.e(TAG, "⚠️ Invalid TERMINAL_COUNTRY_CODE: " + TERMINAL_COUNTRY_CODE);
            valid = false;
        }
        
        if (CURRENCY_CODE == null || CURRENCY_CODE.length() != 3) {
            LogUtil.e(TAG, "⚠️ Invalid CURRENCY_CODE: " + CURRENCY_CODE);
            valid = false;
        }
        
        if (CURRENCY_CODE_TLV == null || CURRENCY_CODE_TLV.length() != 4) {
            LogUtil.e(TAG, "⚠️ Invalid CURRENCY_CODE_TLV: " + CURRENCY_CODE_TLV);
            valid = false;
        }
        
        if (TERMINAL_TYPE == null || TERMINAL_TYPE.length() != 2) {
            LogUtil.e(TAG, "⚠️ Invalid TERMINAL_TYPE: " + TERMINAL_TYPE);
            valid = false;
        }
        
        if (!valid) {
            LogUtil.e(TAG, "⚠️ Configuration validation failed - please check PaymentConfig values");
        } else {
            LogUtil.e(TAG, "✓ Configuration validated successfully");
            LogUtil.e(TAG, "  Country: " + TERMINAL_COUNTRY_CODE + ", Currency: " + CURRENCY_CODE + " (" + CURRENCY_NAME + ")");
        }
        
        return valid;
    }
    
    /**
     * Get formatted currency display string
     * 
     * @param amount Amount to format (in smallest currency unit, e.g., piasters for EGP)
     * @return Formatted string (e.g., "100 EGP")
     */
    public static String formatCurrency(long amount) {
        // Convert from smallest unit (piasters) to main unit (pounds) for display
        double mainUnit = amount / 100.0;
        return String.format("%.2f %s", mainUnit, CURRENCY_NAME);
    }
}

