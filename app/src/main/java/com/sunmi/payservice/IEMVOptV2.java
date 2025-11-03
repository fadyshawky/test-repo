package com.sunmi.payservice;

/**
 * Mock Sunmi SDK shim for JVM unit test compilation.
 * Real PayLib AAR provides actual implementations on device.
 */
public interface IEMVOptV2 {

    default int setTlvList(int op, String[] tags, String[] values) { return 0; }

    default int getTlv(int op, String tag, byte[] out) { return 0; }

    default int getTlvList(int op, String[] tags, byte[] out) { return 0; }

}

