package sunmi.paylib;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import com.sunmi.pay.hardware.aidl.DeviceProvide;
import com.sunmi.pay.hardware.aidl.emv.EMVOpt;
import com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt;
import com.sunmi.pay.hardware.aidl.print.PrinterOpt;
import com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt;
import com.sunmi.pay.hardware.aidl.security.SecurityOpt;
import com.sunmi.pay.hardware.aidl.system.BasicOpt;
import com.sunmi.pay.hardware.aidl.tax.TaxOpt;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2;
import com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.jar:sunmi/paylib/SunmiPayKernel.class */
public class SunmiPayKernel {
    private static final String TAG = "SunmiPayKernel";
    public static final int EMV_WAY_SPHS = 0;
    public static final int EMV_WAY_L2SPLIT = 1;

    @Deprecated
    public BasicOpt mBasicOpt;

    @Deprecated
    public ReadCardOpt mReadCardOpt;

    @Deprecated
    public PinPadOpt mPinPadOpt;

    @Deprecated
    public EMVOpt mEMVOpt;

    @Deprecated
    public SecurityOpt mSecurityOpt;

    @Deprecated
    public PrinterOpt mPrinterOpt;

    @Deprecated
    public TaxOpt mTaxOpt;
    public BasicOptV2 mBasicOptV2;
    public ReadCardOptV2 mReadCardOptV2;
    public PinPadOptV2 mPinPadOptV2;
    public EMVOptV2 mEMVOptV2;
    public SecurityOptV2 mSecurityOptV2;
    public PrinterOptV2 mPrinterOptV2;
    public TaxOptV2 mTaxOptV2;
    public ETCOptV2 mETCOptV2;
    public TestOptV2 mTestOptV2;
    public DevCertManagerV2 mDevCertManagerV2;
    public NoLostKeyManagerV2 mNoLostKeyManagerV2;
    public BiometricManagerV2 mBiometricManagerV2;
    public HCEManagerV2Wrapper mHCEManagerV2Wrapper;
    public RFIDOptV2 mRFIDOptV2;
    private ConnCallback mConnCallback;
    private volatile Context appContext;
    private volatile boolean connectedPaySdk;

