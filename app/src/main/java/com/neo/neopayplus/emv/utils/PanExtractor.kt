package com.neo.neopayplus.emv.utils

import android.util.Log
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLV
import com.neo.neopayplus.emv.TLVUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.payservice.AidlConstantsV2

/**
 * Extracts PAN (Primary Account Number) from EMV TLV data.
 * 
 * Follows Single Responsibility Principle - only handles PAN extraction.
 * 
 * For contactless transactions, tries brand-specific TLV opcodes first:
 * - OP_PAYWAVE for Visa (A000000003)
 * - OP_PAYPASS for Mastercard (A000000004/5)
 * - OP_NORMAL as fallback
 * 
 * @param emvOptV2 EMV service instance
 */
class PanExtractor(private val emvOptV2: EMVOptV2) {
    
    companion object {
        private const val TAG = Constant.TAG
        
        // TLV tags that may contain PAN
        private val PAN_TAGS = arrayOf("5A", "57", "9F6B")
        
        // AID tags to determine brand
        private val AID_TAGS = arrayOf("4F", "9F06")
    }
    
    /**
     * Extract PAN from TLV tags
     * 
     * For contactless transactions, tries brand-specific opcodes first:
     * 1. Read AID to determine brand
     * 2. Try brand-specific opcode (OP_PAYWAVE for Visa, OP_PAYPASS for Mastercard)
     * 3. Fall back to OP_NORMAL
     * 
     * Priority order for tags:
     * 1. Tag 5A (PAN) - Primary Account Number
     * 2. Tag 57 (Track 2 Equivalent Data) - Contains PAN
     * 3. Tag 9F6B (Contactless Track 2 Data) - Contactless PAN
     * 
     * @return PAN if found, null otherwise
     */
    fun extractPan(): String? {
        return try {
            // First, try to determine brand from AID for contactless transactions
            val aid = readAid()
            val brandOpCode = determineBrandOpCode(aid)
            
            // Try brand-specific opcode first (if available), then OP_NORMAL
            val opCodesToTry = if (brandOpCode != null) {
                listOf(brandOpCode, AidlConstants.EMV.TLVOpCode.OP_NORMAL)
            } else {
                listOf(AidlConstants.EMV.TLVOpCode.OP_NORMAL)
            }
            
            // Try each opcode until we find PAN
            for (opCode in opCodesToTry) {
                val pan = tryExtractPanWithOpCode(opCode)
                if (pan != null) {
                    return pan
                }
            }
            
            Log.e(TAG, "No PAN found in any TLV opcode space")
            null
                
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting PAN from TLV: ${e.message}")
            null
        }
    }
    
    /**
     * Read AID (Application Identifier) from EMV kernel
     * @return AID string if found, null otherwise
     */
    private fun readAid(): String? {
        return try {
            val aidOut = ByteArray(256)
            val aidLen = emvOptV2.getTlvList(
                AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                AID_TAGS,
                aidOut
            )
            
            if (aidLen > 0) {
                val aidTlvMap = TLVUtil.buildTLVMap(aidOut.copyOf(aidLen))
                val aidTlv = aidTlvMap["4F"] ?: aidTlvMap["9F06"]
                aidTlv?.value
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read AID: ${e.message}")
            null
        }
    }
    
    /**
     * Determine brand-specific TLV opcode based on AID
     * @param aid Application Identifier
     * @return Brand-specific opcode if detected, null otherwise
     */
    private fun determineBrandOpCode(aid: String?): Int? {
        if (aid == null || aid.isEmpty()) {
            return null
        }
        
        return when {
            aid.startsWith("A000000004") || aid.startsWith("A000000005") -> {
                // Mastercard - use PayPass opcode
                AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS
            }
            aid.startsWith("A000000003") -> {
                // Visa - use PayWave opcode
                AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE
            }
            else -> null
        }
    }
    
    /**
     * Try to extract PAN using a specific TLV opcode
     * @param opCode TLV opcode to use
     * @return PAN if found, null otherwise
     */
    private fun tryExtractPanWithOpCode(opCode: Int): String? {
        return try {
            val outData = ByteArray(512)
            val len = emvOptV2.getTlvList(opCode, PAN_TAGS, outData)
            
            if (len <= 0) {
                return null
            }
            
            val tlvMap = TLVUtil.buildTLVMap(outData.copyOf(len))
            
            // Try tag 5A first (PAN)
            extractFromTag5A(tlvMap) 
                ?: extractFromTag57(tlvMap) 
                ?: extractFromTag9F6B(tlvMap)
                
        } catch (e: Exception) {
            // Silently fail and try next opcode
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
     * Return PAN exactly as extracted from EMV tag - no masking, no truncation
     */
    private fun extractFromTag57(tlvMap: Map<String, TLV>): String? {
        val tlv = tlvMap["57"]
        if (tlv != null && tlv.value != null) {
            val track2Hex = tlv.value.uppercase()
            val delimiterIndex = track2Hex.indexOf('D')
            if (delimiterIndex > 0) {
                // Extract PAN part (before 'D' delimiter) and remove ONLY trailing 'F' padding
                val pan = track2Hex.substring(0, delimiterIndex).replace(Regex("F+$"), "")
                Log.e(TAG, "✓ PAN extracted from tag 57 (full, unmasked): $pan (length: ${pan.length})")
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
