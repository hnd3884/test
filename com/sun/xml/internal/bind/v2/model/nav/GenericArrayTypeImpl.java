package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.Type;
import java.lang.reflect.GenericArrayType;

final class GenericArrayTypeImpl implements GenericArrayType
{
    private Type genericComponentType;
    
    GenericArrayTypeImpl(final Type ct) {
        assert ct != null;
        this.genericComponentType = ct;
    }
    
    @Override
    public Type getGenericComponentType() {
        return this.genericComponentType;
    }
    
    @Override
    public String toString() {
        final Type componentType = this.getGenericComponentType();
        final StringBuilder sb = new StringBuilder();
        if (componentType instanceof Class) {
            sb.append(((Class)componentType).getName());
        }
        else {
            sb.append(componentType.toString());
        }
        sb.append("[]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof GenericArrayType) {
            final GenericArrayType that = (GenericArrayType)o;
            final Type thatComponentType = that.getGenericComponentType();
            return this.genericComponentType.equals(thatComponentType);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.genericComponentType.hashCode();
    }
}
