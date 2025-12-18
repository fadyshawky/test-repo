package com.sunmi.pay.hardware.aidlv2.system;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/system/BasicOptV2.class */
public interface BasicOptV2 extends IInterface {
    String getSysParam(String str) throws RemoteException;

    int setSysParam(String str, String str2) throws RemoteException;

    void buzzerOnDevice(int i, int i2, int i3, int i4) throws RemoteException;

    int ledStatusOnDevice(int i, int i2) throws RemoteException;

    int setScreenMode(int i) throws RemoteException;

    int sysGetRandom(byte[] bArr, int i) throws RemoteException;

    int ledStatusOnDeviceEx(int i, int i2, int i3, int i4) throws RemoteException;

    int setStatusBarDropDownMode(int i) throws RemoteException;

    int setNavigationBarVisibility(int i) throws RemoteException;

    int setHideNavigationBarItems(int i) throws RemoteException;

    int sysPowerManage(int i) throws RemoteException;

    int allowDynamicPermission(String str) throws RemoteException;

    int setGlobalProxy(String str) throws RemoteException;

    int installApplicationCertificate(String str, String str2) throws RemoteException;

    int uninstallApplicationCertificate(String str) throws RemoteException;

    String getCpuUsage() throws RemoteException;

    String getCpuTemperature() throws RemoteException;

    int setScheduleReboot(int i, int i2, int i3, int i4) throws RemoteException;

    int clearScheduleReboot() throws RemoteException;

    int customizeFunctionKey(Bundle bundle) throws RemoteException;

    int setLMKPackage(String str) throws RemoteException;

    int removeLMKPackage(String str) throws RemoteException;

    int sysSetWakeup(int i, int i2, Bundle bundle) throws RemoteException;

    int setPreferredNetworkMode(int i, int i2) throws RemoteException;

    String getSupportedNetworkType(int i) throws RemoteException;

    int setAirplaneMode(boolean z) throws RemoteException;

    int setDataRoamingEnable(int i, boolean z) throws RemoteException;

    int enablePhoneCall(boolean z) throws RemoteException;

    int getCardUsageCount(int i, boolean z) throws RemoteException;

    int getModuleAccessibility(int i) throws RemoteException;

    int setModuleAccessibility(int i, int i2) throws RemoteException;

    int getPedMode() throws RemoteException;

    int setPedMode(int i) throws RemoteException;

    int getPedKeysInfo(Bundle bundle) throws RemoteException;

    int installSharedLib(String str) throws RemoteException;

    int deleteSharedLib(String str) throws RemoteException;

    int litesoInstaller(int i, String str) throws RemoteException;

    int litesoRun(int i) throws RemoteException;

    int litesoInfo(int i, Bundle bundle) throws RemoteException;

    int litesoRunInfo(Bundle bundle) throws RemoteException;

