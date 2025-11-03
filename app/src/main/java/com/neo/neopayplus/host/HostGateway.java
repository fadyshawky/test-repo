package com.neo.neopayplus.host;

import com.neo.neopayplus.host.dto.HostResult;
import com.neo.neopayplus.host.dto.SessionInfo;
import com.neo.neopayplus.host.dto.KeyChangeReq;
import com.neo.neopayplus.host.dto.PurchaseReq;
import com.neo.neopayplus.host.dto.ReversalReq;
import com.neo.neopayplus.host.dto.SettlementReq;
import com.neo.neopayplus.host.dto.PinReq;

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
