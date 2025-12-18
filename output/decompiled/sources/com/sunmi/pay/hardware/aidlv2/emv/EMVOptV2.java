package com.sunmi.pay.hardware.aidlv2.emv;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.bean.AidV2;
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2;
import com.sunmi.pay.hardware.aidlv2.bean.DrlV2;
import com.sunmi.pay.hardware.aidlv2.bean.EMVTransDataV2;
import com.sunmi.pay.hardware.aidlv2.bean.EmvTermParamV2;
import com.sunmi.pay.hardware.aidlv2.bean.RevocListV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVDataListenerV2;
import com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVOptV2.class */
public interface EMVOptV2 extends IInterface {
    int addAid(AidV2 aidV2) throws RemoteException;

    int deleteAid(String str) throws RemoteException;

    int addCapk(CapkV2 capkV2) throws RemoteException;

    int deleteCapk(String str, String str2) throws RemoteException;

    int setTerminalParam(EmvTermParamV2 emvTermParamV2) throws RemoteException;

    int checkAidAndCapk() throws RemoteException;

    int initEmvProcess() throws RemoteException;

    void transactProcess(EMVTransDataV2 eMVTransDataV2, EMVListenerV2 eMVListenerV2) throws RemoteException;

    int getTlv(int i, String str, byte[] bArr) throws RemoteException;

    int getTlvList(int i, String[] strArr, byte[] bArr) throws RemoteException;

    void setTlv(int i, String str, String str2) throws RemoteException;

    void setTlvList(int i, String[] strArr, String[] strArr2) throws RemoteException;

    void importAppSelect(int i) throws RemoteException;

    void importAppFinalSelectStatus(int i) throws RemoteException;

    void importPinInputStatus(int i, int i2) throws RemoteException;

    void importSignatureStatus(int i) throws RemoteException;

    void importCertStatus(int i) throws RemoteException;

    void importCardNoStatus(int i) throws RemoteException;

    int importOnlineProcStatus(int i, String[] strArr, String[] strArr2, byte[] bArr) throws RemoteException;

    int readTransLog(int i, List<String> list) throws RemoteException;

    void abortTransactProcess() throws RemoteException;

    void importDataExchangeStatus(int i) throws RemoteException;

    void transactProcessEx(Bundle bundle, EMVListenerV2 eMVListenerV2) throws RemoteException;

    int queryECBalance(Bundle bundle) throws RemoteException;

    int addDrlLimitSet(DrlV2 drlV2) throws RemoteException;

    int deleteDrlLimitSet(String str) throws RemoteException;

    void setTermParamEx(Bundle bundle) throws RemoteException;

    int queryAidCapkList(int i, List<String> list) throws RemoteException;

    int transactPreProcess() throws RemoteException;

    int addRevocList(RevocListV2 revocListV2) throws RemoteException;

    int deleteRevocList(RevocListV2 revocListV2) throws RemoteException;

    int sysSetTime(long j) throws RemoteException;

    int sysGetTime(byte[] bArr) throws RemoteException;

    int clearData(int i) throws RemoteException;

    int setAccountDataSecParam(Bundle bundle) throws RemoteException;

    int getAccountSecData(int i, String[] strArr, Bundle bundle) throws RemoteException;

    void importTermRiskManagementStatus(int i) throws RemoteException;

    void importPreFirstGenACStatus(int i) throws RemoteException;

    void importDataStorage(String[] strArr, String[] strArr2) throws RemoteException;

    void addEMVDataListener(EMVDataListenerV2 eMVDataListenerV2) throws RemoteException;

    int addDETData(byte[] bArr) throws RemoteException;

