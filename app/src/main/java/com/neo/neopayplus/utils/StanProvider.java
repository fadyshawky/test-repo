package com.neo.neopayplus.utils;

import android.content.Context;
import com.neo.neopayplus.db.TxnDb;

public class StanProvider {
    public static synchronized String nextStan(Context ctx) {
        TxnDb db = new TxnDb(ctx);
        int stan = db.nextStan();
        return String.format("%06d", stan);
    }
}
