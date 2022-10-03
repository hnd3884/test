package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Locale;

public class LongLocaleConverter extends DecimalLocaleConverter
{
    public LongLocaleConverter() {
        this(false);
    }
    
    public LongLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public LongLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public LongLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public LongLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public LongLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public LongLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public LongLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public LongLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public LongLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public LongLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public LongLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Object result = super.parse(value, pattern);
        if (result == null || result instanceof Long) {
            return result;
        }
        return new Long(((Number)result).longValue());
    }
}
