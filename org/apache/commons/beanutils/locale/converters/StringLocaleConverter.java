package org.apache.commons.beanutils.locale.converters;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;

public class StringLocaleConverter extends BaseLocaleConverter
{
    private final Log log;
    
    public StringLocaleConverter() {
        this(false);
    }
    
    public StringLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public StringLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public StringLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public StringLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public StringLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)StringLocaleConverter.class);
    }
    
    public StringLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public StringLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public StringLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public StringLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)StringLocaleConverter.class);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        String result = null;
        if (value instanceof Integer || value instanceof Long || value instanceof BigInteger || value instanceof Byte || value instanceof Short) {
            result = this.getDecimalFormat(this.locale, pattern).format(((Number)value).longValue());
        }
        else if (value instanceof Double || value instanceof BigDecimal || value instanceof Float) {
            result = this.getDecimalFormat(this.locale, pattern).format(((Number)value).doubleValue());
        }
        else if (value instanceof Date) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, this.locale);
            result = dateFormat.format(value);
        }
        else {
            result = value.toString();
        }
        return result;
    }
    
    private DecimalFormat getDecimalFormat(final Locale locale, final String pattern) {
        final DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getInstance(locale);
        if (pattern != null) {
            if (this.locPattern) {
                numberFormat.applyLocalizedPattern(pattern);
            }
            else {
                numberFormat.applyPattern(pattern);
            }
        }
        else {
            this.log.debug((Object)"No pattern provided, using default.");
        }
        return numberFormat;
    }
}
