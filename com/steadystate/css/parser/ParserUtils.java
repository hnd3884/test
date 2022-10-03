package com.steadystate.css.parser;

public final class ParserUtils
{
    private ParserUtils() {
    }
    
    public static String trimBy(final StringBuilder s, final int left, final int right) {
        return s.substring(left, s.length() - right);
    }
    
    public static String trimUrl(final StringBuilder s) {
        final String s2 = trimBy(s, 4, 1).trim();
        if (s2.length() == 0) {
            return s2;
        }
        final int end = s2.length() - 1;
        final char c0 = s2.charAt(0);
        if ((c0 == '\"' && s2.charAt(end) == '\"') || (c0 == '\'' && s2.charAt(end) == '\'')) {
            return s2.substring(1, end);
        }
        return s2;
    }
}
