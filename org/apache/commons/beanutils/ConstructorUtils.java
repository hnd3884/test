package org.apache.commons.beanutils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructorUtils
{
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS;
    private static final Object[] EMPTY_OBJECT_ARRAY;
    
    public static <T> T invokeConstructor(final Class<T> klass, final Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Object[] args = toArray(arg);
        return invokeConstructor(klass, args);
    }
    
    public static <T> T invokeConstructor(final Class<T> klass, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = ConstructorUtils.EMPTY_OBJECT_ARRAY;
        }
        final int arguments = args.length;
        final Class<?>[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeConstructor(klass, args, parameterTypes);
    }
    
    public static <T> T invokeConstructor(final Class<T> klass, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (parameterTypes == null) {
            parameterTypes = ConstructorUtils.EMPTY_CLASS_PARAMETERS;
        }
        if (args == null) {
            args = ConstructorUtils.EMPTY_OBJECT_ARRAY;
        }
        final Constructor<T> ctor = getMatchingAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }
    
    public static <T> T invokeExactConstructor(final Class<T> klass, final Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Object[] args = toArray(arg);
        return invokeExactConstructor(klass, args);
    }
    
    public static <T> T invokeExactConstructor(final Class<T> klass, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == args) {
            args = ConstructorUtils.EMPTY_OBJECT_ARRAY;
        }
        final int arguments = args.length;
        final Class<?>[] parameterTypes = new Class[arguments];
        for (int i = 0; i < arguments; ++i) {
            parameterTypes[i] = args[i].getClass();
        }
        return invokeExactConstructor(klass, args, parameterTypes);
    }
    
    public static <T> T invokeExactConstructor(final Class<T> klass, Object[] args, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (args == null) {
            args = ConstructorUtils.EMPTY_OBJECT_ARRAY;
        }
        if (parameterTypes == null) {
            parameterTypes = ConstructorUtils.EMPTY_CLASS_PARAMETERS;
        }
        final Constructor<T> ctor = getAccessibleConstructor(klass, parameterTypes);
        if (null == ctor) {
            throw new NoSuchMethodException("No such accessible constructor on object: " + klass.getName());
        }
        return ctor.newInstance(args);
    }
    
    public static <T> Constructor<T> getAccessibleConstructor(final Class<T> klass, final Class<?> parameterType) {
        final Class<?>[] parameterTypes = { parameterType };
        return getAccessibleConstructor(klass, parameterTypes);
    }
    
    public static <T> Constructor<T> getAccessibleConstructor(final Class<T> klass, final Class<?>[] parameterTypes) {
        try {
            return getAccessibleConstructor(klass.getConstructor(parameterTypes));
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
    }
    
    public static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
        if (ctor == null) {
            return null;
        }
        if (!Modifier.isPublic(ctor.getModifiers())) {
            return null;
        }
        final Class<T> clazz = ctor.getDeclaringClass();
        if (Modifier.isPublic(clazz.getModifiers())) {
            return ctor;
        }
        return null;
    }
    
    private static Object[] toArray(final Object arg) {
        Object[] args = null;
        if (arg != null) {
            args = new Object[] { arg };
        }
        return args;
    }
    
    private static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> clazz, final Class<?>[] parameterTypes) {
        try {
            final Constructor<T> ctor = clazz.getConstructor(parameterTypes);
            try {
                ctor.setAccessible(true);
            }
            catch (final SecurityException ex) {}
            return ctor;
        }
        catch (final NoSuchMethodException ex2) {
            final int paramSize = parameterTypes.length;
            final Constructor<?>[] constructors;
            final Constructor<?>[] ctors = constructors = clazz.getConstructors();
            for (final Constructor<?> ctor2 : constructors) {
                final Class<?>[] ctorParams = ctor2.getParameterTypes();
                final int ctorParamSize = ctorParams.length;
                if (ctorParamSize == paramSize) {
                    boolean match = true;
                    for (int n = 0; n < ctorParamSize; ++n) {
                        if (!MethodUtils.isAssignmentCompatible(ctorParams[n], parameterTypes[n])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        final Constructor<?> ctor3 = getAccessibleConstructor(ctor2);
                        if (ctor3 != null) {
                            try {
                                ctor3.setAccessible(true);
                            }
                            catch (final SecurityException ex3) {}
                            final Constructor<T> typedCtor = (Constructor<T>)ctor3;
                            return typedCtor;
                        }
                    }
                }
            }
            return null;
        }
    }
    
    static {
        EMPTY_CLASS_PARAMETERS = new Class[0];
        EMPTY_OBJECT_ARRAY = new Object[0];
    }
}
