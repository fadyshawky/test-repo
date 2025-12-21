package com.neo.neopayplus;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.neo.neopayplus.api.EmvConfigApiFactory;
import com.neo.neopayplus.api.EmvConfigApiService;
import com.neo.neopayplus.api.PaymentApiFactory;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.emv.EmvConfigurationManager;
import com.neo.neopayplus.emv.EmvTTS;
import com.neo.neopayplus.keys.KeyRegistry;
import com.neo.neopayplus.utils.LogUtil;
import com.neo.neopayplus.utils.PreferencesUtil;
import com.neo.neopayplus.utils.Utility;
import com.neo.neopayplus.security.KeyStoreRsaUtil;
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
// import com.sunmi.scanner.IScanInterface; // Optional scanner dependency - commented out if not available

import java.util.Locale;

import sunmi.paylib.SunmiPayKernel;
import com.sunmi.payservice.AidlConstantsV2;
import com.sunmi.pay.hardware.aidl.AidlConstants;

@HiltAndroidApp
public class MyApplication extends Application {
    public static MyApplication app;

    public BasicOptV2 basicOptV2; // 获取基础操作模块
    public ReadCardOptV2 readCardOptV2; // 获取读卡模块
    public PinPadOptV2 pinPadOptV2; // 获取PinPad操作模块
    public SecurityOptV2 securityOptV2; // 获取安全操作模块
    public EMVOptV2 emvOptV2; // 获取EMV操作模块
    public TaxOptV2 taxOptV2; // 获取税控操作模块
    public ETCOptV2 etcOptV2; // 获取ETC操作模块
    public PrinterOptV2 printerOptV2; // 获取打印操作模块
    public TestOptV2 testOptV2; // 获取测试操作模块
    public DevCertManagerV2 devCertManagerV2; // 设备证书操作模块
    public NoLostKeyManagerV2 noLostKeyManagerV2; // NoLostKey操作模块
    public HCEManagerV2Wrapper hceV2Wrapper; // HCE操作模块
    public RFIDOptV2 rfidOptV2; // RFID操作模块
    public SunmiPrinterService sunmiPrinterService; // 打印模块
    // public IScanInterface scanInterface; // 扫码模块 - Optional scanner dependency
    public BiometricManagerV2 mBiometricManagerV2; // 生物特征模块

    private boolean connectPaySDK;// 是否已连接PaySDK

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
        public final String terminalId;
        public final String merchantId;
        public final String currencyCode;
        public final String currencyCodeTlv;
        public final String terminalCountryCode;
        public final String merchantName;

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

            // Validate currency code is numeric (3 digits) - if not, use default
            String normalizedCurrencyCode = currencyCode;
            if (currencyCode != null && currencyCode.length() >= 3) {
                // Extract last 3 characters
                normalizedCurrencyCode = currencyCode.substring(currencyCode.length() - 3);
            }

            // Validate it's numeric (3 digits) - if not (e.g., "EGP"), use default
            if (normalizedCurrencyCode == null || !normalizedCurrencyCode.matches("\\d{3}")) {
                LogUtil.e(Constant.TAG,
                        "⚠️ Invalid currency code format: " + currencyCode + " (not numeric), using default: 818");
                normalizedCurrencyCode = com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE; // "818"
            }

            this.currencyCode = normalizedCurrencyCode;
            this.currencyCodeTlv = normalizedCurrencyCode.length() == 3 ? "0" + normalizedCurrencyCode : "0818";
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