    int litesoCustomCmd(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int litesoRemove(int i) throws RemoteException;

    int sysGetDebugData(byte[] bArr, int i) throws RemoteException;

    int sysPutDebugData(byte[] bArr, int i) throws RemoteException;

    int getRtcBatVol(Bundle bundle) throws RemoteException;

    int readPuk(int i, Bundle bundle) throws RemoteException;

    int logControl(Bundle bundle) throws RemoteException;

    int setCardWaterMarkAlpha(int i, float f) throws RemoteException;

    float getCardWaterMarkAlpha(int i) throws RemoteException;

    int getCardWaterMarkLocation(int i, int i2, Bundle bundle) throws RemoteException;

    int sysCitTest(int i, Bundle bundle) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/system/BasicOptV2$Default.class */
    public static class Default implements BasicOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public String getSysParam(String key) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setSysParam(String key, String value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public void buzzerOnDevice(int count, int freq, int duration, int interval) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int ledStatusOnDevice(int ledIndex, int ledStatus) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setScreenMode(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysGetRandom(byte[] randData, int len) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int ledStatusOnDeviceEx(int redStatus, int greenStatus, int yellowStatus, int blueStatus) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setStatusBarDropDownMode(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setNavigationBarVisibility(int visibility) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setHideNavigationBarItems(int flag) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysPowerManage(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int allowDynamicPermission(String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setGlobalProxy(String proxy) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int installApplicationCertificate(String name, String contents) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int uninstallApplicationCertificate(String name) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public String getCpuUsage() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public String getCpuTemperature() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setScheduleReboot(int hour, int minute, int second, int millisecond) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int clearScheduleReboot() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int customizeFunctionKey(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setLMKPackage(String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int removeLMKPackage(String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysSetWakeup(int channel, int mode, Bundle attr) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setPreferredNetworkMode(int mode, int slotIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public String getSupportedNetworkType(int slotIndex) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setAirplaneMode(boolean enable) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setDataRoamingEnable(int slotIndex, boolean enable) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int enablePhoneCall(boolean enable) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getCardUsageCount(int cardType, boolean isSuccess) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getModuleAccessibility(int module) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setModuleAccessibility(int module, int ability) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getPedMode() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setPedMode(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getPedKeysInfo(Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int installSharedLib(String path) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int deleteSharedLib(String name) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoInstaller(int index, String filePath) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoRun(int index) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoInfo(int index, Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoRunInfo(Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoCustomCmd(int cmd, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int litesoRemove(int index) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysGetDebugData(byte[] data, int len) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysPutDebugData(byte[] data, int len) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getRtcBatVol(Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int readPuk(int pukType, Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int logControl(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int setCardWaterMarkAlpha(int waterMarkIndex, float alpha) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public float getCardWaterMarkAlpha(int waterMarkIndex) throws RemoteException {
            return 0.0f;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int getCardWaterMarkLocation(int waterMarkIndex, int rotation, Bundle info) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
        public int sysCitTest(int testId, Bundle info) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/system/BasicOptV2$Stub.class */
    public static abstract class Stub extends Binder implements BasicOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.system.BasicOptV2";
        static final int TRANSACTION_getSysParam = 1;
        static final int TRANSACTION_setSysParam = 2;
        static final int TRANSACTION_buzzerOnDevice = 3;
        static final int TRANSACTION_ledStatusOnDevice = 4;
        static final int TRANSACTION_setScreenMode = 5;
        static final int TRANSACTION_sysGetRandom = 6;
        static final int TRANSACTION_ledStatusOnDeviceEx = 7;
        static final int TRANSACTION_setStatusBarDropDownMode = 8;
        static final int TRANSACTION_setNavigationBarVisibility = 9;
        static final int TRANSACTION_setHideNavigationBarItems = 10;
        static final int TRANSACTION_sysPowerManage = 11;
        static final int TRANSACTION_allowDynamicPermission = 12;
        static final int TRANSACTION_setGlobalProxy = 13;
        static final int TRANSACTION_installApplicationCertificate = 14;
        static final int TRANSACTION_uninstallApplicationCertificate = 15;
        static final int TRANSACTION_getCpuUsage = 16;
        static final int TRANSACTION_getCpuTemperature = 17;
        static final int TRANSACTION_setScheduleReboot = 18;
        static final int TRANSACTION_clearScheduleReboot = 19;
        static final int TRANSACTION_customizeFunctionKey = 20;
        static final int TRANSACTION_setLMKPackage = 21;
        static final int TRANSACTION_removeLMKPackage = 22;
        static final int TRANSACTION_sysSetWakeup = 23;
        static final int TRANSACTION_setPreferredNetworkMode = 24;
        static final int TRANSACTION_getSupportedNetworkType = 25;
        static final int TRANSACTION_setAirplaneMode = 26;
        static final int TRANSACTION_setDataRoamingEnable = 27;
        static final int TRANSACTION_enablePhoneCall = 28;
        static final int TRANSACTION_getCardUsageCount = 29;
        static final int TRANSACTION_getModuleAccessibility = 30;
        static final int TRANSACTION_setModuleAccessibility = 31;
        static final int TRANSACTION_getPedMode = 32;
        static final int TRANSACTION_setPedMode = 33;
        static final int TRANSACTION_getPedKeysInfo = 34;
        static final int TRANSACTION_installSharedLib = 35;
        static final int TRANSACTION_deleteSharedLib = 36;
        static final int TRANSACTION_litesoInstaller = 37;
        static final int TRANSACTION_litesoRun = 38;
        static final int TRANSACTION_litesoInfo = 39;
        static final int TRANSACTION_litesoRunInfo = 40;
        static final int TRANSACTION_litesoCustomCmd = 41;
        static final int TRANSACTION_litesoRemove = 42;
        static final int TRANSACTION_sysGetDebugData = 43;
        static final int TRANSACTION_sysPutDebugData = 44;
        static final int TRANSACTION_getRtcBatVol = 45;
        static final int TRANSACTION_readPuk = 46;
        static final int TRANSACTION_logControl = 47;
        static final int TRANSACTION_setCardWaterMarkAlpha = 48;
        static final int TRANSACTION_getCardWaterMarkAlpha = 49;
        static final int TRANSACTION_getCardWaterMarkLocation = 50;
        static final int TRANSACTION_sysCitTest = 51;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static BasicOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof BasicOptV2)) {
                return (BasicOptV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg0;
            byte[] _arg2;
            Bundle _arg22;
            Bundle _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    String _result = getSysParam(_arg03);
                    reply.writeNoException();
                    reply.writeString(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    int _result2 = setSysParam(_arg04, data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg1 = data.readInt();
                    int _arg23 = data.readInt();
                    int _arg3 = data.readInt();
                    buzzerOnDevice(_arg05, _arg1, _arg23, _arg3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _result3 = ledStatusOnDevice(_arg06, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _result4 = setScreenMode(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg08 = data.createByteArray();
                    int _result5 = sysGetRandom(_arg08, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    reply.writeByteArray(_arg08);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _arg12 = data.readInt();
                    int _arg24 = data.readInt();
                    int _arg32 = data.readInt();
                    int _result6 = ledStatusOnDeviceEx(_arg09, _arg12, _arg24, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _result7 = setStatusBarDropDownMode(_arg010);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _result8 = setNavigationBarVisibility(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _result9 = setHideNavigationBarItems(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _result10 = sysPowerManage(_arg013);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg014 = data.readString();
                    int _result11 = allowDynamicPermission(_arg014);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg015 = data.readString();
                    int _result12 = setGlobalProxy(_arg015);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg016 = data.readString();
                    int _result13 = installApplicationCertificate(_arg016, data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg017 = data.readString();
                    int _result14 = uninstallApplicationCertificate(_arg017);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    break;
                case TRANSACTION_getCpuUsage /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result15 = getCpuUsage();
                    reply.writeNoException();
                    reply.writeString(_result15);
                    break;
                case TRANSACTION_getCpuTemperature /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result16 = getCpuTemperature();
                    reply.writeNoException();
                    reply.writeString(_result16);
                    break;
                case TRANSACTION_setScheduleReboot /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    int _arg13 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg33 = data.readInt();
                    int _result17 = setScheduleReboot(_arg018, _arg13, _arg25, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_clearScheduleReboot /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result18 = clearScheduleReboot();
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    break;
                case TRANSACTION_customizeFunctionKey /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result19 = customizeFunctionKey(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    break;
                case TRANSACTION_setLMKPackage /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg019 = data.readString();
                    int _result20 = setLMKPackage(_arg019);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    break;
                case TRANSACTION_removeLMKPackage /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg020 = data.readString();
                    int _result21 = removeLMKPackage(_arg020);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    break;
                case TRANSACTION_sysSetWakeup /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    int _arg14 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg22 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    int _result22 = sysSetWakeup(_arg021, _arg14, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    break;
                case TRANSACTION_setPreferredNetworkMode /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    int _result23 = setPreferredNetworkMode(_arg022, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    break;
                case TRANSACTION_getSupportedNetworkType /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    String _result24 = getSupportedNetworkType(_arg023);
                    reply.writeNoException();
                    reply.writeString(_result24);
                    break;
                case TRANSACTION_setAirplaneMode /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg024 = 0 != data.readInt();
                    int _result25 = setAirplaneMode(_arg024);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    break;
                case TRANSACTION_setDataRoamingEnable /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    int _result26 = setDataRoamingEnable(_arg025, 0 != data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    break;
                case TRANSACTION_enablePhoneCall /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _arg026 = 0 != data.readInt();
                    int _result27 = enablePhoneCall(_arg026);
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    break;
                case TRANSACTION_getCardUsageCount /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    int _result28 = getCardUsageCount(_arg027, 0 != data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    break;
                case TRANSACTION_getModuleAccessibility /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    int _result29 = getModuleAccessibility(_arg028);
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    break;
                case TRANSACTION_setModuleAccessibility /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    int _result30 = setModuleAccessibility(_arg029, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    break;
                case TRANSACTION_getPedMode /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result31 = getPedMode();
                    reply.writeNoException();
                    reply.writeInt(_result31);
                    break;
                case TRANSACTION_setPedMode /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    int _result32 = setPedMode(_arg030);
                    reply.writeNoException();
                    reply.writeInt(_result32);
                    break;
                case TRANSACTION_getPedKeysInfo /* 34 */:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg031 = new Bundle();
                    int _result33 = getPedKeysInfo(_arg031);
                    reply.writeNoException();
                    reply.writeInt(_result33);
                    if (_arg031 != null) {
                        reply.writeInt(1);
                        _arg031.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_installSharedLib /* 35 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg032 = data.readString();
                    int _result34 = installSharedLib(_arg032);
                    reply.writeNoException();
                    reply.writeInt(_result34);
                    break;
                case TRANSACTION_deleteSharedLib /* 36 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg033 = data.readString();
                    int _result35 = deleteSharedLib(_arg033);
                    reply.writeNoException();
                    reply.writeInt(_result35);
                    break;
                case TRANSACTION_litesoInstaller /* 37 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg034 = data.readInt();
                    int _result36 = litesoInstaller(_arg034, data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result36);
                    break;
                case TRANSACTION_litesoRun /* 38 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg035 = data.readInt();
                    int _result37 = litesoRun(_arg035);
                    reply.writeNoException();
                    reply.writeInt(_result37);
                    break;
                case TRANSACTION_litesoInfo /* 39 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg036 = data.readInt();
                    Bundle _arg15 = new Bundle();
                    int _result38 = litesoInfo(_arg036, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result38);
                    if (_arg15 != null) {
                        reply.writeInt(1);
                        _arg15.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_litesoRunInfo /* 40 */:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg037 = new Bundle();
                    int _result39 = litesoRunInfo(_arg037);
                    reply.writeNoException();
                    reply.writeInt(_result39);
                    if (_arg037 != null) {
                        reply.writeInt(1);
                        _arg037.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_litesoCustomCmd /* 41 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    byte[] _arg16 = data.createByteArray();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length];
                    }
                    int _result40 = litesoCustomCmd(_arg038, _arg16, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result40);
                    reply.writeByteArray(_arg2);
                    break;
                case TRANSACTION_litesoRemove /* 42 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg039 = data.readInt();
                    int _result41 = litesoRemove(_arg039);
                    reply.writeNoException();
                    reply.writeInt(_result41);
                    break;
                case TRANSACTION_sysGetDebugData /* 43 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg040 = data.createByteArray();
                    int _result42 = sysGetDebugData(_arg040, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result42);
                    reply.writeByteArray(_arg040);
                    break;
                case TRANSACTION_sysPutDebugData /* 44 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg041 = data.createByteArray();
                    int _result43 = sysPutDebugData(_arg041, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result43);
                    reply.writeByteArray(_arg041);
                    break;
                case TRANSACTION_getRtcBatVol /* 45 */:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg042 = new Bundle();
                    int _result44 = getRtcBatVol(_arg042);
                    reply.writeNoException();
                    reply.writeInt(_result44);
                    if (_arg042 != null) {
                        reply.writeInt(1);
                        _arg042.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_readPuk /* 46 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    Bundle _arg17 = new Bundle();
                    int _result45 = readPuk(_arg043, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result45);
                    if (_arg17 != null) {
                        reply.writeInt(1);
                        _arg17.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_logControl /* 47 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result46 = logControl(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result46);
                    break;
                case TRANSACTION_setCardWaterMarkAlpha /* 48 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    int _result47 = setCardWaterMarkAlpha(_arg044, data.readFloat());
                    reply.writeNoException();
                    reply.writeInt(_result47);
                    break;
                case TRANSACTION_getCardWaterMarkAlpha /* 49 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg045 = data.readInt();
                    float _result48 = getCardWaterMarkAlpha(_arg045);
                    reply.writeNoException();
                    reply.writeFloat(_result48);
                    break;
                case TRANSACTION_getCardWaterMarkLocation /* 50 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    int _arg18 = data.readInt();
                    Bundle _arg26 = new Bundle();
                    int _result49 = getCardWaterMarkLocation(_arg046, _arg18, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result49);
                    if (_arg26 != null) {
                        reply.writeInt(1);
                        _arg26.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_sysCitTest /* 51 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    Bundle _arg19 = new Bundle();
                    int _result50 = sysCitTest(_arg047, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result50);
                    if (_arg19 != null) {
                        reply.writeInt(1);
                        _arg19.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/system/BasicOptV2$Stub$Proxy.class */
        private static class Proxy implements BasicOptV2 {
            private IBinder mRemote;
            public static BasicOptV2 sDefaultImpl;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public String getSysParam(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String sysParam = Stub.getDefaultImpl().getSysParam(key);
                        _reply.recycle();
                        _data.recycle();
                        return sysParam;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setSysParam(String key, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int sysParam = Stub.getDefaultImpl().setSysParam(key, value);
                        _reply.recycle();
                        _data.recycle();
                        return sysParam;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public void buzzerOnDevice(int count, int freq, int duration, int interval) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(count);
                    _data.writeInt(freq);
                    _data.writeInt(duration);
                    _data.writeInt(interval);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().buzzerOnDevice(count, freq, duration, interval);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int ledStatusOnDevice(int ledIndex, int ledStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ledIndex);
                    _data.writeInt(ledStatus);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLedStatusOnDevice = Stub.getDefaultImpl().ledStatusOnDevice(ledIndex, ledStatus);
                        _reply.recycle();
                        _data.recycle();
                        return iLedStatusOnDevice;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setScreenMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int screenMode = Stub.getDefaultImpl().setScreenMode(mode);
                        _reply.recycle();
                        _data.recycle();
                        return screenMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysGetRandom(byte[] randData, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(randData);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysGetRandom = Stub.getDefaultImpl().sysGetRandom(randData, len);
                        _reply.recycle();
                        _data.recycle();
                        return iSysGetRandom;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(randData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int ledStatusOnDeviceEx(int redStatus, int greenStatus, int yellowStatus, int blueStatus) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(redStatus);
                    _data.writeInt(greenStatus);
                    _data.writeInt(yellowStatus);
                    _data.writeInt(blueStatus);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLedStatusOnDeviceEx = Stub.getDefaultImpl().ledStatusOnDeviceEx(redStatus, greenStatus, yellowStatus, blueStatus);
                        _reply.recycle();
                        _data.recycle();
                        return iLedStatusOnDeviceEx;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setStatusBarDropDownMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int statusBarDropDownMode = Stub.getDefaultImpl().setStatusBarDropDownMode(mode);
                        _reply.recycle();
                        _data.recycle();
                        return statusBarDropDownMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setNavigationBarVisibility(int visibility) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(visibility);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int navigationBarVisibility = Stub.getDefaultImpl().setNavigationBarVisibility(visibility);
                        _reply.recycle();
                        _data.recycle();
                        return navigationBarVisibility;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setHideNavigationBarItems(int flag) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flag);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int hideNavigationBarItems = Stub.getDefaultImpl().setHideNavigationBarItems(flag);
                        _reply.recycle();
                        _data.recycle();
                        return hideNavigationBarItems;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysPowerManage(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysPowerManage = Stub.getDefaultImpl().sysPowerManage(mode);
                        _reply.recycle();
                        _data.recycle();
                        return iSysPowerManage;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int allowDynamicPermission(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAllowDynamicPermission = Stub.getDefaultImpl().allowDynamicPermission(packageName);
                        _reply.recycle();
                        _data.recycle();
                        return iAllowDynamicPermission;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setGlobalProxy(String proxy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(proxy);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int globalProxy = Stub.getDefaultImpl().setGlobalProxy(proxy);
                        _reply.recycle();
                        _data.recycle();
                        return globalProxy;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int installApplicationCertificate(String name, String contents) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(contents);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInstallApplicationCertificate = Stub.getDefaultImpl().installApplicationCertificate(name, contents);
                        _reply.recycle();
                        _data.recycle();
                        return iInstallApplicationCertificate;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int uninstallApplicationCertificate(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iUninstallApplicationCertificate = Stub.getDefaultImpl().uninstallApplicationCertificate(name);
                        _reply.recycle();
                        _data.recycle();
                        return iUninstallApplicationCertificate;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public String getCpuUsage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCpuUsage, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String cpuUsage = Stub.getDefaultImpl().getCpuUsage();
                        _reply.recycle();
                        _data.recycle();
                        return cpuUsage;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public String getCpuTemperature() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCpuTemperature, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String cpuTemperature = Stub.getDefaultImpl().getCpuTemperature();
                        _reply.recycle();
                        _data.recycle();
                        return cpuTemperature;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setScheduleReboot(int hour, int minute, int second, int millisecond) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(hour);
                    _data.writeInt(minute);
                    _data.writeInt(second);
                    _data.writeInt(millisecond);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setScheduleReboot, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int scheduleReboot = Stub.getDefaultImpl().setScheduleReboot(hour, minute, second, millisecond);
                        _reply.recycle();
                        _data.recycle();
                        return scheduleReboot;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int clearScheduleReboot() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_clearScheduleReboot, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iClearScheduleReboot = Stub.getDefaultImpl().clearScheduleReboot();
                        _reply.recycle();
                        _data.recycle();
                        return iClearScheduleReboot;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int customizeFunctionKey(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_customizeFunctionKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCustomizeFunctionKey = Stub.getDefaultImpl().customizeFunctionKey(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iCustomizeFunctionKey;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setLMKPackage(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setLMKPackage, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int lMKPackage = Stub.getDefaultImpl().setLMKPackage(packageName);
                        _reply.recycle();
                        _data.recycle();
                        return lMKPackage;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int removeLMKPackage(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_removeLMKPackage, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iRemoveLMKPackage = Stub.getDefaultImpl().removeLMKPackage(packageName);
                        _reply.recycle();
                        _data.recycle();
                        return iRemoveLMKPackage;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysSetWakeup(int channel, int mode, Bundle attr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(channel);
                    _data.writeInt(mode);
                    if (attr != null) {
                        _data.writeInt(1);
                        attr.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysSetWakeup, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysSetWakeup = Stub.getDefaultImpl().sysSetWakeup(channel, mode, attr);
                        _reply.recycle();
                        _data.recycle();
                        return iSysSetWakeup;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setPreferredNetworkMode(int mode, int slotIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeInt(slotIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setPreferredNetworkMode, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int preferredNetworkMode = Stub.getDefaultImpl().setPreferredNetworkMode(mode, slotIndex);
                        _reply.recycle();
                        _data.recycle();
                        return preferredNetworkMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public String getSupportedNetworkType(int slotIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getSupportedNetworkType, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String supportedNetworkType = Stub.getDefaultImpl().getSupportedNetworkType(slotIndex);
                        _reply.recycle();
                        _data.recycle();
                        return supportedNetworkType;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setAirplaneMode(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setAirplaneMode, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int airplaneMode = Stub.getDefaultImpl().setAirplaneMode(enable);
                        _reply.recycle();
                        _data.recycle();
                        return airplaneMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setDataRoamingEnable(int slotIndex, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(slotIndex);
                    _data.writeInt(enable ? 1 : 0);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setDataRoamingEnable, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int dataRoamingEnable = Stub.getDefaultImpl().setDataRoamingEnable(slotIndex, enable);
                        _reply.recycle();
                        _data.recycle();
                        return dataRoamingEnable;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int enablePhoneCall(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(enable ? 1 : 0);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_enablePhoneCall, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iEnablePhoneCall = Stub.getDefaultImpl().enablePhoneCall(enable);
                        _reply.recycle();
                        _data.recycle();
                        return iEnablePhoneCall;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getCardUsageCount(int cardType, boolean isSuccess) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeInt(isSuccess ? 1 : 0);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCardUsageCount, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int cardUsageCount = Stub.getDefaultImpl().getCardUsageCount(cardType, isSuccess);
                        _reply.recycle();
                        _data.recycle();
                        return cardUsageCount;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getModuleAccessibility(int module) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(module);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getModuleAccessibility, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int moduleAccessibility = Stub.getDefaultImpl().getModuleAccessibility(module);
                        _reply.recycle();
                        _data.recycle();
                        return moduleAccessibility;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setModuleAccessibility(int module, int ability) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(module);
                    _data.writeInt(ability);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setModuleAccessibility, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int moduleAccessibility = Stub.getDefaultImpl().setModuleAccessibility(module, ability);
                        _reply.recycle();
                        _data.recycle();
                        return moduleAccessibility;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getPedMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getPedMode, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int pedMode = Stub.getDefaultImpl().getPedMode();
                        _reply.recycle();
                        _data.recycle();
                        return pedMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setPedMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setPedMode, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int pedMode = Stub.getDefaultImpl().setPedMode(mode);
                        _reply.recycle();
                        _data.recycle();
                        return pedMode;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getPedKeysInfo(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getPedKeysInfo, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int pedKeysInfo = Stub.getDefaultImpl().getPedKeysInfo(info);
                        _reply.recycle();
                        _data.recycle();
                        return pedKeysInfo;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int installSharedLib(String path) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(path);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_installSharedLib, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInstallSharedLib = Stub.getDefaultImpl().installSharedLib(path);
                        _reply.recycle();
                        _data.recycle();
                        return iInstallSharedLib;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int deleteSharedLib(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_deleteSharedLib, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteSharedLib = Stub.getDefaultImpl().deleteSharedLib(name);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteSharedLib;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoInstaller(int index, String filePath) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    _data.writeString(filePath);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoInstaller, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoInstaller = Stub.getDefaultImpl().litesoInstaller(index, filePath);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoInstaller;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoRun(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoRun, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoRun = Stub.getDefaultImpl().litesoRun(index);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoRun;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoInfo(int index, Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoInfo, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoInfo = Stub.getDefaultImpl().litesoInfo(index, info);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoInfo;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoRunInfo(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoRunInfo, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoRunInfo = Stub.getDefaultImpl().litesoRunInfo(info);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoRunInfo;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoCustomCmd(int cmd, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cmd);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoCustomCmd, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoCustomCmd = Stub.getDefaultImpl().litesoCustomCmd(cmd, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoCustomCmd;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(dataOut);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int litesoRemove(int index) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_litesoRemove, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLitesoRemove = Stub.getDefaultImpl().litesoRemove(index);
                        _reply.recycle();
                        _data.recycle();
                        return iLitesoRemove;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysGetDebugData(byte[] data, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysGetDebugData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysGetDebugData = Stub.getDefaultImpl().sysGetDebugData(data, len);
                        _reply.recycle();
                        _data.recycle();
                        return iSysGetDebugData;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(data);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysPutDebugData(byte[] data, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysPutDebugData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysPutDebugData = Stub.getDefaultImpl().sysPutDebugData(data, len);
                        _reply.recycle();
                        _data.recycle();
                        return iSysPutDebugData;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(data);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getRtcBatVol(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getRtcBatVol, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int rtcBatVol = Stub.getDefaultImpl().getRtcBatVol(info);
                        _reply.recycle();
                        _data.recycle();
                        return rtcBatVol;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int readPuk(int pukType, Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukType);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_readPuk, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int puk = Stub.getDefaultImpl().readPuk(pukType, info);
                        _reply.recycle();
                        _data.recycle();
                        return puk;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int logControl(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_logControl, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iLogControl = Stub.getDefaultImpl().logControl(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iLogControl;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int setCardWaterMarkAlpha(int waterMarkIndex, float alpha) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(waterMarkIndex);
                    _data.writeFloat(alpha);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setCardWaterMarkAlpha, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int cardWaterMarkAlpha = Stub.getDefaultImpl().setCardWaterMarkAlpha(waterMarkIndex, alpha);
                        _reply.recycle();
                        _data.recycle();
                        return cardWaterMarkAlpha;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public float getCardWaterMarkAlpha(int waterMarkIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(waterMarkIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCardWaterMarkAlpha, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        float cardWaterMarkAlpha = Stub.getDefaultImpl().getCardWaterMarkAlpha(waterMarkIndex);
                        _reply.recycle();
                        _data.recycle();
                        return cardWaterMarkAlpha;
                    }
                    _reply.readException();
                    float _result = _reply.readFloat();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int getCardWaterMarkLocation(int waterMarkIndex, int rotation, Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(waterMarkIndex);
                    _data.writeInt(rotation);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCardWaterMarkLocation, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int cardWaterMarkLocation = Stub.getDefaultImpl().getCardWaterMarkLocation(waterMarkIndex, rotation, info);
                        _reply.recycle();
                        _data.recycle();
                        return cardWaterMarkLocation;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
            public int sysCitTest(int testId, Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(testId);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysCitTest, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysCitTest = Stub.getDefaultImpl().sysCitTest(testId, info);
                        _reply.recycle();
                        _data.recycle();
                        return iSysCitTest;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        info.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(BasicOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static BasicOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
