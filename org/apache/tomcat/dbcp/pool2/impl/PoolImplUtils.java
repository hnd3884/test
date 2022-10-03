package org.apache.tomcat.dbcp.pool2.impl;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;

class PoolImplUtils
{
    static Class<?> getFactoryType(final Class<? extends PooledObjectFactory> factoryClass) {
        final Class<PooledObjectFactory> type = PooledObjectFactory.class;
        final Object genericType = getGenericType(type, factoryClass);
        if (genericType instanceof Integer) {
            final ParameterizedType pi = getParameterizedType(type, factoryClass);
            if (pi != null) {
                final Type[] bounds = ((TypeVariable)pi.getActualTypeArguments()[(int)genericType]).getBounds();
                if (bounds != null && bounds.length > 0) {
                    final Type bound0 = bounds[0];
                    if (bound0 instanceof Class) {
                        return (Class)bound0;
                    }
                }
            }
            return Object.class;
        }
        return (Class)genericType;
    }
    
    private static <T> Object getGenericType(final Class<T> type, final Class<? extends T> clazz) {
        if (type == null || clazz == null) {
            return null;
        }
        final ParameterizedType pi = getParameterizedType(type, clazz);
        if (pi != null) {
            return getTypeParameter(clazz, pi.getActualTypeArguments()[0]);
        }
        final Class<? extends T> superClass = (Class<? extends T>)clazz.getSuperclass();
        final Object result = getGenericType((Class<Object>)type, superClass);
        if (result instanceof Class) {
            return result;
        }
        if (result instanceof Integer) {
            final ParameterizedType superClassType = (ParameterizedType)clazz.getGenericSuperclass();
            return getTypeParameter(clazz, superClassType.getActualTypeArguments()[(int)result]);
        }
        return null;
    }
    
    private static <T> ParameterizedType getParameterizedType(final Class<T> type, final Class<? extends T> clazz) {
        for (final Type iface : clazz.getGenericInterfaces()) {
            if (iface instanceof ParameterizedType) {
                final ParameterizedType pi = (ParameterizedType)iface;
                if (pi.getRawType() instanceof Class && type.isAssignableFrom((Class<?>)pi.getRawType())) {
                    return pi;
                }
            }
        }
        return null;
    }
    
    private static Object getTypeParameter(final Class<?> clazz, final Type argType) {
        if (argType instanceof Class) {
            return argType;
        }
        final TypeVariable<?>[] tvs = clazz.getTypeParameters();
        for (int i = 0; i < tvs.length; ++i) {
            if (tvs[i].equals(argType)) {
                return i;
            }
        }
        return null;
    }
}
