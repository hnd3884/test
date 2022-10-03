package sun.misc;

import java.util.Comparator;

public class ASCIICaseInsensitiveComparator implements Comparator<String>
{
    public static final Comparator<String> CASE_INSENSITIVE_ORDER;
    
    @Override
    public int compare(final String s, final String s2) {
        final int length = s.length();
        final int length2 = s2.length();
        for (int n = (length < length2) ? length : length2, i = 0; i < n; ++i) {
            final char char1 = s.charAt(i);
            final char char2 = s2.charAt(i);
            assert char1 <= '\u007f' && char2 <= '\u007f';
            if (char1 != char2) {
                final char c = (char)toLower(char1);
                final char c2 = (char)toLower(char2);
                if (c != c2) {
                    return c - c2;
                }
            }
        }
        return length - length2;
    }
    
    public static int lowerCaseHashCode(final String s) {
        int n = 0;
        for (int length = s.length(), i = 0; i < length; ++i) {
            n = 31 * n + toLower(s.charAt(i));
        }
        return n;
    }
    
    static boolean isLower(final int n) {
        return (n - 97 | 122 - n) >= 0;
    }
    
    static boolean isUpper(final int n) {
        return (n - 65 | 90 - n) >= 0;
    }
    
    static int toLower(final int n) {
        return isUpper(n) ? (n + 32) : n;
    }
    
    static int toUpper(final int n) {
        return isLower(n) ? (n - 32) : n;
    }
    
    static {
        CASE_INSENSITIVE_ORDER = new ASCIICaseInsensitiveComparator();
    }
}
