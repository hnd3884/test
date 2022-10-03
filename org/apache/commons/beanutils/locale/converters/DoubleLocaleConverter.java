package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Locale;

public class DoubleLocaleConverter extends DecimalLocaleConverter
{
    public DoubleLocaleConverter() {
        this(false);
    }
    
    public DoubleLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public DoubleLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public DoubleLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public DoubleLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public DoubleLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public DoubleLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public DoubleLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public DoubleLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public DoubleLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public DoubleLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public DoubleLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Number result = (Number)super.parse(value, pattern);
        if (result instanceof Long) {
            return new Double(result.doubleValue());
        }
        return result;
    }
}
