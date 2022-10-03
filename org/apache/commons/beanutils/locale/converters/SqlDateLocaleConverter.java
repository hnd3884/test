package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class SqlDateLocaleConverter extends DateLocaleConverter
{
    public SqlDateLocaleConverter() {
        this(false);
    }
    
    public SqlDateLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public SqlDateLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public SqlDateLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public SqlDateLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public SqlDateLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public SqlDateLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        return new java.sql.Date(((Date)super.parse(value, pattern)).getTime());
    }
}
