package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/EmvTermParamV2.class */
public class EmvTermParamV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public String ifDsn;
    public String terminalType;
    public String countryCode;
    public boolean forceOnline;
    public boolean getDataPIN;
    public boolean surportPSESel;
    public boolean useTermAIPFlg;
    public boolean termAIP;
    public boolean bypassAllFlg;
    public boolean bypassPin;
    public boolean batchCapture;
    public boolean ectSiFlg;
    public boolean ectSiVal;
    public boolean ectTlFlg;
    public String ectTlVal;
    public String capability;
    public String addCapability;
    public boolean scriptMode;
    public boolean adviceFlag;
    public boolean isSupportSM;
    public boolean isSupportTransLog;
    public boolean isSupportMultiLang;
    public boolean isSupportExceptFile;
    public boolean isSupportAccountSelect;
    public String TTQ;
    public boolean IsReadLogInCard;
    private byte[] reserved;
    public String currencyCode;
    public String currencyExp;
    public String accountType;
    public static final Parcelable.Creator<EmvTermParamV2> CREATOR = new Parcelable.Creator<EmvTermParamV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.EmvTermParamV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EmvTermParamV2 createFromParcel(Parcel in) {
            return new EmvTermParamV2(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EmvTermParamV2[] newArray(int size) {
            return new EmvTermParamV2[size];
        }
    };

    public EmvTermParamV2() {
        this.ifDsn = "3030303030393035";
        this.terminalType = "22";
        this.countryCode = "0156";
        this.forceOnline = false;
        this.getDataPIN = true;
        this.surportPSESel = true;
        this.useTermAIPFlg = true;
        this.termAIP = true;
        this.bypassPin = true;
        this.ectSiFlg = true;
        this.ectSiVal = true;
        this.ectTlFlg = true;
        this.ectTlVal = "100000";
        this.capability = "60F8C8";
        this.addCapability = "0300C00000";
        this.adviceFlag = true;
        this.isSupportSM = true;
        this.isSupportTransLog = true;
        this.isSupportMultiLang = true;
        this.isSupportExceptFile = true;
        this.isSupportAccountSelect = true;
        this.TTQ = "26000080";
        this.reserved = new byte[3];
        this.currencyCode = "0156";
        this.currencyExp = "02";
        this.accountType = "00";
    }

    protected EmvTermParamV2(Parcel in) {
        this.ifDsn = "3030303030393035";
        this.terminalType = "22";
        this.countryCode = "0156";
        this.forceOnline = false;
        this.getDataPIN = true;
        this.surportPSESel = true;
        this.useTermAIPFlg = true;
        this.termAIP = true;
        this.bypassPin = true;
        this.ectSiFlg = true;
        this.ectSiVal = true;
        this.ectTlFlg = true;
        this.ectTlVal = "100000";
        this.capability = "60F8C8";
        this.addCapability = "0300C00000";
        this.adviceFlag = true;
        this.isSupportSM = true;
        this.isSupportTransLog = true;
        this.isSupportMultiLang = true;
        this.isSupportExceptFile = true;
        this.isSupportAccountSelect = true;
        this.TTQ = "26000080";
        this.reserved = new byte[3];
        this.currencyCode = "0156";
        this.currencyExp = "02";
        this.accountType = "00";
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.ifDsn = in.readString();
        this.terminalType = in.readString();
        this.countryCode = in.readString();
        this.forceOnline = in.readByte() != 0;
        this.getDataPIN = in.readByte() != 0;
        this.surportPSESel = in.readByte() != 0;
        this.useTermAIPFlg = in.readByte() != 0;
        this.termAIP = in.readByte() != 0;
        this.bypassAllFlg = in.readByte() != 0;
        this.bypassPin = in.readByte() != 0;
        this.batchCapture = in.readByte() != 0;
        this.ectSiFlg = in.readByte() != 0;
        this.ectSiVal = in.readByte() != 0;
        this.ectTlFlg = in.readByte() != 0;
        this.ectTlVal = in.readString();
        this.capability = in.readString();
        this.addCapability = in.readString();
        this.scriptMode = in.readByte() != 0;
        this.adviceFlag = in.readByte() != 0;
        this.isSupportSM = in.readByte() != 0;
        this.isSupportTransLog = in.readByte() != 0;
        this.isSupportMultiLang = in.readByte() != 0;
        this.isSupportExceptFile = in.readByte() != 0;
        this.isSupportAccountSelect = in.readByte() != 0;
        this.TTQ = in.readString();
        this.IsReadLogInCard = in.readByte() != 0;
        this.reserved = in.createByteArray();
        this.currencyCode = in.readString();
        this.currencyExp = in.readString();
        this.accountType = in.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ifDsn);
        dest.writeString(this.terminalType);
        dest.writeString(this.countryCode);
        dest.writeByte((byte) (this.forceOnline ? 1 : 0));
        dest.writeByte((byte) (this.getDataPIN ? 1 : 0));
        dest.writeByte((byte) (this.surportPSESel ? 1 : 0));
        dest.writeByte((byte) (this.useTermAIPFlg ? 1 : 0));
        dest.writeByte((byte) (this.termAIP ? 1 : 0));
        dest.writeByte((byte) (this.bypassAllFlg ? 1 : 0));
        dest.writeByte((byte) (this.bypassPin ? 1 : 0));
        dest.writeByte((byte) (this.batchCapture ? 1 : 0));
        dest.writeByte((byte) (this.ectSiFlg ? 1 : 0));
        dest.writeByte((byte) (this.ectSiVal ? 1 : 0));
        dest.writeByte((byte) (this.ectTlFlg ? 1 : 0));
        dest.writeString(this.ectTlVal);
        dest.writeString(this.capability);
        dest.writeString(this.addCapability);
        dest.writeByte((byte) (this.scriptMode ? 1 : 0));
        dest.writeByte((byte) (this.adviceFlag ? 1 : 0));
        dest.writeByte((byte) (this.isSupportSM ? 1 : 0));
        dest.writeByte((byte) (this.isSupportTransLog ? 1 : 0));
        dest.writeByte((byte) (this.isSupportMultiLang ? 1 : 0));
        dest.writeByte((byte) (this.isSupportExceptFile ? 1 : 0));
        dest.writeByte((byte) (this.isSupportAccountSelect ? 1 : 0));
        dest.writeString(this.TTQ);
        dest.writeByte((byte) (this.IsReadLogInCard ? 1 : 0));
        dest.writeByteArray(this.reserved);
        dest.writeString(this.currencyCode);
        dest.writeString(this.currencyExp);
        dest.writeString(this.accountType);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "EmvTermParamV2{ifDsn='" + this.ifDsn + "', terminalType='" + this.terminalType + "', countryCode='" + this.countryCode + "', forceOnline=" + this.forceOnline + ", getDataPIN=" + this.getDataPIN + ", surportPSESel=" + this.surportPSESel + ", useTermAIPFlg=" + this.useTermAIPFlg + ", termAIP=" + this.termAIP + ", bypassAllFlg=" + this.bypassAllFlg + ", bypassPin=" + this.bypassPin + ", batchCapture=" + this.batchCapture + ", ectSiFlg=" + this.ectSiFlg + ", ectSiVal=" + this.ectSiVal + ", ectTlFlg=" + this.ectTlFlg + ", ectTlVal='" + this.ectTlVal + "', capability='" + this.capability + "', addCapability='" + this.addCapability + "', scriptMode=" + this.scriptMode + ", adviceFlag=" + this.adviceFlag + ", isSupportSM=" + this.isSupportSM + ", isSupportTransLog=" + this.isSupportTransLog + ", isSupportMultiLang=" + this.isSupportMultiLang + ", isSupportExceptFile=" + this.isSupportExceptFile + ", isSupportAccountSelect=" + this.isSupportAccountSelect + ", TTQ='" + this.TTQ + "', IsReadLogInCard=" + this.IsReadLogInCard + ", reserved=" + bytes2HexString(this.reserved) + ", currencyCode='" + this.currencyCode + "', currencyExp='" + this.currencyExp + "', accountType='" + this.accountType + "'}";
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
