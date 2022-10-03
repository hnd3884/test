package com.fasterxml.jackson.databind.exc;

import java.io.Closeable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ValueInstantiationException extends JsonMappingException
{
    protected final JavaType _type;
    
    protected ValueInstantiationException(final JsonParser p, final String msg, final JavaType type, final Throwable cause) {
        super((Closeable)p, msg, cause);
        this._type = type;
    }
    
    protected ValueInstantiationException(final JsonParser p, final String msg, final JavaType type) {
        super((Closeable)p, msg);
        this._type = type;
    }
    
    public static ValueInstantiationException from(final JsonParser p, final String msg, final JavaType type) {
        return new ValueInstantiationException(p, msg, type);
    }
    
    public static ValueInstantiationException from(final JsonParser p, final String msg, final JavaType type, final Throwable cause) {
        return new ValueInstantiationException(p, msg, type, cause);
    }
    
    public JavaType getType() {
        return this._type;
    }
}
