package org.apache.commons.beanutils.converters;

public final class IntegerConverter extends NumberConverter
{
    public IntegerConverter() {
        super(false);
    }
    
    public IntegerConverter(final Object defaultValue) {
        super(false, defaultValue);
    }
    
    @Override
    protected Class<Integer> getDefaultType() {
        return Integer.class;
    }
}
