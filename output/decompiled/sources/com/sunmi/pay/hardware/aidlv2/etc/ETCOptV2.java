package com.sunmi.pay.hardware.aidlv2.etc;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.etc.ETCSearchListenerV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCSearchTradeOBUListenerV2;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCOptV2.class */
public interface ETCOptV2 extends IInterface {
    int i2cDataExchange(int i, byte[] bArr, int i2, int i3, byte[] bArr2) throws RemoteException;

    void search(int i, ETCSearchListenerV2 eTCSearchListenerV2, int i2) throws RemoteException;

    void cancelSearch() throws RemoteException;

    int setSearchParam(Bundle bundle) throws RemoteException;

    void searchTradeOBU(int i, String str, int i2, ETCSearchTradeOBUListenerV2 eTCSearchTradeOBUListenerV2) throws RemoteException;

    int getTradeVehicleCipherInfo(int i, String str, int i2, int i3, Bundle bundle) throws RemoteException;

    int getTradeRecord(Bundle bundle) throws RemoteException;

    int initTrade(int i, int i2, String str, Bundle bundle) throws RemoteException;

    int complexTrade(byte[] bArr, String str, String str2, String str3, String str4, Bundle bundle) throws RemoteException;

    int finishTrade(int i) throws RemoteException;

    int tradeHeartbeat() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCOptV2$Default.class */
    public static class Default implements ETCOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int i2cDataExchange(int addr, byte[] send, int expOutLen, int timeout, byte[] recv) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public void search(int maxNum, ETCSearchListenerV2 listener, int timeout) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public void cancelSearch() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int setSearchParam(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public void searchTradeOBU(int unixTime, String obuId, int timeout, ETCSearchTradeOBUListenerV2 listener) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int getTradeVehicleCipherInfo(int expectLen, String random, int macKeyVersion, int encryptVersion, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int getTradeRecord(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int initTrade(int keyIndex, int amount, String terminalNo, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int complexTrade(byte[] cacheData, String tradeNo, String tradeDate, String tradeTime, String mac, Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int finishTrade(int tradeResult) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
        public int tradeHeartbeat() throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCOptV2$Stub.class */
    public static abstract class Stub extends Binder implements ETCOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2";
        static final int TRANSACTION_i2cDataExchange = 1;
        static final int TRANSACTION_search = 2;
        static final int TRANSACTION_cancelSearch = 3;
        static final int TRANSACTION_setSearchParam = 4;
        static final int TRANSACTION_searchTradeOBU = 5;
        static final int TRANSACTION_getTradeVehicleCipherInfo = 6;
        static final int TRANSACTION_getTradeRecord = 7;
        static final int TRANSACTION_initTrade = 8;
        static final int TRANSACTION_complexTrade = 9;
        static final int TRANSACTION_finishTrade = 10;
        static final int TRANSACTION_tradeHeartbeat = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ETCOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ETCOptV2)) {
                return (ETCOptV2) iin;
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
            byte[] _arg4;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    byte[] _arg1 = data.createByteArray();
                    int _arg2 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4_length = data.readInt();
                    if (_arg4_length < 0) {
                        _arg4 = null;
                    } else {
                        _arg4 = new byte[_arg4_length];
                    }
                    int _result = i2cDataExchange(_arg02, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    reply.writeByteArray(_arg4);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    ETCSearchListenerV2 _arg12 = ETCSearchListenerV2.Stub.asInterface(data.readStrongBinder());
                    int _arg22 = data.readInt();
                    search(_arg03, _arg12, _arg22);
                    reply.writeNoException();
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    cancelSearch();
                    reply.writeNoException();
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result2 = setSearchParam(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    String _arg13 = data.readString();
                    int _arg23 = data.readInt();
                    ETCSearchTradeOBUListenerV2 _arg32 = ETCSearchTradeOBUListenerV2.Stub.asInterface(data.readStrongBinder());
                    searchTradeOBU(_arg04, _arg13, _arg23, _arg32);
                    reply.writeNoException();
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg14 = data.readString();
                    int _arg24 = data.readInt();
                    int _arg33 = data.readInt();
                    Bundle _arg42 = new Bundle();
                    int _result3 = getTradeVehicleCipherInfo(_arg05, _arg14, _arg24, _arg33, _arg42);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    if (_arg42 != null) {
                        reply.writeInt(1);
                        _arg42.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg06 = new Bundle();
                    int _result4 = getTradeRecord(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    if (_arg06 != null) {
                        reply.writeInt(1);
                        _arg06.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _arg15 = data.readInt();
                    String _arg25 = data.readString();
                    Bundle _arg34 = new Bundle();
                    int _result5 = initTrade(_arg07, _arg15, _arg25, _arg34);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    if (_arg34 != null) {
                        reply.writeInt(1);
                        _arg34.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg08 = data.createByteArray();
                    String _arg16 = data.readString();
                    String _arg26 = data.readString();
                    String _arg35 = data.readString();
                    String _arg43 = data.readString();
                    Bundle _arg5 = new Bundle();
                    int _result6 = complexTrade(_arg08, _arg16, _arg26, _arg35, _arg43, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    if (_arg5 != null) {
                        reply.writeInt(1);
                        _arg5.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg09 = data.readInt();
                    int _result7 = finishTrade(_arg09);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = tradeHeartbeat();
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/etc/ETCOptV2$Stub$Proxy.class */
        private static class Proxy implements ETCOptV2 {
            private IBinder mRemote;
            public static ETCOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int i2cDataExchange(int addr, byte[] send, int expOutLen, int timeout, byte[] recv) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(addr);
                    _data.writeByteArray(send);
                    _data.writeInt(expOutLen);
                    _data.writeInt(timeout);
                    if (recv == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(recv.length);
                    }
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iI2cDataExchange = Stub.getDefaultImpl().i2cDataExchange(addr, send, expOutLen, timeout, recv);
                        _reply.recycle();
                        _data.recycle();
                        return iI2cDataExchange;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readByteArray(recv);
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public void search(int maxNum, ETCSearchListenerV2 listener, int timeout) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(maxNum);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeInt(timeout);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().search(maxNum, listener, timeout);
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public void cancelSearch() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().cancelSearch();
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int setSearchParam(Bundle bundle) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int searchParam = Stub.getDefaultImpl().setSearchParam(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return searchParam;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public void searchTradeOBU(int unixTime, String obuId, int timeout, ETCSearchTradeOBUListenerV2 listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(unixTime);
                    _data.writeString(obuId);
                    _data.writeInt(timeout);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().searchTradeOBU(unixTime, obuId, timeout, listener);
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int getTradeVehicleCipherInfo(int expectLen, String random, int macKeyVersion, int encryptVersion, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(expectLen);
                    _data.writeString(random);
                    _data.writeInt(macKeyVersion);
                    _data.writeInt(encryptVersion);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int tradeVehicleCipherInfo = Stub.getDefaultImpl().getTradeVehicleCipherInfo(expectLen, random, macKeyVersion, encryptVersion, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return tradeVehicleCipherInfo;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int getTradeRecord(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int tradeRecord = Stub.getDefaultImpl().getTradeRecord(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return tradeRecord;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int initTrade(int keyIndex, int amount, String terminalNo, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(keyIndex);
                    _data.writeInt(amount);
                    _data.writeString(terminalNo);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iInitTrade = Stub.getDefaultImpl().initTrade(keyIndex, amount, terminalNo, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iInitTrade;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int complexTrade(byte[] cacheData, String tradeNo, String tradeDate, String tradeTime, String mac, Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(cacheData);
                    _data.writeString(tradeNo);
                    _data.writeString(tradeDate);
                    _data.writeString(tradeTime);
                    _data.writeString(mac);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iComplexTrade = Stub.getDefaultImpl().complexTrade(cacheData, tradeNo, tradeDate, tradeTime, mac, bundle);
                        _reply.recycle();
                        _data.recycle();
                        return iComplexTrade;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int finishTrade(int tradeResult) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(tradeResult);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iFinishTrade = Stub.getDefaultImpl().finishTrade(tradeResult);
                        _reply.recycle();
                        _data.recycle();
                        return iFinishTrade;
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

            @Override // com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2
            public int tradeHeartbeat() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iTradeHeartbeat = Stub.getDefaultImpl().tradeHeartbeat();
                        _reply.recycle();
                        _data.recycle();
                        return iTradeHeartbeat;
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

        public static boolean setDefaultImpl(ETCOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ETCOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
