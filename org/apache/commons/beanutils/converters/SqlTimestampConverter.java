package org.apache.commons.beanutils.converters;

import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Locale;
import java.sql.Timestamp;

public final class SqlTimestampConverter extends DateTimeConverter
{
    public SqlTimestampConverter() {
    }
    
    public SqlTimestampConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Timestamp.class;
    }
    
    @Override
    protected DateFormat getFormat(final Locale locale, final TimeZone timeZone) {
        DateFormat format = null;
        if (locale == null) {
            format = DateFormat.getDateTimeInstance(3, 3);
        }
        else {
            format = DateFormat.getDateTimeInstance(3, 3, locale);
        }
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        return format;
    }
}
