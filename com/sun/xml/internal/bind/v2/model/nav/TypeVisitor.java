package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeVisitor<T, P>
{
    public final T visit(final Type t, final P param) {
        assert t != null;
        if (t instanceof Class) {
            return this.onClass((Class)t, param);
        }
        if (t instanceof ParameterizedType) {
            return this.onParameterizdType((ParameterizedType)t, param);
        }
        if (t instanceof GenericArrayType) {
            return this.onGenericArray((GenericArrayType)t, param);
        }
        if (t instanceof WildcardType) {
            return this.onWildcard((WildcardType)t, param);
        }
        if (t instanceof TypeVariable) {
            return this.onVariable((TypeVariable)t, param);
        }
        assert false;
        throw new IllegalArgumentException();
    }
    
    protected abstract T onClass(final Class p0, final P p1);
    
    protected abstract T onParameterizdType(final ParameterizedType p0, final P p1);
    
    protected abstract T onGenericArray(final GenericArrayType p0, final P p1);
    
    protected abstract T onVariable(final TypeVariable p0, final P p1);
    
    protected abstract T onWildcard(final WildcardType p0, final P p1);
}
