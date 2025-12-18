package com.sunmi.pay.hardware.aidl.security;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/security/SecurityOpt.class */
public interface SecurityOpt extends IInterface {
    int saveKey(int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4, boolean z) throws RemoteException;

    int dataEncrypt(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int calcMac(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getEncryptTUSN(String str, byte[] bArr) throws RemoteException;

    int storeSM4Key(byte[] bArr) throws RemoteException;

    int encryptDataBySM4Key(byte[] bArr, byte[] bArr2) throws RemoteException;

    int getSecStatus() throws RemoteException;

    int verifyApkSign(byte[] bArr, byte[] bArr2) throws RemoteException;

    String getAuthStatus(int i) throws RemoteException;

    String getTermStatus() throws RemoteException;

    int setTermStatus() throws RemoteException;

    int sysRequestAuth(byte b, int i, String str, byte[] bArr) throws RemoteException;

    int sysConfirmAuth(byte[] bArr) throws RemoteException;

    int saveTerminalKey(byte[] bArr, byte[] bArr2) throws RemoteException;

    int readTerminalPuk(byte[] bArr) throws RemoteException;

    int getTerminalCertData(byte[] bArr, byte[] bArr2) throws RemoteException;

    int saveBaseKey(int i, byte[] bArr) throws RemoteException;

    int dataDecrypt(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int saveKeyDukpt(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2, int i3, boolean z) throws RemoteException;

    int calcMacDukpt(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int dataEncryptDukpt(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int dataDecryptDukpt(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int calcSecKey(int i, int i2, int i3, byte[] bArr) throws RemoteException;

    int sm1EncryptData(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, int i, byte[] bArr5, byte[] bArr6) throws RemoteException;

    int sm1DecryptData(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, int i, byte[] bArr5, byte[] bArr6) throws RemoteException;

    int sm2EncryptData(byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2DecryptData(byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2SignData(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6) throws RemoteException;

    int sm2VerifySign(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5) throws RemoteException;

    int sm3CalHash(byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm4EncryptData(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4) throws RemoteException;

    int sm4DecryptData(byte[] bArr, byte[] bArr2, int i, byte[] bArr3, byte[] bArr4) throws RemoteException;

    int calcSM4Mac(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/security/SecurityOpt$Default.class */
    public static class Default implements SecurityOpt {
        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int saveKey(int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex, boolean isEncrypt) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int dataEncrypt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int calcMac(int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int getEncryptTUSN(String dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int storeSM4Key(byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int encryptDataBySM4Key(byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int getSecStatus() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int verifyApkSign(byte[] hashMessage, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public String getAuthStatus(int type) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public String getTermStatus() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int setTermStatus() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sysRequestAuth(byte reqType, int authCode, String SN, byte[] authData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sysConfirmAuth(byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int saveTerminalKey(byte[] dataInPuk, byte[] dataInPvk) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int readTerminalPuk(byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int getTerminalCertData(byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int saveBaseKey(int destinationIndex, byte[] keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int dataDecrypt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int saveKeyDukpt(byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptIndex, int encryptType, int keyIndex, boolean isEncrypt) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int calcMacDukpt(int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int dataEncryptDukpt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int dataDecryptDukpt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int calcSecKey(int keySystem, int keyIndex, int ctrlCode, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm1EncryptData(byte[] dataIn, byte[] sk, byte[] ak, byte[] ek, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm1DecryptData(byte[] dataIn, byte[] sk, byte[] ak, byte[] ek, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm2EncryptData(byte[] dataIn, byte[] key, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm2DecryptData(byte[] dataIn, byte[] key, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm2SignData(byte[] userId, byte[] dataIn, byte[] pubKey, byte[] priKey, byte[] sign, byte[] eValue) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm2VerifySign(byte[] userId, byte[] dataIn, byte[] pubKey, byte[] priKey, byte[] sign) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm3CalHash(byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm4EncryptData(byte[] dataIn, byte[] key, int encryptMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int sm4DecryptData(byte[] dataIn, byte[] key, int encryptMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
        public int calcSM4Mac(byte[] macKey, byte[] iv, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/security/SecurityOpt$Stub.class */
    public static abstract class Stub extends Binder implements SecurityOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.security.SecurityOpt";
        static final int TRANSACTION_saveKey = 1;
        static final int TRANSACTION_dataEncrypt = 2;
        static final int TRANSACTION_calcMac = 3;
        static final int TRANSACTION_getEncryptTUSN = 4;
        static final int TRANSACTION_storeSM4Key = 5;
        static final int TRANSACTION_encryptDataBySM4Key = 6;
        static final int TRANSACTION_getSecStatus = 7;
        static final int TRANSACTION_verifyApkSign = 8;
        static final int TRANSACTION_getAuthStatus = 9;
        static final int TRANSACTION_getTermStatus = 10;
        static final int TRANSACTION_setTermStatus = 11;
        static final int TRANSACTION_sysRequestAuth = 12;
        static final int TRANSACTION_sysConfirmAuth = 13;
        static final int TRANSACTION_saveTerminalKey = 14;
        static final int TRANSACTION_readTerminalPuk = 15;
        static final int TRANSACTION_getTerminalCertData = 16;
        static final int TRANSACTION_saveBaseKey = 17;
        static final int TRANSACTION_dataDecrypt = 18;
        static final int TRANSACTION_saveKeyDukpt = 19;
        static final int TRANSACTION_calcMacDukpt = 20;
        static final int TRANSACTION_dataEncryptDukpt = 21;
        static final int TRANSACTION_dataDecryptDukpt = 22;
        static final int TRANSACTION_calcSecKey = 23;
        static final int TRANSACTION_sm1EncryptData = 24;
        static final int TRANSACTION_sm1DecryptData = 25;
        static final int TRANSACTION_sm2EncryptData = 26;
        static final int TRANSACTION_sm2DecryptData = 27;
        static final int TRANSACTION_sm2SignData = 28;
        static final int TRANSACTION_sm2VerifySign = 29;
        static final int TRANSACTION_sm3CalHash = 30;
        static final int TRANSACTION_sm4EncryptData = 31;
        static final int TRANSACTION_sm4DecryptData = 32;
        static final int TRANSACTION_calcSM4Mac = 33;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static SecurityOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof SecurityOpt)) {
                return (SecurityOpt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            byte[] _arg3;
            byte[] _arg4;
            byte[] _arg42;
            byte[] _arg2;
            byte[] _arg43;
            byte[] _arg5;
            byte[] _arg22;
            byte[] _arg23;
            byte[] _arg6;
            byte[] _arg62;
            byte[] _arg32;
            byte[] _arg24;
            byte[] _arg25;
            byte[] _arg33;
            byte[] _arg26;
            byte[] _arg1;
            byte[] _arg0;
            byte[] _arg34;
            byte[] _arg12;
            byte[] _arg13;
            byte[] _arg35;
            byte[] _arg27;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    byte[] _arg14 = data.createByteArray();
                    byte[] _arg28 = data.createByteArray();
                    int _arg36 = data.readInt();
                    int _arg44 = data.readInt();
                    int _arg52 = data.readInt();
                    boolean _arg63 = 0 != data.readInt();
                    int _result = saveKey(_arg02, _arg14, _arg28, _arg36, _arg44, _arg52, _arg63);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    byte[] _arg15 = data.createByteArray();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg27 = null;
                    } else {
                        _arg27 = new byte[_arg2_length];
                    }
                    int _result2 = dataEncrypt(_arg03, _arg15, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeByteArray(_arg27);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg16 = data.readInt();
                    byte[] _arg29 = data.createByteArray();
                    int _arg3_length = data.readInt();
                    if (_arg3_length < 0) {
                        _arg35 = null;
                    } else {
                        _arg35 = new byte[_arg3_length];
                    }
                    int _result3 = calcMac(_arg04, _arg16, _arg29, _arg35);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg35);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg13 = null;
                    } else {
                        _arg13 = new byte[_arg1_length];
                    }
                    int _result4 = getEncryptTUSN(_arg05, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    reply.writeByteArray(_arg13);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg06 = data.createByteArray();
                    int _result5 = storeSM4Key(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg07 = data.createByteArray();
                    int _arg1_length2 = data.readInt();
                    if (_arg1_length2 < 0) {
                        _arg12 = null;
                    } else {
                        _arg12 = new byte[_arg1_length2];
                    }
                    int _result6 = encryptDataBySM4Key(_arg07, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    reply.writeByteArray(_arg12);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = getSecStatus();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg08 = data.createByteArray();
                    byte[] _arg17 = data.createByteArray();
                    int _result8 = verifyApkSign(_arg08, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    String _result9 = getAuthStatus(_arg09);
                    reply.writeNoException();
                    reply.writeString(_result9);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    String _result10 = getTermStatus();
                    reply.writeNoException();
                    reply.writeString(_result10);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = setTermStatus();
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    byte _arg010 = data.readByte();
                    int _arg18 = data.readInt();
                    String _arg210 = data.readString();
                    int _arg3_length2 = data.readInt();
                    if (_arg3_length2 < 0) {
                        _arg34 = null;
                    } else {
                        _arg34 = new byte[_arg3_length2];
                    }
                    int _result12 = sysRequestAuth(_arg010, _arg18, _arg210, _arg34);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    reply.writeByteArray(_arg34);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg011 = data.createByteArray();
                    int _result13 = sysConfirmAuth(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg012 = data.createByteArray();
                    byte[] _arg19 = data.createByteArray();
                    int _result14 = saveTerminalKey(_arg012, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0_length = data.readInt();
                    if (_arg0_length < 0) {
                        _arg0 = null;
                    } else {
                        _arg0 = new byte[_arg0_length];
                    }
                    int _result15 = readTerminalPuk(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    reply.writeByteArray(_arg0);
                    break;
                case TRANSACTION_getTerminalCertData /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg013 = data.createByteArray();
                    int _arg1_length3 = data.readInt();
                    if (_arg1_length3 < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length3];
                    }
                    int _result16 = getTerminalCertData(_arg013, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    reply.writeByteArray(_arg1);
                    break;
                case TRANSACTION_saveBaseKey /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    byte[] _arg110 = data.createByteArray();
                    int _result17 = saveBaseKey(_arg014, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_dataDecrypt /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    byte[] _arg111 = data.createByteArray();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg26 = null;
                    } else {
                        _arg26 = new byte[_arg2_length2];
                    }
                    int _result18 = dataDecrypt(_arg015, _arg111, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    reply.writeByteArray(_arg26);
                    break;
                case TRANSACTION_saveKeyDukpt /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg016 = data.createByteArray();
                    byte[] _arg112 = data.createByteArray();
                    byte[] _arg211 = data.createByteArray();
                    int _arg37 = data.readInt();
                    int _arg45 = data.readInt();
                    int _arg53 = data.readInt();
                    boolean _arg64 = 0 != data.readInt();
                    int _result19 = saveKeyDukpt(_arg016, _arg112, _arg211, _arg37, _arg45, _arg53, _arg64);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    break;
                case TRANSACTION_calcMacDukpt /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg017 = data.readInt();
                    int _arg113 = data.readInt();
                    byte[] _arg212 = data.createByteArray();
                    int _arg3_length3 = data.readInt();
                    if (_arg3_length3 < 0) {
                        _arg33 = null;
                    } else {
                        _arg33 = new byte[_arg3_length3];
                    }
                    int _result20 = calcMacDukpt(_arg017, _arg113, _arg212, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    reply.writeByteArray(_arg33);
                    break;
                case TRANSACTION_dataEncryptDukpt /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    byte[] _arg114 = data.createByteArray();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg25 = null;
                    } else {
                        _arg25 = new byte[_arg2_length3];
                    }
                    int _result21 = dataEncryptDukpt(_arg018, _arg114, _arg25);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    reply.writeByteArray(_arg25);
                    break;
                case TRANSACTION_dataDecryptDukpt /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    byte[] _arg115 = data.createByteArray();
                    int _arg2_length4 = data.readInt();
                    if (_arg2_length4 < 0) {
                        _arg24 = null;
                    } else {
                        _arg24 = new byte[_arg2_length4];
                    }
                    int _result22 = dataDecryptDukpt(_arg019, _arg115, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    reply.writeByteArray(_arg24);
                    break;
                case TRANSACTION_calcSecKey /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    int _arg116 = data.readInt();
                    int _arg213 = data.readInt();
                    int _arg3_length4 = data.readInt();
                    if (_arg3_length4 < 0) {
                        _arg32 = null;
                    } else {
                        _arg32 = new byte[_arg3_length4];
                    }
                    int _result23 = calcSecKey(_arg020, _arg116, _arg213, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    reply.writeByteArray(_arg32);
                    break;
                case TRANSACTION_sm1EncryptData /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg021 = data.createByteArray();
                    byte[] _arg117 = data.createByteArray();
                    byte[] _arg214 = data.createByteArray();
                    byte[] _arg38 = data.createByteArray();
                    int _arg46 = data.readInt();
                    byte[] _arg54 = data.createByteArray();
                    int _arg6_length = data.readInt();
                    if (_arg6_length < 0) {
                        _arg62 = null;
                    } else {
                        _arg62 = new byte[_arg6_length];
                    }
                    int _result24 = sm1EncryptData(_arg021, _arg117, _arg214, _arg38, _arg46, _arg54, _arg62);
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    reply.writeByteArray(_arg62);
                    break;
                case TRANSACTION_sm1DecryptData /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg022 = data.createByteArray();
                    byte[] _arg118 = data.createByteArray();
                    byte[] _arg215 = data.createByteArray();
                    byte[] _arg39 = data.createByteArray();
                    int _arg47 = data.readInt();
                    byte[] _arg55 = data.createByteArray();
                    int _arg6_length2 = data.readInt();
                    if (_arg6_length2 < 0) {
                        _arg6 = null;
                    } else {
                        _arg6 = new byte[_arg6_length2];
                    }
                    int _result25 = sm1DecryptData(_arg022, _arg118, _arg215, _arg39, _arg47, _arg55, _arg6);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    reply.writeByteArray(_arg6);
                    break;
                case TRANSACTION_sm2EncryptData /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg023 = data.createByteArray();
                    byte[] _arg119 = data.createByteArray();
                    int _arg2_length5 = data.readInt();
                    if (_arg2_length5 < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length5];
                    }
                    int _result26 = sm2EncryptData(_arg023, _arg119, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    reply.writeByteArray(_arg23);
                    break;
                case TRANSACTION_sm2DecryptData /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg024 = data.createByteArray();
                    byte[] _arg120 = data.createByteArray();
                    int _arg2_length6 = data.readInt();
                    if (_arg2_length6 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length6];
                    }
                    int _result27 = sm2DecryptData(_arg024, _arg120, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    reply.writeByteArray(_arg22);
                    break;
                case TRANSACTION_sm2SignData /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg025 = data.createByteArray();
                    byte[] _arg121 = data.createByteArray();
                    byte[] _arg216 = data.createByteArray();
                    byte[] _arg310 = data.createByteArray();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg43 = null;
                    } else {
                        _arg43 = new byte[_arg4_length];
                    }
                    int _arg5_length = data.readInt();
                    if (_arg5_length < 0) {
                        _arg5 = null;
                    } else {
                        _arg5 = new byte[_arg5_length];
                    }
                    int _result28 = sm2SignData(_arg025, _arg121, _arg216, _arg310, _arg43, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    reply.writeByteArray(_arg43);
                    reply.writeByteArray(_arg5);
                    break;
                case TRANSACTION_sm2VerifySign /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg026 = data.createByteArray();
                    byte[] _arg122 = data.createByteArray();
                    byte[] _arg217 = data.createByteArray();
                    byte[] _arg311 = data.createByteArray();
                    byte[] _arg48 = data.createByteArray();
                    int _result29 = sm2VerifySign(_arg026, _arg122, _arg217, _arg311, _arg48);
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    break;
                case TRANSACTION_sm3CalHash /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg027 = data.createByteArray();
                    byte[] _arg123 = data.createByteArray();
                    int _arg2_length7 = data.readInt();
                    if (_arg2_length7 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length7];
                    }
                    int _result30 = sm3CalHash(_arg027, _arg123, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    reply.writeByteArray(_arg2);
                    break;
                case TRANSACTION_sm4EncryptData /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg028 = data.createByteArray();
                    byte[] _arg124 = data.createByteArray();
                    int _arg218 = data.readInt();
                    byte[] _arg312 = data.createByteArray();
                    int _arg4_length2 = data.readInt();
                    if (_arg4_length2 < 0) {
                        _arg42 = null;
                    } else {
                        _arg42 = new byte[_arg4_length2];
                    }
                    int _result31 = sm4EncryptData(_arg028, _arg124, _arg218, _arg312, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result31);
                    reply.writeByteArray(_arg42);
                    break;
                case TRANSACTION_sm4DecryptData /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg029 = data.createByteArray();
                    byte[] _arg125 = data.createByteArray();
                    int _arg219 = data.readInt();
                    byte[] _arg313 = data.createByteArray();
                    int _arg4_length3 = data.readInt();
                    if (_arg4_length3 < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new byte[_arg4_length3];
                    }
                    int _result32 = sm4DecryptData(_arg029, _arg125, _arg219, _arg313, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result32);
                    reply.writeByteArray(_arg4);
                    break;
                case TRANSACTION_calcSM4Mac /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg030 = data.createByteArray();
                    byte[] _arg126 = data.createByteArray();
                    byte[] _arg220 = data.createByteArray();
                    int _arg3_length5 = data.readInt();
                    if (_arg3_length5 < 0) {
                        _arg3 = null;
                    } else {
                        _arg3 = new byte[_arg3_length5];
                    }
                    int _result33 = calcSM4Mac(_arg030, _arg126, _arg220, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result33);
                    reply.writeByteArray(_arg3);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/security/SecurityOpt$Stub$Proxy.class */
        private static class Proxy implements SecurityOpt {
            private IBinder mRemote;
            public static SecurityOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int saveKey(int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex, boolean isEncrypt) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(encryptIndex);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    _data.writeInt(isEncrypt ? 1 : 0);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKey = Stub.getDefaultImpl().saveKey(keyType, keyValue, checkValue, encryptIndex, keyAlgType, keyIndex, isEncrypt);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int dataEncrypt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncrypt = Stub.getDefaultImpl().dataEncrypt(keyIndex, dataIn, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int calcMac(int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macType);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMac = Stub.getDefaultImpl().calcMac(keyIndex, macType, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMac;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int getEncryptTUSN(String dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int encryptTUSN = Stub.getDefaultImpl().getEncryptTUSN(dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return encryptTUSN;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int storeSM4Key(byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iStoreSM4Key = Stub.getDefaultImpl().storeSM4Key(dataIn);
                        _reply.recycle();
                        _data.recycle();
                        return iStoreSM4Key;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int encryptDataBySM4Key(byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iEncryptDataBySM4Key = Stub.getDefaultImpl().encryptDataBySM4Key(dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iEncryptDataBySM4Key;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int getSecStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int secStatus = Stub.getDefaultImpl().getSecStatus();
                        _reply.recycle();
                        _data.recycle();
                        return secStatus;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int verifyApkSign(byte[] hashMessage, byte[] signData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(hashMessage);
                    _data.writeByteArray(signData);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iVerifyApkSign = Stub.getDefaultImpl().verifyApkSign(hashMessage, signData);
                        _reply.recycle();
                        _data.recycle();
                        return iVerifyApkSign;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public String getAuthStatus(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String authStatus = Stub.getDefaultImpl().getAuthStatus(type);
                        _reply.recycle();
                        _data.recycle();
                        return authStatus;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public String getTermStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String termStatus = Stub.getDefaultImpl().getTermStatus();
                        _reply.recycle();
                        _data.recycle();
                        return termStatus;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int setTermStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int termStatus = Stub.getDefaultImpl().setTermStatus();
                        _reply.recycle();
                        _data.recycle();
                        return termStatus;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sysRequestAuth(byte reqType, int authCode, String SN, byte[] authData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(reqType);
                    _data.writeInt(authCode);
                    _data.writeString(SN);
                    if (authData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(authData.length);
                    }
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysRequestAuth = Stub.getDefaultImpl().sysRequestAuth(reqType, authCode, SN, authData);
                        _reply.recycle();
                        _data.recycle();
                        return iSysRequestAuth;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(authData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sysConfirmAuth(byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSysConfirmAuth = Stub.getDefaultImpl().sysConfirmAuth(dataIn);
                        _reply.recycle();
                        _data.recycle();
                        return iSysConfirmAuth;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int saveTerminalKey(byte[] dataInPuk, byte[] dataInPvk) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataInPuk);
                    _data.writeByteArray(dataInPvk);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveTerminalKey = Stub.getDefaultImpl().saveTerminalKey(dataInPuk, dataInPvk);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveTerminalKey;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int readTerminalPuk(byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int terminalPuk = Stub.getDefaultImpl().readTerminalPuk(dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return terminalPuk;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int getTerminalCertData(byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getTerminalCertData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int terminalCertData = Stub.getDefaultImpl().getTerminalCertData(dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return terminalCertData;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int saveBaseKey(int destinationIndex, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(destinationIndex);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveBaseKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveBaseKey = Stub.getDefaultImpl().saveBaseKey(destinationIndex, keyData);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveBaseKey;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int dataDecrypt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataDecrypt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecrypt = Stub.getDefaultImpl().dataDecrypt(keyIndex, dataIn, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int saveKeyDukpt(byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptIndex, int encryptType, int keyIndex, boolean isEncrypt) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(ksn);
                    _data.writeInt(encryptIndex);
                    _data.writeInt(encryptType);
                    _data.writeInt(keyIndex);
                    _data.writeInt(isEncrypt ? 1 : 0);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveKeyDukpt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKeyDukpt = Stub.getDefaultImpl().saveKeyDukpt(keyValue, checkValue, ksn, encryptIndex, encryptType, keyIndex, isEncrypt);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveKeyDukpt;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int calcMacDukpt(int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macType);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcMacDukpt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMacDukpt = Stub.getDefaultImpl().calcMacDukpt(keyIndex, macType, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMacDukpt;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int dataEncryptDukpt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataEncryptDukpt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncryptDukpt = Stub.getDefaultImpl().dataEncryptDukpt(keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataEncryptDukpt;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int dataDecryptDukpt(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataDecryptDukpt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecryptDukpt = Stub.getDefaultImpl().dataDecryptDukpt(keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataDecryptDukpt;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int calcSecKey(int keySystem, int keyIndex, int ctrlCode, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySystem);
                    _data.writeInt(keyIndex);
                    _data.writeInt(ctrlCode);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcSecKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcSecKey = Stub.getDefaultImpl().calcSecKey(keySystem, keyIndex, ctrlCode, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcSecKey;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm1EncryptData(byte[] dataIn, byte[] sk, byte[] ak, byte[] ek, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(sk);
                    _data.writeByteArray(ak);
                    _data.writeByteArray(ek);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm1EncryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm1EncryptData = Stub.getDefaultImpl().sm1EncryptData(dataIn, sk, ak, ek, encryptionMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm1EncryptData;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm1DecryptData(byte[] dataIn, byte[] sk, byte[] ak, byte[] ek, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(sk);
                    _data.writeByteArray(ak);
                    _data.writeByteArray(ek);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm1DecryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm1DecryptData = Stub.getDefaultImpl().sm1DecryptData(dataIn, sk, ak, ek, encryptionMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm1DecryptData;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm2EncryptData(byte[] dataIn, byte[] key, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(key);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2EncryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2EncryptData = Stub.getDefaultImpl().sm2EncryptData(dataIn, key, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm2DecryptData(byte[] dataIn, byte[] key, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(key);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2DecryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2DecryptData = Stub.getDefaultImpl().sm2DecryptData(dataIn, key, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm2SignData(byte[] userId, byte[] dataIn, byte[] pubKey, byte[] priKey, byte[] sign, byte[] eValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(pubKey);
                    _data.writeByteArray(priKey);
                    if (sign == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(sign.length);
                    }
                    if (eValue == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(eValue.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2SignData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2SignData = Stub.getDefaultImpl().sm2SignData(userId, dataIn, pubKey, priKey, sign, eValue);
                        _reply.recycle();
                        _data.recycle();
                        return iSm2SignData;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(sign);
                    _reply.readByteArray(eValue);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm2VerifySign(byte[] userId, byte[] dataIn, byte[] pubKey, byte[] priKey, byte[] sign) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(pubKey);
                    _data.writeByteArray(priKey);
                    _data.writeByteArray(sign);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm2VerifySign, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm2VerifySign = Stub.getDefaultImpl().sm2VerifySign(userId, dataIn, pubKey, priKey, sign);
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm3CalHash(byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(userId);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm3CalHash, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm3CalHash = Stub.getDefaultImpl().sm3CalHash(userId, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm3CalHash;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm4EncryptData(byte[] dataIn, byte[] key, int encryptMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(key);
                    _data.writeInt(encryptMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm4EncryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm4EncryptData = Stub.getDefaultImpl().sm4EncryptData(dataIn, key, encryptMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm4EncryptData;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int sm4DecryptData(byte[] dataIn, byte[] key, int encryptMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(key);
                    _data.writeInt(encryptMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sm4DecryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSm4DecryptData = Stub.getDefaultImpl().sm4DecryptData(dataIn, key, encryptMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSm4DecryptData;
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

            @Override // com.sunmi.pay.hardware.aidl.security.SecurityOpt
            public int calcSM4Mac(byte[] macKey, byte[] iv, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(macKey);
                    _data.writeByteArray(iv);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcSM4Mac, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcSM4Mac = Stub.getDefaultImpl().calcSM4Mac(macKey, iv, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcSM4Mac;
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

        public static boolean setDefaultImpl(SecurityOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static SecurityOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
