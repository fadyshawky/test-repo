package com.neo.neopayplus.utils;

import android.os.RemoteException;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.security.KeyManagerPOS;
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidl.AidlConstants.PinBlockFormat;

import java.nio.charset.StandardCharsets;

/**
 * PIN Pad Helper Utility
 * 
 * Eliminates duplicate PIN pad initialization code across activities.
 * Provides a consistent, secure way to configure and initialize PIN pads.
 */
public class PinPadHelper {
    
    private static final String TAG = Constant.TAG;
    
    /**
     * PIN Pad Configuration Builder
     * Provides a fluent API for building PIN pad configurations
     */
    public static class PinPadConfigBuilder {
        private PinPadConfigV2 config = new PinPadConfigV2();
        private String cardNo;
        private int pinType = 0; // 0 = offline, 1 = online
        private int pinKeyIndex = 12; // Default PIN key index
        private int timeout = 60 * 1000; // 60 seconds
        private int minInput = 0;
        private int maxInput = 12;
        private int keySystem = 0; // 0 = MKSK, 1 = DUKPT
        private int algorithmType = 0; // 0 = 3DES
        private boolean useActiveSlot = false; // Use KeyManagerPOS.getActivePinSlot()
        
        /**
         * Set card number (required for PIN block encryption)
         */
        public PinPadConfigBuilder setCardNo(String cardNo) {
            this.cardNo = cardNo;
            return this;
        }
        
        /**
         * Set PIN type (0 = offline PIN, 1 = online PIN)
         */
        public PinPadConfigBuilder setPinType(int pinType) {
            this.pinType = pinType;
            this.config.setPinType(pinType);
            return this;
        }
        
        /**
         * Set PIN key index (TPK slot)
         * If useActiveSlot() is called, this value is ignored
         */
        public PinPadConfigBuilder setPinKeyIndex(int pinKeyIndex) {
            this.pinKeyIndex = pinKeyIndex;
            return this;
        }
        
        /**
         * Use active PIN slot from KeyManagerPOS (recommended for production)
         */
        public PinPadConfigBuilder useActiveSlot() {
            this.useActiveSlot = true;
            return this;
        }
        
        /**
         * Set timeout in milliseconds
         */
        public PinPadConfigBuilder setTimeout(int timeoutMs) {
            this.timeout = timeoutMs;
            this.config.setTimeout(timeoutMs);
            return this;
        }
        
        /**
         * Set PIN input length constraints
         */
        public PinPadConfigBuilder setInputLength(int minInput, int maxInput) {
            this.minInput = minInput;
            this.maxInput = maxInput;
            this.config.setMinInput(minInput);
            this.config.setMaxInput(maxInput);
            return this;
        }
        
        /**
         * Set key system (0 = MKSK, 1 = DUKPT)
         */
        public PinPadConfigBuilder setKeySystem(int keySystem) {
            this.keySystem = keySystem;
            this.config.setKeySystem(keySystem);
            return this;
        }
        
        /**
         * Set algorithm type (0 = 3DES, 1 = SM4, 2 = AES)
         */
        public PinPadConfigBuilder setAlgorithmType(int algorithmType) {
            this.algorithmType = algorithmType;
            this.config.setAlgorithmType(algorithmType);
            return this;
        }
        
