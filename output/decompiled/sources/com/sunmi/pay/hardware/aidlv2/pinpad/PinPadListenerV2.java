package com.sunmi.pay.hardware.aidlv2.pinpad;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadListenerV2.class */
public interface PinPadListenerV2 extends IInterface {
    void onPinLength(int i) throws RemoteException;

    void onConfirm(int i, byte[] bArr) throws RemoteException;

    void onCancel() throws RemoteException;

    void onError(int i) throws RemoteException;

    void onHover(int i, byte[] bArr) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadListenerV2$Default.class */
    public static class Default implements PinPadListenerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
        public void onPinLength(int length) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
        public void onConfirm(int type, byte[] pinBlock) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
        public void onCancel() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
        public void onError(int errorCode) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
        public void onHover(int event, byte[] data) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadListenerV2$Stub.class */
    public static abstract class Stub extends Binder implements PinPadListenerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2";
        static final int TRANSACTION_onPinLength = 1;
        static final int TRANSACTION_onConfirm = 2;
        static final int TRANSACTION_onCancel = 3;
        static final int TRANSACTION_onError = 4;
        static final int TRANSACTION_onHover = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PinPadListenerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PinPadListenerV2)) {
                return (PinPadListenerV2) iin;
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
                    int _arg0 = data.readInt();
                    onPinLength(_arg0);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    byte[] _arg1 = data.createByteArray();
                    onConfirm(_arg02, _arg1);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onCancel();
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    onError(_arg03);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    byte[] _arg12 = data.createByteArray();
                    onHover(_arg04, _arg12);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadListenerV2$Stub$Proxy.class */
        private static class Proxy implements PinPadListenerV2 {
            private IBinder mRemote;
            public static PinPadListenerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
            public void onPinLength(int length) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(length);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onPinLength(length);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
            public void onConfirm(int type, byte[] pinBlock) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeByteArray(pinBlock);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onConfirm(type, pinBlock);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
            public void onCancel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onCancel();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
            public void onError(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(errorCode);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onError(errorCode);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
            public void onHover(int event, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(event);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onHover(event, data);
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

        public static boolean setDefaultImpl(PinPadListenerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PinPadListenerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
