package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;

public class ErrorThrowingDeserializer extends JsonDeserializer<Object>
{
    private final Error _cause;
    
    public ErrorThrowingDeserializer(final NoClassDefFoundError cause) {
        this._cause = cause;
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        throw this._cause;
    }
}