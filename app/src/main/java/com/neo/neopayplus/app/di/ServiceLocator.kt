package com.neo.neopayplus.app.di

import android.content.Context
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.KeyManager
import com.neo.neopayplus.emv.PinPadManager
import com.neo.neopayplus.keys.KeyRegistry
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2

/**
 * Lightweight service locator for dependency injection.
 * Services are initialized after PaySDK connects in MyApplication.
 */
object ServiceLocator {

    // CRITICAL: Use TransactionManager as SINGLE SOURCE OF TRUTH for all service instances
    // Do NOT store duplicate references - always get from TransactionManager
    // This ensures we use the SAME instance throughout the entire transaction lifecycle
    
    private var keyManagerRef: KeyManager? = null
    private var pinPadManagerRef: PinPadManager? = null

    /**
     * Initialize ServiceLocator (just KeyRegistry initialization)
     * 
     * CRITICAL: Services are now managed by TransactionManager (single source of truth).
     * This method only initializes KeyRegistry - all service instances come from TransactionManager.
     * 
     * @param context Application context
     * @param emvOpt Ignored - use TransactionManager.getEmv() instead
     * @param readCard Ignored - use TransactionManager.getReadCard() instead
     * @param pinPad Ignored - use TransactionManager.getPinPad() instead
     * @param securityOpt Ignored - use TransactionManager.getSecurity() instead
     */
    @JvmStatic
    fun initialize(
        context: Context,
        emvOpt: EMVOptV2,
        readCard: ReadCardOptV2,
        pinPad: PinPadOptV2,
        securityOpt: SecurityOptV2
    ) {
        // Only initialize KeyRegistry - services come from TransactionManager
        KeyRegistry.init(context.applicationContext)
        LogUtil.e(Constant.TAG, "ServiceLocator: KeyRegistry initialized")
        LogUtil.e(Constant.TAG, "  ✓ All service instances will be obtained from TransactionManager (single source of truth)")
    }

    /**
     * Check if all required services are available
     * Uses TransactionManager as source of truth
     */
    @JvmStatic
    fun isReady(): Boolean {
        return com.neo.neopayplus.emv.TransactionManager.isReady()
    }

    @JvmStatic
    fun keyManager(): KeyManager {
        val existing = keyManagerRef
        if (existing != null) return existing
        // Create KeyManager using instance from TransactionManager (single source of truth)
        val security = com.neo.neopayplus.emv.TransactionManager.getSecurity()
        return KeyManager(security).also { keyManagerRef = it }
    }

    @JvmStatic
    fun pinPadManager(): PinPadManager {
        val existing = pinPadManagerRef
        if (existing != null) return existing
        val pinPadOpt = com.neo.neopayplus.emv.TransactionManager.getPinPad()
        return PinPadManager(pinPadOpt).also { pinPadManagerRef = it }
    }
}
