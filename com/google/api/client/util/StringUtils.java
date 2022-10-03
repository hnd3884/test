package com.google.api.client.util;

import java.nio.charset.StandardCharsets;

public class StringUtils
{
    public static final String LINE_SEPARATOR;
    
    public static byte[] getBytesUtf8(final String string) {
        if (string == null) {
            return null;
        }
        return string.getBytes(StandardCharsets.UTF_8);
    }
    
    public static String newStringUtf8(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    private StringUtils() {
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
