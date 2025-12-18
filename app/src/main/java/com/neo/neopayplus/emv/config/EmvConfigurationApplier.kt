package com.neo.neopayplus.emv.config

import android.os.Bundle
import android.util.Log
import com.neo.neopayplus.Constant
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2

/**
 * Applies EMV configuration (TLVs) for payment schemes.
 * 
 * Follows Single Responsibility Principle - only handles TLV configuration.
 * Uses Strategy pattern for extensibility (Open/Closed Principle).
 * 
 * @param emvOptV2 EMV service instance
 */
class EmvConfigurationApplier(private val emvOptV2: EMVOptV2) {
    
    companion object {
        private const val TAG = Constant.TAG
        
        // CVM limits - set very high to prevent kernel from requiring CVM
        private const val HIGH_CVM_LIMIT = "999999999999" // Very high - kernel won't require CVM
        private const val MAX_TRANS_LIMIT = "999999999999" // Allow any amount
        private const val DF8119_NO_CVM_ONLY = "02" // No CVM only - prevents card PIN request
    }
    
    /**
     * Apply configuration for all payment schemes
     * 
     * @param amount Transaction amount in minor units
     * @param strategies List of payment scheme strategies to apply
     * @return true if all configurations applied successfully
     */
    fun applyConfiguration(amount: Long, strategies: List<PaymentSchemeStrategy>): Boolean {
        return try {
            Log.e(TAG, "=== Applying EMV Configuration ===")
            Log.e(TAG, "Amount: $amount minor units (${amount / 100.0} EGP)")
            Log.e(TAG, "DF8119: $DF8119_NO_CVM_ONLY (No CVM only - prevents card PIN request)")
            Log.e(TAG, "DF8124: $HIGH_CVM_LIMIT (very high - kernel never requires CVM)")
            
            strategies.forEach { strategy ->
                applySchemeConfiguration(amount, strategy)
            }
            
            applyTerminalParameters()
            
            Log.e(TAG, "✓ EMV Configuration applied successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to apply EMV configuration: ${e.message}")
            false
        }
    }
    
    /**
     * Apply configuration for a single payment scheme
     */
    private fun applySchemeConfiguration(amount: Long, strategy: PaymentSchemeStrategy) {
        try {
            val tags = strategy.getTlvTags()
            val values = strategy.getTlvValues(
                amount = amount,
                df8119Value = DF8119_NO_CVM_ONLY,
                highCvmLimit = HIGH_CVM_LIMIT,
                maxTransLimit = MAX_TRANS_LIMIT
            )
            
            emvOptV2.setTlvList(strategy.getTlvOpCode(), tags, values)
            Log.e(TAG, "✓ ${strategy.getSchemeName()} TLVs applied")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to apply ${strategy.getSchemeName()} configuration: ${e.message}")
            throw e
        }
    }
    
    /**
     * Apply terminal parameters
     */
    private fun applyTerminalParameters() {
        try {
            val termParams = Bundle().apply {
                putBoolean("optOnlineRes", true)
            }
            emvOptV2.setTermParamEx(termParams)
            Log.e(TAG, "✓ Terminal parameters applied")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to apply terminal parameters: ${e.message}")
            throw e
        }
    }
    
    /**
     * Get default strategies for all supported schemes
     */
    fun getDefaultStrategies(): List<PaymentSchemeStrategy> {
        return listOf(
            PayPassStrategy(),
            PayWaveStrategy()
        )
    }
}
