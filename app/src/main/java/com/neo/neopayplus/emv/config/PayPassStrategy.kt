package com.neo.neopayplus.emv.config

import com.neo.neopayplus.config.PaymentConfig
import com.sunmi.pay.hardware.aidl.AidlConstants

/**
 * PayPass (Mastercard) payment scheme strategy.
 * 
 * Implements PaymentSchemeStrategy for Mastercard contactless transactions.
 */
class PayPassStrategy : PaymentSchemeStrategy {
    
    override fun getTlvTags(): Array<String> {
        return arrayOf(
            "DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
            "DF8123", "DF8124", "DF8125", "DF8126", "DF811B", "DF811D",
            "DF8122", "DF8120", "DF8121", "9F33", "9F66"
        )
    }
    
    override fun getTlvValues(
        amount: Long,
        df8119Value: String,
        highCvmLimit: String,
        maxTransLimit: String
    ): Array<String> {
        return arrayOf(
            PaymentConfig.PayPassConfig.DF8117,
            PaymentConfig.PayPassConfig.DF8118,
            df8119Value, // 02 = No CVM only
            PaymentConfig.PayPassConfig.DF811F,
            PaymentConfig.PayPassConfig.DF811E,
            PaymentConfig.PayPassConfig.DF812C,
            highCvmLimit, // DF8123 - Floor Limit (very high)
            highCvmLimit, // DF8124 - CVM Required Limit (very high - kernel never requires CVM)
            maxTransLimit, // DF8125 - Max Transaction Limit
            highCvmLimit, // DF8126 - Contactless Floor Limit
            PaymentConfig.PayPassConfig.DF811B,
            PaymentConfig.PayPassConfig.DF811D,
            PaymentConfig.PayPassConfig.DF8122,
            PaymentConfig.PayPassConfig.DF8120,
            PaymentConfig.PayPassConfig.DF8121,
            PaymentConfig.TERMINAL_CAPABILITIES,
            PaymentConfig.TTQ_9F66
        )
    }
    
    override fun getTlvOpCode(): Int {
        return AidlConstants.EMV.TLVOpCode.OP_PAYPASS
    }
    
    override fun getSchemeName(): String {
        return "PayPass (Mastercard)"
    }
}
