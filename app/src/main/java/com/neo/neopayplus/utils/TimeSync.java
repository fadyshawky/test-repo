package com.neo.neopayplus.utils;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Server Time Sync Utility
 * 
 * Fetches server time for drift detection and receipt timestamps
 */
public class TimeSync {

    private static final String TAG = Constant.TAG;

    /**
     * Callback interface for time sync
     */
    public interface OnTime {
        void ok(String isoUtc);

        void err(String m);
    }

    /**
     * Sync time from server health endpoint
     * 
     * @param callback Callback to receive server time or error
     */
    public static void sync(OnTime callback) {
        // Use existing PaymentApiService for health check
        // TODO: Add health endpoint to PaymentApiService if not exists
        // For now, we'll use a simple GET request pattern

        // If health endpoint exists in PaymentApiService, use it
        // Otherwise, this is a placeholder
        try {
            // Placeholder - will be replaced when health endpoint is added
            callback.err("Health endpoint not yet implemented");
        } catch (Exception e) {
            LogUtil.e(TAG, "TimeSync error: " + e.getMessage());
            callback.err(e.getMessage());
        }
    }

    /**
     * Get current time as ISO8601 UTC string
     * 
     * @return ISO8601 formatted time string (e.g., "2025-01-15T10:30:45Z")
     */
    public static String nowIso() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        return sdf.format(new Date());
    }

    /**
     * Get current time as local formatted string for receipts
     * 
     * @return Formatted time string (e.g., "2025-01-15 10:30:45")
     */
    public static String nowFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.format(new Date());
    }

    /**
     * Calculate time drift between server and device
     * 
     * @param serverTime ISO8601 server time string
     * @return Drift in seconds (positive = device ahead, negative = device behind)
     */
    public static long calculateDrift(String serverTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            Date server = sdf.parse(serverTime);
            Date device = new Date();
            return (device.getTime() - server.getTime()) / 1000; // seconds
        } catch (Exception e) {
            LogUtil.e(TAG, "TimeSync: Error calculating drift: " + e.getMessage());
            return 0;
        }
    }
}
