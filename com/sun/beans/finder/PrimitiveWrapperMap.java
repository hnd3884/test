package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

public final class PrimitiveWrapperMap
{
    private static final Map<String, Class<?>> map;
    
    static void replacePrimitivesWithWrappers(final Class<?>[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null && array[i].isPrimitive()) {
                array[i] = getType(array[i].getName());
            }
        }
    }
    
    public static Class<?> getType(final String s) {
        return PrimitiveWrapperMap.map.get(s);
    }
    
    private PrimitiveWrapperMap() {
    }
    
    static {
        (map = new HashMap<String, Class<?>>(9)).put(Boolean.TYPE.getName(), Boolean.class);
        PrimitiveWrapperMap.map.put(Character.TYPE.getName(), Character.class);
        PrimitiveWrapperMap.map.put(Byte.TYPE.getName(), Byte.class);
        PrimitiveWrapperMap.map.put(Short.TYPE.getName(), Short.class);
        PrimitiveWrapperMap.map.put(Integer.TYPE.getName(), Integer.class);
        PrimitiveWrapperMap.map.put(Long.TYPE.getName(), Long.class);
        PrimitiveWrapperMap.map.put(Float.TYPE.getName(), Float.class);
        PrimitiveWrapperMap.map.put(Double.TYPE.getName(), Double.class);
        PrimitiveWrapperMap.map.put(Void.TYPE.getName(), Void.class);
    }
}
