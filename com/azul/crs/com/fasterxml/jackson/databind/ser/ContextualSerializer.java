package com.azul.crs.com.fasterxml.jackson.databind.ser;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;

public interface ContextualSerializer
{
    JsonSerializer<?> createContextual(final SerializerProvider p0, final BeanProperty p1) throws JsonMappingException;
}
