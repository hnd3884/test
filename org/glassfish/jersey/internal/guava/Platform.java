package org.glassfish.jersey.internal.guava;

import java.lang.reflect.Array;

final class Platform
{
    private Platform() {
    }
    
    static long systemNanoTime() {
        return System.nanoTime();
    }
    
    static <T> T[] newArray(final T[] reference, final int length) {
        final Class<?> type = reference.getClass().getComponentType();
        final T[] result = (T[])Array.newInstance(type, length);
        return result;
    }
}
