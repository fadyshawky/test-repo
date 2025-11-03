package com.neo.neopayplus.processing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.R;
import com.neo.neopayplus.data.DataViewActivity;
import com.neo.neopayplus.pin.PinInputActivity;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.wrapper.CheckCardCallbackV2Wrapper;
import com.sunmi.payservice.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.pay.hardware.aidlv2.bean.AidV2;
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2;
import com.sunmi.pay.hardware.aidl.bean.CardInfo;
import com.sunmi.pay.hardware.aidl.AidlConstants.PinBlockFormat;
import com.neo.neopayplus.emv.TLV;
import com.neo.neopayplus.emv.TLVUtil;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.db.TxnDb;
import com.neo.neopayplus.utils.EntryModeUtil;
import com.neo.neopayplus.utils.TimeSync;
import com.neo.neopayplus.security.KeyManagerPOS;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextUtils;

/**
 * ProcessingActivity - EMV Transaction Processing
 * 
 * This activity handles the complete EMV transaction flow including:
 * - Card detection (ICC/NFC/contactless)
 * - EMV kernel processing
 * - CVM (Cardholder Verification Method) handling
 * - PIN entry (online/offline)
 * - Authorization request building
 * 
 * PREREQUISITES (must be initialized before transactions):
 * 1. Keys must be injected via SecurityOptV2:
 *    - For MKSK system: TMK, TPK (PIN key), TAK (MAC key), TDK (optional)
 *    - For DUKPT: IPEK and KSN (for online PIN in some regions)
 *    - See: SunmiPayLibKeyManager for key initialization utilities
 * 
 * 2. EMV configuration must be loaded:
 *    - Terminal parameters (setTermParamEx)
 *    - AIDs (Application Identifiers) via addAid()
 *    - CAPKs (Certificate Authority Public Keys) via addCapk()
 *    - See: EmvConfigurationManager.initialize() (called automatically at app startup)
 * 
 * Initialization Order (from reference guide):
 * 1. securityOptV2.saveKeyDukpt() or saveKeyEx() - inject keys
 * 2. emvOptV2.setTermParamEx() - set terminal parameters  
 * 3. emvOptV2.addAid() - load all scheme AIDs
 * 4. emvOptV2.addCapk() - load CAPKs
 * 5. Run transaction via emvOptV2.transactProcessEx()
 * 
 * Key Index Reference:
 * - MKSK Master Key (TMK): 0-9
 * - MKSK Working Keys (TPK/TAK/TDK): 0-199 (current PIN key index: 12)
 * - DUKPT Keys: 1100-1199
 * 
 * CVM Handling:
 * - Terminal is configured to prefer Online PIN over Offline PIN
 * - For cards requiring Online PIN only, ensure terminal capabilities are set correctly
 * - PIN block format: ISO-0 (standard for online PIN)
 * - See: onRequestShowPinPad() for PIN entry handling
 */
