package com.neo.neopayplus.emv;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.emv.config.EmvConfigurationApplier;
import com.neo.neopayplus.emv.utils.CvmRequirementLogger;
import com.neo.neopayplus.emv.utils.PanExtractor;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import javax.inject.Inject;
import java.util.List;

/**
 * EMV Handler - Simplified EMV Transaction Management
 * 
 * Based on colleague's approach using EMVListenerV2.Stub with unified callback
 * pattern.
 * 
 * Refactored to use Dependency Injection (DIP - Dependency Inversion
 * Principle).
 * Dependencies are injected via constructor, making the class testable and
 * following SOLID principles.
 * 
 * Usage:
 * 1. Create instance with callback (dependencies injected via Hilt)
 * 2. Call startCardDetection() to detect card
 * 3. Handle callbacks via EMVCallback interface
 * 4. Call appropriate import methods based on callback step
 */
public class EMVHandler {
    private static final String TAG = "EMVHandler";

    private final Context context;
    private final EMVCallback callback;
    private final ReadCardOptV2 readCardOptV2;
    private final EMVOptV2 emvOptV2;
    private final PinPadOptV2 pinPadOptV2;

    // Transaction state
    private String currentAmount = "0";
    private int currentCardType = 0;
    private String currentPan = null;
    private int currentPinType = 0;
    private String currentTransactionType = "00"; // "00" = Purchase, "20" = Refund (ISO8583)

    // Guard to prevent double EMV process start (findRFCard can be called multiple
    // times)
    private volatile boolean emvProcessStarted = false;

    /**
     * Constructor with dependency injection.
     * 
     * Note: This constructor is package-private. Use EmvHandlerFactory to create
     * instances.
     * 
     * @param context       Application context
     * @param callback      EMV callback interface
     * @param readCardOptV2 Read card service (injected via Hilt)
     * @param emvOptV2      EMV service (injected via Hilt)
     * @param pinPadOptV2   PIN pad service (injected via Hilt)
     */
    EMVHandler(
            Context context,
            EMVCallback callback,
            ReadCardOptV2 readCardOptV2,
            EMVOptV2 emvOptV2,
            PinPadOptV2 pinPadOptV2) {
        this.context = context;
        this.callback = callback;
        this.readCardOptV2 = readCardOptV2;
        this.emvOptV2 = emvOptV2;
        this.pinPadOptV2 = pinPadOptV2;

        // Log service status
        Log.e(TAG, "=== EMVHandler initialized (with DI) ===");
        Log.e(TAG, "  readCardOptV2: " + (readCardOptV2 != null ? "OK" : "NULL"));
        Log.e(TAG, "  emvOptV2: " + (emvOptV2 != null ? "OK" : "NULL"));
        Log.e(TAG, "  pinPadOptV2: " + (pinPadOptV2 != null ? "OK" : "NULL"));

        if (readCardOptV2 == null || emvOptV2 == null) {
            Log.e(TAG, "  ❌ CRITICAL: SDK services not available!");
            Log.e(TAG, "     Check if PaySDK is connected. Call TransactionManager.waitForReady() first.");
        }
    }

    /**
     * Start card detection
     * 
     * @param amount          Transaction amount in minor units (e.g., "1000" for
     *                        10.00)
     * @param timeoutSeconds  Timeout for card detection
     * @param transactionType Transaction type: "purchase" or "refund" (defaults to
     *                        "purchase")
     */
    public void startCardDetection(String amount, int timeoutSeconds, String transactionType) {
        this.currentAmount = amount;
        this.emvProcessStarted = false; // Reset guard for new transaction
        // Map transaction type: "purchase" -> "00", "refund" -> "20" (ISO8583)
        this.currentTransactionType = "refund".equals(transactionType) ? "20" : "00";
        Log.e(TAG, "=== Starting Card Detection ===");
        Log.e(TAG, "Amount: " + amount);
        Log.e(TAG, "Transaction Type: " + transactionType + " -> ISO8583: " + this.currentTransactionType);
        Log.e(TAG, "Timeout: " + timeoutSeconds + " seconds");

        // Check if SDK services are available
        if (readCardOptV2 == null) {
            Log.e(TAG, "❌ CRITICAL: readCardOptV2 is NULL!");
            Log.e(TAG, "   PaySDK not connected. Cannot detect cards.");
            onResultCallBack(EMVSteps.CARD_ERROR, "Card reader service not available. Please restart the app.");
            return;
        }

        try {
            onResultCallBack(EMVSteps.CARD_DETECT, null);

            // Support both IC and NFC
            int nfcValue = AidlConstants.CardType.NFC.getValue();
            int icValue = AidlConstants.CardType.IC.getValue();
            int cardType = nfcValue | icValue;

            Log.e(TAG, "Card types enabled:");
            Log.e(TAG, "  NFC value: " + nfcValue);
            Log.e(TAG, "  IC value: " + icValue);
            Log.e(TAG, "  Combined: " + cardType);

            Log.e(TAG, "Calling readCardOptV2.checkCard()...");
            readCardOptV2.checkCard(cardType, checkCardCallback, timeoutSeconds);
            Log.e(TAG, "checkCard() called successfully - waiting for callback...");
        } catch (Exception e) {
            Log.e(TAG, "Card detection failed: " + e.getMessage());
            e.printStackTrace();
            onResultCallBack(EMVSteps.CARD_ERROR, e.getMessage());
        }
    }

    /**
     * Start card detection (backward compatibility - defaults to purchase)
     * 
     * @param amount         Transaction amount in minor units (e.g., "1000" for
     *                       10.00)
     * @param timeoutSeconds Timeout for card detection
     */
    public void startCardDetection(String amount, int timeoutSeconds) {
        startCardDetection(amount, timeoutSeconds, "purchase");
    }

