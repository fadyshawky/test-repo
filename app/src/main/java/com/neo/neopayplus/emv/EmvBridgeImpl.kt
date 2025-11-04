package com.neo.neopayplus.emv

import android.os.RemoteException
import android.text.TextUtils
import com.neo.neopayplus.MyApplication
import com.neo.neopayplus.utils.ErrorHandler
import com.neo.neopayplus.utils.LogUtil
import com.neo.neopayplus.utils.PinPadHelper
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.Constant
import com.neo.neopayplus.emv.TLV
import com.neo.neopayplus.emv.TLVUtil
import com.sunmi.payservice.AidlConstantsV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadListenerV2
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.neo.neopayplus.wrapper.CheckCardCallbackV2Wrapper
import com.neo.neopayplus.wrapper.PinPadListenerV2Wrapper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

/**
 * Bridge implementation connecting Compose UI to existing Java PayLib services
 */
class EmvBridgeImpl : EmvBridge {
    
    private val readCardOptV2: ReadCardOptV2? get() = MyApplication.app.readCardOptV2
    private val pinPadOptV2: PinPadOptV2? get() = MyApplication.app.pinPadOptV2
    private val emvOptV2: EMVOptV2? get() = MyApplication.app.emvOptV2
    
    private var currentCardCallback: ((Boolean, String?) -> Unit)? = null
    private var currentPinCallback: (() -> Unit)? = null
    private var currentPinCancelCallback: (() -> Unit)? = null
    private var currentCardNo: String? = null
    private var currentPinType: Int = 1 // Default to online PIN
    private var currentCardType: Int = 0
    private val isCheckingCard = AtomicBoolean(false)
    
    override fun detectCard(onResult: (requiresPin: Boolean, cardNo: String?) -> Unit, onError: (Throwable) -> Unit) {
        if (isCheckingCard.get()) {
            LogUtil.e(Constant.TAG, "Card detection already in progress")
            return
        }
        
        val readCard = readCardOptV2
        if (readCard == null) {
            onError(IllegalStateException("ReadCardOptV2 not available - PaySDK not connected"))
            return
        }
        
        try {
            isCheckingCard.set(true)
            currentCardCallback = onResult
            
            val cardType = AidlConstantsV2.CardType.NFC.getValue() or 
                          AidlConstantsV2.CardType.IC.getValue()
            
            readCard.checkCard(cardType, object : CheckCardCallbackV2Wrapper() {
                override fun findICCard(atr: String) {
                    LogUtil.e(Constant.TAG, "IC card detected: $atr")
                    currentCardType = AidlConstantsV2.CardType.IC.getValue()
                    // Start EMV process to extract card number
                    extractCardNumber(true) // IC cards typically require PIN
                }
                
                override fun findRFCard(uuid: String) {
                    LogUtil.e(Constant.TAG, "Contactless card detected: $uuid")
                    currentCardType = AidlConstantsV2.CardType.NFC.getValue()
                    // Start EMV process to extract card number
                    extractCardNumber(true) // Contactless may require PIN - CVM handler will determine
                }
                
                override fun findMagCard(bundle: android.os.Bundle) {
                    LogUtil.e(Constant.TAG, "Magnetic card detected")
                    currentCardType = AidlConstantsV2.CardType.MSR.getValue()
                    // Mag cards don't go through EMV, extract from track data if available
                    extractCardNumber(true) // Mag cards typically require PIN
                }
                
                override fun onError(code: Int, message: String) {
                    isCheckingCard.set(false)
                    LogUtil.e(Constant.TAG, "Card detection error: $code - $message")
                    currentCardCallback = null
                    onError(Exception("Card detection failed: $code - $message"))
                }
            }, 60)
            
        } catch (e: Exception) {
            isCheckingCard.set(false)
            ErrorHandler.logError(Constant.TAG, "detectCard", e)
            onError(e)
        }
    }
    
