package com.neo.neopayplus.utils;

import com.sunmi.pay.hardware.aidlv2.AidlConstantsV2;

/**
 * Entry Mode Utility
 * 
 * Calculates ISO8583 DE22 (POS Entry Mode) based on card type and PIN entry status
 * 
 * Common values:
 * - 051 = ICC (Chip), PIN entered
 * - 021 = ICC (Chip), no PIN
 * - 071 = CTLS (Contactless/NFC), PIN entered
 * - 072 = CTLS (Contactless/NFC), no PIN
 * - 801 = Magnetic stripe fallback, no PIN
 * - 802 = Magnetic stripe fallback, PIN entered
 */
public class EntryModeUtil {
    
    /**
     * Returns ISO DE22 (3 digits) as string per entry type
     * 
     * @param cardType Card type from EMV kernel (see AidlConstantsV2.CardType)
     * @param pinEntered Whether PIN was entered in this transaction
     * @param fallback Whether magnetic stripe fallback was used
     * @return 3-digit DE22 string (e.g., "051", "072")
     */
    public static String de22(int cardType, boolean pinEntered, boolean fallback) {
        int posEntry;
        
        if (fallback) {
            // Magnetic stripe fallback
            posEntry = pinEntered ? 802 : 801;
        } else if (cardType == AidlConstantsV2.CardType.NFC.getValue() || 
                   cardType == AidlConstantsV2.CardType.CONTACTLESS.getValue()) {
            // Contactless/NFC
            posEntry = pinEntered ? 71 : 72;
        } else {
            // ICC (Chip)
            posEntry = pinEntered ? 51 : 21;
        }
        
        return String.format("%03d", posEntry);
    }
    
    /**
     * Overload for backward compatibility
     */
    public static String de22(boolean isContactless, boolean pinEntered, boolean fallback) {
        int posEntry;
        
        if (fallback) {
            posEntry = pinEntered ? 802 : 801;
        } else if (isContactless) {
            posEntry = pinEntered ? 71 : 72;
        } else {
            posEntry = pinEntered ? 51 : 21;
        }
        
        return String.format("%03d", posEntry);
    }
}

