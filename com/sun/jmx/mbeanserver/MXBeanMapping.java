package com.sun.jmx.mbeanserver;

import javax.management.openmbean.OpenDataException;
import java.io.InvalidObjectException;
import javax.management.openmbean.OpenType;
import java.lang.reflect.Type;

public abstract class MXBeanMapping
{
    private final Type javaType;
    private final OpenType<?> openType;
    private final Class<?> openClass;
    
    protected MXBeanMapping(final Type javaType, final OpenType<?> openType) {
        if (javaType == null || openType == null) {
            throw new NullPointerException("Null argument");
        }
        this.javaType = javaType;
        this.openType = openType;
        this.openClass = makeOpenClass(javaType, openType);
    }
    
    public final Type getJavaType() {
        return this.javaType;
    }
    
    public final OpenType<?> getOpenType() {
        return this.openType;
    }
    
    public final Class<?> getOpenClass() {
        return this.openClass;
    }
    
    private static Class<?> makeOpenClass(final Type type, final OpenType<?> openType) {
        if (type instanceof Class && ((Class)type).isPrimitive()) {
            return (Class)type;
        }
        try {
            return Class.forName(openType.getClassName(), false, MXBeanMapping.class.getClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public abstract Object fromOpenValue(final Object p0) throws InvalidObjectException;
    
    public abstract Object toOpenValue(final Object p0) throws OpenDataException;
    
    public void checkReconstructible() throws InvalidObjectException {
    }
}
