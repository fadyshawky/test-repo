package com.sunmi.pay.hardware.aidl.system;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/system/BasicOpt.class */
public interface BasicOpt extends IInterface {
    String getSysParam(String str) throws RemoteException;

    int setSysParam(String str, String str2) throws RemoteException;

    int buzzerOnDevice(int i) throws RemoteException;

    int ledStatusOnDevice(int i, int i2) throws RemoteException;

    int setScreenMode(int i) throws RemoteException;

    void resetSP() throws RemoteException;

    int sysPowerManager(int i) throws RemoteException;

    int sysGetRandom(byte[] bArr, int i) throws RemoteException;

    int sysGetDebugData(byte[] bArr, int i) throws RemoteException;

    int sysPutDebugData(byte[] bArr, int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/system/BasicOpt$Default.class */
    public static class Default implements BasicOpt {
        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public String getSysParam(String key) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int setSysParam(String key, String value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int buzzerOnDevice(int times) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int ledStatusOnDevice(int ledIndex, int ledStatus) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int setScreenMode(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public void resetSP() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int sysPowerManager(int mode) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int sysGetRandom(byte[] randData, int len) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int sysGetDebugData(byte[] data, int len) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
        public int sysPutDebugData(byte[] data, int len) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/system/BasicOpt$Stub.class */
    public static abstract class Stub extends Binder implements BasicOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.system.BasicOpt";
        static final int TRANSACTION_getSysParam = 1;
        static final int TRANSACTION_setSysParam = 2;
        static final int TRANSACTION_buzzerOnDevice = 3;
        static final int TRANSACTION_ledStatusOnDevice = 4;
        static final int TRANSACTION_setScreenMode = 5;
        static final int TRANSACTION_resetSP = 6;
        static final int TRANSACTION_sysPowerManager = 7;
        static final int TRANSACTION_sysGetRandom = 8;
        static final int TRANSACTION_sysGetDebugData = 9;
        static final int TRANSACTION_sysPutDebugData = 10;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static BasicOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof BasicOpt)) {
                return (BasicOpt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _result = getSysParam(data.readString());
                    reply.writeNoException();
                    reply.writeString(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    String _arg1 = data.readString();
                    int _result2 = setSysParam(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = buzzerOnDevice(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _arg12 = data.readInt();
                    int _result4 = ledStatusOnDevice(_arg02, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _result5 = setScreenMode(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    resetSP();
                    reply.writeNoException();
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result6 = sysPowerManager(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg03 = data.createByteArray();
                    int _arg13 = data.readInt();
                    int _result7 = sysGetRandom(_arg03, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    reply.writeByteArray(_arg03);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg04 = data.createByteArray();
                    int _arg14 = data.readInt();
                    int _result8 = sysGetDebugData(_arg04, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    reply.writeByteArray(_arg04);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg05 = data.createByteArray();
                    int _arg15 = data.readInt();
                    int _result9 = sysPutDebugData(_arg05, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg05);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/system/BasicOpt$Stub$Proxy.class */
        private static class Proxy implements BasicOpt {
            private IBinder mRemote;
            public static BasicOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public int buzzerOnDevice(int times) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(times);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iBuzzerOnDevice = Stub.getDefaultImpl().buzzerOnDevice(times);
                        _reply.recycle();
                        _data.recycle();
                        return iBuzzerOnDevice;
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public void resetSP() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().resetSP();
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public int sysPowerManager(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysPowerManager = Stub.getDefaultImpl().sysPowerManager(mode);
                        _reply.recycle();
                        _data.recycle();
                        return iSysPowerManager;
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public int sysGetRandom(byte[] randData, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(randData);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public int sysGetDebugData(byte[] data, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.system.BasicOpt
            public int sysPutDebugData(byte[] data, int len) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    _data.writeInt(len);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
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
        }

        public static boolean setDefaultImpl(BasicOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static BasicOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
