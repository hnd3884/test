package org.glassfish.jersey.internal.util;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.lang.reflect.Constructor;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Level;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import java.util.Properties;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public final class PropertiesHelper
{
    private static final Logger LOGGER;
    
    public static PrivilegedAction<Properties> getSystemProperties() {
        return new PrivilegedAction<Properties>() {
            @Override
            public Properties run() {
                return System.getProperties();
            }
        };
    }
    
    public static PrivilegedAction<String> getSystemProperty(final String name) {
        return new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(name);
            }
        };
    }
    
    public static PrivilegedAction<String> getSystemProperty(final String name, final String def) {
        return new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(name, def);
            }
        };
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final T defaultValue, final Map<String, String> legacyMap) {
        return getValue(properties, null, key, defaultValue, legacyMap);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtimeType, final String key, final T defaultValue, final Map<String, String> legacyMap) {
        return getValue(properties, runtimeType, key, defaultValue, defaultValue.getClass(), legacyMap);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final T defaultValue, final Class<T> type, final Map<String, String> legacyMap) {
        return getValue(properties, null, key, defaultValue, type, legacyMap);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtimeType, final String key, final T defaultValue, final Class<T> type, final Map<String, String> legacyMap) {
        T value = getValue(properties, runtimeType, key, type, legacyMap);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final Class<T> type, final Map<String, String> legacyMap) {
        return getValue(properties, (RuntimeType)null, key, type, legacyMap);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtimeType, final String key, final Class<T> type, final Map<String, String> legacyMap) {
        Object value = null;
        if (runtimeType != null) {
            String runtimeAwareKey = getPropertyNameForRuntime(key, runtimeType);
            if (key.equals(runtimeAwareKey)) {
                runtimeAwareKey = key + "." + runtimeType.name().toLowerCase();
            }
            value = properties.get(runtimeAwareKey);
        }
        if (value == null) {
            value = properties.get(key);
        }
        if (value == null) {
            value = getLegacyFallbackValue(properties, legacyMap, key);
        }
        if (value == null) {
            return null;
        }
        return convertValue(value, type);
    }
    
    public static String getPropertyNameForRuntime(final String key, final RuntimeType runtimeType) {
        if (runtimeType != null && key.startsWith("jersey.config")) {
            final RuntimeType[] values;
            final RuntimeType[] types = values = RuntimeType.values();
            for (final RuntimeType type : values) {
                if (key.startsWith("jersey.config." + type.name().toLowerCase())) {
                    return key;
                }
            }
            return key.replace("jersey.config", "jersey.config." + runtimeType.name().toLowerCase());
        }
        return key;
    }
    
    private static Object getLegacyFallbackValue(final Map<String, ?> properties, final Map<String, String> legacyFallbackMap, final String key) {
        if (legacyFallbackMap == null || !legacyFallbackMap.containsKey(key)) {
            return null;
        }
        final String fallbackKey = legacyFallbackMap.get(key);
        final Object value = properties.get(fallbackKey);
        if (value != null && PropertiesHelper.LOGGER.isLoggable(Level.CONFIG)) {
            PropertiesHelper.LOGGER.config(LocalizationMessages.PROPERTIES_HELPER_DEPRECATED_PROPERTY_NAME(fallbackKey, key));
        }
        return value;
    }
    
    public static <T> T convertValue(final Object value, final Class<T> type) {
        if (!type.isInstance(value)) {
            final Constructor constructor = AccessController.doPrivileged(ReflectionHelper.getStringConstructorPA(type));
            if (constructor != null) {
                try {
                    return type.cast(constructor.newInstance(value));
                }
                catch (final Exception ex) {}
            }
            final Method valueOf = AccessController.doPrivileged(ReflectionHelper.getValueOfStringMethodPA(type));
            if (valueOf != null) {
                try {
                    return type.cast(valueOf.invoke(null, value));
                }
                catch (final Exception ex2) {}
            }
            if (PropertiesHelper.LOGGER.isLoggable(Level.WARNING)) {
                PropertiesHelper.LOGGER.warning(LocalizationMessages.PROPERTIES_HELPER_GET_VALUE_NO_TRANSFORM(String.valueOf(value), value.getClass().getName(), type.getName()));
            }
            return null;
        }
        return type.cast(value);
    }
    
    public static boolean isProperty(final Map<String, Object> properties, final String name) {
        return properties.containsKey(name) && isProperty(properties.get(name));
    }
    
    public static boolean isProperty(final Object value) {
        if (value instanceof Boolean) {
            return Boolean.class.cast(value);
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }
    
    private PropertiesHelper() {
    }
    
    static {
        LOGGER = Logger.getLogger(PropertiesHelper.class.getName());
    }
}
