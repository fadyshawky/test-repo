package com.sunmi.pay.hardware.aidlv2.security;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/NoLostKeyManagerV2.class */
public interface NoLostKeyManagerV2 extends IInterface {
    int saveKey(Bundle bundle) throws RemoteException;

    int dataEncrypt(Bundle bundle, byte[] bArr) throws RemoteException;

    int dataDecrypt(Bundle bundle, byte[] bArr) throws RemoteException;

    int getKeyCheckValue(Bundle bundle, byte[] bArr) throws RemoteException;

    int deleteKey(Bundle bundle) throws RemoteException;

    int generateRSAKeypair(Bundle bundle, byte[] bArr) throws RemoteException;

    int injectRSAKey(Bundle bundle) throws RemoteException;

    int getRsaPubKey(int i, Bundle bundle) throws RemoteException;

    int rsaRecover(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int generateEccKeypair(int i, int i2, byte[] bArr) throws RemoteException;

    int injectEccPubKey(int i, int i2, byte[] bArr) throws RemoteException;

    int injectEccPvtKey(int i, int i2, byte[] bArr) throws RemoteException;

    int getEccPubKey(int i, Bundle bundle) throws RemoteException;

    int eccRecover(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int eccSign(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int eccVerify(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int saveCert(Bundle bundle) throws RemoteException;

    int getCert(int i, Bundle bundle) throws RemoteException;

    int generateSM2Keypair(int i, Bundle bundle) throws RemoteException;

    int injectSM2Key(int i, Bundle bundle) throws RemoteException;

    int sm2Sign(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2VerifySign(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2EncryptData(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int sm2DecryptData(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int readSM2Key(int i, Bundle bundle) throws RemoteException;

    int calcSM3HashWithID(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2SingleSign(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/NoLostKeyManagerV2$Default.class */
    public static class Default implements NoLostKeyManagerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int saveKey(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int dataEncrypt(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int dataDecrypt(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int getKeyCheckValue(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int deleteKey(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int generateRSAKeypair(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int injectRSAKey(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int getRsaPubKey(int keyIndex, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int rsaRecover(int keyIndex, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int generateEccKeypair(int pvkIndex, int keySize, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int injectEccPubKey(int pukIndex, int keySize, byte[] pubKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int injectEccPvtKey(int pvkIndex, int keySize, byte[] pvkKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int getEccPubKey(int keyIndex, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int eccRecover(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int eccSign(int pvkIndex, int hashType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int eccVerify(int pukIndex, int hashType, byte[] dataIn, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int saveCert(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int getCert(int certIndex, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int generateSM2Keypair(int pvkIndex, Bundle pubKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int injectSM2Key(int keyIndex, Bundle keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int sm2Sign(int pukIndex, int pvkIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int sm2VerifySign(int pukIndex, byte[] userId, byte[] dataIn, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int sm2EncryptData(int pukIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int sm2DecryptData(int pvkIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int readSM2Key(int keyIndex, Bundle keyInfo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int calcSM3HashWithID(int keyIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
        public int sm2SingleSign(int keyIndex, byte[] hash, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/NoLostKeyManagerV2$Stub.class */
    public static abstract class Stub extends Binder implements NoLostKeyManagerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2";
        static final int TRANSACTION_saveKey = 1;
        static final int TRANSACTION_dataEncrypt = 2;
        static final int TRANSACTION_dataDecrypt = 3;
        static final int TRANSACTION_getKeyCheckValue = 4;
        static final int TRANSACTION_deleteKey = 5;
        static final int TRANSACTION_generateRSAKeypair = 6;
        static final int TRANSACTION_injectRSAKey = 7;
        static final int TRANSACTION_getRsaPubKey = 8;
        static final int TRANSACTION_rsaRecover = 9;
        static final int TRANSACTION_generateEccKeypair = 10;
        static final int TRANSACTION_injectEccPubKey = 11;
        static final int TRANSACTION_injectEccPvtKey = 12;
        static final int TRANSACTION_getEccPubKey = 13;
        static final int TRANSACTION_eccRecover = 14;
        static final int TRANSACTION_eccSign = 15;
        static final int TRANSACTION_eccVerify = 16;
        static final int TRANSACTION_saveCert = 17;
        static final int TRANSACTION_getCert = 18;
        static final int TRANSACTION_generateSM2Keypair = 19;
        static final int TRANSACTION_injectSM2Key = 20;
        static final int TRANSACTION_sm2Sign = 21;
        static final int TRANSACTION_sm2VerifySign = 22;
        static final int TRANSACTION_sm2EncryptData = 23;
        static final int TRANSACTION_sm2DecryptData = 24;
        static final int TRANSACTION_readSM2Key = 25;
        static final int TRANSACTION_calcSM3HashWithID = 26;
        static final int TRANSACTION_sm2SingleSign = 27;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static NoLostKeyManagerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof NoLostKeyManagerV2)) {
                return (NoLostKeyManagerV2) iin;
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
            byte[] _arg3;
            byte[] _arg22;
            byte[] _arg23;
            byte[] _arg4;
            Bundle _arg1;
            Bundle _arg0;
            byte[] _arg32;
            byte[] _arg24;
            byte[] _arg25;
            byte[] _arg33;
            Bundle _arg02;
            Bundle _arg03;
            byte[] _arg12;
            Bundle _arg04;
            Bundle _arg05;
            byte[] _arg13;
            Bundle _arg06;
            byte[] _arg14;
            Bundle _arg07;
            byte[] _arg15;
            Bundle _arg08;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    int _result = saveKey(_arg08);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg15 = null;
                    } else {
                        _arg15 = new byte[_arg1_length];
                    }
                    int _result2 = dataEncrypt(_arg07, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeByteArray(_arg15);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _arg1_length2 = data.readInt();
                    if (_arg1_length2 < 0) {
                        _arg14 = null;
                    } else {
                        _arg14 = new byte[_arg1_length2];
                    }
                    int _result3 = dataDecrypt(_arg06, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg14);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _arg1_length3 = data.readInt();
                    if (_arg1_length3 < 0) {
                        _arg13 = null;
                    } else {
                        _arg13 = new byte[_arg1_length3];
                    }
                    int _result4 = getKeyCheckValue(_arg05, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    reply.writeByteArray(_arg13);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    int _result5 = deleteKey(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _arg1_length4 = data.readInt();
                    if (_arg1_length4 < 0) {
                        _arg12 = null;
                    } else {
                        _arg12 = new byte[_arg1_length4];
                    }
                    int _result6 = generateRSAKeypair(_arg03, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    reply.writeByteArray(_arg12);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result7 = injectRSAKey(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    Bundle _arg16 = new Bundle();
                    int _result8 = getRsaPubKey(_arg09, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    if (_arg16 != null) {
                        reply.writeInt(1);
                        _arg16.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _arg17 = data.readInt();
                    byte[] _arg26 = data.createByteArray();
                    int _arg3_length = data.readInt();
                    if (_arg3_length < 0) {
                        _arg33 = null;
                    } else {
                        _arg33 = new byte[_arg3_length];
                    }
                    int _result9 = rsaRecover(_arg010, _arg17, _arg26, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg33);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _arg18 = data.readInt();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg25 = null;
                    } else {
                        _arg25 = new byte[_arg2_length];
                    }
                    int _result10 = generateEccKeypair(_arg011, _arg18, _arg25);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeByteArray(_arg25);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _arg19 = data.readInt();
                    byte[] _arg27 = data.createByteArray();
                    int _result11 = injectEccPubKey(_arg012, _arg19, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    int _arg110 = data.readInt();
                    byte[] _arg28 = data.createByteArray();
                    int _result12 = injectEccPvtKey(_arg013, _arg110, _arg28);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    Bundle _arg111 = new Bundle();
                    int _result13 = getEccPubKey(_arg014, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    if (_arg111 != null) {
                        reply.writeInt(1);
                        _arg111.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    byte[] _arg112 = data.createByteArray();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg24 = null;
                    } else {
                        _arg24 = new byte[_arg2_length2];
                    }
                    int _result14 = eccRecover(_arg015, _arg112, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    reply.writeByteArray(_arg24);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    int _arg113 = data.readInt();
                    byte[] _arg29 = data.createByteArray();
                    int _arg3_length2 = data.readInt();
                    if (_arg3_length2 < 0) {
                        _arg32 = null;
                    } else {
                        _arg32 = new byte[_arg3_length2];
                    }
                    int _result15 = eccSign(_arg016, _arg113, _arg29, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    reply.writeByteArray(_arg32);
                    break;
                case TRANSACTION_eccVerify /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    int _arg114 = data.readInt();
                    byte[] _arg210 = data.createByteArray();
                    byte[] _arg34 = data.createByteArray();
                    int _result16 = eccVerify(_arg017, _arg114, _arg210, _arg34);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    break;
                case TRANSACTION_saveCert /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result17 = saveCert(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_getCert /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    Bundle _arg115 = new Bundle();
                    int _result18 = getCert(_arg018, _arg115);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    if (_arg115 != null) {
                        reply.writeInt(1);
                        _arg115.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_generateSM2Keypair /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    Bundle _arg116 = new Bundle();
                    int _result19 = generateSM2Keypair(_arg019, _arg116);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    if (_arg116 != null) {
                        reply.writeInt(1);
                        _arg116.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_injectSM2Key /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    int _result20 = injectSM2Key(_arg020, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    break;
                case TRANSACTION_sm2Sign /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    int _arg117 = data.readInt();
                    byte[] _arg211 = data.createByteArray();
                    byte[] _arg35 = data.createByteArray();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new byte[_arg4_length];
                    }
                    int _result21 = sm2Sign(_arg021, _arg117, _arg211, _arg35, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    reply.writeByteArray(_arg4);
                    break;
                case TRANSACTION_sm2VerifySign /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    byte[] _arg118 = data.createByteArray();
                    byte[] _arg212 = data.createByteArray();
                    byte[] _arg36 = data.createByteArray();
                    int _result22 = sm2VerifySign(_arg022, _arg118, _arg212, _arg36);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    break;
                case TRANSACTION_sm2EncryptData /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    byte[] _arg119 = data.createByteArray();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length3];
                    }
                    int _result23 = sm2EncryptData(_arg023, _arg119, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    reply.writeByteArray(_arg23);
                    break;
                case TRANSACTION_sm2DecryptData /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    byte[] _arg120 = data.createByteArray();
                    int _arg2_length4 = data.readInt();
                    if (_arg2_length4 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length4];
                    }
                    int _result24 = sm2DecryptData(_arg024, _arg120, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    reply.writeByteArray(_arg22);
                    break;
                case TRANSACTION_readSM2Key /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg025 = data.readInt();
                    Bundle _arg121 = new Bundle();
                    int _result25 = readSM2Key(_arg025, _arg121);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    if (_arg121 != null) {
                        reply.writeInt(1);
                        _arg121.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_calcSM3HashWithID /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg026 = data.readInt();
                    byte[] _arg122 = data.createByteArray();
                    byte[] _arg213 = data.createByteArray();
                    int _arg3_length3 = data.readInt();
                    if (_arg3_length3 < 0) {
                        _arg3 = null;
                    } else {
                        _arg3 = new byte[_arg3_length3];
                    }
                    int _result26 = calcSM3HashWithID(_arg026, _arg122, _arg213, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    reply.writeByteArray(_arg3);
                    break;
                case TRANSACTION_sm2SingleSign /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    byte[] _arg123 = data.createByteArray();
                    int _arg2_length5 = data.readInt();
                    if (_arg2_length5 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length5];
                    }
                    int _result27 = sm2SingleSign(_arg027, _arg123, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    reply.writeByteArray(_arg2);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/NoLostKeyManagerV2$Stub$Proxy.class */
        private static class Proxy implements NoLostKeyManagerV2 {
            private IBinder mRemote;
            public static NoLostKeyManagerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int saveKey(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKey = Stub.getDefaultImpl().saveKey(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int dataEncrypt(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncrypt = Stub.getDefaultImpl().dataEncrypt(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataEncrypt;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int dataDecrypt(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecrypt = Stub.getDefaultImpl().dataDecrypt(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataDecrypt;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int getKeyCheckValue(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int keyCheckValue = Stub.getDefaultImpl().getKeyCheckValue(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return keyCheckValue;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int deleteKey(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteKey = Stub.getDefaultImpl().deleteKey(bundle);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int generateRSAKeypair(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateRSAKeypair = Stub.getDefaultImpl().generateRSAKeypair(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateRSAKeypair;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int injectRSAKey(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectRSAKey = Stub.getDefaultImpl().injectRSAKey(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectRSAKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int getRsaPubKey(int keyIndex, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int rsaPubKey = Stub.getDefaultImpl().getRsaPubKey(keyIndex, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return rsaPubKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int rsaRecover(int keyIndex, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(padding);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iRsaRecover = Stub.getDefaultImpl().rsaRecover(keyIndex, padding, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iRsaRecover;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int generateEccKeypair(int pvkIndex, int keySize, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeInt(keySize);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateEccKeypair = Stub.getDefaultImpl().generateEccKeypair(pvkIndex, keySize, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateEccKeypair;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int injectEccPubKey(int pukIndex, int keySize, byte[] pubKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeInt(keySize);
                    _data.writeByteArray(pubKey);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectEccPubKey = Stub.getDefaultImpl().injectEccPubKey(pukIndex, keySize, pubKey);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectEccPubKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int injectEccPvtKey(int pvkIndex, int keySize, byte[] pvkKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeInt(keySize);
                    _data.writeByteArray(pvkKey);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectEccPvtKey = Stub.getDefaultImpl().injectEccPvtKey(pvkIndex, keySize, pvkKey);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectEccPvtKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int getEccPubKey(int keyIndex, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int eccPubKey = Stub.getDefaultImpl().getEccPubKey(keyIndex, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return eccPubKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int eccRecover(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iEccRecover = Stub.getDefaultImpl().eccRecover(keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iEccRecover;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int eccSign(int pvkIndex, int hashType, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeInt(hashType);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iEccSign = Stub.getDefaultImpl().eccSign(pvkIndex, hashType, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iEccSign;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int eccVerify(int pukIndex, int hashType, byte[] dataIn, byte[] signData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeInt(hashType);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(signData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_eccVerify, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iEccVerify = Stub.getDefaultImpl().eccVerify(pukIndex, hashType, dataIn, signData);
                        _reply.recycle();
                        _data.recycle();
                        return iEccVerify;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int saveCert(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveCert, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveCert = Stub.getDefaultImpl().saveCert(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveCert;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int getCert(int certIndex, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCert, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int cert = Stub.getDefaultImpl().getCert(certIndex, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return cert;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int generateSM2Keypair(int pvkIndex, Bundle pubKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateSM2Keypair, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateSM2Keypair = Stub.getDefaultImpl().generateSM2Keypair(pvkIndex, pubKey);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateSM2Keypair;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        pubKey.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int injectSM2Key(int keyIndex, Bundle keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    if (keyData != null) {
                        _data.writeInt(1);
                        keyData.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectSM2Key, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectSM2Key = Stub.getDefaultImpl().injectSM2Key(keyIndex, keyData);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectSM2Key;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int sm2Sign(int pukIndex, int pvkIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeInt(pvkIndex);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2Sign, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2Sign = Stub.getDefaultImpl().sm2Sign(pukIndex, pvkIndex, userId, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2Sign;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int sm2VerifySign(int pukIndex, byte[] userId, byte[] dataIn, byte[] signData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(signData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2VerifySign, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2VerifySign = Stub.getDefaultImpl().sm2VerifySign(pukIndex, userId, dataIn, signData);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2VerifySign;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int sm2EncryptData(int pukIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2EncryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2EncryptData = Stub.getDefaultImpl().sm2EncryptData(pukIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2EncryptData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int sm2DecryptData(int pvkIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2DecryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2DecryptData = Stub.getDefaultImpl().sm2DecryptData(pvkIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2DecryptData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int readSM2Key(int keyIndex, Bundle keyInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_readSM2Key, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int sM2Key = Stub.getDefaultImpl().readSM2Key(keyIndex, keyInfo);
                        _reply.recycle();
                        _data.recycle();
                        return sM2Key;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        keyInfo.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int calcSM3HashWithID(int keyIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcSM3HashWithID, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcSM3HashWithID = Stub.getDefaultImpl().calcSM3HashWithID(keyIndex, userId, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcSM3HashWithID;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.NoLostKeyManagerV2
            public int sm2SingleSign(int keyIndex, byte[] hash, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(hash);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2SingleSign, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2SingleSign = Stub.getDefaultImpl().sm2SingleSign(keyIndex, hash, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2SingleSign;
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
        }

        public static boolean setDefaultImpl(NoLostKeyManagerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static NoLostKeyManagerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
