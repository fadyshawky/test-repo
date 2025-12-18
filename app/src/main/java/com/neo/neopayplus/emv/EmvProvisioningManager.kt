package com.neo.neopayplus.emv

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Ensures EMV configuration (AIDs, CAPKs, terminal params) is loaded before a sale.
 * Runs lazily on first demand instead of at app startup.
 */
object EmvProvisioningManager {

    private val mutex = Mutex()
    @Volatile
    private var provisioned = false

    suspend fun ensureProvisioned(): Boolean {
        if (provisioned) return true
        return mutex.withLock {
            if (provisioned) return true
            val result = withContext(Dispatchers.IO) {
                try {
                    val ok = EmvConfigurationManager.getInstance().initialize()
                    if (!ok) {
                        LogUtil.e(Constant.TAG, "❌ EMV configuration initialization failed (lazy)")
                    } else {
                        LogUtil.e(Constant.TAG, "✓ EMV configuration initialized (lazy)")
                    }
                    ok
                } catch (t: Throwable) {
                    LogUtil.e(Constant.TAG, "❌ EMV provisioning error: ${t.message}")
                    false
                }
            }
            provisioned = result
            result
        }
    }
}

