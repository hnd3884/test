package biz.source_code.base64Coder;

public class Base64Coder
{
    private static final String systemLineSeparator;
    private static char[] map1;
    private static byte[] map2;
    
    public static String encodeString(final String s) {
        return new String(encode(s.getBytes()));
    }
    
    public static String encodeLines(final byte[] array) {
        return encodeLines(array, 0, array.length, 76, Base64Coder.systemLineSeparator);
    }
    
    public static String encodeLines(final byte[] array, final int n, final int n2, final int n3, final String s) {
        final int n4 = n3 * 3 / 4;
        if (n4 <= 0) {
            throw new IllegalArgumentException();
        }
        final StringBuilder sb = new StringBuilder((n2 + 2) / 3 * 4 + (n2 + n4 - 1) / n4 * s.length());
        int min;
        for (int i = 0; i < n2; i += min) {
            min = Math.min(n2 - i, n4);
            sb.append(encode(array, n + i, min));
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static char[] encode(final byte[] array) {
        return encode(array, 0, array.length);
    }
    
    public static char[] encode(final byte[] array, final int n) {
        return encode(array, 0, n);
    }
    
    public static char[] encode(final byte[] array, final int n, final int n2) {
        final int n3 = (n2 * 4 + 2) / 3;
        final char[] array2 = new char[(n2 + 2) / 3 * 4];
        int n6;
        int n7;
        int n8;
        int n9;
        int n10;
        int n11;
        int n12;
        for (int i = n, n4 = n + n2, n5 = 0; i < n4; n6 = (array[i++] & 0xFF), n7 = ((i < n4) ? (array[i++] & 0xFF) : 0), n8 = ((i < n4) ? (array[i++] & 0xFF) : 0), n9 = n6 >>> 2, n10 = ((n6 & 0x3) << 4 | n7 >>> 4), n11 = ((n7 & 0xF) << 2 | n8 >>> 6), n12 = (n8 & 0x3F), array2[n5++] = Base64Coder.map1[n9], array2[n5++] = Base64Coder.map1[n10], array2[n5] = ((n5 < n3) ? Base64Coder.map1[n11] : '='), ++n5, array2[n5] = ((n5 < n3) ? Base64Coder.map1[n12] : '='), ++n5) {}
        return array2;
    }
    
    public static String decodeString(final String s) {
        return new String(decode(s));
    }
    
    public static byte[] decodeLines(final String s) {
        final char[] array = new char[s.length()];
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 != ' ' && char1 != '\r' && char1 != '\n' && char1 != '\t') {
                array[n++] = char1;
            }
        }
        return decode(array, 0, n);
    }
    
    public static byte[] decode(final String s) {
        return decode(s.toCharArray());
    }
    
    public static byte[] decode(final char[] array) {
        return decode(array, 0, array.length);
    }
    
    public static byte[] decode(final char[] array, final int n, int n2) {
        if (n2 % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        }
        while (n2 > 0 && array[n + n2 - 1] == '=') {
            --n2;
        }
        final int n3 = n2 * 3 / 4;
        final byte[] array2 = new byte[n3];
        int i = n;
        final int n4 = n + n2;
        int n5 = 0;
        while (i < n4) {
            final char c = array[i++];
            final char c2 = array[i++];
            final char c3 = (i < n4) ? array[i++] : 'A';
            final char c4 = (i < n4) ? array[i++] : 'A';
            if (c > '\u007f' || c2 > '\u007f' || c3 > '\u007f' || c4 > '\u007f') {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            final byte b = Base64Coder.map2[c];
            final byte b2 = Base64Coder.map2[c2];
            final byte b3 = Base64Coder.map2[c3];
            final byte b4 = Base64Coder.map2[c4];
            if (b < 0 || b2 < 0 || b3 < 0 || b4 < 0) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            final int n6 = b << 2 | b2 >>> 4;
            final int n7 = (b2 & 0xF) << 4 | b3 >>> 2;
            final int n8 = (b3 & 0x3) << 6 | b4;
            array2[n5++] = (byte)n6;
            if (n5 < n3) {
                array2[n5++] = (byte)n7;
            }
            if (n5 >= n3) {
                continue;
            }
            array2[n5++] = (byte)n8;
        }
        return array2;
    }
    
    private Base64Coder() {
    }
    
    static {
        systemLineSeparator = System.getProperty("line.separator");
        Base64Coder.map1 = new char[64];
        int n = 0;
        for (char c = 'A'; c <= 'Z'; ++c) {
            Base64Coder.map1[n++] = c;
        }
        for (char c2 = 'a'; c2 <= 'z'; ++c2) {
            Base64Coder.map1[n++] = c2;
        }
        for (char c3 = '0'; c3 <= '9'; ++c3) {
            Base64Coder.map1[n++] = c3;
        }
        Base64Coder.map1[n++] = '+';
        Base64Coder.map1[n++] = '/';
        Base64Coder.map2 = new byte[128];
        for (int i = 0; i < Base64Coder.map2.length; ++i) {
            Base64Coder.map2[i] = -1;
        }
        for (int j = 0; j < 64; ++j) {
            Base64Coder.map2[Base64Coder.map1[j]] = (byte)j;
        }
    }
}
