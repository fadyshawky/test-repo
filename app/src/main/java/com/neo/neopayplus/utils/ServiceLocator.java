package com.neo.neopayplus.utils;

import android.content.Context;

import com.neo.neopayplus.crypto.CryptoProvider;
import com.neo.neopayplus.host.HostGateway;

public final class ServiceLocator {
    private static HostGateway host;
    private static CryptoProvider crypto;
    private static Context ctx;
    private static StanProvider stan;

    private ServiceLocator() {}

    public static void init(HostGateway h, CryptoProvider c, Context context) {
        host = h; crypto = c; ctx = context;
        stan = new StanProvider();
    }

    public static HostGateway host() { return host; }
    public static CryptoProvider crypto() { return crypto; }
    public static StanProvider stan() { return stan; }
    public static Context ctx() { return ctx; }
}
