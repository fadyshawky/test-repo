package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ISO8583 Packer
 * 
 * Builds raw ISO8583 binary messages (MTI + bitmap + data elements)
 * 
 * Message Types:
 * - 0100: Authorization Request (Sale)
 * - 0400: Reversal Request
 */
public class Iso8583Packer {

    private static final String TAG = Constant.TAG;

    /**
     * Pack ISO8583 authorization request (0100)
     * 
     * @param pan            Primary Account Number (masked)
     * @param processingCode Processing Code (e.g., "000000" = Purchase)
     * @param amount         Transaction amount (minor currency units, e.g., "1000")
     * @param stan           Systems Trace Audit Number (6 digits, e.g., "123456")
     * @param posEntryMode   POS Entry Mode (3 digits, e.g., "051" = Chip+PIN)
     * @param currencyCode   Currency Code (3 digits, e.g., "818" = EGP)
     * @param field55        EMV Field 55 (ICC Data) - hex string
     * @param terminalId     Terminal ID
     * @param merchantId     Merchant ID
     * @param pinBlock       PIN block (8 bytes hex string, optional - only for
     *                       online PIN)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack0100(String pan, String processingCode, String amount,
            String stan, String posEntryMode, String currencyCode,
            String field55, String terminalId, String merchantId, String pinBlock) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 0100 (Authorization Request) ===");

            StringBuilder buffer = new StringBuilder();

            // MTI: 0100 = Authorization Request
            // MTI must be ASCII "0100" (4 bytes: 0x30, 0x31, 0x30, 0x30), not hex "0100"
            // We'll append it as hex representation of ASCII bytes
            buffer.append("30313030"); // ASCII "0100" in hex

            // Primary Bitmap (64 bits, binary)
            // Set bits for fields present: 2 (PAN, only if present), 3, 4, 11, 22, 49, 52
            // (PIN block), 55
            // Note: PIN block is encrypted under TPK, so we can only validate length (16
            // hex chars = 8 bytes)
            // Format validation (ISO 9564 Format 0/1) must be done after decryption by
            // backend
            boolean includePan = (pan != null && !pan.isEmpty());
            LogUtil.e(TAG, "  DE2 (PAN) check:");
            LogUtil.e(TAG, "    PAN value: '" + (pan != null ? pan : "null") + "'");
            LogUtil.e(TAG, "    PAN length: " + (pan != null ? pan.length() : 0));
            LogUtil.e(TAG, "    includePan: " + includePan);
            if (!includePan) {
                LogUtil.e(TAG, "  DE2 (PAN): Empty - excluding from bitmap and message");
                LogUtil.e(TAG, "    Note: PAN will be extracted from Field 55 (DE55) by backend");
            }
            boolean includePinBlock = false;
            if (pinBlock != null && !pinBlock.isEmpty() && pinBlock.length() == 16) {
                // PIN block is encrypted, so we can only check length
                // Format validation will be done by backend after decryption with TPK
                includePinBlock = true;
            }

            // Build fields array based on what's actually present
            List<Integer> fieldList = new ArrayList<>();
            if (includePan) {
                fieldList.add(2); // DE2: PAN (only if present)
            }
            fieldList.add(3); // DE3: Processing Code
            fieldList.add(4); // DE4: Amount
            fieldList.add(11); // DE11: STAN
            fieldList.add(22); // DE22: POS Entry Mode
            fieldList.add(49); // DE49: Currency Code
            if (includePinBlock) {
                fieldList.add(52); // DE52: PIN Block (only if present)
            }
            fieldList.add(55); // DE55: ICC Data (Field 55)

            LogUtil.e(TAG, "  Fields to include in bitmap: " + fieldList.toString());
            int[] fields = new int[fieldList.size()];
            for (int i = 0; i < fieldList.size(); i++) {
                fields[i] = fieldList.get(i);
            }
            String bitmap = buildBitmap(fields);
            LogUtil.e(TAG, "  Built bitmap (hex): " + bitmap);
            buffer.append(bitmap);

            // DE2: PAN (Primary Account Number) - only append if present
            if (includePan) {
                buffer.append(formatPan(pan));
            }

            // DE3: Processing Code (numeric, zero-padded to 6 digits)
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (processingCode != null && !processingCode.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(processingCode, 6)));
            } else {
                buffer.append(asciiToHex("000000")); // Default: 6 ASCII zeros
            }

            // DE4: Amount, Authorized (12 digits, right-justified, zero-filled)
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (amount != null && !amount.isEmpty()) {
                String paddedAmount = zeroPadNumeric(amount, 12);
                LogUtil.e(TAG, "  DE4 (Amount):");
                LogUtil.e(TAG, "    Input amount string: '" + amount + "'");
                LogUtil.e(TAG, "    Padded to 12 digits: '" + paddedAmount + "'");
                LogUtil.e(TAG, "    Amount in minor units: " + paddedAmount);
                // Verify: if amount is "20000" (200.00 EGP), padded should be "000000020000"
                try {
                    long amountLong = Long.parseLong(paddedAmount);
                    double amountMainUnit = amountLong / 100.0;
                    LogUtil.e(TAG, "    Amount in main unit: " + amountMainUnit + " EGP");
                } catch (NumberFormatException e) {
                    LogUtil.e(TAG, "    ⚠️ Failed to parse amount: " + e.getMessage());
                }
                buffer.append(asciiToHex(paddedAmount));
            } else {
                LogUtil.e(TAG, "  DE4 (Amount): Empty - using default zeros");
                buffer.append(asciiToHex("000000000000")); // Default: 12 ASCII zeros
            }

            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (stan != null && !stan.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(stan, 6)));
            } else {
                buffer.append(asciiToHex("000000")); // Default: 6 ASCII zeros
            }

            // DE22: POS Entry Mode - 3 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (posEntryMode != null && !posEntryMode.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(posEntryMode, 3)));
            } else {
                buffer.append(asciiToHex("000")); // Default: 3 ASCII zeros
            }

            // DE49: Currency Code, Transaction - 3 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (currencyCode != null && !currencyCode.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(currencyCode, 3)));
            } else {
                buffer.append(asciiToHex("818")); // Default: EGP
            }

            // DE52: PIN Block (8 bytes hex string, only for online PIN)
            // ISO 9564 Format 0 or Format 1 (8 bytes = 16 hex characters)
            // NOTE: PIN block is encrypted under TPK, so we cannot validate format here
            // The encrypted bytes will appear random. Format validation is done by backend
            // after decryption.
            if (includePinBlock) {
                // DE52 format: 8 bytes (16 hex characters) - fixed length, no length indicator
                buffer.append(pinBlock); // Already hex, use as-is (encrypted under TPK)
                LogUtil.e(TAG, "✓ DE52 (PIN Block) included - length: 8 bytes (encrypted under TPK)");
                LogUtil.e(TAG,
                        "  Note: PIN block is encrypted - format validation will be done by backend after decryption");
            } else if (pinBlock != null && !pinBlock.isEmpty()) {
                // PIN block provided but invalid length - log warning but don't include in
                // message
                LogUtil.e(TAG, "⚠️ PIN Block invalid length - not included in message:");
                LogUtil.e(TAG, "  Length: " + pinBlock.length() + " hex chars (expected 16 hex chars = 8 bytes)");
            }

            // DE55: ICC Data (EMV Field 55)
            // Format: LLL + hex data (where LLL is 2 bytes BCD length per MsgSpec v341)
            // MsgSpec v341: "LLLVAR field with length 15 will have length indicator
            // '0015h', occupying two bytes"
            // Line 1183: "Length Attribute n 3 2 0LLL - BCD length of data to follow,
            // maximum 255 bytes"
            // BCD encoding: Each decimal digit is encoded in a nibble (4 bits)
            // Example: length 100 = 0x01 0x00 (BCD) = hex string "0100"
            if (field55 != null && !field55.isEmpty()) {
                // Convert hex string length to bytes
                int byteLength = field55.length() / 2;
                if (byteLength > 255) {
                    byteLength = 255; // Max per spec
                }
                // Encode as 2 bytes BCD: thousands/hundreds in first byte, tens/ones in second
                // byte
                // For length 100: thousands=0, hundreds=1, tens=0, ones=0 → 0x01 0x00 → hex
                // "0100"
                int thousands = byteLength / 1000;
                int hundreds = (byteLength / 100) % 10;
                int tens = (byteLength / 10) % 10;
                int ones = byteLength % 10;
                byte bcdByte1 = (byte) ((thousands << 4) | hundreds);
                byte bcdByte2 = (byte) ((tens << 4) | ones);
                String lengthHex = String.format("%02X%02X", bcdByte1 & 0xFF, bcdByte2 & 0xFF);
                buffer.append(lengthHex);
                buffer.append(field55);
                LogUtil.e(TAG, "✓ DE55 (ICC Data) included - length: " + byteLength + " bytes");
            }

            // Convert hex string to bytes
            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);

            LogUtil.e(TAG, "✓ ISO8583 0100 packed - total length: " + isoFrame.length + " bytes");
            LogUtil.e(TAG, "  Hex: " + hexString.substring(0, Math.min(100, hexString.length())) + "...");

            // Detailed structure logging
            if (isoFrame.length >= 12) {
                // MTI (4 bytes ASCII)
                String mti = new String(isoFrame, 0, 4, StandardCharsets.US_ASCII);
                String mtiHex = bytesToHexString(isoFrame, 0, 4);
                LogUtil.e(TAG, "  MTI (ASCII): " + mti + " (hex: " + mtiHex + ")");

                // Bitmap (8 bytes binary)
                String bitmapHex = bytesToHexString(isoFrame, 4, 8);
                LogUtil.e(TAG, "  Primary Bitmap (binary, 8 bytes): " + bitmapHex);
                LogUtil.e(TAG, "  Primary Bitmap bytes: " + java.util.Arrays.toString(
                        java.util.Arrays.copyOfRange(isoFrame, 4, 12)));
            }

            return isoFrame;

        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Packing ISO8583 0100", e);
            return new byte[0];
        }
    }

    /**
     * Pack ISO8583 reversal request (0400)
     * 
     * @param rrn            Retrieval Reference Number (12 digits)
     * @param amount         Original transaction amount (minor currency units)
     * @param stan           Systems Trace Audit Number (6 digits)
     * @param currencyCode   Currency Code (3 digits, e.g., "818" = EGP)
     * @param terminalId     Terminal ID
     * @param merchantId     Merchant ID
     * @param reversalReason Reason for reversal (optional)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack0400(String rrn, String amount, String stan,
            String currencyCode, String terminalId,
            String merchantId, String reversalReason) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 0400 (Reversal Request) ===");
            LogUtil.e(TAG, "  RRN: " + rrn);

            StringBuilder buffer = new StringBuilder();

            // MTI: 0400 = Reversal Request
            // MTI must be ASCII "0400" (4 bytes: 0x30, 0x34, 0x30, 0x30), not hex "0400"
            // We'll append it as hex representation of ASCII bytes
            buffer.append("30343030"); // ASCII "0400" in hex

            // Primary Bitmap (64 bits, binary)
            // Set bits for fields present: 2, 3, 4, 11, 22, 37 (RRN), 49
            String bitmap = buildBitmap(new int[] { 2, 3, 4, 11, 22, 37, 49 });
            buffer.append(bitmap);

            // DE2: PAN (from original transaction - may be masked)
            // For reversal, we may not have full PAN - use placeholder
            buffer.append("0000000000000000"); // Placeholder PAN

            // DE3: Processing Code (000000 = Purchase)
            buffer.append("000000");

            // DE4: Amount, Authorized (12 digits, right-justified, zero-filled)
            if (amount != null && !amount.isEmpty()) {
                buffer.append(zeroPadNumeric(amount, 12));
            } else {
                buffer.append("000000000000");
            }

            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            if (stan != null && !stan.isEmpty()) {
                buffer.append(zeroPadNumeric(stan, 6));
            } else {
                // Generate STAN from current time (use seconds component)
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.US);
                String time = timeFormat.format(new Date());
                String seconds = time.substring(4, 6);
                buffer.append(zeroPadNumeric(seconds + "00", 6));
            }

            // DE22: POS Entry Mode (default to Chip+PIN: 051)
            buffer.append("051");

            // DE37: Retrieval Reference Number (RRN) - 12 digits
            if (rrn != null && !rrn.isEmpty()) {
                buffer.append(zeroPadNumeric(rrn, 12));
            } else {
                buffer.append("000000000000");
            }

            // DE49: Currency Code, Transaction - 3 digits
            if (currencyCode != null && !currencyCode.isEmpty()) {
                buffer.append(zeroPadNumeric(currencyCode, 3));
            } else {
                buffer.append("818"); // Default EGP
            }

            // Convert hex string to bytes
            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);

            LogUtil.e(TAG, "✓ ISO8583 0400 packed - total length: " + isoFrame.length + " bytes");
            LogUtil.e(TAG, "  Hex: " + hexString.substring(0, Math.min(100, hexString.length())) + "...");

            return isoFrame;

        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Packing ISO8583 0400", e);
            return new byte[0];
        }
    }

    /**
     * Zero-pad a numeric string on the left to the given width.
     * Non-digit characters are stripped; if parsing fails, returns zeros.
     * Returns the numeric string as-is (will be converted to ASCII hex in the
     * buffer).
     * 
     * Note: This returns a decimal string (e.g., "000000"), which will be converted
     * to ASCII hex representation (e.g., "303030303030") when building the message.
     */
    private static String zeroPadNumeric(String value, int width) {
        if (value == null) {
            return String.format(Locale.US, "%0" + width + "d", 0);
        }

        String digits = value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return String.format(Locale.US, "%0" + width + "d", 0);
        }

