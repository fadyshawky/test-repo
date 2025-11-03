package com.neo.neopayplus.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLite Database for Transaction Journal
 * 
 * Stores:
 * - Transaction journal (STAN, RRN, EMV data, amounts, etc.)
 * - Pending reversals (FIFO queue)
 * - STAN counter (rolls 1..999999)
 */
public class TxnDb extends SQLiteOpenHelper {
    
    private static final String TAG = Constant.TAG;
    private static final String DB_NAME = "pos_journal.db";
    private static final int DB_VER = 1;
    
    public TxnDb(Context c) {
        super(c, DB_NAME, null, DB_VER);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Transaction journal table
        db.execSQL("CREATE TABLE IF NOT EXISTS journal (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stan INTEGER," +
                "rrn TEXT," +
                "amount_minor INTEGER," +
                "currency TEXT," +
                "pan_masked TEXT," +
                "ksn TEXT," +
                "entry_mode TEXT," +
                "aid TEXT," +
                "tsi TEXT," +
                "tvr TEXT," +
                "resp_code TEXT," +
                "auth_code TEXT," +
                "datetime TEXT," +
                "payload_json TEXT)");
        
        // Pending reversals queue
        db.execSQL("CREATE TABLE IF NOT EXISTS reversals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "stan INTEGER," +
                "rrn TEXT," +
                "amount_minor INTEGER," +
                "currency TEXT," +
                "reason TEXT," +
                "created_at TEXT," +
                "payload_json TEXT)");
        
        // Key-value store (for STAN counter)
        db.execSQL("CREATE TABLE IF NOT EXISTS kv (k TEXT PRIMARY KEY, v TEXT)");
        db.execSQL("INSERT OR IGNORE INTO kv(k,v) VALUES('stan','1')");
        
        LogUtil.e(TAG, "TxnDb: Database created");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Future migrations here
    }
    
    /**
     * Get next STAN (System Trace Audit Number)
     * Rolls 1..999999
     * Thread-safe
     */
    public synchronized int nextStan() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT v FROM kv WHERE k='stan'", null);
        int stan = 1;
        if (c.moveToFirst()) {
            try {
                stan = Integer.parseInt(c.getString(0));
            } catch (Exception e) {
                stan = 1;
            }
        }
        c.close();
        
        int newStan = (stan % 999999) + 1;
        
        ContentValues cv = new ContentValues();
        cv.put("v", String.valueOf(newStan));
        db.update("kv", cv, "k='stan'", null);
        db.close();
        
        LogUtil.e(TAG, "TxnDb: Next STAN = " + stan);
        return stan;
    }
    
    /**
     * Insert transaction into journal
     */
    public long insertJournal(Map<String, Object> m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        put(cv, "stan", m.get("stan"));
        put(cv, "rrn", m.get("rrn"));
        put(cv, "amount_minor", m.get("amount_minor"));
        put(cv, "currency", m.get("currency"));
        put(cv, "pan_masked", m.get("pan_masked"));
        put(cv, "ksn", m.get("ksn"));
        put(cv, "entry_mode", m.get("entry_mode"));
        put(cv, "aid", m.get("aid"));
        put(cv, "tsi", m.get("tsi"));
        put(cv, "tvr", m.get("tvr"));
        put(cv, "resp_code", m.get("resp_code"));
        put(cv, "auth_code", m.get("auth_code"));
        put(cv, "datetime", m.get("datetime"));
        
        // Store full payload as JSON for debugging
        try {
            cv.put("payload_json", new JSONObject(m).toString());
        } catch (Exception ignore) {
            // Ignore JSON errors
        }
        
        long id = db.insert("journal", null, cv);
        db.close();
        
        LogUtil.e(TAG, "TxnDb: Journal entry inserted, id=" + id);
        return id;
    }
    
    /**
     * Enqueue a reversal for later retry
     */
    public long enqueueReversal(Map<String, Object> m) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        
        put(cv, "stan", m.get("stan"));
        put(cv, "rrn", m.get("rrn"));
        put(cv, "amount_minor", m.get("amount_minor"));
        put(cv, "currency", m.get("currency"));
        put(cv, "reason", m.get("reason"));
        put(cv, "created_at", m.get("created_at"));
        
        // Store full payload as JSON
        try {
            cv.put("payload_json", new JSONObject(m).toString());
        } catch (Exception ignore) {
            // Ignore JSON errors
        }
        
        long id = db.insert("reversals", null, cv);
        db.close();
        
        LogUtil.e(TAG, "TxnDb: Reversal enqueued, id=" + id);
        return id;
    }
    
    /**
     * Get pending reversals (FIFO order)
     */
    public List<Map<String, Object>> pendingReversals(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, stan, rrn, amount_minor, currency, reason, created_at, payload_json FROM reversals ORDER BY id ASC LIMIT " + limit, null);
        
        List<Map<String, Object>> list = new ArrayList<>();
        while (c.moveToNext()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getLong(0));
            m.put("stan", c.getInt(1));
            m.put("rrn", c.getString(2));
            m.put("amount_minor", c.getInt(3));
            m.put("currency", c.getString(4));
            m.put("reason", c.getString(5));
            m.put("created_at", c.getString(6));
            m.put("payload_json", c.getString(7));
            list.add(m);
        }
        c.close();
        db.close();
        
        return list;
    }
    
    /**
     * Delete a reversal (after successful retry)
     */
    public void deleteReversal(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("reversals", "id=?", new String[]{String.valueOf(id)});
        db.close();
        
        LogUtil.e(TAG, "TxnDb: Reversal deleted, id=" + id);
    }
    
    /**
     * Get last N journal entries (for transaction history)
     */
    public List<Map<String, Object>> getLastTransactions(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, stan, rrn, amount_minor, currency, pan_masked, datetime, resp_code FROM journal ORDER BY id DESC LIMIT " + limit, null);
        
        List<Map<String, Object>> list = new ArrayList<>();
        while (c.moveToNext()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getLong(0));
            m.put("stan", c.getInt(1));
            m.put("rrn", c.getString(2));
            m.put("amount_minor", c.getInt(3));
            m.put("currency", c.getString(4));
            m.put("pan_masked", c.getString(5));
            m.put("datetime", c.getString(6));
            m.put("resp_code", c.getString(7));
            list.add(m);
        }
        c.close();
        db.close();
        
        return list;
    }
    
    /**
     * Helper to put values into ContentValues
     */
    private void put(ContentValues cv, String k, Object v) {
        if (v == null) return;
        if (v instanceof Integer) {
            cv.put(k, (Integer) v);
        } else if (v instanceof Long) {
            cv.put(k, (Long) v);
        } else {
            cv.put(k, String.valueOf(v));
        }
    }
}

