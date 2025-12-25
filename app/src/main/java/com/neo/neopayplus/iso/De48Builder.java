package com.neo.neopayplus.iso;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * DE48: Additional Private Data Builder
 * 
 * Builds DE48 field with sub-elements in the format:
 * - Type (3 ASCII characters, e.g., "P25", "P31", "M50")
 * - Length (3 ASCII digits, e.g., "001", "004", "011")
 * - Value (variable length based on length field)
 * 
 * Format: LLL + (Type + Length + Value)*
 * Where LLL is 3-byte ASCII length of all sub-elements
 */
public class De48Builder {

    private static final String TAG = Constant.TAG;

    /**
     * DE48 Sub-element
     */
    public static class SubElement {
        public final String type;      // 3 ASCII characters (e.g., "P25", "M50")
        public final int length;       // Length of value in bytes
        public final String value;     // Value (ASCII string)

        public SubElement(String type, int length, String value) {
            this.type = type;
            this.length = length;
            this.value = value != null ? value : "";
        }
    }

    /**
     * Build DE48 field from sub-elements
     * 
     * @param subElements List of sub-elements
     * @return DE48 field as hex string (ready to append to ISO message)
     */
    public static String buildDe48(List<SubElement> subElements) {
        if (subElements == null || subElements.isEmpty()) {
            LogUtil.e(TAG, "DE48: No sub-elements provided - returning empty field");
            return "";
        }

        try {
            // Build sub-elements string: Type(3) + Length(3) + Value(variable)
            StringBuilder subElementsData = new StringBuilder();
            
            for (SubElement sub : subElements) {
                // Type: 3 ASCII characters
                String type = sub.type != null ? sub.type : "   ";
                if (type.length() != 3) {
                    type = String.format("%-3s", type).substring(0, 3); // Pad to 3 chars
                }
                subElementsData.append(type);

                // Length: 3 ASCII digits (padded with zeros)
                int actualLength = sub.value != null ? sub.value.length() : 0;
                String lengthStr = String.format("%03d", actualLength);
                subElementsData.append(lengthStr);

                // Value: variable length ASCII
                if (sub.value != null && actualLength > 0) {
                    subElementsData.append(sub.value);
                }
            }

            // Convert to bytes for length calculation
            byte[] subElementsBytes = subElementsData.toString().getBytes(StandardCharsets.US_ASCII);
            int totalLength = subElementsBytes.length;

            // Build DE48: LLL (3-byte ASCII length) + sub-elements data
            StringBuilder de48 = new StringBuilder();
            
            // LLL: 3-byte ASCII length (e.g., "180" for 180 bytes)
            String lengthPrefix = String.format("%03d", totalLength);
            de48.append(lengthPrefix);
            de48.append(subElementsData.toString());

            // Convert to hex for ISO message
            byte[] de48Bytes = de48.toString().getBytes(StandardCharsets.US_ASCII);
            String de48Hex = bytesToHex(de48Bytes);

            LogUtil.e(TAG, "✓ DE48 built - total length: " + totalLength + " bytes, " + subElements.size() + " sub-elements");
            for (SubElement sub : subElements) {
                LogUtil.e(TAG, "  Sub-element: " + sub.type + " (length: " + sub.length + ", value: " + 
                    (sub.value != null && sub.value.length() > 20 ? sub.value.substring(0, 20) + "..." : sub.value) + ")");
            }

            return de48Hex;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error building DE48: " + e.getMessage());
            return "";
        }
    }

    /**
     * Build DE48 with common sub-elements for authorization request
     * 
     * @param cardBrand Card brand (e.g., "VISA", "MASTERCARD")
     * @param arqcResult ARQC result (null, "1" = incorrect, "2" = correct)
     * @param messageReasonCode Message reason code (4 digits, optional)
     * @return DE48 field as hex string
     */
    public static String buildDe48ForAuthorization(String cardBrand, String arqcResult, String messageReasonCode) {
        return buildDe48ForAuthorization(cardBrand, arqcResult, messageReasonCode, null, null, null, null, null);
    }

