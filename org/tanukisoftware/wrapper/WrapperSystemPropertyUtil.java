package org.tanukisoftware.wrapper;

public final class WrapperSystemPropertyUtil
{
    public static String getStringProperty(final String name, final String defaultValue) {
        final String val = System.getProperty(name);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }
    
    public static boolean getBooleanProperty(final String name, final boolean defaultValue) {
        final String val = getStringProperty(name, null);
        if (val == null) {
            return defaultValue;
        }
        return val.equalsIgnoreCase("TRUE");
    }
    
    public static int getIntProperty(final String name, final int defaultValue) {
        final String val = getStringProperty(name, null);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static long getLongProperty(final String name, final long defaultValue) {
        final String val = getStringProperty(name, null);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val);
        }
        catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
    
    private WrapperSystemPropertyUtil() {
    }
}
