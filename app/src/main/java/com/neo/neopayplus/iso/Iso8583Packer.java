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
 * - 1804: Network Management Request (PowerCARD)
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

            // Extract expiry date from Field 55 if available
            String expiryDate = extractExpiryDateFromField55(field55);
            boolean includeExpiryDate = (expiryDate != null && expiryDate.length() >= 4);
            
            // Extract card sequence number from Field 55 if available (tag 5F34)
            String cardSequenceNumber = extractCardSequenceNumberFromField55(field55);
            boolean includeCardSequenceNumber = (cardSequenceNumber != null && cardSequenceNumber.length() > 0);

            // Build fields array based on what's actually present
            // Mandatory fields per PowerCARD spec: 1, 2, 3, 4, 6, 7, 10, 11, 12, 14, 15, 16, 18, 19, 21, 22, 24, 27, 32, 33, 37, 41, 42, 43, 49, 51, 53, 61, 124, 128
            List<Integer> fieldList = new ArrayList<>();
            fieldList.add(1); // DE1: Secondary Bitmap (mandatory - may be all zeros if no fields 65-128)
            if (includePan) {
                fieldList.add(2); // DE2: PAN (only if present, otherwise extracted from DE55)
            }
            fieldList.add(3); // DE3: Processing Code
            fieldList.add(4); // DE4: Amount
            fieldList.add(6); // DE6: Cardholder Billing Amount (mandatory)
            fieldList.add(7); // DE7: Transmission Date and Time (mandatory)
            fieldList.add(10); // DE10: Cardholder Billing Exchange Rate (mandatory)
            fieldList.add(11); // DE11: STAN
            fieldList.add(12); // DE12: Transaction Local Date and Time (mandatory)
            if (includeExpiryDate) {
                fieldList.add(14); // DE14: Expiry Date (mandatory if available)
            }
            fieldList.add(15); // DE15: Settlement Date (mandatory)
            fieldList.add(16); // DE16: Exchange Date (mandatory)
            fieldList.add(18); // DE18: Merchant Type (mandatory)
            fieldList.add(19); // DE19: Acquiring Institution Country Code (mandatory)
            fieldList.add(21); // DE21: Forwarding Institution Country Code (mandatory)
            fieldList.add(22); // DE22: POS Entry Mode
            if (includeCardSequenceNumber) {
                fieldList.add(23); // DE23: Card Sequence Number (conditional - mandatory for EMV)
            }
            fieldList.add(24); // DE24: Function Code (mandatory)
            fieldList.add(27); // DE27: Authorization Code Length (mandatory)
            fieldList.add(32); // DE32: Acquiring Institution Identification Code (mandatory)
            fieldList.add(33); // DE33: Forwarding Institution Identification Code (mandatory)
            fieldList.add(37); // DE37: Retrieval Reference Number (mandatory - placeholder, backend generates)
            fieldList.add(41); // DE41: Card Acceptor Terminal Identifier (mandatory)
            fieldList.add(42); // DE42: Card Acceptor Identification Code (mandatory)
            fieldList.add(43); // DE43: Card Acceptor Name and Address (mandatory)
            fieldList.add(48); // DE48: Additional Private Data (conditional)
            fieldList.add(49); // DE49: Currency Code
            fieldList.add(51); // DE51: Cardholder Billing Currency Code (mandatory)
            fieldList.add(53); // DE53: Security Check Data (mandatory)
            fieldList.add(60); // DE60: Reserved for National Use (conditional)
            fieldList.add(61); // DE61: Reserved for National Use (mandatory)
            fieldList.add(62); // DE62: Reserved for Private Use - CPS Data (conditional)
            if (includePinBlock) {
                fieldList.add(52); // DE52: PIN Block (only if present)
            }
            fieldList.add(55); // DE55: ICC Data (Field 55)
            fieldList.add(124); // DE124: Transaction Originator Institution Identification Code (mandatory)
            fieldList.add(128); // DE128: Message Authentication Code (mandatory)

            LogUtil.e(TAG, "  Fields to include in bitmap: " + fieldList.toString());
            int[] fields = new int[fieldList.size()];
            for (int i = 0; i < fieldList.size(); i++) {
                fields[i] = fieldList.get(i);
            }
            String bitmap = buildBitmap(fields);
            LogUtil.e(TAG, "  Built bitmap (hex): " + bitmap);
            buffer.append(bitmap);

            // DE1: Secondary Bitmap (8 bytes binary) - required if we have fields 65-128
            // Since we have DE124 and DE128, we need secondary bitmap
            // Set bits for fields 65-128: 124 (bit 59 in secondary bitmap), 128 (bit 63 in secondary bitmap)
            // Bit position in secondary: field 124 = bit 59 (124 - 65 = 59), field 128 = bit 63 (128 - 65 = 63)
            long secondaryBitmap = 0L;
            secondaryBitmap |= (1L << (63 - 59)); // DE124 (bit 59 in secondary, which is bit 4 from left)
            secondaryBitmap |= (1L << (63 - 63)); // DE128 (bit 63 in secondary, which is bit 0 from left)
            String secondaryBitmapHex = String.format("%016X", secondaryBitmap);
            buffer.append(secondaryBitmapHex);
            LogUtil.e(TAG, "  DE1 (Secondary Bitmap): " + secondaryBitmapHex);

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
            String paddedAmount = "000000000000";
            if (amount != null && !amount.isEmpty()) {
                paddedAmount = zeroPadNumeric(amount, 12);
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
            } else {
                LogUtil.e(TAG, "  DE4 (Amount): Empty - using default zeros");
            }
            buffer.append(asciiToHex(paddedAmount));

            // DE6: Cardholder Billing Amount (12 digits) - same as DE4 for same currency
            buffer.append(asciiToHex(paddedAmount));
            LogUtil.e(TAG, "  DE6 (Cardholder Billing Amount): " + paddedAmount);

            // DE7: Transmission Date and Time (UTC format: YYMMDDhhmm) - 10 digits
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyMMddHHmm", Locale.US);
            utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String transmissionDateTime = utcFormat.format(new Date());
            buffer.append(asciiToHex(transmissionDateTime));
            LogUtil.e(TAG, "  DE7 (Transmission Date and Time UTC): " + transmissionDateTime);

            // DE10: Cardholder Billing Conversion Rate (8 digits) - default 1:1
            buffer.append(asciiToHex("00000001")); // 1:1 conversion rate
            LogUtil.e(TAG, "  DE10 (Cardholder Billing Conversion Rate): 00000001 (1:1)");

            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (stan != null && !stan.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(stan, 6)));
            } else {
                buffer.append(asciiToHex("000000")); // Default: 6 ASCII zeros
            }

            // DE12: Transaction Local Date and Time (YYMMDDhhmmss) - 12 digits
            SimpleDateFormat localFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
            String transactionDateTime = localFormat.format(new Date());
            buffer.append(asciiToHex(transactionDateTime));
            LogUtil.e(TAG, "  DE12 (Transaction Local Date and Time): " + transactionDateTime);

            // DE14: Expiry Date (YYMM) - 4 digits
            if (includeExpiryDate) {
                String expiryYYMM = expiryDate.substring(0, 4); // Take first 4 digits (YYMM)
                buffer.append(asciiToHex(expiryYYMM));
                LogUtil.e(TAG, "  DE14 (Expiry Date): " + expiryYYMM);
            }

            // DE15: Settlement Date (YYMMDD) - 6 digits
            SimpleDateFormat settlementFormat = new SimpleDateFormat("yyMMdd", Locale.US);
            String settlementDate = settlementFormat.format(new Date());
            buffer.append(asciiToHex(settlementDate));
            LogUtil.e(TAG, "  DE15 (Settlement Date): " + settlementDate);

            // DE16: Exchange Date (MMDD) - 4 digits
            SimpleDateFormat exchangeFormat = new SimpleDateFormat("MMdd", Locale.US);
            String exchangeDate = exchangeFormat.format(new Date());
            buffer.append(asciiToHex(exchangeDate));
            LogUtil.e(TAG, "  DE16 (Exchange Date): " + exchangeDate);

            // DE18: Merchant Type (4 digits) - default 6011 (Merchandise and Services)
            buffer.append(asciiToHex("6011"));
            LogUtil.e(TAG, "  DE18 (Merchant Type): 6011");

            // DE19: Acquiring Institution Country Code (3 digits) - default 818 (Egypt)
            buffer.append(asciiToHex("818"));
            LogUtil.e(TAG, "  DE19 (Acquiring Institution Country Code): 818");

            // DE21: Forwarding Institution Country Code (3 digits) - default 818 (Egypt)
            buffer.append(asciiToHex("818"));
            LogUtil.e(TAG, "  DE21 (Forwarding Institution Country Code): 818");

            // DE22: POS Entry Mode - 3 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            if (posEntryMode != null && !posEntryMode.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(posEntryMode, 3)));
            } else {
                buffer.append(asciiToHex("000")); // Default: 3 ASCII zeros
            }

            // DE23: Card Sequence Number (3 digits) - conditional, mandatory for EMV
            if (includeCardSequenceNumber) {
                String paddedCsn = zeroPadNumeric(cardSequenceNumber, 3);
                buffer.append(asciiToHex(paddedCsn));
                LogUtil.e(TAG, "  DE23 (Card Sequence Number): " + paddedCsn);
            }

            // DE24: Function Code (3 digits) - default 100 (Normal Request)
            buffer.append(asciiToHex("100"));
            LogUtil.e(TAG, "  DE24 (Function Code): 100");

            // DE27: Authorization Code Length (1 digit) - default 6
            buffer.append(asciiToHex("6"));
            LogUtil.e(TAG, "  DE27 (Authorization Code Length): 6");

            // DE32: Acquiring Institution Identification Code (LLVAR n..11)
            // Format: LL + value (LL is 2-digit ASCII length)
            String acquiringInstId = "00000000000"; // 11 digits default
            buffer.append(asciiToHex(String.format("%02d", acquiringInstId.length())));
            buffer.append(asciiToHex(acquiringInstId));
            LogUtil.e(TAG, "  DE32 (Acquiring Institution ID): " + acquiringInstId);

            // DE33: Forwarding Institution Identification Code (LLVAR n..11)
            // Format: LL + value (LL is 2-digit ASCII length)
            String forwardingInstId = "00000000000"; // 11 digits default
            buffer.append(asciiToHex(String.format("%02d", forwardingInstId.length())));
            buffer.append(asciiToHex(forwardingInstId));
            LogUtil.e(TAG, "  DE33 (Forwarding Institution ID): " + forwardingInstId);

            // DE37: Retrieval Reference Number (12 digits) - placeholder, backend generates
            buffer.append(asciiToHex("000000000000")); // Placeholder, backend will generate
            LogUtil.e(TAG, "  DE37 (RRN): 000000000000 (placeholder, backend generates)");

            // DE41: Card Acceptor Terminal Identifier (8 alphanumeric)
            String paddedTerminalId = zeroPadNumeric(terminalId, 8);
            buffer.append(asciiToHex(paddedTerminalId));
            LogUtil.e(TAG, "  DE41 (Terminal ID): " + paddedTerminalId);

            // DE42: Card Acceptor Identification Code (15 alphanumeric)
            String paddedMerchantId = zeroPadNumeric(merchantId, 15);
            buffer.append(asciiToHex(paddedMerchantId));
            LogUtil.e(TAG, "  DE42 (Merchant ID): " + paddedMerchantId);

            // DE43: Card Acceptor Name and Address (LLVAR ans..40)
            // Format: LL + value (LL is 2-digit ASCII length)
            String merchantName = "NeoPayPlus"; // Default merchant name
            String merchantAddress = merchantName.substring(0, Math.min(40, merchantName.length()));
            buffer.append(asciiToHex(String.format("%02d", merchantAddress.length())));
            buffer.append(asciiToHex(merchantAddress));
            LogUtil.e(TAG, "  DE43 (Merchant Name/Address): " + merchantAddress);

            // DE48: Additional Private Data (LLLVAR ans..999)
            // Build DE48 with common sub-elements
            // Extract card brand from PAN or Field 55 if available
            String cardBrand = null;
            if (field55 != null && !field55.isEmpty()) {
                // Try to extract AID from Field 55 to determine card brand
                // AID A000000003 = Visa, A000000004/A000000005 = Mastercard
                if (field55.contains("A000000003")) {
                    cardBrand = "VISA";
                } else if (field55.contains("A000000004") || field55.contains("A000000005")) {
                    cardBrand = "MASTERCARD";
                } else if (field55.contains("A000000732")) {
                    cardBrand = "MEEZA";
                }
            }
            // If not found in Field 55, try PAN BIN
            if (cardBrand == null && pan != null && pan.length() >= 6) {
                String bin = pan.substring(0, 6);
                if (bin.startsWith("4")) {
                    cardBrand = "VISA";
                } else if (bin.startsWith("5") || bin.startsWith("2")) {
                    cardBrand = "MASTERCARD";
                }
            }
            
            // Build DE48 with P25 (ARQC result), P31 (message reason code), P95 (card brand)
            // ARQC result: assume correct (2) for EMV chip transactions, or space if not checked
            String arqcResult = (field55 != null && !field55.isEmpty()) ? "2" : null;
            String messageReasonCode = "0000"; // Default: no special reason
            String de48Hex = De48Builder.buildDe48ForAuthorization(cardBrand, arqcResult, messageReasonCode);
            if (de48Hex != null && !de48Hex.isEmpty()) {
                buffer.append(de48Hex);
                LogUtil.e(TAG, "✓ DE48 (Additional Private Data) included");
            } else {
                LogUtil.e(TAG, "⚠️ DE48 (Additional Private Data) empty - not included");
            }

            // DE49: Currency Code, Transaction - 3 digits
            // Per MsgSpec v341, numeric fields are ASCII encoded
            String finalCurrencyCode = currencyCode != null && !currencyCode.isEmpty() ? currencyCode : "818";
            buffer.append(asciiToHex(zeroPadNumeric(finalCurrencyCode, 3)));

            // DE51: Cardholder Billing Currency Code (3 digits) - same as DE49
            buffer.append(asciiToHex(zeroPadNumeric(finalCurrencyCode, 3)));
            LogUtil.e(TAG, "  DE51 (Cardholder Billing Currency Code): " + finalCurrencyCode);

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

            // DE53: Security Check Data (16 digits) - default zeros
            buffer.append(asciiToHex("0000000000000000"));
            LogUtil.e(TAG, "  DE53 (Security Check Data): 0000000000000000");

            // DE60: Reserved for National Use (LLLVAR ans..999)
            // Format: LLL + value (LLL is 3-digit ASCII length)
            // Position 1: Authentication Reliability Indicator (0, 1, 2, 3)
            //   - 0 = Not authenticated
            //   - 1 = Authenticated offline
            //   - 2 = Authenticated online
            //   - 3 = Authenticated offline and online
            // Position 2: Chip Condition Code (0, 1, 2)
            //   - 0 = Chip read successfully
            //   - 1 = Chip read with errors
            //   - 2 = Chip not read (fallback to mag stripe)
            // Position 3: VSDC Transaction Indicator (0, 1)
            //   - 0 = Not a VSDC transaction
            //   - 1 = VSDC transaction
            // Default: "000" (not authenticated, chip read successfully, not VSDC)
            String de60Value = "000"; // Default: minimal value
            // If Field 55 is present, this is likely an EMV chip transaction
            if (field55 != null && !field55.isEmpty()) {
                // Assume chip read successfully (0), authenticated online (2), VSDC if contactless (1)
                // Check if contactless from POS Entry Mode (DE22) - but we don't have it here
                // For now, use default "000"
                de60Value = "020"; // Authenticated online (2), chip read successfully (0), not VSDC (0)
            }
            buffer.append(asciiToHex(String.format("%03d", de60Value.length())));
            buffer.append(asciiToHex(de60Value));
            LogUtil.e(TAG, "  DE60 (Reserved for National Use): " + de60Value);

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

            // DE61: Reserved for National Use (LLLVAR ans..999)
            // Format: LLL + sub-elements (LLL is 3-digit ASCII length)
            // Sub-element format: Type(3) + Length(3) + Value(variable)
            // Sub-element '039': Response code from Issuer (002)
            // Build DE61 with sub-element '039' (empty for request, will be populated in response)
            StringBuilder de61Builder = new StringBuilder();
            // Sub-element '039': Response code from Issuer (002) - empty for request
            de61Builder.append("039"); // Type
            de61Builder.append("002"); // Length (2 bytes)
            de61Builder.append("  ");  // Value (2 spaces = empty, will be populated in response)
            
            String de61Value = de61Builder.toString();
            buffer.append(asciiToHex(String.format("%03d", de61Value.length())));
            buffer.append(asciiToHex(de61Value));
            LogUtil.e(TAG, "  DE61 (Reserved for National Use): " + de61Value.length() + " bytes (sub-element 039)");

            // DE62: Reserved for Private Use - CPS Data (LLLVAR ans..999)
            // Format: LLL + sub-elements (LLL is 3-digit ASCII length)
            // Sub-element format: Type(3) + Length(3) + Value(variable)
            // F01: Authorization Characteristic Indicator (ACI) (001)
            //   - A = Qualification successful (Card present, Track read, CVV requested)
            //   - E = Qualification successful (Merchant class A compliant)
            //   - N = Qualification unsuccessful
            // F02: Transaction Identifier (015)
            // F03: CPS Validation Code (004)
            // F04: Identifier of Specific Data for Market (001)
            //   - A = Car rent, H = Hotel, N = Other
            // F05: Duration of Pre-authorization (002) - in days
            // F06: Indicator of Prestige Property (001)
            //   - D = Limit to 500 USD, B = Limit to 1000 USD, S = Limit to 1,500 USD
            // For standard POS transactions, use minimal/default values
            StringBuilder de62Builder = new StringBuilder();
            // F01: Authorization Characteristic Indicator (001) - default to 'A' (qualification successful)
            de62Builder.append("F01"); // Type
            de62Builder.append("001"); // Length (1 byte)
            de62Builder.append("A");   // Value ('A' = qualification successful)
            
            // F02: Transaction Identifier (015) - use STAN or generate
            String transactionId = stan != null ? stan : "000000000000000"; // 15 chars
            if (transactionId.length() < 15) {
                transactionId = String.format("%-15s", transactionId).substring(0, 15); // Pad to 15
            } else if (transactionId.length() > 15) {
                transactionId = transactionId.substring(0, 15); // Truncate to 15
            }
            de62Builder.append("F02"); // Type
            de62Builder.append("015"); // Length (15 bytes)
            de62Builder.append(transactionId); // Value
            LogUtil.e(TAG, "    F02 (Transaction Identifier): " + transactionId);

            // F03: CPS Validation Code (004) - 4 bytes
            String cpsValidationCode = "0000"; // Default: no CPS validation code
            de62Builder.append("F03"); // Type
            de62Builder.append("004"); // Length
            de62Builder.append(cpsValidationCode); // Value
            LogUtil.e(TAG, "    F03 (CPS Validation Code): " + cpsValidationCode);

            // F04: Identifier of Specific Data for Market (001) - 1 byte
            // A = Car rent, H = Hotel, N = Other (default)
            String marketIdentifier = "N"; // Default: Other
            de62Builder.append("F04"); // Type
            de62Builder.append("001"); // Length
            de62Builder.append(marketIdentifier); // Value
            LogUtil.e(TAG, "    F04 (Market Identifier): " + marketIdentifier);

            // F05: Duration of Pre-authorization (002) - 2 bytes (days)
            String preauthDuration = "00"; // Default: 0 days (not a pre-auth)
            de62Builder.append("F05"); // Type
            de62Builder.append("002"); // Length
            de62Builder.append(preauthDuration); // Value
            LogUtil.e(TAG, "    F05 (Pre-auth Duration): " + preauthDuration + " days");

            // F06: Indicator of Prestige Property (001) - 1 byte
            // D = Limit to 500 USD, B = Limit to 1000 USD, S = Limit to 1,500 USD, space = No limit
            String prestigeIndicator = " "; // Default: No limit
            de62Builder.append("F06"); // Type
            de62Builder.append("001"); // Length
            de62Builder.append(prestigeIndicator); // Value
            LogUtil.e(TAG, "    F06 (Prestige Indicator): '" + prestigeIndicator + "' (no limit)");
            
            String de62Value = de62Builder.toString();
            buffer.append(asciiToHex(String.format("%03d", de62Value.length())));
            buffer.append(asciiToHex(de62Value));
            LogUtil.e(TAG, "  DE62 (Reserved for Private Use - CPS Data): " + de62Value.length() + " bytes (F01-F06)");

            // DE124: Transaction Originator Institution Identification Code (LLVAR n..11)
            // Format: LL + value (LL is 2-digit ASCII length)
            String originatorInstId = "00000000000"; // 11 digits default
            buffer.append(asciiToHex(String.format("%02d", originatorInstId.length())));
            buffer.append(asciiToHex(originatorInstId));
            LogUtil.e(TAG, "  DE124 (Transaction Originator Institution ID): " + originatorInstId);

            // DE128: Message Authentication Code (16 bytes binary) - default zeros
            buffer.append("00000000000000000000000000000000"); // 16 bytes = 32 hex chars
            LogUtil.e(TAG, "  DE128 (Message Authentication Code): 00000000000000000000000000000000");

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
     * Pack ISO8583 financial transaction request (1200)
     * 
     * Financial Transaction Request is used for immediate completion of transactions
     * (as opposed to authorization-only with 0100).
     * 
     * @param pan            Primary Account Number
     * @param processingCode Processing Code (e.g., "000000" = Purchase)
     * @param amount         Transaction amount (minor currency units)
     * @param stan           Systems Trace Audit Number (6 digits)
     * @param posEntryMode   POS Entry Mode (3 digits)
     * @param currencyCode   Currency Code (3 digits, e.g., "818" = EGP)
     * @param field55        EMV Field 55 (ICC Data) - hex string
     * @param terminalId      Terminal ID
     * @param merchantId     Merchant ID
     * @param pinBlock       PIN block (8 bytes hex string, optional)
     * @param functionCode    Function Code (101 = estimated, 200 = exact amount, 281 = balance request)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack1200(String pan, String processingCode, String amount,
            String stan, String posEntryMode, String currencyCode,
            String field55, String terminalId, String merchantId, String pinBlock,
            String functionCode) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 1200 (Financial Transaction Request) ===");
            LogUtil.e(TAG, "  Function Code: " + (functionCode != null ? functionCode : "200"));
            
            // Validate function code
            if (functionCode == null || functionCode.isEmpty()) {
                functionCode = "200"; // Default: exact amount
            }
            if (!functionCode.equals("101") && !functionCode.equals("200") && !functionCode.equals("281")) {
                LogUtil.e(TAG, "  ⚠️ Invalid function code: " + functionCode + ", using default 200");
                functionCode = "200";
            }

            // Build 1200 message from scratch (similar to pack0100 but with additional mandatory fields)
            StringBuilder buffer = new StringBuilder();

            // MTI: 1200 = Financial Transaction Request
            buffer.append("31323030"); // ASCII "1200" in hex

            // Determine if PAN, Expiry Date, and Card Sequence Number are present
            boolean includePan = (pan != null && !pan.isEmpty());
            String expiryDate = extractExpiryDateFromField55(field55);
            boolean includeExpiryDate = (expiryDate != null && expiryDate.length() >= 4);
            String cardSequenceNumber = extractCardSequenceNumberFromField55(field55);
            boolean includeCardSequenceNumber = (cardSequenceNumber != null && cardSequenceNumber.length() > 0);
            boolean includePinBlock = (pinBlock != null && !pinBlock.isEmpty() && pinBlock.length() == 16);

            // Build fields list for bitmap - 1200 has additional mandatory fields: DE5, DE9, DE50
            List<Integer> fieldList = new ArrayList<>();
            fieldList.add(1); // DE1: Secondary Bitmap
            if (includePan) {
                fieldList.add(2); // DE2: PAN
            }
            fieldList.add(3); // DE3: Processing Code
            fieldList.add(4); // DE4: Amount
            fieldList.add(5); // DE5: Settlement Amount (MANDATORY for 1200)
            fieldList.add(6); // DE6: Cardholder Billing Amount
            fieldList.add(7); // DE7: Transmission Date and Time
            fieldList.add(9); // DE9: Exchange rate, settlement (MANDATORY for 1200)
            fieldList.add(10); // DE10: Cardholder Billing Exchange Rate
            fieldList.add(11); // DE11: STAN
            fieldList.add(12); // DE12: Transaction Local Date and Time
            if (includeExpiryDate) {
                fieldList.add(14); // DE14: Expiry Date
            }
            fieldList.add(15); // DE15: Settlement Date
            fieldList.add(16); // DE16: Exchange Date
            fieldList.add(18); // DE18: Merchant Type
            fieldList.add(19); // DE19: Acquiring Institution Country Code
            fieldList.add(21); // DE21: Forwarding Institution Country Code
            fieldList.add(22); // DE22: POS Entry Mode
            if (includeCardSequenceNumber) {
                fieldList.add(23); // DE23: Card Sequence Number
            }
            fieldList.add(24); // DE24: Function Code
            fieldList.add(27); // DE27: Authorization Code Length
            fieldList.add(32); // DE32: Acquiring Institution Identification Code
            fieldList.add(33); // DE33: Forwarding Institution Identification Code
            fieldList.add(37); // DE37: Retrieval Reference Number
            fieldList.add(41); // DE41: Card Acceptor Terminal Identifier
            fieldList.add(42); // DE42: Card Acceptor Identification Code
            fieldList.add(43); // DE43: Card Acceptor Name and Address
            fieldList.add(48); // DE48: Additional Private Data
            fieldList.add(49); // DE49: Transaction Currency Code
            fieldList.add(50); // DE50: Reconciliation Currency Code (MANDATORY for 1200)
            fieldList.add(51); // DE51: Cardholder Billing Currency Code
            fieldList.add(53); // DE53: Security Check Data
            fieldList.add(60); // DE60: Reserved for National Use
            fieldList.add(61); // DE61: Reserved for National Use
            fieldList.add(62); // DE62: Reserved for Private Use - CPS Data
            if (includePinBlock) {
                fieldList.add(52); // DE52: PIN Block
            }
            fieldList.add(55); // DE55: ICC Data (Field 55)
            fieldList.add(124); // DE124: Transaction Originator Institution Identification Code
            fieldList.add(128); // DE128: Message Authentication Code

            String bitmap = buildBitmap(fieldList.stream().mapToInt(i -> i).toArray());
            buffer.append(bitmap);

            // Secondary bitmap (always included if DE1 is set)
            long secondaryBitmap = 0L;
            secondaryBitmap |= (1L << (63 - 59)); // DE124
            secondaryBitmap |= (1L << (63 - 63)); // DE128
            String secondaryBitmapHex = String.format("%016X", secondaryBitmap);
            buffer.append(secondaryBitmapHex);

            // DE2: PAN
            if (includePan) {
                buffer.append(formatPan(pan));
            }

            // DE3: Processing Code
            buffer.append(asciiToHex(zeroPadNumeric(processingCode, 6)));

            // DE4: Amount, Authorized
            String paddedAmount = zeroPadNumeric(amount != null ? amount : "0", 12);
            buffer.append(asciiToHex(paddedAmount));

            // DE5: Settlement Amount (MANDATORY for 1200) - same as DE4 for same currency
            buffer.append(asciiToHex(paddedAmount));
            LogUtil.e(TAG, "  DE5 (Settlement Amount): " + paddedAmount);

            // DE6: Cardholder Billing Amount
            buffer.append(asciiToHex(paddedAmount));

            // DE7: Transmission Date and Time
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyMMddHHmm", Locale.US);
            utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            buffer.append(asciiToHex(utcFormat.format(new Date())));

            // DE9: Exchange rate, settlement (MANDATORY for 1200) - default 1:1
            buffer.append(asciiToHex("00000001")); // 1:1 conversion rate
            LogUtil.e(TAG, "  DE9 (Exchange rate, settlement): 00000001 (1:1)");

            // DE10: Cardholder Billing Conversion Rate
            buffer.append(asciiToHex("00000001"));

            // DE11: STAN
            buffer.append(asciiToHex(zeroPadNumeric(stan, 6)));

            // DE12: Transaction Local Date and Time
            SimpleDateFormat localFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
            buffer.append(asciiToHex(localFormat.format(new Date())));

            // DE14: Expiry Date
            if (includeExpiryDate) {
                buffer.append(asciiToHex(expiryDate.substring(0, 4)));
            }

            // DE15: Settlement Date
            SimpleDateFormat settlementFormat = new SimpleDateFormat("yyMMdd", Locale.US);
            buffer.append(asciiToHex(settlementFormat.format(new Date())));

            // DE16: Exchange Date
            SimpleDateFormat exchangeFormat = new SimpleDateFormat("MMdd", Locale.US);
            buffer.append(asciiToHex(exchangeFormat.format(new Date())));

            // DE18: Merchant Type
            buffer.append(asciiToHex("6011"));

            // DE19: Acquiring Institution Country Code
            buffer.append(asciiToHex("818"));

            // DE21: Forwarding Institution Country Code
            buffer.append(asciiToHex("818"));

            // DE22: POS Entry Mode
            buffer.append(asciiToHex(zeroPadNumeric(posEntryMode, 3)));

            // DE23: Card Sequence Number
            if (includeCardSequenceNumber) {
                buffer.append(asciiToHex(zeroPadNumeric(cardSequenceNumber, 3)));
            }

            // DE24: Function Code
            buffer.append(asciiToHex(zeroPadNumeric(functionCode, 3)));

            // DE27: Authorization Code Length
            buffer.append(asciiToHex("6"));

            // DE32: Acquiring Institution Identification Code
            String acquiringInstId = "00000000000";
            buffer.append(asciiToHex(String.format("%02d", acquiringInstId.length())));
            buffer.append(asciiToHex(acquiringInstId));

            // DE33: Forwarding Institution Identification Code
            String forwardingInstId = "00000000000";
            buffer.append(asciiToHex(String.format("%02d", forwardingInstId.length())));
            buffer.append(asciiToHex(forwardingInstId));

            // DE37: Retrieval Reference Number
            buffer.append(asciiToHex("000000000000")); // Placeholder, backend generates

            // DE41: Card Acceptor Terminal Identifier
            buffer.append(asciiToHex(zeroPadNumeric(terminalId, 8)));

            // DE42: Card Acceptor Identification Code
            buffer.append(asciiToHex(zeroPadNumeric(merchantId, 15)));

            // DE43: Card Acceptor Name and Address
            buffer.append(asciiToHex(String.format("%02d", "NeoPayPlus".length())));
            buffer.append(asciiToHex("NeoPayPlus"));

            // DE48: Additional Private Data
            String cardBrand = null;
            if (field55 != null && !field55.isEmpty()) {
                if (field55.contains("A000000003")) {
                    cardBrand = "VISA";
                } else if (field55.contains("A000000004") || field55.contains("A000000005")) {
                    cardBrand = "MASTERCARD";
                } else if (field55.contains("A000000732")) {
                    cardBrand = "MEEZA";
                }
            }
            if (cardBrand == null && pan != null && pan.length() >= 6) {
                String bin = pan.substring(0, 6);
                if (bin.startsWith("4")) {
                    cardBrand = "VISA";
                } else if (bin.startsWith("5") || bin.startsWith("2")) {
                    cardBrand = "MASTERCARD";
                }
            }
            String arqcResult = (field55 != null && !field55.isEmpty()) ? "2" : null;
            String messageReasonCode = "0000";
            String de48Hex = De48Builder.buildDe48ForAuthorization(cardBrand, arqcResult, messageReasonCode);
            if (de48Hex != null && !de48Hex.isEmpty()) {
                buffer.append(de48Hex);
            }

            // DE49: Transaction Currency Code
            String finalCurrencyCode = currencyCode != null && !currencyCode.isEmpty() ? currencyCode : "818";
            buffer.append(asciiToHex(zeroPadNumeric(finalCurrencyCode, 3)));

            // DE50: Reconciliation Currency Code (MANDATORY for 1200) - same as DE49
            buffer.append(asciiToHex(zeroPadNumeric(finalCurrencyCode, 3)));
            LogUtil.e(TAG, "  DE50 (Reconciliation Currency Code): " + finalCurrencyCode);

            // DE51: Cardholder Billing Currency Code
            buffer.append(asciiToHex(zeroPadNumeric(finalCurrencyCode, 3)));

            // DE52: PIN Block
            if (includePinBlock) {
                buffer.append(pinBlock);
            }

            // DE53: Security Check Data
            buffer.append(asciiToHex("0000000000000000"));

            // DE55: ICC Data (EMV Field 55)
            if (field55 != null && !field55.isEmpty()) {
                int byteLength = field55.length() / 2;
                if (byteLength > 255) {
                    byteLength = 255;
                }
                int thousands = byteLength / 1000;
                int hundreds = (byteLength / 100) % 10;
                int tens = (byteLength / 10) % 10;
                int ones = byteLength % 10;
                byte bcdByte1 = (byte) ((thousands << 4) | hundreds);
                byte bcdByte2 = (byte) ((tens << 4) | ones);
                String lengthHex = String.format("%02X%02X", bcdByte1 & 0xFF, bcdByte2 & 0xFF);
                buffer.append(lengthHex);
                buffer.append(field55);
            }

            // DE60: Reserved for National Use
            String de60Value = "020"; // AuthReliability=0, ChipCondition=2, VSDC=0
            buffer.append(asciiToHex(String.format("%03d", de60Value.length())));
            buffer.append(asciiToHex(de60Value));

            // DE61: Reserved for National Use
            StringBuilder de61Builder = new StringBuilder();
            de61Builder.append("039"); // Type
            de61Builder.append("002"); // Length (2 bytes)
            de61Builder.append("  ");  // Value (2 spaces = empty)
            String de61Value = de61Builder.toString();
            buffer.append(asciiToHex(String.format("%03d", de61Value.length())));
            buffer.append(asciiToHex(de61Value));

            // DE62: Reserved for Private Use - CPS Data
            StringBuilder de62Builder = new StringBuilder();
            de62Builder.append("F01"); // Type
            de62Builder.append("001"); // Length (1 byte)
            de62Builder.append("A");   // Value ('A' = qualification successful)
            de62Builder.append("F02"); // Type
            de62Builder.append("015"); // Length (15 bytes)
            String transactionId = stan != null ? stan : "000000000000000";
            if (transactionId.length() < 15) {
                transactionId = String.format("%-15s", transactionId).substring(0, 15);
            } else if (transactionId.length() > 15) {
                transactionId = transactionId.substring(0, 15);
            }
            de62Builder.append(transactionId); // Value
            de62Builder.append("F03"); // Type
            de62Builder.append("004"); // Length (4 bytes)
            de62Builder.append("0000"); // Value (default zeros)
            de62Builder.append("F04"); // Type
            de62Builder.append("001"); // Length (1 byte)
            de62Builder.append("N");   // Value ('N' = Other)
            de62Builder.append("F05"); // Type
            de62Builder.append("002"); // Length (2 bytes)
            de62Builder.append("00");  // Value (default zeros)
            de62Builder.append("F06"); // Type
            de62Builder.append("001"); // Length (1 byte)
            de62Builder.append(" ");   // Value (space = not applicable)
            String de62Value = de62Builder.toString();
            buffer.append(asciiToHex(String.format("%03d", de62Value.length())));
            buffer.append(asciiToHex(de62Value));

            // DE124: Transaction Originator Institution Identification Code
            String originatorInstId = "00000000000";
            buffer.append(asciiToHex(String.format("%02d", originatorInstId.length())));
            buffer.append(asciiToHex(originatorInstId));

            // DE128: Message Authentication Code
            buffer.append("00000000000000000000000000000000");

            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);

            LogUtil.e(TAG, "✓ ISO8583 1200 packed - total length: " + isoFrame.length + " bytes");
            LogUtil.e(TAG, "  Hex: " + hexString.substring(0, Math.min(100, hexString.length())) + "...");

            return isoFrame;
        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error packing ISO8583 1200: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Packing ISO8583 1200", e);
            return new byte[0];
        }
    }

    /**
     * Pack ISO8583 Acquirer Reversal Advice (1420) - PowerCARD format
     * 
     * @param rrn            Retrieval Reference Number (12 digits)
     * @param amount         Original transaction amount (minor currency units)
     * @param stan           Systems Trace Audit Number (6 digits)
     * @param currencyCode   Currency Code (3 digits, e.g., "818" = EGP)
     * @param terminalId     Terminal ID
     * @param merchantId     Merchant ID
     * @param functionCode   Function Code (400 = full, 401 = status undefined, 402 = partial)
     * @param messageReasonCode Message Reason Code (4 digits, e.g., "4000" = Cancellation by cardholder)
     * @param originalAmount Original amount for partial reversal (12 digits, optional)
     * @param reconciliationAmount Reconciliation amount (12 digits, optional)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack1420(String rrn, String amount, String stan,
            String currencyCode, String terminalId, String merchantId,
            String functionCode, String messageReasonCode,
            String originalAmount, String reconciliationAmount) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 1420 (Acquirer Reversal Advice) ===");
            LogUtil.e(TAG, "  RRN: " + rrn);
            LogUtil.e(TAG, "  Function Code: " + (functionCode != null ? functionCode : "400"));
            LogUtil.e(TAG, "  Message Reason Code: " + (messageReasonCode != null ? messageReasonCode : "4001"));

            // Validate function code
            if (functionCode == null || functionCode.isEmpty()) {
                functionCode = "400"; // Default: full reversal
            }
            if (!functionCode.equals("400") && !functionCode.equals("401") && !functionCode.equals("402")) {
                LogUtil.e(TAG, "  ⚠️ Invalid function code: " + functionCode + ", using default 400");
                functionCode = "400";
            }

            // Validate message reason code
            if (messageReasonCode == null || messageReasonCode.isEmpty()) {
                messageReasonCode = "4001"; // Default: Not specified
            }

            StringBuilder buffer = new StringBuilder();

            // MTI: 1420 = Acquirer Reversal Advice
            buffer.append("31343230"); // ASCII "1420" in hex
            LogUtil.e(TAG, "  MTI: 1420 (Acquirer Reversal Advice)");

            // Build fields array - similar to 0400 but with DE25 and DE30
            List<Integer> fieldList = new ArrayList<>();
            fieldList.add(1); // DE1: Secondary Bitmap
            fieldList.add(2); // DE2: PAN (from original transaction)
            fieldList.add(3); // DE3: Processing Code
            fieldList.add(4); // DE4: Amount
            fieldList.add(7); // DE7: Transmission Date and Time
            fieldList.add(11); // DE11: STAN
            fieldList.add(12); // DE12: Transaction Local Date and Time
            fieldList.add(24); // DE24: Function Code
            fieldList.add(25); // DE25: Message Reason Code (mandatory for 1420)
            fieldList.add(30); // DE30: Initial Amounts (conditional - mandatory for partial reversal)
            fieldList.add(37); // DE37: RRN
            fieldList.add(41); // DE41: Card Acceptor Terminal Identifier
            fieldList.add(42); // DE42: Card Acceptor Identification Code
            fieldList.add(43); // DE43: Card Acceptor Name and Address
            fieldList.add(49); // DE49: Currency Code
            fieldList.add(90); // DE90: Original Data Elements (mandatory for reversal)
            fieldList.add(124); // DE124: Transaction Originator Institution Identification Code
            fieldList.add(128); // DE128: Message Authentication Code

            LogUtil.e(TAG, "  Fields to include in bitmap: " + fieldList.toString());
            int[] fields = new int[fieldList.size()];
            for (int i = 0; i < fieldList.size(); i++) {
                fields[i] = fieldList.get(i);
            }
            String bitmap = buildBitmap(fields);
            LogUtil.e(TAG, "  Built bitmap (hex): " + bitmap);
            buffer.append(bitmap);

            // DE1: Secondary Bitmap
            long secondaryBitmap = 0L;
            secondaryBitmap |= (1L << (63 - 59)); // DE124
            secondaryBitmap |= (1L << (63 - 63)); // DE128
            String secondaryBitmapHex = String.format("%016X", secondaryBitmap);
            buffer.append(secondaryBitmapHex);
            LogUtil.e(TAG, "  DE1 (Secondary Bitmap): " + secondaryBitmapHex);

            // DE2: PAN (placeholder - from original transaction)
            buffer.append("0000000000000000");
            LogUtil.e(TAG, "  DE2 (PAN): placeholder");

            // DE3: Processing Code
            buffer.append(asciiToHex("000000"));
            LogUtil.e(TAG, "  DE3 (Processing Code): 000000");

            // DE4: Amount
            String paddedAmount = "000000000000";
            if (amount != null && !amount.isEmpty()) {
                paddedAmount = zeroPadNumeric(amount, 12);
            }
            buffer.append(asciiToHex(paddedAmount));
            LogUtil.e(TAG, "  DE4 (Amount): " + paddedAmount);

            // DE7: Transmission Date and Time (UTC)
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyMMddHHmm", Locale.US);
            utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String transmissionDateTime = utcFormat.format(new Date());
            buffer.append(asciiToHex(transmissionDateTime));
            LogUtil.e(TAG, "  DE7 (Transmission Date and Time UTC): " + transmissionDateTime);

            // DE11: STAN
            if (stan != null && !stan.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(stan, 6)));
            } else {
                buffer.append(asciiToHex("000000"));
            }

            // DE12: Transaction Local Date and Time
            SimpleDateFormat localFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
            String transactionDateTime = localFormat.format(new Date());
            buffer.append(asciiToHex(transactionDateTime));
            LogUtil.e(TAG, "  DE12 (Transaction Local Date and Time): " + transactionDateTime);

            // DE24: Function Code
            buffer.append(asciiToHex(zeroPadNumeric(functionCode, 3)));
            LogUtil.e(TAG, "  DE24 (Function Code): " + functionCode);

            // DE25: Message Reason Code (4 digits) - mandatory for 1420
            buffer.append(asciiToHex(zeroPadNumeric(messageReasonCode, 4)));
            LogUtil.e(TAG, "  DE25 (Message Reason Code): " + messageReasonCode);

            // DE30: Initial Amounts (24 digits) - conditional, mandatory for partial reversal
            // Format: positions 1-12 = transaction amount, positions 13-24 = reconciliation amount
            String de30 = "";
            if (functionCode.equals("402") && originalAmount != null && reconciliationAmount != null) {
                // Partial reversal - include both amounts
                de30 = zeroPadNumeric(originalAmount, 12) + zeroPadNumeric(reconciliationAmount, 12);
            } else {
                // Full reversal - use same amount for both
                de30 = paddedAmount + paddedAmount;
            }
            buffer.append(asciiToHex(de30));
            LogUtil.e(TAG, "  DE30 (Initial Amounts): " + de30);

            // DE37: RRN
            if (rrn != null && !rrn.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(rrn, 12)));
            } else {
                buffer.append(asciiToHex("000000000000"));
            }
            LogUtil.e(TAG, "  DE37 (RRN): " + (rrn != null ? rrn : "000000000000"));

            // DE41: Card Acceptor Terminal Identifier
            String paddedTerminalId = (terminalId != null ? terminalId : "00000001").substring(0, Math.min(8, (terminalId != null ? terminalId : "00000001").length()));
            paddedTerminalId = String.format("%-8s", paddedTerminalId).substring(0, 8);
            buffer.append(asciiToHex(paddedTerminalId));
            LogUtil.e(TAG, "  DE41 (Terminal ID): " + paddedTerminalId);

            // DE42: Card Acceptor Identification Code
            String paddedMerchantId = (merchantId != null ? merchantId : "000000000000001").substring(0, Math.min(15, (merchantId != null ? merchantId : "000000000000001").length()));
            paddedMerchantId = String.format("%-15s", paddedMerchantId).substring(0, 15);
            buffer.append(asciiToHex(paddedMerchantId));
            LogUtil.e(TAG, "  DE42 (Merchant ID): " + paddedMerchantId);

            // DE43: Card Acceptor Name and Address
            String merchantName = "MERCHANT NAME";
            String merchantNameLength = String.format("%02d", merchantName.length());
            buffer.append(asciiToHex(merchantNameLength));
            buffer.append(asciiToHex(merchantName));
            LogUtil.e(TAG, "  DE43 (Merchant Name): " + merchantName);

            // DE49: Currency Code
            if (currencyCode != null && !currencyCode.isEmpty()) {
                buffer.append(asciiToHex(zeroPadNumeric(currencyCode, 3)));
            } else {
                buffer.append(asciiToHex("818"));
            }
            LogUtil.e(TAG, "  DE49 (Currency Code): " + (currencyCode != null ? currencyCode : "818"));

            // DE90: Original Data Elements (42 bytes) - mandatory for reversal
            // Format: Original MTI (4) + Original STAN (6) + Original Transmission Date/Time (10) + Original RRN (12) + Original Amount (12)
            String originalMti = "0100"; // Assume original was authorization
            String originalStan = stan != null ? stan : "000000";
            String originalTransmissionDateTime = transmissionDateTime; // Use current for now
            String originalRrn = rrn != null ? rrn : "000000000000";
            String originalAmountDe90 = paddedAmount;
            String de90 = originalMti + originalStan + originalTransmissionDateTime + originalRrn + originalAmountDe90;
            // Pad to 42 bytes if needed
            while (de90.length() < 42) {
                de90 += "0";
            }
            de90 = de90.substring(0, 42);
            buffer.append(asciiToHex(de90));
            LogUtil.e(TAG, "  DE90 (Original Data Elements): " + de90.substring(0, 20) + "...");

            // DE124: Transaction Originator Institution Identification Code
            String originatorInstId = "00000001";
            String originatorInstIdLength = String.format("%02d", originatorInstId.length());
            buffer.append(asciiToHex(originatorInstIdLength));
            buffer.append(asciiToHex(originatorInstId));
            LogUtil.e(TAG, "  DE124 (Transaction Originator Institution ID): " + originatorInstId);

            // DE128: Message Authentication Code (placeholder)
            buffer.append(asciiToHex("00000000000000000000000000000000"));
            LogUtil.e(TAG, "  DE128 (Message Authentication Code): placeholder");

            // Convert hex string to bytes
            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);

            LogUtil.e(TAG, "✓ ISO8583 1420 packed - total length: " + isoFrame.length + " bytes");
            return isoFrame;

        } catch (Exception e) {
            LogUtil.e(TAG, "✗ Error packing ISO8583 1420: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Packing ISO8583 1420", e);
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
     * Extract expiry date from Field 55 (EMV TLV data)
     * Looks for tag 59 (Card Expiration Date) or tag 5F24 (Application Expiration Date)
     * 
     * @param field55 Field 55 hex string (EMV TLV data)
     * @return Expiry date in YYMM format, or null if not found
     */
    private static String extractExpiryDateFromField55(String field55) {
        if (field55 == null || field55.isEmpty()) {
            return null;
        }

        try {
            String hexStr = field55.replaceAll("\\s", "").toUpperCase();

            // Try tag 59 first (Card Expiration Date - YYMM format, BCD encoded)
            int tag59Index = hexStr.indexOf("59");
            if (tag59Index != -1 && tag59Index % 2 == 0) {
                int lengthStart = tag59Index + 2;
                if (lengthStart + 2 <= hexStr.length()) {
                    int length = Integer.parseInt(hexStr.substring(lengthStart, lengthStart + 2), 16);
                    int valueStart = lengthStart + 2;
                    int valueEnd = valueStart + length * 2;
                    if (valueEnd <= hexStr.length()) {
                        String expiryHex = hexStr.substring(valueStart, valueEnd);
                        // Remove padding (F characters)
                        String expiryClean = expiryHex.replaceAll("F+$", "");
                        if (expiryClean.length() >= 4) {
                            return expiryClean.substring(0, 4); // YYMM
                        }
                    }
                }
            }

            // Try tag 5F24 (Application Expiration Date - YYMMDD format, BCD encoded)
            int tag5F24Index = hexStr.indexOf("5F24");
            if (tag5F24Index != -1 && tag5F24Index % 2 == 0) {
                int lengthStart = tag5F24Index + 4;
                if (lengthStart + 2 <= hexStr.length()) {
                    int length = Integer.parseInt(hexStr.substring(lengthStart, lengthStart + 2), 16);
                    int valueStart = lengthStart + 2;
                    int valueEnd = valueStart + length * 2;
                    if (valueEnd <= hexStr.length()) {
                        String expiryHex = hexStr.substring(valueStart, valueEnd);
                        String expiryClean = expiryHex.replaceAll("F+$", "");
                        if (expiryClean.length() >= 4) {
                            return expiryClean.substring(0, 4); // YYMM (first 4 digits)
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            LogUtil.e(TAG, "Error extracting expiry date from Field 55: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extract card sequence number from Field 55 (EMV TLV data)
     * Looks for tag 5F34 (Application PAN Sequence Number)
     * 
     * @param field55 Field 55 hex string (EMV TLV data)
     * @return Card sequence number, or null if not found
     */
    private static String extractCardSequenceNumberFromField55(String field55) {
        if (field55 == null || field55.isEmpty()) {
            return null;
        }

        try {
            String hexStr = field55.replaceAll("\\s", "").toUpperCase();

            // Try tag 5F34 (Application PAN Sequence Number)
            int tag5F34Index = hexStr.indexOf("5F34");
            if (tag5F34Index != -1 && tag5F34Index % 2 == 0) {
                int lengthStart = tag5F34Index + 4;
                if (lengthStart + 2 <= hexStr.length()) {
                    int length = Integer.parseInt(hexStr.substring(lengthStart, lengthStart + 2), 16);
                    int valueStart = lengthStart + 2;
                    int valueEnd = valueStart + length * 2;
                    if (valueEnd <= hexStr.length()) {
                        String csnHex = hexStr.substring(valueStart, valueEnd);
                        // Convert hex to decimal
                        try {
                            int csn = Integer.parseInt(csnHex, 16);
                            return String.valueOf(csn);
                        } catch (NumberFormatException e) {
                            // If hex parsing fails, try BCD
                            if (csnHex.length() >= 2) {
                                int high = Character.digit(csnHex.charAt(0), 16);
                                int low = Character.digit(csnHex.charAt(1), 16);
                                if (high >= 0 && low >= 0) {
                                    int csn = high * 10 + low;
                                    return String.valueOf(csn);
                                }
                            }
                        }
                    }
                }
            }

            return null;
        } catch (Exception e) {
            LogUtil.e(TAG, "Error extracting card sequence number from Field 55: " + e.getMessage());
            return null;
        }
    }

    /**
     * Pack ISO8583 Network Management Request (1804) - PowerCARD
     * 
     * @param functionCode     Function code (801=Logon, 802=Logout, 803=Echo Test, etc.)
     * @param stan              Systems Trace Audit Number (6 digits)
     * @param forwardingInstId  Forwarding Institution Identification Code (LLVAR n..11)
     * @return Raw ISO8583 binary frame (bytes)
     */
    public static byte[] pack1804(String functionCode, String stan, String forwardingInstId) {
        try {
            LogUtil.e(TAG, "=== Packing ISO8583 1804 (Network Management Request) ===");
            LogUtil.e(TAG, "  Function Code: " + functionCode);

            StringBuilder buffer = new StringBuilder();

            // MTI: 1804 = Network Management Request
            buffer.append("31383034"); // ASCII "1804" in hex

            // Primary Bitmap (64 bits, binary)
            // Set bits for fields present: 1 (Secondary bitmap), 7, 11, 12, 24, 25, 33, 37, 39, 128
            // Note: DE1 (secondary bitmap) is mandatory for 1804
            String bitmap = buildBitmap(new int[] { 1, 7, 11, 12, 24, 25, 33, 37, 39, 128 });
            buffer.append(bitmap);

            // DE1: Secondary Bitmap (64 bits, binary) - all zeros for now (no fields 65-128)
            buffer.append("0000000000000000"); // 8 bytes of zeros

            // DE7: Transmission Date and Time (UTC format: YYMMDDhhmm) - 10 digits
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm", Locale.US);
            dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String transmissionDateTime = dateFormat.format(new Date());
            buffer.append(transmissionDateTime);

            // DE11: STAN (Systems Trace Audit Number) - 6 digits
            if (stan != null && !stan.isEmpty()) {
                buffer.append(zeroPadNumeric(stan, 6));
            } else {
                // Generate STAN from current time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.US);
                String time = timeFormat.format(new Date());
                buffer.append(zeroPadNumeric(time, 6));
            }

            // DE12: Transaction Local Date and Time (YYMMDDhhmmss) - 12 digits
            SimpleDateFormat localDateTimeFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
            String localDateTime = localDateTimeFormat.format(new Date());
            buffer.append(localDateTime);

            // DE24: Function Code - 3 digits
            if (functionCode != null && !functionCode.isEmpty()) {
                buffer.append(zeroPadNumeric(functionCode, 3));
            } else {
                buffer.append("801"); // Default: Session logon
            }

            // DE25: Message Reason Code - 4 digits (mandatory but can be zeros for logon/logout)
            buffer.append("0000");

            // DE33: Forwarding Institution Identification Code (LLVAR n..11)
            if (forwardingInstId != null && !forwardingInstId.isEmpty()) {
                // LLVAR format: 1 byte BCD length + value
                int length = forwardingInstId.length();
                if (length > 11) {
                    length = 11; // Max 11 digits
                    forwardingInstId = forwardingInstId.substring(0, 11);
                }
                // BCD encode length (1 byte)
                byte lengthByte = (byte) length;
                buffer.append(String.format("%02X", lengthByte & 0xFF));
                buffer.append(forwardingInstId);
            } else {
                // Default: empty (length = 0)
                buffer.append("00");
            }

            // DE37: Retrieval Reference Number (RRN) - 12 digits
            // Generate RRN from timestamp
            SimpleDateFormat rrnFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
            String rrn = rrnFormat.format(new Date());
            buffer.append(zeroPadNumeric(rrn, 12));

            // DE39: Action Code - 3 digits (not set in request, will be in response)
            buffer.append("000");

            // DE128: Authentication Message Code - 16 bytes (MAC)
            // For now, use zeros (will be calculated with proper MAC key in production)
            buffer.append("00000000000000000000000000000000"); // 32 hex chars = 16 bytes

            // Convert hex string to bytes
            String hexString = buffer.toString();
            byte[] isoFrame = hexStringToBytes(hexString);

            LogUtil.e(TAG, "✓ ISO8583 1804 packed - total length: " + isoFrame.length + " bytes");
            LogUtil.e(TAG, "  Function Code: " + functionCode);

            return isoFrame;

        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Packing ISO8583 1804", e);
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
