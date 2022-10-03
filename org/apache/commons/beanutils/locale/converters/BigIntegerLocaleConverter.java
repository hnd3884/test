package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import org.apache.commons.beanutils.ConversionException;
import java.math.BigInteger;
import java.util.Locale;

public class BigIntegerLocaleConverter extends DecimalLocaleConverter
{
    public BigIntegerLocaleConverter() {
        this(false);
    }
    
    public BigIntegerLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public BigIntegerLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public BigIntegerLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public BigIntegerLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public BigIntegerLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public BigIntegerLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        final Object result = super.parse(value, pattern);
        if (result == null || result instanceof BigInteger) {
            return result;
        }
        if (result instanceof Number) {
            return BigInteger.valueOf(((Number)result).longValue());
        }
        try {
            return new BigInteger(result.toString());
        }
        catch (final NumberFormatException ex) {
            throw new ConversionException("Suplied number is not of type BigInteger: " + result);
        }
    }
}
