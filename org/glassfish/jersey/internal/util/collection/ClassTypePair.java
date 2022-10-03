package org.glassfish.jersey.internal.util.collection;

import java.lang.reflect.Type;

public final class ClassTypePair
{
    private final Type type;
    private final Class<?> rawClass;
    
    private ClassTypePair(final Class<?> c, final Type t) {
        this.type = t;
        this.rawClass = c;
    }
    
    public Class<?> rawClass() {
        return this.rawClass;
    }
    
    public Type type() {
        return this.type;
    }
    
    public static ClassTypePair of(final Class<?> rawClass) {
        return new ClassTypePair(rawClass, rawClass);
    }
    
    public static ClassTypePair of(final Class<?> rawClass, final Type type) {
        return new ClassTypePair(rawClass, type);
    }
}
