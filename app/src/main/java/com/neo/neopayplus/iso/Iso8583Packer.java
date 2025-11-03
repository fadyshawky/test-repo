package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
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
     * @param pan Primary Account Number (masked)
     * @param processingCode Processing Code (e.g., "000000" = Purchase)
     * @param amount Transaction amount (minor currency units, e.g., "1000")
     * @param stan Systems Trace Audit Number (6 digits, e.g., "123456")
     * @param posEntryMode POS Entry Mode (3 digits, e.g., "051" = Chip+PIN)
     * @param currencyCode Currency Code (3 digits, e.g., "818" = EGP)
     * @param field55 EMV Field 55 (ICC Data) - hex string
     * @param terminalId Terminal ID
     * @param merchantId Merchant ID
     * @param pinBlock PIN block (8 bytes hex string, optional - only for online PIN)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack0100(String pan, String processingCode, String amount, 
                                  String stan, String posEntryMode, String currencyCode,
                                  String field55, String terminalId, String merchantId, String pinBlock) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 0100 (Authorization Request) ===");
            
            StringBuilder buffer = new StringBuilder();
            
            // MTI: 0100 = Authorization Request
            buffer.append("0100");
            
            // Primary Bitmap (64 bits, binary)
            // Set bits for fields present: 2, 3, 4, 11, 22, 49, 52 (PIN block), 55
            int[] fields = {2, 3, 4, 11, 22, 49, 55};
            if (pinBlock != null && !pinBlock.isEmpty()) {
                // Include DE52 (PIN block) if provided
                fields = new int[]{2, 3, 4, 11, 22, 49, 52, 55};
            }
            String bitmap = buildBitmap(fields);
            buffer.append(bitmap);
            
            // DE2: PAN (Primary Account Number)
            if (pan != null && !pan.isEmpty()) {
                buffer.append(formatPan(pan));
            }
            
            // DE3: Processing Code
            if (processingCode != null && !processingCode.isEmpty()) {
                buffer.append(String.format("%06s", processingCode));
            }
            
            // DE4: Amount, Authorized (12 digits, right-justified, zero-filled)
            if (amount != null && !amount.isEmpty()) {
                buffer.append(String.format("%012s", amount));
            }
            
            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            if (stan != null && !stan.isEmpty()) {
                buffer.append(String.format("%06s", stan));
            }
            
            // DE22: POS Entry Mode - 3 digits
            if (posEntryMode != null && !posEntryMode.isEmpty()) {
                buffer.append(String.format("%03s", posEntryMode));
            }
            
            // DE49: Currency Code, Transaction - 3 digits
            if (currencyCode != null && !currencyCode.isEmpty()) {
                buffer.append(String.format("%03s", currencyCode));
            }
            
            // DE52: PIN Block (8 bytes hex string, only for online PIN)
            if (pinBlock != null && !pinBlock.isEmpty()) {
                // DE52 format: 8 bytes (16 hex characters)
                if (pinBlock.length() == 16) {
                    buffer.append(pinBlock); // Already hex, use as-is
                    LogUtil.e(TAG, "✓ DE52 (PIN Block) included - length: 8 bytes");
                } else {
                    LogUtil.e(TAG, "⚠️ PIN Block length invalid: " + pinBlock.length() + " (expected 16 hex chars)");
                }
            }
            
            // DE55: ICC Data (EMV Field 55)
            // Format: LL + hex data (where LL is 2-digit length in hex)
            if (field55 != null && !field55.isEmpty()) {
                // Convert hex string length to bytes
                int byteLength = field55.length() / 2;
                // Format as LL (2 hex digits for length)
                String lengthHex = String.format("%02X", byteLength);
                buffer.append(lengthHex);
                buffer.append(field55);
                LogUtil.e(TAG, "✓ DE55 (ICC Data) included - length: " + byteLength + " bytes");
            }
            
            // Convert hex string to bytes
            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);
            
            LogUtil.e(TAG, "✓ ISO8583 0100 packed - total length: " + isoFrame.length + " bytes");
            LogUtil.e(TAG, "  Hex: " + hexString.substring(0, Math.min(100, hexString.length())) + "...");
            
            return isoFrame;
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error packing ISO8583 0100: " + e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    /**
     * Pack ISO8583 reversal request (0400)
     * 
     * @param rrn Retrieval Reference Number (12 digits)
     * @param amount Original transaction amount (minor currency units)
     * @param stan Systems Trace Audit Number (6 digits)
     * @param currencyCode Currency Code (3 digits, e.g., "818" = EGP)
     * @param terminalId Terminal ID
     * @param merchantId Merchant ID
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
            buffer.append("0400");
            
            // Primary Bitmap (64 bits, binary)
            // Set bits for fields present: 2, 3, 4, 11, 22, 37 (RRN), 49
            String bitmap = buildBitmap(new int[]{2, 3, 4, 11, 22, 37, 49});
            buffer.append(bitmap);
            
            // DE2: PAN (from original transaction - may be masked)
            // For reversal, we may not have full PAN - use placeholder
            buffer.append("0000000000000000"); // Placeholder PAN
            
            // DE3: Processing Code (000000 = Purchase)
            buffer.append("000000");
            
            // DE4: Amount, Authorized (12 digits, right-justified, zero-filled)
            if (amount != null && !amount.isEmpty()) {
                buffer.append(String.format("%012s", amount));
            } else {
                buffer.append("000000000000");
            }
            
            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            if (stan != null && !stan.isEmpty()) {
                buffer.append(String.format("%06s", stan));
            } else {
                // Generate STAN from current time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.US);
                String time = timeFormat.format(new Date());
                String seconds = time.substring(4, 6);
                buffer.append(String.format("%06s", seconds + "00"));
            }
            
            // DE22: POS Entry Mode (default to Chip+PIN: 051)
            buffer.append("051");
            
            // DE37: Retrieval Reference Number (RRN) - 12 digits
            if (rrn != null && !rrn.isEmpty()) {
                buffer.append(String.format("%012s", rrn));
            } else {
                buffer.append("000000000000");
            }
            
            // DE49: Currency Code, Transaction - 3 digits
            if (currencyCode != null && !currencyCode.isEmpty()) {
                buffer.append(String.format("%03s", currencyCode));
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
            LogUtil.e(TAG, "❌ Error packing ISO8583 0400: " + e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
    
    /**
     * Build primary bitmap (64 bits)
     * 
     * @param fieldNumbers Array of field numbers present (e.g., [2, 3, 4, 11, 22, 49, 55])
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
     * ISO8583 DE2 format: LL + PAN (where LL is 2-digit length in BCD)
     * 
     * @param pan PAN (may be masked, e.g., "557607******9549")
     * @return Formatted PAN string
     */
    private static String formatPan(String pan) {
        // Remove masking characters for packing
        String cleanPan = pan.replaceAll("[*]", "");
        
        // LL format: length in hex (2 digits)
        int length = cleanPan.length();
        String lengthHex = String.format("%02X", length);
        
        return lengthHex + cleanPan;
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
}

