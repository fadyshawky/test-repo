package com.neo.neopayplus.api;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * Payment API Factory
 * 
 * Factory class for creating PaymentApiService instances.
 * Switch between mock and production implementations based on configuration.
 */
public class PaymentApiFactory {
    
    private static final String TAG = Constant.TAG;
    private static PaymentApiService instance;
    
    /**
     * API Implementation Type
     */
    public enum ApiType {
        MOCK,           // Mock implementation for testing
        PRODUCTION      // Real backend implementation
    }
    
    /**
     * Get PaymentApiService instance
     * 
     * @param type API implementation type
     * @return PaymentApiService instance
     */
    public static synchronized PaymentApiService getInstance(ApiType type) {
        if (instance == null || !isInstanceOfType(instance, type)) {
            instance = createInstance(type);
        }
        return instance;
    }
    
    /**
     * Get default PaymentApiService instance
     * 
     * @return PaymentApiService instance
     */
    public static synchronized PaymentApiService getInstance() {
        // Using configured server at 192.168.100.176:8080
        return getInstance(ApiType.PRODUCTION);
    }
    
    /**
     * Create new instance based on type
     */
    private static PaymentApiService createInstance(ApiType type) {
        switch (type) {
            case MOCK:
                LogUtil.e(TAG, "Creating Payment API Service (MOCK - baseUrl not configured)");
                PaymentApiServiceImpl service = new PaymentApiServiceImpl(); // No baseUrl = mock mode
                service.setMockConfiguration(true, 1500, 0.95); // 1.5s delay, 95% approval
                return service;
                
            case PRODUCTION:
                LogUtil.e(TAG, "Creating Payment API Service (PRODUCTION mode)");
                // API key and base URL loaded from buildConfigField (secure storage)
                // Set via environment variables or build.gradle for production builds
                String apiBaseUrl = com.neo.neopayplus.BuildConfig.API_BASE_URL;
                String apiKey = com.neo.neopayplus.BuildConfig.API_KEY;
                LogUtil.e(TAG, "Using API Base URL: " + (apiBaseUrl != null && !apiBaseUrl.isEmpty() ? apiBaseUrl : "NOT CONFIGURED"));
                LogUtil.e(TAG, "Using API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
                return new PaymentApiServiceImpl(apiBaseUrl, apiKey);
                
            default:
                throw new IllegalArgumentException("Unknown API type: " + type);
        }
    }
    
    /**
     * Check if instance is of specified type
     */
    private static boolean isInstanceOfType(PaymentApiService service, ApiType type) {
        // Both modes use the same implementation class
        return service instanceof PaymentApiServiceImpl;
    }
    
    /**
     * Reset instance (for testing)
     */
    public static synchronized void reset() {
        instance = null;
    }
}

