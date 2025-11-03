package com.neo.neopayplus.processing.repository;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.api.PaymentApiService;
import com.neo.neopayplus.api.PaymentApiFactory;
import com.neo.neopayplus.utils.LogUtil;

/**
 * Transaction Repository
 * 
 * Handles data access for transaction operations.
 * Encapsulates API calls and provides a clean interface for business logic.
 */
public class TransactionRepository {
    
    private static final String TAG = Constant.TAG;
    private final PaymentApiService apiService;
    
    public TransactionRepository() {
        this.apiService = PaymentApiFactory.getInstance();
    }
    
    /**
     * Constructor for dependency injection (testing)
     */
    public TransactionRepository(PaymentApiService apiService) {
        this.apiService = apiService;
    }
    
    /**
     * Check if repository is available
     */
    public boolean isAvailable() {
        return apiService != null && apiService.isAvailable();
    }
    
    /**
     * Authorize transaction with backend
     * 
     * @param request Authorization request
     * @param callback Callback for response
     */
    public void authorizeTransaction(PaymentApiService.AuthorizationRequest request, 
                                     PaymentApiService.AuthorizationCallback callback) {
        if (!isAvailable()) {
            LogUtil.e(TAG, "⚠️ Payment API service not available");
            callback.onAuthorizationError(new IllegalStateException("Payment API service not available"));
            return;
        }
        
        LogUtil.e(TAG, "Requesting backend authorization: " + request.toString());
        apiService.authorizeTransaction(request, callback);
    }
    
    /**
     * Reverse transaction with backend
     * 
     * @param request Reversal request
     * @param callback Callback for response
     */
    public void reverseTransaction(PaymentApiService.ReversalRequest request,
                                   PaymentApiService.ReversalCallback callback) {
        if (!isAvailable()) {
            LogUtil.e(TAG, "⚠️ Payment API service not available for reversal");
            callback.onReversalError(new IllegalStateException("Payment API service not available"));
            return;
        }
        
        LogUtil.e(TAG, "Requesting backend reversal: " + request.toString());
        apiService.reverseTransaction(request, callback);
    }
}