    /**
     * Cancel ongoing card detection
     */
    public void cancelCardDetection() {
        try {
            readCardOptV2.cancelCheckCard();
            Log.e(TAG, "Card detection cancelled");
        } catch (Exception e) {
            Log.e(TAG, "Cancel card detection failed: " + e.getMessage());
        }
    }

    /**
     * Start EMV transaction after card is detected
     */
    private void startEmvProcess() {
        // Guard against double starts (findRFCard/findRFCardEx can both trigger)
        if (emvProcessStarted) {
            Log.e(TAG, "=== EMV Process already started - ignoring duplicate call ===");
            return;
        }
        emvProcessStarted = true;

        try {
            Log.e(TAG, "=== Starting EMV Process ===");

            // Initialize EMV process (clears all TLV)
            emvOptV2.initEmvProcess();

            // Apply TLV configuration
            applyEmvConfiguration();

            // Build transaction bundle
            Bundle bundle = new Bundle();
            bundle.putString("amount", currentAmount);
            // Transaction type: "00" = Purchase, "20" = Refund (ISO8583)
            // SAM (Secure Application Module) uses this for cryptographic operations
            bundle.putString("transType", currentTransactionType);
            Log.e(TAG, "EMV Bundle - Transaction Type: " + currentTransactionType +
                    (currentTransactionType.equals("20") ? " (Refund)" : " (Purchase)"));

            // Determine flow type based on card type
            // IMPORTANT: Always use NFC Speedup for contactless cards
            // TYPE_EMV_STANDARD causes -4125 "L2 candidate list is empty" error with NFC
            // because the card leaves the RF field after initial read, breaking the
            // standard flow.
            //
            // PIN behavior for NFC Speedup:
            // - Terminal CVM limit (DF8124) is respected by most cards
            // - Some cards (issuer policy) may force PIN regardless of amount via their CVM
            // List
            // - This is card-level behavior that cannot be overridden by terminal config
            if (currentCardType == AidlConstants.CardType.NFC.getValue()) {
                bundle.putInt("flowType", AidlConstants.EMV.FlowType.TYPE_NFC_SPEEDUP);
                Log.e(TAG, "Using NFC Speedup flow type for contactless card");
                Log.e(TAG, "  Note: PIN decision made by kernel based on terminal CVM limit and card's CVM List");
            } else {
                bundle.putInt("flowType", AidlConstants.EMV.FlowType.TYPE_EMV_STANDARD);
                Log.e(TAG, "Using EMV Standard flow type for IC card");
            }
            bundle.putInt("cardType", currentCardType);

            // Start EMV transaction
            Log.e(TAG, "Calling transactProcessEx...");
            emvOptV2.transactProcessEx(bundle, emvListener);

        } catch (Exception e) {
            Log.e(TAG, "EMV process failed: " + e.getMessage());
            e.printStackTrace();
            onResultCallBack(EMVSteps.EMV_TRANS_FAIL, e.getMessage(), -1);
        }
    }

    // Flag to track if manual PIN is needed for high amounts
    private volatile boolean needManualPin = false;

    // Our own CVM limit threshold (600 EGP = 60000 minor units)
    private static final long MANUAL_CVM_THRESHOLD = 60000;

    // Extracted components (Single Responsibility Principle)
    private EmvConfigurationApplier configurationApplier;
    private CvmRequirementLogger cvmLogger;
    private PanExtractor panExtractor;

    /**
     * Apply EMV configuration (TLVs for all schemes)
     * 
     * Refactored to use EmvConfigurationApplier (Single Responsibility Principle)
     * Uses Strategy pattern for extensibility (Open/Closed Principle)
     */
    private void applyEmvConfiguration() {
        try {
            // Initialize components if not already done
            if (configurationApplier == null) {
                configurationApplier = new EmvConfigurationApplier(emvOptV2);
            }

            // Check if we need manual PIN for this transaction (our own logic)
            long amountValue = 0;
            try {
                amountValue = Long.parseLong(currentAmount);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing amount: " + e.getMessage());
            }

            // Our manual PIN threshold: 600 EGP (60000 minor units)
            needManualPin = (amountValue >= MANUAL_CVM_THRESHOLD);

            Log.e(TAG, "=== EMV Configuration ===");
            Log.e(TAG, "Amount: " + amountValue + " minor units (" + (amountValue / 100.0) + " EGP)");
            Log.e(TAG,
                    "Manual PIN threshold: " + MANUAL_CVM_THRESHOLD + " (" + (MANUAL_CVM_THRESHOLD / 100.0) + " EGP)");
            Log.e(TAG, "Manual PIN required: " + needManualPin);

            // Apply configuration using strategy pattern (extensible for new schemes)
            boolean success = configurationApplier.applyConfiguration(
                    amountValue,
                    configurationApplier.getDefaultStrategies());

            if (!success) {
                Log.e(TAG, "⚠️ EMV configuration application failed");
            }

        } catch (Exception e) {
            Log.e(TAG, "Apply EMV configuration failed: " + e.getMessage());
        }
    }

    // ==================== Public Methods ====================

    /**
     * Check if manual PIN entry is required for this transaction
     * This is true when amount >= CVM limit and DF8119=02 is used
     * (kernel won't request PIN, so we must handle it manually)
     */
    public boolean isManualPinRequired() {
        return needManualPin;
    }

    /**
     * Clear the manual PIN flag after PIN has been collected
     */
    public void clearManualPinFlag() {
        needManualPin = false;
        Log.e(TAG, "Manual PIN flag cleared");
    }

    // ==================== Callback Handler ====================

    /**
     * Unified callback handler - forwards to EMVCallback interface
     */
    private void onResultCallBack(EMVSteps step, Object data) {
        Log.e(TAG, "onResultCallBack: " + step.name());
        if (callback != null) {
            callback.onResult(step, data);
        }
    }

