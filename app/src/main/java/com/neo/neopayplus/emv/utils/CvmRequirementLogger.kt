package com.neo.neopayplus.emv.utils

import android.util.Log
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLV
import com.neo.neopayplus.emv.TLVUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2

/**
 * Logs card's CVM (Cardholder Verification Method) requirements for debugging.
 * 
 * Follows Single Responsibility Principle - only handles CVM logging.
 * 
 * @param emvOptV2 EMV service instance
 */
class CvmRequirementLogger(private val emvOptV2: EMVOptV2) {
    
    companion object {
        private const val TAG = Constant.TAG
    }
    
    /**
     * Log all CVM-related information from the card
     */
    fun logCardCvmRequirements() {
        try {
            Log.e(TAG, "=== CARD CVM REQUIREMENTS DEBUG ===")
            
            logCtq()
            logCvmList()
            logCvmResults()
            
            Log.e(TAG, "=== END CARD CVM DEBUG ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error logging card CVM requirements: ${e.message}")
        }
    }
    
    /**
     * Log CTQ (9F6C) - Card Transaction Qualifiers for PayPass
     */
    private fun logCtq() {
        try {
            val outData = ByteArray(512)
            val len = emvOptV2.getTlvList(
                com.sunmi.pay.hardware.aidl.AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                arrayOf("9F6C"),
                outData
            )
            
            if (len > 0) {
                val tlvMap = TLVUtil.buildTLVMap(outData.copyOf(len))
                val ctqTlv = tlvMap["9F6C"]
                
                if (ctqTlv != null && ctqTlv.value != null) {
                    val ctq = ctqTlv.value
                    Log.e(TAG, "  CTQ (9F6C - Card Transaction Qualifiers): $ctq")
                    parseCtqBytes(ctq)
                } else {
                    Log.e(TAG, "  CTQ (9F6C): Not present")
                }
            } else {
                Log.e(TAG, "  CTQ (9F6C): Not present")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CTQ: ${e.message}")
        }
    }
    
    /**
     * Parse and log CTQ bytes
     */
    private fun parseCtqBytes(ctq: String) {
        if (ctq.length >= 2) {
            val byte1 = ctq.substring(0, 2).toInt(16)
            if ((byte1 and 0x80) != 0) Log.e(TAG, "    → Bit 8: ONLINE PIN REQUIRED ⚠️")
            if ((byte1 and 0x40) != 0) Log.e(TAG, "    → Bit 7: SIGNATURE REQUIRED")
            if ((byte1 and 0x20) != 0) Log.e(TAG, "    → Bit 6: Go Online if ODA Fails")
            if ((byte1 and 0x10) != 0) Log.e(TAG, "    → Bit 5: Switch Interface for Cash Transactions")
            if ((byte1 and 0x08) != 0) Log.e(TAG, "    → Bit 4: Switch Interface for Cashback Transactions")
            if ((byte1 and 0x02) != 0) Log.e(TAG, "    → Bit 2: Consumer Device CVM Performed")
            if ((byte1 and 0x01) != 0) Log.e(TAG, "    → Bit 1: Card supports CDCVM")
        }
        
        if (ctq.length >= 4) {
            val byte2 = ctq.substring(2, 4).toInt(16)
            if ((byte2 and 0x80) != 0) {
                Log.e(TAG, "    → Byte2 Bit 8: Online PIN Required (Issuer Update Processing)")
            }
        }
    }
    
    /**
     * Log CVM List (8E) - Cardholder Verification Method List
     */
    private fun logCvmList() {
        try {
            val outData = ByteArray(512)
            val len = emvOptV2.getTlvList(
                com.sunmi.pay.hardware.aidl.AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                arrayOf("8E"),
                outData
            )
            
            if (len > 0) {
                val tlvMap = TLVUtil.buildTLVMap(outData.copyOf(len))
                val cvmListTlv = tlvMap["8E"]
                
                if (cvmListTlv != null && cvmListTlv.value != null) {
                    val cvmList = cvmListTlv.value
                    Log.e(TAG, "  CVM List (8E): $cvmList")
                    parseCvmList(cvmList)
                } else {
                    Log.e(TAG, "  CVM List (8E): Not present")
                }
            } else {
                Log.e(TAG, "  CVM List (8E): Not present")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CVM List: ${e.message}")
        }
    }
    
    /**
     * Parse and log CVM List rules
     */
    private fun parseCvmList(cvmList: String) {
        if (cvmList.length >= 16) {
            val xAmount = cvmList.substring(0, 8)
            val yAmount = cvmList.substring(8, 16)
            Log.e(TAG, "    X Amount (CVM threshold): $xAmount")
            Log.e(TAG, "    Y Amount (secondary threshold): $yAmount")
            
            // Parse CVM rules (2 bytes each, starting at byte 9)
            var i = 16
            while (i + 4 <= cvmList.length) {
                val rule = cvmList.substring(i, i + 4)
                val cvmCode = rule.substring(0, 2).toInt(16)
                val cvmCondition = rule.substring(2, 4).toInt(16)
                val cvmType = parseCvmCode(cvmCode)
                val cvmCond = parseCvmCondition(cvmCondition)
                Log.e(TAG, "    Rule: $rule → $cvmType if $cvmCond")
                i += 4
            }
        }
    }
    
    /**
     * Log CVM Results (9F34)
     */
    private fun logCvmResults() {
        try {
            val outData = ByteArray(512)
            val len = emvOptV2.getTlvList(
                com.sunmi.pay.hardware.aidl.AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                arrayOf("9F34"),
                outData
            )
            
            if (len > 0) {
                val tlvMap = TLVUtil.buildTLVMap(outData.copyOf(len))
                val cvmResultsTlv = tlvMap["9F34"]
                if (cvmResultsTlv != null) {
                    Log.e(TAG, "  CVM Results (9F34): ${cvmResultsTlv.value}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CVM Results: ${e.message}")
        }
    }
    
    /**
     * Parse CVM code to human-readable string
     */
    private fun parseCvmCode(code: Int): String {
        val method = code and 0x3F // Lower 6 bits
        return when (method) {
            0x00 -> "Fail CVM processing"
            0x01 -> "Plaintext PIN by ICC"
            0x02 -> "Enciphered PIN online"
            0x03 -> "Plaintext PIN + Signature"
            0x04 -> "Enciphered PIN by ICC"
            0x05 -> "Enciphered PIN + Signature"
            0x1E -> "Signature (paper)"
            0x1F -> "No CVM required"
            0x20 -> "CDCVM (Consumer Device CVM)"
            else -> "Unknown CVM (${String.format("%02X", method)})"
        }
    }
    
    /**
     * Parse CVM condition to human-readable string
     */
    private fun parseCvmCondition(condition: Int): String {
        return when (condition) {
            0x00 -> "Always"
            0x01 -> "If unattended cash"
            0x02 -> "If not unattended cash and not manual cash and not purchase with cashback"
            0x03 -> "If terminal supports the CVM"
            0x04 -> "If manual cash"
            0x05 -> "If purchase with cashback"
            0x06 -> "If transaction is in application currency and is under X value"
            0x07 -> "If transaction is in application currency and is over X value"
            0x08 -> "If transaction is in application currency and is under Y value"
            0x09 -> "If transaction is in application currency and is over Y value"
            else -> "Unknown condition (${String.format("%02X", condition)})"
        }
    }
}
