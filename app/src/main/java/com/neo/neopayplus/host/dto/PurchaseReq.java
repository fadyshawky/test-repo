package com.neo.neopayplus.host.dto;

import java.util.Map;

public class PurchaseReq {
    public String panMasked;      // masked PAN for logs
    public byte[] de55;           // EMV data
    public boolean pinPresent;

    public String de3;            // Processing code
    public String de4;            // Amount (minor units, 12n)
    public String de7;            // Transmission datetime (MMDDhhmmss)
    public String de11;           // STAN (6n)
    public String de12;           // Local time (hhmmss)
    public String de14;           // Expiry (YYMM)
    public String de18;           // MCC
    public String de22;           // Entry mode
    public String de24 = "200";  // Function code for purchase
    public String de32;           // Acquirer ID (LLVAR)
    public String de41;           // TID
    public String de42;           // MID
    public String de49;           // Currency
    public String de53;           // Security control info

    public byte[] de52;           // PIN block (if pinPresent)
    public String rrn;            // optional outbound; filled on response

    public Map<Integer, byte[]> extra; // optional
}
