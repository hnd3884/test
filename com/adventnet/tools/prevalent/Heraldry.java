package com.adventnet.tools.prevalent;

public final class Heraldry
{
    public static final char[] basis_32;
    public static final char[] REM_MAP;
    
    private Heraldry() {
    }
    
    public static String getString(final String ss) {
        int len = ss.length();
        String str = null;
        char[] ch = null;
        if (len < 5) {
            ch = new char[5];
            for (int i = 0; i < len; ++i) {
                ch[i] = ss.charAt(i);
            }
            for (int rem = 5 - len, j = 0; j < rem; ++j) {
                ch[len] = Heraldry.REM_MAP[j];
                ++len;
            }
            str = new String(ch);
        }
        else if (len % 5 != 0) {
            final int k = len % 5;
            final int rem2 = 5 - k;
            ch = new char[len + rem2];
            for (int l = 0; l < len; ++l) {
                ch[l] = ss.charAt(l);
            }
            for (int l = 0; l < rem2; ++l) {
                ch[len] = Heraldry.REM_MAP[l];
                ++len;
            }
            str = new String(ch);
        }
        else {
            str = ss;
        }
        return getString(str.toCharArray());
    }
    
    public static String getString(final char[] octetString) {
        int outIndex = 0;
        int i = 0;
        final char[] out = new char[((octetString.length - 1) / 5 + 1) * 8];
        final int len = octetString.length;
        while (i + 5 <= len) {
            final char[] ch = new char[5];
            for (int k = 0; k < 5; ++k) {
                ch[k] = octetString[i];
                ++i;
            }
            final char[] dd = base32encode(ch);
            for (int l = 0; l < 8; ++l) {
                out[outIndex] = dd[l];
                ++outIndex;
            }
        }
        return new String(out);
    }
    
    private static char[] base32encode(final char[] buf) {
        final char[] ch = { Heraldry.basis_32[buf[0] >> 3], Heraldry.basis_32[(buf[0] & '\u0007') << 2 | buf[1] >> 6], Heraldry.basis_32[(buf[1] & '?') >> 1], Heraldry.basis_32[(buf[1] & '\u0001') << 4 | buf[2] >> 4], Heraldry.basis_32[(buf[2] & '\u000f') << 1 | buf[3] >> 7], Heraldry.basis_32[(buf[3] & '\u007f') >> 2], Heraldry.basis_32[(buf[3] & '\u0003') << 3 | buf[4] >> 5], Heraldry.basis_32[buf[4] & '\u001f'] };
        return ch;
    }
    
    static {
        basis_32 = new char[] { 'A', 'c', 'x', 'D', '9', 'p', 'G', '3', 'u', 'J', 'K', 'l', 'f', 'd', 'n', 'Z', 'Q', 'R', 'S', 'T', 'y', 'V', '7', 'X', 'z', 'P', 'a', 'b', '1', 'N', 'e', 'M' };
        REM_MAP = new char[] { 'a', 'e', '5', 'z' };
    }
}
