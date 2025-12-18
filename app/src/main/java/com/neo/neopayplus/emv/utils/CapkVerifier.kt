package com.neo.neopayplus.emv.utils

import com.neo.neopayplus.BuildConfig
import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import java.util.ArrayList

/**
 * CAPK Verification Utility
 * 
 * Compares installed CAPKs against the EFTlab reference list to identify missing keys.
 * This is critical for debugging ICC Data Authentication failures (-4002 errors).
 * 
 * Reference: https://www.eftlab.com/knowledge-base/list-of-ca-public-keys
 */
class CapkVerifier(private val emv: EMVOptV2) {
    
    /**
     * Mastercard CAPK reference data from EFTlab
     * RID: A000000004
     * 
     * Key indices that should be installed for production Mastercard cards:
     * - 00: Test key (expired 31.12.2009) - may still be needed for some cards
     * - 01: Test key (expired 31.12.2009) - may still be needed for some cards
     * - 02: Test key (expired 31.12.2009) - may still be needed for some cards
     * - 03: Live key (expired 31.12.2009) - legacy cards
     * - 04: Live key (expires 31.12.2017) - common
     * - 05: Test key (N/A) + Live key (expires 31.12.2024) - current
     * - 06: Live key (expires 31.12.2028) - current
     * - 09: Live key (no expiry) - common
     * - 22: Live key (no expiry) - common
     * - EF-F9: Various test keys
     * - FA-FF: Various keys
     * 
     * For production, focus on Live keys with current expiry dates.
     */
    data class CapkReference(
        val rid: String,
        val index: String,
        val keyType: String, // "Live" or "Test"
        val expires: String? = null,
        val keyLength: Int? = null,
        val sha1: String? = null
    )
    
