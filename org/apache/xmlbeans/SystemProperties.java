package org.apache.xmlbeans;

import java.util.Hashtable;

public class SystemProperties
{
    protected static Hashtable propertyH;
    
    public static String getProperty(final String key) {
        if (SystemProperties.propertyH == null) {
            try {
                SystemProperties.propertyH = System.getProperties();
            }
            catch (final SecurityException ex) {
                SystemProperties.propertyH = new Hashtable();
                return null;
            }
        }
        return SystemProperties.propertyH.get(key);
    }
    
    public static String getProperty(final String key, final String defaultValue) {
        final String result = getProperty(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }
    
    public static void setPropertyH(final Hashtable aPropertyH) {
        SystemProperties.propertyH = aPropertyH;
    }
}
