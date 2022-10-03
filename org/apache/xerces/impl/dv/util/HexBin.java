package org.apache.xerces.impl.dv.util;

public final class HexBin
{
    private static final int BASELENGTH = 128;
    private static final int LOOKUPLENGTH = 16;
    private static final byte[] hexNumberTable;
    private static final char[] lookUpHexAlphabet;
    
    public static String encode(final byte[] array) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        final char[] array2 = new char[length * 2];
        for (int i = 0; i < length; ++i) {
            int n = array[i];
            if (n < 0) {
                n += 256;
            }
            array2[i * 2] = HexBin.lookUpHexAlphabet[n >> 4];
            array2[i * 2 + 1] = HexBin.lookUpHexAlphabet[n & 0xF];
        }
        return new String(array2);
    }
    
    public static byte[] decode(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        if (length % 2 != 0) {
            return null;
        }
        final char[] charArray = s.toCharArray();
        final int n = length / 2;
        final byte[] array = new byte[n];
        for (int i = 0; i < n; ++i) {
            final char c = charArray[i * 2];
            final byte b = (byte)((c < '\u0080') ? HexBin.hexNumberTable[c] : -1);
            if (b == -1) {
                return null;
            }
            final char c2 = charArray[i * 2 + 1];
            final byte b2 = (byte)((c2 < '\u0080') ? HexBin.hexNumberTable[c2] : -1);
            if (b2 == -1) {
                return null;
            }
            array[i] = (byte)(b << 4 | b2);
        }
        return array;
    }
    
    static {
        hexNumberTable = new byte[128];
        lookUpHexAlphabet = new char[16];
        for (int i = 0; i < 128; ++i) {
            HexBin.hexNumberTable[i] = -1;
        }
        for (int j = 57; j >= 48; --j) {
            HexBin.hexNumberTable[j] = (byte)(j - 48);
        }
        for (int k = 70; k >= 65; --k) {
            HexBin.hexNumberTable[k] = (byte)(k - 65 + 10);
        }
        for (int l = 102; l >= 97; --l) {
            HexBin.hexNumberTable[l] = (byte)(l - 97 + 10);
        }
        for (int n = 0; n < 10; ++n) {
            HexBin.lookUpHexAlphabet[n] = (char)(48 + n);
        }
        for (int n2 = 10; n2 <= 15; ++n2) {
            HexBin.lookUpHexAlphabet[n2] = (char)(65 + n2 - 10);
        }
    }
}
