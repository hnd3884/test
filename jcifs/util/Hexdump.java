package jcifs.util;

import java.io.PrintStream;

public class Hexdump
{
    private static final String NL;
    private static final int NL_LENGTH;
    private static final char[] SPACE_CHARS;
    public static final char[] HEX_DIGITS;
    
    public static void hexdump(final PrintStream ps, final byte[] src, final int srcIndex, final int length) {
        if (length == 0) {
            return;
        }
        final int s = length % 16;
        final int r = (s == 0) ? (length / 16) : (length / 16 + 1);
        final char[] c = new char[r * (74 + Hexdump.NL_LENGTH)];
        final char[] d = new char[16];
        int si = 0;
        int ci = 0;
        do {
            Label_0056: {
                toHexChars(si, c, ci, 5);
            }
            ci += 5;
            c[ci++] = ':';
            while (true) {
                while (si != length) {
                    c[ci++] = ' ';
                    final int i = src[srcIndex + si] & 0xFF;
                    toHexChars(i, c, ci, 2);
                    ci += 2;
                    if (i < 0 || Character.isISOControl((char)i)) {
                        d[si % 16] = '.';
                    }
                    else {
                        d[si % 16] = (char)i;
                    }
                    if (++si % 16 == 0) {
                        c[ci++] = ' ';
                        c[ci++] = ' ';
                        c[ci++] = '|';
                        System.arraycopy(d, 0, c, ci, 16);
                        ci += 16;
                        c[ci++] = '|';
                        Hexdump.NL.getChars(0, Hexdump.NL_LENGTH, c, ci);
                        ci += Hexdump.NL_LENGTH;
                        continue Label_0056;
                    }
                }
                final int n = 16 - s;
                System.arraycopy(Hexdump.SPACE_CHARS, 0, c, ci, n * 3);
                ci += n * 3;
                System.arraycopy(Hexdump.SPACE_CHARS, 0, d, s, n);
                continue;
            }
        } while (si < length);
        ps.println(c);
    }
    
    public static String toHexString(final int val, final int size) {
        final char[] c = new char[size];
        toHexChars(val, c, 0, size);
        return new String(c);
    }
    
    public static String toHexString(final long val, final int size) {
        final char[] c = new char[size];
        toHexChars(val, c, 0, size);
        return new String(c);
    }
    
    public static String toHexString(final byte[] src, final int srcIndex, int size) {
        final char[] c = new char[size];
        size = ((size % 2 == 0) ? (size / 2) : (size / 2 + 1));
        int i = 0;
        int j = 0;
        while (i < size) {
            c[j++] = Hexdump.HEX_DIGITS[src[i] >> 4 & 0xF];
            if (j == c.length) {
                break;
            }
            c[j++] = Hexdump.HEX_DIGITS[src[i] & 0xF];
            ++i;
        }
        return new String(c);
    }
    
    public static void toHexChars(int val, final char[] dst, final int dstIndex, int size) {
        while (size > 0) {
            final int i = dstIndex + size - 1;
            if (i < dst.length) {
                dst[i] = Hexdump.HEX_DIGITS[val & 0xF];
            }
            if (val != 0) {
                val >>>= 4;
            }
            --size;
        }
    }
    
    public static void toHexChars(long val, final char[] dst, final int dstIndex, int size) {
        while (size > 0) {
            dst[dstIndex + size - 1] = Hexdump.HEX_DIGITS[(int)(val & 0xFL)];
            if (val != 0L) {
                val >>>= 4;
            }
            --size;
        }
    }
    
    static {
        NL = System.getProperty("line.separator");
        NL_LENGTH = Hexdump.NL.length();
        SPACE_CHARS = new char[] { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
