package com.neo.neopayplus.emv

import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.utils.LogUtil
import com.neo.neopayplus.wrapper.PinPadListenerV2Wrapper
import com.sunmi.pay.hardware.aidlv2.bean.PinPadConfigV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2

/**
 * Wrapper around PinPadOptV2 that centralizes all PIN pad interactions.
 * 
 * Based on EMV Book 3 CVM handling:
 * - pinType=0: Online PIN - PIN block encrypted with TPK, sent to host in ISO8583 Field 52
 * - pinType=1: Offline PIN - Sunmi SDK sends VERIFY command to ICC internally
 * 
 * The EMV kernel decides which PIN type to use based on:
 * 1. Card's CVM List (read from the chip)
 * 2. Terminal Capabilities (9F33)
 * 3. Transaction amount vs CVM limits
 */
class PinPadManager(
    private val pinPadOpt: PinPadOptV2
) {

    sealed class PinResult {
        /**
         * Offline PIN verified successfully by ICC (SW=9000)
         * pinData may be null for offline PIN (verification done by card)
         */
        data class OfflineSuccess(val pinData: ByteArray?) : PinResult()
        
        /**
         * Online PIN captured - PIN block encrypted with TPK
         * This goes to host in ISO8583 Field 52
         */
        data class OnlineSuccess(val pinBlock: ByteArray?) : PinResult()
        
        /**
         * User pressed bypass/skip (allowed for some CVM rules)
         */
        object Bypassed : PinResult()
        
        /**
         * User cancelled PIN entry
         */
        object Cancelled : PinResult()
        
        /**
         * PIN entry failed (timeout, pinpad error, etc.)
         */
        data class Failed(val code: Int, val message: String) : PinResult()
    }

    /**
     * Request PIN entry from user.
     * 
     * @param pan Card PAN (used to build PIN block for online PIN)
     * @param pinType 0=Online PIN, 1=Offline PIN (from onRequestShowPinPad callback)
     * @param timeoutMs Timeout in milliseconds (default 60 seconds)
     * @param onResult Callback with PIN result
     * 
     * For Offline PIN (pinType=1):
     * - Sunmi SDK handles VERIFY command to ICC internally
     * - Result indicates if ICC accepted the PIN (SW=9000) or not
     * 
     * For Online PIN (pinType=0):
     * - PIN block is encrypted with TPK at index KeyManager.TPK_INDEX
     * - PIN block should be sent to host in ISO8583 Field 52
     */
    fun requestPin(
        pan: String,
        pinType: Int,
        timeoutMs: Int = 60_000,
        onResult: (PinResult) -> Unit
    ) {
        val pinTypeName = if (pinType == 1) "OFFLINE" else "ONLINE"
        LogUtil.e(Constant.TAG, "PinPadManager: requestPin pinType=$pinType ($pinTypeName), timeout=${timeoutMs}ms")
        
        try {
            // DEBUG: Verify TPK status before PIN entry
            LogUtil.e(Constant.TAG, "PinPadManager: Checking TPK at slot ${KeyManager.TPK_INDEX}...")
            try {
                val securityOpt = com.neo.neopayplus.MyApplication.app.securityOptV2
                if (securityOpt != null) {
                    val kcv = ByteArray(8)
                    val rc = securityOpt.getKeyCheckValue(
                        com.sunmi.pay.hardware.aidl.AidlConstants.Security.KEY_TYPE_PIK,
                        KeyManager.TPK_INDEX,
                        kcv
                    )
                    if (rc == 0) {
                        val kcvHex = ByteUtil.bytes2HexStr(kcv, 0, 3)
                        LogUtil.e(Constant.TAG, "PinPadManager: ✓ TPK loaded at slot ${KeyManager.TPK_INDEX}, KCV=$kcvHex")
                    } else {
                        LogUtil.e(Constant.TAG, "PinPadManager: ❌ TPK NOT loaded at slot ${KeyManager.TPK_INDEX}, rc=$rc")
                    }
                } else {
                    LogUtil.e(Constant.TAG, "PinPadManager: ⚠️ SecurityOptV2 not available for key check")
                }
            } catch (e: Exception) {
                LogUtil.e(Constant.TAG, "PinPadManager: Key check failed: ${e.message}")
            }
            
            // Cancel any existing PIN pad operation before starting a new one
            // This helps avoid conflicts if a PIN pad is already showing
            try {
                LogUtil.e(Constant.TAG, "PinPadManager: Cancelling any existing PIN pad...")
                pinPadOpt.cancelInputPin()
                Thread.sleep(200)  // Give time for cancellation to complete
                LogUtil.e(Constant.TAG, "PinPadManager: Cancel complete, proceeding with initPinPad")
            } catch (e: Exception) {
                LogUtil.e(Constant.TAG, "PinPadManager: Cancel failed (may be normal): ${e.message}")
            }
            
            // Build PAN bytes (rightmost 12 digits excluding check digit)
            // This is used for ISO-9564 PIN block format
            val panBytes = buildPanForPinBlock(pan)
            LogUtil.e(Constant.TAG, "PinPadManager: PAN for PIN block: ${String(panBytes)}")
            
            val config = PinPadConfigV2().apply {
                setPinPadType(0)  // 0 = standard PIN pad
                setPinType(pinType)  // 0=online, 1=offline
                setOrderNumKey(false)  // Don't randomize key positions
                setPan(panBytes)
                setTimeout(timeoutMs)
                setPinKeyIndex(KeyManager.TPK_INDEX)  // TPK slot (12) for online PIN encryption
                setMaxInput(12)
                setMinInput(0)  // 0 allows bypass (empty PIN)
                setKeySystem(0)  // 0 = MKSK (Master Key / Session Key), NOT DUKPT (1)
                setAlgorithmType(0)  // 0 = 3DES
            }

            pinPadOpt.initPinPad(config, object : PinPadListenerV2Wrapper() {
                override fun onPinLength(len: Int) {
                    LogUtil.e(Constant.TAG, "PinPadManager: onPinLength=$len")
                    // Could update UI to show PIN dots here
                }
                
                override fun onConfirm(type: Int, pinBlock: ByteArray?) {
                    // type: 0=online PIN confirmed, 1=offline PIN confirmed
                    // pinBlock: For online PIN, this is the encrypted PIN block
                    //           For offline PIN, this may be null (ICC verified internally)
                    val typeName = if (type == 1) "OFFLINE" else "ONLINE"
                    val blockHex = pinBlock?.let { ByteUtil.bytes2HexStr(it, 0, it.size) } ?: "null"
                    LogUtil.e(Constant.TAG, "PinPadManager: onConfirm type=$type ($typeName), pinBlock=$blockHex")
                    
                    if (pinBlock == null && pinType == 0) {
                        // User pressed confirm without entering PIN (bypass)
                        LogUtil.e(Constant.TAG, "PinPadManager: PIN bypassed (no PIN entered)")
                        onResult(PinResult.Bypassed)
                    } else if (type == 1) {
                        // Offline PIN - ICC verified the PIN
                        onResult(PinResult.OfflineSuccess(pinBlock))
                    } else {
                        // Online PIN - encrypted PIN block for host
                        onResult(PinResult.OnlineSuccess(pinBlock))
                    }
                }

                override fun onCancel() {
                    LogUtil.e(Constant.TAG, "PinPadManager: onCancel - user cancelled PIN entry")
                    onResult(PinResult.Cancelled)
                }

                override fun onError(errCode: Int) {
                    LogUtil.e(Constant.TAG, "PinPadManager: onError code=$errCode")
                    val message = when (errCode) {
                        -1 -> "PIN pad timeout"
                        -2 -> "PIN pad cancelled"
                        -3 -> "PIN pad busy"
                        -4 -> "PIN key not loaded"
                        -60001 -> "Input PIN timeout"
                        -60002 -> "Keyboard failed to activate - check TPK and EMV state"
                        -60003 -> "PinPadType type error"
                        else -> "PIN pad error $errCode"
                    }
                    LogUtil.e(Constant.TAG, "PinPadManager: $message")
                    onResult(PinResult.Failed(errCode, message))
                }
            })
        } catch (t: Throwable) {
            LogUtil.e(Constant.TAG, "PinPadManager: initPinPad exception: ${t.message}")
            onResult(PinResult.Failed(-999, t.message ?: "PinPad init failed"))
        }
    }

    /**
     * Build PAN bytes for PIN block calculation (ISO-9564 format).
     * Uses rightmost 12 digits of PAN excluding check digit.
     */
    private fun buildPanForPinBlock(pan: String): ByteArray {
        val normalized = pan.filter { it.isDigit() }
        return if (normalized.length >= 13) {
            // Take rightmost 13 chars, drop last (check digit), keep 12
            normalized.substring(normalized.length - 13, normalized.length - 1)
                .toByteArray(Charsets.US_ASCII)
        } else {
            // PAN too short, use as-is (shouldn't happen with valid cards)
            normalized.toByteArray(Charsets.US_ASCII)
        }
    }
}
