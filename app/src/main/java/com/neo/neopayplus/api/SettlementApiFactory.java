package com.neo.neopayplus.api;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * Settlement API Factory
 * 
 * Factory class for creating SettlementApiService instances.
 * Switch between mock and production implementations based on configuration.
 */
public class SettlementApiFactory {
    
    private static final String TAG = Constant.TAG;
    private static SettlementApiService instance;
    
    /**
     * API Implementation Type
     */
    public enum ApiType {
        MOCK,           // Mock implementation for testing
        PRODUCTION      // Real backend implementation
    }
    
    /**
     * Get SettlementApiService instance
     * 
     * @param type API implementation type
     * @return SettlementApiService instance
     */
    public static synchronized SettlementApiService getInstance(ApiType type) {
        if (instance == null || !isInstanceOfType(instance, type)) {
            instance = createInstance(type);
        }
        return instance;
    }
    
    /**
     * Get default SettlementApiService instance
     * 
     * @return SettlementApiService instance
     */
    public static synchronized SettlementApiService getInstance() {
        // Using configured server at 192.168.100.176:8080
        return getInstance(ApiType.PRODUCTION);
    }
    
    /**
     * Create new instance based on type
     */
    private static SettlementApiService createInstance(ApiType type) {
        switch (type) {
            case MOCK:
                LogUtil.e(TAG, "Creating Settlement API Service (MOCK - baseUrl not configured)");
                SettlementApiServiceImpl service = new SettlementApiServiceImpl(); // No baseUrl = mock mode
                return service;
                
            case PRODUCTION:
                LogUtil.e(TAG, "Creating Settlement API Service (PRODUCTION mode)");
                // API key and base URL loaded from buildConfigField (secure storage)
                // Set via environment variables or build.gradle for production builds
                String apiBaseUrl = com.neo.neopayplus.BuildConfig.API_BASE_URL;
                String apiKey = com.neo.neopayplus.BuildConfig.API_KEY;
                LogUtil.e(TAG, "Using API Base URL: " + (apiBaseUrl != null && !apiBaseUrl.isEmpty() ? apiBaseUrl : "NOT CONFIGURED"));
                LogUtil.e(TAG, "Using API Key: " + (apiKey != null && !apiKey.isEmpty() ? "***CONFIGURED***" : "NOT CONFIGURED"));
                return new SettlementApiServiceImpl(apiBaseUrl, apiKey);
                
            default:
                throw new IllegalArgumentException("Unknown API type: " + type);
        }
    }
    
    /**
     * Check if instance is of specified type
     */
    private static boolean isInstanceOfType(SettlementApiService service, ApiType type) {
        // Both modes use the same implementation class
        return service instanceof SettlementApiServiceImpl;
    }
    
    /**
     * Reset instance (for testing)
     */
    public static synchronized void reset() {
        instance = null;
    }
}

