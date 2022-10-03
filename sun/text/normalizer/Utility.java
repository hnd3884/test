package sun.text.normalizer;

public final class Utility
{
    private static final char[] UNESCAPE_MAP;
    static final char[] DIGITS;
    
    public static final boolean arrayRegionMatches(final char[] array, final int n, final char[] array2, final int n2, final int n3) {
        final int n4 = n + n3;
        final int n5 = n2 - n;
        for (int i = n; i < n4; ++i) {
            if (array[i] != array2[i + n5]) {
                return false;
            }
        }
        return true;
    }
    
    public static final String escape(final String s) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            final int char1 = UTF16.charAt(s, i);
            i += UTF16.getCharCount(char1);
            if (char1 >= 32 && char1 <= 127) {
                if (char1 == 92) {
                    sb.append("\\\\");
                }
                else {
                    sb.append((char)char1);
                }
            }
            else {
                final boolean b = char1 <= 65535;
                sb.append(b ? "\\u" : "\\U");
                hex(char1, b ? 4 : 8, sb);
            }
        }
        return sb.toString();
    }
    
    public static int unescapeAt(final String s, final int[] array) {
        int rawSupplementary = 0;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 4;
        boolean b = false;
        final int n5 = array[0];
        final int length = s.length();
        if (n5 < 0 || n5 >= length) {
            return -1;
        }
        int n6 = UTF16.charAt(s, n5);
        int n7 = n5 + UTF16.getCharCount(n6);
        switch (n6) {
            case 117: {
                n3 = (n2 = 4);
                break;
            }
            case 85: {
                n3 = (n2 = 8);
                break;
            }
            case 120: {
                n2 = 1;
                if (n7 < length && UTF16.charAt(s, n7) == 123) {
                    ++n7;
                    b = true;
                    n3 = 8;
                    break;
                }
                n3 = 2;
                break;
            }
            default: {
                final int digit = UCharacter.digit(n6, 8);
                if (digit >= 0) {
                    n2 = 1;
                    n3 = 3;
                    n = 1;
                    n4 = 3;
                    rawSupplementary = digit;
                    break;
                }
                break;
            }
        }
        if (n2 != 0) {
            while (n7 < length && n < n3) {
                n6 = UTF16.charAt(s, n7);
                final int digit2 = UCharacter.digit(n6, (n4 == 3) ? 8 : 16);
                if (digit2 < 0) {
                    break;
                }
                rawSupplementary = (rawSupplementary << n4 | digit2);
                n7 += UTF16.getCharCount(n6);
                ++n;
            }
            if (n < n2) {
                return -1;
            }
            if (b) {
                if (n6 != 125) {
                    return -1;
                }
                ++n7;
            }
            if (rawSupplementary < 0 || rawSupplementary >= 1114112) {
                return -1;
            }
            if (n7 < length && UTF16.isLeadSurrogate((char)rawSupplementary)) {
                int n8 = n7 + 1;
                int n9 = s.charAt(n7);
                if (n9 == 92 && n8 < length) {
                    final int[] array2 = { n8 };
                    n9 = unescapeAt(s, array2);
                    n8 = array2[0];
                }
                if (UTF16.isTrailSurrogate((char)n9)) {
                    n7 = n8;
                    rawSupplementary = UCharacterProperty.getRawSupplementary((char)rawSupplementary, (char)n9);
                }
            }
            array[0] = n7;
            return rawSupplementary;
        }
        else {
            for (int i = 0; i < Utility.UNESCAPE_MAP.length; i += 2) {
                if (n6 == Utility.UNESCAPE_MAP[i]) {
                    array[0] = n7;
                    return Utility.UNESCAPE_MAP[i + 1];
                }
                if (n6 < Utility.UNESCAPE_MAP[i]) {
                    break;
                }
            }
            if (n6 == 'c' && n7 < length) {
                final int char1 = UTF16.charAt(s, n7);
                array[0] = n7 + UTF16.getCharCount(char1);
                return 0x1F & char1;
            }
            array[0] = n7;
            return n6;
        }
    }
    
    public static StringBuffer hex(final int n, final int n2, final StringBuffer sb) {
        return appendNumber(sb, n, 16, n2);
    }
    
    public static String hex(final int n, final int n2) {
        return appendNumber(new StringBuffer(), n, 16, n2).toString();
    }
    
    public static int skipWhitespace(final String s, int i) {
        while (i < s.length()) {
            final int char1 = UTF16.charAt(s, i);
            if (!UCharacterProperty.isRuleWhiteSpace(char1)) {
                break;
            }
            i += UTF16.getCharCount(char1);
        }
        return i;
    }
    
    private static void recursiveAppendNumber(final StringBuffer sb, final int n, final int n2, final int n3) {
        final int n4 = n % n2;
        if (n >= n2 || n3 > 1) {
            recursiveAppendNumber(sb, n / n2, n2, n3 - 1);
        }
        sb.append(Utility.DIGITS[n4]);
    }
    
    public static StringBuffer appendNumber(final StringBuffer sb, final int n, final int n2, final int n3) throws IllegalArgumentException {
        if (n2 < 2 || n2 > 36) {
            throw new IllegalArgumentException("Illegal radix " + n2);
        }
        int n4;
        if ((n4 = n) < 0) {
            n4 = -n;
            sb.append("-");
        }
        recursiveAppendNumber(sb, n4, n2, n3);
        return sb;
    }
    
    public static boolean isUnprintable(final int n) {
        return n < 32 || n > 126;
    }
    
    public static boolean escapeUnprintable(final StringBuffer sb, final int n) {
        if (isUnprintable(n)) {
            sb.append('\\');
            if ((n & 0xFFFF0000) != 0x0) {
                sb.append('U');
                sb.append(Utility.DIGITS[0xF & n >> 28]);
                sb.append(Utility.DIGITS[0xF & n >> 24]);
                sb.append(Utility.DIGITS[0xF & n >> 20]);
                sb.append(Utility.DIGITS[0xF & n >> 16]);
            }
            else {
                sb.append('u');
            }
            sb.append(Utility.DIGITS[0xF & n >> 12]);
            sb.append(Utility.DIGITS[0xF & n >> 8]);
            sb.append(Utility.DIGITS[0xF & n >> 4]);
            sb.append(Utility.DIGITS[0xF & n]);
            return true;
        }
        return false;
    }
    
    public static void getChars(final StringBuffer sb, final int n, final int n2, final char[] array, final int n3) {
        if (n == n2) {
            return;
        }
        sb.getChars(n, n2, array, n3);
    }
    
    static {
        UNESCAPE_MAP = new char[] { 'a', '\u0007', 'b', '\b', 'e', '\u001b', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\u000b' };
        DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    }
}
