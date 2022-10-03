package org.apache.commons.beanutils.converters;

public final class ByteConverter extends NumberConverter
{
    public ByteConverter() {
        super(false);
    }
    
    public ByteConverter(final Object defaultValue) {
        super(false, defaultValue);
    }
    
    @Override
    protected Class<Byte> getDefaultType() {
        return Byte.class;
    }
}
