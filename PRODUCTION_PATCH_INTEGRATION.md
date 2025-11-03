# Production Patch Set Integration - Status & Next Steps

## ✅ Completed

### Core Infrastructure Files

1. **`db/TxnDb.java`** ✅

   - SQLite database for transaction journal
   - STAN counter (1..999999 rollover)
   - Transaction persistence (RRN, amount, EMV data, response codes)
   - Reversal queue (FIFO)
   - Thread-safe STAN generation

2. **`utils/EntryModeUtil.java`** ✅

   - ISO8583 DE22 calculation
   - Supports: ICC (chip), CTLS (contactless), magnetic stripe fallback
   - PIN entered vs no PIN variants

3. **`utils/TimeSync.java`** ✅

   - Server time sync utility
   - ISO8601 formatting
   - Time drift calculation
   - ⚠️ **Needs**: Health endpoint implementation in PaymentApiService

4. **`security/TamperGuard.java`** ✅

   - Polling-based tamper detection
   - Key zeroization on tamper
   - ⚠️ **Needs**: SDK tamper detection method verification

5. **`payment/ReversalWorker.java`** ✅
   - Background retry of pending reversals
   - FIFO queue processing
   - Auto-cleanup on success

### Integration

6. **`MyApplication.java`** ✅
   - Background operations thread started
   - TamperGuard started after PaySDK connects
   - ReversalWorker + TimeSync loop (30-second intervals)

---

## ⏳ Pending Integration

### 1. **ProcessingActivity.java** — STAN + DE22 + KSN + Journal

**Add imports:**

```java
import com.neo.neopayplus.db.TxnDb;
import com.neo.neopayplus.utils.EntryModeUtil;
import com.neo.neopayplus.utils.TimeSync;
import com.neo.neopayplus.security.KeyManagerPOS;
```

**Add fields:**

```java
private int currentStan = 0;
private boolean pinEnteredThisTxn = false;
private boolean fallbackUsed = false;
```

**Before EMV start (in `startRealPaymentProcess()`):**

```java
// Generate STAN
TxnDb db = new TxnDb(this);
currentStan = db.nextStan();
LogUtil.e(Constant.TAG, "Generated STAN: " + currentStan);
```

**In `onRequestShowPinPad()`:**

```java
pinEnteredThisTxn = true; // Mark that PIN will be collected
```

**KSN extraction helper:**

```java
private String getKsnForActiveSlot() {
    try {
        SecurityOptV2 sec = MyApplication.app.securityOptV2;
        if (sec == null) return "";

        // If using DUKPT, get current KSN
        byte[] ksn = new byte[10];
        int result = sec.dukptCurrentKSN(1100, ksn); // DUKPT key index
        if (result == 0) {
            return com.neo.neopayplus.utils.ByteUtil.bytes2HexStr(ksn);
        }

        // If using session TPK, get KSN from KeyManagerPOS
        // TODO: Verify if KeyManagerPOS exposes KSN
        return "";
    } catch (Throwable t) {
        LogUtil.e(Constant.TAG, "Error getting KSN: " + t.getMessage());
        return "";
    }
}
```

**Extract EMV tags (after TLV read):**

```java
// Extract AID, TSI, TVR from field55 or EMV kernel
String field55 = ...; // Your existing field55 extraction
String aid = TLVUtil.getTagValue(field55, "84"); // AID
String tsi = TLVUtil.getTagValue(field55, "9B"); // TSI
String tvr = TLVUtil.getTagValue(field55, "95"); // TVR
```

**Build DE22:**

```java
String entryMode = EntryModeUtil.de22(mCardType, pinEnteredThisTxn, fallbackUsed);
```

**In authorization request (add to JSON):**

```java
b.put("stan", currentStan);
b.put("entry_mode", entryMode);
String ksn = getKsnForActiveSlot();
if (!ksn.isEmpty()) {
    b.put("ksn", ksn);
}
```

**Journal persistence (on APPROVED/DECLINED):**

```java
private void saveJournal(String rrn, String respCode, String authCode,
                         String entryMode, String aid, String tsi, String tvr) {
    try {
        TxnDb db = new TxnDb(this);
        Map<String, Object> rec = new HashMap<>();
        rec.put("stan", currentStan);
        rec.put("rrn", rrn != null ? rrn : "");
        rec.put("amount_minor", Integer.parseInt(mAmount));
        rec.put("currency", com.neo.neopayplus.config.PaymentConfig.getCurrencyCode());
        rec.put("pan_masked", maskCardNumber(mCardNo));
        rec.put("ksn", getKsnForActiveSlot());
        rec.put("entry_mode", entryMode);
        rec.put("aid", aid != null ? aid : "");
        rec.put("tsi", tsi != null ? tsi : "");
        rec.put("tvr", tvr != null ? tvr : "");
        rec.put("resp_code", respCode != null ? respCode : "");
        rec.put("auth_code", authCode != null ? authCode : "");
        rec.put("datetime", TimeSync.nowFormatted());

        db.insertJournal(rec);
        LogUtil.e(Constant.TAG, "✓ Journal entry saved - STAN: " + currentStan);
    } catch (Exception e) {
        LogUtil.e(Constant.TAG, "Error saving journal: " + e.getMessage());
    }
}
```

