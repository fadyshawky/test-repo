package com.sunmi.payservice;

/**
 * Mock Sunmi SDK shim for JVM unit test compilation.
 * Real PayLib AAR provides actual implementations on device.
 */
public interface IReadCardOptV2 {

    default void checkCard(int type, Object callback, int timeout) {}

    default int cardOff(int type) { return 0; }

    default int apduCommand(int type, byte[] send, byte[] recv) { return 0; }

}

