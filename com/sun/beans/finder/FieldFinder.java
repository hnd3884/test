package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

public final class FieldFinder
{
    public static Field findField(final Class<?> clazz, final String s) throws NoSuchFieldException {
        if (s == null) {
            throw new IllegalArgumentException("Field name is not set");
        }
        final Field field = clazz.getField(s);
        if (!Modifier.isPublic(field.getModifiers())) {
            throw new NoSuchFieldException("Field '" + s + "' is not public");
        }
        final Class<?> declaringClass = field.getDeclaringClass();
        if (!Modifier.isPublic(declaringClass.getModifiers()) || !ReflectUtil.isPackageAccessible(declaringClass)) {
            throw new NoSuchFieldException("Field '" + s + "' is not accessible");
        }
        return field;
    }
    
    public static Field findInstanceField(final Class<?> clazz, final String s) throws NoSuchFieldException {
        final Field field = findField(clazz, s);
        if (Modifier.isStatic(field.getModifiers())) {
            throw new NoSuchFieldException("Field '" + s + "' is static");
        }
        return field;
    }
    
    public static Field findStaticField(final Class<?> clazz, final String s) throws NoSuchFieldException {
        final Field field = findField(clazz, s);
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new NoSuchFieldException("Field '" + s + "' is not static");
        }
        return field;
    }
    
    private FieldFinder() {
    }
}
