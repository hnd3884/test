package org.apache.lucene.util;

public final class ToStringUtils
{
    private static final char[] HEX;
    
    private ToStringUtils() {
    }
    
    public static void byteArray(final StringBuilder buffer, final byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            buffer.append("b[").append(i).append("]=").append(bytes[i]);
            if (i < bytes.length - 1) {
                buffer.append(',');
            }
        }
    }
    
    public static String longHex(long x) {
        final char[] asHex = new char[16];
        int i = 16;
        while (--i >= 0) {
            asHex[i] = ToStringUtils.HEX[(int)x & 0xF];
            x >>>= 4;
        }
        return "0x" + new String(asHex);
    }
    
    @Deprecated
    public static String boost(final float boost) {
        if (boost != 1.0f) {
            return "^" + Float.toString(boost);
        }
        return "";
    }
    
    static {
        HEX = "0123456789abcdef".toCharArray();
    }
}
