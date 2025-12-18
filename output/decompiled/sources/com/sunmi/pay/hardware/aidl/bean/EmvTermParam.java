package com.sunmi.pay.hardware.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidl/bean/EmvTermParam.class */
public class EmvTermParam implements Parcelable {
    public String tag9F1E;
    public String tag9F35;
    public String tag9F1A;
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
    public String tag9F33;
    public String tag9F40;
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
    public static final Parcelable.Creator<EmvTermParam> CREATOR = new Parcelable.Creator<EmvTermParam>() { // from class: com.sunmi.pay.hardware.aidl.bean.EmvTermParam.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EmvTermParam createFromParcel(Parcel in) {
            return new EmvTermParam(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EmvTermParam[] newArray(int size) {
            return new EmvTermParam[size];
        }
    };

    public EmvTermParam() {
        this.tag9F1E = "3030303030393035";
        this.tag9F35 = "22";
        this.tag9F1A = "0156";
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
        this.tag9F33 = "60F8C8";
        this.tag9F40 = "0300C00000";
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

    protected EmvTermParam(Parcel in) {
        this.tag9F1E = "3030303030393035";
        this.tag9F35 = "22";
        this.tag9F1A = "0156";
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
        this.tag9F33 = "60F8C8";
        this.tag9F40 = "0300C00000";
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
        this.tag9F1E = in.readString();
        this.tag9F35 = in.readString();
        this.tag9F1A = in.readString();
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
        this.tag9F33 = in.readString();
        this.tag9F40 = in.readString();
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
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag9F1E);
        dest.writeString(this.tag9F35);
        dest.writeString(this.tag9F1A);
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
        dest.writeString(this.tag9F33);
        dest.writeString(this.tag9F40);
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

    public String toString() {
        return "EmvTermParam{tag9F1E='" + this.tag9F1E + "', tag9F35='" + this.tag9F35 + "', tag9F1A='" + this.tag9F1A + "', forceOnline=" + this.forceOnline + ", getDataPIN=" + this.getDataPIN + ", surportPSESel=" + this.surportPSESel + ", useTermAIPFlg=" + this.useTermAIPFlg + ", termAIP=" + this.termAIP + ", bypassAllFlg=" + this.bypassAllFlg + ", bypassPin=" + this.bypassPin + ", batchCapture=" + this.batchCapture + ", ectSiFlg=" + this.ectSiFlg + ", ectSiVal=" + this.ectSiVal + ", ectTlFlg=" + this.ectTlFlg + ", ectTlVal='" + this.ectTlVal + "', tag9F33='" + this.tag9F33 + "', tag9F40='" + this.tag9F40 + "', scriptMode=" + this.scriptMode + ", adviceFlag=" + this.adviceFlag + ", isSupportSM=" + this.isSupportSM + ", isSupportTransLog=" + this.isSupportTransLog + ", isSupportMultiLang=" + this.isSupportMultiLang + ", isSupportExceptFile=" + this.isSupportExceptFile + ", isSupportAccountSelect=" + this.isSupportAccountSelect + ", TTQ='" + this.TTQ + "', IsReadLogInCard=" + this.IsReadLogInCard + ", reserved=" + Arrays.toString(this.reserved) + ", currencyCode='" + this.currencyCode + "', currencyExp='" + this.currencyExp + "', accountType='" + this.accountType + "'}";
    }
}
