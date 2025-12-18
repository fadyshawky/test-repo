package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/PinPadDataV2Ex.class */
public class PinPadDataV2Ex implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public int numX;
    public int numY;
    public int numH;
    public int numW;
    public int lineW;
    public int cancelX;
    public int cancelY;
    public int cancelH;
    public int cancelW;
    public int enterX;
    public int enterY;
    public int enterH;
    public int enterW;
    public int clearX;
    public int clearY;
    public int clearH;
    public int clearW;
    public int rows;
    public int clos;
    public byte[] keyMap;
    public static final Parcelable.Creator<PinPadDataV2Ex> CREATOR = new Parcelable.Creator<PinPadDataV2Ex>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.PinPadDataV2Ex.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadDataV2Ex createFromParcel(Parcel in) {
            return new PinPadDataV2Ex(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadDataV2Ex[] newArray(int size) {
            return new PinPadDataV2Ex[size];
        }
    };

    public PinPadDataV2Ex() {
        this.keyMap = new byte[64];
    }

    protected PinPadDataV2Ex(Parcel in) {
        this.keyMap = new byte[64];
        readFromParcel(in);
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
        this.enterX = in.readInt();
        this.enterY = in.readInt();
        this.enterH = in.readInt();
        this.enterW = in.readInt();
        this.clearX = in.readInt();
        this.clearY = in.readInt();
        this.clearH = in.readInt();
        this.clearW = in.readInt();
        this.rows = in.readInt();
        this.clos = in.readInt();
        this.keyMap = in.createByteArray();
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
        dest.writeInt(this.enterX);
        dest.writeInt(this.enterY);
        dest.writeInt(this.enterH);
        dest.writeInt(this.enterW);
        dest.writeInt(this.clearX);
        dest.writeInt(this.clearY);
        dest.writeInt(this.clearH);
        dest.writeInt(this.clearW);
        dest.writeInt(this.rows);
        dest.writeInt(this.clos);
        dest.writeByteArray(this.keyMap);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "PinPadDataV2Ex{numX=" + this.numX + ", numY=" + this.numY + ", numH=" + this.numH + ", numW=" + this.numW + ", lineW=" + this.lineW + ", cancelX=" + this.cancelX + ", cancelY=" + this.cancelY + ", cancelH=" + this.cancelH + ", cancelW=" + this.cancelW + ", enterX=" + this.enterX + ", enterY=" + this.enterY + ", enterH=" + this.enterH + ", enterW=" + this.enterW + ", clearX=" + this.clearX + ", clearY=" + this.clearY + ", clearH=" + this.clearH + ", clearW=" + this.clearW + ", rows=" + this.rows + ", clos=" + this.clos + ", keyMap=" + bytes2HexString(this.keyMap) + '}';
    }

    private String bytes2HexString(byte... src) {
        if (src == null || src.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : src) {
            String hex = Integer.toHexString(b & 255);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
}
