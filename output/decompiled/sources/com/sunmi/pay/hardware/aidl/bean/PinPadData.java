package com.sunmi.pay.hardware.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/bean/PinPadData.class */
public class PinPadData implements Parcelable {
    public int numX;
    public int numY;
    public int numH;
    public int numW;
    public int lineW;
    public int cancelX;
    public int cancelY;
    public int cancelH;
    public int cancelW;
    public int rows;
    public int clos;
    public byte[] keyMap;
    public static final Parcelable.Creator<PinPadData> CREATOR = new Parcelable.Creator<PinPadData>() { // from class: com.sunmi.pay.hardware.aidl.bean.PinPadData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadData createFromParcel(Parcel in) {
            return new PinPadData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadData[] newArray(int size) {
            return new PinPadData[size];
        }
    };

    public PinPadData() {
        this.keyMap = new byte[64];
    }

    public int getNumX() {
        return this.numX;
    }

    public void setNumX(int numX) {
        this.numX = numX;
    }

    public int getNumY() {
        return this.numY;
    }

    public void setNumY(int numY) {
        this.numY = numY;
    }

    public int getNumH() {
        return this.numH;
    }

    public void setNumH(int numH) {
        this.numH = numH;
    }

    public int getNumW() {
        return this.numW;
    }

    public void setNumW(int numW) {
        this.numW = numW;
    }

    public int getLineW() {
        return this.lineW;
    }

    public void setLineW(int lineW) {
        this.lineW = lineW;
    }

    public int getCancelX() {
        return this.cancelX;
    }

    public void setCancelX(int cancelX) {
        this.cancelX = cancelX;
    }

    public int getCancelY() {
        return this.cancelY;
    }

    public void setCancelY(int cancelY) {
        this.cancelY = cancelY;
    }

    public int getCancelH() {
        return this.cancelH;
    }

    public void setCancelH(int cancelH) {
        this.cancelH = cancelH;
    }

    public int getCancelW() {
        return this.cancelW;
    }

    public void setCancelW(int cancelW) {
        this.cancelW = cancelW;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getClos() {
        return this.clos;
    }

    public void setClos(int clos) {
        this.clos = clos;
    }

    public byte[] getKeyMap() {
        return this.keyMap;
    }

    public void setKeyMap(byte[] keyMap) {
        this.keyMap = keyMap;
    }

    protected PinPadData(Parcel in) {
        this.keyMap = new byte[64];
        this.numX = in.readInt();
        this.numY = in.readInt();
        this.numH = in.readInt();
        this.numW = in.readInt();
        this.lineW = in.readInt();
        this.cancelX = in.readInt();
        this.cancelY = in.readInt();
        this.cancelH = in.readInt();
        this.cancelW = in.readInt();
        this.rows = in.readInt();
        this.clos = in.readInt();
        this.keyMap = in.createByteArray();
    }

    public boolean check() {
        return (this.numH == 0 || this.numW == 0 || this.cancelH == 0 || this.cancelW == 0) ? false : true;
    }

    public String toString() {
        return "numX:" + this.numX + "\nnumW:" + this.numW + "\nnumH:" + this.numH + "\nnumY:" + this.numY + "\ncancelH:" + this.cancelH + "\ncancelW:" + this.cancelW + "\ncancelX:" + this.cancelX + "\ncancelY:" + this.cancelY;
    }

    public void readFromParcel(Parcel in) {
        this.numX = in.readInt();
        this.numY = in.readInt();
        this.numH = in.readInt();
        this.numW = in.readInt();
        this.lineW = in.readInt();
        this.cancelX = in.readInt();
        this.cancelY = in.readInt();
        this.cancelH = in.readInt();
        this.cancelW = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.numX);
        dest.writeInt(this.numY);
        dest.writeInt(this.numH);
        dest.writeInt(this.numW);
        dest.writeInt(this.lineW);
        dest.writeInt(this.cancelX);
        dest.writeInt(this.cancelY);
        dest.writeInt(this.cancelH);
        dest.writeInt(this.cancelW);
        dest.writeInt(this.rows);
        dest.writeInt(this.clos);
        dest.writeByteArray(this.keyMap);
    }
}
