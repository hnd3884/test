package org.apache.commons.beanutils.locale.converters;

import java.text.ParseException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

public class SqlTimestampLocaleConverter extends DateLocaleConverter
{
    public SqlTimestampLocaleConverter() {
        this(false);
    }
    
    public SqlTimestampLocaleConverter(final boolean locPattern) {
        this(Locale.getDefault(), locPattern);
    }
    
    public SqlTimestampLocaleConverter(final Locale locale) {
        this(locale, null);
    }
    
    public SqlTimestampLocaleConverter(final Locale locale, final boolean locPattern) {
        this(locale, null);
    }
    
    public SqlTimestampLocaleConverter(final Locale locale, final String pattern) {
        this(locale, pattern, false);
    }
    
    public SqlTimestampLocaleConverter(final Locale locale, final String pattern, final boolean locPattern) {
        super(locale, pattern, locPattern);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue) {
        this(defaultValue, false);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue, final boolean locPattern) {
        this(defaultValue, Locale.getDefault(), locPattern);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue, final Locale locale) {
        this(defaultValue, locale, false);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue, final Locale locale, final boolean locPattern) {
        this(defaultValue, locale, null, locPattern);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue, final Locale locale, final String pattern) {
        this(defaultValue, locale, pattern, false);
    }
    
    public SqlTimestampLocaleConverter(final Object defaultValue, final Locale locale, final String pattern, final boolean locPattern) {
        super(defaultValue, locale, pattern, locPattern);
    }
    
    @Override
    protected Object parse(final Object value, final String pattern) throws ParseException {
        return new Timestamp(((Date)super.parse(value, pattern)).getTime());
    }
}
