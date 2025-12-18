package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/ApduRecvV2.class */
public class ApduRecvV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public short outlen;
    public byte[] outData;
    public byte swa;
    public byte swb;
    public static final Parcelable.Creator<ApduRecvV2> CREATOR = new Parcelable.Creator<ApduRecvV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.ApduRecvV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduRecvV2 createFromParcel(Parcel source) {
            return new ApduRecvV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduRecvV2[] newArray(int size) {
            return new ApduRecvV2[size];
        }
    };

    public ApduRecvV2() {
    }

    protected ApduRecvV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.outlen = (short) in.readInt();
        this.outData = in.createByteArray();
        this.swa = in.readByte();
        this.swb = in.readByte();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.outlen);
        dest.writeByteArray(this.outData);
        dest.writeByte(this.swa);
        dest.writeByte(this.swb);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "ApduRecvV2{outlen=" + ((int) this.outlen) + ", outData=" + bytes2HexString(this.outData) + ", swa=" + ((int) this.swa) + ", swb=" + ((int) this.swb) + '}';
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
