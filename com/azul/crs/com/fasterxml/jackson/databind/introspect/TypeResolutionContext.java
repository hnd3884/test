package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import com.azul.crs.com.fasterxml.jackson.databind.type.TypeBindings;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Type;

public interface TypeResolutionContext
{
    JavaType resolveType(final Type p0);
    
    public static class Basic implements TypeResolutionContext
    {
        private final TypeFactory _typeFactory;
        private final TypeBindings _bindings;
        
        public Basic(final TypeFactory tf, final TypeBindings b) {
            this._typeFactory = tf;
            this._bindings = b;
        }
        
        @Override
        public JavaType resolveType(final Type type) {
            return this._typeFactory.resolveMemberType(type, this._bindings);
        }
    }
    
    public static class Empty implements TypeResolutionContext
    {
        private final TypeFactory _typeFactory;
        
        public Empty(final TypeFactory tf) {
            this._typeFactory = tf;
        }
        
        @Override
        public JavaType resolveType(final Type type) {
            return this._typeFactory.constructType(type);
        }
    }
}
