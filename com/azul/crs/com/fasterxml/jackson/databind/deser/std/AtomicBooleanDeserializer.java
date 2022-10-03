package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicBooleanDeserializer() {
        super(AtomicBoolean.class);
    }
    
    @Override
    public AtomicBoolean deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_TRUE) {
            return new AtomicBoolean(true);
        }
        if (t == JsonToken.VALUE_FALSE) {
            return new AtomicBoolean(false);
        }
        final Boolean b = this._parseBoolean(p, ctxt, AtomicBoolean.class);
        return (b == null) ? null : new AtomicBoolean(b);
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Boolean;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicBoolean(false);
    }
}
