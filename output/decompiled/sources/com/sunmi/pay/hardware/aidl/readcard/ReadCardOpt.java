package com.sunmi.pay.hardware.aidl.readcard;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.bean.TransData;
import com.sunmi.pay.hardware.aidl.readcard.CheckCardCallback;
import com.sunmi.pay.hardware.aidl.readcard.ReadCardCallback;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardOpt.class */
public interface ReadCardOpt extends IInterface {
    void checkBankCard(int i, ReadCardCallback readCardCallback, int i2) throws RemoteException;

    void checkCard(int i, ReadCardCallback readCardCallback, int i2) throws RemoteException;

    void cancelCheckCard() throws RemoteException;

    int smartCardExchange(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int cardOff(int i) throws RemoteException;

    int mifareAuth(int i, int i2, byte[] bArr) throws RemoteException;

    int mifareReadBlock(int i, byte[] bArr) throws RemoteException;

    int mifareWriteBlock(int i, byte[] bArr) throws RemoteException;

    int mifareIncValue(int i, byte[] bArr) throws RemoteException;

    int mifareDecValue(int i, byte[] bArr) throws RemoteException;

    int initTransData(TransData transData) throws RemoteException;

    int smartCardExChangeNISO(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int getCardExistStatus(int i) throws RemoteException;

    void detectCard(int i, CheckCardCallback checkCardCallback, int i2) throws RemoteException;

    int smartCardExChangePASS(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    int smartCardExChangePASSNoLength(int i, byte[] bArr, byte[] bArr2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardOpt$Default.class */
    public static class Default implements ReadCardOpt {
        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public void checkBankCard(int cardType, ReadCardCallback callback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public void checkCard(int cardType, ReadCardCallback callback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public void cancelCheckCard() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int smartCardExchange(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int cardOff(int cardType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int mifareAuth(int keyType, int block, byte[] key) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int mifareReadBlock(int block, byte[] blockData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int mifareWriteBlock(int block, byte[] blockData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int mifareIncValue(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int mifareDecValue(int block, byte[] value) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int initTransData(TransData transData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int smartCardExChangeNISO(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int getCardExistStatus(int cardType) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public void detectCard(int cardType, CheckCardCallback checkCardCallback, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int smartCardExChangePASS(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
        public int smartCardExChangePASSNoLength(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardOpt$Stub.class */
    public static abstract class Stub extends Binder implements ReadCardOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt";
        static final int TRANSACTION_checkBankCard = 1;
        static final int TRANSACTION_checkCard = 2;
        static final int TRANSACTION_cancelCheckCard = 3;
        static final int TRANSACTION_smartCardExchange = 4;
        static final int TRANSACTION_cardOff = 5;
        static final int TRANSACTION_mifareAuth = 6;
        static final int TRANSACTION_mifareReadBlock = 7;
        static final int TRANSACTION_mifareWriteBlock = 8;
        static final int TRANSACTION_mifareIncValue = 9;
        static final int TRANSACTION_mifareDecValue = 10;
        static final int TRANSACTION_initTransData = 11;
        static final int TRANSACTION_smartCardExChangeNISO = 12;
        static final int TRANSACTION_getCardExistStatus = 13;
        static final int TRANSACTION_detectCard = 14;
        static final int TRANSACTION_smartCardExChangePASS = 15;
        static final int TRANSACTION_smartCardExChangePASSNoLength = 16;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ReadCardOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ReadCardOpt)) {
                return (ReadCardOpt) iin;
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
            byte[] _arg22;
            byte[] _arg23;
            TransData _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    ReadCardCallback _arg1 = ReadCardCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg24 = data.readInt();
                    checkBankCard(_arg02, _arg1, _arg24);
                    reply.writeNoException();
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    ReadCardCallback _arg12 = ReadCardCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg25 = data.readInt();
                    checkCard(_arg03, _arg12, _arg25);
                    reply.writeNoException();
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    cancelCheckCard();
                    reply.writeNoException();
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    byte[] _arg13 = data.createByteArray();
                    byte[] _arg26 = data.createByteArray();
                    int _result = smartCardExchange(_arg04, _arg13, _arg26);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    reply.writeByteArray(_arg13);
                    reply.writeByteArray(_arg26);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _result2 = cardOff(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _arg14 = data.readInt();
                    byte[] _arg27 = data.createByteArray();
                    int _result3 = mifareAuth(_arg06, _arg14, _arg27);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    reply.writeByteArray(_arg27);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    byte[] _arg15 = data.createByteArray();
                    int _result4 = mifareReadBlock(_arg07, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    reply.writeByteArray(_arg15);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg08 = data.readInt();
                    byte[] _arg16 = data.createByteArray();
                    int _result5 = mifareWriteBlock(_arg08, _arg16);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    reply.writeByteArray(_arg16);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    byte[] _arg17 = data.createByteArray();
                    int _result6 = mifareIncValue(_arg09, _arg17);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    reply.writeByteArray(_arg17);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg010 = data.readInt();
                    byte[] _arg18 = data.createByteArray();
                    int _result7 = mifareDecValue(_arg010, _arg18);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    reply.writeByteArray(_arg18);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = TransData.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result8 = initTransData(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg011 = data.readInt();
                    byte[] _arg19 = data.createByteArray();
                    int _arg2_length = data.readInt();
                    if (_arg2_length < 0) {
                        _arg23 = null;
                    } else {
                        _arg23 = new byte[_arg2_length];
                    }
                    int _result9 = smartCardExChangeNISO(_arg011, _arg19, _arg23);
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    reply.writeByteArray(_arg23);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _result10 = getCardExistStatus(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg013 = data.readInt();
                    CheckCardCallback _arg110 = CheckCardCallback.Stub.asInterface(data.readStrongBinder());
                    int _arg28 = data.readInt();
                    detectCard(_arg013, _arg110, _arg28);
                    reply.writeNoException();
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg014 = data.readInt();
                    byte[] _arg111 = data.createByteArray();
                    int _arg2_length2 = data.readInt();
                    if (_arg2_length2 < 0) {
                        _arg22 = null;
                    } else {
                        _arg22 = new byte[_arg2_length2];
                    }
                    int _result11 = smartCardExChangePASS(_arg014, _arg111, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    reply.writeByteArray(_arg22);
                    break;
                case TRANSACTION_smartCardExChangePASSNoLength /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg015 = data.readInt();
                    byte[] _arg112 = data.createByteArray();
                    int _arg2_length3 = data.readInt();
                    if (_arg2_length3 < 0) {
                        _arg2 = null;
                    } else {
                        _arg2 = new byte[_arg2_length3];
                    }
                    int _result12 = smartCardExChangePASSNoLength(_arg015, _arg112, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result12);
                    reply.writeByteArray(_arg2);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/readcard/ReadCardOpt$Stub$Proxy.class */
        private static class Proxy implements ReadCardOpt {
            private IBinder mRemote;
            public static ReadCardOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public void checkBankCard(int cardType, ReadCardCallback callback, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().checkBankCard(cardType, callback, timeout);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public void checkCard(int cardType, ReadCardCallback callback, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().checkCard(cardType, callback, timeout);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public void cancelCheckCard() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int cardOff(int cardType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int mifareAuth(int keyType, int block, byte[] key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyType);
                    _data.writeInt(block);
                    _data.writeByteArray(key);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int mifareReadBlock(int block, byte[] blockData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(blockData);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareReadBlock = Stub.getDefaultImpl().mifareReadBlock(block, blockData);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareReadBlock;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(blockData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int mifareWriteBlock(int block, byte[] blockData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(blockData);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareWriteBlock = Stub.getDefaultImpl().mifareWriteBlock(block, blockData);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareWriteBlock;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(blockData);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int mifareIncValue(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareIncValue = Stub.getDefaultImpl().mifareIncValue(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareIncValue;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(value);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int mifareDecValue(int block, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(block);
                    _data.writeByteArray(value);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iMifareDecValue = Stub.getDefaultImpl().mifareDecValue(block, value);
                        _reply.recycle();
                        _data.recycle();
                        return iMifareDecValue;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(value);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int initTransData(TransData transData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (transData != null) {
                        _data.writeInt(1);
                        transData.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInitTransData = Stub.getDefaultImpl().initTransData(transData);
                        _reply.recycle();
                        _data.recycle();
                        return iInitTransData;
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int smartCardExChangeNISO(int cardType, byte[] apduSend, byte[] apduRecv) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iSmartCardExChangeNISO = Stub.getDefaultImpl().smartCardExChangeNISO(cardType, apduSend, apduRecv);
                        _reply.recycle();
                        _data.recycle();
                        return iSmartCardExChangeNISO;
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public int getCardExistStatus(int cardType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
            public void detectCard(int cardType, CheckCardCallback checkCardCallback, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cardType);
                    _data.writeStrongBinder(checkCardCallback != null ? checkCardCallback.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().detectCard(cardType, checkCardCallback, timeout);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
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
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
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

            @Override // com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt
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
        }

        public static boolean setDefaultImpl(ReadCardOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ReadCardOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
