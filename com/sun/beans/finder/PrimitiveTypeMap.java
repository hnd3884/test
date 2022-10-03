package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

final class PrimitiveTypeMap
{
    private static final Map<String, Class<?>> map;
    
    static Class<?> getType(final String s) {
        return PrimitiveTypeMap.map.get(s);
    }
    
    private PrimitiveTypeMap() {
    }
    
    static {
        (map = new HashMap<String, Class<?>>(9)).put(Boolean.TYPE.getName(), Boolean.TYPE);
        PrimitiveTypeMap.map.put(Character.TYPE.getName(), Character.TYPE);
        PrimitiveTypeMap.map.put(Byte.TYPE.getName(), Byte.TYPE);
        PrimitiveTypeMap.map.put(Short.TYPE.getName(), Short.TYPE);
        PrimitiveTypeMap.map.put(Integer.TYPE.getName(), Integer.TYPE);
        PrimitiveTypeMap.map.put(Long.TYPE.getName(), Long.TYPE);
        PrimitiveTypeMap.map.put(Float.TYPE.getName(), Float.TYPE);
        PrimitiveTypeMap.map.put(Double.TYPE.getName(), Double.TYPE);
        PrimitiveTypeMap.map.put(Void.TYPE.getName(), Void.TYPE);
    }
}
