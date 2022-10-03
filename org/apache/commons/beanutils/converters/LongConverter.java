package org.apache.commons.beanutils.converters;

public final class LongConverter extends NumberConverter
{
    public LongConverter() {
        super(false);
    }
    
    public LongConverter(final Object defaultValue) {
        super(false, defaultValue);
    }
    
    @Override
    protected Class<Long> getDefaultType() {
        return Long.class;
    }
}
