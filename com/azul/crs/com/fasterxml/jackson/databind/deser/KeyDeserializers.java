package com.azul.crs.com.fasterxml.jackson.databind.deser;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;

public interface KeyDeserializers
{
    KeyDeserializer findKeyDeserializer(final JavaType p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
}
