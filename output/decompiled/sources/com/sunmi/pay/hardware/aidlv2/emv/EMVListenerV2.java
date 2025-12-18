package com.sunmi.pay.hardware.aidlv2.emv;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2;
import java.util.List;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVListenerV2.class */
public interface EMVListenerV2 extends IInterface {
    void onWaitAppSelect(List<EMVCandidateV2> list, boolean z) throws RemoteException;

    void onAppFinalSelect(String str) throws RemoteException;

    void onConfirmCardNo(String str) throws RemoteException;

    void onRequestShowPinPad(int i, int i2) throws RemoteException;

    void onRequestSignature() throws RemoteException;

    void onCertVerify(int i, String str) throws RemoteException;

    void onOnlineProc() throws RemoteException;

    void onCardDataExchangeComplete() throws RemoteException;

    void onTransResult(int i, String str) throws RemoteException;

    void onConfirmationCodeVerified() throws RemoteException;

    void onRequestDataExchange(String str) throws RemoteException;

    void onTermRiskManagement() throws RemoteException;

    void onPreFirstGenAC() throws RemoteException;

    void onDataStorageProc(String[] strArr, String[] strArr2) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVListenerV2$Default.class */
    public static class Default implements EMVListenerV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onWaitAppSelect(List<EMVCandidateV2> candList, boolean isFirstSelect) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onConfirmCardNo(String cardNo) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onRequestShowPinPad(int pinType, int remainTimes) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onRequestSignature() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onCertVerify(int certType, String certInfo) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onOnlineProc() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onCardDataExchangeComplete() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onTransResult(int code, String desc) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onConfirmationCodeVerified() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onRequestDataExchange(String cardNo) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onTermRiskManagement() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onPreFirstGenAC() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
        public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVListenerV2$Stub.class */
    public static abstract class Stub extends Binder implements EMVListenerV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2";
        static final int TRANSACTION_onWaitAppSelect = 1;
        static final int TRANSACTION_onAppFinalSelect = 2;
        static final int TRANSACTION_onConfirmCardNo = 3;
        static final int TRANSACTION_onRequestShowPinPad = 4;
        static final int TRANSACTION_onRequestSignature = 5;
        static final int TRANSACTION_onCertVerify = 6;
        static final int TRANSACTION_onOnlineProc = 7;
        static final int TRANSACTION_onCardDataExchangeComplete = 8;
        static final int TRANSACTION_onTransResult = 9;
        static final int TRANSACTION_onConfirmationCodeVerified = 10;
        static final int TRANSACTION_onRequestDataExchange = 11;
        static final int TRANSACTION_onTermRiskManagement = 12;
        static final int TRANSACTION_onPreFirstGenAC = 13;
        static final int TRANSACTION_onDataStorageProc = 14;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static EMVListenerV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof EMVListenerV2)) {
                return (EMVListenerV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    List<EMVCandidateV2> _arg0 = data.createTypedArrayList(EMVCandidateV2.CREATOR);
                    boolean _arg1 = 0 != data.readInt();
                    onWaitAppSelect(_arg0, _arg1);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    onAppFinalSelect(_arg02);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    onConfirmCardNo(_arg03);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg04 = data.readInt();
                    int _arg12 = data.readInt();
                    onRequestShowPinPad(_arg04, _arg12);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    onRequestSignature();
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    String _arg13 = data.readString();
                    onCertVerify(_arg05, _arg13);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    onOnlineProc();
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    onCardDataExchangeComplete();
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    String _arg14 = data.readString();
                    onTransResult(_arg06, _arg14);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    onConfirmationCodeVerified();
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg07 = data.readString();
                    onRequestDataExchange(_arg07);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    onTermRiskManagement();
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    onPreFirstGenAC();
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    String[] _arg08 = data.createStringArray();
                    String[] _arg15 = data.createStringArray();
                    onDataStorageProc(_arg08, _arg15);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/emv/EMVListenerV2$Stub$Proxy.class */
        private static class Proxy implements EMVListenerV2 {
            private IBinder mRemote;
            public static EMVListenerV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onWaitAppSelect(List<EMVCandidateV2> candList, boolean isFirstSelect) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(candList);
                    _data.writeInt(isFirstSelect ? 1 : 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onWaitAppSelect(candList, isFirstSelect);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onAppFinalSelect(String tag9F06Value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(tag9F06Value);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onAppFinalSelect(tag9F06Value);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onConfirmCardNo(String cardNo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cardNo);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onConfirmCardNo(cardNo);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onRequestShowPinPad(int pinType, int remainTimes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pinType);
                    _data.writeInt(remainTimes);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onRequestShowPinPad(pinType, remainTimes);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onRequestSignature() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onRequestSignature();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onCertVerify(int certType, String certInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(certType);
                    _data.writeString(certInfo);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onCertVerify(certType, certInfo);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onOnlineProc() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onOnlineProc();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onCardDataExchangeComplete() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onCardDataExchangeComplete();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onTransResult(int code, String desc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(desc);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onTransResult(code, desc);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onConfirmationCodeVerified() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onConfirmationCodeVerified();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onRequestDataExchange(String cardNo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cardNo);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onRequestDataExchange(cardNo);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onTermRiskManagement() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onTermRiskManagement();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onPreFirstGenAC() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onPreFirstGenAC();
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.emv.EMVListenerV2
            public void onDataStorageProc(String[] containerID, String[] containerContent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(containerID);
                    _data.writeStringArray(containerContent);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onDataStorageProc(containerID, containerContent);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }
        }

        public static boolean setDefaultImpl(EMVListenerV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static EMVListenerV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
