package com.azul.crs.com.fasterxml.jackson.databind.deser;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public interface ContextualDeserializer
{
    JsonDeserializer<?> createContextual(final DeserializationContext p0, final BeanProperty p1) throws JsonMappingException;
}
