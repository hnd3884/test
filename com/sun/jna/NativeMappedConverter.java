package com.sun.jna;

import java.util.WeakHashMap;
import java.util.Map;

public class NativeMappedConverter implements TypeConverter
{
    private static Map converters;
    private final Class type;
    private final Class nativeType;
    private final NativeMapped instance;
    
    public static NativeMappedConverter getInstance(final Class cls) {
        synchronized (NativeMappedConverter.converters) {
            NativeMappedConverter nmc = NativeMappedConverter.converters.get(cls);
            if (nmc == null) {
                nmc = new NativeMappedConverter(cls);
                NativeMappedConverter.converters.put(cls, nmc);
            }
            return nmc;
        }
    }
    
    public NativeMappedConverter(final Class type) {
        if (!NativeMapped.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
        }
        this.type = type;
        this.instance = this.defaultValue();
        this.nativeType = this.instance.nativeType();
    }
    
    public NativeMapped defaultValue() {
        try {
            return this.type.newInstance();
        }
        catch (final InstantiationException e) {
            final String msg = "Can't create an instance of " + this.type + ", requires a no-arg constructor: " + e;
            throw new IllegalArgumentException(msg);
        }
        catch (final IllegalAccessException e2) {
            final String msg = "Not allowed to create an instance of " + this.type + ", requires a public, no-arg constructor: " + e2;
            throw new IllegalArgumentException(msg);
        }
    }
    
    public Object fromNative(final Object nativeValue, final FromNativeContext context) {
        return this.instance.fromNative(nativeValue, context);
    }
    
    public Class nativeType() {
        return this.nativeType;
    }
    
    public Object toNative(Object value, final ToNativeContext context) {
        if (value == null) {
            if (Pointer.class.isAssignableFrom(this.nativeType)) {
                return null;
            }
            value = this.defaultValue();
        }
        return ((NativeMapped)value).toNative();
    }
    
    static {
        NativeMappedConverter.converters = new WeakHashMap();
    }
}
