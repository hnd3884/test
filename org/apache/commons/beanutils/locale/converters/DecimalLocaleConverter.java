package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.beanutils.locale.BaseLocaleConverter;

public class DecimalLocaleConverter extends BaseLocaleConverter
{
    private final Log log;
    
    public DecimalLocaleConverter() {
        this(false);
    }
    
    public DecimalLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public DecimalLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public DecimalLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public DecimalLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public DecimalLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)DecimalLocaleConverter.class);
    }
    
    public DecimalLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public DecimalLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public DecimalLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public DecimalLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public DecimalLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public DecimalLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
        this.log = LogFactory.getLog((Class)DecimalLocaleConverter.class);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        if (value instanceof Number) {
            return value;
        }
        final DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance(this.locale);
        if (pattern != null) {
            if (this.locPattern) {
                formatter.applyLocalizedPattern(pattern);
            }
            else {
                formatter.applyPattern(pattern);
            }
        }
        else {
            this.log.debug((Object)"No pattern provided, using default.");
        }
        return formatter.parse((String)value);
    }
}
