package org.apache.lucene.queryparser.flexible.messages;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public class NLS
{
    private static Map<String, Class<? extends NLS>> bundles;
    
    protected NLS() {
    }
    
    public static String getLocalizedMessage(final String key) {
        return getLocalizedMessage(key, Locale.getDefault());
    }
    
    public static String getLocalizedMessage(final String key, final Locale locale) {
        final Object message = getResourceBundleObject(key, locale);
        if (message == null) {
            return "Message with key:" + key + " and locale: " + locale + " not found.";
        }
        return message.toString();
    }
    
    public static String getLocalizedMessage(final String key, final Locale locale, final Object... args) {
        String str = getLocalizedMessage(key, locale);
        if (args.length > 0) {
            str = new MessageFormat(str, Locale.ROOT).format(args);
        }
        return str;
    }
    
    public static String getLocalizedMessage(final String key, final Object... args) {
        return getLocalizedMessage(key, Locale.getDefault(), args);
    }
    
    protected static void initializeMessages(final String bundleName, final Class<? extends NLS> clazz) {
        try {
            load(clazz);
            if (!NLS.bundles.containsKey(bundleName)) {
                NLS.bundles.put(bundleName, clazz);
            }
        }
        catch (final Throwable t) {}
    }
    
    private static Object getResourceBundleObject(final String messageKey, final Locale locale) {
        final Iterator<String> it = NLS.bundles.keySet().iterator();
        while (it.hasNext()) {
            final Class<? extends NLS> clazz = NLS.bundles.get(it.next());
            final ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), locale);
            if (resourceBundle != null) {
                try {
                    final Object obj = resourceBundle.getObject(messageKey);
                    if (obj != null) {
                        return obj;
                    }
                    continue;
                }
                catch (final MissingResourceException ex) {}
            }
        }
        return null;
    }
    
    private static void load(final Class<? extends NLS> clazz) {
        final Field[] fieldArray = clazz.getDeclaredFields();
        final int len = fieldArray.length;
        final Map<String, Field> fields = new HashMap<String, Field>(len * 2);
        for (int i = 0; i < len; ++i) {
            fields.put(fieldArray[i].getName(), fieldArray[i]);
            loadfieldValue(fieldArray[i], clazz);
        }
    }
    
    private static void loadfieldValue(final Field field, final Class<? extends NLS> clazz) {
        final int MOD_EXPECTED = 9;
        final int MOD_MASK = MOD_EXPECTED | 0x10;
        if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
            return;
        }
        try {
            field.set(null, field.getName());
            validateMessage(field.getName(), clazz);
        }
        catch (final IllegalArgumentException | IllegalAccessException ex) {}
    }
    
    private static void validateMessage(final String key, final Class<? extends NLS> clazz) {
        try {
            final ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), Locale.getDefault());
            if (resourceBundle != null) {
                final Object obj = resourceBundle.getObject(key);
            }
        }
        catch (final MissingResourceException e) {}
        catch (final Throwable t) {}
    }
    
    static {
        NLS.bundles = new HashMap<String, Class<? extends NLS>>(0);
    }
}
