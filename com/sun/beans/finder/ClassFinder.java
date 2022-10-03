package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;

public final class ClassFinder
{
    public static Class<?> findClass(final String s) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            if (classLoader != null) {
                return Class.forName(s, false, classLoader);
            }
        }
        catch (final ClassNotFoundException ex) {}
        catch (final SecurityException ex2) {}
        return Class.forName(s);
    }
    
    public static Class<?> findClass(final String s, final ClassLoader classLoader) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        if (classLoader != null) {
            try {
                return Class.forName(s, false, classLoader);
            }
            catch (final ClassNotFoundException ex) {}
            catch (final SecurityException ex2) {}
        }
        return findClass(s);
    }
    
    public static Class<?> resolveClass(final String s) throws ClassNotFoundException {
        return resolveClass(s, null);
    }
    
    public static Class<?> resolveClass(final String s, final ClassLoader classLoader) throws ClassNotFoundException {
        final Class<?> type = PrimitiveTypeMap.getType(s);
        return (type == null) ? findClass(s, classLoader) : type;
    }
    
    private ClassFinder() {
    }
}
