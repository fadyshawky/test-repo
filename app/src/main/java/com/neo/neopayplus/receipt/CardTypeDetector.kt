package com.neo.neopayplus.receipt

/**
 * Utility to detect card type (DEBIT or CREDIT) from PAN BIN
 * 
 * Note: This is a simplified implementation. For production, consider using
 * a comprehensive BIN database or API service for accurate detection.
 */
object CardTypeDetector {
    
    /**
     * Detect card type (DEBIT or CREDIT) from PAN
     * 
     * @param pan Primary Account Number (full or partial)
     * @param brand Card brand (VISA, MASTERCARD, MEEZA) - optional, helps with detection
     * @return "DEBIT" or "CREDIT", defaults to "DEBIT" if cannot be determined
     */
    fun detectCardType(pan: String?, brand: String? = null): String {
        if (pan == null || pan.isEmpty() || pan.length < 6) {
            // Default to DEBIT (most common in Egypt)
            return "DEBIT"
        }
        
        // Extract BIN (first 6 digits)
        val bin = pan.take(6)
        
        // For now, default to DEBIT as most cards in Egypt are DEBIT
        // This can be enhanced with a BIN database lookup in the future
        // Common patterns:
        // - Most Egyptian bank cards are DEBIT
        // - Some specific BIN ranges indicate CREDIT (would need BIN database)
        
        // TODO: Implement BIN database lookup for accurate detection
        // For now, return DEBIT as default (most common)
        return "DEBIT"
    }
    
    /**
     * Format card brand with type for display
     * 
     * @param brand Card brand (VISA, MASTERCARD, MEEZA)
     * @param cardType Card type (DEBIT or CREDIT)
     * @return Formatted string like "VISA DEBIT", "MASTERCARD CREDIT", etc.
     */
    fun formatBrandWithType(brand: String?, cardType: String?): String {
        val brandUpper = brand?.uppercase() ?: ""
        val typeUpper = cardType?.uppercase() ?: "DEBIT"
        
        return when {
            brandUpper.isEmpty() -> ""
            typeUpper.isEmpty() -> brandUpper
            else -> "$brandUpper $typeUpper"
        }
    }
}



