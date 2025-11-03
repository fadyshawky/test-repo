package com.neo.neopayplus.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeUtil {
    private TimeUtil() {}

    public static String localTs() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss", Locale.US);
        return sdf.format(new Date());
    }

    public static String gmtTs() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * Local time in YYMMDDhhmmss format (for ISO8583 DE12)
     */
    public static String localYYMMDDhhmmss() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
        return sdf.format(new Date());
    }

    /**
     * GMT time in YYMMDDhhmm format (for ISO8583 DE7)
     */
    public static String gmtYYMMDDhhmm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
