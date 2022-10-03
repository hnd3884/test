package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

@JacksonStdImpl
public class ToStringSerializer extends ToStringSerializerBase
{
    public static final ToStringSerializer instance;
    
    public ToStringSerializer() {
        super(Object.class);
    }
    
    public ToStringSerializer(final Class<?> handledType) {
        super(handledType);
    }
    
    @Override
    public final String valueToString(final Object value) {
        return value.toString();
    }
    
    static {
        instance = new ToStringSerializer();
    }
}
