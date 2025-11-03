package com.neo.neopayplus.utils;

public class De53Builder {
    /**
     * Returns default DE53 Security Control Information (PIN/KEY indicators)
     * Common default: 2600000000000000 (PIN present, 3DES)
     */
    public static String defaultValue(boolean pinPresent) {
        return pinPresent ? "2600000000000000" : "0000000000000000";
    }
}
