package com.neo.neopayplus.processing.usecase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

/**
 * Unit tests for PinEntryUseCase
 * 
 * Demonstrates how business logic can be tested in isolation.
 * Uses Robolectric to run Android-dependent code on JVM.
 */
@RunWith(RobolectricTestRunner.class)
public class PinEntryUseCaseTest {
    
    private PinEntryUseCase pinEntryUseCase;
    
    @Before
    public void setUp() {
        pinEntryUseCase = new PinEntryUseCase();
    }
    
    @Test
    public void testResetAttempts() {
        // Arrange: Decrement attempts first
        pinEntryUseCase.decrementAttempts();
        assertEquals(2, pinEntryUseCase.getRemainingAttempts());
        
        // Act
        pinEntryUseCase.resetAttempts();
        
        // Assert
        assertEquals(3, pinEntryUseCase.getRemainingAttempts());
    }
    
    @Test
    public void testDecrementAttempts() {
        // Act
        pinEntryUseCase.decrementAttempts();
        
        // Assert
        assertEquals(2, pinEntryUseCase.getRemainingAttempts());
    }
    
    @Test
    public void testAreAttemptsExhausted_WhenAttemptsRemaining() {
        // Assert
        assertFalse("Attempts should not be exhausted", pinEntryUseCase.areAttemptsExhausted());
    }
    
    @Test
    public void testAreAttemptsExhausted_WhenNoAttemptsRemaining() {
        // Arrange: Exhaust all attempts
        pinEntryUseCase.decrementAttempts(); // 2 left
        pinEntryUseCase.decrementAttempts(); // 1 left
        pinEntryUseCase.decrementAttempts(); // 0 left
        
        // Assert
        assertTrue("Attempts should be exhausted", pinEntryUseCase.areAttemptsExhausted());
    }
    
    @Test
    public void testHandlePinEntry_Success() {
        // Arrange
        byte[] pinBlock = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
        int pinType = 1; // Online PIN
        
        // Act
        PinEntryUseCase.PinEntryResult result = pinEntryUseCase.handlePinEntry(pinBlock, pinType);
        
        // Assert
        assertTrue("PIN entry should succeed", result.success);
        assertNotNull("PIN block should not be null", result.pinBlock);
        assertEquals(1, result.pinType);
        assertNull("Error message should be null", result.errorMessage);
    }
    
    @Test
    public void testHandlePinEntry_WithNullPinBlock() {
        // Act
        PinEntryUseCase.PinEntryResult result = pinEntryUseCase.handlePinEntry(null, 1);
        
        // Assert
        assertFalse("PIN entry should fail with null PIN block", result.success);
        assertNotNull("Error message should not be null", result.errorMessage);
    }
    
    @Test
    public void testHandlePinCancellation() {
        // Act
        PinEntryUseCase.PinEntryResult result = pinEntryUseCase.handlePinCancellation();
        
        // Assert
        assertFalse("Cancellation should fail", result.success);
        assertTrue("Error message should mention cancellation", 
                   result.errorMessage.contains("cancelled"));
    }
    
    @Test
    public void testHandlePinError() {
        // Act
        PinEntryUseCase.PinEntryResult result = pinEntryUseCase.handlePinError(123);
        
        // Assert
        assertFalse("PIN error should fail", result.success);
        assertTrue("Error message should contain error code", 
                   result.errorMessage.contains("123"));
    }
}

