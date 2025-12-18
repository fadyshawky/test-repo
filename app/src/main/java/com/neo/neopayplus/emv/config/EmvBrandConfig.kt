package com.neo.neopayplus.emv.config

import com.neo.neopayplus.Constant
import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.emv.TLVUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2
import java.text.SimpleDateFormat
import java.util.*

/**
 * Brand-specific EMV configuration for payment card schemes.
 * 
 * Defines terminal capabilities, limits, and TTQ values for each supported
 * card brand (Visa, Mastercard, Meeza) following EMV specifications and
 * Egyptian market requirements.
 */
object EmvBrandConfig {
    
    /**
     * Brand profile containing EMV parameters for a specific card scheme.
     * 
     * @property name Brand name (e.g., "VISA", "MASTERCARD")
     * @property t9F33 Terminal Capabilities (tag 9F33)
     * @property t9F40 Additional Terminal Capabilities (tag 9F40)
     * @property t9F35 Terminal Type (tag 9F35)
     * @property t9F53 Transaction Category Code (tag 9F53), null if not used
     * @property ttq9F66 Terminal Transaction Qualifiers (tag 9F66) for contactless
     * @property noCvmLimitMinor Maximum amount for no CVM required (minor units)
     * @property cvmRequiredLimitMinor Minimum amount requiring CVM (minor units)
     * @property clFloorLimitMinor Contactless floor limit for offline approval (minor units)
     */
    data class BrandProfile(
        val name: String,
        // Static terminal TLVs
        val t9F33: String,         // Terminal Capabilities
        val t9F40: String,         // Additional Terminal Capabilities
        val t9F35: String,         // Terminal Type
        val t9F53: String?,        // Transaction Category Code (if scheme uses)
        val ttq9F66: String,       // TTQ (for NFC path)
        // Contactless CVM/limits (scheme-specific)
        val noCvmLimitMinor: Long,
        val cvmRequiredLimitMinor: Long,
        val clFloorLimitMinor: Long,
    )

    // Egypt numeric codes (ISO 3166/4217)
    const val ISO_NUM_EGYPT = "0818" // 818 packed BCD

    /**
     * Visa brand profile tuned for Egypt terminals.
     * 
     * - Supports qVSDC (quick Visa Smart Debit/Credit)
     * - CVM limit: 600.00 EGP
     * - Contactless floor limit: 500.00 EGP (allows offline approval)
     */
    @JvmField
    val VISA = BrandProfile(
        name = "VISA",
        t9F33 = "60F8C8",
        t9F40 = "F000F0F001",
        t9F35 = "21",
        t9F53 = "708000",
        // Use centralized TTQ from PaymentConfig (36A0C000)
        ttq9F66 = PaymentConfig.TTQ_9F66,
        noCvmLimitMinor = 600_00,  // EGP 600.00 → minor units
        cvmRequiredLimitMinor = 600_00,
        clFloorLimitMinor = 500_00  // EGP 500.00 - allow offline approval up to this amount
    )

    /**
     * Mastercard brand profile tuned for Egypt terminals.
     * 
     * - Supports PayPass contactless
     * - CVM limit: 600.00 EGP
     * - Contactless floor limit: 500.00 EGP (allows offline approval)
     */
    @JvmField
    val MASTERCARD = BrandProfile(
        name = "MASTERCARD",
        t9F33 = "60F8C8",
        t9F40 = "F000F0F001",
        t9F35 = "21",
        t9F53 = "708000",
        // Use centralized TTQ from PaymentConfig (36A0C000)
        ttq9F66 = PaymentConfig.TTQ_9F66,
        noCvmLimitMinor = 600_00,
        cvmRequiredLimitMinor = 600_00,
        clFloorLimitMinor = 500_00  // EGP 500.00 - allow offline approval up to this amount
    )

