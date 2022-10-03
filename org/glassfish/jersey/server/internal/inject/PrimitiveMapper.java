package org.glassfish.jersey.server.internal.inject;

import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;

final class PrimitiveMapper
{
    static final Map<Class, Class> primitiveToClassMap;
    static final Map<Class, Object> primitiveToDefaultValueMap;
    
    private static Map<Class, Class> getPrimitiveToClassMap() {
        final Map<Class, Class> m = new WeakHashMap<Class, Class>();
        m.put(Boolean.TYPE, Boolean.class);
        m.put(Byte.TYPE, Byte.class);
        m.put(Character.TYPE, Character.class);
        m.put(Short.TYPE, Short.class);
        m.put(Integer.TYPE, Integer.class);
        m.put(Long.TYPE, Long.class);
        m.put(Float.TYPE, Float.class);
        m.put(Double.TYPE, Double.class);
        return (Map<Class, Class>)Collections.unmodifiableMap((Map<? extends Class, ? extends Class>)m);
    }
    
    private static Map<Class, Object> getPrimitiveToDefaultValueMap() {
        final Map<Class, Object> m = new WeakHashMap<Class, Object>();
        m.put(Boolean.class, false);
        m.put(Byte.class, 0);
        m.put(Character.class, '\0');
        m.put(Short.class, 0);
        m.put(Integer.class, 0);
        m.put(Long.class, 0L);
        m.put(Float.class, 0.0f);
        m.put(Double.class, 0.0);
        return (Map<Class, Object>)Collections.unmodifiableMap((Map<? extends Class, ?>)m);
    }
    
    private PrimitiveMapper() {
    }
    
    static {
        primitiveToClassMap = getPrimitiveToClassMap();
        primitiveToDefaultValueMap = getPrimitiveToDefaultValueMap();
    }
}
