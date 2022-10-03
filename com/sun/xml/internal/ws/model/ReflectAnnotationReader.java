package com.sun.xml.internal.ws.model;

import java.util.Map;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;

public class ReflectAnnotationReader implements MetadataReader
{
    @Override
    public Annotation[] getAnnotations(final Method m) {
        return m.getAnnotations();
    }
    
    @Override
    public Annotation[][] getParameterAnnotations(final Method method) {
        return AccessController.doPrivileged((PrivilegedAction<Annotation[][]>)new PrivilegedAction<Annotation[][]>() {
            @Override
            public Annotation[][] run() {
                return method.getParameterAnnotations();
            }
        });
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
        return AccessController.doPrivileged((PrivilegedAction<A>)new PrivilegedAction<A>() {
            @Override
            public A run() {
                return m.getAnnotation(annType);
            }
        });
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Class<?> cls) {
        return AccessController.doPrivileged((PrivilegedAction<A>)new PrivilegedAction<A>() {
            @Override
            public A run() {
                return cls.getAnnotation(annType);
            }
        });
    }
    
    @Override
    public Annotation[] getAnnotations(final Class<?> cls) {
        return AccessController.doPrivileged((PrivilegedAction<Annotation[]>)new PrivilegedAction<Annotation[]>() {
            @Override
            public Annotation[] run() {
                return cls.getAnnotations();
            }
        });
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Class<?> cls) {
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Method method) {
    }
    
    @Override
    public void getProperties(final Map<String, Object> prop, final Method method, final int pos) {
    }
}
