package com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWithSerializerProvider
{
    SerializerProvider getProvider();
    
    void setProvider(final SerializerProvider p0);
}
