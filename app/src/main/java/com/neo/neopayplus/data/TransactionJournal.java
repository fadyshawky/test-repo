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
    private static final long RETENTION_DAYS = 16; // Keep transactions for 16 days
    private static final long RETENTION_MILLIS = RETENTION_DAYS * 24 * 60 * 60 * 1000L; // 16 days in milliseconds

    /**
     * Transaction Record
     */
    public static class TransactionRecord {
        public String transactionId; // Unique transaction identifier
        public String rrn; // Retrieval Reference Number
        public String authCode; // Authorization code
        public String pan; // Primary Account Number (masked: first 6 + "****" + last 4)
        public String cardholderName; // Cardholder name from EMV tag 5F20
        public String amount; // Transaction amount in smallest currency unit
        public String currencyCode; // Currency code
        public String transactionType; // Transaction type (00=sale, 20=reversal)
        public String entryMode; // Entry mode (IC, CONTACTLESS, MAGNETIC)
        public String aid; // Application Identifier (AID)
        public String cardBrand; // Card brand (VISA, MASTERCARD, MEEZA)
        public String cardType; // Card type (DEBIT, CREDIT)
        public String date; // Transaction date (YYMMDD)
        public String time; // Transaction time (HHMMSS)
        public String responseCode; // ISO 8583 response code (e.g., "00" = approved)
        public String status; // Transaction status (APPROVED, DECLINED, PENDING)
        public final long timestamp; // System timestamp (milliseconds)
        public boolean isReversal; // true if this is a reversal transaction
        public String originalRrn; // Original RRN (for reversals)
        public String batchNumber; // Batch number (YYMMDD + sequence, e.g., "250115001")
        public String receiptNumber; // Receipt number within batch (6 digits, e.g., "000001")
        public boolean isSettled; // true if transaction has been settled (required for refunds)

        public TransactionRecord() {
            this.timestamp = System.currentTimeMillis();
            this.isReversal = false;
            this.isSettled = false;
        }

        @Override
        public String toString() {
            return "TransactionRecord{" +
                    "transactionId='" + transactionId + '\'' +
                    ", rrn='" + rrn + '\'' +
                    ", amount='" + amount + '\'' +
                    ", status='" + status + '\'' +
                    ", isSettled=" + isSettled +
                    ", isReversal=" + isReversal +
                    '}';
        }
    }

    /**
     * Generate unique transaction ID
     * Format: TXN + MMDD (4) + STAN (4) + sequence (2) = TXN + 10 chars total
     * This creates a shorter but still unique transaction ID
     * 
     * @param stan Optional STAN to include in transaction ID. If null, will use current STAN from TxnDb
     */
    public static String generateTransactionId(Integer stan) {
        // Get current date (MMDD format - month and day only)
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd", Locale.US);
        String date = dateFormat.format(new Date());
        
        // Use provided STAN or get current STAN from TxnDb (without incrementing)
        int stanValue = stan != null ? stan : 0;
        if (stanValue == 0) {
            try {
                com.neo.neopayplus.db.TxnDb db = new com.neo.neopayplus.db.TxnDb(MyApplication.app);
                // Get current STAN without incrementing (read-only)
                android.database.sqlite.SQLiteDatabase sqliteDb = db.getReadableDatabase();
                android.database.Cursor c = sqliteDb.rawQuery("SELECT v FROM kv WHERE k='stan'", null);
                if (c.moveToFirst()) {
                    try {
                        stanValue = Integer.parseInt(c.getString(0));
                    } catch (Exception e) {
                        stanValue = (int) (System.currentTimeMillis() % 10000);
                    }
                } else {
                    stanValue = (int) (System.currentTimeMillis() % 10000);
                }
                c.close();
                sqliteDb.close();
            } catch (Exception e) {
                LogUtil.e(TAG, "Failed to get STAN for transaction ID: " + e.getMessage());
                // Use timestamp-based fallback
                stanValue = (int) (System.currentTimeMillis() % 10000);
            }
        }
        
        // Use last 4 digits of STAN (mod 10000 to ensure 4 digits max)
        stanValue = stanValue % 10000;
        
        // Use milliseconds modulo 100 for sequence (0-99)
        int sequence = (int) (System.currentTimeMillis() % 100);
        
        // Format: TXN + MMDD (4) + STAN (4) + sequence (2) = TXN + 10 chars = 13 total
        return String.format(Locale.US, "TXN%s%04d%02d", date, stanValue, sequence);
    }
    
    /**
     * Generate unique transaction ID (overload without STAN parameter)
     */
    public static String generateTransactionId() {
        return generateTransactionId(null);
    }

    /**
     * Save transaction to journal
     * @return The transaction ID (generated or existing)
     */
    public static String saveTransaction(TransactionRecord record) {
        try {
            // Generate transaction ID if not already set
            if (record.transactionId == null || record.transactionId.isEmpty()) {
                record.transactionId = generateTransactionId();
            }
            
            // Assign batch number if not already set
            if (record.batchNumber == null || record.batchNumber.isEmpty()) {
                // For reversals/refunds, try to get batch from original transaction
                if (record.isReversal && record.originalRrn != null) {
                    TransactionRecord originalTx = findTransactionByRrn(record.originalRrn);
                    if (originalTx != null && originalTx.batchNumber != null) {
                        record.batchNumber = originalTx.batchNumber;
                        LogUtil.e(TAG, "✓ Using original batch number for reversal: " + record.batchNumber);
                    } else {
                        record.batchNumber = BatchManager.getCurrentBatchNumber();
                    }
                } else {
                    record.batchNumber = BatchManager.getCurrentBatchNumber();
                }
            }
            
            // Assign receipt number if not already set
            if (record.receiptNumber == null || record.receiptNumber.isEmpty()) {
                record.receiptNumber = BatchManager.getNextReceiptNumber();
            }

            List<TransactionRecord> journal = loadJournal();

            // Remove transactions older than retention period
            journal = removeOldTransactions(journal);

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

            android.util.Log.e(TAG, "✓ Transaction saved to journal: ID=" + record.transactionId + ", RRN=" + record.rrn + ", Status=" + record.status);
            LogUtil.e(TAG, "✓ Transaction saved to journal: ID=" + record.transactionId + ", RRN=" + record.rrn);
            
            // Verify transactionId is not null before returning
            if (record.transactionId == null || record.transactionId.isEmpty()) {
                LogUtil.e(TAG, "❌ WARNING: Transaction saved without transactionId! RRN=" + record.rrn);
                // Generate one now as fallback
                record.transactionId = generateTransactionId();
                // Re-save with transactionId - reload journal and update
                List<TransactionRecord> updatedJournal = loadJournal();
                for (TransactionRecord tx : updatedJournal) {
                    if (tx.rrn != null && tx.rrn.equals(record.rrn) && 
                        (tx.transactionId == null || tx.transactionId.isEmpty())) {
                        tx.transactionId = record.transactionId;
                        break;
                    }
                }
                saveJournal(updatedJournal);
                LogUtil.e(TAG, "✓ Fixed: Generated transactionId=" + record.transactionId + " for RRN=" + record.rrn);
            }
            
            return record.transactionId;
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error saving transaction to journal: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
            return null;
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
     * Get last N transactions (filtered to last 16 days)
     */
    public static List<TransactionRecord> getLastTransactions(int count) {
        List<TransactionRecord> journal = getAllTransactions();
        int size = Math.min(count, journal.size());
        return journal.subList(0, size);
    }

    /**
     * Get all transactions in journal (filtered to last 16 days)
     */
    public static List<TransactionRecord> getAllTransactions() {
        List<TransactionRecord> journal = loadJournal();
        return removeOldTransactions(journal);
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
     * Get transactions by batch number
     * Returns all transactions in the specified batch
     */
    public static List<TransactionRecord> getTransactionsByBatch(String batchNumber) {
        if (batchNumber == null || batchNumber.isEmpty()) {
            return new ArrayList<>();
        }

        List<TransactionRecord> journal = loadJournal();
        List<TransactionRecord> batchTransactions = new ArrayList<>();
        
        for (TransactionRecord record : journal) {
            // Match by exact batch number
            if (batchNumber.equals(record.batchNumber)) {
                batchTransactions.add(record);
            }
        }
        
        LogUtil.e(TAG, "📋 getTransactionsByBatch: Looking for batch=" + batchNumber + ", Found=" + batchTransactions.size() + " transactions");
        if (batchTransactions.size() == 0 && journal.size() > 0) {
            // Debug: show what batch numbers exist
            java.util.Set<String> existingBatches = new java.util.HashSet<>();
            for (TransactionRecord tx : journal) {
                if (tx.batchNumber != null && !tx.batchNumber.isEmpty()) {
                    existingBatches.add(tx.batchNumber);
                }
            }
            LogUtil.e(TAG, "⚠️ No transactions found for batch " + batchNumber + ". Existing batch numbers: " + existingBatches);
        }
        
        return batchTransactions;
    }
    
    /**
     * Get current batch number
     */
    public static String getCurrentBatchNumber() {
        return BatchManager.getCurrentBatchNumber();
    }
    
    /**
     * Find transaction by transaction ID
     * Supports both "TXN123..." and "123..." formats (automatically prepends "TXN" if missing)
     */
    public static TransactionRecord findTransactionById(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            LogUtil.e(TAG, "⚠️ findTransactionById: transactionId is null or empty");
            return null;
        }

        // Normalize transaction ID: if it doesn't start with "TXN", prepend it
        String normalizedId = transactionId.trim().toUpperCase();
        if (!normalizedId.startsWith("TXN")) {
            normalizedId = "TXN" + normalizedId;
        }
        
        android.util.Log.e(TAG, "🔍 Searching for transaction ID: " + normalizedId + " (original input: " + transactionId + ")");
        LogUtil.e(TAG, "🔍 Searching for transaction ID: " + normalizedId + " (original input: " + transactionId + ")");

        List<TransactionRecord> journal = loadJournal();
        android.util.Log.e(TAG, "📋 Loaded journal with " + journal.size() + " transactions");
        LogUtil.e(TAG, "📋 Loaded journal with " + journal.size() + " transactions");
        
        // Debug: Log first few transaction IDs
        int sampleCount = Math.min(5, journal.size());
        for (int i = 0; i < sampleCount; i++) {
            TransactionRecord tx = journal.get(i);
            android.util.Log.e(TAG, "  Sample TX[" + i + "]: ID=" + tx.transactionId + ", RRN=" + tx.rrn + ", Status=" + tx.status + ", Settled=" + tx.isSettled);
            LogUtil.e(TAG, "  Sample TX[" + i + "]: ID=" + tx.transactionId + ", RRN=" + tx.rrn + ", Status=" + tx.status + ", Settled=" + tx.isSettled);
        }
        
        for (TransactionRecord record : journal) {
            if (record.transactionId != null) {
                String recordIdUpper = record.transactionId.toUpperCase();
                if (normalizedId.equals(recordIdUpper)) {
                    LogUtil.e(TAG, "✓ Found transaction: ID=" + record.transactionId + ", RRN=" + record.rrn + ", Status=" + record.status);
                    return record;
                }
            } else {
                LogUtil.e(TAG, "⚠️ Transaction without transactionId found: RRN=" + record.rrn);
            }
        }
        
        LogUtil.e(TAG, "❌ Transaction not found: " + normalizedId);
        return null;
    }

    /**
     * Find voidable transaction by transaction ID
     * Only returns transactions that are:
     * - Approved (status = "APPROVED")
     * - Not settled (isSettled = false) - REQUIRED for voids (opposite of refunds)
     * - Not already refunded (status != "REFUNDED")
     * - Not already voided (status != "VOID")
     * - Not a reversal transaction (isReversal = false)
     * Supports both "TXN123..." and "123..." formats (automatically prepends "TXN" if missing)
     */
    public static TransactionRecord findVoidableTransactionById(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            android.util.Log.e(TAG, "⚠️ findVoidableTransactionById: transactionId is null or empty");
            return null;
        }

        // Normalize transaction ID: if it doesn't start with "TXN", prepend it
        String normalizedId = transactionId.trim().toUpperCase();
        if (!normalizedId.startsWith("TXN")) {
            normalizedId = "TXN" + normalizedId;
        }
        
        android.util.Log.e(TAG, "🔍 Searching for voidable transaction ID: " + normalizedId + " (original input: " + transactionId + ")");

        List<TransactionRecord> journal = loadJournal();
        android.util.Log.e(TAG, "📋 Loaded journal with " + journal.size() + " transactions for void search");
        
        // Debug: Log first few transaction IDs
        int sampleCount = Math.min(5, journal.size());
        for (int i = 0; i < sampleCount; i++) {
            TransactionRecord tx = journal.get(i);
            android.util.Log.e(TAG, "  Sample TX[" + i + "]: ID=" + tx.transactionId + ", RRN=" + tx.rrn + ", Status=" + tx.status + ", Settled=" + tx.isSettled);
        }
        for (TransactionRecord record : journal) {
            if (record.transactionId != null) {
                String recordIdUpper = record.transactionId.toUpperCase();
                if (normalizedId.equals(recordIdUpper)) {
                    android.util.Log.e(TAG, "✓ Found transaction by ID: " + record.transactionId);
                    // Check if transaction is voidable
                    boolean isApproved = "APPROVED".equals(record.status);
                    boolean isNotSettled = !record.isSettled; // Must NOT be settled for void
                    boolean isNotRefunded = !"REFUNDED".equals(record.status);
                    boolean isNotVoided = !"VOID".equals(record.status);
                    boolean isNotReversal = !record.isReversal;
                    
                    android.util.Log.e(TAG, "  Checking voidability: Approved=" + isApproved + ", NotSettled=" + isNotSettled + 
                            ", NotRefunded=" + isNotRefunded + ", NotVoided=" + isNotVoided + ", NotReversal=" + isNotReversal);
                    
                    if (isApproved && isNotSettled && isNotRefunded && isNotVoided && isNotReversal) {
                        android.util.Log.e(TAG, "✓ Transaction is voidable!");
                        return record;
                    } else {
                        // Transaction found but not voidable
                        android.util.Log.e(TAG, "❌ Transaction found but not voidable: ID=" + record.transactionId + 
                                ", Status=" + record.status + 
                                ", IsSettled=" + record.isSettled +
                                ", IsReversal=" + record.isReversal);
                        LogUtil.e(TAG, "Transaction found but not voidable: ID=" + record.transactionId + 
                                ", Status=" + record.status + 
                                ", IsSettled=" + record.isSettled +
                                ", IsReversal=" + record.isReversal);
                        return null;
                    }
                }
            } else {
                android.util.Log.e(TAG, "⚠️ Transaction without transactionId found: RRN=" + record.rrn);
            }
        }
        
        android.util.Log.e(TAG, "❌ Transaction not found: " + normalizedId);
        return null;
    }
    
    /**
     * Find refundable transaction by transaction ID
     * Only returns transactions that are:
     * - Approved (status = "APPROVED")
     * - Settled (isSettled = true) - REQUIRED for refunds
     * - Not already refunded (status != "REFUNDED")
     * - Not a reversal transaction (isReversal = false)
     * Supports both "TXN123..." and "123..." formats (automatically prepends "TXN" if missing)
     */
    public static TransactionRecord findRefundableTransactionById(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            android.util.Log.e(TAG, "⚠️ findRefundableTransactionById: transactionId is null or empty");
            return null;
        }

        // Normalize transaction ID: if it doesn't start with "TXN", prepend it
        String normalizedId = transactionId.trim().toUpperCase();
        if (!normalizedId.startsWith("TXN")) {
            normalizedId = "TXN" + normalizedId;
        }
        
        android.util.Log.e(TAG, "🔍 Searching for refundable transaction ID: " + normalizedId + " (original input: " + transactionId + ")");

        List<TransactionRecord> journal = loadJournal();
        android.util.Log.e(TAG, "📋 Loaded journal with " + journal.size() + " transactions for refund search");
        
        // Debug: Log first few transaction IDs
        int sampleCount = Math.min(5, journal.size());
        for (int i = 0; i < sampleCount; i++) {
            TransactionRecord tx = journal.get(i);
            android.util.Log.e(TAG, "  Sample TX[" + i + "]: ID=" + tx.transactionId + ", RRN=" + tx.rrn + ", Status=" + tx.status + ", Settled=" + tx.isSettled);
        }
        
        for (TransactionRecord record : journal) {
            if (record.transactionId != null) {
                String recordIdUpper = record.transactionId.toUpperCase();
                if (normalizedId.equals(recordIdUpper)) {
                    android.util.Log.e(TAG, "✓ Found transaction by ID: " + record.transactionId);
                    // Check if transaction is refundable
                    boolean isApproved = "APPROVED".equals(record.status);
                    boolean isSettled = record.isSettled;
                    boolean isNotRefunded = !"REFUNDED".equals(record.status);
                    boolean isNotReversal = !record.isReversal;
                    
                    android.util.Log.e(TAG, "  Checking refundability: Approved=" + isApproved + ", Settled=" + isSettled + 
                            ", NotRefunded=" + isNotRefunded + ", NotReversal=" + isNotReversal);
                    
                    if (isApproved && isSettled && isNotRefunded && isNotReversal) {
                        android.util.Log.e(TAG, "✓ Transaction is refundable!");
                        return record;
                    } else {
                        // Transaction found but not refundable
                        android.util.Log.e(TAG, "❌ Transaction found but not refundable: ID=" + record.transactionId + 
                                ", Status=" + record.status + 
                                ", IsSettled=" + record.isSettled +
                                ", IsReversal=" + record.isReversal);
                        LogUtil.e(TAG, "Transaction found but not refundable: ID=" + record.transactionId + 
                                ", Status=" + record.status + 
                                ", IsSettled=" + record.isSettled +
                                ", IsReversal=" + record.isReversal);
                        return null;
                    }
                }
            } else {
                android.util.Log.e(TAG, "⚠️ Transaction without transactionId found: RRN=" + record.rrn);
            }
        }
        
        android.util.Log.e(TAG, "❌ Transaction not found: " + normalizedId);
        return null;
    }
    
    /**
     * Mark transactions as settled by batch number
     * Called after successful settlement batch upload
     * 
     * @param batchNumber Batch number to mark as settled
     * @param acceptedRrns List of RRNs that were accepted in settlement (optional, for validation)
     * @return Number of transactions marked as settled
     */
    public static int markBatchAsSettled(String batchNumber, List<String> acceptedRrns) {
        if (batchNumber == null || batchNumber.isEmpty()) {
            return 0;
        }

        try {
            List<TransactionRecord> journal = loadJournal();
            int settledCount = 0;

            for (TransactionRecord record : journal) {
                if (batchNumber.equals(record.batchNumber) && 
                    "APPROVED".equals(record.status) && 
                    !record.isReversal) {
                    
                    // If acceptedRrns provided, only mark those as settled
                    if (acceptedRrns != null && !acceptedRrns.isEmpty()) {
                        if (record.rrn != null && acceptedRrns.contains(record.rrn)) {
                            record.isSettled = true;
                            settledCount++;
                            LogUtil.e(TAG, "✓ Marked transaction as settled: ID=" + record.transactionId + 
                                    ", RRN=" + record.rrn);
                        }
                    } else {
                        // Mark all approved transactions in batch as settled
                        record.isSettled = true;
                        settledCount++;
                        LogUtil.e(TAG, "✓ Marked transaction as settled: ID=" + record.transactionId + 
                                ", RRN=" + record.rrn);
                    }
                }
            }

            if (settledCount > 0) {
                // Save updated journal
                saveJournal(journal);
                LogUtil.e(TAG, "✓ Marked " + settledCount + " transaction(s) as settled for batch: " + batchNumber);
            }

            return settledCount;
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error marking batch as settled: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
            return 0;
        }
    }

    /**
     * Update transaction status by transaction ID
     * Used to mark original transactions as REFUNDED when they are refunded
     */
    public static boolean updateTransactionStatus(String transactionId, String newStatus) {
        if (transactionId == null || transactionId.isEmpty() || newStatus == null || newStatus.isEmpty()) {
            return false;
        }

        try {
            // Normalize transaction ID
            String normalizedId = transactionId.trim().toUpperCase();
            if (!normalizedId.startsWith("TXN")) {
                normalizedId = "TXN" + normalizedId;
            }

            List<TransactionRecord> journal = loadJournal();
            boolean updated = false;

            for (TransactionRecord record : journal) {
                if (record.transactionId != null && normalizedId.equals(record.transactionId.toUpperCase())) {
                    String oldStatus = record.status;
                    record.status = newStatus;
                    updated = true;
                    LogUtil.e(TAG, "✓ Updated transaction status: ID=" + record.transactionId + 
                            ", Old Status=" + oldStatus + ", New Status=" + newStatus);
                    break;
                }
            }

            if (updated) {
                // Save updated journal
                saveJournal(journal);
                LogUtil.e(TAG, "✓ Transaction status updated to: " + newStatus);
            } else {
                LogUtil.e(TAG, "⚠️ Transaction not found for status update: " + transactionId);
            }

            return updated;
        } catch (Exception e) {
            LogUtil.e(TAG, "❌ Error updating transaction status: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
            return false;
        }
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
            Type type = new TypeToken<List<TransactionRecord>>() {
            }.getType();
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
     * Remove transactions older than retention period (16 days)
     * 
     * @param journal List of transactions to filter
     * @return Filtered list containing only transactions within retention period
     */
    private static List<TransactionRecord> removeOldTransactions(List<TransactionRecord> journal) {
        if (journal == null || journal.isEmpty()) {
            return journal;
        }

        long cutoffTime = System.currentTimeMillis() - RETENTION_MILLIS;
        List<TransactionRecord> filtered = new ArrayList<>();
        int removedCount = 0;

        for (TransactionRecord record : journal) {
            if (record.timestamp >= cutoffTime) {
                filtered.add(record);
            } else {
                removedCount++;
            }
        }

        if (removedCount > 0) {
            LogUtil.e(TAG, "Removed " + removedCount + " transaction(s) older than " + RETENTION_DAYS + " days");
            // Save the filtered journal back to storage
            saveJournal(filtered);
        }

        return filtered;
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
            Type type = new TypeToken<List<TransactionRecord>>() {
            }.getType();
            List<TransactionRecord> journal = gson.fromJson(json, type);
            
            if (journal == null) {
                LogUtil.e(TAG, "⚠️ loadJournal: JSON deserialized to null");
                return new ArrayList<>();
            }
            
            LogUtil.e(TAG, "📋 loadJournal: Loaded " + journal.size() + " transactions from JSON");
            
            // Migration: Ensure all transactions have transactionId
            // This handles old transactions that were saved before transactionId was added
            boolean needsSave = false;
            // Track RRN to transactionId mapping to avoid duplicates
            java.util.Map<String, String> rrnToIdMap = new java.util.HashMap<>();
            
            // First pass: collect existing transactionIds by RRN and check for missing ones
            int missingIdCount = 0;
            for (TransactionRecord record : journal) {
                if (record.transactionId != null && !record.transactionId.isEmpty() && 
                    record.rrn != null && !record.rrn.isEmpty()) {
                    rrnToIdMap.put(record.rrn, record.transactionId);
                } else if (record.transactionId == null || record.transactionId.isEmpty()) {
                    missingIdCount++;
                }
            }
            
            if (missingIdCount > 0) {
                LogUtil.e(TAG, "⚠️ Found " + missingIdCount + " transaction(s) without transactionId");
            }
            
            // Second pass: assign transactionIds to records that don't have one
            for (TransactionRecord record : journal) {
                if (record.transactionId == null || record.transactionId.isEmpty()) {
                    // Try to reuse existing transactionId for same RRN, otherwise generate new one
                    if (record.rrn != null && !record.rrn.isEmpty() && rrnToIdMap.containsKey(record.rrn)) {
                        record.transactionId = rrnToIdMap.get(record.rrn);
                        LogUtil.e(TAG, "✓ Reused transaction ID for RRN: " + record.rrn + ", ID=" + record.transactionId);
                    } else {
                        record.transactionId = generateTransactionId();
                        if (record.rrn != null && !record.rrn.isEmpty()) {
                            rrnToIdMap.put(record.rrn, record.transactionId);
                        }
                        LogUtil.e(TAG, "✓ Generated transaction ID for old transaction: RRN=" + record.rrn + 
                                ", New ID=" + record.transactionId);
                    }
                    needsSave = true;
                }
            }
            
            // Save updated journal if any transactions were migrated
            if (needsSave) {
                LogUtil.e(TAG, "💾 Migrating transaction IDs - saving updated journal");
                saveJournal(journal);
                LogUtil.e(TAG, "✓ Migrated transaction IDs for old transactions");
            }
            
            // Debug: Log sample of loaded transactions
            int sampleCount = Math.min(3, journal.size());
            for (int i = 0; i < sampleCount; i++) {
                TransactionRecord tx = journal.get(i);
                LogUtil.e(TAG, "  Loaded TX[" + i + "]: ID=" + tx.transactionId + ", RRN=" + tx.rrn);
            }
            
            return journal;
        } catch (Exception e) {
            LogUtil.e(TAG, "Error loading transaction journal: " + e.getMessage());
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "TransactionJournal", e);
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
