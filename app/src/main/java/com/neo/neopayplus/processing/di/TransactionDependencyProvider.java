package com.neo.neopayplus.processing.di;

import com.neo.neopayplus.processing.CvmHandler;
import com.neo.neopayplus.processing.repository.TransactionRepository;
import com.neo.neopayplus.processing.usecase.PinEntryUseCase;
import com.neo.neopayplus.processing.usecase.ProcessEmvTransactionUseCase;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;

/**
 * Dependency Provider for Transaction Processing
 * 
 * Provides dependencies for transaction processing components.
 * This is a simple manual dependency injection approach.
 * In the future, can be replaced with Dagger/Hilt.
 */
public class TransactionDependencyProvider {
    
    private final EMVOptV2 emvOptV2;
    private final ReadCardOptV2 readCardOptV2;
    private final PinPadOptV2 pinPadOptV2;
    private final SecurityOptV2 securityOptV2;
    
    public TransactionDependencyProvider(EMVOptV2 emvOptV2,
                                        ReadCardOptV2 readCardOptV2,
                                        PinPadOptV2 pinPadOptV2,
                                        SecurityOptV2 securityOptV2) {
        this.emvOptV2 = emvOptV2;
        this.readCardOptV2 = readCardOptV2;
        this.pinPadOptV2 = pinPadOptV2;
        this.securityOptV2 = securityOptV2;
    }
    
    /**
     * Create CVM Handler
     */
    public CvmHandler createCvmHandler() {
        return new CvmHandler(emvOptV2);
    }
    
    /**
     * Create Transaction Repository
     */
    public TransactionRepository createTransactionRepository() {
        return new TransactionRepository();
    }
    
    /**
     * Create PIN Entry Use Case
     */
    public PinEntryUseCase createPinEntryUseCase() {
        return new PinEntryUseCase();
    }
    
    /**
     * Create Process EMV Transaction Use Case
     */
    public ProcessEmvTransactionUseCase createProcessEmvTransactionUseCase() {
        TransactionRepository repository = createTransactionRepository();
        CvmHandler cvmHandler = createCvmHandler();
        return new ProcessEmvTransactionUseCase(emvOptV2, repository, cvmHandler);
    }
    
    // Getters for direct access to SDK components if needed
    public EMVOptV2 getEmvOptV2() {
        return emvOptV2;
    }
    
    public ReadCardOptV2 getReadCardOptV2() {
        return readCardOptV2;
    }
    
    public PinPadOptV2 getPinPadOptV2() {
        return pinPadOptV2;
    }
    
    public SecurityOptV2 getSecurityOptV2() {
        return securityOptV2;
    }
}

