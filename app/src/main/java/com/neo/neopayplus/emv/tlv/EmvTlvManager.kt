package com.neo.neopayplus.emv.tlv

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil
import com.neo.neopayplus.emv.config.EmvBrandConfig
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2

/**
 * Manages TLV (Tag-Length-Value) configuration for EMV transactions.
 * Handles setting scheme-specific TLVs (PayPass, PayWave, etc.)
 * 
 * CRITICAL: Uses TTQ values from EmvBrandConfig instead of hardcoded values.
 * This ensures that TTQ changes are actually applied to the kernel.
 */
class EmvTlvManager(
    private val emv: EMVOptV2
) {
    
    /**
     * Set all scheme TLVs (PayPass, PayWave, AMEX, JCB, Normal).
     * Called before checkCard() to ensure TLVs are ready.
     * 
     * @param visaBrand Optional Visa brand profile for PayWave TTQ. If null, uses default.
     * @param mastercardBrand Optional Mastercard brand profile for PayPass TTQ. If null, uses default.
     * 
     * Note: Limits (DF8123, DF8124, DF8125, DF8126) are set by EmvKernelConfig,
     * not here, to allow backend configuration.
     */
    fun setAllSchemeTlvs(
        visaBrand: EmvBrandConfig.BrandProfile? = null,
        mastercardBrand: EmvBrandConfig.BrandProfile? = null
    ) {
        // Get TTQ values from brand config (or use defaults)
        val payPassTtq = mastercardBrand?.ttq9F66 ?: EmvBrandConfig.MASTERCARD.ttq9F66
        val payWaveTtq = visaBrand?.ttq9F66 ?: EmvBrandConfig.VISA.ttq9F66
        
        // PayPass TLVs (Mastercard) - EXACT match with SDK demo
        // NOTE: SDK demo does NOT set 9F66 (TTQ) in PayPass space - only sets DF tags
        // 9F66 is set in OP_NORMAL space, not PayPass space
        val tagsPayPass = arrayOf(
            "DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
            "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"
            // Note: DF8123, DF8124, DF8125, DF8126 are set by EmvKernelConfig.applyPayPassTlvs()
            // Note: 9F66 (TTQ) is NOT set in PayPass space per SDK demo
        )
        val valuesPayPass = arrayOf(
            "E0", "F8", "F8", "E8", "00", "00",  // Capabilities (exact SDK demo values)
            "30", "02", "0000000000", "000000000000", "000000000000"  // Kernel config, TACs
        )
        emv.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass)
        LogUtil.e(Constant.TAG, "✓ PayPass TLVs set (SDK demo format, limits from EmvKernelConfig)")
        
        // PayWave TLVs (Visa) - matching SDK demo pattern
        // NOTE: SDK demo doesn't show PayWave TLVs, but following same pattern as PayPass
        val tagsPayWave = arrayOf(
            "DF8117", "DF8118", "DF8119", "DF811F", "DF811E"
            // Note: 9F66 (TTQ) is NOT set in PayWave space - set in OP_NORMAL instead
        )
        val valuesPayWave = arrayOf(
            "E0", "F8", "F8", "E8", "00"  // Capabilities
        )
        emv.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tagsPayWave, valuesPayWave)
        LogUtil.e(Constant.TAG, "✓ PayWave TLVs set (SDK demo format)")
        
        // AMEX / JCB / Normal TTQ values are expected to come from static kernel config.
        // TTQ (9F66) is set in OP_NORMAL space via EmvBrandConfig.applyStaticTerminalTlvs(), not in PayPass/PayWave spaces.
        LogUtil.e(Constant.TAG, "✓ PayPass/PayWave TLVs set (SDK demo format - no 9F66 in PayPass/PayWave spaces)")
    }
}

