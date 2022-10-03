package com.sun.mail.util;

import java.util.Hashtable;
import javax.mail.Session;
import java.util.Properties;

public class PropUtil
{
    private PropUtil() {
    }
    
    public static int getIntProperty(final Properties props, final String name, final int def) {
        return getInt(getProp(props, name), def);
    }
    
    public static boolean getBooleanProperty(final Properties props, final String name, final boolean def) {
        return getBoolean(getProp(props, name), def);
    }
    
    @Deprecated
    public static int getIntSessionProperty(final Session session, final String name, final int def) {
        return getInt(getProp(session.getProperties(), name), def);
    }
    
    @Deprecated
    public static boolean getBooleanSessionProperty(final Session session, final String name, final boolean def) {
        return getBoolean(getProp(session.getProperties(), name), def);
    }
    
    public static boolean getBooleanSystemProperty(final String name, final boolean def) {
        try {
            return getBoolean(getProp(System.getProperties(), name), def);
        }
        catch (final SecurityException ex) {
            try {
                final String value = System.getProperty(name);
                if (value == null) {
                    return def;
                }
                if (def) {
                    return !value.equalsIgnoreCase("false");
                }
                return value.equalsIgnoreCase("true");
            }
            catch (final SecurityException sex) {
                return def;
            }
        }
    }
    
    private static Object getProp(final Properties props, final String name) {
        final Object val = ((Hashtable<K, Object>)props).get(name);
        if (val != null) {
            return val;
        }
        return props.getProperty(name);
    }
    
    private static int getInt(final Object value, final int def) {
        if (value == null) {
            return def;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        if (value instanceof Integer) {
            return (int)value;
        }
        return def;
    }
    
    private static boolean getBoolean(final Object value, final boolean def) {
        if (value == null) {
            return def;
        }
        if (value instanceof String) {
            if (def) {
                return !((String)value).equalsIgnoreCase("false");
            }
            return ((String)value).equalsIgnoreCase("true");
        }
        else {
            if (value instanceof Boolean) {
                return (boolean)value;
            }
            return def;
        }
    }
}
