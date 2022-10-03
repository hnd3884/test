package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public final class RuntimeInlineAnnotationReader extends AbstractInlineAnnotationReaderImpl<Type, Class, Field, Method> implements RuntimeAnnotationReader
{
    private final Map<Class<? extends Annotation>, Map<Package, Annotation>> packageCache;
    
    public RuntimeInlineAnnotationReader() {
        this.packageCache = new HashMap<Class<? extends Annotation>, Map<Package, Annotation>>();
    }
    
    @Override
    public <A extends Annotation> A getFieldAnnotation(final Class<A> annotation, final Field field, final Locatable srcPos) {
        return LocatableAnnotation.create((A)field.getAnnotation((Class<A>)annotation), srcPos);
    }
    
    @Override
    public boolean hasFieldAnnotation(final Class<? extends Annotation> annotationType, final Field field) {
        return field.isAnnotationPresent(annotationType);
    }
    
    @Override
    public boolean hasClassAnnotation(final Class clazz, final Class<? extends Annotation> annotationType) {
        return clazz.isAnnotationPresent(annotationType);
    }
    
    @Override
    public Annotation[] getAllFieldAnnotations(final Field field, final Locatable srcPos) {
        final Annotation[] r = field.getAnnotations();
        for (int i = 0; i < r.length; ++i) {
            r[i] = LocatableAnnotation.create(r[i], srcPos);
        }
        return r;
    }
    
    @Override
    public <A extends Annotation> A getMethodAnnotation(final Class<A> annotation, final Method method, final Locatable srcPos) {
        return LocatableAnnotation.create((A)method.getAnnotation((Class<A>)annotation), srcPos);
    }
    
    @Override
    public boolean hasMethodAnnotation(final Class<? extends Annotation> annotation, final Method method) {
        return method.isAnnotationPresent(annotation);
    }
    
    @Override
    public Annotation[] getAllMethodAnnotations(final Method method, final Locatable srcPos) {
        final Annotation[] r = method.getAnnotations();
        for (int i = 0; i < r.length; ++i) {
            r[i] = LocatableAnnotation.create(r[i], srcPos);
        }
        return r;
    }
    
    @Override
    public <A extends Annotation> A getMethodParameterAnnotation(final Class<A> annotation, final Method method, final int paramIndex, final Locatable srcPos) {
        final Annotation[] array;
        final Annotation[] pa = array = method.getParameterAnnotations()[paramIndex];
        for (final Annotation a : array) {
            if (a.annotationType() == annotation) {
                return LocatableAnnotation.create(a, srcPos);
            }
        }
        return null;
    }
    
    @Override
    public <A extends Annotation> A getClassAnnotation(final Class<A> a, final Class clazz, final Locatable srcPos) {
        return LocatableAnnotation.create((A)clazz.getAnnotation(a), srcPos);
    }
    
    @Override
    public <A extends Annotation> A getPackageAnnotation(final Class<A> a, final Class clazz, final Locatable srcPos) {
        final Package p = clazz.getPackage();
        if (p == null) {
            return null;
        }
        Map<Package, Annotation> cache = this.packageCache.get(a);
        if (cache == null) {
            cache = new HashMap<Package, Annotation>();
            this.packageCache.put(a, cache);
        }
        if (cache.containsKey(p)) {
            return (A)cache.get(p);
        }
        final A ann = LocatableAnnotation.create((A)p.getAnnotation((Class<A>)a), srcPos);
        cache.put(p, ann);
        return ann;
    }
    
    @Override
    public Class getClassValue(final Annotation a, final String name) {
        try {
            return (Class)a.annotationType().getMethod(name, (Class<?>[])new Class[0]).invoke(a, new Object[0]);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (final InvocationTargetException e2) {
            throw new InternalError(Messages.CLASS_NOT_FOUND.format(a.annotationType(), e2.getMessage()));
        }
        catch (final NoSuchMethodException e3) {
            throw new NoSuchMethodError(e3.getMessage());
        }
    }
    
    @Override
    public Class[] getClassArrayValue(final Annotation a, final String name) {
        try {
            return (Class[])a.annotationType().getMethod(name, (Class<?>[])new Class[0]).invoke(a, new Object[0]);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalAccessError(e.getMessage());
        }
        catch (final InvocationTargetException e2) {
            throw new InternalError(e2.getMessage());
        }
        catch (final NoSuchMethodException e3) {
            throw new NoSuchMethodError(e3.getMessage());
        }
    }
    
    @Override
    protected String fullName(final Method m) {
        return m.getDeclaringClass().getName() + '#' + m.getName();
    }
}
