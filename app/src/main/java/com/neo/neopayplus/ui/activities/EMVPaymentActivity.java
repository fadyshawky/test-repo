package com.neo.neopayplus.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.R;
import com.neo.neopayplus.emv.EMVCallback;
import com.neo.neopayplus.emv.EMVHandler;
import com.neo.neopayplus.emv.EMVSteps;
import com.neo.neopayplus.emv.Emv55Builder;
import com.neo.neopayplus.emv.PinPadManager;
import com.neo.neopayplus.emv.TLV;
import com.neo.neopayplus.emv.TLVUtil;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.processing.di.TransactionDependencyProvider;
import com.neo.neopayplus.processing.usecase.ProcessEmvTransactionUseCase;
import com.neo.neopayplus.ui.viewmodel.EmvPaymentViewModel;
import com.neo.neopayplus.utils.ByteUtil;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.payservice.AidlConstantsV2;
import com.sunmi.pay.hardware.aidl.AidlConstants;

import dagger.hilt.android.AndroidEntryPoint;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * EMV Payment Activity - Complete payment flow using EMVHandler
 * Refactored to use MVVM pattern with EmvPaymentViewModel.
 * Business logic is handled by the ViewModel, activity only handles UI.
 * This activity handles the entire payment flow:
 * 1. Amount entry (entered here or passed via intent)
 * 2. Card detection (IC/NFC)
 * 3. EMV processing with all callbacks
 * 4. PIN entry when required
 * 5. Online authorization
 * 6. Transaction result
 */
@AndroidEntryPoint
public class EMVPaymentActivity extends AppCompatActivity implements EMVCallback {
    private static final String TAG = "EMVPaymentActivity";

    // ViewModel
    private EmvPaymentViewModel viewModel;

    // UI Components - Amount Entry
    private LinearLayout layoutAmountEntry;
    private TextView tvAmountTitle;
    private TextView tvAmountDisplay;
    private Button btnConfirmAmount;
    private Button btnCancelAmount;

    // UI Components - Card Processing
    private LinearLayout layoutCardProcessing;
    private TextView tvTitle;
    private TextView tvAmount;
    private TextView tvStatus;
    private ImageView ivCardIcon;
    private ProgressBar progressBar;
    private Button btnCancel;

    // EMV Handler (still needed for direct callbacks, but created via ViewModel)
    private EMVHandler emvHandler;

    // Guard to prevent multiple card detection starts
    private boolean cardDetectionStarted = false;

    // Store selected AID for brand detection
    private String selectedAid = null;

    // Transaction data for receipt
    private String currentPan = null;
    private double currentAmount = 0.0;
    private int currentCardType = 0; // 0=IC, 1=NFC
    private int currentStan = 0;
    private int currentPinType = -1; // -1=no pin, 0=online, 1=offline, -99=manual
    private boolean signatureRequired = false;

    // API response data (for receipt)
    private PaymentApiService.AuthorizationResponse apiResponse = null;

    // TVR and TSI extracted from Field 55 (EMV data sent to backend)
    private String tvrFromField55 = null;
    private String tsiFromField55 = null;
    // Field 55 hex string (for extracting PAN later)
    private String field55Hex = null;

    // Amount entry state
    private StringBuilder amountDigits = new StringBuilder();
    private static final int MAX_AMOUNT_DIGITS = 9;

