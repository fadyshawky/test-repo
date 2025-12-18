package com.sunmi.pay.hardware.aidl.readcard;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/CheckCardCallback.class */
public interface CheckCardCallback extends IInterface {
    void onStartCheckCard() throws RemoteException;

    void findMagCard(Bundle bundle) throws RemoteException;

    void findICCard(String str) throws RemoteException;

    void findRFCard(String str) throws RemoteException;

    void onError(int i, String str) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/CheckCardCallback$Default.class */
    public static class Default implements CheckCardCallback {
        @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
        public void onStartCheckCard() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
        public void findMagCard(Bundle bundle) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
        public void findICCard(String atr) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
        public void findRFCard(String uuid) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
        public void onError(int code, String message) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/CheckCardCallback$Stub.class */
    public static abstract class Stub extends Binder implements CheckCardCallback {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback";
        static final int TRANSACTION_onStartCheckCard = 1;
        static final int TRANSACTION_findMagCard = 2;
        static final int TRANSACTION_findICCard = 3;
        static final int TRANSACTION_findRFCard = 4;
        static final int TRANSACTION_onError = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static CheckCardCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof CheckCardCallback)) {
                return (CheckCardCallback) iin;
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
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    onStartCheckCard();
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    findMagCard(_arg0);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    findICCard(_arg02);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    findRFCard(_arg03);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    String _arg1 = data.readString();
                    onError(_arg04, _arg1);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/CheckCardCallback$Stub$Proxy.class */
        private static class Proxy implements CheckCardCallback {
            private IBinder mRemote;
            public static CheckCardCallback sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
            public void onStartCheckCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onStartCheckCard();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
            public void findMagCard(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findMagCard(bundle);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
            public void findICCard(String atr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(atr);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findICCard(atr);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
            public void findRFCard(String uuid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findRFCard(uuid);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback
            public void onError(int code, String message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(message);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onError(code, message);
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

        public static boolean setDefaultImpl(CheckCardCallback impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static CheckCardCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
