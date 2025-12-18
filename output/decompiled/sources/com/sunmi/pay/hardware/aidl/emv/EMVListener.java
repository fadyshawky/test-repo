package com.sunmi.pay.hardware.aidl.emv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVListener.class */
public interface EMVListener extends IInterface {
    void requestShowPinPad(int i) throws RemoteException;

    void onProcessEnd() throws RemoteException;

    void onError(int i) throws RemoteException;

    void offlineApproval() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVListener$Default.class */
    public static class Default implements EMVListener {
        @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
        public void requestShowPinPad(int pinType) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
        public void onProcessEnd() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
        public void onError(int erroCode) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
        public void offlineApproval() throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVListener$Stub.class */
    public static abstract class Stub extends Binder implements EMVListener {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.emv.EMVListener";
        static final int TRANSACTION_requestShowPinPad = 1;
        static final int TRANSACTION_onProcessEnd = 2;
        static final int TRANSACTION_onError = 3;
        static final int TRANSACTION_offlineApproval = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static EMVListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof EMVListener)) {
                return (EMVListener) iin;
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
                    requestShowPinPad(_arg0);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    onProcessEnd();
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    onError(_arg02);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    offlineApproval();
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/emv/EMVListener$Stub$Proxy.class */
        private static class Proxy implements EMVListener {
            private IBinder mRemote;
            public static EMVListener sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
            public void requestShowPinPad(int pinType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pinType);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().requestShowPinPad(pinType);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
            public void onProcessEnd() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onProcessEnd();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
            public void onError(int erroCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(erroCode);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onError(erroCode);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.emv.EMVListener
            public void offlineApproval() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().offlineApproval();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(EMVListener impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static EMVListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
