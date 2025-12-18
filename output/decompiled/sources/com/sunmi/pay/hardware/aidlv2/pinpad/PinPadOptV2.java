package com.sunmi.pay.hardware.aidlv2.pinpad;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadDataV2;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadDataV2Ex;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadTextConfigV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadOptV2.class */
public interface PinPadOptV2 extends IInterface {
    String initPinPad(PinPadConfigV2 pinPadConfigV2, PinPadListenerV2 pinPadListenerV2) throws RemoteException;

    void importPinPadData(PinPadDataV2 pinPadDataV2) throws RemoteException;

    void cancelInputPin() throws RemoteException;

    void setPinPadText(PinPadTextConfigV2 pinPadTextConfigV2) throws RemoteException;

    int setPinPadMode(Bundle bundle) throws RemoteException;

    int getPinPadMode(Bundle bundle) throws RemoteException;

    String initPinPadEx(Bundle bundle, PinPadListenerV2 pinPadListenerV2) throws RemoteException;

    int setAntiExhaustiveProtectionMode(int i) throws RemoteException;

    int getAntiExhaustiveProtectionMode() throws RemoteException;

    int setVisualImpairmentModeParam(Bundle bundle) throws RemoteException;

    int getVisualImpairmentModeParam(Bundle bundle) throws RemoteException;

    int startInputPin(Bundle bundle, PinPadListenerV2 pinPadListenerV2) throws RemoteException;

    int getPinBlock(Bundle bundle, byte[] bArr) throws RemoteException;

    int offlinePinVerify(Bundle bundle, Bundle bundle2) throws RemoteException;

