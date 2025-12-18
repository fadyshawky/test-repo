package com.sunmi.pay.hardware.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/bean/PinPadConfig.class */
public class PinPadConfig implements Parcelable {
    private int PinPadType;
    private int PinType;
    private boolean isOrderNumKey;
    private byte[] Pan;
    private int PinKeyIndex;
    private int MaxInput;
    private int MinInput;
    private int Timeout;
    public static final Parcelable.Creator<PinPadConfig> CREATOR = new Parcelable.Creator<PinPadConfig>() { // from class: com.sunmi.pay.hardware.aidl.bean.PinPadConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadConfig createFromParcel(Parcel in) {
            return new PinPadConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadConfig[] newArray(int size) {
            return new PinPadConfig[size];
        }
    };

    public PinPadConfig() {
        this.PinType = 0;
        this.isOrderNumKey = false;
        this.MaxInput = 6;
        this.MinInput = 0;
        this.Timeout = 60000;
    }

    public int getPinPadType() {
        return this.PinPadType;
    }

    public void setPinPadType(int pinPadType) {
        this.PinPadType = pinPadType;
    }

    public int getPinType() {
        return this.PinType;
    }

    public void setPinType(int pinType) {
        this.PinType = pinType;
    }

    public boolean isOrderNumKey() {
        return this.isOrderNumKey;
    }

    public void setOrderNumKey(boolean orderNumKey) {
        this.isOrderNumKey = orderNumKey;
    }

    public byte[] getPan() {
        return this.Pan;
    }

    public void setPan(byte[] pan) {
        this.Pan = pan;
    }

    public int getPinKeyIndex() {
        return this.PinKeyIndex;
    }

    public void setPinKeyIndex(int pinKeyIndex) {
        this.PinKeyIndex = pinKeyIndex;
    }

    public int getMaxInput() {
        return this.MaxInput;
    }

    public void setMaxInput(int maxInput) {
        this.MaxInput = maxInput;
    }

    public int getMinInput() {
        return this.MinInput;
    }

    public void setMinInput(int minInput) {
        this.MinInput = minInput;
    }

    public int getTimeout() {
        return this.Timeout;
    }

    public void setTimeout(int timeout) {
        this.Timeout = timeout;
    }

    protected PinPadConfig(Parcel in) {
        this.PinType = 0;
        this.isOrderNumKey = false;
        this.MaxInput = 6;
        this.MinInput = 0;
        this.Timeout = 60000;
        this.PinPadType = in.readInt();
        this.PinType = in.readInt();
        this.isOrderNumKey = in.readByte() != 0;
        this.Pan = in.createByteArray();
        this.PinKeyIndex = in.readInt();
        this.MaxInput = in.readInt();
        this.MinInput = in.readInt();
        this.Timeout = in.readInt();
    }

    public void readFromParcel(Parcel in) {
        this.PinPadType = in.readInt();
        this.PinType = in.readInt();
        this.isOrderNumKey = in.readByte() != 0;
        this.Pan = in.createByteArray();
        this.PinKeyIndex = in.readInt();
        this.MaxInput = in.readInt();
        this.MinInput = in.readInt();
        this.Timeout = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.PinPadType);
        dest.writeInt(this.PinType);
        dest.writeByte((byte) (this.isOrderNumKey ? 1 : 0));
        dest.writeByteArray(this.Pan);
        dest.writeInt(this.PinKeyIndex);
        dest.writeInt(this.MaxInput);
        dest.writeInt(this.MinInput);
        dest.writeInt(this.Timeout);
    }
}
