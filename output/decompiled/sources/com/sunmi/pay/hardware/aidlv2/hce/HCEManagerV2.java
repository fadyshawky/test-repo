package com.sunmi.pay.hardware.aidlv2.hce;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/hce/HCEManagerV2.class */
public interface HCEManagerV2 extends IInterface {
    int hceOpen(int i, byte[] bArr) throws RemoteException;

    int hceNdefWrite(byte[] bArr) throws RemoteException;

    int hceNdefRead(byte[] bArr) throws RemoteException;

    int hceClose() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/hce/HCEManagerV2$Default.class */
    public static class Default implements HCEManagerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
        public int hceOpen(int cardType, byte[] param) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
        public int hceNdefWrite(byte[] ndefMsg) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
        public int hceNdefRead(byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
        public int hceClose() throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/hce/HCEManagerV2$Stub.class */
    public static abstract class Stub extends Binder implements HCEManagerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2";
        static final int TRANSACTION_hceOpen = 1;
        static final int TRANSACTION_hceNdefWrite = 2;
        static final int TRANSACTION_hceNdefRead = 3;
        static final int TRANSACTION_hceClose = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static HCEManagerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof HCEManagerV2)) {
                return (HCEManagerV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    byte[] _arg1 = data.createByteArray();
                    int _result = hceOpen(_arg02, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg03 = data.createByteArray();
                    int _result2 = hceNdefWrite(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0_length = data.readInt();
                    if (_arg0_length < 0) {
                        _arg0 = null;
                    } else {
                        _arg0 = new byte[_arg0_length];
                    }
                    int _result3 = hceNdefRead(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg0);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result4 = hceClose();
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/hce/HCEManagerV2$Stub$Proxy.class */
        private static class Proxy implements HCEManagerV2 {
            private IBinder mRemote;
            public static HCEManagerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
            public int hceOpen(int cardType, byte[] param) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(param);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHceOpen = Stub.getDefaultImpl().hceOpen(cardType, param);
                        _reply.recycle();
                        _data.recycle();
                        return iHceOpen;
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

            @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
            public int hceNdefWrite(byte[] ndefMsg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(ndefMsg);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHceNdefWrite = Stub.getDefaultImpl().hceNdefWrite(ndefMsg);
                        _reply.recycle();
                        _data.recycle();
                        return iHceNdefWrite;
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

            @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
            public int hceNdefRead(byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHceNdefRead = Stub.getDefaultImpl().hceNdefRead(outData);
                        _reply.recycle();
                        _data.recycle();
                        return iHceNdefRead;
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

            @Override // com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2
            public int hceClose() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHceClose = Stub.getDefaultImpl().hceClose();
                        _reply.recycle();
                        _data.recycle();
                        return iHceClose;
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

        public static boolean setDefaultImpl(HCEManagerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static HCEManagerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
