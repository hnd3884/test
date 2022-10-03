package com.adventnet.tools.prevalent;

public class DString
{
    static final char[] b2c;
    static final char pad = '=';
    static byte[] c2b;
    
    public static String decode(final String s) {
        if (DString.c2b == null) {
            DString.c2b = new byte[256];
            for (byte b = 0; b < 64; ++b) {
                DString.c2b[(byte)DString.b2c[b]] = b;
            }
        }
        final byte[] nibble = new byte[4];
        final char[] decode = new char[s.length()];
        int d = 0;
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            nibble[n] = DString.c2b[c];
            if (c == '=') {
                break;
            }
            switch (n) {
                case 0: {
                    ++n;
                    break;
                }
                case 1: {
                    final byte b2 = (byte)(nibble[0] * 4 + nibble[1] / 16);
                    decode[d++] = (char)b2;
                    ++n;
                    break;
                }
                case 2: {
                    final byte b2 = (byte)((nibble[1] & 0xF) * 16 + nibble[2] / 4);
                    decode[d++] = (char)b2;
                    ++n;
                    break;
                }
                default: {
                    final byte b2 = (byte)((nibble[2] & 0x3) * 64 + nibble[3]);
                    decode[d++] = (char)b2;
                    n = 0;
                    break;
                }
            }
        }
        final String decoded = new String(decode, 0, d);
        return decoded;
    }
    
    static {
        b2c = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        DString.c2b = null;
    }
}
