package com.neo.neopayplus.emv.utils

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil

/**
 * Helper class for validating offline PIN failures from TVR.
 * Extracted from transaction handlers for reusability and clarity.
 */
object EmvOfflinePinValidator {
    
    /**
     * Result of offline PIN validation
     */
    data class ValidationResult(
        val failed: Boolean,
        val reason: String,
        val offlinePinTriesExceeded: Boolean,
        val pinTryLimitExceeded: Boolean,
        val pinNotEntered: Boolean
    )
    
    /**
     * Check if offline PIN was required and failed based on TVR and CVM result.
     * 
     * @param tvr TVR (Terminal Verification Results) tag 95
     * @param cvmResult CVM Result tag 9F34
     * @param currentPinType Current PIN type (0=online, 1=offline)
     * @return ValidationResult indicating if offline PIN failed
     */
    fun validateOfflinePin(
        tvr: String?,
        cvmResult: String?,
        currentPinType: Int
    ): ValidationResult {
        // Default: no failure
        if (tvr == null || tvr.length < 10) {
            return ValidationResult(false, "", false, false, false)
        }
        
        // Parse TVR bytes
        val byte2 = tvr.substring(2, 4).toIntOrNull(16) ?: 0
        val byte3 = tvr.substring(4, 6).toIntOrNull(16) ?: 0
        
        val offlinePinTriesExceeded = (byte2 and 0x02) != 0
        val pinTryLimitExceeded = (byte3 and 0x80) != 0
        val pinNotEntered = (byte3 and 0x10) != 0
        
        // Check if offline PIN was required
        val offlinePinRequired = cvmResult != null && cvmResult.length >= 2 && 
                                (cvmResult.startsWith("42") || cvmResult.startsWith("03") || 
                                 cvmResult.startsWith("04") || cvmResult.startsWith("05"))
        
        if (!offlinePinRequired) {
            return ValidationResult(false, "", offlinePinTriesExceeded, pinTryLimitExceeded, pinNotEntered)
        }
        
        // Log offline PIN status
        LogUtil.e(Constant.TAG, "🔍 Offline PIN was required for this transaction")
        LogUtil.e(Constant.TAG, "   CVM Result (9F34): $cvmResult")
        LogUtil.e(Constant.TAG, "   Offline PIN Tries Exceeded: $offlinePinTriesExceeded")
        LogUtil.e(Constant.TAG, "   PIN Try Limit Exceeded: $pinTryLimitExceeded")
        LogUtil.e(Constant.TAG, "   PIN Not Entered: $pinNotEntered")
        
        // Check for failures
        if (offlinePinTriesExceeded || pinTryLimitExceeded) {
            LogUtil.e(Constant.TAG, "❌❌❌ OFFLINE PIN FAILED - Transaction must be DECLINED ❌❌❌")
            LogUtil.e(Constant.TAG, "   According to EMV spec: If offline PIN fails, transaction must be declined")
            return ValidationResult(
                failed = true,
                reason = "Offline PIN verification failed - PIN tries exceeded",
                offlinePinTriesExceeded = true,
                pinTryLimitExceeded = pinTryLimitExceeded,
                pinNotEntered = pinNotEntered
            )
        }
        
        if (pinNotEntered && currentPinType == 1) {
            LogUtil.e(Constant.TAG, "❌❌❌ OFFLINE PIN REQUIRED BUT NOT ENTERED - Transaction must be DECLINED ❌❌❌")
            return ValidationResult(
                failed = true,
                reason = "Offline PIN required but not entered",
                offlinePinTriesExceeded = false,
                pinTryLimitExceeded = false,
                pinNotEntered = true
            )
        }
        
        return ValidationResult(false, "", offlinePinTriesExceeded, pinTryLimitExceeded, pinNotEntered)
    }
}

