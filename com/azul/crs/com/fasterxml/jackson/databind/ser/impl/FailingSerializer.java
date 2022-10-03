package com.azul.crs.com.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FailingSerializer extends StdSerializer<Object>
{
    protected final String _msg;
    
    public FailingSerializer(final String msg) {
        super(Object.class);
        this._msg = msg;
    }
    
    @Override
    public void serialize(final Object value, final JsonGenerator g, final SerializerProvider ctxt) throws IOException {
        ctxt.reportMappingProblem(this._msg, new Object[0]);
    }
}
