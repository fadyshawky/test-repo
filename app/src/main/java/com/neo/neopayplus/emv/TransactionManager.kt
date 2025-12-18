package com.neo.neopayplus.emv

import android.content.Context
import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2
import sunmi.paylib.SunmiPayKernel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * CRITICAL: Singleton EMV Service Manager
 * 
 * This is the ONLY place that handles EMV service binding.
 * 
 * RULES:
 * 1. Service binds ONCE at application startup
 * 2. Service instance is cached and reused for all transactions
 * 3. Never rebind during transactions
 * 4. Never create new EmvManager instances
 * 5. Logging is enabled ONCE when service connects
 * 
 * This fixes:
 * - Repeated "new EmvManager" logs (hundreds per second)
 * - Disappearing TLV parameters
 * - Unstable kernel configuration
 * - TLV overrides not sticking
 * - Kernel logs not appearing
 * - Random -4125 errors
 */
object TransactionManager {
    
    private var emvOptV2: EMVOptV2? = null
    private var readCardOptV2: ReadCardOptV2? = null
    private var pinPadOptV2: PinPadOptV2? = null
    private var securityOptV2: SecurityOptV2? = null
    private var basicOptV2: BasicOptV2? = null
    
    private val isBinding = AtomicBoolean(false)
    private val isReady = AtomicBoolean(false)
    private var connectCallback: ((Boolean) -> Unit)? = null
    private var onServicesReadyCallback: Runnable? = null
    
