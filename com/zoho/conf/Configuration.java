package com.zoho.conf;

import java.util.Properties;

public class Configuration
{
    public static void reloadProperties(final String path) {
    }
    
    public static String getString(final String key) {
        return System.getProperty(key);
    }
    
    public static String getString(final String key, final String defaultvalue) {
        return System.getProperty(key, defaultvalue);
    }
    
    public static boolean getBoolean(final String key) {
        return Boolean.parseBoolean(System.getProperty(key));
    }
    
    public static boolean getBoolean(final String key, final String defaultValue) {
        return Boolean.parseBoolean(System.getProperty(key, defaultValue));
    }
    
    public static int getInteger(final String key) {
        return getInteger(key, 0);
    }
    
    public static int getInteger(final String key, final int defaultValue) {
        return Integer.parseInt(System.getProperty(key, String.valueOf(defaultValue)));
    }
    
    public static long getLong(final String key) {
        return getLong(key, 0L);
    }
    
    public static long getLong(final String key, final long defaultValue) {
        return Long.parseLong(System.getProperty(key, String.valueOf(defaultValue)));
    }
    
    public static void setString(final String key, final String value) {
        System.setProperty(key, value);
    }
    
    public static Properties getProperties() {
        return System.getProperties();
    }
    
    public static void setProperties(final Properties props) {
        System.setProperties(props);
    }
}
