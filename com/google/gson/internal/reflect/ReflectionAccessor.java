package com.google.gson.internal.reflect;

import com.google.gson.internal.JavaVersion;
import java.lang.reflect.AccessibleObject;

public abstract class ReflectionAccessor
{
    private static final ReflectionAccessor instance;
    
    public abstract void makeAccessible(final AccessibleObject p0);
    
    public static ReflectionAccessor getInstance() {
        return ReflectionAccessor.instance;
    }
    
    static {
        instance = ((JavaVersion.getMajorJavaVersion() < 9) ? new PreJava9ReflectionAccessor() : new UnsafeReflectionAccessor());
    }
}
