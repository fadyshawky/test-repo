package com.neo.neopayplus.host.dto;

public class KeyChangeReq {
    public String tid;
    public String keyType; // ZPK/TPK
    public int keyIndex;   // 12, etc.
}
