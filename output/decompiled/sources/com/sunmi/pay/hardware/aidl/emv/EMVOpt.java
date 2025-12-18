package com.sunmi.pay.hardware.aidl.emv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.bean.EmvTermParam;
import com.sunmi.pay.hardware.aidl.bean.TransData;
import com.sunmi.pay.hardware.aidl.emv.EMVListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVOpt.class */
public interface EMVOpt extends IInterface {
    int updateAID(int i, String str) throws RemoteException;

    int updateCAPK(int i, String str) throws RemoteException;

    int insertAID(List<String> list) throws RemoteException;

    int insertCAPK(List<String> list) throws RemoteException;

    int syncParam() throws RemoteException;

    int setTerminalParam(EmvTermParam emvTermParam) throws RemoteException;

    int isExistCapkAndAID() throws RemoteException;

    void transactProcess(EMVListener eMVListener) throws RemoteException;

    int readKernelData(String[] strArr, byte[] bArr) throws RemoteException;

    int importResponseData(int i, byte[] bArr, int i2, byte[] bArr2) throws RemoteException;

    int readTransLog(int i, List<String> list) throws RemoteException;

    void importPinInputStatus(int i, int i2) throws RemoteException;

    int TransPreProcess(int i, TransData transData) throws RemoteException;

    int setKernelData(byte[] bArr) throws RemoteException;

