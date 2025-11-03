package com.neo.neopayplus.host.dto;

import java.util.Map;

public class HostResult {
    public String rc;                // response code (e.g., 000)
    public String rrn;               // DE37
    public String authCode;          // DE38
    public Map<Integer, byte[]> fields; // optional raw field echoes
    public byte[] de62Bytes;         // DE62: Session keys TLV (for Master/Session model)
    
    /**
     * Check if response indicates approval
     */
    public boolean isApproved() {
        return "000".equals(rc) || "00".equals(rc);
    }
}
