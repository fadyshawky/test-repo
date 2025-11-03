package com.neo.neopayplus.payment;

import android.content.Context;
import android.content.SharedPreferences;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Reversal Queue Store
 * 
 * Manages offline reversal queue using SharedPreferences.
 * Stores failed reversals and retries them automatically when host is available.
 * 
 * FIFO queue behavior: First failed → First retried
 */
public class ReversalQueueStore {
    
    private static final String TAG = Constant.TAG;
    private static final String PREF = "REVERSAL_QUEUE";
    private static final String KEY = "items";
    
    /**
     * Add reversal to queue (when host is down)
     * 
     * @param ctx Context
     * @param rev Reversal JSON object with {terminal_id, merchant_id, rrn, amount, currency, reversal_reason}
     */
    public static void add(Context ctx, JSONObject rev) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            JSONArray arr = new JSONArray(sp.getString(KEY, "[]"));
            arr.put(rev);
            sp.edit().putString(KEY, arr.toString()).apply();
            LogUtil.e(TAG, "✓ Reversal queued offline: " + (rev.has("rrn") ? rev.optString("rrn") : "N/A"));
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error queuing reversal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load all pending reversals
     * 
     * @param ctx Context
     * @return JSONArray of pending reversals (FIFO order)
     */
    public static JSONArray load(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            String jsonStr = sp.getString(KEY, "[]");
            return new JSONArray(jsonStr);
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading reversal queue: " + e.getMessage());
            return new JSONArray();
        }
    }
    
    /**
     * Remove first item from queue (after successful reversal)
     * FIFO: First In, First Out
     * 
     * @param ctx Context
     */
    public static void removeFirst(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            JSONArray arr = new JSONArray(sp.getString(KEY, "[]"));
            if (arr.length() > 0) {
                JSONArray newArr = new JSONArray();
                for (int i = 1; i < arr.length(); i++) {
                    newArr.put(arr.get(i));
                }
                sp.edit().putString(KEY, newArr.toString()).apply();
                LogUtil.e(TAG, "✓ First reversal removed from queue");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error removing reversal from queue: " + e.getMessage());
        }
    }
    
    /**
     * Get queue size
     * 
     * @param ctx Context
     * @return Number of pending reversals
     */
    public static int getQueueSize(Context ctx) {
        try {
            JSONArray arr = load(ctx);
            return arr.length();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Clear queue (for admin/testing)
     * 
     * @param ctx Context
     */
    public static void clear(Context ctx) {
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            sp.edit().remove(KEY).apply();
            LogUtil.e(TAG, "✓ Reversal queue cleared");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error clearing reversal queue: " + e.getMessage());
        }
    }
    
    /**
     * Get first reversal in queue (without removing it)
     * 
     * @param ctx Context
     * @return First JSONObject in queue, or null if empty
     */
    public static JSONObject peekFirst(Context ctx) {
        try {
            JSONArray arr = load(ctx);
            if (arr.length() > 0) {
                return arr.getJSONObject(0);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "Error peeking first reversal: " + e.getMessage());
        }
        return null;
    }
}

