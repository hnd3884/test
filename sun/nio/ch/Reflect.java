package sun.nio.ch;

import java.lang.reflect.Field;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.AccessibleObject;

class Reflect
{
    private Reflect() {
    }
    
    private static void setAccessible(final AccessibleObject accessibleObject) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                accessibleObject.setAccessible(true);
                return null;
            }
        });
    }
    
    static Constructor<?> lookupConstructor(final String s, final Class<?>[] array) {
        try {
            final Constructor<?> declaredConstructor = Class.forName(s).getDeclaredConstructor(array);
            setAccessible(declaredConstructor);
            return declaredConstructor;
        }
        catch (final ClassNotFoundException | NoSuchMethodException ex) {
            throw new ReflectionError((Throwable)ex);
        }
    }
    
    static Object invoke(final Constructor<?> constructor, final Object[] array) {
        try {
            return constructor.newInstance(array);
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            throw new ReflectionError((Throwable)ex);
        }
    }
    
    static Method lookupMethod(final String s, final String s2, final Class... array) {
        try {
            final Method declaredMethod = Class.forName(s).getDeclaredMethod(s2, (Class<?>[])array);
            setAccessible(declaredMethod);
            return declaredMethod;
        }
        catch (final ClassNotFoundException | NoSuchMethodException ex) {
            throw new ReflectionError((Throwable)ex);
        }
    }
    
    static Object invoke(final Method method, final Object o, final Object[] array) {
        try {
            return method.invoke(o, array);
        }
        catch (final IllegalAccessException | InvocationTargetException ex) {
            throw new ReflectionError((Throwable)ex);
        }
    }
    
    static Object invokeIO(final Method method, final Object o, final Object[] array) throws IOException {
        try {
            return method.invoke(o, array);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionError(ex);
        }
        catch (final InvocationTargetException ex2) {
            if (IOException.class.isInstance(ex2.getCause())) {
                throw (IOException)ex2.getCause();
            }
            throw new ReflectionError(ex2);
        }
    }
    
    static Field lookupField(final String s, final String s2) {
        try {
            final Field declaredField = Class.forName(s).getDeclaredField(s2);
            setAccessible(declaredField);
            return declaredField;
        }
        catch (final ClassNotFoundException | NoSuchFieldException ex) {
            throw new ReflectionError((Throwable)ex);
        }
    }
    
    static Object get(final Object o, final Field field) {
        try {
            return field.get(o);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionError(ex);
        }
    }
    
    static Object get(final Field field) {
        return get(null, field);
    }
    
    static void set(final Object o, final Field field, final Object o2) {
        try {
            field.set(o, o2);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionError(ex);
        }
    }
    
    static void setInt(final Object o, final Field field, final int n) {
        try {
            field.setInt(o, n);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionError(ex);
        }
    }
    
    static void setBoolean(final Object o, final Field field, final boolean b) {
        try {
            field.setBoolean(o, b);
        }
        catch (final IllegalAccessException ex) {
            throw new ReflectionError(ex);
        }
    }
    
    private static class ReflectionError extends Error
    {
        private static final long serialVersionUID = -8659519328078164097L;
        
        ReflectionError(final Throwable t) {
            super(t);
        }
    }
}
