package com.neo.neopayplus.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import java.util.Locale;

/**
 * Batch Manager
 * 
 * Manages batch numbers for transactions.
 * - Batch numbers are 6-digit sequential numbers starting from 000001
 * - Format: 6 digits, zero-padded (000001-999999)
 * - Used in settlement, void, and refund operations
 */
public class BatchManager {
    
    private static final String TAG = Constant.TAG;
    private static final String PREFERENCE_FILE_NAME = "neopayplus_prefs";
    private static final String KEY_BATCH_NUMBER = "current_batch_number";
    private static final int MAX_BATCH_NUMBER = 999999;
    
    /**
     * Get current batch number
     * Format: 6 digits, zero-padded (000001-999999)
     * Example: "000001", "000002", "000123"
     */
    public static String getCurrentBatchNumber() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            
            int currentBatch = 0;
            
            // First, check if there's an old string-based batch number that needs migration
            // We need to check this BEFORE trying to get it as integer to avoid ClassCastException
            try {
                String oldBatchString = pref.getString(KEY_BATCH_NUMBER, null);
                if (oldBatchString != null && !oldBatchString.isEmpty()) {
                    // Check if it's in old date-based format (e.g., "251223001" - 9+ digits)
                    if (oldBatchString.length() >= 9 && oldBatchString.matches("\\d{9,}")) {
                        LogUtil.e(TAG, "⚠️ Found old date-based batch number: " + oldBatchString + " - resetting to 000001");
                        // Clear old format and start fresh
                        pref.edit().remove(KEY_BATCH_NUMBER).apply();
                        currentBatch = 0;
                    } else {
                        // Try to parse as integer if it's a valid number
                        try {
                            int parsed = Integer.parseInt(oldBatchString);
                            if (parsed > 0 && parsed <= MAX_BATCH_NUMBER) {
                                currentBatch = parsed;
                                // Migrate to integer storage - remove string first, then save as int
                                pref.edit().remove(KEY_BATCH_NUMBER).apply();
                                pref.edit().putInt(KEY_BATCH_NUMBER, currentBatch).apply();
                                LogUtil.e(TAG, "✓ Migrated batch number from string to int: " + oldBatchString + " -> " + currentBatch);
                            } else {
                                currentBatch = 0;
                            }
                        } catch (NumberFormatException e) {
                            // Not a valid number, reset
                            currentBatch = 0;
                        }
                    }
                } else {
                    // No string value found, try to get as integer
                    try {
                        currentBatch = pref.getInt(KEY_BATCH_NUMBER, 0);
                    } catch (ClassCastException e) {
                        // Key exists but is not an integer, clear it and start fresh
                        LogUtil.e(TAG, "⚠️ Batch number stored in unexpected format, resetting to 000001");
                        pref.edit().remove(KEY_BATCH_NUMBER).apply();
                        currentBatch = 0;
                    }
                }
            } catch (ClassCastException e) {
                // Key exists but is not a string, try to get as integer
                try {
                    currentBatch = pref.getInt(KEY_BATCH_NUMBER, 0);
                } catch (ClassCastException e2) {
                    // Key exists but is neither string nor int, clear it and start fresh
                    LogUtil.e(TAG, "⚠️ Batch number stored in unexpected format, resetting to 000001");
                    pref.edit().remove(KEY_BATCH_NUMBER).apply();
                    currentBatch = 0;
                }
            }
            
            // If no batch number exists, start from 000001
            if (currentBatch == 0) {
                currentBatch = 1;
                pref.edit().putInt(KEY_BATCH_NUMBER, currentBatch).apply();
                LogUtil.e(TAG, "✓ New batch number created: 000001");
            }
            
            // Format as 6-digit string with leading zeros
            String batchNumber = String.format(Locale.US, "%06d", currentBatch);
            LogUtil.e(TAG, "✓ Current batch number: " + batchNumber + " (stored as int: " + currentBatch + ")");
            return batchNumber;
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error getting batch number: " + e.getMessage());
            // Fallback: return 000001
            return "000001";
        }
    }
    
    /**
     * Increment batch number for new batch
     * Called when starting a new batch (e.g., after settlement)
     * Format: 6 digits, zero-padded (000001-999999)
     */
    public static String incrementBatchNumber() {
        try {
            // Use getCurrentBatchNumber() which handles migration, then increment
            String currentBatchStr = getCurrentBatchNumber();
            int currentBatch = Integer.parseInt(currentBatchStr);
            
            // Increment batch number
            currentBatch++;
            
            // Reset to 1 if exceeds maximum
            if (currentBatch > MAX_BATCH_NUMBER) {
                LogUtil.e(TAG, "⚠️ Maximum batch number reached. Resetting to 000001.");
                currentBatch = 1;
            }
            
            // Save incremented batch number
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().putInt(KEY_BATCH_NUMBER, currentBatch).apply();
            
            // Format as 6-digit string with leading zeros
            String newBatchNumber = String.format(Locale.US, "%06d", currentBatch);
            LogUtil.e(TAG, "✓ Batch number incremented to: " + newBatchNumber);
            return newBatchNumber;
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error incrementing batch number: " + e.getMessage());
            return getCurrentBatchNumber();
        }
    }
    
    /**
     * Get batch number for a specific date
     * Note: With sequential batch numbers, this just returns the current batch number
     * (kept for backward compatibility with existing code)
     */
    public static String getBatchNumberForDate(String date) {
        // With sequential batch numbers, we don't use dates anymore
        // Just return current batch number
        return getCurrentBatchNumber();
    }
    
    /**
     * Reset batch number to 000001 (for testing/admin)
     */
    public static void resetBatchNumber() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().putInt(KEY_BATCH_NUMBER, 1).apply();
            LogUtil.e(TAG, "✓ Batch number reset to: 000001");
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error resetting batch number: " + e.getMessage());
        }
    }
    
    /**
     * Get receipt number (increments per transaction in current batch)
     * Format: 6 digits, zero-padded (000001-999999)
     */
    public static String getNextReceiptNumber() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            
            String batchNumber = getCurrentBatchNumber();
            String receiptKey = "receipt_" + batchNumber;
            
            int receiptNumber = pref.getInt(receiptKey, 0);
            receiptNumber++;
            
            // Reset if exceeds max
            if (receiptNumber > 999999) {
                receiptNumber = 1;
            }
            
            pref.edit().putInt(receiptKey, receiptNumber).apply();
            
            String receiptNumberStr = String.format(Locale.US, "%06d", receiptNumber);
            LogUtil.e(TAG, "✓ Receipt number: " + receiptNumberStr + " (Batch: " + batchNumber + ")");
            return receiptNumberStr;
            
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error getting receipt number: " + e.getMessage());
            return "000001";
        }
    }
}
