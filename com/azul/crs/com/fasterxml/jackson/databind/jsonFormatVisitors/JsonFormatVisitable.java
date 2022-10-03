package com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;

public interface JsonFormatVisitable
{
    void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper p0, final JavaType p1) throws JsonMappingException;
}
