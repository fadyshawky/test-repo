package com.neo.neopayplus.emv

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2

/**
 * EMV TLV Utilities for building and encoding TLV data structures.
 */
object EmvTlvUtils {
    private const val TAG = "EmvTlvUtils"

    /**
     * Convert hex string to byte array
     */
    fun hex(s: String): ByteArray {
        val clean = s.replace("\\s".toRegex(), "")
        require(clean.length % 2 == 0) { "Invalid hex length" }
        return ByteArray(clean.length / 2) { i ->
            Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16).toByte()
        }
    }

    /**
     * Convert byte array to hex string
     */
    fun toHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02X".format(it) }
    }

    /**
     * Encode length as 1+ bytes per BER-TLV rules (short length if <128)
     */
    private fun encodeLength(len: Int): ByteArray {
        return when {
            len < 0x80 -> byteArrayOf(len.toByte())
            len <= 0xFF -> byteArrayOf(0x81.toByte(), len.toByte())
            len <= 0xFFFF -> byteArrayOf(0x82.toByte(), ((len ushr 8) and 0xFF).toByte(), (len and 0xFF).toByte())
            else -> throw IllegalArgumentException("TLV length too large")
        }
    }

    /**
     * Build a TLV structure from tag and value hex strings
     * @param tagHex Tag in hex format (e.g., "9F1A", "5F2A")
     * @param valueHex Value in hex format
     * @return Complete TLV as byte array
     */
    fun makeTlv(tagHex: String, valueHex: String): ByteArray {
        val tag = hex(tagHex)
        val value = hex(valueHex)
        val lenEnc = encodeLength(value.size)
        return tag + lenEnc + value
    }

    /**
     * Concatenate multiple byte arrays
     */
    fun concat(vararg parts: ByteArray): ByteArray {
        val total = parts.sumOf { it.size }
        val out = ByteArray(total)
        var pos = 0
        for (p in parts) {
            System.arraycopy(p, 0, out, pos, p.size)
            pos += p.size
        }
        return out
    }
}

/**
 * EMV Terminal Configuration for Retail POS (Profile 1)
 * 
 * Uses Sunmi Pay SDK V2 Bundle keys (NOT TLV packing).
 * Keys are from SUNMI PAY SDK V2 Development Document v3.3.15.
 */
object EmvTerminalConfig {
    private const val TAG = "EmvTerminalConfig"

    /**
     * Configure Sunmi Terminal Parameters for Retail POS (Egypt)
     * 
     * Uses the correct Sunmi SDK V2 Bundle keys - no TLV packing needed.
     * Sunmi's EMV kernel builds its own TLV table internally.
     *
     * @param emvOptV2 EMVOptV2 from SunmiPayKernel instance (must be connected)
     */
    @JvmStatic
    fun configureSunmiTerminalParamsRetail(emvOptV2: EMVOptV2) {
        val b = Bundle()
        
        // --- Mandatory EMV parameters ---
        b.putString("countryCode", "0818")         // Egypt (9F1A)
        b.putString("currencyCode", "0818")        // EGP (5F2A)
        b.putString("termType", "21")              // Attended POS (9F35)
        b.putString("capability", "60F8C8")        // Terminal Capabilities (9F33)
        b.putString("addCapability", "6000F0A001") // Additional Capabilities (9F40) - 5 bytes
        b.putString("transCurrExp", "02")          // EGP has 2 decimals
        b.putString("referCurrExp", "02")
        b.putString("referCurrCode", "0818")       // Reference currency (9F3C)
        
        // Transaction type (Purchase)
        b.putString("transType", "00")             // (9C)
        
        // --- Merchant / Acceptor ---
        b.putString("merchName", "NEOPAY")
        b.putString("termId", "12345678")
        b.putString("merchId", "000000000000001")
        b.putString("acquirerId", "00000012345")
        @Suppress("DEPRECATION", "HardwareIds")
        b.putString("ifdSerialNumber", "SUNMI-${Build.SERIAL}")
        
        // --- Contactless parameters ---
        // TTQ (9F66) should be configured centrally via PaymentConfig/EmvKernelConfig.
        // Do NOT override TTQ here; leave it to kernel/brand configuration.
        // CVM Limits from PaymentConfig - DF8124 = No PIN required below this amount
        b.putString("udol", "")                           // UDoL (rarely needed)
        b.putString("noCvmLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8124)  // 600.00 EGP - no PIN below this
        b.putString("cvmLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8125)    // Max contactless limit
        b.putString("contactlessFloorLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8123) // Floor limit
        
        // --- Apply ---
        try {
            emvOptV2.setTermParamEx(b)
            Log.i(TAG, "✓ Terminal parameters configured successfully (Retail Profile)")
        } catch (e: Exception) {
            Log.e(TAG, "setTermParamEx failed: ${e.message}")
            throw RuntimeException("setTermParamEx failed: ${e.message}", e)
        }
    }

    /**
     * Configure terminal parameters via reflection (for Any type)
     */
    @JvmStatic
    fun configureSunmiTerminalParamsRetailReflective(emvOpt: Any) {
        val b = Bundle()
        
        // --- Mandatory EMV parameters ---
        b.putString("countryCode", "0818")
        b.putString("currencyCode", "0818")
        b.putString("termType", "21")
        b.putString("capability", "60F8C8")
        b.putString("addCapability", "6000F0A001")
        b.putString("transCurrExp", "02")
        b.putString("referCurrExp", "02")
        b.putString("referCurrCode", "0818")
        b.putString("transType", "00")
        
        // --- Merchant / Acceptor ---
        b.putString("merchName", "NEOPAY")
        b.putString("termId", "12345678")
        b.putString("merchId", "000000000000001")
        b.putString("acquirerId", "00000012345")
        @Suppress("DEPRECATION", "HardwareIds")
        b.putString("ifdSerialNumber", "SUNMI-${Build.SERIAL}")
        
        // --- Contactless parameters ---
        // TTQ (9F66) configured centrally; do not override here.
        // CVM Limits from PaymentConfig - DF8124 = No PIN required below this amount
        b.putString("udol", "")
        b.putString("noCvmLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8124)  // 600.00 EGP - no PIN below this
        b.putString("cvmLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8125)    // Max contactless limit
        b.putString("contactlessFloorLimit", com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8123) // Floor limit
        
        // --- Apply via reflection ---
        try {
            val setMethod = emvOpt.javaClass.getMethod("setTermParamEx", Bundle::class.java)
            val rc = setMethod.invoke(emvOpt, b) as? Int ?: -1
            if (rc != 0) {
                Log.e(TAG, "setTermParamEx failed rc=$rc")
                throw RuntimeException("setTermParamEx failed rc=$rc")
            }
            Log.i(TAG, "✓ Terminal parameters configured successfully (Retail Profile)")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to call setTermParamEx: ${t.message}")
            throw RuntimeException("Failed to configure terminal params: ${t.message}", t)
        }
    }

    // Scheme-specific TTQ overrides have been removed; TTQ configuration is centralized
    // in PaymentConfig/EmvKernelConfig/EmvProvisioner and not changed during the flow.
}

