package org.apache.commons.beanutils.converters;

import java.util.Calendar;

public final class CalendarConverter extends DateTimeConverter
{
    public CalendarConverter() {
    }
    
    public CalendarConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Calendar.class;
    }
}
