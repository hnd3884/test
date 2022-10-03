package org.owasp.esapi;

import java.util.Arrays;
import java.util.regex.Pattern;

public class StringUtilities
{
    private static final Pattern p;
    
    public static String replaceLinearWhiteSpace(final String input) {
        return StringUtilities.p.matcher(input).replaceAll(" ");
    }
    
    public static String stripControls(final String input) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            final char c = input.charAt(i);
            if (c > ' ' && c < '\u007f') {
                sb.append(c);
            }
            else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
    
    public static char[] union(final char[]... list) {
        final StringBuilder sb = new StringBuilder();
        for (final char[] arr$2 : list) {
            final char[] characters = arr$2;
            for (final char c : arr$2) {
                if (!contains(sb, c)) {
                    sb.append(c);
                }
            }
        }
        final char[] toReturn = new char[sb.length()];
        sb.getChars(0, sb.length(), toReturn, 0);
        Arrays.sort(toReturn);
        return toReturn;
    }
    
    public static boolean contains(final StringBuilder input, final char c) {
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == c) {
                return true;
            }
        }
        return false;
    }
    
    public static String replaceNull(final String test, final String replace) {
        return (test == null || "null".equalsIgnoreCase(test.trim()) || "".equals(test.trim())) ? replace : test;
    }
    
    public static int getLevenshteinDistance(final String s, final String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        final int n = s.length();
        final int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        for (int i = 0; i <= n; ++i) {
            p[i] = i;
        }
        for (int j = 1; j <= m; ++j) {
            final char t_j = t.charAt(j - 1);
            d[0] = j;
            for (int i = 1; i <= n; ++i) {
                final int cost = (s.charAt(i - 1) != t_j) ? 1 : 0;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }
            final int[] _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }
    
    public static boolean notNullOrEmpty(final String str, final boolean trim) {
        if (trim) {
            return str != null && !str.trim().equals("");
        }
        return str != null && !str.equals("");
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }
    
    static {
        p = Pattern.compile("\\s");
    }
}
