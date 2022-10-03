package com.sun.beans;

import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import java.lang.reflect.ParameterizedType;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Map;
import java.lang.reflect.Type;

public final class TypeResolver
{
    private static final WeakCache<Type, Map<Type, Type>> CACHE;
    
    public static Type resolveInClass(final Class<?> clazz, final Type type) {
        return resolve(getActualType(clazz), type);
    }
    
    public static Type[] resolveInClass(final Class<?> clazz, final Type[] array) {
        return resolve(getActualType(clazz), array);
    }
    
    public static Type resolve(final Type type, final Type type2) {
        if (type2 instanceof Class) {
            return type2;
        }
        if (type2 instanceof GenericArrayType) {
            final Type resolve = resolve(type, ((GenericArrayType)type2).getGenericComponentType());
            return (resolve instanceof Class) ? Array.newInstance((Class<?>)resolve, 0).getClass() : GenericArrayTypeImpl.make(resolve);
        }
        if (type2 instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type2;
            return ParameterizedTypeImpl.make((Class<?>)parameterizedType.getRawType(), resolve(type, parameterizedType.getActualTypeArguments()), parameterizedType.getOwnerType());
        }
        if (type2 instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType)type2;
            return new WildcardTypeImpl(resolve(type, wildcardType.getUpperBounds()), resolve(type, wildcardType.getLowerBounds()));
        }
        if (!(type2 instanceof TypeVariable)) {
            throw new IllegalArgumentException("Bad Type kind: " + type2.getClass());
        }
        Map map;
        synchronized (TypeResolver.CACHE) {
            map = TypeResolver.CACHE.get(type);
            if (map == null) {
                map = new HashMap();
                prepare(map, type);
                TypeResolver.CACHE.put(type, map);
            }
        }
        final Type type3 = (Type)map.get(type2);
        if (type3 == null || type3.equals(type2)) {
            return type2;
        }
        return resolve(type, fixGenericArray(type3));
    }
    
    public static Type[] resolve(final Type type, final Type[] array) {
        final int length = array.length;
        final Type[] array2 = new Type[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = resolve(type, array[i]);
        }
        return array2;
    }
    
    public static Class<?> erase(final Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        if (type instanceof TypeVariable) {
            final Type[] bounds = ((TypeVariable)type).getBounds();
            return (0 < bounds.length) ? erase(bounds[0]) : Object.class;
        }
        if (type instanceof WildcardType) {
            final Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            return (0 < upperBounds.length) ? erase(upperBounds[0]) : Object.class;
        }
        if (type instanceof GenericArrayType) {
            return Array.newInstance(erase(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
        }
        throw new IllegalArgumentException("Unknown Type kind: " + type.getClass());
    }
    
    public static Class[] erase(final Type[] array) {
        final int length = array.length;
        final Class[] array2 = new Class[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = erase(array[i]);
        }
        return array2;
    }
    
    private static void prepare(final Map<Type, Type> map, final Type type) {
        final Class clazz = (Class)((type instanceof Class) ? type : ((ParameterizedType)type).getRawType());
        final TypeVariable[] typeParameters = clazz.getTypeParameters();
        final Type[] array = (type instanceof Class) ? typeParameters : ((ParameterizedType)type).getActualTypeArguments();
        assert typeParameters.length == array.length;
        for (int i = 0; i < typeParameters.length; ++i) {
            map.put(typeParameters[i], array[i]);
        }
        final Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass != null) {
            prepare(map, genericSuperclass);
        }
        final Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (int length = genericInterfaces.length, j = 0; j < length; ++j) {
            prepare(map, genericInterfaces[j]);
        }
        if (type instanceof Class && typeParameters.length > 0) {
            for (final Map.Entry entry : map.entrySet()) {
                entry.setValue(erase((Type)entry.getValue()));
            }
        }
    }
    
    private static Type fixGenericArray(final Type type) {
        if (type instanceof GenericArrayType) {
            final Type fixGenericArray = fixGenericArray(((GenericArrayType)type).getGenericComponentType());
            if (fixGenericArray instanceof Class) {
                return Array.newInstance((Class<?>)fixGenericArray, 0).getClass();
            }
        }
        return type;
    }
    
    private static Type getActualType(final Class<?> clazz) {
        final TypeVariable[] typeParameters = clazz.getTypeParameters();
        return (typeParameters.length == 0) ? clazz : ParameterizedTypeImpl.make(clazz, typeParameters, clazz.getEnclosingClass());
    }
    
    static {
        CACHE = new WeakCache<Type, Map<Type, Type>>();
    }
}
