package com.neo.neopayplus.emv

import android.util.Log
import com.neo.neopayplus.Constant
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.emv.EmvConfigurationManager
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.bean.AidV2
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.payservice.AidlConstantsV2
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * SDK Wrapper to minimize SDK calls and batch operations.
 * 
 * CRITICAL: Each SDK call (addAid, queryAidCapkList, setTlvList, etc.) triggers
 * the SDK service to create a new internal EmvManager instance. This wrapper:
 * 
 * 1. Caches all query results (AIDs, CAPKs, TLVs)
 * 2. Batches TLV writes where possible
 * 3. Only calls SDK when cache is invalid or operation is required
 * 4. Uses the singleton EMVOptV2 instance from TransactionManager
 * 
 * This dramatically reduces the number of "new EmvManager" logs from the SDK.
 */
object SdkWrapper {
    private const val TAG = "SDK_WRAPPER"
    
    // Singleton EMV instance (from TransactionManager)
    private var emv: EMVOptV2? = null
    
    // Cache for AID queries (0 = AIDs, 1 = CAPKs)
    private val aidCache = ConcurrentHashMap<Int, List<String>>()
    private val aidCacheValid = AtomicBoolean(false)
    
    // Cache for TLV reads (opCode -> tag -> value)
    private val tlvCache = ConcurrentHashMap<Int, ConcurrentHashMap<String, String>>()
    
    // Pending TLV writes (batched)
    private val pendingTlvWrites = ConcurrentHashMap<Int, MutableMap<String, String>>()
    private val tlvWriteLock = Any()
    