public class ProcessingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private TextView mTvAmountDisplay;
    private TextView mTvStatus;
    private ProgressBar mProgressBar;
    private Button mBtnCancel;
    
    private String mAmount; // Piasters for SDK
    private String mAmountDisplay; // Pounds for display
    private String mPin;
    private Handler mHandler;
    private int mProgress = 0;
    
    // EMV related
    private EMVOptV2 mEMVOptV2;
    private ReadCardOptV2 mReadCardOptV2;
    private PinPadOptV2 mPinPadOptV2;
    private com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2 mSecurityOptV2;
    private int mCardType;
    private String mCardNo;
    private String mEmvTlvData;
    private boolean mIsEmvProcessRunning = false;
    private long mLastEmvProcessTime = 0;
    private static final long EMV_COOLDOWN_MS = 2000; // 2 seconds cooldown
    private static final long EMV_TIMEOUT_MS = 10000; // 10 seconds timeout for faster ICC processing
    private Handler mTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimeoutRunnable;
    private static final int REQ_PIN_INPUT = 1001;
    
    // PIN handling state (per CVM & PIN pad handling guide)
    private static final int MAX_PIN_ATTEMPTS = 3;
    private static final int PIN_ENTRY_TIMEOUT_MS = 60_000; // 60 seconds
    private static final boolean ALLOW_PIN_FALLBACK_TO_SIGNATURE = true;
    private java.util.concurrent.atomic.AtomicInteger mPinAttemptsLeft = new java.util.concurrent.atomic.AtomicInteger(MAX_PIN_ATTEMPTS);
    private byte[] mOnlinePinBlock = null; // Store PIN block for online PIN
    private String mKsn = null; // Store KSN for online PIN
    private int mCurrentPinType = -1; // 0=offline, 1=online
    private String mBackendRequestJson = null; // Store exact JSON sent to backend
    private String mCvmResultCode = null; // Store CVM Result code (9F34) to determine if PIN should be sent to backend
    // We no longer use activity-for-result for PIN; PIN is entered before processing
    
    // STAN, DE22, KSN, Journal, Reversal fields
    private int currentStan = 0;
    private boolean pinEnteredThisTxn = false;
    private boolean fallbackUsed = false; // set to true if you trigger magstripe fallback path
    private String lastPanMasked = "";
    private String lastRrn = null;
    private String currentEntryMode = null;
    private String currentAid = null;
    private String currentTsi = null;
    private String currentTvr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        
        // Get data from previous screen
        mAmount = getIntent().getStringExtra("amount"); // Piasters for SDK
        mAmountDisplay = getIntent().getStringExtra("amountDisplay"); // Pounds for display
        mPin = getIntent().getStringExtra("pin");
        
        // Validate required data
        if (mAmount == null || mAmount.isEmpty()) {
            LogUtil.e(Constant.TAG, "Amount not provided, finishing activity");
            showToast("Amount not provided");
            finish();
            return;
        }
        if (mAmountDisplay == null || mAmountDisplay.isEmpty()) {
            LogUtil.e(Constant.TAG, "Amount display not provided, finishing activity");
            showToast("Amount display not provided");
            finish();
            return;
        }
        // PIN should be provided by user input, not hardcoded
        if (mPin == null) {
            LogUtil.e(Constant.TAG, "⚠️ SECURITY WARNING: PIN not set");
        }
        
        mHandler = new Handler(Looper.getMainLooper());
        
        // Initialize EMV services
        mEMVOptV2 = MyApplication.app.emvOptV2;
        mReadCardOptV2 = MyApplication.app.readCardOptV2;
        mPinPadOptV2 = MyApplication.app.pinPadOptV2;
        mSecurityOptV2 = MyApplication.app.securityOptV2;
        
        initView();
        startRealPaymentProcess();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Processing Payment");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvAmountDisplay = findViewById(R.id.tv_amount_display);
        mTvStatus = findViewById(R.id.tv_status);
        mProgressBar = findViewById(R.id.progress_bar);
        mBtnCancel = findViewById(R.id.btn_cancel);
        
        mBtnCancel.setOnClickListener(this);
        
        updateAmountDisplay();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel) {
            onCancelClick();
        }
    }

    private void onCancelClick() {
        // Clean up EMV process before finishing
        if (mIsEmvProcessRunning) {
            try {
                mEMVOptV2.importAppSelect(-1); // Cancel any pending operations
                mIsEmvProcessRunning = false;
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error cleaning up EMV process on cancel: " + e.getMessage());
            }
        }
        // Cancel timeout
        cancelEmvTimeout();
        // Navigate back to main screen
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up EMV process when activity is destroyed
        if (mIsEmvProcessRunning) {
            try {
                mEMVOptV2.importAppSelect(-1);
                mIsEmvProcessRunning = false;
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error cleaning up EMV process on destroy: " + e.getMessage());
            }
        }
        // Cancel timeout
        cancelEmvTimeout();
    }

    private void updateAmountDisplay() {
        long amountInPounds = Long.parseLong(mAmountDisplay);
        mTvAmountDisplay.setText(String.format("%d %s", amountInPounds, com.neo.neopayplus.config.PaymentConfig.CURRENCY_NAME));
    }

    private void startRealPaymentProcess() {
        try {
            // Check if EMV process is already running
            if (mIsEmvProcessRunning) {
                LogUtil.e(Constant.TAG, "EMV process already running, skipping...");
                updateStatus("EMV process already running...", 50);
                return;
            }
            
            // Generate STAN before starting EMV
            TxnDb db = new TxnDb(this);
            currentStan = db.nextStan();
            LogUtil.e(Constant.TAG, "✓ Generated STAN: " + currentStan);
            
            // Reset per-transaction flags
            pinEnteredThisTxn = false;
            fallbackUsed = false;
            lastPanMasked = "";
            lastRrn = null;
            currentEntryMode = null;
            currentAid = null;
            currentTsi = null;
            currentTvr = null;
            
            // Check cooldown period to prevent -50009 error
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastEmvProcessTime < EMV_COOLDOWN_MS) {
                long remainingTime = EMV_COOLDOWN_MS - (currentTime - mLastEmvProcessTime);
                LogUtil.e(Constant.TAG, "EMV cooldown active, waiting " + remainingTime + "ms");
                updateStatus("Please wait " + (remainingTime / 1000) + " seconds...", 10);
                
                // Wait for cooldown period
                mHandler.postDelayed(() -> {
                    if (!mIsEmvProcessRunning) {
                        startRealPaymentProcess();
                    }
                }, remainingTime);
                return;
            }
            
            updateStatus("Initializing EMV process...", 10);
            
            // Validate amount
            if (mAmount == null || mAmount.isEmpty()) {
                throw new IllegalArgumentException("Amount cannot be null or empty");
            }
            
            long amountValue = Long.parseLong(mAmount);
            if (amountValue <= 0) {
                throw new IllegalArgumentException("Amount must be greater than 0");
            }
            
            LogUtil.e(Constant.TAG, "Starting payment process - Amount: " + mAmount + " piasters");
            
                // Clean up any previous EMV process
                try {
                    mEMVOptV2.importAppSelect(-1); // Cancel any pending app selection
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "Error cleaning up previous EMV process: " + e.getMessage());
                }
            
                // Verify EMV configuration is initialized BEFORE starting transaction
                if (!com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().isInitialized()) {
                    throw new IllegalStateException("EMV configuration not initialized - cannot start transaction. Ensure EmvConfigurationManager.initialize() was called at app startup.");
                }
                
                // Initialize EMV process before checking card
                mEMVOptV2.initEmvProcess();
                initEmvTlvData();
                
                mIsEmvProcessRunning = true;
                mLastEmvProcessTime = currentTime;
                updateStatus("Please insert or tap your card...", 20);
                
                // Start timeout mechanism
                startEmvTimeout();
                
                checkCard();
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Error initializing payment process", 0);
            showToast("Error starting payment process: " + e.getMessage());
            mIsEmvProcessRunning = false;
        }
    }
    
    /**
     * Initialize EMV TLV data for transaction
     * 
     * NOTE: AIDs and CAPKs are loaded once at app startup via EmvConfigurationManager.
     * However, terminal parameters must be set per transaction as they may be overwritten
     * by the EMV kernel during transaction processing.
     */
    private void initEmvTlvData() {
        try {
            // Verify EMV configuration is initialized
            if (!com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().isInitialized()) {
                LogUtil.e(Constant.TAG, "⚠️ WARNING: EMV configuration not initialized - transaction may fail");
                LogUtil.e(Constant.TAG, "EMV configuration should be initialized at app startup via EmvConfigurationManager");
            } else {
                LogUtil.e(Constant.TAG, "✓ EMV configuration verified - ready for transaction");
            }
            
            // Log AID count for debugging (if available via SDK)
            try {
                // Note: PayLib 2.0.32 doesn't expose getAidList(), so we can't verify AID count
                // But we trust EmvConfigurationManager has loaded them correctly
                LogUtil.e(Constant.TAG, "ℹ️ AIDs loaded via EmvConfigurationManager at app startup");
            } catch (Exception e) {
                // Ignore - AID listing not available in this SDK version
            }
            
            // Set terminal parameters per transaction (required for contactless)
            // These parameters may be overwritten by the EMV kernel, so we set them each time
            try {
                // CRITICAL: For contactless, set terminal parameters for both OP_NORMAL and scheme-specific codes
                // This ensures terminal parameters are available during L2 candidate matching
                
                // Determine scheme-specific operation code for contactless
                int schemeOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    // For contactless, also set parameters for PayPass and PayWave
                    // This ensures correct terminal configuration during L2 phase
                    schemeOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS; // Default to PayPass for Mastercard
                }
                
                // Set terminal capabilities (9F33) - supports Online PIN, Offline PIN, CDCVM, contactless
            String[] termCapTags = {"9F33"};
                String[] termCapValues = {com.neo.neopayplus.config.PaymentConfig.TERMINAL_CAPABILITIES};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termCapTags, termCapValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    // Also set for PayPass and PayWave operation codes
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, termCapTags, termCapValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, termCapTags, termCapValues);
                }
            
                // Set additional terminal capabilities (9F40) - Online PIN supported
            String[] addCapTags = {"9F40"};
                String[] addCapValues = {com.neo.neopayplus.config.PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, addCapTags, addCapValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, addCapTags, addCapValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, addCapTags, addCapValues);
                }
                
                // Set terminal type (9F35)
            String[] termTypeTags = {"9F35"};
                String[] termTypeValues = {com.neo.neopayplus.config.PaymentConfig.TERMINAL_TYPE};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, termTypeTags, termTypeValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, termTypeTags, termTypeValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, termTypeTags, termTypeValues);
                }
            
                // Set country code (9F1A) - use API-provided value if available
            String[] countryTags = {"9F1A"};
                String countryCode = com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().getTerminalCountryCode();
                String[] countryValues = {countryCode};
                LogUtil.e(Constant.TAG, "Setting country code (9F1A): " + countryCode + " for contactless: " + (mCardType == AidlConstantsV2.CardType.NFC.getValue()));
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, countryTags, countryValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, countryTags, countryValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, countryTags, countryValues);
                    LogUtil.e(Constant.TAG, "✓ Country code set for PayPass/PayWave: " + countryCode);
                }
            
                // Set currency code (5F2A) - use API-provided value if available
            String[] currencyTags = {"5F2A"};
                String currencyCode = com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().getCurrencyCode();
                String[] currencyValues = {currencyCode};
                LogUtil.e(Constant.TAG, "Setting currency code (5F2A): " + currencyCode + " for contactless: " + (mCardType == AidlConstantsV2.CardType.NFC.getValue()));
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, currencyTags, currencyValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, currencyTags, currencyValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, currencyTags, currencyValues);
                    LogUtil.e(Constant.TAG, "✓ Currency code set for PayPass/PayWave: " + currencyCode);
                }
            
                // Set transaction category code (9F53) for contactless - CRITICAL for L2 candidate matching
            String[] tccTags = {"9F53"};
                String[] tccValues = {com.neo.neopayplus.config.PaymentConfig.TRANSACTION_CATEGORY_CODE};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tccTags, tccValues);
                if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                    // 9F53 is especially critical for contactless - set for both PayPass and PayWave
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tccTags, tccValues);
                    mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE, tccTags, tccValues);
                    LogUtil.e(Constant.TAG, "✓ Transaction category code (9F53) set for contactless PayPass/PayWave");
                }
            
                LogUtil.e(Constant.TAG, "✓ Terminal parameters set for transaction");
        } catch (Exception e) {
                LogUtil.e(Constant.TAG, "⚠️ Error setting terminal parameters: " + e.getMessage());
            e.printStackTrace();
            }
            
            } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error verifying EMV configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Debug method to print loaded AIDs and CAPKs for analysis
     */
    private void debugLoadedAidsAndCapks() {
        try {
            LogUtil.e(Constant.TAG, "=== DEBUG: AID/CAPK Provisioning ===");
            // Print current date/time (EMV relies on this)
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US);
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
            java.util.Date now = new java.util.Date();
            LogUtil.e(Constant.TAG, "Current Date: " + dateFormat.format(now));
            LogUtil.e(Constant.TAG, "Current Time: " + timeFormat.format(now));
            // PayLib 2.0.32 does not expose getAidList/getCapkList. Assuming provisioning successful.
            LogUtil.e(Constant.TAG, "AIDs/CAPKs loaded using deleteAid/deleteCapk + addAid/addCapk APIs");
            LogUtil.e(Constant.TAG, "=== END DEBUG: AID/CAPK Provisioning ===");
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error in debugLoadedAidsAndCapks: " + e.getMessage());
        }
    }
    
    /**
     * Debug method to extract and analyze EMV tags for PIN/CVM debugging
     * Based on PayLib v2.0.32 EMV tag analysis
     */
    private void extractPinAndCvmData() {
        try {
            LogUtil.e(Constant.TAG, "=== DEBUG: EMV PIN/CVM Analysis ===");
            
            // Extract key EMV tags for PIN/CVM debugging
            String[] cvmTags = {"8E", "9F34", "95", "9F10", "9F27"};
            String[] tagNames = {"CVM List", "CVM Results", "TVR", "Issuer App Data", "CID"};
            
            for (int i = 0; i < cvmTags.length; i++) {
                try {
                    byte[] outData = new byte[256];
                    int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, cvmTags[i], outData);
                    if (len > 0) {
                        byte[] tagData = new byte[len];
                        System.arraycopy(outData, 0, tagData, 0, len);
                        String hexValue = ByteUtil.bytes2HexStr(tagData);
                        LogUtil.e(Constant.TAG, tagNames[i] + " (" + cvmTags[i] + "): " + hexValue);
                        
                        // Analyze CVM List (8E) to check available CVM options
                        if ("8E".equals(cvmTags[i]) && hexValue.length() >= 2) {
                            // Extract actual CVM List value (skip tag and length if present)
                            String cvmListValue = hexValue;
                            if (hexValue.startsWith("8E")) {
                                if (hexValue.length() >= 6) {
                                    String lengthHex = hexValue.substring(4, 6);
                                    int length = Integer.parseInt(lengthHex, 16);
                                    if (hexValue.length() >= 6 + length * 2) {
                                        cvmListValue = hexValue.substring(6, 6 + length * 2);
                                    }
                                }
                            }
                            
                            // Parse CVM List to check if online PIN (02) is available
                            boolean hasOnlinePin = false;
                            boolean hasOfflinePin = false;
                            
                            // CVM List format: Each CVM rule is 3 bytes
                            // Bytes 1-2: CVM Code (e.g., 02 = Online PIN, 42 = Offline PIN, 1E/1F = Online PIN)
                            // Byte 3: CVM Condition
                            // Parse in groups of 6 hex characters (3 bytes)
                            for (int j = 0; j <= cvmListValue.length() - 6; j += 6) {
                                String cvmCode = cvmListValue.substring(j, j + 2);
                                String cvmCode2 = j + 2 < cvmListValue.length() ? cvmListValue.substring(j + 2, j + 4) : "";
                                String condition = j + 4 < cvmListValue.length() ? cvmListValue.substring(j + 4, j + 6) : "";
                                
                                // Check for Online PIN codes: 02, 1E, 1F
                                if ("02".equals(cvmCode) || "1E".equals(cvmCode) || "1F".equals(cvmCode) ||
                                    ("00".equals(cvmCode) && ("1E".equals(cvmCode2) || "1F".equals(cvmCode2)))) {
                                    hasOnlinePin = true;
                                    String fullCode = cvmCode + cvmCode2;
                                    LogUtil.e(Constant.TAG, "✓ CVM List contains Online PIN option: " + fullCode + " (condition: " + condition + ")");
                                } 
                                // Check for Offline PIN codes: 42, 01
                                else if ("42".equals(cvmCode) || "01".equals(cvmCode) ||
                                         ("00".equals(cvmCode) && ("42".equals(cvmCode2) || "01".equals(cvmCode2)))) {
                                    hasOfflinePin = true;
                                    String fullCode = cvmCode + cvmCode2;
                                    LogUtil.e(Constant.TAG, "✓ CVM List contains Offline PIN option: " + fullCode + " (condition: " + condition + ")");
                                }
                            }
                            
                            if (hasOnlinePin && hasOfflinePin) {
                                LogUtil.e(Constant.TAG, "✓ Card supports both Online and Offline PIN in CVM List");
                                LogUtil.e(Constant.TAG, "✓ Terminal will handle PIN according to CVM List order and conditions");
                                LogUtil.e(Constant.TAG, "✓ EMV kernel will select appropriate CVM based on transaction conditions");
                            } else if (hasOnlinePin && !hasOfflinePin) {
                                LogUtil.e(Constant.TAG, "✓ Card supports Online PIN only - correct CVM flow");
                            } else if (hasOfflinePin && !hasOnlinePin) {
                                LogUtil.e(Constant.TAG, "✓ Card supports Offline PIN only - correct CVM flow");
                            }
                        }
                        
                        // Analyze CVM Results (9F34) specifically
                        if ("9F34".equals(cvmTags[i]) && hexValue.length() >= 2) {
                            // The hexValue is the raw TLV data, need to extract the actual CVM value
                            // Format: 9F34 + length + value, so skip the tag and length bytes
                            String actualCvmValue = hexValue;
                            if (hexValue.startsWith("9F34")) {
                                // Extract length byte (2 chars after tag)
                                if (hexValue.length() >= 6) {
                                    String lengthHex = hexValue.substring(4, 6);
                                    int length = Integer.parseInt(lengthHex, 16);
                                    if (hexValue.length() >= 6 + length * 2) {
                                        actualCvmValue = hexValue.substring(6, 6 + length * 2);
                                    }
                                }
                            }
                            
                            LogUtil.e(Constant.TAG, "CVM Value analysis: " + actualCvmValue);
                            
                            if (actualCvmValue.length() >= 2) {
                                String cvmCode = actualCvmValue.substring(0, 2);
                                LogUtil.e(Constant.TAG, "CVM Code: " + cvmCode);
                                
                                switch (cvmCode) {
                                    case "00":
                                        LogUtil.e(Constant.TAG, "✓ No CVM required");
                                        break;
                                    case "01":
                                        LogUtil.e(Constant.TAG, "✓ Offline PIN verified");
                                        break;
                                    case "02":
                                        LogUtil.e(Constant.TAG, "✓ Online PIN verified (correct for cards requiring online PIN)");
                                        break;
                                    case "03":
                                        LogUtil.e(Constant.TAG, "✓ CDCVM performed (Apple Pay/Google Pay)");
                                        break;
                                    case "42":
                                        LogUtil.e(Constant.TAG, "⚠️ Offline PIN performed - Check if card requires Online PIN only");
                                        break;
                                    case "5E":
                                        LogUtil.e(Constant.TAG, "✓ CDCVM detected - Apple Pay/Google Pay device authentication successful");
                                        break;
                                    default:
                                        LogUtil.e(Constant.TAG, "? Unknown CVM code: " + cvmCode + " - may cause offline denial");
                                        break;
                                }
                            }
                        }
                    } else {
                        LogUtil.e(Constant.TAG, tagNames[i] + " (" + cvmTags[i] + "): No data");
                    }
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "Error extracting " + tagNames[i] + ": " + e.getMessage());
                }
            }
            
            LogUtil.e(Constant.TAG, "=== END DEBUG: EMV PIN/CVM Analysis ===");
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error in extractPinAndCvmData: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void checkCard() {
        try {
            updateStatus("Detecting card...", 30);
            int cardType = AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
            
            // PayLib v2.0.32: Prefer checkCardEnc for better security
            try {
                Bundle cardEncBundle = new Bundle();
                cardEncBundle.putInt("cardType", cardType);
                cardEncBundle.putInt("timeout", 60);
                mReadCardOptV2.checkCardEnc(cardEncBundle, mCheckCardCallback, 60);
                LogUtil.e(Constant.TAG, "Using checkCardEnc for secure card detection");
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "checkCardEnc not available, falling back to checkCard: " + e.getMessage());
                // Fallback to checkCard for backward compatibility
                mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mIsEmvProcessRunning = false;
            updateStatus("Error checking for card", 0);
            showToast("Error checking for card: " + e.getMessage());
        }
    }

    private void updateStatus(String status, int progress) {
        runOnUiThread(() -> {
            mTvStatus.setText(status);
            mProgressBar.setProgress(progress);
        });
    }

    private final CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {
        @Override
        public void findICCard(String atr) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            mCardType = AidlConstantsV2.CardType.IC.getValue();
            runOnUiThread(() -> updateStatus("Card detected! Processing...", 50));
            // IC card Beep buzzer when check card success
            MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            transactProcess();
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid + " (Contactless card detected)");
            mCardType = AidlConstantsV2.CardType.NFC.getValue();
            
            // Log the UUID to help identify the card type
            // Apple Pay typically has specific UUID patterns
            if (uuid != null && uuid.length() > 0) {
                LogUtil.e(Constant.TAG, "Contactless card UUID: " + uuid);
                LogUtil.e(Constant.TAG, "This could be Apple Pay, Google Pay, or physical contactless card");
            }
            
            // Verify EMV configuration before processing contactless transaction
            if (!com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().isInitialized()) {
                LogUtil.e(Constant.TAG, "❌ CRITICAL: EMV configuration not initialized - contactless transaction will fail");
                runOnUiThread(() -> {
                    updateStatus("EMV not configured - transaction failed", 0);
                    showToast("EMV configuration error - please restart app");
                });
                mIsEmvProcessRunning = false;
                return;
            }
            
            LogUtil.e(Constant.TAG, "✓ EMV configuration verified - proceeding with contactless transaction");
            runOnUiThread(() -> updateStatus("Contactless payment detected! Processing...", 50));
            transactProcess();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "Card detection error: " + message + " (Code: " + code + ")";
            LogUtil.e(Constant.TAG, error);
            mIsEmvProcessRunning = false;
            runOnUiThread(() -> {
                updateStatus("Card detection failed", 0);
                showToast(error);
            });
        }
    };
    
    private void transactProcess() {
        try {
            updateStatus("Starting EMV transaction...", 60);
            
            // Reset PIN attempts counter for new transaction
            mPinAttemptsLeft.set(MAX_PIN_ATTEMPTS);
            mOnlinePinBlock = null;
            mKsn = null;
            mCurrentPinType = -1;
            LogUtil.e(Constant.TAG, "🔄 Transaction started - PIN attempts counter reset to " + MAX_PIN_ATTEMPTS);
            
            // CRITICAL: Re-initialize EMV process and TLV data right before transaction
            // This ensures terminal parameters are set correctly for contactless
            // The EMV kernel may reset these during card detection phase
            try {
                mEMVOptV2.initEmvProcess();
                LogUtil.e(Constant.TAG, "✓ EMV process re-initialized for transaction");
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "⚠️ Error re-initializing EMV process: " + e.getMessage());
                // Continue anyway
            }
            
            // CRITICAL: Re-set terminal parameters right before transactProcessEx()
            // For contactless, terminal parameters MUST be set before transaction starts
            // They may have been overwritten during card detection phase
            initEmvTlvData();
            
            // Guide diagnostic: Log expected terminal parameters for contactless
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                LogUtil.e(Constant.TAG, "=== CONTACTLESS TRANSACTION PRE-FLIGHT CHECK ===");
                String expectedCountry = com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().getTerminalCountryCode();
                String expectedCurrency = com.neo.neopayplus.emv.EmvConfigurationManager.getInstance().getCurrencyCode();
                LogUtil.e(Constant.TAG, "Expected terminal parameters for contactless:");
                LogUtil.e(Constant.TAG, "  9F1A (Country): " + expectedCountry);
                LogUtil.e(Constant.TAG, "  5F2A (Currency): " + expectedCurrency);
                LogUtil.e(Constant.TAG, "  9F33 (Term Cap): " + com.neo.neopayplus.config.PaymentConfig.TERMINAL_CAPABILITIES);
                LogUtil.e(Constant.TAG, "  9F40 (Add Cap): " + com.neo.neopayplus.config.PaymentConfig.ADDITIONAL_TERMINAL_CAPABILITIES);
                LogUtil.e(Constant.TAG, "  9F35 (Term Type): " + com.neo.neopayplus.config.PaymentConfig.TERMINAL_TYPE);
                LogUtil.e(Constant.TAG, "  9F53 (TCC): " + com.neo.neopayplus.config.PaymentConfig.TRANSACTION_CATEGORY_CODE);
                LogUtil.e(Constant.TAG, "Note: Parameters set for OP_NORMAL, OP_PAYPASS, and OP_PAYWAVE");
            }
            
            // Diagnostic: Query and log all loaded AIDs before transaction
            try {
                java.util.List<String> aidList = new java.util.ArrayList<>();
                int queryResult = mEMVOptV2.queryAidCapkList(0, aidList); // 0 = query AIDs
                if (queryResult == 0) {
                    LogUtil.e(Constant.TAG, "=== DIAGNOSTIC: Loaded AIDs Count: " + aidList.size() + " ===");
                    LogUtil.e(Constant.TAG, "⚠️ NOTE: This query may only show explicitly added AIDs, not SDK built-in AIDs");
                    
                    boolean hasMastercardPayPass = false;
                    for (int i = 0; i < aidList.size(); i++) {
                        String aidData = aidList.get(i);
                        LogUtil.e(Constant.TAG, "  AID[" + i + "]: " + (aidData.length() > 100 ? aidData.substring(0, 100) + "..." : aidData));
                        
                        // Check for Mastercard PayPass AID (A0000000043060)
                        if (aidData.contains("A0000000043060") || aidData.contains("9F0607A0000000043060")) {
                            hasMastercardPayPass = true;
                            LogUtil.e(Constant.TAG, "    ✓ Found Mastercard PayPass AID");
                        }
                    }
                    
                    if (!hasMastercardPayPass) {
                        LogUtil.e(Constant.TAG, "⚠️ WARNING: Mastercard PayPass (A0000000043060) not found in queried AIDs");
                        LogUtil.e(Constant.TAG, "⚠️ NOTE: SDK built-in AIDs may still be available (not shown in queryAidCapkList)");
                    }
                    
                    if (aidList.isEmpty()) {
                        LogUtil.e(Constant.TAG, "⚠️ WARNING: No AIDs returned from query! This may indicate issue, but SDK built-ins may still work");
                    }
                } else {
                    LogUtil.e(Constant.TAG, "⚠️ Could not query AID list (code: " + queryResult + ")");
                    LogUtil.e(Constant.TAG, "⚠️ NOTE: SDK built-in AIDs should still be available for Mastercard PayPass");
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "⚠️ Error querying AIDs: " + e.getMessage());
                LogUtil.e(Constant.TAG, "⚠️ NOTE: SDK built-in AIDs should still be available");
            }
            
            Bundle bundle = new Bundle();
            
            // PayLib v2.0.32 requires date and time in EMV bundle
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US);
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
            java.util.Date now = new java.util.Date();
            
            // Use amount as-is, not zero-padded
            bundle.putString("amount", mAmount); // Amount in piasters
            bundle.putString("transType", "00"); // Purchase transaction
            bundle.putString("currencyCode", com.neo.neopayplus.config.PaymentConfig.getCurrencyCode());
            bundle.putString("date", dateFormat.format(now)); // YYMMDD format
            bundle.putString("time", timeFormat.format(now)); // HHMMSS format
            
            // PayLib v2.0.32: flowType constants
            // TYPE_EMV_STANDARD (0x01) for contact EMV
            // TYPE_NFC_SPEEDUP (0x04) for contactless (QPBOC, PayPass, PayWave)
            // Note: flowType=0x04 only valid for contactless transaction
            int flowTypeValue;
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                flowTypeValue = AidlConstantsV2.EMV.FlowType.TYPE_NFC_SPEEDUP; // Contactless speedup (0x04)
                bundle.putInt("flowType", flowTypeValue);
            } else {
                flowTypeValue = AidlConstantsV2.EMV.FlowType.TYPE_EMV_STANDARD; // Contact standard (0x01)
                bundle.putInt("flowType", flowTypeValue);
            }
            bundle.putInt("cardType", mCardType); // Card type (IC or NFC)
            
            LogUtil.e(Constant.TAG, "=== TRANSACTION START ===");
            LogUtil.e(Constant.TAG, "Transaction data - Amount: " + mAmount + ", CardType: " + mCardType + 
                     ", Date: " + dateFormat.format(now) + ", Time: " + timeFormat.format(now));
            LogUtil.e(Constant.TAG, "flowType: " + flowTypeValue + " (0x" + Integer.toHexString(flowTypeValue) + 
                     (mCardType == AidlConstantsV2.CardType.NFC.getValue() ? " = TYPE_NFC_SPEEDUP for contactless)" : " = TYPE_EMV_STANDARD for contact)"));
            
            // transactProcessEx returns void in 2.0.32; results arrive via callbacks
            LogUtil.e(Constant.TAG, "Calling transactProcessEx() with flowType=" + flowTypeValue + "...");
            mEMVOptV2.transactProcessEx(bundle, mEMVListener);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Error starting transaction", 0);
            showToast("Error starting transaction: " + e.getMessage());
        }
    }
    
    private final EMVListenerV2 mEMVListener = new EMVListenerV2.Stub() {
        @Override
        public void onWaitAppSelect(List<EMVCandidateV2> appNameList, boolean isFirstSelect) throws RemoteException {
            LogUtil.e(Constant.TAG, "=== AID SELECTION (EMV Kernel Automatic Selection) ===");
            LogUtil.e(Constant.TAG, "onWaitAppSelect isFirstSelect:" + isFirstSelect);
            LogUtil.e(Constant.TAG, "Note: EMV kernel automatically matched card AID (tag 84) with terminal's loaded AIDs");
            LogUtil.e(Constant.TAG, "AID candidate list size: " + (appNameList != null ? appNameList.size() : 0));
            
            if (appNameList != null && !appNameList.isEmpty()) {
                for (int i = 0; i < appNameList.size(); i++) {
                    EMVCandidateV2 candidate = appNameList.get(i);
                    LogUtil.e(Constant.TAG, "  Candidate " + i + ": " + candidate.appLabel + " (AID: " + candidate.aid + ")");
                }
                // Auto-select first candidate (EMV kernel has already matched, we just confirm)
            runOnUiThread(() -> updateStatus("Selecting application...", 75));
            try {
                mEMVOptV2.importAppSelect(0);
                    LogUtil.e(Constant.TAG, "✓ Auto-selected first AID candidate (index 0) - EMV kernel matched successfully");
            } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "❌ Error selecting AID: " + e.getMessage());
                e.printStackTrace();
                }
            } else {
                LogUtil.e(Constant.TAG, "❌ CRITICAL: AID candidate list is empty!");
                LogUtil.e(Constant.TAG, "⚠️ EMV kernel could not match card's AID (tag 84) with any loaded terminal AIDs");
                LogUtil.e(Constant.TAG, "⚠️ This means L2 candidate matching failed - card AID doesn't match terminal AIDs");
                LogUtil.e(Constant.TAG, "⚠️ Possible causes:");
                LogUtil.e(Constant.TAG, "    1. Card AID (tag 84) doesn't match any loaded AIDs in terminal");
                LogUtil.e(Constant.TAG, "    2. Terminal parameters (9F33, 9F40, 9F35, 9F1A, 5F2A, 9F53) incorrect for contactless");
                LogUtil.e(Constant.TAG, "    3. Contactless AIDs missing selFlag=0 (required for partial matching)");
                LogUtil.e(Constant.TAG, "    4. flowType mismatch (should be 0x04 for contactless, may bypass onWaitAppSelect)");
                runOnUiThread(() -> updateStatus("No matching AID found...", 75));
            }
        }

        @Override
        public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
            LogUtil.e(Constant.TAG, "=== AID FINAL SELECTION ===");
            LogUtil.e(Constant.TAG, "onAppFinalSelect: " + tag9F06Value);
            LogUtil.e(Constant.TAG, "Note: This is the AID (tag 84) that EMV kernel selected from card");
            runOnUiThread(() -> updateStatus("Application selected...", 80));
            
            // Diagnostic: Query selected AID and CAPK index to verify correct selection
            try {
                String[] diagnosticTags = {"84", "8F", "90"};
                byte[] outData = new byte[512];
                int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, diagnosticTags, outData);
                if (len > 0) {
                    // Parse TLV to get tag 84 (AID) and tag 8F (CAPK index)
                    // For now, just log that we got data
                    LogUtil.e(Constant.TAG, "✓ Diagnostic TLV data retrieved (AID tag 84, CAPK index tag 8F)");
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "⚠️ Could not retrieve diagnostic AID/CAPK info: " + e.getMessage());
            }
        }

        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            // Security: Never log full PAN
            LogUtil.e(Constant.TAG, "onConfirmCardNo: " + (cardNo != null ? maskCardNumber(cardNo) : "null"));
            mCardNo = cardNo;
            runOnUiThread(() -> updateStatus("Card number confirmed...", 85));
            // Auto-confirm card number
            try {
                mEMVOptV2.importCardNoStatus(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRequestShowPinPad(int pinType, int remainTimes) throws RemoteException {
            if (pinType == 1) {
                LogUtil.e(Constant.TAG, "=== ONLINE PIN FLOW STARTED ===");
            } else {
            LogUtil.e(Constant.TAG, "=== OFFLINE PIN FLOW STARTED ===");
            }
            LogUtil.e(Constant.TAG, "onRequestShowPinPad pinType:" + pinType + " remainTimes:" + remainTimes);
            
            // Store current PIN type
            mCurrentPinType = pinType;
            
            // Mark that PIN will be entered in this transaction
            pinEnteredThisTxn = true;
            LogUtil.e(Constant.TAG, "✓ PIN entry marked for this transaction");
            
            // Log current PIN attempts status
            int attemptsLeft = mPinAttemptsLeft.get();
            LogUtil.e(Constant.TAG, "📊 PIN attempts remaining: " + attemptsLeft + " / " + MAX_PIN_ATTEMPTS);
            
            // Get card number if not already available (required for PinPad PAN)
            if (mCardNo == null || mCardNo.isEmpty()) {
                mCardNo = getCardNo();
                LogUtil.e(Constant.TAG, "Retrieved card number for PinPad: " + (mCardNo != null && !mCardNo.isEmpty() ? maskCardNumber(mCardNo) : "Failed to retrieve"));
            }
            
            LogUtil.e(Constant.TAG, "Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
            LogUtil.e(Constant.TAG, "Amount: " + mAmount + " " + com.neo.neopayplus.config.PaymentConfig.CURRENCY_NAME);
            
            // Extract EMV tags for debugging PIN/CVM issues
            extractPinAndCvmData();
            
            if (pinType == 0) {
                LogUtil.e(Constant.TAG, "🔑 OFFLINE PIN REQUESTED - PIN will be verified by card itself");
                LogUtil.e(Constant.TAG, "✓ Terminal handling PIN according to card's CVM List");
                LogUtil.e(Constant.TAG, "📊 Remaining PIN attempts (card): " + (remainTimes >= 0 ? remainTimes : "Unknown (-1 = counter unavailable)"));
                if (remainTimes == -1) {
                    LogUtil.e(Constant.TAG, "⚠️ PIN Try Counter unavailable (remainTimes=-1) - card may not support GET DATA for PIN counter");
                }
                runOnUiThread(() -> updateStatus("Enter PIN (Offline) - Attempts: " + (remainTimes >= 0 ? remainTimes : "?"), 90));
            } else if (pinType == 1) {
                LogUtil.e(Constant.TAG, "🌐 ONLINE PIN REQUESTED - PIN will be sent to backend");
                LogUtil.e(Constant.TAG, "✓ Correct CVM flow for cards requiring Online PIN only");
                LogUtil.e(Constant.TAG, "📊 Remaining PIN attempts (terminal): " + attemptsLeft);
                runOnUiThread(() -> updateStatus("Enter PIN (Online) - Attempts left: " + attemptsLeft, 90));
            } else {
                LogUtil.e(Constant.TAG, "❓ Unknown PIN type: " + pinType);
                runOnUiThread(() -> updateStatus("PIN required (Type: " + pinType + ")", 90));
            }
            
            // Use Sunmi SDK secure PinPad instead of custom PIN input
            try {
                LogUtil.e(Constant.TAG, "Launching Sunmi secure PinPad - Amount: " + mAmount + " " + com.neo.neopayplus.config.PaymentConfig.CURRENCY_NAME + ", Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
                
                // Configure PinPad for secure PIN entry with proper CVM handling
                com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2 pinPadConfig = new com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2();
                pinPadConfig.setPinPadType(0); // 0 = SDK built-in PinPad, 1 = Client customized PinPad
                pinPadConfig.setPinType(pinType); // 0 = offline PIN, 1 = online PIN
                pinPadConfig.setOrderNumKey(false); // Don't use ordered number key
                
                // Key system configuration
                // 0 = MKSK (Master Key Set Key) - Default for most terminals
                // 1 = DUKPT (Derived Unique Key Per Transaction) - Required for online PIN in some regions
                // For online PIN, DUKPT provides better security with unique keys per transaction
                // Check terminal capability or configuration for DUKPT support
                // If your acquirer requires DUKPT for online PIN, set keySystem = 1
                // Note: DUKPT keys must be initialized via SunmiPayLibKeyManager.saveDukptKey()
                int keySystem = 0; // Default to MKSK - update if DUKPT required by acquirer
                pinPadConfig.setKeySystem(keySystem);
                
                pinPadConfig.setAlgorithmType(0); // 0 = 3DES (most common), 1 = SM4, 2 = AES
                pinPadConfig.setMaxInput(6); // Maximum PIN length
                pinPadConfig.setMinInput(4); // Minimum PIN length
                pinPadConfig.setTimeout(PIN_ENTRY_TIMEOUT_MS); // 60 seconds timeout (per guide)
                // PIN key index (TPK - Terminal PIN Key)
                // Use active slot from KeyManagerPOS (session TPK with pin_key_id)
                int activePinSlot = com.neo.neopayplus.security.KeyManagerPOS.getActivePinSlot();
                pinPadConfig.setPinKeyIndex(activePinSlot);
                LogUtil.e(Constant.TAG, "Using session TPK slot: " + activePinSlot);
                
                // PIN Block Format - CRITICAL for online PIN
                // ISO 9564 Format 0: Most common, uses PAN (12 digits excluding check digit)
                // ISO 9564 Format 4: Uses full PAN (12-19 digits) - used with AES
                // For online PIN, Format 0 is standard (PIN block sent to backend in authorization)
                pinPadConfig.setPinblockFormat(PinBlockFormat.SEC_PIN_BLK_ISO_FMT0);
                
                LogUtil.e(Constant.TAG, "PinPad Configuration:");
                LogUtil.e(Constant.TAG, "  PIN Type: " + (pinType == 1 ? "Online" : "Offline"));
                LogUtil.e(Constant.TAG, "  Key System: " + (keySystem == 1 ? "DUKPT" : "MKSK"));
                LogUtil.e(Constant.TAG, "  PIN Block Format: ISO-0");
                LogUtil.e(Constant.TAG, "  Algorithm: 3DES");
                LogUtil.e(Constant.TAG, "  Key Index: 12");
                
                // Set PAN if available - REQUIRED for PIN block encryption (ISO-0 format)
                if (mCardNo != null && mCardNo.length() >= 14) {
                    try {
                        // For ISO-0 format: Use only the last 12 digits of PAN (excluding check digit)
                        // This is required for PIN block encryption
                        String panSubstring = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1);
                        byte[] panBytes = panSubstring.getBytes("US-ASCII");
                        pinPadConfig.setPan(panBytes);
                        LogUtil.e(Constant.TAG, "PAN set for PinPad: " + panSubstring + " (required for PIN block encryption)");
                    } catch (Exception e) {
                        LogUtil.e(Constant.TAG, "Error setting PAN for PinPad: " + e.getMessage());
                        // Continue without PAN - PinPad may fail for online PIN
                    }
                } else {
                    LogUtil.e(Constant.TAG, "⚠️ WARNING: No valid PAN available for PinPad");
                    LogUtil.e(Constant.TAG, "⚠️ PAN is required for online PIN encryption (ISO-0 format)");
                }
                
                // Use the secure PinPad from the SDK
                mPinPadOptV2.initPinPad(pinPadConfig, new PinPadListenerV2.Stub() {
                    @Override
                    public void onPinLength(int length) throws RemoteException {
                        LogUtil.e(Constant.TAG, "📝 PIN input progress: " + length + " digits entered");
                        runOnUiThread(() -> updateStatus("PIN: " + length + " digits", 90));
                    }

                    @Override
                    public void onConfirm(int type, byte[] pinBlock) throws RemoteException {
                        LogUtil.e(Constant.TAG, "=== PIN ENTRY COMPLETED ===");
                        // Security: Only log PIN block in debug builds, and mask it
                        if (com.neo.neopayplus.BuildConfig.DEBUG) {
                            String pinBlockHex = ByteUtil.bytes2HexStr(pinBlock);
                            // Mask PIN block for security - only show first 4 and last 4 hex chars
                            String maskedPinBlock = pinBlockHex.length() > 8 
                                ? pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4)
                                : "****";
                            LogUtil.e(Constant.TAG, "🔐 PIN block received (encrypted, masked): " + maskedPinBlock);
                        } else {
                            LogUtil.e(Constant.TAG, "🔐 PIN block received (encrypted) - length: " + pinBlock.length + " bytes");
                        }
                        LogUtil.e(Constant.TAG, "📋 PIN type: " + type + " (0=Offline, 1=Online)");
                        LogUtil.e(Constant.TAG, "💳 Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
                        
                        if (pinType == 0) {
                            // ============================================================
                            // OFFLINE PIN FLOW (per EMV flowchart)
                            // ============================================================
                            // 1. User enters PIN → PIN block received
                            // 2. ✅ CRITICAL: Notify EMV kernel IMMEDIATELY (before 10s timeout)
                            // 3. Card verifies PIN internally (no backend needed for PIN verification)
                            // 4. Backend authorization happens later in onOnlineProc() (does NOT block)
                            // ============================================================
                            LogUtil.e(Constant.TAG, "🔑 OFFLINE PIN FLOW STARTED");
                            LogUtil.e(Constant.TAG, "📝 PIN block received from card");
                            LogUtil.e(Constant.TAG, "⏳ Card will verify PIN internally using ICC");
                            LogUtil.e(Constant.TAG, "ℹ️ PIN block length: " + pinBlock.length + " bytes");
                            
                            // ✅ CRITICAL STEP: Notify EMV kernel IMMEDIATELY after PIN entry
                            // This MUST happen BEFORE backend call to prevent 10s EMV timeout
                            // Backend authorization will happen later in onOnlineProc() asynchronously
                            runOnUiThread(() -> updateStatus("PIN verified by card", 95));
                        try {
                                mEMVOptV2.importPinInputStatus(0, 0); // PIN OK (0 = success, 0 = not cancelled)
                                LogUtil.e(Constant.TAG, "✅ EMV kernel notified IMMEDIATELY: Offline PIN entry successful");
                                LogUtil.e(Constant.TAG, "✓ This notification prevents EMV timeout (must be < 10s)");
                                LogUtil.e(Constant.TAG, "✓ Backend authorization will happen in onOnlineProc() later (non-blocking)");
                        } catch (Exception e) {
                                LogUtil.e(Constant.TAG, "❌ CRITICAL ERROR: Failed to notify EMV kernel of offline PIN - may cause timeout!");
                                LogUtil.e(Constant.TAG, "   Error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            // ============================================================
                            // ONLINE PIN FLOW (per EMV flowchart)
                            // ============================================================
                            // 1. User enters PIN → PIN block received
                            // 2. Store PIN block + KSN for backend verification
                            // 3. Wait for onOnlineProc() callback from EMV kernel
                            // 4. Send PIN block + KSN to backend for verification
                            // 5. Backend verifies PIN and returns result
                            // 6. Notify EMV kernel with importOnlineProcStatus() after backend response
                            // ============================================================
                            LogUtil.e(Constant.TAG, "🌐 ONLINE PIN FLOW STARTED");
                            LogUtil.e(Constant.TAG, "📝 PIN block received from terminal");
                            LogUtil.e(Constant.TAG, "ℹ️ PIN block length: " + pinBlock.length + " bytes (should be 8 bytes for ISO-0)");
                            LogUtil.e(Constant.TAG, "ℹ️ PIN block format: ISO-0");
                            
                            // Store PIN block securely (will be sent to backend in onOnlineProc)
                            mOnlinePinBlock = new byte[pinBlock.length];
                            System.arraycopy(pinBlock, 0, mOnlinePinBlock, 0, pinBlock.length);
                            mCurrentPinType = 1; // Mark as online PIN
                            
                            // Fetch KSN (required for online PIN decryption at backend using DUKPT)
                            fetchKsnForOnlinePin();
                            
                            runOnUiThread(() -> updateStatus("PIN entered. Waiting for backend verification...", 95));
                            
                            // ✅ CRITICAL: For online PIN, DO NOT call importPinInputStatus() here
                            // The EMV kernel will call onOnlineProc() next, which will:
                            // 1. Send PIN block + KSN to backend
                            // 2. Wait for backend response
                            // 3. Call importOnlineProcStatus() with backend result
                            // This flow allows backend verification while preventing timeout
                            LogUtil.e(Constant.TAG, "✓ Online PIN block + KSN stored");
                            LogUtil.e(Constant.TAG, "⏳ Waiting for onOnlineProc() to send PIN block to backend");
                            LogUtil.e(Constant.TAG, "✓ EMV kernel will wait for backend response (no timeout risk)");
                        }
                        
                        // Security: Zero the original pinBlock array after copying (if online PIN)
                        if (pinType == 1) {
                            java.util.Arrays.fill(pinBlock, (byte) 0);
                        }
                    }

                    @Override
                    public void onCancel() throws RemoteException {
                        LogUtil.e(Constant.TAG, "=== PIN ENTRY CANCELLED ===");
                        LogUtil.e(Constant.TAG, "🚫 User cancelled PIN entry");
                        LogUtil.e(Constant.TAG, "💳 Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
                        runOnUiThread(() -> updateStatus("PIN entry cancelled", 0));
                        
                        // Handle cancellation based on PIN type
                        if (pinType == 0) {
                            // Offline PIN: Notify EMV kernel of cancellation
                        try {
                            mEMVOptV2.importPinInputStatus(1, 0); // PIN cancelled
                                LogUtil.e(Constant.TAG, "✅ EMV kernel notified: Offline PIN entry cancelled");
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "❌ Error notifying EMV kernel of PIN cancellation: " + e.getMessage());
                            }
                        } else {
                            // Online PIN: Clear stored PIN block, offer fallback if allowed
                            mOnlinePinBlock = null;
                            mKsn = null;
                            if (ALLOW_PIN_FALLBACK_TO_SIGNATURE) {
                                LogUtil.e(Constant.TAG, "⚠️ PIN cancelled - offering signature fallback");
                                // TODO: Implement signature fallback UI
                                runOnUiThread(() -> updateStatus("PIN cancelled. Signature required?", 90));
                            } else {
                            // Decline transaction
                            try {
                                String[] tags = {};
                                String[] values = {};
                                byte[] out = new byte[1024];
                                mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // 1 = decline
                                LogUtil.e(Constant.TAG, "✅ EMV kernel notified: Transaction declined (PIN cancelled)");
                            } catch (Exception e) {
                                LogUtil.e(Constant.TAG, "❌ Error declining transaction: " + e.getMessage());
                            }
                            }
                        }
                    }

                    @Override
                    public void onError(int errCode) throws RemoteException {
                        LogUtil.e(Constant.TAG, "=== PIN ENTRY ERROR ===");
                        LogUtil.e(Constant.TAG, "❌ PIN entry error: " + errCode);
                        LogUtil.e(Constant.TAG, "💳 Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
                        runOnUiThread(() -> updateStatus("PIN entry error: " + errCode, 0));
                        
                        // Handle error based on PIN type
                        if (pinType == 0) {
                            // Offline PIN: Notify EMV kernel of error
                        try {
                            mEMVOptV2.importPinInputStatus(1, 0); // PIN failed
                                LogUtil.e(Constant.TAG, "✅ EMV kernel notified: Offline PIN entry failed");
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "❌ Error notifying EMV kernel of PIN error: " + e.getMessage());
                            }
                        } else {
                            // Online PIN: Clear stored PIN block, offer fallback if allowed
                            mOnlinePinBlock = null;
                            mKsn = null;
                            if (ALLOW_PIN_FALLBACK_TO_SIGNATURE) {
                                LogUtil.e(Constant.TAG, "⚠️ PIN error - offering signature fallback");
                                runOnUiThread(() -> updateStatus("PIN error. Signature required?", 90));
                            } else {
                                // Decline transaction
                                try {
                                    String[] tags = {};
                                    String[] values = {};
                                    byte[] out = new byte[1024];
                                    mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // 1 = decline
                                    LogUtil.e(Constant.TAG, "✅ EMV kernel notified: Transaction declined (PIN error)");
                                } catch (Exception e) {
                                    LogUtil.e(Constant.TAG, "❌ Error declining transaction: " + e.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onHover(int event, byte[] data) throws RemoteException {
                        // Handle hover events for accessibility (optional)
                        LogUtil.e(Constant.TAG, "🎯 PIN pad hover event: " + event);
                    }
                });
                
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error launching Sunmi secure PinPad: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback: notify EMV kernel that PIN entry failed
                try {
                    mEMVOptV2.importPinInputStatus(1, 0);
                } catch (Exception ex) {
                    LogUtil.e(Constant.TAG, "Error notifying EMV kernel of PIN failure: " + ex.getMessage());
                }
            }
        }

        @Override
        public void onRequestSignature() throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestSignature");
            runOnUiThread(() -> updateStatus("Signature required...", 92));
            // Auto-approve signature
            try {
                mEMVOptV2.importSignatureStatus(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onCertVerify certType:" + certType + " certInfo:" + certInfo);
            runOnUiThread(() -> updateStatus("Certificate verification...", 93));
            // Auto-approve certificate
            try {
                mEMVOptV2.importCertStatus(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onOnlineProc() throws RemoteException {
            // ============================================================
            // ONLINE PROCESSING CALLBACK (called by EMV kernel)
            // ============================================================
            // This is called AFTER PIN entry for both offline and online PIN:
            // - Offline PIN: PIN already verified by card, kernel notified immediately
            //   → Backend authorization happens here (non-blocking, no timeout risk)
            // - Online PIN: PIN block stored, waiting for backend verification
            //   → Send PIN block to backend, wait for response, notify kernel
            // ============================================================
            LogUtil.e(Constant.TAG, "=== ONLINE PROCESSING REQUESTED ===");
            LogUtil.e(Constant.TAG, "🌐 EMV kernel requesting online authorization");
            LogUtil.e(Constant.TAG, "📋 PIN Type: " + (mCurrentPinType == 0 ? "Offline PIN (already verified by card)" : "Online PIN (needs backend verification)"));
            runOnUiThread(() -> updateStatus("Online processing... Building Field 55", 95));
            
            try {
                // PayLib v2.0.32: Prefer getAccountSecData() with getTlvList() fallback
                Bundle tlvBundle = null;
                String field55 = "";
                
                try {
                    // Try secure account data extraction first (PayLib v2.0.32 preferred)
                    String[] field55Tags = {
                        "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                        "82", "9F1A", "9F34", "9F33", "9F35", "9F1E", "84", "9F09", "9F41", "5A", "5F24", "5F34"
                    };
                    Bundle secDataBundle = new Bundle();
                    int result = mEMVOptV2.getAccountSecData(0, field55Tags, secDataBundle);
                    if (result == 0) {
                        tlvBundle = secDataBundle;
                        LogUtil.e(Constant.TAG, "Using getAccountSecData() for secure extraction");
                    } else {
                        throw new Exception("getAccountSecData failed with code: " + result);
                    }
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "getAccountSecData() failed, falling back to getTlvList(): " + e.getMessage());
                    
                    // Fallback to getTlvList() with full Apple Pay tag list
                    String[] field55Tags = {
                        "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                        "82", "9F1A", "9F34", "9F33", "9F35", "9F1E", "84", "9F09", "9F41", "5A", "5F24", "5F34"
                    };
                    
                    byte[] outData = new byte[2048];
                    int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, field55Tags, outData);
                    if (len > 0) {
                        byte[] tlvData = new byte[len];
                        System.arraycopy(outData, 0, tlvData, 0, len);
                        field55 = ByteUtil.bytes2HexStr(tlvData);
                        // Security: Only log Field 55 in debug builds (contains ARQC and sensitive EMV data)
                        if (com.neo.neopayplus.BuildConfig.DEBUG) {
                            String maskedField55 = field55.length() > 20 
                                ? field55.substring(0, 10) + "..." + field55.substring(field55.length() - 10)
                                : "****";
                            LogUtil.e(Constant.TAG, "Field 55 built using getTlvList() (masked): " + maskedField55);
                        } else {
                            LogUtil.e(Constant.TAG, "Field 55 built using getTlvList() - length: " + field55.length() + " bytes");
                        }
                    }
                }
                
                if (tlvBundle != null) {
                    // Build Field 55 from secure data bundle
                    field55 = buildField55FromBundle(tlvBundle);
                    // Security: Only log Field 55 in debug builds
                    if (com.neo.neopayplus.BuildConfig.DEBUG) {
                        String maskedField55 = field55.length() > 20 
                            ? field55.substring(0, 10) + "..." + field55.substring(field55.length() - 10)
                            : "****";
                        LogUtil.e(Constant.TAG, "Field 55 built from getAccountSecData() (masked): " + maskedField55);
                    } else {
                        LogUtil.e(Constant.TAG, "Field 55 built from getAccountSecData() - length: " + field55.length() + " bytes");
                    }
                }
                
                // Extract CVM Result (9F34) to determine if PIN should be sent to backend
                mCvmResultCode = extractCvmResultCode();
                LogUtil.e(Constant.TAG, "=== CVM RESULT ANALYSIS ===");
                LogUtil.e(Constant.TAG, "CVM Result Code (9F34): " + (mCvmResultCode != null ? mCvmResultCode : "Not available"));
                
                // Determine PIN handling based on CVM Result
                boolean shouldSendPinToBackend = false;
                String cvmDescription = "";
                
                if (mCvmResultCode != null) {
                    switch (mCvmResultCode) {
                        case "00":
                            cvmDescription = "No CVM required";
                            shouldSendPinToBackend = false;
                            LogUtil.e(Constant.TAG, "✓ No PIN required - skipping PIN block");
                            break;
                        case "01":
                        case "02":
                            // Online PIN codes: 0x01 or 0x02 indicate online PIN required
                            cvmDescription = "Online PIN required";
                            shouldSendPinToBackend = true;
                            LogUtil.e(Constant.TAG, "✓ Online PIN detected (code: " + mCvmResultCode + ") - PIN block will be sent to backend");
                            break;
                        case "42":
                            // Offline PIN: Card already verified PIN internally
                            cvmDescription = "Offline PIN verified by card";
                            shouldSendPinToBackend = false;
                            LogUtil.e(Constant.TAG, "✓ Offline PIN detected (code: 42) - Card verified PIN, do NOT send PIN block to backend");
                            break;
                        case "03":
                        case "5E":
                            cvmDescription = "CDCVM performed (Apple Pay/Google Pay)";
                            shouldSendPinToBackend = false;
                            LogUtil.e(Constant.TAG, "✓ CDCVM detected - no PIN block needed");
                            break;
                        default:
                            cvmDescription = "Unknown CVM code: " + mCvmResultCode;
                            shouldSendPinToBackend = false;
                            LogUtil.e(Constant.TAG, "⚠️ Unknown CVM code: " + mCvmResultCode + " - not sending PIN block");
                            break;
                    }
                } else {
                    // Fallback: if CVM result not available, check PIN type from EMV kernel
                    if (mCurrentPinType == 1) {
                        shouldSendPinToBackend = true;
                        cvmDescription = "Online PIN (detected from PIN type)";
                        LogUtil.e(Constant.TAG, "⚠️ CVM result not available, but PIN type indicates online PIN - will send PIN block");
                    } else {
                        shouldSendPinToBackend = false;
                        cvmDescription = "CVM result not available";
                        LogUtil.e(Constant.TAG, "⚠️ CVM result not available - not sending PIN block");
                    }
                }
                
                // Send transaction to backend for authorization via API service
                // Security: Never log full Field 55 (contains ARQC and sensitive EMV data)
                if (com.neo.neopayplus.BuildConfig.DEBUG) {
                    String maskedField55 = field55.length() > 20 
                        ? field55.substring(0, 10) + "..." + field55.substring(field55.length() - 10)
                        : "****";
                    LogUtil.e(Constant.TAG, "Field 55 ready for backend authorization (masked): " + maskedField55);
                } else {
                    LogUtil.e(Constant.TAG, "Field 55 ready for backend authorization - length: " + field55.length() + " bytes");
                }
                
                // Build authorization request
                com.neo.neopayplus.api.PaymentApiService.AuthorizationRequest authRequest = 
                    new com.neo.neopayplus.api.PaymentApiService.AuthorizationRequest();
                authRequest.field55 = field55;
                authRequest.pan = mCardNo;
                authRequest.amount = mAmount;
                authRequest.currencyCode = com.neo.neopayplus.config.PaymentConfig.getCurrencyCode();
                authRequest.transactionType = "00"; // Purchase
                
                // Set date/time
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US);
                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
                java.util.Date now = new java.util.Date();
                authRequest.date = dateFormat.format(now);
                authRequest.time = timeFormat.format(now);
                
                // Include PIN block + KSN ONLY if CVM indicates online PIN
                if (shouldSendPinToBackend && mOnlinePinBlock != null && mOnlinePinBlock.length > 0) {
                    String pinBlockHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(mOnlinePinBlock);
                    authRequest.pinBlock = pinBlockHex.getBytes(); // Convert hex string to bytes
                    authRequest.ksn = mKsn;
                    LogUtil.e(Constant.TAG, "✓ Including PIN block + KSN in authorization request (" + cvmDescription + ")");
                    if (com.neo.neopayplus.BuildConfig.DEBUG) {
                        LogUtil.e(Constant.TAG, "  PIN block (masked): " + 
                            (pinBlockHex.length() > 8 
                                ? pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4)
                                : "****"));
                        LogUtil.e(Constant.TAG, "  KSN: " + (mKsn != null ? mKsn : "null"));
                    }
                } else {
                    if (mCvmResultCode != null && "42".equals(mCvmResultCode)) {
                        LogUtil.e(Constant.TAG, "✓ Offline PIN transaction - PIN already verified by card, NOT sending PIN block to backend");
                    } else if ("00".equals(mCvmResultCode)) {
                        LogUtil.e(Constant.TAG, "✓ No PIN required - NOT sending PIN block to backend");
                    } else {
                        LogUtil.e(Constant.TAG, "⚠️ No PIN block available or CVM indicates offline PIN - transaction may be offline PIN or no PIN required");
                }
                }
                
                // Extract EMV tags (AID, TSI, TVR, DE55) immediately before sending online
                try {
                    // Extract Field 55 (ICC Data)
                    byte[] field55Bytes = new byte[2048];
                    int field55Len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_READ, "55", field55Bytes);
                    String tlv55 = field55Len > 0 ? ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(field55Bytes, field55Len)) : null;
                    
                    // Extract AID (tag 84)
                    byte[] aidBytes = new byte[256];
                    int aidLen = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_READ, "84", aidBytes);
                    currentAid = aidLen > 0 ? ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(aidBytes, aidLen)) : null;
                    
                    // Extract TSI (tag 9B)
                    byte[] tsiBytes = new byte[64];
                    int tsiLen = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_READ, "9B", tsiBytes);
                    currentTsi = tsiLen > 0 ? ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(tsiBytes, tsiLen)) : null;
                    
                    // Extract TVR (tag 95)
                    byte[] tvrBytes = new byte[64];
                    int tvrLen = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_READ, "95", tvrBytes);
                    currentTvr = tvrLen > 0 ? ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(tvrBytes, tvrLen)) : null;
                    
                    // Extract PAN (tag 5A) and mask it
                    byte[] panBytes = new byte[256];
                    int panLen = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_READ, "5A", panBytes);
                    String panHex = panLen > 0 ? ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(panBytes, panLen)) : null;
                    if (panHex != null && panHex.length() > 0) {
                        // Convert hex PAN to ASCII and mask
                        String panAscii = hexToAscii(panHex);
                        lastPanMasked = maskCardNumber(panAscii);
                    }
                    
                    // Build DE22 (Entry Mode)
                    currentEntryMode = EntryModeUtil.de22(mCardType, pinEnteredThisTxn, fallbackUsed);
                    
                    // Get KSN for active slot
                    String ksn = getKsnForActiveSlot();
                    if (!TextUtils.isEmpty(ksn)) {
                        authRequest.ksn = ksn;
                    }
                    
                    // STAN is already set via generateStan() - AuthorizationRequest doesn't have stan field
                    
                    LogUtil.e(Constant.TAG, "✓ Extracted EMV tags:");
                    LogUtil.e(Constant.TAG, "  STAN: " + currentStan);
                    LogUtil.e(Constant.TAG, "  Entry Mode: " + currentEntryMode);
                    LogUtil.e(Constant.TAG, "  AID: " + (currentAid != null ? currentAid.substring(0, Math.min(20, currentAid.length())) + "..." : "null"));
                    LogUtil.e(Constant.TAG, "  TSI: " + (currentTsi != null ? currentTsi : "null"));
                    LogUtil.e(Constant.TAG, "  TVR: " + (currentTvr != null ? currentTvr : "null"));
                    LogUtil.e(Constant.TAG, "  KSN: " + (ksn != null && !ksn.isEmpty() ? ksn.substring(0, Math.min(20, ksn.length())) + "..." : "null"));
                    
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "⚠️ Error extracting EMV tags: " + e.getMessage());
                    e.printStackTrace();
                    // Continue with transaction even if tag extraction fails
                    currentEntryMode = EntryModeUtil.de22(mCardType, pinEnteredThisTxn, fallbackUsed);
                }
                
                // Build and store exact JSON request body that will be sent to backend
                mBackendRequestJson = buildBackendRequestJson(authRequest);
                LogUtil.e(Constant.TAG, "✓ Built backend request JSON (length: " + mBackendRequestJson.length() + " chars)");
                
                LogUtil.e(Constant.TAG, "Requesting backend authorization: " + authRequest.toString());
                
                // (Non-invasive) POS-only host gateway call for 1200 (mock)
                try {
                    com.neo.neopayplus.host.dto.PurchaseReq req = new com.neo.neopayplus.host.dto.PurchaseReq();
                    req.panMasked = lastPanMasked != null ? lastPanMasked : (mCardNo != null ? maskCardNumber(mCardNo) : "");
                    req.de3 = "000000"; // Processing Code
                    req.de4 = String.format(java.util.Locale.US, "%012d", Long.parseLong(mAmount)); // Amount (minor units)
                    req.de11 = String.format(java.util.Locale.US, "%06d", currentStan); // STAN
                    // DE12: Local time (hhmmss) - extract from YYMMDDhhmmss
                    String localFull = com.neo.neopayplus.utils.TimeUtil.localYYMMDDhhmmss(); // Returns "yyMMddHHmmss"
                    req.de12 = localFull.substring(6); // Extract "HHmmss" part
                    req.de7 = com.neo.neopayplus.utils.TimeUtil.gmtYYMMDDhhmm(); // DE7: Transmission datetime (YYMMDDhhmm) - GMT
                    req.de14 = "0000"; // Expiry (YYMM) - fill if available
                    req.de18 = "0000"; // MCC - fill if available
                    req.de22 = currentEntryMode != null ? currentEntryMode : com.neo.neopayplus.utils.EntryModeUtil.de22(mCardType, pinEnteredThisTxn, fallbackUsed);
                    req.de24 = "200"; // Function code for purchase
                    req.de32 = "000000"; // Acquirer ID (LLVAR) - supply real acquirer id when available
                    req.de41 = com.neo.neopayplus.config.PaymentConfig.getTerminalId(); // TID
                    req.de42 = com.neo.neopayplus.config.PaymentConfig.getMerchantId(); // MID
                    req.de49 = com.neo.neopayplus.config.PaymentConfig.getCurrencyCode(); // Currency
                    req.de55 = (currentAid != null || currentTsi != null || currentTvr != null) ? new byte[0] : new byte[0]; // placeholder
                    req.pinPresent = (mOnlinePinBlock != null && mOnlinePinBlock.length > 0);
                    if (req.pinPresent) req.de52 = mOnlinePinBlock;
                    
                    com.neo.neopayplus.host.dto.HostResult mockResp = com.neo.neopayplus.utils.ServiceLocator.host().purchase(req);
                    LogUtil.e(Constant.TAG, "[HostGateway] purchase rc=" + mockResp.rc + ", rrn=" + mockResp.rrn + ", auth=" + mockResp.authCode);
                } catch (Throwable th) {
                    LogUtil.e(Constant.TAG, "[HostGateway] purchase mock call error: " + th.getMessage());
                }
                
                // Get API service and request authorization
                com.neo.neopayplus.api.PaymentApiService apiService = 
                    com.neo.neopayplus.api.PaymentApiFactory.getInstance();
                
                if (!apiService.isAvailable()) {
                    LogUtil.e(Constant.TAG, "⚠️ Payment API service not available");
                    handleAuthorizationError(new Exception("Payment API service not available"));
                    return;
                }
                
                // Request authorization asynchronously
                runOnUiThread(() -> updateStatus("Authorizing transaction...", 92));
                
                apiService.authorizeTransaction(authRequest, new com.neo.neopayplus.api.PaymentApiService.AuthorizationCallback() {
                    @Override
                    public void onAuthorizationComplete(com.neo.neopayplus.api.PaymentApiService.AuthorizationResponse response) {
                        handleAuthorizationResponse(response);
                    }
                    
                    @Override
                    public void onAuthorizationError(Throwable error) {
                        handleAuthorizationError(error);
                    }
                });
                
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error in online processing: " + e.getMessage());
                e.printStackTrace();
                
                // Notify EMV kernel of failed online processing
                try {
                    String[] tags = {};
                    String[] values = {};
                    byte[] out = new byte[1024];
                    int len = mEMVOptV2.importOnlineProcStatus(1, tags, values, out);
                    if (len >= 0) {
                        LogUtil.e(Constant.TAG, "Notified EMV kernel of online processing failure");
                    } else {
                        LogUtil.e(Constant.TAG, "Failed to notify EMV kernel of online processing failure, code: " + len);
                    }
                } catch (Exception ex) {
                    LogUtil.e(Constant.TAG, "Error notifying EMV kernel of online processing failure: " + ex.getMessage());
                }
            }
        }

        @Override
        public void onCardDataExchangeComplete() throws RemoteException {
            LogUtil.e(Constant.TAG, "onCardDataExchangeComplete");
            runOnUiThread(() -> updateStatus("Card data exchange complete...", 98));
        }

        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            LogUtil.e(Constant.TAG, "=== TRANSACTION RESULT ===");
            LogUtil.e(Constant.TAG, "onTransResult code:" + code + " desc:" + desc);
            LogUtil.e(Constant.TAG, "💳 Card: " + (mCardNo != null ? maskCardNumber(mCardNo) : "Unknown"));
            LogUtil.e(Constant.TAG, "💰 Amount: " + mAmount + " " + com.neo.neopayplus.config.PaymentConfig.CURRENCY_NAME);
            
            // Diagnostic: Log which AID (tag 84) and CAPK index (tag 8F) were selected by EMV kernel
            // This helps diagnose "L2 candidate list empty" errors
            // The EMV kernel automatically selects based on card's AID and CAPK index
            try {
                String[] diagnosticTags = {"84", "8F"}; // AID (tag 84) and CAPK index (tag 8F)
                byte[] outData = new byte[512];
                int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, diagnosticTags, outData);
                if (len > 0) {
                    // Extract TLV data - tag 84 (AID) and tag 8F (CAPK index)
                    String tlvHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(java.util.Arrays.copyOf(outData, len));
                    LogUtil.e(Constant.TAG, "=== EMV KERNEL SELECTION DIAGNOSTIC ===");
                    LogUtil.e(Constant.TAG, "Selected AID (tag 84) and CAPK index (tag 8F) TLV: " + 
                              (tlvHex.length() > 100 ? tlvHex.substring(0, 100) + "..." : tlvHex));
                    LogUtil.e(Constant.TAG, "Note: Tag 84 = card's AID (EMV kernel matched this with terminal AIDs)");
                    LogUtil.e(Constant.TAG, "Note: Tag 8F = CAPK index (EMV kernel looked up CAPK by RID + Index)");
                    if (code == -4125) {
                        LogUtil.e(Constant.TAG, "⚠️ L2 candidate list empty - Tag 84 may be missing (AID matching failed)");
                    }
                } else {
                    LogUtil.e(Constant.TAG, "⚠️ Could not retrieve AID/CAPK selection diagnostic (no TLV data)");
                    if (code == -4125) {
                        LogUtil.e(Constant.TAG, "⚠️ This confirms AID matching failed - tag 84 is missing");
                    }
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "⚠️ Error retrieving AID/CAPK selection diagnostic: " + e.getMessage());
            }
            
            // Get card number if not available
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            
            // Mark EMV process as finished
            mIsEmvProcessRunning = false;
            
            // Cancel timeout since transaction completed
            cancelEmvTimeout();
            
            // Extract EMV TLV data if transaction successful or offline denial (code 2)
            if (code == 0 || code == 2) {
                try {
                    // Extract comprehensive EMV TLV data
                    mEmvTlvData = extractEmvTlvData();
                    LogUtil.e(Constant.TAG, "Extracted EMV TLV data: " + mEmvTlvData);
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "Error extracting EMV TLV data: " + e.getMessage());
                    mEmvTlvData = "Error extracting EMV data";
                }
            }
            
            // Run UI updates on main thread
            runOnUiThread(() -> {
                if (code == 0) {
                    // Transaction successful
                    LogUtil.e(Constant.TAG, "✅ TRANSACTION SUCCESSFUL");
                    LogUtil.e(Constant.TAG, "🎉 EMV transaction completed successfully");
                    
                    // Reset per-transaction flags
                    pinEnteredThisTxn = false;
                    fallbackUsed = false;
                    
                    updateStatus("Transaction completed!", 100);
                    completeProcessing();
                } else {
                    // Transaction failed
                    LogUtil.e(Constant.TAG, "❌ TRANSACTION FAILED");
                    LogUtil.e(Constant.TAG, "🚫 Error code: " + code + " - " + desc);
                    
                    String errorMessage = "Transaction failed";
                    if (code == -50009) {
                        errorMessage = "EMV process not finished (50009) - Please wait and try again";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Wait for current EMV process to complete");
                        // Clean up the EMV process
                        try {
                            mEMVOptV2.importAppSelect(-1);
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "Error cleaning up EMV process: " + e.getMessage());
                        }
                    } else if (code == -50019) {
                        errorMessage = "Transaction data invalid (50019) - Check amount format and parameters";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Check transaction data bundle parameters");
                    } else if (code == -50017) {
                        errorMessage = "Card check failed (50017) - Please try again";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Check card contact or try different card");
                    } else if (code == -50020) {
                        errorMessage = "PIN entry cancelled (50020)";
                        LogUtil.e(Constant.TAG, "🔧 Fix: User cancelled PIN entry");
                    } else if (code == -50021) {
                        errorMessage = "PIN entry error (50021)";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Check PIN pad configuration or user cancelled PIN");
                    } else if (code == -50025) {
                        errorMessage = "Final select timeout (50025) - Please try again";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Check AID/CAPK configuration or card contact");
                    } else if (code == -4125) {
                        errorMessage = "L2 candidate list is empty (4125) - No matching AIDs found";
                        LogUtil.e(Constant.TAG, "🔧 Fix: Check AID configuration for contactless cards");
                        LogUtil.e(Constant.TAG, "⚠️ This error means the EMV kernel cannot find any matching AIDs for the card");
                        LogUtil.e(Constant.TAG, "⚠️ Ensure AIDs are loaded correctly and terminal parameters are set");
                    } else if (code == 2) {
                        // Offline denial - transaction requires online authorization
                        LogUtil.e(Constant.TAG, "⚠️ OFFLINE DENIAL DETECTED");
                        LogUtil.e(Constant.TAG, "🔍 Card requires online authorization - backend authorization required");
                        LogUtil.e(Constant.TAG, "=== DEBUG: Offline Denial Analysis ===");
                        LogUtil.e(Constant.TAG, "Card Type: " + mCardType + " (NFC=" + AidlConstantsV2.CardType.NFC.getValue() + ")");
                        // Security: Never log full PAN in production
                        if (com.neo.neopayplus.BuildConfig.DEBUG && mCardNo != null) {
                            LogUtil.e(Constant.TAG, "Card Number (DEBUG): " + maskCardNumber(mCardNo));
                        } else {
                            LogUtil.e(Constant.TAG, "Card Number: " + (mCardNo != null ? "Available (masked)" : "Not available"));
                        }
                        LogUtil.e(Constant.TAG, "EMV TLV Data: " + (mEmvTlvData != null ? mEmvTlvData : "Not available"));
                        
                        // Extract comprehensive EMV tags for offline denial analysis
                        extractPinAndCvmData();
                        
                        // Debug AID used in transaction
                        try {
                            String[] aidTags = {"84"};
                            byte[] aidOutData = new byte[256];
                            int aidLen = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, aidTags, aidOutData);
                            if (aidLen > 0) {
                                byte[] aidData = new byte[aidLen];
                                System.arraycopy(aidOutData, 0, aidData, 0, aidLen);
                                String aidUsed = ByteUtil.bytes2HexStr(aidData);
                                LogUtil.e(Constant.TAG, "AID used in transaction: " + aidUsed);
                            }
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "Error getting AID from transaction: " + e.getMessage());
                        }
                        
                        if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                            errorMessage = "Transaction requires online authorization - Please process through backend";
                            LogUtil.e(Constant.TAG, "⚠️ SECURITY: Offline denial - Backend authorization required");
                            LogUtil.e(Constant.TAG, "⚠️ Do not treat offline denial as success - requires backend validation");
                        } else {
                            errorMessage = "Transaction requires online authorization";
                            LogUtil.e(Constant.TAG, "⚠️ SECURITY: Offline denial - Backend authorization required");
                        }
                        
                        LogUtil.e(Constant.TAG, "=== END DEBUG: Offline Denial Analysis ===");
                        
                        updateStatus("Transaction requires online authorization", 0);
                        // EMV TLV data should already be extracted above
                        completeProcessing();
                        return; // Exit early to complete processing
                    } else {
                        LogUtil.e(Constant.TAG, "🔧 Unknown error - check EMV configuration");
                        errorMessage = "Transaction failed: " + desc + " (Code: " + code + ")";
                    }
                    
                    updateStatus(errorMessage, 0);
                    showToast(errorMessage);
                }
            });
        }

        @Override
        public void onConfirmationCodeVerified() throws RemoteException {
            LogUtil.e(Constant.TAG, "onConfirmationCodeVerified");
            runOnUiThread(() -> updateStatus("Confirmation code verified...", 96));
        }

        @Override
        public void onRequestDataExchange(String cardNo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestDataExchange:" + cardNo);
            runOnUiThread(() -> updateStatus("Data exchange...", 97));
        }

        @Override
        public void onTermRiskManagement() throws RemoteException {
            LogUtil.e(Constant.TAG, "onTermRiskManagement");
            runOnUiThread(() -> updateStatus("Risk management...", 94));
        }

        @Override
        public void onPreFirstGenAC() throws RemoteException {
            LogUtil.e(Constant.TAG, "onPreFirstGenAC");
            runOnUiThread(() -> updateStatus("Pre-first generation AC...", 91));
        }

        @Override
        public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
            LogUtil.e(Constant.TAG, "onDataStorageProc");
            // Handle data storage processing if needed
            String[] tags = new String[0];
            String[] values = new String[0];
            try {
                mEMVOptV2.importDataStorage(tags, values);
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error importing data storage: " + e.getMessage());
            }
        }
    };
    
    /**
     * Handle authorization response from backend API
     */
    private void handleAuthorizationResponse(com.neo.neopayplus.api.PaymentApiService.AuthorizationResponse response) {
        LogUtil.e(Constant.TAG, "=== AUTHORIZATION RESPONSE ===");
        LogUtil.e(Constant.TAG, response.toString());
        
        try {
            byte[] out = new byte[1024];
            int status; // 0 = approve, 1 = decline
            
            // Check for wrong PIN response codes (55, 63, N)
            String responseCode = response.responseCode != null ? response.responseCode : "";
            boolean isWrongPin = "55".equals(responseCode) || "63".equals(responseCode) || "N".equals(responseCode);
            
            if (response.approved && !isWrongPin) {
                // Success: Transaction approved
                status = 0; // Approve
                LogUtil.e(Constant.TAG, "✅ Transaction APPROVED by backend");
                LogUtil.e(Constant.TAG, "  Auth Code: " + response.authCode);
                LogUtil.e(Constant.TAG, "  RRN: " + response.rrn);
                LogUtil.e(Constant.TAG, "  Response Code: " + response.responseCode);
                
                // Reset PIN attempts for next transaction
                mPinAttemptsLeft.set(MAX_PIN_ATTEMPTS);
                
                // Clear stored PIN block for security
                if (mOnlinePinBlock != null) {
                    java.util.Arrays.fill(mOnlinePinBlock, (byte) 0);
                    mOnlinePinBlock = null;
                }
                mKsn = null;
                
                // Store RRN and auth code for journal
                lastRrn = response.rrn != null ? response.rrn : "";
                String authCode = response.authCode != null ? response.authCode : "";
                
                // Save journal entry (APPROVED)
                saveJournal(lastRrn, "00", authCode, currentEntryMode, currentAid, currentTsi, currentTvr);
                
                // Import online processing status to EMV kernel
                String[] tags = response.responseTags != null ? response.responseTags : new String[0];
                String[] values = response.responseValues != null ? response.responseValues : new String[0];
                
                int len = mEMVOptV2.importOnlineProcStatus(status, tags, values, out);
                if (len >= 0) {
                    LogUtil.e(Constant.TAG, "EMV kernel notified of authorization result: APPROVED");
                } else {
                    LogUtil.e(Constant.TAG, "Error notifying EMV kernel, code: " + len);
                }
            } else if (isWrongPin && mCurrentPinType == 1) {
                // Wrong PIN: Handle retry logic for online PIN
                LogUtil.e(Constant.TAG, "❌ WRONG PIN RESPONSE from backend");
                LogUtil.e(Constant.TAG, "  Response Code: " + responseCode + " (Wrong PIN)");
                
                int attemptsLeft = mPinAttemptsLeft.decrementAndGet();
                LogUtil.e(Constant.TAG, "  Attempts remaining: " + attemptsLeft);
                
                if (attemptsLeft > 0) {
                    // Retry: Clear stored PIN block and re-prompt
                    if (mOnlinePinBlock != null) {
                        java.util.Arrays.fill(mOnlinePinBlock, (byte) 0);
                        mOnlinePinBlock = null;
                    }
                    mKsn = null;
                    
                    // Show retry message and re-prompt for PIN
                    runOnUiThread(() -> {
                        updateStatus("Wrong PIN. Attempts left: " + attemptsLeft, 90);
                        showToast("Incorrect PIN. " + attemptsLeft + " attempt(s) remaining");
                        
                        // Note: We cannot directly call onRequestShowPinPad - it's a callback from the EMV kernel
                        // The kernel will automatically re-prompt for PIN if we call importOnlineProcStatus with decline
                        // For proper retry, we would need to restart the transaction flow
                        // For now, decline and let the transaction fail - user can retry the entire transaction
                        LogUtil.e(Constant.TAG, "⚠️ Note: PIN retry requires restarting transaction");
                        // Decline transaction - user will need to retry from beginning
                        try {
                            String[] tags = {};
                            String[] values = {};
                            byte[] retryOut = new byte[1024];
                            mEMVOptV2.importOnlineProcStatus(1, tags, values, retryOut); // 1 = decline
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "Error declining transaction: " + e.getMessage());
                        }
                    });
                } else {
                    // No attempts left: Fallback to signature or decline
                    handleWrongPinExhausted();
                }
            } else if ("97".equals(responseCode)) {
                // Key sync required: Rotate keys
                LogUtil.e(Constant.TAG, "⚠️ Key sync required (response code 97)");
                LogUtil.e(Constant.TAG, "  Initiating key rotation...");
                runOnUiThread(() -> {
                    updateStatus("Key rotation required. Rotating keys...", 90);
                    showToast("Key rotation required");
                });
                
                // Rotate keys asynchronously
                handleKeyRotation();
            } else {
                // Generic decline
                status = 1; // Decline
                LogUtil.e(Constant.TAG, "❌ Transaction DECLINED by backend");
                LogUtil.e(Constant.TAG, "  Response Code: " + responseCode);
                LogUtil.e(Constant.TAG, "  Message: " + response.message);
                
                // Store RRN for journal
                lastRrn = response.rrn != null ? response.rrn : "";
                
                // Save journal entry (DECLINED)
                saveJournal(lastRrn, responseCode, "", currentEntryMode, currentAid, currentTsi, currentTvr);
                
                // Import online processing status to EMV kernel
                String[] tags = response.responseTags != null ? response.responseTags : new String[0];
                String[] values = response.responseValues != null ? response.responseValues : new String[0];
                
                int len = mEMVOptV2.importOnlineProcStatus(status, tags, values, out);
                if (len >= 0) {
                    LogUtil.e(Constant.TAG, "EMV kernel notified of authorization result: DECLINED");
                } else {
                    LogUtil.e(Constant.TAG, "Error notifying EMV kernel, code: " + len);
                }
            }
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error handling authorization response: " + e.getMessage());
            e.printStackTrace();
            // Fallback: decline transaction on error
            try {
                String[] tags = {};
                String[] values = {};
                byte[] out = new byte[1024];
                mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // Decline on error
            } catch (Exception ex) {
                LogUtil.e(Constant.TAG, "Error declining transaction: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Handle authorization error (network, timeout, etc.)
     */
    private void handleAuthorizationError(Throwable error) {
        LogUtil.e(Constant.TAG, "❌ AUTHORIZATION ERROR: " + error.getMessage());
        error.printStackTrace();
        
        runOnUiThread(() -> updateStatus("Authorization error: " + error.getMessage(), 0));
        
        // Enqueue reversal for host timeout/error
        enqueueReversal(lastRrn, "HOST_TIMEOUT");
        
        // Save journal entry (host unavailable - RC 91)
        saveJournal(lastRrn, "91", "", currentEntryMode, currentAid, currentTsi, currentTvr);
        
        try {
            // Decline transaction on error (host unavailable)
            // Note: -2 might not be recognized, use 1 (decline) for compatibility
            String[] tags = {};
            String[] values = {};
            byte[] out = new byte[1024];
            mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // 1 = decline
            LogUtil.e(Constant.TAG, "EMV kernel notified of authorization error (declined)");
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error notifying EMV kernel: " + e.getMessage());
        }
    }
    
    /**
     * Fetch KSN (Key Serial Number) for online PIN encryption
     * Required for backend to decrypt the PIN block
     */
    private void fetchKsnForOnlinePin() {
        if (mSecurityOptV2 == null) {
            LogUtil.e(Constant.TAG, "⚠️ SecurityOptV2 not available - cannot fetch KSN");
            return;
        }
        
        try {
            // DUKPT key index (typically 1100-1199 for online PIN)
            int dukptKeyIndex = 1100;
            byte[] ksn = new byte[10];
            int result = mSecurityOptV2.dukptCurrentKSN(dukptKeyIndex, ksn);
            
            if (result == 0) {
                mKsn = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(ksn);
                LogUtil.e(Constant.TAG, "✓ KSN fetched successfully: " + mKsn);
            } else {
                LogUtil.e(Constant.TAG, "⚠️ Failed to fetch KSN via dukptCurrentKSN(), result: " + result);
                // Some SDKs may return KSN in pinBlock callback or via other API
                // For now, set to null - backend may still work if KSN is provided via other means
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "⚠️ Exception fetching KSN: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get KSN for active slot (DUKPT or session TPK)
     * Helper method for journal persistence
     */
    private String getKsnForActiveSlot() {
        try {
            if (mSecurityOptV2 == null) {
                return "";
            }
            
            // If using DUKPT, get current KSN from active slot
            int dukptKeyIndex = 1100; // DUKPT key index
            byte[] ksn = new byte[10];
            int result = mSecurityOptV2.dukptCurrentKSN(dukptKeyIndex, ksn);
            
            if (result == 0) {
                return ByteUtil.bytes2HexStr(ksn);
            }
            
            // If using session TPK (KeyManagerPOS), KSN may not be applicable
            // Return empty string if not using DUKPT
            return "";
            
        } catch (Throwable t) {
            LogUtil.e(Constant.TAG, "Error getting KSN: " + t.getMessage());
            return "";
        }
    }
    
    /**
     * Save transaction to journal
     */
    private void saveJournal(String rrn, String respCode, String authCode, 
                             String entryMode, String aid, String tsi, String tvr) {
        try {
            TxnDb db = new TxnDb(this);
            Map<String, Object> rec = new HashMap<>();
            rec.put("stan", currentStan);
            rec.put("rrn", rrn != null ? rrn : "");
            rec.put("amount_minor", Integer.parseInt(mAmount));
            rec.put("currency", com.neo.neopayplus.config.PaymentConfig.getCurrencyCode());
            rec.put("pan_masked", lastPanMasked);
            rec.put("ksn", getKsnForActiveSlot());
            rec.put("entry_mode", entryMode != null ? entryMode : "");
            rec.put("aid", aid != null ? aid : "");
            rec.put("tsi", tsi != null ? tsi : "");
            rec.put("tvr", tvr != null ? tvr : "");
            rec.put("resp_code", respCode != null ? respCode : "");
            rec.put("auth_code", authCode != null ? authCode : "");
            rec.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new java.util.Date()));
            
            db.insertJournal(rec);
            LogUtil.e(Constant.TAG, "✓ Journal entry saved - STAN: " + currentStan + ", RRN: " + rrn);
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error saving journal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Enqueue reversal for later retry
     */
    private void enqueueReversal(String rrn, String reason) {
        try {
            TxnDb db = new TxnDb(this);
            Map<String, Object> r = new HashMap<>();
            r.put("stan", currentStan);
            r.put("rrn", rrn != null ? rrn : "");
            r.put("amount_minor", Integer.parseInt(mAmount));
            r.put("currency", com.neo.neopayplus.config.PaymentConfig.getCurrencyCode());
            r.put("reason", reason);
            r.put("created_at", TimeSync.nowIso());
            
            db.enqueueReversal(r);
            LogUtil.e(Constant.TAG, "✓ Reversal enqueued - STAN: " + currentStan + ", Reason: " + reason);
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error enqueueing reversal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Convert hex string to ASCII
     */
    private String hexToAscii(String hex) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hex.length(); i += 2) {
                String str = hex.substring(i, i + 2);
                sb.append((char) Integer.parseInt(str, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            return hex; // Return hex if conversion fails
        }
    }
    
    /**
     * Handle key rotation when response code 97 (KEY_SYNC_REQUIRED) is received
     */
    private void handleKeyRotation() {
        LogUtil.e(Constant.TAG, "=== KEY ROTATION FLOW ===");
        
        // Get terminal ID from config (dynamic)
        String terminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId();
        if (terminalId == null || terminalId.isEmpty()) {
            LogUtil.e(Constant.TAG, "❌ Terminal ID not configured - cannot rotate keys");
            runOnUiThread(() -> {
                updateStatus("Key rotation failed: Terminal ID not configured", 0);
                showToast("Key rotation failed");
            });
            return;
        }
        
        // Request key rotation
        com.neo.neopayplus.api.PaymentApiService apiService = 
            com.neo.neopayplus.api.PaymentApiFactory.getInstance();
        
        if (!apiService.isAvailable()) {
            LogUtil.e(Constant.TAG, "❌ Payment API service not available - cannot rotate keys");
            runOnUiThread(() -> {
                updateStatus("Key rotation failed: API service unavailable", 0);
                showToast("Key rotation failed");
            });
            return;
        }
        
        com.neo.neopayplus.api.PaymentApiService.KeyRotationRequest rotationRequest = 
            new com.neo.neopayplus.api.PaymentApiService.KeyRotationRequest(terminalId, "DUKPT");
        
        apiService.rotateKeys(rotationRequest, new com.neo.neopayplus.api.PaymentApiService.KeyRotationCallback() {
            @Override
            public void onKeyRotationComplete(com.neo.neopayplus.api.PaymentApiService.KeyRotationResponse response) {
                LogUtil.e(Constant.TAG, "✓ Key rotation successful");
                LogUtil.e(Constant.TAG, "  Key Index: " + response.keyIndex);
                LogUtil.e(Constant.TAG, "  IPEK: " + (response.ipek != null ? response.ipek.substring(0, 8) + "****" : "null"));
                LogUtil.e(Constant.TAG, "  KSN: " + response.ksn);
                LogUtil.e(Constant.TAG, "  Effective Date: " + response.effectiveDate);
                
                // Store new keys securely using SunmiPayLibKeyManager
                storeNewKeys(response);
            }
            
            @Override
            public void onKeyRotationError(Throwable error) {
                LogUtil.e(Constant.TAG, "❌ Key rotation failed: " + error.getMessage());
                error.printStackTrace();
                runOnUiThread(() -> {
                    updateStatus("Key rotation failed: " + error.getMessage(), 0);
                    showToast("Key rotation failed");
                    
                    // Decline transaction since key rotation failed
                    try {
                        String[] tags = {};
                        String[] values = {};
                        byte[] out = new byte[1024];
                        mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // Decline
                    } catch (Exception e) {
                        LogUtil.e(Constant.TAG, "Error declining transaction: " + e.getMessage());
                    }
                });
            }
        });
    }
    
    /**
     * Store new keys securely using SunmiPayLibKeyManager
     */
    private void storeNewKeys(com.neo.neopayplus.api.PaymentApiService.KeyRotationResponse response) {
        LogUtil.e(Constant.TAG, "=== STORING NEW KEYS ===");
        
        try {
            // Convert hex IPEK to byte array
            byte[] ipekBytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(response.ipek);
            
            // Convert hex KSN to byte array
            byte[] ksnBytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(response.ksn);
            
            // Store DUKPT key using SunmiPayLibKeyManager
            // Note: Key index should match the one from rotation response
            int keyIndex = response.keyIndex; // Use the key index from backend
            
            if (mSecurityOptV2 == null) {
                LogUtil.e(Constant.TAG, "❌ SecurityOptV2 not available - cannot store keys");
                runOnUiThread(() -> {
                    updateStatus("Key rotation failed: Security module unavailable", 0);
                    showToast("Key rotation failed");
                });
                return;
            }
            
            // Save DUKPT key (IPEK)
            int result = mSecurityOptV2.saveKeyDukpt(
                com.sunmi.payservice.AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK,
                ipekBytes,
                null, // checkValue (optional)
                ksnBytes,
                com.sunmi.payservice.AidlConstantsV2.Security.KEY_ALG_TYPE_3DES,
                keyIndex
            );
            
            if (result == 0) {
                LogUtil.e(Constant.TAG, "✓ New DUKPT keys stored successfully");
                LogUtil.e(Constant.TAG, "  Key Index: " + keyIndex);
                LogUtil.e(Constant.TAG, "  KSN: " + response.ksn);
                
                runOnUiThread(() -> {
                    updateStatus("Keys rotated successfully. Retry transaction.", 90);
                    showToast("Keys rotated successfully");
                    
                    // Note: After key rotation, the transaction should be retried
                    // The EMV kernel may need to be informed or the transaction restarted
                    // For now, we decline the current transaction and user should retry
                    try {
                        String[] tags = {};
                        String[] values = {};
                        byte[] out = new byte[1024];
                        mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // Decline current transaction
                        LogUtil.e(Constant.TAG, "⚠️ Current transaction declined - user should retry with new keys");
                    } catch (Exception e) {
                        LogUtil.e(Constant.TAG, "Error declining transaction: " + e.getMessage());
                    }
                });
            } else {
                LogUtil.e(Constant.TAG, "❌ Failed to store new keys, result code: " + result);
                runOnUiThread(() -> {
                    updateStatus("Key storage failed (code: " + result + ")", 0);
                    showToast("Key storage failed");
                });
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error storing new keys: " + e.getMessage());
            e.printStackTrace();
            runOnUiThread(() -> {
                updateStatus("Key storage failed: " + e.getMessage(), 0);
                showToast("Key storage failed");
            });
        }
    }
    
    /**
     * Handle wrong PIN exhausted (no attempts left)
     * Offer signature fallback if allowed, otherwise decline
     */
    private void handleWrongPinExhausted() {
        LogUtil.e(Constant.TAG, "❌ PIN attempts exhausted");
        
        // Clear stored PIN block
        if (mOnlinePinBlock != null) {
            java.util.Arrays.fill(mOnlinePinBlock, (byte) 0);
            mOnlinePinBlock = null;
        }
        mKsn = null;
        
        if (ALLOW_PIN_FALLBACK_TO_SIGNATURE) {
            LogUtil.e(Constant.TAG, "⚠️ Offering signature fallback");
            runOnUiThread(() -> {
                updateStatus("PIN attempts exhausted. Signature required?", 90);
                showToast("PIN attempts exhausted. Please provide signature");
                // TODO: Implement signature capture UI
                // For now, decline transaction
                try {
                    String[] tags = {};
                    String[] values = {};
                    byte[] out = new byte[1024];
                    mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // 1 = decline
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "Error declining transaction: " + e.getMessage());
                }
            });
        } else {
            // Decline transaction
            runOnUiThread(() -> {
                updateStatus("PIN attempts exhausted. Transaction declined", 0);
                showToast("PIN attempts exhausted");
            });
            try {
                String[] tags = {};
                String[] values = {};
                byte[] out = new byte[1024];
                mEMVOptV2.importOnlineProcStatus(1, tags, values, out); // 1 = decline
                LogUtil.e(Constant.TAG, "✅ EMV kernel notified: Transaction declined (PIN exhausted)");
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error declining transaction: " + e.getMessage());
            }
        }
        
        // Reset attempts for next transaction
        mPinAttemptsLeft.set(MAX_PIN_ATTEMPTS);
    }
    
    private void completeProcessing() {
        // Mark EMV process as finished
        mIsEmvProcessRunning = false;
        
        // Cancel timeout
        cancelEmvTimeout();
        
        // Navigate to data view screen with real EMV data
        Intent intent = new Intent(this, DataViewActivity.class);
        intent.putExtra("amount", mAmount); // Piasters for SDK
        intent.putExtra("amountDisplay", mAmountDisplay); // Pounds for display
        intent.putExtra("pin", mPin);
        intent.putExtra("cardNo", mCardNo);
        intent.putExtra("emvTlvData", mEmvTlvData);
        intent.putExtra("status", "success");
        intent.putExtra("transactionId", "TXN" + System.currentTimeMillis());
        intent.putExtra("backendRequestJson", mBackendRequestJson); // Exact JSON sent to backend
        startActivity(intent);
        finish();
    }
    
    /**
     * Build exact JSON request body that will be sent to backend API
     * This matches the format in PaymentApiServiceImpl.buildRequestJson()
     */
    private String buildBackendRequestJson(com.neo.neopayplus.api.PaymentApiService.AuthorizationRequest request) {
        try {
            com.google.gson.JsonObject json = new com.google.gson.JsonObject();
            
            // Backend API spec format: terminal_id, merchant_id, amount, currency, etc.
            json.addProperty("terminal_id", com.neo.neopayplus.config.PaymentConfig.getTerminalId());
            json.addProperty("merchant_id", com.neo.neopayplus.config.PaymentConfig.getMerchantId());
            
            // Convert amount from piasters (smallest unit) to main currency unit
            try {
                long amount = Long.parseLong(request.amount);
                double amountInMainUnit = amount / 100.0; // Convert to main unit
                json.addProperty("amount", amountInMainUnit);
            } catch (Exception e) {
                json.addProperty("amount", 0.0);
            }
            json.addProperty("currency", com.neo.neopayplus.config.PaymentConfig.CURRENCY_NAME); // "EGP"
            json.addProperty("transaction_type", "SALE");
            
            // Mask PAN for security (real data from terminal)
            String panMasked = request.pan != null ? maskCardNumber(request.pan) : "";
            json.addProperty("pan_masked", panMasked);
            
            // EMV data structure (from field55 - real data from terminal)
            if (request.field55 != null && !request.field55.isEmpty()) {
                com.google.gson.JsonObject emvData = new com.google.gson.JsonObject();
                json.addProperty("track2_encrypted", ""); // Would be encrypted track2 if available
                json.addProperty("emv_data_raw", request.field55);
                
                // Parse common EMV tags from field55 (for structured data)
                try {
                    parseEmvTagsFromField55(request.field55, emvData);
                    json.add("emv_data", emvData);
                } catch (Exception e) {
                    json.add("emv_data", emvData);
                }
            }
            
            // PIN block and KSN (real data from terminal - required for online PIN with DUKPT)
            // Only include PIN block if CVM indicates online PIN (01/02), not offline PIN (42)
            // Note: request.pinBlock is hex string converted to bytes (see ProcessingActivity line 1241)
            if (request.pinBlock != null && request.pinBlock.length > 0) {
                // Convert bytes back to hex string (request.pinBlock contains hex string as bytes)
                String pinBlockHex = new String(request.pinBlock);
                json.addProperty("pin_block", pinBlockHex);
                LogUtil.e(Constant.TAG, "✓ PIN block included in request (online PIN)");
            } else {
                LogUtil.e(Constant.TAG, "ℹ️ No PIN block in request - CVM indicates offline PIN or no PIN required");
            }
            if (request.ksn != null && !request.ksn.isEmpty()) {
                json.addProperty("ksn", request.ksn);
            }
            
            // Include CVM result code for backend reference
            if (mCvmResultCode != null) {
                json.addProperty("cvm_result", mCvmResultCode);
                String cvmDescription = getCvmDescription(mCvmResultCode);
                json.addProperty("cvm_description", cvmDescription);
            }
            
            // Datetime in ISO8601 format (real transaction date/time)
            String datetime = formatDateTime(request.date, request.time);
            json.addProperty("datetime", datetime);
            
            // ISO8583 Fields (simulated for backend integration)
            com.google.gson.JsonObject isoFields = buildIsoFields(request);
            json.add("iso_fields", isoFields);
            
            // Convert to pretty-printed JSON string
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(json);
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error building backend request JSON: " + e.getMessage());
            return "{\"error\": \"Failed to build request JSON: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * Extract CVM Result code (9F34) from EMV kernel
     * Returns the CVM code to determine if PIN should be sent to backend
     * - "00" = No CVM required
     * - "01" or "02" = Online PIN (send PIN block to backend)
     * - "42" = Offline PIN (card verified PIN, do NOT send PIN block)
     * - "03" or "5E" = CDCVM (no PIN block)
     */
    private String extractCvmResultCode() {
        try {
            byte[] outData = new byte[256];
            int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "9F34", outData);
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
                    LogUtil.e(Constant.TAG, "Extracted CVM Result code: " + cvmCode + " from hex value: " + actualCvmValue);
                    return cvmCode;
                }
            } else {
                LogUtil.e(Constant.TAG, "⚠️ Could not extract CVM Result (9F34) - tag not available");
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error extracting CVM Result code: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Parse EMV tags from field55 (simplified version)
     */
    private void parseEmvTagsFromField55(String field55, com.google.gson.JsonObject emvData) {
        // This is a simplified version - actual TLV parsing would be more robust
        // The backend will handle full TLV parsing
    }
    
    /**
     * Format datetime from YYMMDD and HHMMSS to ISO8601
     */
    private String formatDateTime(String date, String time) {
        try {
            if (date != null && date.length() == 6 && time != null && time.length() == 6) {
                String year = "20" + date.substring(0, 2);
                String month = date.substring(2, 4);
                String day = date.substring(4, 6);
                String hour = time.substring(0, 2);
                String minute = time.substring(2, 4);
                String second = time.substring(4, 6);
                return String.format("%s-%s-%sT%s:%s:%sZ", year, month, day, hour, minute, second);
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error formatting datetime: " + e.getMessage());
        }
        return java.time.Instant.now().toString();
    }
    
    /**
     * Mask card number for display
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            return "****";
        }
        // Format: "400000******7899"
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(cardNumber.length() - 4);
    }
    
    /**
     * Build ISO8583 iso_fields object
     * Includes standard ISO8583 data elements plus DE55 (ICC Data) from EMV Field 55
     * 
     * @param request Authorization request with transaction data
     * @return JsonObject containing ISO8583 fields
     */
    private com.google.gson.JsonObject buildIsoFields(com.neo.neopayplus.api.PaymentApiService.AuthorizationRequest request) {
        com.google.gson.JsonObject isoFields = new com.google.gson.JsonObject();
        
        // DE2: PAN (Primary Account Number) - masked
        if (request.pan != null && !request.pan.isEmpty()) {
            String panMasked = maskCardNumber(request.pan);
            isoFields.addProperty("2", panMasked);
        }
        
        // DE3: Processing Code (000000 = Purchase)
        isoFields.addProperty("3", "000000");
        
        // DE4: Amount, Authorized (in minor currency units)
        if (request.amount != null && !request.amount.isEmpty()) {
            isoFields.addProperty("4", request.amount);
        }
        
        // DE11: STAN (Systems Trace Audit Number) - generated from timestamp
        String stan = generateStan(request.date, request.time);
        isoFields.addProperty("11", stan);
        
        // DE22: POS Entry Mode
        // 051 = Chip + PIN
        // 021 = Chip (no PIN)
        // 071 = Contactless (NFC) + PIN
        // 072 = Contactless (NFC) - no PIN
        String posEntryMode = determinePosEntryMode(request);
        isoFields.addProperty("22", posEntryMode);
        
        // DE49: Currency Code, Transaction (e.g., "818" for EGP)
        if (request.currencyCode != null && !request.currencyCode.isEmpty()) {
            isoFields.addProperty("49", request.currencyCode);
        } else {
            isoFields.addProperty("49", "818"); // Default EGP
        }
        
        // DE55: ICC Data (EMV Field 55 - Integrated Circuit Card Data)
        // This is the most critical field for EMV transactions
        // Contains all EMV tags (ARQC, CVM, AIP, etc.)
        if (request.field55 != null && !request.field55.isEmpty()) {
            isoFields.addProperty("55", request.field55);
            LogUtil.e(Constant.TAG, "✓ DE55 (ICC Data) included in ISO fields - length: " + request.field55.length() + " hex chars");
        } else {
            LogUtil.e(Constant.TAG, "⚠️ No Field 55 available - DE55 will be empty");
        }
        
        // Transaction Type Code (derived from MTI in full ISO8583)
        // 0100 = Authorization Request
        // 0200 = Financial Transaction Request  
        isoFields.addProperty("mti", "0200"); // Financial Transaction
        
        return isoFields;
    }
    
    /**
     * Generate STAN (Systems Trace Audit Number) from date/time
     * Format: 6 digits (000000-999999), typically incremented per transaction
     * 
     * @param date Transaction date (YYMMDD)
     * @param time Transaction time (HHMMSS)
     * @return STAN as 6-digit string
     */
    private String generateStan(String date, String time) {
        try {
            // Use seconds from time + milliseconds to create unique STAN
            if (time != null && time.length() >= 6) {
                String seconds = time.substring(4, 6); // Last 2 digits (SS)
                // Add milliseconds (0-9) to seconds for uniqueness
                int millis = (int) (System.currentTimeMillis() % 1000) / 100; // 0-9
                int stanValue = Integer.parseInt(seconds) * 10 + millis;
                // Ensure 6 digits by padding
                return String.format("%06d", stanValue % 1000000);
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error generating STAN: " + e.getMessage());
        }
        // Fallback: use last 6 digits of timestamp
        long timestamp = System.currentTimeMillis();
        return String.format("%06d", timestamp % 1000000);
    }
    
    /**
     * Determine POS Entry Mode (DE22) based on transaction context
     * 
     * @param request Authorization request
     * @return POS Entry Mode code (3 digits)
     */
    private String determinePosEntryMode(com.neo.neopayplus.api.PaymentApiService.AuthorizationRequest request) {
        // Default: Chip + PIN (051)
        // This should be determined from actual transaction flow
        // - Chip + PIN = 051
        // - Chip only (no PIN) = 021
        // - Contactless + PIN = 071
        // - Contactless (no PIN) = 072
        // - Signature = 050
        
        // For now, default to Chip + PIN (most common for EMV)
        // In production, this should come from EMV kernel or transaction context
        boolean hasPin = (request.pinBlock != null && request.pinBlock.length > 0);
        boolean isContactless = (mCardType == AidlConstantsV2.CardType.NFC.getValue());
        
        if (isContactless) {
            return hasPin ? "071" : "072"; // Contactless + PIN : Contactless only
        } else {
            return hasPin ? "051" : "021"; // Chip + PIN : Chip only
        }
    }
    
    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "";
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02X", b));
        }
        return hex.toString();
    }
    
    /**
     * Get human-readable description for CVM result code
     */
    private String getCvmDescription(String cvmCode) {
        if (cvmCode == null) return "Unknown";
        switch (cvmCode) {
            case "00": return "No CVM required";
            case "01": return "Online PIN required";
            case "02": return "Online PIN required";
            case "03": return "CDCVM performed";
            case "42": return "Offline PIN verified by card";
            case "5E": return "CDCVM (Apple Pay/Google Pay)";
            default: return "Unknown CVM code: " + cvmCode;
        }
    }
    
    private void startEmvTimeout() {
        // Cancel any existing timeout
        cancelEmvTimeout();
        
        // Create new timeout runnable
        mTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (mIsEmvProcessRunning) {
                    LogUtil.e(Constant.TAG, "EMV process timeout after " + EMV_TIMEOUT_MS + "ms");
                    mIsEmvProcessRunning = false;
                    
                    // Clean up EMV process
                    try {
                        mEMVOptV2.importAppSelect(-1);
                    } catch (Exception e) {
                        LogUtil.e(Constant.TAG, "Error cleaning up EMV process on timeout: " + e.getMessage());
                    }
                    
                    // Show timeout error
                    runOnUiThread(() -> {
                        updateStatus("Transaction timeout - Please try again", 0);
                        showToast("Transaction timeout - Please try again");
                    });
                }
            }
        };
        
        // Start timeout
        mTimeoutHandler.postDelayed(mTimeoutRunnable, EMV_TIMEOUT_MS);
    }
    
    private void cancelEmvTimeout() {
        if (mTimeoutRunnable != null) {
            mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
            mTimeoutRunnable = null;
        }
    }
    
    /**
     * Get card number from EMV TLV data
     */
    private String getCardNo() {
        LogUtil.e(Constant.TAG, "getCardNo");
        try {
            String[] tagList = {"57", "5A"};
            byte[] outData = new byte[256];
            int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData);
            if (len <= 0) {
                LogUtil.e(Constant.TAG, "getCardNo error,code:" + len);
                return "";
            }
            byte[] bytes = Arrays.copyOf(outData, len);
            Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(bytes);
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("57")).getValue())) {
                TLV tlv57 = tlvMap.get("57");
                CardInfo cardInfo = parseTrack2(tlv57.getValue());
                return cardInfo.cardNo;
            }
            if (!TextUtils.isEmpty(Objects.requireNonNull(tlvMap.get("5A")).getValue())) {
                return Objects.requireNonNull(tlvMap.get("5A")).getValue();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * Parse track2 data
     */
    public static CardInfo parseTrack2(String track2) {
        // Security: Never log full Track 2 data
        if (com.neo.neopayplus.BuildConfig.DEBUG && track2 != null && track2.length() > 8) {
            // Mask Track 2 - show first 4 and last 4 chars
            String maskedTrack2 = track2.substring(0, 4) + "****" + track2.substring(track2.length() - 4);
            LogUtil.e(Constant.TAG, "track2 (DEBUG, masked): " + maskedTrack2);
        } else {
            LogUtil.e(Constant.TAG, "track2: Available (not logged for security)");
        }
        String track_2 = stringFilter(track2);
        int index = track_2.indexOf("=");
        if (index == -1) {
            index = track_2.indexOf("D");
        }
        CardInfo cardInfo = new CardInfo();
        if (index == -1) {
            return cardInfo;
        }
        String cardNumber = "";
        if (track_2.length() > index) {
            cardNumber = track_2.substring(0, index);
        }
        String expiryDate = "";
        if (track_2.length() > index + 5) {
            expiryDate = track_2.substring(index + 1, index + 5);
        }
        String serviceCode = "";
        if (track_2.length() > index + 8) {
            serviceCode = track_2.substring(index + 5, index + 8);
        }
        LogUtil.e(Constant.TAG, "cardNumber:" + cardNumber + " expireDate:" + expiryDate + " serviceCode:" + serviceCode);
        cardInfo.cardNo = cardNumber;
        cardInfo.expireDate = expiryDate;
        cardInfo.serviceCode = serviceCode;
        return cardInfo;
    }
    
    /**
     * Remove characters not number,=,D
     */
    static String stringFilter(String str) {
        String regEx = "[^0-9=D]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.replaceAll("").trim();
    }
    
    /**
     * Extract essential EMV TLV data for Apple Pay Field 55 construction
     * Based on the comprehensive Apple Pay EMV tag requirements
     */
    private String extractEmvTlvData() {
        LogUtil.e(Constant.TAG, "Starting Apple Pay EMV TLV data extraction...");
        StringBuilder tlvData = new StringBuilder();
        
        try {
            // Apple Pay Field 55 tag list - Production ready, optimized for Apple Pay
            String[] applePayTags = {
                // Core EMV Tags (Required for Apple Pay Field 55)
                "9F26", // Application Cryptogram (ARQC/ARPC)
                "9F27", // Cryptogram Information Data
                "9F10", // Issuer Application Data (contains tokenization data)
                "9F37", // Unpredictable Number
                "9F36", // Application Transaction Counter
                "95",   // Terminal Verification Results
                "9A",   // Transaction Date
                "9C",   // Transaction Type
                "9F02", // Amount, Authorised
                "5F2A", // Transaction Currency Code
                "82",   // Application Interchange Profile
                "9F1A", // Terminal Country Code
                "9F34", // CVM Results (Apple Pay = CDCVM 5E0300)
                "9F03", // Amount, Other (if cashback)
                "9F33", // Terminal Capabilities
                "9F35", // Terminal Type
                "9F1E", // Interface Device Serial Number
                "84",   // Dedicated File (AID)
                "9F09", // Application Version Number
                "9F41", // Transaction Sequence Counter
                "5A",   // Application PAN (DPAN in Apple Pay)
                "5F34", // PAN Sequence Number
                "5F24", // Application Expiration Date
                "57"    // Track 2 Equivalent Data (contains DPAN for Apple Pay)
            };
            
            // Extract each tag for Apple Pay Field 55
            int extractedCount = 0;
            for (String tag : applePayTags) {
                try {
                    byte[] outData = new byte[256];
                    int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tag, outData);
                    if (len > 0) {
                        byte[] tagData = new byte[len];
                        System.arraycopy(outData, 0, tagData, 0, len);
                        String hexValue = ByteUtil.bytes2HexStr(tagData);
                        tlvData.append(tag).append(": ").append(hexValue).append("\n");
                        extractedCount++;
                        LogUtil.e(Constant.TAG, "Successfully extracted tag " + tag + ": " + hexValue);
                    } else {
                        LogUtil.e(Constant.TAG, "No data for tag " + tag + " (len=" + len + ")");
                    }
                } catch (Exception e) {
                    // Skip this tag if there's an error
                    LogUtil.e(Constant.TAG, "Error extracting tag " + tag + ": " + e.getMessage());
                }
            }
            
            LogUtil.e(Constant.TAG, "Extracted " + extractedCount + " out of " + applePayTags.length + " EMV tags");
            
            if (tlvData.length() == 0) {
                return "No EMV TLV data available";
            }
            
            return tlvData.toString();
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error extracting EMV TLV data: " + e.getMessage());
            return "Error extracting EMV data: " + e.getMessage();
        }
    }
    
    /**
     * Build Field 55 from secure data bundle (PayLib v2.0.32 getAccountSecData)
     */
    private String buildField55FromBundle(Bundle tlvBundle) {
        if (tlvBundle == null) return "";
        
        StringBuilder field55 = new StringBuilder();
        String[] field55Tags = {
            "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
            "82", "9F1A", "9F34", "9F33", "9F35", "9F1E", "84", "9F09", "9F41", "5A", "5F24", "5F34"
        };
        
        for (String tag : field55Tags) {
            String value = tlvBundle.getString(tag);
            if (value != null && !value.isEmpty()) {
                String len = String.format("%02X", value.length() / 2);
                field55.append(tag).append(len).append(value);
            }
        }
        
        return field55.toString();
    }
    
    /**
     * Build Field 55 (EMV data) for Apple Pay/Contactless transactions
     * This is essential for backend integration and ISO8583 processing
     * Based on the comprehensive Apple Pay EMV tag requirements
     */
    private String buildField55() {
        StringBuilder field55 = new StringBuilder();
        
        try {
            // Apple Pay Field 55 tag list - Production ready, optimized for Apple Pay
            // Using int array format for better performance with PayLib
            int[] applePayTagList = {
                0x9F26, // Application Cryptogram (ARQC/ARPC) - CRITICAL for Apple Pay
                0x9F27, // Cryptogram Information Data
                0x9F10, // Issuer Application Data (contains tokenization data)
                0x9F37, // Unpredictable Number
                0x9F36, // Application Transaction Counter
                0x95,   // Terminal Verification Results
                0x9A,   // Transaction Date
                0x9C,   // Transaction Type
                0x9F02, // Amount, Authorised
                0x5F2A, // Transaction Currency Code
                0x82,   // Application Interchange Profile
                0x9F1A, // Terminal Country Code
                0x9F34, // CVM Results (Apple Pay = CDCVM 5E0300)
                0x9F03, // Amount, Other (if cashback)
                0x9F33, // Terminal Capabilities
                0x9F35, // Terminal Type
                0x9F1E, // Interface Device Serial Number
                0x84,   // Dedicated File (AID)
                0x9F09, // Application Version Number
                0x9F41, // Transaction Sequence Counter
                0x5A,   // Application PAN (DPAN in Apple Pay)
                0x5F34, // PAN Sequence Number
                0x5F24, // Application Expiration Date
                0x57    // Track 2 Equivalent Data (contains DPAN for Apple Pay)
            };
            
            // Also keep string array for compatibility
            String[] field55Tags = {
                "9F26", // Application Cryptogram (ARQC/ARPC) - CRITICAL for Apple Pay
                "9F27", // Cryptogram Information Data
                "9F10", // Issuer Application Data (contains tokenization data)
                "9F37", // Unpredictable Number
                "9F36", // Application Transaction Counter
                "95",   // Terminal Verification Results
                "9A",   // Transaction Date
                "9C",   // Transaction Type
                "9F02", // Amount, Authorised
                "5F2A", // Transaction Currency Code
                "82",   // Application Interchange Profile
                "9F1A", // Terminal Country Code
                "9F34", // CVM Results (Apple Pay = CDCVM 5E0300)
                "9F03", // Amount, Other (if cashback)
                "9F33", // Terminal Capabilities
                "9F35", // Terminal Type
                "9F1E", // Interface Device Serial Number
                "84",   // Dedicated File (AID)
                "9F09", // Application Version Number
                "9F41", // Transaction Sequence Counter
                "5A",   // Application PAN (DPAN in Apple Pay)
                "5F34", // PAN Sequence Number
                "5F24", // Application Expiration Date
                "57"    // Track 2 Equivalent Data (contains DPAN for Apple Pay)
            };
            
            LogUtil.e(Constant.TAG, "Building Field 55 for Apple Pay/Contactless transaction...");
            
            // Try using string array format for Field 55 construction
            try {
                byte[] outData = new byte[2048];
                int len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, field55Tags, outData);
                if (len > 0) {
                    byte[] tlvData = new byte[len];
                    System.arraycopy(outData, 0, tlvData, 0, len);
                    String hexValue = ByteUtil.bytes2HexStr(tlvData);
                    // Security: Never log full Field 55
                    if (com.neo.neopayplus.BuildConfig.DEBUG) {
                        String masked = hexValue.length() > 20 ? hexValue.substring(0, 10) + "..." + hexValue.substring(hexValue.length() - 10) : "****";
                        LogUtil.e(Constant.TAG, "Field 55 (Apple Pay) built successfully (masked): " + masked);
                    } else {
                        LogUtil.e(Constant.TAG, "Field 55 (Apple Pay) built successfully - length: " + hexValue.length() + " bytes");
                    }
                    return hexValue;
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error using string array format: " + e.getMessage());
            }
            
            // Fallback to string array format
            for (String tag : field55Tags) {
                try {
                    byte[] outData = new byte[256];
                    int len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tag, outData);
                    if (len > 0) {
                        byte[] tagData = new byte[len];
                        System.arraycopy(outData, 0, tagData, 0, len);
                        String hexValue = ByteUtil.bytes2HexStr(tagData);
                        field55.append(tag).append(hexValue);
                        // Security: Mask sensitive EMV tag values (especially ARQC in 9F26, DPAN in 5A/57)
                        if (com.neo.neopayplus.BuildConfig.DEBUG) {
                            // Mask sensitive tags more aggressively
                            boolean isSensitiveTag = tag.equals("9F26") || tag.equals("5A") || tag.equals("57") || tag.equals("9F10");
                            String masked = isSensitiveTag && hexValue.length() > 12 
                                ? hexValue.substring(0, 4) + "****" + hexValue.substring(hexValue.length() - 4)
                                : (hexValue.length() > 12 ? hexValue.substring(0, 6) + "..." + hexValue.substring(hexValue.length() - 6) : hexValue);
                            LogUtil.e(Constant.TAG, "Field 55 - " + tag + ": " + masked);
                        } else {
                            LogUtil.e(Constant.TAG, "Field 55 - " + tag + ": [not logged for security]");
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "Error extracting Field 55 tag " + tag + ": " + e.getMessage());
                }
            }
            
            if (field55.length() == 0) {
                return "No Field 55 data available";
            }
            
            // Security: Never log full Field 55
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                String field55Str = field55.toString();
                String masked = field55Str.length() > 20 ? field55Str.substring(0, 10) + "..." + field55Str.substring(field55Str.length() - 10) : "****";
                LogUtil.e(Constant.TAG, "Field 55 built successfully (masked): " + masked);
            } else {
                LogUtil.e(Constant.TAG, "Field 55 built successfully");
            }
            return field55.toString();
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error building Field 55: " + e.getMessage());
            return "Error building Field 55: " + e.getMessage();
        }
    }
    
    /**
     * Get Apple Pay specific information from EMV data
     */
    private String getApplePayInfo() {
        StringBuilder info = new StringBuilder();
        
        try {
            // Check if this is Apple Pay by looking for specific indicators
            boolean isApplePay = false;
            String pan = "";
            String dpan = "";
            
            // Get PAN (may be DPAN for Apple Pay)
            try {
                byte[] panData = new byte[256];
                int panLen = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "5A", panData);
                if (panLen > 0) {
                    byte[] panBytes = new byte[panLen];
                    System.arraycopy(panData, 0, panBytes, 0, panLen);
                    pan = ByteUtil.bytes2HexStr(panBytes);
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error getting PAN: " + e.getMessage());
            }
            
            // Get Track 2 data (may contain DPAN)
            try {
                byte[] track2Data = new byte[256];
                int track2Len = mEMVOptV2.getTlv(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, "57", track2Data);
                if (track2Len > 0) {
                    byte[] track2Bytes = new byte[track2Len];
                    System.arraycopy(track2Data, 0, track2Bytes, 0, track2Len);
                    dpan = ByteUtil.bytes2HexStr(track2Bytes);
                    // Security: Never log full Track 2 / DPAN data
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "Error getting Track 2: " + e.getMessage());
            }
            
            // Check for Apple Pay indicators
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                isApplePay = true;
                info.append("Payment Method: Apple Pay/Contactless\n");
                // Security: Never log full PAN
                if (com.neo.neopayplus.BuildConfig.DEBUG && pan != null && pan.length() > 8) {
                    String maskedPan = pan.substring(0, 4) + "****" + pan.substring(pan.length() - 4);
                    info.append("PAN (masked): ").append(maskedPan).append("\n");
                } else {
                    info.append("PAN: Available (not logged for security)\n");
                }
                // Security: Never log full Track 2 / DPAN
                if (com.neo.neopayplus.BuildConfig.DEBUG && dpan != null && dpan.length() > 8) {
                    String maskedDpan = dpan.substring(0, 4) + "****" + dpan.substring(dpan.length() - 4);
                    info.append("Track 2 (masked): ").append(maskedDpan).append("\n");
                } else {
                    info.append("Track 2: Available (not logged for security)\n");
                }
                info.append("Field 55: ").append(buildField55()).append("\n");
            } else {
                info.append("Payment Method: EMV Card\n");
                // Security: Never log full PAN
                if (com.neo.neopayplus.BuildConfig.DEBUG && pan != null && pan.length() > 8) {
                    String maskedPan = pan.substring(0, 4) + "****" + pan.substring(pan.length() - 4);
                    info.append("PAN (masked): ").append(maskedPan).append("\n");
                } else {
                    info.append("PAN: Available (not logged for security)\n");
                }
            }
            
            return info.toString();
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "Error getting Apple Pay info: " + e.getMessage());
            return "Error getting Apple Pay info: " + e.getMessage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PIN_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                String pin = data.getStringExtra("pin");
                LogUtil.e(Constant.TAG, "PIN entered successfully, length=" + (pin != null ? pin.length() : 0));
                // Notify EMV kernel that PIN entry succeeded
                try {
                    mEMVOptV2.importPinInputStatus(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> updateStatus("PIN entered. Continuing...", 92));
            } else {
                LogUtil.e(Constant.TAG, "PIN entry cancelled or failed");
                try {
                    // Notify EMV kernel that PIN entry was cancelled
                    mEMVOptV2.importPinInputStatus(1, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    updateStatus("PIN entry cancelled", 0);
                    showToast("PIN entry cancelled");
                });
            }
        }
    }
}
