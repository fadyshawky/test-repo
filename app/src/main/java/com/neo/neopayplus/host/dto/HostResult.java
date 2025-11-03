package com.neo.neopayplus.host.dto;

import java.util.Map;

public class HostResult {
    public String rc;                // response code (e.g., 000)
    public String rrn;               // DE37
    public String authCode;          // DE38
    public Map<Integer, byte[]> fields; // optional raw field echoes
}
