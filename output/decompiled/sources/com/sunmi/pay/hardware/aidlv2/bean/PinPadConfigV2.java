package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/PinPadConfigV2.class */
public class PinPadConfigV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    private int PinPadType;
    private byte[] Pan;
    private int PinKeyIndex;
    public static final Parcelable.Creator<PinPadConfigV2> CREATOR = new Parcelable.Creator<PinPadConfigV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadConfigV2 createFromParcel(Parcel in) {
            return new PinPadConfigV2(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadConfigV2[] newArray(int size) {
            return new PinPadConfigV2[size];
        }
    };
    private int PinType = 0;
    private boolean isOrderNumKey = false;
    private int MaxInput = 6;
    private int MinInput = 0;
    private int Timeout = 60000;
    private boolean isSupportbypass = true;
    private int PinblockFormat = 0;
    private int AlgorithmType = 0;
    private int KeySystem = 0;

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

    public boolean isSupportbypass() {
        return this.isSupportbypass;
    }

    public void setSupportbypass(boolean supportbypass) {
        this.isSupportbypass = supportbypass;
    }

    public int getPinblockFormat() {
        return this.PinblockFormat;
    }

    public void setPinblockFormat(int pinblockFormat) {
        this.PinblockFormat = pinblockFormat;
    }

    public int getAlgorithmType() {
        return this.AlgorithmType;
    }

    public void setAlgorithmType(int algorithmType) {
        this.AlgorithmType = algorithmType;
    }

    public int getKeySystem() {
        return this.KeySystem;
    }

    public void setKeySystem(int keySystem) {
        this.KeySystem = keySystem;
    }

    public PinPadConfigV2() {
    }

    protected PinPadConfigV2(Parcel in) {
        readFromParcel(in);
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
        this.isSupportbypass = in.readByte() != 0;
        this.PinblockFormat = in.readInt();
        this.AlgorithmType = in.readInt();
        this.KeySystem = in.readInt();
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
        dest.writeByte((byte) (this.isSupportbypass ? 1 : 0));
        dest.writeInt(this.PinblockFormat);
        dest.writeInt(this.AlgorithmType);
        dest.writeInt(this.KeySystem);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "PinPadConfigV2{PinPadType=" + this.PinPadType + ", PinType=" + this.PinType + ", isOrderNumKey=" + this.isOrderNumKey + ", Pan=" + bytes2HexString(this.Pan) + ", PinKeyIndex=" + this.PinKeyIndex + ", MaxInput=" + this.MaxInput + ", MinInput=" + this.MinInput + ", Timeout=" + this.Timeout + ", isSupportbypass=" + this.isSupportbypass + ", PinblockFormat=" + this.PinblockFormat + ", AlgorithmType=" + this.AlgorithmType + ", KeySystem=" + this.KeySystem + '}';
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
