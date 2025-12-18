package com.neo.neopayplus.emv.config

/**
 * Strategy interface for payment scheme configuration.
 * 
 * Follows Open/Closed Principle - new payment schemes can be added
 * without modifying existing code.
 * 
 * @see PayPassStrategy
 * @see PayWaveStrategy
 */
interface PaymentSchemeStrategy {
    /**
     * Get TLV tags for this payment scheme
     */
    fun getTlvTags(): Array<String>
    
    /**
     * Get TLV values for this payment scheme based on amount
     * 
     * @param amount Amount in minor units
     * @param df8119Value CVM capability value (e.g., "02" for No CVM only)
     * @param highCvmLimit High CVM limit to prevent kernel from requiring CVM
     * @param maxTransLimit Maximum transaction limit
     * @return Array of TLV values in same order as tags
     */
    fun getTlvValues(
        amount: Long,
        df8119Value: String,
        highCvmLimit: String,
        maxTransLimit: String
    ): Array<String>
    
    /**
     * Get the TLV operation code for this scheme
     */
    fun getTlvOpCode(): Int
    
    /**
     * Get scheme name for logging
     */
    fun getSchemeName(): String
}
