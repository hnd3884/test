package sun.util.locale;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LocaleUtils
{
    private LocaleUtils() {
    }
    
    public static boolean caseIgnoreMatch(final String s, final String s2) {
        if (s == s2) {
            return true;
        }
        final int length = s.length();
        if (length != s2.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            final char char2 = s2.charAt(i);
            if (char1 != char2 && toLower(char1) != toLower(char2)) {
                return false;
            }
        }
        return true;
    }
    
    static int caseIgnoreCompare(final String s, final String s2) {
        if (s == s2) {
            return 0;
        }
        return toLowerString(s).compareTo(toLowerString(s2));
    }
    
    static char toUpper(final char c) {
        return isLower(c) ? ((char)(c - ' ')) : c;
    }
    
    static char toLower(final char c) {
        return isUpper(c) ? ((char)(c + ' ')) : c;
    }
    
    public static String toLowerString(final String s) {
        int length;
        int n;
        for (length = s.length(), n = 0; n < length && !isUpper(s.charAt(n)); ++n) {}
        if (n == length) {
            return s;
        }
        final char[] array = new char[length];
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            array[i] = ((i < n) ? char1 : toLower(char1));
        }
        return new String(array);
    }
    
    static String toUpperString(final String s) {
        int length;
        int n;
        for (length = s.length(), n = 0; n < length && !isLower(s.charAt(n)); ++n) {}
        if (n == length) {
            return s;
        }
        final char[] array = new char[length];
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            array[i] = ((i < n) ? char1 : toUpper(char1));
        }
        return new String(array);
    }
    
    static String toTitleString(final String s) {
        final int length;
        if ((length = s.length()) == 0) {
            return s;
        }
        int i = 0;
        if (!isLower(s.charAt(i))) {
            for (i = 1; i < length; ++i) {
                if (isUpper(s.charAt(i))) {
                    break;
                }
            }
        }
        if (i == length) {
            return s;
        }
        final char[] array = new char[length];
        for (int j = 0; j < length; ++j) {
            final char char1 = s.charAt(j);
            if (j == 0 && i == 0) {
                array[j] = toUpper(char1);
            }
            else if (j < i) {
                array[j] = char1;
            }
            else {
                array[j] = toLower(char1);
            }
        }
        return new String(array);
    }
    
    private static boolean isUpper(final char c) {
        return c >= 'A' && c <= 'Z';
    }
    
    private static boolean isLower(final char c) {
        return c >= 'a' && c <= 'z';
    }
    
    static boolean isAlpha(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
    
    static boolean isAlphaString(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (!isAlpha(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isNumeric(final char c) {
        return c >= '0' && c <= '9';
    }
    
    static boolean isNumericString(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (!isNumeric(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isAlphaNumeric(final char c) {
        return isAlpha(c) || isNumeric(c);
    }
    
    public static boolean isAlphaNumericString(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (!isAlphaNumeric(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
    }
    
    static boolean isEmpty(final Set<?> set) {
        return set == null || set.isEmpty();
    }
    
    static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    static boolean isEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }
}
