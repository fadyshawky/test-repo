package com.neo.neopayplus.emv.utils

import android.util.Log
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLV
import com.neo.neopayplus.emv.TLVUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2

/**
 * Extracts PAN (Primary Account Number) from EMV TLV data.
 * 
 * Follows Single Responsibility Principle - only handles PAN extraction.
 * 
 * @param emvOptV2 EMV service instance
 */
class PanExtractor(private val emvOptV2: EMVOptV2) {
    
    companion object {
        private const val TAG = Constant.TAG
        
        // TLV tags that may contain PAN
        private val PAN_TAGS = arrayOf("5A", "57", "9F6B")
    }
    
    /**
     * Extract PAN from TLV tags
     * 
     * Priority order:
     * 1. Tag 5A (PAN) - Primary Account Number
     * 2. Tag 57 (Track 2 Equivalent Data) - Contains PAN
     * 3. Tag 9F6B (Contactless Track 2 Data) - Contactless PAN
     * 
     * @return PAN if found, null otherwise
     */
    fun extractPan(): String? {
        return try {
            val outData = ByteArray(512)
            val len = emvOptV2.getTlvList(
                com.sunmi.pay.hardware.aidl.AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                PAN_TAGS,
                outData
            )
            
            if (len <= 0) {
                Log.e(TAG, "No TLV data available for PAN extraction")
                return null
            }
            
            val tlvMap = TLVUtil.buildTLVMap(outData.copyOf(len))
            
            // Try tag 5A first (PAN)
            extractFromTag5A(tlvMap) 
                ?: extractFromTag57(tlvMap) 
                ?: extractFromTag9F6B(tlvMap)
                
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting PAN from TLV: ${e.message}")
            null
        }
    }
    
    /**
     * Extract PAN from tag 5A (Primary Account Number)
     * PAN is BCD encoded, remove ONLY trailing 'F' padding (not all 'F' characters)
     * Return PAN exactly as extracted from EMV tag - no masking, no truncation
     */
    private fun extractFromTag5A(tlvMap: Map<String, TLV>): String? {
        val tlv = tlvMap["5A"]
        if (tlv != null && tlv.value != null) {
            val panHex = tlv.value
            // PAN is BCD encoded, remove ONLY trailing 'F' padding (regex: F+$ means one or more F at end)
            val pan = panHex.uppercase().replace(Regex("F+$"), "")
            Log.e(TAG, "✓ PAN extracted from tag 5A (full, unmasked): $pan (length: ${pan.length})")
            return pan
        }
        return null
    }
    
    /**
     * Extract PAN from tag 57 (Track 2 Equivalent Data)
     * Format: PAN + 'D' + ExpiryDate + ServiceCode + ...
     */
    private fun extractFromTag57(tlvMap: Map<String, TLV>): String? {
        val tlv = tlvMap["57"]
        if (tlv != null && tlv.value != null) {
            val track2Hex = tlv.value.uppercase()
            val delimiterIndex = track2Hex.indexOf('D')
            if (delimiterIndex > 0) {
                val pan = track2Hex.substring(0, delimiterIndex).replace("F", "")
                Log.e(TAG, "PAN extracted from tag 57: ${maskPan(pan)}")
                return pan
            }
        }
        return null
    }
    
    /**
     * Extract PAN from tag 9F6B (Contactless Track 2 Data)
     * Format: Same as tag 57
     * Return PAN exactly as extracted from EMV tag - no masking, no truncation
     */
    private fun extractFromTag9F6B(tlvMap: Map<String, TLV>): String? {
        val tlv = tlvMap["9F6B"]
        if (tlv != null && tlv.value != null) {
            val track2Hex = tlv.value.uppercase()
            val delimiterIndex = track2Hex.indexOf('D')
            if (delimiterIndex > 0) {
                // Extract PAN part (before 'D' delimiter) and remove ONLY trailing 'F' padding
                val pan = track2Hex.substring(0, delimiterIndex).replace(Regex("F+$"), "")
                Log.e(TAG, "✓ PAN extracted from tag 9F6B (full, unmasked): $pan (length: ${pan.length})")
                return pan
            }
        }
        return null
    }
    
    /**
     * Mask PAN for logging (security)
     * Shows first 6 and last 4 digits only
     */
    private fun maskPan(pan: String): String {
        if (pan.length < 10) return "****"
        return "${pan.substring(0, 6)}****${pan.substring(pan.length - 4)}"
    }
}
