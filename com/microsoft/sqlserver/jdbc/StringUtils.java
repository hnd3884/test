package com.microsoft.sqlserver.jdbc;

public class StringUtils
{
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    
    private StringUtils() {
    }
    
    public static boolean isEmpty(final CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }
    
    public static boolean isNumeric(final String str) {
        return !isEmpty(str) && str.matches("\\d+(\\.\\d+)?");
    }
    
    public static boolean isInteger(final String str) {
        try {
            Integer.parseInt(str);
            return true;
        }
        catch (final NumberFormatException ex) {
            return false;
        }
    }
}
