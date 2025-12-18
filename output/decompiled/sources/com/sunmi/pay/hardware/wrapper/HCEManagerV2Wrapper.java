package com.sunmi.pay.hardware.wrapper;

import android.nfc.NdefMessage;
import android.util.Log;
import com.sunmi.pay.hardware.aidl.AidlConstants;
import com.sunmi.pay.hardware.aidl.AidlErrorCode;
import com.sunmi.pay.hardware.aidlv2.hce.HCEManagerV2;
import java.util.Arrays;

/* loaded from: classes.jar:com/sunmi/pay/hardware/wrapper/HCEManagerV2Wrapper.class */
public class HCEManagerV2Wrapper {
    private static final String TAG = "HCEManagerV2Wrapper";
    private final HCEManagerV2 proxy;

    public HCEManagerV2Wrapper(HCEManagerV2 proxy) {
        this.proxy = proxy;
    }

    public int hceOpen(int cardType) {
        return hceOpen(cardType, null);
    }

    public int hceOpen(int cardType, byte[] param) {
        try {
            if ((cardType != AidlConstants.CardType.NFC.getValue() && cardType != AidlConstants.CardType.IC.getValue()) || (param != null && param.length > 255)) {
                return AidlErrorCode.INVOKING_ERROR_PARAMS.getCode();
            }
            return this.proxy.hceOpen(cardType, param);
        } catch (Exception e) {
            e.printStackTrace();
            return AidlErrorCode.UNKNOWN.getCode();
        }
    }

    public int hceNdefWrite(NdefMessage msg) {
        if (msg == null) {
            return AidlErrorCode.INVOKING_ERROR_PARAMS.getCode();
        }
        return hceWrite(msg.toByteArray());
    }

    public int hceWrite(byte[] msg) {
        if (msg != null) {
            try {
                if (msg.length <= 1024) {
                    return this.proxy.hceNdefWrite(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return AidlErrorCode.UNKNOWN.getCode();
            }
        }
        return AidlErrorCode.INVOKING_ERROR_PARAMS.getCode();
    }

    public NdefMessage hceNdefRead() {
        try {
            byte[] buffer = new byte[2048];
            int len = this.proxy.hceNdefRead(buffer);
            if (len < 0) {
                Log.e(TAG, "hceNdefRead() failed, code:" + len);
                return null;
            }
            byte[] valid = Arrays.copyOf(buffer, len);
            return new NdefMessage(valid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int hceRead(byte[] outData) {
        if (outData != null) {
            try {
                if (outData.length != 0) {
                    byte[] buffer = new byte[2048];
                    int len = this.proxy.hceNdefRead(buffer);
                    if (len < 0) {
                        Log.e("HCE", "hceNdefRead() failed, code:" + len);
                        return len;
                    }
                    byte[] valid = Arrays.copyOf(buffer, len);
                    int copyLen = Math.min(valid.length, outData.length);
                    System.arraycopy(valid, 0, outData, 0, copyLen);
                    return copyLen;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return AidlErrorCode.UNKNOWN.getCode();
            }
        }
        return AidlErrorCode.INVOKING_ERROR_PARAMS.getCode();
    }

    public int hceClose() {
        try {
            return this.proxy.hceClose();
        } catch (Exception e) {
            e.printStackTrace();
            return AidlErrorCode.UNKNOWN.getCode();
        }
    }
}
