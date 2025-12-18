package com.sunmi.pay.hardware.aidl.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterCallback.class */
public interface PrinterCallback extends IInterface {
    void onPrinterStatusUpdate(int i) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterCallback$Default.class */
    public static class Default implements PrinterCallback {
        @Override // com.sunmi.pay.hardware.aidl.print.PrinterCallback
        public void onPrinterStatusUpdate(int status) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterCallback$Stub.class */
    public static abstract class Stub extends Binder implements PrinterCallback {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.print.PrinterCallback";
        static final int TRANSACTION_onPrinterStatusUpdate = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PrinterCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PrinterCallback)) {
                return (PrinterCallback) iin;
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
                    onPrinterStatusUpdate(_arg0);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterCallback$Stub$Proxy.class */
        private static class Proxy implements PrinterCallback {
            private IBinder mRemote;
            public static PrinterCallback sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterCallback
            public void onPrinterStatusUpdate(int status) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onPrinterStatusUpdate(status);
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

        public static boolean setDefaultImpl(PrinterCallback impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PrinterCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
