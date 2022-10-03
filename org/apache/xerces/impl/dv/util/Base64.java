package org.apache.xerces.impl.dv.util;

public final class Base64
{
    private static final int BASELENGTH = 128;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int SIXBIT = 6;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final char PAD = '=';
    private static final boolean fDebug = false;
    private static final byte[] base64Alphabet;
    private static final char[] lookUpBase64Alphabet;
    
    protected static boolean isWhiteSpace(final char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
    
    protected static boolean isPad(final char c) {
        return c == '=';
    }
    
    protected static boolean isData(final char c) {
        return c < '\u0080' && Base64.base64Alphabet[c] != -1;
    }
    
    protected static boolean isBase64(final char c) {
        return isWhiteSpace(c) || isPad(c) || isData(c);
    }
    
    public static String encode(final byte[] array) {
        if (array == null) {
            return null;
        }
        final int n = array.length * 8;
        if (n == 0) {
            return "";
        }
        final int n2 = n % 24;
        final int n3 = n / 24;
        final char[] array2 = new char[((n2 != 0) ? (n3 + 1) : n3) * 4];
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            final byte b = array[n5++];
            final byte b2 = array[n5++];
            final byte b3 = array[n5++];
            final byte b4 = (byte)(b2 & 0xF);
            final byte b5 = (byte)(b & 0x3);
            final byte b6 = ((b & 0xFFFFFF80) == 0x0) ? ((byte)(b >> 2)) : ((byte)(b >> 2 ^ 0xC0));
            final byte b7 = ((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0));
            final byte b8 = ((b3 & 0xFFFFFF80) == 0x0) ? ((byte)(b3 >> 6)) : ((byte)(b3 >> 6 ^ 0xFC));
            array2[n4++] = Base64.lookUpBase64Alphabet[b6];
            array2[n4++] = Base64.lookUpBase64Alphabet[b7 | b5 << 4];
            array2[n4++] = Base64.lookUpBase64Alphabet[b4 << 2 | b8];
            array2[n4++] = Base64.lookUpBase64Alphabet[b3 & 0x3F];
        }
        if (n2 == 8) {
            final byte b9 = array[n5];
            final byte b10 = (byte)(b9 & 0x3);
            array2[n4++] = Base64.lookUpBase64Alphabet[((b9 & 0xFFFFFF80) == 0x0) ? ((byte)(b9 >> 2)) : ((byte)(b9 >> 2 ^ 0xC0))];
            array2[n4++] = Base64.lookUpBase64Alphabet[b10 << 4];
            array2[n4++] = '=';
            array2[n4++] = '=';
        }
        else if (n2 == 16) {
            final byte b11 = array[n5];
            final byte b12 = array[n5 + 1];
            final byte b13 = (byte)(b12 & 0xF);
            final byte b14 = (byte)(b11 & 0x3);
            final byte b15 = ((b11 & 0xFFFFFF80) == 0x0) ? ((byte)(b11 >> 2)) : ((byte)(b11 >> 2 ^ 0xC0));
            final byte b16 = ((b12 & 0xFFFFFF80) == 0x0) ? ((byte)(b12 >> 4)) : ((byte)(b12 >> 4 ^ 0xF0));
            array2[n4++] = Base64.lookUpBase64Alphabet[b15];
            array2[n4++] = Base64.lookUpBase64Alphabet[b16 | b14 << 4];
            array2[n4++] = Base64.lookUpBase64Alphabet[b13 << 2];
            array2[n4++] = '=';
        }
        return new String(array2);
    }
    
