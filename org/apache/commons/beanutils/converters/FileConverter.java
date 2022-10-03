package org.apache.commons.beanutils.converters;

import java.io.File;

public final class FileConverter extends AbstractConverter
{
    public FileConverter() {
    }
    
    public FileConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return File.class;
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (File.class.equals(type)) {
            return type.cast(new File(value.toString()));
        }
        throw this.conversionException(type, value);
    }
}
