package com.sunmi.pay.hardware.aidlv2.security;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/SecurityOptV2.class */
public interface SecurityOptV2 extends IInterface {
    int saveBaseKey(int i, byte[] bArr) throws RemoteException;

    int savePlaintextKey(int i, byte[] bArr, byte[] bArr2, int i2, int i3) throws RemoteException;

    int saveCiphertextKey(int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4) throws RemoteException;

    int calcMac(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int dataEncrypt(int i, byte[] bArr, int i2, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int dataDecrypt(int i, byte[] bArr, int i2, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int saveKeyDukpt(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2, int i3) throws RemoteException;

    int calcMacDukpt(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int dataEncryptDukpt(int i, byte[] bArr, int i2, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int dataDecryptDukpt(int i, byte[] bArr, int i2, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int dukptIncreaseKSN(int i) throws RemoteException;

    int dukptCurrentKSN(int i, byte[] bArr) throws RemoteException;

    int getKeyCheckValue(int i, int i2, byte[] bArr) throws RemoteException;

    int getTUSNEncryptData(String str, byte[] bArr) throws RemoteException;

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

    int generateRSAKeys(int i, int i2, int i3, String str) throws RemoteException;

    int getRSAPublicKey(int i, byte[] bArr) throws RemoteException;

    int getRSAPrivateKey(int i, byte[] bArr) throws RemoteException;

    int dataEncryptRSA(String str, int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int dataDecryptRSA(String str, int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int removeRSAKey(int i) throws RemoteException;

    int storeCertificate(int i, byte[] bArr) throws RemoteException;

    int getCertificate(int i, byte[] bArr) throws RemoteException;

    int dukptGetInitKSN(byte[] bArr) throws RemoteException;

    int signingRSA(String str, int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int verifySignatureRSA(String str, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int injectPlaintextKey(String str, int i, byte[] bArr, byte[] bArr2, int i2, int i3) throws RemoteException;

    int injectCiphertextKey(String str, int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4) throws RemoteException;

    int dataEncryptDukptEx(int i, int i2, byte[] bArr, int i3, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int dataDecryptDukptEx(int i, int i2, byte[] bArr, int i3, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int calcMacDukptEx(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int verifyMacDukptEx(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int saveTR31Key(byte[] bArr, int i, int i2) throws RemoteException;

    int saveCiphertextKeyRSA(int i, byte[] bArr, byte[] bArr2, int i2, int i3, int i4, String str) throws RemoteException;

    int saveRSAKey(int i, byte[] bArr, int i2) throws RemoteException;

    int deleteKey(int i, int i2) throws RemoteException;

    int saveKeyDukptAES(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3, int i3, int i4) throws RemoteException;

    int calcMacEx(int i, int i2, int i3, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int generateSM2Keypair(int i, Bundle bundle) throws RemoteException;

    int injectSM2Key(int i, Bundle bundle) throws RemoteException;

    int sm2Sign(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2VerifySign(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2EncryptData(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int sm2DecryptData(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int calcSecHash(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int verifyMac(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int generateRSAKeypair(int i, int i2, String str, byte[] bArr) throws RemoteException;

    int injectRSAKey(int i, int i2, String str, String str2) throws RemoteException;

    int generateSymKey(int i, int i2, int i3) throws RemoteException;

    int injectSymKey(int i, int i2, byte[] bArr, byte[] bArr2, int i3) throws RemoteException;

    int hsmSaveKeyShare(int i, byte[] bArr, byte[] bArr2, int i2, int i3) throws RemoteException;

    int hsmSaveKeyShareDukpt(int i, int i2, byte[] bArr, byte[] bArr2, byte[] bArr3, int i3, int i4) throws RemoteException;

    int hsmCombineKeyShare(int i, int i2, int i3, int i4, int i5, int i6, byte[] bArr) throws RemoteException;

    int hsmGenerateRSAKeypair(int i, int i2, String str, byte[] bArr) throws RemoteException;

    int hsmInjectRSAKey(int i, int i2, String str, String str2) throws RemoteException;

    int hsmSaveKeyUnderKEK(int i, byte[] bArr, int i2, int i3, int i4, int i5) throws RemoteException;

    int hsmExportKeyUnderKEK(int i, int i2, int i3, byte[] bArr) throws RemoteException;

    int hsmExportTR31KeyBlock(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int hsmDestroyKey(int i) throws RemoteException;

    int hsmExchangeKeyEcc(int i, String str, int i2, int i3, int i4, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int hsmAsymKeyFun(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int operateSensitiveService(int i, byte[] bArr) throws RemoteException;

    int rsaEncryptOrDecryptData(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int storeDeviceCertPrivateKey(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getDeviceCertificate(int i, byte[] bArr) throws RemoteException;

    int devicePrivateKeyRecover(int i, int i2, int i3, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getKeyCheckValueEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int deleteKeyEx(Bundle bundle) throws RemoteException;

    int injectCiphertextKeyEx(Bundle bundle) throws RemoteException;

    int injectKeyDukptEx(Bundle bundle) throws RemoteException;

    int saveKeyEx(Bundle bundle) throws RemoteException;

    int calcMacExtended(Bundle bundle, byte[] bArr) throws RemoteException;

    int calcMacDukptExtended(Bundle bundle, byte[] bArr) throws RemoteException;

    int readRSAKey(int i, Bundle bundle) throws RemoteException;

    int getKeyLength(int i, int i2) throws RemoteException;

    int writeKeyVariable(Bundle bundle) throws RemoteException;

    int secKeyIoControl(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int apacsMac(int i, int i2, int i3, int i4, byte[] bArr, byte[] bArr2) throws RemoteException;

    int hsmSaveKeyUnderKEKEx(Bundle bundle) throws RemoteException;

    int hsmExportKeyUnderKEKEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int hsmGenerateKeyByOaep(int i, int i2, int i3, int i4, byte[] bArr, byte[] bArr2) throws RemoteException;

    int saveCiphertextKeyUnderRSA(int i, int i2, int i3, int i4, byte[] bArr, byte[] bArr2) throws RemoteException;

    int injectCiphertextKeyUnderRSA(String str, int i, int i2, int i3, int i4, byte[] bArr, byte[] bArr2) throws RemoteException;

    int generateSymKeyEx(Bundle bundle) throws RemoteException;

    int injectSymKeyEx(Bundle bundle) throws RemoteException;

    int injectDeviceCertPrivateKey(Bundle bundle) throws RemoteException;

    int generateRSAKeypairEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int injectRSAKeyEx(Bundle bundle) throws RemoteException;

    int setDeviceCertificate(int i, byte[] bArr) throws RemoteException;

    int injectPlaintextKeyWL(Bundle bundle) throws RemoteException;

    int injectCiphertextKeyWL(Bundle bundle) throws RemoteException;

    int injectKeyDukptWL(Bundle bundle) throws RemoteException;

    int getKeyCheckValueWL(Bundle bundle, byte[] bArr) throws RemoteException;

    int deleteKeyWL(Bundle bundle) throws RemoteException;

    int dataEncryptEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int dataDecryptEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int queryKeyMappingRecordListWL(List<Bundle> list) throws RemoteException;

    int genTR34CredTokenWL(Bundle bundle, byte[] bArr) throws RemoteException;

    int genTR34RandomTokenWL(int i, byte[] bArr) throws RemoteException;

    int validateTR34CredTokenWL(byte[] bArr) throws RemoteException;

    int validateTR34KeyTokenWL(Bundle bundle) throws RemoteException;

    int validateTR34UNBTokenWL(Bundle bundle) throws RemoteException;

    int queryKeyMappingRecordList(Bundle bundle, List<Bundle> list) throws RemoteException;

    int readSM2Key(int i, Bundle bundle) throws RemoteException;

    int calcSM3HashWithID(int i, byte[] bArr, byte[] bArr2, byte[] bArr3) throws RemoteException;

    int sm2SingleSign(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int injectTR31Key(Bundle bundle) throws RemoteException;

    int hsmExchangeKeyEccEx(Bundle bundle, byte[] bArr) throws RemoteException;

    int saveCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException;

    int injectCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException;

    int convertKeyType(Bundle bundle) throws RemoteException;

    int hsmExchangeKeyEccKdf(Bundle bundle, Bundle bundle2, Bundle bundle3) throws RemoteException;

    int generateSM2KeypairEx(Bundle bundle, Bundle bundle2) throws RemoteException;

    int injectSM2KeyEx(Bundle bundle) throws RemoteException;

    int generateEccKeypair(int i, int i2, byte[] bArr) throws RemoteException;

    int injectEccPubKey(int i, int i2, byte[] bArr) throws RemoteException;

    int injectEccPvtKey(int i, int i2, byte[] bArr) throws RemoteException;

    int getEccPubKey(int i, Bundle bundle) throws RemoteException;

    int eccRecover(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int eccSign(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int eccVerify(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int queryKeyMappingRecord(Bundle bundle, Bundle bundle2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/SecurityOptV2$Default.class */
    public static class Default implements SecurityOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveBaseKey(int destinationIndex, byte[] keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int savePlaintextKey(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveCiphertextKey(int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMac(int keyIndex, int macAlgType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataEncrypt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataDecrypt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveKeyDukpt(int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMacDukpt(int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataEncryptDukpt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataDecryptDukpt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dukptIncreaseKSN(int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dukptCurrentKSN(int keyIndex, byte[] outKSN) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getKeyCheckValue(int keySystem, int keyIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getTUSNEncryptData(String dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int storeSM4Key(byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int encryptDataBySM4Key(byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getSecStatus() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int verifyApkSign(byte[] hashMessage, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public String getAuthStatus(int type) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public String getTermStatus() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int setTermStatus() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sysRequestAuth(byte reqType, int authCode, String SN, byte[] authData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sysConfirmAuth(byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveTerminalKey(byte[] dataInPuk, byte[] dataInPvk) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int readTerminalPuk(byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getTerminalCertData(byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateRSAKeys(int pubKeyIndex, int pvtKeyIndex, int keysize, String pubExponent) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getRSAPublicKey(int pubKeyIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getRSAPrivateKey(int pvtKeyIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataEncryptRSA(String transformation, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataDecryptRSA(String transformation, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int removeRSAKey(int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int storeCertificate(int certIndex, byte[] certData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getCertificate(int certIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dukptGetInitKSN(byte[] outKSN) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int signingRSA(String signAlg, int pvtKeyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int verifySignatureRSA(String signAlg, byte[] pubKey, byte[] srcData, byte[] signature) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectPlaintextKey(String targetPkgName, int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectCiphertextKey(String targetPkgName, int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataEncryptDukptEx(int keySelect, int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataDecryptDukptEx(int keySelect, int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMacDukptEx(int keySelect, int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int verifyMacDukptEx(int keySelect, int keyIndex, int macType, byte[] dataIn, byte[] macData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveTR31Key(byte[] keyValue, int kbpkIndex, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveCiphertextKeyRSA(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex, int encryptIndexRSA, String transformation) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveRSAKey(int keyType, byte[] keyValue, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int deleteKey(int keySystem, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveKeyDukptAES(int dukptKeyType, int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMacEx(int keyIndex, int keyLen, int macAlgType, byte[] diversify, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateSM2Keypair(int pvkIndex, Bundle pubKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectSM2Key(int keyIndex, Bundle keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sm2Sign(int pukIndex, int pvkIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sm2VerifySign(int pukIndex, byte[] userId, byte[] dataIn, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sm2EncryptData(int pukIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sm2DecryptData(int pvkIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcSecHash(int mode, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int verifyMac(int keyIndex, int macAlgType, byte[] dataIn, byte[] mac) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateRSAKeypair(int pvkIndex, int keySize, String pubExponent, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectRSAKey(int keyIndex, int keySize, String module, String exponent) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateSymKey(int keyIndex, int keyType, int keyAlgType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectSymKey(int keyIndex, int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmSaveKeyShare(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmSaveKeyShareDukpt(int dukptKeyType, int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmCombineKeyShare(int keyType, int keyAlgType, int keyIndex, int keyShareIndex1, int keyShareIndex2, int keyShareIndex3, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmGenerateRSAKeypair(int pvtKeyIndex, int keySize, String pubExponent, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmInjectRSAKey(int keyIndex, int keySize, String module, String exponent) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmSaveKeyUnderKEK(int keyIndex, byte[] keyValue, int keyType, int keyAlgType, int encryptKeySystem, int encryptIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExportKeyUnderKEK(int keyIndex, int kekIndex, int kekKeySystem, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExportTR31KeyBlock(int keyIndex, int encryptIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmDestroyKey(int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExchangeKeyEcc(int mode, String curveParam, int keyIndex, int keyType, int keyAlgType, byte[] pubKeyA, byte[] checkValue, byte[] pubKeyB) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmAsymKeyFun(int mode, int keySystem, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int operateSensitiveService(int mode, byte[] pinPadParam) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int rsaEncryptOrDecryptData(int keyIndex, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int storeDeviceCertPrivateKey(int certIndex, int mode, int encryptIndex, byte[] certData, byte[] pvkData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getDeviceCertificate(int certIndex, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int devicePrivateKeyRecover(int keyIndex, int mode, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getKeyCheckValueEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int deleteKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectCiphertextKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectKeyDukptEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMacExtended(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcMacDukptExtended(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int readRSAKey(int keyIndex, Bundle keyInfo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getKeyLength(int keySystem, int keyIndex) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int writeKeyVariable(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int secKeyIoControl(int keyIndex, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int apacsMac(int initMakIndex, int makIndex, int pikIndex, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmSaveKeyUnderKEKEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExportKeyUnderKEKEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmGenerateKeyByOaep(int keyIndex, int dependIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveCiphertextKeyUnderRSA(int keyIndex, int rsaKeyIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectCiphertextKeyUnderRSA(String targetPkgName, int keyIndex, int rsaKeyIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateSymKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectSymKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectDeviceCertPrivateKey(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateRSAKeypairEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectRSAKeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int setDeviceCertificate(int certIndex, byte[] certData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectPlaintextKeyWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectCiphertextKeyWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectKeyDukptWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getKeyCheckValueWL(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int deleteKeyWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataEncryptEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int dataDecryptEx(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int queryKeyMappingRecordListWL(List<Bundle> list) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int genTR34CredTokenWL(Bundle bundle, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int genTR34RandomTokenWL(int randomSize, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int validateTR34CredTokenWL(byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int validateTR34KeyTokenWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int validateTR34UNBTokenWL(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int queryKeyMappingRecordList(Bundle bundle, List<Bundle> list) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int readSM2Key(int keyIndex, Bundle keyInfo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int calcSM3HashWithID(int keyIndex, byte[] userId, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int sm2SingleSign(int keyIndex, byte[] hash, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectTR31Key(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExchangeKeyEccEx(Bundle bundle, byte[] pubKeyB) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int saveCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int convertKeyType(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int hsmExchangeKeyEccKdf(Bundle keyInfo, Bundle kdfInfo, Bundle outInfo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateSM2KeypairEx(Bundle bundle, Bundle pubKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectSM2KeyEx(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int generateEccKeypair(int pvkIndex, int keySize, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectEccPubKey(int pukIndex, int keySize, byte[] pubKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int injectEccPvtKey(int pvkIndex, int keySize, byte[] pvkKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int getEccPubKey(int keyIndex, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int eccRecover(int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int eccSign(int pvkIndex, int hashType, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int eccVerify(int pukIndex, int hashType, byte[] dataIn, byte[] signData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
        public int queryKeyMappingRecord(Bundle bundle, Bundle keyInfo) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/SecurityOptV2$Stub.class */
    public static abstract class Stub extends Binder implements SecurityOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2";
        static final int TRANSACTION_saveBaseKey = 1;
        static final int TRANSACTION_savePlaintextKey = 2;
        static final int TRANSACTION_saveCiphertextKey = 3;
        static final int TRANSACTION_calcMac = 4;
        static final int TRANSACTION_dataEncrypt = 5;
        static final int TRANSACTION_dataDecrypt = 6;
        static final int TRANSACTION_saveKeyDukpt = 7;
        static final int TRANSACTION_calcMacDukpt = 8;
        static final int TRANSACTION_dataEncryptDukpt = 9;
        static final int TRANSACTION_dataDecryptDukpt = 10;
        static final int TRANSACTION_dukptIncreaseKSN = 11;
        static final int TRANSACTION_dukptCurrentKSN = 12;
        static final int TRANSACTION_getKeyCheckValue = 13;
        static final int TRANSACTION_getTUSNEncryptData = 14;
        static final int TRANSACTION_storeSM4Key = 15;
        static final int TRANSACTION_encryptDataBySM4Key = 16;
        static final int TRANSACTION_getSecStatus = 17;
        static final int TRANSACTION_verifyApkSign = 18;
        static final int TRANSACTION_getAuthStatus = 19;
        static final int TRANSACTION_getTermStatus = 20;
        static final int TRANSACTION_setTermStatus = 21;
        static final int TRANSACTION_sysRequestAuth = 22;
        static final int TRANSACTION_sysConfirmAuth = 23;
        static final int TRANSACTION_saveTerminalKey = 24;
        static final int TRANSACTION_readTerminalPuk = 25;
        static final int TRANSACTION_getTerminalCertData = 26;
        static final int TRANSACTION_generateRSAKeys = 27;
        static final int TRANSACTION_getRSAPublicKey = 28;
        static final int TRANSACTION_getRSAPrivateKey = 29;
        static final int TRANSACTION_dataEncryptRSA = 30;
        static final int TRANSACTION_dataDecryptRSA = 31;
        static final int TRANSACTION_removeRSAKey = 32;
        static final int TRANSACTION_storeCertificate = 33;
        static final int TRANSACTION_getCertificate = 34;
        static final int TRANSACTION_dukptGetInitKSN = 35;
        static final int TRANSACTION_signingRSA = 36;
        static final int TRANSACTION_verifySignatureRSA = 37;
        static final int TRANSACTION_injectPlaintextKey = 38;
        static final int TRANSACTION_injectCiphertextKey = 39;
        static final int TRANSACTION_dataEncryptDukptEx = 40;
        static final int TRANSACTION_dataDecryptDukptEx = 41;
        static final int TRANSACTION_calcMacDukptEx = 42;
        static final int TRANSACTION_verifyMacDukptEx = 43;
        static final int TRANSACTION_saveTR31Key = 44;
        static final int TRANSACTION_saveCiphertextKeyRSA = 45;
        static final int TRANSACTION_saveRSAKey = 46;
        static final int TRANSACTION_deleteKey = 47;
        static final int TRANSACTION_saveKeyDukptAES = 48;
        static final int TRANSACTION_calcMacEx = 49;
        static final int TRANSACTION_generateSM2Keypair = 50;
        static final int TRANSACTION_injectSM2Key = 51;
        static final int TRANSACTION_sm2Sign = 52;
        static final int TRANSACTION_sm2VerifySign = 53;
        static final int TRANSACTION_sm2EncryptData = 54;
        static final int TRANSACTION_sm2DecryptData = 55;
        static final int TRANSACTION_calcSecHash = 56;
        static final int TRANSACTION_verifyMac = 57;
        static final int TRANSACTION_generateRSAKeypair = 58;
        static final int TRANSACTION_injectRSAKey = 59;
        static final int TRANSACTION_generateSymKey = 60;
        static final int TRANSACTION_injectSymKey = 61;
        static final int TRANSACTION_hsmSaveKeyShare = 62;
        static final int TRANSACTION_hsmSaveKeyShareDukpt = 63;
        static final int TRANSACTION_hsmCombineKeyShare = 64;
        static final int TRANSACTION_hsmGenerateRSAKeypair = 65;
        static final int TRANSACTION_hsmInjectRSAKey = 66;
        static final int TRANSACTION_hsmSaveKeyUnderKEK = 67;
        static final int TRANSACTION_hsmExportKeyUnderKEK = 68;
        static final int TRANSACTION_hsmExportTR31KeyBlock = 69;
        static final int TRANSACTION_hsmDestroyKey = 70;
        static final int TRANSACTION_hsmExchangeKeyEcc = 71;
        static final int TRANSACTION_hsmAsymKeyFun = 72;
        static final int TRANSACTION_operateSensitiveService = 73;
        static final int TRANSACTION_rsaEncryptOrDecryptData = 74;
        static final int TRANSACTION_storeDeviceCertPrivateKey = 75;
        static final int TRANSACTION_getDeviceCertificate = 76;
        static final int TRANSACTION_devicePrivateKeyRecover = 77;
        static final int TRANSACTION_getKeyCheckValueEx = 78;
        static final int TRANSACTION_deleteKeyEx = 79;
        static final int TRANSACTION_injectCiphertextKeyEx = 80;
        static final int TRANSACTION_injectKeyDukptEx = 81;
        static final int TRANSACTION_saveKeyEx = 82;
        static final int TRANSACTION_calcMacExtended = 83;
        static final int TRANSACTION_calcMacDukptExtended = 84;
        static final int TRANSACTION_readRSAKey = 85;
        static final int TRANSACTION_getKeyLength = 86;
        static final int TRANSACTION_writeKeyVariable = 87;
        static final int TRANSACTION_secKeyIoControl = 88;
        static final int TRANSACTION_apacsMac = 89;
        static final int TRANSACTION_hsmSaveKeyUnderKEKEx = 90;
        static final int TRANSACTION_hsmExportKeyUnderKEKEx = 91;
        static final int TRANSACTION_hsmGenerateKeyByOaep = 92;
        static final int TRANSACTION_saveCiphertextKeyUnderRSA = 93;
        static final int TRANSACTION_injectCiphertextKeyUnderRSA = 94;
        static final int TRANSACTION_generateSymKeyEx = 95;
        static final int TRANSACTION_injectSymKeyEx = 96;
        static final int TRANSACTION_injectDeviceCertPrivateKey = 97;
        static final int TRANSACTION_generateRSAKeypairEx = 98;
        static final int TRANSACTION_injectRSAKeyEx = 99;
        static final int TRANSACTION_setDeviceCertificate = 100;
        static final int TRANSACTION_injectPlaintextKeyWL = 101;
        static final int TRANSACTION_injectCiphertextKeyWL = 102;
        static final int TRANSACTION_injectKeyDukptWL = 103;
        static final int TRANSACTION_getKeyCheckValueWL = 104;
        static final int TRANSACTION_deleteKeyWL = 105;
        static final int TRANSACTION_dataEncryptEx = 106;
        static final int TRANSACTION_dataDecryptEx = 107;
        static final int TRANSACTION_queryKeyMappingRecordListWL = 108;
        static final int TRANSACTION_genTR34CredTokenWL = 109;
        static final int TRANSACTION_genTR34RandomTokenWL = 110;
        static final int TRANSACTION_validateTR34CredTokenWL = 111;
        static final int TRANSACTION_validateTR34KeyTokenWL = 112;
        static final int TRANSACTION_validateTR34UNBTokenWL = 113;
        static final int TRANSACTION_queryKeyMappingRecordList = 114;
        static final int TRANSACTION_readSM2Key = 115;
        static final int TRANSACTION_calcSM3HashWithID = 116;
        static final int TRANSACTION_sm2SingleSign = 117;
        static final int TRANSACTION_injectTR31Key = 118;
        static final int TRANSACTION_hsmExchangeKeyEccEx = 119;
        static final int TRANSACTION_saveCiphertextKeyUnderRSAEx = 120;
        static final int TRANSACTION_injectCiphertextKeyUnderRSAEx = 121;
        static final int TRANSACTION_convertKeyType = 122;
        static final int TRANSACTION_hsmExchangeKeyEccKdf = 123;
        static final int TRANSACTION_generateSM2KeypairEx = 124;
        static final int TRANSACTION_injectSM2KeyEx = 125;
        static final int TRANSACTION_generateEccKeypair = 126;
        static final int TRANSACTION_injectEccPubKey = 127;
        static final int TRANSACTION_injectEccPvtKey = 128;
        static final int TRANSACTION_getEccPubKey = 129;
        static final int TRANSACTION_eccRecover = 130;
        static final int TRANSACTION_eccSign = 131;
        static final int TRANSACTION_eccVerify = 132;
        static final int TRANSACTION_queryKeyMappingRecord = 133;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static SecurityOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof SecurityOptV2)) {
                return (SecurityOptV2) iin;
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
            byte[] _arg3;
            byte[] _arg2;
            byte[] _arg22;
            Bundle _arg02;
            Bundle _arg03;
            Bundle _arg04;
            Bundle _arg1;
            Bundle _arg05;
            Bundle _arg06;
            Bundle _arg07;
            Bundle _arg08;
            byte[] _arg12;
            Bundle _arg09;
            byte[] _arg23;
            byte[] _arg32;
            Bundle _arg010;
            Bundle _arg011;
            Bundle _arg012;
            byte[] _arg13;
            Bundle _arg013;
            byte[] _arg14;
            Bundle _arg014;
            byte[] _arg15;
            Bundle _arg015;
            byte[] _arg16;
            Bundle _arg016;
            Bundle _arg017;
            byte[] _arg17;
            Bundle _arg018;
            Bundle _arg019;
            Bundle _arg020;
            Bundle _arg021;
            Bundle _arg022;
            byte[] _arg18;
            Bundle _arg023;
            Bundle _arg024;
            Bundle _arg025;
            Bundle _arg026;
            byte[] _arg19;
            Bundle _arg027;
            byte[] _arg5;
            byte[] _arg33;
            Bundle _arg028;
            Bundle _arg029;
            byte[] _arg110;
            Bundle _arg030;
            byte[] _arg111;
            Bundle _arg031;
            Bundle _arg032;
            Bundle _arg033;
            Bundle _arg034;
            Bundle _arg035;
            byte[] _arg112;
            byte[] _arg4;
            byte[] _arg113;
            byte[] _arg34;
            byte[] _arg7;
            byte[] _arg35;
            byte[] _arg36;
            byte[] _arg37;
            byte[] _arg6;
            byte[] _arg38;
            byte[] _arg24;
            byte[] _arg25;
            byte[] _arg26;
            byte[] _arg42;
            Bundle _arg114;
            byte[] _arg52;
            byte[] _arg43;
            byte[] _arg53;
            byte[] _arg54;
            byte[] _arg39;
            byte[] _arg036;
            byte[] _arg115;
            byte[] _arg310;
            byte[] _arg311;
            byte[] _arg116;
            byte[] _arg117;
            byte[] _arg118;
            byte[] _arg037;
            byte[] _arg312;
            byte[] _arg119;
            byte[] _arg120;
            byte[] _arg27;
            byte[] _arg121;
            byte[] _arg44;
            byte[] _arg45;
            byte[] _arg313;
            byte[] _arg46;
            byte[] _arg47;
            byte[] _arg314;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    int _result = saveBaseKey(_arg038, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg039 = data.readInt();
                    byte[] _arg122 = data.createByteArray();
                    byte[] _arg28 = data.createByteArray();
                    int _arg315 = data.readInt();
                    int _arg48 = data.readInt();
                    int _result2 = savePlaintextKey(_arg039, _arg122, _arg28, _arg315, _arg48);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg040 = data.readInt();
                    byte[] _arg123 = data.createByteArray();
                    byte[] _arg29 = data.createByteArray();
                    int _arg316 = data.readInt();
                    int _arg49 = data.readInt();
                    int _arg55 = data.readInt();
                    int _result3 = saveCiphertextKey(_arg040, _arg123, _arg29, _arg316, _arg49, _arg55);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg041 = data.readInt();
                    int _arg124 = data.readInt();
                    byte[] _arg210 = data.createByteArray();
                    int _arg3_length = data.readInt();
                    if (_arg3_length < 0) {
                        _arg314 = null;
                    } else {
                        _arg314 = new byte[_arg3_length];
                    }
                    int _result4 = calcMac(_arg041, _arg124, _arg210, _arg314);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    reply.writeByteArray(_arg314);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg042 = data.readInt();
                    byte[] _arg125 = data.createByteArray();
                    int _arg211 = data.readInt();
                    byte[] _arg317 = data.createByteArray();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg47 = null;
                    } else {
                        _arg47 = new byte[_arg4_length];
                    }
                    int _result5 = dataEncrypt(_arg042, _arg125, _arg211, _arg317, _arg47);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    reply.writeByteArray(_arg47);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    byte[] _arg126 = data.createByteArray();
                    int _arg212 = data.readInt();
                    byte[] _arg318 = data.createByteArray();
                    int _arg4_length2 = data.readInt();
                    if (_arg4_length2 < 0) {
                        _arg46 = null;
                    } else {
                        _arg46 = new byte[_arg4_length2];
                    }
                    int _result6 = dataDecrypt(_arg043, _arg126, _arg212, _arg318, _arg46);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    reply.writeByteArray(_arg46);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    byte[] _arg127 = data.createByteArray();
                    byte[] _arg213 = data.createByteArray();
                    byte[] _arg319 = data.createByteArray();
                    int _arg410 = data.readInt();
                    int _arg56 = data.readInt();
                    int _result7 = saveKeyDukpt(_arg044, _arg127, _arg213, _arg319, _arg410, _arg56);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg045 = data.readInt();
                    int _arg128 = data.readInt();
                    byte[] _arg214 = data.createByteArray();
                    int _arg3_length2 = data.readInt();
                    if (_arg3_length2 < 0) {
                        _arg313 = null;
                    } else {
                        _arg313 = new byte[_arg3_length2];
                    }
                    int _result8 = calcMacDukpt(_arg045, _arg128, _arg214, _arg313);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    reply.writeByteArray(_arg313);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    byte[] _arg129 = data.createByteArray();
                    int _arg215 = data.readInt();
                    byte[] _arg320 = data.createByteArray();
                    int _arg4_length3 = data.readInt();
                    if (_arg4_length3 < 0) {
                        _arg45 = null;
                    } else {
                        _arg45 = new byte[_arg4_length3];
                    }
                    int _result9 = dataEncryptDukpt(_arg046, _arg129, _arg215, _arg320, _arg45);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg45);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    byte[] _arg130 = data.createByteArray();
                    int _arg216 = data.readInt();
                    byte[] _arg321 = data.createByteArray();
                    int _arg4_length4 = data.readInt();
                    if (_arg4_length4 < 0) {
                        _arg44 = null;
                    } else {
                        _arg44 = new byte[_arg4_length4];
                    }
                    int _result10 = dataDecryptDukpt(_arg047, _arg130, _arg216, _arg321, _arg44);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeByteArray(_arg44);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg048 = data.readInt();
                    int _result11 = dukptIncreaseKSN(_arg048);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg049 = data.readInt();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg121 = null;
                    } else {
                        _arg121 = new byte[_arg1_length];
                    }
                    int _result12 = dukptCurrentKSN(_arg049, _arg121);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    reply.writeByteArray(_arg121);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg050 = data.readInt();
                    int _arg131 = data.readInt();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg27 = null;
                    } else {
                        _arg27 = new byte[_arg2_length];
                    }
                    int _result13 = getKeyCheckValue(_arg050, _arg131, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    reply.writeByteArray(_arg27);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg051 = data.readString();
                    int _arg1_length2 = data.readInt();
                    if (_arg1_length2 < 0) {
                        _arg120 = null;
                    } else {
                        _arg120 = new byte[_arg1_length2];
                    }
                    int _result14 = getTUSNEncryptData(_arg051, _arg120);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    reply.writeByteArray(_arg120);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg052 = data.createByteArray();
                    int _result15 = storeSM4Key(_arg052);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    break;
                case TRANSACTION_encryptDataBySM4Key /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg053 = data.createByteArray();
                    int _arg1_length3 = data.readInt();
                    if (_arg1_length3 < 0) {
                        _arg119 = null;
                    } else {
                        _arg119 = new byte[_arg1_length3];
                    }
                    int _result16 = encryptDataBySM4Key(_arg053, _arg119);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    reply.writeByteArray(_arg119);
                    break;
                case TRANSACTION_getSecStatus /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result17 = getSecStatus();
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_verifyApkSign /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg054 = data.createByteArray();
                    int _result18 = verifyApkSign(_arg054, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    break;
                case TRANSACTION_getAuthStatus /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg055 = data.readInt();
                    String _result19 = getAuthStatus(_arg055);
                    reply.writeNoException();
                    reply.writeString(_result19);
                    break;
                case TRANSACTION_getTermStatus /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _result20 = getTermStatus();
                    reply.writeNoException();
                    reply.writeString(_result20);
                    break;
                case TRANSACTION_setTermStatus /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result21 = setTermStatus();
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    break;
                case TRANSACTION_sysRequestAuth /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte _arg056 = data.readByte();
                    int _arg132 = data.readInt();
                    String _arg217 = data.readString();
                    int _arg3_length3 = data.readInt();
                    if (_arg3_length3 < 0) {
                        _arg312 = null;
                    } else {
                        _arg312 = new byte[_arg3_length3];
                    }
                    int _result22 = sysRequestAuth(_arg056, _arg132, _arg217, _arg312);
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    reply.writeByteArray(_arg312);
                    break;
                case TRANSACTION_sysConfirmAuth /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg057 = data.createByteArray();
                    int _result23 = sysConfirmAuth(_arg057);
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    break;
                case TRANSACTION_saveTerminalKey /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg058 = data.createByteArray();
                    int _result24 = saveTerminalKey(_arg058, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    break;
                case TRANSACTION_readTerminalPuk /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0_length = data.readInt();
                    if (_arg0_length < 0) {
                        _arg037 = null;
                    } else {
                        _arg037 = new byte[_arg0_length];
                    }
                    int _result25 = readTerminalPuk(_arg037);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    reply.writeByteArray(_arg037);
                    break;
                case TRANSACTION_getTerminalCertData /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg059 = data.createByteArray();
                    int _arg1_length4 = data.readInt();
                    if (_arg1_length4 < 0) {
                        _arg118 = null;
                    } else {
                        _arg118 = new byte[_arg1_length4];
                    }
                    int _result26 = getTerminalCertData(_arg059, _arg118);
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    reply.writeByteArray(_arg118);
                    break;
                case TRANSACTION_generateRSAKeys /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg060 = data.readInt();
                    int _arg133 = data.readInt();
                    int _arg218 = data.readInt();
                    String _arg322 = data.readString();
                    int _result27 = generateRSAKeys(_arg060, _arg133, _arg218, _arg322);
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    break;
                case TRANSACTION_getRSAPublicKey /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg061 = data.readInt();
                    int _arg1_length5 = data.readInt();
                    if (_arg1_length5 < 0) {
                        _arg117 = null;
                    } else {
                        _arg117 = new byte[_arg1_length5];
                    }
                    int _result28 = getRSAPublicKey(_arg061, _arg117);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    reply.writeByteArray(_arg117);
                    break;
                case TRANSACTION_getRSAPrivateKey /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg062 = data.readInt();
                    int _arg1_length6 = data.readInt();
                    if (_arg1_length6 < 0) {
                        _arg116 = null;
                    } else {
                        _arg116 = new byte[_arg1_length6];
                    }
                    int _result29 = getRSAPrivateKey(_arg062, _arg116);
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    reply.writeByteArray(_arg116);
                    break;
                case TRANSACTION_dataEncryptRSA /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg063 = data.readString();
                    int _arg134 = data.readInt();
                    byte[] _arg219 = data.createByteArray();
                    int _arg3_length4 = data.readInt();
                    if (_arg3_length4 < 0) {
                        _arg311 = null;
                    } else {
                        _arg311 = new byte[_arg3_length4];
                    }
                    int _result30 = dataEncryptRSA(_arg063, _arg134, _arg219, _arg311);
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    reply.writeByteArray(_arg311);
                    break;
                case TRANSACTION_dataDecryptRSA /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg064 = data.readString();
                    int _arg135 = data.readInt();
                    byte[] _arg220 = data.createByteArray();
                    int _arg3_length5 = data.readInt();
                    if (_arg3_length5 < 0) {
                        _arg310 = null;
                    } else {
                        _arg310 = new byte[_arg3_length5];
                    }
                    int _result31 = dataDecryptRSA(_arg064, _arg135, _arg220, _arg310);
                    reply.writeNoException();
                    reply.writeInt(_result31);
                    reply.writeByteArray(_arg310);
                    break;
                case TRANSACTION_removeRSAKey /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg065 = data.readInt();
                    int _result32 = removeRSAKey(_arg065);
                    reply.writeNoException();
                    reply.writeInt(_result32);
                    break;
                case TRANSACTION_storeCertificate /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg066 = data.readInt();
                    int _result33 = storeCertificate(_arg066, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result33);
                    break;
                case TRANSACTION_getCertificate /* 34 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg067 = data.readInt();
                    int _arg1_length7 = data.readInt();
                    if (_arg1_length7 < 0) {
                        _arg115 = null;
                    } else {
                        _arg115 = new byte[_arg1_length7];
                    }
                    int _result34 = getCertificate(_arg067, _arg115);
                    reply.writeNoException();
                    reply.writeInt(_result34);
                    reply.writeByteArray(_arg115);
                    break;
                case TRANSACTION_dukptGetInitKSN /* 35 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0_length2 = data.readInt();
                    if (_arg0_length2 < 0) {
                        _arg036 = null;
                    } else {
                        _arg036 = new byte[_arg0_length2];
                    }
                    int _result35 = dukptGetInitKSN(_arg036);
                    reply.writeNoException();
                    reply.writeInt(_result35);
                    reply.writeByteArray(_arg036);
                    break;
                case TRANSACTION_signingRSA /* 36 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg068 = data.readString();
                    int _arg136 = data.readInt();
                    byte[] _arg221 = data.createByteArray();
                    int _arg3_length6 = data.readInt();
                    if (_arg3_length6 < 0) {
                        _arg39 = null;
                    } else {
                        _arg39 = new byte[_arg3_length6];
                    }
                    int _result36 = signingRSA(_arg068, _arg136, _arg221, _arg39);
                    reply.writeNoException();
                    reply.writeInt(_result36);
                    reply.writeByteArray(_arg39);
                    break;
                case TRANSACTION_verifySignatureRSA /* 37 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg069 = data.readString();
                    byte[] _arg137 = data.createByteArray();
                    byte[] _arg222 = data.createByteArray();
                    byte[] _arg323 = data.createByteArray();
                    int _result37 = verifySignatureRSA(_arg069, _arg137, _arg222, _arg323);
                    reply.writeNoException();
                    reply.writeInt(_result37);
                    break;
                case TRANSACTION_injectPlaintextKey /* 38 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg070 = data.readString();
                    int _arg138 = data.readInt();
                    byte[] _arg223 = data.createByteArray();
                    byte[] _arg324 = data.createByteArray();
                    int _arg411 = data.readInt();
                    int _arg57 = data.readInt();
                    int _result38 = injectPlaintextKey(_arg070, _arg138, _arg223, _arg324, _arg411, _arg57);
                    reply.writeNoException();
                    reply.writeInt(_result38);
                    break;
                case TRANSACTION_injectCiphertextKey /* 39 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg071 = data.readString();
                    int _arg139 = data.readInt();
                    byte[] _arg224 = data.createByteArray();
                    byte[] _arg325 = data.createByteArray();
                    int _arg412 = data.readInt();
                    int _arg58 = data.readInt();
                    int _arg62 = data.readInt();
                    int _result39 = injectCiphertextKey(_arg071, _arg139, _arg224, _arg325, _arg412, _arg58, _arg62);
                    reply.writeNoException();
                    reply.writeInt(_result39);
                    break;
                case TRANSACTION_dataEncryptDukptEx /* 40 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg072 = data.readInt();
                    int _arg140 = data.readInt();
                    byte[] _arg225 = data.createByteArray();
                    int _arg326 = data.readInt();
                    byte[] _arg413 = data.createByteArray();
                    int _arg5_length = data.readInt();
                    if (_arg5_length < 0) {
                        _arg54 = null;
                    } else {
                        _arg54 = new byte[_arg5_length];
                    }
                    int _result40 = dataEncryptDukptEx(_arg072, _arg140, _arg225, _arg326, _arg413, _arg54);
                    reply.writeNoException();
                    reply.writeInt(_result40);
                    reply.writeByteArray(_arg54);
                    break;
                case TRANSACTION_dataDecryptDukptEx /* 41 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg073 = data.readInt();
                    int _arg141 = data.readInt();
                    byte[] _arg226 = data.createByteArray();
                    int _arg327 = data.readInt();
                    byte[] _arg414 = data.createByteArray();
                    int _arg5_length2 = data.readInt();
                    if (_arg5_length2 < 0) {
                        _arg53 = null;
                    } else {
                        _arg53 = new byte[_arg5_length2];
                    }
                    int _result41 = dataDecryptDukptEx(_arg073, _arg141, _arg226, _arg327, _arg414, _arg53);
                    reply.writeNoException();
                    reply.writeInt(_result41);
                    reply.writeByteArray(_arg53);
                    break;
                case TRANSACTION_calcMacDukptEx /* 42 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg074 = data.readInt();
                    int _arg142 = data.readInt();
                    int _arg227 = data.readInt();
                    byte[] _arg328 = data.createByteArray();
                    int _arg4_length5 = data.readInt();
                    if (_arg4_length5 < 0) {
                        _arg43 = null;
                    } else {
                        _arg43 = new byte[_arg4_length5];
                    }
                    int _result42 = calcMacDukptEx(_arg074, _arg142, _arg227, _arg328, _arg43);
                    reply.writeNoException();
                    reply.writeInt(_result42);
                    reply.writeByteArray(_arg43);
                    break;
                case TRANSACTION_verifyMacDukptEx /* 43 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg075 = data.readInt();
                    int _arg143 = data.readInt();
                    int _arg228 = data.readInt();
                    byte[] _arg329 = data.createByteArray();
                    byte[] _arg415 = data.createByteArray();
                    int _result43 = verifyMacDukptEx(_arg075, _arg143, _arg228, _arg329, _arg415);
                    reply.writeNoException();
                    reply.writeInt(_result43);
                    break;
                case TRANSACTION_saveTR31Key /* 44 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg076 = data.createByteArray();
                    int _arg144 = data.readInt();
                    int _arg229 = data.readInt();
                    int _result44 = saveTR31Key(_arg076, _arg144, _arg229);
                    reply.writeNoException();
                    reply.writeInt(_result44);
                    break;
                case TRANSACTION_saveCiphertextKeyRSA /* 45 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg077 = data.readInt();
                    byte[] _arg145 = data.createByteArray();
                    byte[] _arg230 = data.createByteArray();
                    int _arg330 = data.readInt();
                    int _arg416 = data.readInt();
                    int _arg59 = data.readInt();
                    String _arg63 = data.readString();
                    int _result45 = saveCiphertextKeyRSA(_arg077, _arg145, _arg230, _arg330, _arg416, _arg59, _arg63);
                    reply.writeNoException();
                    reply.writeInt(_result45);
                    break;
                case TRANSACTION_saveRSAKey /* 46 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg078 = data.readInt();
                    byte[] _arg146 = data.createByteArray();
                    int _arg231 = data.readInt();
                    int _result46 = saveRSAKey(_arg078, _arg146, _arg231);
                    reply.writeNoException();
                    reply.writeInt(_result46);
                    break;
                case TRANSACTION_deleteKey /* 47 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg079 = data.readInt();
                    int _result47 = deleteKey(_arg079, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result47);
                    break;
                case TRANSACTION_saveKeyDukptAES /* 48 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg080 = data.readInt();
                    int _arg147 = data.readInt();
                    byte[] _arg232 = data.createByteArray();
                    byte[] _arg331 = data.createByteArray();
                    byte[] _arg417 = data.createByteArray();
                    int _arg510 = data.readInt();
                    int _arg64 = data.readInt();
                    int _result48 = saveKeyDukptAES(_arg080, _arg147, _arg232, _arg331, _arg417, _arg510, _arg64);
                    reply.writeNoException();
                    reply.writeInt(_result48);
                    break;
                case TRANSACTION_calcMacEx /* 49 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg081 = data.readInt();
                    int _arg148 = data.readInt();
                    int _arg233 = data.readInt();
                    byte[] _arg332 = data.createByteArray();
                    byte[] _arg418 = data.createByteArray();
                    int _arg5_length3 = data.readInt();
                    if (_arg5_length3 < 0) {
                        _arg52 = null;
                    } else {
                        _arg52 = new byte[_arg5_length3];
                    }
                    int _result49 = calcMacEx(_arg081, _arg148, _arg233, _arg332, _arg418, _arg52);
                    reply.writeNoException();
                    reply.writeInt(_result49);
                    reply.writeByteArray(_arg52);
                    break;
                case TRANSACTION_generateSM2Keypair /* 50 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg082 = data.readInt();
                    Bundle _arg149 = new Bundle();
                    int _result50 = generateSM2Keypair(_arg082, _arg149);
                    reply.writeNoException();
                    reply.writeInt(_result50);
                    if (_arg149 != null) {
                        reply.writeInt(1);
                        _arg149.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_injectSM2Key /* 51 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg083 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg114 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg114 = null;
                    }
                    int _result51 = injectSM2Key(_arg083, _arg114);
                    reply.writeNoException();
                    reply.writeInt(_result51);
                    break;
                case TRANSACTION_sm2Sign /* 52 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg084 = data.readInt();
                    int _arg150 = data.readInt();
                    byte[] _arg234 = data.createByteArray();
                    byte[] _arg333 = data.createByteArray();
                    int _arg4_length6 = data.readInt();
                    if (_arg4_length6 < 0) {
                        _arg42 = null;
                    } else {
                        _arg42 = new byte[_arg4_length6];
                    }
                    int _result52 = sm2Sign(_arg084, _arg150, _arg234, _arg333, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result52);
                    reply.writeByteArray(_arg42);
                    break;
                case TRANSACTION_sm2VerifySign /* 53 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg085 = data.readInt();
                    byte[] _arg151 = data.createByteArray();
                    byte[] _arg235 = data.createByteArray();
                    byte[] _arg334 = data.createByteArray();
                    int _result53 = sm2VerifySign(_arg085, _arg151, _arg235, _arg334);
                    reply.writeNoException();
                    reply.writeInt(_result53);
                    break;
                case TRANSACTION_sm2EncryptData /* 54 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg086 = data.readInt();
                    byte[] _arg152 = data.createByteArray();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg26 = null;
                    } else {
                        _arg26 = new byte[_arg2_length2];
                    }
                    int _result54 = sm2EncryptData(_arg086, _arg152, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result54);
                    reply.writeByteArray(_arg26);
                    break;
                case TRANSACTION_sm2DecryptData /* 55 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg087 = data.readInt();
                    byte[] _arg153 = data.createByteArray();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg25 = null;
                    } else {
                        _arg25 = new byte[_arg2_length3];
                    }
                    int _result55 = sm2DecryptData(_arg087, _arg153, _arg25);
                    reply.writeNoException();
                    reply.writeInt(_result55);
                    reply.writeByteArray(_arg25);
                    break;
                case TRANSACTION_calcSecHash /* 56 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg088 = data.readInt();
                    byte[] _arg154 = data.createByteArray();
                    int _arg2_length4 = data.readInt();
                    if (_arg2_length4 < 0) {
                        _arg24 = null;
                    } else {
                        _arg24 = new byte[_arg2_length4];
                    }
                    int _result56 = calcSecHash(_arg088, _arg154, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result56);
                    reply.writeByteArray(_arg24);
                    break;
                case TRANSACTION_verifyMac /* 57 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg089 = data.readInt();
                    int _arg155 = data.readInt();
                    byte[] _arg236 = data.createByteArray();
                    byte[] _arg335 = data.createByteArray();
                    int _result57 = verifyMac(_arg089, _arg155, _arg236, _arg335);
                    reply.writeNoException();
                    reply.writeInt(_result57);
                    break;
                case TRANSACTION_generateRSAKeypair /* 58 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg090 = data.readInt();
                    int _arg156 = data.readInt();
                    String _arg237 = data.readString();
                    int _arg3_length7 = data.readInt();
                    if (_arg3_length7 < 0) {
                        _arg38 = null;
                    } else {
                        _arg38 = new byte[_arg3_length7];
                    }
                    int _result58 = generateRSAKeypair(_arg090, _arg156, _arg237, _arg38);
                    reply.writeNoException();
                    reply.writeInt(_result58);
                    reply.writeByteArray(_arg38);
                    break;
                case TRANSACTION_injectRSAKey /* 59 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg091 = data.readInt();
                    int _arg157 = data.readInt();
                    String _arg238 = data.readString();
                    String _arg336 = data.readString();
                    int _result59 = injectRSAKey(_arg091, _arg157, _arg238, _arg336);
                    reply.writeNoException();
                    reply.writeInt(_result59);
                    break;
                case TRANSACTION_generateSymKey /* 60 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg092 = data.readInt();
                    int _arg158 = data.readInt();
                    int _arg239 = data.readInt();
                    int _result60 = generateSymKey(_arg092, _arg158, _arg239);
                    reply.writeNoException();
                    reply.writeInt(_result60);
                    break;
                case TRANSACTION_injectSymKey /* 61 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg093 = data.readInt();
                    int _arg159 = data.readInt();
                    byte[] _arg240 = data.createByteArray();
                    byte[] _arg337 = data.createByteArray();
                    int _arg419 = data.readInt();
                    int _result61 = injectSymKey(_arg093, _arg159, _arg240, _arg337, _arg419);
                    reply.writeNoException();
                    reply.writeInt(_result61);
                    break;
                case TRANSACTION_hsmSaveKeyShare /* 62 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg094 = data.readInt();
                    byte[] _arg160 = data.createByteArray();
                    byte[] _arg241 = data.createByteArray();
                    int _arg338 = data.readInt();
                    int _arg420 = data.readInt();
                    int _result62 = hsmSaveKeyShare(_arg094, _arg160, _arg241, _arg338, _arg420);
                    reply.writeNoException();
                    reply.writeInt(_result62);
                    break;
                case TRANSACTION_hsmSaveKeyShareDukpt /* 63 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg095 = data.readInt();
                    int _arg161 = data.readInt();
                    byte[] _arg242 = data.createByteArray();
                    byte[] _arg339 = data.createByteArray();
                    byte[] _arg421 = data.createByteArray();
                    int _arg511 = data.readInt();
                    int _arg65 = data.readInt();
                    int _result63 = hsmSaveKeyShareDukpt(_arg095, _arg161, _arg242, _arg339, _arg421, _arg511, _arg65);
                    reply.writeNoException();
                    reply.writeInt(_result63);
                    break;
                case TRANSACTION_hsmCombineKeyShare /* 64 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg096 = data.readInt();
                    int _arg162 = data.readInt();
                    int _arg243 = data.readInt();
                    int _arg340 = data.readInt();
                    int _arg422 = data.readInt();
                    int _arg512 = data.readInt();
                    int _arg6_length = data.readInt();
                    if (_arg6_length < 0) {
                        _arg6 = null;
                    } else {
                        _arg6 = new byte[_arg6_length];
                    }
                    int _result64 = hsmCombineKeyShare(_arg096, _arg162, _arg243, _arg340, _arg422, _arg512, _arg6);
                    reply.writeNoException();
                    reply.writeInt(_result64);
                    reply.writeByteArray(_arg6);
                    break;
                case 65:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg097 = data.readInt();
                    int _arg163 = data.readInt();
                    String _arg244 = data.readString();
                    int _arg3_length8 = data.readInt();
                    if (_arg3_length8 < 0) {
                        _arg37 = null;
                    } else {
                        _arg37 = new byte[_arg3_length8];
                    }
                    int _result65 = hsmGenerateRSAKeypair(_arg097, _arg163, _arg244, _arg37);
                    reply.writeNoException();
                    reply.writeInt(_result65);
                    reply.writeByteArray(_arg37);
                    break;
                case 66:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg098 = data.readInt();
                    int _arg164 = data.readInt();
                    String _arg245 = data.readString();
                    String _arg341 = data.readString();
                    int _result66 = hsmInjectRSAKey(_arg098, _arg164, _arg245, _arg341);
                    reply.writeNoException();
                    reply.writeInt(_result66);
                    break;
                case 67:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg099 = data.readInt();
                    byte[] _arg165 = data.createByteArray();
                    int _arg246 = data.readInt();
                    int _arg342 = data.readInt();
                    int _arg423 = data.readInt();
                    int _arg513 = data.readInt();
                    int _result67 = hsmSaveKeyUnderKEK(_arg099, _arg165, _arg246, _arg342, _arg423, _arg513);
                    reply.writeNoException();
                    reply.writeInt(_result67);
                    break;
                case TRANSACTION_hsmExportKeyUnderKEK /* 68 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0100 = data.readInt();
                    int _arg166 = data.readInt();
                    int _arg247 = data.readInt();
                    int _arg3_length9 = data.readInt();
                    if (_arg3_length9 < 0) {
                        _arg36 = null;
                    } else {
                        _arg36 = new byte[_arg3_length9];
                    }
                    int _result68 = hsmExportKeyUnderKEK(_arg0100, _arg166, _arg247, _arg36);
                    reply.writeNoException();
                    reply.writeInt(_result68);
                    reply.writeByteArray(_arg36);
                    break;
                case TRANSACTION_hsmExportTR31KeyBlock /* 69 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0101 = data.readInt();
                    int _arg167 = data.readInt();
                    byte[] _arg248 = data.createByteArray();
                    int _arg3_length10 = data.readInt();
                    if (_arg3_length10 < 0) {
                        _arg35 = null;
                    } else {
                        _arg35 = new byte[_arg3_length10];
                    }
                    int _result69 = hsmExportTR31KeyBlock(_arg0101, _arg167, _arg248, _arg35);
                    reply.writeNoException();
                    reply.writeInt(_result69);
                    reply.writeByteArray(_arg35);
                    break;
                case TRANSACTION_hsmDestroyKey /* 70 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0102 = data.readInt();
                    int _result70 = hsmDestroyKey(_arg0102);
                    reply.writeNoException();
                    reply.writeInt(_result70);
                    break;
                case TRANSACTION_hsmExchangeKeyEcc /* 71 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0103 = data.readInt();
                    String _arg168 = data.readString();
                    int _arg249 = data.readInt();
                    int _arg343 = data.readInt();
                    int _arg424 = data.readInt();
                    byte[] _arg514 = data.createByteArray();
                    byte[] _arg66 = data.createByteArray();
                    int _arg7_length = data.readInt();
                    if (_arg7_length < 0) {
                        _arg7 = null;
                    } else {
                        _arg7 = new byte[_arg7_length];
                    }
                    int _result71 = hsmExchangeKeyEcc(_arg0103, _arg168, _arg249, _arg343, _arg424, _arg514, _arg66, _arg7);
                    reply.writeNoException();
                    reply.writeInt(_result71);
                    reply.writeByteArray(_arg7);
                    break;
                case TRANSACTION_hsmAsymKeyFun /* 72 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0104 = data.readInt();
                    int _arg169 = data.readInt();
                    int _arg250 = data.readInt();
                    byte[] _arg344 = data.createByteArray();
                    byte[] _arg425 = data.createByteArray();
                    int _result72 = hsmAsymKeyFun(_arg0104, _arg169, _arg250, _arg344, _arg425);
                    reply.writeNoException();
                    reply.writeInt(_result72);
                    reply.writeByteArray(_arg425);
                    break;
                case TRANSACTION_operateSensitiveService /* 73 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0105 = data.readInt();
                    int _result73 = operateSensitiveService(_arg0105, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result73);
                    break;
                case TRANSACTION_rsaEncryptOrDecryptData /* 74 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0106 = data.readInt();
                    int _arg170 = data.readInt();
                    byte[] _arg251 = data.createByteArray();
                    int _arg3_length11 = data.readInt();
                    if (_arg3_length11 < 0) {
                        _arg34 = null;
                    } else {
                        _arg34 = new byte[_arg3_length11];
                    }
                    int _result74 = rsaEncryptOrDecryptData(_arg0106, _arg170, _arg251, _arg34);
                    reply.writeNoException();
                    reply.writeInt(_result74);
                    reply.writeByteArray(_arg34);
                    break;
                case TRANSACTION_storeDeviceCertPrivateKey /* 75 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0107 = data.readInt();
                    int _arg171 = data.readInt();
                    int _arg252 = data.readInt();
                    byte[] _arg345 = data.createByteArray();
                    byte[] _arg426 = data.createByteArray();
                    int _result75 = storeDeviceCertPrivateKey(_arg0107, _arg171, _arg252, _arg345, _arg426);
                    reply.writeNoException();
                    reply.writeInt(_result75);
                    break;
                case TRANSACTION_getDeviceCertificate /* 76 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0108 = data.readInt();
                    int _arg1_length8 = data.readInt();
                    if (_arg1_length8 < 0) {
                        _arg113 = null;
                    } else {
                        _arg113 = new byte[_arg1_length8];
                    }
                    int _result76 = getDeviceCertificate(_arg0108, _arg113);
                    reply.writeNoException();
                    reply.writeInt(_result76);
                    reply.writeByteArray(_arg113);
                    break;
                case TRANSACTION_devicePrivateKeyRecover /* 77 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0109 = data.readInt();
                    int _arg172 = data.readInt();
                    int _arg253 = data.readInt();
                    byte[] _arg346 = data.createByteArray();
                    int _arg4_length7 = data.readInt();
                    if (_arg4_length7 < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new byte[_arg4_length7];
                    }
                    int _result77 = devicePrivateKeyRecover(_arg0109, _arg172, _arg253, _arg346, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result77);
                    reply.writeByteArray(_arg4);
                    break;
                case TRANSACTION_getKeyCheckValueEx /* 78 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg035 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg035 = null;
                    }
                    int _arg1_length9 = data.readInt();
                    if (_arg1_length9 < 0) {
                        _arg112 = null;
                    } else {
                        _arg112 = new byte[_arg1_length9];
                    }
                    int _result78 = getKeyCheckValueEx(_arg035, _arg112);
                    reply.writeNoException();
                    reply.writeInt(_result78);
                    reply.writeByteArray(_arg112);
                    break;
                case TRANSACTION_deleteKeyEx /* 79 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg034 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg034 = null;
                    }
                    int _result79 = deleteKeyEx(_arg034);
                    reply.writeNoException();
                    reply.writeInt(_result79);
                    break;
                case TRANSACTION_injectCiphertextKeyEx /* 80 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg033 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg033 = null;
                    }
                    int _result80 = injectCiphertextKeyEx(_arg033);
                    reply.writeNoException();
                    reply.writeInt(_result80);
                    break;
                case TRANSACTION_injectKeyDukptEx /* 81 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg032 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg032 = null;
                    }
                    int _result81 = injectKeyDukptEx(_arg032);
                    reply.writeNoException();
                    reply.writeInt(_result81);
                    break;
                case TRANSACTION_saveKeyEx /* 82 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg031 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg031 = null;
                    }
                    int _result82 = saveKeyEx(_arg031);
                    reply.writeNoException();
                    reply.writeInt(_result82);
                    break;
                case TRANSACTION_calcMacExtended /* 83 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg030 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg030 = null;
                    }
                    int _arg1_length10 = data.readInt();
                    if (_arg1_length10 < 0) {
                        _arg111 = null;
                    } else {
                        _arg111 = new byte[_arg1_length10];
                    }
                    int _result83 = calcMacExtended(_arg030, _arg111);
                    reply.writeNoException();
                    reply.writeInt(_result83);
                    reply.writeByteArray(_arg111);
                    break;
                case TRANSACTION_calcMacDukptExtended /* 84 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg029 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg029 = null;
                    }
                    int _arg1_length11 = data.readInt();
                    if (_arg1_length11 < 0) {
                        _arg110 = null;
                    } else {
                        _arg110 = new byte[_arg1_length11];
                    }
                    int _result84 = calcMacDukptExtended(_arg029, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result84);
                    reply.writeByteArray(_arg110);
                    break;
                case TRANSACTION_readRSAKey /* 85 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0110 = data.readInt();
                    Bundle _arg173 = new Bundle();
                    int _result85 = readRSAKey(_arg0110, _arg173);
                    reply.writeNoException();
                    reply.writeInt(_result85);
                    if (_arg173 != null) {
                        reply.writeInt(1);
                        _arg173.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_getKeyLength /* 86 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0111 = data.readInt();
                    int _result86 = getKeyLength(_arg0111, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result86);
                    break;
                case TRANSACTION_writeKeyVariable /* 87 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg028 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg028 = null;
                    }
                    int _result87 = writeKeyVariable(_arg028);
                    reply.writeNoException();
                    reply.writeInt(_result87);
                    break;
                case TRANSACTION_secKeyIoControl /* 88 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0112 = data.readInt();
                    int _arg174 = data.readInt();
                    byte[] _arg254 = data.createByteArray();
                    int _arg3_length12 = data.readInt();
                    if (_arg3_length12 < 0) {
                        _arg33 = null;
                    } else {
                        _arg33 = new byte[_arg3_length12];
                    }
                    int _result88 = secKeyIoControl(_arg0112, _arg174, _arg254, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result88);
                    reply.writeByteArray(_arg33);
                    break;
                case TRANSACTION_apacsMac /* 89 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0113 = data.readInt();
                    int _arg175 = data.readInt();
                    int _arg255 = data.readInt();
                    int _arg347 = data.readInt();
                    byte[] _arg427 = data.createByteArray();
                    int _arg5_length4 = data.readInt();
                    if (_arg5_length4 < 0) {
                        _arg5 = null;
                    } else {
                        _arg5 = new byte[_arg5_length4];
                    }
                    int _result89 = apacsMac(_arg0113, _arg175, _arg255, _arg347, _arg427, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result89);
                    reply.writeByteArray(_arg5);
                    break;
                case TRANSACTION_hsmSaveKeyUnderKEKEx /* 90 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg027 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg027 = null;
                    }
                    int _result90 = hsmSaveKeyUnderKEKEx(_arg027);
                    reply.writeNoException();
                    reply.writeInt(_result90);
                    break;
                case TRANSACTION_hsmExportKeyUnderKEKEx /* 91 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg026 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg026 = null;
                    }
                    int _arg1_length12 = data.readInt();
                    if (_arg1_length12 < 0) {
                        _arg19 = null;
                    } else {
                        _arg19 = new byte[_arg1_length12];
                    }
                    int _result91 = hsmExportKeyUnderKEKEx(_arg026, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result91);
                    reply.writeByteArray(_arg19);
                    break;
                case TRANSACTION_hsmGenerateKeyByOaep /* 92 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0114 = data.readInt();
                    int _arg176 = data.readInt();
                    int _arg256 = data.readInt();
                    int _arg348 = data.readInt();
                    byte[] _arg428 = data.createByteArray();
                    byte[] _arg515 = data.createByteArray();
                    int _result92 = hsmGenerateKeyByOaep(_arg0114, _arg176, _arg256, _arg348, _arg428, _arg515);
                    reply.writeNoException();
                    reply.writeInt(_result92);
                    break;
                case TRANSACTION_saveCiphertextKeyUnderRSA /* 93 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0115 = data.readInt();
                    int _arg177 = data.readInt();
                    int _arg257 = data.readInt();
                    int _arg349 = data.readInt();
                    byte[] _arg429 = data.createByteArray();
                    byte[] _arg516 = data.createByteArray();
                    int _result93 = saveCiphertextKeyUnderRSA(_arg0115, _arg177, _arg257, _arg349, _arg429, _arg516);
                    reply.writeNoException();
                    reply.writeInt(_result93);
                    break;
                case TRANSACTION_injectCiphertextKeyUnderRSA /* 94 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0116 = data.readString();
                    int _arg178 = data.readInt();
                    int _arg258 = data.readInt();
                    int _arg350 = data.readInt();
                    int _arg430 = data.readInt();
                    byte[] _arg517 = data.createByteArray();
                    byte[] _arg67 = data.createByteArray();
                    int _result94 = injectCiphertextKeyUnderRSA(_arg0116, _arg178, _arg258, _arg350, _arg430, _arg517, _arg67);
                    reply.writeNoException();
                    reply.writeInt(_result94);
                    break;
                case TRANSACTION_generateSymKeyEx /* 95 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg025 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg025 = null;
                    }
                    int _result95 = generateSymKeyEx(_arg025);
                    reply.writeNoException();
                    reply.writeInt(_result95);
                    break;
                case TRANSACTION_injectSymKeyEx /* 96 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg024 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg024 = null;
                    }
                    int _result96 = injectSymKeyEx(_arg024);
                    reply.writeNoException();
                    reply.writeInt(_result96);
                    break;
                case TRANSACTION_injectDeviceCertPrivateKey /* 97 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg023 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg023 = null;
                    }
                    int _result97 = injectDeviceCertPrivateKey(_arg023);
                    reply.writeNoException();
                    reply.writeInt(_result97);
                    break;
                case TRANSACTION_generateRSAKeypairEx /* 98 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg022 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg022 = null;
                    }
                    int _arg1_length13 = data.readInt();
                    if (_arg1_length13 < 0) {
                        _arg18 = null;
                    } else {
                        _arg18 = new byte[_arg1_length13];
                    }
                    int _result98 = generateRSAKeypairEx(_arg022, _arg18);
                    reply.writeNoException();
                    reply.writeInt(_result98);
                    reply.writeByteArray(_arg18);
                    break;
                case TRANSACTION_injectRSAKeyEx /* 99 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg021 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg021 = null;
                    }
                    int _result99 = injectRSAKeyEx(_arg021);
                    reply.writeNoException();
                    reply.writeInt(_result99);
                    break;
                case TRANSACTION_setDeviceCertificate /* 100 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0117 = data.readInt();
                    int _result100 = setDeviceCertificate(_arg0117, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result100);
                    break;
                case 101:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg020 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg020 = null;
                    }
                    int _result101 = injectPlaintextKeyWL(_arg020);
                    reply.writeNoException();
                    reply.writeInt(_result101);
                    break;
                case 102:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg019 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg019 = null;
                    }
                    int _result102 = injectCiphertextKeyWL(_arg019);
                    reply.writeNoException();
                    reply.writeInt(_result102);
                    break;
                case 103:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg018 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg018 = null;
                    }
                    int _result103 = injectKeyDukptWL(_arg018);
                    reply.writeNoException();
                    reply.writeInt(_result103);
                    break;
                case 104:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg017 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg017 = null;
                    }
                    int _arg1_length14 = data.readInt();
                    if (_arg1_length14 < 0) {
                        _arg17 = null;
                    } else {
                        _arg17 = new byte[_arg1_length14];
                    }
                    int _result104 = getKeyCheckValueWL(_arg017, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result104);
                    reply.writeByteArray(_arg17);
                    break;
                case 105:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg016 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg016 = null;
                    }
                    int _result105 = deleteKeyWL(_arg016);
                    reply.writeNoException();
                    reply.writeInt(_result105);
                    break;
                case 106:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg015 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg015 = null;
                    }
                    int _arg1_length15 = data.readInt();
                    if (_arg1_length15 < 0) {
                        _arg16 = null;
                    } else {
                        _arg16 = new byte[_arg1_length15];
                    }
                    int _result106 = dataEncryptEx(_arg015, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result106);
                    reply.writeByteArray(_arg16);
                    break;
                case TRANSACTION_dataDecryptEx /* 107 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg014 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg014 = null;
                    }
                    int _arg1_length16 = data.readInt();
                    if (_arg1_length16 < 0) {
                        _arg15 = null;
                    } else {
                        _arg15 = new byte[_arg1_length16];
                    }
                    int _result107 = dataDecryptEx(_arg014, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result107);
                    reply.writeByteArray(_arg15);
                    break;
                case TRANSACTION_queryKeyMappingRecordListWL /* 108 */:
                    data.enforceInterface(DESCRIPTOR);
                    ArrayList arrayList = new ArrayList();
                    int _result108 = queryKeyMappingRecordListWL(arrayList);
                    reply.writeNoException();
                    reply.writeInt(_result108);
                    reply.writeTypedList(arrayList);
                    break;
                case TRANSACTION_genTR34CredTokenWL /* 109 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg013 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg013 = null;
                    }
                    int _arg1_length17 = data.readInt();
                    if (_arg1_length17 < 0) {
                        _arg14 = null;
                    } else {
                        _arg14 = new byte[_arg1_length17];
                    }
                    int _result109 = genTR34CredTokenWL(_arg013, _arg14);
                    reply.writeNoException();
                    reply.writeInt(_result109);
                    reply.writeByteArray(_arg14);
                    break;
                case TRANSACTION_genTR34RandomTokenWL /* 110 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0118 = data.readInt();
                    int _arg1_length18 = data.readInt();
                    if (_arg1_length18 < 0) {
                        _arg13 = null;
                    } else {
                        _arg13 = new byte[_arg1_length18];
                    }
                    int _result110 = genTR34RandomTokenWL(_arg0118, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result110);
                    reply.writeByteArray(_arg13);
                    break;
                case TRANSACTION_validateTR34CredTokenWL /* 111 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg0119 = data.createByteArray();
                    int _result111 = validateTR34CredTokenWL(_arg0119);
                    reply.writeNoException();
                    reply.writeInt(_result111);
                    break;
                case TRANSACTION_validateTR34KeyTokenWL /* 112 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg012 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg012 = null;
                    }
                    int _result112 = validateTR34KeyTokenWL(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result112);
                    break;
                case TRANSACTION_validateTR34UNBTokenWL /* 113 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg011 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    int _result113 = validateTR34UNBTokenWL(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result113);
                    break;
                case TRANSACTION_queryKeyMappingRecordList /* 114 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    ArrayList arrayList2 = new ArrayList();
                    int _result114 = queryKeyMappingRecordList(_arg010, arrayList2);
                    reply.writeNoException();
                    reply.writeInt(_result114);
                    reply.writeTypedList(arrayList2);
                    break;
                case TRANSACTION_readSM2Key /* 115 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0120 = data.readInt();
                    Bundle _arg179 = new Bundle();
                    int _result115 = readSM2Key(_arg0120, _arg179);
                    reply.writeNoException();
                    reply.writeInt(_result115);
                    if (_arg179 != null) {
                        reply.writeInt(1);
                        _arg179.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_calcSM3HashWithID /* 116 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0121 = data.readInt();
                    byte[] _arg180 = data.createByteArray();
                    byte[] _arg259 = data.createByteArray();
                    int _arg3_length13 = data.readInt();
                    if (_arg3_length13 < 0) {
                        _arg32 = null;
                    } else {
                        _arg32 = new byte[_arg3_length13];
                    }
                    int _result116 = calcSM3HashWithID(_arg0121, _arg180, _arg259, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result116);
                    reply.writeByteArray(_arg32);
                    break;
                case TRANSACTION_sm2SingleSign /* 117 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0122 = data.readInt();
                    byte[] _arg181 = data.createByteArray();
                    int _arg2_length5 = data.readInt();
                    if (_arg2_length5 < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length5];
                    }
                    int _result117 = sm2SingleSign(_arg0122, _arg181, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result117);
                    reply.writeByteArray(_arg23);
                    break;
                case TRANSACTION_injectTR31Key /* 118 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    int _result118 = injectTR31Key(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result118);
                    break;
                case TRANSACTION_hsmExchangeKeyEccEx /* 119 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    int _arg1_length19 = data.readInt();
                    if (_arg1_length19 < 0) {
                        _arg12 = null;
                    } else {
                        _arg12 = new byte[_arg1_length19];
                    }
                    int _result119 = hsmExchangeKeyEccEx(_arg08, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result119);
                    reply.writeByteArray(_arg12);
                    break;
                case TRANSACTION_saveCiphertextKeyUnderRSAEx /* 120 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _result120 = saveCiphertextKeyUnderRSAEx(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result120);
                    break;
                case TRANSACTION_injectCiphertextKeyUnderRSAEx /* 121 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    int _result121 = injectCiphertextKeyUnderRSAEx(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result121);
                    break;
                case TRANSACTION_convertKeyType /* 122 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _result122 = convertKeyType(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result122);
                    break;
                case TRANSACTION_hsmExchangeKeyEccKdf /* 123 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    Bundle _arg260 = new Bundle();
                    int _result123 = hsmExchangeKeyEccKdf(_arg04, _arg1, _arg260);
                    reply.writeNoException();
                    reply.writeInt(_result123);
                    if (_arg260 != null) {
                        reply.writeInt(1);
                        _arg260.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_generateSM2KeypairEx /* 124 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    Bundle _arg182 = new Bundle();
                    int _result124 = generateSM2KeypairEx(_arg03, _arg182);
                    reply.writeNoException();
                    reply.writeInt(_result124);
                    if (_arg182 != null) {
                        reply.writeInt(1);
                        _arg182.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_injectSM2KeyEx /* 125 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result125 = injectSM2KeyEx(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result125);
                    break;
                case TRANSACTION_generateEccKeypair /* 126 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0123 = data.readInt();
                    int _arg183 = data.readInt();
                    int _arg2_length6 = data.readInt();
                    if (_arg2_length6 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length6];
                    }
                    int _result126 = generateEccKeypair(_arg0123, _arg183, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result126);
                    reply.writeByteArray(_arg22);
                    break;
                case TRANSACTION_injectEccPubKey /* 127 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0124 = data.readInt();
                    int _arg184 = data.readInt();
                    byte[] _arg261 = data.createByteArray();
                    int _result127 = injectEccPubKey(_arg0124, _arg184, _arg261);
                    reply.writeNoException();
                    reply.writeInt(_result127);
                    break;
                case 128:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0125 = data.readInt();
                    int _arg185 = data.readInt();
                    byte[] _arg262 = data.createByteArray();
                    int _result128 = injectEccPvtKey(_arg0125, _arg185, _arg262);
                    reply.writeNoException();
                    reply.writeInt(_result128);
                    break;
                case 129:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0126 = data.readInt();
                    Bundle _arg186 = new Bundle();
                    int _result129 = getEccPubKey(_arg0126, _arg186);
                    reply.writeNoException();
                    reply.writeInt(_result129);
                    if (_arg186 != null) {
                        reply.writeInt(1);
                        _arg186.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 130:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0127 = data.readInt();
                    byte[] _arg187 = data.createByteArray();
                    int _arg2_length7 = data.readInt();
                    if (_arg2_length7 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length7];
                    }
                    int _result130 = eccRecover(_arg0127, _arg187, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result130);
                    reply.writeByteArray(_arg2);
                    break;
                case 131:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0128 = data.readInt();
                    int _arg188 = data.readInt();
                    byte[] _arg263 = data.createByteArray();
                    int _arg3_length14 = data.readInt();
                    if (_arg3_length14 < 0) {
                        _arg3 = null;
                    } else {
                        _arg3 = new byte[_arg3_length14];
                    }
                    int _result131 = eccSign(_arg0128, _arg188, _arg263, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result131);
                    reply.writeByteArray(_arg3);
                    break;
                case TRANSACTION_eccVerify /* 132 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0129 = data.readInt();
                    int _arg189 = data.readInt();
                    byte[] _arg264 = data.createByteArray();
                    byte[] _arg351 = data.createByteArray();
                    int _result132 = eccVerify(_arg0129, _arg189, _arg264, _arg351);
                    reply.writeNoException();
                    reply.writeInt(_result132);
                    break;
                case TRANSACTION_queryKeyMappingRecord /* 133 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Bundle _arg190 = new Bundle();
                    int _result133 = queryKeyMappingRecord(_arg0, _arg190);
                    reply.writeNoException();
                    reply.writeInt(_result133);
                    if (_arg190 != null) {
                        reply.writeInt(1);
                        _arg190.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/security/SecurityOptV2$Stub$Proxy.class */
        private static class Proxy implements SecurityOptV2 {
            private IBinder mRemote;
            public static SecurityOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveBaseKey(int destinationIndex, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(destinationIndex);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int savePlaintextKey(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSavePlaintextKey = Stub.getDefaultImpl().savePlaintextKey(keyType, keyValue, checkValue, keyAlgType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iSavePlaintextKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveCiphertextKey(int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveCiphertextKey = Stub.getDefaultImpl().saveCiphertextKey(keyType, keyValue, checkValue, encryptIndex, keyAlgType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveCiphertextKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcMac(int keyIndex, int macAlgType, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macAlgType);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMac = Stub.getDefaultImpl().calcMac(keyIndex, macAlgType, dataIn, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataEncrypt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncrypt = Stub.getDefaultImpl().dataEncrypt(keyIndex, dataIn, encryptionMode, iv, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataDecrypt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecrypt = Stub.getDefaultImpl().dataDecrypt(keyIndex, dataIn, encryptionMode, iv, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveKeyDukpt(int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(ksn);
                    _data.writeInt(encryptType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKeyDukpt = Stub.getDefaultImpl().saveKeyDukpt(keyType, keyValue, checkValue, ksn, encryptType, keyIndex);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataEncryptDukpt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncryptDukpt = Stub.getDefaultImpl().dataEncryptDukpt(keyIndex, dataIn, encryptionMode, iv, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataDecryptDukpt(int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecryptDukpt = Stub.getDefaultImpl().dataDecryptDukpt(keyIndex, dataIn, encryptionMode, iv, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dukptIncreaseKSN(int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDukptIncreaseKSN = Stub.getDefaultImpl().dukptIncreaseKSN(keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iDukptIncreaseKSN;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dukptCurrentKSN(int keyIndex, byte[] outKSN) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    if (outKSN == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outKSN.length);
                    }
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDukptCurrentKSN = Stub.getDefaultImpl().dukptCurrentKSN(keyIndex, outKSN);
                        _reply.recycle();
                        _data.recycle();
                        return iDukptCurrentKSN;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outKSN);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getKeyCheckValue(int keySystem, int keyIndex, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySystem);
                    _data.writeInt(keyIndex);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int keyCheckValue = Stub.getDefaultImpl().getKeyCheckValue(keySystem, keyIndex, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getTUSNEncryptData(String dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int tUSNEncryptData = Stub.getDefaultImpl().getTUSNEncryptData(dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return tUSNEncryptData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int storeSM4Key(byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_encryptDataBySM4Key, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getSecStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getSecStatus, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int verifyApkSign(byte[] hashMessage, byte[] signData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(hashMessage);
                    _data.writeByteArray(signData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_verifyApkSign, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public String getAuthStatus(int type) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getAuthStatus, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public String getTermStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getTermStatus, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int setTermStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setTermStatus, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysRequestAuth, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int sysConfirmAuth(byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sysConfirmAuth, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveTerminalKey(byte[] dataInPuk, byte[] dataInPvk) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataInPuk);
                    _data.writeByteArray(dataInPvk);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveTerminalKey, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_readTerminalPuk, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateRSAKeys(int pubKeyIndex, int pvtKeyIndex, int keysize, String pubExponent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pubKeyIndex);
                    _data.writeInt(pvtKeyIndex);
                    _data.writeInt(keysize);
                    _data.writeString(pubExponent);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateRSAKeys, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateRSAKeys = Stub.getDefaultImpl().generateRSAKeys(pubKeyIndex, pvtKeyIndex, keysize, pubExponent);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateRSAKeys;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getRSAPublicKey(int pubKeyIndex, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pubKeyIndex);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getRSAPublicKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int rSAPublicKey = Stub.getDefaultImpl().getRSAPublicKey(pubKeyIndex, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return rSAPublicKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getRSAPrivateKey(int pvtKeyIndex, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvtKeyIndex);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getRSAPrivateKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int rSAPrivateKey = Stub.getDefaultImpl().getRSAPrivateKey(pvtKeyIndex, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return rSAPrivateKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataEncryptRSA(String transformation, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(transformation);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataEncryptRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncryptRSA = Stub.getDefaultImpl().dataEncryptRSA(transformation, keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataEncryptRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataDecryptRSA(String transformation, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(transformation);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataDecryptRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecryptRSA = Stub.getDefaultImpl().dataDecryptRSA(transformation, keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataDecryptRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int removeRSAKey(int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_removeRSAKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iRemoveRSAKey = Stub.getDefaultImpl().removeRSAKey(keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iRemoveRSAKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int storeCertificate(int certIndex, byte[] certData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    _data.writeByteArray(certData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_storeCertificate, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iStoreCertificate = Stub.getDefaultImpl().storeCertificate(certIndex, certData);
                        _reply.recycle();
                        _data.recycle();
                        return iStoreCertificate;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getCertificate(int certIndex, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getCertificate, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int certificate = Stub.getDefaultImpl().getCertificate(certIndex, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return certificate;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dukptGetInitKSN(byte[] outKSN) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (outKSN == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outKSN.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dukptGetInitKSN, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDukptGetInitKSN = Stub.getDefaultImpl().dukptGetInitKSN(outKSN);
                        _reply.recycle();
                        _data.recycle();
                        return iDukptGetInitKSN;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(outKSN);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int signingRSA(String signAlg, int pvtKeyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(signAlg);
                    _data.writeInt(pvtKeyIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_signingRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSigningRSA = Stub.getDefaultImpl().signingRSA(signAlg, pvtKeyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSigningRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int verifySignatureRSA(String signAlg, byte[] pubKey, byte[] srcData, byte[] signature) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(signAlg);
                    _data.writeByteArray(pubKey);
                    _data.writeByteArray(srcData);
                    _data.writeByteArray(signature);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_verifySignatureRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iVerifySignatureRSA = Stub.getDefaultImpl().verifySignatureRSA(signAlg, pubKey, srcData, signature);
                        _reply.recycle();
                        _data.recycle();
                        return iVerifySignatureRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectPlaintextKey(String targetPkgName, int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPkgName);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectPlaintextKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectPlaintextKey = Stub.getDefaultImpl().injectPlaintextKey(targetPkgName, keyType, keyValue, checkValue, keyAlgType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectPlaintextKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectCiphertextKey(String targetPkgName, int keyType, byte[] keyValue, byte[] checkValue, int encryptIndex, int keyAlgType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPkgName);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(encryptIndex);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectCiphertextKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectCiphertextKey = Stub.getDefaultImpl().injectCiphertextKey(targetPkgName, keyType, keyValue, checkValue, encryptIndex, keyAlgType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectCiphertextKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataEncryptDukptEx(int keySelect, int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySelect);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataEncryptDukptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncryptDukptEx = Stub.getDefaultImpl().dataEncryptDukptEx(keySelect, keyIndex, dataIn, encryptionMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataEncryptDukptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataDecryptDukptEx(int keySelect, int keyIndex, byte[] dataIn, int encryptionMode, byte[] iv, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySelect);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeInt(encryptionMode);
                    _data.writeByteArray(iv);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataDecryptDukptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecryptDukptEx = Stub.getDefaultImpl().dataDecryptDukptEx(keySelect, keyIndex, dataIn, encryptionMode, iv, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataDecryptDukptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcMacDukptEx(int keySelect, int keyIndex, int macType, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySelect);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macType);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcMacDukptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMacDukptEx = Stub.getDefaultImpl().calcMacDukptEx(keySelect, keyIndex, macType, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMacDukptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int verifyMacDukptEx(int keySelect, int keyIndex, int macType, byte[] dataIn, byte[] macData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySelect);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macType);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(macData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_verifyMacDukptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iVerifyMacDukptEx = Stub.getDefaultImpl().verifyMacDukptEx(keySelect, keyIndex, macType, dataIn, macData);
                        _reply.recycle();
                        _data.recycle();
                        return iVerifyMacDukptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveTR31Key(byte[] keyValue, int kbpkIndex, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(keyValue);
                    _data.writeInt(kbpkIndex);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveTR31Key, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveTR31Key = Stub.getDefaultImpl().saveTR31Key(keyValue, kbpkIndex, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveTR31Key;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveCiphertextKeyRSA(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex, int encryptIndexRSA, String transformation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    _data.writeInt(encryptIndexRSA);
                    _data.writeString(transformation);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveCiphertextKeyRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveCiphertextKeyRSA = Stub.getDefaultImpl().saveCiphertextKeyRSA(keyType, keyValue, checkValue, keyAlgType, keyIndex, encryptIndexRSA, transformation);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveCiphertextKeyRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveRSAKey(int keyType, byte[] keyValue, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveRSAKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveRSAKey = Stub.getDefaultImpl().saveRSAKey(keyType, keyValue, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveRSAKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int deleteKey(int keySystem, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySystem);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_deleteKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteKey = Stub.getDefaultImpl().deleteKey(keySystem, keyIndex);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveKeyDukptAES(int dukptKeyType, int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(dukptKeyType);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(ksn);
                    _data.writeInt(encryptType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveKeyDukptAES, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKeyDukptAES = Stub.getDefaultImpl().saveKeyDukptAES(dukptKeyType, keyType, keyValue, checkValue, ksn, encryptType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveKeyDukptAES;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcMacEx(int keyIndex, int keyLen, int macAlgType, byte[] diversify, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keyLen);
                    _data.writeInt(macAlgType);
                    _data.writeByteArray(diversify);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcMacEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMacEx = Stub.getDefaultImpl().calcMacEx(keyIndex, keyLen, macAlgType, diversify, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMacEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcSecHash(int mode, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcSecHash, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcSecHash = Stub.getDefaultImpl().calcSecHash(mode, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcSecHash;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int verifyMac(int keyIndex, int macAlgType, byte[] dataIn, byte[] mac) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(macAlgType);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(mac);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_verifyMac, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iVerifyMac = Stub.getDefaultImpl().verifyMac(keyIndex, macAlgType, dataIn, mac);
                        _reply.recycle();
                        _data.recycle();
                        return iVerifyMac;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateRSAKeypair(int pvkIndex, int keySize, String pubExponent, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeInt(keySize);
                    _data.writeString(pubExponent);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateRSAKeypair, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateRSAKeypair = Stub.getDefaultImpl().generateRSAKeypair(pvkIndex, keySize, pubExponent, dataOut);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectRSAKey(int keyIndex, int keySize, String module, String exponent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keySize);
                    _data.writeString(module);
                    _data.writeString(exponent);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectRSAKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectRSAKey = Stub.getDefaultImpl().injectRSAKey(keyIndex, keySize, module, exponent);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateSymKey(int keyIndex, int keyType, int keyAlgType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateSymKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateSymKey = Stub.getDefaultImpl().generateSymKey(keyIndex, keyType, keyAlgType);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateSymKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectSymKey(int keyIndex, int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(keyAlgType);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectSymKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectSymKey = Stub.getDefaultImpl().injectSymKey(keyIndex, keyType, keyValue, checkValue, keyAlgType);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectSymKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmSaveKeyShare(int keyType, byte[] keyValue, byte[] checkValue, int keyAlgType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmSaveKeyShare, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmSaveKeyShare = Stub.getDefaultImpl().hsmSaveKeyShare(keyType, keyValue, checkValue, keyAlgType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmSaveKeyShare;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmSaveKeyShareDukpt(int dukptKeyType, int keyType, byte[] keyValue, byte[] checkValue, byte[] ksn, int encryptType, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(dukptKeyType);
                    _data.writeInt(keyType);
                    _data.writeByteArray(keyValue);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(ksn);
                    _data.writeInt(encryptType);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmSaveKeyShareDukpt, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmSaveKeyShareDukpt = Stub.getDefaultImpl().hsmSaveKeyShareDukpt(dukptKeyType, keyType, keyValue, checkValue, ksn, encryptType, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmSaveKeyShareDukpt;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmCombineKeyShare(int keyType, int keyAlgType, int keyIndex, int keyShareIndex1, int keyShareIndex2, int keyShareIndex3, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keyShareIndex1);
                    _data.writeInt(keyShareIndex2);
                    _data.writeInt(keyShareIndex3);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmCombineKeyShare, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmCombineKeyShare = Stub.getDefaultImpl().hsmCombineKeyShare(keyType, keyAlgType, keyIndex, keyShareIndex1, keyShareIndex2, keyShareIndex3, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmCombineKeyShare;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmGenerateRSAKeypair(int pvtKeyIndex, int keySize, String pubExponent, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvtKeyIndex);
                    _data.writeInt(keySize);
                    _data.writeString(pubExponent);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(65, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmGenerateRSAKeypair = Stub.getDefaultImpl().hsmGenerateRSAKeypair(pvtKeyIndex, keySize, pubExponent, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmGenerateRSAKeypair;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmInjectRSAKey(int keyIndex, int keySize, String module, String exponent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keySize);
                    _data.writeString(module);
                    _data.writeString(exponent);
                    boolean _status = this.mRemote.transact(66, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmInjectRSAKey = Stub.getDefaultImpl().hsmInjectRSAKey(keyIndex, keySize, module, exponent);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmInjectRSAKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmSaveKeyUnderKEK(int keyIndex, byte[] keyValue, int keyType, int keyAlgType, int encryptKeySystem, int encryptIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(keyValue);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeInt(encryptKeySystem);
                    _data.writeInt(encryptIndex);
                    boolean _status = this.mRemote.transact(67, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmSaveKeyUnderKEK = Stub.getDefaultImpl().hsmSaveKeyUnderKEK(keyIndex, keyValue, keyType, keyAlgType, encryptKeySystem, encryptIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmSaveKeyUnderKEK;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExportKeyUnderKEK(int keyIndex, int kekIndex, int kekKeySystem, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(kekIndex);
                    _data.writeInt(kekKeySystem);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExportKeyUnderKEK, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExportKeyUnderKEK = Stub.getDefaultImpl().hsmExportKeyUnderKEK(keyIndex, kekIndex, kekKeySystem, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExportKeyUnderKEK;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExportTR31KeyBlock(int keyIndex, int encryptIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(encryptIndex);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExportTR31KeyBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExportTR31KeyBlock = Stub.getDefaultImpl().hsmExportTR31KeyBlock(keyIndex, encryptIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExportTR31KeyBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmDestroyKey(int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmDestroyKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmDestroyKey = Stub.getDefaultImpl().hsmDestroyKey(keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmDestroyKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExchangeKeyEcc(int mode, String curveParam, int keyIndex, int keyType, int keyAlgType, byte[] pubKeyA, byte[] checkValue, byte[] pubKeyB) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeString(curveParam);
                    _data.writeInt(keyIndex);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeByteArray(pubKeyA);
                    _data.writeByteArray(checkValue);
                    if (pubKeyB == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(pubKeyB.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExchangeKeyEcc, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExchangeKeyEcc = Stub.getDefaultImpl().hsmExchangeKeyEcc(mode, curveParam, keyIndex, keyType, keyAlgType, pubKeyA, checkValue, pubKeyB);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExchangeKeyEcc;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(pubKeyB);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmAsymKeyFun(int mode, int keySystem, int keyIndex, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeInt(keySystem);
                    _data.writeInt(keyIndex);
                    _data.writeByteArray(dataIn);
                    _data.writeByteArray(dataOut);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmAsymKeyFun, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmAsymKeyFun = Stub.getDefaultImpl().hsmAsymKeyFun(mode, keySystem, keyIndex, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmAsymKeyFun;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int operateSensitiveService(int mode, byte[] pinPadParam) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeByteArray(pinPadParam);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_operateSensitiveService, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iOperateSensitiveService = Stub.getDefaultImpl().operateSensitiveService(mode, pinPadParam);
                        _reply.recycle();
                        _data.recycle();
                        return iOperateSensitiveService;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int rsaEncryptOrDecryptData(int keyIndex, int padding, byte[] dataIn, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_rsaEncryptOrDecryptData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iRsaEncryptOrDecryptData = Stub.getDefaultImpl().rsaEncryptOrDecryptData(keyIndex, padding, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iRsaEncryptOrDecryptData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_storeDeviceCertPrivateKey, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getDeviceCertificate, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_devicePrivateKeyRecover, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getKeyCheckValueEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getKeyCheckValueEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int keyCheckValueEx = Stub.getDefaultImpl().getKeyCheckValueEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return keyCheckValueEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int deleteKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_deleteKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDeleteKeyEx = Stub.getDefaultImpl().deleteKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iDeleteKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectCiphertextKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectCiphertextKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectCiphertextKeyEx = Stub.getDefaultImpl().injectCiphertextKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectCiphertextKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectKeyDukptEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectKeyDukptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectKeyDukptEx = Stub.getDefaultImpl().injectKeyDukptEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectKeyDukptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveKeyEx = Stub.getDefaultImpl().saveKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcMacExtended(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcMacExtended, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMacExtended = Stub.getDefaultImpl().calcMacExtended(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMacExtended;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int calcMacDukptExtended(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_calcMacDukptExtended, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCalcMacDukptExtended = Stub.getDefaultImpl().calcMacDukptExtended(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iCalcMacDukptExtended;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int readRSAKey(int keyIndex, Bundle keyInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_readRSAKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int rSAKey = Stub.getDefaultImpl().readRSAKey(keyIndex, keyInfo);
                        _reply.recycle();
                        _data.recycle();
                        return rSAKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getKeyLength(int keySystem, int keyIndex) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keySystem);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getKeyLength, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int keyLength = Stub.getDefaultImpl().getKeyLength(keySystem, keyIndex);
                        _reply.recycle();
                        _data.recycle();
                        return keyLength;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int writeKeyVariable(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_writeKeyVariable, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iWriteKeyVariable = Stub.getDefaultImpl().writeKeyVariable(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iWriteKeyVariable;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int secKeyIoControl(int keyIndex, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(ctrCode);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_secKeyIoControl, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSecKeyIoControl = Stub.getDefaultImpl().secKeyIoControl(keyIndex, ctrCode, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSecKeyIoControl;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int apacsMac(int initMakIndex, int makIndex, int pikIndex, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(initMakIndex);
                    _data.writeInt(makIndex);
                    _data.writeInt(pikIndex);
                    _data.writeInt(ctrCode);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_apacsMac, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iApacsMac = Stub.getDefaultImpl().apacsMac(initMakIndex, makIndex, pikIndex, ctrCode, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iApacsMac;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmSaveKeyUnderKEKEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmSaveKeyUnderKEKEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmSaveKeyUnderKEKEx = Stub.getDefaultImpl().hsmSaveKeyUnderKEKEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmSaveKeyUnderKEKEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExportKeyUnderKEKEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExportKeyUnderKEKEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExportKeyUnderKEKEx = Stub.getDefaultImpl().hsmExportKeyUnderKEKEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExportKeyUnderKEKEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmGenerateKeyByOaep(int keyIndex, int dependIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(dependIndex);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmGenerateKeyByOaep, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmGenerateKeyByOaep = Stub.getDefaultImpl().hsmGenerateKeyByOaep(keyIndex, dependIndex, keyType, keyAlgType, checkValue, keyData);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmGenerateKeyByOaep;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveCiphertextKeyUnderRSA(int keyIndex, int rsaKeyIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(rsaKeyIndex);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveCiphertextKeyUnderRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveCiphertextKeyUnderRSA = Stub.getDefaultImpl().saveCiphertextKeyUnderRSA(keyIndex, rsaKeyIndex, keyType, keyAlgType, checkValue, keyData);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveCiphertextKeyUnderRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectCiphertextKeyUnderRSA(String targetPkgName, int keyIndex, int rsaKeyIndex, int keyType, int keyAlgType, byte[] checkValue, byte[] keyData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPkgName);
                    _data.writeInt(keyIndex);
                    _data.writeInt(rsaKeyIndex);
                    _data.writeInt(keyType);
                    _data.writeInt(keyAlgType);
                    _data.writeByteArray(checkValue);
                    _data.writeByteArray(keyData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectCiphertextKeyUnderRSA, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectCiphertextKeyUnderRSA = Stub.getDefaultImpl().injectCiphertextKeyUnderRSA(targetPkgName, keyIndex, rsaKeyIndex, keyType, keyAlgType, checkValue, keyData);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectCiphertextKeyUnderRSA;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateSymKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateSymKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateSymKeyEx = Stub.getDefaultImpl().generateSymKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateSymKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectSymKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectSymKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectSymKeyEx = Stub.getDefaultImpl().injectSymKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectSymKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectDeviceCertPrivateKey(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectDeviceCertPrivateKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectDeviceCertPrivateKey = Stub.getDefaultImpl().injectDeviceCertPrivateKey(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectDeviceCertPrivateKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateRSAKeypairEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateRSAKeypairEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateRSAKeypairEx = Stub.getDefaultImpl().generateRSAKeypairEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateRSAKeypairEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectRSAKeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectRSAKeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectRSAKeyEx = Stub.getDefaultImpl().injectRSAKeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectRSAKeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int setDeviceCertificate(int certIndex, byte[] certData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certIndex);
                    _data.writeByteArray(certData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setDeviceCertificate, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int deviceCertificate = Stub.getDefaultImpl().setDeviceCertificate(certIndex, certData);
                        _reply.recycle();
                        _data.recycle();
                        return deviceCertificate;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectPlaintextKeyWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(101, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectPlaintextKeyWL = Stub.getDefaultImpl().injectPlaintextKeyWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectPlaintextKeyWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectCiphertextKeyWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(102, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectCiphertextKeyWL = Stub.getDefaultImpl().injectCiphertextKeyWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectCiphertextKeyWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectKeyDukptWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(103, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectKeyDukptWL = Stub.getDefaultImpl().injectKeyDukptWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectKeyDukptWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getKeyCheckValueWL(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(104, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int keyCheckValueWL = Stub.getDefaultImpl().getKeyCheckValueWL(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return keyCheckValueWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(105, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataEncryptEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(106, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataEncryptEx = Stub.getDefaultImpl().dataEncryptEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataEncryptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int dataDecryptEx(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dataDecryptEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iDataDecryptEx = Stub.getDefaultImpl().dataDecryptEx(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iDataDecryptEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int queryKeyMappingRecordListWL(List<Bundle> list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_queryKeyMappingRecordListWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryKeyMappingRecordListWL = Stub.getDefaultImpl().queryKeyMappingRecordListWL(list);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryKeyMappingRecordListWL;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readTypedList(list, Bundle.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int genTR34CredTokenWL(Bundle bundle, byte[] dataOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_genTR34CredTokenWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenTR34CredTokenWL = Stub.getDefaultImpl().genTR34CredTokenWL(bundle, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenTR34CredTokenWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int genTR34RandomTokenWL(int randomSize, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(randomSize);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_genTR34RandomTokenWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenTR34RandomTokenWL = Stub.getDefaultImpl().genTR34RandomTokenWL(randomSize, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iGenTR34RandomTokenWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int validateTR34CredTokenWL(byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_validateTR34CredTokenWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iValidateTR34CredTokenWL = Stub.getDefaultImpl().validateTR34CredTokenWL(dataIn);
                        _reply.recycle();
                        _data.recycle();
                        return iValidateTR34CredTokenWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int validateTR34KeyTokenWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_validateTR34KeyTokenWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iValidateTR34KeyTokenWL = Stub.getDefaultImpl().validateTR34KeyTokenWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iValidateTR34KeyTokenWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int validateTR34UNBTokenWL(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_validateTR34UNBTokenWL, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iValidateTR34UNBTokenWL = Stub.getDefaultImpl().validateTR34UNBTokenWL(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iValidateTR34UNBTokenWL;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int queryKeyMappingRecordList(Bundle bundle, List<Bundle> list) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_queryKeyMappingRecordList, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryKeyMappingRecordList = Stub.getDefaultImpl().queryKeyMappingRecordList(bundle, list);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryKeyMappingRecordList;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readTypedList(list, Bundle.CREATOR);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectTR31Key(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectTR31Key, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectTR31Key = Stub.getDefaultImpl().injectTR31Key(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectTR31Key;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExchangeKeyEccEx(Bundle bundle, byte[] pubKeyB) throws RemoteException {
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
                    if (pubKeyB == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(pubKeyB.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExchangeKeyEccEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExchangeKeyEccEx = Stub.getDefaultImpl().hsmExchangeKeyEccEx(bundle, pubKeyB);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExchangeKeyEccEx;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(pubKeyB);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int saveCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_saveCiphertextKeyUnderRSAEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSaveCiphertextKeyUnderRSAEx = Stub.getDefaultImpl().saveCiphertextKeyUnderRSAEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSaveCiphertextKeyUnderRSAEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectCiphertextKeyUnderRSAEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectCiphertextKeyUnderRSAEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectCiphertextKeyUnderRSAEx = Stub.getDefaultImpl().injectCiphertextKeyUnderRSAEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectCiphertextKeyUnderRSAEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int convertKeyType(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_convertKeyType, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iConvertKeyType = Stub.getDefaultImpl().convertKeyType(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iConvertKeyType;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int hsmExchangeKeyEccKdf(Bundle keyInfo, Bundle kdfInfo, Bundle outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyInfo != null) {
                        _data.writeInt(1);
                        keyInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (kdfInfo != null) {
                        _data.writeInt(1);
                        kdfInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_hsmExchangeKeyEccKdf, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iHsmExchangeKeyEccKdf = Stub.getDefaultImpl().hsmExchangeKeyEccKdf(keyInfo, kdfInfo, outInfo);
                        _reply.recycle();
                        _data.recycle();
                        return iHsmExchangeKeyEccKdf;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        outInfo.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int generateSM2KeypairEx(Bundle bundle, Bundle pubKey) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateSM2KeypairEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iGenerateSM2KeypairEx = Stub.getDefaultImpl().generateSM2KeypairEx(bundle, pubKey);
                        _reply.recycle();
                        _data.recycle();
                        return iGenerateSM2KeypairEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectSM2KeyEx(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectSM2KeyEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInjectSM2KeyEx = Stub.getDefaultImpl().injectSM2KeyEx(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInjectSM2KeyEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_generateEccKeypair, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectEccPubKey(int pukIndex, int keySize, byte[] pubKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pukIndex);
                    _data.writeInt(keySize);
                    _data.writeByteArray(pubKey);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_injectEccPubKey, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int injectEccPvtKey(int pvkIndex, int keySize, byte[] pvkKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pvkIndex);
                    _data.writeInt(keySize);
                    _data.writeByteArray(pvkKey);
                    boolean _status = this.mRemote.transact(128, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int getEccPubKey(int keyIndex, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    boolean _status = this.mRemote.transact(129, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(130, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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
                    boolean _status = this.mRemote.transact(131, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
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

            @Override // com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
            public int queryKeyMappingRecord(Bundle bundle, Bundle keyInfo) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_queryKeyMappingRecord, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iQueryKeyMappingRecord = Stub.getDefaultImpl().queryKeyMappingRecord(bundle, keyInfo);
                        _reply.recycle();
                        _data.recycle();
                        return iQueryKeyMappingRecord;
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
        }

        public static boolean setDefaultImpl(SecurityOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static SecurityOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
