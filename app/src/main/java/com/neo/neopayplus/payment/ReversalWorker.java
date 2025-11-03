package com.neo.neopayplus.payment;

import android.content.Context;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.api.PaymentApiFactory;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.config.PaymentConfig;
import com.neo.neopayplus.db.TxnDb;
import com.neo.neopayplus.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reversal Worker
 * 
 * Background worker that automatically retries pending reversals
 * Runs in background thread, processes FIFO queue
 */
public class ReversalWorker {
    
    private static final String TAG = Constant.TAG;
    private final Context ctx;
    private final PaymentApiService apiService;
    
    public ReversalWorker(Context c) {
        this.ctx = c;
        this.apiService = PaymentApiFactory.getInstance();
    }
    
    /**
     * Process one batch of pending reversals
     * Called periodically from background thread
     */
    public void tick() {
        try {
            TxnDb db = new TxnDb(ctx);
            List<Map<String, Object>> queue = db.pendingReversals(10); // Process up to 10 at a time
            
            if (queue.isEmpty()) {
                return; // No pending reversals
            }
            
            LogUtil.e(TAG, "ReversalWorker: Processing " + queue.size() + " pending reversals");
            
            for (Map<String, Object> reversal : queue) {
                // Build reversal request
                Map<String, Object> body = new HashMap<>();
                body.put("terminal_id", PaymentConfig.getTerminalId());
                body.put("merchant_id", PaymentConfig.getMerchantId());
                body.put("rrn", reversal.get("rrn"));
                
                // Convert amount from minor units to main currency
                Object amountObj = reversal.get("amount_minor");
                if (amountObj instanceof Number) {
                    long amountMinor = ((Number) amountObj).longValue();
                    body.put("amount", amountMinor / 100.0);
                } else {
                    body.put("amount", 0.0);
                }
                
                body.put("currency", String.valueOf(reversal.get("currency")));
                body.put("reversal_reason", String.valueOf(reversal.get("reason")));
                
                // Send reversal request
                PaymentApiService.ReversalRequest request = new PaymentApiService.ReversalRequest();
                request.terminalId = PaymentConfig.getTerminalId();
                request.merchantId = PaymentConfig.getMerchantId();
                request.rrn = String.valueOf(reversal.get("rrn"));
                request.amount = ((Number) reversal.get("amount_minor")).doubleValue() / 100.0;
                request.currency = String.valueOf(reversal.get("currency"));
                request.reversalReason = String.valueOf(reversal.get("reason"));
                
                final Map<String, Object> finalReversal = reversal;
                
                apiService.reverseTransaction(request, new PaymentApiService.ReversalCallback() {
                    @Override
                    public void onReversalComplete(PaymentApiService.ReversalResponse response) {
                        String rc = response.responseCode != null ? response.responseCode : "";
                        
                        // Remove from queue if successful or already reversed/declined
                        if ("00".equals(rc) || "94".equals(rc) || "12".equals(rc)) {
                            TxnDb db = new TxnDb(ctx);
                            Object idObj = finalReversal.get("id");
                            if (idObj instanceof Number) {
                                db.deleteReversal(((Number) idObj).longValue());
                                LogUtil.e(TAG, "ReversalWorker: Reversal removed from queue - RRN: " + finalReversal.get("rrn") + ", RC: " + rc);
                            }
                        } else {
                            LogUtil.e(TAG, "ReversalWorker: Reversal still pending - RRN: " + finalReversal.get("rrn") + ", RC: " + rc);
                        }
                    }
                    
                    @Override
                    public void onReversalError(Throwable error) {
                        LogUtil.e(TAG, "ReversalWorker: Reversal error - RRN: " + finalReversal.get("rrn") + ", Error: " + error.getMessage());
                        // Keep in queue, will retry later
                    }
                });
                
                // Small delay between reversals to avoid overwhelming server
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            
        } catch (Exception e) {
            LogUtil.e(TAG, "ReversalWorker: Error in tick: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

