package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.util.Locale;

public class ShortLocaleConverter extends DecimalLocaleConverter
{
    public ShortLocaleConverter() {
        this(false);
    }
    
    public ShortLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public ShortLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public ShortLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public ShortLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public ShortLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public ShortLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public ShortLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public ShortLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public ShortLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public ShortLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public ShortLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Object result = super.parse(value, pattern);
        if (result == null || result instanceof Short) {
            return result;
        }
        final Number parsed = (Number)result;
        if (parsed.longValue() != parsed.shortValue()) {
            throw new ConversionException("Supplied number is not of type Short: " + parsed.longValue());
        }
        return new Short(parsed.shortValue());
    }
}
