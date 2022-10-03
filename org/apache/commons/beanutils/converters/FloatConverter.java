package org.apache.commons.beanutils.converters;

public final class FloatConverter extends NumberConverter
{
    public FloatConverter() {
        super(true);
    }
    
    public FloatConverter(final Object defaultValue) {
        super(true, defaultValue);
    }
    
    @Override
    protected Class<Float> getDefaultType() {
        return Float.class;
    }
}
