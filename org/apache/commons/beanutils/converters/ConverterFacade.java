package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.Converter;

public final class ConverterFacade implements Converter
{
    private final Converter converter;
    
    public ConverterFacade(final Converter converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter is missing");
        }
        this.converter = converter;
    }
    
    @Override
    public <T> T convert(final Class<T> type, final Object value) {
        return this.converter.convert(type, value);
    }
    
    @Override
    public String toString() {
        return "ConverterFacade[" + this.converter.toString() + "]";
    }
}
