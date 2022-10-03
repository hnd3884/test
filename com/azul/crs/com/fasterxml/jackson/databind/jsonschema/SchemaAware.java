package com.azul.crs.com.fasterxml.jackson.databind.jsonschema;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;

public interface SchemaAware
{
    JsonNode getSchema(final SerializerProvider p0, final Type p1) throws JsonMappingException;
    
    JsonNode getSchema(final SerializerProvider p0, final Type p1, final boolean p2) throws JsonMappingException;
}