        /**
         * Build the PIN pad configuration
         */
        public PinPadConfigV2 build() throws IllegalArgumentException {
            // Set common defaults
            config.setPinPadType(0); // SDK built-in PinPad
            config.setOrderNumKey(false); // Don't use ordered number key
            
            // Set PIN key index (use active slot if requested)
            int finalPinKeyIndex = useActiveSlot 
                ? KeyManagerPOS.getActivePinSlot() 
                : pinKeyIndex;
            config.setPinKeyIndex(finalPinKeyIndex);
            
            if (useActiveSlot) {
                LogUtil.e(TAG, "Using active PIN slot from KeyManagerPOS: " + finalPinKeyIndex);
            }
            
            // Set PAN if card number provided (required for ISO-0 PIN block format)
            if (cardNo != null && cardNo.length() >= 14) {
                try {
                    // For ISO-0 format: Use last 12 digits of PAN (excluding check digit)
                    String panSubstring = cardNo.substring(cardNo.length() - 13, cardNo.length() - 1);
                    byte[] panBytes = panSubstring.getBytes(StandardCharsets.US_ASCII);
                    config.setPan(panBytes);
                    LogUtil.e(TAG, "PAN set for PinPad: " + panSubstring);
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error setting PAN for PinPad: " + e.getMessage());
                    throw new IllegalArgumentException("Invalid card number format", e);
                }
            } else {
                LogUtil.e(TAG, "⚠️ WARNING: No valid PAN available for PinPad");
                if (pinType == 1) {
                    LogUtil.e(TAG, "⚠️ PAN is required for online PIN encryption (ISO-0 format)");
                }
            }
            
            // Set PIN block format (ISO-0 for online PIN)
            if (pinType == 1) {
                config.setPinblockFormat(PinBlockFormat.SEC_PIN_BLK_ISO_FMT0);
            }
            
            LogUtil.e(TAG, "PinPad Configuration:");
            LogUtil.e(TAG, "  PIN Type: " + (pinType == 1 ? "Online" : "Offline"));
            LogUtil.e(TAG, "  Key System: " + (keySystem == 1 ? "DUKPT" : "MKSK"));
            LogUtil.e(TAG, "  PIN Block Format: " + (pinType == 1 ? "ISO-0" : "Default"));
            LogUtil.e(TAG, "  Algorithm: " + (algorithmType == 0 ? "3DES" : (algorithmType == 1 ? "SM4" : "AES")));
            LogUtil.e(TAG, "  Key Index: " + finalPinKeyIndex);
            
            return config;
        }
    }
    
    /**
     * Create a new PIN pad configuration builder
     */
    public static PinPadConfigBuilder builder() {
        return new PinPadConfigBuilder();
    }
    
    /**
     * Initialize PIN pad with default configuration (legacy compatibility)
     * 
     * @param pinPadOptV2 PIN pad service
     * @param cardNo Card number
     * @param pinType PIN type (0 = offline, 1 = online)
     * @param listener PIN pad listener
     * @throws RemoteException if initialization fails
     */
    public static void initPinPad(PinPadOptV2 pinPadOptV2, String cardNo, int pinType, 
                                  PinPadListenerV2 listener) throws RemoteException {
        initPinPad(pinPadOptV2, cardNo, pinType, 12, listener);
    }
    
    /**
     * Initialize PIN pad with specified key index
     * 
     * @param pinPadOptV2 PIN pad service
     * @param cardNo Card number
     * @param pinType PIN type (0 = offline, 1 = online)
     * @param pinKeyIndex PIN key index
     * @param listener PIN pad listener
     * @throws RemoteException if initialization fails
     */
    public static void initPinPad(PinPadOptV2 pinPadOptV2, String cardNo, int pinType, 
                                  int pinKeyIndex, PinPadListenerV2 listener) throws RemoteException {
        PinPadConfigV2 config = builder()
            .setCardNo(cardNo)
            .setPinType(pinType)
            .setPinKeyIndex(pinKeyIndex)
            .setTimeout(60 * 1000)
            .setInputLength(0, 12)
            .setKeySystem(0)
            .setAlgorithmType(0)
            .build();
        
        pinPadOptV2.initPinPad(config, listener);
    }
    
    /**
     * Initialize PIN pad with production-ready configuration (uses active slot)
     * Recommended for ProcessingActivity and production code
     * 
     * @param pinPadOptV2 PIN pad service
     * @param cardNo Card number
     * @param pinType PIN type (0 = offline, 1 = online)
     * @param listener PIN pad listener
     * @throws RemoteException if initialization fails
     */
    public static void initPinPadProduction(PinPadOptV2 pinPadOptV2, String cardNo, int pinType,
                                           PinPadListenerV2 listener) throws RemoteException {
        PinPadConfigV2 config = builder()
            .setCardNo(cardNo)
            .setPinType(pinType)
            .useActiveSlot() // Use active slot from KeyManagerPOS
            .setTimeout(60 * 1000)
            .setInputLength(4, 6) // Standard PIN length
            .setKeySystem(0) // MKSK
            .setAlgorithmType(0) // 3DES
            .build();
        
        pinPadOptV2.initPinPad(config, listener);
    }
}

