package com.maverick.crypto.encoders;

public class Hex
{
    private static HexTranslator b;
    
    public static byte[] encode(final byte[] array) {
        return encode(array, 0, array.length);
    }
    
    public static byte[] encode(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2 * 2];
        Hex.b.encode(array, n, n2, array2, 0);
        return array2;
    }
    
    public static byte[] decode(final String s) {
        final byte[] array = new byte[s.length() / 2];
        final String lowerCase = s.toLowerCase();
        for (int i = 0; i < lowerCase.length(); i += 2) {
            final char char1 = lowerCase.charAt(i);
            final char char2 = lowerCase.charAt(i + 1);
            final int n = i / 2;
            if (char1 < 'a') {
                array[n] = (byte)(char1 - '0' << 4);
            }
            else {
                array[n] = (byte)(char1 - 'a' + 10 << 4);
            }
            if (char2 < 'a') {
                final byte[] array2 = array;
                final int n2 = n;
                array2[n2] += (byte)(char2 - '0');
            }
            else {
                final byte[] array3 = array;
                final int n3 = n;
                array3[n3] += (byte)(char2 - 'a' + 10);
            }
        }
        return array;
    }
    
    public static byte[] decode(final byte[] array) {
        final byte[] array2 = new byte[array.length / 2];
        Hex.b.decode(array, 0, array.length, array2, 0);
        return array2;
    }
    
    static {
        Hex.b = new HexTranslator();
    }
}
