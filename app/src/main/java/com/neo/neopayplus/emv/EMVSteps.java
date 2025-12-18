package com.neo.neopayplus.emv;

/**
 * EMV Transaction Steps/States
 * 
 * Used to identify the current EMV callback step in the unified
 * onResultCallBack approach.
 */
public enum EMVSteps {
    /**
     * Card detection initiated - waiting for card
     */
    CARD_DETECT,

    /**
     * Card detected - IC or NFC card found
     */
    CARD_FOUND,

    /**
     * Multiple applications on card - selection required
     */
    EMV_APP_SELECT,

    /**
     * Application selected (final selection with AID)
     */
    EMV_FINAL_APP_SELECT,

    /**
     * Card number confirmation required
     */
    EMV_CONFIRM_CARD_NO,

    /**
     * Card data exchange complete
     */
    CARD_DATA_EXCHANGE_COMPLETE,

    /**
     * PIN pad display required
     * data = pinType (0=Online, 1=Offline)
     */
    EMV_SHOW_PIN_PAD,

    /**
     * Signature required
     */
    EMV_SIGNATURE,

    /**
     * Certificate verification required
     */
    EMV_CERT_VERIFY,

    /**
     * Online processing required - send to host
     */
    EMV_ONLINE_PROCESS,

    /**
     * Terminal risk management callback
     */
    EMV_TERM_RISK_MANAGEMENT,

    /**
     * Pre-first generate AC callback
     */
    EMV_PRE_FIRST_GEN_AC,

    /**
     * Data storage processing (DPAS 2.0)
     */
    EMV_DATA_STORAGE,

    /**
     * Transaction successful
     */
    EMV_TRANS_SUCCESS,

    /**
     * Transaction failed
     */
    EMV_TRANS_FAIL,

    /**
     * Present card again (code 4)
     */
    TRANS_PRESENT_CARD,

    /**
     * Card detection error
     */
    CARD_ERROR,

    /**
     * User cancelled
     */
    USER_CANCELLED
}
