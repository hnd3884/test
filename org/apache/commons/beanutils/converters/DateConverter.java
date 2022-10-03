package org.apache.commons.beanutils.converters;

import java.util.Date;

public final class DateConverter extends DateTimeConverter
{
    public DateConverter() {
    }
    
    public DateConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return Date.class;
    }
}
