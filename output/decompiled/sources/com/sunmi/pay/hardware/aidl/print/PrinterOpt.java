package com.sunmi.pay.hardware.aidl.print;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.sunmi.pay.hardware.aidl.print.PrinterCallback;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterOpt.class */
public interface PrinterOpt extends IInterface {
    int printOpen() throws RemoteException;

    int printClose() throws RemoteException;

    int printPointLine(byte[] bArr) throws RemoteException;

    int printFeedPaper(int i) throws RemoteException;

    int getPrinterStatus() throws RemoteException;

    String getPrinterDriverVersion() throws RemoteException;

    int setGrayLevel(int i) throws RemoteException;

    int getBufferRemainingRows() throws RemoteException;

    String getPrinterConfig() throws RemoteException;

    int getPrintGrayLevel() throws RemoteException;

    int getTotalPrintDistance() throws RemoteException;

    String getPrinterSN() throws RemoteException;

    void registerPrintCallback(PrinterCallback printerCallback) throws RemoteException;

    void unregisterPrintCallback() throws RemoteException;

    int setPrintSpeed(int i) throws RemoteException;

    int setPrintHeatPoint(int i) throws RemoteException;

    int clearBuffer() throws RemoteException;

    int setPrintDotSpeedLevel(int i) throws RemoteException;

