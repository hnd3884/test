package org.glassfish.hk2.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.lang.reflect.Constructor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ClassAnalyzer
{
    public static final String DEFAULT_IMPLEMENTATION_NAME = "default";
    
     <T> Constructor<T> getConstructor(final Class<T> p0) throws MultiException, NoSuchMethodException;
    
     <T> Set<Method> getInitializerMethods(final Class<T> p0) throws MultiException;
    
     <T> Set<Field> getFields(final Class<T> p0) throws MultiException;
    
     <T> Method getPostConstructMethod(final Class<T> p0) throws MultiException;
    
     <T> Method getPreDestroyMethod(final Class<T> p0) throws MultiException;
}
