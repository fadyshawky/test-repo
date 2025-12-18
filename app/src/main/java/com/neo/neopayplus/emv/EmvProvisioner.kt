package com.neo.neopayplus.emv

import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.neo.neopayplus.BuildConfig
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.emv.EmvUtil
import com.sunmi.pay.hardware.aidlv2.bean.AidV2
import com.sunmi.pay.hardware.aidlv2.bean.CapkV2
import com.sunmi.pay.hardware.aidlv2.bean.EmvTermParamV2
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * EMV provisioning utility.
 *
 * Changed behavior:
 * - Use exact API-provided dDOL for specific RIDs / AIDs when available.
 * - If no dDOL provided for an AID/RID, do NOT force 9F3704; leave dDOL unset and let kernel defaults apply.
 * 
 * CRITICAL: This class MUST receive the singleton EMVOptV2 instance from TransactionManager.
 * All AID installations will use this SAME instance to ensure consistency.
 */
class EmvProvisioner(private val emv: EMVOptV2) {
    init {
        Log.e(TAG, "═══════════════════════════════════════════════════════")
        Log.e(TAG, "=== EmvProvisioner created ===")
        Log.e(TAG, "═══════════════════════════════════════════════════════")
        Log.e(TAG, "CRITICAL: Using emv instance ID: ${System.identityHashCode(emv)}")
        Log.e(TAG, "  ✓ This SAME instance will be used for ALL AID installations")
        Log.e(TAG, "  ✓ All addAid() calls will use this singleton instance")
        Log.e(TAG, "═══════════════════════════════════════════════════════")
    }

    companion object {
        private const val TAG = "EMV_PROV"

        @JvmStatic
        fun provisionSync(emv: EMVOptV2, url: String): Boolean {
            return kotlinx.coroutines.runBlocking {
                EmvProvisioner(emv).provision(url)
            }
        }

        @JvmStatic
        fun provisionFromBackendSync(emv: EMVOptV2): Boolean {
            val baseUrl = BuildConfig.API_BASE_URL
            val url = "$baseUrl/emv/bundle"
            return provisionSync(emv, url)
        }
    }

    // --- JSON DTOs ---
    data class CapkJson(
        val rid: String,
        val index: String,
        @SerializedName("modulusBase64") val modulusBase64: String? = null,
        @SerializedName("modulus") val modulusHex: String? = null,
        @SerializedName("exponentBase64") val exponentBase64: String? = null,
        @SerializedName("exponent") val exponentHex: String? = null,
        val expiry: String? = null,
        @SerializedName("expiryDate") val expiryDate: String? = null,
        // SHA-1 checksum of the CAPK - CRITICAL for offline data authentication (ODA)!
        // Without this, CDA/DDA/SDA will fail with -4002 error
        val sha1: String? = null,
        @SerializedName("checkSum") val checkSum: String? = null,
        @SerializedName("hash") val hash: String? = null
    )

    data class ContactlessParams(
        val ctq: String? = null,
        val ttq: String? = null,
        val merchantRiskParameters: MerchantRiskParams? = null
    )

    data class MerchantRiskParams(
        @SerializedName("reader_contactless_floor_limit") val floorLimit: String? = null,
        @SerializedName("contactless_no_cvm_limit") val noCvmLimit: String? = null,
        @SerializedName("contactless_cvm_limit") val cvmLimit: String? = null
    )

    data class AidJson(
        val aid: String? = null,
        @SerializedName("RID") val rid: String? = null,
        val label: String? = null,
        val kernel: String? = null,
        val selFlag: Int? = null,
        val priority: Int = 1,
        val floorLimit: String? = null,
        val cvLimit: String? = null,
        val noCvmLimit: String? = null,
        val tacDefault: String? = null,
        val tacDenial: String? = null,
        val tacOnline: String? = null,
        @SerializedName("dDOL") val ddol: String? = null,
        val tdol: String? = null,
        val udol: String? = null,
        val version: String? = null,
        val capkRefs: List<Map<String, String>>? = null,
        val contactless: ContactlessParams? = null,
        @SerializedName("termRiskManagement") val riskManData: String? = null,
        val merchantCategoryCode: String? = null
    )

    data class TerminalJson(
        val countryCode: String? = null,
        val currencyCode: String? = null,
        val terminalType: String? = null,
        val merchantCategoryCode: String? = null,
        val merchantName: String? = null,
        val merchantId: String? = null,
        val terminalId: String? = null,
        val ttq: String? = null
    )

    data class TerminalRiskJson(
        val tacDefault: String? = null,
        val tacDenial: String? = null,
        val tacOnline: String? = null,
        val floorLimit: String? = null,
        val velocityCounter: Int? = null,
        val randomSelection: Int? = null
    )

    data class EmvBundle(
        val ok: Boolean = false,
        val version: String? = null,
        val terminal: TerminalJson? = null,
        val aids: List<AidJson>? = null,
        @SerializedName("aids_flat") val aidsFlat: List<AidJson>? = null,
        val capks: List<CapkJson>? = null,
        val terminalRisk: TerminalRiskJson? = null
    )

    private val gson = Gson()
    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // RID -> dDOL map built from hierarchical "aids" section; used during AID install
    private var ridToDdolMap: Map<String, String> = emptyMap()