    @SuppressLint({"StaticFieldLeak"})
    private static final SunmiPayKernel INSTANCE = new SunmiPayKernel();
    private final Map<ConnectCallback, Boolean> callbackMap = new LinkedHashMap();
    private volatile boolean isBind = false;
    private volatile boolean emvl2Split = true;
    private volatile int emvWay = 0;
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: sunmi.paylib.SunmiPayKernel.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                DeviceProvide provider = DeviceProvide.Stub.asInterface(service);
                if (!setBinder(provider)) {
                    return;
                }
                SunmiPayKernel.this.mBasicOpt = (BasicOpt) BasicOpt.class.cast(provider.getBasicOpt());
                SunmiPayKernel.this.mReadCardOpt = (ReadCardOpt) ReadCardOpt.class.cast(provider.getReadCardOpt());
                SunmiPayKernel.this.mPinPadOpt = (PinPadOpt) PinPadOpt.class.cast(provider.getPinPadOpt());
                SunmiPayKernel.this.mEMVOpt = (EMVOpt) EMVOpt.class.cast(provider.getEMVOpt());
                SunmiPayKernel.this.mSecurityOpt = (SecurityOpt) SecurityOpt.class.cast(provider.getSecurityOpt());
                SunmiPayKernel.this.mPrinterOpt = (PrinterOpt) PrinterOpt.class.cast(provider.getPrinterOpt());
                SunmiPayKernel.this.mTaxOpt = (TaxOpt) TaxOpt.class.cast(provider.getTaxOpt());
                SunmiPayKernel.this.mBasicOptV2 = getBasicOptV2(provider);
                SunmiPayKernel.this.mReadCardOptV2 = (ReadCardOptV2) ReadCardOptV2.class.cast(provider.getReadCardOptV2());
                SunmiPayKernel.this.mPinPadOptV2 = (PinPadOptV2) PinPadOptV2.class.cast(provider.getPinPadOptV2());
                SunmiPayKernel.this.mEMVOptV2 = getEmvOptV2(provider);
                SunmiPayKernel.this.mSecurityOptV2 = (SecurityOptV2) SecurityOptV2.class.cast(provider.getSecurityOptV2());
                SunmiPayKernel.this.mPrinterOptV2 = (PrinterOptV2) PrinterOptV2.class.cast(provider.getPrinterOptV2());
                SunmiPayKernel.this.mTaxOptV2 = (TaxOptV2) TaxOptV2.class.cast(provider.getTaxOptV2());
                SunmiPayKernel.this.mETCOptV2 = (ETCOptV2) ETCOptV2.class.cast(provider.getETCOptV2());
                SunmiPayKernel.this.mTestOptV2 = (TestOptV2) TestOptV2.class.cast(provider.getTestOptV2());
                SunmiPayKernel.this.mDevCertManagerV2 = (DevCertManagerV2) DevCertManagerV2.class.cast(provider.getDevCertManagerV2());
                SunmiPayKernel.this.mNoLostKeyManagerV2 = NoLostKeyManagerV2.Stub.asInterface(provider.getOptBinderV2("NoLostKeyManagerV2"));
                SunmiPayKernel.this.mBiometricManagerV2 = BiometricManagerV2.Stub.asInterface(provider.getOptBinderV2("BiometricManagerV2"));
                SunmiPayKernel.this.mHCEManagerV2Wrapper = getHceManagerWrapper(provider);
                SunmiPayKernel.this.mRFIDOptV2 = RFIDOptV2.Stub.asInterface(provider.getOptBinderV2("RFIDOptV2"));
                setClientParam();
                SunmiPayKernel.this.connectedPaySdk = true;
                SunmiPayKernel.this.notifyConnectedPaySDK();
                if (SunmiPayKernel.this.mConnCallback != null) {
                    SunmiPayKernel.this.mConnCallback.onServiceConnected();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(SunmiPayKernel.TAG, "bind SunmiPayHardwareService exception:" + e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            SunmiPayKernel.this.connectedPaySdk = false;
            SunmiPayKernel.this.notifyDisConnectedPaySDK();
            if (SunmiPayKernel.this.mConnCallback != null) {
                SunmiPayKernel.this.mConnCallback.onServiceDisconnected();
            }
        }

        private boolean setBinder(DeviceProvide provider) {
            try {
                return provider.setBinder(new Binder()) >= 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void setClientParam() {
            try {
                Bundle bundle = new Bundle();
                bundle.putInt("payLibVersionCode", 272);
                bundle.putString("payLibVersionName", "2.0.32");
                SunmiPayKernel.this.mTestOptV2.setParam(bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private BasicOptV2 getBasicOptV2(DeviceProvide provider) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
            try {
                BasicOptV2 proxy = (BasicOptV2) BasicOptV2.class.cast(provider.getBasicOptV2());
                if (SunmiPayKernel.this.emvl2Split && SunmiPayKernel.this.checkSupportEmvl2Split()) {
                    Class<?> cls = Class.forName("com.sunmi.emv.l2.basic.Basicl2Splitter");
                    Method m = cls.getDeclaredMethod("getInstance", BasicOptV2.class);
                    return (BasicOptV2) m.invoke(null, proxy);
                }
                return proxy;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private EMVOptV2 getEmvOptV2(DeviceProvide provider) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
            try {
                SunmiPayKernel.this.emvWay = 0;
                EMVOptV2 proxy = (EMVOptV2) EMVOptV2.class.cast(provider.getEMVOptV2());
                if (SunmiPayKernel.this.emvl2Split && SunmiPayKernel.this.checkSupportEmvl2Split()) {
                    Class<?> cls = Class.forName("com.sunmi.emv.l2.emv.Emvl2Splitter");
                    Method m1 = cls.getDeclaredMethod("getInstance", EMVOptV2.class);
                    Method m2 = cls.getDeclaredMethod("initEmvl2Split", (Class[]) null);
                    EMVOptV2 instance = (EMVOptV2) m1.invoke(null, proxy);
                    m2.invoke(instance, (Object[]) null);
                    SunmiPayKernel.this.emvWay = 1;
                    return instance;
                }
                return proxy;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private HCEManagerV2Wrapper getHceManagerWrapper(DeviceProvide provider) {
            try {
                HCEManagerV2 proxy = HCEManagerV2.Stub.asInterface(provider.getOptBinderV2("HCEManagerV2"));
                return new HCEManagerV2Wrapper(proxy);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    @Deprecated
    /* loaded from: classes.jar:sunmi/paylib/SunmiPayKernel$ConnCallback.class */
    public interface ConnCallback {
        void onServiceConnected();

        void onServiceDisconnected();
    }

    /* loaded from: classes.jar:sunmi/paylib/SunmiPayKernel$ConnectCallback.class */
    public interface ConnectCallback {
        void onConnectPaySDK();

        void onDisconnectPaySDK();
    }

    private SunmiPayKernel() {
    }

    public static SunmiPayKernel getInstance() {
        return INSTANCE;
    }

    public Context getAppContext() {
        return this.appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    public String getPayLibVersion() {
        return "2.0.32";
    }

    public void setEmvL2Split(boolean enable) {
        this.emvl2Split = enable;
    }

    public boolean getEmvl2split() {
        return this.emvl2Split;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x003b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean checkSupportEmvl2Split() {
        boolean result = false;
        try {
        } catch (Throwable t) {
            Log.e(TAG, "checkSupportEmvl2Split()->failed:" + t);
        }
        if (getPaySDKVersionCode() >= 50000) {
            Class<?> cls = Class.forName("com.sunmi.emv.l2.emv.Emvl2Splitter");
            Method m = cls.getDeclaredMethod("checkROMPermission", new Class[0]);
            Boolean ret = (Boolean) m.invoke(null, (Object[]) null);
            if (ret != null) {
                boolean z = ret.booleanValue();
                result = z;
            }
            Log.e(TAG, "checkSupportEmvl2Split()->result:" + result);
            return result;
        }
        Log.e(TAG, "checkSupportEmvl2Split()->result:" + result);
        return result;
    }

    public int getCurrentEmvWay() {
        return this.emvWay;
    }

    public String getMatchedPaySDKVersion() throws PackageManager.NameNotFoundException {
        try {
            if (this.appContext == null) {
                return null;
            }
            PackageManager pkgMgr = this.appContext.getPackageManager();
            PackageInfo info = pkgMgr.getPackageInfo("com.sunmi.pay.hardware_v3", 0);
            if (info == null || TextUtils.isEmpty(info.versionName)) {
                return null;
            }
            if (info.versionName.startsWith("v5")) {
                return "v5.0.42";
            }
            return "v3.3.341";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public void connectPayService(Context context, ConnCallback connCallback) throws PackageManager.NameNotFoundException {
        this.mConnCallback = connCallback;
        Intent intent = new Intent("sunmi.intent.action.PAY_HARDWARE");
        intent.setPackage("com.sunmi.pay.hardware_v3");
        this.appContext = context.getApplicationContext();
        PackageManager pkgManager = this.appContext.getPackageManager();
        List<ResolveInfo> infos = pkgManager.queryIntentServices(intent, 0);
        if (infos != null && !infos.isEmpty()) {
            checkPayHardwareServiceVersion();
            this.appContext.startService(intent);
            this.isBind = this.appContext.bindService(intent, this.mServiceConnection, 4);
            return;
        }
        Log.e(TAG, "bind PayHardwareService failed: service not found");
    }

    public boolean initPaySDK(Context context, ConnectCallback callback) throws PackageManager.NameNotFoundException {
        addConnectCallback(callback);
        Intent intent = new Intent("sunmi.intent.action.PAY_HARDWARE");
        intent.setPackage("com.sunmi.pay.hardware_v3");
        this.appContext = context.getApplicationContext();
        PackageManager pkgManager = this.appContext.getPackageManager();
        List<ResolveInfo> infos = pkgManager.queryIntentServices(intent, 0);
        if (infos == null || infos.isEmpty()) {
            this.isBind = false;
            Log.e(TAG, "bind PayHardwareService failed: service not found");
        } else if (this.connectedPaySdk) {
            this.isBind = true;
            notifyConnectedPaySDK();
        } else {
            checkPayHardwareServiceVersion();
            this.appContext.startService(intent);
            this.isBind = this.appContext.bindService(intent, this.mServiceConnection, 4);
        }
        return this.isBind;
    }

    @Deprecated
    public void unbindPayService(Context context) {
        if (this.isBind) {
            context.getApplicationContext().unbindService(this.mServiceConnection);
            this.isBind = false;
        }
    }

    public void destroyPaySDK() {
        if (this.isBind) {
            this.appContext.unbindService(this.mServiceConnection);
            this.isBind = false;
        }
    }

    public synchronized void removeConnectCallback(ConnectCallback callback) {
        if (callback != null) {
            this.callbackMap.remove(callback);
        }
    }

    private synchronized void addConnectCallback(ConnectCallback callback) {
        if (callback != null) {
            this.callbackMap.put(callback, this.callbackMap.get(callback));
        }
    }

    private void checkPayHardwareServiceVersion() throws PackageManager.NameNotFoundException {
        try {
            PackageManager pkgMgr = this.appContext.getPackageManager();
            PackageInfo info = pkgMgr.getPackageInfo("com.sunmi.pay.hardware_v3", 0);
            int versionCode = info.versionCode;
            String versionName = info.versionName;
            Log.e(TAG, "PayHardwareService pkg info: versionCode:" + versionCode + ",versionName:" + versionName);
            if (versionCode < 1000) {
                Log.e(TAG, "Low PayHardwareService version, please upgrade to v3.3.300+ version");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void notifyConnectedPaySDK() {
        for (Map.Entry<ConnectCallback, Boolean> e : this.callbackMap.entrySet()) {
            if (e.getValue() == null || !e.getValue().booleanValue()) {
                e.getKey().onConnectPaySDK();
                this.callbackMap.put(e.getKey(), true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void notifyDisConnectedPaySDK() {
        for (ConnectCallback callback : this.callbackMap.keySet()) {
            callback.onDisconnectPaySDK();
        }
        this.callbackMap.clear();
    }

    private int getPaySDKVersionCode() throws PackageManager.NameNotFoundException {
        try {
            if (this.appContext != null) {
                PackageInfo pkgInfo = this.appContext.getPackageManager().getPackageInfo("com.sunmi.pay.hardware_v3", 0);
                return pkgInfo.versionCode;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Deprecated
    public static void screenMonopoly(Window window) {
        UIUtils.screenMonopoly(window);
    }

    @Deprecated
    public static void screenMonopoly(Dialog dialog) {
        UIUtils.screenMonopoly(dialog);
    }
}
