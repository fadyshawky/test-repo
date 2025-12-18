package com.sunmi.pay.hardware.aidlv2.test;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/test/TestOptV2.class */
public interface TestOptV2 extends IInterface {
    int testTransmission(byte[] bArr, byte[] bArr2) throws RemoteException;

    int setParam(Bundle bundle) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/test/TestOptV2$Default.class */
    public static class Default implements TestOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.test.TestOptV2
        public int testTransmission(byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.test.TestOptV2
        public int setParam(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/test/TestOptV2$Stub.class */
    public static abstract class Stub extends Binder implements TestOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.test.TestOptV2";
        static final int TRANSACTION_testTransmission = 1;
        static final int TRANSACTION_setParam = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static TestOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof TestOptV2)) {
                return (TestOptV2) iin;
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
            byte[] _arg1;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg02 = data.createByteArray();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length];
                    }
                    int _result = testTransmission(_arg02, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    reply.writeByteArray(_arg1);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result2 = setParam(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/test/TestOptV2$Stub$Proxy.class */
        private static class Proxy implements TestOptV2 {
            private IBinder mRemote;
            public static TestOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.test.TestOptV2
            public int testTransmission(byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTestTransmission = Stub.getDefaultImpl().testTransmission(dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iTestTransmission;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(dataOut);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.test.TestOptV2
            public int setParam(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        _data.writeInt(1);
                        bundle.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int param = Stub.getDefaultImpl().setParam(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return param;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
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

        public static boolean setDefaultImpl(TestOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static TestOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
