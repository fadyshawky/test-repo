package com.sunmi.pay.hardware.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/bean/TransData.class */
public class TransData implements Parcelable {
    public String amount;
    public String transType;
    public int isForceOnline;
    public static final Parcelable.Creator<TransData> CREATOR = new Parcelable.Creator<TransData>() { // from class: com.sunmi.pay.hardware.aidl.bean.TransData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TransData createFromParcel(Parcel in) {
            return new TransData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TransData[] newArray(int size) {
            return new TransData[size];
        }
    };

    protected TransData(Parcel in) {
        this.transType = "00";
        this.amount = in.readString();
        this.transType = in.readString();
        this.isForceOnline = in.readInt();
    }

    public TransData() {
        this.transType = "00";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.amount);
        dest.writeString(this.transType);
        dest.writeInt(this.isForceOnline);
    }
}