    /**
     * Initialize with singleton EMV instance from TransactionManager
     */
    @JvmStatic
    fun init() {
        emv = com.neo.neopayplus.emv.TransactionManager.getEmv()
        if (emv == null) {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.init() called but EMV singleton not available")
            return
        }
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "=== SdkWrapper initialized ===")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
        LogUtil.e(Constant.TAG, "CRITICAL: Using singleton emv instance ID: ${System.identityHashCode(emv)}")
        LogUtil.e(Constant.TAG, "  ✓ All SDK calls will use this SAME instance")
        LogUtil.e(Constant.TAG, "  ✓ Caching enabled to minimize SDK calls")
        LogUtil.e(Constant.TAG, "  ✓ Batching enabled for TLV writes")
        LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
    }
    
    /**
     * Get cached AID list (0 = AIDs, 1 = CAPKs)
     * Only queries SDK if cache is invalid
     */
    @JvmStatic
    fun getAidList(type: Int): List<String> {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.getAidList() called but EMV not initialized")
            return emptyList()
        }
        
        // Return cached if valid
        if (aidCacheValid.get() && aidCache.containsKey(type)) {
            val cached = aidCache[type] ?: emptyList()
            LogUtil.d(Constant.TAG, "SdkWrapper.getAidList(type=$type): Using cache (${cached.size} items)")
            return cached
        }
        
        // Query SDK (this will trigger SDK to create internal EmvManager)
        LogUtil.e(Constant.TAG, "SdkWrapper.getAidList(type=$type): Cache miss - querying SDK")
        LogUtil.e(Constant.TAG, "  ⚠️ This queryAidCapkList() call will trigger SDK internal EmvManager")
        try {
            val aidList = java.util.ArrayList<String>()
            val rc = emvInstance.queryAidCapkList(type, aidList)
            if (rc == 0) {
                val result = aidList.toList()
                aidCache[type] = result
                aidCacheValid.set(true)
                LogUtil.e(Constant.TAG, "  ✓ Cached ${result.size} items (type=$type)")
                return result
            } else {
                LogUtil.e(Constant.TAG, "  ❌ queryAidCapkList() returned rc=$rc")
                return emptyList()
            }
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "  ❌ queryAidCapkList() failed: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Invalidate AID cache (call after addAid/deleteAid)
     */
    @JvmStatic
    fun invalidateAidCache() {
        aidCacheValid.set(false)
        aidCache.clear()
        LogUtil.d(Constant.TAG, "SdkWrapper: AID cache invalidated")
    }
    
    /**
     * Add AID (calls SDK and invalidates cache)
     */
    @JvmStatic
    fun addAid(aid: AidV2): Int {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.addAid() called but EMV not initialized")
            return -1
        }
        
        LogUtil.e(Constant.TAG, "SdkWrapper.addAid(): Calling SDK (will trigger internal EmvManager)")
        LogUtil.e(Constant.TAG, "  Using singleton emv instance ID: ${System.identityHashCode(emvInstance)}")
        val rc = emvInstance.addAid(aid)
        
        // Invalidate cache after add
        if (rc == 0) {
            invalidateAidCache()
        }
        
        return rc
    }
    
    /**
     * Delete AID (calls SDK and invalidates cache)
     */
    @JvmStatic
    fun deleteAid(aidHex: String?): Int {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.deleteAid() called but EMV not initialized")
            return -1
        }
        
        LogUtil.e(Constant.TAG, "SdkWrapper.deleteAid($aidHex): Calling SDK (will trigger internal EmvManager)")
        val rc = emvInstance.deleteAid(aidHex)
        
        // Invalidate cache after delete
        if (rc == 0) {
            invalidateAidCache()
        }
        
        return rc
    }
    
    /**
     * Get TLV value (cached)
     */
    @JvmStatic
    fun getTlv(opCode: Int, tag: String): String? {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.getTlv() called but EMV not initialized")
            return null
        }
        
        // Check cache first
        val cacheKey = opCode
        val tagCache = tlvCache[cacheKey]
        if (tagCache != null && tagCache.containsKey(tag)) {
            LogUtil.d(Constant.TAG, "SdkWrapper.getTlv(opCode=$opCode, tag=$tag): Using cache")
            return tagCache[tag]
        }
        
        // Query SDK (this will trigger SDK to create internal EmvManager)
        LogUtil.d(Constant.TAG, "SdkWrapper.getTlv(opCode=$opCode, tag=$tag): Cache miss - querying SDK")
        try {
            val out = ByteArray(1024)
            val len = emvInstance.getTlvList(opCode, arrayOf(tag), out)
            if (len > 0) {
                val map = com.neo.neopayplus.emv.TLVUtil.buildTLVMap(out.copyOf(len))
                val value = map[tag]?.value
                
                // Cache result
                val cache = tlvCache.getOrPut(cacheKey) { ConcurrentHashMap() }
                if (value != null) {
                    cache[tag] = value
                }
                
                return value
            }
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "SdkWrapper.getTlv() failed: ${e.message}")
        }
        
        return null
    }
    
    /**
     * Batch TLV write (adds to pending writes, doesn't call SDK immediately)
     */
    @JvmStatic
    fun batchSetTlv(opCode: Int, tag: String, value: String) {
        synchronized(tlvWriteLock) {
            val pending = pendingTlvWrites.getOrPut(opCode) { mutableMapOf() }
            pending[tag] = value
            
            // Update cache immediately (optimistic)
            val cache = tlvCache.getOrPut(opCode) { ConcurrentHashMap() }
            cache[tag] = value
        }
        LogUtil.d(Constant.TAG, "SdkWrapper.batchSetTlv(opCode=$opCode, tag=$tag): Added to batch")
    }
    
    /**
     * Flush all pending TLV writes (calls SDK once with all tags/values)
     */
    @JvmStatic
    fun flushTlvWrites() {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.flushTlvWrites() called but EMV not initialized")
            return
        }
        
        synchronized(tlvWriteLock) {
            if (pendingTlvWrites.isEmpty()) {
                LogUtil.d(Constant.TAG, "SdkWrapper.flushTlvWrites(): No pending writes")
                return
            }
            
            // Group by opCode and flush each
            for ((opCode, tags) in pendingTlvWrites) {
                if (tags.isEmpty()) continue
                
                val tagArray = tags.keys.toTypedArray()
                val valueArray = tags.values.toTypedArray()
                
                LogUtil.e(Constant.TAG, "SdkWrapper.flushTlvWrites(): Flushing ${tags.size} TLVs for opCode=$opCode")
                LogUtil.e(Constant.TAG, "  ⚠️ This setTlvList() call will trigger SDK internal EmvManager")
                LogUtil.e(Constant.TAG, "  Using singleton emv instance ID: ${System.identityHashCode(emvInstance)}")
                
                try {
                    emvInstance.setTlvList(opCode, tagArray, valueArray)
                    LogUtil.e(Constant.TAG, "  ✓ Flushed ${tags.size} TLVs for opCode=$opCode")
                } catch (e: Exception) {
                    LogUtil.e(Constant.TAG, "  ❌ setTlvList() failed: ${e.message}")
                }
            }
            
            pendingTlvWrites.clear()
        }
    }
    
    /**
     * Set TLV immediately (for critical operations that can't be batched)
     */
    @JvmStatic
    fun setTlvImmediate(opCode: Int, tag: String, value: String) {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.setTlvImmediate() called but EMV not initialized")
            return
        }
        
        LogUtil.e(Constant.TAG, "SdkWrapper.setTlvImmediate(opCode=$opCode, tag=$tag): Calling SDK immediately")
        LogUtil.e(Constant.TAG, "  ⚠️ This setTlvList() call will trigger SDK internal EmvManager")
        LogUtil.e(Constant.TAG, "  Using singleton emv instance ID: ${System.identityHashCode(emvInstance)}")
        
        try {
            emvInstance.setTlvList(opCode, arrayOf(tag), arrayOf(value))
            
            // Update cache
            val cache = tlvCache.getOrPut(opCode) { ConcurrentHashMap() }
            cache[tag] = value
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "SdkWrapper.setTlvImmediate() failed: ${e.message}")
        }
    }
    
    /**
     * Invalidate TLV cache (call when TLVs may have changed externally)
     */
    @JvmStatic
    fun invalidateTlvCache() {
        tlvCache.clear()
        LogUtil.d(Constant.TAG, "SdkWrapper: TLV cache invalidated")
    }
    
    /**
     * Get raw EMV instance (for operations not yet wrapped)
     */
    @JvmStatic
    fun getEmvInstance(): EMVOptV2? = emv

    /**
     * Build and install AIDs according to transaction type, matching colleague pattern.
     *
     * - PURCHASE / REVERSAL: normal TACs
     * - REFUND / VOID: refund TACs (if defined), otherwise normal TACs
     *
     * Uses default_aids.json as the source of AID definitions.
     */
    @JvmStatic
    fun installDefaultAidsForTransactionType(transactionType: TransactionType) {
        val emvInstance = emv ?: run {
            LogUtil.e(Constant.TAG, "⚠️ SdkWrapper.installDefaultAidsForTransactionType() called but EMV not initialized")
            return
        }

        try {
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            LogUtil.e(Constant.TAG, "=== Installing default AIDs for transactionType=$transactionType ===")
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            LogUtil.e(Constant.TAG, "  Using singleton emv instance ID: ${System.identityHashCode(emvInstance)}")

            // Build AidV2 list from JSON using EmvConfigurationManager helper
            val aids: List<AidV2> =
                EmvConfigurationManager.getDefaultAidsForTransactionType(transactionType)

            if (aids.isEmpty()) {
                LogUtil.e(Constant.TAG, "⚠️ No AIDs built for transactionType=$transactionType (default_aids.json empty?)")
                return
            }

            LogUtil.e(Constant.TAG, "  Built ${aids.size} AIDs from default_aids.json for $transactionType")

            // Install each AID: delete then add, like colleague pattern
            var success = 0
            var fail = 0

            aids.forEach { aid ->
                try {
                    val aidHex = if (aid.aid != null && aid.aid.isNotEmpty()) {
                        ByteUtil.bytes2HexStr(aid.aid, 0, aid.aid.size)
                    } else {
                        ""
                    }

                    if (aidHex.isEmpty()) {
                        LogUtil.e(Constant.TAG, "  ⚠️ Skipping AID with empty aid field")
                        fail++
                        return@forEach
                    }

                    // Delete existing AID first
                    val delRc = deleteAid(aidHex)
                    if (delRc == 0) {
                        LogUtil.e(Constant.TAG, "  🗑️ Deleted existing AID $aidHex before re-installation")
                        // small delay to let SDK settle
                        try { Thread.sleep(50) } catch (_: InterruptedException) {}
                    }

                    // Add new AID
                    val addRc = addAid(aid)
                    if (addRc == 0) {
                        LogUtil.e(Constant.TAG, "  ✓ Installed AID $aidHex for $transactionType (selFlag=${aid.selFlag}, kernelType=${aid.kernelType})")
                        success++
                    } else {
                        LogUtil.e(Constant.TAG, "  ❌ addAid failed for $aidHex, rc=$addRc")
                        fail++
                    }
                } catch (e: Exception) {
                    LogUtil.e(Constant.TAG, "  ❌ Error installing AID for $transactionType: ${e.message}")
                    fail++
                }
            }

            LogUtil.e(Constant.TAG, "=== AID install for $transactionType complete: $success ok, $fail failed ===")
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "❌ installDefaultAidsForTransactionType($transactionType) failed: ${e.message}")
        }
    }
}


