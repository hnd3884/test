package org.w3c.tidy;

public final class EncodingUtils
{
    public static final int UNICODE_BOM_BE = 65279;
    public static final int UNICODE_BOM = 65279;
    public static final int UNICODE_BOM_LE = 65534;
    public static final int UNICODE_BOM_UTF8 = 15711167;
    public static final int FSM_ASCII = 0;
    public static final int FSM_ESC = 1;
    public static final int FSM_ESCD = 2;
    public static final int FSM_ESCDP = 3;
    public static final int FSM_ESCP = 4;
    public static final int FSM_NONASCII = 5;
    public static final int MAX_UTF8_FROM_UCS4 = 1114111;
    public static final int MAX_UTF16_FROM_UCS4 = 1114111;
    public static final int LOW_UTF16_SURROGATE = 55296;
    public static final int UTF16_SURROGATES_BEGIN = 65536;
    public static final int UTF16_LOW_SURROGATE_BEGIN = 55296;
    public static final int UTF16_LOW_SURROGATE_END = 56319;
    public static final int UTF16_HIGH_SURROGATE_BEGIN = 56320;
    public static final int UTF16_HIGH_SURROGATE_END = 57343;
    public static final int HIGH_UTF16_SURROGATE = 57343;
    private static final int UTF8_BYTE_SWAP_NOT_A_CHAR = 65534;
    private static final int UTF8_NOT_A_CHAR = 65535;
    private static final int[] WIN2UNICODE;
    private static final int[] MAC2UNICODE;
    private static final int[] SYMBOL2UNICODE;
    private static final ValidUTF8Sequence[] VALID_UTF8;
    private static final int NUM_UTF8_SEQUENCES;
    private static final int[] OFFSET_UTF8_SEQUENCES;
    
    private EncodingUtils() {
    }
    
    protected static int decodeWin1252(final int n) {
        return EncodingUtils.WIN2UNICODE[n - 128];
    }
    
    protected static int decodeMacRoman(int n) {
        if (127 < n) {
            n = EncodingUtils.MAC2UNICODE[n - 128];
        }
        return n;
    }
    
    static int decodeSymbolFont(final int n) {
        if (n > 255) {
            return n;
        }
        return EncodingUtils.SYMBOL2UNICODE[n];
    }
    
    static boolean decodeUTF8BytesToChar(final int[] array, final int n, final byte[] array2, final GetBytes getBytes, final int[] array3, final int n2) {
        byte[] array4 = new byte[10];
        boolean b = false;
        if (array2.length != 0) {
            array4 = array2;
        }
        if (n == -1) {
            array[0] = n;
            array3[0] = 1;
            return false;
        }
        final int unsigned = TidyUtils.toUnsigned(n);
        int n3;
        int n4;
        if (unsigned <= 127) {
            n3 = unsigned;
            n4 = 1;
        }
        else if ((unsigned & 0xE0) == 0xC0) {
            n3 = (unsigned & 0x1F);
            n4 = 2;
        }
        else if ((unsigned & 0xF0) == 0xE0) {
            n3 = (unsigned & 0xF);
            n4 = 3;
        }
        else if ((unsigned & 0xF8) == 0xF0) {
            n3 = (unsigned & 0x7);
            n4 = 4;
        }
        else if ((unsigned & 0xFC) == 0xF8) {
            n3 = (unsigned & 0x3);
            n4 = 5;
            b = true;
        }
        else if ((unsigned & 0xFE) == 0xFC) {
            n3 = (unsigned & 0x1);
            n4 = 6;
            b = true;
        }
        else {
            n3 = unsigned;
            n4 = 1;
            b = true;
        }
        int i = 1;
        while (i < n4) {
            final int[] array5 = { 0 };
            if (getBytes != null && n4 - i > 0) {
                array5[0] = 1;
                getBytes.doGet(new int[] { array4[n2 + i - 1] }, array5, false);
                if (array5[0] <= 0) {
                    b = true;
                    n4 = i;
                    break;
                }
            }
            if ((array4[n2 + i - 1] & 0xC0) != 0x80) {
                b = true;
                n4 = i;
                if (getBytes != null) {
                    final int[] array6 = { array4[n2 + i - 1] };
                    array5[0] = 1;
                    getBytes.doGet(array6, array5, true);
                    break;
                }
                break;
            }
            else {
                n3 = (n3 << 6 | (array4[n2 + i - 1] & 0x3F));
                ++i;
            }
        }
        if (!b && (n3 == 65534 || n3 == 65535)) {
            b = true;
        }
        if (!b && n3 > 1114111) {
            b = true;
        }
        if (!b && n3 >= 55296 && n3 <= 57343) {
            b = true;
        }
        if (!b) {
            final int n5 = EncodingUtils.OFFSET_UTF8_SEQUENCES[n4 - 1];
            final int n6 = EncodingUtils.OFFSET_UTF8_SEQUENCES[n4] - 1;
            if (n3 < EncodingUtils.VALID_UTF8[n5].lowChar || n3 > EncodingUtils.VALID_UTF8[n6].highChar) {
                b = true;
            }
            else {
                b = true;
                for (int j = n5; j <= n6; ++j) {
                    for (int k = 0; k < n4; ++k) {
                        char c;
                        if (!TidyUtils.toBoolean(k)) {
                            c = (char)n;
                        }
                        else {
                            c = (char)array4[n2 + k - 1];
                        }
                        if (c >= EncodingUtils.VALID_UTF8[j].validBytes[k * 2] && c <= EncodingUtils.VALID_UTF8[j].validBytes[k * 2 + 1]) {
                            b = false;
                        }
                        if (b) {
                            break;
                        }
                    }
                }
            }
        }
        array3[0] = n4;
        array[0] = n3;
        return b;
    }
    
