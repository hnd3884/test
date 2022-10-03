package com.azul.crs.com.fasterxml.jackson.databind.deser;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public interface ContextualKeyDeserializer
{
    KeyDeserializer createContextual(final DeserializationContext p0, final BeanProperty p1) throws JsonMappingException;
}
