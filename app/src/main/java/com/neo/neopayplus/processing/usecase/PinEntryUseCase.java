package com.neo.neopayplus.processing.usecase;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * PIN Entry Use Case
 * 
 * Handles PIN entry business logic.
 * Extracted from ProcessingActivity to improve separation of concerns.
 */
public class PinEntryUseCase {
    
    private static final String TAG = Constant.TAG;
    private static final int MAX_PIN_ATTEMPTS = 3;
    
    private int pinAttemptsLeft = MAX_PIN_ATTEMPTS;
    
    /**
     * PIN entry result
     */
    public static class PinEntryResult {
        public final boolean success;
        public final byte[] pinBlock;
        public final int pinType; // 0=offline, 1=online
        public final String errorMessage;
        
        private PinEntryResult(boolean success, byte[] pinBlock, int pinType, String errorMessage) {
            this.success = success;
            this.pinBlock = pinBlock;
            this.pinType = pinType;
            this.errorMessage = errorMessage;
        }
        
        public static PinEntryResult success(byte[] pinBlock, int pinType) {
            return new PinEntryResult(true, pinBlock, pinType, null);
        }
        
        public static PinEntryResult error(String errorMessage) {
            return new PinEntryResult(false, null, -1, errorMessage);
        }
    }
    
    /**
     * Reset PIN attempts counter
     */
    public void resetAttempts() {
        pinAttemptsLeft = MAX_PIN_ATTEMPTS;
        LogUtil.e(TAG, "🔄 PIN attempts counter reset to " + MAX_PIN_ATTEMPTS);
    }
    
    /**
     * Get remaining PIN attempts
     */
    public int getRemainingAttempts() {
        return pinAttemptsLeft;
    }
    
    /**
     * Decrement PIN attempts
     */
    public void decrementAttempts() {
        if (pinAttemptsLeft > 0) {
            pinAttemptsLeft--;
            LogUtil.e(TAG, "📊 PIN attempts remaining: " + pinAttemptsLeft + " / " + MAX_PIN_ATTEMPTS);
        }
    }
    
    /**
     * Check if PIN attempts exhausted
     */
    public boolean areAttemptsExhausted() {
        return pinAttemptsLeft <= 0;
    }
    
    /**
     * Handle PIN entry result
     * 
     * @param pinBlock PIN block from pinpad
     * @param pinType PIN type (0=offline, 1=online)
     * @return PinEntryResult
     */
    public PinEntryResult handlePinEntry(byte[] pinBlock, int pinType) {
        if (pinBlock == null || pinBlock.length == 0) {
            return PinEntryResult.error("PIN block is null or empty");
        }
        
        LogUtil.e(TAG, "🔐 PIN block received (encrypted) - length: " + pinBlock.length + " bytes");
        LogUtil.e(TAG, "📋 PIN type: " + pinType + " (0=Offline, 1=Online)");
        
        return PinEntryResult.success(pinBlock, pinType);
    }
    
    /**
     * Handle PIN entry cancellation
     */
    public PinEntryResult handlePinCancellation() {
        LogUtil.e(TAG, "🚫 User cancelled PIN entry");
        return PinEntryResult.error("PIN entry cancelled by user");
    }
    
    /**
     * Handle PIN entry error
     */
    public PinEntryResult handlePinError(int errorCode) {
        LogUtil.e(TAG, "❌ PIN entry error: " + errorCode);
        return PinEntryResult.error("PIN entry failed with error code: " + errorCode);
    }
}