    /**
     * Build DE48 with extended sub-elements for authorization request
     * 
     * @param cardBrand Card brand (e.g., "VISA", "MASTERCARD")
     * @param arqcResult ARQC result (null, "1" = incorrect, "2" = correct)
     * @param messageReasonCode Message reason code (4 digits, optional)
     * @param networkId Network identifier (e.g., "0002" for Visa, "0004" for Plus)
     * @param transactionId Transaction identifier (up to 32 chars, optional)
     * @param paymentFacilitatorId Payment Facilitator ID (11 chars, optional)
     * @param subMerchantId Sub-Merchant ID (15 chars, optional)
     * @param dccIndicator DCC indicator ("1" = DCC performed, "0" = not performed, null = not applicable)
     * @return DE48 field as hex string
     */
    public static String buildDe48ForAuthorization(
            String cardBrand, 
            String arqcResult, 
            String messageReasonCode,
            String networkId,
            String transactionId,
            String paymentFacilitatorId,
            String subMerchantId,
            String dccIndicator) {
        List<SubElement> subElements = new ArrayList<>();

        // P25: Result of Card Authentication (001)
        // ' ' = ARQC not checked, '1' = ARQC incorrect, '2' = ARQC correct
        if (arqcResult != null) {
            String arqcValue = arqcResult.equals("2") ? "2" : (arqcResult.equals("1") ? "1" : " ");
            subElements.add(new SubElement("P25", 1, arqcValue));
        }

        // P31: Message Reason Code (004)
        if (messageReasonCode != null && messageReasonCode.length() == 4) {
            subElements.add(new SubElement("P31", 4, messageReasonCode));
        }

        // P40: Network Identifier (004)
        // 0002 = Visa network, 0004 = Plus network
        if (networkId != null && networkId.length() == 4) {
            subElements.add(new SubElement("P40", 4, networkId));
        } else if (cardBrand != null) {
            // Auto-detect network from card brand
            if (cardBrand.toUpperCase().contains("VISA")) {
                subElements.add(new SubElement("P40", 4, "0002"));
            } else if (cardBrand.toUpperCase().contains("PLUS")) {
                subElements.add(new SubElement("P40", 4, "0004"));
            }
        }

        // P61: Additional POS Data (..12)
        // Position 1: Partial Approval Terminal Support Indicator ('0' = not supported, '1' = supported)
        // Position 2: Purchase Amount Only Terminal Support Indicator ('0' = not supported, '1' = supported)
        // Default: "00" (not supported)
        subElements.add(new SubElement("P61", 2, "00"));

        // P63: Transaction Identifier (..32)
        if (transactionId != null && transactionId.length() > 0 && transactionId.length() <= 32) {
            subElements.add(new SubElement("P63", transactionId.length(), transactionId));
        }

        // P95: Card Brand (002)
        // '01' = Visa, '02' = MasterCard, '03' = American Express, '04' = Diners Club, '05' = JCB
        if (cardBrand != null) {
            String brandCode = "01"; // Default to Visa
            if (cardBrand.toUpperCase().contains("VISA")) {
                brandCode = "01";
            } else if (cardBrand.toUpperCase().contains("MASTERCARD") || cardBrand.toUpperCase().contains("MASTER")) {
                brandCode = "02";
            } else if (cardBrand.toUpperCase().contains("AMEX") || cardBrand.toUpperCase().contains("AMERICAN")) {
                brandCode = "03";
            } else if (cardBrand.toUpperCase().contains("DINERS")) {
                brandCode = "04";
            } else if (cardBrand.toUpperCase().contains("JCB")) {
                brandCode = "05";
            }
            subElements.add(new SubElement("P95", 2, brandCode));
        }

        // M50: Payment Facilitator ID (011)
        if (paymentFacilitatorId != null && paymentFacilitatorId.length() == 11) {
            subElements.add(new SubElement("M50", 11, paymentFacilitatorId));
        }

        // M51: Independent Sales Organization ID (011)
        // Not typically used in standard POS transactions, skip for now

        // M52: Sub-Merchant ID (015)
        if (subMerchantId != null && subMerchantId.length() > 0 && subMerchantId.length() <= 15) {
            // Pad to 15 characters if needed
            String paddedSubMerchantId = String.format("%-15s", subMerchantId).substring(0, 15);
            subElements.add(new SubElement("M52", 15, paddedSubMerchantId));
        }

        // M53: Sub-Merchant Country Code (003)
        // Not typically used, skip for now

        // M54: Payment Facilitator Name (022)
        // Not typically used, skip for now

        // M55: Dynamic Currency Conversion Indicator (001)
        // '1' = DCC was performed at POS, '0' = DCC not performed
        if (dccIndicator != null && (dccIndicator.equals("0") || dccIndicator.equals("1"))) {
            subElements.add(new SubElement("M55", 1, dccIndicator));
        }

        return buildDe48(subElements);
    }

    /**
     * Parse DE48 field from hex string
     * 
     * @param de48Hex DE48 field as hex string
     * @return List of sub-elements
     */
    public static List<SubElement> parseDe48(String de48Hex) {
        List<SubElement> subElements = new ArrayList<>();
        
        if (de48Hex == null || de48Hex.isEmpty()) {
            return subElements;
        }

        try {
            // Convert hex to bytes
            byte[] de48Bytes = hexToBytes(de48Hex);
            String de48Str = new String(de48Bytes, StandardCharsets.US_ASCII);

            // Read length prefix (3 bytes)
            if (de48Str.length() < 3) {
                LogUtil.e(TAG, "✗ DE48 too short for length prefix");
                return subElements;
            }

            int totalLength = Integer.parseInt(de48Str.substring(0, 3));
            String subElementsData = de48Str.substring(3);

            if (subElementsData.length() < totalLength) {
                LogUtil.e(TAG, "✗ DE48 data length mismatch: expected " + totalLength + ", got " + subElementsData.length());
                return subElements;
            }

            // Parse sub-elements: Type(3) + Length(3) + Value(variable)
            int offset = 0;
            while (offset < totalLength) {
                if (offset + 6 > totalLength) {
                    break; // Not enough data for Type + Length
                }

                // Type: 3 ASCII characters
                String type = subElementsData.substring(offset, offset + 3);
                offset += 3;

                // Length: 3 ASCII digits
                int valueLength = Integer.parseInt(subElementsData.substring(offset, offset + 3));
                offset += 3;

                // Value: variable length
                if (offset + valueLength > totalLength) {
                    LogUtil.e(TAG, "✗ DE48 sub-element value length exceeds remaining data");
                    break;
                }

                String value = subElementsData.substring(offset, offset + valueLength);
                offset += valueLength;

                subElements.add(new SubElement(type, valueLength, value));
            }

            LogUtil.e(TAG, "✓ DE48 parsed - " + subElements.size() + " sub-elements");
            return subElements;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error parsing DE48: " + e.getMessage());
            return subElements;
        }
    }

    /**
     * Convert bytes to hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }

    /**
     * Convert hex string to bytes
     */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}

