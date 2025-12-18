package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/AidV2.class */
public class AidV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public byte[] aid;
    public byte[] cvmLmt;
    public byte[] termClssLmt;
    public byte[] termClssOfflineFloorLmt;
    public byte[] termOfflineFloorLmt;
    public byte selFlag;
    public byte targetPer;
    public byte maxTargetPer;
    public byte[] floorLimit;
    public byte randTransSel;
    public byte velocityCheck;
    public byte[] threshold;
    public byte[] TACDenial;
    public byte[] TACOnline;
    public byte[] TACDefault;
    public byte[] AcquierId;
    public byte[] dDOL;
    public byte[] tDOL;
    public byte[] version;
    public byte rMDLen;
    public byte[] riskManData;
    public byte[] merchName;
    public byte[] merchCateCode;
    public byte[] merchId;
    public byte[] termId;
    public byte[] referCurrCode;
    public byte referCurrExp;
    public byte[] referCurrCon;
    public byte clsStatusCheck;
    public byte zeroCheck;
    public byte kernelType;
    public byte paramType;
    public byte[] ttq;
    public byte[] kernelID;
    public byte extSelectSupFlg;
    public static final Parcelable.Creator<AidV2> CREATOR = new Parcelable.Creator<AidV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.AidV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AidV2 createFromParcel(Parcel source) {
            return new AidV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AidV2[] newArray(int size) {
            return new AidV2[size];
        }
    };

    public AidV2() {
        this.cvmLmt = new byte[6];
        this.termClssLmt = new byte[6];
        this.termClssOfflineFloorLmt = new byte[6];
        this.termOfflineFloorLmt = new byte[6];
        this.threshold = new byte[4];
        this.TACDenial = new byte[5];
        this.TACOnline = new byte[5];
        this.TACDefault = new byte[5];
        this.AcquierId = new byte[6];
        this.version = new byte[2];
        this.riskManData = new byte[8];
        this.merchName = new byte[AidlConstants.Security.INJECT_DERIVER_OWF2];
        this.merchCateCode = new byte[2];
        this.merchId = new byte[16];
        this.termId = new byte[8];
        this.referCurrCode = new byte[]{1, 86};
        this.referCurrCon = new byte[4];
        this.ttq = new byte[4];
    }

    protected AidV2(Parcel in) {
        this.cvmLmt = new byte[6];
        this.termClssLmt = new byte[6];
        this.termClssOfflineFloorLmt = new byte[6];
        this.termOfflineFloorLmt = new byte[6];
        this.threshold = new byte[4];
        this.TACDenial = new byte[5];
        this.TACOnline = new byte[5];
        this.TACDefault = new byte[5];
        this.AcquierId = new byte[6];
        this.version = new byte[2];
        this.riskManData = new byte[8];
        this.merchName = new byte[AidlConstants.Security.INJECT_DERIVER_OWF2];
        this.merchCateCode = new byte[2];
        this.merchId = new byte[16];
        this.termId = new byte[8];
        this.referCurrCode = new byte[]{1, 86};
        this.referCurrCon = new byte[4];
        this.ttq = new byte[4];
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.aid = in.createByteArray();
        this.cvmLmt = in.createByteArray();
        this.termClssLmt = in.createByteArray();
        this.termClssOfflineFloorLmt = in.createByteArray();
        this.termOfflineFloorLmt = in.createByteArray();
        this.selFlag = in.readByte();
        this.targetPer = in.readByte();
        this.maxTargetPer = in.readByte();
        this.floorLimit = in.createByteArray();
        this.randTransSel = in.readByte();
        this.velocityCheck = in.readByte();
        this.threshold = in.createByteArray();
        this.TACDenial = in.createByteArray();
        this.TACOnline = in.createByteArray();
        this.TACDefault = in.createByteArray();
        this.AcquierId = in.createByteArray();
        this.dDOL = in.createByteArray();
        this.tDOL = in.createByteArray();
        this.version = in.createByteArray();
        this.rMDLen = in.readByte();
        this.riskManData = in.createByteArray();
        this.merchName = in.createByteArray();
        this.merchCateCode = in.createByteArray();
        this.merchId = in.createByteArray();
        this.termId = in.createByteArray();
        this.referCurrCode = in.createByteArray();
        this.referCurrExp = in.readByte();
        this.referCurrCon = in.createByteArray();
        this.clsStatusCheck = in.readByte();
        this.zeroCheck = in.readByte();
        this.kernelType = in.readByte();
        this.paramType = in.readByte();
        this.ttq = in.createByteArray();
        this.kernelID = in.createByteArray();
        this.extSelectSupFlg = in.readByte();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.aid);
        dest.writeByteArray(this.cvmLmt);
        dest.writeByteArray(this.termClssLmt);
        dest.writeByteArray(this.termClssOfflineFloorLmt);
        dest.writeByteArray(this.termOfflineFloorLmt);
        dest.writeByte(this.selFlag);
        dest.writeByte(this.targetPer);
        dest.writeByte(this.maxTargetPer);
        dest.writeByteArray(this.floorLimit);
        dest.writeByte(this.randTransSel);
        dest.writeByte(this.velocityCheck);
        dest.writeByteArray(this.threshold);
        dest.writeByteArray(this.TACDenial);
        dest.writeByteArray(this.TACOnline);
        dest.writeByteArray(this.TACDefault);
        dest.writeByteArray(this.AcquierId);
        dest.writeByteArray(this.dDOL);
        dest.writeByteArray(this.tDOL);
        dest.writeByteArray(this.version);
        dest.writeByte(this.rMDLen);
        dest.writeByteArray(this.riskManData);
        dest.writeByteArray(this.merchName);
        dest.writeByteArray(this.merchCateCode);
        dest.writeByteArray(this.merchId);
        dest.writeByteArray(this.termId);
        dest.writeByteArray(this.referCurrCode);
        dest.writeByte(this.referCurrExp);
        dest.writeByteArray(this.referCurrCon);
        dest.writeByte(this.clsStatusCheck);
        dest.writeByte(this.zeroCheck);
        dest.writeByte(this.kernelType);
        dest.writeByte(this.paramType);
        dest.writeByteArray(this.ttq);
        dest.writeByteArray(this.kernelID);
        dest.writeByte(this.extSelectSupFlg);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "AidV2{aid=" + bytes2HexString(this.aid) + ", cvmLmt=" + bytes2HexString(this.cvmLmt) + ", termClssLmt=" + bytes2HexString(this.termClssLmt) + ", termClssOfflineFloorLmt=" + bytes2HexString(this.termClssOfflineFloorLmt) + ", termOfflineFloorLmt=" + bytes2HexString(this.termOfflineFloorLmt) + ", selFlag=" + ((int) this.selFlag) + ", targetPer=" + ((int) this.targetPer) + ", maxTargetPer=" + ((int) this.maxTargetPer) + ", floorLimit=" + bytes2HexString(this.floorLimit) + ", randTransSel=" + ((int) this.randTransSel) + ", velocityCheck=" + ((int) this.velocityCheck) + ", threshold=" + bytes2HexString(this.threshold) + ", TACDenial=" + bytes2HexString(this.TACDenial) + ", TACOnline=" + bytes2HexString(this.TACOnline) + ", TACDefault=" + bytes2HexString(this.TACDefault) + ", AcquierId=" + bytes2HexString(this.AcquierId) + ", dDOL=" + bytes2HexString(this.dDOL) + ", tDOL=" + bytes2HexString(this.tDOL) + ", version=" + bytes2HexString(this.version) + ", rMDLen=" + ((int) this.rMDLen) + ", riskManData=" + bytes2HexString(this.riskManData) + ", merchName=" + bytes2HexString(this.merchName) + ", merchCateCode=" + bytes2HexString(this.merchCateCode) + ", merchId=" + bytes2HexString(this.merchId) + ", termId=" + bytes2HexString(this.termId) + ", referCurrCode=" + bytes2HexString(this.referCurrCode) + ", referCurrExp=" + ((int) this.referCurrExp) + ", referCurrCon=" + bytes2HexString(this.referCurrCon) + ", clsStatusCheck=" + ((int) this.clsStatusCheck) + ", zeroCheck=" + ((int) this.zeroCheck) + ", kernelType=" + ((int) this.kernelType) + ", paramType=" + ((int) this.paramType) + ", ttq=" + bytes2HexString(this.ttq) + ", kernelID=" + bytes2HexString(this.kernelID) + ", extSelectSupFlg=" + ((int) this.extSelectSupFlg) + '}';
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
