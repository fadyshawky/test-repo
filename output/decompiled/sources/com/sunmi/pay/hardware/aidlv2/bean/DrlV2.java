package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/DrlV2.class */
public class DrlV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public boolean isDefaultLmt;
    public boolean statusCheck;
    public byte zeroCheck;
    public byte[] programID;
    public byte[] cvmLmt;
    public byte[] termClssLmt;
    public byte[] termClssFloorLmt;
    public byte[] termFloorLmt;
    public boolean cvmLmtActivate;
    public boolean termClssLmtActivate;
    public byte termClssFloorLmtActivate;
    public static final Parcelable.Creator<DrlV2> CREATOR = new Parcelable.Creator<DrlV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.DrlV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DrlV2 createFromParcel(Parcel source) {
            return new DrlV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public DrlV2[] newArray(int size) {
            return new DrlV2[size];
        }
    };

    public DrlV2() {
        this.isDefaultLmt = false;
        this.statusCheck = false;
        this.zeroCheck = (byte) 1;
        this.cvmLmt = new byte[6];
        this.termClssLmt = new byte[6];
        this.termClssFloorLmt = new byte[6];
        this.termFloorLmt = new byte[6];
        this.cvmLmtActivate = true;
        this.termClssLmtActivate = false;
        this.termClssFloorLmtActivate = (byte) 1;
    }

    protected DrlV2(Parcel in) {
        this.isDefaultLmt = false;
        this.statusCheck = false;
        this.zeroCheck = (byte) 1;
        this.cvmLmt = new byte[6];
        this.termClssLmt = new byte[6];
        this.termClssFloorLmt = new byte[6];
        this.termFloorLmt = new byte[6];
        this.cvmLmtActivate = true;
        this.termClssLmtActivate = false;
        this.termClssFloorLmtActivate = (byte) 1;
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.isDefaultLmt = in.readByte() != 0;
        this.statusCheck = in.readByte() != 0;
        this.zeroCheck = in.readByte();
        this.programID = in.createByteArray();
        this.cvmLmt = in.createByteArray();
        this.termClssLmt = in.createByteArray();
        this.termClssFloorLmt = in.createByteArray();
        this.termFloorLmt = in.createByteArray();
        this.cvmLmtActivate = in.readByte() != 0;
        this.termClssLmtActivate = in.readByte() != 0;
        this.termClssFloorLmtActivate = in.readByte();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isDefaultLmt ? (byte) 1 : (byte) 0);
        dest.writeByte(this.statusCheck ? (byte) 1 : (byte) 0);
        dest.writeByte(this.zeroCheck);
        dest.writeByteArray(this.programID);
        dest.writeByteArray(this.cvmLmt);
        dest.writeByteArray(this.termClssLmt);
        dest.writeByteArray(this.termClssFloorLmt);
        dest.writeByteArray(this.termFloorLmt);
        dest.writeByte(this.cvmLmtActivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.termClssLmtActivate ? (byte) 1 : (byte) 0);
        dest.writeByte(this.termClssFloorLmtActivate);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "DrlV2{isDefaultLmt=" + this.isDefaultLmt + ", statusCheck=" + this.statusCheck + ", zeroCheck=" + ((int) this.zeroCheck) + ", programID=" + bytes2HexString(this.programID) + ", cvmLmt=" + bytes2HexString(this.cvmLmt) + ", termClssLmt=" + bytes2HexString(this.termClssLmt) + ", termClssOfflineFloorLmt=" + bytes2HexString(this.termClssFloorLmt) + ", termOfflineFloorLmt=" + bytes2HexString(this.termFloorLmt) + ", cvmLmtStatus=" + this.cvmLmtActivate + ", termClssLmtStatus=" + this.termClssLmtActivate + ", termClssOfflineFloorLmtStatus=" + ((int) this.termClssFloorLmtActivate) + '}';
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