    companion object {
        // Mastercard (A000000004) CAPK reference list from EFTlab
        // Focus on Live keys that are currently valid
        private val MASTERCARD_CAPK_REFERENCES = listOf(
            // Current/Recent Live Keys (Priority)
            CapkReference("A000000004", "05", "Live", "31.12.2024", 1408, "EBFA0D5D06D8CE702DA3EAE890701D45E274C845"),
            CapkReference("A000000004", "06", "Live", "31.12.2028", 1984, "F910A1504D5FFB793D94F3B500765E1ABCAD72D9"),
            CapkReference("A000000004", "04", "Live", "31.12.2017", 1152, "381A035DA58B482EE2AF75F4C3F2CA469BA4AA6C"),
            CapkReference("A000000004", "09", "Live", null, 768, null),
            CapkReference("A000000004", "22", "Live", null, 768, null),
            
            // Legacy Live Keys (may still be needed for older cards)
            CapkReference("A000000004", "00", "Live", "31.12.2009", 768, "8BB99ADDF7B560110955014505FB6B5F8308CE27"),
            CapkReference("A000000004", "01", "Live", "31.12.2009", 768, "EA950DD4234FEB7C900C0BE817F64DE66EEEF7C4"),
            CapkReference("A000000004", "02", "Live", "31.12.2009", 896, "AF1CC1FD1C1BC9BCA07E78DA6CBA2163F169CBB7"),
            CapkReference("A000000004", "03", "Live", "31.12.2009", 1024, "5ADDF21D09278661141179CBEFF272EA384B13BB"),
            
            // Test Keys (for testing environments)
            CapkReference("A000000004", "00", "Test", "N/A", 1280, "EC0A59D35D19F031E9E8CBEC56DB80E22B1DE130"),
            CapkReference("A000000004", "01", "Test", "N/A", 1024, "8C05A64127485B923C94B63D264AF0BF85CB45D9"),
            CapkReference("A000000004", "02", "Test", "N/A", 1536, "33408B96C814742AD73536C72F0926E4471E8E47"),
            CapkReference("A000000004", "05", "Test", "N/A", 1024, "53D04903B496F59544A84309AF169251F2896874"),
            CapkReference("A000000004", "EF", "Test", "N/A", 1984, "21766EBB0EE122AFB65D7845B73DB46BAB65427A"),
            CapkReference("A000000004", "F0", "Test", null, 1024, "AE667445F8DE6F82C38800E5EBABA322F03F58F2"),
            CapkReference("A000000004", "F1", "Test", "N/A", 1408, "D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB"),
            CapkReference("A000000004", "F3", "Test", "N/A", 1152, "A69AC7603DAF566E972DEDC2CB433E07E8B01A9A"),
            CapkReference("A000000004", "F4", "Test", null, 1408, null),
            CapkReference("A000000004", "F5", "Test", "N/A", 1984, "C2239804C8098170BE52D6D5D4159E81CE8466BF"),
            CapkReference("A000000004", "F6", "Test", "N/A", 1792, "502909ED545E3C8DBD00EA582D0617FEE9F6F684"),
            CapkReference("A000000004", "F7", "Test", "N/A", 1024, "EEB0DD9B2477BEE3209A914CDBA94C1C4A9BDED9"),
            CapkReference("A000000004", "F8", "Test", "N/A", 1024, "F06ECC6D2AAEBF259B7E755A38D9A9B24E2FF3DD"),
            CapkReference("A000000004", "F9", "Test", "N/A", 1536, "336712DCC28554809C6AA9B02358DE6F755164DB"),
            CapkReference("A000000004", "FA", "Test", "N/A", 1280, "0ABCADAD2C7558CA9C7081AE55DDDC714F8D45F8"),
            CapkReference("A000000004", "FE", "Test", "N/A", 1024, "9A295B05FB390EF7923F57618A9FDA2941FC34E0"),
        )
        
        // Visa (A000000003) CAPK reference list - commonly needed keys
        private val VISA_CAPK_REFERENCES = listOf(
            CapkReference("A000000003", "01", "Live", "31.12.2009", 1024, "D34A6A776011C7E7CE3AEC5F03AD2F8CFC5503CC"),
            CapkReference("A000000003", "07", "Live", "31.12.2017", 1152, "B4BC56CC4E88324932CBC643D6898F6FE593B172"),
            CapkReference("A000000003", "08", "Live", "31.12.2024", 1408, "20D213126955DE205ADC2FD2822BD22DE21CF9A8"),
            CapkReference("A000000003", "09", "Live", "31.12.2028", 1984, "1FF80A40173F52D7D27E0F26A146A1C8CCB29046"),
            CapkReference("A000000003", "10", "Live", null, 1024, "833B1947778036B6D759FCE3F618DDEB2749372C"),
            CapkReference("A000000003", "20", "Live", null, 1024, "7AC3D80EF01E9A998F0A77181E64B36747DC51EB"),
        )
    }
    