    /**
     * Meeza brand profile tuned for Egypt terminals.
     * 
     * - Egyptian national payment scheme
     * - Online-centric: requires online authorization for all amounts
     * - Floor limit: 0 (no offline approval)
     */
    @JvmField
    val MEEZA = BrandProfile(
        name = "MEEZA",
        t9F33 = "60F8C8",
        t9F40 = "F000F0F001",
        t9F35 = "21",
        t9F53 = "708000",
        // Meeza keeps its own TTQ profile (online-centric)
        ttq9F66 = "E0F0C8A0",
        noCvmLimitMinor = 0,
        cvmRequiredLimitMinor = 0,
        clFloorLimitMinor = 0  // Meeza requires online - keep at 0
    )

    /**
     * Get brand profile for a given AID.
     * 
     * @param aid Application Identifier (e.g., "A0000000031010")
     * @return BrandProfile if AID matches a known brand, null otherwise
     */
    fun getBrandForAid(aid: String): BrandProfile? {
        return when {
            aid.startsWith("A000000003") -> VISA
            aid.startsWith("A000000004") || aid.startsWith("A000000005") -> MASTERCARD
            aid.startsWith("A000000732") -> MEEZA
            else -> null
        }
    }

    /**
     * Get all supported brand profiles.
     */
    fun getAllBrands(): List<BrandProfile> = listOf(VISA, MASTERCARD, MEEZA)

    /**
     * Terminal configuration for EMV processing.
     */
    data class TerminalCfg(
        val terminalId: String,
        val merchantId: String,
        val merchantNameAddr40: String,
        val acquirerId: String,
        val posDataCode12: String,   // DE22 (an12) configured per bank
        val ifdSerial9F1EHex: String,
        val currency4217Numeric: String = ISO_NUM_EGYPT, // "0818" packed BCD
        val country3166Numeric: String = ISO_NUM_EGYPT,  // "0818" packed BCD
        val zpkIndex: String = "001",
        val zmkMacIndex: String = "000"
    )

    /**
     * Apply static terminal TLVs for brand.
     * Sets terminal capabilities, country code, currency code, etc. for all kernels.
     * 
     * NOTE: To match SDK demo exactly, 9F66 (TTQ) is NOT set in PayPass/PayWave spaces.
     * 9F66 is only set in OP_NORMAL and other kernel spaces (not PayPass/PayWave).
     */
    fun applyStaticTerminalTlvs(emv: EMVOptV2, brand: BrandProfile, cfg: TerminalCfg) {
        // Include TTQ (9F66) so kernel uses our TTQ instead of Sunmi defaults
        val tagsWithTtq = mutableListOf("9F33","9F40","9F35","9F1A","5F2A","9F66")
        val valsWithTtq = mutableListOf(
            brand.t9F33,
            brand.t9F40,
            brand.t9F35,
            cfg.country3166Numeric,
            cfg.currency4217Numeric,
            brand.ttq9F66
        )
        brand.t9F53?.let { tagsWithTtq += "9F53"; valsWithTtq += it }
        
        // Tags WITHOUT 9F66 (for PayPass/PayWave per SDK demo)
        val tagsWithoutTtq = mutableListOf("9F33","9F40","9F35","9F1A","5F2A")
        val valsWithoutTtq = mutableListOf(
            brand.t9F33,
            brand.t9F40,
            brand.t9F35,
            cfg.country3166Numeric,
            cfg.currency4217Numeric
        )
        brand.t9F53?.let { tagsWithoutTtq += "9F53"; valsWithoutTtq += it }
        
        // Set static terminal TLVs with 9F66 for OP_NORMAL and other kernels (NOT PayPass/PayWave)
        val kernelsWithTtq = arrayOf(
            AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL,
            AidlConstantsV2.EMV.TLVOpCode.OP_AE,
            AidlConstantsV2.EMV.TLVOpCode.OP_JCB
        )
        
        for (kernel in kernelsWithTtq) {
            emv.setTlvList(kernel, tagsWithTtq.toTypedArray(), valsWithTtq.toTypedArray())
        }
        
        // Set static terminal TLVs WITHOUT 9F66 for PayPass/PayWave (matching SDK demo)
        val kernelsWithoutTtq = arrayOf(
            AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS,
            AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE
        )
        
        for (kernel in kernelsWithoutTtq) {
            emv.setTlvList(kernel, tagsWithoutTtq.toTypedArray(), valsWithoutTtq.toTypedArray())
        }
    }