    public static byte[] decode(final String s) {
        if (s == null) {
            return null;
        }
        final char[] charArray = s.toCharArray();
        final int removeWhiteSpace = removeWhiteSpace(charArray);
        if (removeWhiteSpace % 4 != 0) {
            return null;
        }
        final int n = removeWhiteSpace / 4;
        if (n == 0) {
            return new byte[0];
        }
        int i = 0;
        int n2 = 0;
        int n3 = 0;
        final byte[] array = new byte[n * 3];
        while (i < n - 1) {
            final char c;
            final char c2;
            final char c3;
            final char c4;
            if (!isData(c = charArray[n3++]) || !isData(c2 = charArray[n3++]) || !isData(c3 = charArray[n3++]) || !isData(c4 = charArray[n3++])) {
                return null;
            }
            final byte b = Base64.base64Alphabet[c];
            final byte b2 = Base64.base64Alphabet[c2];
            final byte b3 = Base64.base64Alphabet[c3];
            final byte b4 = Base64.base64Alphabet[c4];
            array[n2++] = (byte)(b << 2 | b2 >> 4);
            array[n2++] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            array[n2++] = (byte)(b3 << 6 | b4);
            ++i;
        }
        final char c5;
        final char c6;
        if (!isData(c5 = charArray[n3++]) || !isData(c6 = charArray[n3++])) {
            return null;
        }
        final byte b5 = Base64.base64Alphabet[c5];
        final byte b6 = Base64.base64Alphabet[c6];
        final char c7 = charArray[n3++];
        final char c8 = charArray[n3++];
        if (isData(c7) && isData(c8)) {
            final byte b7 = Base64.base64Alphabet[c7];
            final byte b8 = Base64.base64Alphabet[c8];
            array[n2++] = (byte)(b5 << 2 | b6 >> 4);
            array[n2++] = (byte)((b6 & 0xF) << 4 | (b7 >> 2 & 0xF));
            array[n2++] = (byte)(b7 << 6 | b8);
            return array;
        }
        if (isPad(c7) && isPad(c8)) {
            if ((b6 & 0xF) != 0x0) {
                return null;
            }
            final byte[] array2 = new byte[i * 3 + 1];
            System.arraycopy(array, 0, array2, 0, i * 3);
            array2[n2] = (byte)(b5 << 2 | b6 >> 4);
            return array2;
        }
        else {
            if (isPad(c7) || !isPad(c8)) {
                return null;
            }
            final byte b9 = Base64.base64Alphabet[c7];
            if ((b9 & 0x3) != 0x0) {
                return null;
            }
            final byte[] array3 = new byte[i * 3 + 2];
            System.arraycopy(array, 0, array3, 0, i * 3);
            array3[n2++] = (byte)(b5 << 2 | b6 >> 4);
            array3[n2] = (byte)((b6 & 0xF) << 4 | (b9 >> 2 & 0xF));
            return array3;
        }
    }
    
    protected static int removeWhiteSpace(final char[] array) {
        if (array == null) {
            return 0;
        }
        int n = 0;
        for (int length = array.length, i = 0; i < length; ++i) {
            if (!isWhiteSpace(array[i])) {
                array[n++] = array[i];
            }
        }
        return n;
    }
    
    static {
        base64Alphabet = new byte[128];
        lookUpBase64Alphabet = new char[64];
        for (int i = 0; i < 128; ++i) {
            Base64.base64Alphabet[i] = -1;
        }
        for (int j = 90; j >= 65; --j) {
            Base64.base64Alphabet[j] = (byte)(j - 65);
        }
        for (int k = 122; k >= 97; --k) {
            Base64.base64Alphabet[k] = (byte)(k - 97 + 26);
        }
        for (int l = 57; l >= 48; --l) {
            Base64.base64Alphabet[l] = (byte)(l - 48 + 52);
        }
        Base64.base64Alphabet[43] = 62;
        Base64.base64Alphabet[47] = 63;
        for (int n = 0; n <= 25; ++n) {
            Base64.lookUpBase64Alphabet[n] = (char)(65 + n);
        }
        for (int n2 = 26, n3 = 0; n2 <= 51; ++n2, ++n3) {
            Base64.lookUpBase64Alphabet[n2] = (char)(97 + n3);
        }
        for (int n4 = 52, n5 = 0; n4 <= 61; ++n4, ++n5) {
            Base64.lookUpBase64Alphabet[n4] = (char)(48 + n5);
        }
        Base64.lookUpBase64Alphabet[62] = '+';
        Base64.lookUpBase64Alphabet[63] = '/';
    }
}
