package org.glassfish.jersey.internal.util;

import java.io.UnsupportedEncodingException;

public class Base64
{
    private static final byte[] CHAR_SET;
    private static final byte[] BASE64INDEXES;
    
    public static byte[] encode(final byte[] buffer) {
        final int ccount = buffer.length / 3;
        final int rest = buffer.length % 3;
        final byte[] result = new byte[(ccount + ((rest > 0) ? 1 : 0)) * 4];
        for (int i = 0; i < ccount; ++i) {
            result[i * 4] = Base64.CHAR_SET[buffer[i * 3] >> 2 & 0xFF];
            result[i * 4 + 1] = Base64.CHAR_SET[((buffer[i * 3] & 0x3) << 4 | buffer[i * 3 + 1] >> 4) & 0xFF];
            result[i * 4 + 2] = Base64.CHAR_SET[((buffer[i * 3 + 1] & 0xF) << 2 | buffer[i * 3 + 2] >> 6) & 0xFF];
            result[i * 4 + 3] = Base64.CHAR_SET[buffer[i * 3 + 2] & 0x3F];
        }
        int temp = 0;
        if (rest > 0) {
            if (rest == 2) {
                result[ccount * 4 + 2] = Base64.CHAR_SET[(buffer[ccount * 3 + 1] & 0xF) << 2 & 0xFF];
                temp = buffer[ccount * 3 + 1] >> 4;
            }
            else {
                result[ccount * 4 + 2] = Base64.CHAR_SET[Base64.CHAR_SET.length - 1];
            }
            result[ccount * 4 + 3] = Base64.CHAR_SET[Base64.CHAR_SET.length - 1];
            result[ccount * 4 + 1] = Base64.CHAR_SET[((buffer[ccount * 3] & 0x3) << 4 | temp) & 0xFF];
            result[ccount * 4] = Base64.CHAR_SET[buffer[ccount * 3] >> 2 & 0xFF];
        }
        return result;
    }
    
    public static byte[] decode(final byte[] buffer) {
        if (buffer.length < 4 && buffer.length % 4 != 0) {
            return new byte[0];
        }
        final int ccount = buffer.length / 4;
        final int paddingCount = ((buffer[buffer.length - 1] == 61) + (buffer[buffer.length - 2] == 61)) ? 1 : 0;
        final byte[] result = new byte[3 * (ccount - 1) + (3 - paddingCount)];
        for (int i = 0; i < ccount - 1; ++i) {
            result[i * 3] = (byte)(Base64.BASE64INDEXES[buffer[i * 4]] << 2 | Base64.BASE64INDEXES[buffer[i * 4 + 1]] >> 4);
            result[i * 3 + 1] = (byte)(Base64.BASE64INDEXES[buffer[i * 4 + 1]] << 4 | Base64.BASE64INDEXES[buffer[i * 4 + 2]] >> 2);
            result[i * 3 + 2] = (byte)(Base64.BASE64INDEXES[buffer[i * 4 + 2]] << 6 | Base64.BASE64INDEXES[buffer[i * 4 + 3]]);
        }
        int i = ccount - 1;
        switch (paddingCount) {
            case 0: {
                result[i * 3 + 2] = (byte)(Base64.BASE64INDEXES[buffer[i * 4 + 2]] << 6 | Base64.BASE64INDEXES[buffer[i * 4 + 3]]);
                result[i * 3 + 1] = (byte)(Base64.BASE64INDEXES[buffer[i * 4 + 1]] << 4 | Base64.BASE64INDEXES[buffer[i * 4 + 2]] >> 2);
                result[i * 3] = (byte)(Base64.BASE64INDEXES[buffer[i * 4]] << 2 | Base64.BASE64INDEXES[buffer[i * 4 + 1]] >> 4);
                break;
            }
            case 1: {
                result[i * 3 + 1] = (byte)(Base64.BASE64INDEXES[buffer[i * 4 + 1]] << 4 | Base64.BASE64INDEXES[buffer[i * 4 + 2]] >> 2);
                result[i * 3] = (byte)(Base64.BASE64INDEXES[buffer[i * 4]] << 2 | Base64.BASE64INDEXES[buffer[i * 4 + 1]] >> 4);
                break;
            }
            case 2: {
                result[i * 3] = (byte)(Base64.BASE64INDEXES[buffer[i * 4]] << 2 | Base64.BASE64INDEXES[buffer[i * 4 + 1]] >> 4);
                break;
            }
        }
        return result;
    }
    
    public static String encodeAsString(final byte[] buffer) {
        final byte[] result = encode(buffer);
        try {
            return new String(result, "ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(result);
        }
    }
    
    public static String encodeAsString(final String text) {
        return encodeAsString(text.getBytes());
    }
    
    public static String decodeAsString(final byte[] buffer) {
        final byte[] result = decode(buffer);
        try {
            return new String(result, "ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(result);
        }
    }
    
    public static String decodeAsString(final String text) {
        return decodeAsString(text.getBytes());
    }
    
    static {
        final String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        byte[] cs;
        try {
            cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes("ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            cs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes();
        }
        CHAR_SET = cs;
        BASE64INDEXES = new byte[] { 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 62, 64, 64, 64, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 64, 64, 64, 64, 64, 64, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 64, 64, 64, 64, 64, 64, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64, 64 };
    }
}
