package com.neo.neopayplus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.neo.neopayplus.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Local cache utility class
 */
public final class PreferencesUtil {
    private PreferencesUtil() {
        throw new AssertionError("create instance of PreferencesUtil is prohibited");
    }

    private static final String PREFERENCE_FILE_NAME = "neopayplus_prefs";
    private static final String KEY_PINPAD_MODE = "key_pinpad_mode";

    /**
     * Get PIN pad mode
     */
    public static String getPinPadMode() {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_PINPAD_MODE, "");
    }

    /**
     * Set PIN pad mode
     */
    public static void setPinPadMode(String mode) {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(KEY_PINPAD_MODE, mode).apply();
    }

    /**
     * Convert serializable object to Base64 string
     */
    private static String object2String(Serializable obj) {
        // Create byte output stream
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            // Create object output stream, wrap byte stream
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            // Write object to byte stream
            oos.writeObject(obj);
            // Encode byte stream to Base64 string
            return new String(Base64.encode(bos.toByteArray(), 0));
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError("PreferencesUtil", e);
        } finally {
            IOUtil.close(bos);
            IOUtil.close(oos);
        }
        return null;
    }

    /**
     * Convert Base64 string to serializable object
     */
    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T string2Object(String base64Str) {
        if (TextUtils.isEmpty(base64Str)) {
            return null;
        }
        // Read bytes
        byte[] bytes = Base64.decode(base64Str.getBytes(), 0);
        // Wrap in byte input stream
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            // Wrap in object input stream
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            // Read object
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            com.neo.neopayplus.utils.ErrorHandler.logError("PreferencesUtil", e);
        } finally {
            IOUtil.close(ois);
            IOUtil.close(bis);
        }
        return null;
    }

    /**
     * Store EMV configuration JSON string
     */
    private static final String KEY_EMV_CONFIG = "key_emv_config";
    private static final String KEY_EMV_CONFIG_TIMESTAMP = "key_emv_config_timestamp";

    /**
     * Save EMV configuration JSON to local storage
     */
    public static void saveEmvConfigJson(String json) {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMV_CONFIG, json);
        editor.putLong(KEY_EMV_CONFIG_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Get EMV configuration JSON from local storage
     */
    public static String getEmvConfigJson() {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getString(KEY_EMV_CONFIG, null);
    }

    /**
     * Get EMV configuration timestamp
     */
    public static long getEmvConfigTimestamp() {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getLong(KEY_EMV_CONFIG_TIMESTAMP, 0);
    }

    /**
     * Clear EMV configuration from local storage
     */
    public static void clearEmvConfig() {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_EMV_CONFIG);
        editor.remove(KEY_EMV_CONFIG_TIMESTAMP);
        editor.apply();
    }

    // ==================== TERMINAL CONFIG CACHE ====================

    private static final String PREFIX_TERMINAL_CONFIG = "terminal_config_";

    /**
     * Save terminal configuration value to local cache
     */
    public static void saveTerminalConfig(String key, String value) {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREFIX_TERMINAL_CONFIG + key, value);
        editor.apply();
    }

    /**
     * Get terminal configuration value from local cache
     */
    public static String getTerminalConfig(String key, String defaultValue) {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREFIX_TERMINAL_CONFIG + key, defaultValue);
    }

    /**
     * Clear terminal configuration from local cache
     */
    public static void clearTerminalConfig() {
        SharedPreferences pref = MyApplication.app.getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // Clear all terminal config keys
        editor.remove(PREFIX_TERMINAL_CONFIG + "terminal_id");
        editor.remove(PREFIX_TERMINAL_CONFIG + "merchant_id");
        editor.remove(PREFIX_TERMINAL_CONFIG + "currency_code");
        editor.remove(PREFIX_TERMINAL_CONFIG + "timestamp");
        // Clear ISO socket config
        editor.remove(PREFIX_TERMINAL_CONFIG + "iso_socket_host");
        editor.remove(PREFIX_TERMINAL_CONFIG + "iso_socket_port");
        editor.apply();
    }

}
