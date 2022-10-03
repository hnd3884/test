package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.InputStream;

public class ASCIIUtility
{
    private ASCIIUtility() {
    }
    
    public static int parseInt(final byte[] b, final int start, final int end, final int radix) throws NumberFormatException {
        if (b == null) {
            throw new NumberFormatException("null");
        }
        int result = 0;
        boolean negative = false;
        int i;
        if (end <= (i = start)) {
            throw new NumberFormatException("illegal number");
        }
        int limit;
        if (b[i] == 45) {
            negative = true;
            limit = Integer.MIN_VALUE;
            ++i;
        }
        else {
            limit = -2147483647;
        }
        final int multmin = limit / radix;
        if (i < end) {
            final int digit = Character.digit((char)b[i++], radix);
            if (digit < 0) {
                throw new NumberFormatException("illegal number: " + toString(b, start, end));
            }
            result = -digit;
        }
        while (i < end) {
            final int digit = Character.digit((char)b[i++], radix);
            if (digit < 0) {
                throw new NumberFormatException("illegal number");
            }
            if (result < multmin) {
                throw new NumberFormatException("illegal number");
            }
            result *= radix;
            if (result < limit + digit) {
                throw new NumberFormatException("illegal number");
            }
            result -= digit;
        }
        if (!negative) {
            return -result;
        }
        if (i > start + 1) {
            return result;
        }
        throw new NumberFormatException("illegal number");
    }
    
    public static String toString(final byte[] b, final int start, final int end) {
        final int size = end - start;
        final char[] theChars = new char[size];
        for (int i = 0, j = start; i < size; theChars[i++] = (char)(b[j++] & 0xFF)) {}
        return new String(theChars);
    }
    
    public static byte[] getBytes(final String s) {
        final char[] chars = s.toCharArray();
        final int size = chars.length;
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; bytes[i] = (byte)chars[i++]) {}
        return bytes;
    }
    
    @Deprecated
    public static byte[] getBytes(final InputStream is) throws IOException {
        final ByteOutputStream bos = new ByteOutputStream();
        try {
            bos.write(is);
        }
        finally {
            is.close();
        }
        return bos.toByteArray();
    }
}
