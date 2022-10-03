package com.sun.xml.internal.messaging.saaj.util;

import java.security.AccessControlException;

public final class SAAJUtil
{
    public static boolean getSystemBoolean(final String arg) {
        try {
            return Boolean.getBoolean(arg);
        }
        catch (final AccessControlException ex) {
            return false;
        }
    }
    
    public static String getSystemProperty(final String arg) {
        try {
            return System.getProperty(arg);
        }
        catch (final SecurityException ex) {
            return null;
        }
    }
}
