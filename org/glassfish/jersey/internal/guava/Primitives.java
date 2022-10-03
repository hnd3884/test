package org.glassfish.jersey.internal.guava;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives
{
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    
    private Primitives() {
    }
    
    private static void add(final Map<Class<?>, Class<?>> forward, final Class<?> key, final Class<?> value) {
        forward.put(key, value);
    }
    
    public static <T> Class<T> wrap(final Class<T> type) {
        Preconditions.checkNotNull(type);
        final Class<T> wrapped = (Class<T>)Primitives.PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return (wrapped == null) ? type : wrapped;
    }
    
    static {
        final Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
        add(primToWrap, Boolean.TYPE, Boolean.class);
        add(primToWrap, Byte.TYPE, Byte.class);
        add(primToWrap, Character.TYPE, Character.class);
        add(primToWrap, Double.TYPE, Double.class);
        add(primToWrap, Float.TYPE, Float.class);
        add(primToWrap, Integer.TYPE, Integer.class);
        add(primToWrap, Long.TYPE, Long.class);
        add(primToWrap, Short.TYPE, Short.class);
        add(primToWrap, Void.TYPE, Void.class);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap((Map<? extends Class<?>, ? extends Class<?>>)primToWrap);
    }
}
