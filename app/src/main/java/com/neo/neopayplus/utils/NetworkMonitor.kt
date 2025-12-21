package com.neo.neopayplus.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import com.neo.neopayplus.Constant
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Monitors network connectivity and triggers retry of failed network operations
 * when network is reconnected.
 */
class NetworkMonitor private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = Constant.TAG
        @Volatile
        private var instance: NetworkMonitor? = null
        
        @JvmStatic
        fun getInstance(context: Context): NetworkMonitor {
            return instance ?: synchronized(this) {
                instance ?: NetworkMonitor(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val connectivityManager: ConnectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val pendingOperations = CopyOnWriteArrayList<Runnable>()
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isNetworkAvailable = false
    private var isMonitoring = false
    
    /**
     * Check if network is currently available
     */
    fun isNetworkAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            )
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }
    
    /**
     * Start monitoring network connectivity
     */
    fun startMonitoring() {
        if (isMonitoring) {
            LogUtil.e(TAG, "NetworkMonitor: Already monitoring")
            return
        }
        
        isMonitoring = true
        isNetworkAvailable = isNetworkAvailable()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    LogUtil.e(TAG, "NetworkMonitor: Network available")
                    val wasOffline = !isNetworkAvailable
                    isNetworkAvailable = true
                    
                    if (wasOffline) {
                        LogUtil.e(TAG, "NetworkMonitor: Network reconnected - retrying pending operations")
                        retryPendingOperations()
                    }
                }
                
                override fun onLost(network: Network) {
                    LogUtil.e(TAG, "NetworkMonitor: Network lost")
                    isNetworkAvailable = false
                }
                
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    val hasInternet = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                    )
                    val isValidated = networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                    
                    if (hasInternet && isValidated && !isNetworkAvailable) {
                        LogUtil.e(TAG, "NetworkMonitor: Network validated - retrying pending operations")
                        isNetworkAvailable = true
                        retryPendingOperations()
                    }
                }
            }
            
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build()
            
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
            LogUtil.e(TAG, "NetworkMonitor: Started monitoring (API ${Build.VERSION.SDK_INT})")
        } else {
            // Fallback for older Android versions
            LogUtil.e(TAG, "NetworkMonitor: Using basic monitoring (API ${Build.VERSION.SDK_INT})")
        }
    }
    
    /**
     * Stop monitoring network connectivity
     */
    fun stopMonitoring() {
        if (!isMonitoring) {
            return
        }
        
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
        
        isMonitoring = false
        LogUtil.e(TAG, "NetworkMonitor: Stopped monitoring")
    }
    
    /**
     * Register an operation to retry when network is reconnected
     * If network is already available, the operation is executed immediately
     */
    @JvmName("registerRetryOperation")
    fun registerRetryOperation(operation: Runnable) {
        if (isNetworkAvailable()) {
            LogUtil.e(TAG, "NetworkMonitor: Network available - executing operation immediately")
            try {
                operation.run()
            } catch (e: Exception) {
                LogUtil.e(TAG, "NetworkMonitor: Error executing operation: ${e.message}")
                // Add to pending if execution fails
                pendingOperations.add(operation)
            }
        } else {
            LogUtil.e(TAG, "NetworkMonitor: Network unavailable - queuing operation for retry")
            pendingOperations.add(operation)
        }
    }
    
    /**
     * Retry all pending operations
     */
    private fun retryPendingOperations() {
        if (pendingOperations.isEmpty()) {
            LogUtil.e(TAG, "NetworkMonitor: No pending operations to retry")
            return
        }
        
        LogUtil.e(TAG, "NetworkMonitor: Retrying ${pendingOperations.size} pending operations")
        
        val operationsToRetry = ArrayList(pendingOperations)
        pendingOperations.clear()
        
        // Execute operations on background thread to avoid blocking
        Thread {
            operationsToRetry.forEach { operation ->
                try {
                    LogUtil.e(TAG, "NetworkMonitor: Executing pending operation...")
                    operation.run()
                    LogUtil.e(TAG, "NetworkMonitor: ✓ Operation completed successfully")
                } catch (e: Exception) {
                    LogUtil.e(TAG, "NetworkMonitor: ❌ Operation failed: ${e.message}")
                    // Re-add to pending if it fails again
                    pendingOperations.add(operation)
                }
            }
        }.start()
    }
    
    /**
     * Clear all pending operations
     */
    fun clearPendingOperations() {
        pendingOperations.clear()
        LogUtil.e(TAG, "NetworkMonitor: Cleared all pending operations")
    }
}