    override fun requestPin(
        cardNo: String,
        pinType: Int, // 0 = offline, 1 = online
        onDone: () -> Unit,
        onCancel: () -> Unit
    ) {
        val pinPad = pinPadOptV2
        if (pinPad == null) {
            LogUtil.e(Constant.TAG, "PinPadOptV2 not available")
            onCancel()
            return
        }
        
        try {
            currentCardNo = cardNo
            currentPinType = pinType
            currentPinCallback = onDone
            currentPinCancelCallback = onCancel
            
            val config = PinPadHelper.builder()
                .setCardNo(cardNo)
                .setPinType(pinType)
                .useActiveSlot()
                .setTimeout(60 * 1000)
                .setInputLength(4, 6)
                .setKeySystem(0) // MKSK
                .setAlgorithmType(0) // 3DES
                .build()
            
            pinPad.initPinPad(config, object : PinPadListenerV2Wrapper() {
                override fun onConfirm(type: Int, pinBlock: ByteArray) {
                    LogUtil.e(Constant.TAG, "PIN entered successfully")
                    currentPinCallback?.invoke()
                    currentPinCallback = null
                    currentPinCancelCallback = null
                }
                
                override fun onCancel() {
                    LogUtil.e(Constant.TAG, "PIN entry cancelled")
                    currentPinCancelCallback?.invoke()
                    currentPinCallback = null
                    currentPinCancelCallback = null
                }
                
                override fun onError(errCode: Int) {
                    LogUtil.e(Constant.TAG, "PIN entry error: $errCode")
                    currentPinCancelCallback?.invoke()
                    currentPinCallback = null
                    currentPinCancelCallback = null
                }
            })
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "requestPin", e)
            onCancel()
        }
    }
    
    override fun authorize(amountCents: Long, onResult: (approved: Boolean) -> Unit) {
        // This will be implemented by calling ProcessEmvTransactionUseCase
        // For now, return a placeholder
        LogUtil.e(Constant.TAG, "Authorize called with amount: $amountCents")
        // TODO: Implement full authorization flow
        onResult(false)
    }
    
    override fun printReceipt(copy: String, onDone: () -> Unit) {
        // TODO: Implement printing using PrinterOptV2
        LogUtil.e(Constant.TAG, "Print receipt: $copy")
        onDone()
    }
    
    fun cancelCardDetection() {
        if (isCheckingCard.get()) {
            try {
                readCardOptV2?.cancelCheckCard()
                isCheckingCard.set(false)
                currentCardCallback = null
            } catch (e: Exception) {
                ErrorHandler.logError(Constant.TAG, "cancelCardDetection", e)
            }
        }
    }
    
    /**
     * Extract card number from EMV tags after card detection
     * This starts a minimal EMV process to read card data
     */
    private fun extractCardNumber(requiresPin: Boolean) {
        val emv = emvOptV2
        if (emv == null) {
            LogUtil.e(Constant.TAG, "EMVOptV2 not available - cannot extract card number")
            isCheckingCard.set(false)
            currentCardCallback?.invoke(requiresPin, null)
            currentCardCallback = null
            return
        }
        
        try {
            // Initialize EMV process to read card data
            emv.initEmvProcess()
            
            // For contactless/IC cards, we need to start transaction process to read card data
            // However, we can try to extract PAN directly from EMV tags if available
            // Try to get card number from EMV tags (5A = Application PAN, 57 = Track 2 Equivalent)
            val cardNo = getCardNoFromEmv(emv)
            
            isCheckingCard.set(false)
            currentCardCallback?.invoke(requiresPin, cardNo)
            currentCardCallback = null
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "extractCardNumber", e)
            isCheckingCard.set(false)
            // Continue with null card number - it may be available later
            currentCardCallback?.invoke(requiresPin, null)
            currentCardCallback = null
        }
    }
    
    /**
     * Extract card number from EMV TLV tags (same logic as ProcessingActivity.getCardNo())
     */
    private fun getCardNoFromEmv(emv: EMVOptV2): String? {
        try {
            val tagList = arrayOf("57", "5A") // Track 2 Equivalent and Application PAN
            val outData = ByteArray(256)
            val len = emv.getTlvList(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL, tagList, outData)
            
            if (len <= 0) {
                LogUtil.e(Constant.TAG, "getCardNoFromEmv: No TLV data available (len=$len)")
                return null
            }
            
            val bytes = outData.copyOf(len)
            val tlvMap = TLVUtil.buildTLVMap(bytes)
            
            // Try tag 57 (Track 2 Equivalent) first - contains PAN in track format
            val tlv57 = tlvMap["57"]
            if (tlv57 != null && !TextUtils.isEmpty(tlv57.value)) {
                val cardInfo = parseTrack2(tlv57.value)
                if (!TextUtils.isEmpty(cardInfo.cardNo)) {
                    LogUtil.e(Constant.TAG, "Card number extracted from tag 57 (Track 2)")
                    return cardInfo.cardNo
                }
            }
            
            // Fallback to tag 5A (Application PAN)
            val tlv5A = tlvMap["5A"]
            if (tlv5A != null && !TextUtils.isEmpty(tlv5A.value)) {
                LogUtil.e(Constant.TAG, "Card number extracted from tag 5A (Application PAN)")
                return tlv5A.value
            }
            
            LogUtil.e(Constant.TAG, "getCardNoFromEmv: No card number found in EMV tags")
            return null
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "getCardNoFromEmv", e)
            return null
        }
    }
    
    /**
     * Parse Track 2 data to extract card number (same logic as ProcessingActivity.parseTrack2())
     */
    private fun parseTrack2(track2: String): CardInfo {
        val cardInfo = CardInfo()
        
        try {
            // Security: Never log full Track 2 data
            if (com.neo.neopayplus.BuildConfig.DEBUG && track2.length > 8) {
                val masked = track2.substring(0, 4) + "****" + track2.substring(track2.length - 4)
                LogUtil.e(Constant.TAG, "track2 (DEBUG, masked): $masked")
            }
            
            // Filter to keep only digits, =, and D
            val track2Filtered = track2.replace(Regex("[^0-9=D]"), "").trim()
            
            // Find separator (= or D)
            var index = track2Filtered.indexOf("=")
            if (index == -1) {
                index = track2Filtered.indexOf("D")
            }
            
            if (index == -1) {
                return cardInfo
            }
            
            // Extract card number (everything before separator)
            if (track2Filtered.length > index) {
                cardInfo.cardNo = track2Filtered.substring(0, index)
            }
            
            // Extract expiry date (4 digits after separator)
            if (track2Filtered.length > index + 5) {
                cardInfo.expireDate = track2Filtered.substring(index + 1, index + 5)
            }
            
            // Extract service code (3 digits after expiry)
            if (track2Filtered.length > index + 8) {
                cardInfo.serviceCode = track2Filtered.substring(index + 5, index + 8)
            }
            
            LogUtil.e(Constant.TAG, "Parsed Track 2: cardNo=${if (cardInfo.cardNo != null) maskCardNumber(cardInfo.cardNo!!) else "N/A"}, expireDate=${cardInfo.expireDate}, serviceCode=${cardInfo.serviceCode}")
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "parseTrack2", e)
        }
        
        return cardInfo
    }
    
    /**
     * Mask card number for security (show only first 4 and last 4 digits)
     */
    private fun maskCardNumber(cardNo: String): String {
        if (cardNo.length <= 8) {
            return "****"
        }
        val first4 = cardNo.substring(0, 4)
        val last4 = cardNo.substring(cardNo.length - 4)
        return "$first4****$last4"
    }
    
    /**
     * Card info data class
     */
    private data class CardInfo(
        var cardNo: String? = null,
        var expireDate: String? = null,
        var serviceCode: String? = null
    )
}

