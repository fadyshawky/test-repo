# NeoPayPlus POS UI (Compose Integration)

This is a modern Jetpack Compose UI layer for the NeoPayPlus POS app, designed to work with your existing Java PayLib services.

## Features

- **Home hub** with 5 flows: Card Payment, Refund, Void, Settlement, Transactions History
- **Minimal-click UX** with modern Material Design 3
- **Jetpack Compose** single-activity navigation
- **EMV/PIN/Print** abstracted via `EmvBridge` interface

## Architecture

### Bridge Pattern
- **`EmvBridge.kt`**: Interface defining operations for Compose UI
- **`EmvBridgeImpl.kt`**: Implementation connecting to existing Java services (EMVOptV2, PinPadOptV2, etc.)

### Compose UI Structure
```
app/src/main/java/com/neo/neopayplus/
├── ui/
│   ├── theme/          # Material Theme configuration
│   ├── components/     # Reusable Compose components (NumPad, etc.)
│   └── screens/        # Screen composables (Home, Amount, CardDetect, etc.)
├── navigation/         # Navigation routes and NavHost
├── vm/                 # ViewModels (if needed)
└── emv/
    ├── EmvBridge.kt    # Bridge interface
    └── EmvBridgeImpl.kt # Bridge implementation
```

## Usage

### Option 1: Use Compose UI as Main Activity
Update `AndroidManifest.xml` to set `MainActivityCompose` as the launcher:
```xml
<activity
    android:name=".MainActivityCompose"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Option 2: Keep Existing MainActivity
The existing `MainActivity` continues to work. `MainActivityCompose` is available as an alternative entry point.

## Integration Points

### 1. Card Detection
- `EmvBridgeImpl.detectCard()` uses `ReadCardOptV2` from `MyApplication.app`
- Calls `CheckCardCallbackV2Wrapper` to handle IC/NFC/Mag cards

### 2. PIN Entry
- `EmvBridgeImpl.requestPin()` uses `PinPadOptV2` with `PinPadHelper` utility
- Configures PIN pad with production-ready settings (active slot, ISO-0 format)

### 3. Transaction Authorization
- `ProcessingScreen` currently has a placeholder - TODO: Connect to `ProcessEmvTransactionUseCase`
- Full EMV transaction flow should be integrated here

### 4. Transaction History
- `TxnHistoryScreen` uses `TransactionJournal.loadJournal()` to display transactions
- Connects to existing transaction journal system

## TODOs

- [ ] Complete `authorize()` implementation in `EmvBridgeImpl` to call `ProcessEmvTransactionUseCase`
- [ ] Implement `printReceipt()` using `PrinterOptV2`
- [ ] Extract actual card number from card detection callback
- [ ] Connect `ProcessingScreen` to full EMV transaction flow
- [ ] Add ViewModel for state management if needed
- [ ] Add error handling and retry logic

## Dependencies

- Compose BOM 2024.10.01
- Material3 1.3.0
- Navigation-Compose 2.8.3
- Kotlin 1.9.0

## Notes

- Existing Java services (`MyApplication.app.*`) are accessed from Kotlin code
- PaySDK initialization is handled by `MyApplication.java` before Compose UI loads
- All existing business logic remains in Java - Compose UI is a presentation layer only

