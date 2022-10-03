package com.sun.xml.internal.ws.api.databinding;

import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface MetadataReader
{
    Annotation[] getAnnotations(final Method p0);
    
    Annotation[][] getParameterAnnotations(final Method p0);
    
     <A extends Annotation> A getAnnotation(final Class<A> p0, final Method p1);
    
     <A extends Annotation> A getAnnotation(final Class<A> p0, final Class<?> p1);
    
    Annotation[] getAnnotations(final Class<?> p0);
    
    void getProperties(final Map<String, Object> p0, final Class<?> p1);
    
    void getProperties(final Map<String, Object> p0, final Method p1);
    
    void getProperties(final Map<String, Object> p0, final Method p1, final int p2);
}
