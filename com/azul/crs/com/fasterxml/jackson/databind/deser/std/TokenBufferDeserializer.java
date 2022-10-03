package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.util.TokenBuffer;

@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer>
{
    private static final long serialVersionUID = 1L;
    
    public TokenBufferDeserializer() {
        super(TokenBuffer.class);
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Untyped;
    }
    
    @Override
    public TokenBuffer deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return this.createBufferInstance(p).deserialize(p, ctxt);
    }
    
    protected TokenBuffer createBufferInstance(final JsonParser p) {
        return new TokenBuffer(p);
    }
}
