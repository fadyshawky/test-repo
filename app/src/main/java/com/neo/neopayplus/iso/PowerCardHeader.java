package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * PowerCARD Header Builder
 * 
 * Implements PowerCARD header structure per SWI-ID1135-SID-Protocol.docx
 * 
 * PowerCARD Header Format (8 bytes):
 * - Position 1: Product specification ('6', '7', or '8')
 * - Positions 2-5: Protocol version = '0100'
 * - Positions 6-8: Error element number ('000' if no error)
 */
public class PowerCardHeader {

    private static final String TAG = Constant.TAG;

    // Product specification values
    public static final char PRODUCT_ISSUER_ONLY = '6';        // Issuer member interface only
    public static final char PRODUCT_ISSUER_ACQUIRER = '7';    // Issuer and Acquirer members interface
    public static final char PRODUCT_ACQUIRER_ONLY = '8';      // Acquirer member interface only

    // Protocol version (always '0100')
    private static final String PROTOCOL_VERSION = "0100";

    /**
     * Build PowerCARD header (8 bytes)
     * 
     * @param productType Product specification ('6', '7', or '8')
     * @param errorElementNumber Error element number ('000' if no error)
     * @return PowerCARD header as byte array (8 bytes)
     */
    public static byte[] buildHeader(char productType, String errorElementNumber) {
        byte[] header = new byte[8];

        // Position 1: Product specification (ASCII)
        header[0] = (byte) productType;

        // Positions 2-5: Protocol version = '0100' (ASCII)
        byte[] protocolVersionBytes = PROTOCOL_VERSION.getBytes();
        System.arraycopy(protocolVersionBytes, 0, header, 1, 4);

        // Positions 6-8: Error element number (ASCII, '000' if no error)
        String errorNum = errorElementNumber != null ? errorElementNumber : "000";
        if (errorNum.length() < 3) {
            errorNum = String.format("%03d", Integer.parseInt(errorNum));
        } else if (errorNum.length() > 3) {
            errorNum = errorNum.substring(0, 3);
        }
        byte[] errorBytes = errorNum.getBytes();
        System.arraycopy(errorBytes, 0, header, 5, 3);

        LogUtil.e(TAG, "=== PowerCARD Header Built ===");
        LogUtil.e(TAG, "  Product Type: " + productType);
        LogUtil.e(TAG, "  Protocol Version: " + PROTOCOL_VERSION);
        LogUtil.e(TAG, "  Error Element Number: " + errorNum);

        return header;
    }

    /**
     * Build PowerCARD header with default values (Acquirer only, no error)
     * 
     * @return PowerCARD header as byte array (8 bytes)
     */
    public static byte[] buildDefaultHeader() {
        return buildHeader(PRODUCT_ACQUIRER_ONLY, "000");
    }

    /**
     * Parse PowerCARD header from message
     * 
     * @param data Byte array containing PowerCARD header at offset 0
     * @return Parsed header information
     */
    public static PowerCardHeaderInfo parseHeader(byte[] data) {
        if (data == null || data.length < 8) {
            throw new IllegalArgumentException(
                    "PowerCARD header too short: " + (data != null ? data.length : 0) + " bytes (expected 8)");
        }

        char productType = (char) data[0];
        String protocolVersion = new String(data, 1, 4);
        String errorElementNumber = new String(data, 5, 3);

        // Validate product type
        if (productType != PRODUCT_ISSUER_ONLY && productType != PRODUCT_ISSUER_ACQUIRER
                && productType != PRODUCT_ACQUIRER_ONLY) {
            throw new IllegalArgumentException("Invalid PowerCARD product type: " + productType);
        }

        // Validate protocol version
        if (!PROTOCOL_VERSION.equals(protocolVersion)) {
            throw new IllegalArgumentException(
                    "Invalid PowerCARD protocol version: " + protocolVersion + " (expected '0100')");
        }

        return new PowerCardHeaderInfo(productType, protocolVersion, errorElementNumber);
    }

    /**
     * PowerCARD Header Information
     */
    public static class PowerCardHeaderInfo {
        public final char productType;
        public final String protocolVersion;
        public final String errorElementNumber;

        public PowerCardHeaderInfo(char productType, String protocolVersion, String errorElementNumber) {
            this.productType = productType;
            this.protocolVersion = protocolVersion;
            this.errorElementNumber = errorElementNumber;
        }
    }
}

