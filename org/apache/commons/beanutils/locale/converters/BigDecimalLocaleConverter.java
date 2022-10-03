package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.math.BigDecimal;
import java.util.Locale;

public class BigDecimalLocaleConverter extends DecimalLocaleConverter
{
    public BigDecimalLocaleConverter() {
        this(false);
    }
    
    public BigDecimalLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public BigDecimalLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public BigDecimalLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public BigDecimalLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public BigDecimalLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public BigDecimalLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Object result = super.parse(value, pattern);
        if (result == null || result instanceof BigDecimal) {
            return result;
        }
        try {
            return new BigDecimal(result.toString());
        }
        catch (final NumberFormatException ex) {
            throw new ConversionException("Suplied number is not of type BigDecimal: " + result);
        }
    }
}