**Reversal enqueue (on host timeout/error):**

```java
private void enqueueReversal(String rrn, String reason) {
    try {
        TxnDb db = new TxnDb(this);
        Map<String, Object> r = new HashMap<>();
        r.put("stan", currentStan);
        r.put("rrn", rrn != null ? rrn : "");
        r.put("amount_minor", Integer.parseInt(mAmount));
        r.put("currency", com.neo.neopayplus.config.PaymentConfig.getCurrencyCode());
        r.put("reason", reason);
        r.put("created_at", TimeSync.nowIso());

        db.enqueueReversal(r);
        LogUtil.e(Constant.TAG, "✓ Reversal enqueued - STAN: " + currentStan);
    } catch (Exception e) {
        LogUtil.e(Constant.TAG, "Error enqueueing reversal: " + e.getMessage());
    }
}
```

**Reset per-transaction flags:**

```java
// At end of transaction (success or failure)
pinEnteredThisTxn = false;
fallbackUsed = false;
```

---

### 2. **ReceiptTemplate.java** — EMV Meta Fields

**Create or update `ReceiptTemplate.java`:**

```java
package com.neo.neopayplus.utils;

/**
 * Receipt Template Builder
 */
public class ReceiptTemplate {
    public String merchantName, merchantId, terminalId;
    public String rrn, datetime, amount, panMasked, response, authCode;
    public String stan, entryMode, aid, tsi, tvr; // EMV meta fields

    public ReceiptTemplate withMerchant(String merchantName, String merchantId, String terminalId) {
        this.merchantName = merchantName;
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        return this;
    }

    public ReceiptTemplate withTxn(String rrn, String datetime, String amount,
                                    String panMasked, String response, String authCode) {
        this.rrn = rrn;
        this.datetime = datetime;
        this.amount = amount;
        this.panMasked = panMasked;
        this.response = response;
        this.authCode = authCode;
        return this;
    }

    public ReceiptTemplate withEmvMeta(String stan, String entryMode, String aid,
                                        String tsi, String tvr) {
        this.stan = stan;
        this.entryMode = entryMode;
        this.aid = aid;
        this.tsi = tsi;
        this.tvr = tvr;
        return this;
    }

    // ... other builder methods ...
}
```

---

### 3. **Receipt Printing** — Add EMV Fields

**In your receipt printing code:**

```java
if (template.stan != null) {
    printer.printText("STAN: " + template.stan + "\n", null);
}
if (template.entryMode != null) {
    printer.printText("Entry: " + template.entryMode + "\n", null);
}
if (template.aid != null) {
    printer.printText("AID: " + template.aid + "\n", null);
}
if (template.tsi != null) {
    printer.printText("TSI: " + template.tsi + "\n", null);
}
if (template.tvr != null) {
    printer.printText("TVR: " + template.tvr + "\n", null);
}
```

---

## 🔧 SDK Method Verification Needed

### 1. **TamperGuard.checkTamperEvent()**

- **Current**: Placeholder using `sec.getSecStatus()`
- **Action**: Verify actual tamper detection method in Sunmi PayLib 2.0.32
- **Options**: `hasTamperEvent()`, `checkTamperStatus()`, or bit mask in `getSecStatus()`

### 2. **TimeSync.sync()**

- **Current**: Placeholder
- **Action**: Add health endpoint to PaymentApiService or use existing endpoint
- **Endpoint**: `GET /v1/health` returning `{time: "2025-01-15T10:30:45Z"}`

### 3. **ProcessingActivity.getKsnForActiveSlot()**

- **Current**: Using `dukptCurrentKSN()` for DUKPT keys
- **Action**: Verify if session TPK (KeyManagerPOS) exposes KSN
- **Alternative**: If using MKSK, KSN may not be applicable

---

## 📋 Testing Checklist

- [ ] STAN generation (1..999999 rollover)
- [ ] DE22 calculation (chip/contactless/fallback + PIN)
- [ ] KSN extraction (DUKPT or session TPK)
- [ ] Journal persistence (transaction records)
- [ ] Reversal queue (offline reversals)
- [ ] Receipt printing (EMV meta fields)
- [ ] TamperGuard (key zeroization)
- [ ] TimeSync (server time drift detection)
- [ ] ReversalWorker (background retry)

---

## 🚀 Next Steps

1. **Update ProcessingActivity** with STAN + DE22 + KSN + Journal
2. **Create/Update ReceiptTemplate** with EMV meta fields
3. **Verify SDK methods** (tamper detection, time sync endpoint)
4. **Test end-to-end** (transaction → journal → receipt → reversal)

---

**Status**: ⚠️ **Core infrastructure complete, integration pending**  
**Files Created**: ✅ 5/5  
**Integration**: ⏳ 3/3 pending
