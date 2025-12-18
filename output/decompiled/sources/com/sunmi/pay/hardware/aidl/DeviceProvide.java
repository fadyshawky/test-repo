package com.sunmi.pay.hardware.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.emv.EMVOpt;
import com.sunmi.pay.hardware.aidl.pinpad.PinPadOpt;
import com.sunmi.pay.hardware.aidl.print.PrinterOpt;
import com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt;
import com.sunmi.pay.hardware.aidl.security.SecurityOpt;
import com.sunmi.pay.hardware.aidl.system.BasicOpt;
import com.sunmi.pay.hardware.aidl.tax.TaxOpt;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.etc.ETCOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.print.PrinterOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.DevCertManagerV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;
import com.sunmi.pay.hardware.aidlv2.tax.TaxOptV2;
import com.sunmi.pay.hardware.aidlv2.test.TestOptV2;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/DeviceProvide.class */
public interface DeviceProvide extends IInterface {
    BasicOpt getBasicOpt() throws RemoteException;

    ReadCardOpt getReadCardOpt() throws RemoteException;

    PinPadOpt getPinPadOpt() throws RemoteException;

    EMVOpt getEMVOpt() throws RemoteException;

    SecurityOpt getSecurityOpt() throws RemoteException;

    int setBinder(IBinder iBinder) throws RemoteException;

    PrinterOpt getPrinterOpt() throws RemoteException;

    TaxOpt getTaxOpt() throws RemoteException;

    BasicOptV2 getBasicOptV2() throws RemoteException;

    ReadCardOptV2 getReadCardOptV2() throws RemoteException;

    PinPadOptV2 getPinPadOptV2() throws RemoteException;

    EMVOptV2 getEMVOptV2() throws RemoteException;

    SecurityOptV2 getSecurityOptV2() throws RemoteException;

    PrinterOptV2 getPrinterOptV2() throws RemoteException;

    TaxOptV2 getTaxOptV2() throws RemoteException;

    ETCOptV2 getETCOptV2() throws RemoteException;

    TestOptV2 getTestOptV2() throws RemoteException;

    DevCertManagerV2 getDevCertManagerV2() throws RemoteException;

