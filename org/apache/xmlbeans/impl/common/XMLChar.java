package org.apache.xmlbeans.impl.common;

public class XMLChar
{
    private static final byte[] CHARS;
    public static final int MASK_VALID = 1;
    public static final int MASK_SPACE = 2;
    public static final int MASK_NAME_START = 4;
    public static final int MASK_NAME = 8;
    public static final int MASK_PUBID = 16;
    public static final int MASK_CONTENT = 32;
    public static final int MASK_NCNAME_START = 64;
    public static final int MASK_NCNAME = 128;
    
    public static boolean isSupplemental(final int c) {
        return c >= 65536 && c <= 1114111;
    }
    
    public static int supplemental(final char h, final char l) {
        return (h - '\ud800') * 1024 + (l - '\udc00') + 65536;
    }
    
    public static char highSurrogate(final int c) {
        return (char)((c - 65536 >> 10) + 55296);
    }
    
    public static char lowSurrogate(final int c) {
        return (char)((c - 65536 & 0x3FF) + 56320);
    }
    
    public static boolean isHighSurrogate(final int c) {
        return 55296 <= c && c <= 56319;
    }
    
    public static boolean isLowSurrogate(final int c) {
        return 56320 <= c && c <= 57343;
    }
    
    public static boolean isValid(final int c) {
        return (c < 65536 && (XMLChar.CHARS[c] & 0x1) != 0x0) || (65536 <= c && c <= 1114111);
    }
    
    public static boolean isInvalid(final int c) {
        return !isValid(c);
    }
    
    public static boolean isContent(final int c) {
        return (c < 65536 && (XMLChar.CHARS[c] & 0x20) != 0x0) || (65536 <= c && c <= 1114111);
    }
    
    public static boolean isMarkup(final int c) {
        return c == 60 || c == 38 || c == 37;
    }
    
