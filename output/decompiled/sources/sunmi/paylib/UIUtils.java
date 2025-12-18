package sunmi.paylib;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.sunmi.pay.hardware.aidl.AidlConstants;

/* loaded from: classes.jar:sunmi/paylib/UIUtils.class */
class UIUtils {
    protected static final int SYSTEM_UI_FLAG_SUNMI_SEC = 8;

    UIUtils() {
    }

    public static void setSunmiSecStatusBar(View view) {
        int systemUiVisibility = view.getSystemUiVisibility();
        view.setSystemUiVisibility(systemUiVisibility | 8);
    }

    public static void banPowerKey(Window window) {
        if (window != null) {
            window.setFlags(4, 4);
        }
    }

    public static void banVolumeKey(Dialog dialog) {
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: sunmi.paylib.UIUtils.1
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog2, int keyCode, KeyEvent event) {
                if (keyCode == 25) {
                    Log.e("onKeyDown", "KEYCODE_VOLUME_DOWN");
                    return true;
                }
                if (keyCode == 24) {
                    Log.e("onKeyDown", "KEYCODE_VOLUME_UP");
                    return true;
                }
                return false;
            }
        });
    }

    public static void screenMonopoly(Window window) {
        banPowerKey(window);
        setSunmiSecStatusBar(window.getDecorView());
        window.addFlags(AidlConstants.Security.INJECT_DERIVER_OWF2);
    }

    public static void screenMonopoly(Dialog dialog) {
        Window window = dialog.getWindow();
        window.addFlags(AidlConstants.Security.INJECT_DERIVER_OWF2);
        banPowerKey(window);
        setSunmiSecStatusBar(window.getDecorView());
        banVolumeKey(dialog);
    }

    public static void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness).floatValue() * 0.003921569f;
        context.getWindow().setAttributes(lp);
    }
}
