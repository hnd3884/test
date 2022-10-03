package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FailingDeserializer extends StdDeserializer<Object>
{
    private static final long serialVersionUID = 1L;
    protected final String _message;
    
    public FailingDeserializer(final String m) {
        this(Object.class, m);
    }
    
    public FailingDeserializer(final Class<?> rawType, final String m) {
        super(rawType);
        this._message = m;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, this._message, new Object[0]);
        return null;
    }
}
