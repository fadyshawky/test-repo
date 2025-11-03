package com.neo.neopayplus.processing.usecase;

import android.os.Bundle;

import com.neo.neopayplus.BuildConfig;
import com.neo.neopayplus.Constant;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.processing.CvmHandler;
import com.neo.neopayplus.processing.repository.TransactionRepository;
import com.neo.neopayplus.utils.ByteUtil;
import com.neo.neopayplus.utils.EntryModeUtil;
import com.neo.neopayplus.utils.LogUtil;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.payservice.AidlConstantsV2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Process EMV Transaction Use Case
 * 
 * Handles the business logic for processing EMV transactions.
 * Extracted from ProcessingActivity to improve separation of concerns.
 */
public class ProcessEmvTransactionUseCase {
    
    private static final String TAG = Constant.TAG;
    
    private final EMVOptV2 emvOptV2;
    private final TransactionRepository transactionRepository;
    private final CvmHandler cvmHandler;
    
    public ProcessEmvTransactionUseCase(EMVOptV2 emvOptV2, 
                                        TransactionRepository transactionRepository,
                                        CvmHandler cvmHandler) {
        this.emvOptV2 = emvOptV2;
        this.transactionRepository = transactionRepository;
        this.cvmHandler = cvmHandler;
    }
    
    /**
     * Transaction data for processing
     */
    public static class TransactionData {
        public final String pan;
        public final String amount;
        public final String currencyCode;
        public final int cardType;
        public final boolean pinEntered;
        public final boolean fallbackUsed;
        public final byte[] onlinePinBlock;
        public final String ksn;
        public final int pinType;
        public final int stan;
        
        public TransactionData(String pan, String amount, String currencyCode, 
                             int cardType, boolean pinEntered, boolean fallbackUsed,
                             byte[] onlinePinBlock, String ksn, int pinType, int stan) {
            this.pan = pan;
            this.amount = amount;
            this.currencyCode = currencyCode;
            this.cardType = cardType;
            this.pinEntered = pinEntered;
            this.fallbackUsed = fallbackUsed;
            this.onlinePinBlock = onlinePinBlock;
            this.ksn = ksn;
            this.pinType = pinType;
            this.stan = stan;
        }
    }
    
    /**
     * Callback for transaction processing
     */
    public interface TransactionCallback {
        void onSuccess(PaymentApiService.AuthorizationResponse response);
        void onError(Throwable error);
    }
    
    /**
     * Process online authorization
     * 
     * @param transactionData Transaction data
     * @param callback Callback for result
     */
    public void processOnlineAuthorization(TransactionData transactionData, 
                                         TransactionCallback callback) {
        try {
            LogUtil.e(TAG, "=== ONLINE PROCESSING REQUESTED ===");
            LogUtil.e(TAG, "🌐 EMV kernel requesting online authorization");
            LogUtil.e(TAG, "📋 PIN Type: " + (transactionData.pinType == 0 ? "Offline PIN (already verified by card)" : "Online PIN (needs backend verification)"));
            
            // Extract Field 55 (EMV data)
            String field55 = extractField55();
            
            if (field55 == null || field55.isEmpty()) {
                callback.onError(new IllegalStateException("Failed to extract Field 55 from EMV kernel"));
                return;
            }
            
            // Extract CVM Result
            String cvmResultCode = cvmHandler.extractCvmResultCode();
            LogUtil.e(TAG, "=== CVM RESULT ANALYSIS ===");
            LogUtil.e(TAG, "CVM Result Code (9F34): " + (cvmResultCode != null ? cvmResultCode : "Not available"));
            
            // Determine PIN handling
            CvmHandler.CvmResult cvmResult = cvmHandler.determinePinHandling(cvmResultCode, transactionData.pinType);
            
            // Build authorization request
            PaymentApiService.AuthorizationRequest authRequest = buildAuthorizationRequest(
                transactionData, field55, cvmResult);
            
            // Send to backend
            transactionRepository.authorizeTransaction(authRequest, 
                new PaymentApiService.AuthorizationCallback() {
                    @Override
                    public void onAuthorizationComplete(PaymentApiService.AuthorizationResponse response) {
                        callback.onSuccess(response);
                    }
                    
                    @Override
                    public void onAuthorizationError(Throwable error) {
                        callback.onError(error);
                    }
                });
            
        } catch (Exception e) {
            com.neo.neopayplus.utils.ErrorHandler.logError(TAG, "Online processing", e);
            callback.onError(e);
        }
    }
    