    int dataInputOutputProcess(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    void importPinInputStatusForToss(byte[] bArr, int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVOptV2$Default.class */
    public static class Default implements EMVOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int addAid(AidV2 aid) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int deleteAid(String tag9F06Value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int addCapk(CapkV2 capk) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int deleteCapk(String tag9F06Value, String tag9F22Value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int setTerminalParam(EmvTermParamV2 termParam) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int checkAidAndCapk() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int initEmvProcess() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void transactProcess(EMVTransDataV2 transData, EMVListenerV2 listener) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int getTlv(int opCode, String tag, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int getTlvList(int opCode, String[] tags, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void setTlv(int opCode, String tag, String hexValue) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void setTlvList(int opCode, String[] tags, String[] hexValues) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importAppSelect(int selectIndex) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importAppFinalSelectStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importPinInputStatus(int pinType, int inputResult) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importSignatureStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importCertStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importCardNoStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int importOnlineProcStatus(int status, String[] tags, String[] hexValues, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int readTransLog(int logType, List<String> infoOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void abortTransactProcess() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importDataExchangeStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void transactProcessEx(Bundle transData, EMVListenerV2 listener) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int queryECBalance(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int addDrlLimitSet(DrlV2 drl) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int deleteDrlLimitSet(String programId) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void setTermParamEx(Bundle bundle) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int queryAidCapkList(int type, List<String> list) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int transactPreProcess() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int addRevocList(RevocListV2 revocList) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int deleteRevocList(RevocListV2 revocList) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int sysSetTime(long timeStamp) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int sysGetTime(byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int clearData(int opCode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int setAccountDataSecParam(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int getAccountSecData(int opCode, String[] tags, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importTermRiskManagementStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importPreFirstGenACStatus(int status) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importDataStorage(String[] tags, String[] hexValues) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void addEMVDataListener(EMVDataListenerV2 listener) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int addDETData(byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public int dataInputOutputProcess(int mode, int procType, byte[] inData, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
        public void importPinInputStatusForToss(byte[] pinValue, int inputResult) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVOptV2$Stub.class */
    public static abstract class Stub extends Binder implements EMVOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2";
        static final int TRANSACTION_addAid = 1;
        static final int TRANSACTION_deleteAid = 2;
        static final int TRANSACTION_addCapk = 3;
        static final int TRANSACTION_deleteCapk = 4;
        static final int TRANSACTION_setTerminalParam = 5;
        static final int TRANSACTION_checkAidAndCapk = 6;
        static final int TRANSACTION_initEmvProcess = 7;
        static final int TRANSACTION_transactProcess = 8;
        static final int TRANSACTION_getTlv = 9;
        static final int TRANSACTION_getTlvList = 10;
        static final int TRANSACTION_setTlv = 11;
        static final int TRANSACTION_setTlvList = 12;
        static final int TRANSACTION_importAppSelect = 13;
        static final int TRANSACTION_importAppFinalSelectStatus = 14;
        static final int TRANSACTION_importPinInputStatus = 15;
        static final int TRANSACTION_importSignatureStatus = 16;
        static final int TRANSACTION_importCertStatus = 17;
        static final int TRANSACTION_importCardNoStatus = 18;
        static final int TRANSACTION_importOnlineProcStatus = 19;
        static final int TRANSACTION_readTransLog = 20;
        static final int TRANSACTION_abortTransactProcess = 21;
        static final int TRANSACTION_importDataExchangeStatus = 22;
        static final int TRANSACTION_transactProcessEx = 23;
        static final int TRANSACTION_queryECBalance = 24;
        static final int TRANSACTION_addDrlLimitSet = 25;
        static final int TRANSACTION_deleteDrlLimitSet = 26;
        static final int TRANSACTION_setTermParamEx = 27;
        static final int TRANSACTION_queryAidCapkList = 28;
        static final int TRANSACTION_transactPreProcess = 29;
        static final int TRANSACTION_addRevocList = 30;
        static final int TRANSACTION_deleteRevocList = 31;
        static final int TRANSACTION_sysSetTime = 32;
        static final int TRANSACTION_sysGetTime = 33;
        static final int TRANSACTION_clearData = 34;
        static final int TRANSACTION_setAccountDataSecParam = 35;
        static final int TRANSACTION_getAccountSecData = 36;
        static final int TRANSACTION_importTermRiskManagementStatus = 37;
        static final int TRANSACTION_importPreFirstGenACStatus = 38;
        static final int TRANSACTION_importDataStorage = 39;
        static final int TRANSACTION_addEMVDataListener = 40;
        static final int TRANSACTION_addDETData = 41;
        static final int TRANSACTION_dataInputOutputProcess = 42;
        static final int TRANSACTION_importPinInputStatusForToss = 43;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static EMVOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof EMVOptV2)) {
                return (EMVOptV2) iin;
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
            RevocListV2 _arg02;
            RevocListV2 _arg03;
            Bundle _arg04;
            DrlV2 _arg05;
            Bundle _arg06;
            Bundle _arg07;
            EMVTransDataV2 _arg08;
            EmvTermParamV2 _arg09;
            CapkV2 _arg010;
            AidV2 _arg011;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = AidV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    int _result = addAid(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg012 = data.readString();
                    int _result2 = deleteAid(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = CapkV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    int _result3 = addCapk(_arg010);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg013 = data.readString();
                    String _arg1 = data.readString();
                    int _result4 = deleteCapk(_arg013, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = EmvTermParamV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    int _result5 = setTerminalParam(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _result6 = checkAidAndCapk();
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = initEmvProcess();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = EMVTransDataV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    EMVListenerV2 _arg12 = EMVListenerV2.Stub.asInterface(data.readStrongBinder());
                    transactProcess(_arg08, _arg12);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    String _arg13 = data.readString();
                    byte[] _arg2 = data.createByteArray();
                    int _result8 = getTlv(_arg014, _arg13, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    reply.writeByteArray(_arg2);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    String[] _arg14 = data.createStringArray();
                    byte[] _arg22 = data.createByteArray();
                    int _result9 = getTlvList(_arg015, _arg14, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg22);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    String _arg15 = data.readString();
                    String _arg23 = data.readString();
                    setTlv(_arg016, _arg15, _arg23);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    String[] _arg16 = data.createStringArray();
                    String[] _arg24 = data.createStringArray();
                    setTlvList(_arg017, _arg16, _arg24);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    importAppSelect(_arg018);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    importAppFinalSelectStatus(_arg019);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    int _arg17 = data.readInt();
                    importPinInputStatus(_arg020, _arg17);
                    break;
                case TRANSACTION_importSignatureStatus /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    importSignatureStatus(_arg021);
                    break;
                case TRANSACTION_importCertStatus /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    importCertStatus(_arg022);
                    break;
                case TRANSACTION_importCardNoStatus /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    importCardNoStatus(_arg023);
                    break;
                case TRANSACTION_importOnlineProcStatus /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    String[] _arg18 = data.createStringArray();
                    String[] _arg25 = data.createStringArray();
                    byte[] _arg3 = data.createByteArray();
                    int _result10 = importOnlineProcStatus(_arg024, _arg18, _arg25, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeByteArray(_arg3);
                    break;
                case TRANSACTION_readTransLog /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    List<String> _arg19 = data.createStringArrayList();
                    int _result11 = readTransLog(_arg025, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    reply.writeStringList(_arg19);
                    break;
                case TRANSACTION_abortTransactProcess /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    abortTransactProcess();
                    reply.writeNoException();
                    break;
                case TRANSACTION_importDataExchangeStatus /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    importDataExchangeStatus(_arg026);
                    break;
                case TRANSACTION_transactProcessEx /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    EMVListenerV2 _arg110 = EMVListenerV2.Stub.asInterface(data.readStrongBinder());
                    transactProcessEx(_arg07, _arg110);
                    break;
                case TRANSACTION_queryECBalance /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _result12 = queryECBalance(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    if (_arg06 != null) {
                        reply.writeInt(1);
                        _arg06.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_addDrlLimitSet /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = DrlV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _result13 = addDrlLimitSet(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case TRANSACTION_deleteDrlLimitSet /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg027 = data.readString();
                    int _result14 = deleteDrlLimitSet(_arg027);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    break;
                case TRANSACTION_setTermParamEx /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    setTermParamEx(_arg04);
                    reply.writeNoException();
                    break;
                case TRANSACTION_queryAidCapkList /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    List<String> _arg111 = data.createStringArrayList();
                    int _result15 = queryAidCapkList(_arg028, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    reply.writeStringList(_arg111);
                    break;
                case TRANSACTION_transactPreProcess /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result16 = transactPreProcess();
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    break;
                case TRANSACTION_addRevocList /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = RevocListV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _result17 = addRevocList(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_deleteRevocList /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = RevocListV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result18 = deleteRevocList(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    break;
                case TRANSACTION_sysSetTime /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg029 = data.readLong();
                    int _result19 = sysSetTime(_arg029);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    break;
                case TRANSACTION_sysGetTime /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg030 = data.createByteArray();
                    int _result20 = sysGetTime(_arg030);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    reply.writeByteArray(_arg030);
                    break;
                case TRANSACTION_clearData /* 34 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg031 = data.readInt();
                    int _result21 = clearData(_arg031);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    break;
                case TRANSACTION_setAccountDataSecParam /* 35 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result22 = setAccountDataSecParam(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    break;
                case TRANSACTION_getAccountSecData /* 36 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    String[] _arg112 = data.createStringArray();
                    Bundle _arg26 = new Bundle();
                    int _result23 = getAccountSecData(_arg032, _arg112, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    if (_arg26 != null) {
                        reply.writeInt(1);
                        _arg26.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_importTermRiskManagementStatus /* 37 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg033 = data.readInt();
                    importTermRiskManagementStatus(_arg033);
                    break;
                case TRANSACTION_importPreFirstGenACStatus /* 38 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg034 = data.readInt();
                    importPreFirstGenACStatus(_arg034);
                    break;
                case TRANSACTION_importDataStorage /* 39 */:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg035 = data.createStringArray();
                    String[] _arg113 = data.createStringArray();
                    importDataStorage(_arg035, _arg113);
                    break;
                case TRANSACTION_addEMVDataListener /* 40 */:
                    data.enforceInterface(DESCRIPTOR);
                    EMVDataListenerV2 _arg036 = EMVDataListenerV2.Stub.asInterface(data.readStrongBinder());
                    addEMVDataListener(_arg036);
                    break;
                case TRANSACTION_addDETData /* 41 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg037 = data.createByteArray();
                    int _result24 = addDETData(_arg037);
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    break;
                case TRANSACTION_dataInputOutputProcess /* 42 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    int _arg114 = data.readInt();
                    byte[] _arg27 = data.createByteArray();
                    byte[] _arg32 = data.createByteArray();
                    int _result25 = dataInputOutputProcess(_arg038, _arg114, _arg27, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    reply.writeByteArray(_arg32);
                    break;
                case TRANSACTION_importPinInputStatusForToss /* 43 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg039 = data.createByteArray();
                    int _arg115 = data.readInt();
                    importPinInputStatusForToss(_arg039, _arg115);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVOptV2$Stub$Proxy.class */
        private static class Proxy implements EMVOptV2 {
            private IBinder mRemote;
            public static EMVOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int addAid(AidV2 aid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (aid != null) {
                        _data.writeInt(1);
                        aid.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAddAid = Stub.getDefaultImpl().addAid(aid);
                        _reply.recycle();
                        _data.recycle();
                        return iAddAid;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int deleteAid(String tag9F06Value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag9F06Value);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteAid = Stub.getDefaultImpl().deleteAid(tag9F06Value);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteAid;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int addCapk(CapkV2 capk) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (capk != null) {
                        _data.writeInt(1);
                        capk.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAddCapk = Stub.getDefaultImpl().addCapk(capk);
                        _reply.recycle();
                        _data.recycle();
                        return iAddCapk;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int deleteCapk(String tag9F06Value, String tag9F22Value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag9F06Value);
                    _data.writeString(tag9F22Value);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteCapk = Stub.getDefaultImpl().deleteCapk(tag9F06Value, tag9F22Value);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteCapk;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int setTerminalParam(EmvTermParamV2 termParam) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (termParam != null) {
                        _data.writeInt(1);
                        termParam.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int terminalParam = Stub.getDefaultImpl().setTerminalParam(termParam);
                        _reply.recycle();
                        _data.recycle();
                        return terminalParam;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int checkAidAndCapk() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCheckAidAndCapk = Stub.getDefaultImpl().checkAidAndCapk();
                        _reply.recycle();
                        _data.recycle();
                        return iCheckAidAndCapk;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int initEmvProcess() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInitEmvProcess = Stub.getDefaultImpl().initEmvProcess();
                        _reply.recycle();
                        _data.recycle();
                        return iInitEmvProcess;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void transactProcess(EMVTransDataV2 transData, EMVListenerV2 listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (transData != null) {
                        _data.writeInt(1);
                        transData.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().transactProcess(transData, listener);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int getTlv(int opCode, String tag, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    _data.writeString(tag);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int tlv = Stub.getDefaultImpl().getTlv(opCode, tag, outData);
                        _reply.recycle();
                        _data.recycle();
                        return tlv;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int getTlvList(int opCode, String[] tags, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    _data.writeStringArray(tags);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int tlvList = Stub.getDefaultImpl().getTlvList(opCode, tags, outData);
                        _reply.recycle();
                        _data.recycle();
                        return tlvList;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void setTlv(int opCode, String tag, String hexValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    _data.writeString(tag);
                    _data.writeString(hexValue);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setTlv(opCode, tag, hexValue);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void setTlvList(int opCode, String[] tags, String[] hexValues) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    _data.writeStringArray(tags);
                    _data.writeStringArray(hexValues);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setTlvList(opCode, tags, hexValues);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importAppSelect(int selectIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(selectIndex);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importAppSelect(selectIndex);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importAppFinalSelectStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importAppFinalSelectStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importPinInputStatus(int pinType, int inputResult) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pinType);
                    _data.writeInt(inputResult);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importPinInputStatus(pinType, inputResult);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importSignatureStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importSignatureStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importSignatureStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importCertStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importCertStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importCertStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importCardNoStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importCardNoStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importCardNoStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int importOnlineProcStatus(int status, String[] tags, String[] hexValues, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    _data.writeStringArray(tags);
                    _data.writeStringArray(hexValues);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importOnlineProcStatus, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iImportOnlineProcStatus = Stub.getDefaultImpl().importOnlineProcStatus(status, tags, hexValues, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iImportOnlineProcStatus;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int readTransLog(int logType, List<String> infoOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logType);
                    _data.writeStringList(infoOut);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_readTransLog, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int transLog = Stub.getDefaultImpl().readTransLog(logType, infoOut);
                        _reply.recycle();
                        _data.recycle();
                        return transLog;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readStringList(infoOut);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void abortTransactProcess() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_abortTransactProcess, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().abortTransactProcess();
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importDataExchangeStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importDataExchangeStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importDataExchangeStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void transactProcessEx(Bundle transData, EMVListenerV2 listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (transData != null) {
                        _data.writeInt(1);
                        transData.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transactProcessEx, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().transactProcessEx(transData, listener);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int queryECBalance(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_queryECBalance, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryECBalance = Stub.getDefaultImpl().queryECBalance(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryECBalance;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        bundle.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int addDrlLimitSet(DrlV2 drl) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (drl != null) {
                        _data.writeInt(1);
                        drl.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_addDrlLimitSet, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAddDrlLimitSet = Stub.getDefaultImpl().addDrlLimitSet(drl);
                        _reply.recycle();
                        _data.recycle();
                        return iAddDrlLimitSet;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int deleteDrlLimitSet(String programId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(programId);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_deleteDrlLimitSet, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteDrlLimitSet = Stub.getDefaultImpl().deleteDrlLimitSet(programId);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteDrlLimitSet;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void setTermParamEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setTermParamEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setTermParamEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int queryAidCapkList(int type, List<String> list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeStringList(list);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_queryAidCapkList, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryAidCapkList = Stub.getDefaultImpl().queryAidCapkList(type, list);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryAidCapkList;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readStringList(list);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int transactPreProcess() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transactPreProcess, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransactPreProcess = Stub.getDefaultImpl().transactPreProcess();
                        _reply.recycle();
                        _data.recycle();
                        return iTransactPreProcess;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int addRevocList(RevocListV2 revocList) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (revocList != null) {
                        _data.writeInt(1);
                        revocList.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_addRevocList, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAddRevocList = Stub.getDefaultImpl().addRevocList(revocList);
                        _reply.recycle();
                        _data.recycle();
                        return iAddRevocList;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int deleteRevocList(RevocListV2 revocList) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (revocList != null) {
                        _data.writeInt(1);
                        revocList.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_deleteRevocList, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteRevocList = Stub.getDefaultImpl().deleteRevocList(revocList);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteRevocList;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int sysSetTime(long timeStamp) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(timeStamp);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysSetTime, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysSetTime = Stub.getDefaultImpl().sysSetTime(timeStamp);
                        _reply.recycle();
                        _data.recycle();
                        return iSysSetTime;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int sysGetTime(byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysGetTime, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysGetTime = Stub.getDefaultImpl().sysGetTime(outData);
                        _reply.recycle();
                        _data.recycle();
                        return iSysGetTime;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int clearData(int opCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_clearData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iClearData = Stub.getDefaultImpl().clearData(opCode);
                        _reply.recycle();
                        _data.recycle();
                        return iClearData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int setAccountDataSecParam(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setAccountDataSecParam, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int accountDataSecParam = Stub.getDefaultImpl().setAccountDataSecParam(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return accountDataSecParam;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int getAccountSecData(int opCode, String[] tags, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    _data.writeStringArray(tags);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getAccountSecData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int accountSecData = Stub.getDefaultImpl().getAccountSecData(opCode, tags, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return accountSecData;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        bundle.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importTermRiskManagementStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importTermRiskManagementStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importTermRiskManagementStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importPreFirstGenACStatus(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importPreFirstGenACStatus, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importPreFirstGenACStatus(status);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importDataStorage(String[] tags, String[] hexValues) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(tags);
                    _data.writeStringArray(hexValues);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importDataStorage, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importDataStorage(tags, hexValues);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void addEMVDataListener(EMVDataListenerV2 listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_addEMVDataListener, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().addEMVDataListener(listener);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int addDETData(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_addDETData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAddDETData = Stub.getDefaultImpl().addDETData(data);
                        _reply.recycle();
                        _data.recycle();
                        return iAddDETData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public int dataInputOutputProcess(int mode, int procType, byte[] inData, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeInt(procType);
                    _data.writeByteArray(inData);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataInputOutputProcess, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataInputOutputProcess = Stub.getDefaultImpl().dataInputOutputProcess(mode, procType, inData, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iDataInputOutputProcess;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
            public void importPinInputStatusForToss(byte[] pinValue, int inputResult) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(pinValue);
                    _data.writeInt(inputResult);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_importPinInputStatusForToss, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importPinInputStatusForToss(pinValue, inputResult);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }

        public static boolean setDefaultImpl(EMVOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static EMVOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