    /**
     * Verify installed CAPKs against reference list and report missing keys.
     * 
     * @param rid RID to check (e.g., "A000000004" for Mastercard)
     * @return Verification result with missing CAPKs list
     */
    fun verifyCapks(rid: String = "A000000004"): CapkVerificationResult {
        val result = CapkVerificationResult(rid)
        
        try {
            LogUtil.e(Constant.TAG, "")
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            LogUtil.e(Constant.TAG, "=== CAPK VERIFICATION: RID $rid ===")
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            
            // Query installed CAPKs
            val installedCapks = queryInstalledCapks()
            result.installedCount = installedCapks.size
            
            // Filter installed CAPKs for this RID
            val installedForRid = installedCapks.filter { it.rid.equals(rid, ignoreCase = true) }
            result.installedForRid = installedForRid.size
            
            LogUtil.e(Constant.TAG, "Installed CAPKs: ${installedCapks.size} total, ${installedForRid.size} for RID $rid")
            
            // Get reference list for this RID
            val referenceList = when {
                rid.equals("A000000004", ignoreCase = true) -> MASTERCARD_CAPK_REFERENCES
                rid.equals("A000000003", ignoreCase = true) -> VISA_CAPK_REFERENCES
                else -> emptyList()
            }
            
            if (referenceList.isEmpty()) {
                LogUtil.e(Constant.TAG, "⚠️ No reference data available for RID $rid")
                LogUtil.e(Constant.TAG, "   Reference: https://www.eftlab.com/knowledge-base/list-of-ca-public-keys")
                return result
            }
            
            // Extract installed indices
            val installedIndices = installedForRid.map { it.index.uppercase() }.toSet()
            
            // Check each reference CAPK
            val liveKeys = referenceList.filter { it.keyType == "Live" }
            val testKeys = referenceList.filter { it.keyType == "Test" }
            
            LogUtil.e(Constant.TAG, "")
            LogUtil.e(Constant.TAG, "Reference CAPKs: ${liveKeys.size} Live, ${testKeys.size} Test")
            
            // Check Live keys first (production priority)
            val missingLiveKeys = mutableListOf<CapkReference>()
            liveKeys.forEach { ref ->
                val isInstalled = installedIndices.contains(ref.index.uppercase())
                if (!isInstalled) {
                    missingLiveKeys.add(ref)
                    result.missingCapks.add(ref)
                } else {
                    result.foundCapks.add(ref)
                }
            }
            
            // Check Test keys
            val missingTestKeys = mutableListOf<CapkReference>()
            testKeys.forEach { ref ->
                val isInstalled = installedIndices.contains(ref.index.uppercase())
                if (!isInstalled) {
                    missingTestKeys.add(ref)
                    // Only add to missing if not already found as Live
                    if (!result.foundCapks.any { it.index == ref.index }) {
                        result.missingCapks.add(ref)
                    }
                } else {
                    result.foundCapks.add(ref)
                }
            }
            
            // Report results
            LogUtil.e(Constant.TAG, "")
            LogUtil.e(Constant.TAG, "📋 VERIFICATION RESULTS:")
            LogUtil.e(Constant.TAG, "   Found: ${result.foundCapks.size} CAPKs")
            LogUtil.e(Constant.TAG, "   Missing: ${result.missingCapks.size} CAPKs")
            
            if (result.foundCapks.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "✓ INSTALLED CAPKs:")
                result.foundCapks.forEach { ref ->
                    val expiryInfo = ref.expires?.let { " (expires $it)" } ?: " (no expiry)"
                    LogUtil.e(Constant.TAG, "   - Index ${ref.index}: ${ref.keyType}$expiryInfo")
                }
            }
            
            if (missingLiveKeys.isNotEmpty()) {
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "⚠️⚠️⚠️ MISSING LIVE CAPKs (CRITICAL for production):")
                missingLiveKeys.forEach { ref ->
                    val expiryInfo = ref.expires?.let { " (expires $it)" } ?: " (no expiry)"
                    LogUtil.e(Constant.TAG, "   ❌ Index ${ref.index}: ${ref.keyType}$expiryInfo")
                    if (ref.keyLength != null) {
                        LogUtil.e(Constant.TAG, "      Key length: ${ref.keyLength} bits")
                    }
                }
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "   ⚠️ Missing Live CAPKs can cause ICC Data Authentication failures!")
                LogUtil.e(Constant.TAG, "   ⚠️ Cards issued with these CAPKs will fail with -4002 error")
                LogUtil.e(Constant.TAG, "   ⚠️ ACTION REQUIRED: Install missing CAPKs from backend/EMV bundle")
            }
            
            if (missingTestKeys.isNotEmpty() && missingLiveKeys.isEmpty()) {
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "ℹ️ MISSING TEST CAPKs (for testing only):")
                missingTestKeys.forEach { ref ->
                    LogUtil.e(Constant.TAG, "   - Index ${ref.index}: ${ref.keyType}")
                }
            }
            
            // Recommendations
            LogUtil.e(Constant.TAG, "")
            LogUtil.e(Constant.TAG, "💡 RECOMMENDATIONS:")
            if (missingLiveKeys.isEmpty()) {
                LogUtil.e(Constant.TAG, "   ✓ All critical Live CAPKs are installed")
            } else {
                LogUtil.e(Constant.TAG, "   1. Install missing Live CAPKs from backend EMV bundle")
                LogUtil.e(Constant.TAG, "   2. Verify CAPKs are loaded from: ${BuildConfig.API_BASE_URL}/emv/bundle")
                LogUtil.e(Constant.TAG, "   3. Check backend EMV configuration includes all required CAPKs")
                LogUtil.e(Constant.TAG, "   4. Reference: https://www.eftlab.com/knowledge-base/list-of-ca-public-keys")
            }
            
            if (result.foundCapks.size < 3) {
                LogUtil.e(Constant.TAG, "")
                LogUtil.e(Constant.TAG, "   ⚠️ WARNING: Very few CAPKs installed (${result.foundCapks.size})")
                LogUtil.e(Constant.TAG, "   ⚠️ This may cause authentication failures for cards from different issuers")
                LogUtil.e(Constant.TAG, "   ⚠️ Consider installing additional CAPKs for better card compatibility")
            }
            
            LogUtil.e(Constant.TAG, "")
            LogUtil.e(Constant.TAG, "═══════════════════════════════════════════════════════")
            
        } catch (e: Exception) {
            LogUtil.e(Constant.TAG, "❌ Error verifying CAPKs: ${e.message}")
            result.error = e
        }
        
        return result
    }
    
    /**
     * Query installed CAPKs from EMV kernel
     */
    private fun queryInstalledCapks(): List<InstalledCapk> {
        val capkList = ArrayList<String>()
        val rc = emv.queryAidCapkList(1, capkList) // 1 = query CAPKs
        
        if (rc != 0) {
            LogUtil.e(Constant.TAG, "⚠️ queryAidCapkList() returned rc=$rc")
            return emptyList()
        }
        
        return capkList.mapNotNull { capkTlv ->
            try {
                val rid = extractRidFromCapkTlv(capkTlv)
                val index = extractIndexFromCapkTlv(capkTlv) ?: return@mapNotNull null
                InstalledCapk(rid, index, capkTlv)
            } catch (e: Exception) {
                LogUtil.e(Constant.TAG, "⚠️ Error parsing CAPK TLV: ${e.message}")
                null
            }
        }
    }
    
    /**
     * Extract RID from CAPK TLV string
     */
    private fun extractRidFromCapkTlv(tlvString: String): String {
        // RID is typically in tag 9F06 (AID) in CAPK TLV
        val tagIndex = tlvString.indexOf("9F06")
        if (tagIndex >= 0 && tagIndex + 6 < tlvString.length) {
            val lengthHex = tlvString.substring(tagIndex + 4, tagIndex + 6)
            val lengthBytes = lengthHex.toIntOrNull(16) ?: 0
            val lengthHexChars = lengthBytes * 2
            val ridStart = tagIndex + 6
            val ridEnd = ridStart + lengthHexChars.coerceAtMost(10) // RID is 5 bytes = 10 hex chars
            if (ridEnd <= tlvString.length) {
                return tlvString.substring(ridStart, ridEnd).uppercase()
            }
        }
        return "UNKNOWN"
    }
    
    /**
     * Extract index from CAPK TLV string
     */
    private fun extractIndexFromCapkTlv(tlvString: String): String? {
        // Index is typically in tag 9F22 (CAPK Index) in CAPK TLV
        val tagIndex = tlvString.indexOf("9F22")
        if (tagIndex >= 0 && tagIndex + 6 < tlvString.length) {
            val lengthHex = tlvString.substring(tagIndex + 4, tagIndex + 6)
            val lengthBytes = lengthHex.toIntOrNull(16) ?: 0
            val lengthHexChars = lengthBytes * 2
            val indexStart = tagIndex + 6
            val indexEnd = indexStart + lengthHexChars.coerceAtMost(2) // Index is 1 byte = 2 hex chars
            if (indexEnd <= tlvString.length) {
                return tlvString.substring(indexStart, indexEnd).uppercase()
            }
        }
        return null
    }
    
    data class InstalledCapk(
        val rid: String,
        val index: String,
        val tlv: String
    )
    
    data class CapkVerificationResult(
        val rid: String,
        var installedCount: Int = 0,
        var installedForRid: Int = 0,
        val foundCapks: MutableList<CapkReference> = mutableListOf(),
        val missingCapks: MutableList<CapkReference> = mutableListOf(),
        var error: Throwable? = null
    )
}
