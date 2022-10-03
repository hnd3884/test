package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;

public interface ResolvableDeserializer
{
    void resolve(final DeserializationContext p0) throws JsonMappingException;
}
