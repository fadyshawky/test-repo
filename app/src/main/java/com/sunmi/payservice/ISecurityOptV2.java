package com.sunmi.payservice;

/**
 * Mock Sunmi SDK shim for JVM unit test compilation.
 * Real PayLib AAR provides actual implementations on device.
 */
public interface ISecurityOptV2 {

    default int getKeyCheckValue(int type, int index, byte[] kcv) { return 0; }

}

