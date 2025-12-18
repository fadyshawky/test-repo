package com.sunmi.pay.hardware.aidl.pinpad;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadListener.class */
public interface PinPadListener extends IInterface {
    void onPinLength(int i) throws RemoteException;

    void onConfirm(int i, byte[] bArr) throws RemoteException;

    void onCancel() throws RemoteException;

    void onError(int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadListener$Default.class */
    public static class Default implements PinPadListener {
        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
        public void onPinLength(int length) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
        public void onConfirm(int type, byte[] pinBlock) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
        public void onCancel() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
        public void onError(int errorCode) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadListener$Stub.class */
    public static abstract class Stub extends Binder implements PinPadListener {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.pinpad.PinPadListener";
        static final int TRANSACTION_onPinLength = 1;
        static final int TRANSACTION_onConfirm = 2;
        static final int TRANSACTION_onCancel = 3;
        static final int TRANSACTION_onError = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PinPadListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PinPadListener)) {
                return (PinPadListener) iin;
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
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadListener$Stub$Proxy.class */
        private static class Proxy implements PinPadListener {
            private IBinder mRemote;
            public static PinPadListener sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
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

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
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

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
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

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadListener
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
        }

        public static boolean setDefaultImpl(PinPadListener impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PinPadListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
