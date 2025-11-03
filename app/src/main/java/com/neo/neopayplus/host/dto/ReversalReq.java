package com.neo.neopayplus.host.dto;

public class ReversalReq {
    public String de11; // original STAN
    public String de37; // original RRN
    public String de4;  // amount
    public String de7;  // transmission datetime
    public String de12; // local time
    public String de24 = "400"; // function code reversal
    public String de32; // acquirer id
    public String de41; // tid
    public String de42; // mid
    public String de49; // currency
    public String de53; // security control info
}
