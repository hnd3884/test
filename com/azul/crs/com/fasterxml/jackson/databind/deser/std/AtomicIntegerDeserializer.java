package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDeserializer extends StdScalarDeserializer<AtomicInteger>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicIntegerDeserializer() {
        super(AtomicInteger.class);
    }
    
    @Override
    public AtomicInteger deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.isExpectedNumberIntToken()) {
            return new AtomicInteger(p.getIntValue());
        }
        final Integer I = this._parseInteger(p, ctxt, AtomicInteger.class);
        return (I == null) ? null : new AtomicInteger(I);
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Integer;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicInteger();
    }
}
