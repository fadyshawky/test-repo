package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * PowerCARD Protocol Utilities
 * 
 * Implements PowerCARD protocol identification and message length prefix
 * per SWI-ID1135-SID-Protocol.docx
 */
public class PowerCardProtocol {

    private static final String TAG = Constant.TAG;

    // Protocol Identification (3-character ISO protocol identification)
    private static final String PROTOCOL_ID = "ISO"; // Standard ISO 8583 protocol

    /**
     * Build protocol identification (3 bytes ASCII)
     * 
     * @param protocolId Protocol identification (default: "ISO")
     * @return Protocol ID as byte array (3 bytes)
     */
    public static byte[] buildProtocolIdentification(String protocolId) {
        if (protocolId == null || protocolId.isEmpty()) {
            protocolId = PROTOCOL_ID;
        }
        String padded = String.format("%-3s", protocolId).substring(0, 3);
        return padded.getBytes();
    }

    /**
     * Build protocol identification with default value
     * 
     * @return Protocol ID as byte array (3 bytes)
     */
    public static byte[] buildProtocolIdentification() {
        return buildProtocolIdentification(PROTOCOL_ID);
    }

    /**
     * Parse protocol identification from message
     * 
     * @param data Byte array containing protocol ID at offset 0
     * @return Protocol identification string
     */
    public static String parseProtocolIdentification(byte[] data) {
        if (data == null || data.length < 3) {
            throw new IllegalArgumentException(
                    "Protocol ID too short: " + (data != null ? data.length : 0) + " bytes (expected 3)");
        }
        return new String(data, 0, 3).trim();
    }

    /**
     * Build message length prefix (4-character right-justified ASCII, zero-padded)
     * 
     * @param messageLength Length of the message (excluding the length prefix itself)
     * @return Length prefix as byte array (4 bytes ASCII)
     */
    public static byte[] buildMessageLengthPrefix(int messageLength) {
        if (messageLength < 0 || messageLength > 9999) {
            throw new IllegalArgumentException("Message length out of range: " + messageLength + " (max 9999)");
        }
        String lengthStr = String.format("%04d", messageLength);
        return lengthStr.getBytes();
    }

    /**
     * Parse message length prefix from message
     * 
     * @param data Byte array containing length prefix at offset 0
     * @return Message length (excluding the length prefix)
     */
    public static int parseMessageLengthPrefix(byte[] data) {
        if (data == null || data.length < 4) {
            throw new IllegalArgumentException(
                    "Length prefix too short: " + (data != null ? data.length : 0) + " bytes (expected 4)");
        }
        String lengthStr = new String(data, 0, 4);
        try {
            return Integer.parseInt(lengthStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid length prefix: " + lengthStr, e);
        }
    }

    /**
     * Calculate complete message length (excluding length prefix)
     * Structure: Protocol ID (3) + PowerCARD Header (8) + TPDU (5) + Application Data + CRC (2)
     * 
     * @param applicationDataLength Length of application data
     * @return Total message length (excluding length prefix)
     */
    public static int calculateMessageLength(int applicationDataLength) {
        final int PROTOCOL_ID_LENGTH = 3;
        final int POWERCARD_HEADER_LENGTH = 8;
        final int TPDU_LENGTH = 5;
        final int CRC_LENGTH = 2;

        return PROTOCOL_ID_LENGTH + POWERCARD_HEADER_LENGTH + TPDU_LENGTH + applicationDataLength + CRC_LENGTH;
    }
}