        // If longer than width, keep the rightmost digits
        if (digits.length() > width) {
            digits = digits.substring(digits.length() - width);
        }

        long numeric;
        try {
            numeric = Long.parseLong(digits);
        } catch (NumberFormatException e) {
            numeric = 0L;
        }

        return String.format(Locale.US, "%0" + width + "d", numeric);
    }

    /**
     * Convert a numeric ASCII string to its hex representation.
     * Example: "000000" -> "303030303030" (ASCII "000000" in hex)
     * 
     * @param asciiString ASCII string of digits
     * @return Hex string representation
     */
    private static String asciiToHex(String asciiString) {
        if (asciiString == null) {
            return "";
        }
        StringBuilder hex = new StringBuilder();
        for (char c : asciiString.toCharArray()) {
            hex.append(String.format("%02X", (int) c));
        }
        return hex.toString();
    }

    /**
     * Build primary bitmap (64 bits)
     * 
     * @param fieldNumbers Array of field numbers present (e.g., [2, 3, 4, 11, 22,
     *                     49, 55])
     * @return Hex string representation of bitmap (16 hex chars = 64 bits)
     */
    private static String buildBitmap(int[] fieldNumbers) {
        long bitmap = 0L;

        for (int fieldNum : fieldNumbers) {
            if (fieldNum > 0 && fieldNum <= 64) {
                // Set bit (fieldNum - 1) to 1 (fields are 1-indexed)
                bitmap |= (1L << (64 - fieldNum));
            }
        }

        // Convert to 16 hex characters (64 bits = 16 hex chars)
        return String.format("%016X", bitmap);
    }

    /**
     * Format PAN (Primary Account Number)
     * ISO8583 DE2 format: LL + PAN (where LL is 1 byte BCD length per MsgSpec v341)
     * MsgSpec v341: "LLVAR field with length 15 will have length indicator '15h'
     * occupying one byte"
     * BCD encoding: Each decimal digit is encoded in a nibble (4 bits)
     * Example: length 16 = 0x16 (BCD) = hex string "16"
     * PAN itself is ASCII encoded (each digit is ASCII byte)
     * 
     * @param pan PAN (may be masked, e.g., "557607******9549")
     * @return Formatted PAN string (hex representation: BCD length byte + ASCII PAN
     *         in hex)
     */
    private static String formatPan(String pan) {
        // Remove masking characters for packing
        String cleanPan = pan.replaceAll("[*]", "");

        // LL format: 1 byte BCD length (e.g., length 16 = 0x16 in BCD)
        // BCD: high nibble = tens digit, low nibble = ones digit
        int length = cleanPan.length();
        if (length > 19) {
            length = 19; // Max per ISO 8583
        }
        // Encode length as BCD: "16" means byte 0x16 (BCD 16), not hex 0x10
        // For length 16: tens=1, ones=6 → 0x16 → hex string "16"
        int tens = length / 10;
        int ones = length % 10;
        byte bcdByte = (byte) ((tens << 4) | ones);
        String lengthHex = String.format("%02X", bcdByte & 0xFF);

        // PAN is ASCII encoded (each digit is an ASCII byte)
        // Convert PAN digits to ASCII hex representation
        String panHex = asciiToHex(cleanPan);

        return lengthHex + panHex;
    }

    /**
     * Convert hex string to bytes
     * 
     * @param hexString Hex string (e.g., "0100" = bytes [0x01, 0x00])
     * @return Byte array
     */
    private static byte[] hexStringToBytes(String hexString) {
        try {
            // Remove any whitespace
            hexString = hexString.replaceAll("\\s", "");

            // Ensure even length
            if (hexString.length() % 2 != 0) {
                hexString = "0" + hexString;
            }

            byte[] bytes = new byte[hexString.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                int index = i * 2;
                bytes[i] = (byte) Integer.parseInt(hexString.substring(index, index + 2), 16);
            }

            return bytes;
        } catch (Exception e) {
            LogUtil.e(TAG, "Error converting hex to bytes: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Convert bytes to hex string for logging
     * 
     * @param bytes  Byte array
     * @param offset Start offset
     * @param length Number of bytes to convert
     * @return Hex string representation
     */
    private static String bytesToHexString(byte[] bytes, int offset, int length) {
        StringBuilder hex = new StringBuilder();
        for (int i = offset; i < offset + length && i < bytes.length; i++) {
            hex.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return hex.toString();
    }
}
