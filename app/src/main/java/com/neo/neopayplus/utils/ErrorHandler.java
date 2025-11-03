package com.neo.neopayplus.utils;

import com.neo.neopayplus.BuildConfig;
import com.neo.neopayplus.Constant;

/**
 * Error Handler Utility
 * 
 * Provides consistent error logging across the application.
 * Eliminates duplicate printStackTrace() calls and ensures proper error handling.
 */
public class ErrorHandler {
    
    private static final String TAG = Constant.TAG;
    
    /**
     * Log error with context message
     * Only prints stack trace in DEBUG builds for security
     * 
     * @param context Context where error occurred (e.g., "initPinPad", "processTransaction")
     * @param error The exception/error
     */
    public static void logError(String context, Throwable error) {
        logError(TAG, context, error);
    }
    
    /**
     * Log error with custom tag and context
     * 
     * @param tag Log tag
     * @param context Context where error occurred
     * @param error The exception/error
     */
    public static void logError(String tag, String context, Throwable error) {
        String message = error != null && error.getMessage() != null 
            ? error.getMessage() 
            : "Unknown error";
        
        LogUtil.e(tag, "❌ Error in " + context + ": " + message);
        
        // Only print stack trace in DEBUG builds (security best practice)
        if (BuildConfig.DEBUG && error != null) {
            error.printStackTrace();
        }
    }
    
    /**
     * Log error with custom message
     * 
     * @param context Context where error occurred
     * @param message Custom error message
     * @param error The exception/error
     */
    public static void logErrorWithMessage(String context, String message, Throwable error) {
        logErrorWithMessage(TAG, context, message, error);
    }
    
    /**
     * Log error with custom tag, context, and message
     * 
     * @param tag Log tag
     * @param context Context where error occurred
     * @param message Custom error message
     * @param error The exception/error
     */
    public static void logErrorWithMessage(String tag, String context, String message, Throwable error) {
        LogUtil.e(tag, "❌ Error in " + context + ": " + message);
        
        if (error != null) {
            String errorMsg = error.getMessage() != null ? error.getMessage() : "Unknown error";
            LogUtil.e(tag, "   Cause: " + errorMsg);
            
            // Only print stack trace in DEBUG builds
            if (BuildConfig.DEBUG) {
                error.printStackTrace();
            }
        }
    }
    
    /**
     * Log warning (non-critical error)
     * 
     * @param context Context where warning occurred
     * @param message Warning message
     */
    public static void logWarning(String context, String message) {
        logWarning(TAG, context, message);
    }
    
    /**
     * Log warning with custom tag
     * 
     * @param tag Log tag
     * @param context Context where warning occurred
     * @param message Warning message
     */
    public static void logWarning(String tag, String context, String message) {
        LogUtil.e(tag, "⚠️ Warning in " + context + ": " + message);
    }
}

