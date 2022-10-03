package com.sun.xml.internal.bind;

import java.util.logging.Logger;

public final class Util
{
    private Util() {
    }
    
    public static Logger getClassLogger() {
        try {
            final StackTraceElement[] trace = new Exception().getStackTrace();
            return Logger.getLogger(trace[1].getClassName());
        }
        catch (final SecurityException e) {
            return Logger.getLogger("com.sun.xml.internal.bind");
        }
    }
    
    public static String getSystemProperty(final String name) {
        try {
            return System.getProperty(name);
        }
        catch (final SecurityException e) {
            return null;
        }
    }
}
