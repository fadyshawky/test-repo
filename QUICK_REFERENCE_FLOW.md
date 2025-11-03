# NeoPayPlus - Quick Reference Flow Diagram

## 🎯 Transaction Flow Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    USER INTERACTION                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  MAIN ACTIVITY (UI)                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                │
│  │  Payment │  │ Reversal │  │Settlement│                │
│  └──────────┘  └──────────┘  └──────────┘                │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              PAYMENT/PROCESSING ACTIVITY                     │
│                                                              │
│  1. Initialize EMV Process                                  │
│  2. Detect Card (IC/NFC)                                     │
│  3. AID Selection                                            │
│  4. Certificate Verification                                 │
│  5. CVM Processing (PIN Entry)                              │
│  6. Extract Field 55 (EMV Data)                             │
│  7. Build ISO8583 Fields                                     │
│  8. Backend Authorization                                    │
│  9. EMV Completion                                           │
│  10. Print Receipt                                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              SUNMI P2 PAYMENT SDK                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │  EMVOptV2    │  │ PinPadOptV2  │  │SecurityOptV2│       │
│  │ (EMV Kernel) │  │  (PIN Entry) │  │  (DUKPT)    │       │
│  └─────────────┘  └─────────────┘  └─────────────┘       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              BACKEND API INTEGRATION                         │
│                                                              │
│  POST /v1/transactions/authorize                            │
│  {                                                           │
│    "terminal_id": "T001",                                   │
│    "merchant_id": "M001",                                   │
│    "amount": 10.00,                                         │
│    "currency": "EGP",                                       │
│    "field55": "9F26...",                                    │
│    "pin_block": "A1B2...",                                  │
│    "ksn": "FFFF...",                                        │
│    "iso_fields": {                                          │
│      "2": "PAN",                                            │
│      "3": "000000",                                         │
│      "4": "1000",                                           │
│      "11": "123456",                                        │
│      "22": "051",                                           │
│      "49": "818",                                           │
│      "55": "Field55..."                                     │
│    }                                                         │
│  }                                                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              BACKEND SERVER                                  │
│                                                              │
│  1. Decrypt PIN Block (DUKPT)                               │
│  2. Verify PIN                                               │
│  3. Process EMV Data                                         │
│  4. Generate Response                                        │
│                                                              │
│  Response Codes:                                             │
│  - 00: Approved ✅                                           │
│  - 05: Declined ❌                                           │
│  - 55/63: Wrong PIN 🔄                                       │
│  - 97: Key Sync Required 🔑                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 PIN Handling Flow

### Online PIN (9F34 = 01/02)

```
User → PIN Pad → DUKPT Encryption → PIN Block + KSN
                                              ↓
                                    Backend Authorization
                                              ↓
                                    Backend Decrypts PIN
                                              ↓
                                    Verify PIN ✅/❌
```

### Offline PIN (9F34 = 42)

```
User → PIN Pad → Card Verifies PIN → importPinInputStatus()
                                              ↓
                                    Backend Authorization
                                    (NO PIN BLOCK SENT)
                                              ↓
                                    Response 00/05
```

### No PIN (9F34 = 00)

```
No PIN Pad → Direct Authorization → Response 00/05
```

---

## 🔄 Reversal Flow

```
User → Reverse Button → Enter RRN → Backend API
                                      ↓
                        ┌─────────────┴─────────────┐
                        │                           │
                    Success ✅                  Failure ❌
                        │                           │
                  Print Receipt           Queue Offline ⏳
                                                    │
                                            Auto-Retry on Boot
```

---

## 🔄 Offline Queue Flow (FIFO)

```
Host Down → Queue Reversal → SharedPreferences
                              ↓
                       Activity Start
                              ↓
                       Retry First Item
                              ↓
                  ┌───────────┴───────────┐
                  │                       │
              Success ✅            Still Down ⚠️
                  │                       │
            Remove Item            Stop Retry
                  │                       │
            Retry Next Item       Will Retry Later
```

---

## 📊 Data Flow Summary

### Authorization Request

| Field | Source | Description |
|-------|--------|-------------|
| `terminal_id` | Terminal Config | Terminal identifier |
| `merchant_id` | Terminal Config | Merchant identifier |
| `amount` | User Input | Transaction amount |
| `currency` | Terminal Config | Currency code (EGP) |
| `field55` | EMV Kernel | All EMV TLV data |
| `pin_block` | DUKPT Encryption | Encrypted PIN (if online) |
| `ksn` | Security Module | Key Serial Number |
| `iso_fields.DE2` | Card Data | PAN (masked) |
| `iso_fields.DE3` | Fixed | Processing Code |
| `iso_fields.DE4` | Transaction | Amount |
| `iso_fields.DE11` | Generated | STAN |
| `iso_fields.DE22` | Auto-detected | POS Entry Mode |
| `iso_fields.DE49` | Terminal Config | Currency Code |
| `iso_fields.DE55` | EMV Kernel | ICC Data (Field 55) |

### Response Handling

| Response Code | Action |
|--------------|--------|
| `00` | Approved → Complete transaction, print receipt |
| `05` | Declined → Show error, no receipt |
| `55` / `63` | Wrong PIN → Retry (max 3 attempts) |
| `97` | Key Sync → Fetch new keys, retry |
| `IOException` | Network Error → Queue reversal offline |

---

## 🎯 Key Integration Points

### 1. **Terminal Configuration**
- Endpoint: `GET /v1/terminal/config`
- Trigger: App boot
- Cache: SharedPreferences
- Fields: `terminal_id`, `merchant_id`, `currency`

### 2. **DUKPT Key Injection**
- Endpoint: `GET /v1/terminal/dukpt`
- Trigger: App boot
- Injection: `SecurityOptV2.saveKeyDukpt()`
- Fields: `ipek`, `ksn`, `key_index`

### 3. **Authorization Request**
- Endpoint: `POST /v1/transactions/authorize`
- Trigger: EMV kernel `onOnlineProc()`
- Fields: Full transaction data + ISO8583 fields

### 4. **Reversal Request**
- Endpoint: `POST /v1/transactions/reverse`
- Trigger: User action or offline queue retry
- Fields: `rrn`, `amount`, `currency`, `reason`

### 5. **Settlement Upload**
- Endpoint: `POST /v1/settlement/upload`
- Trigger: User action (Settlement button)
- Fields: Batch of transaction RRNs

---

## 🔐 Security Features

✅ **DUKPT PIN Encryption** - Online PIN encrypted per transaction  
✅ **PAN Masking** - Card numbers masked in logs  
✅ **PIN Block Masking** - PIN blocks masked in logs  
✅ **Field 55 Masking** - EMV data masked in logs  
✅ **Hardware Security** - Keys stored in hardware security module  
✅ **Key Rotation** - Automatic key sync on response 97  

---

**Version:** 1.0  
**Status:** ✅ Complete  
**Last Updated:** 2025-01-15

