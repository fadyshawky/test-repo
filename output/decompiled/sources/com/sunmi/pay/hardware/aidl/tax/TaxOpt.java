package com.sunmi.pay.hardware.aidl.tax;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/tax/TaxOpt.class */
public interface TaxOpt extends IInterface {
    int taxDataExchange(byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/tax/TaxOpt$Default.class */
    public static class Default implements TaxOpt {
        @Override // com.sunmi.pay.hardware.aidl.tax.TaxOpt
        public int taxDataExchange(byte[] taxSend, byte[] taxRecv) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/tax/TaxOpt$Stub.class */
    public static abstract class Stub extends Binder implements TaxOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.tax.TaxOpt";
        static final int TRANSACTION_taxDataExchange = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static TaxOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof TaxOpt)) {
                return (TaxOpt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg0 = data.createByteArray();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length];
                    }
                    int _result = taxDataExchange(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    reply.writeByteArray(_arg1);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/tax/TaxOpt$Stub$Proxy.class */
        private static class Proxy implements TaxOpt {
            private IBinder mRemote;
            public static TaxOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.tax.TaxOpt
            public int taxDataExchange(byte[] taxSend, byte[] taxRecv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(taxSend);
                    if (taxRecv == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(taxRecv.length);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTaxDataExchange = Stub.getDefaultImpl().taxDataExchange(taxSend, taxRecv);
                        _reply.recycle();
                        _data.recycle();
                        return iTaxDataExchange;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(taxRecv);
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

        public static boolean setDefaultImpl(TaxOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static TaxOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
