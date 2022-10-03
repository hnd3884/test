package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.sql.Time;
import java.util.Date;
import java.util.Locale;

public class SqlTimeLocaleConverter extends DateLocaleConverter
{
    public SqlTimeLocaleConverter() {
        this(false);
    }
    
    public SqlTimeLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public SqlTimeLocaleConverter(final Locale locale) {
        this(locale, false);
    }
    
    public SqlTimeLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null, locPattern);
    }
    
    public SqlTimeLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public SqlTimeLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), false);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public SqlTimeLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        return new Time(((Date)super.parse(value, pattern)).getTime());
    }
}
