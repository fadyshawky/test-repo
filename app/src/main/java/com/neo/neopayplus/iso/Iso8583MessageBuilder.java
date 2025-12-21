package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.utils.LogUtil;

/**
 * ISO 8583 Message Builder (MsgSpec v341)
 * 
 * Builds complete ISO 8583 messages with:
 * - Header (ADR + CB + TPDU)
 * - Application Data (MTI + Bitmap + Data Elements)
 * - CRC (HDLC checksum)
 */
public class Iso8583MessageBuilder {

    private static final String TAG = Constant.TAG;

    // TPDU constants per MsgSpec v341
    private static final byte TPDU_ID_TRANSACTION = 0x60; // Transactions
    private static final byte TPDU_ID_NMS = 0x68; // NMS/TNMS

    // HDLC constants
    private static final byte ADR_DEFAULT = 0x30; // HDLC poll address (normally 30h)
    private static final byte CB_DEFAULT = 0x00; // HDLC control byte

    /**
     * Build complete ISO 8583 message with header and CRC
     * 
     * @param applicationData    Application data (MTI + Bitmap + Data Elements)
     *                           from Iso8583Packer
     * @param destinationAddress Destination address (2 bytes, Network International
     *                           Identifier)
     * @param originatorAddress  Originator address (2 bytes, Terminal ID)
     * @return Complete ISO 8583 message with header and CRC
     */
    public static byte[] buildCompleteMessage(byte[] applicationData,
            byte[] destinationAddress,
            byte[] originatorAddress) {
        try {
            LogUtil.e(TAG, "=== Building ISO 8583 Message (MsgSpec v341) ===");

            // Build TPDU (5 bytes)
            byte[] tpdu = buildTpdu(TPDU_ID_TRANSACTION, destinationAddress, originatorAddress);

            // Build header (ADR + CB + TPDU)
            byte[] header = buildHeader(tpdu);

            // Calculate CRC (HDLC checksum) for header + application data
            byte[] dataForCrc = new byte[header.length + applicationData.length];
            System.arraycopy(header, 0, dataForCrc, 0, header.length);
            System.arraycopy(applicationData, 0, dataForCrc, header.length, applicationData.length);

            byte[] crc = calculateHdlcCrc(dataForCrc);

            // Build complete message: Header + Application Data + CRC
            byte[] completeMessage = new byte[header.length + applicationData.length + crc.length];
            System.arraycopy(header, 0, completeMessage, 0, header.length);
            System.arraycopy(applicationData, 0, completeMessage, header.length, applicationData.length);
            System.arraycopy(crc, 0, completeMessage, header.length + applicationData.length, crc.length);

            LogUtil.e(TAG, "✓ Complete message built:");
            LogUtil.e(TAG, "  Header: " + header.length + " bytes");
            LogUtil.e(TAG, "  Application Data: " + applicationData.length + " bytes");
            LogUtil.e(TAG, "  CRC: " + crc.length + " bytes");
            LogUtil.e(TAG, "  Total: " + completeMessage.length + " bytes");

            return completeMessage;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error building ISO 8583 message: " + e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Build TPDU (Transport Protocol Data Unit) - 5 bytes
     * Per MsgSpec v341 section 4.1.1.1:
     * - Byte 1: TPDU Id (60h for transactions)
     * - Bytes 2-3: Destination Address (Network International Identifier)
     * - Bytes 4-5: Originator Address (Terminal identifier)
     */
    private static byte[] buildTpdu(byte tpduId, byte[] destinationAddress, byte[] originatorAddress) {
        byte[] tpdu = new byte[5];
        tpdu[0] = tpduId;

        // Destination Address (2 bytes)
        if (destinationAddress != null && destinationAddress.length >= 2) {
            tpdu[1] = destinationAddress[0];
            tpdu[2] = destinationAddress[1];
        } else {
            // Default: use network identifier (e.g., 0x0000)
            tpdu[1] = 0x00;
            tpdu[2] = 0x00;
        }

        // Originator Address (2 bytes) - Terminal ID
        if (originatorAddress != null && originatorAddress.length >= 2) {
            tpdu[3] = originatorAddress[0];
            tpdu[4] = originatorAddress[1];
        } else {
            // Default: use terminal ID from config (convert to 2 bytes)
            String terminalId = PaymentConfig.getTerminalId();
            byte[] terminalBytes = terminalIdToBytes(terminalId);
            tpdu[3] = terminalBytes[0];
            tpdu[4] = terminalBytes[1];
        }

        return tpdu;
    }

    /**
     * Build header: ADR (1 byte) + CB (1 byte) + TPDU (5 bytes)
     */
    private static byte[] buildHeader(byte[] tpdu) {
        byte[] header = new byte[7]; // ADR (1) + CB (1) + TPDU (5)
        header[0] = ADR_DEFAULT; // HDLC poll address
        header[1] = CB_DEFAULT; // HDLC control byte
        System.arraycopy(tpdu, 0, header, 2, 5); // TPDU
        return header;
    }

    /**
     * Calculate HDLC CRC (CCITT CRC-16) per MsgSpec v341 section 4.3
     * 
     * @param data Data to calculate CRC for
     * @return CRC as 2 bytes (little-endian)
     */
    private static byte[] calculateHdlcCrc(byte[] data) {
        int crc = 0xFFFF; // Initial value

        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ 0x8408; // CCITT polynomial
                } else {
                    crc = crc >> 1;
                }
            }
        }

