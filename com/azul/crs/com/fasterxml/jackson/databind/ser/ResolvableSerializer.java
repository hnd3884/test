package com.azul.crs.com.fasterxml.jackson.databind.ser;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;

public interface ResolvableSerializer
{
    void resolve(final SerializerProvider p0) throws JsonMappingException;
}
