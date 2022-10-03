package com.azul.crs.com.fasterxml.jackson.databind.util;

import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<IN, OUT>
{
    OUT convert(final IN p0);
    
    JavaType getInputType(final TypeFactory p0);
    
    JavaType getOutputType(final TypeFactory p0);
    
    public abstract static class None implements Converter<Object, Object>
    {
    }
}
