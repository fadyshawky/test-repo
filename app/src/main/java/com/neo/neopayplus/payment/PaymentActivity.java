package com.neo.neopayplus.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.neo.neopayplus.BaseAppCompatActivity;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.R;
import com.neo.neopayplus.api.PaymentApiFactory;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.data.TransactionJournal;
import com.neo.neopayplus.emv.EmvUtil;
import com.neo.neopayplus.emv.TLV;
import com.neo.neopayplus.emv.TLVUtil;
import com.neo.neopayplus.payment.ReversalQueueStore;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.utils.SettingUtil;
import com.neo.neopayplus.utils.ThreadPoolUtil;

import org.json.JSONObject;
import com.neo.neopayplus.wrapper.CheckCardCallbackV2Wrapper;
import com.neo.neopayplus.wrapper.PinPadListenerV2Wrapper;
import com.sunmi.pay.hardware.aidl.bean.CardInfo;
import com.sunmi.payservice.AidlConstantsV2;
import com.sunmi.pay.hardware.aidlv2.AidlErrorCodeV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Payment Activity for Card Transactions
 * Handles amount input, card reading, PIN input, and displays full transaction payload
 */
public class PaymentActivity extends BaseAppCompatActivity {

    private EMVOptV2 mEMVOptV2;
    private PinPadOptV2 mPinPadOptV2;
    private ReadCardOptV2 mReadCardOptV2;

    private TextView mTvAmountDisplay;
    private TextView mTvPinDisplay;
    private TextView mTvShowInfo;
    private Button mBtnStartPayment;
    private ScrollView mScrollView;
    
    // PIN pad buttons
    private Button[] mPinButtons = new Button[10];
    private Button mBtnPinClear;
    private Button mBtnPinEnter;
    
    // PIN input state
    private StringBuilder mPinInput = new StringBuilder();
    private static final int MAX_PIN_LENGTH = 6;

    private int mCardType;  // card type
    private String mCardNo; // card number
    private int mPinType;   // 0-online pin, 1-offline pin
    private String mCertInfo;
    private int mSelectIndex;
    private String mAmount;

    private int mAppSelect = 0;
    private int mProcessStep;
    private AlertDialog mAppSelectDialog;
    private Map<String, String> configMap;

    private static final int EMV_APP_SELECT = 1;
    private static final int EMV_FINAL_APP_SELECT = 2;
    private static final int EMV_CONFIRM_CARD_NO = 3;
    private static final int EMV_CERT_VERIFY = 4;
    private static final int EMV_SHOW_PIN_PAD = 5;
    private static final int EMV_ONLINE_PROCESS = 6;
    private static final int EMV_SIGNATURE = 7;
    private static final int EMV_TRANS_SUCCESS = 888;
    private static final int EMV_TRANS_FAIL = 999;
    private static final int REMOVE_CARD = 1000;

