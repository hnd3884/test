package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.OutputStream;
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
    
    public static void copyStream(final InputStream is, final OutputStream out) throws IOException {
        final int size = 1024;
        final byte[] buf = new byte[size];
        int len;
        while ((len = is.read(buf, 0, size)) != -1) {
            out.write(buf, 0, len);
        }
    }
}
