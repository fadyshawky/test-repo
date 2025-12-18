package com.sunmi.pay.hardware.aidlv2.etc;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchTradeOBUListenerV2.class */
public interface ETCSearchTradeOBUListenerV2 extends IInterface {
    void onSuccess(Bundle bundle) throws RemoteException;

    void onError(int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchTradeOBUListenerV2$Default.class */
    public static class Default implements ETCSearchTradeOBUListenerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2
        public void onSuccess(Bundle bundle) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2
        public void onError(int code) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchTradeOBUListenerV2$Stub.class */
    public static abstract class Stub extends Binder implements ETCSearchTradeOBUListenerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2";
        static final int TRANSACTION_onSuccess = 1;
        static final int TRANSACTION_onError = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ETCSearchTradeOBUListenerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ETCSearchTradeOBUListenerV2)) {
                return (ETCSearchTradeOBUListenerV2) iin;
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
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onSuccess(_arg0);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    onError(_arg02);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchTradeOBUListenerV2$Stub$Proxy.class */
        private static class Proxy implements ETCSearchTradeOBUListenerV2 {
            private IBinder mRemote;
            public static ETCSearchTradeOBUListenerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2
            public void onSuccess(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onSuccess(bundle);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2
            public void onError(int code) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onError(code);
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

        public static boolean setDefaultImpl(ETCSearchTradeOBUListenerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ETCSearchTradeOBUListenerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
