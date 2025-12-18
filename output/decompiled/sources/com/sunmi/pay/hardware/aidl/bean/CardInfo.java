package com.sunmi.pay.hardware.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/bean/CardInfo.class */
public class CardInfo implements Parcelable {
    public int cardType;
    public String cardNo;
    public String track1;
    public String track2;
    public String track3;
    public String expireDate;
    public String serviceCode;
    public String countryCode;
    public boolean isCardSerialNo;
    public String cardSerialNo;
    public String uuid;
    public String atr;
    public static final Parcelable.Creator<CardInfo> CREATOR = new Parcelable.Creator<CardInfo>() { // from class: com.sunmi.pay.hardware.aidl.bean.CardInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CardInfo createFromParcel(Parcel in) {
            return new CardInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };

    public CardInfo() {
    }

    protected CardInfo(Parcel in) {
        this.cardType = in.readInt();
        this.cardNo = in.readString();
        this.track1 = in.readString();
        this.track2 = in.readString();
        this.track3 = in.readString();
        this.expireDate = in.readString();
        this.serviceCode = in.readString();
        this.countryCode = in.readString();
        this.uuid = in.readString();
        this.atr = in.readString();
        this.cardSerialNo = in.readString();
        this.isCardSerialNo = in.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cardType);
        dest.writeString(this.cardNo);
        dest.writeString(this.track1);
        dest.writeString(this.track2);
        dest.writeString(this.track3);
        dest.writeString(this.expireDate);
        dest.writeString(this.serviceCode);
        dest.writeString(this.countryCode);
        dest.writeString(this.uuid);
        dest.writeString(this.atr);
        dest.writeString(this.cardSerialNo);
        byte b = (byte) (this.isCardSerialNo ? 1 : 0);
        dest.writeByte(b);
    }

    public void readFromParcel(Parcel in) {
        this.cardType = in.readInt();
        this.cardNo = in.readString();
        this.track1 = in.readString();
        this.track2 = in.readString();
        this.track3 = in.readString();
        this.expireDate = in.readString();
        this.serviceCode = in.readString();
        this.countryCode = in.readString();
        this.uuid = in.readString();
        this.atr = in.readString();
        this.cardSerialNo = in.readString();
        this.isCardSerialNo = in.readByte() != 0;
    }

    public String toString() {
        return "CardInfo{cardType=" + this.cardType + ", cardNo='" + this.cardNo + "', track1='" + this.track1 + "', track2='" + this.track2 + "', track3='" + this.track3 + "', expireDate='" + this.expireDate + "', serviceCode='" + this.serviceCode + "', countryCode='" + this.countryCode + "', isCardSerialNo=" + this.isCardSerialNo + ", cardSerialNo='" + this.cardSerialNo + "', uuid='" + this.uuid + "', atr='" + this.atr + "'}";
    }
}
