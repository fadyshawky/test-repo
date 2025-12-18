package com.sunmi.pay.hardware.aidlv2;

import android.app.Application;
import com.sunmi.pay.hardware.aidl.AidlErrorCode;
import com.sunmi.paylib.R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: classes.jar:com/sunmi/pay/hardware/aidlv2/AidlErrorCodeV2.class */
public enum AidlErrorCodeV2 {
    AIDL_ERROR(Integer.MIN_VALUE, getString(R.string.unknown));

    private int code;
    private String msg;

    AidlErrorCodeV2(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static AidlErrorCodeV2 valueOf(int errCode) {
        AidlErrorCode error = AidlErrorCode.valueOf(errCode);
        AIDL_ERROR.code = error.getCode();
        AIDL_ERROR.msg = error.getMsg();
        return AIDL_ERROR;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    private static String getString(int id) throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Application app = getApplication();
        return app == null ? "unknown error" : app.getString(id);
    }

    private static Application getApplication() throws IllegalAccessException, NoSuchMethodException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        try {
            Class<?> atCls = Class.forName("android.app.ActivityThread");
            Method method = atCls.getDeclaredMethod("currentActivityThread", new Class[0]);
            method.setAccessible(true);
            Object atObject = method.invoke(null, new Object[0]);
            Method method2 = atCls.getDeclaredMethod("getApplication", new Class[0]);
            method2.setAccessible(true);
            return (Application) method2.invoke(atObject, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