    void abortTransactProcess() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVOpt$Default.class */
    public static class Default implements EMVOpt {
        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int updateAID(int actType, String aid) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int updateCAPK(int actType, String capk) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int insertAID(List<String> aidList) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int insertCAPK(List<String> capkList) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int syncParam() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int setTerminalParam(EmvTermParam emvTermParam) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int isExistCapkAndAID() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public void transactProcess(EMVListener listener) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int readKernelData(String[] tags, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int importResponseData(int onlineResult, byte[] tagIn, int tagInLength, byte[] tagOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int readTransLog(int logType, List<String> infoOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public void importPinInputStatus(int pinType, int inputResult) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int TransPreProcess(int icCardType, TransData transData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public int setKernelData(byte[] tlvData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
        public void abortTransactProcess() throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVOpt$Stub.class */
    public static abstract class Stub extends Binder implements EMVOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.emv.EMVOpt";
        static final int TRANSACTION_updateAID = 1;
        static final int TRANSACTION_updateCAPK = 2;
        static final int TRANSACTION_insertAID = 3;
        static final int TRANSACTION_insertCAPK = 4;
        static final int TRANSACTION_syncParam = 5;
        static final int TRANSACTION_setTerminalParam = 6;
        static final int TRANSACTION_isExistCapkAndAID = 7;
        static final int TRANSACTION_transactProcess = 8;
        static final int TRANSACTION_readKernelData = 9;
        static final int TRANSACTION_importResponseData = 10;
        static final int TRANSACTION_readTransLog = 11;
        static final int TRANSACTION_importPinInputStatus = 12;
        static final int TRANSACTION_TransPreProcess = 13;
        static final int TRANSACTION_setKernelData = 14;
        static final int TRANSACTION_abortTransactProcess = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static EMVOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof EMVOpt)) {
                return (EMVOpt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            TransData _arg1;
            EmvTermParam _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    String _arg12 = data.readString();
                    int _result = updateAID(_arg02, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    String _arg13 = data.readString();
                    int _result2 = updateCAPK(_arg03, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    List<String> _arg04 = data.createStringArrayList();
                    int _result3 = insertAID(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    List<String> _arg05 = data.createStringArrayList();
                    int _result4 = insertCAPK(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _result5 = syncParam();
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = EmvTermParam.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result6 = setTerminalParam(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = isExistCapkAndAID();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    EMVListener _arg06 = EMVListener.Stub.asInterface(data.readStrongBinder());
                    transactProcess(_arg06);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg07 = data.createStringArray();
                    byte[] _arg14 = data.createByteArray();
                    int _result8 = readKernelData(_arg07, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    reply.writeByteArray(_arg14);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    byte[] _arg15 = data.createByteArray();
                    int _arg2 = data.readInt();
                    byte[] _arg3 = data.createByteArray();
                    int _result9 = importResponseData(_arg08, _arg15, _arg2, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg15);
                    reply.writeByteArray(_arg3);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    List<String> _arg16 = new ArrayList<>();
                    int _result10 = readTransLog(_arg09, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeStringList(_arg16);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg17 = data.readInt();
                    importPinInputStatus(_arg010, _arg17);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = TransData.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _result11 = TransPreProcess(_arg011, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg012 = data.createByteArray();
                    int _result12 = setKernelData(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    abortTransactProcess();
                    reply.writeNoException();
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVOpt$Stub$Proxy.class */
        private static class Proxy implements EMVOpt {
            private IBinder mRemote;
            public static EMVOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int updateAID(int actType, String aid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actType);
                    _data.writeString(aid);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iUpdateAID = Stub.getDefaultImpl().updateAID(actType, aid);
                        _reply.recycle();
                        _data.recycle();
                        return iUpdateAID;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int updateCAPK(int actType, String capk) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actType);
                    _data.writeString(capk);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iUpdateCAPK = Stub.getDefaultImpl().updateCAPK(actType, capk);
                        _reply.recycle();
                        _data.recycle();
                        return iUpdateCAPK;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int insertAID(List<String> aidList) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(aidList);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInsertAID = Stub.getDefaultImpl().insertAID(aidList);
                        _reply.recycle();
                        _data.recycle();
                        return iInsertAID;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int insertCAPK(List<String> capkList) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(capkList);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInsertCAPK = Stub.getDefaultImpl().insertCAPK(capkList);
                        _reply.recycle();
                        _data.recycle();
                        return iInsertCAPK;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int syncParam() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSyncParam = Stub.getDefaultImpl().syncParam();
                        _reply.recycle();
                        _data.recycle();
                        return iSyncParam;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int setTerminalParam(EmvTermParam emvTermParam) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (emvTermParam != null) {
                        _data.writeInt(1);
                        emvTermParam.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int terminalParam = Stub.getDefaultImpl().setTerminalParam(emvTermParam);
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int isExistCapkAndAID() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iIsExistCapkAndAID = Stub.getDefaultImpl().isExistCapkAndAID();
                        _reply.recycle();
                        _data.recycle();
                        return iIsExistCapkAndAID;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public void transactProcess(EMVListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().transactProcess(listener);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int readKernelData(String[] tags, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(tags);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int kernelData = Stub.getDefaultImpl().readKernelData(tags, outData);
                        _reply.recycle();
                        _data.recycle();
                        return kernelData;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int importResponseData(int onlineResult, byte[] tagIn, int tagInLength, byte[] tagOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(onlineResult);
                    _data.writeByteArray(tagIn);
                    _data.writeInt(tagInLength);
                    _data.writeByteArray(tagOut);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iImportResponseData = Stub.getDefaultImpl().importResponseData(onlineResult, tagIn, tagInLength, tagOut);
                        _reply.recycle();
                        _data.recycle();
                        return iImportResponseData;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(tagIn);
                    _reply.readByteArray(tagOut);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int readTransLog(int logType, List<String> infoOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(logType);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public void importPinInputStatus(int pinType, int inputResult) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pinType);
                    _data.writeInt(inputResult);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int TransPreProcess(int icCardType, TransData transData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(icCardType);
                    if (transData != null) {
                        _data.writeInt(1);
                        transData.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransPreProcess = Stub.getDefaultImpl().TransPreProcess(icCardType, transData);
                        _reply.recycle();
                        _data.recycle();
                        return iTransPreProcess;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public int setKernelData(byte[] tlvData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(tlvData);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int kernelData = Stub.getDefaultImpl().setKernelData(tlvData);
                        _reply.recycle();
                        _data.recycle();
                        return kernelData;
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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVOpt
            public void abortTransactProcess() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
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
        }

        public static boolean setDefaultImpl(EMVOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static EMVOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