    /**
     * Unified callback handler with code - for transaction results
     */
    private void onResultCallBack(EMVSteps step, String desc, int code) {
        Log.e(TAG, "onResultCallBack: " + step.name() + ", code: " + code);
        if (callback != null) {
            callback.onResult(step, desc, code);
        }
    }

    // ==================== Card Detection Callback ====================

    private final CheckCardCallbackV2.Stub checkCardCallback = new CheckCardCallbackV2.Stub() {
        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: findMagCard ===");
            Log.e(TAG, "Magnetic stripe not supported for EMV");
            onResultCallBack(EMVSteps.CARD_ERROR,
                    "Magnetic stripe cards not supported. Please use chip or contactless.");
        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: findICCard ===");
            Log.e(TAG, "ATR: " + atr);
            currentCardType = AidlConstants.CardType.IC.getValue();

            // Beep buzzer when IC card detected (per SDK demo)
            try {
                if (MyApplication.app.basicOptV2 != null) {
                    MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                }
            } catch (Exception e) {
                Log.e(TAG, "Buzzer failed: " + e.getMessage());
            }

            Bundle cardInfo = new Bundle();
            cardInfo.putString("atr", atr);
            cardInfo.putInt("type", currentCardType);
            onResultCallBack(EMVSteps.CARD_FOUND, cardInfo);
            startEmvProcess();
        }

