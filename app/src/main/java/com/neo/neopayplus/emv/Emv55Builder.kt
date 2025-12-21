package com.neo.neopayplus.emv

import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.utils.LogUtil
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.config.EmvBrandConfig
import com.sunmi.payservice.AidlConstantsV2
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import java.util.Locale

/**
 * Build EMV Field 55 (DE55) as binary TLV.
 *
 * - Pulls the standard online authorization tag set from the EMV kernel.
 * - Assembles them in canonical order.
 * - Falls back to POS-supplied amount/currency if missing from kernel.
 * - PAN tags (5A, 57, 9F6B) are included UNMASKED for bank communication.
 *   PAN is read directly from EMV kernel and not masked in Field 55.
 */
object Emv55Builder {
    
    // Brand-specific tag lists for simpler buildForBrand() method
    // Note: 9F6E (POS Entry Mode) is added separately if not provided by kernel
    // PAN tags (5A, 57, 9F6B) are included for bank communication in Field 55
    // Expiry date (59) and Application Expiration Date (5F24) are included for bank communication in Field 55
    // TVR (95) and TSI (9B) are terminal-generated tags required for receipts and bank communication
    // Track 2 data (57, 9F6B) also contains expiry date as fallback
    private val VISA_TAGS = arrayOf("9F26","9F27","9F10","9F37","9F36","95","9A","9C","9F02","5F2A","82","9F1A","9F03","9F33","9F34","9F35","9F1E","84","9F09","9F41","9F6E","9B","5A","57","9F6B","59","5F24")
    private val MC_TAGS   = arrayOf("9F26","9F27","9F10","9F37","9F36","95","9A","9C","9F02","5F2A","82","9F1A","9F03","9F33","9F34","9F35","9F1E","84","9F09","9F6E","9B","5A","57","9F6B","59","5F24")
    private val MEEZA_TAGS= arrayOf("9F26","9F27","9F10","9F37","9F36","95","9A","9C","9F02","5F2A","82","9F1A","9F03","9F33","9F34","9F35","9F1E","84","9F09","9F41","9F6E","9B","5A","57","9F6B","59","5F24")

