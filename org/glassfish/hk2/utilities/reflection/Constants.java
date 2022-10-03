package org.glassfish.hk2.utilities.reflection;

import java.util.HashMap;

public class Constants
{
    public static final String SYSTEM_LOADER_NAME = "SystemLoader";
    public static final HashMap<Class<?>, Class<?>> PRIMITIVE_MAP;
    
    static {
        (PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>()).put(Character.TYPE, Character.class);
        Constants.PRIMITIVE_MAP.put(Byte.TYPE, Byte.class);
        Constants.PRIMITIVE_MAP.put(Short.TYPE, Short.class);
        Constants.PRIMITIVE_MAP.put(Integer.TYPE, Integer.class);
        Constants.PRIMITIVE_MAP.put(Long.TYPE, Long.class);
        Constants.PRIMITIVE_MAP.put(Float.TYPE, Float.class);
        Constants.PRIMITIVE_MAP.put(Double.TYPE, Double.class);
    }
}
