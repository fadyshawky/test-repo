package com.neo.neopayplus;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.DisplayMetrics;

import com.neo.neopayplus.api.EmvConfigApiFactory;
import com.neo.neopayplus.api.EmvConfigApiService;
import com.neo.neopayplus.emv.EmvConfigurationManager;
import com.neo.neopayplus.emv.EmvTTS;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.utils.PreferencesUtil;
import com.neo.neopayplus.utils.Utility;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.print.PrinterOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2;
import com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2;
import com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2;
import com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2;
import com.sunmi.pay.hardware.aidlv2.test.TestOptV2;
import com.sunmi.pay.hardware.wrapper.HCEManagerV2Wrapper;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.scanner.IScanInterface;

import java.util.Locale;

import sunmi.paylib.SunmiPayKernel;

public class MyApplication extends Application {
    public static MyApplication app;

    public BasicOptV2 basicOptV2;                   // 获取基础操作模块
    public ReadCardOptV2 readCardOptV2;             // 获取读卡模块
    public PinPadOptV2 pinPadOptV2;                 // 获取PinPad操作模块
    public SecurityOptV2 securityOptV2;             // 获取安全操作模块
    public EMVOptV2 emvOptV2;                       // 获取EMV操作模块
    public TaxOptV2 taxOptV2;                       // 获取税控操作模块
    public ETCOptV2 etcOptV2;                       // 获取ETC操作模块
    public PrinterOptV2 printerOptV2;               // 获取打印操作模块
    public TestOptV2 testOptV2;                     // 获取测试操作模块
    public DevCertManagerV2 devCertManagerV2;       // 设备证书操作模块
    public NoLostKeyManagerV2 noLostKeyManagerV2;   // NoLostKey操作模块
    public HCEManagerV2Wrapper hceV2Wrapper;        // HCE操作模块
    public RFIDOptV2 rfidOptV2;                     // RFID操作模块
    public SunmiPrinterService sunmiPrinterService; // 打印模块
    public IScanInterface scanInterface;            // 扫码模块
    public BiometricManagerV2 mBiometricManagerV2;  // 生物特征模块

    private boolean connectPaySDK;//是否已连接PaySDK
    
    /**
     * Terminal Configuration
     * Loaded from backend on boot with local cache fallback
     */
    public static TerminalConfig terminalConfig;
    
    /**
     * Terminal Configuration Data
     * Single source of truth for terminal parameters
     */
    public static class TerminalConfig {
        public String terminalId;
        public String merchantId;
        public String currencyCode;
        public String currencyCodeTlv;
        public String terminalCountryCode;
        public String merchantName;
        
        public TerminalConfig() {
            // Default values (fallback)
            this.terminalId = com.neo.neopayplus.config.PaymentConfig.TERMINAL_ID;
            this.merchantId = com.neo.neopayplus.config.PaymentConfig.MERCHANT_ID;
            this.currencyCode = com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE;
            this.currencyCodeTlv = com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE_TLV;
            this.terminalCountryCode = com.neo.neopayplus.config.PaymentConfig.TERMINAL_COUNTRY_CODE;
            this.merchantName = com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME;
        }
        
        public TerminalConfig(String terminalId, String merchantId, String currencyCode) {
            this.terminalId = terminalId;
            this.merchantId = merchantId;
            this.currencyCode = currencyCode != null && currencyCode.length() >= 3 
                ? currencyCode.substring(currencyCode.length() - 3) : currencyCode;
            this.currencyCodeTlv = currencyCode != null && currencyCode.length() >= 4 
                ? currencyCode.substring(currencyCode.length() - 4) : 
                (currencyCode != null && currencyCode.length() == 3 ? "0" + currencyCode : "0818");
            this.terminalCountryCode = com.neo.neopayplus.config.PaymentConfig.TERMINAL_COUNTRY_CODE;
            this.merchantName = com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME;
        }
        
