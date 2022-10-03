package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongDeserializer extends StdScalarDeserializer<AtomicLong>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicLongDeserializer() {
        super(AtomicLong.class);
    }
    
    @Override
    public AtomicLong deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.isExpectedNumberIntToken()) {
            return new AtomicLong(p.getLongValue());
        }
        final Long L = this._parseLong(p, ctxt, AtomicLong.class);
        return (L == null) ? null : new AtomicLong(L.intValue());
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Integer;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return new AtomicLong();
    }
}