    void importPinPadDataEx(PinPadDataV2Ex pinPadDataV2Ex) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadOptV2$Default.class */
    public static class Default implements PinPadOptV2 {
        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public String initPinPad(PinPadConfigV2 config, PinPadListenerV2 listener) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public void importPinPadData(PinPadDataV2 data) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public void cancelInputPin() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public void setPinPadText(PinPadTextConfigV2 config) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int setPinPadMode(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int getPinPadMode(Bundle bundle) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public String initPinPadEx(Bundle config, PinPadListenerV2 listener) throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int setAntiExhaustiveProtectionMode(int level) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int getAntiExhaustiveProtectionMode() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int setVisualImpairmentModeParam(Bundle param) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int getVisualImpairmentModeParam(Bundle param) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int startInputPin(Bundle param, PinPadListenerV2 listener) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int getPinBlock(Bundle param, byte[] dataOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public int offlinePinVerify(Bundle paramIn, Bundle paramOut) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
        public void importPinPadDataEx(PinPadDataV2Ex data) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadOptV2$Stub.class */
    public static abstract class Stub extends Binder implements PinPadOptV2 {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2";
        static final int TRANSACTION_initPinPad = 1;
        static final int TRANSACTION_importPinPadData = 2;
        static final int TRANSACTION_cancelInputPin = 3;
        static final int TRANSACTION_setPinPadText = 4;
        static final int TRANSACTION_setPinPadMode = 5;
        static final int TRANSACTION_getPinPadMode = 6;
        static final int TRANSACTION_initPinPadEx = 7;
        static final int TRANSACTION_setAntiExhaustiveProtectionMode = 8;
        static final int TRANSACTION_getAntiExhaustiveProtectionMode = 9;
        static final int TRANSACTION_setVisualImpairmentModeParam = 10;
        static final int TRANSACTION_getVisualImpairmentModeParam = 11;
        static final int TRANSACTION_startInputPin = 12;
        static final int TRANSACTION_getPinBlock = 13;
        static final int TRANSACTION_offlinePinVerify = 14;
        static final int TRANSACTION_importPinPadDataEx = 15;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PinPadOptV2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PinPadOptV2)) {
                return (PinPadOptV2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            PinPadDataV2Ex _arg0;
            Bundle _arg02;
            Bundle _arg03;
            byte[] _arg1;
            Bundle _arg04;
            Bundle _arg05;
            Bundle _arg06;
            Bundle _arg07;
            PinPadTextConfigV2 _arg08;
            PinPadDataV2 _arg09;
            PinPadConfigV2 _arg010;
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg010 = PinPadConfigV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    String _result = initPinPad(_arg010, PinPadListenerV2.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeString(_result);
                    if (_arg010 != null) {
                        reply.writeInt(1);
                        _arg010.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg09 = PinPadDataV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    importPinPadData(_arg09);
                    reply.writeNoException();
                    if (_arg09 != null) {
                        reply.writeInt(1);
                        _arg09.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    cancelInputPin();
                    reply.writeNoException();
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg08 = PinPadTextConfigV2.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    setPinPadText(_arg08);
                    reply.writeNoException();
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg07 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    int _result2 = setPinPadMode(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg011 = new Bundle();
                    int _result3 = getPinPadMode(_arg011);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    if (_arg011 != null) {
                        reply.writeInt(1);
                        _arg011.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg06 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    String _result4 = initPinPadEx(_arg06, PinPadListenerV2.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeString(_result4);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg012 = data.readInt();
                    int _result5 = setAntiExhaustiveProtectionMode(_arg012);
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _result6 = getAntiExhaustiveProtectionMode();
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg05 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    int _result7 = setVisualImpairmentModeParam(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    Bundle _arg013 = new Bundle();
                    int _result8 = getVisualImpairmentModeParam(_arg013);
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    if (_arg013 != null) {
                        reply.writeInt(1);
                        _arg013.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg04 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    int _result9 = startInputPin(_arg04, PinPadListenerV2.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeInt(_result9);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg03 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    int _arg1_length = data.readInt();
                    if (_arg1_length < 0) {
                        _arg1 = null;
                    } else {
                        _arg1 = new byte[_arg1_length];
                    }
                    int _result10 = getPinBlock(_arg03, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    reply.writeByteArray(_arg1);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    Bundle _arg12 = new Bundle();
                    int _result11 = offlinePinVerify(_arg02, _arg12);
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    if (_arg12 != null) {
                        reply.writeInt(1);
                        _arg12.writeToParcel(reply, 1);
                        break;
                    } else {
                        reply.writeInt(0);
                        break;
                    }
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    if (0 != data.readInt()) {
                        _arg0 = PinPadDataV2Ex.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    importPinPadDataEx(_arg0);
                    reply.writeNoException();
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/pinpad/PinPadOptV2$Stub$Proxy.class */
        private static class Proxy implements PinPadOptV2 {
            private IBinder mRemote;
            public static PinPadOptV2 sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public String initPinPad(PinPadConfigV2 config, PinPadListenerV2 listener) throws RemoteException {
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
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String strInitPinPad = Stub.getDefaultImpl().initPinPad(config, listener);
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public void importPinPadData(PinPadDataV2 data) throws RemoteException {
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public void cancelInputPin() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().cancelInputPin();
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public void setPinPadText(PinPadTextConfigV2 config) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setPinPadText(config);
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int setPinPadMode(Bundle bundle) throws RemoteException {
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
                        int pinPadMode = Stub.getDefaultImpl().setPinPadMode(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return pinPadMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int getPinPadMode(Bundle bundle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int pinPadMode = Stub.getDefaultImpl().getPinPadMode(bundle);
                        _reply.recycle();
                        _data.recycle();
                        return pinPadMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public String initPinPadEx(Bundle config, PinPadListenerV2 listener) throws RemoteException {
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
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String strInitPinPadEx = Stub.getDefaultImpl().initPinPadEx(config, listener);
                        _reply.recycle();
                        _data.recycle();
                        return strInitPinPadEx;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int setAntiExhaustiveProtectionMode(int level) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int antiExhaustiveProtectionMode = Stub.getDefaultImpl().setAntiExhaustiveProtectionMode(level);
                        _reply.recycle();
                        _data.recycle();
                        return antiExhaustiveProtectionMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int getAntiExhaustiveProtectionMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int antiExhaustiveProtectionMode = Stub.getDefaultImpl().getAntiExhaustiveProtectionMode();
                        _reply.recycle();
                        _data.recycle();
                        return antiExhaustiveProtectionMode;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int setVisualImpairmentModeParam(Bundle param) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (param != null) {
                        _data.writeInt(1);
                        param.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int visualImpairmentModeParam = Stub.getDefaultImpl().setVisualImpairmentModeParam(param);
                        _reply.recycle();
                        _data.recycle();
                        return visualImpairmentModeParam;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int getVisualImpairmentModeParam(Bundle param) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int visualImpairmentModeParam = Stub.getDefaultImpl().getVisualImpairmentModeParam(param);
                        _reply.recycle();
                        _data.recycle();
                        return visualImpairmentModeParam;
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    if (0 != _reply.readInt()) {
                        param.readFromParcel(_reply);
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int startInputPin(Bundle param, PinPadListenerV2 listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (param != null) {
                        _data.writeInt(1);
                        param.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iStartInputPin = Stub.getDefaultImpl().startInputPin(param, listener);
                        _reply.recycle();
                        _data.recycle();
                        return iStartInputPin;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int getPinBlock(Bundle param, byte[] dataOut) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (param != null) {
                        _data.writeInt(1);
                        param.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (dataOut == null) {
                        _data.writeInt(-1);
                    } else {
                        _data.writeInt(dataOut.length);
                    }
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int pinBlock = Stub.getDefaultImpl().getPinBlock(param, dataOut);
                        _reply.recycle();
                        _data.recycle();
                        return pinBlock;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public int offlinePinVerify(Bundle paramIn, Bundle paramOut) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iOfflinePinVerify = Stub.getDefaultImpl().offlinePinVerify(paramIn, paramOut);
                        _reply.recycle();
                        _data.recycle();
                        return iOfflinePinVerify;
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

            @Override // com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
            public void importPinPadDataEx(PinPadDataV2Ex data) throws RemoteException {
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
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().importPinPadDataEx(data);
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
        }

        public static boolean setDefaultImpl(PinPadOptV2 impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PinPadOptV2 getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
