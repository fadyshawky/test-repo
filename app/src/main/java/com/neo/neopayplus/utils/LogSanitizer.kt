package com.neo.neopayplus.utils

/**
 * Utility for sanitizing sensitive data in logs.
 * 
 * Security: Prevents sensitive information from appearing in logs.
 */
object LogSanitizer {
    
    /**
     * Mask PAN (Primary Account Number) for logging
     * Shows first 6 and last 4 digits only
     * 
     * Example: "1234567890123456" -> "123456****3456"
     */
    fun maskPan(pan: String?): String {
        if (pan.isNullOrBlank()) return "****"
        if (pan.length < 10) return "****"
        return "${pan.substring(0, 6)}****${pan.substring(pan.length - 4)}"
    }
    
    /**
     * Mask card number (handles various formats)
     */
    fun maskCardNumber(cardNumber: String?): String {
        if (cardNumber.isNullOrBlank()) return "****"
        
        // Remove spaces and dashes
        val cleaned = cardNumber.replace(Regex("[\\s-]"), "")
        
        if (cleaned.length < 10) return "****"
        
        // Show first 6 and last 4
        return "${cleaned.substring(0, 6)}****${cleaned.substring(cleaned.length - 4)}"
    }
    
    /**
     * Mask PIN block (shows only length)
     */
    fun maskPinBlock(pinBlock: ByteArray?): String {
        if (pinBlock == null) return "null"
        return "[PIN_BLOCK:${pinBlock.size} bytes]"
    }
    
    /**
     * Mask key material (shows only length)
     */
    fun maskKey(key: ByteArray?): String {
        if (key == null) return "null"
        return "[KEY:${key.size} bytes]"
    }
    
    /**
     * Mask KSN (Key Serial Number) - shows only last 4 bytes
     */
    fun maskKsn(ksn: String?): String {
        if (ksn.isNullOrBlank()) return "****"
        if (ksn.length <= 8) return "****"
        return "****${ksn.substring(ksn.length - 8)}"
    }
    
    /**
     * Sanitize log message by masking sensitive patterns
     */
    fun sanitizeLogMessage(message: String): String {
        var sanitized = message
        
        // Mask PAN patterns (13-19 digits)
        sanitized = sanitized.replace(Regex("\\b\\d{13,19}\\b")) { matchResult ->
            maskPan(matchResult.value)
        }
        
        // Mask card numbers with spaces/dashes
        sanitized = sanitized.replace(Regex("\\b\\d{4}[\\s-]\\d{4}[\\s-]\\d{4}[\\s-]\\d{4}\\b")) { matchResult ->
            maskCardNumber(matchResult.value)
        }
        
        return sanitized
    }
}
