package org.glassfish.hk2.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.io.Serializable;
import java.lang.annotation.Annotation;

public abstract class AnnotationLiteral<T extends Annotation> implements Annotation, Serializable
{
    private static final long serialVersionUID = -3645430766814376616L;
    private transient Class<T> annotationType;
    private transient Method[] members;
    
    protected AnnotationLiteral() {
        final Class<?> thisClass = this.getClass();
        boolean foundAnnotation = false;
        for (final Class<?> iClass : thisClass.getInterfaces()) {
            if (iClass.isAnnotation()) {
                foundAnnotation = true;
                break;
            }
        }
        if (!foundAnnotation) {
            throw new IllegalStateException("The subclass " + thisClass.getName() + " of AnnotationLiteral must implement an Annotation");
        }
    }
    
    private Method[] getMembers() {
        if (this.members == null) {
            this.members = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction<Method[]>() {
                @Override
                public Method[] run() {
                    return AnnotationLiteral.this.annotationType().getDeclaredMethods();
                }
            });
            if (this.members.length > 0 && !this.annotationType().isAssignableFrom(this.getClass())) {
                throw new RuntimeException(this.getClass() + " does not implement the annotation type with members " + this.annotationType().getName());
            }
        }
        return this.members;
    }
    
    private static Class<?> getAnnotationLiteralSubclass(final Class<?> clazz) {
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass.equals(AnnotationLiteral.class)) {
            return clazz;
        }
        if (superclass.equals(Object.class)) {
            return null;
        }
        return getAnnotationLiteralSubclass(superclass);
    }
    
    private static <T> Class<T> getTypeParameter(final Class<?> annotationLiteralSuperclass) {
        final Type type = annotationLiteralSuperclass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType)type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                return (Class)parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }
    
    @Override
    public Class<? extends Annotation> annotationType() {
        if (this.annotationType == null) {
            final Class<?> annotationLiteralSubclass = getAnnotationLiteralSubclass(this.getClass());
            if (annotationLiteralSubclass == null) {
                throw new RuntimeException(this.getClass() + "is not a subclass of AnnotationLiteral");
            }
            this.annotationType = getTypeParameter(annotationLiteralSubclass);
            if (this.annotationType == null) {
                throw new RuntimeException(this.getClass() + " does not specify the type parameter T of AnnotationLiteral<T>");
            }
        }
        return this.annotationType;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Annotation) {
            final Annotation that = (Annotation)other;
            if (this.annotationType().equals(that.annotationType())) {
                for (final Method member : this.getMembers()) {
                    final Object thisValue = invoke(member, this);
                    final Object thatValue = invoke(member, that);
                    if (thisValue instanceof byte[] && thatValue instanceof byte[]) {
                        if (!Arrays.equals((byte[])thisValue, (byte[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof short[] && thatValue instanceof short[]) {
                        if (!Arrays.equals((short[])thisValue, (short[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof int[] && thatValue instanceof int[]) {
                        if (!Arrays.equals((int[])thisValue, (int[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof long[] && thatValue instanceof long[]) {
                        if (!Arrays.equals((long[])thisValue, (long[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof float[] && thatValue instanceof float[]) {
                        if (!Arrays.equals((float[])thisValue, (float[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof double[] && thatValue instanceof double[]) {
                        if (!Arrays.equals((double[])thisValue, (double[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof char[] && thatValue instanceof char[]) {
                        if (!Arrays.equals((char[])thisValue, (char[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof boolean[] && thatValue instanceof boolean[]) {
                        if (!Arrays.equals((boolean[])thisValue, (boolean[])thatValue)) {
                            return false;
                        }
                    }
                    else if (thisValue instanceof Object[] && thatValue instanceof Object[]) {
                        if (!Arrays.equals((Object[])thisValue, (Object[])thatValue)) {
                            return false;
                        }
                    }
                    else if (!thisValue.equals(thatValue)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (final Method member : this.getMembers()) {
            final int memberNameHashCode = 127 * member.getName().hashCode();
            final Object value = invoke(member, this);
            int memberValueHashCode;
            if (value instanceof boolean[]) {
                memberValueHashCode = Arrays.hashCode((boolean[])value);
            }
            else if (value instanceof short[]) {
                memberValueHashCode = Arrays.hashCode((short[])value);
            }
            else if (value instanceof int[]) {
                memberValueHashCode = Arrays.hashCode((int[])value);
            }
            else if (value instanceof long[]) {
                memberValueHashCode = Arrays.hashCode((long[])value);
            }
            else if (value instanceof float[]) {
                memberValueHashCode = Arrays.hashCode((float[])value);
            }
            else if (value instanceof double[]) {
                memberValueHashCode = Arrays.hashCode((double[])value);
            }
            else if (value instanceof byte[]) {
                memberValueHashCode = Arrays.hashCode((byte[])value);
            }
            else if (value instanceof char[]) {
                memberValueHashCode = Arrays.hashCode((char[])value);
            }
            else if (value instanceof Object[]) {
                memberValueHashCode = Arrays.hashCode((Object[])value);
            }
            else {
                memberValueHashCode = value.hashCode();
            }
            hashCode += (memberNameHashCode ^ memberValueHashCode);
        }
        return hashCode;
    }
    
    private static void setAccessible(final AccessibleObject ao) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                ao.setAccessible(true);
                return null;
            }
        });
    }
    
    private static Object invoke(final Method method, final Object instance) {
        try {
            if (!method.isAccessible()) {
                setAccessible(method);
            }
            return method.invoke(instance, new Object[0]);
        }
        catch (final IllegalArgumentException e) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e);
        }
        catch (final IllegalAccessException e2) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e2);
        }
        catch (final InvocationTargetException e3) {
            throw new RuntimeException("Error checking value of member method " + method.getName() + " on " + method.getDeclaringClass(), e3);
        }
    }
}
