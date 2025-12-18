package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/EMVTransDataV2.class */
public class EMVTransDataV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public String amount;
    public String transType = "00";
    public int flowType = 1;
    public int cardType = 2;
    public static final Parcelable.Creator<EMVTransDataV2> CREATOR = new Parcelable.Creator<EMVTransDataV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.EMVTransDataV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EMVTransDataV2 createFromParcel(Parcel source) {
            return new EMVTransDataV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EMVTransDataV2[] newArray(int size) {
            return new EMVTransDataV2[size];
        }
    };

    public EMVTransDataV2() {
    }

    protected EMVTransDataV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.amount = in.readString();
        this.transType = in.readString();
        this.flowType = in.readInt();
        this.cardType = in.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.amount);
        dest.writeString(this.transType);
        dest.writeInt(this.flowType);
        dest.writeInt(this.cardType);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "EMVTransDataV2{amount='" + this.amount + "', transType='" + this.transType + "', flowType=" + this.flowType + ", cardType=" + this.cardType + '}';
    }
}
