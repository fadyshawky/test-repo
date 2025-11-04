package com.neo.neopayplus.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.neo.neopayplus.MyApplication;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Transaction Journal Manager
 * 
 * Stores transaction history locally for:
 * - Last transactions screen (auto-fill RRN for reversal)
 * - Offline reversal queue (when host is down)
 * - Transaction history lookup
 */
public class TransactionJournal {
    
    private static final String TAG = Constant.TAG;
    private static final String PREFERENCE_FILE_NAME = "neopayplus_prefs";
    private static final String KEY_TRANSACTION_JOURNAL = "transaction_journal";
    private static final String KEY_LAST_RRN = "last_rrn";
    private static final int MAX_JOURNAL_SIZE = 100; // Keep last 100 transactions
    
    /**
     * Transaction Record
     */
    public static class TransactionRecord {
        public String rrn;               // Retrieval Reference Number
        public String authCode;          // Authorization code
        public String pan;               // Primary Account Number (masked)
        public String amount;            // Transaction amount in smallest currency unit
        public String currencyCode;      // Currency code
        public String transactionType;   // Transaction type (00=sale, 20=reversal)
        public String date;              // Transaction date (YYMMDD)
        public String time;              // Transaction time (HHMMSS)
        public String responseCode;      // ISO 8583 response code (e.g., "00" = approved)
        public String status;            // Transaction status (APPROVED, DECLINED, PENDING)
        public long timestamp;           // System timestamp (milliseconds)
        public boolean isReversal;       // true if this is a reversal transaction
        public String originalRrn;      // Original RRN (for reversals)
        
        public TransactionRecord() {
            this.timestamp = System.currentTimeMillis();
            this.isReversal = false;
        }
        
        @Override
        public String toString() {
            return "TransactionRecord{" +
                    "rrn='" + rrn + '\'' +
                    ", amount='" + amount + '\'' +
                    ", status='" + status + '\'' +
                    ", isReversal=" + isReversal +
                    '}';
        }
    }
    
    /**
     * Save transaction to journal
     */
    public static void saveTransaction(TransactionRecord record) {
        try {
            List<TransactionRecord> journal = loadJournal();
            
            // Add new transaction at the beginning
            journal.add(0, record);
            
            // Keep only last MAX_JOURNAL_SIZE transactions
            if (journal.size() > MAX_JOURNAL_SIZE) {
                journal = journal.subList(0, MAX_JOURNAL_SIZE);
            }
            
            // Save updated journal
            saveJournal(journal);
            
            // Save as last RRN if approved
            if (record.rrn != null && "00".equals(record.responseCode)) {
                saveLastRrn(record.rrn);
            }
            
            LogUtil.e(TAG, "✓ Transaction saved to journal: " + record.rrn);
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error saving transaction to journal: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
        }
    }
    
    /**
     * Get last RRN (for auto-fill in reversal)
     */
    public static String getLastRrn() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            return pref.getString(KEY_LAST_RRN, null);
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading last RRN: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Save last RRN
     */
    private static void saveLastRrn(String rrn) {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().putString(KEY_LAST_RRN, rrn).apply();
        } catch (Exception e) {
            LogUtil.e(TAG, "Error saving last RRN: " + e.getMessage());
        }
    }
    
    /**
     * Get last N transactions
     */
    public static List<TransactionRecord> getLastTransactions(int count) {
        List<TransactionRecord> journal = loadJournal();
        int size = Math.min(count, journal.size());
        return journal.subList(0, size);
    }
    
    /**
     * Get all transactions in journal
     */
    public static List<TransactionRecord> getAllTransactions() {
        return loadJournal();
    }
    
    /**
     * Find transaction by RRN
     */
    public static TransactionRecord findTransactionByRrn(String rrn) {
        if (rrn == null || rrn.isEmpty()) {
            return null;
        }
        
        List<TransactionRecord> journal = loadJournal();
        for (TransactionRecord record : journal) {
            if (rrn.equals(record.rrn)) {
                return record;
            }
        }
        return null;
    }
    
    /**
     * Save pending reversal (for offline queue)
     */
    public static void savePendingReversal(String rrn, String amount, String currencyCode, String reason) {
        try {
            TransactionRecord reversal = new TransactionRecord();
            reversal.rrn = rrn;
            reversal.amount = amount;
            reversal.currencyCode = currencyCode;
            reversal.transactionType = "20"; // Reversal
            reversal.status = "PENDING";
            reversal.isReversal = true;
            reversal.originalRrn = rrn;
            
            // Use current date/time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.US);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.US);
            Date now = new Date();
            reversal.date = dateFormat.format(now);
            reversal.time = timeFormat.format(now);
            
            // Store in pending reversals list
            List<TransactionRecord> pendingReversals = getPendingReversals();
            pendingReversals.add(reversal);
            savePendingReversals(pendingReversals);
            
            LogUtil.e(TAG, "✓ Pending reversal saved: " + rrn);
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error saving pending reversal: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
        }
    }
    
    /**
     * Get pending reversals (for retry when host comes back online)
     */
    public static List<TransactionRecord> getPendingReversals() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            String json = pref.getString("pending_reversals", "[]");
            
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            
            Gson gson = new Gson();
            Type type = new TypeToken<List<TransactionRecord>>(){}.getType();
            List<TransactionRecord> reversals = gson.fromJson(json, type);
            return reversals != null ? reversals : new ArrayList<>();
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading pending reversals: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Remove pending reversal (after successful retry)
     */
    public static void removePendingReversal(String rrn) {
        try {
            List<TransactionRecord> pendingReversals = getPendingReversals();
            pendingReversals.removeIf(r -> rrn.equals(r.rrn));
            savePendingReversals(pendingReversals);
            LogUtil.e(TAG, "✓ Pending reversal removed: " + rrn);
        } catch (Exception e) {
            LogUtil.e(TAG, "Error removing pending reversal: " + e.getMessage());
        }
    }
    
    /**
     * Load journal from SharedPreferences
     */
    private static List<TransactionRecord> loadJournal() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            String json = pref.getString(KEY_TRANSACTION_JOURNAL, "[]");
            
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            
            Gson gson = new Gson();
            Type type = new TypeToken<List<TransactionRecord>>(){}.getType();
            List<TransactionRecord> journal = gson.fromJson(json, type);
            return journal != null ? journal : new ArrayList<>();
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading transaction journal: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Save journal to SharedPreferences
     */
    private static void saveJournal(List<TransactionRecord> journal) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(journal);
            
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().putString(KEY_TRANSACTION_JOURNAL, json).apply();
        } catch (Exception e) {
            LogUtil.e(TAG, "Error saving transaction journal: " + e.getMessage());
        }
    }
    
    /**
     * Save pending reversals to SharedPreferences
     */
    private static void savePendingReversals(List<TransactionRecord> reversals) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(reversals);
            
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().putString("pending_reversals", json).apply();
        } catch (Exception e) {
            LogUtil.e(TAG, "Error saving pending reversals: " + e.getMessage());
        }
    }
    
    /**
     * Clear journal (for testing/admin)
     */
    public static void clearJournal() {
        try {
            SharedPreferences pref = MyApplication.app.getSharedPreferences(
                    PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            pref.edit().remove(KEY_TRANSACTION_JOURNAL).apply();
            pref.edit().remove(KEY_LAST_RRN).apply();
            pref.edit().remove("pending_reversals").apply();
            LogUtil.e(TAG, "✓ Transaction journal cleared");
        } catch (Exception e) {
            LogUtil.e(TAG, "Error clearing journal: " + e.getMessage());
        }
    }
}