    static boolean encodeCharToUTF8Bytes(final int n, final byte[] array, final PutBytes putBytes, final int[] array2) {
        int n2 = 0;
        byte[] array3 = new byte[10];
        if (array != null) {
            array3 = array;
        }
        boolean b = false;
        if (n <= 127) {
            array3[0] = (byte)n;
            n2 = 1;
        }
        else if (n <= 2047) {
            array3[0] = (byte)(0xC0 | n >> 6);
            array3[1] = (byte)(0x80 | (n & 0x3F));
            n2 = 2;
        }
        else if (n <= 65535) {
            array3[0] = (byte)(0xE0 | n >> 12);
            array3[1] = (byte)(0x80 | (n >> 6 & 0x3F));
            array3[2] = (byte)(0x80 | (n & 0x3F));
            n2 = 3;
            if (n == 65534 || n == 65535) {
                b = true;
            }
            else if (n >= 55296 && n <= 57343) {
                b = true;
            }
        }
        else if (n <= 2097151) {
            array3[0] = (byte)(0xF0 | n >> 18);
            array3[1] = (byte)(0x80 | (n >> 12 & 0x3F));
            array3[2] = (byte)(0x80 | (n >> 6 & 0x3F));
            array3[3] = (byte)(0x80 | (n & 0x3F));
            n2 = 4;
            if (n > 1114111) {
                b = true;
            }
        }
        else if (n <= 67108863) {
            array3[0] = (byte)(0xF8 | n >> 24);
            array3[1] = (byte)(0x80 | n >> 18);
            array3[2] = (byte)(0x80 | (n >> 12 & 0x3F));
            array3[3] = (byte)(0x80 | (n >> 6 & 0x3F));
            array3[4] = (byte)(0x80 | (n & 0x3F));
            n2 = 5;
            b = true;
        }
        else if (n <= Integer.MAX_VALUE) {
            array3[0] = (byte)(0xFC | n >> 30);
            array3[1] = (byte)(0x80 | (n >> 24 & 0x3F));
            array3[2] = (byte)(0x80 | (n >> 18 & 0x3F));
            array3[3] = (byte)(0x80 | (n >> 12 & 0x3F));
            array3[4] = (byte)(0x80 | (n >> 6 & 0x3F));
            array3[5] = (byte)(0x80 | (n & 0x3F));
            n2 = 6;
            b = true;
        }
        else {
            b = true;
        }
        if (!b && putBytes != null) {
            final int[] array4 = { n2 };
            putBytes.doPut(array3, array4);
            if (array4[0] < n2) {
                b = true;
            }
        }
        array2[0] = n2;
        return b;
    }
    