    // State
    private Handler mainHandler;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emv_payment);

        mainHandler = new Handler(Looper.getMainLooper());
        executor = Executors.newSingleThreadExecutor();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(EmvPaymentViewModel.class);

        // Initialize UI
        initViews();

        // Observe ViewModel state
        observeViewModel();

        // Get intent extras
        String transactionType = getIntent().getStringExtra("type");
        if (transactionType == null) {
            transactionType = "purchase";
        }
        viewModel.setTransactionType(transactionType);

        String amountStr = getIntent().getStringExtra("amount");
        if (amountStr != null && !amountStr.isEmpty()) {
            // Amount passed via intent - skip amount entry
            selectedAid = null; // Reset AID for new transaction
            BigDecimal amount = new BigDecimal(amountStr);
            viewModel.setAmount(amount);
            currentAmount = amount.doubleValue(); // Store amount for receipt
            showCardProcessingScreen();
            viewModel.startPayment();
        } else {
            // Show amount entry screen
            showAmountEntryScreen();
        }
    }

    /**
     * Observe ViewModel state changes
     */
    private void observeViewModel() {
        // Observe UI state (using LiveData for Java compatibility)
        viewModel.getUiStateLiveData().observe(this, uiState -> {
            // Update UI based on state
            if (uiState != null) {
                updateUI(uiState);

                // Trigger card detection when ViewModel indicates it's ready
                // Check if processing started and status indicates card detection should start
                String statusMsg = uiState.getStatusMessage();
                if (uiState.isProcessing() &&
                        statusMsg != null && statusMsg.contains("Present Card") &&
                        emvHandler == null && !cardDetectionStarted) {
                    // ViewModel has set the state to start card detection
                    // Now Activity should actually start it
                    Log.e(TAG, "Observer: Starting card detection (status: " + statusMsg + ")");
                    cardDetectionStarted = true;
                    doStartCardDetection();
                }
            }
        });
    }

    /**
     * Update UI based on ViewModel state
     */
    private void updateUI(EmvPaymentViewModel.UiState state) {
        // Update status message
        if (tvStatus != null && state.getStatusMessage() != null) {
            tvStatus.setText(state.getStatusMessage());
        }

        // Update amount display
        if (tvAmount != null && state.getAmount() != null) {
            tvAmount.setText(String.format(Locale.getDefault(), "EGP %.2f", state.getAmount()));
        }

        // Update progress bar
        if (progressBar != null) {
            progressBar.setVisibility(state.isProcessing() ? View.VISIBLE : View.GONE);
        }

        // Update card icon based on card type
        if (ivCardIcon != null) {
            int cardType = state.getCardType();
            if (cardType == AidlConstants.CardType.NFC.getValue()) {
                ivCardIcon.setImageResource(R.drawable.ic_nfc);
            } else if (cardType != 0) {
                ivCardIcon.setImageResource(R.drawable.ic_credit_card);
            }
        }
    }

    private void initViews() {
        // Amount Entry Views
        layoutAmountEntry = findViewById(R.id.layoutAmountEntry);
        tvAmountTitle = findViewById(R.id.tvAmountTitle);
        tvAmountDisplay = findViewById(R.id.tvAmountDisplay);
        btnConfirmAmount = findViewById(R.id.btnConfirmAmount);
        btnCancelAmount = findViewById(R.id.btnCancelAmount);

        // Card Processing Views
        layoutCardProcessing = findViewById(R.id.layoutCardProcessing);
        tvTitle = findViewById(R.id.tvTitle);
        tvAmount = findViewById(R.id.tvAmount);
        tvStatus = findViewById(R.id.tvStatus);
        ivCardIcon = findViewById(R.id.ivCardIcon);
        progressBar = findViewById(R.id.progressBar);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup amount entry buttons
        setupNumPad();

        btnConfirmAmount.setOnClickListener(v -> onAmountConfirmed());
        btnCancelAmount.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> onCancelClicked());

        // Set title based on transaction type (from ViewModel)
        String transactionType = viewModel.getUiStateLiveData().getValue() != null
                ? viewModel.getUiStateLiveData().getValue().getTransactionType()
                : "purchase";
        String title = "purchase".equals(transactionType) ? "Enter Amount" : "Enter Refund Amount";
        tvAmountTitle.setText(title);
    }

    private void setupNumPad() {
        int[] numButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int i = 0; i < numButtonIds.length; i++) {
            final int digit = i;
            Button btn = findViewById(numButtonIds[i]);
            if (btn != null) {
                btn.setOnClickListener(v -> onDigitPressed(String.valueOf(digit)));
            }
        }

        Button btnClear = findViewById(R.id.btnClear);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> onClearPressed());
        }

        Button btnDelete = findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> onDeletePressed());
        }
    }

    private void onDigitPressed(String digit) {
        if (amountDigits.length() < MAX_AMOUNT_DIGITS) {
            amountDigits.append(digit);
            updateAmountDisplay();
        }
    }

    private void onDeletePressed() {
        if (amountDigits.length() > 0) {
            amountDigits.deleteCharAt(amountDigits.length() - 1);
            updateAmountDisplay();
        }
    }

    private void onClearPressed() {
        amountDigits = new StringBuilder();
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        if (amountDigits.length() == 0) {
            tvAmountDisplay.setText(getString(R.string.default_amount));
        } else {
            BigDecimal cents = new BigDecimal(amountDigits.toString());
            BigDecimal displayAmount = cents.movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
            tvAmountDisplay.setText(String.format(Locale.getDefault(), "%.2f", displayAmount));
        }
    }

    private void onAmountConfirmed() {
        if (amountDigits.length() == 0) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal cents = new BigDecimal(amountDigits.toString());
        BigDecimal amount = cents.movePointLeft(2).setScale(2, RoundingMode.HALF_UP);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(this, "Amount must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e(TAG, "Amount confirmed: " + amount);

        // Reset transaction data for new transaction
        selectedAid = null;
        currentPan = null;
        currentCardType = 0;
        currentStan = 0;
        currentPinType = -1;
        signatureRequired = false;

        // Set amount in ViewModel and store locally for receipt
        viewModel.setAmount(amount);
        currentAmount = amount.doubleValue(); // Store amount for receipt
        Log.e(TAG, "Stored currentAmount: " + currentAmount);

        // Switch to card processing screen
        showCardProcessingScreen();
        viewModel.startPayment();
    }

    private void showAmountEntryScreen() {
        layoutAmountEntry.setVisibility(View.VISIBLE);
        layoutCardProcessing.setVisibility(View.GONE);
    }

    private void showCardProcessingScreen() {
        layoutAmountEntry.setVisibility(View.GONE);
        layoutCardProcessing.setVisibility(View.VISIBLE);
    }

    // Payment is now handled by ViewModel - this method is kept for backward
    // compatibility
    // but delegates to ViewModel
    private void startPayment() {
        // Payment flow is now handled by ViewModel
        // UI updates happen via observeViewModel()
        viewModel.startPayment();
    }

    // Card detection is now handled by ViewModel
    // This method is kept for backward compatibility but delegates to ViewModel
    private void startCardDetection() {
        // Card detection is handled by ViewModel.startPayment()
        // This method is no longer needed
    }

    private void doStartCardDetection() {
        // Create EMV handler using factory from ViewModel
        // Note: Factory is injected into ViewModel via Hilt
        com.neo.neopayplus.emv.EmvHandlerFactory factory = viewModel.getEmvHandlerFactory();
        emvHandler = factory.create(this, this);

        // Get amount from ViewModel state
        EmvPaymentViewModel.UiState state = viewModel.getUiStateLiveData().getValue();
        String amountMinor12 = state != null ? state.getAmountMinor12() : "000000000000";

        // Update UI
        tvStatus.setText("Present Card\nTap, insert, or swipe");
        ivCardIcon.setImageResource(R.drawable.ic_nfc);

        // Start card detection
        emvHandler.startCardDetection(amountMinor12, 60);
    }

    private void onCancelClicked() {
        // Cancel via ViewModel
        cardDetectionStarted = false;
        if (emvHandler != null) {
            emvHandler.cancelCardDetection();
            emvHandler = null;
        }
        viewModel.cancelCardDetection();
        finish();
    }

    // ==================== EMVCallback Implementation ====================

    @Override
    public void onResult(EMVSteps step, Object data) {
        Log.e(TAG, "onResult: " + step.name());

        mainHandler.post(() -> {
            switch (step) {
                case CARD_DETECT:
                    tvStatus.setText(getString(R.string.waiting_for_card));
                    break;

                case CARD_FOUND:
                    Bundle cardInfo = (Bundle) data;
                    int cardType = cardInfo.getInt("type");
                    // Store card type for receipt
                    currentCardType = cardType;
                    Log.e(TAG, "Card found - type: " + cardType + " (NFC=" + AidlConstants.CardType.NFC.getValue()
                            + ", AidlConstantsV2.NFC=" + AidlConstantsV2.CardType.NFC.getValue() + ")");
                    if (cardType == AidlConstants.CardType.NFC.getValue()) {
                        tvStatus.setText(getString(R.string.contactless_card_detected));
                        ivCardIcon.setImageResource(R.drawable.ic_nfc);
                    } else {
                        tvStatus.setText(getString(R.string.chip_card_detected));
                        ivCardIcon.setImageResource(R.drawable.ic_credit_card);
                    }
                    break;

                case EMV_APP_SELECT:
                    // Multiple apps on card - show selection dialog
                    @SuppressWarnings("unchecked")
                    List<EMVCandidateV2> candidates = (List<EMVCandidateV2>) data;
                    showAppSelectDialog(candidates);
                    break;

                case EMV_FINAL_APP_SELECT:
                    tvStatus.setText("Application selected");
                    // Auto-confirm
                    emvHandler.importAppFinalSelectStatus(0);
                    break;

                case EMV_CONFIRM_CARD_NO:
                    String cardNo = (String) data;
                    String maskedPan = maskPan(cardNo);
                    tvStatus.setText("Card: " + maskedPan);
                    // Auto-confirm
                    emvHandler.importCardNoStatus(0);
                    break;

                case CARD_DATA_EXCHANGE_COMPLETE:
                    tvStatus.setText("Reading card data...");
                    break;

                case EMV_SHOW_PIN_PAD:
                    Bundle pinInfo = (Bundle) data;
                    int pinType = pinInfo.getInt("pinType");
                    int remainTime = pinInfo.getInt("remainTime");
                    handlePinEntry(pinType, remainTime);
                    break;

                case EMV_SIGNATURE:
                    tvStatus.setText("Signature required");
                    signatureRequired = true;
                    // Auto-approve signature
                    emvHandler.importSignatureStatus(0);
                    break;

                case EMV_CERT_VERIFY:
                    tvStatus.setText("Verifying certificate...");
                    // Auto-approve
                    emvHandler.importCertStatus(0);
                    break;

                case EMV_ONLINE_PROCESS:
                    tvStatus.setText("Connecting to bank...");
                    handleOnlineProcess();
                    break;

                case EMV_TERM_RISK_MANAGEMENT:
                    // Auto-approve
                    emvHandler.importTermRiskManagementStatus(0);
                    break;

                case EMV_PRE_FIRST_GEN_AC:
                    // Auto-approve
                    emvHandler.importPreFirstGenACStatus(0);
                    break;

                case CARD_ERROR:
                    String errorMsg = (String) data;
                    showError(errorMsg);
                    break;

                case USER_CANCELLED:
                    finish();
                    break;
            }
        });
    }

    @Override
    public void onResult(EMVSteps step, String desc, int code) {
        Log.e(TAG, "onResult: " + step.name() + ", code: " + code + ", desc: " + desc);

        mainHandler.post(() -> {
            switch (step) {
                case EMV_TRANS_SUCCESS:
                    handleTransactionSuccess(desc, code);
                    break;

                case EMV_TRANS_FAIL:
                    handleTransactionFailed(desc, code);
                    break;

                case TRANS_PRESENT_CARD:
                    tvStatus.setText(getString(R.string.please_present_card_again));
                    // Restart card detection
                    if (emvHandler != null) {
                        EmvPaymentViewModel.UiState state = viewModel.getUiStateLiveData().getValue();
                        String amountMinor12 = state != null ? state.getAmountMinor12() : "000000000000";
                        emvHandler.startCardDetection(amountMinor12, 60);
                    } else {
                        Log.e(TAG, "TRANS_PRESENT_CARD: emvHandler is null, cannot restart detection");
                        showError("EMV handler not available");
                    }
                    break;
            }
        });
    }

    // ==================== Helper Methods ====================

    private void showAppSelectDialog(List<EMVCandidateV2> candidates) {
        String[] appNames = new String[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            EMVCandidateV2 cand = candidates.get(i);
            String name = cand.appName;
            if (name == null || name.isEmpty()) {
                name = cand.appLabel;
            }
            if (name == null || name.isEmpty()) {
                name = "Application " + (i + 1);
            }
            appNames[i] = name;
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Application")
                .setItems(appNames, (dialog, which) -> {
                    // Store selected AID before confirming
                    EMVCandidateV2 selectedCandidate = candidates.get(which);
                    if (selectedCandidate != null) {
                        // Try to get AID from candidate (tag 9F06 or 4F)
                        String aid = selectedCandidate.aid;
                        if (aid == null || aid.isEmpty()) {
                            // Fallback: AID will be captured in EMV_FINAL_APP_SELECT callback
                        } else {
                            selectedAid = aid;
                            Log.e(TAG, "Stored AID from candidate: " + selectedAid);
                        }
                    }
                    emvHandler.importAppSelect(which);
                })
                .setCancelable(false)
                .show();
    }

    // Store manual PIN block for use in online auth
    private byte[] manualPinBlock = null;
    private boolean isManualPinEntry = false;

    private void handlePinEntry(int pinType, int remainTime) {
        // Check if this is manual PIN (pinType=-99 means amount >= CVM limit)
        isManualPinEntry = (pinType == -99);
        int actualPinType = isManualPinEntry ? 0 : pinType; // Treat manual PIN as online PIN

        if (isManualPinEntry) {
            Log.e(TAG, "=== MANUAL PIN ENTRY ===");
            Log.e(TAG, "Amount >= CVM limit, collecting PIN manually");
            tvStatus.setText(getString(R.string.enter_pin_amount_verification));
        } else {
            tvStatus.setText(actualPinType == 0 ? "Enter Online PIN" : "Enter Offline PIN");
        }

        // Get PAN for PIN entry
        // In NFC Speedup mode, currentPan is extracted from TLV in
        // onCardDataExchangeComplete
        String pan = emvHandler.getCurrentPan();

        // Fallback: Try to read directly from EMV kernel TLV tags
        if (pan == null || pan.isEmpty()) {
            Log.e(TAG, "PAN not in currentPan, trying TLV tag 5A...");
            pan = emvHandler.readTlv("5A");
            if (pan != null && !pan.isEmpty()) {
                // Clean up PAN - remove 'F' padding
                pan = pan.toUpperCase().replace("F", "");
                Log.e(TAG, "PAN extracted from TLV 5A: " + maskPan(pan));
            }
        }

        // Fallback: Try Track 2 Equivalent Data (tag 57)
        if (pan == null || pan.isEmpty()) {
            Log.e(TAG, "Trying TLV tag 57 (Track 2)...");
            String track2 = emvHandler.readTlv("57");
            if (track2 != null && !track2.isEmpty()) {
                track2 = track2.toUpperCase();
                int separatorIdx = track2.indexOf('D');
                if (separatorIdx > 0) {
                    pan = track2.substring(0, separatorIdx).replace("F", "");
                    Log.e(TAG, "PAN extracted from TLV 57: " + maskPan(pan));
                }
            }
        }

        // Fallback: Try contactless Track 2 (tag 9F6B)
        if (pan == null || pan.isEmpty()) {
            Log.e(TAG, "Trying TLV tag 9F6B (Contactless Track 2)...");
            String track2 = emvHandler.readTlv("9F6B");
            if (track2 != null && !track2.isEmpty()) {
                track2 = track2.toUpperCase();
                int separatorIdx = track2.indexOf('D');
                if (separatorIdx > 0) {
                    pan = track2.substring(0, separatorIdx).replace("F", "");
                    Log.e(TAG, "PAN extracted from TLV 9F6B: " + maskPan(pan));
                }
            }
        }

        if (pan == null || pan.isEmpty()) {
            Log.e(TAG, "❌ PAN not available for PIN entry - bypassing PIN");
            if (isManualPinEntry) {
                // For manual PIN, skip PIN and proceed with online auth
                Log.e(TAG, "Manual PIN: No PAN, proceeding without PIN");
                emvHandler.clearManualPinFlag();
                handleOnlineProcess();
            } else {
                emvHandler.importPinInputStatus(pinType, 2); // Bypass
            }
            return;
        }

        Log.e(TAG, "✓ PAN available for PIN entry: " + maskPan(pan));

        // Use PinPadManager to request PIN
        // For manual PIN (pinType=-99), use 0 (online PIN)
        PinPadManager pinPadManager = new PinPadManager(MyApplication.app.pinPadOptV2);
        pinPadManager.requestPin(pan, actualPinType, 60000, result -> {
            mainHandler.post(() -> {
                if (result instanceof PinPadManager.PinResult.OnlineSuccess) {
                    PinPadManager.PinResult.OnlineSuccess success = (PinPadManager.PinResult.OnlineSuccess) result;

                    // Update currentPinType to reflect that PIN was entered
                    currentPinType = 0; // Online PIN
                    Log.e(TAG, "✓ PIN entered successfully - updated currentPinType to ONLINE_PIN (0)");

                    // Always store PIN block when available (for both manual and kernel-requested
                    // PIN)
                    // This ensures the PIN block is included in the ISO 8583 message
                    byte[] pinBlock = success.getPinBlock();
                    if (pinBlock != null && pinBlock.length > 0) {
                        manualPinBlock = pinBlock;
                        Log.e(TAG, "✓ PIN block stored for ISO 8583 message (length: " + pinBlock.length + " bytes)");
                    } else {
                        Log.e(TAG, "⚠️ PIN block is null or empty - will not be included in ISO 8583 message");
                    }

                    if (isManualPinEntry) {
                        // Manual PIN: Proceed directly to online auth
                        Log.e(TAG, "Manual PIN collected successfully");
                        emvHandler.clearManualPinFlag();
                        handleOnlineProcess();
                    } else {
                        // Kernel-requested PIN: Tell kernel PIN was successful
                        emvHandler.importPinInputStatus(actualPinType, 0); // Success
                    }
                } else if (result instanceof PinPadManager.PinResult.OfflineSuccess) {
                    // Update currentPinType to reflect that offline PIN was entered
                    currentPinType = 1; // Offline PIN
                    Log.e(TAG, "✓ Offline PIN entered successfully - updated currentPinType to OFFLINE_PIN (1)");

                    if (isManualPinEntry) {
                        emvHandler.clearManualPinFlag();
                        handleOnlineProcess();
                    } else {
                        emvHandler.importPinInputStatus(actualPinType, 0); // Success
                    }
                } else if (result instanceof PinPadManager.PinResult.Bypassed) {
                    // PIN was bypassed - keep currentPinType as -1 (no PIN)
                    Log.e(TAG, "PIN bypassed - currentPinType remains NO_PIN (-1)");

                    if (isManualPinEntry) {
                        Log.e(TAG, "Manual PIN bypassed - proceeding without PIN");
                        manualPinBlock = null;
                        emvHandler.clearManualPinFlag();
                        handleOnlineProcess();
                    } else {
                        emvHandler.importPinInputStatus(actualPinType, 2); // Bypassed
                    }
                } else if (result instanceof PinPadManager.PinResult.Cancelled) {
                    // PIN was cancelled - keep currentPinType as -1 (no PIN)
                    Log.e(TAG, "PIN cancelled - currentPinType remains NO_PIN (-1)");

                    if (isManualPinEntry) {
                        // User cancelled - abort transaction
                        Log.e(TAG, "Manual PIN cancelled by user");
                        showError("Transaction cancelled");
                    } else {
                        emvHandler.importPinInputStatus(actualPinType, 1); // Cancelled
                    }
                } else {
                    // PIN entry failed - keep currentPinType as -1 (no PIN)
                    Log.e(TAG, "PIN entry failed - currentPinType remains NO_PIN (-1)");

                    if (isManualPinEntry) {
                        Log.e(TAG, "Manual PIN failed");
                        showError("PIN entry failed");
                    } else {
                        emvHandler.importPinInputStatus(actualPinType, 3); // Failed
                    }
                }
            });
            return kotlin.Unit.INSTANCE;
        });
    }

    private void handleOnlineProcess() {
        tvStatus.setText("Processing online authorization...");

        executor.execute(() -> {
            try {
                // Build Field 55 (EMV data)
                boolean isContactless = emvHandler.isContactless();

                // Detect brand from AID
                // First try: Use stored AID from callback
                String aidToUse = selectedAid;

                // Fallback: Read AID from TLV if not stored
                if (aidToUse == null || aidToUse.isEmpty()) {
                    try {
                        byte[] outData = new byte[256];
                        int len = MyApplication.app.emvOptV2.getTlvList(
                                com.sunmi.payservice.AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL,
                                new String[] { "4F", "9F06" }, outData);
                        if (len > 0) {
                            java.util.Map<String, TLV> tlvMap = TLVUtil
                                    .buildTLVMap(java.util.Arrays.copyOf(outData, len));
                            TLV aidTlv = tlvMap.get("4F");
                            if (aidTlv == null) {
                                aidTlv = tlvMap.get("9F06");
                            }
                            if (aidTlv != null) {
                                aidToUse = aidTlv.getValue();
                                Log.e(TAG, "Read AID from TLV: " + aidToUse);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read AID from TLV: " + e.getMessage());
                    }
                }

                // Detect brand from AID
                com.neo.neopayplus.emv.config.EmvBrandConfig.BrandProfile brand = null;
                if (aidToUse != null && !aidToUse.isEmpty()) {
                    brand = com.neo.neopayplus.emv.config.EmvBrandConfig.INSTANCE.getBrandForAid(aidToUse);
                }

                // Fallback to VISA if AID not available or brand not detected
                if (brand == null) {
                    Log.e(TAG, "Brand detection failed for AID: " + aidToUse + ", using VISA as fallback");
                    brand = com.neo.neopayplus.emv.config.EmvBrandConfig.VISA;
                } else {
                    Log.e(TAG, "Detected brand from AID: " + aidToUse);
                }

                byte[] field55 = Emv55Builder.buildForBrand(
                        MyApplication.app.emvOptV2,
                        brand,
                        isContactless,
                        isManualPinEntry // Check if PIN was entered
                );

                Log.e(TAG, "Field 55 built: " + field55.length + " bytes");
                field55Hex = ByteUtil.bytes2HexStr(field55, 0, field55.length);

                // Extract TVR (tag 95), TSI (tag 9B), and Expiry Date (tag 59) from Field 55
                // (EMV data sent to
                // backend)
                try {
                    java.util.Map<String, com.neo.neopayplus.emv.TLV> tlvMap = com.neo.neopayplus.emv.TLVUtil
                            .buildTLVMap(field55);
                    com.neo.neopayplus.emv.TLV tvrTlv = tlvMap.get("95");
                    com.neo.neopayplus.emv.TLV tsiTlv = tlvMap.get("9B");
                    com.neo.neopayplus.emv.TLV expiry59Tlv = tlvMap.get("59");

                    if (tvrTlv != null) {
                        tvrFromField55 = tvrTlv.getValue();
                        Log.e(TAG, "✓ TVR (95) extracted from Field 55: " + tvrFromField55);
                    } else {
                        Log.e(TAG, "⚠️ TVR (95) not found in Field 55");
                    }
                    if (tsiTlv != null) {
                        tsiFromField55 = tsiTlv.getValue();
                        Log.e(TAG, "✓ TSI (9B) extracted from Field 55: " + tsiFromField55);
                    } else {
                        Log.e(TAG, "⚠️ TSI (9B) not found in Field 55");
                    }
                    if (expiry59Tlv != null && expiry59Tlv.getValue() != null && !expiry59Tlv.getValue().isEmpty()) {
                        String expiry59Hex = expiry59Tlv.getValue();
                        String expiry59Display = expiry59Hex.toUpperCase().replaceAll("F+$", "");
                        if (expiry59Display.length() >= 4) {
                            String yy = expiry59Display.substring(0, 2);
                            String mm = expiry59Display.substring(2, 4);
                            Log.e(TAG, "✓ Expiry date (59) present in Field 55 (unmasked for backend): " + mm + "/" + yy
                                    + " (hex: " + expiry59Hex + ")");
                        }
                    } else {
                        Log.e(TAG, "⚠️ Expiry date (59) not found in Field 55");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "❌ Failed to extract TVR/TSI/Expiry from Field 55: " + e.getMessage());
                }

                // Read transaction data
                String pan = emvHandler.getCurrentPan();
                int cardType = emvHandler.getCurrentCardType();

                // Store for receipt
                currentPan = pan;
                currentCardType = cardType;

                // Update ViewModel state
                if (pan != null) {
                    viewModel.updatePan(pan);
                }
                viewModel.updateCardType(cardType);

                // Create transaction use case
                TransactionDependencyProvider provider = new TransactionDependencyProvider(
                        MyApplication.app.emvOptV2,
                        MyApplication.app.readCardOptV2,
                        MyApplication.app.pinPadOptV2,
                        MyApplication.app.securityOptV2);
                ProcessEmvTransactionUseCase useCase = provider.createProcessEmvTransactionUseCase();

                // Check if we have a manual PIN block from high-amount transaction
                boolean hasPinBlock = (manualPinBlock != null && manualPinBlock.length > 0);
                byte[] pinBlockToUse = manualPinBlock;

                if (hasPinBlock) {
                    Log.e(TAG, "Using manual PIN block for high-amount transaction");
                }

                // Get amount and STAN from ViewModel state
                EmvPaymentViewModel.UiState currentState = viewModel.getUiStateLiveData().getValue();
                String amountMinor12 = currentState != null ? currentState.getAmountMinor12() : "000000000000";
                int stan = currentState != null ? currentState.getStan() : 0;

                // Build transaction data
                ProcessEmvTransactionUseCase.TransactionData txnData = new ProcessEmvTransactionUseCase.TransactionData(
                        pan,
                        amountMinor12,
                        "818", // EGP currency code
                        cardType,
                        hasPinBlock, // pinEntered - true if manual PIN was collected
                        false, // fallbackUsed
                        pinBlockToUse, // onlinePinBlock - from manual PIN if collected
                        null, // ksn
                        0, // pinType (online)
                        stan,
                        field55Hex);

                // Clear the manual PIN block after use
                if (hasPinBlock) {
                    manualPinBlock = null;
                }

                // Send to backend using callback
                useCase.processOnlineAuthorization(txnData, new ProcessEmvTransactionUseCase.TransactionCallback() {
                    @Override
                    public void onSuccess(PaymentApiService.AuthorizationResponse response) {
                        Log.e(TAG, "Online response: approved=" + response.approved + ", rc=" + response.responseCode);

                        // Store API response for receipt (contains authCode, rrn from API)
                        apiResponse = response;
                        Log.e(TAG, "Stored API response: rrn=" + response.rrn + ", authCode=" + response.authCode);

                        // Import result to EMV kernel
                        String[] tags = response.responseTags != null ? response.responseTags : new String[] {};
                        String[] values = response.responseValues != null ? response.responseValues : new String[] {};

                        mainHandler.post(() -> {
                            if (response.approved) {
                                emvHandler.importOnlineProcStatus(0, tags, values);
                            } else {
                                emvHandler.importOnlineProcStatus(1, tags, values);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Online processing failed: " + error.getMessage());
                        mainHandler.post(() -> {
                            // Unable to go online
                            emvHandler.importOnlineProcStatus(2, new String[] {}, new String[] {});
                        });
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Online processing failed: " + e.getMessage());
                mainHandler.post(() -> {
                    // Unable to go online
                    emvHandler.importOnlineProcStatus(2, new String[] {}, new String[] {});
                });
            }
        });
    }

    private void handleTransactionSuccess(String desc, int code) {
        // Update ViewModel state
        viewModel.setProcessing(false);
        cardDetectionStarted = false;
        progressBar.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);

        // Read RRN and AUTH code - prefer API response, fallback to EMV tags
        String rrn = null;
        String authCode = null;

        // Get from API response if available (more reliable)
        if (apiResponse != null) {
            rrn = apiResponse.rrn;
            authCode = apiResponse.authCode;
            Log.e(TAG, "Using RRN and AUTH from API response: rrn=" + rrn + ", authCode=" + authCode);
        }

        // Fallback to EMV tags if API response not available
        if (rrn == null || rrn.isEmpty()) {
            rrn = emvHandler.readTlv("9F26"); // Application Cryptogram as RRN fallback
        }
        if (authCode == null || authCode.isEmpty()) {
            authCode = emvHandler.readTlv("8A"); // Authorization Response Code
        }

        // Read Application Preferred Name (tag 50) for chip transactions
        String applicationPreferredName = null;
        if (currentCardType != AidlConstants.CardType.NFC.getValue()) {
            // For chip transactions, read tag 50
            try {
                String tag50Hex = emvHandler.readTlv("50");
                if (tag50Hex != null && !tag50Hex.isEmpty()) {
                    // Tag 50 is ASCII text, convert from hex
                    byte[] tag50Bytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(tag50Hex);
                    applicationPreferredName = new String(tag50Bytes, "UTF-8").trim();
                    Log.e(TAG, "Application Preferred Name (50): " + applicationPreferredName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to read Application Preferred Name (50): " + e.getMessage());
            }
        }

        // Determine CVM method
        String cvmMethod = "NO_PIN";
        if (signatureRequired) {
            cvmMethod = "SIGNATURE";
        } else if (currentPinType == 0) {
            cvmMethod = "ONLINE_PIN";
        } else if (currentPinType == 1) {
            cvmMethod = "OFFLINE_PIN";
        }

        // Determine entry mode - use AidlConstants (same as EMVHandler) not
        // AidlConstantsV2
        String entryMode = (currentCardType == AidlConstants.CardType.NFC.getValue()) ? "CONTACTLESS" : "IC";
        Log.e(TAG, "Entry mode determined - currentCardType: " + currentCardType + ", NFC value: "
                + AidlConstants.CardType.NFC.getValue() + ", entryMode: " + entryMode);

        // Read AID from TLV if not already captured
        if (selectedAid == null || selectedAid.isEmpty()) {
            try {
                // Try to read AID from TLV (tag 4F or 9F06)
                String aidFromTlv = emvHandler.readTlv("4F");
                if (aidFromTlv == null || aidFromTlv.isEmpty()) {
                    aidFromTlv = emvHandler.readTlv("9F06");
                }
                if (aidFromTlv != null && !aidFromTlv.isEmpty()) {
                    selectedAid = aidFromTlv;
                    Log.e(TAG, "Read AID from TLV: " + selectedAid);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to read AID from TLV: " + e.getMessage());
            }
        }

        // Extract TVR, TSI, and PAN directly from EMV kernel (not from Field 55 or
        // response)
        String tvr = null;
        String tsi = null;
        String receiptPan = null;

        // Read TVR (Terminal Verification Results, tag 95) from EMV kernel
        try {
            tvr = emvHandler.readTlv("95");
            if (tvr != null && !tvr.isEmpty()) {
                Log.e(TAG, "✓ TVR (95) extracted from EMV kernel: " + tvr);
            } else {
                Log.e(TAG, "⚠️ TVR (95) not found in EMV kernel");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to read TVR (95) from EMV kernel: " + e.getMessage());
        }

        // Read TSI (Transaction Status Information, tag 9B) from EMV kernel
        try {
            tsi = emvHandler.readTlv("9B");
            if (tsi != null && !tsi.isEmpty()) {
                Log.e(TAG, "✓ TSI (9B) extracted from EMV kernel: " + tsi);
            } else {
                Log.e(TAG, "⚠️ TSI (9B) not found in EMV kernel");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to read TSI (9B) from EMV kernel: " + e.getMessage());
        }

        // Extract PAN from EMV kernel (tag 5A, 57, or 9F6B)
        try {
            // Try tag 5A (PAN) first
            String pan5A = emvHandler.readTlv("5A");
            if (pan5A != null && !pan5A.isEmpty()) {
                // PAN is BCD encoded, remove trailing 'F' padding
                receiptPan = pan5A.toUpperCase().replaceAll("F+$", "");
                Log.e(TAG, "✓ PAN extracted from EMV kernel tag 5A: " + maskPan(receiptPan));
            } else {
                // Try tag 57 (Track 2 Equivalent Data)
                String pan57 = emvHandler.readTlv("57");
                if (pan57 != null && !pan57.isEmpty()) {
                    String track2Hex = pan57.toUpperCase();
                    // Track 2 format: PAN + 'D' + ExpiryDate + ServiceCode + ...
                    int delimiterIndex = track2Hex.indexOf('D');
                    if (delimiterIndex > 0) {
                        String panHex = track2Hex.substring(0, delimiterIndex);
                        receiptPan = panHex.replaceAll("F+$", ""); // Remove trailing 'F' padding
                        Log.e(TAG, "✓ PAN extracted from EMV kernel tag 57: " + maskPan(receiptPan));
                    }
                } else {
                    // Try tag 9F6B (Contactless Track 2 Data)
                    String pan9F6B = emvHandler.readTlv("9F6B");
                    if (pan9F6B != null && !pan9F6B.isEmpty()) {
                        String track2Hex = pan9F6B.toUpperCase();
                        int delimiterIndex = track2Hex.indexOf('D');
                        if (delimiterIndex > 0) {
                            String panHex = track2Hex.substring(0, delimiterIndex);
                            receiptPan = panHex.replaceAll("F+$", ""); // Remove trailing 'F' padding
                            Log.e(TAG, "✓ PAN extracted from EMV kernel tag 9F6B: " + maskPan(receiptPan));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to extract PAN from EMV kernel: " + e.getMessage());
        }

        // Fallback to currentPan if extraction failed
        if (receiptPan == null || receiptPan.isEmpty()) {
            receiptPan = currentPan;
            Log.e(TAG, "⚠️ Using currentPan as fallback: " + (receiptPan != null ? maskPan(receiptPan) : "null"));
        }

        // Extract expiry date from EMV kernel
        // Priority: tag 59 (Card expiration date) -> tag 5F24 (Application Expiration
        // Date) -> Track 2 (57/9F6B)
        String expiryDate = null;
        String maskedExpiryDate = null;
        try {
            // Try tag 59 first (Card expiration date - YYMM format)
            String expiry59 = emvHandler.readTlv("59");
            if (expiry59 != null && !expiry59.isEmpty()) {
                // Tag 59 is BCD encoded: YYMM (4 hex chars = 2 bytes)
                expiryDate = expiry59.toUpperCase().replaceAll("F+$", "");
                if (expiryDate.length() >= 4) {
                    String yy = expiryDate.substring(0, 2);
                    String mm = expiryDate.substring(2, 4);
                    maskedExpiryDate = "**/**";
                    Log.e(TAG, "✓ Expiry date (59) extracted from EMV kernel: " + maskedExpiryDate + " (unmasked: "
                            + expiryDate + ")");
                }
            }

            // Fallback to tag 5F24 (Application Expiration Date - YYMMDD format)
            if (expiryDate == null || expiryDate.length() < 4) {
                String expiry5F24 = emvHandler.readTlv("5F24");
                if (expiry5F24 != null && !expiry5F24.isEmpty()) {
                    // Tag 5F24 is BCD encoded: YYMMDD (6 hex chars = 3 bytes)
                    String expiry5F24Clean = expiry5F24.toUpperCase().replaceAll("F+$", "");
                    if (expiry5F24Clean.length() >= 4) {
                        // Extract YYMM from YYMMDD
                        expiryDate = expiry5F24Clean.substring(0, 4);
                        String yy = expiryDate.substring(0, 2);
                        String mm = expiryDate.substring(2, 4);
                        maskedExpiryDate = "**/**";
                        Log.e(TAG,
                                "✓ Expiry date (5F24) extracted from EMV kernel: " + maskedExpiryDate + " (unmasked: "
                                        + expiryDate + ")");
                    }
                }
            }

            // Fallback to Track 2 data (tag 57 or 9F6B) - format: PAN + 'D' +
            // ExpiryDate(YYMM) + ServiceCode
            if (expiryDate == null || expiryDate.length() < 4) {
                // Try tag 57 (Track 2 Equivalent Data)
                String track257 = emvHandler.readTlv("57");
                if (track257 != null && !track257.isEmpty()) {
                    String track2Hex = track257.toUpperCase();
                    int delimiterIndex = track2Hex.indexOf('D');
                    if (delimiterIndex > 0 && track2Hex.length() > delimiterIndex + 4) {
                        // Extract 4 hex chars after 'D' separator (YYMM)
                        String expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5);
                        expiryDate = expiryHex.replaceAll("F+$", "");
                        if (expiryDate.length() >= 4) {
                            String yy = expiryDate.substring(0, 2);
                            String mm = expiryDate.substring(2, 4);
                            maskedExpiryDate = "**/**";
                            Log.e(TAG, "✓ Expiry date extracted from Track 2 (57): " + maskedExpiryDate + " (unmasked: "
                                    + expiryDate + ")");
                        }
                    }
                }

                // Try tag 9F6B (Contactless Track 2 Data)
                if (expiryDate == null || expiryDate.length() < 4) {
                    String track29F6B = emvHandler.readTlv("9F6B");
                    if (track29F6B != null && !track29F6B.isEmpty()) {
                        String track2Hex = track29F6B.toUpperCase();
                        int delimiterIndex = track2Hex.indexOf('D');
                        if (delimiterIndex > 0 && track2Hex.length() > delimiterIndex + 4) {
                            String expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5);
                            expiryDate = expiryHex.replaceAll("F+$", "");
                            if (expiryDate.length() >= 4) {
                                String yy = expiryDate.substring(0, 2);
                                String mm = expiryDate.substring(2, 4);
                                maskedExpiryDate = "**/**";
                                Log.e(TAG,
                                        "✓ Expiry date extracted from Track 2 (9F6B): " + maskedExpiryDate
                                                + " (unmasked: "
                                                + expiryDate + ")");
                            }
                        }
                    }
                }
            }

            if (expiryDate == null || expiryDate.length() < 4) {
                Log.e(TAG, "⚠️ Expiry date not found in EMV kernel (tried tags 59, 5F24, 57, 9F6B)");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to extract expiry date from EMV kernel: " + e.getMessage());
        }

        // Get STAN from ViewModel
        EmvPaymentViewModel.UiState currentState = viewModel.getUiStateLiveData().getValue();
        int stan = currentState != null ? currentState.getStan() : currentStan;

        // Navigate to result with full transaction data
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("approved", true);
        intent.putExtra("rc", "00");
        intent.putExtra("msg", "APPROVED");
        intent.putExtra("rrn", rrn != null ? rrn : "");
        intent.putExtra("amount", currentAmount);
        intent.putExtra("currency", "EGP");
        intent.putExtra("cardPan", receiptPan != null ? receiptPan : "");
        intent.putExtra("aid", selectedAid != null ? selectedAid : "");
        intent.putExtra("applicationPreferredName", applicationPreferredName != null ? applicationPreferredName : "");
        intent.putExtra("entryMode", entryMode);
        intent.putExtra("transactionType", "SALE"); // TODO: Support REFUND/VOID
        intent.putExtra("authCode", authCode != null ? authCode : "");
        intent.putExtra("tvr", tvr != null ? tvr : "");
        intent.putExtra("tsi", tsi != null ? tsi : "");
        intent.putExtra("maskedExpiryDate", maskedExpiryDate != null ? maskedExpiryDate : "");

        // Log what we're passing
        Log.e(TAG, "Passing to ResultActivity - amount: " + currentAmount + ", rrn: " + rrn + ", authCode: " + authCode
                + ", aid: " + selectedAid + ", pan: " + (receiptPan != null ? maskPan(receiptPan) : "null") + ", tvr: "
                + tvr + ", tsi: " + tsi);
        intent.putExtra("responseCode", "00");
        intent.putExtra("responseMessage", "APPROVED");
        intent.putExtra("stan", stan);
        intent.putExtra("cvmMethod", cvmMethod);
        startActivity(intent);
        finish();
    }

    private void handleTransactionFailed(String desc, int code) {
        // Update ViewModel state
        viewModel.setProcessing(false);
        cardDetectionStarted = false;
        progressBar.setVisibility(View.GONE);
        btnCancel.setText(getString(R.string.back));
        btnCancel.setVisibility(View.VISIBLE);

        String errorMsg = desc != null ? desc : getString(R.string.transaction_failed);
        tvStatus.setText(getString(R.string.declined_format, errorMsg, code));
        ivCardIcon.setImageResource(R.drawable.ic_error);

        // Extract PAN if not already set (for declined transactions, PAN might not be
        // extracted yet)
        if (currentPan == null || currentPan.isEmpty()) {
            try {
                // Try to get PAN from EMV handler
                String pan = emvHandler.getCurrentPan();

                // Fallback: Try to read directly from EMV kernel TLV tags
                if (pan == null || pan.isEmpty()) {
                    Log.e(TAG, "PAN not in currentPan, trying TLV tag 5A...");
                    pan = emvHandler.readTlv("5A");
                    if (pan != null && !pan.isEmpty()) {
                        // Clean up PAN - remove 'F' padding
                        pan = pan.toUpperCase().replace("F", "");
                        Log.e(TAG, "PAN extracted from TLV 5A (declined): " + maskPan(pan));
                    }
                }

                // Fallback: Try Track 2 Equivalent Data (tag 57)
                if (pan == null || pan.isEmpty()) {
                    Log.e(TAG, "Trying TLV tag 57 (Track 2) for declined transaction...");
                    String track2 = emvHandler.readTlv("57");
                    if (track2 != null && !track2.isEmpty()) {
                        track2 = track2.toUpperCase();
                        int separatorIdx = track2.indexOf('D');
                        if (separatorIdx > 0) {
                            pan = track2.substring(0, separatorIdx).replace("F", "");
                            Log.e(TAG, "PAN extracted from TLV 57 (declined): " + maskPan(pan));
                        }
                    }
                }

                // Fallback: Try contactless Track 2 (tag 9F6B)
                if (pan == null || pan.isEmpty()) {
                    Log.e(TAG, "Trying TLV tag 9F6B (Contactless Track 2) for declined transaction...");
                    String track2 = emvHandler.readTlv("9F6B");
                    if (track2 != null && !track2.isEmpty()) {
                        track2 = track2.toUpperCase();
                        int separatorIdx = track2.indexOf('D');
                        if (separatorIdx > 0) {
                            pan = track2.substring(0, separatorIdx).replace("F", "");
                            Log.e(TAG, "PAN extracted from TLV 9F6B (declined): " + maskPan(pan));
                        }
                    }
                }

                if (pan != null && !pan.isEmpty()) {
                    currentPan = pan;
                    Log.e(TAG, "PAN set for declined receipt: " + maskPan(currentPan));
                } else {
                    Log.e(TAG, "WARNING: Could not extract PAN for declined receipt - all methods failed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to extract PAN for declined receipt: " + e.getMessage());
            }
        }

        // Read RRN if available (might be available even for declined transactions)
        String rrn = null;
        try {
            rrn = emvHandler.readTlv("9F26"); // Application Cryptogram as RRN fallback
            if (rrn == null || rrn.isEmpty()) {
                // Try to get from API response if available
                if (apiResponse != null) {
                    rrn = apiResponse.rrn;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read RRN: " + e.getMessage());
        }

        // Read Application Preferred Name (tag 50) for chip transactions
        String applicationPreferredName = null;
        if (currentCardType != AidlConstants.CardType.NFC.getValue()) {
            // For chip transactions, read tag 50
            try {
                String tag50Hex = emvHandler.readTlv("50");
                if (tag50Hex != null && !tag50Hex.isEmpty()) {
                    // Tag 50 is ASCII text, convert from hex
                    byte[] tag50Bytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(tag50Hex);
                    applicationPreferredName = new String(tag50Bytes, "UTF-8").trim();
                    Log.e(TAG, "Application Preferred Name (50): " + applicationPreferredName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to read Application Preferred Name (50): " + e.getMessage());
            }
        }

        // Determine entry mode - use AidlConstants (same as EMVHandler) not
        // AidlConstantsV2
        String entryMode = (currentCardType == AidlConstants.CardType.NFC.getValue()) ? "CONTACTLESS" : "IC";
        Log.e(TAG, "Entry mode determined (declined) - currentCardType: " + currentCardType + ", NFC value: "
                + AidlConstants.CardType.NFC.getValue() + ", entryMode: " + entryMode);

        // Read AID from TLV if not already captured
        if (selectedAid == null || selectedAid.isEmpty()) {
            try {
                // Try to read AID from TLV (tag 4F or 9F06)
                String aidFromTlv = emvHandler.readTlv("4F");
                if (aidFromTlv == null || aidFromTlv.isEmpty()) {
                    aidFromTlv = emvHandler.readTlv("9F06");
                }
                if (aidFromTlv != null && !aidFromTlv.isEmpty()) {
                    selectedAid = aidFromTlv;
                    Log.e(TAG, "Read AID from TLV (declined): " + selectedAid);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to read AID from TLV: " + e.getMessage());
            }
        }

        // Determine CVM method
        String cvmMethod = "NO_PIN";
        if (signatureRequired) {
            cvmMethod = "SIGNATURE";
        } else if (currentPinType == 0) {
            cvmMethod = "ONLINE_PIN";
        } else if (currentPinType == 1) {
            cvmMethod = "OFFLINE_PIN";
        }

        // Get STAN from ViewModel
        EmvPaymentViewModel.UiState currentState = viewModel.getUiStateLiveData().getValue();
        int stan = currentState != null ? currentState.getStan() : currentStan;

        // Navigate to result with full transaction data (same as approved)
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("approved", false);
        intent.putExtra("rc", String.valueOf(code));
        intent.putExtra("msg", errorMsg);
        intent.putExtra("rrn", rrn != null ? rrn : "");
        intent.putExtra("amount", currentAmount);
        intent.putExtra("currency", "EGP");
        intent.putExtra("cardPan", currentPan != null ? currentPan : "");
        intent.putExtra("aid", selectedAid != null ? selectedAid : "");
        intent.putExtra("applicationPreferredName", applicationPreferredName != null ? applicationPreferredName : "");
        intent.putExtra("entryMode", entryMode);
        intent.putExtra("transactionType", "SALE"); // TODO: Support REFUND/VOID
        intent.putExtra("responseCode", String.valueOf(code));
        intent.putExtra("responseMessage", errorMsg);
        intent.putExtra("stan", stan);
        intent.putExtra("cvmMethod", cvmMethod);
        intent.putExtra("orderId", (String) null); // TODO: Get from transaction
        intent.putExtra("transactionId", (String) null); // TODO: Get from transaction
        intent.putExtra("batchNumber", (String) null); // TODO: Get from batch
        intent.putExtra("receiptNumber", (String) null); // TODO: Get from receipt counter
        // Check if this is a bank decline (from production API)
        boolean isBankDecline = (apiResponse != null && apiResponse.isBankDecline);
        intent.putExtra("isBankDecline", isBankDecline);
        // For bank declines, use terminal config values for bank TID/MID
        if (isBankDecline) {
            intent.putExtra("bankTerminalId", com.neo.neopayplus.config.PaymentConfig.getTerminalId());
            intent.putExtra("bankMerchantId", com.neo.neopayplus.config.PaymentConfig.getMerchantId());
        } else {
            intent.putExtra("bankTerminalId", (String) null);
            intent.putExtra("bankMerchantId", (String) null);
        }

        // Extract TVR, TSI, and PAN directly from EMV kernel (not from Field 55 or
        // response)
        String tvr = null;
        String tsi = null;
        String receiptPan = null;

        // Read TVR (Terminal Verification Results, tag 95) from EMV kernel
        try {
            tvr = emvHandler.readTlv("95");
            if (tvr != null && !tvr.isEmpty()) {
                Log.e(TAG, "✓ TVR (95) extracted from EMV kernel: " + tvr);
            } else {
                Log.e(TAG, "⚠️ TVR (95) not found in EMV kernel");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to read TVR (95) from EMV kernel: " + e.getMessage());
        }

        // Read TSI (Transaction Status Information, tag 9B) from EMV kernel
        try {
            tsi = emvHandler.readTlv("9B");
            if (tsi != null && !tsi.isEmpty()) {
                Log.e(TAG, "✓ TSI (9B) extracted from EMV kernel: " + tsi);
            } else {
                Log.e(TAG, "⚠️ TSI (9B) not found in EMV kernel");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to read TSI (9B) from EMV kernel: " + e.getMessage());
        }

        // Extract PAN from EMV kernel (tag 5A, 57, or 9F6B)
        try {
            // Try tag 5A (PAN) first
            String pan5A = emvHandler.readTlv("5A");
            if (pan5A != null && !pan5A.isEmpty()) {
                receiptPan = pan5A.toUpperCase().replaceAll("F+$", "");
                Log.e(TAG, "✓ PAN extracted from EMV kernel tag 5A (declined): " + maskPan(receiptPan));
            } else {
                // Try tag 57 (Track 2 Equivalent Data)
                String pan57 = emvHandler.readTlv("57");
                if (pan57 != null && !pan57.isEmpty()) {
                    String track2Hex = pan57.toUpperCase();
                    int delimiterIndex = track2Hex.indexOf('D');
                    if (delimiterIndex > 0) {
                        String panHex = track2Hex.substring(0, delimiterIndex);
                        receiptPan = panHex.replaceAll("F+$", "");
                        Log.e(TAG, "✓ PAN extracted from EMV kernel tag 57 (declined): " + maskPan(receiptPan));
                    }
                } else {
                    // Try tag 9F6B (Contactless Track 2 Data)
                    String pan9F6B = emvHandler.readTlv("9F6B");
                    if (pan9F6B != null && !pan9F6B.isEmpty()) {
                        String track2Hex = pan9F6B.toUpperCase();
                        int delimiterIndex = track2Hex.indexOf('D');
                        if (delimiterIndex > 0) {
                            String panHex = track2Hex.substring(0, delimiterIndex);
                            receiptPan = panHex.replaceAll("F+$", "");
                            Log.e(TAG, "✓ PAN extracted from EMV kernel tag 9F6B (declined): " + maskPan(receiptPan));
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to extract PAN from EMV kernel (declined): " + e.getMessage());
        }

        // Fallback to currentPan if extraction failed
        if (receiptPan == null || receiptPan.isEmpty()) {
            receiptPan = currentPan;
        }

        // Extract expiry date from EMV kernel (declined transaction)
        // Priority: tag 59 (Card expiration date) -> tag 5F24 (Application Expiration
        // Date) -> Track 2 (57/9F6B)
        String expiryDate = null;
        String maskedExpiryDate = null;
        try {
            // Try tag 59 first (Card expiration date - YYMM format)
            String expiry59 = emvHandler.readTlv("59");
            if (expiry59 != null && !expiry59.isEmpty()) {
                expiryDate = expiry59.toUpperCase().replaceAll("F+$", "");
                if (expiryDate.length() >= 4) {
                    String yy = expiryDate.substring(0, 2);
                    String mm = expiryDate.substring(2, 4);
                    maskedExpiryDate = "**/**";
                    Log.e(TAG, "✓ Expiry date (59) extracted from EMV kernel (declined): " + maskedExpiryDate
                            + " (unmasked: " + expiryDate + ")");
                }
            }

            // Fallback to tag 5F24 (Application Expiration Date - YYMMDD format)
            if (expiryDate == null || expiryDate.length() < 4) {
                String expiry5F24 = emvHandler.readTlv("5F24");
                if (expiry5F24 != null && !expiry5F24.isEmpty()) {
                    String expiry5F24Clean = expiry5F24.toUpperCase().replaceAll("F+$", "");
                    if (expiry5F24Clean.length() >= 4) {
                        expiryDate = expiry5F24Clean.substring(0, 4);
                        String yy = expiryDate.substring(0, 2);
                        String mm = expiryDate.substring(2, 4);
                        maskedExpiryDate = "**/**";
                        Log.e(TAG, "✓ Expiry date (5F24) extracted from EMV kernel (declined): " + maskedExpiryDate
                                + " (unmasked: " + expiryDate + ")");
                    }
                }
            }

            // Fallback to Track 2 data (tag 57 or 9F6B)
            if (expiryDate == null || expiryDate.length() < 4) {
                String track257 = emvHandler.readTlv("57");
                if (track257 != null && !track257.isEmpty()) {
                    String track2Hex = track257.toUpperCase();
                    int delimiterIndex = track2Hex.indexOf('D');
                    if (delimiterIndex > 0 && track2Hex.length() > delimiterIndex + 4) {
                        String expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5);
                        expiryDate = expiryHex.replaceAll("F+$", "");
                        if (expiryDate.length() >= 4) {
                            String yy = expiryDate.substring(0, 2);
                            String mm = expiryDate.substring(2, 4);
                            maskedExpiryDate = "**/**";
                            Log.e(TAG, "✓ Expiry date extracted from Track 2 (57) (declined): " + maskedExpiryDate
                                    + " (unmasked: " + expiryDate + ")");
                        }
                    }
                }

                if (expiryDate == null || expiryDate.length() < 4) {
                    String track29F6B = emvHandler.readTlv("9F6B");
                    if (track29F6B != null && !track29F6B.isEmpty()) {
                        String track2Hex = track29F6B.toUpperCase();
                        int delimiterIndex = track2Hex.indexOf('D');
                        if (delimiterIndex > 0 && track2Hex.length() > delimiterIndex + 4) {
                            String expiryHex = track2Hex.substring(delimiterIndex + 1, delimiterIndex + 5);
                            expiryDate = expiryHex.replaceAll("F+$", "");
                            if (expiryDate.length() >= 4) {
                                String yy = expiryDate.substring(0, 2);
                                String mm = expiryDate.substring(2, 4);
                                maskedExpiryDate = "**/**";
                                Log.e(TAG, "✓ Expiry date extracted from Track 2 (9F6B) (declined): " + maskedExpiryDate
                                        + " (unmasked: " + expiryDate + ")");
                            }
                        }
                    }
                }
            }

            if (expiryDate == null || expiryDate.length() < 4) {
                Log.e(TAG, "⚠️ Expiry date not found in EMV kernel (declined, tried tags 59, 5F24, 57, 9F6B)");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Failed to extract expiry date from EMV kernel (declined): " + e.getMessage());
        }

        intent.putExtra("cardPan", receiptPan != null ? receiptPan : "");
        intent.putExtra("tvr", tvr != null ? tvr : "");
        intent.putExtra("tsi", tsi != null ? tsi : "");
        intent.putExtra("maskedExpiryDate", maskedExpiryDate != null ? maskedExpiryDate : "");

        // Log what we're passing
        Log.e(TAG, "Passing to ResultActivity (declined) - amount: " + currentAmount + ", rrn: " + rrn
                + ", entryMode: " + entryMode + ", aid: " + selectedAid + ", cardPan: "
                + (receiptPan != null ? maskPan(receiptPan) : "null") + ", isBankDecline: " + isBankDecline
                + ", tvr: " + tvr + ", tsi: " + tsi);

        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        viewModel.setProcessing(false);
        cardDetectionStarted = false;
        progressBar.setVisibility(View.GONE);
        btnCancel.setText(getString(R.string.back));
        btnCancel.setVisibility(View.VISIBLE);

        tvStatus.setText(getString(R.string.error_format, message));
        ivCardIcon.setImageResource(R.drawable.ic_error);
    }

    private String maskPan(String pan) {
        if (pan == null || pan.length() < 10) {
            return "****";
        }
        // Always show first 6 digits and last 4 digits
        return pan.substring(0, 6) + "****" + pan.substring(pan.length() - 4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Check processing state from ViewModel
        EmvPaymentViewModel.UiState state = viewModel.getUiStateLiveData().getValue();
        boolean isProcessing = state != null && state.isProcessing();
        if (emvHandler != null && isProcessing) {
            emvHandler.cancelCardDetection();
            emvHandler = null;
        }
        cardDetectionStarted = false;
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        // Check processing state from ViewModel
        EmvPaymentViewModel.UiState state = viewModel.getUiStateLiveData().getValue();
        boolean isProcessing = state != null && state.isProcessing();
        if (isProcessing) {
            onCancelClicked();
        } else if (layoutCardProcessing.getVisibility() == View.VISIBLE) {
            // Go back to amount entry
            if (emvHandler != null) {
                emvHandler.cancelCardDetection();
            }
            showAmountEntryScreen();
            amountDigits = new StringBuilder();
            updateAmountDisplay();
        } else {
            super.onBackPressed();
        }
    }
}
