package com.neo.neopayplus.host.dto;

public class PinReq {
    public String pan;     // unmasked PAN for PIN block construction (test only)
    public String pin;     // plaintext PIN (test only; not used in production)
}
