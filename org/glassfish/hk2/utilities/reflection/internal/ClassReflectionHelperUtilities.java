package org.glassfish.hk2.utilities.reflection.internal;

import javax.annotation.PreDestroy;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.security.AccessController;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import java.util.Set;

public class ClassReflectionHelperUtilities
{
    static final String CONVENTION_POST_CONSTRUCT = "postConstruct";
    static final String CONVENTION_PRE_DESTROY = "preDestroy";
    private static final Set<MethodWrapper> OBJECT_METHODS;
    private static final Set<Field> OBJECT_FIELDS;
    
    private static Set<MethodWrapper> getObjectMethods() {
        return AccessController.doPrivileged((PrivilegedAction<Set<MethodWrapper>>)new PrivilegedAction<Set<MethodWrapper>>() {
            @Override
            public Set<MethodWrapper> run() {
                final Set<MethodWrapper> retVal = new LinkedHashSet<MethodWrapper>();
                for (final Method method : Object.class.getDeclaredMethods()) {
                    retVal.add(new MethodWrapperImpl(method));
                }
                return retVal;
            }
        });
    }
    
    private static Set<Field> getObjectFields() {
        return AccessController.doPrivileged((PrivilegedAction<Set<Field>>)new PrivilegedAction<Set<Field>>() {
            @Override
            public Set<Field> run() {
                final Set<Field> retVal = new LinkedHashSet<Field>();
                for (final Field field : Object.class.getDeclaredFields()) {
                    retVal.add(field);
                }
                return retVal;
            }
        });
    }
    
    private static Method[] secureGetDeclaredMethods(final Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
            @Override
            public Method[] run() {
                return clazz.getDeclaredMethods();
            }
        });
    }
    
    private static Field[] secureGetDeclaredFields(final Class<?> clazz) {
        return AccessController.doPrivileged((PrivilegedAction<Field[]>)new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                return clazz.getDeclaredFields();
            }
        });
    }
    
    private static Set<MethodWrapper> getDeclaredMethodWrappers(final Class<?> clazz) {
        final Method[] declaredMethods = secureGetDeclaredMethods(clazz);
        final Set<MethodWrapper> retVal = new LinkedHashSet<MethodWrapper>();
        for (final Method method : declaredMethods) {
            retVal.add(new MethodWrapperImpl(method));
        }
        return retVal;
    }
    
    private static Set<Field> getDeclaredFieldWrappers(final Class<?> clazz) {
        final Field[] declaredFields = secureGetDeclaredFields(clazz);
        final Set<Field> retVal = new LinkedHashSet<Field>();
        for (final Field field : declaredFields) {
            retVal.add(field);
        }
        return retVal;
    }
    
    static Set<Field> getAllFieldWrappers(final Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptySet();
        }
        if (Object.class.equals(clazz)) {
            return ClassReflectionHelperUtilities.OBJECT_FIELDS;
        }
        if (clazz.isInterface()) {
            return Collections.emptySet();
        }
        final Set<Field> retVal = new LinkedHashSet<Field>();
        retVal.addAll(getDeclaredFieldWrappers(clazz));
        retVal.addAll(getAllFieldWrappers(clazz.getSuperclass()));
        return retVal;
    }
    
    static Set<MethodWrapper> getAllMethodWrappers(final Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptySet();
        }
        if (Object.class.equals(clazz)) {
            return ClassReflectionHelperUtilities.OBJECT_METHODS;
        }
        final Set<MethodWrapper> retVal = new LinkedHashSet<MethodWrapper>();
        if (clazz.isInterface()) {
            for (final Method m : clazz.getDeclaredMethods()) {
                final MethodWrapper wrapper = new MethodWrapperImpl(m);
                retVal.add(wrapper);
            }
            for (final Class<?> extendee : clazz.getInterfaces()) {
                retVal.addAll(getAllMethodWrappers(extendee));
            }
        }
        else {
            retVal.addAll(getDeclaredMethodWrappers(clazz));
            retVal.addAll(getAllMethodWrappers(clazz.getSuperclass()));
        }
        return retVal;
    }
    
    static boolean isPostConstruct(final Method m) {
        if (!m.isAnnotationPresent(PostConstruct.class)) {
            return m.getParameterTypes().length == 0 && "postConstruct".equals(m.getName());
        }
        if (m.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("The method " + Pretty.method(m) + " annotated with @PostConstruct must not have any arguments");
        }
        return true;
    }
    
    static boolean isPreDestroy(final Method m) {
        if (!m.isAnnotationPresent(PreDestroy.class)) {
            return m.getParameterTypes().length == 0 && "preDestroy".equals(m.getName());
        }
        if (m.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("The method " + Pretty.method(m) + " annotated with @PreDestroy must not have any arguments");
        }
        return true;
    }
    
    static {
        OBJECT_METHODS = getObjectMethods();
        OBJECT_FIELDS = getObjectFields();
    }
}
