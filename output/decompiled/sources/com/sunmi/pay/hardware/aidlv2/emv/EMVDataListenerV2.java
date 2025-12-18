package com.sunmi.pay.hardware.aidlv2.emv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVDataListenerV2.class */
public interface EMVDataListenerV2 extends IInterface {
    void onRequestDETData(byte[] bArr) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVDataListenerV2$Default.class */
    public static class Default implements EMVDataListenerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVDataListenerV2
        public void onRequestDETData(byte[] data) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVDataListenerV2$Stub.class */
    public static abstract class Stub extends Binder implements EMVDataListenerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.emv.EMVDataListenerV2";
        static final int TRANSACTION_onRequestDETData = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static EMVDataListenerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof EMVDataListenerV2)) {
                return (EMVDataListenerV2) iin;
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
                    byte[] _arg0 = data.createByteArray();
                    onRequestDETData(_arg0);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVDataListenerV2$Stub$Proxy.class */
        private static class Proxy implements EMVDataListenerV2 {
            private IBinder mRemote;
            public static EMVDataListenerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVDataListenerV2
            public void onRequestDETData(byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onRequestDETData(data);
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

        public static boolean setDefaultImpl(EMVDataListenerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static EMVDataListenerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
