package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.util.Locale;

public class IntegerLocaleConverter extends DecimalLocaleConverter
{
    public IntegerLocaleConverter() {
        this(false);
    }
    
    public IntegerLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public IntegerLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public IntegerLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public IntegerLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public IntegerLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public IntegerLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public IntegerLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public IntegerLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public IntegerLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public IntegerLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public IntegerLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Number parsed = (Number)super.parse(value, pattern);
        if (parsed.longValue() != parsed.intValue()) {
            throw new ConversionException("Suplied number is not of type Integer: " + parsed.longValue());
        }
        return new Integer(parsed.intValue());
    }
}