    public static boolean isSpace(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x2) != 0x0;
    }
    
    public static boolean isXML11Space(final int c) {
        return (c < 65536 && (XMLChar.CHARS[c] & 0x2) != 0x0) || c == 133 || c == 8232;
    }
    
    public static boolean isNameStart(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x4) != 0x0;
    }
    
    public static boolean isName(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x8) != 0x0;
    }
    
    public static boolean isNCNameStart(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x40) != 0x0;
    }
    
    public static boolean isNCName(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x80) != 0x0;
    }
    
    public static boolean isPubid(final int c) {
        return c < 65536 && (XMLChar.CHARS[c] & 0x10) != 0x0;
    }
    
    public static boolean isValidName(final String name) {
        if (name.length() == 0) {
            return false;
        }
        char ch = name.charAt(0);
        if (!isNameStart(ch)) {
            return false;
        }
        for (int i = 1; i < name.length(); ++i) {
            ch = name.charAt(i);
            if (!isName(ch)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidNCName(final String ncName) {
        if (ncName.length() == 0) {
            return false;
        }
        char ch = ncName.charAt(0);
        if (!isNCNameStart(ch)) {
            return false;
        }
        for (int i = 1; i < ncName.length(); ++i) {
            ch = ncName.charAt(i);
            if (!isNCName(ch)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidNmtoken(final String nmtoken) {
        if (nmtoken.length() == 0) {
            return false;
        }
        for (int i = 0; i < nmtoken.length(); ++i) {
            final char ch = nmtoken.charAt(i);
            if (!isName(ch)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidIANAEncoding(final String ianaEncoding) {
        if (ianaEncoding != null) {
            final int length = ianaEncoding.length();
            if (length > 0) {
                char c = ianaEncoding.charAt(0);
                if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                    for (int i = 1; i < length; ++i) {
                        c = ianaEncoding.charAt(i);
                        if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '.' && c != '_' && c != '-') {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isValidJavaEncoding(final String javaEncoding) {
        if (javaEncoding != null) {
            final int length = javaEncoding.length();
            if (length > 0) {
                for (int i = 1; i < length; ++i) {
                    final char c = javaEncoding.charAt(i);
                    if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '.' && c != '_' && c != '-') {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    static {
        CHARS = new byte[65536];
        final int[] charRange = { 9, 10, 13, 13, 32, 55295, 57344, 65533 };
        final int[] spaceChar = { 32, 9, 13, 10 };
        final int[] nameChar = { 45, 46 };
        final int[] nameStartChar = { 58, 95 };
        final int[] pubidChar = { 10, 13, 32, 33, 35, 36, 37, 61, 95 };
        final int[] pubidRange = { 39, 59, 63, 90, 97, 122 };
        final int[] letterRange = { 65, 90, 97, 122, 192, 214, 216, 246, 248, 305, 308, 318, 321, 328, 330, 382, 384, 451, 461, 496, 500, 501, 506, 535, 592, 680, 699, 705, 904, 906, 910, 929, 931, 974, 976, 982, 994, 1011, 1025, 1036, 1038, 1103, 1105, 1116, 1118, 1153, 1168, 1220, 1223, 1224, 1227, 1228, 1232, 1259, 1262, 1269, 1272, 1273, 1329, 1366, 1377, 1414, 1488, 1514, 1520, 1522, 1569, 1594, 1601, 1610, 1649, 1719, 1722, 1726, 1728, 1742, 1744, 1747, 1765, 1766, 2309, 2361, 2392, 2401, 2437, 2444, 2447, 2448, 2451, 2472, 2474, 2480, 2486, 2489, 2524, 2525, 2527, 2529, 2544, 2545, 2565, 2570, 2575, 2576, 2579, 2600, 2602, 2608, 2610, 2611, 2613, 2614, 2616, 2617, 2649, 2652, 2674, 2676, 2693, 2699, 2703, 2705, 2707, 2728, 2730, 2736, 2738, 2739, 2741, 2745, 2821, 2828, 2831, 2832, 2835, 2856, 2858, 2864, 2866, 2867, 2870, 2873, 2908, 2909, 2911, 2913, 2949, 2954, 2958, 2960, 2962, 2965, 2969, 2970, 2974, 2975, 2979, 2980, 2984, 2986, 2990, 2997, 2999, 3001, 3077, 3084, 3086, 3088, 3090, 3112, 3114, 3123, 3125, 3129, 3168, 3169, 3205, 3212, 3214, 3216, 3218, 3240, 3242, 3251, 3253, 3257, 3296, 3297, 3333, 3340, 3342, 3344, 3346, 3368, 3370, 3385, 3424, 3425, 3585, 3630, 3634, 3635, 3648, 3653, 3713, 3714, 3719, 3720, 3732, 3735, 3737, 3743, 3745, 3747, 3754, 3755, 3757, 3758, 3762, 3763, 3776, 3780, 3904, 3911, 3913, 3945, 4256, 4293, 4304, 4342, 4354, 4355, 4357, 4359, 4363, 4364, 4366, 4370, 4436, 4437, 4447, 4449, 4461, 4462, 4466, 4467, 4526, 4527, 4535, 4536, 4540, 4546, 7680, 7835, 7840, 7929, 7936, 7957, 7960, 7965, 7968, 8005, 8008, 8013, 8016, 8023, 8031, 8061, 8064, 8116, 8118, 8124, 8130, 8132, 8134, 8140, 8144, 8147, 8150, 8155, 8160, 8172, 8178, 8180, 8182, 8188, 8490, 8491, 8576, 8578, 12353, 12436, 12449, 12538, 12549, 12588, 44032, 55203, 12321, 12329, 19968, 40869 };
        final int[] letterChar = { 902, 908, 986, 988, 990, 992, 1369, 1749, 2365, 2482, 2654, 2701, 2749, 2784, 2877, 2972, 3294, 3632, 3716, 3722, 3725, 3749, 3751, 3760, 3773, 4352, 4361, 4412, 4414, 4416, 4428, 4430, 4432, 4441, 4451, 4453, 4455, 4457, 4469, 4510, 4520, 4523, 4538, 4587, 4592, 4601, 8025, 8027, 8029, 8126, 8486, 8494, 12295 };
        final int[] combiningCharRange = { 768, 837, 864, 865, 1155, 1158, 1425, 1441, 1443, 1465, 1467, 1469, 1473, 1474, 1611, 1618, 1750, 1756, 1757, 1759, 1760, 1764, 1767, 1768, 1770, 1773, 2305, 2307, 2366, 2380, 2385, 2388, 2402, 2403, 2433, 2435, 2496, 2500, 2503, 2504, 2507, 2509, 2530, 2531, 2624, 2626, 2631, 2632, 2635, 2637, 2672, 2673, 2689, 2691, 2750, 2757, 2759, 2761, 2763, 2765, 2817, 2819, 2878, 2883, 2887, 2888, 2891, 2893, 2902, 2903, 2946, 2947, 3006, 3010, 3014, 3016, 3018, 3021, 3073, 3075, 3134, 3140, 3142, 3144, 3146, 3149, 3157, 3158, 3202, 3203, 3262, 3268, 3270, 3272, 3274, 3277, 3285, 3286, 3330, 3331, 3390, 3395, 3398, 3400, 3402, 3405, 3636, 3642, 3655, 3662, 3764, 3769, 3771, 3772, 3784, 3789, 3864, 3865, 3953, 3972, 3974, 3979, 3984, 3989, 3993, 4013, 4017, 4023, 8400, 8412, 12330, 12335 };
        final int[] combiningCharChar = { 1471, 1476, 1648, 2364, 2381, 2492, 2494, 2495, 2519, 2562, 2620, 2622, 2623, 2748, 2876, 3031, 3415, 3633, 3761, 3893, 3895, 3897, 3902, 3903, 3991, 4025, 8417, 12441, 12442 };
        final int[] digitRange = { 48, 57, 1632, 1641, 1776, 1785, 2406, 2415, 2534, 2543, 2662, 2671, 2790, 2799, 2918, 2927, 3047, 3055, 3174, 3183, 3302, 3311, 3430, 3439, 3664, 3673, 3792, 3801, 3872, 3881 };
        final int[] extenderRange = { 12337, 12341, 12445, 12446, 12540, 12542 };
        final int[] extenderChar = { 183, 720, 721, 903, 1600, 3654, 3782, 12293 };
        final int[] specialChar = { 60, 38, 10, 13, 93 };
        for (int i = 0; i < charRange.length; i += 2) {
            for (int j = charRange[i]; j <= charRange[i + 1]; ++j) {
                final byte[] chars = XMLChar.CHARS;
                final int n = j;
                chars[n] |= 0x21;
            }
        }
        for (int i = 0; i < specialChar.length; ++i) {
            XMLChar.CHARS[specialChar[i]] &= 0xFFFFFFDF;
        }
        for (int i = 0; i < spaceChar.length; ++i) {
            final byte[] chars2 = XMLChar.CHARS;
            final int n2 = spaceChar[i];
            chars2[n2] |= 0x2;
        }
        for (int i = 0; i < nameStartChar.length; ++i) {
            final byte[] chars3 = XMLChar.CHARS;
            final int n3 = nameStartChar[i];
            chars3[n3] |= (byte)204;
        }
        for (int i = 0; i < letterRange.length; i += 2) {
            for (int j = letterRange[i]; j <= letterRange[i + 1]; ++j) {
                final byte[] chars4 = XMLChar.CHARS;
                final int n4 = j;
                chars4[n4] |= (byte)204;
            }
        }
        for (int i = 0; i < letterChar.length; ++i) {
            final byte[] chars5 = XMLChar.CHARS;
            final int n5 = letterChar[i];
            chars5[n5] |= (byte)204;
        }
        for (int i = 0; i < nameChar.length; ++i) {
            final byte[] chars6 = XMLChar.CHARS;
            final int n6 = nameChar[i];
            chars6[n6] |= (byte)136;
        }
        for (int i = 0; i < digitRange.length; i += 2) {
            for (int j = digitRange[i]; j <= digitRange[i + 1]; ++j) {
                final byte[] chars7 = XMLChar.CHARS;
                final int n7 = j;
                chars7[n7] |= (byte)136;
            }
        }
        for (int i = 0; i < combiningCharRange.length; i += 2) {
            for (int j = combiningCharRange[i]; j <= combiningCharRange[i + 1]; ++j) {
                final byte[] chars8 = XMLChar.CHARS;
                final int n8 = j;
                chars8[n8] |= (byte)136;
            }
        }
        for (int i = 0; i < combiningCharChar.length; ++i) {
            final byte[] chars9 = XMLChar.CHARS;
            final int n9 = combiningCharChar[i];
            chars9[n9] |= (byte)136;
        }
        for (int i = 0; i < extenderRange.length; i += 2) {
            for (int j = extenderRange[i]; j <= extenderRange[i + 1]; ++j) {
                final byte[] chars10 = XMLChar.CHARS;
                final int n10 = j;
                chars10[n10] |= (byte)136;
            }
        }
        for (int i = 0; i < extenderChar.length; ++i) {
            final byte[] chars11 = XMLChar.CHARS;
            final int n11 = extenderChar[i];
            chars11[n11] |= (byte)136;
        }
        final byte[] chars12 = XMLChar.CHARS;
        final int n12 = 58;
        chars12[n12] &= (byte)(-193);
        for (int i = 0; i < pubidChar.length; ++i) {
            final byte[] chars13 = XMLChar.CHARS;
            final int n13 = pubidChar[i];
            chars13[n13] |= 0x10;
        }
        for (int i = 0; i < pubidRange.length; i += 2) {
            for (int j = pubidRange[i]; j <= pubidRange[i + 1]; ++j) {
                final byte[] chars14 = XMLChar.CHARS;
                final int n14 = j;
                chars14[n14] |= 0x10;
            }
        }
    }
}
