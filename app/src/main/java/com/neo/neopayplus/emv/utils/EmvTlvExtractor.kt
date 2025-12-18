package com.neo.neopayplus.emv.utils

import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLVUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2

/**
 * Utilities for extracting TLV values from TLV strings and querying installed AIDs.
 */
object EmvTlvExtractor {
    
    /**
     * Extract TLV value for a given tag from a TLV string.
     * 
     * @param tlvString TLV string in hex format
     * @param tag Tag to extract (e.g., "9F09", "DF01")
     * @return Extracted value as hex string, or null if not found
     */
    fun extractTlvValue(tlvString: String, tag: String): String? {
        val tagUpper = tag.uppercase()
        val tagIndex = tlvString.indexOf(tagUpper, ignoreCase = true)
        if (tagIndex == -1) {
            return null
        }
        
        try {
            // Read length (1-2 hex chars, representing bytes)
            val lengthStart = tagIndex + tagUpper.length
            if (lengthStart + 2 > tlvString.length) {
                return null
            }
            
            val lengthHex = tlvString.substring(lengthStart, lengthStart + 2)
            val lengthBytes = lengthHex.toInt(16)
            val lengthChars = lengthBytes * 2  // Each byte = 2 hex chars
            
            // Read value
            val valueStart = lengthStart + 2
            if (valueStart + lengthChars > tlvString.length) {
                return null
            }
            
            return tlvString.substring(valueStart, valueStart + lengthChars)
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "Error extracting TLV value for tag $tag: ${e.message}")
            return null
        }
    }
    
    /**
     * Extract AID hex from TLV format string.
     * AIDs are returned as TLV with tag 9F06 containing the AID.
     * Format: 9F06[length][AID value]
     * Example: 9F0607A0000000031010 (tag=9F06, length=07=7 bytes, AID=A0000000031010)
     * 
     * @param tlvString TLV string in hex format
     * @return Extracted AID as hex string, or null if not found
     */
    fun extractAidFromTlv(tlvString: String): String? {
        try {
            // First try: Parse TLV to extract tag 9F06 (AID)
            val tlvBytes = hexToBytes(tlvString)
            val tlvMap = TLVUtil.buildTLVMap(tlvBytes)
            val aidTlv = tlvMap["9F06"]
            if (aidTlv != null && aidTlv.value.isNotEmpty()) {
                return aidTlv.value.uppercase()
            }
        } catch (e: Exception) {
            // TLV parsing failed, try manual extraction
        }
        
        // Fallback: Manual TLV parsing
        // Look for tag 9F06 in hex string
        val tagIndex = tlvString.indexOf("9F06")
        if (tagIndex >= 0 && tagIndex + 6 < tlvString.length) {
            try {
                // Read length byte (2 hex chars after "9F06")
                val lengthHex = tlvString.substring(tagIndex + 4, tagIndex + 6)
                val lengthBytes = lengthHex.toIntOrNull(16) ?: 0
                val lengthHexChars = lengthBytes * 2  // Each byte = 2 hex chars
                
                // Read AID value
                val aidStart = tagIndex + 6
                val aidEnd = aidStart + lengthHexChars
                if (aidEnd <= tlvString.length) {
                    val extracted = tlvString.substring(aidStart, aidEnd).uppercase()
                    if (extracted.startsWith("A0") && extracted.length >= 14) {
                        return extracted
                    }
                }
            } catch (e: Exception) {
                // Manual extraction failed
            }
        }
        
        // Last resort: Try to find AID pattern in string
        val aidPattern = Regex("A0[0-9A-F]{12,}")
        val match = aidPattern.find(tlvString.uppercase())
        return match?.value
    }
    
    /**
     * Convert hex string to byte array.
     */
    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace("\\s".toRegex(), "")
        return ByteArray(clean.length / 2) { i ->
            Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16).toByte()
        }
    }
}