    static {
        WIN2UNICODE = new int[] { 8364, 0, 8218, 402, 8222, 8230, 8224, 8225, 710, 8240, 352, 8249, 338, 0, 381, 0, 0, 8216, 8217, 8220, 8221, 8226, 8211, 8212, 732, 8482, 353, 8250, 339, 0, 382, 376 };
        MAC2UNICODE = new int[] { 196, 197, 199, 201, 209, 214, 220, 225, 224, 226, 228, 227, 229, 231, 233, 232, 234, 235, 237, 236, 238, 239, 241, 243, 242, 244, 246, 245, 250, 249, 251, 252, 8224, 176, 162, 163, 167, 8226, 182, 223, 174, 169, 8482, 180, 168, 8800, 198, 216, 8734, 177, 8804, 8805, 165, 181, 8706, 8721, 8719, 960, 8747, 170, 186, 937, 230, 248, 191, 161, 172, 8730, 402, 8776, 8710, 171, 187, 8230, 160, 192, 195, 213, 338, 339, 8211, 8212, 8220, 8221, 8216, 8217, 247, 9674, 255, 376, 8260, 8364, 8249, 8250, 64257, 64258, 8225, 183, 8218, 8222, 8240, 194, 202, 193, 203, 200, 205, 206, 207, 204, 211, 212, 63743, 210, 218, 219, 217, 305, 710, 732, 175, 728, 729, 730, 184, 733, 731, 711 };
        SYMBOL2UNICODE = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 8704, 35, 8707, 37, 38, 8717, 40, 41, 8727, 43, 44, 8722, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 8773, 913, 914, 935, 916, 917, 934, 915, 919, 921, 977, 922, 923, 924, 925, 927, 928, 920, 929, 931, 932, 933, 962, 937, 926, 936, 918, 91, 8756, 93, 8869, 95, 175, 945, 946, 967, 948, 949, 966, 947, 951, 953, 981, 954, 955, 956, 957, 959, 960, 952, 961, 963, 964, 965, 982, 969, 958, 968, 950, 123, 124, 125, 8764, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 160, 978, 8242, 8804, 8260, 8734, 402, 9827, 9830, 9829, 9824, 8596, 8592, 8593, 8594, 8595, 176, 177, 8243, 8805, 215, 8733, 8706, 183, 247, 8800, 8801, 8776, 8230, 63, 63, 8629, 8501, 8465, 8476, 8472, 8855, 8853, 8709, 8745, 8746, 8835, 8839, 8836, 8834, 8838, 8712, 8713, 8736, 8711, 174, 169, 8482, 8719, 8730, 8901, 172, 8743, 8744, 8660, 8656, 8657, 8658, 8659, 9674, 9001, 174, 169, 8482, 8721, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63, 8364, 9002, 8747, 8992, 63, 8993, 63, 63, 63, 63, 63, 63, 63, 63, 63, 63 };
        VALID_UTF8 = new ValidUTF8Sequence[] { new ValidUTF8Sequence(0, 127, 1, new char[] { '\0', '\u007f', '\0', '\0', '\0', '\0', '\0', '\0' }), new ValidUTF8Sequence(128, 2047, 2, new char[] { '\u00c2', '\u00df', '\u0080', '¿', '\0', '\0', '\0', '\0' }), new ValidUTF8Sequence(2048, 4095, 3, new char[] { '\u00e0', '\u00e0', ' ', '¿', '\u0080', '¿', '\0', '\0' }), new ValidUTF8Sequence(4096, 65535, 3, new char[] { '\u00e1', '\u00ef', '\u0080', '¿', '\u0080', '¿', '\0', '\0' }), new ValidUTF8Sequence(65536, 262143, 4, new char[] { '\u00f0', '\u00f0', '\u0090', '¿', '\u0080', '¿', '\u0080', '¿' }), new ValidUTF8Sequence(262144, 1048575, 4, new char[] { '\u00f1', '\u00f3', '\u0080', '¿', '\u0080', '¿', '\u0080', '¿' }), new ValidUTF8Sequence(1048576, 1114111, 4, new char[] { '\u00f4', '\u00f4', '\u0080', '\u008f', '\u0080', '¿', '\u0080', '¿' }) };
        NUM_UTF8_SEQUENCES = EncodingUtils.VALID_UTF8.length;
        OFFSET_UTF8_SEQUENCES = new int[] { 0, 1, 2, 4, EncodingUtils.NUM_UTF8_SEQUENCES };
    }
    
    interface GetBytes
    {
        void doGet(final int[] p0, final int[] p1, final boolean p2);
    }
    
    interface PutBytes
    {
        void doPut(final byte[] p0, final int[] p1);
    }
}
