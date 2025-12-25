package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * ISO 8583 Response Parser
 * 
 * Parses ISO 8583 response messages:
 * - 0110: Authorization Response
 * - 1814: Network Management Response (PowerCARD)
 * Extracts fields: MTI, Response Code (DE39), Auth Code (DE38), RRN (DE37),
 * Function Code (DE24), Action Code (DE39), etc.
 */
public class Iso8583ResponseParser {

    private static final String TAG = Constant.TAG;

    /**
     * Parse ISO 8583 response message (0110 or 1210)
     * 
     * @param applicationData Application data (MTI + Bitmap + Data Elements)
     * @return Parsed response data
     */
    public static ParsedResponse parse0110(byte[] applicationData) {
        return parseResponse(applicationData, "0110");
    }

    /**
     * Parse ISO 8583 response message (1210)
     * 
     * @param applicationData Application data (MTI + Bitmap + Data Elements)
     * @return Parsed response data
     */
    public static ParsedResponse parse1210(byte[] applicationData) {
        return parseResponse(applicationData, "1210");
    }

    /**
     * Parse ISO 8583 response message (generic - works for 0110, 1210, etc.)
     * 
     * @param applicationData Application data (MTI + Bitmap + Data Elements)
     * @param expectedMti Expected MTI for logging (e.g., "0110", "1210")
     * @return Parsed response data
     */
    private static ParsedResponse parseResponse(byte[] applicationData, String expectedMti) {
        if (applicationData == null || applicationData.length < 12) {
            LogUtil.e(TAG,
                    "✗ Invalid application data length: " + (applicationData != null ? applicationData.length : 0));
            return null;
        }

        try {
            LogUtil.e(TAG, "=== Parsing ISO 8583 Response (" + expectedMti + ") ===");
            LogUtil.e(TAG, "  Application data length: " + applicationData.length + " bytes");

            ParsedResponse response = new ParsedResponse();
            int offset = 0;

            // Parse MTI (4 bytes = 4 ASCII digits)
            if (applicationData.length < offset + 4) {
                LogUtil.e(TAG, "✗ Insufficient data for MTI");
                return null;
            }
            byte[] mtiBytes = new byte[4];
            System.arraycopy(applicationData, offset, mtiBytes, 0, 4);
            response.mti = new String(mtiBytes);
            offset += 4;
            LogUtil.e(TAG, "  MTI: " + response.mti);

            // Parse Primary Bitmap (8 bytes = 64 bits)
            if (applicationData.length < offset + 8) {
                LogUtil.e(TAG, "✗ Insufficient data for bitmap");
                return null;
            }
            byte[] bitmap = new byte[8];
            System.arraycopy(applicationData, offset, bitmap, 0, 8);
            offset += 8;

            // Parse bitmap to determine which fields are present
            boolean[] fieldsPresent = parseBitmap(bitmap);

            // Parse data elements based on bitmap
            // Common fields in response:
            // DE2: PAN (not needed)
            // DE3: Processing Code (not needed)
            // DE4: Amount (not needed)
            // DE11: STAN (not needed)
            // DE37: RRN (Retrieval Reference Number) - 12 digits
            // DE38: Auth Code (Authorization Identification Response) - 6 digits
            // DE39: Response Code - 2 digits
            // DE55: ICC Data (EMV response tags) - variable length

            // DE37: RRN (field 37)
            if (fieldsPresent[36]) { // Field 37 (0-indexed: 36)
                if (applicationData.length >= offset + 12) {
                    response.rrn = parseField37(applicationData, offset);
                    offset += 12; // RRN is 12 digits (ASCII)
                    LogUtil.e(TAG, "  RRN: " + response.rrn);
                } else {
                    LogUtil.e(TAG, "  ⚠️ Insufficient data for DE37 (RRN)");
                }
            }

            // DE38: Auth Code (field 38) - only present if approved
            if (fieldsPresent[37]) { // Field 38 (0-indexed: 37)
                if (applicationData.length >= offset + 6) {
                    response.authCode = parseField38(applicationData, offset);
                    offset += 6; // Auth code is 6 digits (ASCII)
                    LogUtil.e(TAG, "  Auth Code: " + response.authCode);
                } else {
                    LogUtil.e(TAG, "  ⚠️ Insufficient data for DE38 (Auth Code)");
                }
            }

            // DE39: Response Code (field 39) - always present
            if (fieldsPresent[38]) { // Field 39 (0-indexed: 38)
                if (applicationData.length >= offset + 2) {
                    response.responseCode = parseField39(applicationData, offset);
                    offset += 2; // Response code is 2 digits (ASCII)
                    LogUtil.e(TAG, "  Response Code: " + response.responseCode);
                } else {
                    LogUtil.e(TAG, "  ⚠️ Insufficient data for DE39 (Response Code)");
                }
            } else {
                LogUtil.e(TAG, "  ⚠️ DE39 (Response Code) not present in bitmap");
            }

            // DE55: ICC Data (field 55) - EMV response tags
            if (fieldsPresent[54]) { // Field 55 (0-indexed: 54)
                response.field55 = parseField55(applicationData, offset);
                if (response.field55 != null) {
                    // Field 55 is variable length (LL + data)
                    int length = (applicationData[offset] & 0xFF) * 256 + (applicationData[offset + 1] & 0xFF);
                    offset += 2 + length; // LL (2 bytes) + data
                    LogUtil.e(TAG, "  Field 55 length: " + length + " bytes");
                }
            }

            response.approved = "00".equals(response.responseCode);

            LogUtil.e(TAG, "✓ Response parsed successfully");
            LogUtil.e(TAG, "  Approved: " + response.approved);

            return response;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error parsing ISO 8583 response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parse bitmap to determine which fields are present
     */
    private static boolean[] parseBitmap(byte[] bitmap) {
        boolean[] fields = new boolean[64];
        for (int i = 0; i < 8; i++) {
            byte b = bitmap[i];
            for (int j = 0; j < 8; j++) {
                int fieldIndex = i * 8 + j;
                if (fieldIndex < 64) {
                    fields[fieldIndex] = ((b >> (7 - j)) & 0x01) == 1;
                }
            }
        }
        return fields;
    }

    /**
     * Parse DE37: RRN (Retrieval Reference Number) - 12 digits (ASCII)
     * Per MsgSpec v341, numeric fields are ASCII encoded
     */
    private static String parseField37(byte[] data, int offset) {
        if (data.length < offset + 12) {
            return null;
        }
        // RRN is 12 ASCII digits
        return new String(data, offset, 12);
    }

    /**
     * Parse DE38: Auth Code (Authorization Identification Response) - 6 digits
     * (ASCII)
     * Per MsgSpec v341, numeric fields are ASCII encoded
     */
    private static String parseField38(byte[] data, int offset) {
        if (data.length < offset + 6) {
            return null;
        }
        // Auth Code is 6 ASCII digits
        return new String(data, offset, 6);
    }

    /**
     * Parse DE39: Response Code - 2 digits (ASCII)
     * Per MsgSpec v341, numeric fields are ASCII encoded
     */
    private static String parseField39(byte[] data, int offset) {
        if (data.length < offset + 2) {
            return null;
        }
        // Response Code is 2 ASCII digits
        return new String(data, offset, 2);
    }

    /**
     * Parse DE55: ICC Data (EMV Field 55) - variable length (LL + hex data)
     */
    private static String parseField55(byte[] data, int offset) {
        if (data.length < offset + 2) {
            return null;
        }

        // Read length (2 bytes, big-endian)
        int length = (data[offset] & 0xFF) * 256 + (data[offset + 1] & 0xFF);

        if (data.length < offset + 2 + length) {
            LogUtil.e(TAG, "✗ Insufficient data for Field 55: need " + (2 + length) + " bytes, have "
                    + (data.length - offset));
            return null;
        }

        // Convert hex data to string
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < length; i++) {
            hex.append(String.format("%02X", data[offset + 2 + i]));
        }

        return hex.toString();
    }

    /**
     * Parse ISO 8583 Network Management Response (1814) - PowerCARD
     * 
     * @param applicationData Application data (MTI + Bitmap + Data Elements)
     * @return Parsed network management response data
     */
    public static NetworkManagementResponse parse1814(byte[] applicationData) {
        if (applicationData == null || applicationData.length < 12) {
            LogUtil.e(TAG,
                    "✗ Invalid application data length: " + (applicationData != null ? applicationData.length : 0));
            return null;
        }

        try {
            LogUtil.e(TAG, "=== Parsing ISO 8583 Network Management Response (1814) ===");
            LogUtil.e(TAG, "  Application data length: " + applicationData.length + " bytes");

            NetworkManagementResponse response = new NetworkManagementResponse();
            int offset = 0;

            // Parse MTI (4 bytes = 4 ASCII digits)
            if (applicationData.length < offset + 4) {
                LogUtil.e(TAG, "✗ Insufficient data for MTI");
                return null;
            }
            byte[] mtiBytes = new byte[4];
            System.arraycopy(applicationData, offset, mtiBytes, 0, 4);
            response.mti = new String(mtiBytes);
            offset += 4;
            LogUtil.e(TAG, "  MTI: " + response.mti);

            // Parse Primary Bitmap (8 bytes = 64 bits)
            if (applicationData.length < offset + 8) {
                LogUtil.e(TAG, "✗ Insufficient data for bitmap");
                return null;
            }
            byte[] bitmap = new byte[8];
            System.arraycopy(applicationData, offset, bitmap, 0, 8);
            offset += 8;

            // Check if secondary bitmap is present (bit 1 of primary bitmap)
            boolean hasSecondaryBitmap = ((bitmap[0] >> 7) & 0x01) == 1;
            if (hasSecondaryBitmap) {
                // Skip secondary bitmap (8 bytes)
                if (applicationData.length < offset + 8) {
                    LogUtil.e(TAG, "✗ Insufficient data for secondary bitmap");
                    return null;
                }
                offset += 8;
            }

            // Parse bitmap to determine which fields are present
            boolean[] fieldsPresent = parseBitmap(bitmap);

            // DE24: Function Code (field 24) - 3 digits
            if (fieldsPresent[23]) { // Field 24 (0-indexed: 23)
                if (applicationData.length >= offset + 3) {
                    response.functionCode = new String(applicationData, offset, 3);
                    offset += 3;
                    LogUtil.e(TAG, "  Function Code: " + response.functionCode);
                }
            }

            // DE39: Action Code (field 39) - 3 digits (response code for network management)
            if (fieldsPresent[38]) { // Field 39 (0-indexed: 38)
                if (applicationData.length >= offset + 3) {
                    response.actionCode = new String(applicationData, offset, 3);
                    offset += 3;
                    LogUtil.e(TAG, "  Action Code: " + response.actionCode);
                } else {
                    LogUtil.e(TAG, "  ⚠️ Insufficient data for DE39 (Action Code)");
                }
            } else {
                LogUtil.e(TAG, "  ⚠️ DE39 (Action Code) not present in bitmap");
            }

            // DE37: Retrieval Reference Number (field 37) - 12 digits
            if (fieldsPresent[36]) { // Field 37 (0-indexed: 36)
                if (applicationData.length >= offset + 12) {
                    response.rrn = new String(applicationData, offset, 12);
                    offset += 12;
                    LogUtil.e(TAG, "  RRN: " + response.rrn);
                }
            }

            // Success if action code is 800 (per PowerCARD spec)
            response.success = "800".equals(response.actionCode);

            LogUtil.e(TAG, "✓ Network Management Response parsed successfully");
            LogUtil.e(TAG, "  Success: " + response.success);

            return response;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error parsing Network Management Response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parsed response data structure
     */
    public static class ParsedResponse {
        public String mti; // Message Type Identifier (e.g., "0110")
        public String responseCode; // DE39: Response Code (e.g., "00" = approved)
        public String authCode; // DE38: Authorization Code
        public String rrn; // DE37: Retrieval Reference Number
        public String field55; // DE55: ICC Data (EMV response tags)
        public boolean approved; // true if responseCode == "00"
    }

    /**
     * Network Management Response data structure
     */
    public static class NetworkManagementResponse {
        public String mti; // Message Type Identifier (e.g., "1814")
        public String functionCode; // DE24: Function Code (e.g., "801", "802", "803")
        public String actionCode; // DE39: Action Code (e.g., "800" = success)
        public String rrn; // DE37: Retrieval Reference Number
        public boolean success; // true if actionCode == "800"
    }
}
