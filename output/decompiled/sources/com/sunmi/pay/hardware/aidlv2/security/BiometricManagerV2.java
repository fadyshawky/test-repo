package com.sunmi.pay.hardware.aidlv2.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/BiometricManagerV2.class */
public interface BiometricManagerV2 extends IInterface {
    int sysFaceRegisterFeature(byte[] bArr, float[] fArr) throws RemoteException;

    int sysDeleterFeature(byte[] bArr) throws RemoteException;

    int sysSearchFeature(float[] fArr, float f, byte[] bArr) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/BiometricManagerV2$Default.class */
    public static class Default implements BiometricManagerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
        public int sysFaceRegisterFeature(byte[] userid, float[] feature) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
        public int sysDeleterFeature(byte[] userid) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
        public int sysSearchFeature(float[] feature, float threshold, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/BiometricManagerV2$Stub.class */
    public static abstract class Stub extends Binder implements BiometricManagerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2";
        static final int TRANSACTION_sysFaceRegisterFeature = 1;
        static final int TRANSACTION_sysDeleterFeature = 2;
        static final int TRANSACTION_sysSearchFeature = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static BiometricManagerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof BiometricManagerV2)) {
                return (BiometricManagerV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _arg2;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg0 = data.createByteArray();
                    float[] _arg1 = data.createFloatArray();
                    int _result = sysFaceRegisterFeature(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg02 = data.createByteArray();
                    int _result2 = sysDeleterFeature(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    float[] _arg03 = data.createFloatArray();
                    float _arg12 = data.readFloat();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length];
                    }
                    int _result3 = sysSearchFeature(_arg03, _arg12, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg2);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/BiometricManagerV2$Stub$Proxy.class */
        private static class Proxy implements BiometricManagerV2 {
            private IBinder mRemote;
            public static BiometricManagerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
            public int sysFaceRegisterFeature(byte[] userid, float[] feature) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(userid);
                    _data.writeFloatArray(feature);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysFaceRegisterFeature = Stub.getDefaultImpl().sysFaceRegisterFeature(userid, feature);
                        _reply.recycle();
                        _data.recycle();
                        return iSysFaceRegisterFeature;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
            public int sysDeleterFeature(byte[] userid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(userid);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysDeleterFeature = Stub.getDefaultImpl().sysDeleterFeature(userid);
                        _reply.recycle();
                        _data.recycle();
                        return iSysDeleterFeature;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.BiometricManagerV2
            public int sysSearchFeature(float[] feature, float threshold, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloatArray(feature);
                    _data.writeFloat(threshold);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysSearchFeature = Stub.getDefaultImpl().sysSearchFeature(feature, threshold, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iSysSearchFeature;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outData);
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

        public static boolean setDefaultImpl(BiometricManagerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static BiometricManagerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
