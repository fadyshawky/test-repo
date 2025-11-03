package com.neo.neopayplus.crypto;

public interface CryptoProvider {
    byte[] calcMac(byte[] msg);
    byte[] encryptPinBlock01(String pan, String pin);
}
