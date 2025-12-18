package com.neo.neopayplus.emv

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2

/**
 * EMV Kernel Debug utilities.
 * 
 * Provides validation and diagnostic functions for EMV kernel configuration.
 */
object EmvKernelDebug {
    
    private const val TAG = "EmvKernelDebug"
    
    /**
     * Validate all contactless kernels are properly configured.
     * Reads back TLVs to verify they were set correctly.
     */
    @JvmStatic
    fun validateAllKernels(emv: EMVOptV2?) {
        if (emv == null) {
            LogUtil.e(TAG, "❌ EMV service not available for kernel validation")
            return
        }
        
        LogUtil.e(TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(TAG, "=== VALIDATING EMV KERNEL CONFIGURATION ===")
        LogUtil.e(TAG, "═══════════════════════════════════════════════════════")
        
        try {
            // Validate PayPass kernel
            validatePayPassKernel(emv)
            
            // Validate PayWave kernel
            validatePayWaveKernel(emv)
            
            // Validate normal TLVs
            validateNormalTlvs(emv)
            
            LogUtil.e(TAG, "═══════════════════════════════════════════════════════")
            LogUtil.e(TAG, "=== KERNEL VALIDATION COMPLETE ===")
            LogUtil.e(TAG, "═══════════════════════════════════════════════════════")
        } catch (e: Exception) {
            LogUtil.e(TAG, "❌ Kernel validation failed: ${e.message}")
        }
    }
    
    private fun validatePayPassKernel(emv: EMVOptV2) {
        LogUtil.e(TAG, "--- PayPass (Mastercard) Kernel ---")
        val tlvData = ByteArray(256)
        
        // Check key PayPass TLVs
        val tagsToCheck = arrayOf("DF8117", "DF8118", "DF8119", "DF8123", "DF8124", "DF8125", "DF8126")
        val tagNames = arrayOf(
            "Card Data Input Capability",
            "CVM Capability - CVM Required", 
            "CVM Capability - No CVM Required",
            "Reader Contactless Floor Limit",           // DF8123 - offline floor
            "Reader CVM Required Limit (PIN threshold)", // DF8124 - PIN required >= this
            "Reader Contactless Transaction Limit",     // DF8125 - max contactless
            "Reader Contactless Floor Limit (Online)"   // DF8126 - online floor
        )
        
        for (i in tagsToCheck.indices) {
            val tag = tagsToCheck[i]
            val len = emv.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tag, tlvData)
            if (len > 0) {
                val value = ByteUtil.bytes2HexStr(tlvData, 0, len)
                LogUtil.e(TAG, "  ✓ $tag (${tagNames[i]}) = $value")
            } else {
                LogUtil.e(TAG, "  ⚠️ $tag (${tagNames[i]}) NOT SET")
            }
        }
    }
    
    private fun validatePayWaveKernel(emv: EMVOptV2) {
        LogUtil.e(TAG, "--- PayWave (Visa) Kernel ---")
        val tlvData = ByteArray(256)
        
        // Check key PayWave TLVs
        val tagsToCheck = arrayOf("DF8117", "DF8118", "DF8119")
        val tagNames = arrayOf(
            "Card Data Input Capability",
            "CVM Capability - CVM Required",
            "CVM Capability - No CVM Required"
        )
        
        for (i in tagsToCheck.indices) {
            val tag = tagsToCheck[i]
            val len = emv.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tag, tlvData)
            if (len > 0) {
                val value = ByteUtil.bytes2HexStr(tlvData, 0, len)
                LogUtil.e(TAG, "  ✓ $tag (${tagNames[i]}) = $value")
            } else {
                LogUtil.e(TAG, "  ⚠️ $tag (${tagNames[i]}) NOT SET")
            }
        }
    }
    
    private fun validateNormalTlvs(emv: EMVOptV2) {
        LogUtil.e(TAG, "--- Normal TLVs ---")
        val tlvData = ByteArray(256)
        
        // Check TTQ (9F66)
        val len9F66 = emv.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "9F66", tlvData)
        if (len9F66 > 0) {
            val ttqValue = ByteUtil.bytes2HexStr(tlvData, 0, len9F66)
            LogUtil.e(TAG, "  ✓ 9F66 (TTQ) = $ttqValue")
            
            // Decode TTQ bits
            if (len9F66 >= 4) {
                val byte1 = tlvData[0].toInt() and 0xFF
                LogUtil.e(TAG, "    Byte 1: ${String.format("%02X", byte1)}")
                LogUtil.e(TAG, "      - MSD supported: ${(byte1 and 0x80) != 0}")
                LogUtil.e(TAG, "      - qVSDC supported: ${(byte1 and 0x20) != 0}")
                LogUtil.e(TAG, "      - EMV mode supported: ${(byte1 and 0x10) != 0}")
                LogUtil.e(TAG, "      - Contact chip supported: ${(byte1 and 0x08) != 0}")
            }
        } else {
            LogUtil.e(TAG, "  ⚠️ 9F66 (TTQ) NOT SET")
        }
    }
    
    /**
     * Log all currently configured AIDs.
     */
    @JvmStatic
    fun logConfiguredAids(emv: EMVOptV2?) {
        if (emv == null) {
            LogUtil.e(TAG, "❌ EMV service not available")
            return
        }
        
        LogUtil.e(TAG, "=== Configured AIDs ===")
        // Note: The SDK doesn't provide a direct way to list all AIDs
        // This would require iterating through known AIDs
        LogUtil.e(TAG, "  (AID enumeration not available via SDK)")
    }
}
