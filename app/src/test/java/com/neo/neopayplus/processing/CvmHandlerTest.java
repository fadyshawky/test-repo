package com.neo.neopayplus.processing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.payservice.AidlConstantsV2;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CvmHandler
 * 
 * Demonstrates how clean architecture makes testing easier.
 * Uses Robolectric to run Android-dependent code on JVM.
 */
@RunWith(RobolectricTestRunner.class)
public class CvmHandlerTest {
    
    @Mock
    private EMVOptV2 mockEmvOptV2;
    
    private CvmHandler cvmHandler;
    
    @Before
    public void setUp() {
        // Initialize mocks when using Robolectric
        MockitoAnnotations.openMocks(this);
        cvmHandler = new CvmHandler(mockEmvOptV2);
    }
    
    @Test
    public void testExtractCvmResultCode_OnlinePin() throws Exception {
        // Arrange: Mock EMV kernel to return online PIN CVM code (01)
        byte[] mockCvmData = hexStringToByteArray("9F340101");
        when(mockEmvOptV2.getTlv(
            eq(AidlConstantsV2.EMV.TLVOpCode.OP_NORMAL),
            eq("9F34"),
            any(byte[].class)
        )).thenAnswer(invocation -> {
            byte[] outData = invocation.getArgument(2);
            System.arraycopy(mockCvmData, 0, outData, 0, mockCvmData.length);
            return mockCvmData.length;
        });
        
        // Act
        String result = cvmHandler.extractCvmResultCode();
        
        // Assert
        assertEquals("01", result);
    }
    
    @Test
    public void testDeterminePinHandling_OnlinePin() {
        // Act
        CvmHandler.CvmResult result = cvmHandler.determinePinHandling("01", 1);
        
        // Assert
        assertTrue("Should send PIN to backend for online PIN", result.shouldSendPinToBackend);
        assertEquals("01", result.code);
        assertEquals("Online PIN required", result.description);
    }
    
    @Test
    public void testDeterminePinHandling_OfflinePin() {
        // Act
        CvmHandler.CvmResult result = cvmHandler.determinePinHandling("42", 0);
        
        // Assert
        assertFalse("Should NOT send PIN to backend for offline PIN", result.shouldSendPinToBackend);
        assertEquals("42", result.code);
        assertEquals("Offline PIN verified by card", result.description);
    }
    
    @Test
    public void testDeterminePinHandling_NoCvm() {
        // Act
        CvmHandler.CvmResult result = cvmHandler.determinePinHandling("00", 0);
        
        // Assert
        assertFalse("Should NOT send PIN when no CVM required", result.shouldSendPinToBackend);
        assertEquals("00", result.code);
        assertEquals("No CVM required", result.description);
    }
    
    @Test
    public void testDeterminePinHandling_Cdcvm() {
        // Act
        CvmHandler.CvmResult result = cvmHandler.determinePinHandling("03", 0);
        
        // Assert
        assertFalse("Should NOT send PIN for CDCVM", result.shouldSendPinToBackend);
        assertEquals("03", result.code);
        assertTrue("Description should mention CDCVM", result.description.contains("CDCVM"));
    }
    
    @Test
    public void testDeterminePinHandling_FallbackToPinType() {
        // Act: When CVM code is null, fallback to PIN type
        CvmHandler.CvmResult result = cvmHandler.determinePinHandling(null, 1);
        
        // Assert: Should use PIN type as fallback
        assertTrue("Should send PIN when PIN type indicates online", result.shouldSendPinToBackend);
    }
    
    // Helper method
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}