    /**
     * Build Field 55 for a specific brand using brand-specific tag lists and TLVOpCode.
     * This is a simpler version that directly reads from the kernel using the appropriate TLVOpCode.
     * 
     * @param emv EMVOptV2 instance
     * @param brand Brand profile
     * @param isContactless Whether this is a contactless transaction
     * @param hasPin Whether PIN was entered (for 9F6E generation if not provided by kernel)
     * @return Field 55 as ByteArray
     */
    @JvmStatic
    @JvmOverloads
    fun buildForBrand(emv: EMVOptV2, brand: EmvBrandConfig.BrandProfile, isContactless: Boolean = false, hasPin: Boolean = false): ByteArray {
        val tags = when (brand.name) {
            "VISA" -> VISA_TAGS
            "MASTERCARD" -> MC_TAGS
            "MEEZA" -> MEEZA_TAGS
            else -> VISA_TAGS
        }
        
        // Use the correct TLVOpCode based on card type and brand (like SDK demo)
        val tlvOpCode = if (isContactless) {
            when (brand.name) {
                "MASTERCARD" -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS
                "VISA" -> AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE
                else -> AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
            }
        } else {
            AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL
        }
        
        val out = ByteArray(4096)
        val len = emv.getTlvList(tlvOpCode, tags, out)
        
        if (len <= 0) {
            LogUtil.e(Constant.TAG, "⚠️ No tags returned from EMV kernel")
            return ByteArray(0)
        }
        
        // Parse the TLV data to check if 9F6E is present
        val tlvData = out.copyOf(len)
        val tlvMap = TLVUtil.buildTLVMap(tlvData)
        val has9F6E = tlvMap.containsKey("9F6E")
        
        LogUtil.e(Constant.TAG, "=== Building Field 55 (DE55) ===")
        LogUtil.e(Constant.TAG, "Extracted ${tlvMap.size} tags from EMV kernel")
        LogUtil.e(Constant.TAG, "Tag 9F6E present: $has9F6E (contactless=$isContactless, hasPin=$hasPin)")
        
        // Verify PAN tags are present and unmasked (for bank communication)
        val pan5A = tlvMap["5A"]?.value
        val pan57 = tlvMap["57"]?.value
        val pan9F6B = tlvMap["9F6B"]?.value
        if (pan5A != null && pan5A.isNotEmpty()) {
            // PAN in tag 5A is BCD encoded, remove trailing 'F' padding for display
            val panDisplay = pan5A.replace(Regex("F+$"), "")
            val maskedPan = if (panDisplay.length >= 10) {
                "${panDisplay.take(6)}****${panDisplay.takeLast(4)}"
            } else {
                "****"
            }
            LogUtil.e(Constant.TAG, "✓ PAN tag 5A present in Field 55 (unmasked for backend): $maskedPan")
        } else if (pan57 != null && pan57.isNotEmpty()) {
            LogUtil.e(Constant.TAG, "✓ PAN tag 57 (Track 2) present in Field 55 (unmasked for backend)")
        } else if (pan9F6B != null && pan9F6B.isNotEmpty()) {
            LogUtil.e(Constant.TAG, "✓ PAN tag 9F6B (Contactless Track 2) present in Field 55 (unmasked for backend)")
        } else {
            LogUtil.e(Constant.TAG, "⚠️ No PAN tags (5A, 57, 9F6B) found in Field 55")
        }
        
        // Verify expiry date is present (for bank communication)
        // Priority: tag 59 -> tag 5F24 -> Track 2 (57/9F6B)
        var expiryFound = false
        val expiry59 = tlvMap["59"]?.value
        if (expiry59 != null && expiry59.isNotEmpty()) {
            val expiryDisplay = expiry59.replace(Regex("F+$"), "")
            if (expiryDisplay.length >= 4) {
                val yy = expiryDisplay.substring(0, 2)
                val mm = expiryDisplay.substring(2, 4)
                LogUtil.e(Constant.TAG, "✓ Expiry date tag 59 present in Field 55 (unmasked for backend): $mm/$yy (hex: $expiry59)")
                expiryFound = true
            }
        }
        
        // Fallback to tag 5F24
        if (!expiryFound) {
            val expiry5F24 = tlvMap["5F24"]?.value
            if (expiry5F24 != null && expiry5F24.isNotEmpty()) {
                val expiryDisplay = expiry5F24.replace(Regex("F+$"), "")
                if (expiryDisplay.length >= 4) {
                    val yy = expiryDisplay.substring(0, 2)
                    val mm = expiryDisplay.substring(2, 4)
                    LogUtil.e(Constant.TAG, "✓ Expiry date tag 5F24 present in Field 55 (unmasked for backend): $mm/$yy (hex: $expiry5F24)")
                    expiryFound = true
                }
            }
        }
        
        // Fallback to Track 2 data (tag 57 or 9F6B)
        if (!expiryFound) {
            val track257 = tlvMap["57"]?.value
            if (track257 != null && track257.isNotEmpty()) {
                val track2Hex = track257.uppercase()
                val delimiterIndex = track2Hex.indexOf('D')
                if (delimiterIndex > 0 && track2Hex.length > delimiterIndex + 4) {
                    val expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5)
                    val expiry = expiryHex.replace(Regex("F+$"), "")
                    if (expiry.length >= 4) {
                        val yy = expiry.substring(0, 2)
                        val mm = expiry.substring(2, 4)
                        LogUtil.e(Constant.TAG, "✓ Expiry date extracted from Track 2 (57) in Field 55 (unmasked for backend): $mm/$yy")
                        expiryFound = true
                    }
                }
            }
            
            if (!expiryFound) {
                val track29F6B = tlvMap["9F6B"]?.value
                if (track29F6B != null && track29F6B.isNotEmpty()) {
                    val track2Hex = track29F6B.uppercase()
                    val delimiterIndex = track2Hex.indexOf('D')
                    if (delimiterIndex > 0 && track2Hex.length > delimiterIndex + 4) {
                        val expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5)
                        val expiry = expiryHex.replace(Regex("F+$"), "")
                        if (expiry.length >= 4) {
                            val yy = expiry.substring(0, 2)
                            val mm = expiry.substring(2, 4)
                            LogUtil.e(Constant.TAG, "✓ Expiry date extracted from Track 2 (9F6B) in Field 55 (unmasked for backend): $mm/$yy")
                            expiryFound = true
                        }
                    }
                }
            }
        }
        
        if (!expiryFound) {
            LogUtil.e(Constant.TAG, "⚠️ Expiry date not found in Field 55 (tried tags 59, 5F24, 57, 9F6B)")
        }
        
        // If 9F6E is missing, generate it and append to Field 55
        if (!has9F6E) {
            LogUtil.e(Constant.TAG, "⚠️ Tag 9F6E (POS Entry Mode) not provided by kernel - generating based on transaction type")
            val posEntryModeHex = generate9F6E(isContactless, hasPin)
            val tag9F6E = encodeTlv("9F6E", posEntryModeHex)
            
            // Append 9F6E to the existing TLV data
            val newLen = len + tag9F6E.size
            val newData = ByteArray(newLen)
            System.arraycopy(tlvData, 0, newData, 0, len)
            System.arraycopy(tag9F6E, 0, newData, len, tag9F6E.size)
            
            val posEntryModeStr = when {
                isContactless && hasPin -> "071"
                isContactless && !hasPin -> "072"
                !isContactless && hasPin -> "051"
                else -> "021"
            }
            LogUtil.e(Constant.TAG, "✓ Added tag 9F6E (POS Entry Mode): $posEntryModeStr (hex: $posEntryModeHex, ${tag9F6E.size} bytes)")
            LogUtil.e(Constant.TAG, "✓ Field 55 (DE55) built - total length: ${newData.size} bytes")
            return newData
        } else {
            val tag9F6EValue = tlvMap["9F6E"]?.getValue() ?: ""
            LogUtil.e(Constant.TAG, "✓ Tag 9F6E (POS Entry Mode) provided by kernel: $tag9F6EValue")
            LogUtil.e(Constant.TAG, "✓ Field 55 (DE55) built - total length: ${tlvData.size} bytes")
            return tlvData
        }
    }
    
    /**
     * Generate tag 9F6E (POS Entry Mode) value based on transaction type.
     * Format: 3 bytes (6 hex chars) representing POS Entry Mode
     * - "071" = Contactless with PIN (0x07, 0x10, 0x00)
     * - "072" = Contactless without PIN (0x07, 0x20, 0x00)
     * - "051" = Chip (contact) with PIN (0x05, 0x10, 0x00)
     * - "021" = Chip (contact) without PIN (0x02, 0x10, 0x00)
     * 
     * @param isContactless Whether this is a contactless transaction
     * @param hasPin Whether PIN was entered
     * @return Hex string representation of 9F6E value (6 hex chars = 3 bytes)
     */
    private fun generate9F6E(isContactless: Boolean, hasPin: Boolean): String {
        val posEntryMode = when {
            isContactless && hasPin -> "071"  // Contactless + PIN
            isContactless && !hasPin -> "072" // Contactless, no PIN
            !isContactless && hasPin -> "051" // Chip + PIN
            else -> "021"                      // Chip, no PIN
        }
        
        // Convert "071" to hex: 0x07, 0x10, 0x00 (BCD encoding)
        // First byte: entry mode (07 for contactless, 05 for chip, 02 for chip no PIN)
        // Second byte: PIN entry capability (10 = PIN entered, 20 = no PIN)
        // Third byte: reserved (00)
        val byte1 = when {
            isContactless -> 0x07
            hasPin -> 0x05
            else -> 0x02
        }
        val byte2 = if (hasPin) 0x10 else 0x20
        val byte3 = 0x00
        
        return String.format(Locale.US, "%02X%02X%02X", byte1, byte2, byte3)
    }

    // Canonical order (Visa/MC common superset)
    // PAN tags (5A, 57, 9F6B) are included for bank communication in Field 55
    private val REQUIRED_ORDER = listOf(
        "9F26","9F27","9F10","9F37","9F36","95","9A","9C","9F02","5F2A",
        "82","9F1A","9F03","9F33","9F34","9F35","9F1E","84","9F09","9F41"
    )

    // Useful optionals to include when present
    // PAN tags (5A, 57, 9F6B) are included for bank communication
    // Expiry date (59) is included for bank communication
    private val OPTIONAL_ORDER = listOf("5F34","9F6E","9F12","50","9F4E","9B","5A","57","9F6B","59")

    /**
     * @param emv EMVOptV2 from Sunmi PayLib
     * @param fallbackAmountMinor If kernel doesn't provide 9F02, use this (minor units, e.g. "5000")
     * @param fallbackCurrencyCode 3-digit numeric (e.g. "818") -> converted to 5F2A
     * @param includeOptionals include optional tags if kernel exposes them
     * @return Field 55 as binary ByteArray (ready for ISO8583 DE55)
     */
    fun buildDE55(
        emv: EMVOptV2,
        fallbackAmountMinor: String? = null,
        fallbackCurrencyCode: String? = null,
        includeOptionals: Boolean = true
    ): ByteArray {
        // Ask kernel for everything at once (faster, fewer binder calls)
        val allTags = (REQUIRED_ORDER + OPTIONAL_ORDER).distinct().toTypedArray()
        val out = ByteArray(4096)
        val len = emv.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, allTags, out)
        val tlvMapJava: Map<String, com.neo.neopayplus.emv.TLV> = if (len > 0) {
            TLVUtil.buildTLVMap(out.copyOf(len))
        } else {
            emptyMap()
        }

        LogUtil.e(Constant.TAG, "=== Building Field 55 (DE55) ===")
        LogUtil.e(Constant.TAG, "Extracted ${tlvMapJava.size} tags from EMV kernel")

        val pieces = ArrayList<ByteArray>(REQUIRED_ORDER.size + OPTIONAL_ORDER.size)

        // Build in required order (skipping tags truly not available, except 9F02/5F2A—fallback provided)
        REQUIRED_ORDER.forEach { tag ->
            val tlv = tlvMapJava[tag]
            val valueHex = tlv?.value
                ?: when (tag) {
                    "9F02" -> fallbackFor9F02(fallbackAmountMinor)
                    "5F2A" -> fallbackFor5F2A(fallbackCurrencyCode)
                    else -> null
                }

            if (!valueHex.isNullOrEmpty()) {
                val tlvBytes = encodeTlv(tag, valueHex)
                pieces.add(tlvBytes)
                LogUtil.e(Constant.TAG, "✓ DE55: Added tag $tag (${tlvBytes.size} bytes)")
            } else {
                // Most kernels provide these; log if missing so you can see why DE55 was short
                LogUtil.e(Constant.TAG, "⚠️ DE55: missing tag $tag (no kernel value${if (tag == "9F02" || tag == "5F2A") " and no fallback" else ""})")
            }
        }

        if (includeOptionals) {
            OPTIONAL_ORDER.forEach { tag ->
                val tlv = tlvMapJava[tag]
                val valueHex = tlv?.value
                if (!valueHex.isNullOrEmpty()) {
                    val tlvBytes = encodeTlv(tag, valueHex)
                    pieces.add(tlvBytes)
                    LogUtil.e(Constant.TAG, "✓ DE55: Added optional tag $tag (${tlvBytes.size} bytes)")
                }
            }
        }

        // Concatenate into a single DE55 buffer
        val totalLen = pieces.sumOf { it.size }
        val result = ByteArray(totalLen)
        var pos = 0
        for (p in pieces) {
            System.arraycopy(p, 0, result, pos, p.size)
            pos += p.size
        }

        LogUtil.e(Constant.TAG, "✓ Field 55 (DE55) built - total length: ${result.size} bytes")
        LogUtil.e(Constant.TAG, "  Hex preview: ${ByteUtil.bytes2HexStr(result, 0, minOf(64, result.size))}...")

        return result
    }

    // --- helpers ---

    private fun fallbackFor9F02(fallbackAmountMinor: String?): String? {
        // 9F02 is 6 bytes BCD (12 digits)
        if (fallbackAmountMinor.isNullOrEmpty()) return null
        val digits = fallbackAmountMinor.trim().filter { it.isDigit() }
        if (digits.isEmpty()) return null
        val padded = digits.padStart(12, '0')
        return bcdHexFromDigits(padded) // hex string for 6 BCD bytes
    }

    private fun fallbackFor5F2A(fallbackCurrencyNumeric: String?): String? {
        // 5F2A is 2 bytes numeric BCD (ISO 4217)
        if (fallbackCurrencyNumeric.isNullOrEmpty()) return null
        val digits = fallbackCurrencyNumeric.trim()
        if (!digits.matches(Regex("\\d{3}"))) return null
        val padded = digits.padStart(4, '0')  // 3-digit → pad to 4 digits for 2 BCD bytes (e.g., "0818")
        return bcdHexFromDigits(padded)
    }

    private fun bcdHexFromDigits(digits: String): String {
        // Turn "000000005000" into hex of BCD bytes
        val clean = if (digits.length % 2 == 0) digits else "0$digits"
        val sb = StringBuilder(clean.length)

        // Each two digits become one BCD byte; output hex string of that byte
        var i = 0
        while (i < clean.length) {
            val hi = clean[i].digitToInt()
            val lo = clean[i + 1].digitToInt()
            val b = ((hi shl 4) or lo) and 0xFF
            sb.append(String.format(Locale.US, "%02X", b))
            i += 2
        }

        return sb.toString()
    }

    /** Encode one TLV (Tag|Len|Value) where tag & value are hex strings, len is DER short/long form. */
    private fun encodeTlv(tagHex: String, valueHex: String): ByteArray {
        val tagBytes = ByteUtil.hexStr2Bytes(tagHex)
        val valueBytes = ByteUtil.hexStr2Bytes(valueHex)
        val lenBytes = encodeLength(valueBytes.size)

        val out = ByteArray(tagBytes.size + lenBytes.size + valueBytes.size)
        System.arraycopy(tagBytes, 0, out, 0, tagBytes.size)
        System.arraycopy(lenBytes, 0, out, tagBytes.size, lenBytes.size)
        System.arraycopy(valueBytes, 0, out, tagBytes.size + lenBytes.size, valueBytes.size)

        return out
    }

    /** EMV/BER-TLV definite length encoding. */
    private fun encodeLength(length: Int): ByteArray {
        require(length >= 0)
        return when {
            length < 0x80 -> byteArrayOf(length.toByte())
            length <= 0xFF -> byteArrayOf(0x81.toByte(), length.toByte())
            length <= 0xFFFF -> byteArrayOf(
                0x82.toByte(),
                ((length shr 8) and 0xFF).toByte(),
                (length and 0xFF).toByte()
            )
            else -> throw IllegalArgumentException("Length too large: $length")
        }
    }
}

