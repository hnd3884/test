package org.apache.commons.beanutils.converters;

public final class ShortConverter extends NumberConverter
{
    public ShortConverter() {
        super(false);
    }
    
    public ShortConverter(final Object defaultValue) {
        super(false, defaultValue);
    }
    
    @Override
    protected Class<Short> getDefaultType() {
        return Short.class;
    }
}
