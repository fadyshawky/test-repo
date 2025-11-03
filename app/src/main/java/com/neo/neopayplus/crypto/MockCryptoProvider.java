package com.neo.neopayplus.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MockCryptoProvider implements CryptoProvider {

    // TEST KEYS ONLY (lab): 16-byte 3DES key (K1||K2), expand to K1||K2||K1
    private final byte[] zpk16;

    public MockCryptoProvider() {
        // Default test key: 0123456789ABCDEFFEDCBA9876543210
        this.zpk16 = hex("0123456789ABCDEFFEDCBA9876543210");
    }

    public MockCryptoProvider(byte[] zpk16) {
        this.zpk16 = zpk16;
    }

    @Override
    public byte[] calcMac(byte[] msg) {
        // POS-only mock: return 8 zero bytes (replace with real MAC later)
        return new byte[8];
    }

    @Override
    public byte[] encryptPinBlock01(String pan, String pin) {
        try {
            byte[] pinBlock = buildIso0PinBlock(pan, pin);
            byte[] key24 = expandTo3Des(zpk16);
            SecretKey key = new SecretKeySpec(key24, "DESede");
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(pinBlock);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] buildIso0PinBlock(String pan, String pin) {
        // ISO-0: PIN field (0x0n | PIN | 0xFF padding) XOR with PAN12 (rightmost 12 excluding check digit)
        byte[] pinField = new byte[8];
        int pinLen = pin.length();
        pinField[0] = (byte) (0x20 | pinLen); // 0010nnnn
        int idx = 1;
        for (int i = 0; i < pinLen; i += 2) {
            int d1 = Character.digit(pin.charAt(i), 10);
            int d2 = (i + 1 < pinLen) ? Character.digit(pin.charAt(i + 1), 10) : 0x0F;
            pinField[idx++] = (byte) ((d1 << 4) | d2);
        }
        while (idx < 8) pinField[idx++] = (byte) 0xFF;

        // PAN 12 rightmost excluding check digit
        String pan12 = extractPan12(pan);
        byte[] panField = new byte[8];
        idx = 0;
        panField[idx++] = 0x00; panField[idx++] = 0x00;
        for (int i = 0; i < 12; i += 2) {
            int d1 = Character.digit(pan12.charAt(i), 10);
            int d2 = Character.digit(pan12.charAt(i + 1), 10);
            panField[idx++] = (byte) ((d1 << 4) | d2);
        }

        // XOR
        byte[] out = new byte[8];
        for (int i = 0; i < 8; i++) out[i] = (byte) (pinField[i] ^ panField[i]);
        return out;
    }

    private String extractPan12(String pan) {
        String digits = pan.replaceAll("[^0-9]", "");
        if (digits.length() < 13) return String.format("%012d", 0);
        String withoutCheck = digits.substring(0, digits.length() - 1);
        String right12 = withoutCheck.length() >= 12 ? withoutCheck.substring(withoutCheck.length() - 12) : String.format("%012d", 0);
        return right12;
    }

    private byte[] expandTo3Des(byte[] k16) {
        byte[] k24 = new byte[24];
        System.arraycopy(k16, 0, k24, 0, 16);
        System.arraycopy(k16, 0, k24, 16, 8);
        return k24;
    }

    private static byte[] hex(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return d;
    }
}
