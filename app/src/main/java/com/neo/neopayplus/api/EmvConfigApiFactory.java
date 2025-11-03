package com.neo.neopayplus.api;

import com.neo.neopayplus.Constant;
import com.neo.neopayplus.utils.LogUtil;

/**
 * EMV Configuration API Factory
 * 
 * Factory class for creating EmvConfigApiService instances.
 * Switch between mock and production implementations based on configuration.
 */
public class EmvConfigApiFactory {
    
    private static final String TAG = Constant.TAG;
    private static EmvConfigApiService instance;
    
    /**
     * API Implementation Type
     */
    public enum ApiType {
        MOCK,           // Mock implementation for testing
        PRODUCTION      // Real backend implementation
    }
    
    /**
     * Get EmvConfigApiService instance
     * 
     * @param type API implementation type
     * @return EmvConfigApiService instance
     */
    public static synchronized EmvConfigApiService getInstance(ApiType type) {
        if (instance == null || !isInstanceOfType(instance, type)) {
            instance = createInstance(type);
        }
        return instance;
    }
    
    /**
     * Get default EmvConfigApiService instance
     * 
     * @return EmvConfigApiService instance
     */
    public static synchronized EmvConfigApiService getInstance() {
        // Using configured server at 192.168.100.176:8080
        return getInstance(ApiType.PRODUCTION);
    }
    
    /**
     * Create new instance based on type
     */
    private static EmvConfigApiService createInstance(ApiType type) {
        switch (type) {
            case MOCK:
                LogUtil.e(TAG, "Creating EMV Config API Service (MOCK - baseUrl not configured)");
                EmvConfigApiServiceImpl service = new EmvConfigApiServiceImpl(); // No baseUrl = mock mode
                service.setMockConfiguration(true, 500); // 500ms delay
                return service;
                
            case PRODUCTION:
                LogUtil.e(TAG, "Creating EMV Config API Service (PRODUCTION mode)");
                // Configured server at 192.168.100.176:8080
                // Base URL includes /v1 prefix as per API documentation
                String apiBaseUrl = "http://192.168.100.176:8080/v1";
                String apiKey = "test-token"; // Default test token - replace with actual auth token if needed
                return new EmvConfigApiServiceImpl(apiBaseUrl, apiKey);
                
            default:
                throw new IllegalArgumentException("Unknown API type: " + type);
        }
    }
    
    /**
     * Check if instance is of specified type
     */
    private static boolean isInstanceOfType(EmvConfigApiService service, ApiType type) {
        // Both modes use the same implementation class
        return service instanceof EmvConfigApiServiceImpl;
    }
    
    /**
     * Reset instance (for testing)
     */
    public static synchronized void reset() {
        instance = null;
    }
}

