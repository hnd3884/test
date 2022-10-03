package com.azul.crs.shared;

import java.util.Arrays;
import java.util.UUID;

public class Utils
{
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
    
    public static String uuid(final String value) {
        return UUID.nameUUIDFromBytes(value.getBytes()).toString();
    }
    
    public static String uuid(final Object... values) {
        return uuid(Arrays.toString(values));
    }
    
    public static String lower(final String s) {
        return (s != null) ? s.toLowerCase() : null;
    }
    
    public static long currentTimeCount() {
        return System.nanoTime();
    }
    
    public static long nextTimeCount(final long timeoutMillis) {
        return System.nanoTime() + timeoutMillis * 1000000L;
    }
    
    public static String elapsedTimeString(final long startTimeStamp) {
        return String.format(" (%,d ms)", elapsedTimeMillis(startTimeStamp));
    }
    
    public static long elapsedTimeMillis(final long startTimeCount) {
        return (System.nanoTime() - startTimeCount + 500000L) / 1000000L;
    }
    
    public static void sleep(final long time) {
        try {
            Thread.sleep(time);
        }
        catch (final InterruptedException ex) {}
    }
    
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
