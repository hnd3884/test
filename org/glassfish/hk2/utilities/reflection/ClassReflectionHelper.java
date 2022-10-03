package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface ClassReflectionHelper
{
    Set<MethodWrapper> getAllMethods(final Class<?> p0);
    
    MethodWrapper createMethodWrapper(final Method p0);
    
    Set<Field> getAllFields(final Class<?> p0);
    
    Method findPostConstruct(final Class<?> p0, final Class<?> p1) throws IllegalArgumentException;
    
    Method findPreDestroy(final Class<?> p0, final Class<?> p1) throws IllegalArgumentException;
    
    void clean(final Class<?> p0);
    
    void dispose();
    
    int size();
}
