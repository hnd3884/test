package com.adventnet.tools.prevalent;

public final class AString
{
    public static final char[] alphabet;
    
    private AString() {
    }
    
    public static String getString(final String s) {
        return getString(s.getBytes());
    }
    
    public static String getString(final byte[] octetString) {
        char[] out;
        int outIndex;
        int i;
        int bits24;
        int bits25;
        for (out = new char[((octetString.length - 1) / 3 + 1) * 4], outIndex = 0, i = 0; i + 3 <= octetString.length; bits24 = (octetString[i++] & 0xFF) << 16, bits24 |= (octetString[i++] & 0xFF) << 8, bits24 |= (octetString[i++] & 0xFF) << 0, bits25 = (bits24 & 0xFC0000) >> 18, out[outIndex++] = AString.alphabet[bits25], bits25 = (bits24 & 0x3F000) >> 12, out[outIndex++] = AString.alphabet[bits25], bits25 = (bits24 & 0xFC0) >> 6, out[outIndex++] = AString.alphabet[bits25], bits25 = (bits24 & 0x3F), out[outIndex++] = AString.alphabet[bits25]) {}
        if (octetString.length - i == 2) {
            bits24 = (octetString[i] & 0xFF) << 16;
            bits24 |= (octetString[i + 1] & 0xFF) << 8;
            bits25 = (bits24 & 0xFC0000) >> 18;
            out[outIndex++] = AString.alphabet[bits25];
            bits25 = (bits24 & 0x3F000) >> 12;
            out[outIndex++] = AString.alphabet[bits25];
            bits25 = (bits24 & 0xFC0) >> 6;
            out[outIndex++] = AString.alphabet[bits25];
            out[outIndex++] = '1';
        }
        else if (octetString.length - i == 1) {
            bits24 = (octetString[i] & 0xFF) << 16;
            bits25 = (bits24 & 0xFC0000) >> 18;
            out[outIndex++] = AString.alphabet[bits25];
            bits25 = (bits24 & 0x3F000) >> 12;
            out[outIndex++] = AString.alphabet[bits25];
            out[outIndex++] = '1';
            out[outIndex++] = '1';
        }
        return new String(out);
    }
    
    static {
        alphabet = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', ']', 'J', 'K', 'L', 'M', 'N', '#', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', '[', 'm', 'n', '{', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '}', '=', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    }
}
