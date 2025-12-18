package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/CapkV2.class */
public class CapkV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public byte[] rid;
    public byte index;
    public byte hashInd;
    public byte arithInd;
    public byte[] modul;
    public byte[] exponent;
    public byte[] expDate;
    public byte[] checkSum;
    public static final Parcelable.Creator<CapkV2> CREATOR = new Parcelable.Creator<CapkV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.CapkV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CapkV2 createFromParcel(Parcel source) {
            return new CapkV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CapkV2[] newArray(int size) {
            return new CapkV2[size];
        }
    };

    public CapkV2() {
        this.rid = new byte[5];
        this.expDate = new byte[3];
        this.checkSum = new byte[20];
    }

    protected CapkV2(Parcel in) {
        this.rid = new byte[5];
        this.expDate = new byte[3];
        this.checkSum = new byte[20];
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.rid = in.createByteArray();
        this.index = in.readByte();
        this.hashInd = in.readByte();
        this.arithInd = in.readByte();
        this.modul = in.createByteArray();
        this.exponent = in.createByteArray();
        this.expDate = in.createByteArray();
        this.checkSum = in.createByteArray();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.rid);
        dest.writeByte(this.index);
        dest.writeByte(this.hashInd);
        dest.writeByte(this.arithInd);
        dest.writeByteArray(this.modul);
        dest.writeByteArray(this.exponent);
        dest.writeByteArray(this.expDate);
        dest.writeByteArray(this.checkSum);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "CapkV2{rid=" + bytes2HexString(this.rid) + ", index=" + ((int) this.index) + ", hashInd=" + ((int) this.hashInd) + ", arithInd=" + ((int) this.arithInd) + ", modul=" + bytes2HexString(this.modul) + ", exponent=" + bytes2HexString(this.exponent) + ", expDate=" + bytes2HexString(this.expDate) + ", checkSum=" + bytes2HexString(this.checkSum) + '}';
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
