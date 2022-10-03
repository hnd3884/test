package org.glassfish.hk2.api;

import java.lang.reflect.WildcardType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeLiteral<T>
{
    private transient Type type;
    private transient Class<T> rawType;
    
    protected TypeLiteral() {
    }
    
    public final Type getType() {
        if (this.type == null) {
            final Class<?> typeLiteralSubclass = getTypeLiteralSubclass(this.getClass());
            if (typeLiteralSubclass == null) {
                throw new RuntimeException(this.getClass() + " is not a subclass of TypeLiteral<T>");
            }
            this.type = getTypeParameter(typeLiteralSubclass);
            if (this.type == null) {
                throw new RuntimeException(this.getClass() + " does not specify the type parameter T of TypeLiteral<T>");
            }
        }
        return this.type;
    }
    
    public final Type[] getParameterTypes() {
        this.type = this.getType();
        if (this.type instanceof ParameterizedType) {
            return ((ParameterizedType)this.type).getActualTypeArguments();
        }
        return new Type[0];
    }
    
    public final Class<T> getRawType() {
        if (this.rawType == null) {
            final Type t = this.getType();
            return (Class<T>)getRawType(t);
        }
        return this.rawType;
    }
    
    public static Class<?> getRawType(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            return (Class)parameterizedType.getRawType();
        }
        if (type instanceof GenericArrayType) {
            return Object[].class;
        }
        if (type instanceof WildcardType) {
            return null;
        }
        throw new RuntimeException("Illegal type");
    }
    
    private static Class<?> getTypeLiteralSubclass(final Class<?> clazz) {
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(TypeLiteral.class)) {
            return clazz;
        }
        if (superClass.equals(Object.class)) {
            return null;
        }
        return getTypeLiteralSubclass(superClass);
    }
    
    private static Type getTypeParameter(final Class<?> typeLiteralSubclass) {
        final Type type = typeLiteralSubclass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TypeLiteral) {
            final TypeLiteral<?> that = (TypeLiteral<?>)obj;
            return this.getType().equals(that.getType());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getType().hashCode();
    }
    
    @Override
    public String toString() {
        return this.getType().toString();
    }
}
