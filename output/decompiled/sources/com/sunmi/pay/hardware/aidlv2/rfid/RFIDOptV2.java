package com.sunmi.pay.hardware.aidlv2.rfid;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/rfid/RFIDOptV2.class */
public interface RFIDOptV2 extends IInterface {
    int m112Reset(int i, int i2) throws RemoteException;

    int m112GetVersion(int i, int i2, byte[] bArr) throws RemoteException;

    int m112GetCPUId(int i, int i2, byte[] bArr) throws RemoteException;

    int m112QueryTagInField(int i, int i2, byte[] bArr) throws RemoteException;

    int m112EnableAutoDetectMode(int i, int i2, int i3) throws RemoteException;

    int m112DisableAutoDetectMode(int i, int i2) throws RemoteException;

    int m112WriteT557Block(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/rfid/RFIDOptV2$Default.class */
    public static class Default implements RFIDOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112Reset(int srcAddress, int destAddress) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112GetVersion(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112GetCPUId(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112QueryTagInField(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112EnableAutoDetectMode(int srcAddress, int destAddress, int freq) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112DisableAutoDetectMode(int srcAddress, int destAddress) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
        public int m112WriteT557Block(int srcAddress, int destAddress, int page, int block, int lockFlag, byte[] data, int pwdFlag, byte[] pwd) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/rfid/RFIDOptV2$Stub.class */
    public static abstract class Stub extends Binder implements RFIDOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2";
        static final int TRANSACTION_m112Reset = 1;
        static final int TRANSACTION_m112GetVersion = 2;
        static final int TRANSACTION_m112GetCPUId = 3;
        static final int TRANSACTION_m112QueryTagInField = 4;
        static final int TRANSACTION_m112EnableAutoDetectMode = 5;
        static final int TRANSACTION_m112DisableAutoDetectMode = 6;
        static final int TRANSACTION_m112WriteT557Block = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static RFIDOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof RFIDOptV2)) {
                return (RFIDOptV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _arg2;
            byte[] _arg22;
            byte[] _arg23;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0 = data.readInt();
                    int _arg1 = data.readInt();
                    int _result = m112Reset(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _arg12 = data.readInt();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length];
                    }
                    int _result2 = m112GetVersion(_arg02, _arg12, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeByteArray(_arg23);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _arg13 = data.readInt();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length2];
                    }
                    int _result3 = m112GetCPUId(_arg03, _arg13, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg22);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg14 = data.readInt();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length3];
                    }
                    int _result4 = m112QueryTagInField(_arg04, _arg14, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    reply.writeByteArray(_arg2);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _arg15 = data.readInt();
                    int _arg24 = data.readInt();
                    int _result5 = m112EnableAutoDetectMode(_arg05, _arg15, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _arg16 = data.readInt();
                    int _result6 = m112DisableAutoDetectMode(_arg06, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg17 = data.readInt();
                    int _arg25 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    byte[] _arg5 = data.createByteArray();
                    int _arg6 = data.readInt();
                    byte[] _arg7 = data.createByteArray();
                    int _result7 = m112WriteT557Block(_arg07, _arg17, _arg25, _arg3, _arg4, _arg5, _arg6, _arg7);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/rfid/RFIDOptV2$Stub$Proxy.class */
        private static class Proxy implements RFIDOptV2 {
            private IBinder mRemote;
            public static RFIDOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112Reset(int srcAddress, int destAddress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112Reset = Stub.getDefaultImpl().m112Reset(srcAddress, destAddress);
                        _reply.recycle();
                        _data.recycle();
                        return iM112Reset;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112GetVersion(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112GetVersion = Stub.getDefaultImpl().m112GetVersion(srcAddress, destAddress, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iM112GetVersion;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112GetCPUId(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112GetCPUId = Stub.getDefaultImpl().m112GetCPUId(srcAddress, destAddress, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iM112GetCPUId;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112QueryTagInField(int srcAddress, int destAddress, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112QueryTagInField = Stub.getDefaultImpl().m112QueryTagInField(srcAddress, destAddress, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iM112QueryTagInField;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112EnableAutoDetectMode(int srcAddress, int destAddress, int freq) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    _data.writeInt(freq);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112EnableAutoDetectMode = Stub.getDefaultImpl().m112EnableAutoDetectMode(srcAddress, destAddress, freq);
                        _reply.recycle();
                        _data.recycle();
                        return iM112EnableAutoDetectMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112DisableAutoDetectMode(int srcAddress, int destAddress) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112DisableAutoDetectMode = Stub.getDefaultImpl().m112DisableAutoDetectMode(srcAddress, destAddress);
                        _reply.recycle();
                        _data.recycle();
                        return iM112DisableAutoDetectMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.rfid.RFIDOptV2
            public int m112WriteT557Block(int srcAddress, int destAddress, int page, int block, int lockFlag, byte[] data, int pwdFlag, byte[] pwd) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcAddress);
                    _data.writeInt(destAddress);
                    _data.writeInt(page);
                    _data.writeInt(block);
                    _data.writeInt(lockFlag);
                    _data.writeByteArray(data);
                    _data.writeInt(pwdFlag);
                    _data.writeByteArray(pwd);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iM112WriteT557Block = Stub.getDefaultImpl().m112WriteT557Block(srcAddress, destAddress, page, block, lockFlag, data, pwdFlag, pwd);
                        _reply.recycle();
                        _data.recycle();
                        return iM112WriteT557Block;
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
        }

        public static boolean setDefaultImpl(RFIDOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static RFIDOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
