package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/EMVCandidateV2.class */
public class EMVCandidateV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public short index;
    public String aid;
    public String appPreName;
    public String appLabel;
    public String issDiscrData;
    public byte priority;
    public String appName;
    public byte kernelType;
    public static final Parcelable.Creator<EMVCandidateV2> CREATOR = new Parcelable.Creator<EMVCandidateV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.EMVCandidateV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EMVCandidateV2 createFromParcel(Parcel source) {
            return new EMVCandidateV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EMVCandidateV2[] newArray(int size) {
            return new EMVCandidateV2[size];
        }
    };

    public EMVCandidateV2() {
    }

    protected EMVCandidateV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.index = (short) in.readInt();
        this.aid = in.readString();
        this.appPreName = in.readString();
        this.appLabel = in.readString();
        this.issDiscrData = in.readString();
        this.priority = in.readByte();
        this.appName = in.readString();
        this.kernelType = in.readByte();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.aid);
        dest.writeString(this.appPreName);
        dest.writeString(this.appLabel);
        dest.writeString(this.issDiscrData);
        dest.writeByte(this.priority);
        dest.writeString(this.appName);
        dest.writeByte(this.kernelType);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "EMVCandidateV2{index=" + ((int) this.index) + ", aid='" + this.aid + "', appPreName='" + this.appPreName + "', appLabel='" + this.appLabel + "', issDiscrData='" + this.issDiscrData + "', priority=" + ((int) this.priority) + ", appName='" + this.appName + "', kernelType=" + ((int) this.kernelType) + '}';
    }
}