    /**
     * Apply per-transaction TLVs – must be called for every transaction.
     * Sets transaction date, time, amount, transaction type, etc. for all kernels.
     */
    fun applyTxnTlvs(emv: EMVOptV2, brand: BrandProfile, cfg: TerminalCfg, amountMinor12: String) {
        val now = Date()
        val fDate = SimpleDateFormat("yyMMdd", Locale.US)
        val fTime = SimpleDateFormat("HHmmss", Locale.US)
        val dateYYMMDD = fDate.format(now)
        val timeHHMMSS = fTime.format(now)

        // Transaction TLVs that must be set for ALL kernels
        val txnTags = arrayOf("9A","9F21","9C","9F02","9F03","9F1E")
        val txnValues = arrayOf(
                dateYYMMDD,
                timeHHMMSS,
                "00",                    // purchase
                amountMinor12,
                "000000000000",
            cfg.ifdSerial9F1EHex
        )

        // Set transaction TLVs for ALL kernels to prevent -50019 error
        // The kernel will use the appropriate one based on card type
        val kernels = arrayOf(
            AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL,
            AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS,
            AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE,
            AidlConstantsV2.EMV.TLVOpCode.OP_AE,
            AidlConstantsV2.EMV.TLVOpCode.OP_JCB
        )

        for (kernel in kernels) {
            emv.setTlvList(kernel, txnTags, txnValues)
        }
        
        LogUtil.e(Constant.TAG, "✓ Transaction TLVs set for all kernels (amount=$amountMinor12)")

        // Optional validation: check that TLVs exist (non-fatal, just for debugging)
        // Note: TLVs will be re-set right before transactProcessEx() in startEmvImmediate()
        // to ensure they're fresh, so this is just a sanity check
        validateKernelTlvs(emv, amountMinor12)
    }

    private fun validateKernelTlvs(emv: EMVOptV2, amountMinor12: String) {
        val tags = arrayOf("9F02","9A","9F21","9C","9F1E","9F66","9F1A","5F2A")
        val out = ByteArray(2048)
        val len = emv.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tags, out)
        if (len <= 0) {
            LogUtil.e(Constant.TAG, "⚠️ TLV read failed before EMV start - kernel may not have TLVs yet")
            // Don't fail - TLVs will be set right before transactProcessEx
            return
        }

        val map = TLVUtil.buildTLVMap(out.copyOf(len))
        
        // Check that required tags exist (but don't validate exact values since kernel might have stale data)
        // The TLVs are set right before transactProcessEx, so this is just a sanity check
        val missingTags = mutableListOf<String>()
        if (map["9F02"]?.value.isNullOrEmpty()) missingTags.add("9F02")
        if (map["9A"]?.value.isNullOrEmpty()) missingTags.add("9A")
        if (map["9F21"]?.value.isNullOrEmpty()) missingTags.add("9F21")
        if (map["9C"]?.value.isNullOrEmpty()) missingTags.add("9C")
        if (map["9F1E"]?.value.isNullOrEmpty()) missingTags.add("9F1E")
        if (map["9F66"]?.value.isNullOrEmpty()) missingTags.add("9F66")
        if (map["9F1A"]?.value.isNullOrEmpty()) missingTags.add("9F1A")
        if (map["5F2A"]?.value.isNullOrEmpty()) missingTags.add("5F2A")
        
        if (missingTags.isNotEmpty()) {
            LogUtil.e(Constant.TAG, "⚠️ Some TLVs missing from kernel (will be set before transaction): ${missingTags.joinToString()}")
            LogUtil.e(Constant.TAG, "  This is OK - TLVs will be refreshed right before transactProcessEx()")
            // Don't fail - TLVs will be set in startEmvImmediate()
            return
        }
        
        // Log actual values for debugging (but don't validate exact match since format may differ)
        val amount9F02 = map["9F02"]?.value
        LogUtil.e(Constant.TAG, "✓ TLV validation passed - all required tags present")
        LogUtil.e(Constant.TAG, "  9F02 (amount) in kernel: $amount9F02 (expected: $amountMinor12 as BCD)")
        LogUtil.e(Constant.TAG, "  Note: 9F02 is stored as BCD hex, not decimal string")
    }
}