    private static final int PIN_CLICK_NUMBER = 50;
    private static final int PIN_CLICK_PIN = 51;
    private static final int PIN_CLICK_CONFIRM = 52;
    private static final int PIN_CLICK_CANCEL = 53;
    private static final int PIN_ERROR = 54;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EMV_FINAL_APP_SELECT:
                    importFinalAppSelectStatus(0);
                    break;
                case EMV_APP_SELECT:
                    dismissLoadingDialog();
                    String[] candiNames = (String[]) msg.obj;
                    mAppSelectDialog = new AlertDialog.Builder(PaymentActivity.this)
                            .setTitle("Select Payment Application")
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                        importAppSelect(-1);
                                    }
                            )
                            .setPositiveButton("OK", (dialog, which) -> {
                                        showLoadingDialog("Processing...");
                                        importAppSelect(mSelectIndex);
                                    }
                            )
                            .setSingleChoiceItems(candiNames, 0, (dialog, which) -> {
                                        mSelectIndex = which;
                                        LogUtil.e(Constant.TAG, "singleChoiceItems which:" + which);
                                    }
                            ).create();
                    mSelectIndex = 0;
                    mAppSelectDialog.show();
                    break;
                case EMV_CONFIRM_CARD_NO:
                    dismissLoadingDialog();
                    mTvShowInfo.setText("Card Number: " + mCardNo + "\n\nPlease confirm the card number above.");
                    mBtnStartPayment.setText("Confirm Card Number");
                    break;
                case EMV_CERT_VERIFY:
                    dismissLoadingDialog();
                    mTvShowInfo.setText("Certificate Info: " + mCertInfo + "\n\nPlease confirm certificate information.");
                    mBtnStartPayment.setText("Confirm Certificate");
                    break;
                case EMV_SHOW_PIN_PAD:
                    dismissLoadingDialog();
                    initPinPad();
                    break;
                case EMV_ONLINE_PROCESS:
                    processTransactionData();
                    break;
                case EMV_SIGNATURE:
                    importSignatureStatus(0);
                    break;
                case PIN_CLICK_NUMBER:
                    break;
                case PIN_CLICK_PIN:
                    importPinInputStatus(0);
                    break;
                case PIN_CLICK_CONFIRM:
                    importPinInputStatus(2);
                    break;
                case PIN_CLICK_CANCEL:
                    showToast("User cancelled PIN input");
                    importPinInputStatus(1);
                    break;
                case PIN_ERROR:
                    showToast("PIN Error: " + msg.obj + " -- " + msg.arg1);
                    importPinInputStatus(3);
                    break;
                case EMV_TRANS_FAIL:
                    resetUI();
                    dismissLoadingDialog();
                    showToast("Transaction Failed: " + msg.obj + " -- " + msg.arg1);
                    break;
                case EMV_TRANS_SUCCESS:
                    resetUI();
                    checkAndRemoveCard();
                    showToast("Transaction Successful!");
                    break;
                case REMOVE_CARD:
                    checkAndRemoveCard();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initData();
        initView();
        
        // Check if launched in reversal mode
        String mode = getIntent().getStringExtra("mode");
        if ("reversal".equals(mode)) {
            // PayActivity's startReversal() will be called when user enters RRN
            // or it may be triggered automatically if lastRrn is available
            LogUtil.e(Constant.TAG, "PayActivity launched in REVERSAL mode");
        }
        
        // Auto-retry pending reversals on activity start (FIFO queue)
        retryPendingReversals();
    }

    private void initView() {
        mEMVOptV2 = MyApplication.app.emvOptV2;
        mPinPadOptV2 = MyApplication.app.pinPadOptV2;
        mReadCardOptV2 = MyApplication.app.readCardOptV2;
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Card Payment");
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        mTvAmountDisplay = findViewById(R.id.tv_amount_display);
        mTvPinDisplay = findViewById(R.id.tv_pin_display);
        mTvShowInfo = findViewById(R.id.tv_info);
        mBtnStartPayment = findViewById(R.id.btn_start_payment);
        mScrollView = findViewById(R.id.scroll_view);
        
        // Initialize PIN pad buttons
        initPinPadButtons();
        
        mBtnStartPayment.setOnClickListener(this);
        
        // Handle system window insets
        setupSystemWindowInsets();
        
        // Set initial UI state
        updateAmountDisplay(0);
        updatePinDisplay();
        mTvShowInfo.setText("Tap 'Start Payment' to begin transaction. Amount will be set automatically.");
    }

    private void initData() {
        // Disable check card buzzer
        SettingUtil.setBuzzerEnable(false);
        // Configure EMV parameters
        configMap = EmvUtil.getConfig(EmvUtil.COUNTRY_CHINA);
        ThreadPoolUtil.executeInCachePool(() -> {
            EmvUtil.initKey();
            EmvUtil.initAidAndRid();
            EmvUtil.setTerminalParam(configMap);
            runOnUiThread(() -> showToast("Payment system initialized"));
        });
    }

    @Override
    public void onBackPressed() {
        if (mProcessStep == EMV_APP_SELECT) {
            importAppSelect(-1);
        } else if (mProcessStep == EMV_FINAL_APP_SELECT) {
            importFinalAppSelectStatus(-1);
        } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
            importCardNoStatus(1);
        } else if (mProcessStep == EMV_CERT_VERIFY) {
            importCertStatus(1);
        } else if (mProcessStep == PIN_ERROR) {
            importPinInputStatus(3);
        } else if (mProcessStep == EMV_ONLINE_PROCESS) {
            importOnlineProcessStatus(1);
        } else if (mProcessStep == EMV_SIGNATURE) {
            importSignatureStatus(1);
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_start_payment) {
            if (mProcessStep == 0) {
                // Check if in reversal mode
                String mode = getIntent().getStringExtra("mode");
                if ("reversal".equals(mode)) {
                    startReversal();
                } else {
                startPaymentProcess();
                }
            } else if (mProcessStep == EMV_CONFIRM_CARD_NO) {
                showLoadingDialog("Processing...");
                importCardNoStatus(0);
            } else if (mProcessStep == EMV_CERT_VERIFY) {
                showLoadingDialog("Processing...");
                importCertStatus(0);
            }
        }
    }

    private void startPaymentProcess() {
        // Set a default amount for demonstration (can be changed later)
        mAmount = "1000"; // $10.00 in cents
        updateAmountDisplay(Long.parseLong(mAmount));
        
        mTvShowInfo.setText("Initializing payment process...\nAmount: " + mAmount + " cents");
        
        LogUtil.e(Constant.TAG, "Starting payment process for amount: " + mAmount);
        
        try {
            // Initialize EMV process
            mEMVOptV2.initEmvProcess();
            initEmvTlvData();
            checkCard();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error starting payment process");
        }
    }

    private void initEmvTlvData() {
        try {
            // Set PayPass (MasterCard) TLV data
            String[] tagsPayPass = {"DF8117", "DF8118", "DF8119", "DF811F", "DF811E", "DF812C",
                    "DF8123", "DF8124", "DF8125", "DF8126",
                    "DF811B", "DF811D", "DF8122", "DF8120", "DF8121"};
            String[] valuesPayPass = {"E0", "F8", "F8", "E8", "00", "00",
                    "000000000000", "000000100000", "999999999999", "000000100000",
                    "30", "02", "0000000000", "000000000000", "000000000000"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, tagsPayPass, valuesPayPass);

            // Set AMEX (AmericanExpress) TLV data
            String[] tagsAE = {"9F6D", "9F6E", "9F33", "9F35", "DF8168", "DF8167", "DF8169", "DF8170"};
            String[] valuesAE = {"C0", "D8E00000", "E0E888", "22", "00", "00", "00", "60"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_AE, tagsAE, valuesAE);

            String[] tagsJCB = {"9F53", "DF8161"};
            String[] valuesJCB = {"708000", "7F00"};
            mEMVOptV2.setTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_JCB, tagsJCB, valuesJCB);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void checkCard() {
        try {
            showLoadingDialog("Please insert or tap your card...");
            int cardType = AidlConstantsV2.CardType.NFC.getValue() | AidlConstantsV2.CardType.IC.getValue();
            mReadCardOptV2.checkCard(cardType, mCheckCardCallback, 60);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error checking for card");
        }
    }

    private final CheckCardCallbackV2 mCheckCardCallback = new CheckCardCallbackV2Wrapper() {

        @Override
        public void findMagCard(Bundle bundle) throws RemoteException {
            LogUtil.e(Constant.TAG, "findMagCard:" + bundle);
            showToast("Magnetic card detected - Please use chip or contactless");
        }

        @Override
        public void findICCard(String atr) throws RemoteException {
            LogUtil.e(Constant.TAG, "findICCard:" + atr);
            MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            mCardType = AidlConstantsV2.CardType.IC.getValue();
            transactProcess();
        }

        @Override
        public void findRFCard(String uuid) throws RemoteException {
            LogUtil.e(Constant.TAG, "findRFCard:" + uuid);
            mCardType = AidlConstantsV2.CardType.NFC.getValue();
            transactProcess();
        }

        @Override
        public void onError(int code, String message) throws RemoteException {
            String error = "Card Error: " + message + " (Code: " + code + ")";
            LogUtil.e(Constant.TAG, error);
            showToast(error);
            dismissLoadingDialog();
        }
    };

    private void transactProcess() {
        LogUtil.e(Constant.TAG, "Starting EMV transaction process");
        try {
            Bundle bundle = new Bundle();
            bundle.putString("amount", mAmount);
            bundle.putString("transType", "00");
            
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_NFC_SPEEDUP);
            } else {
                bundle.putInt("flowType", AidlConstantsV2.EMV.FlowType.TYPE_EMV_STANDARD);
            }
            bundle.putInt("cardType", mCardType);
            
            mEMVOptV2.transactProcessEx(bundle, mEMVListener);
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error starting transaction process");
        }
    }

    private final EMVListenerV2 mEMVListener = new EMVListenerV2.Stub() {

        @Override
        public void onWaitAppSelect(List<EMVCandidateV2> appNameList, boolean isFirstSelect) throws RemoteException {
            LogUtil.e(Constant.TAG, "onWaitAppSelect isFirstSelect:" + isFirstSelect);
            mProcessStep = EMV_APP_SELECT;
            String[] candidateNames = getCandidateNames(appNameList);
            mHandler.obtainMessage(EMV_APP_SELECT, candidateNames).sendToTarget();
        }

        @Override
        public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
            LogUtil.e(Constant.TAG, "onAppFinalSelect tag9F06Value:" + tag9F06Value);
            if (tag9F06Value != null && tag9F06Value.length() > 0) {
                boolean isUnionPay = tag9F06Value.startsWith("A000000333");
                boolean isVisa = tag9F06Value.startsWith("A000000003");
                boolean isMaster = tag9F06Value.startsWith("A000000004") || tag9F06Value.startsWith("A000000005");
                boolean isAmericanExpress = tag9F06Value.startsWith("A000000025");
                boolean isJCB = tag9F06Value.startsWith("A000000065");
                boolean isRupay = tag9F06Value.startsWith("A000000524");
                
                String paymentType = "Unknown";
                if (isUnionPay) {
                    paymentType = "UnionPay";
                    mAppSelect = 0;
                } else if (isVisa) {
                    paymentType = "Visa";
                    mAppSelect = 1;
                } else if (isMaster) {
                    paymentType = "MasterCard";
                    mAppSelect = 2;
                } else if (isAmericanExpress) {
                    paymentType = "American Express";
                } else if (isJCB) {
                    paymentType = "JCB";
                } else if (isRupay) {
                    paymentType = "RuPay";
                }
                LogUtil.e(Constant.TAG, "Detected " + paymentType + " card");
            }
            mProcessStep = EMV_FINAL_APP_SELECT;
            mHandler.obtainMessage(EMV_FINAL_APP_SELECT, tag9F06Value).sendToTarget();
        }

        @Override
        public void onConfirmCardNo(String cardNo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onConfirmCardNo cardNo:" + cardNo);
            mCardNo = cardNo;
            mProcessStep = EMV_CONFIRM_CARD_NO;
            mHandler.obtainMessage(EMV_CONFIRM_CARD_NO).sendToTarget();
        }

        @Override
        public void onRequestShowPinPad(int pinType, int remainTime) throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestShowPinPad pinType:" + pinType + " remainTime:" + remainTime);
            mPinType = pinType;
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            mProcessStep = EMV_SHOW_PIN_PAD;
            mHandler.obtainMessage(EMV_SHOW_PIN_PAD).sendToTarget();
        }

        @Override
        public void onRequestSignature() throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestSignature");
            mProcessStep = EMV_SIGNATURE;
            mHandler.obtainMessage(EMV_SIGNATURE).sendToTarget();
        }

        @Override
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onCertVerify certType:" + certType + " certInfo:" + certInfo);
            mCertInfo = certInfo;
            mProcessStep = EMV_CERT_VERIFY;
            mHandler.obtainMessage(EMV_CERT_VERIFY).sendToTarget();
        }

        @Override
        public void onOnlineProc() throws RemoteException {
            LogUtil.e(Constant.TAG, "onOnlineProcess");
            mProcessStep = EMV_ONLINE_PROCESS;
            mHandler.obtainMessage(EMV_ONLINE_PROCESS).sendToTarget();
        }

        @Override
        public void onCardDataExchangeComplete() throws RemoteException {
            LogUtil.e(Constant.TAG, "onCardDataExchangeComplete");
            if (mCardType == AidlConstantsV2.CardType.NFC.getValue()) {
                MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
            }
        }

        @Override
        public void onTransResult(int code, String desc) throws RemoteException {
            LogUtil.e(Constant.TAG, "onTransResult code:" + code + " desc:" + desc);
            if (mCardNo == null) {
                mCardNo = getCardNo();
            }
            
            if (code == 0) {
                mHandler.obtainMessage(EMV_TRANS_SUCCESS, code, code, desc).sendToTarget();
            } else if (code == 4) {
                tryAgain();
            } else {
                mHandler.obtainMessage(EMV_TRANS_FAIL, code, code, desc).sendToTarget();
            }
        }

        @Override
        public void onConfirmationCodeVerified() throws RemoteException {
            LogUtil.e(Constant.TAG, "onConfirmationCodeVerified");
            // Handle confirmation code verification if needed
        }

        @Override
        public void onRequestDataExchange(String cardNo) throws RemoteException {
            LogUtil.e(Constant.TAG, "onRequestDataExchange,cardNo:" + cardNo);
            mEMVOptV2.importDataExchangeStatus(0);
        }

        @Override
        public void onTermRiskManagement() throws RemoteException {
            LogUtil.e(Constant.TAG, "onTermRiskManagement");
            mEMVOptV2.importTermRiskManagementStatus(0);
        }

        @Override
        public void onPreFirstGenAC() throws RemoteException {
            LogUtil.e(Constant.TAG, "onPreFirstGenAC");
            mEMVOptV2.importPreFirstGenACStatus(0);
        }

        @Override
        public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
            LogUtil.e(Constant.TAG, "onDataStorageProc");
            String[] tags = new String[0];
            String[] values = new String[0];
            mEMVOptV2.importDataStorage(tags, values);
        }
    };

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

    public static CardInfo parseTrack2(String track2) {
        LogUtil.e(Constant.TAG, "track2:" + track2);
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

    static String stringFilter(String str) {
        String regEx = "[^0-9=D]";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.replaceAll("").trim();
    }

    private void initPinPad() {
        LogUtil.e(Constant.TAG, "initPinPad");
        try {
            PinPadConfigV2 pinPadConfig = new PinPadConfigV2();
            pinPadConfig.setPinPadType(0);
            pinPadConfig.setPinType(mPinType);
            pinPadConfig.setOrderNumKey(false);
            byte[] panBytes = mCardNo.substring(mCardNo.length() - 13, mCardNo.length() - 1).getBytes("US-ASCII");
            pinPadConfig.setPan(panBytes);
            pinPadConfig.setTimeout(60 * 1000);
            pinPadConfig.setPinKeyIndex(12);
            pinPadConfig.setMaxInput(12);
            pinPadConfig.setMinInput(0);
            pinPadConfig.setKeySystem(0);
            pinPadConfig.setAlgorithmType(0);
            mPinPadOptV2.initPinPad(pinPadConfig, mPinPadListener);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(PIN_ERROR, Integer.MIN_VALUE, Integer.MIN_VALUE, "initPinPad() failure").sendToTarget();
        }
    }

    private final PinPadListenerV2 mPinPadListener = new PinPadListenerV2Wrapper() {

        @Override
        public void onPinLength(int len) {
            LogUtil.e(Constant.TAG, "onPinLength:" + len);
            mHandler.obtainMessage(PIN_CLICK_NUMBER, len).sendToTarget();
        }

        @Override
        public void onConfirm(int i, byte[] pinBlock) {
            if (pinBlock != null) {
                String hexStr = ByteUtil.bytes2HexStr(pinBlock);
                LogUtil.e(Constant.TAG, "onConfirm pin block:" + hexStr);
                mHandler.obtainMessage(PIN_CLICK_PIN, pinBlock).sendToTarget();
            } else {
                mHandler.obtainMessage(PIN_CLICK_CONFIRM).sendToTarget();
            }
        }

        @Override
        public void onCancel() {
            LogUtil.e(Constant.TAG, "onCancel");
            mHandler.obtainMessage(PIN_CLICK_CANCEL).sendToTarget();
        }

        @Override
        public void onError(int code) {
            LogUtil.e(Constant.TAG, "onError:" + code);
            String msg = AidlErrorCodeV2.valueOf(code).getMsg();
            mHandler.obtainMessage(PIN_ERROR, code, code, msg).sendToTarget();
        }
    };

    private void importAppSelect(int selectIndex) {
        LogUtil.e(Constant.TAG, "importAppSelect selectIndex:" + selectIndex);
        try {
            mEMVOptV2.importAppSelect(selectIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importFinalAppSelectStatus(int status) {
        try {
            LogUtil.e(Constant.TAG, "importFinalAppSelectStatus status:" + status);
            mEMVOptV2.importAppFinalSelectStatus(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void importCardNoStatus(int status) {
        LogUtil.e(Constant.TAG, "importCardNoStatus status:" + status);
        try {
            mEMVOptV2.importCardNoStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importCertStatus(int status) {
        LogUtil.e(Constant.TAG, "importCertStatus status:" + status);
        try {
            mEMVOptV2.importCertStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importPinInputStatus(int inputResult) {
        LogUtil.e(Constant.TAG, "importPinInputStatus:" + inputResult);
        try {
            mEMVOptV2.importPinInputStatus(mPinType, inputResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importSignatureStatus(int status) {
        LogUtil.e(Constant.TAG, "importSignatureStatus status:" + status);
        try {
            mEMVOptV2.importSignatureStatus(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importOnlineProcessStatus(int status) {
        LogUtil.e(Constant.TAG, "importOnlineProcessStatus status:" + status);
        try {
            String[] tags = {"71", "72", "91", "8A", "89"};
            String[] values = {"", "", "", "", ""};
            byte[] out = new byte[1024];
            int len = mEMVOptV2.importOnlineProcStatus(status, tags, values, out);
            if (len < 0) {
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus error,code:" + len);
            } else {
                byte[] bytes = Arrays.copyOf(out, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                LogUtil.e(Constant.TAG, "importOnlineProcessStatus outData:" + hexStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processTransactionData() {
        new Thread(() -> {
            try {
                showLoadingDialog("Processing transaction data...");
                getTlvData();
                Thread.sleep(1000);
                importOnlineProcessStatus(0);
            } catch (Exception e) {
                e.printStackTrace();
                importOnlineProcessStatus(-1);
            } finally {
                dismissLoadingDialog();
            }
        }).start();
    }

    private void getTlvData() {
        try {
            String[] tagList = {
                    "DF02", "5F34", "9F06", "FF30", "FF31", "95", "9B", "9F36", "9F26",
                    "9F27", "DF31", "5A", "57", "5F24", "9F1A", "9F33", "9F35", "9F40",
                    "9F03", "9F10", "9F37", "9C", "9A", "9F02", "5F2A", "82", "9F34", "9F1E",
                    "84", "4F", "9F66", "9F6C", "9F09", "9F41", "9F63", "5F20", "9F12", "50",
            };
            byte[] outData = new byte[2048];
            Map<String, TLV> map = new TreeMap<>();
            int tlvOpCode;
            if (AidlConstantsV2.CardType.NFC.getValue() == mCardType) {
                if (mAppSelect == 2) {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS;
                } else if (mAppSelect == 1) {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_PAYWAVE;
                } else {
                    tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
                }
            } else {
                tlvOpCode = AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL;
            }
            int len = mEMVOptV2.getTlvList(tlvOpCode, tagList, outData);
            if (len > 0) {
                byte[] bytes = Arrays.copyOf(outData, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(hexStr);
                map.putAll(tlvMap);
            }

            // PayPass tags
            String[] payPassTags = {
                    "DF811E", "DF812C", "DF8118", "DF8119", "DF811F", "DF8117", "DF8124",
                    "DF8125", "9F6D", "DF811B", "9F53", "DF810C", "9F1D", "DF8130", "DF812D",
                    "DF811C", "DF811D", "9F7C",
            };
            len = mEMVOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS, payPassTags, outData);
            if (len > 0) {
                byte[] bytes = Arrays.copyOf(outData, len);
                String hexStr = ByteUtil.bytes2HexStr(bytes);
                Map<String, TLV> tlvMap = TLVUtil.buildTLVMap(hexStr);
                map.putAll(tlvMap);
            }

            final StringBuilder sb = new StringBuilder();
            sb.append("=== TRANSACTION PAYLOAD DATA ===\n\n");
            sb.append("Amount: ").append(mAmount).append(" cents\n");
            sb.append("Card Number: ").append(mCardNo != null ? mCardNo : "N/A").append("\n");
            sb.append("Card Type: ").append(mCardType == AidlConstantsV2.CardType.NFC.getValue() ? "NFC" : "IC").append("\n\n");
            sb.append("=== EMV TLV DATA ===\n\n");
            
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                TLV tlv = map.get(key);
                sb.append(key).append(": ");
                if (tlv != null) {
                    String value = tlv.getValue();
                    sb.append(value);
                }
                sb.append("\n");
            }
            
            sb.append("\n=== END OF PAYLOAD ===\n");
            sb.append("This is the complete data that would be sent to your backend API.");
            
            runOnUiThread(() -> {
                mTvShowInfo.setText(sb.toString());
                mScrollView.fullScroll(View.FOCUS_DOWN);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetUI() {
        runOnUiThread(() -> {
            mProcessStep = 0;
            mPinInput.setLength(0);
            updatePinDisplay();
            updateAmountDisplay(0);
            mBtnStartPayment.setText("Start Payment");
            dismissLoadingDialog();
            dismissAppSelectDialog();
        });
    }

    private void dismissAppSelectDialog() {
        runOnUiThread(() -> {
            if (mAppSelectDialog != null) {
                try {
                    mAppSelectDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAppSelectDialog = null;
            }
        });
    }

    private String[] getCandidateNames(List<EMVCandidateV2> candiList) {
        if (candiList == null || candiList.size() == 0) return new String[0];
        String[] result = new String[candiList.size()];
        for (int i = 0; i < candiList.size(); i++) {
            EMVCandidateV2 candi = candiList.get(i);
            String name = candi.appPreName;
            name = TextUtils.isEmpty(name) ? candi.appLabel : name;
            name = TextUtils.isEmpty(name) ? candi.appName : name;
            name = TextUtils.isEmpty(name) ? "" : name;
            result[i] = name;
            LogUtil.e(Constant.TAG, "EMVCandidateV2: " + name);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCheckCard();
    }

    private void cancelCheckCard() {
        try {
            mReadCardOptV2.cardOff(AidlConstantsV2.CardType.NFC.getValue());
            mReadCardOptV2.cardOff(AidlConstantsV2.CardType.IC.getValue());
            mReadCardOptV2.cancelCheckCard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tryAgain() {
        try {
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Try Again")
                    .setMessage("Please read the card again")
                    .setPositiveButton("OK", (dia, which) -> {
                                dia.dismiss();
                                checkCard();
                            }
                    )
                    .show()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndRemoveCard() {
        try {
            int status = mReadCardOptV2.getCardExistStatus(mCardType);
            if (status < 0) {
                LogUtil.e(Constant.TAG, "getCardExistStatus error, code:" + status);
                dismissLoadingDialog();
                return;
            }
            if (status == AidlConstantsV2.CardExistStatus.CARD_ABSENT) {
                dismissLoadingDialog();
            } else if (status == AidlConstantsV2.CardExistStatus.CARD_PRESENT) {
                showLoadingDialog("Please remove card...");
                MyApplication.app.basicOptV2.buzzerOnDevice(1, 2750, 200, 0);
                mHandler.sendEmptyMessageDelayed(REMOVE_CARD, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPinPadButtons() {
        // Initialize number buttons
        int[] buttonIds = {
            R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
            R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };
        
        for (int i = 0; i < buttonIds.length; i++) {
            mPinButtons[i] = findViewById(buttonIds[i]);
            final int digit = i;
            mPinButtons[i].setOnClickListener(v -> onPinDigitClick(digit));
        }
        
        // Initialize clear and enter buttons
        mBtnPinClear = findViewById(R.id.btn_pin_clear);
        mBtnPinEnter = findViewById(R.id.btn_pin_enter);
        
        mBtnPinClear.setOnClickListener(v -> onPinClearClick());
        mBtnPinEnter.setOnClickListener(v -> onPinEnterClick());
    }

    private void onPinDigitClick(int digit) {
        if (mPinInput.length() < MAX_PIN_LENGTH) {
            mPinInput.append(digit);
            updatePinDisplay();
            LogUtil.e(Constant.TAG, "PIN input: " + mPinInput.toString());
        }
    }

    private void onPinClearClick() {
        mPinInput.setLength(0);
        updatePinDisplay();
        LogUtil.e(Constant.TAG, "PIN cleared");
    }

    private void onPinEnterClick() {
        if (mPinInput.length() > 0) {
            String pin = mPinInput.toString();
            LogUtil.e(Constant.TAG, "PIN entered: " + pin);
            
            // Here you would typically send the PIN to the EMV process
            // For now, just show a message
            showToast("PIN entered: " + pin);
            
            // Clear PIN after entry
            mPinInput.setLength(0);
            updatePinDisplay();
        } else {
            showToast("Please enter a PIN");
        }
    }

    private void updatePinDisplay() {
        StringBuilder display = new StringBuilder();
        for (int i = 0; i < MAX_PIN_LENGTH; i++) {
            if (i < mPinInput.length()) {
                display.append("•");
            } else {
                display.append("•");
            }
        }
        mTvPinDisplay.setText(display.toString());
    }

    private void updateAmountDisplay(long amountInCents) {
        double amountInDollars = amountInCents / 100.0;
        mTvAmountDisplay.setText(String.format("$%.2f", amountInDollars));
    }

    private void setupSystemWindowInsets() {
        View rootView = findViewById(android.R.id.content);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            // Get system bar insets
            int statusBarHeight = insets.getSystemWindowInsetTop();
            int navigationBarHeight = insets.getSystemWindowInsetBottom();
            
            // Apply padding to the ScrollView to account for system bars
            mScrollView.setPadding(
                mScrollView.getPaddingLeft(),
                mScrollView.getPaddingTop(),
                mScrollView.getPaddingRight(),
                mScrollView.getPaddingBottom() + navigationBarHeight
            );
            
            return insets;
        });
    }
    
    // ==================== REVERSAL FLOW ====================
    
    /**
     * Start reversal transaction
     * Called when user taps Reverse button or when reversal mode is activated
     */
    private void startReversal() {
        // Get last RRN from journal (auto-fill if available)
        String lastRrn = TransactionJournal.getLastRrn();
        
        // Show dialog to enter RRN (or use last RRN if available)
        showReversalDialog(lastRrn);
    }
    
    /**
     * Show reversal RRN input dialog
     */
    private void showReversalDialog(String defaultRrn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reverse Transaction");
        
        // Create EditText for RRN input
        final EditText input = new EditText(this);
        input.setHint("Enter RRN");
        if (defaultRrn != null && !defaultRrn.isEmpty()) {
            input.setText(defaultRrn);
        }
        builder.setView(input);
        
        builder.setPositiveButton("Reverse", (dialog, which) -> {
            String rrn = input.getText().toString().trim();
            if (rrn.isEmpty()) {
                showToast("Please enter RRN");
                return;
            }
            processReversal(rrn);
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.setNeutralButton("Select from History", (dialog, which) -> {
            // Open transaction history to select RRN
            Intent historyIntent = new Intent(this, 
                com.neo.neopayplus.transactions.TransactionHistoryActivity.class);
            startActivityForResult(historyIntent, REQUEST_CODE_SELECT_RRN);
        });
        
        builder.show();
    }
    
    private static final int REQUEST_CODE_SELECT_RRN = 1001;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_RRN && resultCode == RESULT_OK) {
            String rrn = data.getStringExtra("rrn");
            if (rrn != null && !rrn.isEmpty()) {
                processReversal(rrn);
            }
        }
    }
    
    /**
     * Process reversal transaction
     * Sends reversal request to backend, queues offline if host is down
     */
    private void processReversal(String rrn) {
        LogUtil.e(Constant.TAG, "=== PROCESSING REVERSAL ===");
        LogUtil.e(Constant.TAG, "  RRN: " + rrn);
        
        // Find original transaction by RRN
        TransactionJournal.TransactionRecord originalTx = 
            TransactionJournal.findTransactionByRrn(rrn);
        
        if (originalTx == null) {
            showToast("Transaction not found for RRN: " + rrn);
            LogUtil.e(Constant.TAG, "❌ No transaction found with RRN: " + rrn);
            return;
        }
        
        // Build reversal request
        PaymentApiService.ReversalRequest request = new PaymentApiService.ReversalRequest();
        request.terminalId = PaymentConfig.getTerminalId();
        request.merchantId = PaymentConfig.getMerchantId();
        request.rrn = rrn;
        request.amount = originalTx.amount;
        request.currencyCode = originalTx.currencyCode != null ? 
            originalTx.currencyCode : PaymentConfig.getCurrencyCode();
        request.reversalReason = "USER_REQUEST";
        
        // Send reversal to server
        sendReversalToServer(request);
    }
    
    /**
     * Send reversal request to backend
     * Queues offline if host is down
     */
    private void sendReversalToServer(PaymentApiService.ReversalRequest request) {
        PaymentApiService apiService = PaymentApiFactory.getInstance();
        
        // API will handle network errors in callback
        // We'll queue offline if network error occurs
        
        LogUtil.e(Constant.TAG, "Sending reversal request to backend...");
        showToast("Processing reversal...");
        
        apiService.reverseTransaction(request, new PaymentApiService.ReversalCallback() {
            @Override
            public void onReversalComplete(PaymentApiService.ReversalResponse response) {
                runOnUiThread(() -> {
                    if (response.approved) {
                        LogUtil.e(Constant.TAG, "✓ Reversal Approved ✅");
                        showToast("Reversal Approved ✅");
                        
                        // Save to journal
                        TransactionJournal.TransactionRecord reversal = 
                            new TransactionJournal.TransactionRecord();
                        reversal.rrn = request.rrn;
                        reversal.amount = request.amount;
                        reversal.currencyCode = request.currencyCode;
                        reversal.transactionType = "20"; // Reversal
                        reversal.responseCode = response.responseCode;
                        reversal.status = "APPROVED";
                        reversal.isReversal = true;
                        reversal.originalRrn = request.rrn;
                        
                        // Use current date/time
                        java.text.SimpleDateFormat dateFormat = 
                            new java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US);
                        java.text.SimpleDateFormat timeFormat = 
                            new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
                        java.util.Date now = new java.util.Date();
                        reversal.date = dateFormat.format(now);
                        reversal.time = timeFormat.format(now);
                        
                        TransactionJournal.saveTransaction(reversal);
                        
                        // TODO: Print reversal receipt with QR
                        // ReceiptPrinter.printReversalCopy(reversal);
                        
                    } else {
                        LogUtil.e(Constant.TAG, "❌ Reversal Declined: " + response.responseCode);
                        showToast("Reversal Declined: " + response.responseMessage);
                    }
                });
            }
            
            @Override
            public void onReversalError(Throwable error) {
                runOnUiThread(() -> {
                    LogUtil.e(Constant.TAG, "❌ Reversal failed: " + error.getMessage());
                    // Host down - queue reversal offline
                    queueReversalOffline(request);
                });
            }
        });
    }
    
    /**
     * Queue reversal offline (when host is down)
     */
    private void queueReversalOffline(PaymentApiService.ReversalRequest request) {
        try {
            JSONObject reversalJson = new JSONObject();
            reversalJson.put("terminal_id", request.terminalId);
            reversalJson.put("merchant_id", request.merchantId);
            reversalJson.put("rrn", request.rrn);
            reversalJson.put("amount", request.amount);
            reversalJson.put("currency", request.currencyCode);
            reversalJson.put("reversal_reason", request.reversalReason != null ? 
                request.reversalReason : "HOST_UNAVAILABLE");
            
            ReversalQueueStore.add(this, reversalJson);
            
            LogUtil.e(Constant.TAG, "✓ Reversal queued offline ⏳");
            showToast("Host down — reversal queued ⏳");
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error queueing reversal offline: " + e.getMessage());
            e.printStackTrace();
            showToast("Error queueing reversal offline");
        }
    }
    
    /**
     * Retry pending reversals (FIFO queue)
     * Called automatically on activity start and after successful transactions
     */
    private void retryPendingReversals() {
        org.json.JSONArray arr = ReversalQueueStore.load(this);
        if (arr.length() == 0) {
            return; // No pending reversals
        }
        
        LogUtil.e(Constant.TAG, "=== RETRYING PENDING REVERSALS ===");
        LogUtil.e(Constant.TAG, "  Pending count: " + arr.length());
        
        try {
            org.json.JSONObject rev = arr.getJSONObject(0); // FIFO: First In, First Out
            String rrn = rev.optString("rrn", "");
            
            LogUtil.e(Constant.TAG, "Retrying reversal for RRN: " + rrn);
            
            // Build reversal request from queued JSON
            PaymentApiService.ReversalRequest request = new PaymentApiService.ReversalRequest();
            request.terminalId = rev.optString("terminal_id", PaymentConfig.getTerminalId());
            request.merchantId = rev.optString("merchant_id", PaymentConfig.getMerchantId());
            request.rrn = rrn;
            request.amount = rev.optString("amount", "");
            request.currencyCode = rev.optString("currency", PaymentConfig.getCurrencyCode());
            request.reversalReason = rev.optString("reversal_reason", "RETRY");
            
            PaymentApiService apiService = PaymentApiFactory.getInstance();
            
            // API will handle network errors in callback
            // If host is down, we'll stop retrying and queue will remain
            
            // Retry reversal
            apiService.reverseTransaction(request, new PaymentApiService.ReversalCallback() {
                @Override
                public void onReversalComplete(PaymentApiService.ReversalResponse response) {
                    runOnUiThread(() -> {
                        if (response.approved) {
                            LogUtil.e(Constant.TAG, "✓ Pending reversal approved ✅");
                            showToast("Pending reversal approved ✅");
                            
                            // Remove from queue (FIFO: first item)
                            ReversalQueueStore.removeFirst(PaymentActivity.this);
                            
                            // Save to journal
                            TransactionJournal.TransactionRecord reversal = 
                                new TransactionJournal.TransactionRecord();
                            reversal.rrn = request.rrn;
                            reversal.amount = request.amount;
                            reversal.currencyCode = request.currencyCode;
                            reversal.transactionType = "20"; // Reversal
                            reversal.responseCode = response.responseCode;
                            reversal.status = "APPROVED";
                            reversal.isReversal = true;
                            reversal.originalRrn = request.rrn;
                            
                            java.text.SimpleDateFormat dateFormat = 
                                new java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US);
                            java.text.SimpleDateFormat timeFormat = 
                                new java.text.SimpleDateFormat("HHmmss", java.util.Locale.US);
                            java.util.Date now = new java.util.Date();
                            reversal.date = dateFormat.format(now);
                            reversal.time = timeFormat.format(now);
                            
                            TransactionJournal.saveTransaction(reversal);
                            
                            // TODO: Print reversal receipt
                            
                            // Retry next pending reversal (FIFO)
                            retryPendingReversals();
                            
                        } else {
                            LogUtil.e(Constant.TAG, "❌ Pending reversal declined: " + response.responseCode);
                            showToast("Pending reversal declined: " + response.responseMessage);
                            // Remove from queue even if declined (don't retry declined reversals)
                            ReversalQueueStore.removeFirst(PaymentActivity.this);
                            // Continue with next reversal
                            retryPendingReversals();
                        }
                    });
                }
                
                @Override
                public void onReversalError(Throwable error) {
                    runOnUiThread(() -> {
                        LogUtil.e(Constant.TAG, "⚠️ Host still unavailable - will retry later");
                        // Stop retrying (host still down)
                        // Will retry on next activity start or transaction
                    });
                }
            });
            
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error retrying pending reversal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
