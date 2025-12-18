package com.neo.neopayplus.emv

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

/**
 * Terminal Risk Manager (EMV Book 3 - Terminal Risk Management).
 *
 * Responsibilities:
 * - Floor limit enforcement (force online when amount >= limit)
 * - Random online selection (configurable percentage)
 * - Velocity / frequency control per PAN within a sliding time window
 *
 * The current deployment (Meeza) is online-only, but auditors still
 * require explicit risk controls and logging.
 */
class TerminalRiskManager(
    private val floorLimitMinor: Long,
    private val randomOnlinePercentage: Int,
    private val velocityWindowSeconds: Long,
    private val velocityMaxPerWindow: Int
) {

    private val secureRandom = SecureRandom()
    private val panHistory: ConcurrentHashMap<String, MutableList<Long>> = ConcurrentHashMap()

    /**
     * Evaluates whether a transaction must go online.
     *
     * @param panMasked Masked PAN (first 6/last 4 digits only)
     * @param amountMinor Amount in minor units (12-digit numeric string converted to long)
     * @param nowEpochSec Current epoch seconds (test hook)
     */
    fun shouldGoOnline(
        panMasked: String,
        amountMinor: Long,
        nowEpochSec: Long = System.currentTimeMillis() / 1000
    ): Boolean {
        if (amountMinor >= floorLimitMinor) {
            LogUtil.e(Constant.TAG, "TerminalRiskManager: Floor limit breached ($amountMinor >= $floorLimitMinor)")
            return true
        }

        if (isVelocityExceeded(panMasked, nowEpochSec)) {
            LogUtil.e(Constant.TAG, "TerminalRiskManager: Velocity exceeded for $panMasked")
            return true
        }

        if (randomOnlinePercentage in 1..99) {
            val value = secureRandom.nextInt(100)
            if (value < randomOnlinePercentage) {
                LogUtil.e(Constant.TAG, "TerminalRiskManager: Random online triggered ($value < $randomOnlinePercentage)")
                return true
            }
        }

        return false
    }

    /**
     * Records a successful transaction for velocity tracking.
     */
    fun recordTransaction(panMasked: String, nowEpochSec: Long = System.currentTimeMillis() / 1000) {
        if (panMasked.isBlank()) return
        val list = panHistory.computeIfAbsent(panMasked) { mutableListOf() }
        synchronized(list) {
            list.add(nowEpochSec)
            list.removeIf { it < nowEpochSec - velocityWindowSeconds }
            while (list.size > 1000) {
                list.removeAt(0)
            }
        }
    }

    fun clear() {
        panHistory.clear()
    }

    private fun isVelocityExceeded(panMasked: String, nowEpochSec: Long): Boolean {
        if (panMasked.isBlank()) return false
        val list = panHistory[panMasked] ?: return false
        synchronized(list) {
            list.removeIf { it < nowEpochSec - velocityWindowSeconds }
            return list.size >= velocityMaxPerWindow
        }
    }
}

