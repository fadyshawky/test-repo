package com.neo.neopayplus.emv.utils

import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.neo.neopayplus.emv.TLVUtil

/**
 * Utility for reading EMV tags from the kernel.
 * Handles TLVOpCode selection based on card type and scheme.
 */
class EmvTagReader(
    private val emv: EMVOptV2
) {
    
    /**
     * Read a TLV tag value from the EMV kernel.
     * Automatically selects the correct TLVOpCode based on card type and scheme.
     */
    fun readTag(
        tag: String,
        cardType: Int,
        detectedScheme: Int
    ): String? {
        val isContactless = cardType == AidlConstants.CardType.NFC.getValue()
        val tlvOpCode = if (isContactless) {
            when (detectedScheme) {
                2 -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS  // Mastercard
                1 -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE  // Visa
                else -> AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
            }
        } else {
            AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
        }
        
        val buf = ByteArray(1024)
        val len = emv.getTlvList(tlvOpCode, arrayOf(tag), buf)
        if (len <= 0) return null
        val map = TLVUtil.buildTLVMap(buf.copyOf(len))
        return map[tag]?.value
    }
    
    /**
     * Read a card-side TLV tag from the appropriate TLV space.
     * Card-side tags (9F6E, 9F6C, DF21, etc.) come from the card during transaction.
     * For PayPass/PayWave, card-side tags may be stored in the brand-specific space.
     * 
     * @param tag Tag to read (e.g., "9F6E", "9F6C", "DF21")
     * @param cardType Card type (NFC, IC, etc.)
     * @param detectedScheme Detected scheme (1=Visa, 2=Mastercard, etc.)
     * @return Tag value as hex string, or null if not found
     */
    fun readCardTag(tag: String, cardType: Int, detectedScheme: Int): String? {
        val buf = ByteArray(1024)
        val isContactless = cardType == AidlConstants.CardType.NFC.getValue()
        
        // For PayPass/PayWave, card-side tags may be in brand-specific space
        // Try brand-specific space first, then fall back to OP_NORMAL
        val spacesToTry = if (isContactless) {
            when (detectedScheme) {
                2 -> listOf(
                    AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS,  // Mastercard PayPass - try first
                    AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL   // Fallback
                )
                1 -> listOf(
                    AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE,  // Visa PayWave - try first
                    AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL    // Fallback
                )
                else -> listOf(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL)
            }
        } else {
            listOf(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL)
        }
        
        // Try each space until we find the tag
        for (tlvOpCode in spacesToTry) {
            val len = emv.getTlvList(tlvOpCode, arrayOf(tag), buf)
            if (len > 0) {
                val map = TLVUtil.buildTLVMap(buf.copyOf(len))
                val value = map[tag]?.value
                if (value != null) {
                    return value
                }
            }
        }
        
        return null
    }
    
    /**
     * Mask PAN for risk management (first 6 + last 4 digits).
     */
    fun maskPanForRisk(pan: String?): String {
        if (pan == null || pan.isEmpty()) return ""
        return if (pan.length >= 10) {
            pan.take(6) + pan.takeLast(4)
        } else {
            pan
        }
    }
    
    /**
     * Mask card number for logging (first 6 + last 4 digits with stars).
     */
    fun maskCardNumber(pan: String?): String {
        if (pan == null || pan.length < 8) return "****"
        return "${pan.take(6)}****${pan.takeLast(4)}"
    }
}

