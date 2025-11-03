package com.neo.neopayplus.host.dto;

import java.util.List;

public class SettlementReq {
    public String tid;
    public String mid;
    public String batchNo;
    public List<String> rrns; // list of rrns included
}
