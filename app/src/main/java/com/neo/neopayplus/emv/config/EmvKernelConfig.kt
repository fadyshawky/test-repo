package com.neo.neopayplus.emv.config

import android.os.Bundle
import com.neo.neopayplus.Constant
import com.neo.neopayplus.MyApplication
import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2

/**
 * EMV Kernel Configuration for Egypt/EGP transactions.
 * 
 * Provides terminal parameters and contactless kernel TLVs
 * required for PayPass, PayWave, and other contactless schemes.
 */
object EmvKernelConfig {
    
    private const val TAG = "EmvKernelConfig"
    
    /**
     * Build terminal parameters for Egypt (EGP currency).
     * These parameters configure the EMV terminal capabilities.
     */
    @JvmStatic
    fun buildTerminalParamsEgypt(): Bundle {
        return Bundle().apply {
            // Terminal capabilities
            putString("terminalType", "22") // Attended, online only
            putString("terminalCapability", "E0F8C8") // ICC + Magnetic + Manual key entry
            putString("terminalExCapability", "F000F0A001") // Extended capabilities
            
            // Country and currency (Egypt)
            putString("terminalCountryCode", "0818") // Egypt country code
            putString("transCurrCode", "0818") // EGP currency code
            putString("transCurrExp", "02") // 2 decimal places
            putString("referCurrCode", "0818")
            putString("referCurrExp", "02")
            
            // Merchant info
            putString("merchantId", com.neo.neopayplus.config.PaymentConfig.MERCHANT_ID)
            putString("terminalId", com.neo.neopayplus.config.PaymentConfig.TERMINAL_ID)
            putString("merchantName", com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME)
            putString("merchantCateCode", "5411") // Grocery stores
            
            // Transaction limits (in minor units - piasters)
            putString("terminalFloorLimit", "000000000000") // No floor limit (always go online)
            putString("termRiskManageData", "6C00000000000000") // TRM data
            
            // CRITICAL: Enable contactless support
            putBoolean("supportNFC", true)
            putBoolean("supportClss", true)
            putBoolean("supportIC", true)
            putBoolean("supportMag", true)
            
            // Other settings
            putBoolean("bypassPIN", true) // Allow PIN bypass
            putBoolean("supportSM", false) // No SM support
            putInt("getDataPIN", 0) // PIN try counter
            putInt("ECTermSupportIndicator", 0)
        }
    }
    
    /**
     * Apply contactless kernel configuration (PayPass, PayWave TLVs).
     * Must be called after terminal params are set.
     */
    @JvmStatic
    fun applyContactlessKernelConfig(emv: EMVOptV2) {
        LogUtil.e(TAG, "Applying contactless kernel configuration...")
        
        try {
            // PayPass (Mastercard) kernel TLVs
            applyPayPassTlvs(emv)
            
            // PayWave (Visa) kernel TLVs
            applyPayWaveTlvs(emv)
            
            // Apply normal TLVs (TTQ, etc.)
            applyNormalTlvs(emv)
            
            LogUtil.e(TAG, "✓ Contactless kernel TLVs applied successfully")
        } catch (e: Exception) {
            LogUtil.e(TAG, "❌ Failed to apply contactless TLVs: ${e.message}")
        }
    }
    
    /**
     * Apply PayPass (Mastercard) specific TLVs.
     * Uses PaymentConfig.PayPassConfig for CVM limits.
     */
    private fun applyPayPassTlvs(emv: EMVOptV2) {
        val tagsPayPass = arrayOf(
            "DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
            "DF811B", "DF811D", "DF8122", "DF8120", "DF8121",
            "DF8123", "DF8124", "DF8125", "DF8126" // Limits
        )
        val valuesPayPass = arrayOf(
            PaymentConfig.PayPassConfig.DF8117,
            PaymentConfig.PayPassConfig.DF8118,
            PaymentConfig.PayPassConfig.DF8119,
            PaymentConfig.PayPassConfig.DF811F,
            PaymentConfig.PayPassConfig.DF811E,
            PaymentConfig.PayPassConfig.DF812C,
            PaymentConfig.PayPassConfig.DF811B,
            PaymentConfig.PayPassConfig.DF811D,
            PaymentConfig.PayPassConfig.DF8122,
            PaymentConfig.PayPassConfig.DF8120,
            PaymentConfig.PayPassConfig.DF8121,
            PaymentConfig.PayPassConfig.DF8123, // Floor Limit - 600.00 EGP
            PaymentConfig.PayPassConfig.DF8124, // CVM Limit - 600.00 EGP (no PIN below this)
            PaymentConfig.PayPassConfig.DF8125, // Max Contactless - 1,000,000.00 EGP
            PaymentConfig.PayPassConfig.DF8126  // Reader Floor Limit - 600.00 EGP
        )
        
        val result = emv.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass)
        LogUtil.e(TAG, "  PayPass TLVs set (CVM limit: ${PaymentConfig.PayPassConfig.DF8124}), result=$result")
    }
    
    /**
     * Apply PayWave (Visa) specific TLVs.
     */
    private fun applyPayWaveTlvs(emv: EMVOptV2) {
        val tagsPayWave = arrayOf(
            "DF8117", "DF8118", "DF8119", "DF811F", "DF811E"
        )
        val valuesPayWave = arrayOf(
            "E0", "F8", "F8", "E8", "00"
        )
        
        val result = emv.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tagsPayWave, valuesPayWave)
        LogUtil.e(TAG, "  PayWave TLVs set, result=$result")
    }
    
    /**
     * Apply normal TLVs including TTQ (Terminal Transaction Qualifiers).
     */
    private fun applyNormalTlvs(emv: EMVOptV2) {
        // TTQ (9F66) - Terminal Transaction Qualifiers
        // Bits: qVSDC supported, MSD supported, Contact chip supported, EMV mode supported
        val tagsNormal = arrayOf("9F66")
        val valuesNormal = arrayOf("36000000") // Enable qVSDC, contact, EMV mode
        
        val result = emv.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagsNormal, valuesNormal)
        LogUtil.e(TAG, "  Normal TLVs (TTQ) set, result=$result")
    }
    
    /**
     * Provision and validate EMV configuration.
     * Called after AIDs/CAPKs are loaded to verify everything is correct.
     */
    @JvmStatic
    fun provisionAndValidate() {
        LogUtil.e(TAG, "Validating EMV configuration...")
        
        val emv = MyApplication.app.emvOptV2
        if (emv == null) {
            LogUtil.e(TAG, "❌ EMV service not available for validation")
            return
        }
        
        try {
            // Read back some TLVs to verify they were set
            val tlvData = ByteArray(256)
            
            // Check 9F66 (TTQ)
            val len9F66 = emv.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "9F66", tlvData)
            if (len9F66 > 0) {
                val ttqHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(tlvData, 0, len9F66)
                LogUtil.e(TAG, "  ✓ TTQ (9F66) = $ttqHex")
            } else {
                LogUtil.e(TAG, "  ⚠️ TTQ (9F66) not set")
            }
            
            // Check PayPass CVM limit (DF8123)
            val lenDF8123 = emv.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, "DF8123", tlvData)
            if (lenDF8123 > 0) {
                val cvmLimitHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(tlvData, 0, lenDF8123)
                LogUtil.e(TAG, "  ✓ PayPass CVM Limit (DF8123) = $cvmLimitHex")
            }
            
            LogUtil.e(TAG, "✓ EMV configuration validation complete")
        } catch (e: Exception) {
            LogUtil.e(TAG, "⚠️ Validation error: ${e.message}")
        }
    }
}
