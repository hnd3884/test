package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.Hashtable;
import java.util.Properties;

final class PropUtil
{
    private PropUtil() {
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
