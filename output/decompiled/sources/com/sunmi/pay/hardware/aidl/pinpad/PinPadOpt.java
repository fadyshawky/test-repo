package com.sunmi.pay.hardware.aidl.pinpad;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.bean.PinPadConfig;
import com.sunmi.pay.hardware.aidl.bean.PinPadData;
import com.sunmi.pay.hardware.aidl.pinpad.PinPadListener;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadOpt.class */
public interface PinPadOpt extends IInterface {
    String initPinPad(PinPadConfig pinPadConfig, PinPadListener pinPadListener) throws RemoteException;

    void importPinPadData(PinPadData pinPadData) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadOpt$Default.class */
    public static class Default implements PinPadOpt {
        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt
        public String initPinPad(PinPadConfig config, PinPadListener listerner) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt
        public void importPinPadData(PinPadData data) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadOpt$Stub.class */
    public static abstract class Stub extends Binder implements PinPadOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt";
        static final int TRANSACTION_initPinPad = 1;
        static final int TRANSACTION_importPinPadData = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PinPadOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PinPadOpt)) {
                return (PinPadOpt) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            PinPadData _arg0;
            PinPadConfig _arg02;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = PinPadConfig.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    PinPadListener _arg1 = PinPadListener.Stub.asInterface(data.readStrongBinder());
                    String _result = initPinPad(_arg02, _arg1);
                    reply.writeNoException();
                    reply.writeString(_result);
                    if (_arg02 != null) {
                        reply.writeInt(1);
                        _arg02.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PinPadData.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    importPinPadData(_arg0);
                    reply.writeNoException();
                    if (_arg0 != null) {
                        reply.writeInt(1);
                        _arg0.writeToParcel(reply, 1);
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

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/pinpad/PinPadOpt$Stub$Proxy.class */
        private static class Proxy implements PinPadOpt {
            private IBinder mRemote;
            public static PinPadOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt
            public String initPinPad(PinPadConfig config, PinPadListener listerner) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (config != null) {
                        _data.writeInt(1);
                        config.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listerner != null ? listerner.asBinder() : null);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String strInitPinPad = Stub.getDefaultImpl().initPinPad(config, listerner);
                        _reply.recycle();
                        _data.recycle();
                        return strInitPinPad;
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    if (0 != _reply.readInt()) {
                        config.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt
            public void importPinPadData(PinPadData data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importPinPadData(data);
                        _reply.recycle();
                        _data.recycle();
                    } else {
                        _reply.readException();
                        if (0 != _reply.readInt()) {
                            data.readFromParcel(_reply);
                        }
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(PinPadOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PinPadOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