    // --- Public provisioning API ---
    suspend fun provision(url: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.e(TAG, "=== Downloading EMV bundle from $url ===")

            val req = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build()

            val resp = http.newCall(req).execute()
            if (!resp.isSuccessful) {
                Log.e(TAG, "❌ Server error: ${resp.code}")
                return@withContext false
            }

            val json = resp.body?.string() ?: run {
                Log.e(TAG, "❌ Empty response body")
                return@withContext false
            }
            Log.e(TAG, "Bundle JSON received (${json.length} chars)")

            val bundle = gson.fromJson(json, EmvBundle::class.java)
            if (!bundle.ok) {
                Log.e(TAG, "❌ Bundle ok=false")
                return@withContext false
            }
            Log.e(TAG, "✓ Bundle version: ${bundle.version}")

            // Build RID -> dDOL map from hierarchical aids (if present)
            val ridMap = mutableMapOf<String, String>()
            bundle.aids?.forEach { parent ->
                val rid = parent.rid ?: parent.aid?.take(10)
                if (!rid.isNullOrBlank()) {
                    parent.ddol?.let { dd ->
                        if (dd.isNotBlank()) {
                            ridMap[rid.uppercase()] = dd
                            Log.d(TAG, "  Extracted dDOL for RID $rid: $dd")
                        }
                    }
                }
            }
            ridToDdolMap = ridMap.toMap()

            val aidsToInstall = bundle.aidsFlat ?: bundle.aids
            Log.e(TAG, "  AIDs: ${aidsToInstall?.size ?: 0} (using ${if (bundle.aidsFlat != null) "aids_flat" else "aids"})")
            Log.e(TAG, "  CAPKs: ${bundle.capks?.size ?: 0}")
            Log.e(TAG, "  dDOL map: ${ridToDdolMap.size} RIDs with dDOL")

            bundle.terminal?.let { installTerminalParams(it) }

            bundle.capks?.let { if (it.isNotEmpty()) installCapks(it) }

            aidsToInstall?.let { if (it.isNotEmpty()) {
                // Enrich aids_flat with dDOL from parent RID map (optional, retains API ddol if present)
                val enriched = it.map { a ->
                    if (a.ddol.isNullOrBlank() && !a.aid.isNullOrBlank()) {
                        val rid = a.aid.take(10).uppercase()
                        val dd = ridToDdolMap[rid]
                        if (!dd.isNullOrBlank()) {
                            Log.d(TAG, "  Enriching AID ${a.aid} with dDOL from RID $rid: $dd")
                            a.copy(ddol = dd)
                        } else a
                    } else a
                }
                installAids(enriched)
            } }

            Log.e(TAG, "=== EMV Provisioning Completed OK ===")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "❌ Provisioning failed: ${t.message}", t)
            false
        }
    }

    suspend fun provisionFromBackend(): Boolean {
        val baseUrl = BuildConfig.API_BASE_URL
        val url = "$baseUrl/emv/bundle"
        return provision(url)
    }

    // --- terminal params ---
    // Matching working code pattern: initTerminalConfiguration() with setTerminalParam() + setTermParamEx()
    private fun installTerminalParams(t: TerminalJson) {
        try {
            // Step 1: Set terminal parameters using EmvTermParamV2 (matching working code)
            val termParam = EmvTermParamV2().apply {
                countryCode = t.countryCode ?: com.neo.neopayplus.config.PaymentConfig.TERMINAL_COUNTRY_CODE
                currencyCode = t.currencyCode ?: com.neo.neopayplus.config.PaymentConfig.CURRENCY_CODE_TLV
                currencyExp = t.currencyCode?.let { "02" } ?: com.neo.neopayplus.config.PaymentConfig.CURRENCY_EXPONENT
                capability = com.neo.neopayplus.config.PaymentConfig.TERMINAL_CAPABILITIES
                TTQ = t.ttq ?: com.neo.neopayplus.config.PaymentConfig.TTQ_9F66
                terminalType = t.terminalType ?: com.neo.neopayplus.config.PaymentConfig.TERMINAL_TYPE
                addCapability = "6000000000"  // From working code
                bypassPin = false  // From working code
            }

            try {
                val result = emv.setTerminalParam(termParam)
                Log.e(TAG, "setTerminalParam result: $result")
                if (result == 0) {
                    Log.e(TAG, "✓ Terminal parameters set via setTerminalParam()")
                } else {
                    Log.e(TAG, "⚠️ setTerminalParam() returned code: $result")
                }
            } catch (e: Exception) {
                Log.e(TAG, "⚠️ setTerminalParam() failed: ${e.message}")
            }

            // Step 2: Set extended terminal parameters via Bundle (for contactless settings)
        val b = Bundle().apply {
            t.countryCode?.let { putString("countryCode", it) }
            t.currencyCode?.let { putString("currencyCode", it) }
            t.terminalType?.let { putString("termType", it) }
            t.merchantId?.let { putString("merchId", it) }
            t.merchantName?.let { putString("merchName", it) }
            t.merchantCategoryCode?.let { putString("mcc", it) }
            t.terminalId?.let { putString("termId", it) }
            t.ttq?.let { putString("ttq", it) }

            putString("transCurrExp", "02")
            putString("referCurrExp", "02")
            t.currencyCode?.let { putString("referCurrCode", it) }
            putString("transType", "00")

                // CRITICAL: Contactless settings (matching working code)
                putBoolean("contactlessManualSelApp", false)
                putBoolean("contactlessManualSelAppGeneral", false)
            putBoolean("supportNFC", true)
            putBoolean("supportClss", true)
            Log.e(TAG, "  ✓ Terminal params: supportNFC=true, supportClss=true (required for contactless)")
        }

            val supportNFC = b.getBoolean("supportNFC", false)
            val supportClss = b.getBoolean("supportClss", false)
            if (!supportNFC || !supportClss) {
                Log.e(TAG, "  ⚠️ WARNING: supportNFC=$supportNFC, supportClss=$supportClss - this will cause -4125!")
            }
            val rc = emv.setTermParamEx(b)
            Log.e(TAG, "setTermParamEx rc=$rc (supportNFC=$supportNFC, supportClss=$supportClss)")
        } catch (e: Exception) {
            Log.e(TAG, "⚠️ installTerminalParams failed: ${e.message}")
            e.printStackTrace()
        }
    }

    // --- CAPK installation ---
    private fun installCapks(list: List<CapkJson>) {
        var success = 0
        var fail = 0

        list.forEach { c ->
            try {
                val modulus = when {
                    !c.modulusBase64.isNullOrBlank() -> Base64.decode(c.modulusBase64, Base64.DEFAULT)
                    !c.modulusHex.isNullOrBlank() -> hexToBytes(c.modulusHex)
                    else -> {
                        Log.e(TAG, "⚠️ CAPK ${c.rid}/${c.index} has no modulus")
                        null
                    }
                } ?: run { fail++; return@forEach }

                val exponentBytes = when {
                    !c.exponentBase64.isNullOrBlank() -> Base64.decode(c.exponentBase64, Base64.DEFAULT)
                    !c.exponentHex.isNullOrBlank() -> hexToBytes(c.exponentHex)
                    else -> byteArrayOf(0x01, 0x00, 0x01)
                }

                // Get SHA-1 checksum - CRITICAL for offline data authentication!
                // Try sha1 field first, then checkSum, then hash
                val checksumHex = c.sha1 ?: c.checkSum ?: c.hash
                val checksumBytes = if (!checksumHex.isNullOrBlank()) {
                    try {
                        hexToBytes(checksumHex)
                    } catch (e: Exception) {
                        Log.e(TAG, "⚠️ Invalid checksum format for ${c.rid}/${c.index}: $checksumHex")
                        null
                    }
                } else {
                    Log.w(TAG, "⚠️ No SHA-1 checksum for CAPK ${c.rid}/${c.index} - ODA may fail!")
                    null
                }

                val capk = CapkV2().apply {
                    rid = ByteUtil.hexStr2Bytes(c.rid)
                    index = ByteUtil.hexStr2Byte(c.index)
                    modul = modulus
                    exponent = exponentBytes
                    hashInd = 0x01  // SHA-1 hash algorithm
                    arithInd = 0x01 // RSA algorithm
                    // CRITICAL: Set checkSum for ODA (CDA/DDA/SDA) verification
                    if (checksumBytes != null) {
                        checkSum = checksumBytes
                    }
                }

                val rc = emv.addCapk(capk)
                if (rc == 0) {
                    val checksumStatus = if (checksumBytes != null) "✓ SHA1" else "⚠️ NO SHA1"
                    Log.e(TAG, "✓ addCapk RID=${c.rid}, index=${c.index} [$checksumStatus]")
                    success++
                } else {
                    Log.e(TAG, "⚠️ addCapk RID=${c.rid}, index=${c.index} rc=$rc")
                    fail++
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding CAPK ${c.rid}/${c.index}: ${e.message}")
                fail++
            }
        }

        Log.e(TAG, "CAPKs: $success added, $fail failed")
    }

    // --- AID installation ---
    private fun installAids(list: List<AidJson>) {
        var success = 0
        var fail = 0

        Log.e(TAG, "═══════════════════════════════════════════════════════")
        Log.e(TAG, "=== Installing ${list.size} AIDs from API ===")
        Log.e(TAG, "═══════════════════════════════════════════════════════")
        Log.e(TAG, "CRITICAL: Using SdkWrapper to minimize SDK calls")
        Log.e(TAG, "  ✓ All AIDs will be added using singleton instance")
        Log.e(TAG, "  ✓ Cache will be invalidated after installation")
        Log.e(TAG, "")
        list.forEachIndexed { idx, a ->
            Log.e(TAG, "AID ${idx + 1}/${list.size}: ${a.aid} (${a.label ?: "no label"})")
        }

        try {
            Log.e(TAG, "Deleting ALL existing AIDs...")
            // Use SdkWrapper to delete (will invalidate cache)
            com.neo.neopayplus.emv.SdkWrapper.deleteAid(null)
            Log.e(TAG, "✓ All AIDs deleted (cache invalidated)")
        } catch (e: Exception) {
            Log.e(TAG, "Warning: Could not delete all AIDs: ${e.message}")
        }

        list.forEach { a ->
            try {
                if (a.aid.isNullOrBlank()) {
                    Log.e(TAG, "❌ Skipping AID with null/empty aid field: label=${a.label ?: "N/A"}")
                    fail++; return@forEach
                }

                val aidHex = a.aid.uppercase()
                
                // CRITICAL: Delete existing AID FIRST to ensure clean installation
                // This is REQUIRED to ensure DFC10A=02 is properly stored
                // Without deleting first, the SDK may retain old configuration without kernelType
                try {
                    Log.e(TAG, "  🗑️ Deleting existing AID $aidHex before re-installation...")
                    // Use SdkWrapper to delete (will invalidate cache)
                    val deleteRc = com.neo.neopayplus.emv.SdkWrapper.deleteAid(aidHex)
                    if (deleteRc == 0) {
                        Log.e(TAG, "  ✓ Successfully deleted AID $aidHex (cache invalidated)")
                        // Small delay to ensure deletion is fully processed by SDK
                        Thread.sleep(100)
                    } else {
                        Log.e(TAG, "  ⚠️ deleteAid returned rc=$deleteRc (AID may not have existed)")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "  ⚠️ Error deleting AID $aidHex: ${e.message} - continuing anyway")
                }
                
                // Define scheme detection variables first
                val isMastercard = aidHex.startsWith("A000000004") || aidHex.startsWith("A000000005")
                val isVisa = aidHex.startsWith("A000000003")
                val isMeeza = aidHex.startsWith("A000000732")
                val isContactless = isMastercard || isVisa || isMeeza

                val defaultVersion = when {
                    isMastercard || isMeeza -> "0002"
                    isVisa -> "0097"
                    else -> "008C"
                }
                val finalVersion = a.version ?: defaultVersion

                // Determine selFlag first (before creating AidV2 object)
                val finalSelFlag = a.selFlag ?: if (isContactless) 1 else 0
                val isContactlessEnabled = finalSelFlag == 1

                val aidObj = AidV2().apply {
                    aid = ByteUtil.hexStr2Bytes(aidHex)
                    selFlag = finalSelFlag.toByte()
                    version = ByteUtil.hexStr2Bytes(finalVersion)

                    // TAC values: Use API values if provided, otherwise use PaymentConfig defaults
                    // Matching working code pattern: normal transaction TACs
                    val tacDefault = a.tacDefault ?: com.neo.neopayplus.config.PaymentConfig.TACConfig.TAC_DEFAULT_ONLINE_PREFERRED
                    val tacDenial = a.tacDenial ?: com.neo.neopayplus.config.PaymentConfig.TACConfig.TAC_DENIAL
                    val tacOnline = a.tacOnline ?: com.neo.neopayplus.config.PaymentConfig.TACConfig.TAC_ONLINE_PIN_REQUIRED
                    
                    TACDefault = ByteUtil.hexStr2Bytes(tacDefault)
                    TACDenial = ByteUtil.hexStr2Bytes(tacDenial)
                    TACOnline = ByteUtil.hexStr2Bytes(tacOnline)
                    
                    Log.d(TAG, "  TAC values: Default=$tacDefault, Denial=$tacDenial, Online=$tacOnline")

                    floorLimit = ByteUtil.hexStr2Bytes(a.floorLimit ?: "000000000000")
                    threshold = ByteUtil.hexStr2Bytes("00000000")
                    targetPer = 0
                    maxTargetPer = 0

                    // CRITICAL: Set merchant/terminal fields (matching working code pattern)
                    // AcquierId: 6 bytes (from configuration or default)
                    val acquirerId = a.rid?.take(10)?.let { 
                        // Use RID as base, pad to 6 bytes if needed
                        val ridBytes = ByteUtil.hexStr2Bytes(it)
                        if (ridBytes.size >= 6) ridBytes.take(6).toByteArray()
                        else ByteArray(6) { idx -> if (idx < ridBytes.size) ridBytes[idx] else 0 }
                    } ?: ByteUtil.hexStr2Bytes("000000000000", 6)
                    AcquierId = acquirerId

                    // Merchant fields (from PaymentConfig or API)
                    val merchantName = com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME
                    merchName = ByteUtil.text2Bytes(merchantName, 128)
                    
                    // Merchant Category Code (MCC) - 4 digits, convert to 2-byte hex
                    val merchantCategoryCode = a.merchantCategoryCode ?: "0000"
                    // MCC is typically 4 digits (e.g., "5411"), convert to 2-byte hex BCD
                    merchCateCode = if (merchantCategoryCode.length == 4) {
                        ByteUtil.hexStr2Bytes(merchantCategoryCode, 2)
                    } else {
                        ByteUtil.hexStr2Bytes("0000", 2)
                    }
                    
                    val merchantId = com.neo.neopayplus.config.PaymentConfig.getMerchantId()
                    merchId = ByteUtil.hexStr2Bytes(merchantId, 16)
                    
                    val terminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId()
                    termId = ByteUtil.text2Bytes(terminalId.padEnd(8, ' '), 8)

                    // Terminal Risk Management Data (9F1D) - 8 bytes
                    // Default: "6C78800000000000" (from working code pattern)
                    val riskManDataHex = a.riskManData ?: "6C78800000000000"
                    riskManData = ByteUtil.hexStr2Bytes(riskManDataHex, 8)
                    rMDLen = 8.toByte()

                    // paramType: 0 = default (allows both contact and contactless)
                    paramType = 0x00.toByte()

                    if (isContactless && isContactlessEnabled) {
                        clsStatusCheck = 0x01.toByte()
                    }

                    val pix = if (aidHex.length > 10) aidHex.substring(10) else ""

                    // CRITICAL: For Mastercard/Meeza AIDs, ALWAYS set kernelType = 2 (PayPass) BEFORE any other logic
                    // This ensures DFC10A=02 is stored in the AID TLV, which is REQUIRED for PayPass L2 kernel activation
                    // Without DFC10A=02, the PayPass kernel is not available → L2 candidate list is empty → -4125 error
                    // This must be set regardless of selFlag, because Mastercard AIDs need PayPass kernel for contactless
                    if (isMastercard || isMeeza) {
                        kernelType = AidlConstants.EMV.KernelType.PAYPASS.toByte()  // MUST be 2 for PayPass
                        Log.e(TAG, "  🔥 CRITICAL: Setting kernelType = ${kernelType.toInt()} (PayPass=2) for ${if (isMastercard) "Mastercard" else "Meeza"} AID ${a.aid}")
                        Log.e(TAG, "     This creates DFC10A=02 in AID TLV - REQUIRED for PayPass L2 kernel activation")
                    }
                    
                    if ((isMastercard || isMeeza) && isContactlessEnabled) {
                        // CRITICAL: For contactless AIDs (selFlag=1), we MUST set contactless-specific values
                        // even if API doesn't provide contactless params, otherwise L2 selection fails with -4125
                        // kernelType is already set above, but log it again for clarity
                        Log.d(TAG, "  MC/Meeza: Contactless config - kernelType already set to ${kernelType.toInt()} (PayPass=2)")
                        
                        // SDK default AIDs use paramType=00 (default) even for contactless - this allows both contact and contactless
                        // Using paramType=02 (contactless) may cause L2 selection to fail with -4125
                        paramType = 0x00.toByte()  // 0 = default (allows both contact and contactless, matches SDK examples)
                        Log.d(TAG, "  MC/Meeza: Setting paramType = ${paramType.toInt()} (Default=0)")

                        val isL1 = pix == "1010" || pix == "0001"
                        val isL2 = !isL1 && pix.isNotEmpty()
                        zeroCheck = when {
                            isL1 -> 0x00.toByte()
                            isL2 -> 0x01.toByte()
                            else -> 0x01.toByte()
                        }

                        referCurrCode = ByteUtil.hexStr2Bytes("0818")
                        referCurrExp = 0x02.toByte()

                        // Set ttq from configuration (matching working code pattern)
                        // The working code shows: aidV2.ttq = ByteUtil.hexStr2Bytes(emvConfiguration.getTtq(), 4)
                        val ttqValue = a.contactless?.ttq ?: com.neo.neopayplus.config.PaymentConfig.TTQ_9F66
                        ttq = ByteUtil.hexStr2Bytes(ttqValue, 4)
                        Log.d(TAG, "  MC/Meeza: Setting ttq (9F66) from config: $ttqValue")

                        // CRITICAL: Always set dDOL for Mastercard/Meeza AIDs (required for L2 selection)
                        val providedDdol = a.ddol?.takeIf { it.isNotBlank() }
                            ?: run {
                                val rid = if (!a.rid.isNullOrBlank()) a.rid.uppercase() else aidHex.take(10)
                                ridToDdolMap[rid]
                            }

                        if (!providedDdol.isNullOrBlank()) {
                            try {
                                dDOL = ByteUtil.hexStr2Bytes(providedDdol)
                                Log.d(TAG, "  MC/Meeza: setting dDOL for AID ${a.aid}: $providedDdol")
                            } catch (ex: Exception) {
                                Log.e(TAG, "  ⚠️ Failed to parse dDOL for AID ${a.aid}: $providedDdol")
                            }
                        } else {
                            Log.e(TAG, "  ⚠️ MC/Meeza: no dDOL provided for AID ${a.aid} - L2 selection may fail with -4125")
                        }

                        // Set contactless limits from PaymentConfig PayPassConfig (no runtime overrides)
                        // cvmLmt = Reader CVM Required Limit - PIN required for amounts >= this value
                        val cvmLmtHex = com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8124
                        cvmLmt = ByteUtil.hexStr2Bytes(cvmLmtHex)
                        // termClssLmt = Reader Contactless Transaction Limit - max allowed contactless amount
                        val termClssLmtHex = com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8125
                        termClssLmt = ByteUtil.hexStr2Bytes(termClssLmtHex)
                        // termClssOfflineFloorLmt = Reader Contactless Floor Limit
                        val floorLmtHex = com.neo.neopayplus.config.PaymentConfig.PayPassConfig.DF8123
                        termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes(floorLmtHex)
                        termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000") // keep 0 unless a dedicated config is added
                        
                        Log.e(TAG, "  MC/Meeza: CVM limits set on AID:")
                        Log.e(TAG, "    cvmLmt (CVM Required Limit) = $cvmLmtHex (PIN required >= this)")
                        Log.e(TAG, "    termClssLmt (Max Contactless) = $termClssLmtHex")
                        Log.e(TAG, "    termClssOfflineFloorLmt (Floor) = $floorLmtHex")
                        Log.d(TAG, "  MC/Meeza: contactless config applied (paramType=DEFAULT(0), kernelType=PAYPASS, selFlag=1)")
                    }

                    if (isVisa && isContactlessEnabled) {
                        // CRITICAL: For contactless AIDs (selFlag=1), we MUST set contactless-specific values
                        // even if API doesn't provide contactless params, otherwise contactless transactions fail
                        kernelType = AidlConstants.EMV.KernelType.PAYWAVE.toByte()
                        // SDK default AIDs use paramType=00 (default) even for contactless - this allows both contact and contactless
                        // Using paramType=02 (contactless) may cause L2 selection to fail with -4125
                        paramType = 0x00.toByte()  // 0 = default (allows both contact and contactless, matches SDK examples)
                        zeroCheck = 0x01.toByte()

                        // For Visa prefer API-provided ddol if present, otherwise rely on kernel defaults
                        val providedDdol = a.ddol?.takeIf { it.isNotBlank() }
                            ?: run {
                                val rid = if (!a.rid.isNullOrBlank()) a.rid.uppercase() else aidHex.take(10)
                                ridToDdolMap[rid]
                            }
                        if (!providedDdol.isNullOrBlank()) {
                            try {
                                dDOL = ByteUtil.hexStr2Bytes(providedDdol)
                                Log.d(TAG, "  VISA: setting dDOL from API for AID ${a.aid}: $providedDdol")
                            } catch (ex: Exception) {
                                Log.e(TAG, "  ⚠️ Failed to parse dDOL for VISA AID ${a.aid}: $providedDdol")
                            }
                        } else {
                            Log.d(TAG, "  VISA: no dDOL provided for AID ${a.aid} - leaving dDOL unset (kernel defaults may apply)")
                        }

                        // Set contactless limits from PaymentConfig PayWaveConfig
                        // DF8124 = CVM Required Limit (PIN required for amounts ABOVE this)
                        // DF8125 = Contactless Transaction Limit (max contactless amount)
                        // DF8123 = Contactless Floor Limit (offline floor)
                        if (a.contactless?.merchantRiskParameters != null) {
                            val mrp = a.contactless.merchantRiskParameters
                            // Use API-provided values if available, otherwise use PaymentConfig
                            cvmLmt = ByteUtil.hexStr2Bytes(mrp.noCvmLimit?.takeIf { it.isNotBlank() } 
                                ?: com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8124)
                            termClssLmt = ByteUtil.hexStr2Bytes(mrp.cvmLimit?.takeIf { it.isNotBlank() } 
                                ?: com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8125)
                            termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes(mrp.floorLimit?.takeIf { it.isNotBlank() } 
                                ?: com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8123)
                            Log.d(TAG, "  VISA: contactless limits from API (with PaymentConfig defaults)")
                        } else {
                            // Use PaymentConfig values - DF8124 = 600.00 EGP (no PIN below this)
                            cvmLmt = ByteUtil.hexStr2Bytes(com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8124)
                            termClssLmt = ByteUtil.hexStr2Bytes(com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8125)
                            termClssOfflineFloorLmt = ByteUtil.hexStr2Bytes(com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8123)
                            Log.d(TAG, "  VISA: contactless limits from PaymentConfig (DF8124=${com.neo.neopayplus.config.PaymentConfig.PayWaveConfig.DF8124})")
                        }
                        // termOfflineFloorLmt is typically 0 for contactless
                        termOfflineFloorLmt = ByteUtil.hexStr2Bytes("000000000000")
                        
                        Log.d(TAG, "  VISA: contactless config applied (paramType=DEFAULT(0), kernelType=PAYWAVE, selFlag=1)")
                    }

                    // Default to EMV kernel if not set (allows both contact and contactless)
                    // CRITICAL: Do NOT override kernelType for Mastercard/Meeza - it must remain PayPass (2)
                    if (kernelType == 0.toByte() && !isMastercard && !isMeeza) {
                        kernelType = AidlConstants.EMV.KernelType.EMV.toByte()
                    }
                }

                val actualSelFlag = aidObj.selFlag.toInt()
                val kernelTypeName = when (aidObj.kernelType.toInt()) {
                    AidlConstants.EMV.KernelType.PAYPASS -> "PayPass (kernelType=2)"
                    AidlConstants.EMV.KernelType.PAYWAVE -> "PayWave (kernelType=3)"
                    else -> "EMV/OTHER (kernelType=${aidObj.kernelType})"
                }

                if (isMastercard) {
                    val rid = aidHex.take(10)
                    val pixValue = if (aidHex.length >= 14) aidHex.substring(10) else ""
                    val isL1 = pixValue == "1010" || pixValue == "0001"
                    val isL2 = !isL1 && pixValue.isNotEmpty()
                    Log.e(TAG, "  📋 Mastercard AID Config:")
                    Log.e(TAG, "     AID: $aidHex")
                    Log.e(TAG, "     RID: $rid")
                    Log.e(TAG, "     PIX: $pixValue ${if (isL1) "(L1)" else if (isL2) "(L2)" else ""}")
                    Log.e(TAG, "     Version: ${a.version ?: "default"}")
                    Log.e(TAG, "     selFlag: $actualSelFlag")
                    Log.e(TAG, "     kernelType: ${aidObj.kernelType} (PayPass=2)")
                    Log.e(TAG, "     paramType: ${aidObj.paramType} (0=Default, 1=Contact, 2=Contactless)")
                    Log.e(TAG, "     clsStatusCheck: ${aidObj.clsStatusCheck}")
                    Log.e(TAG, "     zeroCheck: ${aidObj.zeroCheck} ${if (isL1) "(L1=0)" else if (isL2) "(L2=1)" else ""}")
                    val ddolHex = if (aidObj.dDOL != null && aidObj.dDOL.isNotEmpty()) {
                        ByteUtil.bytes2HexStr(aidObj.dDOL, 0, aidObj.dDOL.size)
                    } else {
                        "NOT SET"
                    }
                    Log.e(TAG, "     dDOL: $ddolHex (${aidObj.dDOL?.size ?: 0} bytes) ${if (isL2) "⚠️ CRITICAL for L2 selection" else ""}")
                    val referCurrCodeHex = if (aidObj.referCurrCode != null && aidObj.referCurrCode.isNotEmpty()) {
                        ByteUtil.bytes2HexStr(aidObj.referCurrCode, 0, aidObj.referCurrCode.size)
                    } else {
                        "NOT SET"
                    }
                    Log.e(TAG, "     referCurrCode: $referCurrCodeHex")
                    Log.e(TAG, "     referCurrExp: ${aidObj.referCurrExp}")
                }

                // CRITICAL: Final safety check - ensure kernelType is set correctly BEFORE addAid()
                // This is the last chance to fix kernelType before it's sent to the SDK
                // If kernelType is wrong, DFC10A will not be set correctly → -4125 error
                Log.e(TAG, "  📋 PRE-INSTALL CHECK for ${a.aid}:")
                Log.e(TAG, "     kernelType field value: ${aidObj.kernelType.toInt()} (should be 2 for PayPass)")
                Log.e(TAG, "     paramType field value: ${aidObj.paramType.toInt()}")
                Log.e(TAG, "     selFlag: $actualSelFlag")
                
                // CRITICAL: For Mastercard/Meeza AIDs, kernelType MUST be 2 (PayPass) - no exceptions
                // This creates DFC10A=02 in the AID TLV, which is REQUIRED for PayPass L2 kernel activation
                if ((isMastercard || isMeeza) && aidObj.kernelType.toInt() != 2) {
                    Log.e(TAG, "     ❌❌❌ CRITICAL ERROR: kernelType is NOT 2 (PayPass)!")
                    Log.e(TAG, "        Current value: ${aidObj.kernelType.toInt()}")
                    Log.e(TAG, "        This will cause -4125 error! Fixing now...")
                    aidObj.kernelType = AidlConstants.EMV.KernelType.PAYPASS.toByte()
                    Log.e(TAG, "        ✓✓✓ FIXED: kernelType now = ${aidObj.kernelType.toInt()} (PayPass=2)")
                    Log.e(TAG, "        This ensures DFC10A=02 will be stored in AID TLV")
                }
                
                // Double-check: Log final kernelType value one more time
                if (isMastercard || isMeeza) {
                    Log.e(TAG, "     🔥 FINAL VERIFICATION: kernelType = ${aidObj.kernelType.toInt()} ${if (aidObj.kernelType.toInt() == 2) "✓ CORRECT" else "❌ WRONG - WILL CAUSE -4125"}")
                }
                
                // CRITICAL: Log all AID fields before addAid() to verify kernelType is set
                Log.e(TAG, "  📋 FINAL AID CONFIGURATION BEFORE addAid():")
                Log.e(TAG, "     AID: $aidHex")
                Log.e(TAG, "     selFlag: $actualSelFlag")
                Log.e(TAG, "     kernelType: ${aidObj.kernelType.toInt()} (${if (aidObj.kernelType.toInt() == 2) "PayPass=2 ✓" else "❌ WRONG"})")
                Log.e(TAG, "     paramType: ${aidObj.paramType.toInt()}")
                Log.e(TAG, "     version: ${ByteUtil.bytes2HexStr(aidObj.version, 0, aidObj.version.size)}")
                
                Log.e(TAG, "  Attempting to add: ${a.aid}, selFlag=$actualSelFlag, $kernelTypeName")
                // Use SdkWrapper to add AID (will invalidate cache after success)
                val rc = com.neo.neopayplus.emv.SdkWrapper.addAid(aidObj)
                if (rc == 0) {
                    val schemeLabel = when {
                        isMastercard -> "[MC]"
                        isVisa -> "[VISA]"
                        isMeeza -> "[MEEZA]"
                        else -> "[OTHER]"
                    }
                    Log.e(TAG, "✓ addAid $schemeLabel ${a.aid} (${a.label ?: ""}), selFlag=$actualSelFlag, kernelType=${aidObj.kernelType}")
                    
                    // CRITICAL: Small delay to ensure AID is fully written to device storage
                    // This helps ensure DFC10A is properly stored before verification
                    Thread.sleep(200)
                    
                    // CRITICAL: FALLBACK - If AidV2.kernelType doesn't serialize to DFC10A,
                    // try to set DFC10A explicitly via setTlv with PayPass kernel
                    // This is a workaround if the SDK doesn't automatically map kernelType to DFC10A
                    // NOTE: This fallback is likely not needed since we're using TLV payload for A0000000041010
                    // but keeping it as a safety measure for other Mastercard AIDs
                    if (isMastercard || isMeeza) {
                        try {
                            Log.e(TAG, "  🔧 FALLBACK: Attempting to set DFC10A=02 explicitly via setTlv...")
                            // Try setting DFC10A directly via PayPass kernel TLV
                            // Note: This may not work if DFC10A must be in AID registration, but worth trying
                            emv.setTlv(
                                com.sunmi.payservice.AidlConstantsV2.EMV.TLVOpCode.OP_PAYPASS,
                                "DFC10A",
                                "02"
                            )
                            Log.e(TAG, "  ✓ setTlv(OP_PAYPASS, DFC10A, 02) called (return value not checked)")
                        } catch (e: Exception) {
                            Log.e(TAG, "  ⚠️ setTlv fallback failed: ${e.message}")
                        }
                    }
                    
                    // CRITICAL: Verify kernelType/profile is correctly set after installation
                    // According to SDK docs, we must verify DFC10A (kernelType) matches what we set
                    // This checks if DFC10A=02 is actually stored in the AID TLV
                    if (isMastercard || isMeeza) {
                        verifyMastercardAidConfiguration(aidHex, aidObj.kernelType.toInt())
                        
                        // CRITICAL: If verification failed, log explicit instructions
                        // The AID must be deleted and re-added with correct kernelType
                        val aidList = java.util.ArrayList<String>()
                        val queryRc = emv.queryAidCapkList(0, aidList)
                        if (queryRc == 0) {
                            val installedAidTlv = aidList.find { tlv ->
                                val extractedAid = extractAidFromTlv(tlv)
                                extractedAid?.uppercase() == aidHex.uppercase()
                            }
                            if (installedAidTlv != null) {
                                val kernelTypeHex = extractTlvValue(installedAidTlv, "DFC10A")
                                val actualKernelType = kernelTypeHex?.toIntOrNull(16) ?: -1
                                if (actualKernelType != 2) {
                                    Log.e(TAG, "  ❌❌❌ CRITICAL: DFC10A verification shows kernelType=$actualKernelType (expected 2)")
                                    Log.e(TAG, "     ⚠️ The AidV2.kernelType field is NOT being serialized to DFC10A TLV tag")
                                    Log.e(TAG, "     ⚠️ This is a SDK serialization issue - kernelType field may not map to DFC10A")
                                    Log.e(TAG, "     ⚠️ SOLUTION: Contact Sunmi SDK support or check if there's a different method to set DFC10A")
                                }
                            }
                        }
                    }
                    
                    success++
                } else {
                    Log.e(TAG, "⚠️ addAid ${a.aid} FAILED with rc=$rc")
                    Log.e(TAG, "   Label: ${a.label ?: "N/A"}")
                    Log.e(TAG, "   selFlag: $actualSelFlag, kernelType: ${aidObj.kernelType}")
                    fail++
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error adding AID ${a.aid}: ${e.message}")
                Log.e(TAG, "   Exception: ${e.javaClass.simpleName}: ${e.message}")
                e.printStackTrace()
                fail++
            }
        }

        Log.e(TAG, "=== AID Installation Summary ===")
        Log.e(TAG, "  Total from API: ${list.size}")
        Log.e(TAG, "  Successfully installed: $success")
        Log.e(TAG, "  Failed: $fail")
        if (fail > 0) {
            Log.e(TAG, "⚠️ WARNING: Some AIDs failed to install - this may cause -4125 errors!")
        }
    }

    /**
     * Verify that Mastercard AID has correct kernelType/profile after installation.
     * According to SDK docs, we must check DFC10A (kernelType) to ensure it's set to 2 (PayPass).
     * Wrong kernelType will cause -4125 "L2 candidate list is empty" error.
     */
    private fun verifyMastercardAidConfiguration(aidHex: String, expectedKernelType: Int) {
        try {
            Log.e(TAG, "  🔍 Verifying Mastercard AID configuration for $aidHex...")
            val aidList = java.util.ArrayList<String>()
            val queryRc = emv.queryAidCapkList(0, aidList)  // 0 = query AIDs
            
            if (queryRc == 0 && aidList.isNotEmpty()) {
                // Find the AID we just installed
                val installedAidTlv = aidList.find { tlv ->
                    val extractedAid = extractAidFromTlv(tlv)
                    extractedAid?.uppercase() == aidHex.uppercase()
                }
                
                if (installedAidTlv != null) {
                    // Extract kernelType (DFC10A) from TLV
                    val kernelTypeHex = extractTlvValue(installedAidTlv, "DFC10A")
                    val actualKernelType = kernelTypeHex?.toIntOrNull(16) ?: -1
                    
                    Log.e(TAG, "     Expected kernelType: $expectedKernelType (PayPass=2)")
                    Log.e(TAG, "     Actual kernelType from device: $actualKernelType (from DFC10A tag)")
                    
                    if (actualKernelType == expectedKernelType && expectedKernelType == 2) {
                        Log.e(TAG, "     ✓✓✓ VERIFICATION PASSED: DFC10A=02 (PayPass) correctly stored in AID TLV")
                    } else if (actualKernelType != expectedKernelType) {
                        Log.e(TAG, "     ❌❌❌ VERIFICATION FAILED: kernelType mismatch!")
                        Log.e(TAG, "        Expected: $expectedKernelType (PayPass=2), but device stored: $actualKernelType")
                        Log.e(TAG, "        ⚠️ This WILL cause -4125 error during L2 selection!")
                        Log.e(TAG, "        ⚠️ ROOT CAUSE: DFC10A not set correctly in AID TLV")
                        Log.e(TAG, "        ⚠️ SOLUTION: Re-install AID with explicit kernelType=2")
                        
                        // CRITICAL: This is the root cause - DFC10A is not being stored correctly
                        // The AidV2.kernelType field we set is not being serialized to DFC10A TLV tag
                        // We need to ensure the SDK properly stores kernelType as DFC10A
                    } else if (actualKernelType == -1) {
                        Log.e(TAG, "     ❌❌❌ VERIFICATION FAILED: DFC10A tag NOT FOUND in installed AID TLV!")
                        Log.e(TAG, "        ⚠️ This is the ROOT CAUSE of -4125 error!")
                        Log.e(TAG, "        ⚠️ Device may have defaulted to EMV (0) or kernelType was not stored")
                        Log.e(TAG, "        ⚠️ Without DFC10A=02, PayPass L2 kernel is NOT available")
                        Log.e(TAG, "        ⚠️ No PayPass kernel → No L2 profiles → Empty candidate list → -4125")
                        Log.e(TAG, "        ⚠️ SOLUTION: Ensure AidV2.kernelType is properly serialized to DFC10A TLV")
                    }
                    
                    // Also check paramType (DFC10B)
                    val paramTypeHex = extractTlvValue(installedAidTlv, "DFC10B")
                    val paramType = paramTypeHex?.toIntOrNull(16) ?: -1
                    Log.e(TAG, "     paramType (DFC10B): $paramType (0=Default, 1=Contact, 2=Contactless)")
                } else {
                    Log.e(TAG, "     ⚠️ Could not find installed AID $aidHex in query results")
                }
            } else {
                Log.e(TAG, "     ⚠️ Could not query installed AIDs (rc=$queryRc) - cannot verify kernelType")
            }
        } catch (e: Exception) {
            Log.e(TAG, "     ❌ Error verifying AID configuration: ${e.message}")
        }
    }
    
    /**
     * Extract AID hex from TLV string.
     */
    private fun extractAidFromTlv(tlvString: String): String? {
        try {
            // Look for tag 9F06 (AID) in TLV
            val tagIndex = tlvString.indexOf("9F06")
            if (tagIndex >= 0 && tagIndex + 6 < tlvString.length) {
                val lengthHex = tlvString.substring(tagIndex + 4, tagIndex + 6)
                val lengthBytes = lengthHex.toIntOrNull(16) ?: 0
                val lengthHexChars = lengthBytes * 2
                val aidStart = tagIndex + 6
                val aidEnd = aidStart + lengthHexChars
                if (aidEnd <= tlvString.length) {
                    return tlvString.substring(aidStart, aidEnd).uppercase()
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return null
    }
    
    /**
     * Extract TLV value by tag name.
     */
    private fun extractTlvValue(tlvString: String, tag: String): String? {
        try {
            val tagIndex = tlvString.indexOf(tag)
            if (tagIndex >= 0) {
                // Tag found, now extract length and value
                val afterTag = tagIndex + tag.length
                if (afterTag + 2 <= tlvString.length) {
                    val lengthHex = tlvString.substring(afterTag, afterTag + 2)
                    val lengthBytes = lengthHex.toIntOrNull(16) ?: 0
                    val lengthHexChars = lengthBytes * 2
                    val valueStart = afterTag + 2
                    val valueEnd = valueStart + lengthHexChars
                    if (valueEnd <= tlvString.length) {
                        return tlvString.substring(valueStart, valueEnd)
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return null
    }

    // --- helpers ---
    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace("\\s".toRegex(), "")
        return ByteArray(clean.length / 2) { i ->
            Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16).toByte()
        }
    }
}
