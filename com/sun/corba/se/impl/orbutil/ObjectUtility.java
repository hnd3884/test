package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Array;

public final class ObjectUtility
{
    private ObjectUtility() {
    }
    
    public static Object concatenateArrays(final Object o, final Object o2) {
        final Class<?> componentType = o.getClass().getComponentType();
        final Class<?> componentType2 = o2.getClass().getComponentType();
        final int length = Array.getLength(o);
        final int length2 = Array.getLength(o2);
        if (componentType == null || componentType2 == null) {
            throw new IllegalStateException("Arguments must be arrays");
        }
        if (!componentType.equals(componentType2)) {
            throw new IllegalStateException("Arguments must be arrays with the same component type");
        }
        final Object instance = Array.newInstance(componentType, length + length2);
        int n = 0;
        for (int i = 0; i < length; ++i) {
            Array.set(instance, n++, Array.get(o, i));
        }
        for (int j = 0; j < length2; ++j) {
            Array.set(instance, n++, Array.get(o2, j));
        }
        return instance;
    }
}