        crc = ~crc; // Invert

        // Return as 2 bytes (little-endian)
        return new byte[] { (byte) (crc & 0xFF), (byte) ((crc >> 8) & 0xFF) };
    }

    /**
     * Convert terminal ID string to 2 bytes
     * Terminal ID is typically 8 digits, we'll use last 4 digits as 2 bytes
     */
    private static byte[] terminalIdToBytes(String terminalId) {
        if (terminalId == null || terminalId.isEmpty()) {
            return new byte[] { 0x00, 0x01 };
        }

        // Extract numeric part
        String digits = terminalId.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return new byte[] { 0x00, 0x01 };
        }

        // Use last 4 digits, pad if needed
        if (digits.length() > 4) {
            digits = digits.substring(digits.length() - 4);
        } else {
            digits = String.format("%04d", Integer.parseInt(digits));
        }

        // Convert to 2 bytes (BCD)
        int value = Integer.parseInt(digits);
        return new byte[] {
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * Parse ISO 8583 response message
     * Extracts application data from complete message (removes header and CRC)
     * 
     * @param completeMessage Complete message with header and CRC
     * @return Application data (MTI + Bitmap + Data Elements)
     */
    public static byte[] parseResponse(byte[] completeMessage) {
        if (completeMessage == null || completeMessage.length < 9) {
            LogUtil.e(TAG, "✗ Invalid message length: " + (completeMessage != null ? completeMessage.length : 0));
            return new byte[0];
        }

        // Header is 7 bytes (ADR + CB + TPDU)
        // CRC is 2 bytes at the end
        int headerLength = 7;
        int crcLength = 2;
        int applicationDataLength = completeMessage.length - headerLength - crcLength;

        if (applicationDataLength <= 0) {
            LogUtil.e(TAG, "✗ Invalid application data length: " + applicationDataLength);
            return new byte[0];
        }

        byte[] applicationData = new byte[applicationDataLength];
        System.arraycopy(completeMessage, headerLength, applicationData, 0, applicationDataLength);

        LogUtil.e(TAG, "✓ Parsed response message:");
        LogUtil.e(TAG, "  Header: " + headerLength + " bytes");
        LogUtil.e(TAG, "  Application Data: " + applicationDataLength + " bytes");
        LogUtil.e(TAG, "  CRC: " + crcLength + " bytes");

        return applicationData;
    }
}
