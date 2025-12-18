package com.neo.neopayplus.utils

import com.neo.neopayplus.Constant
import com.sunmi.pay.hardware.aidl.bean.CardInfo

/**
 * Utility class for parsing Track 2 data
 * Extracted from ProcessingActivity to eliminate dependencies
 */
object Track2Parser {
    
    /**
     * Parse Track 2 data to extract card information
     * 
     * @param track2 Track 2 data string (format: PAN=ExpDate=ServiceCode or PANDExpDateServiceCode)
     * @return CardInfo containing cardNo, expireDate, and serviceCode
     */
    fun parseTrack2(track2: String?): CardInfo {
        val cardInfo = CardInfo()
        
        if (track2 == null || track2.isEmpty()) {
            return cardInfo
        }
        
        try {
            // Security: Never log full Track 2 data
            if (com.neo.neopayplus.BuildConfig.DEBUG && track2.length > 8) {
                // Mask Track 2 - show first 4 and last 4 chars
                val maskedTrack2 = track2.substring(0, 4) + "****" + track2.substring(track2.length - 4)
                LogUtil.e(Constant.TAG, "track2 (DEBUG, masked): $maskedTrack2")
            } else {
                LogUtil.e(Constant.TAG, "track2: Available (not logged for security)")
            }
            
            // Filter to keep only digits, =, and D
            val track2Filtered = stringFilter(track2)
            
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
            
            LogUtil.e(Constant.TAG, "cardNumber:${cardInfo.cardNo} expireDate:${cardInfo.expireDate} serviceCode:${cardInfo.serviceCode}")
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "parseTrack2", e)
        }
        
        return cardInfo
    }
    
    /**
     * Remove characters not number, =, D
     */
    private fun stringFilter(str: String): String {
        return str.replace(Regex("[^0-9=D]"), "").trim()
    }
}

