package com.azul.crs.com.fasterxml.jackson.databind.jsontype;

import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

public abstract class PolymorphicTypeValidator implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public abstract Validity validateBaseType(final MapperConfig<?> p0, final JavaType p1);
    
    public abstract Validity validateSubClassName(final MapperConfig<?> p0, final JavaType p1, final String p2) throws JsonMappingException;
    
    public abstract Validity validateSubType(final MapperConfig<?> p0, final JavaType p1, final JavaType p2) throws JsonMappingException;
    
    public enum Validity
    {
        ALLOWED, 
        DENIED, 
        INDETERMINATE;
    }
    
    public abstract static class Base extends PolymorphicTypeValidator implements Serializable
    {
        private static final long serialVersionUID = 1L;
        
        @Override
        public Validity validateBaseType(final MapperConfig<?> config, final JavaType baseType) {
            return Validity.INDETERMINATE;
        }
        
        @Override
        public Validity validateSubClassName(final MapperConfig<?> config, final JavaType baseType, final String subClassName) throws JsonMappingException {
            return Validity.INDETERMINATE;
        }
        
        @Override
        public Validity validateSubType(final MapperConfig<?> config, final JavaType baseType, final JavaType subType) throws JsonMappingException {
            return Validity.INDETERMINATE;
        }
    }
}
