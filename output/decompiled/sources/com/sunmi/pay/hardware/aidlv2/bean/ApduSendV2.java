package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/ApduSendV2.class */
public class ApduSendV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public byte[] command;
    public short lc;
    public byte[] dataIn;
    public short le;
    public static final Parcelable.Creator<ApduSendV2> CREATOR = new Parcelable.Creator<ApduSendV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.ApduSendV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduSendV2 createFromParcel(Parcel source) {
            return new ApduSendV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduSendV2[] newArray(int size) {
            return new ApduSendV2[size];
        }
    };

    public ApduSendV2() {
    }

    protected ApduSendV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.command = in.createByteArray();
        this.lc = (short) in.readInt();
        this.dataIn = in.createByteArray();
        this.le = (short) in.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.command);
        dest.writeInt(this.lc);
        dest.writeByteArray(this.dataIn);
        dest.writeInt(this.le);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "ApduSendV2{command=" + bytes2HexString(this.command) + ", lc=" + ((int) this.lc) + ", dataIn=" + bytes2HexString(this.dataIn) + ", le=" + ((int) this.le) + '}';
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
