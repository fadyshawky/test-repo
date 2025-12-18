package com.neo.neopayplus.utils

import com.sunmi.pay.hardware.aidl.bean.CardInfo
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for Track2Parser
 */
class Track2ParserTest {
    
    @Test
    fun `test parseTrack2 with valid data`() {
        val track2 = "1234567890123456=2512=101"
        val cardInfo = Track2Parser.parseTrack2(track2)
        
        assertEquals("1234567890123456", cardInfo.cardNo)
        assertEquals("2512", cardInfo.expireDate)
        assertEquals("101", cardInfo.serviceCode)
    }
    
    @Test
    fun `test parseTrack2 with D separator`() {
        val track2 = "1234567890123456D2512D101"
        val cardInfo = Track2Parser.parseTrack2(track2)
        
        assertEquals("1234567890123456", cardInfo.cardNo)
        assertEquals("2512", cardInfo.expireDate)
        assertEquals("101", cardInfo.serviceCode)
    }
    
    @Test
    fun `test parseTrack2 with invalid data`() {
        val track2 = "invalid"
        val cardInfo = Track2Parser.parseTrack2(track2)
        
        assertNull(cardInfo.cardNo)
        assertNull(cardInfo.expireDate)
        assertNull(cardInfo.serviceCode)
    }
    
    @Test
    fun `test parseTrack2 with null`() {
        val cardInfo = Track2Parser.parseTrack2(null)
        
        assertNull(cardInfo.cardNo)
        assertNull(cardInfo.expireDate)
        assertNull(cardInfo.serviceCode)
    }
    
    @Test
    fun `test parseTrack2 with empty string`() {
        val cardInfo = Track2Parser.parseTrack2("")
        
        assertNull(cardInfo.cardNo)
        assertNull(cardInfo.expireDate)
        assertNull(cardInfo.serviceCode)
    }
    
    @Test
    fun `test parseTrack2 filters invalid characters`() {
        val track2 = "1234-5678-9012-3456=2512=101"
        val cardInfo = Track2Parser.parseTrack2(track2)
        
        assertEquals("1234567890123456", cardInfo.cardNo)
    }
}

