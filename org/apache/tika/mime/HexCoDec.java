package org.apache.tika.mime;

public class HexCoDec
{
    private static final char[] HEX_CHARS;
    
    public static byte[] decode(final String hexValue) {
        return decode(hexValue.toCharArray());
    }
    
    public static byte[] decode(final char[] hexChars) {
        return decode(hexChars, 0, hexChars.length);
    }
    
    public static byte[] decode(final char[] hexChars, int startIndex, final int length) {
        if ((length & 0x1) != 0x0) {
            throw new IllegalArgumentException("Length must be even");
        }
        final byte[] result = new byte[length / 2];
        for (int j = 0; j < result.length; ++j) {
            result[j] = (byte)(hexCharToNibble(hexChars[startIndex++]) * 16 + hexCharToNibble(hexChars[startIndex++]));
        }
        return result;
    }
    
    public static char[] encode(final byte[] bites) {
        return encode(bites, 0, bites.length);
    }
    
    public static char[] encode(final byte[] bites, int startIndex, final int length) {
        final char[] result = new char[length * 2];
        int i = 0;
        int j = 0;
        while (i < length) {
            final int bite = bites[startIndex++] & 0xFF;
            result[j++] = HexCoDec.HEX_CHARS[bite >> 4];
            result[j++] = HexCoDec.HEX_CHARS[bite & 0xF];
            ++i;
        }
        return result;
    }
    
    private static int hexCharToNibble(final char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'a' && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 10;
        }
        throw new IllegalArgumentException("Not a hex char - '" + ch + "'");
    }
    
    static {
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