    /**
     * Extract Field 55 from EMV kernel
     */
    private String extractField55() {
        try {
            // Try secure account data extraction first (PayLib v2.0.32 preferred)
            String[] field55Tags = {
                "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                "82", "9F1A", "9F34", "9F33", "9F35", "9F1E", "84", "9F09", "9F41", "5A", "5F24", "5F34"
            };
            
            Bundle secDataBundle = new Bundle();
            int result = emvOptV2.getAccountSecData(0, field55Tags, secDataBundle);
            if (result == 0) {
                String field55 = buildField55FromBundle(secDataBundle);
                LogUtil.e(TAG, "Field 55 built from getAccountSecData() - length: " + field55.length() + " bytes");
                return field55;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getAccountSecData() failed, falling back to getTlvList(): " + e.getMessage());
        }
        
        // Fallback to getTlvList()
        try {
            String[] field55Tags = {
                "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                "82", "9F1A", "9F34", "9F33", "9F35", "9F1E", "84", "9F09", "9F41", "5A", "5F24", "5F34"
            };
            
            byte[] outData = new byte[2048];
            int len = emvOptV2.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, field55Tags, outData);
            if (len > 0) {
                byte[] tlvData = new byte[len];
                System.arraycopy(outData, 0, tlvData, 0, len);
                String field55 = ByteUtil.bytes2HexStr(tlvData);
                LogUtil.e(TAG, "Field 55 built using getTlvList() - length: " + field55.length() + " bytes");
                return field55;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getTlvList() failed: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Build Field 55 from secure data bundle
     */
    private String buildField55FromBundle(Bundle bundle) {
        // TODO: Implement Field 55 building from bundle
        // This is a placeholder - actual implementation depends on PayLib v2.0.32 API
        LogUtil.e(TAG, "⚠️ buildField55FromBundle() not yet implemented - using fallback");
        return extractField55(); // Fallback to getTlvList()
    }
    
    /**
     * Build authorization request
     */
    private PaymentApiService.AuthorizationRequest buildAuthorizationRequest(
            TransactionData transactionData, String field55, CvmHandler.CvmResult cvmResult) {
        
        PaymentApiService.AuthorizationRequest request = new PaymentApiService.AuthorizationRequest();
        request.field55 = field55;
        request.pan = transactionData.pan;
        request.amount = transactionData.amount;
        request.currencyCode = transactionData.currencyCode;
        request.transactionType = "00"; // Purchase
        
        // Set date/time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss", Locale.US);
        Date now = new Date();
        request.date = dateFormat.format(now);
        request.time = timeFormat.format(now);
        
        // Include PIN block + KSN ONLY if CVM indicates online PIN
        if (cvmResult.shouldSendPinToBackend && 
            transactionData.onlinePinBlock != null && 
            transactionData.onlinePinBlock.length > 0) {
            
            String pinBlockHex = ByteUtil.bytes2HexStr(transactionData.onlinePinBlock);
            request.pinBlock = pinBlockHex.getBytes();
            request.ksn = transactionData.ksn;
            
            LogUtil.e(TAG, "✓ Including PIN block + KSN in authorization request (" + cvmResult.description + ")");
            if (com.neo.neopayplus.BuildConfig.DEBUG) {
                String maskedPinBlock = pinBlockHex.length() > 8 
                    ? pinBlockHex.substring(0, 4) + "****" + pinBlockHex.substring(pinBlockHex.length() - 4)
                    : "****";
                LogUtil.e(TAG, "  PIN block (masked): " + maskedPinBlock);
                LogUtil.e(TAG, "  KSN: " + (transactionData.ksn != null ? transactionData.ksn : "null"));
            }
        } else {
            if ("42".equals(cvmResult.code)) {
                LogUtil.e(TAG, "✓ Offline PIN transaction - PIN already verified by card, NOT sending PIN block to backend");
            } else if ("00".equals(cvmResult.code)) {
                LogUtil.e(TAG, "✓ No PIN required - NOT sending PIN block to backend");
            } else {
                LogUtil.e(TAG, "⚠️ No PIN block available or CVM indicates offline PIN - transaction may be offline PIN or no PIN required");
            }
        }
        
        return request;
    }
}