    /**
     * Initialize EMV service binding (call ONCE from Application.onCreate)
     */
    @JvmStatic
    fun init(context: Context) {
        if (isReady.get()) {
            LogUtil.e(Constant.TAG, "TransactionManager: Service already ready, skipping init")
            return
        }
        
        if (isBinding.get()) {
            LogUtil.e(Constant.TAG, "TransactionManager: Service binding already in progress, skipping")
            return
        }
        
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "=== TransactionManager: INITIALIZING EMV SERVICE ===")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "CRITICAL: This should happen ONCE per app lifetime")
        LogUtil.e(Constant.TAG, "If you see this multiple times, binding is broken")
        LogUtil.e(Constant.TAG, "")
        
        isBinding.set(true)
        bindPaySDKService(context)
    }
    
    /**
     * Bind PaySDK service (internal - only called from init)
     */
    private fun bindPaySDKService(context: Context) {
        val payKernel = SunmiPayKernel.getInstance()
        
        // Try to enable EMV L2 split library (call BEFORE initPaySDK per SDK demo)
        try {
            payKernel.setEmvL2Split(true)
            LogUtil.e(Constant.TAG, "TransactionManager: ✓ setEmvL2Split(true) called")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ setEmvL2Split failed: ${e.message}")
        }
        
        val success = payKernel.initPaySDK(context, object : SunmiPayKernel.ConnectCallback {
            override fun onConnectPaySDK() {
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
                LogUtil.e(Constant.TAG, "=== TransactionManager: SERVICE CONNECTED ===")
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
                LogUtil.e(Constant.TAG, "CRITICAL: This callback should fire ONCE")
                LogUtil.e(Constant.TAG, "If you see this multiple times, service is rebinding")
                LogUtil.e(Constant.TAG, "")
                
                // CRITICAL: Cache service instances (SINGLE INSTANCE - reused throughout app lifetime)
                // This follows the exact pattern from Sunmi SDK demo:
                // - Demo: emvOptV2 = payKernel.mEMVOptV2 (in onConnectPaySDK callback)
                // - We do the same: cache once, reuse forever
                // - NEVER call initPaySDK() again or rebind during transactions
                emvOptV2 = payKernel.mEMVOptV2
                readCardOptV2 = payKernel.mReadCardOptV2
                pinPadOptV2 = payKernel.mPinPadOptV2
                securityOptV2 = payKernel.mSecurityOptV2
                basicOptV2 = payKernel.mBasicOptV2
                
                // Log instance IDs to verify single instance (matches demo pattern)
                LogUtil.e(Constant.TAG, "TransactionManager: Cached service instances (following SDK demo pattern):")
                LogUtil.e(Constant.TAG, "  emvOptV2 instance ID: ${System.identityHashCode(emvOptV2)}")
                LogUtil.e(Constant.TAG, "  readCardOptV2 instance ID: ${System.identityHashCode(readCardOptV2)}")
                LogUtil.e(Constant.TAG, "  pinPadOptV2 instance ID: ${System.identityHashCode(pinPadOptV2)}")
                LogUtil.e(Constant.TAG, "  securityOptV2 instance ID: ${System.identityHashCode(securityOptV2)}")
                LogUtil.e(Constant.TAG, "  basicOptV2 instance ID: ${System.identityHashCode(basicOptV2)}")
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "  ✓✓✓ SINGLE INSTANCE PATTERN (matches SDK demo):")
                LogUtil.e(Constant.TAG, "     - SunmiPayKernel.getInstance() → singleton")
                LogUtil.e(Constant.TAG, "     - payKernel.initPaySDK() → called ONCE")
                LogUtil.e(Constant.TAG, "     - emvOptV2 = payKernel.mEMVOptV2 → cached ONCE")
                LogUtil.e(Constant.TAG, "     - These SAME instances reused for ALL transactions")
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "  CRITICAL: This is the ONLY place where we get instances from payKernel")
                LogUtil.e(Constant.TAG, "           All other code should use TransactionManager.getEmv(), etc.")
                
                isBinding.set(false)
                isReady.set(true)
                
                // Initialize ServiceLocator (just KeyRegistry - services come from TransactionManager)
                // CRITICAL: ServiceLocator no longer stores duplicate service references.
                // All services are obtained directly from TransactionManager (single source of truth).
                com.neo.neopayplus.app.di.ServiceLocator.initialize(
                    context.applicationContext,
                    emvOptV2!!,
                    readCardOptV2!!,
                    pinPadOptV2!!,
                    securityOptV2!!
                )
                LogUtil.e(Constant.TAG, "TransactionManager: ✓ ServiceLocator initialized (services come from TransactionManager)")
                
                // CRITICAL: Initialize SdkWrapper to minimize SDK calls
                com.neo.neopayplus.emv.SdkWrapper.init()
                
                // Enable kernel logging ONCE (critical for -4125 debugging)
                enableKernelLogging()
                
                // Notify callback if registered
                connectCallback?.invoke(true)
                connectCallback = null
                
                // Notify services ready callback (for provisioning, etc.)
                onServicesReadyCallback?.run()
                
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
                LogUtil.e(Constant.TAG, "=== TransactionManager: READY ===")
                LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
                LogUtil.e(Constant.TAG, "Service instances cached. All transactions will reuse these.")
                LogUtil.e(Constant.TAG, "You should see 'new EmvManager' ONCE in logcat (from SDK)")
                LogUtil.e(Constant.TAG, "If you see it multiple times, something is wrong.")
                LogUtil.e(Constant.TAG, "")
            }
            
            override fun onDisconnectPaySDK() {
                LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ SERVICE DISCONNECTED")
                LogUtil.e(Constant.TAG, "This should NOT happen during normal operation")
                
                // Clear cached instances
                emvOptV2 = null
                readCardOptV2 = null
                pinPadOptV2 = null
                securityOptV2 = null
                basicOptV2 = null
                
                isBinding.set(false)
                isReady.set(false)
                
                connectCallback?.invoke(false)
                connectCallback = null
            }
        })
        
        if (!success) {
            LogUtil.e(Constant.TAG, "TransactionManager: ❌ initPaySDK returned false")
            isBinding.set(false)
            connectCallback?.invoke(false)
            connectCallback = null
        }
    }
    
    /**
     * Enable kernel-level EMV logging (called ONCE when service connects)
     */
    private fun enableKernelLogging() {
        LogUtil.e(Constant.TAG, "")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "=== ENABLING KERNEL-LEVEL EMV LOGGING ===")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        
        // Method 1: Try PayLib logging
        try {
            val payLibClass = Class.forName("sunmi.paylib.PayLib")
            val getInstanceMethod = payLibClass.getMethod("getInstance")
            val payLibInstance = getInstanceMethod.invoke(null)
            
            val enableLogMethod = payLibClass.getMethod("enableLog", Boolean::class.java)
            val setLogLevelMethod = payLibClass.getMethod("setLogLevel", Int::class.java)
            
            enableLogMethod.invoke(payLibInstance, true)
            setLogLevelMethod.invoke(payLibInstance, 3) // LOG_LEVEL_ALL
            
            LogUtil.e(Constant.TAG, "TransactionManager: ✓ PayLib logging enabled")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ PayLib logging not available: ${e.message}")
        }
        
        // Method 2: Try EMVHandler logging (MOST IMPORTANT)
        try {
            val emvHandlerClass = Class.forName("sunmi.paylib.EMVHandler")
            val getInstanceMethod = emvHandlerClass.getMethod("getInstance")
            val emvHandlerInstance = getInstanceMethod.invoke(null)
            
            val enableLogMethod = emvHandlerClass.getMethod("enableLog", Boolean::class.java)
            val setLogLevelMethod = emvHandlerClass.getMethod("setLogLevel", Int::class.java)
            
            enableLogMethod.invoke(emvHandlerInstance, true)
            setLogLevelMethod.invoke(emvHandlerInstance, 2) // LOG_LEVEL_VERBOSE
            
            LogUtil.e(Constant.TAG, "TransactionManager: ✓✓✓ EMVHandler logging enabled (MOST IMPORTANT)")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ EMVHandler logging not available: ${e.message}")
        }
        
        // Method 3: Try EmvKernelConfig trace level
        try {
            val emvKernelConfigClass = Class.forName("sunmi.paylib.EmvKernelConfig")
            val setTraceLevelMethod = emvKernelConfigClass.getMethod("setTraceLevel", Int::class.java)
            setTraceLevelMethod.invoke(null, 3) // TRACE_LEVEL_ALL
            
            LogUtil.e(Constant.TAG, "TransactionManager: ✓ EmvKernelConfig trace enabled")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ EmvKernelConfig trace not available: ${e.message}")
        }
        
        // Method 4: Use basicOptV2.logControl() as fallback
        try {
            val basicOpt = basicOptV2
            if (basicOpt != null) {
                val logBundle = android.os.Bundle().apply {
                    putBoolean("openLog", true)
                    putBoolean("showConsoleLog", true)
                    putBoolean("printLogToFile", true)
                }
                val logCode = basicOpt.logControl(logBundle)
                if (logCode == 0) {
                    LogUtil.e(Constant.TAG, "TransactionManager: ✓✓✓ basicOptV2.logControl() SUCCESS")
                } else {
                    LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ basicOptV2.logControl() returned code=$logCode")
                }
            }
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "TransactionManager: ⚠️ basicOptV2.logControl() failed: ${e.message}")
        }
        
        LogUtil.e(Constant.TAG, "")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "=== KERNEL LOGGING ENABLED ===")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "Use these logcat filters to see kernel logs:")
        LogUtil.e(Constant.TAG, "  adb logcat -s EMV_PROV:V EMV_KERNEL:V PAYLIB:V SUNMI_PAY:V")
        LogUtil.e(Constant.TAG, "")
    }
    
    /**
     * Check if EMV service is ready
     */
    @JvmStatic
    fun isReady(): Boolean = isReady.get()
    
    /**
     * Get cached EMV service (throws if not ready)
     */
    @JvmStatic
    fun getEmv(): EMVOptV2 {
        return emvOptV2 ?: throw IllegalStateException(
            "EMV service not ready. Call TransactionManager.init() first."
        )
    }
    
    /**
     * Get cached ReadCard service (throws if not ready)
     */
    @JvmStatic
    fun getReadCard(): ReadCardOptV2 {
        return readCardOptV2 ?: throw IllegalStateException(
            "ReadCard service not ready. Call TransactionManager.init() first."
        )
    }
    
    /**
     * Get cached PinPad service (throws if not ready)
     */
    @JvmStatic
    fun getPinPad(): PinPadOptV2 {
        return pinPadOptV2 ?: throw IllegalStateException(
            "PinPad service not ready. Call TransactionManager.init() first."
        )
    }
    
    /**
     * Get cached Security service (throws if not ready)
     */
    @JvmStatic
    fun getSecurity(): SecurityOptV2 {
        return securityOptV2 ?: throw IllegalStateException(
            "Security service not ready. Call TransactionManager.init() first."
        )
    }
    
    /**
     * Get cached Basic service (throws if not ready)
     */
    @JvmStatic
    fun getBasic(): BasicOptV2 {
        return basicOptV2 ?: throw IllegalStateException(
            "Basic service not ready. Call TransactionManager.init() first."
        )
    }
    
    /**
     * Wait for service to be ready (with timeout)
     * Returns true if ready, false if timeout
     */
    @JvmStatic
    @JvmOverloads
    fun waitForReady(timeoutMs: Long = 30000): Boolean {
        val startTime = System.currentTimeMillis()
        while (!isReady.get() && (System.currentTimeMillis() - startTime) < timeoutMs) {
            Thread.sleep(100)
        }
        return isReady.get()
    }
    
    /**
     * Register callback to be invoked when services are ready
     * (for provisioning, terminal config, etc.)
     */
    @JvmStatic
    fun setOnServicesReadyCallback(callback: Runnable) {
        onServicesReadyCallback = callback
        // If already ready, invoke immediately
        if (isReady.get()) {
            callback.run()
        }
    }
}


