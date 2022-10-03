package org.apache.commons.beanutils.converters;

public final class StringConverter extends AbstractConverter
{
    public StringConverter() {
    }
    
    public StringConverter(final Object defaultValue) {
        super(defaultValue);
    }
    
    @Override
    protected Class<?> getDefaultType() {
        return String.class;
    }
    
    @Override
    protected <T> T convertToType(final Class<T> type, final Object value) throws Throwable {
        if (String.class.equals(type) || Object.class.equals(type)) {
            return type.cast(value.toString());
        }
        throw this.conversionException(type, value);
    }
}
