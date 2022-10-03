package org.glassfish.jersey.internal.util;

import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeVisitor<T>
{
    public final T visit(final Type type) {
        assert type != null;
        if (type instanceof Class) {
            return this.onClass((Class)type);
        }
        if (type instanceof ParameterizedType) {
            return this.onParameterizedType((ParameterizedType)type);
        }
        if (type instanceof GenericArrayType) {
            return this.onGenericArray((GenericArrayType)type);
        }
        if (type instanceof WildcardType) {
            return this.onWildcard((WildcardType)type);
        }
        if (type instanceof TypeVariable) {
            return this.onVariable((TypeVariable)type);
        }
        assert false;
        throw this.createError(type);
    }
    
    protected abstract T onClass(final Class p0);
    
    protected abstract T onParameterizedType(final ParameterizedType p0);
    
    protected abstract T onGenericArray(final GenericArrayType p0);
    
    protected abstract T onVariable(final TypeVariable p0);
    
    protected abstract T onWildcard(final WildcardType p0);
    
    protected RuntimeException createError(final Type type) {
        throw new IllegalArgumentException();
    }
}