        @Override
        public void findICCardEx(Bundle bundle) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: findICCardEx ===");
            String atr = bundle != null ? bundle.getString("atr") : null;
            Log.e(TAG, "Bundle: " + bundle);
            findICCard(atr);
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: findRFCard (NFC) ===");
            Log.e(TAG, "UUID: " + uuid);
            currentCardType = AidlConstants.CardType.NFC.getValue();
            Bundle cardInfo = new Bundle();
            cardInfo.putString("uuid", uuid);
            cardInfo.putInt("type", currentCardType);
            onResultCallBack(EMVSteps.CARD_FOUND, cardInfo);
            startEmvProcess();
        }

        @Override
        public void findRFCardEx(Bundle bundle) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: findRFCardEx (NFC) ===");
            Log.e(TAG, "Bundle: " + bundle);
            String uuid = bundle != null ? bundle.getString("uuid") : null;
            if (uuid == null && bundle != null) {
                uuid = bundle.getString("cardId");
            }
            findRFCard(uuid);
        }

        @Override
        public void onErrorEx(Bundle bundle) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: onErrorEx ===");
            Log.e(TAG, "Bundle: " + bundle);
            int code = bundle != null ? bundle.getInt("code", -1) : -1;
            String message = bundle != null ? bundle.getString("message") : "Unknown error";
            onError(code, message);
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            Log.e(TAG, "=== CALLBACK: onError ===");
            Log.e(TAG, "Code: " + code + ", Message: " + message);

            // Decode common error codes
            String errorDesc;
            switch (code) {
                case -1:
                    errorDesc = "Timeout - no card detected";
                    break;
                case -2:
                    errorDesc = "User cancelled";
                    break;
                case -3:
                    errorDesc = "Card removed too early";
                    break;
                default:
                    errorDesc = message != null ? message : "Unknown error";
            }

            onResultCallBack(EMVSteps.CARD_ERROR, errorDesc + " (Code: " + code + ")");
        }
    };

    // ==================== EMV Listener ====================

    private final EMVListenerV2 emvListener = new EMVListenerV2.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void onWaitAppSelect(List<EMVCandidateV2> list, boolean b) throws RemoteException {
            Log.e(TAG, "onWaitAppSelect: " + (list != null ? list.size() : 0) + " candidates");
            onResultCallBack(EMVSteps.EMV_APP_SELECT, list);
        }

        @Override
        public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
            Log.e(TAG, "onAppFinalSelect: AID=" + tag9F06Value);
            onResultCallBack(EMVSteps.EMV_FINAL_APP_SELECT, tag9F06Value);
        }

        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            Log.e(TAG, "onConfirmCardNo: " + maskCardNo(cardNo));
            currentPan = cardNo;
            onResultCallBack(EMVSteps.EMV_CONFIRM_CARD_NO, cardNo);
        }

        @Override
        public void onCardDataExchangeComplete() throws RemoteException {
            Log.e(TAG, "=== onCardDataExchangeComplete ===");
            Log.e(TAG, "EMV kernel and card data exchange finished - card can be removed");

            // For NFC Speedup mode, onConfirmCardNo() is NOT called
            // We need to extract PAN from TLV tags for PIN entry
            if (currentPan == null) {
                extractPanFromTlv();
            }

            // DEBUG: Read card's CVM requirements to understand why PIN might be forced
            logCardCvmRequirements();

            // For NFC cards, beep buzzer to notify user they can remove card
            if (currentCardType == AidlConstants.CardType.NFC.getValue()) {
                try {
                    if (MyApplication.app.basicOptV2 != null) {
                        // Beep: 1 time, 2750Hz, 200ms
                        MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                        Log.e(TAG, "Buzzer played for NFC card removal notification");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to play buzzer: " + e.getMessage());
                }
            }

            onResultCallBack(EMVSteps.CARD_DATA_EXCHANGE_COMPLETE, null);
        }

        /**
         * Log card's CVM requirements to debug why PIN is being requested
         * 
         * Refactored to use CvmRequirementLogger (Single Responsibility Principle)
         */
        private void logCardCvmRequirements() {
            try {
                if (cvmLogger == null) {
                    cvmLogger = new CvmRequirementLogger(emvOptV2);
                }
                cvmLogger.logCardCvmRequirements();
            } catch (Exception e) {
                Log.e(TAG, "Error logging card CVM requirements: " + e.getMessage());
            }
        }

        private String parseCvmCode(int code) {
            int method = code & 0x3F; // Lower 6 bits
            switch (method) {
                case 0x00:
                    return "Fail CVM processing";
                case 0x01:
                    return "Plaintext PIN by ICC";
                case 0x02:
                    return "Enciphered PIN online";
                case 0x03:
                    return "Plaintext PIN + Signature";
                case 0x04:
                    return "Enciphered PIN by ICC";
                case 0x05:
                    return "Enciphered PIN + Signature";
                case 0x1E:
                    return "Signature (paper)";
                case 0x1F:
                    return "No CVM required";
                case 0x20:
                    return "CDCVM (Consumer Device CVM)";
                default:
                    return "Unknown CVM (" + String.format("%02X", method) + ")";
            }
        }

        private String parseCvmCondition(int condition) {
            switch (condition) {
                case 0x00:
                    return "Always";
                case 0x01:
                    return "If unattended cash";
                case 0x02:
                    return "If not (unattended cash or manual cash or purchase with cashback)";
                case 0x03:
                    return "If terminal supports CVM";
                case 0x04:
                    return "If manual cash";
                case 0x05:
                    return "If purchase with cashback";
                case 0x06:
                    return "If transaction currency=app currency AND amount < X";
                case 0x07:
                    return "If transaction currency=app currency AND amount >= X";
                case 0x08:
                    return "If transaction currency=app currency AND amount < Y";
                case 0x09:
                    return "If transaction currency=app currency AND amount >= Y";
                default:
                    return "Condition " + String.format("%02X", condition);
            }
        }

        /**
         * Extract PAN from EMV TLV tags
         * Called in NFC Speedup mode where onConfirmCardNo() is not invoked
         */
        /**
         * Extract PAN from TLV tags (NFC Speedup mode)
         * 
         * Refactored to use PanExtractor (Single Responsibility Principle)
         */
        private void extractPanFromTlv() {
            try {
                Log.e(TAG, "Extracting PAN from TLV tags (NFC Speedup mode)...");

                if (panExtractor == null) {
                    panExtractor = new PanExtractor(emvOptV2);
                }

                String extractedPan = panExtractor.extractPan();
                if (extractedPan != null) {
                    currentPan = extractedPan;
                    Log.e(TAG, "✓ PAN extracted: " + maskCardNo(currentPan));
                } else {
                    Log.e(TAG, "⚠️ Could not extract PAN from TLV tags");
                }
            } catch (Exception e) {
                Log.e(TAG, "extractPanFromTlv failed: " + e.getMessage());
            }
        }

        @Override
        public void onRequestShowPinPad(int pinType, int remainTime) throws RemoteException {
            Log.e(TAG, "onRequestShowPinPad: pinType=" + pinType + ", remainTime=" + remainTime);
            currentPinType = pinType;

            // CRITICAL FIX: If kernel explicitly requests PIN (especially for Visa which
            // ignores DF8119="02"),
            // disable our manual PIN logic to prevent double PIN prompt.
            // This allows the kernel's PIN request to be the primary CVM for this
            // transaction.
            // pinType: 0 = Online PIN, 1 = Offline PIN
            if (pinType == 0 && needManualPin) {
                Log.w(TAG, "═══════════════════════════════════════════════════════");
                Log.w(TAG, "⚠️ KERNEL REQUESTING PIN DESPITE DF8119=02 AND HIGH CVM LIMITS");
                Log.w(TAG, "  This typically happens with Visa cards that override terminal settings");
                Log.w(TAG, "  Disabling manual PIN logic to avoid double PIN prompt");
                Log.w(TAG, "═══════════════════════════════════════════════════════");
                needManualPin = false;
            }

            // DEBUG: Log amount vs CVM limit to understand why PIN was requested
            Log.e(TAG, "=== CVM DECISION DEBUG ===");
            Log.e(TAG, "  Transaction amount: " + currentAmount);
            Log.e(TAG, "  Configured CVM limit (DF8124): " + PaymentConfig.PayPassConfig.DF8124);
            Log.e(TAG, "  Card type: " + (currentCardType == AidlConstants.CardType.NFC.getValue() ? "NFC (Contactless)"
                    : "IC (Contact)"));
            Log.e(TAG, "  Manual PIN flag (after check): " + needManualPin);

            // Parse amounts for comparison (format: 12-digit BCD, implied 2 decimal places)
            try {
                long amountValue = Long.parseLong(currentAmount);
                long cvmLimitValue = Long.parseLong(PaymentConfig.PayPassConfig.DF8124);
                Log.e(TAG, "  Amount numeric: " + amountValue + " (" + (amountValue / 100.0) + " EGP)");
                Log.e(TAG, "  CVM limit numeric: " + cvmLimitValue + " (" + (cvmLimitValue / 100.0) + " EGP)");
                Log.e(TAG, "  Amount < CVM limit? " + (amountValue < cvmLimitValue));
                if (amountValue < cvmLimitValue) {
                    Log.e(TAG, "  ⚠️ PIN SHOULD NOT BE REQUIRED (amount below CVM limit)");
                    Log.e(TAG, "  ⚠️ Possible causes: Card forces PIN, issuer override, or kernel bug");
                } else {
                    Log.e(TAG, "  ✓ PIN correctly required (amount >= CVM limit)");
                }
            } catch (Exception e) {
                Log.e(TAG, "  Failed to parse amounts: " + e.getMessage());
            }

            // Try to read PayPass CVM limit to verify it's set
            try {
                byte[] tlvData = new byte[256];
                int len = emvOptV2.getTlv(AidlConstants.EMV.TLVOpCode.OP_PAYPASS, "DF8124", tlvData);
                if (len > 0) {
                    byte[] actualData = new byte[len];
                    System.arraycopy(tlvData, 0, actualData, 0, len);
                    String actualCvmLimit = bytesToHex(actualData);
                    Log.e(TAG, "  Actual DF8124 in kernel: " + actualCvmLimit);
                } else {
                    Log.e(TAG, "  ⚠️ DF8124 NOT FOUND in PayPass kernel!");
                }
            } catch (Exception e) {
                Log.e(TAG, "  Failed to read DF8124: " + e.getMessage());
            }

            Bundle pinInfo = new Bundle();
            pinInfo.putInt("pinType", pinType);
            pinInfo.putInt("remainTime", remainTime);
            // Ensure manualPin flag is false - this is a kernel-requested PIN, not manual
            pinInfo.putBoolean("manualPin", false);
            onResultCallBack(EMVSteps.EMV_SHOW_PIN_PAD, pinInfo);
        }

        @Override
        public void onRequestSignature() throws RemoteException {
            Log.e(TAG, "onRequestSignature");
            onResultCallBack(EMVSteps.EMV_SIGNATURE, null);
        }

        @Override
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
            Log.e(TAG, "onCertVerify: certType=" + certType);
            Bundle certData = new Bundle();
            certData.putInt("certType", certType);
            certData.putString("certInfo", certInfo);
            onResultCallBack(EMVSteps.EMV_CERT_VERIFY, certData);
        }

        @Override
        public void onOnlineProc() throws RemoteException {
            Log.e(TAG, "onOnlineProc - Online authorization required");

            // Check if manual PIN is needed (amount >= CVM limit with DF8119=02)
            if (needManualPin) {
                // Check CVM Result (9F34) - if "No CVM Required" (3F), skip manual PIN
                // This handles Apple Pay/Google Pay which have their own device-based CVM
                boolean skipPinDueToMobileWallet = false;
                try {
                    byte[] cvmResultData = new byte[32];
                    int len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                            new String[] { "9F34" }, cvmResultData);
                    if (len > 0) {
                        java.util.Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(
                                java.util.Arrays.copyOf(cvmResultData, len));
                        TLV cvmResultTlv = tlvMap.get("9F34");
                        if (cvmResultTlv != null) {
                            String cvmResult = cvmResultTlv.getValue();
                            Log.e(TAG, "CVM Result (9F34): " + cvmResult);

                            // Check CVM Result to detect mobile wallets (Apple Pay, Google Pay)
                            // CVM Result format: Byte1 (CVM Code) + Byte2 (Condition) + Byte3 (Result)
                            // - Byte 3 = 01: Unknown (no CVM was actually performed)
                            // - Byte 3 = 02: Successful (CVM was performed, e.g., device biometrics)
                            //
                            // Mobile wallets return 3Fxx02 because device DID verify user
                            // Regular cards told "no CVM" return 3Fxx01 because nothing was performed
                            if (cvmResult != null && cvmResult.length() >= 6) {
                                String cvmCode = cvmResult.substring(0, 2).toUpperCase();
                                String cvmResultByte = cvmResult.substring(4, 6).toUpperCase();

                                // Only skip PIN if:
                                // 1. CVM code is 3F or 1F (No CVM Required)
                                // 2. AND Result byte is 02 (Successful - device actually verified user)
                                if (("3F".equals(cvmCode) || "1F".equals(cvmCode)) && "02".equals(cvmResultByte)) {
                                    Log.e(TAG, "═══════════════════════════════════════════════════════");
                                    Log.e(TAG, "✓ MOBILE WALLET DETECTED (CVM=" + cvmCode + ", Result=" + cvmResultByte
                                            + ")");
                                    Log.e(TAG, "  Device-based CVM (Face ID/Touch ID/passcode) successfully performed");
                                    Log.e(TAG, "  Skipping manual PIN - not required for mobile wallets");
                                    Log.e(TAG, "═══════════════════════════════════════════════════════");
                                    skipPinDueToMobileWallet = true;
                                    needManualPin = false;
                                } else if ("3F".equals(cvmCode) || "1F".equals(cvmCode)) {
                                    Log.e(TAG, "CVM Result indicates No CVM, but Result=" + cvmResultByte
                                            + " (not Successful)");
                                    Log.e(TAG, "This is a regular card, NOT a mobile wallet - PIN still required");
                                }
                            } else if (cvmResult != null && cvmResult.length() >= 2) {
                                // Short CVM result - can't determine if mobile wallet
                                Log.e(TAG, "CVM Result too short to determine mobile wallet: " + cvmResult);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error checking CVM result: " + e.getMessage());
                }

                if (!skipPinDueToMobileWallet && needManualPin) {
                    Log.e(TAG, "=== MANUAL PIN REQUIRED ===");
                    Log.e(TAG, "Amount >= CVM limit, but DF8119=02 was used for reliable card read");
                    Log.e(TAG, "Triggering manual PIN collection before online auth...");
                    // Send PIN request callback - Activity will handle PIN then continue to online
                    // auth
                    // pinType=-99 as special marker for manual PIN (not kernel-requested)
                    Bundle pinInfo = new Bundle();
                    pinInfo.putInt("pinType", -99); // -99 = manual PIN for high amount
                    pinInfo.putInt("remainTime", 60);
                    pinInfo.putBoolean("manualPin", true);
                    currentPinType = 0; // Treat as online PIN for import
                    onResultCallBack(EMVSteps.EMV_SHOW_PIN_PAD, pinInfo);
                    return;
                }
            }

            onResultCallBack(EMVSteps.EMV_ONLINE_PROCESS, null);
        }

        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            Log.e(TAG, "onTransResult: code=" + code + ", desc=" + desc);
            // Reset the guard for next transaction
            emvProcessStarted = false;

            // Per SDK AidlConstants.EMV.TransResult:
            // 0 = SUCCESS
            // 1 = OFFLINE_APPROVAL
            // 2 = OFFLINE_DECLINE
            // 3 = RESERVE
            // 4 = TRY_AGAIN
            // 5 = ONLINE_APPROVAL
            // 6 = ONLINE_DECLINE

            // Debug: Log CAPK info when transaction fails (helps diagnose -4002 errors)
            if (code != 0 && code != 1 && code != 5) {
                logCapkDebugInfo(code, desc);
            }

            switch (code) {
                case 0: // SUCCESS
                case 1: // OFFLINE_APPROVAL
                case 5: // ONLINE_APPROVAL - THIS IS SUCCESS!
                    Log.e(TAG, "✓ Transaction APPROVED (code=" + code + ")");
                    onResultCallBack(EMVSteps.EMV_TRANS_SUCCESS, desc, code);
                    break;

                case 4: // TRY_AGAIN - Present card again
                    Log.e(TAG, "⚠️ Try again requested (code=4)");
                    onResultCallBack(EMVSteps.TRANS_PRESENT_CARD, desc, code);
                    break;

                case 2: // OFFLINE_DECLINE
                case 6: // ONLINE_DECLINE
                    Log.e(TAG, "✗ Transaction DECLINED (code=" + code + ")");
                    onResultCallBack(EMVSteps.EMV_TRANS_FAIL, desc, code);
                    break;

                default:
                    // Other codes are errors (including -4002 = missing CAPK)
                    Log.e(TAG, "✗ Transaction ERROR (code=" + code + ")");
                    onResultCallBack(EMVSteps.EMV_TRANS_FAIL, desc, code);
                    break;
            }
        }

        /**
         * Log CAPK debug information when transaction fails
         * Helps diagnose -4002 (Missing CAPK) errors
         */
        private void logCapkDebugInfo(int code, String desc) {
            try {
                Log.e(TAG, "═══════════════════════════════════════════════════════");
                Log.e(TAG, "=== TRANSACTION FAILURE DEBUG INFO ===");
                Log.e(TAG, "═══════════════════════════════════════════════════════");
                Log.e(TAG, "Error Code: " + code);
                Log.e(TAG, "Description: " + desc);

                // Check for -4002 (Missing CAPK)
                if (code == -4002 || (desc != null && desc.toLowerCase().contains("capk"))) {
                    Log.e(TAG, "");
                    Log.e(TAG, "🔥 ERROR -4002: MISSING CAPK DETECTED!");
                    Log.e(TAG, "   This means the card requires a CAPK that is not installed.");
                }

                byte[] outData = new byte[256];

                // Read AID (Tag 4F or 9F06)
                int len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                        new String[] { "4F", "9F06" }, outData);
                if (len > 0) {
                    java.util.Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(
                            java.util.Arrays.copyOf(outData, len));
                    TLV aidTlv = tlvMap.get("4F");
                    if (aidTlv == null)
                        aidTlv = tlvMap.get("9F06");
                    if (aidTlv != null) {
                        String aid = aidTlv.getValue();
                        String rid = aid.length() >= 10 ? aid.substring(0, 10) : aid;
                        Log.e(TAG, "");
                        Log.e(TAG, "Card AID (4F/9F06): " + aid);
                        Log.e(TAG, "Card RID: " + rid);
                    }
                }

                // Read CAPK Index (Tag 8F) - This is the key index the card is using
                len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                        new String[] { "8F" }, outData);
                if (len > 0) {
                    java.util.Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(
                            java.util.Arrays.copyOf(outData, len));
                    TLV capkIndexTlv = tlvMap.get("8F");
                    if (capkIndexTlv != null) {
                        String capkIndex = capkIndexTlv.getValue();
                        Log.e(TAG, "");
                        Log.e(TAG, "🔑 CAPK INDEX (8F): " + capkIndex);
                        Log.e(TAG, "   ⚠️ This CAPK index may be MISSING from terminal!");
                        Log.e(TAG, "   → Add CAPK with this RID + Index to your backend /emv/bundle");
                    }
                } else {
                    Log.e(TAG, "CAPK Index (8F): Not available");
                }

                // Read TVR (Tag 95) - Terminal Verification Results
                len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                        new String[] { "95" }, outData);
                if (len > 0) {
                    java.util.Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(
                            java.util.Arrays.copyOf(outData, len));
                    TLV tvrTlv = tlvMap.get("95");
                    if (tvrTlv != null) {
                        String tvr = tvrTlv.getValue();
                        Log.e(TAG, "");
                        Log.e(TAG, "TVR (95): " + tvr);
                        // Parse TVR byte 1 for ODA failures
                        if (tvr != null && tvr.length() >= 2) {
                            int byte1 = Integer.parseInt(tvr.substring(0, 2), 16);
                            if ((byte1 & 0x80) != 0)
                                Log.e(TAG, "  → Offline data authentication was not performed");
                            if ((byte1 & 0x40) != 0)
                                Log.e(TAG, "  → SDA failed");
                            if ((byte1 & 0x20) != 0)
                                Log.e(TAG, "  → ICC data missing");
                            if ((byte1 & 0x10) != 0)
                                Log.e(TAG, "  → Card on terminal exception file");
                            if ((byte1 & 0x08) != 0)
                                Log.e(TAG, "  → DDA failed");
                            if ((byte1 & 0x04) != 0)
                                Log.e(TAG, "  → CDA failed");
                        }
                    }
                }

                Log.e(TAG, "═══════════════════════════════════════════════════════");

            } catch (Exception e) {
                Log.e(TAG, "Error logging CAPK debug info: " + e.getMessage());
            }
        }

        @Override
        public void onConfirmationCodeVerified() throws RemoteException {
            Log.e(TAG, "=== onConfirmationCodeVerified (See Phone Flow) ===");
            // This callback indicates "See Phone" flow - customer needs to authorize on
            // their phone
            // Per SDK demo:
            // 1. Turn off card
            // 2. Show dialog to user
            // 3. Restart transaction when user confirms

            try {
                // Read DF8129 (PayPass outcome) for debugging
                byte[] outData = new byte[512];
                int len = emvOptV2.getTlv(AidlConstants.EMV.TLVOpCode.OP_PAYPASS, "DF8129", outData);
                if (len > 0) {
                    byte[] data = new byte[len];
                    System.arraycopy(outData, 0, data, 0, len);
                    Log.e(TAG, "DF8129 (Outcome Parameter Set): " + bytesToHex(data));
                }

                // Turn off the card
                readCardOptV2.cardOff(currentCardType);
                Log.e(TAG, "Card turned off for See Phone flow");

            } catch (Exception e) {
                Log.e(TAG, "Error in onConfirmationCodeVerified: " + e.getMessage());
            }

            // Notify callback - Activity should show dialog and restart transaction
            onResultCallBack(EMVSteps.TRANS_PRESENT_CARD, "See Phone - Please authorize on your device", 4);
        }

        private String bytesToHex(byte[] bytes) {
            if (bytes == null)
                return "";
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        }

        @Override
        public void onRequestDataExchange(String cardNo) throws RemoteException {
            Log.e(TAG, "onRequestDataExchange: " + maskCardNo(cardNo));
            // MIR cards - auto respond
            try {
                emvOptV2.importDataExchangeStatus(0);
            } catch (Exception e) {
                Log.e(TAG, "importDataExchangeStatus failed: " + e.getMessage());
            }
        }

        @Override
        public void onTermRiskManagement() throws RemoteException {
            Log.e(TAG, "onTermRiskManagement");
            onResultCallBack(EMVSteps.EMV_TERM_RISK_MANAGEMENT, null);
        }

        @Override
        public void onPreFirstGenAC() throws RemoteException {
            Log.e(TAG, "onPreFirstGenAC");
            onResultCallBack(EMVSteps.EMV_PRE_FIRST_GEN_AC, null);
        }

        @Override
        public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
            Log.e(TAG, "onDataStorageProc - DPAS 2.0 callback");
            // This callback is used for DPAS 2.0 (Discover)
            // Configure tags and values according to requirements
            // For now, respond with empty arrays
            try {
                String[] tags = new String[0];
                String[] values = new String[0];
                emvOptV2.importDataStorage(tags, values);
                Log.e(TAG, "importDataStorage called successfully");
            } catch (Exception e) {
                Log.e(TAG, "importDataStorage failed: " + e.getMessage());
            }

            Bundle data = new Bundle();
            data.putStringArray("containerID", containerID);
            data.putStringArray("containerContent", containerContent);
            onResultCallBack(EMVSteps.EMV_DATA_STORAGE, data);
        }
    };

    // ==================== Import Methods (Call from Activity) ====================

    /**
     * Import application selection
     * Call after EMV_APP_SELECT with user-selected index
     */
    public void importAppSelect(int index) {
        try {
            emvOptV2.importAppSelect(index);
            Log.e(TAG, "importAppSelect: index=" + index);
        } catch (Exception e) {
            Log.e(TAG, "importAppSelect failed: " + e.getMessage());
        }
    }

    /**
     * Import final app selection status
     * Call after EMV_FINAL_APP_SELECT (0=accept, 1=reject)
     */
    public void importAppFinalSelectStatus(int status) {
        try {
            emvOptV2.importAppFinalSelectStatus(status);
            Log.e(TAG, "importAppFinalSelectStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importAppFinalSelectStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import card number confirmation status
     * Call after EMV_CONFIRM_CARD_NO (0=confirm, 1=reject)
     */
    public void importCardNoStatus(int status) {
        try {
            emvOptV2.importCardNoStatus(status);
            Log.e(TAG, "importCardNoStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importCardNoStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import PIN input status
     * Call after EMV_SHOW_PIN_PAD
     * 
     * @param pinType 0=Online, 1=Offline (from callback)
     * @param result  0=success, 1=cancelled, 2=bypassed, 3=failed
     */
    public void importPinInputStatus(int pinType, int result) {
        try {
            emvOptV2.importPinInputStatus(pinType, result);
            Log.e(TAG, "importPinInputStatus: pinType=" + pinType + ", result=" + result);
        } catch (Exception e) {
            Log.e(TAG, "importPinInputStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import certificate verification status
     * Call after EMV_CERT_VERIFY (0=accept, 1=reject)
     */
    public void importCertStatus(int status) {
        try {
            emvOptV2.importCertStatus(status);
            Log.e(TAG, "importCertStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importCertStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import signature status
     * Call after EMV_SIGNATURE (0=accept, 1=reject)
     */
    public void importSignatureStatus(int status) {
        try {
            emvOptV2.importSignatureStatus(status);
            Log.e(TAG, "importSignatureStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importSignatureStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import online processing result
     * Call after EMV_ONLINE_PROCESS
     * 
     * @param status 0=approved, 1=declined, 2=unable to go online
     * @param tags   Response TLV tags (91, 8A, 71, 72, etc.)
     * @param values Response TLV values
     */
    public void importOnlineProcStatus(int status, String[] tags, String[] values) {
        try {
            byte[] outData = new byte[1024];
            emvOptV2.importOnlineProcStatus(status, tags, values, outData);
            Log.e(TAG, "importOnlineProcStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importOnlineProcStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import terminal risk management status
     * Call after EMV_TERM_RISK_MANAGEMENT (0=continue, 1=stop)
     */
    public void importTermRiskManagementStatus(int status) {
        try {
            emvOptV2.importTermRiskManagementStatus(status);
            Log.e(TAG, "importTermRiskManagementStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importTermRiskManagementStatus failed: " + e.getMessage());
        }
    }

    /**
     * Import pre-first GenAC status
     * Call after EMV_PRE_FIRST_GEN_AC (0=continue, 1=stop)
     */
    public void importPreFirstGenACStatus(int status) {
        try {
            emvOptV2.importPreFirstGenACStatus(status);
            Log.e(TAG, "importPreFirstGenACStatus: status=" + status);
        } catch (Exception e) {
            Log.e(TAG, "importPreFirstGenACStatus failed: " + e.getMessage());
        }
    }

    // ==================== Card Control Methods ====================

    /**
     * Turn off card (power off)
     * Should be called when card is no longer needed
     */
    public void cardOff() {
        try {
            if (currentCardType != 0) {
                readCardOptV2.cardOff(currentCardType);
                Log.e(TAG, "Card turned off: type=" + currentCardType);
            }
        } catch (Exception e) {
            Log.e(TAG, "cardOff failed: " + e.getMessage());
        }
    }

    /**
     * Check if card is still present
     * 
     * @return AidlConstants.CardExistStatus value, or -1 on error
     */
    public int getCardExistStatus() {
        try {
            return readCardOptV2.getCardExistStatus(currentCardType);
        } catch (Exception e) {
            Log.e(TAG, "getCardExistStatus failed: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Restart transaction (for "try again" scenarios)
     * Should be called when onTransResult returns code=4
     */
    public void restartTransaction() {
        Log.e(TAG, "=== Restarting Transaction ===");
        try {
            // Re-initialize EMV process
            emvOptV2.initEmvProcess();
            // Start card detection again (preserve transaction type)
            startCardDetection(currentAmount, 60,
                    "20".equals(currentTransactionType) ? "refund" : "purchase");
        } catch (Exception e) {
            Log.e(TAG, "restartTransaction failed: " + e.getMessage());
            onResultCallBack(EMVSteps.CARD_ERROR, "Failed to restart transaction: " + e.getMessage());
        }
    }

    // ==================== TLV Helper Methods ====================

    /**
     * Read TLV value from EMV kernel
     * For contactless transactions, tries brand-specific TLVOpCode first, then
     * falls back to OP_NORMAL
     * TVR (95) and TSI (9B) are terminal-generated tags and should be available in
     * OP_NORMAL,
     * but for contactless they may also be in brand-specific space
     */
    public String readTlv(String tag) {
        try {
            // For contactless, try brand-specific space first, then OP_NORMAL
            if (isContactless()) {
                // Read AID from EMV kernel to determine brand
                String aid = null;
                try {
                    byte[] aidOut = new byte[256];
                    int aidLen = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL,
                            new String[] { "4F", "9F06" }, aidOut);
                    if (aidLen > 0) {
                        java.util.Map<String, TLV> aidTlvMap = TLVUtil
                                .buildTLVMap(java.util.Arrays.copyOf(aidOut, aidLen));
                        TLV aidTlv = aidTlvMap.get("4F");
                        if (aidTlv == null) {
                            aidTlv = aidTlvMap.get("9F06");
                        }
                        if (aidTlv != null) {
                            aid = aidTlv.getValue();
                        }
                    }
                } catch (Exception e) {
                    // Failed to read AID, continue with OP_NORMAL
                }

                // Determine brand from AID and try brand-specific TLVOpCode
                if (aid != null && !aid.isEmpty()) {
                    boolean isMastercard = aid.startsWith("A000000004") || aid.startsWith("A000000005");
                    boolean isVisa = aid.startsWith("A000000003");

                    // Try brand-specific TLVOpCode first for contactless
                    int tlvOpCode = AidlConstants.EMV.TLVOpCode.OP_NORMAL;
                    if (isMastercard) {
                        tlvOpCode = com.sunmi.payservice.AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS;
                    } else if (isVisa) {
                        tlvOpCode = com.sunmi.payservice.AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE;
                    }

                    // Try brand-specific space first
                    byte[] out = new byte[256];
                    int len = emvOptV2.getTlvList(tlvOpCode, new String[] { tag }, out);
                    if (len > 0) {
                        TLV tlv = TLVUtil.buildTLVMap(java.util.Arrays.copyOf(out, len)).get(tag);
                        if (tlv != null && tlv.getValue() != null && !tlv.getValue().isEmpty()) {
                            return tlv.getValue();
                        }
                    }
                }
            }

            // Fall back to OP_NORMAL (works for both contact and contactless)
            byte[] out = new byte[256];
            int len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL, new String[] { tag }, out);
            if (len > 0) {
                TLV tlv = TLVUtil.buildTLVMap(java.util.Arrays.copyOf(out, len)).get(tag);
                return tlv != null ? tlv.getValue() : null;
            }
        } catch (Exception e) {
            Log.e(TAG, "readTlv failed for tag " + tag + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Read multiple TLV values from EMV kernel
     */
    public java.util.Map<String, String> readTlvList(String[] tags) {
        java.util.Map<String, String> result = new java.util.HashMap<>();
        try {
            byte[] out = new byte[1024];
            int len = emvOptV2.getTlvList(AidlConstants.EMV.TLVOpCode.OP_NORMAL, tags, out);
            if (len > 0) {
                java.util.Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(java.util.Arrays.copyOf(out, len));
                for (String tag : tags) {
                    TLV tlv = tlvMap.get(tag);
                    if (tlv != null) {
                        result.put(tag, tlv.getValue());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "readTlvList failed: " + e.getMessage());
        }
        return result;
    }

    // ==================== Getters ====================

    public String getCurrentPan() {
        return currentPan;
    }

    public int getCurrentCardType() {
        return currentCardType;
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public int getCurrentPinType() {
        return currentPinType;
    }

    public boolean isContactless() {
        return currentCardType == AidlConstants.CardType.NFC.getValue();
    }

    // ==================== Utility ====================

    private String maskCardNo(String cardNo) {
        if (cardNo == null || cardNo.length() < 10)
            return "****";
        // Always show first 6 digits and last 4 digits
        return cardNo.substring(0, 6) + "****" + cardNo.substring(cardNo.length() - 4);
    }
}
