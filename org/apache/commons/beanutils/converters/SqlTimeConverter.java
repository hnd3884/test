package org.apache.commons.beanutils.converters;

import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Locale;
import java.sql.Time;

public final class SqlTimeConverter extends DateTimeConverter
{
    public SqlTimeConverter() {
    }
    
    public SqlTimeConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Time.class;
    }
    
    @Override
    protected DateFormat getFormat(final Locale locale, final TimeZone timeZone) {
        DateFormat format = null;
        if (locale == null) {
            format = DateFormat.getTimeInstance(3);
        }
        else {
            format = DateFormat.getTimeInstance(3, locale);
        }
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }
}
