package com.maverick.crypto.encoders;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class Base64
{
    private static final byte[] b;
    private static final byte[] c;
    
    public static byte[] encode(final byte[] array) {
        final int n = array.length % 3;
        byte[] array2;
        if (n == 0) {
            array2 = new byte[4 * array.length / 3];
        }
        else {
            array2 = new byte[4 * (array.length / 3 + 1)];
        }
        for (int n2 = array.length - n, i = 0, n3 = 0; i < n2; i += 3, n3 += 4) {
            final int n4 = array[i] & 0xFF;
            final int n5 = array[i + 1] & 0xFF;
            final int n6 = array[i + 2] & 0xFF;
            array2[n3] = Base64.b[n4 >>> 2 & 0x3F];
            array2[n3 + 1] = Base64.b[(n4 << 4 | n5 >>> 4) & 0x3F];
            array2[n3 + 2] = Base64.b[(n5 << 2 | n6 >>> 6) & 0x3F];
            array2[n3 + 3] = Base64.b[n6 & 0x3F];
        }
        switch (n) {
            case 1: {
                final int n7 = array[array.length - 1] & 0xFF;
                final int n8 = n7 >>> 2 & 0x3F;
                final int n9 = n7 << 4 & 0x3F;
                array2[array2.length - 4] = Base64.b[n8];
                array2[array2.length - 3] = Base64.b[n9];
                array2[array2.length - 2] = 61;
                array2[array2.length - 1] = 61;
                break;
            }
            case 2: {
                final int n10 = array[array.length - 2] & 0xFF;
                final int n11 = array[array.length - 1] & 0xFF;
                final int n12 = n10 >>> 2 & 0x3F;
                final int n13 = (n10 << 4 | n11 >>> 4) & 0x3F;
                final int n14 = n11 << 2 & 0x3F;
                array2[array2.length - 4] = Base64.b[n12];
                array2[array2.length - 3] = Base64.b[n13];
                array2[array2.length - 2] = Base64.b[n14];
                array2[array2.length - 1] = 61;
                break;
            }
        }
        return array2;
    }
    
    public static byte[] decode(final byte[] array) {
        byte[] array2;
        if (array[array.length - 2] == 61) {
            array2 = new byte[(array.length / 4 - 1) * 3 + 1];
        }
        else if (array[array.length - 1] == 61) {
            array2 = new byte[(array.length / 4 - 1) * 3 + 2];
        }
        else {
            array2 = new byte[array.length / 4 * 3];
        }
        for (int i = 0, n = 0; i < array.length - 4; i += 4, n += 3) {
            final byte b = Base64.c[array[i]];
            final byte b2 = Base64.c[array[i + 1]];
            final byte b3 = Base64.c[array[i + 2]];
            final byte b4 = Base64.c[array[i + 3]];
            array2[n] = (byte)(b << 2 | b2 >> 4);
            array2[n + 1] = (byte)(b2 << 4 | b3 >> 2);
            array2[n + 2] = (byte)(b3 << 6 | b4);
        }
        if (array[array.length - 2] == 61) {
            array2[array2.length - 1] = (byte)(Base64.c[array[array.length - 4]] << 2 | Base64.c[array[array.length - 3]] >> 4);
        }
        else if (array[array.length - 1] == 61) {
            final byte b5 = Base64.c[array[array.length - 4]];
            final byte b6 = Base64.c[array[array.length - 3]];
            final byte b7 = Base64.c[array[array.length - 2]];
            array2[array2.length - 2] = (byte)(b5 << 2 | b6 >> 4);
            array2[array2.length - 1] = (byte)(b6 << 4 | b7 >> 2);
        }
        else {
            final byte b8 = Base64.c[array[array.length - 4]];
            final byte b9 = Base64.c[array[array.length - 3]];
            final byte b10 = Base64.c[array[array.length - 2]];
            final byte b11 = Base64.c[array[array.length - 1]];
            array2[array2.length - 3] = (byte)(b8 << 2 | b9 >> 4);
            array2[array2.length - 2] = (byte)(b9 << 4 | b10 >> 2);
            array2[array2.length - 1] = (byte)(b10 << 6 | b11);
        }
        return array2;
    }
    
    private static boolean b(final char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }
    
    public static byte[] decode(final String s) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            decode(s, byteArrayOutputStream);
        }
        catch (final IOException ex) {
            throw new RuntimeException("exception decoding base64 string: " + ex);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    public static int decode(final String s, final OutputStream outputStream) throws IOException {
        int n = 0;
        int length;
        for (length = s.length(); length > 0 && b(s.charAt(length - 1)); --length) {}
        for (int i = 0; i < length - 4; i += 4) {
            if (!b(s.charAt(i))) {
                final byte b = Base64.c[s.charAt(i)];
                final byte b2 = Base64.c[s.charAt(i + 1)];
                final byte b3 = Base64.c[s.charAt(i + 2)];
                final byte b4 = Base64.c[s.charAt(i + 3)];
                outputStream.write(b << 2 | b2 >> 4);
                outputStream.write(b2 << 4 | b3 >> 2);
                outputStream.write(b3 << 6 | b4);
                n += 3;
            }
        }
        if (s.charAt(length - 2) == '=') {
            outputStream.write(Base64.c[s.charAt(length - 4)] << 2 | Base64.c[s.charAt(length - 3)] >> 4);
            ++n;
        }
        else if (s.charAt(length - 1) == '=') {
            final byte b5 = Base64.c[s.charAt(length - 4)];
            final byte b6 = Base64.c[s.charAt(length - 3)];
            final byte b7 = Base64.c[s.charAt(length - 2)];
            outputStream.write(b5 << 2 | b6 >> 4);
            outputStream.write(b6 << 4 | b7 >> 2);
            n += 2;
        }
        else {
            final byte b8 = Base64.c[s.charAt(length - 4)];
            final byte b9 = Base64.c[s.charAt(length - 3)];
            final byte b10 = Base64.c[s.charAt(length - 2)];
            final byte b11 = Base64.c[s.charAt(length - 1)];
            outputStream.write(b8 << 2 | b9 >> 4);
            outputStream.write(b9 << 4 | b10 >> 2);
            outputStream.write(b10 << 6 | b11);
            n += 3;
        }
        return n;
    }
    
    static {
        b = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        c = new byte[128];
        for (int i = 65; i <= 90; ++i) {
            Base64.c[i] = (byte)(i - 65);
        }
        for (int j = 97; j <= 122; ++j) {
            Base64.c[j] = (byte)(j - 97 + 26);
        }
        for (int k = 48; k <= 57; ++k) {
            Base64.c[k] = (byte)(k - 48 + 52);
        }
        Base64.c[43] = 62;
        Base64.c[47] = 63;
    }
}
