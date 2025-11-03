package com.neo.neopayplus.host;

import com.neo.neopayplus.host.dto.*;
import com.neo.neopayplus.crypto.CryptoProvider;

public class MockHostGateway implements HostGateway {

    private final CryptoProvider crypto;

    public MockHostGateway(CryptoProvider crypto) {
        this.crypto = crypto;
    }

    @Override
    public HostResult signOn(SessionInfo s) {
        HostResult r = new HostResult();
        r.rc = "000";
        
        // For Master/Session model: return mock DE62 TLV with session keys
        // In production, this will come from the actual host response
        // Format: DF01 (version) + DF10 (TPK enc) + DF11 (TPK KCV) + DF12 (TAK enc) + DF13 (TAK KCV)
        // For testing, return empty or mock TLV
        // r.de62Bytes = buildMockDE62(); // Uncomment if needed for testing
        r.de62Bytes = new byte[0]; // Empty for now; will be populated by real host response
        
        return r;
    }

    @Override
    public HostResult keyChange(KeyChangeReq req) {
        HostResult r = new HostResult();
        r.rc = "000";
        return r;
    }

    @Override
    public HostResult purchase(PurchaseReq req) {
        HostResult r = new HostResult();
        r.rc = "000";
        r.rrn = generateRrn();
        r.authCode = "123456";
        return r;
    }

    @Override
    public HostResult reversal(ReversalReq req) {
        HostResult r = new HostResult();
        r.rc = "000";
        return r;
    }

    @Override
    public HostResult settlement(SettlementReq req) {
        HostResult r = new HostResult();
        r.rc = "000";
        return r;
    }

    @Override
    public byte[] mac(byte[] isoNo128) {
        return crypto.calcMac(isoNo128);
    }

    @Override
    public byte[] encryptPin(PinReq req) {
        return crypto.encryptPinBlock01(req.pan, req.pin);
    }

    private String generateRrn() {
        long ts = System.currentTimeMillis() / 1000;
        String v = String.valueOf(ts);
        return v.substring(Math.max(0, v.length() - 12));
    }
}
