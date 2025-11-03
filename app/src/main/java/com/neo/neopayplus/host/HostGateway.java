package com.neo.neopayplus.host;

public interface HostGateway {
    HostResult signOn(SessionInfo sessionInfo);
    HostResult keyChange(KeyChangeReq request);
    HostResult purchase(PurchaseReq request);
    HostResult reversal(ReversalReq request);
    HostResult settlement(SettlementReq request);

    // Crypto hooks (POS-only mocks; backend will handle in production)
    byte[] mac(byte[] isoWithout128);
    byte[] encryptPin(PinReq req);
}
