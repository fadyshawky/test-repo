package com.sunmi.pay.hardware.aidl.readcard;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.bean.CardInfo;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardCallback.class */
public interface ReadCardCallback extends IInterface {
    void onCardDetected(CardInfo cardInfo) throws RemoteException;

    void onError(int i, String str) throws RemoteException;

    void onStartCheckCard() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardCallback$Default.class */
    public static class Default implements ReadCardCallback {
        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
        public void onCardDetected(CardInfo cardInfo) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
        public void onError(int code, String message) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
        public void onStartCheckCard() throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardCallback$Stub.class */
    public static abstract class Stub extends Binder implements ReadCardCallback {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback";
        static final int TRANSACTION_onCardDetected = 1;
        static final int TRANSACTION_onError = 2;
        static final int TRANSACTION_onStartCheckCard = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ReadCardCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ReadCardCallback)) {
                return (ReadCardCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CardInfo _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = CardInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onCardDetected(_arg0);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    String _arg1 = data.readString();
                    onError(_arg02, _arg1);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    onStartCheckCard();
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardCallback$Stub$Proxy.class */
        private static class Proxy implements ReadCardCallback {
            private IBinder mRemote;
            public static ReadCardCallback sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
            public void onCardDetected(CardInfo cardInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (cardInfo != null) {
                        _data.writeInt(1);
                        cardInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onCardDetected(cardInfo);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
            public void onError(int code, String message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(message);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback
            public void onStartCheckCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onStartCheckCard();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ReadCardCallback impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ReadCardCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
