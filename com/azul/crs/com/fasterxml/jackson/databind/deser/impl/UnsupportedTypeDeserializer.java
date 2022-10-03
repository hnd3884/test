package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class UnsupportedTypeDeserializer extends StdDeserializer<Object>
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _type;
    protected final String _message;
    
    public UnsupportedTypeDeserializer(final JavaType t, final String m) {
        super(t);
        this._type = t;
        this._message = m;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        ctxt.reportBadDefinition(this._type, this._message);
        return null;
    }
}