    IBinder getOptBinderV2(String str) throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/DeviceProvide$Default.class */
    public static class Default implements DeviceProvide {
        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public BasicOpt getBasicOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public ReadCardOpt getReadCardOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public PinPadOpt getPinPadOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public EMVOpt getEMVOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public SecurityOpt getSecurityOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public int setBinder(IBinder client) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public PrinterOpt getPrinterOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public TaxOpt getTaxOpt() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public BasicOptV2 getBasicOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public ReadCardOptV2 getReadCardOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public PinPadOptV2 getPinPadOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public EMVOptV2 getEMVOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public SecurityOptV2 getSecurityOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public PrinterOptV2 getPrinterOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public TaxOptV2 getTaxOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public ETCOptV2 getETCOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public TestOptV2 getTestOptV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public DevCertManagerV2 getDevCertManagerV2() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
        public IBinder getOptBinderV2(String name) throws RemoteException {
            return null;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/DeviceProvide$Stub.class */
    public static abstract class Stub extends Binder implements DeviceProvide {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.DeviceProvide";
        static final int TRANSACTION_getBasicOpt = 1;
        static final int TRANSACTION_getReadCardOpt = 2;
        static final int TRANSACTION_getPinPadOpt = 3;
        static final int TRANSACTION_getEMVOpt = 4;
        static final int TRANSACTION_getSecurityOpt = 5;
        static final int TRANSACTION_setBinder = 6;
        static final int TRANSACTION_getPrinterOpt = 7;
        static final int TRANSACTION_getTaxOpt = 8;
        static final int TRANSACTION_getBasicOptV2 = 9;
        static final int TRANSACTION_getReadCardOptV2 = 10;
        static final int TRANSACTION_getPinPadOptV2 = 11;
        static final int TRANSACTION_getEMVOptV2 = 12;
        static final int TRANSACTION_getSecurityOptV2 = 13;
        static final int TRANSACTION_getPrinterOptV2 = 14;
        static final int TRANSACTION_getTaxOptV2 = 15;
        static final int TRANSACTION_getETCOptV2 = 16;
        static final int TRANSACTION_getTestOptV2 = 17;
        static final int TRANSACTION_getDevCertManagerV2 = 18;
        static final int TRANSACTION_getOptBinderV2 = 19;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static DeviceProvide asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof DeviceProvide)) {
                return (DeviceProvide) iin;
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
                    BasicOpt _result = getBasicOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result != null ? _result.asBinder() : null);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    ReadCardOpt _result2 = getReadCardOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result2 != null ? _result2.asBinder() : null);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    PinPadOpt _result3 = getPinPadOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result3 != null ? _result3.asBinder() : null);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    EMVOpt _result4 = getEMVOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result4 != null ? _result4.asBinder() : null);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    SecurityOpt _result5 = getSecurityOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result5 != null ? _result5.asBinder() : null);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg0 = data.readStrongBinder();
                    int _result6 = setBinder(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    PrinterOpt _result7 = getPrinterOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result7 != null ? _result7.asBinder() : null);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    TaxOpt _result8 = getTaxOpt();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result8 != null ? _result8.asBinder() : null);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    BasicOptV2 _result9 = getBasicOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result9 != null ? _result9.asBinder() : null);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    ReadCardOptV2 _result10 = getReadCardOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result10 != null ? _result10.asBinder() : null);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    PinPadOptV2 _result11 = getPinPadOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result11 != null ? _result11.asBinder() : null);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    EMVOptV2 _result12 = getEMVOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result12 != null ? _result12.asBinder() : null);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    SecurityOptV2 _result13 = getSecurityOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result13 != null ? _result13.asBinder() : null);
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    PrinterOptV2 _result14 = getPrinterOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result14 != null ? _result14.asBinder() : null);
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    TaxOptV2 _result15 = getTaxOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result15 != null ? _result15.asBinder() : null);
                    break;
                case TRANSACTION_getETCOptV2 /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    ETCOptV2 _result16 = getETCOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result16 != null ? _result16.asBinder() : null);
                    break;
                case TRANSACTION_getTestOptV2 /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    TestOptV2 _result17 = getTestOptV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result17 != null ? _result17.asBinder() : null);
                    break;
                case TRANSACTION_getDevCertManagerV2 /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    DevCertManagerV2 _result18 = getDevCertManagerV2();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result18 != null ? _result18.asBinder() : null);
                    break;
                case TRANSACTION_getOptBinderV2 /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg02 = data.readString();
                    IBinder _result19 = getOptBinderV2(_arg02);
                    reply.writeNoException();
                    reply.writeStrongBinder(_result19);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/DeviceProvide$Stub$Proxy.class */
        private static class Proxy implements DeviceProvide {
            private IBinder mRemote;
            public static DeviceProvide sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public BasicOpt getBasicOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        BasicOpt basicOpt = Stub.getDefaultImpl().getBasicOpt();
                        _reply.recycle();
                        _data.recycle();
                        return basicOpt;
                    }
                    _reply.readException();
                    BasicOpt _result = BasicOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public ReadCardOpt getReadCardOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        ReadCardOpt readCardOpt = Stub.getDefaultImpl().getReadCardOpt();
                        _reply.recycle();
                        _data.recycle();
                        return readCardOpt;
                    }
                    _reply.readException();
                    ReadCardOpt _result = ReadCardOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public PinPadOpt getPinPadOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        PinPadOpt pinPadOpt = Stub.getDefaultImpl().getPinPadOpt();
                        _reply.recycle();
                        _data.recycle();
                        return pinPadOpt;
                    }
                    _reply.readException();
                    PinPadOpt _result = PinPadOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public EMVOpt getEMVOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        EMVOpt eMVOpt = Stub.getDefaultImpl().getEMVOpt();
                        _reply.recycle();
                        _data.recycle();
                        return eMVOpt;
                    }
                    _reply.readException();
                    EMVOpt _result = EMVOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public SecurityOpt getSecurityOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        SecurityOpt securityOpt = Stub.getDefaultImpl().getSecurityOpt();
                        _reply.recycle();
                        _data.recycle();
                        return securityOpt;
                    }
                    _reply.readException();
                    SecurityOpt _result = SecurityOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public int setBinder(IBinder client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int binder = Stub.getDefaultImpl().setBinder(client);
                        _reply.recycle();
                        _data.recycle();
                        return binder;
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

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public PrinterOpt getPrinterOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        PrinterOpt printerOpt = Stub.getDefaultImpl().getPrinterOpt();
                        _reply.recycle();
                        _data.recycle();
                        return printerOpt;
                    }
                    _reply.readException();
                    PrinterOpt _result = PrinterOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public TaxOpt getTaxOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        TaxOpt taxOpt = Stub.getDefaultImpl().getTaxOpt();
                        _reply.recycle();
                        _data.recycle();
                        return taxOpt;
                    }
                    _reply.readException();
                    TaxOpt _result = TaxOpt.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public BasicOptV2 getBasicOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        BasicOptV2 basicOptV2 = Stub.getDefaultImpl().getBasicOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return basicOptV2;
                    }
                    _reply.readException();
                    BasicOptV2 _result = BasicOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public ReadCardOptV2 getReadCardOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        ReadCardOptV2 readCardOptV2 = Stub.getDefaultImpl().getReadCardOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return readCardOptV2;
                    }
                    _reply.readException();
                    ReadCardOptV2 _result = ReadCardOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public PinPadOptV2 getPinPadOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        PinPadOptV2 pinPadOptV2 = Stub.getDefaultImpl().getPinPadOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return pinPadOptV2;
                    }
                    _reply.readException();
                    PinPadOptV2 _result = PinPadOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public EMVOptV2 getEMVOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        EMVOptV2 eMVOptV2 = Stub.getDefaultImpl().getEMVOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return eMVOptV2;
                    }
                    _reply.readException();
                    EMVOptV2 _result = EMVOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public SecurityOptV2 getSecurityOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        SecurityOptV2 securityOptV2 = Stub.getDefaultImpl().getSecurityOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return securityOptV2;
                    }
                    _reply.readException();
                    SecurityOptV2 _result = SecurityOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public PrinterOptV2 getPrinterOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        PrinterOptV2 printerOptV2 = Stub.getDefaultImpl().getPrinterOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return printerOptV2;
                    }
                    _reply.readException();
                    PrinterOptV2 _result = PrinterOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public TaxOptV2 getTaxOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        TaxOptV2 taxOptV2 = Stub.getDefaultImpl().getTaxOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return taxOptV2;
                    }
                    _reply.readException();
                    TaxOptV2 _result = TaxOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public ETCOptV2 getETCOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getETCOptV2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        ETCOptV2 eTCOptV2 = Stub.getDefaultImpl().getETCOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return eTCOptV2;
                    }
                    _reply.readException();
                    ETCOptV2 _result = ETCOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public TestOptV2 getTestOptV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getTestOptV2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        TestOptV2 testOptV2 = Stub.getDefaultImpl().getTestOptV2();
                        _reply.recycle();
                        _data.recycle();
                        return testOptV2;
                    }
                    _reply.readException();
                    TestOptV2 _result = TestOptV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public DevCertManagerV2 getDevCertManagerV2() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getDevCertManagerV2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        DevCertManagerV2 devCertManagerV2 = Stub.getDefaultImpl().getDevCertManagerV2();
                        _reply.recycle();
                        _data.recycle();
                        return devCertManagerV2;
                    }
                    _reply.readException();
                    DevCertManagerV2 _result = DevCertManagerV2.Stub.asInterface(_reply.readStrongBinder());
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.sunmi.pay.hardware.aidl.DeviceProvide
            public IBinder getOptBinderV2(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getOptBinderV2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        IBinder optBinderV2 = Stub.getDefaultImpl().getOptBinderV2(name);
                        _reply.recycle();
                        _data.recycle();
                        return optBinderV2;
                    }
                    _reply.readException();
                    IBinder _result = _reply.readStrongBinder();
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

        public static boolean setDefaultImpl(DeviceProvide impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static DeviceProvide getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
