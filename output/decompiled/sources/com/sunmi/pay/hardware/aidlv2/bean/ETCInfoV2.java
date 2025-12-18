package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/ETCInfoV2.class */
public class ETCInfoV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public String deviceNo;
    public String deviceStatus;
    public String cardType;
    public int amount;
    public String licensePlateColor;
    public String licensePlateNo;
    public int signal;
    public static final Parcelable.Creator<ETCInfoV2> CREATOR = new Parcelable.Creator<ETCInfoV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.ETCInfoV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ETCInfoV2 createFromParcel(Parcel source) {
            return new ETCInfoV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ETCInfoV2[] newArray(int size) {
            return new ETCInfoV2[size];
        }
    };

    public ETCInfoV2() {
    }

    protected ETCInfoV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.deviceNo = in.readString();
        this.deviceStatus = in.readString();
        this.cardType = in.readString();
        this.amount = in.readInt();
        this.licensePlateColor = in.readString();
        this.licensePlateNo = in.readString();
        this.signal = in.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.deviceNo);
        dest.writeString(this.deviceStatus);
        dest.writeString(this.cardType);
        dest.writeInt(this.amount);
        dest.writeString(this.licensePlateColor);
        dest.writeString(this.licensePlateNo);
        dest.writeInt(this.signal);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ETCInfoV2)) {
            return false;
        }
        ETCInfoV2 t = (ETCInfoV2) o;
        return TextUtils.equals(this.deviceNo, t.deviceNo);
    }

    public int hashCode() {
        if (TextUtils.isEmpty(this.deviceNo)) {
            return 0;
        }
        return this.deviceNo.hashCode();
    }

    public String toString() {
        return "ETCInfoV2{deviceNo='" + this.deviceNo + "', deviceStatus='" + this.deviceStatus + "', cardType='" + this.cardType + "', amount=" + this.amount + ", licensePlateColor='" + this.licensePlateColor + "', licensePlateNo='" + this.licensePlateNo + "', signal=" + this.signal + '}';
    }
}
