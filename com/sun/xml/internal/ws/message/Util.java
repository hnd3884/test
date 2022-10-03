package com.sun.xml.internal.ws.message;

public abstract class Util
{
    public static boolean parseBool(final String value) {
        if (value.length() == 0) {
            return false;
        }
        final char ch = value.charAt(0);
        return ch == 't' || ch == '1';
    }
}