    int getPrintDotSpeedLevel() throws RemoteException;

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterOpt$Default.class */
    public static class Default implements PrinterOpt {
        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int printOpen() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int printClose() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int printPointLine(byte[] pointRowData) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int printFeedPaper(int nPixels) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int getPrinterStatus() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public String getPrinterDriverVersion() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int setGrayLevel(int level) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int getBufferRemainingRows() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public String getPrinterConfig() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int getPrintGrayLevel() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int getTotalPrintDistance() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public String getPrinterSN() throws RemoteException {
            return null;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public void registerPrintCallback(PrinterCallback callback) throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public void unregisterPrintCallback() throws RemoteException {
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int setPrintSpeed(int speed) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int setPrintHeatPoint(int pointNum) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int clearBuffer() throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int setPrintDotSpeedLevel(int level) throws RemoteException {
            return 0;
        }

        @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
        public int getPrintDotSpeedLevel() throws RemoteException {
            return 0;
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterOpt$Stub.class */
    public static abstract class Stub extends Binder implements PrinterOpt {
        private static final String DESCRIPTOR = "com.sunmi.pay.hardware.aidl.print.PrinterOpt";
        static final int TRANSACTION_printOpen = 1;
        static final int TRANSACTION_printClose = 2;
        static final int TRANSACTION_printPointLine = 3;
        static final int TRANSACTION_printFeedPaper = 4;
        static final int TRANSACTION_getPrinterStatus = 5;
        static final int TRANSACTION_getPrinterDriverVersion = 6;
        static final int TRANSACTION_setGrayLevel = 7;
        static final int TRANSACTION_getBufferRemainingRows = 8;
        static final int TRANSACTION_getPrinterConfig = 9;
        static final int TRANSACTION_getPrintGrayLevel = 10;
        static final int TRANSACTION_getTotalPrintDistance = 11;
        static final int TRANSACTION_getPrinterSN = 12;
        static final int TRANSACTION_registerPrintCallback = 13;
        static final int TRANSACTION_unregisterPrintCallback = 14;
        static final int TRANSACTION_setPrintSpeed = 15;
        static final int TRANSACTION_setPrintHeatPoint = 16;
        static final int TRANSACTION_clearBuffer = 17;
        static final int TRANSACTION_setPrintDotSpeedLevel = 18;
        static final int TRANSACTION_getPrintDotSpeedLevel = 19;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static PrinterOpt asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof PrinterOpt)) {
                return (PrinterOpt) iin;
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
                    int _result = printOpen();
                    reply.writeNoException();
                    reply.writeInt(_result);
                    break;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = printClose();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    break;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    byte[] _arg0 = data.createByteArray();
                    int _result3 = printPointLine(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    break;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    int _result4 = printFeedPaper(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    break;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _result5 = getPrinterStatus();
                    reply.writeNoException();
                    reply.writeInt(_result5);
                    break;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    String _result6 = getPrinterDriverVersion();
                    reply.writeNoException();
                    reply.writeString(_result6);
                    break;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _result7 = setGrayLevel(_arg03);
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    break;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = getBufferRemainingRows();
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    break;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    String _result9 = getPrinterConfig();
                    reply.writeNoException();
                    reply.writeString(_result9);
                    break;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _result10 = getPrintGrayLevel();
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    break;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = getTotalPrintDistance();
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    break;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    String _result12 = getPrinterSN();
                    reply.writeNoException();
                    reply.writeString(_result12);
                    break;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    PrinterCallback _arg04 = PrinterCallback.Stub.asInterface(data.readStrongBinder());
                    registerPrintCallback(_arg04);
                    reply.writeNoException();
                    break;
                case 14:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterPrintCallback();
                    reply.writeNoException();
                    break;
                case 15:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg05 = data.readInt();
                    int _result13 = setPrintSpeed(_arg05);
                    reply.writeNoException();
                    reply.writeInt(_result13);
                    break;
                case TRANSACTION_setPrintHeatPoint /* 16 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg06 = data.readInt();
                    int _result14 = setPrintHeatPoint(_arg06);
                    reply.writeNoException();
                    reply.writeInt(_result14);
                    break;
                case TRANSACTION_clearBuffer /* 17 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result15 = clearBuffer();
                    reply.writeNoException();
                    reply.writeInt(_result15);
                    break;
                case TRANSACTION_setPrintDotSpeedLevel /* 18 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg07 = data.readInt();
                    int _result16 = setPrintDotSpeedLevel(_arg07);
                    reply.writeNoException();
                    reply.writeInt(_result16);
                    break;
                case TRANSACTION_getPrintDotSpeedLevel /* 19 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _result17 = getPrintDotSpeedLevel();
                    reply.writeNoException();
                    reply.writeInt(_result17);
                    break;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    break;
            }
            return true;
        }

        /* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/print/PrinterOpt$Stub$Proxy.class */
        private static class Proxy implements PrinterOpt {
            private IBinder mRemote;
            public static PrinterOpt sDefaultImpl;

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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int printOpen() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iPrintOpen = Stub.getDefaultImpl().printOpen();
                        _reply.recycle();
                        _data.recycle();
                        return iPrintOpen;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int printClose() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iPrintClose = Stub.getDefaultImpl().printClose();
                        _reply.recycle();
                        _data.recycle();
                        return iPrintClose;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int printPointLine(byte[] pointRowData) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(pointRowData);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iPrintPointLine = Stub.getDefaultImpl().printPointLine(pointRowData);
                        _reply.recycle();
                        _data.recycle();
                        return iPrintPointLine;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int printFeedPaper(int nPixels) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(nPixels);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iPrintFeedPaper = Stub.getDefaultImpl().printFeedPaper(nPixels);
                        _reply.recycle();
                        _data.recycle();
                        return iPrintFeedPaper;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int getPrinterStatus() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printerStatus = Stub.getDefaultImpl().getPrinterStatus();
                        _reply.recycle();
                        _data.recycle();
                        return printerStatus;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public String getPrinterDriverVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String printerDriverVersion = Stub.getDefaultImpl().getPrinterDriverVersion();
                        _reply.recycle();
                        _data.recycle();
                        return printerDriverVersion;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int setGrayLevel(int level) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int grayLevel = Stub.getDefaultImpl().setGrayLevel(level);
                        _reply.recycle();
                        _data.recycle();
                        return grayLevel;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int getBufferRemainingRows() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int bufferRemainingRows = Stub.getDefaultImpl().getBufferRemainingRows();
                        _reply.recycle();
                        _data.recycle();
                        return bufferRemainingRows;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public String getPrinterConfig() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String printerConfig = Stub.getDefaultImpl().getPrinterConfig();
                        _reply.recycle();
                        _data.recycle();
                        return printerConfig;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int getPrintGrayLevel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printGrayLevel = Stub.getDefaultImpl().getPrintGrayLevel();
                        _reply.recycle();
                        _data.recycle();
                        return printGrayLevel;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int getTotalPrintDistance() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int totalPrintDistance = Stub.getDefaultImpl().getTotalPrintDistance();
                        _reply.recycle();
                        _data.recycle();
                        return totalPrintDistance;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public String getPrinterSN() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        String printerSN = Stub.getDefaultImpl().getPrinterSN();
                        _reply.recycle();
                        _data.recycle();
                        return printerSN;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public void registerPrintCallback(PrinterCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerPrintCallback(callback);
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public void unregisterPrintCallback() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterPrintCallback();
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int setPrintSpeed(int speed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(speed);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printSpeed = Stub.getDefaultImpl().setPrintSpeed(speed);
                        _reply.recycle();
                        _data.recycle();
                        return printSpeed;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int setPrintHeatPoint(int pointNum) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(pointNum);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setPrintHeatPoint, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printHeatPoint = Stub.getDefaultImpl().setPrintHeatPoint(pointNum);
                        _reply.recycle();
                        _data.recycle();
                        return printHeatPoint;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int clearBuffer() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_clearBuffer, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int iClearBuffer = Stub.getDefaultImpl().clearBuffer();
                        _reply.recycle();
                        _data.recycle();
                        return iClearBuffer;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int setPrintDotSpeedLevel(int level) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(level);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_setPrintDotSpeedLevel, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printDotSpeedLevel = Stub.getDefaultImpl().setPrintDotSpeedLevel(level);
                        _reply.recycle();
                        _data.recycle();
                        return printDotSpeedLevel;
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

            @Override // com.sunmi.pay.hardware.aidl.print.PrinterOpt
            public int getPrintDotSpeedLevel() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getPrintDotSpeedLevel, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        int printDotSpeedLevel = Stub.getDefaultImpl().getPrintDotSpeedLevel();
                        _reply.recycle();
                        _data.recycle();
                        return printDotSpeedLevel;
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

        public static boolean setDefaultImpl(PrinterOpt impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static PrinterOpt getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