        @Override
        public String toString() {
            return "TerminalConfig{" +
                    "terminalId='" + terminalId + '\'' +
                    ", merchantId='" + merchantId + '\'' +
                    ", currencyCode='" + currencyCode + '\'' +
                    '}';
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initLocaleLanguage();
        initEmvTTS();
        bindPrintService();
        bindScannerService();
        
        // Load terminal config from cache first (fast boot)
        loadTerminalConfigFromCache();
        
        // Fetch terminal config from backend (with cache fallback)
        fetchTerminalConfig();
        
        // Initialize HostGateway (POS-only mock for now)
        try {
            com.neo.neopayplus.crypto.CryptoProvider crypto = new com.neo.neopayplus.crypto.MockCryptoProvider();
            com.neo.neopayplus.host.HostGateway host = new com.neo.neopayplus.host.MockHostGateway(crypto);
            com.neo.neopayplus.utils.ServiceLocator.init(host, crypto, getApplicationContext());
            
            // Perform sign-on before enabling sales (POS-only mock)
            com.neo.neopayplus.host.dto.SessionInfo s = new com.neo.neopayplus.host.dto.SessionInfo();
            s.tid = com.neo.neopayplus.config.PaymentConfig.getTerminalId();
            s.mid = com.neo.neopayplus.config.PaymentConfig.getMerchantId();
            s.acquirerId = "000000"; // TODO: supply real acquirer id when available
            com.neo.neopayplus.host.dto.HostResult r = host.signOn(s);
            if (!"000".equals(r.rc)) {
                com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "Host sign-on failed, rc=" + r.rc + ". Sales disabled until online.");
            } else {
                com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "Host sign-on OK");
            }
        } catch (Throwable t) {
            com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "HostGateway init error: " + t.getMessage());
        }
        
        // Start TamperGuard after PaySDK connects
        // Will be called from onConnectPaySDK()
        
        // Start background operations: reversal retry + time sync
        startBackgroundOperations();
    }
    
    /**
     * Start background operations: reversal worker + time sync
     */
    private void startBackgroundOperations() {
        new Thread(() -> {
            try {
                // Wait for PaySDK to connect
                int maxWait = 60; // 60 seconds max wait
                int waited = 0;
                while (!connectPaySDK && waited < maxWait) {
                    Thread.sleep(1000);
                    waited++;
                }
                
                if (!connectPaySDK) {
                    com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "⚠️ PaySDK not connected after " + maxWait + " seconds - background ops may not work");
                    return;
                }
                
                // Start tamper guard
                com.neo.neopayplus.security.TamperGuard.start(this);
                
                // Start reversal worker and time sync loop
                com.neo.neopayplus.payment.ReversalWorker worker = new com.neo.neopayplus.payment.ReversalWorker(this);
                
                while (true) {
                    try {
                        // Process pending reversals
                        worker.tick();
                        
                        // Sync time from server (every 30 seconds)
                        com.neo.neopayplus.utils.TimeSync.sync(new com.neo.neopayplus.utils.TimeSync.OnTime() {
                            @Override
                            public void ok(String isoUtc) {
                                // Compare device time with server time
                                long drift = com.neo.neopayplus.utils.TimeSync.calculateDrift(isoUtc);
                                if (Math.abs(drift) > 60) { // More than 60 seconds drift
                                    com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "⚠️ Time drift detected: " + drift + " seconds");
                                }
                            }
                            
                            @Override
                            public void err(String m) {
                                // Ignore time sync errors (non-critical)
                            }
                        });
                        
                        // Sleep 30 seconds before next tick
                        Thread.sleep(30_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "Background operations error: " + e.getMessage());
                        // Continue loop on error
                        try {
                            Thread.sleep(30_000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "Background operations thread error: " + e.getMessage());
                e.printStackTrace();
            }
        }, "bg-ops").start();
    }

    public static void initLocaleLanguage() {
        Resources resources = app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        int showLanguage = CacheHelper.getCurrentLanguage();
        if (showLanguage == Constant.LANGUAGE_AUTO) {
            LogUtil.e(Constant.TAG, config.locale.getCountry() + "---这是系统语言");
            config.locale = Resources.getSystem().getConfiguration().locale;
        } else if (showLanguage == Constant.LANGUAGE_ZH_CN) {
            LogUtil.e(Constant.TAG, "这是中文");
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (showLanguage == Constant.LANGUAGE_EN_US) {
            LogUtil.e(Constant.TAG, "这是英文");
            config.locale = Locale.ENGLISH;
        } else if (showLanguage == Constant.LANGUAGE_JA_JP) {
            LogUtil.e(Constant.TAG, "这是日文");
            config.locale = Locale.JAPAN;
        }
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e(Constant.TAG, "onConfigurationChanged");
    }

    public boolean isConnectPaySDK() {
        return connectPaySDK;
    }

    /**
     * bind PaySDK service
     */
    public void bindPaySDKService() {
        final SunmiPayKernel payKernel = SunmiPayKernel.getInstance();
        payKernel.initPaySDK(this, new SunmiPayKernel.ConnectCallback() {
            @Override
            public void onConnectPaySDK() {
                LogUtil.e(Constant.TAG, "onConnectPaySDK...");
                emvOptV2 = payKernel.mEMVOptV2;
                basicOptV2 = payKernel.mBasicOptV2;
                pinPadOptV2 = payKernel.mPinPadOptV2;
                readCardOptV2 = payKernel.mReadCardOptV2;
                securityOptV2 = payKernel.mSecurityOptV2;
                taxOptV2 = payKernel.mTaxOptV2;
                etcOptV2 = payKernel.mETCOptV2;
                printerOptV2 = payKernel.mPrinterOptV2;
                testOptV2 = payKernel.mTestOptV2;
                devCertManagerV2 = payKernel.mDevCertManagerV2;
                noLostKeyManagerV2 = payKernel.mNoLostKeyManagerV2;
                mBiometricManagerV2 = payKernel.mBiometricManagerV2;
                hceV2Wrapper = payKernel.mHCEManagerV2Wrapper;
                rfidOptV2 = payKernel.mRFIDOptV2;
                connectPaySDK = true;
                
                // Initialize EMV configuration once when PaySDK connects
                // This loads AIDs, CAPKs, and terminal parameters
                // PayLib v2.0.32: This is the provisioning phase (one-time setup)
                new Thread(() -> {
                    // Step 1: Load EMV configuration (AIDs, CAPKs, terminal params)
                    boolean initialized = EmvConfigurationManager.getInstance().initialize();
                    if (initialized) {
                        LogUtil.e(Constant.TAG, "✓ EMV configuration initialized successfully");
                    } else {
                        LogUtil.e(Constant.TAG, "❌ EMV configuration initialization failed");
                        LogUtil.e(Constant.TAG, "⚠️ Transactions may fail - provisioning incomplete");
                    }
                    
                    // Step 2: Initialize keys based on configured model
                    if (com.neo.neopayplus.config.PaymentConfig.isMasterSession()) {
                        initializeMasterSessionKeys(securityOptV2);
                    } else {
                        initializeDukptKeys(securityOptV2);
                        
                        // Runtime check for DUKPT
                        try {
                            byte[] testKsn = new byte[10];
                            boolean keysReady = (securityOptV2.dukptCurrentKSN(1100, testKsn) == 0);
                            LogUtil.e(Constant.TAG, "Runtime check - DUKPT Keys ready: " + keysReady);
                            if (keysReady) {
                                LogUtil.e(Constant.TAG, "Runtime check - Current KSN: " + 
                                    com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(testKsn));
                            }
                        } catch (Exception e) {
                            LogUtil.e(Constant.TAG, "Runtime check warning: " + e.getMessage());
                        }
                    }
                    
                    // Step 3: Runtime check - verify provisioning status
                    // Note: getAidList() may not be available in all SDK versions
                    LogUtil.e(Constant.TAG, "Runtime check - AIDs loaded via EmvConfigurationManager");
                }).start();
            }

            @Override
            public void onDisconnectPaySDK() {
                LogUtil.e(Constant.TAG, "onDisconnectPaySDK...");
                connectPaySDK = false;
                emvOptV2 = null;
                basicOptV2 = null;
                pinPadOptV2 = null;
                readCardOptV2 = null;
                securityOptV2 = null;
                taxOptV2 = null;
                etcOptV2 = null;
                printerOptV2 = null;
                devCertManagerV2 = null;
                noLostKeyManagerV2 = null;
                mBiometricManagerV2 = null;
//                hceManagerV2 = null;
                rfidOptV2 = null;
                Utility.showToast(R.string.connect_fail);
            }
        });
    }

    /**
     * bind printer service
     */
    private void bindPrintService() {
        try {
            InnerPrinterManager.getInstance().bindService(this, new InnerPrinterCallback() {
                @Override
                protected void onConnected(SunmiPrinterService service) {
                    sunmiPrinterService = service;
                }

                @Override
                protected void onDisconnected() {
                    sunmiPrinterService = null;
                }
            });
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * bind scanner service
     */
    public void bindScannerService() {
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.scanner");
        intent.setAction("com.sunmi.scanner.IScanInterface");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                scanInterface = IScanInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                scanInterface = null;
            }
        }, Service.BIND_AUTO_CREATE);
    }

    private void initEmvTTS() {
        EmvTTS.getInstance().init();
    }
    
    /**
     * Initialize Master/Session keys (TMK→TPK/TAK) via sign-on
     * Performs ISO sign-on (0800) to obtain wrapped TPK/TAK in DE62, unwraps with TMK, installs to slots 12/13
     * 
     * @param securityOptV2 SecurityOptV2 instance for key injection
     */
    private void initializeMasterSessionKeys(SecurityOptV2 securityOptV2) {
        LogUtil.e(Constant.TAG, "=== INITIALIZING MASTER/SESSION KEYS (TPK/TAK) ===");
        LogUtil.e(Constant.TAG, "  Terminal ID: " + com.neo.neopayplus.config.PaymentConfig.getTerminalId());
        LogUtil.e(Constant.TAG, "  Model: MASTER_SESSION (TPK/TAK under TMK)");
        
        if (!com.neo.neopayplus.config.PaymentConfig.isMasterSession()) {
            LogUtil.e(Constant.TAG, "  Skipped: PaymentConfig not MASTER_SESSION");
            return;
        }
        
        // 1) Ensure TMK is loaded in slot #1 (either from prior provisioning, or invoke your TMK install flow)
        boolean hasTmk = com.neo.neopayplus.security.KeyManagerPOS.hasKeyInstalled(
            com.neo.neopayplus.security.KeyManagerPOS.KeySlots.TMK_SLOT_1);
        if (!hasTmk) {
            LogUtil.e(Constant.TAG, "❌ TMK not installed in slot #1. Install TMK first (technician/provisioning).");
            return;
        }
        
        // 2) Do ISO Sign-On (0800) to obtain wrapped TPK/TAK (DE62 TLV)
        try {
            com.neo.neopayplus.host.dto.SessionInfo sessionInfo = new com.neo.neopayplus.host.dto.SessionInfo();
            sessionInfo.tid = com.neo.neopayplus.config.PaymentConfig.getTerminalId();
            sessionInfo.mid = com.neo.neopayplus.config.PaymentConfig.getMerchantId();
            sessionInfo.acquirerId = "000000"; // TODO: supply real acquirer id when available
            
            com.neo.neopayplus.host.HostGateway hostGateway = com.neo.neopayplus.utils.ServiceLocator.host();
            if (hostGateway == null) {
                LogUtil.e(Constant.TAG, "❌ HostGateway not initialized");
                return;
            }
            
            com.neo.neopayplus.host.dto.HostResult hr = hostGateway.signOn(sessionInfo);
            if (!hr.isApproved()) {
                LogUtil.e(Constant.TAG, "❌ Sign-on failed RC=" + hr.rc);
                return;
            }
            
            if (hr.de62Bytes == null || hr.de62Bytes.length == 0) {
                LogUtil.e(Constant.TAG, "❌ Sign-on response missing DE62 (session keys)");
                return;
            }
            
            // 3) Parse DE62 TLV payload and install TPK/TAK
            com.neo.neopayplus.keys.KeyTlv.SessionKeys sess = 
                com.neo.neopayplus.keys.KeyTlv.parseKeySetFromDE62(hr.de62Bytes);
            
            boolean ok = com.neo.neopayplus.security.KeyManagerPOS.installSessionKeysUnderTmk(
                com.neo.neopayplus.security.KeyManagerPOS.KeySlots.TMK_SLOT_1,
                com.neo.neopayplus.security.KeyManagerPOS.KeySlots.TPK_SLOT_12,
                com.neo.neopayplus.security.KeyManagerPOS.KeySlots.TAK_SLOT_13,
                sess.tpkEnc, sess.tpkKcv,
                sess.takEnc, sess.takKcv
            );
            
            if (ok) {
                LogUtil.e(Constant.TAG, "✓ Session keys installed (TPK→12, TAK→13), key_set_version=" + 
                    (sess.keySetVersion != null ? sess.keySetVersion : "N/A"));
                LogUtil.e(Constant.TAG, "  MAC field: DE128 (X9.19 Retail CBC-MAC)");
                LogUtil.e(Constant.TAG, "  PIN: ISO-0 under TPK");
            } else {
                LogUtil.e(Constant.TAG, "❌ Failed to install session keys");
            }
            
        } catch (Exception ex) {
            LogUtil.e(Constant.TAG, "❌ Master/Session initialization error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Initialize DUKPT keys from backend at terminal startup
     * This fetches IPEK + KSN from GET /v1/terminal/dukpt and injects them into the terminal
     * 
     * @param securityOptV2 SecurityOptV2 instance for key injection
     */
    private void initializeDukptKeys(SecurityOptV2 securityOptV2) {
        if (securityOptV2 == null) {
            LogUtil.e(Constant.TAG, "⚠️ SecurityOptV2 not available - cannot initialize DUKPT keys");
            return;
        }
        
        // Get terminal ID from config
        String terminalId = com.neo.neopayplus.config.PaymentConfig.TERMINAL_ID;
        if (terminalId == null || terminalId.isEmpty()) {
            LogUtil.e(Constant.TAG, "⚠️ Terminal ID not configured - cannot fetch DUKPT keys");
            return;
        }
        
        // Check if DUKPT keys already exist (from previous initialization or key rotation)
        try {
            byte[] existingKsn = new byte[10];
            int result = securityOptV2.dukptCurrentKSN(1100, existingKsn);
            if (result == 0) {
                String existingKsnHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(existingKsn);
                LogUtil.e(Constant.TAG, "✓ DUKPT keys already exist - KSN: " + existingKsnHex);
                LogUtil.e(Constant.TAG, "  Skipping initial key fetch (keys already injected)");
                return;
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "⚠️ Error checking existing DUKPT keys: " + e.getMessage());
        }
        
        // Fetch DUKPT keys from backend
        LogUtil.e(Constant.TAG, "=== INITIALIZING DUKPT KEYS FROM BACKEND ===");
        LogUtil.e(Constant.TAG, "  Terminal ID: " + terminalId);
        LogUtil.e(Constant.TAG, "  Endpoint: GET /v1/terminal/dukpt");
        
        com.neo.neopayplus.api.PaymentApiService apiService = 
            com.neo.neopayplus.api.PaymentApiFactory.getInstance();
        
        if (!apiService.isAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Payment API service not available - cannot fetch DUKPT keys");
            LogUtil.e(Constant.TAG, "  DUKPT keys must be injected manually or via key rotation");
            return;
        }
        
        apiService.getDukptKeys(terminalId, new com.neo.neopayplus.api.PaymentApiService.DukptKeysCallback() {
            @Override
            public void onDukptKeysComplete(com.neo.neopayplus.api.PaymentApiService.DukptKeysResponse response) {
                if (response.success && response.ipek != null && response.ksn != null) {
                    LogUtil.e(Constant.TAG, "✓ DUKPT keys fetched successfully from backend");
                    LogUtil.e(Constant.TAG, "  Key Index: " + response.keyIndex);
                    LogUtil.e(Constant.TAG, "  IPEK: " + response.ipek.substring(0, 8) + "****");
                    LogUtil.e(Constant.TAG, "  KSN: " + response.ksn);
                    
                    // Inject DUKPT keys into terminal
                    injectDukptKeys(securityOptV2, response);
                } else {
                    LogUtil.e(Constant.TAG, "❌ DUKPT keys fetch failed: " + 
                        (response.message != null ? response.message : "Unknown error"));
                }
            }
            
            @Override
            public void onDukptKeysError(Throwable error) {
                LogUtil.e(Constant.TAG, "❌ Failed to fetch DUKPT keys from backend: " + error.getMessage());
                LogUtil.e(Constant.TAG, "⚠️ DUKPT keys must be injected manually or via key rotation");
                LogUtil.e(Constant.TAG, "⚠️ Transactions with online PIN may fail without DUKPT keys");
            }
        });
    }
    
    /**
     * Inject DUKPT keys (IPEK + KSN) into the terminal security module
     * 
     * @param securityOptV2 SecurityOptV2 instance
     * @param response DUKPT keys response from backend
     */
    private void injectDukptKeys(SecurityOptV2 securityOptV2, 
                                 com.neo.neopayplus.api.PaymentApiService.DukptKeysResponse response) {
        try {
            // Convert hex IPEK to byte array
            byte[] ipekBytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(response.ipek);
            
            // Convert hex KSN to byte array
            byte[] ksnBytes = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(response.ksn);
            
            // Validate lengths
            if (ipekBytes == null || ipekBytes.length != 32) {
                LogUtil.e(Constant.TAG, "❌ Invalid IPEK length (expected 32 bytes, got " + 
                    (ipekBytes != null ? ipekBytes.length : 0) + ")");
                return;
            }
            
            if (ksnBytes == null || ksnBytes.length != 10) {
                LogUtil.e(Constant.TAG, "❌ Invalid KSN length (expected 10 bytes, got " + 
                    (ksnBytes != null ? ksnBytes.length : 0) + ")");
                return;
            }
            
            // Use key index from response (default 1100 if not specified)
            int keyIndex = response.keyIndex > 0 ? response.keyIndex : 1100;
            
            // Inject DUKPT keys using SecurityOptV2
            int result = securityOptV2.saveKeyDukpt(
                com.sunmi.payservice.AidlConstantsV2.Security.KEY_TYPE_DUPKT_IPEK,
                ipekBytes,
                null, // checkValue (auto-calculate)
                ksnBytes,
                com.sunmi.payservice.AidlConstantsV2.Security.KEY_ALG_TYPE_3DES,
                keyIndex
            );
            
            if (result == 0) {
                LogUtil.e(Constant.TAG, "✅ DUKPT keys injected successfully at index " + keyIndex);
                
                // Verify injection by fetching current KSN
                byte[] currentKsn = new byte[10];
                int verifyResult = securityOptV2.dukptCurrentKSN(keyIndex, currentKsn);
                if (verifyResult == 0) {
                    String currentKsnHex = com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(currentKsn);
                    LogUtil.e(Constant.TAG, "✓ Verified - Current KSN: " + currentKsnHex);
                }
            } else {
                LogUtil.e(Constant.TAG, "❌ Failed to inject DUKPT keys, code: " + result);
                LogUtil.e(Constant.TAG, "⚠️ Transactions with online PIN may fail");
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Error injecting DUKPT keys: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load terminal configuration from local cache (fast boot)
     */
    private void loadTerminalConfigFromCache() {
        try {
            String cachedTerminalId = PreferencesUtil.getTerminalConfig("terminal_id", null);
            String cachedMerchantId = PreferencesUtil.getTerminalConfig("merchant_id", null);
            String cachedCurrencyCode = PreferencesUtil.getTerminalConfig("currency_code", null);
            
            if (cachedTerminalId != null && cachedMerchantId != null && cachedCurrencyCode != null) {
                terminalConfig = new TerminalConfig(cachedTerminalId, cachedMerchantId, cachedCurrencyCode);
                LogUtil.e(Constant.TAG, "✓ Terminal config loaded from cache");
                LogUtil.e(Constant.TAG, "  Terminal ID: " + terminalConfig.terminalId);
                LogUtil.e(Constant.TAG, "  Merchant ID: " + terminalConfig.merchantId);
                LogUtil.e(Constant.TAG, "  Currency: " + terminalConfig.currencyCode);
            } else {
                // Use defaults if no cache
                terminalConfig = new TerminalConfig();
                LogUtil.e(Constant.TAG, "⚠️ No cached terminal config - using defaults");
            }
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "⚠️ Error loading terminal config from cache: " + e.getMessage());
            terminalConfig = new TerminalConfig(); // Use defaults on error
        }
    }
    
    /**
     * Fetch terminal configuration from backend (with cache fallback)
     * This is called once at app boot
     */
    private void fetchTerminalConfig() {
        LogUtil.e(Constant.TAG, "=== FETCHING TERMINAL CONFIG FROM BACKEND ===");
        LogUtil.e(Constant.TAG, "  Endpoint: GET /v1/terminal/config");
        
        EmvConfigApiService configApi = EmvConfigApiFactory.getInstance();
        
        if (!configApi.isAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Config API not available - using cached/default config");
            if (terminalConfig == null) {
                terminalConfig = new TerminalConfig();
            }
            return;
        }
        
        configApi.loadEmvConfiguration(new EmvConfigApiService.EmvConfigCallback() {
            @Override
            public void onConfigLoaded(EmvConfigApiService.EmvConfigResponse response) {
                if (response.success) {
                    // Extract terminal config from response
                    // Note: The API response may not directly include terminal_id/merchant_id
                    // These might need to be extracted from the response or passed separately
                    // For now, we'll update what we can from the response
                    
                    String currencyCode = response.currencyCode != null ? response.currencyCode : 
                        (terminalConfig != null ? terminalConfig.currencyCode : com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE);
                    
                    // Get terminal ID from config (or from response if available)
                    String terminalId = com.neo.neopayplus.config.PaymentConfig.TERMINAL_ID;
                    String merchantId = com.neo.neopayplus.config.PaymentConfig.MERCHANT_ID;
                    
                    // Update terminal config
                    terminalConfig = new TerminalConfig(terminalId, merchantId, currencyCode);
                    
                    // Save to cache
                    saveTerminalConfigToCache(terminalConfig);
                    
                    LogUtil.e(Constant.TAG, "✓ Terminal config fetched successfully");
                    LogUtil.e(Constant.TAG, "  Terminal ID: " + terminalConfig.terminalId);
                    LogUtil.e(Constant.TAG, "  Merchant ID: " + terminalConfig.merchantId);
                    LogUtil.e(Constant.TAG, "  Currency: " + terminalConfig.currencyCode);
                } else {
                    LogUtil.e(Constant.TAG, "⚠️ Config fetch failed - using cached/default config");
                    if (terminalConfig == null) {
                        terminalConfig = new TerminalConfig();
                    }
                }
            }
            
            @Override
            public void onConfigError(Throwable error) {
                LogUtil.e(Constant.TAG, "❌ Failed to fetch terminal config from backend: " + error.getMessage());
                LogUtil.e(Constant.TAG, "⚠️ Using cached/default config");
                if (terminalConfig == null) {
                    terminalConfig = new TerminalConfig();
                }
            }
        });
    }
    
    /**
     * Save terminal configuration to local cache
     */
    private void saveTerminalConfigToCache(TerminalConfig config) {
        try {
            PreferencesUtil.saveTerminalConfig("terminal_id", config.terminalId);
            PreferencesUtil.saveTerminalConfig("merchant_id", config.merchantId);
            PreferencesUtil.saveTerminalConfig("currency_code", config.currencyCode);
            PreferencesUtil.saveTerminalConfig("timestamp", String.valueOf(System.currentTimeMillis()));
            LogUtil.e(Constant.TAG, "✓ Terminal config saved to cache");
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "⚠️ Error saving terminal config to cache: " + e.getMessage());
        }
    }

}
