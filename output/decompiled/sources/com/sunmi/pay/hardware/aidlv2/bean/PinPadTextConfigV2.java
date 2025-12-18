package com.sunmi.pay.hardware.aidlv2.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/bean/PinPadTextConfigV2.class */
public class PinPadTextConfigV2 implements Parcelable, Serializable {
    private static final long serialVersionUID = -1;
    public String confirm;
    public String inputPin;
    public String inputOfflinePin;
    public String reinputOfflinePinFormat;
    public static final Parcelable.Creator<PinPadTextConfigV2> CREATOR = new Parcelable.Creator<PinPadTextConfigV2>() { // from class: com.sunmi.pay.hardware.aidlv2.bean.PinPadTextConfigV2.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadTextConfigV2 createFromParcel(Parcel source) {
            return new PinPadTextConfigV2(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinPadTextConfigV2[] newArray(int size) {
            return new PinPadTextConfigV2[size];
        }
    };

    public PinPadTextConfigV2() {
    }

    protected PinPadTextConfigV2(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.confirm = in.readString();
        this.inputPin = in.readString();
        this.inputOfflinePin = in.readString();
        this.reinputOfflinePinFormat = in.readString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.confirm);
        dest.writeString(this.inputPin);
        dest.writeString(this.inputOfflinePin);
        dest.writeString(this.reinputOfflinePinFormat);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }
}
