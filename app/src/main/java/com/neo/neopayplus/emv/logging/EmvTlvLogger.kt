package com.neo.neopayplus.emv.logging

import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLVUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.payservice.AidlConstantsV2
import java.util.Locale

/**
 * Logs EMV TLV data for debugging purposes.
 */
class EmvTlvLogger(
    private val emv: EMVOptV2,
    private val readTag: (String) -> String?,
    private val getDetectedScheme: () -> Int,
    private val getCardType: () -> Int,
    private val getSelectedAid: () -> String?
) {
    
    /**
     * Dump all TLVs for debugging.
     */
    fun dumpAllTlvs(context: String, tags: Array<String>) {
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "📋 TLV DUMP - $context")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        
        val isContactless = getCardType() == AidlConstants.CardType.NFC.getValue()
        val detectedScheme = getDetectedScheme()
        // Prefer the selected AID from handler; if not yet set (e.g. onConfirmCardNo),
        // fall back to reading 84 from the kernel so we still know the RID.
        val selectedAid = getSelectedAid() ?: readTag("84")
        val aidPrefix = selectedAid?.take(10) ?: ""

        // Choose TLVOpCode based primarily on actual AID (RID), not only on detectedScheme.
        // This gives a more accurate view of the brand-specific kernel TLV space where
        // our configuration (TTQ, limits, etc.) is applied, instead of the SDK's
        // internal/default OP_NORMAL snapshot.
        val tlvOpCode = if (isContactless) {
            when {
                aidPrefix.startsWith("A000000004") || aidPrefix.startsWith("A000000005") ->
                    AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS   // Mastercard PayPass
                aidPrefix.startsWith("A000000003") ->
                    AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE   // Visa payWave
                else -> when (detectedScheme) {
                    2 -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS
                    1 -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE
                    else -> AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
                }
            }
        } else {
            AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
        }
        
        LogUtil.e(
            Constant.TAG,
            "Using TLVOpCode=$tlvOpCode (scheme=$detectedScheme, isContactless=$isContactless, aidPrefix=$aidPrefix)"
        )
        
        val out = ByteArray(4096)
        val len = emv.getTlvList(tlvOpCode, tags, out)
        
        if (len > 0) {
            val tlvHex = out.copyOf(len).joinToString("") { "%02X".format(it) }
            LogUtil.e(Constant.TAG, "TLV RAW HEX ($len bytes): $tlvHex")
            
            // Parse and log individual tags
            val map = TLVUtil.buildTLVMap(out.copyOf(len))
            for (tag in tags) {
                val tlv = map[tag]
                if (tlv != null) {
                    LogUtil.e(Constant.TAG, "  $tag = ${tlv.value} (${getTagName(tag)})")
                }
            }
            
            // Special logging for AIP and AFL
            map["82"]?.let { LogUtil.e(Constant.TAG, ">>> AIP: ${it.value}") }
            map["94"]?.let { LogUtil.e(Constant.TAG, ">>> AFL: ${it.value}") }
            map["9F27"]?.let { LogUtil.e(Constant.TAG, ">>> CID: ${it.value}") }
            map["95"]?.let { LogUtil.e(Constant.TAG, ">>> TVR: ${it.value}") }
        } else {
            LogUtil.e(Constant.TAG, "TLV read failed or empty (len=$len)")
        }
    }
    
    /**
     * Get human-readable tag name.
     */
    private fun getTagName(tag: String): String = when(tag) {
        "82" -> "AIP"
        "94" -> "AFL"
        "9F26" -> "AC"
        "9F27" -> "CID"
        "9F10" -> "IAD"
        "9F36" -> "ATC"
        "9F37" -> "UN"
        "95" -> "TVR"
        "9B" -> "TSI"
        "9F02" -> "Amount"
        "9F66" -> "TTQ"
        "9F6C" -> "CTQ"
        "84" -> "AID"
        else -> ""
    }
    
    /**
     * Comprehensive card reading output logger.
     * Logs all important card data read from the EMV kernel.
     */
    fun logCardReadingOutput(context: String, maskCardNumber: (String) -> String) {
        try {
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            LogUtil.e(Constant.TAG, "📋 CARD READING OUTPUT - $context")
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            
            // Card Type and Detection Info
            val isContactless = getCardType() == AidlConstants.CardType.NFC.getValue()
            val cardTypeName = when (getCardType()) {
                AidlConstants.CardType.NFC.getValue() -> "NFC (Contactless)"
                AidlConstants.CardType.IC.getValue() -> "IC (Chip)"
                AidlConstants.CardType.MAGNETIC.getValue() -> "Magnetic Stripe"
                else -> "Unknown (${getCardType()})"
            }
            LogUtil.e(Constant.TAG, "Card Type: $cardTypeName")
            
            // Scheme Detection
            val currentSelectedAid = getSelectedAid() ?: readTag("84")
            val isMastercard = currentSelectedAid?.startsWith("A000000004") == true || currentSelectedAid?.startsWith("A000000005") == true
            val isMeeza = currentSelectedAid?.startsWith("A000000732") == true
            
            val schemeName = when {
                getDetectedScheme() == 1 -> "VISA"
                getDetectedScheme() == 2 && isMastercard -> "MASTERCARD"
                getDetectedScheme() == 2 && isMeeza -> "MEEZA"
                getDetectedScheme() == 2 -> "MASTERCARD/MEEZA (unknown)"
                else -> "UNKNOWN"
            }
            LogUtil.e(Constant.TAG, "Detected Scheme: $schemeName (code=${getDetectedScheme()})")
            
            // AID (Application Identifier)
            val aid = currentSelectedAid
            if (aid != null && aid.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "Selected AID: $aid")
                when {
                    aid.startsWith("A000000003") -> LogUtil.e(Constant.TAG, "  AID Type: VISA")
                    aid.startsWith("A000000004") || aid.startsWith("A000000005") -> LogUtil.e(Constant.TAG, "  AID Type: MASTERCARD")
                    aid.startsWith("A000000732") -> LogUtil.e(Constant.TAG, "  AID Type: MEEZA")
                    else -> LogUtil.e(Constant.TAG, "  AID Type: OTHER")
                }
            } else {
                LogUtil.e(Constant.TAG, "Selected AID: NOT FOUND")
            }
            
            // PAN (Primary Account Number)
            val panTag = readTag("5A")
            if (panTag != null && panTag.isNotEmpty()) {
                // PAN in tag 5A is BCD format, convert to decimal
                val panDecimal = try {
                    panTag.chunked(2).joinToString("") { hexPair ->
                        val byte = hexPair.toIntOrNull(16) ?: 0
                        val high = (byte shr 4) and 0x0F
                        val low = byte and 0x0F
                        if (high > 9 || low > 9) String.format(Locale.US, "%02d", byte) else "$high$low"
                    }.trimStart('0')
                } catch (e: Exception) {
                    panTag
                }
                LogUtil.e(Constant.TAG, "PAN (5A): ${maskCardNumber(panDecimal)}")
            } else {
                LogUtil.e(Constant.TAG, "PAN: NOT FOUND")
            }
            
            // Card Expiry Date
            val expiry = readTag("5F24")
            if (expiry != null && expiry.length >= 4) {
                val yy = expiry.substring(0, 2)
                val mm = expiry.substring(2, 4)
                LogUtil.e(Constant.TAG, "Expiry Date (5F24): $mm/$yy")
            } else {
                LogUtil.e(Constant.TAG, "Expiry Date: NOT FOUND")
            }
            
            // Cardholder Name
            val cardholderName = readTag("5F20")
            if (cardholderName != null && cardholderName.isNotEmpty()) {
                val name = try {
                    String(hexToBytes(cardholderName), Charsets.UTF_8).trim()
                } catch (e: Exception) {
                    cardholderName
                }
                LogUtil.e(Constant.TAG, "Cardholder Name (5F20): $name")
            } else {
                LogUtil.e(Constant.TAG, "Cardholder Name: NOT FOUND")
            }
            
            // Card Sequence Number
            val cardSeq = readTag("5F34")
            if (cardSeq != null && cardSeq.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "Card Sequence Number (5F34): $cardSeq")
            }
            
            // Application Label
            val appLabel = readTag("50")
            if (appLabel != null && appLabel.isNotEmpty()) {
                val label = try {
                    String(hexToBytes(appLabel), Charsets.UTF_8).trim()
                } catch (e: Exception) {
                    appLabel
                }
                LogUtil.e(Constant.TAG, "Application Label (50): $label")
            }
            
            // Application Interchange Profile
            val aip = readTag("82")
            if (aip != null && aip.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "AIP (82): $aip")
            }
            
            // AID Version (9F09) - CRITICAL for L2 selection matching
            val aidVersion = readTag("9F09")
            if (aidVersion != null && aidVersion.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "🔍 Card AID Version (9F09): $aidVersion (compare with installed AID version)")
                if (getDetectedScheme() == 2) {  // Mastercard
                    LogUtil.e(Constant.TAG, "   ⚠️ If card version ($aidVersion) != installed version (0002), L2 selection may fail with -4125")
                }
            } else {
                LogUtil.e(Constant.TAG, "⚠️ Card AID Version (9F09): NOT FOUND (cannot verify version match)")
            }
            
            // Application File Locator
            val afl = readTag("94")
            if (afl != null && afl.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "AFL (94): $afl")
            }
            
            // Terminal Verification Results
            val tvr = readTag("95")
            if (tvr != null && tvr.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "TVR (95): $tvr")
            }
            
            // Transaction Status Information
            val tsi = readTag("9B")
            if (tsi != null && tsi.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "TSI (9B): $tsi")
            }
            
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "❌ Error logging card reading output: ${e.message}")
        }
    }
    
    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace("\\s".toRegex(), "")
        return ByteArray(clean.length / 2) { i ->
            Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16).toByte()
        }
    }
}

