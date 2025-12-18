package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/RevocListV2.class */
public class RevocListV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public byte[] rid;
    public byte index;
    public byte[] sn;
    public byte[] reserved;
    public static final Parcelable.Creator<RevocListV2> CREATOR = new Parcelable.Creator<RevocListV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.RevocListV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RevocListV2 createFromParcel(Parcel in) {
            return new RevocListV2(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RevocListV2[] newArray(int size) {
            return new RevocListV2[size];
        }
    };

    public RevocListV2() {
        this.rid = new byte[5];
        this.sn = new byte[3];
        this.reserved = new byte[3];
    }

    protected RevocListV2(Parcel in) {
        this.rid = new byte[5];
        this.sn = new byte[3];
        this.reserved = new byte[3];
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.rid = in.createByteArray();
        this.index = in.readByte();
        this.sn = in.createByteArray();
        this.reserved = in.createByteArray();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.rid);
        dest.writeByte(this.index);
        dest.writeByteArray(this.sn);
        dest.writeByteArray(this.reserved);
    }

    public String toString() {
        return "RevocListV2{rid=" + bytes2HexString(this.rid) + ", index=" + ((int) this.index) + ", sn=" + bytes2HexString(this.sn) + ", reserved=" + bytes2HexString(this.reserved) + '}';
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

    public String toTlvString() {
        return "9F0605" + bytes2HexString(this.rid) + "8F01" + bytes2HexString(this.index) + "DF810503" + bytes2HexString(this.sn);
    }
}
