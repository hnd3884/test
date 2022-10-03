package org.apache.taglibs.standard.tag.common.fmt;

import org.apache.taglibs.standard.resources.Resources;
import java.util.Locale;

public class LocaleUtil
{
    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';
    
    public static Locale parseLocaleAttributeValue(final Object stringOrLocale) {
        if (stringOrLocale instanceof Locale) {
            return (Locale)stringOrLocale;
        }
        if (!(stringOrLocale instanceof String)) {
            return null;
        }
        final String string = (String)stringOrLocale;
        if (string.length() == 0) {
            return null;
        }
        return parseLocale(string.trim());
    }
    
    public static Locale parseLocale(final String locale) {
        return parseLocale(locale, null);
    }
    
    public static Locale parseLocale(final String locale, final String variant) {
        int index;
        String language;
        String country;
        if ((index = locale.indexOf(45)) > -1 || (index = locale.indexOf(95)) > -1) {
            if (index == 0) {
                throw new IllegalArgumentException(Resources.getMessage("LOCALE_NO_LANGUAGE"));
            }
            if (index == locale.length() - 1) {
                throw new IllegalArgumentException(Resources.getMessage("LOCALE_EMPTY_COUNTRY"));
            }
            language = locale.substring(0, index);
            country = locale.substring(index + 1);
        }
        else {
            language = locale;
            country = "";
        }
        if (variant != null) {
            return new Locale(language, country, variant);
        }
        return new Locale(language, country);
    }
}
