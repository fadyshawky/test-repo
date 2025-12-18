package com.neo.neopayplus.emv.utils

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil

/**
 * Helper class for consistent EMV transaction logging.
 * Provides structured logging methods for better debugging and code review.
 */
object EmvLoggingHelper {
    
    /**
     * Log a section header with consistent formatting
     */
    fun logSectionHeader(title: String) {
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "  $title")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
    }
    
    /**
     * Log a method entry with consistent formatting
     */
    fun logMethodEntry(methodName: String, cardType: String = "", vararg params: Pair<String, Any>) {
        val cardTypePrefix = if (cardType.isNotEmpty()) " ($cardType)" else ""
        LogUtil.e(Constant.TAG, "=== $methodName$cardTypePrefix ===")
        params.forEach { (key, value) ->
            LogUtil.e(Constant.TAG, "   $key: $value")
        }
    }
    
    /**
     * Log a success message with consistent formatting
     */
    fun logSuccess(message: String, details: Map<String, Any>? = null) {
        LogUtil.e(Constant.TAG, "✓ $message")
        details?.forEach { (key, value) ->
            LogUtil.e(Constant.TAG, "   $key: $value")
        }
    }
    
    /**
     * Log a warning message with consistent formatting
     */
    fun logWarning(message: String, details: Map<String, Any>? = null) {
        LogUtil.e(Constant.TAG, "⚠️ $message")
        details?.forEach { (key, value) ->
            LogUtil.e(Constant.TAG, "   $key: $value")
        }
    }
    
    /**
     * Log an error message with consistent formatting
     */
    fun logError(message: String, details: Map<String, Any>? = null) {
        LogUtil.e(Constant.TAG, "❌ $message")
        details?.forEach { (key, value) ->
            LogUtil.e(Constant.TAG, "   $key: $value")
        }
    }
    
    /**
     * Log a critical error with emphasis
     */
    fun logCriticalError(message: String, details: Map<String, Any>? = null) {
        LogUtil.e(Constant.TAG, "❌❌❌ $message ❌❌❌")
        details?.forEach { (key, value) ->
            LogUtil.e(Constant.TAG, "   $key: $value")
        }
    }
    
    /**
     * Log application information in a structured format
     */
    fun logApplicationInfo(
        aid: String,
        appType: String,
        rid: String,
        pix: String,
        levelInfo: String = ""
    ) {
        LogUtil.e(Constant.TAG, "✅ Selected Application:")
        LogUtil.e(Constant.TAG, "   Type: $appType$levelInfo")
        LogUtil.e(Constant.TAG, "   AID: $aid")
        LogUtil.e(Constant.TAG, "   RID: $rid")
        LogUtil.e(Constant.TAG, "   PIX: ${pix.ifEmpty { "none" }}")
    }
    
    /**
     * Extract and log AID details
     */
    fun parseAidDetails(aid: String?): AidDetails? {
        if (aid.isNullOrBlank()) return null
        
        val rid = if (aid.length >= 10) aid.substring(0, 10) else "unknown"
        val pix = if (aid.length > 10) aid.substring(10) else ""
        
        val appType = when {
            aid.startsWith("A000000003") -> "VISA"
            aid.startsWith("A000000004") -> "MASTERCARD"
            aid.startsWith("A000000005") -> "MASTERCARD (UK)"
            aid.startsWith("A000000732") -> "MEEZA"
            else -> "OTHER"
        }
        
        val levelInfo = if (aid.startsWith("A000000004") || aid.startsWith("A000000005")) {
            when (aid) {
                "A0000000041010", "A0000000050001" -> " (L1 - Standard Mastercard)"
                else -> " (L2 - Maestro/Other)"
            }
        } else ""
        
        return AidDetails(aid, appType, rid, pix, levelInfo)
    }
    
    /**
     * Data class for AID details
     */
    data class AidDetails(
        val aid: String,
        val appType: String,
        val rid: String,
        val pix: String,
        val levelInfo: String
    )
}

