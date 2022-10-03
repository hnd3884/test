package com.azul.crs.com.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class UnsupportedTypeSerializer extends StdSerializer<Object>
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _type;
    protected final String _message;
    
    public UnsupportedTypeSerializer(final JavaType t, final String msg) {
        super(Object.class);
        this._type = t;
        this._message = msg;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator g, final SerializerProvider ctxt) throws IOException {
        ctxt.reportBadDefinition(this._type, this._message);
    }
}
