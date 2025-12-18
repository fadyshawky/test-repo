package com.sunmi.pay.hardware.aidlv2.security;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/DevCertManagerV2.class */
public interface DevCertManagerV2 extends IInterface {
    int storeDeviceCertPrivateKey(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getDeviceCertificate(int i, byte[] bArr) throws RemoteException;

    int devicePrivateKeyRecover(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getDevKeyState(int i) throws RemoteException;

    int genDevKey(int i, int i2, byte[] bArr) throws RemoteException;

    int saveDevCert(int i, byte[] bArr) throws RemoteException;

    int deleteKey(int i) throws RemoteException;

    int getDeviceCertificateEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int queryPhysicalDevCertWL(int i, Bundle bundle) throws RemoteException;

    int devicePrivateKeyRecoverWL(Bundle bundle, byte[] bArr) throws RemoteException;

    int genDevKeyWL(Bundle bundle, byte[] bArr) throws RemoteException;

    int saveDevCertWL(Bundle bundle) throws RemoteException;

    int deleteKeyWL(Bundle bundle) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/DevCertManagerV2$Default.class */
    public static class Default implements DevCertManagerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int storeDeviceCertPrivateKey(int certIndex, int mode, int encryptIndex, byte[] certData, byte[] pvkData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int getDeviceCertificate(int certIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int devicePrivateKeyRecover(int keyIndex, int mode, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int getDevKeyState(int certIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int genDevKey(int certIndex, int mode, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int saveDevCert(int certIndex, byte[] certData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int deleteKey(int certIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int getDeviceCertificateEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int queryPhysicalDevCertWL(int keyIndexMapped, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int devicePrivateKeyRecoverWL(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int genDevKeyWL(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int saveDevCertWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
        public int deleteKeyWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/DevCertManagerV2$Stub.class */
    public static abstract class Stub extends Binder implements DevCertManagerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2";
        static final int TRANSACTION_storeDeviceCertPrivateKey = 1;
        static final int TRANSACTION_getDeviceCertificate = 2;
        static final int TRANSACTION_devicePrivateKeyRecover = 3;
        static final int TRANSACTION_getDevKeyState = 4;
        static final int TRANSACTION_genDevKey = 5;
        static final int TRANSACTION_saveDevCert = 6;
        static final int TRANSACTION_deleteKey = 7;
        static final int TRANSACTION_getDeviceCertificateEx = 8;
        static final int TRANSACTION_queryPhysicalDevCertWL = 9;
        static final int TRANSACTION_devicePrivateKeyRecoverWL = 10;
        static final int TRANSACTION_genDevKeyWL = 11;
        static final int TRANSACTION_saveDevCertWL = 12;
        static final int TRANSACTION_deleteKeyWL = 13;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static DevCertManagerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof DevCertManagerV2)) {
                return (DevCertManagerV2) iin;
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
            Bundle _arg02;
            Bundle _arg03;
            byte[] _arg1;
            Bundle _arg04;
            byte[] _arg12;
            Bundle _arg05;
            byte[] _arg13;
            byte[] _arg2;
            byte[] _arg4;
            byte[] _arg14;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _arg15 = data.readInt();
                    int _arg22 = data.readInt();
                    byte[] _arg3 = data.createByteArray();
                    byte[] _arg42 = data.createByteArray();
                    int _result = storeDeviceCertPrivateKey(_arg06, _arg15, _arg22, _arg3, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg14 = null;
                    } else {
                        _arg14 = new byte[_arg1_length];
                    }
                    int _result2 = getDeviceCertificate(_arg07, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeByteArray(_arg14);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    int _arg16 = data.readInt();
                    int _arg23 = data.readInt();
                    byte[] _arg32 = data.createByteArray();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new byte[_arg4_length];
                    }
                    int _result3 = devicePrivateKeyRecover(_arg08, _arg16, _arg23, _arg32, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg4);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _result4 = getDevKeyState(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg17 = data.readInt();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length];
                    }
                    int _result5 = genDevKey(_arg010, _arg17, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    reply.writeByteArray(_arg2);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    byte[] _arg18 = data.createByteArray();
                    int _result6 = saveDevCert(_arg011, _arg18);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _result7 = deleteKey(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _arg1_length2 = data.readInt();
                    if (_arg1_length2 < 0) {
                        _arg13 = null;
                    } else {
                        _arg13 = new byte[_arg1_length2];
                    }
                    int _result8 = getDeviceCertificateEx(_arg05, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    reply.writeByteArray(_arg13);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    Bundle _arg19 = new Bundle();
                    int _result9 = queryPhysicalDevCertWL(_arg013, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    if (_arg19 != null) {
                        reply.writeInt(1);
                        _arg19.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    int _arg1_length3 = data.readInt();
                    if (_arg1_length3 < 0) {
                        _arg12 = null;
                    } else {
                        _arg12 = new byte[_arg1_length3];
                    }
                    int _result10 = devicePrivateKeyRecoverWL(_arg04, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeByteArray(_arg12);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _arg1_length4 = data.readInt();
                    if (_arg1_length4 < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length4];
                    }
                    int _result11 = genDevKeyWL(_arg03, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    reply.writeByteArray(_arg1);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result12 = saveDevCertWL(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result13 = deleteKeyWL(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/DevCertManagerV2$Stub$Proxy.class */
        private static class Proxy implements DevCertManagerV2 {
            private IBinder mRemote;
            public static DevCertManagerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int storeDeviceCertPrivateKey(int certIndex, int mode, int encryptIndex, byte[] certData, byte[] pvkData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    _data.writeInt(mode);
                    _data.writeInt(encryptIndex);
                    _data.writeByteArray(certData);
                    _data.writeByteArray(pvkData);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iStoreDeviceCertPrivateKey = Stub.getDefaultImpl().storeDeviceCertPrivateKey(certIndex, mode, encryptIndex, certData, pvkData);
                        _reply.recycle();
                        _data.recycle();
                        return iStoreDeviceCertPrivateKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int getDeviceCertificate(int certIndex, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int deviceCertificate = Stub.getDefaultImpl().getDeviceCertificate(certIndex, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return deviceCertificate;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int devicePrivateKeyRecover(int keyIndex, int mode, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(mode);
                    _data.writeInt(padding);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDevicePrivateKeyRecover = Stub.getDefaultImpl().devicePrivateKeyRecover(keyIndex, mode, padding, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDevicePrivateKeyRecover;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int getDevKeyState(int certIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int devKeyState = Stub.getDefaultImpl().getDevKeyState(certIndex);
                        _reply.recycle();
                        _data.recycle();
                        return devKeyState;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int genDevKey(int certIndex, int mode, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    _data.writeInt(mode);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenDevKey = Stub.getDefaultImpl().genDevKey(certIndex, mode, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenDevKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int saveDevCert(int certIndex, byte[] certData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    _data.writeByteArray(certData);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveDevCert = Stub.getDefaultImpl().saveDevCert(certIndex, certData);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveDevCert;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int deleteKey(int certIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteKey = Stub.getDefaultImpl().deleteKey(certIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int getDeviceCertificateEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int deviceCertificateEx = Stub.getDefaultImpl().getDeviceCertificateEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return deviceCertificateEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int queryPhysicalDevCertWL(int keyIndexMapped, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndexMapped);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryPhysicalDevCertWL = Stub.getDefaultImpl().queryPhysicalDevCertWL(keyIndexMapped, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryPhysicalDevCertWL;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        bundle.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int devicePrivateKeyRecoverWL(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDevicePrivateKeyRecoverWL = Stub.getDefaultImpl().devicePrivateKeyRecoverWL(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDevicePrivateKeyRecoverWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int genDevKeyWL(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenDevKeyWL = Stub.getDefaultImpl().genDevKeyWL(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenDevKeyWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int saveDevCertWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveDevCertWL = Stub.getDefaultImpl().saveDevCertWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveDevCertWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2
            public int deleteKeyWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteKeyWL = Stub.getDefaultImpl().deleteKeyWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteKeyWL;
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

        public static boolean setDefaultImpl(DevCertManagerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static DevCertManagerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
