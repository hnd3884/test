package org.jfree.util;

public class StringUtils
{
    private StringUtils() {
    }
    
    public static boolean endsWithIgnoreCase(final String base, final String end) {
        return base.length() >= end.length() && base.regionMatches(true, base.length() - end.length(), end, 0, end.length());
    }
    
    public static String getLineSeparator() {
        try {
            return System.getProperty("line.separator", "\n");
        }
        catch (final Exception ex) {
            return "\n";
        }
    }
    
    public static boolean startsWithIgnoreCase(final String base, final String start) {
        return base.length() >= start.length() && base.regionMatches(true, 0, start, 0, start.length());
    }
}
