package com.sunmi.pay.hardware.aidlv2.readcard;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/CheckCardCallbackV2.class */
public interface CheckCardCallbackV2 extends IInterface {
    void findMagCard(Bundle bundle) throws RemoteException;

    void findICCard(String str) throws RemoteException;

    void findRFCard(String str) throws RemoteException;

    void onError(int i, String str) throws RemoteException;

    void findICCardEx(Bundle bundle) throws RemoteException;

    void findRFCardEx(Bundle bundle) throws RemoteException;

    void onErrorEx(Bundle bundle) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/CheckCardCallbackV2$Default.class */
    public static class Default implements CheckCardCallbackV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void findMagCard(Bundle info) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void findICCard(String atr) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void findRFCard(String uuid) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void onError(int code, String message) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void findICCardEx(Bundle info) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void findRFCardEx(Bundle info) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
        public void onErrorEx(Bundle info) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/CheckCardCallbackV2$Stub.class */
    public static abstract class Stub extends Binder implements CheckCardCallbackV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2";
        static final int TRANSACTION_findMagCard = 1;
        static final int TRANSACTION_findICCard = 2;
        static final int TRANSACTION_findRFCard = 3;
        static final int TRANSACTION_onError = 4;
        static final int TRANSACTION_findICCardEx = 5;
        static final int TRANSACTION_findRFCardEx = 6;
        static final int TRANSACTION_onErrorEx = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static CheckCardCallbackV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof CheckCardCallbackV2)) {
                return (CheckCardCallbackV2) iin;
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
            Bundle _arg04;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    findMagCard(_arg04);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg05 = data.readString();
                    findICCard(_arg05);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    findRFCard(_arg06);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    String _arg1 = data.readString();
                    onError(_arg07, _arg1);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    findICCardEx(_arg03);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    findRFCardEx(_arg02);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onErrorEx(_arg0);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/readcard/CheckCardCallbackV2$Stub$Proxy.class */
        private static class Proxy implements CheckCardCallbackV2 {
            private IBinder mRemote;
            public static CheckCardCallbackV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void findMagCard(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findMagCard(info);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void findICCard(String atr) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(atr);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findICCard(atr);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void findRFCard(String uuid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uuid);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findRFCard(uuid);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void onError(int code, String message) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeString(message);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onError(code, message);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void findICCardEx(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findICCardEx(info);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void findRFCardEx(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().findRFCardEx(info);
                        _data.recycle();
                    } else {
                        _data.recycle();
                    }
                } catch (Throwable th) {
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
            public void onErrorEx(Bundle info) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(1);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onErrorEx(info);
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

        public static boolean setDefaultImpl(CheckCardCallbackV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static CheckCardCallbackV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
