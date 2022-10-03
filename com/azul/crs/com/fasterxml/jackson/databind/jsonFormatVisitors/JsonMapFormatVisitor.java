package com.azul.crs.com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;

public interface JsonMapFormatVisitor extends JsonFormatVisitorWithSerializerProvider
{
    void keyFormat(final JsonFormatVisitable p0, final JavaType p1) throws JsonMappingException;
    
    void valueFormat(final JsonFormatVisitable p0, final JavaType p1) throws JsonMappingException;
    
    public static class Base implements JsonMapFormatVisitor
    {
        protected SerializerProvider _provider;
        
        public Base() {
        }
        
        public Base(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public SerializerProvider getProvider() {
            return this._provider;
        }
        
        @Override
        public void setProvider(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public void keyFormat(final JsonFormatVisitable handler, final JavaType keyType) throws JsonMappingException {
        }
        
        @Override
        public void valueFormat(final JsonFormatVisitable handler, final JavaType valueType) throws JsonMappingException {
        }
    }
}
