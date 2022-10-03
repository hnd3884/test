package org.apache.commons.beanutils.converters;

import java.net.URL;

public final class URLConverter extends AbstractConverter
{
    public URLConverter() {
    }
    
    public URLConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return URL.class;
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (URL.class.equals(type)) {
            return type.cast(new URL(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}
