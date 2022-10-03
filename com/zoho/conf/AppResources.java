package com.zoho.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class AppResources
{
    private static ConcurrentHashMap<String, String> appProps;
    private static String propFilePath;
    private static final Logger LOGGER;
    
    public static void setProperties(final Properties props) {
        final Enumeration elements = props.propertyNames();
        while (elements.hasMoreElements()) {
            final String key = elements.nextElement();
            final String val = props.getProperty(key);
            AppResources.appProps.put(key, val);
        }
        AppResources.LOGGER.log(Level.INFO, "Properties passed has been loaded.");
    }
    
    public static Object getProperties() {
        return new ConcurrentHashMap(AppResources.appProps);
    }
    
    public static String getProperty(final String key) {
        return trim(AppResources.appProps.get(key));
    }
    
    public static String getProperty(final String key, final String defaultValue) {
        final String value = AppResources.appProps.get(key);
        return (value != null) ? value : defaultValue;
    }
    
    public static String getString(final String key) {
        return getString(key, null);
    }
    
    public static String getString(final String key, final String defaultValue) {
        final String value = AppResources.appProps.get(key);
        return (value != null) ? value.trim() : defaultValue;
    }
    
    public static Integer getInteger(final String key) {
        return getInteger(key, null);
    }
    
    public static Integer getInteger(final String key, final Integer defaultValue) {
        final String value = AppResources.appProps.get(key);
        return (value != null) ? Integer.decode(value.trim()) : defaultValue;
    }
    
    public static Long getLong(final String key) {
        return getLong(key, null);
    }
    
    public static Long getLong(final String key, final Long defaultValue) {
        final String value = AppResources.appProps.get(key);
        return (value != null) ? Long.decode(value.trim()) : defaultValue;
    }
    
    public static Boolean getBoolean(final String key) {
        return getBoolean(key, null);
    }
    
    public static Boolean getBoolean(final String key, final Boolean defaultValue) {
        final String value = AppResources.appProps.get(key);
        return (value != null) ? Boolean.valueOf(value.trim()) : defaultValue;
    }
    
    private static void load(final InputStream is) throws IOException {
        if (is != null) {
            try {
                final Properties props = new Properties();
                props.load(is);
                final Enumeration elements = props.propertyNames();
                while (elements.hasMoreElements()) {
                    final String key = elements.nextElement();
                    final String val = props.getProperty(key);
                    AppResources.appProps.put(key, val);
                }
                AppResources.LOGGER.log(Level.INFO, "Properties file has been loaded.");
            }
            finally {
                is.close();
            }
        }
    }
    
    public static void load(final String propFileName) throws IOException {
        AppResources.LOGGER.log(Level.INFO, "Going to load : " + propFileName);
        final InputStream is = new FileInputStream(AppResources.propFilePath + propFileName);
        load(is);
    }
    
    private static String trim(final String value) {
        return (value != null) ? value.trim() : value;
    }
    
    public static Object setProperty(final String key, final String value) {
        return AppResources.appProps.put(key, value);
    }
    
    public static Object setString(final String key, final String value) {
        return AppResources.appProps.put(key, value);
    }
    
    static {
        AppResources.appProps = new ConcurrentHashMap<String, String>();
        AppResources.propFilePath = Configuration.getString("server.home") + "/conf/";
        LOGGER = Logger.getLogger(AppResources.class.getName());
    }
}
