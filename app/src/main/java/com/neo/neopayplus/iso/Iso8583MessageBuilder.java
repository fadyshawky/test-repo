package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.utils.LogUtil;

/**
 * ISO 8583 Message Builder (MsgSpec v341 + PowerCARD Protocol)
 * 
 * Builds complete ISO 8583 messages with PowerCARD protocol structure:
 * - Message Length Prefix (4 bytes ASCII)
 * - Protocol Identification (3 bytes ASCII)
 * - PowerCARD Header (8 bytes)
 * - TPDU (5 bytes)
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
     * Build complete ISO 8583 message with PowerCARD protocol structure
     * 
     * @param applicationData    Application data (MTI + Bitmap + Data Elements)
     *                           from Iso8583Packer
     * @param destinationAddress Destination address (2 bytes, Network International
     *                           Identifier)
     * @param originatorAddress  Originator address (2 bytes, Terminal ID)
     * @return Complete ISO 8583 message with PowerCARD protocol structure
     */
    public static byte[] buildCompleteMessage(byte[] applicationData,
            byte[] destinationAddress,
            byte[] originatorAddress) {
        return buildCompleteMessage(applicationData, destinationAddress, originatorAddress, 
                PowerCardHeader.PRODUCT_ACQUIRER_ONLY, "000");
    }

    /**
     * Build complete ISO 8583 message with PowerCARD protocol structure
     * 
     * @param applicationData    Application data (MTI + Bitmap + Data Elements)
     * @param destinationAddress Destination address (2 bytes)
     * @param originatorAddress  Originator address (2 bytes, Terminal ID)
     * @param productType        PowerCARD product type ('6', '7', or '8')
     * @param errorElementNumber Error element number ('000' if no error)
     * @return Complete ISO 8583 message with PowerCARD protocol structure
     */
    public static byte[] buildCompleteMessage(byte[] applicationData,
            byte[] destinationAddress,
            byte[] originatorAddress,
            char productType,
            String errorElementNumber) {
        try {
            LogUtil.e(TAG, "=== Building ISO 8583 Message (PowerCARD Protocol) ===");

            // Build PowerCARD header (8 bytes)
            byte[] powerCardHeader = PowerCardHeader.buildHeader(productType, errorElementNumber);

            // Build protocol identification (3 bytes)
            byte[] protocolId = PowerCardProtocol.buildProtocolIdentification();

            // Build TPDU (5 bytes)
            byte[] tpdu = buildTpdu(TPDU_ID_TRANSACTION, destinationAddress, originatorAddress);

            // Build HDLC header (ADR + CB) - 2 bytes
            byte[] hdlcHeader = new byte[2];
            hdlcHeader[0] = ADR_DEFAULT;
            hdlcHeader[1] = CB_DEFAULT;

            // Calculate CRC (HDLC checksum) for: protocol ID + PowerCARD header + TPDU + application data
            // Note: CRC is calculated over protocol ID + PowerCARD header + TPDU + application data
            byte[] dataForCrc = new byte[protocolId.length + powerCardHeader.length + tpdu.length + applicationData.length];
            int offset = 0;
            System.arraycopy(protocolId, 0, dataForCrc, offset, protocolId.length);
            offset += protocolId.length;
            System.arraycopy(powerCardHeader, 0, dataForCrc, offset, powerCardHeader.length);
            offset += powerCardHeader.length;
            System.arraycopy(tpdu, 0, dataForCrc, offset, tpdu.length);
            offset += tpdu.length;
            System.arraycopy(applicationData, 0, dataForCrc, offset, applicationData.length);

            byte[] crc = calculateHdlcCrc(dataForCrc);

            // Calculate message length (excluding length prefix itself)
            // Structure: Protocol ID (3) + PowerCARD Header (8) + TPDU (5) + Application Data + CRC (2)
            int messageLength = PowerCardProtocol.calculateMessageLength(applicationData.length);

            // Build message length prefix (4 bytes ASCII)
            byte[] lengthPrefix = PowerCardProtocol.buildMessageLengthPrefix(messageLength);

            // Build complete message: Length Prefix + Protocol ID + PowerCARD Header + TPDU + Application Data + CRC
            byte[] completeMessage = new byte[lengthPrefix.length + messageLength];
            offset = 0;
            System.arraycopy(lengthPrefix, 0, completeMessage, offset, lengthPrefix.length);
            offset += lengthPrefix.length;
            System.arraycopy(protocolId, 0, completeMessage, offset, protocolId.length);
            offset += protocolId.length;
            System.arraycopy(powerCardHeader, 0, completeMessage, offset, powerCardHeader.length);
            offset += powerCardHeader.length;
            System.arraycopy(tpdu, 0, completeMessage, offset, tpdu.length);
            offset += tpdu.length;
            System.arraycopy(applicationData, 0, completeMessage, offset, applicationData.length);
            offset += applicationData.length;
            System.arraycopy(crc, 0, completeMessage, offset, crc.length);

            LogUtil.e(TAG, "✓ Complete PowerCARD message built:");
            LogUtil.e(TAG, "  Length Prefix: " + lengthPrefix.length + " bytes");
            LogUtil.e(TAG, "  Protocol ID: " + protocolId.length + " bytes");
            LogUtil.e(TAG, "  PowerCARD Header: " + powerCardHeader.length + " bytes");
            LogUtil.e(TAG, "  TPDU: " + tpdu.length + " bytes");
            LogUtil.e(TAG, "  Application Data: " + applicationData.length + " bytes");
            LogUtil.e(TAG, "  CRC: " + crc.length + " bytes");
            LogUtil.e(TAG, "  Total: " + completeMessage.length + " bytes");

            return completeMessage;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error building ISO 8583 message: " + e.getMessage());
            e.printStackTrace();
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
     * Parse ISO 8583 response message with PowerCARD protocol structure
     * Extracts application data from complete message
     * 
     * @param completeMessage Complete message with PowerCARD protocol structure
     * @return Application data (MTI + Bitmap + Data Elements)
     */
    public static byte[] parseResponse(byte[] completeMessage) {
        if (completeMessage == null || completeMessage.length < 22) { // Minimum: 4 (length) + 3 (protocol) + 8 (header) + 5 (TPDU) + 2 (CRC) = 22
            LogUtil.e(TAG, "✗ Invalid message length: " + (completeMessage != null ? completeMessage.length : 0));
            return new byte[0];
        }

        try {
            int offset = 0;

            // Parse message length prefix (4 bytes)
            int messageLength = PowerCardProtocol.parseMessageLengthPrefix(completeMessage);
            offset += 4;

            // Parse protocol identification (3 bytes)
            String protocolId = PowerCardProtocol.parseProtocolIdentification(
                    java.util.Arrays.copyOfRange(completeMessage, offset, offset + 3));
            offset += 3;

            // Parse PowerCARD header (8 bytes)
            PowerCardHeader.PowerCardHeaderInfo headerInfo = PowerCardHeader.parseHeader(
                    java.util.Arrays.copyOfRange(completeMessage, offset, offset + 8));
            offset += 8;

            // Skip TPDU (5 bytes)
            offset += 5;

            // Extract application data (remaining bytes minus CRC)
            int crcLength = 2;
            int applicationDataLength = completeMessage.length - offset - crcLength;

            if (applicationDataLength <= 0) {
                LogUtil.e(TAG, "✗ Invalid application data length: " + applicationDataLength);
                return new byte[0];
            }

            byte[] applicationData = new byte[applicationDataLength];
            System.arraycopy(completeMessage, offset, applicationData, 0, applicationDataLength);

            LogUtil.e(TAG, "✓ Parsed PowerCARD response message:");
            LogUtil.e(TAG, "  Protocol ID: " + protocolId);
            LogUtil.e(TAG, "  Product Type: " + headerInfo.productType);
            LogUtil.e(TAG, "  Application Data: " + applicationDataLength + " bytes");

            return applicationData;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error parsing response message: " + e.getMessage());
            e.printStackTrace();
            return new byte[0];
        }
    }
}
