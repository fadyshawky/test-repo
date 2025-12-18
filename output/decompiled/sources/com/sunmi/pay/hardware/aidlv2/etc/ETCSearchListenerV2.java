package com.sunmi.pay.hardware.aidlv2.etc;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.bean.ETCInfoV2;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchListenerV2.class */
public interface ETCSearchListenerV2 extends IInterface {
    void onSuccess(List<ETCInfoV2> list) throws RemoteException;

    void onError(int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchListenerV2$Default.class */
    public static class Default implements ETCSearchListenerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2
        public void onSuccess(List<ETCInfoV2> list) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2
        public void onError(int code) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchListenerV2$Stub.class */
    public static abstract class Stub extends Binder implements ETCSearchListenerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2";
        static final int TRANSACTION_onSuccess = 1;
        static final int TRANSACTION_onError = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ETCSearchListenerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ETCSearchListenerV2)) {
                return (ETCSearchListenerV2) iin;
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
                    List<ETCInfoV2> _arg0 = data.createTypedArrayList(ETCInfoV2.CREATOR);
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

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCSearchListenerV2$Stub$Proxy.class */
        private static class Proxy implements ETCSearchListenerV2 {
            private IBinder mRemote;
            public static ETCSearchListenerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2
            public void onSuccess(List<ETCInfoV2> list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(list);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onSuccess(list);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2
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

        public static boolean setDefaultImpl(ETCSearchListenerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ETCSearchListenerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
