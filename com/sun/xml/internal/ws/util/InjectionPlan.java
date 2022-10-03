package com.sun.xml.internal.ws.util;

import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Collection;
import java.lang.reflect.Modifier;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;
import java.util.concurrent.Callable;

public abstract class InjectionPlan<T, R>
{
    public abstract void inject(final T p0, final R p1);
    
    public void inject(final T instance, final Callable<R> resource) {
        try {
            this.inject(instance, resource.call());
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
    }
    
    private static void invokeMethod(final Method method, final Object instance, final Object... args) {
        if (method == null) {
            return;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(instance, args);
                }
                catch (final IllegalAccessException e) {
                    throw new WebServiceException(e);
                }
                catch (final InvocationTargetException e2) {
                    throw new WebServiceException(e2);
                }
                return null;
            }
        });
    }
    
    public static <T, R> InjectionPlan<T, R> buildInjectionPlan(final Class<? extends T> clazz, final Class<R> resourceType, final boolean isStatic) {
        final List<InjectionPlan<T, R>> plan = new ArrayList<InjectionPlan<T, R>>();
        for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
            for (final Field field : cl.getDeclaredFields()) {
                final Resource resource = field.getAnnotation(Resource.class);
                if (resource != null && isInjectionPoint(resource, field.getType(), "Incorrect type for field" + field.getName(), resourceType)) {
                    if (isStatic && !Modifier.isStatic(field.getModifiers())) {
                        throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + field);
                    }
                    plan.add(new FieldInjectionPlan<T, R>(field));
                }
            }
        }
        for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
            for (final Method method : cl.getDeclaredMethods()) {
                final Resource resource = method.getAnnotation(Resource.class);
                if (resource != null) {
                    final Class[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length != 1) {
                        throw new WebServiceException("Incorrect no of arguments for method " + method);
                    }
                    if (isInjectionPoint(resource, paramTypes[0], "Incorrect argument types for method" + method.getName(), resourceType)) {
                        if (isStatic && !Modifier.isStatic(method.getModifiers())) {
                            throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + method);
                        }
                        plan.add(new MethodInjectionPlan<T, R>(method));
                    }
                }
            }
        }
        return new Compositor<T, R>(plan);
    }
    
    private static boolean isInjectionPoint(final Resource resource, final Class fieldType, final String errorMessage, final Class resourceType) {
        final Class t = resource.type();
        if (t.equals(Object.class)) {
            return fieldType.equals(resourceType);
        }
        if (!t.equals(resourceType)) {
            return false;
        }
        if (fieldType.isAssignableFrom(resourceType)) {
            return true;
        }
        throw new WebServiceException(errorMessage);
    }
    
    public static class FieldInjectionPlan<T, R> extends InjectionPlan<T, R>
    {
        private final Field field;
        
        public FieldInjectionPlan(final Field field) {
            this.field = field;
        }
        
        @Override
        public void inject(final T instance, final R resource) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        if (!FieldInjectionPlan.this.field.isAccessible()) {
                            FieldInjectionPlan.this.field.setAccessible(true);
                        }
                        FieldInjectionPlan.this.field.set(instance, resource);
                        return null;
                    }
                    catch (final IllegalAccessException e) {
                        throw new WebServiceException(e);
                    }
                }
            });
        }
    }
    
    public static class MethodInjectionPlan<T, R> extends InjectionPlan<T, R>
    {
        private final Method method;
        
        public MethodInjectionPlan(final Method method) {
            this.method = method;
        }
        
        @Override
        public void inject(final T instance, final R resource) {
            invokeMethod(this.method, instance, resource);
        }
    }
    
    private static class Compositor<T, R> extends InjectionPlan<T, R>
    {
        private final Collection<InjectionPlan<T, R>> children;
        
        public Compositor(final Collection<InjectionPlan<T, R>> children) {
            this.children = children;
        }
        
        @Override
        public void inject(final T instance, final R res) {
            for (final InjectionPlan<T, R> plan : this.children) {
                plan.inject(instance, res);
            }
        }
        
        @Override
        public void inject(final T instance, final Callable<R> resource) {
            if (!this.children.isEmpty()) {
                super.inject(instance, resource);
            }
        }
    }
}
