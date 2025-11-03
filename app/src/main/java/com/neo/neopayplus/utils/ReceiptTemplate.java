package com.neo.neopayplus.utils;

import android.graphics.Bitmap;

/**
 * Receipt Template Builder
 * 
 * Builder pattern for constructing receipt data with EMV meta fields
 */
public class ReceiptTemplate {
    
    // Merchant info
    public String merchantName;
    public String merchantId;
    public String terminalId;
    
    // Transaction info
    public String rrn;
    public String dateTime;
    public String panMasked;
    public String amountText;
    public String responseMessage;
    public String authCode;
    
    // EMV meta fields
    public String stan;
    public String entryMode;
    public String aid;
    public String tsi;
    public String tvr;
    
    // Optional fields
    public Bitmap signatureBitmap;
    public Bitmap qrBitmap;
    
    /**
     * Set merchant information
     */
    public ReceiptTemplate withMerchant(String merchantName, String merchantId, String terminalId) {
        this.merchantName = merchantName;
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        return this;
    }
    
    /**
     * Set transaction information
     */
    public ReceiptTemplate withTxn(String rrn, String dateTime, String amountText, 
                                    String panMasked, String responseMessage, String authCode) {
        this.rrn = rrn;
        this.dateTime = dateTime;
        this.amountText = amountText;
        this.panMasked = panMasked;
        this.responseMessage = responseMessage;
        this.authCode = authCode;
        return this;
    }
    
    /**
     * Set EMV meta fields (AID, TSI, TVR, STAN, EntryMode)
     */
    public ReceiptTemplate withEmvMeta(String stan, String entryMode, String aid, String tsi, String tvr) {
        this.stan = stan;
        this.entryMode = entryMode;
        this.aid = aid;
        this.tsi = tsi;
        this.tvr = tvr;
        return this;
    }
    
    /**
     * Set signature bitmap
     */
    public ReceiptTemplate withSignature(Bitmap bmp) {
        this.signatureBitmap = bmp;
        return this;
    }
    
    /**
     * Set QR code bitmap
     */
    public ReceiptTemplate withQr(Bitmap bmp) {
        this.qrBitmap = bmp;
        return this;
    }
}

