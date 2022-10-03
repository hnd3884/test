package sun.text.normalizer;

public final class UTF16
{
    public static final int CODEPOINT_MIN_VALUE = 0;
    public static final int CODEPOINT_MAX_VALUE = 1114111;
    public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
    public static final int LEAD_SURROGATE_MIN_VALUE = 55296;
    public static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
    public static final int LEAD_SURROGATE_MAX_VALUE = 56319;
    public static final int TRAIL_SURROGATE_MAX_VALUE = 57343;
    public static final int SURROGATE_MIN_VALUE = 55296;
    private static final int LEAD_SURROGATE_SHIFT_ = 10;
    private static final int TRAIL_SURROGATE_MASK_ = 1023;
    private static final int LEAD_SURROGATE_OFFSET_ = 55232;
    
    public static int charAt(final String s, final int n) {
        final char char1 = s.charAt(n);
        if (char1 < '\ud800') {
            return char1;
        }
        return _charAt(s, n, char1);
    }
    
    private static int _charAt(final String s, int n, final char c) {
        if (c > '\udfff') {
            return c;
        }
        if (c <= '\udbff') {
            ++n;
            if (s.length() != n) {
                final char char1 = s.charAt(n);
                if (char1 >= '\udc00' && char1 <= '\udfff') {
                    return UCharacterProperty.getRawSupplementary(c, char1);
                }
            }
        }
        else if (--n >= 0) {
            final char char2 = s.charAt(n);
            if (char2 >= '\ud800' && char2 <= '\udbff') {
                return UCharacterProperty.getRawSupplementary(char2, c);
            }
        }
        return c;
    }
    
    public static int charAt(final char[] array, final int n, final int n2, int n3) {
        n3 += n;
        if (n3 < n || n3 >= n2) {
            throw new ArrayIndexOutOfBoundsException(n3);
        }
        final char c = array[n3];
        if (!isSurrogate(c)) {
            return c;
        }
        if (c <= '\udbff') {
            if (++n3 >= n2) {
                return c;
            }
            final char c2 = array[n3];
            if (isTrailSurrogate(c2)) {
                return UCharacterProperty.getRawSupplementary(c, c2);
            }
        }
        else {
            if (n3 == n) {
                return c;
            }
            --n3;
            final char c3 = array[n3];
            if (isLeadSurrogate(c3)) {
                return UCharacterProperty.getRawSupplementary(c3, c);
            }
        }
        return c;
    }
    
    public static int getCharCount(final int n) {
        if (n < 65536) {
            return 1;
        }
        return 2;
    }
    
    public static boolean isSurrogate(final char c) {
        return '\ud800' <= c && c <= '\udfff';
    }
    
    public static boolean isTrailSurrogate(final char c) {
        return '\udc00' <= c && c <= '\udfff';
    }
    
    public static boolean isLeadSurrogate(final char c) {
        return '\ud800' <= c && c <= '\udbff';
    }
    
    public static char getLeadSurrogate(final int n) {
        if (n >= 65536) {
            return (char)(55232 + (n >> 10));
        }
        return '\0';
    }
    
    public static char getTrailSurrogate(final int n) {
        if (n >= 65536) {
            return (char)(56320 + (n & 0x3FF));
        }
        return (char)n;
    }
    
    public static String valueOf(final int n) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Illegal codepoint");
        }
        return toString(n);
    }
    
    public static StringBuffer append(final StringBuffer sb, final int n) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Illegal codepoint: " + Integer.toHexString(n));
        }
        if (n >= 65536) {
            sb.append(getLeadSurrogate(n));
            sb.append(getTrailSurrogate(n));
        }
        else {
            sb.append((char)n);
        }
        return sb;
    }
    
    public static int moveCodePointOffset(final char[] array, final int n, final int n2, final int n3, final int n4) {
        final int length = array.length;
        int n5 = n3 + n;
        if (n < 0 || n2 < n) {
            throw new StringIndexOutOfBoundsException(n);
        }
        if (n2 > length) {
            throw new StringIndexOutOfBoundsException(n2);
        }
        if (n3 < 0 || n5 > n2) {
            throw new StringIndexOutOfBoundsException(n3);
        }
        int i;
        if (n4 > 0) {
            if (n4 + n5 > length) {
                throw new StringIndexOutOfBoundsException(n5);
            }
            for (i = n4; n5 < n2 && i > 0; --i, ++n5) {
                if (isLeadSurrogate(array[n5]) && n5 + 1 < n2 && isTrailSurrogate(array[n5 + 1])) {
                    ++n5;
                }
            }
        }
        else {
            if (n5 + n4 < n) {
                throw new StringIndexOutOfBoundsException(n5);
            }
            for (i = -n4; i > 0; --i) {
                if (--n5 < n) {
                    break;
                }
                if (isTrailSurrogate(array[n5]) && n5 > n && isLeadSurrogate(array[n5 - 1])) {
                    --n5;
                }
            }
        }
        if (i != 0) {
            throw new StringIndexOutOfBoundsException(n4);
        }
        return n5 - n;
    }
    
    private static String toString(final int n) {
        if (n < 65536) {
            return String.valueOf((char)n);
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(getLeadSurrogate(n));
        sb.append(getTrailSurrogate(n));
        return sb.toString();
    }
}