    /**
     * Provisions TMK from backend.
     * 1. Registers terminal's RSA public key
     * 2. Requests TMK (RSA-encrypted)
     * 3. Decrypts and injects TMK into secure element
     */
    private boolean ensureTmkInstalled(com.neo.neopayplus.emv.KeyManager keyManager) {
        PaymentApiService apiService = PaymentApiFactory.getInstance();
        if (!apiService.isAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Payment API unavailable");
            return false;
        }

        // Check network and register retry if unavailable
        com.neo.neopayplus.utils.NetworkMonitor networkMonitor = com.neo.neopayplus.utils.NetworkMonitor
                .getInstance(this);

        if (!networkMonitor.isNetworkAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Network unavailable - will retry TMK provisioning when network reconnects");
            networkMonitor.registerRetryOperation(new Runnable() {
                @Override
                public void run() {
                    if (emvOptV2 != null && securityOptV2 != null) {
                        initializeMasterSessionKeys(securityOptV2);
                    }
                }
            });
            return false;
        }

        try {
            String terminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId();

            // Register terminal's RSA public key
            KeyStoreRsaUtil.ensureRsaKey();
            String publicKeyPem = KeyStoreRsaUtil.exportPublicKeyPem();

            PaymentApiService.TerminalRegisterRequest registerRequest = new PaymentApiService.TerminalRegisterRequest();
            registerRequest.terminalId = terminalId;
            registerRequest.publicKeyPem = publicKeyPem;

            PaymentApiService.TerminalRegisterResponse registerResponse = apiService
                    .registerTerminalKeySync(registerRequest);
            if (!registerResponse.success) {
                LogUtil.e(Constant.TAG, "❌ Terminal registration failed: " + registerResponse.message);
                return false;
            }

            // Request TMK
            PaymentApiService.TmkProvisionRequest tmkRequest = new PaymentApiService.TmkProvisionRequest();
            tmkRequest.terminalId = terminalId;
            PaymentApiService.TmkProvisionResponse tmkResponse = apiService.provisionTmkSync(tmkRequest);

            if (!tmkResponse.success || tmkResponse.wrappedTmk == null) {
                LogUtil.e(Constant.TAG, "❌ TMK provisioning failed: " + tmkResponse.message);
                return false;
            }

            // Decrypt TMK and inject
            byte[] clearTmk = KeyStoreRsaUtil.decryptKeyMaterial(tmkResponse.wrappedTmk);
            String kcv = tmkResponse.tmkKcv != null ? tmkResponse.tmkKcv : "";
            keyManager.importPlainTmk(clearTmk, kcv);

            LogUtil.e(Constant.TAG, "✓ TMK provisioned");
            return true;
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "TMK provisioning", e);

            // Check if error is network-related and register retry
            if (e instanceof java.net.UnknownHostException ||
                    e instanceof java.net.ConnectException ||
                    e instanceof java.io.IOException) {
                LogUtil.e(Constant.TAG,
                        "⚠️ Network error detected - will retry TMK provisioning when network reconnects");
                com.neo.neopayplus.utils.NetworkMonitor.getInstance(this)
                        .registerRetryOperation(new Runnable() {
                            @Override
                            public void run() {
                                if (emvOptV2 != null && securityOptV2 != null) {
                                    initializeMasterSessionKeys(securityOptV2);
                                }
                            }
                        });
            }

            return false;
        }
    }

    /**
     * Provisions TPK (and optionally TAK) from backend.
     * Keys are encrypted under TMK and decrypted by SDK.
     */
    private boolean provisionSessionKeysFromBackend(com.neo.neopayplus.emv.KeyManager keyManager) {
        PaymentApiService apiService = PaymentApiFactory.getInstance();
        if (!apiService.isAvailable()) {
            return false;
        }

        // Check network and register retry if unavailable
        com.neo.neopayplus.utils.NetworkMonitor networkMonitor = com.neo.neopayplus.utils.NetworkMonitor
                .getInstance(this);

        if (!networkMonitor.isNetworkAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Network unavailable - will retry TPK provisioning when network reconnects");
            networkMonitor.registerRetryOperation(new Runnable() {
                @Override
                public void run() {
                    if (emvOptV2 != null && securityOptV2 != null) {
                        com.neo.neopayplus.emv.KeyManager km = com.neo.neopayplus.app.di.ServiceLocator.keyManager();
                        provisionSessionKeysFromBackend(km);
                    }
                }
            });
            return false;
        }

        try {
            PaymentApiService.TpkProvisionRequest request = new PaymentApiService.TpkProvisionRequest();
            request.terminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId();
            PaymentApiService.TpkProvisionResponse response = apiService.provisionTpkSync(request);

            if (!response.success || response.wrappedTpk == null) {
                LogUtil.e(Constant.TAG, "⚠️ TPK provisioning failed: " + response.message);
                return false;
            }

            // TPK is hex-encoded and encrypted under TMK
            byte[] wrappedTpk = com.neo.neopayplus.utils.ByteUtil.hexStr2Bytes(response.wrappedTpk);
            String tpkKcv = response.tpkKcv != null ? response.tpkKcv : "";
            com.neo.neopayplus.emv.KeyManager.KeyStatus tpkStatus = keyManager.importWrappedTpk(wrappedTpk, tpkKcv);

            // Save key metadata (including wrapped TPK for testing/decryption)
            String pinKeyId = response.pinKeyId;
            if (pinKeyId == null || pinKeyId.isEmpty()) {
                KeyRegistry.KeyState current = KeyRegistry.current();
                pinKeyId = current.getPinKeyId();
            }

            // Log wrapped TPK for testing (encrypted under TMK - needs TMK to decrypt)
            LogUtil.e(Constant.TAG, "=== TPK Provisioning Details (for testing) ===");
            LogUtil.e(Constant.TAG, "✓ Wrapped TPK (encrypted under TMK): " + response.wrappedTpk);
            LogUtil.e(Constant.TAG, "✓ TPK KCV: " + tpkKcv);
            LogUtil.e(Constant.TAG, "✓ PIN Key ID: " + pinKeyId);
            LogUtil.e(Constant.TAG, "⚠️ SECURITY NOTE: Wrapped TPK is encrypted under TMK.");
            LogUtil.e(Constant.TAG, "  To decrypt: Use TMK to unwrap, then use TPK to decrypt PIN blocks.");

            KeyRegistry.save(new KeyRegistry.KeyState(
                    pinKeyId,
                    tpkStatus.getKcv(),
                    response.wrappedTpk, // Store wrapped TPK for testing
                    System.currentTimeMillis()));

            return true;
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "Backend session key provisioning", e);

            // Check if error is network-related and register retry
            if (e instanceof java.net.UnknownHostException ||
                    e instanceof java.net.ConnectException ||
                    e instanceof java.io.IOException) {
                LogUtil.e(Constant.TAG,
                        "⚠️ Network error detected - will retry TPK provisioning when network reconnects");
                com.neo.neopayplus.utils.NetworkMonitor.getInstance(this)
                        .registerRetryOperation(new Runnable() {
                            @Override
                            public void run() {
                                if (emvOptV2 != null && securityOptV2 != null) {
                                    com.neo.neopayplus.emv.KeyManager km = com.neo.neopayplus.app.di.ServiceLocator
                                            .keyManager();
                                    provisionSessionKeysFromBackend(km);
                                }
                            }
                        });
            }

            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        KeyRegistry.init(this);

        // CRITICAL: Initialize EMV service binding ONCE (singleton pattern)
        // This prevents repeated "new EmvManager" logs and ensures stable service
        // lifecycle
        com.neo.neopayplus.emv.TransactionManager.init(this);

        // Register callback to sync service instances and run provisioning when
        // services are ready
        com.neo.neopayplus.emv.TransactionManager.setOnServicesReadyCallback(new Runnable() {
            @Override
            public void run() {
                syncServiceInstances();
                runProvisioning();
            }
        });

        // Critical initialization that must happen on main thread
        initLocaleLanguage();
        bindPrintService();
        bindScannerService();

        // Defer heavy initialization to background thread to avoid blocking UI
        new Thread(() -> {
            try {
                // Load terminal config from cache (SharedPreferences can be slow on first
                // access)
                loadTerminalConfigFromCache();

                // Load ISO socket config from cache
                com.neo.neopayplus.config.PaymentConfig.loadIsoSocketConfigFromCache();

                // Sign-on is handled via PaymentApiService when needed
                com.neo.neopayplus.utils.LogUtil.e(Constant.TAG, "Host sign-on OK");

                // Initialize TTS (can be slow)
                initEmvTTS();

            } catch (Exception e) {
                com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "Background initialization", e);
            }
        }).start();

        // Initialize network monitor
        com.neo.neopayplus.utils.NetworkMonitor.getInstance(this).startMonitoring();

        // Fetch terminal config from backend (already async, uses callbacks)
        fetchTerminalConfig();

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
                    com.neo.neopayplus.utils.LogUtil.e(Constant.TAG,
                            "⚠️ PaySDK not connected after " + maxWait + " seconds - background ops may not work");
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
                                    com.neo.neopayplus.utils.LogUtil.e(Constant.TAG,
                                            "⚠️ Time drift detected: " + drift + " seconds");
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
                        com.neo.neopayplus.utils.LogUtil.e(Constant.TAG,
                                "Background operations error: " + e.getMessage());
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
                com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "Background operations thread", e);
            }
        }, "bg-ops").start();
    }

    /**
     * Initialize locale language for the application.
     * Note: This payment terminal app requires runtime locale changes to support
     * multiple languages.
     * Play Core library is not used as this is a standalone terminal application,
     * not distributed via Play Store.
     */
    public static void initLocaleLanguage() {
        Resources resources = app.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        int showLanguage = CacheHelper.getCurrentLanguage();
        Locale targetLocale;
        if (showLanguage == Constant.LANGUAGE_AUTO) {
            Locale systemLocale = Resources.getSystem().getConfiguration().getLocales().get(0);
            LogUtil.e(Constant.TAG, systemLocale.getCountry() + "---这是系统语言");
            targetLocale = systemLocale;
        } else if (showLanguage == Constant.LANGUAGE_ZH_CN) {
            LogUtil.e(Constant.TAG, "这是中文");
            targetLocale = Locale.SIMPLIFIED_CHINESE;
        } else if (showLanguage == Constant.LANGUAGE_EN_US) {
            LogUtil.e(Constant.TAG, "这是英文");
            targetLocale = Locale.ENGLISH;
        } else if (showLanguage == Constant.LANGUAGE_JA_JP) {
            LogUtil.e(Constant.TAG, "这是日文");
            targetLocale = Locale.JAPAN;
        } else {
            targetLocale = Locale.getDefault();
        }
        config.setLocale(targetLocale);
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtil.e(Constant.TAG, "onConfigurationChanged");
    }

    public boolean isConnectPaySDK() {
        // Use TransactionManager as source of truth
        return com.neo.neopayplus.emv.TransactionManager.isReady();
    }

    /**
     * Sync service instances from TransactionManager to MyApplication fields
     * (for backward compatibility with existing code that accesses
     * MyApplication.app.emvOptV2)
     * 
     * CRITICAL: This ensures MyApplication.app.emvOptV2 points to the SAME
     * singleton instance
     * from TransactionManager, following the SDK demo pattern where instances are
     * stored
     * in Application class fields and reused throughout the app.
     */
    private void syncServiceInstances() {
        try {
            // CRITICAL: Get singleton instances from TransactionManager (matches SDK demo
            // pattern)
            // Demo pattern: emvOptV2 = payKernel.mEMVOptV2 (in onConnectPaySDK callback)
            // Our pattern: emvOptV2 = TransactionManager.getEmv() (same instance, different
            // access)
            emvOptV2 = com.neo.neopayplus.emv.TransactionManager.getEmv();
            readCardOptV2 = com.neo.neopayplus.emv.TransactionManager.getReadCard();
            pinPadOptV2 = com.neo.neopayplus.emv.TransactionManager.getPinPad();
            securityOptV2 = com.neo.neopayplus.emv.TransactionManager.getSecurity();
            basicOptV2 = com.neo.neopayplus.emv.TransactionManager.getBasic();
            connectPaySDK = true;

            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
            LogUtil.e(Constant.TAG, "=== MyApplication: Service instances synced ===");
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
            LogUtil.e(Constant.TAG, "CRITICAL: MyApplication.app.emvOptV2 now points to singleton instance");
            LogUtil.e(Constant.TAG, "  emvOptV2 instance ID: " + System.identityHashCode(emvOptV2));
            LogUtil.e(Constant.TAG, "  readCardOptV2 instance ID: " + System.identityHashCode(readCardOptV2));
            LogUtil.e(Constant.TAG, "  pinPadOptV2 instance ID: " + System.identityHashCode(pinPadOptV2));
            LogUtil.e(Constant.TAG, "  securityOptV2 instance ID: " + System.identityHashCode(securityOptV2));
            LogUtil.e(Constant.TAG, "  basicOptV2 instance ID: " + System.identityHashCode(basicOptV2));
            LogUtil.e(Constant.TAG, "");
            LogUtil.e(Constant.TAG, "  ✓✓✓ SINGLE INSTANCE VERIFIED:");
            LogUtil.e(Constant.TAG, "     MyApplication.app.emvOptV2 == TransactionManager.getEmv()");
            LogUtil.e(Constant.TAG, "     (Same instance, different access pattern - matches SDK demo)");
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
        } catch (Exception e) {
            LogUtil.e(Constant.TAG, "❌ Failed to sync service instances: " + e.getMessage());
        }
    }

    /**
     * Run provisioning and terminal configuration (called when services are ready)
     * 
     * CRITICAL: This method MUST use the singleton EMVOptV2 instance from
     * TransactionManager.
     * It should only be called ONCE per app lifetime.
     */
    private static volatile boolean provisioningInProgress = false;
    private static volatile boolean provisioningCompleted = false;

    private void runProvisioning() {
        // CRITICAL: Prevent multiple concurrent provisioning calls
        synchronized (MyApplication.class) {
            if (provisioningInProgress) {
                LogUtil.e(Constant.TAG, "⚠️ Provisioning already in progress - skipping duplicate call");
                return;
            }
            if (provisioningCompleted) {
                LogUtil.e(Constant.TAG, "⚠️ Provisioning already completed - skipping duplicate call");
                return;
            }
            provisioningInProgress = true;
        }

        // This runs the old provisioning logic from onConnectPaySDK callback
        // but now it's triggered by TransactionManager when services are ready
        new Thread(() -> {
            try {
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
                LogUtil.e(Constant.TAG, "=== Starting EMV provisioning (ONCE) ===");
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
                LogUtil.e(Constant.TAG, "CRITICAL: This should happen ONLY ONCE per app lifetime");
                LogUtil.e(Constant.TAG, "");

                // CRITICAL: Get singleton instance from TransactionManager (single source of
                // truth)
                com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2 emvSingleton = com.neo.neopayplus.emv.TransactionManager
                        .getEmv();
                com.sunmi.pay.hardware.aidlv2.system.BasicOptV2 basicSingleton = com.neo.neopayplus.emv.TransactionManager
                        .getBasic();
                com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2 securitySingleton = com.neo.neopayplus.emv.TransactionManager
                        .getSecurity();

                if (emvSingleton == null || basicSingleton == null) {
                    LogUtil.e(Constant.TAG, "⚠️ Services not available for provisioning");
                    synchronized (MyApplication.class) {
                        provisioningInProgress = false;
                    }
                    return;
                }

                LogUtil.e(Constant.TAG, "✓ Using singleton EMV instance from TransactionManager:");
                LogUtil.e(Constant.TAG, "  - emv instance ID: " + System.identityHashCode(emvSingleton));
                LogUtil.e(Constant.TAG, "  - basic instance ID: " + System.identityHashCode(basicSingleton));
                LogUtil.e(Constant.TAG, "  - security instance ID: " + System.identityHashCode(securitySingleton));
                LogUtil.e(Constant.TAG, "");

                // Wait a bit for services to fully initialize
                Thread.sleep(500);

                // Terminal params and kernel TLVs
                try {
                    android.os.Bundle termParams = com.neo.neopayplus.emv.config.EmvKernelConfig
                            .buildTerminalParamsEgypt();

                    boolean supportNFC = termParams.getBoolean("supportNFC", false);
                    boolean supportClss = termParams.getBoolean("supportClss", false);
                    LogUtil.e(Constant.TAG, "=== VERIFYING TERMINAL PARAMETERS ===");
                    LogUtil.e(Constant.TAG, "  supportNFC: " + supportNFC + (supportNFC ? " ✓" : " ❌ REQUIRED!"));
                    LogUtil.e(Constant.TAG, "  supportClss: " + supportClss + (supportClss ? " ✓" : " ❌ REQUIRED!"));

                    emvSingleton.setTermParamEx(termParams);
                    LogUtil.e(Constant.TAG, "✓ Terminal parameters applied (using singleton instance)");

                    com.neo.neopayplus.emv.config.EmvKernelConfig.applyContactlessKernelConfig(emvSingleton);
                    LogUtil.e(Constant.TAG, "✓ Contactless kernel TLVs applied (using singleton instance)");

                    com.neo.neopayplus.emv.EmvKernelDebug.validateAllKernels(emvSingleton);
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "❌ Failed to apply kernel TLVs: " + e.getMessage());
                }

                // Provision AIDs/CAPKs from backend
                // CRITICAL: Use singleton instance to ensure AIDs are added to the same EMV
                // service
                try {
                    String bundleUrl = BuildConfig.API_BASE_URL + "/emv/bundle";
                    LogUtil.e(Constant.TAG, "=== EMV Provisioning from: " + bundleUrl + " ===");
                    LogUtil.e(Constant.TAG,
                            "  Using singleton emv instance ID: " + System.identityHashCode(emvSingleton));
                    LogUtil.e(Constant.TAG, "  CRITICAL: All AIDs will be added to this SAME instance");

                    boolean success = com.neo.neopayplus.emv.EmvProvisioner.provisionSync(emvSingleton, bundleUrl);
                    if (success) {
                        LogUtil.e(Constant.TAG, "✓ EMV AIDs/CAPKs provisioned (using singleton instance)");
                    } else {
                        throw new Exception("Provisioning returned false");
                    }
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "⚠️ EMV provisioning failed: " + e.getMessage());
                }

                // Validate AID/CAPK configuration
                try {
                    com.neo.neopayplus.emv.config.EmvKernelConfig.provisionAndValidate();
                    LogUtil.e(Constant.TAG, "✓ EMV configuration validated");
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG, "⚠️ EMV validation failed: " + e.getMessage());
                }

                // Initialize keys (TMK/TPK provisioning from backend)
                initializeMasterSessionKeys(securitySingleton);

                LogUtil.e(Constant.TAG, "");
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");
                LogUtil.e(Constant.TAG, "=== EMV provisioning complete (ONCE) ===");
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════");

                synchronized (MyApplication.class) {
                    provisioningCompleted = true;
                    provisioningInProgress = false;
                }
            } catch (Exception e) {
                LogUtil.e(Constant.TAG, "❌ Error in provisioning thread: " + e.getMessage());
                e.printStackTrace();
                synchronized (MyApplication.class) {
                    provisioningInProgress = false;
                }
            }
        }).start();
    }

    /**
     * bind PaySDK service
     * 
     * DEPRECATED: Use TransactionManager.init() instead.
     * This method is kept for backward compatibility but delegates to
     * TransactionManager.
     * 
     * CRITICAL: Do NOT call this from Activities or Fragments.
     * Service binding should happen ONCE in Application.onCreate() via
     * TransactionManager.
     */
    @Deprecated
    public void bindPaySDKService() {
        // Delegate to TransactionManager to prevent multiple bindings
        if (!com.neo.neopayplus.emv.TransactionManager.isReady()) {
            LogUtil.e(Constant.TAG, "⚠️ bindPaySDKService() called but service not ready");
            LogUtil.e(Constant.TAG,
                    "   This should not happen - TransactionManager.init() should be called in Application.onCreate()");
            LogUtil.e(Constant.TAG, "   Attempting to initialize now (this is a fallback, not recommended)");
            com.neo.neopayplus.emv.TransactionManager.init(this);
        } else {
            LogUtil.e(Constant.TAG, "✓ bindPaySDKService() called but service already ready (ignoring duplicate call)");
        }
    }

    /**
     * bind printer service
     */

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
            com.neo.neopayplus.utils.ErrorHandler.logError(Constant.TAG, "MyApplication printer initialization", e);
        }
    }

    /**
     * bind scanner service
     */
    public void bindScannerService() {
        // Scanner service binding - commented out if IScanInterface not available
        // Intent intent = new Intent();
        // intent.setPackage("com.sunmi.scanner");
        // intent.setAction("com.sunmi.scanner.IScanInterface");
        // bindService(intent, new ServiceConnection() {
        // @Override
        // public void onServiceConnected(ComponentName name, IBinder service) {
        // scanInterface = IScanInterface.Stub.asInterface(service);
        // }
        //
        // @Override
        // public void onServiceDisconnected(ComponentName name) {
        // scanInterface = null;
        // }
        // }, Service.BIND_AUTO_CREATE);
    }

    private void initEmvTTS() {
        EmvTTS.getInstance().init();
    }

    /**
     * Initialize TMK/TPK keys from backend at terminal startup.
     * Flow:
     * 1. Register terminal's RSA public key with backend
     * 2. Request TMK (encrypted with terminal's public key)
     * 3. Decrypt TMK using Android Keystore and inject into secure element
     * 4. Request TPK (wrapped under TMK) and inject into secure element
     * 
     * @param securityOptV2 SecurityOptV2 instance for key injection
     */
    private void initializeMasterSessionKeys(SecurityOptV2 securityOptV2) {
        LogUtil.e(Constant.TAG, "=== INITIALIZING TMK/TPK KEYS ===");
        LogUtil.e(Constant.TAG, "  Terminal ID: " + com.neo.neopayplus.config.PaymentConfig.getTerminalId());

        com.neo.neopayplus.emv.KeyManager keyManager = com.neo.neopayplus.app.di.ServiceLocator.keyManager();

        // Step 1: Ensure TMK is installed (registers public key + provisions TMK)
        if (!ensureTmkInstalled(keyManager)) {
            LogUtil.e(Constant.TAG, "❌ Unable to install TMK via backend - aborting key init");
            return;
        }

        // Step 2: Provision TPK (wrapped under TMK)
        if (!provisionSessionKeysFromBackend(keyManager)) {
            LogUtil.e(Constant.TAG, "⚠️ TPK provisioning failed - transactions requiring PIN encryption may fail");
        }

        LogUtil.e(Constant.TAG, "=== KEY INITIALIZATION COMPLETE ===");
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
        LogUtil.e(Constant.TAG, "  Endpoint: GET /terminal/config");

        EmvConfigApiService configApi = EmvConfigApiFactory.getInstance();

        if (!configApi.isAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Config API not available - using cached/default config");
            if (terminalConfig == null) {
                terminalConfig = new TerminalConfig();
            }
            return;
        }

        // Check network and register retry if unavailable
        com.neo.neopayplus.utils.NetworkMonitor networkMonitor = com.neo.neopayplus.utils.NetworkMonitor
                .getInstance(this);

        if (!networkMonitor.isNetworkAvailable()) {
            LogUtil.e(Constant.TAG, "⚠️ Network unavailable - will retry config fetch when network reconnects");
            networkMonitor.registerRetryOperation(new Runnable() {
                @Override
                public void run() {
                    fetchTerminalConfig();
                }
            });
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

                    String currencyCode = response.currencyCode != null ? response.currencyCode
                            : (terminalConfig != null ? terminalConfig.currencyCode
                                    : com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE);

                    // Validate currency code is numeric (3 digits) - if API returns "EGP" or
                    // invalid, use default
                    if (currencyCode != null && !currencyCode.matches("\\d{3,4}")) {
                        LogUtil.e(Constant.TAG,
                                "⚠️ API returned non-numeric currency code: " + currencyCode + ", using default: 818");
                        currencyCode = com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE; // "818"
                    }

                    // Get terminal ID from config (or from response if available)
                    String terminalId = com.neo.neopayplus.config.PaymentConfig.TERMINAL_ID;
                    String merchantId = com.neo.neopayplus.config.PaymentConfig.MERCHANT_ID;

                    // Update terminal config (constructor will also validate)
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

                // Check if error is network-related and register retry
                if (error instanceof java.net.UnknownHostException ||
                        error instanceof java.net.ConnectException ||
                        error instanceof java.io.IOException) {
                    LogUtil.e(Constant.TAG, "⚠️ Network error detected - will retry when network reconnects");
                    com.neo.neopayplus.utils.NetworkMonitor.getInstance(MyApplication.this)
                            .registerRetryOperation(() -> fetchTerminalConfig());
                }

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
