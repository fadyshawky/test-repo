package com.neo.neopayplus.processing;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.payservice.AidlConstantsV2;

/**
 * CVM (Cardholder Verification Method) Handler
 * 
 * Handles CVM result extraction and PIN block decision logic.
 * Extracted from ProcessingActivity to improve separation of concerns.
 */
public class CvmHandler {

    private static final String TAG = Constant.TAG;
    private final EMVOptV2 emvOptV2;

    public CvmHandler(EMVOptV2 emvOptV2) {
        this.emvOptV2 = emvOptV2;
    }

    /**
     * CVM Result data
     */
    public static class CvmResult {
        public final String code;
        public final String description;
        public final boolean shouldSendPinToBackend;

        private CvmResult(String code, String description, boolean shouldSendPinToBackend) {
            this.code = code;
            this.description = description;
            this.shouldSendPinToBackend = shouldSendPinToBackend;
        }

        public static CvmResult noCvm() {
            return new CvmResult("00", "No CVM required", false);
        }

        public static CvmResult onlinePin(String code) {
            return new CvmResult(code, "Online PIN required", true);
        }

        public static CvmResult offlinePin() {
            return new CvmResult("42", "Offline PIN verified by card", false);
        }

        public static CvmResult cdcvm(String code) {
            return new CvmResult(code, "CDCVM performed (Apple Pay/Google Pay)", false);
        }

        public static CvmResult unknown(String code) {
            return new CvmResult(code, "Unknown CVM code: " + code, false);
        }
    }

    /**
     * Extract CVM Result code (9F34) from EMV kernel
     * Returns the CVM code to determine if PIN should be sent to backend
     * 
     * @return CVM Result code (e.g., "00", "01", "02", "42", "03", "5E") or null if
     *         not available
     */
    public String extractCvmResultCode() {
        try {
            byte[] outData = new byte[256];
            int len = emvOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "9F34", outData);
            if (len > 0) {
                byte[] cvmData = new byte[len];
                System.arraycopy(outData, 0, cvmData, 0, len);
                String hexValue = ByteUtil.bytes2HexStr(cvmData);

                // Extract actual CVM value (skip tag and length if present)
                String actualCvmValue = hexValue;
                if (hexValue.startsWith("9F34")) {
                    // Format: 9F34 + length + value
                    if (hexValue.length() >= 6) {
                        String lengthHex = hexValue.substring(4, 6);
                        int length = Integer.parseInt(lengthHex, 16);
                        if (hexValue.length() >= 6 + length * 2) {
                            actualCvmValue = hexValue.substring(6, 6 + length * 2);
                        }
                    }
                }

                // Extract first byte (CVM code)
                if (actualCvmValue.length() >= 2) {
                    String cvmCode = actualCvmValue.substring(0, 2);
                    LogUtil.e(TAG, "Extracted CVM Result code: " + cvmCode + " from hex value: " + actualCvmValue);
                    return cvmCode;
                }
            } else {
                LogUtil.e(TAG, "⚠️ Could not extract CVM Result (9F34) - tag not available");
            }
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Extracting CVM Result code", e);
        }
        return null;
    }

    /**
     * Determine PIN handling based on CVM Result code
     * 
     * @param cvmResultCode CVM Result code from tag 9F34 (first byte: 00=no CVM,
     *                      01/02=online PIN, 42=offline PIN)
     * @param pinType       PIN type from EMV kernel (0=online PIN, 1=offline PIN) -
     *                      used as fallback if CVM code unavailable
     * @return CvmResult with decision on whether to send PIN to backend
     */
    public CvmResult determinePinHandling(String cvmResultCode, int pinType) {
        if (cvmResultCode != null) {
            switch (cvmResultCode) {
                case "00":
                    LogUtil.e(TAG, "✓ No PIN required - skipping PIN block");
                    return CvmResult.noCvm();

                case "01":
                case "02":
                    LogUtil.e(TAG,
                            "✓ Online PIN detected (code: " + cvmResultCode + ") - PIN block will be sent to backend");
                    return CvmResult.onlinePin(cvmResultCode);

                case "42":
                    LogUtil.e(TAG,
                            "✓ Offline PIN detected (code: 42) - Card verified PIN, do NOT send PIN block to backend");
                    return CvmResult.offlinePin();

                case "03":
                case "5E":
                    LogUtil.e(TAG, "✓ CDCVM detected - no PIN block needed");
                    return CvmResult.cdcvm(cvmResultCode);

                default:
                    LogUtil.e(TAG, "⚠️ Unknown CVM code: " + cvmResultCode + " - not sending PIN block");
                    return CvmResult.unknown(cvmResultCode);
            }
        } else {
            // Fallback: if CVM result not available, check PIN type from EMV kernel
            // pinType: 0=online PIN, 1=offline PIN
            if (pinType == 0) {
                LogUtil.e(TAG, "⚠️ CVM result not available, but PIN type indicates online PIN - will send PIN block");
                return CvmResult.onlinePin("01");
            } else {
                LogUtil.e(TAG, "⚠️ CVM result not available, PIN type indicates offline PIN - not sending PIN block");
                return CvmResult.offlinePin();
            }
        }
    }
}
