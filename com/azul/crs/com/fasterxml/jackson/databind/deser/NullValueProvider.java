package com.azul.crs.com.fasterxml.jackson.databind.deser;

import com.azul.crs.com.fasterxml.jackson.databind.util.AccessPattern;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public interface NullValueProvider
{
    Object getNullValue(final DeserializationContext p0) throws JsonMappingException;
    
    AccessPattern getNullAccessPattern();
}
