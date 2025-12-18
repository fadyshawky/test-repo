package com.sunmi.pay.hardware.aidlv2.readcard;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.bean.ApduRecvV2;
import com.sunmi.pay.hardware.aidlv2.bean.ApduSendV2;
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/ReadCardOptV2.class */
public interface ReadCardOptV2 extends IInterface {
    void checkCard(int i, CheckCardCallbackV2 checkCardCallbackV2, int i2) throws RemoteException;

    void cancelCheckCard() throws RemoteException;

    int apduCommand(int i, ApduSendV2 apduSendV2, ApduRecvV2 apduRecvV2) throws RemoteException;

    int smartCardExchange(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int transmitApdu(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int cardOff(int i) throws RemoteException;

    int getCardExistStatus(int i) throws RemoteException;

    int mifareAuth(int i, int i2, byte[] bArr) throws RemoteException;

    int mifareReadBlock(int i, byte[] bArr) throws RemoteException;

    int mifareWriteBlock(int i, byte[] bArr) throws RemoteException;

    int mifareIncValue(int i, byte[] bArr) throws RemoteException;

    int mifareDecValue(int i, byte[] bArr) throws RemoteException;

    int mifareUltralightCAuth(byte[] bArr) throws RemoteException;

    int mifareUltralightCReadData(int i, byte[] bArr) throws RemoteException;

    int mifareUltralightCWriteData(int i, byte[] bArr) throws RemoteException;

    int smartCardExChangePASS(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int smartCardExChangePASSNoLength(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int mifarePlusReadBlock(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int mifarePlusWriteBlock(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int mifarePlusChangeBlockKey(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int sleAuthKey(byte[] bArr) throws RemoteException;

    int sleChangeKey(byte[] bArr) throws RemoteException;

    int sleReadData(int i, int i2, byte[] bArr) throws RemoteException;

    int sleWriteData(int i, byte[] bArr) throws RemoteException;

    int sleGetRemainAuthCount() throws RemoteException;

    int sleWriteProtectionMemory(int i, int i2) throws RemoteException;

    int sleReadMemoryProtectionStatus(int i, int i2, byte[] bArr) throws RemoteException;

    int at24cReadData(int i, int i2, byte[] bArr) throws RemoteException;

    int at24cWriteData(int i, byte[] bArr) throws RemoteException;

    int at88scAuthKey(byte[] bArr, int i, int i2) throws RemoteException;

    int at88scChangeKey(byte[] bArr, int i, int i2) throws RemoteException;

    int at88scReadData(int i, int i2, int i3, byte[] bArr) throws RemoteException;

    int at88scWriteData(int i, int i2, byte[] bArr) throws RemoteException;

    int at88scGetRemainAuthCount(int i, int i2) throws RemoteException;

    int transmitApduEx(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int ctx512ReadBlock(int i, byte[] bArr) throws RemoteException;

    int ctx512WriteBlock(int i, byte[] bArr) throws RemoteException;

    int ctx512UpdateBlock(int i, byte[] bArr) throws RemoteException;

    int ctx512GetSignature(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int ctx512MultiReadBlock(int i, byte[] bArr) throws RemoteException;

    int mifareIncValueDx(int i, byte[] bArr) throws RemoteException;

    int mifareDecValueDx(int i, byte[] bArr) throws RemoteException;

    int mifareTransfer(int i) throws RemoteException;

    int mifareRestore(int i) throws RemoteException;

    void checkCardEx(int i, int i2, int i3, CheckCardCallbackV2 checkCardCallbackV2, int i4) throws RemoteException;

    int transmitApduExx(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int transmitMultiApdus(int i, int i2, List<String> list, List<String> list2) throws RemoteException;

    int checkCardEnc(Bundle bundle, CheckCardCallbackV2 checkCardCallbackV2, int i) throws RemoteException;

    int smartCardIoControl(int i, int i2, byte[] bArr, byte[] bArr2) throws RemoteException;

    int sriGetUid(Bundle bundle) throws RemoteException;

    int sriReadBlock32(int i, byte[] bArr) throws RemoteException;

    int sriWriteBlock32(int i, byte[] bArr) throws RemoteException;

    int sriProtectBlock(byte b) throws RemoteException;

    int sriGetBlockProtection(byte[] bArr) throws RemoteException;

    void checkCardForToss(Bundle bundle, CheckCardCallbackV2 checkCardCallbackV2, int i) throws RemoteException;

    int setNfcParam(Bundle bundle) throws RemoteException;

    int transmitApduExtended(Bundle bundle, Bundle bundle2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/ReadCardOptV2$Default.class */
    public static class Default implements ReadCardOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public void checkCard(int cardType, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public void cancelCheckCard() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int apduCommand(int cardType, ApduSendV2 send, ApduRecvV2 recv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int smartCardExchange(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int transmitApdu(int cardType, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int cardOff(int cardType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int getCardExistStatus(int cardType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareAuth(int keyType, int block, byte[] key) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareReadBlock(int block, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareWriteBlock(int block, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareIncValue(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareDecValue(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareUltralightCAuth(byte[] authKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareUltralightCReadData(int block, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareUltralightCWriteData(int block, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int smartCardExChangePASS(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int smartCardExChangePASSNoLength(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifarePlusReadBlock(int block, byte[] key, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifarePlusWriteBlock(int block, byte[] key, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifarePlusChangeBlockKey(int block, byte[] oldKey, byte[] newKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleAuthKey(byte[] key) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleChangeKey(byte[] newKey) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleReadData(int startAddress, int length, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleWriteData(int startAddress, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleGetRemainAuthCount() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleWriteProtectionMemory(int startAddress, int length) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sleReadMemoryProtectionStatus(int startAddress, int length, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at24cReadData(int startAddress, int length, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at24cWriteData(int startAddress, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at88scAuthKey(byte[] key, int rwFlag, int zoneNo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at88scChangeKey(byte[] newKey, int rwFlag, int zoneNo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at88scReadData(int startAddress, int length, int zoneFlag, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at88scWriteData(int startAddress, int zoneFlag, byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int at88scGetRemainAuthCount(int rwFlag, int zoneNo) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int transmitApduEx(int cardType, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int ctx512ReadBlock(int block, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int ctx512WriteBlock(int block, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int ctx512UpdateBlock(int block, byte[] data) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int ctx512GetSignature(int block, byte[] random, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int ctx512MultiReadBlock(int startBlock, byte[] outData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareIncValueDx(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareDecValueDx(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareTransfer(int destBlock) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int mifareRestore(int srcBlock) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public void checkCardEx(int cardType, int ctrCode, int stopOnError, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int transmitApduExx(int cardType, int ctrCode, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int transmitMultiApdus(int cardType, int ctrCode, List<String> sendList, List<String> recvList) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int checkCardEnc(Bundle bundle, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int smartCardIoControl(int cardType, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sriGetUid(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sriReadBlock32(int address, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sriWriteBlock32(int address, byte[] dataIn) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sriProtectBlock(byte nLockReg) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int sriGetBlockProtection(byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public void checkCardForToss(Bundle bundle, CheckCardCallbackV2 callback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int setNfcParam(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
        public int transmitApduExtended(Bundle paramIn, Bundle paramOut) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/ReadCardOptV2$Stub.class */
    public static abstract class Stub extends Binder implements ReadCardOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2";
        static final int TRANSACTION_checkCard = 1;
        static final int TRANSACTION_cancelCheckCard = 2;
        static final int TRANSACTION_apduCommand = 3;
        static final int TRANSACTION_smartCardExchange = 4;
        static final int TRANSACTION_transmitApdu = 5;
        static final int TRANSACTION_cardOff = 6;
        static final int TRANSACTION_getCardExistStatus = 7;
        static final int TRANSACTION_mifareAuth = 8;
        static final int TRANSACTION_mifareReadBlock = 9;
        static final int TRANSACTION_mifareWriteBlock = 10;
        static final int TRANSACTION_mifareIncValue = 11;
        static final int TRANSACTION_mifareDecValue = 12;
        static final int TRANSACTION_mifareUltralightCAuth = 13;
        static final int TRANSACTION_mifareUltralightCReadData = 14;
        static final int TRANSACTION_mifareUltralightCWriteData = 15;
        static final int TRANSACTION_smartCardExChangePASS = 16;
        static final int TRANSACTION_smartCardExChangePASSNoLength = 17;
        static final int TRANSACTION_mifarePlusReadBlock = 18;
        static final int TRANSACTION_mifarePlusWriteBlock = 19;
        static final int TRANSACTION_mifarePlusChangeBlockKey = 20;
        static final int TRANSACTION_sleAuthKey = 21;
        static final int TRANSACTION_sleChangeKey = 22;
        static final int TRANSACTION_sleReadData = 23;
        static final int TRANSACTION_sleWriteData = 24;
        static final int TRANSACTION_sleGetRemainAuthCount = 25;
        static final int TRANSACTION_sleWriteProtectionMemory = 26;
        static final int TRANSACTION_sleReadMemoryProtectionStatus = 27;
        static final int TRANSACTION_at24cReadData = 28;
        static final int TRANSACTION_at24cWriteData = 29;
        static final int TRANSACTION_at88scAuthKey = 30;
        static final int TRANSACTION_at88scChangeKey = 31;
        static final int TRANSACTION_at88scReadData = 32;
        static final int TRANSACTION_at88scWriteData = 33;
        static final int TRANSACTION_at88scGetRemainAuthCount = 34;
        static final int TRANSACTION_transmitApduEx = 35;
        static final int TRANSACTION_ctx512ReadBlock = 36;
        static final int TRANSACTION_ctx512WriteBlock = 37;
        static final int TRANSACTION_ctx512UpdateBlock = 38;
        static final int TRANSACTION_ctx512GetSignature = 39;
        static final int TRANSACTION_ctx512MultiReadBlock = 40;
        static final int TRANSACTION_mifareIncValueDx = 41;
        static final int TRANSACTION_mifareDecValueDx = 42;
        static final int TRANSACTION_mifareTransfer = 43;
        static final int TRANSACTION_mifareRestore = 44;
        static final int TRANSACTION_checkCardEx = 45;
        static final int TRANSACTION_transmitApduExx = 46;
        static final int TRANSACTION_transmitMultiApdus = 47;
        static final int TRANSACTION_checkCardEnc = 48;
        static final int TRANSACTION_smartCardIoControl = 49;
        static final int TRANSACTION_sriGetUid = 50;
        static final int TRANSACTION_sriReadBlock32 = 51;
        static final int TRANSACTION_sriWriteBlock32 = 52;
        static final int TRANSACTION_sriProtectBlock = 53;
        static final int TRANSACTION_sriGetBlockProtection = 54;
        static final int TRANSACTION_checkCardForToss = 55;
        static final int TRANSACTION_setNfcParam = 56;
        static final int TRANSACTION_transmitApduExtended = 57;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ReadCardOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ReadCardOptV2)) {
                return (ReadCardOptV2) iin;
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
            byte[] _arg04;
            byte[] _arg1;
            byte[] _arg3;
            Bundle _arg05;
            byte[] _arg32;
            byte[] _arg12;
            byte[] _arg2;
            byte[] _arg13;
            byte[] _arg22;
            byte[] _arg23;
            byte[] _arg24;
            byte[] _arg25;
            ApduSendV2 _arg14;
            ApduRecvV2 _arg26;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    CheckCardCallbackV2 _arg15 = CheckCardCallbackV2.Stub.asInterface(data.readStrongBinder());
                    int _arg27 = data.readInt();
                    checkCard(_arg06, _arg15, _arg27);
                    reply.writeNoException();
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    cancelCheckCard();
                    reply.writeNoException();
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    if (0 != data.readInt()) {
                        _arg14 = ApduSendV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    if (0 != data.readInt()) {
                        _arg26 = ApduRecvV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg26 = null;
                    }
                    int _result = apduCommand(_arg07, _arg14, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    if (_arg26 != null) {
                        reply.writeInt(1);
                        _arg26.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    byte[] _arg16 = data.createByteArray();
                    byte[] _arg28 = data.createByteArray();
                    int _result2 = smartCardExchange(_arg08, _arg16, _arg28);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeByteArray(_arg16);
                    reply.writeByteArray(_arg28);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    byte[] _arg17 = data.createByteArray();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg25 = null;
                    } else {
                        _arg25 = new byte[_arg2_length];
                    }
                    int _result3 = transmitApdu(_arg09, _arg17, _arg25);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg25);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    int _result4 = cardOff(_arg010);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    int _result5 = getCardExistStatus(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _arg18 = data.readInt();
                    byte[] _arg29 = data.createByteArray();
                    int _result6 = mifareAuth(_arg012, _arg18, _arg29);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    reply.writeByteArray(_arg29);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    byte[] _arg19 = data.createByteArray();
                    int _result7 = mifareReadBlock(_arg013, _arg19);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    reply.writeByteArray(_arg19);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    int _result8 = mifareWriteBlock(_arg014, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    int _result9 = mifareIncValue(_arg015, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg016 = data.readInt();
                    int _result10 = mifareDecValue(_arg016, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg017 = data.createByteArray();
                    int _result11 = mifareUltralightCAuth(_arg017);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg018 = data.readInt();
                    byte[] _arg110 = data.createByteArray();
                    int _result12 = mifareUltralightCReadData(_arg018, _arg110);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    reply.writeByteArray(_arg110);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg019 = data.readInt();
                    int _result13 = mifareUltralightCWriteData(_arg019, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case TRANSACTION_smartCardExChangePASS /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg020 = data.readInt();
                    byte[] _arg111 = data.createByteArray();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg24 = null;
                    } else {
                        _arg24 = new byte[_arg2_length2];
                    }
                    int _result14 = smartCardExChangePASS(_arg020, _arg111, _arg24);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    reply.writeByteArray(_arg24);
                    break;
                case TRANSACTION_smartCardExChangePASSNoLength /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg021 = data.readInt();
                    byte[] _arg112 = data.createByteArray();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length3];
                    }
                    int _result15 = smartCardExChangePASSNoLength(_arg021, _arg112, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    reply.writeByteArray(_arg23);
                    break;
                case TRANSACTION_mifarePlusReadBlock /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg022 = data.readInt();
                    byte[] _arg113 = data.createByteArray();
                    byte[] _arg210 = data.createByteArray();
                    int _result16 = mifarePlusReadBlock(_arg022, _arg113, _arg210);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    reply.writeByteArray(_arg210);
                    break;
                case TRANSACTION_mifarePlusWriteBlock /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg023 = data.readInt();
                    byte[] _arg114 = data.createByteArray();
                    byte[] _arg211 = data.createByteArray();
                    int _result17 = mifarePlusWriteBlock(_arg023, _arg114, _arg211);
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case TRANSACTION_mifarePlusChangeBlockKey /* 20 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg024 = data.readInt();
                    byte[] _arg115 = data.createByteArray();
                    byte[] _arg212 = data.createByteArray();
                    int _result18 = mifarePlusChangeBlockKey(_arg024, _arg115, _arg212);
                    reply.writeNoException();
                    reply.writeInt(_result18);
                    break;
                case TRANSACTION_sleAuthKey /* 21 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg025 = data.createByteArray();
                    int _result19 = sleAuthKey(_arg025);
                    reply.writeNoException();
                    reply.writeInt(_result19);
                    break;
                case TRANSACTION_sleChangeKey /* 22 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg026 = data.createByteArray();
                    int _result20 = sleChangeKey(_arg026);
                    reply.writeNoException();
                    reply.writeInt(_result20);
                    break;
                case TRANSACTION_sleReadData /* 23 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg027 = data.readInt();
                    int _arg116 = data.readInt();
                    byte[] _arg213 = data.createByteArray();
                    int _result21 = sleReadData(_arg027, _arg116, _arg213);
                    reply.writeNoException();
                    reply.writeInt(_result21);
                    reply.writeByteArray(_arg213);
                    break;
                case TRANSACTION_sleWriteData /* 24 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg028 = data.readInt();
                    int _result22 = sleWriteData(_arg028, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result22);
                    break;
                case TRANSACTION_sleGetRemainAuthCount /* 25 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result23 = sleGetRemainAuthCount();
                    reply.writeNoException();
                    reply.writeInt(_result23);
                    break;
                case TRANSACTION_sleWriteProtectionMemory /* 26 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg029 = data.readInt();
                    int _result24 = sleWriteProtectionMemory(_arg029, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result24);
                    break;
                case TRANSACTION_sleReadMemoryProtectionStatus /* 27 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg030 = data.readInt();
                    int _arg117 = data.readInt();
                    byte[] _arg214 = data.createByteArray();
                    int _result25 = sleReadMemoryProtectionStatus(_arg030, _arg117, _arg214);
                    reply.writeNoException();
                    reply.writeInt(_result25);
                    reply.writeByteArray(_arg214);
                    break;
                case TRANSACTION_at24cReadData /* 28 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg031 = data.readInt();
                    int _arg118 = data.readInt();
                    byte[] _arg215 = data.createByteArray();
                    int _result26 = at24cReadData(_arg031, _arg118, _arg215);
                    reply.writeNoException();
                    reply.writeInt(_result26);
                    reply.writeByteArray(_arg215);
                    break;
                case TRANSACTION_at24cWriteData /* 29 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg032 = data.readInt();
                    int _result27 = at24cWriteData(_arg032, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result27);
                    break;
                case TRANSACTION_at88scAuthKey /* 30 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg033 = data.createByteArray();
                    int _arg119 = data.readInt();
                    int _arg216 = data.readInt();
                    int _result28 = at88scAuthKey(_arg033, _arg119, _arg216);
                    reply.writeNoException();
                    reply.writeInt(_result28);
                    break;
                case TRANSACTION_at88scChangeKey /* 31 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg034 = data.createByteArray();
                    int _arg120 = data.readInt();
                    int _arg217 = data.readInt();
                    int _result29 = at88scChangeKey(_arg034, _arg120, _arg217);
                    reply.writeNoException();
                    reply.writeInt(_result29);
                    break;
                case TRANSACTION_at88scReadData /* 32 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg035 = data.readInt();
                    int _arg121 = data.readInt();
                    int _arg218 = data.readInt();
                    byte[] _arg33 = data.createByteArray();
                    int _result30 = at88scReadData(_arg035, _arg121, _arg218, _arg33);
                    reply.writeNoException();
                    reply.writeInt(_result30);
                    reply.writeByteArray(_arg33);
                    break;
                case TRANSACTION_at88scWriteData /* 33 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg036 = data.readInt();
                    int _arg122 = data.readInt();
                    byte[] _arg219 = data.createByteArray();
                    int _result31 = at88scWriteData(_arg036, _arg122, _arg219);
                    reply.writeNoException();
                    reply.writeInt(_result31);
                    break;
                case TRANSACTION_at88scGetRemainAuthCount /* 34 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg037 = data.readInt();
                    int _result32 = at88scGetRemainAuthCount(_arg037, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result32);
                    break;
                case TRANSACTION_transmitApduEx /* 35 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg038 = data.readInt();
                    byte[] _arg123 = data.createByteArray();
                    int _arg2_length4 = data.readInt();
                    if (_arg2_length4 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length4];
                    }
                    int _result33 = transmitApduEx(_arg038, _arg123, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result33);
                    reply.writeByteArray(_arg22);
                    break;
                case TRANSACTION_ctx512ReadBlock /* 36 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg039 = data.readInt();
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg13 = null;
                    } else {
                        _arg13 = new byte[_arg1_length];
                    }
                    int _result34 = ctx512ReadBlock(_arg039, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result34);
                    reply.writeByteArray(_arg13);
                    break;
                case TRANSACTION_ctx512WriteBlock /* 37 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg040 = data.readInt();
                    int _result35 = ctx512WriteBlock(_arg040, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result35);
                    break;
                case TRANSACTION_ctx512UpdateBlock /* 38 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg041 = data.readInt();
                    int _result36 = ctx512UpdateBlock(_arg041, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result36);
                    break;
                case TRANSACTION_ctx512GetSignature /* 39 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg042 = data.readInt();
                    byte[] _arg124 = data.createByteArray();
                    int _arg2_length5 = data.readInt();
                    if (_arg2_length5 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length5];
                    }
                    int _result37 = ctx512GetSignature(_arg042, _arg124, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result37);
                    reply.writeByteArray(_arg2);
                    break;
                case TRANSACTION_ctx512MultiReadBlock /* 40 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg043 = data.readInt();
                    int _arg1_length2 = data.readInt();
                    if (_arg1_length2 < 0) {
                        _arg12 = null;
                    } else {
                        _arg12 = new byte[_arg1_length2];
                    }
                    int _result38 = ctx512MultiReadBlock(_arg043, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result38);
                    reply.writeByteArray(_arg12);
                    break;
                case TRANSACTION_mifareIncValueDx /* 41 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg044 = data.readInt();
                    int _result39 = mifareIncValueDx(_arg044, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result39);
                    break;
                case TRANSACTION_mifareDecValueDx /* 42 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg045 = data.readInt();
                    int _result40 = mifareDecValueDx(_arg045, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result40);
                    break;
                case TRANSACTION_mifareTransfer /* 43 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg046 = data.readInt();
                    int _result41 = mifareTransfer(_arg046);
                    reply.writeNoException();
                    reply.writeInt(_result41);
                    break;
                case TRANSACTION_mifareRestore /* 44 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg047 = data.readInt();
                    int _result42 = mifareRestore(_arg047);
                    reply.writeNoException();
                    reply.writeInt(_result42);
                    break;
                case TRANSACTION_checkCardEx /* 45 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg048 = data.readInt();
                    int _arg125 = data.readInt();
                    int _arg220 = data.readInt();
                    CheckCardCallbackV2 _arg34 = CheckCardCallbackV2.Stub.asInterface(data.readStrongBinder());
                    int _arg4 = data.readInt();
                    checkCardEx(_arg048, _arg125, _arg220, _arg34, _arg4);
                    reply.writeNoException();
                    break;
                case TRANSACTION_transmitApduExx /* 46 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg049 = data.readInt();
                    int _arg126 = data.readInt();
                    byte[] _arg221 = data.createByteArray();
                    int _arg3_length = data.readInt();
                    if (_arg3_length < 0) {
                        _arg32 = null;
                    } else {
                        _arg32 = new byte[_arg3_length];
                    }
                    int _result43 = transmitApduExx(_arg049, _arg126, _arg221, _arg32);
                    reply.writeNoException();
                    reply.writeInt(_result43);
                    reply.writeByteArray(_arg32);
                    break;
                case TRANSACTION_transmitMultiApdus /* 47 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg050 = data.readInt();
                    int _arg127 = data.readInt();
                    List<String> _arg222 = data.createStringArrayList();
                    List<String> _arg35 = new ArrayList<>();
                    int _result44 = transmitMultiApdus(_arg050, _arg127, _arg222, _arg35);
                    reply.writeNoException();
                    reply.writeInt(_result44);
                    reply.writeStringList(_arg35);
                    break;
                case TRANSACTION_checkCardEnc /* 48 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    CheckCardCallbackV2 _arg128 = CheckCardCallbackV2.Stub.asInterface(data.readStrongBinder());
                    int _arg223 = data.readInt();
                    int _result45 = checkCardEnc(_arg05, _arg128, _arg223);
                    reply.writeNoException();
                    reply.writeInt(_result45);
                    break;
                case TRANSACTION_smartCardIoControl /* 49 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg051 = data.readInt();
                    int _arg129 = data.readInt();
                    byte[] _arg224 = data.createByteArray();
                    int _arg3_length2 = data.readInt();
                    if (_arg3_length2 < 0) {
                        _arg3 = null;
                    } else {
                        _arg3 = new byte[_arg3_length2];
                    }
                    int _result46 = smartCardIoControl(_arg051, _arg129, _arg224, _arg3);
                    reply.writeNoException();
                    reply.writeInt(_result46);
                    reply.writeByteArray(_arg3);
                    break;
                case TRANSACTION_sriGetUid /* 50 */:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg052 = new Bundle();
                    int _result47 = sriGetUid(_arg052);
                    reply.writeNoException();
                    reply.writeInt(_result47);
                    if (_arg052 != null) {
                        reply.writeInt(1);
                        _arg052.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case TRANSACTION_sriReadBlock32 /* 51 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg053 = data.readInt();
                    int _arg1_length3 = data.readInt();
                    if (_arg1_length3 < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length3];
                    }
                    int _result48 = sriReadBlock32(_arg053, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result48);
                    reply.writeByteArray(_arg1);
                    break;
                case TRANSACTION_sriWriteBlock32 /* 52 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg054 = data.readInt();
                    int _result49 = sriWriteBlock32(_arg054, data.createByteArray());
                    reply.writeNoException();
                    reply.writeInt(_result49);
                    break;
                case TRANSACTION_sriProtectBlock /* 53 */:
                    data.enforceInterface(DESCRIPTOR);
                    byte _arg055 = data.readByte();
                    int _result50 = sriProtectBlock(_arg055);
                    reply.writeNoException();
                    reply.writeInt(_result50);
                    break;
                case TRANSACTION_sriGetBlockProtection /* 54 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0_length = data.readInt();
                    if (_arg0_length < 0) {
                        _arg04 = null;
                    } else {
                        _arg04 = new byte[_arg0_length];
                    }
                    int _result51 = sriGetBlockProtection(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result51);
                    reply.writeByteArray(_arg04);
                    break;
                case TRANSACTION_checkCardForToss /* 55 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    CheckCardCallbackV2 _arg130 = CheckCardCallbackV2.Stub.asInterface(data.readStrongBinder());
                    int _arg225 = data.readInt();
                    checkCardForToss(_arg03, _arg130, _arg225);
                    reply.writeNoException();
                    break;
                case TRANSACTION_setNfcParam /* 56 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _result52 = setNfcParam(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result52);
                    break;
                case TRANSACTION_transmitApduExtended /* 57 */:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Bundle _arg131 = new Bundle();
                    int _result53 = transmitApduExtended(_arg0, _arg131);
                    reply.writeNoException();
                    reply.writeInt(_result53);
                    if (_arg131 != null) {
                        reply.writeInt(1);
                        _arg131.writeToParcel(reply, 1);
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

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/ReadCardOptV2$Stub$Proxy.class */
        private static class Proxy implements ReadCardOptV2 {
            private IBinder mRemote;
            public static ReadCardOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public void checkCard(int cardType, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeStrongBinder(checkCardCallback != null ? checkCardCallback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().checkCard(cardType, checkCardCallback, timeout);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public void cancelCheckCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().cancelCheckCard();
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int apduCommand(int cardType, ApduSendV2 send, ApduRecvV2 recv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    if (send != null) {
                        _data.writeInt(1);
                        send.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (recv != null) {
                        _data.writeInt(1);
                        recv.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iApduCommand = Stub.getDefaultImpl().apduCommand(cardType, send, recv);
                        _reply.recycle();
                        _data.recycle();
                        return iApduCommand;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        recv.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int smartCardExchange(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(apduSend);
                    _data.writeByteArray(apduRecv);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSmartCardExchange = Stub.getDefaultImpl().smartCardExchange(cardType, apduSend, apduRecv);
                        _reply.recycle();
                        _data.recycle();
                        return iSmartCardExchange;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(apduSend);
                    _reply.readByteArray(apduRecv);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int transmitApdu(int cardType, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(sendBuff);
                    if (recvBuff == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(recvBuff.length);
                    }
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransmitApdu = Stub.getDefaultImpl().transmitApdu(cardType, sendBuff, recvBuff);
                        _reply.recycle();
                        _data.recycle();
                        return iTransmitApdu;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(recvBuff);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int cardOff(int cardType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCardOff = Stub.getDefaultImpl().cardOff(cardType);
                        _reply.recycle();
                        _data.recycle();
                        return iCardOff;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int getCardExistStatus(int cardType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int cardExistStatus = Stub.getDefaultImpl().getCardExistStatus(cardType);
                        _reply.recycle();
                        _data.recycle();
                        return cardExistStatus;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareAuth(int keyType, int block, byte[] key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeInt(block);
                    _data.writeByteArray(key);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareAuth = Stub.getDefaultImpl().mifareAuth(keyType, block, key);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareAuth;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(key);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareReadBlock(int block, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareReadBlock = Stub.getDefaultImpl().mifareReadBlock(block, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareReadBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareWriteBlock(int block, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareWriteBlock = Stub.getDefaultImpl().mifareWriteBlock(block, data);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareWriteBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareIncValue(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareIncValue = Stub.getDefaultImpl().mifareIncValue(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareIncValue;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareDecValue(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareDecValue = Stub.getDefaultImpl().mifareDecValue(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareDecValue;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareUltralightCAuth(byte[] authKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(authKey);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareUltralightCAuth = Stub.getDefaultImpl().mifareUltralightCAuth(authKey);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareUltralightCAuth;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareUltralightCReadData(int block, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareUltralightCReadData = Stub.getDefaultImpl().mifareUltralightCReadData(block, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareUltralightCReadData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareUltralightCWriteData(int block, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareUltralightCWriteData = Stub.getDefaultImpl().mifareUltralightCWriteData(block, data);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareUltralightCWriteData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int smartCardExChangePASS(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(apduSend);
                    if (apduRecv == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(apduRecv.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_smartCardExChangePASS, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSmartCardExChangePASS = Stub.getDefaultImpl().smartCardExChangePASS(cardType, apduSend, apduRecv);
                        _reply.recycle();
                        _data.recycle();
                        return iSmartCardExChangePASS;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(apduRecv);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int smartCardExChangePASSNoLength(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(apduSend);
                    if (apduRecv == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(apduRecv.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_smartCardExChangePASSNoLength, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSmartCardExChangePASSNoLength = Stub.getDefaultImpl().smartCardExChangePASSNoLength(cardType, apduSend, apduRecv);
                        _reply.recycle();
                        _data.recycle();
                        return iSmartCardExChangePASSNoLength;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(apduRecv);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifarePlusReadBlock(int block, byte[] key, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(key);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifarePlusReadBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifarePlusReadBlock = Stub.getDefaultImpl().mifarePlusReadBlock(block, key, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iMifarePlusReadBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifarePlusWriteBlock(int block, byte[] key, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(key);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifarePlusWriteBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifarePlusWriteBlock = Stub.getDefaultImpl().mifarePlusWriteBlock(block, key, data);
                        _reply.recycle();
                        _data.recycle();
                        return iMifarePlusWriteBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifarePlusChangeBlockKey(int block, byte[] oldKey, byte[] newKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(oldKey);
                    _data.writeByteArray(newKey);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifarePlusChangeBlockKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifarePlusChangeBlockKey = Stub.getDefaultImpl().mifarePlusChangeBlockKey(block, oldKey, newKey);
                        _reply.recycle();
                        _data.recycle();
                        return iMifarePlusChangeBlockKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleAuthKey(byte[] key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(key);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleAuthKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleAuthKey = Stub.getDefaultImpl().sleAuthKey(key);
                        _reply.recycle();
                        _data.recycle();
                        return iSleAuthKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleChangeKey(byte[] newKey) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(newKey);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleChangeKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleChangeKey = Stub.getDefaultImpl().sleChangeKey(newKey);
                        _reply.recycle();
                        _data.recycle();
                        return iSleChangeKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleReadData(int startAddress, int length, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(length);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleReadData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleReadData = Stub.getDefaultImpl().sleReadData(startAddress, length, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iSleReadData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleWriteData(int startAddress, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleWriteData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleWriteData = Stub.getDefaultImpl().sleWriteData(startAddress, data);
                        _reply.recycle();
                        _data.recycle();
                        return iSleWriteData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleGetRemainAuthCount() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleGetRemainAuthCount, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleGetRemainAuthCount = Stub.getDefaultImpl().sleGetRemainAuthCount();
                        _reply.recycle();
                        _data.recycle();
                        return iSleGetRemainAuthCount;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleWriteProtectionMemory(int startAddress, int length) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(length);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleWriteProtectionMemory, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleWriteProtectionMemory = Stub.getDefaultImpl().sleWriteProtectionMemory(startAddress, length);
                        _reply.recycle();
                        _data.recycle();
                        return iSleWriteProtectionMemory;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sleReadMemoryProtectionStatus(int startAddress, int length, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(length);
                    _data.writeByteArray(dataOut);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sleReadMemoryProtectionStatus, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSleReadMemoryProtectionStatus = Stub.getDefaultImpl().sleReadMemoryProtectionStatus(startAddress, length, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSleReadMemoryProtectionStatus;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at24cReadData(int startAddress, int length, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(length);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at24cReadData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt24cReadData = Stub.getDefaultImpl().at24cReadData(startAddress, length, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iAt24cReadData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at24cWriteData(int startAddress, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at24cWriteData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt24cWriteData = Stub.getDefaultImpl().at24cWriteData(startAddress, data);
                        _reply.recycle();
                        _data.recycle();
                        return iAt24cWriteData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at88scAuthKey(byte[] key, int rwFlag, int zoneNo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(key);
                    _data.writeInt(rwFlag);
                    _data.writeInt(zoneNo);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at88scAuthKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt88scAuthKey = Stub.getDefaultImpl().at88scAuthKey(key, rwFlag, zoneNo);
                        _reply.recycle();
                        _data.recycle();
                        return iAt88scAuthKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at88scChangeKey(byte[] newKey, int rwFlag, int zoneNo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(newKey);
                    _data.writeInt(rwFlag);
                    _data.writeInt(zoneNo);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at88scChangeKey, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt88scChangeKey = Stub.getDefaultImpl().at88scChangeKey(newKey, rwFlag, zoneNo);
                        _reply.recycle();
                        _data.recycle();
                        return iAt88scChangeKey;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at88scReadData(int startAddress, int length, int zoneFlag, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(length);
                    _data.writeInt(zoneFlag);
                    _data.writeByteArray(outData);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at88scReadData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt88scReadData = Stub.getDefaultImpl().at88scReadData(startAddress, length, zoneFlag, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iAt88scReadData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at88scWriteData(int startAddress, int zoneFlag, byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startAddress);
                    _data.writeInt(zoneFlag);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at88scWriteData, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt88scWriteData = Stub.getDefaultImpl().at88scWriteData(startAddress, zoneFlag, dataIn);
                        _reply.recycle();
                        _data.recycle();
                        return iAt88scWriteData;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int at88scGetRemainAuthCount(int rwFlag, int zoneNo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rwFlag);
                    _data.writeInt(zoneNo);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_at88scGetRemainAuthCount, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iAt88scGetRemainAuthCount = Stub.getDefaultImpl().at88scGetRemainAuthCount(rwFlag, zoneNo);
                        _reply.recycle();
                        _data.recycle();
                        return iAt88scGetRemainAuthCount;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int transmitApduEx(int cardType, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeByteArray(sendBuff);
                    if (recvBuff == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(recvBuff.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transmitApduEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransmitApduEx = Stub.getDefaultImpl().transmitApduEx(cardType, sendBuff, recvBuff);
                        _reply.recycle();
                        _data.recycle();
                        return iTransmitApduEx;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(recvBuff);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int ctx512ReadBlock(int block, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_ctx512ReadBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCtx512ReadBlock = Stub.getDefaultImpl().ctx512ReadBlock(block, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iCtx512ReadBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int ctx512WriteBlock(int block, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_ctx512WriteBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCtx512WriteBlock = Stub.getDefaultImpl().ctx512WriteBlock(block, data);
                        _reply.recycle();
                        _data.recycle();
                        return iCtx512WriteBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int ctx512UpdateBlock(int block, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_ctx512UpdateBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCtx512UpdateBlock = Stub.getDefaultImpl().ctx512UpdateBlock(block, data);
                        _reply.recycle();
                        _data.recycle();
                        return iCtx512UpdateBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int ctx512GetSignature(int block, byte[] random, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(random);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_ctx512GetSignature, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCtx512GetSignature = Stub.getDefaultImpl().ctx512GetSignature(block, random, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iCtx512GetSignature;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int ctx512MultiReadBlock(int startBlock, byte[] outData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(startBlock);
                    if (outData == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(outData.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_ctx512MultiReadBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCtx512MultiReadBlock = Stub.getDefaultImpl().ctx512MultiReadBlock(startBlock, outData);
                        _reply.recycle();
                        _data.recycle();
                        return iCtx512MultiReadBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareIncValueDx(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifareIncValueDx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareIncValueDx = Stub.getDefaultImpl().mifareIncValueDx(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareIncValueDx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareDecValueDx(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifareDecValueDx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareDecValueDx = Stub.getDefaultImpl().mifareDecValueDx(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareDecValueDx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareTransfer(int destBlock) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(destBlock);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifareTransfer, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareTransfer = Stub.getDefaultImpl().mifareTransfer(destBlock);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareTransfer;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int mifareRestore(int srcBlock) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(srcBlock);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_mifareRestore, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareRestore = Stub.getDefaultImpl().mifareRestore(srcBlock);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareRestore;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public void checkCardEx(int cardType, int ctrCode, int stopOnError, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeInt(ctrCode);
                    _data.writeInt(stopOnError);
                    _data.writeStrongBinder(checkCardCallback != null ? checkCardCallback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_checkCardEx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().checkCardEx(cardType, ctrCode, stopOnError, checkCardCallback, timeout);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int transmitApduExx(int cardType, int ctrCode, byte[] sendBuff, byte[] recvBuff) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeInt(ctrCode);
                    _data.writeByteArray(sendBuff);
                    if (recvBuff == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(recvBuff.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transmitApduExx, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransmitApduExx = Stub.getDefaultImpl().transmitApduExx(cardType, ctrCode, sendBuff, recvBuff);
                        _reply.recycle();
                        _data.recycle();
                        return iTransmitApduExx;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(recvBuff);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int transmitMultiApdus(int cardType, int ctrCode, List<String> sendList, List<String> recvList) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeInt(ctrCode);
                    _data.writeStringList(sendList);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transmitMultiApdus, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransmitMultiApdus = Stub.getDefaultImpl().transmitMultiApdus(cardType, ctrCode, sendList, recvList);
                        _reply.recycle();
                        _data.recycle();
                        return iTransmitMultiApdus;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readStringList(recvList);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int checkCardEnc(Bundle bundle, CheckCardCallbackV2 checkCardCallback, int timeout) throws RemoteException {
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
                    _data.writeStrongBinder(checkCardCallback != null ? checkCardCallback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_checkCardEnc, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iCheckCardEnc = Stub.getDefaultImpl().checkCardEnc(bundle, checkCardCallback, timeout);
                        _reply.recycle();
                        _data.recycle();
                        return iCheckCardEnc;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int smartCardIoControl(int cardType, int ctrCode, byte[] dataIn, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeInt(ctrCode);
                    _data.writeByteArray(dataIn);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_smartCardIoControl, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSmartCardIoControl = Stub.getDefaultImpl().smartCardIoControl(cardType, ctrCode, dataIn, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSmartCardIoControl;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sriGetUid(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sriGetUid, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSriGetUid = Stub.getDefaultImpl().sriGetUid(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iSriGetUid;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sriReadBlock32(int address, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(address);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sriReadBlock32, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSriReadBlock32 = Stub.getDefaultImpl().sriReadBlock32(address, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSriReadBlock32;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sriWriteBlock32(int address, byte[] dataIn) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(address);
                    _data.writeByteArray(dataIn);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sriWriteBlock32, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSriWriteBlock32 = Stub.getDefaultImpl().sriWriteBlock32(address, dataIn);
                        _reply.recycle();
                        _data.recycle();
                        return iSriWriteBlock32;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sriProtectBlock(byte nLockReg) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByte(nLockReg);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sriProtectBlock, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSriProtectBlock = Stub.getDefaultImpl().sriProtectBlock(nLockReg);
                        _reply.recycle();
                        _data.recycle();
                        return iSriProtectBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int sriGetBlockProtection(byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_sriGetBlockProtection, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSriGetBlockProtection = Stub.getDefaultImpl().sriGetBlockProtection(dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return iSriGetBlockProtection;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public void checkCardForToss(Bundle bundle, CheckCardCallbackV2 callback, int timeout) throws RemoteException {
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
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_checkCardForToss, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().checkCardForToss(bundle, callback, timeout);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int setNfcParam(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setNfcParam, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int nfcParam = Stub.getDefaultImpl().setNfcParam(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return nfcParam;
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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
            public int transmitApduExtended(Bundle paramIn, Bundle paramOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (paramIn != null) {
                        _data.writeInt(1);
                        paramIn.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_transmitApduExtended, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTransmitApduExtended = Stub.getDefaultImpl().transmitApduExtended(paramIn, paramOut);
                        _reply.recycle();
                        _data.recycle();
                        return iTransmitApduExtended;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        paramOut.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ReadCardOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ReadCardOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
