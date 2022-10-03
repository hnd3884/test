package com.sun.xml.internal.org.jvnet.staxex;

class Base64Encoder
{
    private static final char[] encodeMap;
    
    private static char[] initEncodeMap() {
        final char[] map = new char[64];
        for (int i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }
        for (int i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }
        for (int i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }
        map[62] = '+';
        map[63] = '/';
        return map;
    }
    
    public static char encode(final int i) {
        return Base64Encoder.encodeMap[i & 0x3F];
    }
    
    public static byte encodeByte(final int i) {
        return (byte)Base64Encoder.encodeMap[i & 0x3F];
    }
    
    public static String print(final byte[] input, final int offset, final int len) {
        final char[] buf = new char[(len + 2) / 3 * 4];
        final int ptr = print(input, offset, len, buf, 0);
        assert ptr == buf.length;
        return new String(buf);
    }
    
    public static int print(final byte[] input, final int offset, final int len, final char[] buf, int ptr) {
        for (int i = offset; i < len; i += 3) {
            switch (len - i) {
                case 1: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4);
                    buf[ptr++] = '=';
                    buf[ptr++] = '=';
                    break;
                }
                case 2: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    buf[ptr++] = encode((input[i + 1] & 0xF) << 2);
                    buf[ptr++] = '=';
                    break;
                }
                default: {
                    buf[ptr++] = encode(input[i] >> 2);
                    buf[ptr++] = encode((input[i] & 0x3) << 4 | (input[i + 1] >> 4 & 0xF));
                    buf[ptr++] = encode((input[i + 1] & 0xF) << 2 | (input[i + 2] >> 6 & 0x3));
                    buf[ptr++] = encode(input[i + 2] & 0x3F);
                    break;
                }
            }
        }
        return ptr;
    }
    
    static {
        encodeMap = initEncodeMap();
    }
}
