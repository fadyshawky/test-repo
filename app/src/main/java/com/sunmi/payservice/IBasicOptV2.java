package com.sunmi.payservice;

/**
 * Mock Sunmi SDK shim for JVM unit test compilation.
 * Real PayLib AAR provides actual implementations on device.
 */
public interface IBasicOptV2 {

    default int sysPowerManage(int mode) { return 0; }

    default String getSysParam(String key) { return ""; }

    default int setSysParam(String key, String value) { return 0; }

}

