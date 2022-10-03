package org.apache.commons.beanutils.converters;

public final class DoubleConverter extends NumberConverter
{
    public DoubleConverter() {
        super(true);
    }
    
    public DoubleConverter(final Object defaultValue) {
        super(true, defaultValue);
    }
    
    @Override
    protected Class<Double> getDefaultType() {
        return Double.class;
    }
}
