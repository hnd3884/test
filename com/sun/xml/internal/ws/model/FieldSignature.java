package com.sun.xml.internal.ws.model;

import java.lang.reflect.WildcardType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

final class FieldSignature
{
    static String vms(final Type t) {
        if (t instanceof Class && ((Class)t).isPrimitive()) {
            final Class c = (Class)t;
            if (c == Integer.TYPE) {
                return "I";
            }
            if (c == Void.TYPE) {
                return "V";
            }
            if (c == Boolean.TYPE) {
                return "Z";
            }
            if (c == Byte.TYPE) {
                return "B";
            }
            if (c == Character.TYPE) {
                return "C";
            }
            if (c == Short.TYPE) {
                return "S";
            }
            if (c == Double.TYPE) {
                return "D";
            }
            if (c == Float.TYPE) {
                return "F";
            }
            if (c == Long.TYPE) {
                return "J";
            }
        }
        else {
            if (t instanceof Class && ((Class)t).isArray()) {
                return "[" + vms(((Class)t).getComponentType());
            }
            if (t instanceof Class || t instanceof ParameterizedType) {
                return "L" + fqcn(t) + ";";
            }
            if (t instanceof GenericArrayType) {
                return "[" + vms(((GenericArrayType)t).getGenericComponentType());
            }
            if (t instanceof TypeVariable) {
                return "Ljava/lang/Object;";
            }
            if (t instanceof WildcardType) {
                final WildcardType w = (WildcardType)t;
                if (w.getLowerBounds().length > 0) {
                    return "-" + vms(w.getLowerBounds()[0]);
                }
                if (w.getUpperBounds().length > 0) {
                    final Type wt = w.getUpperBounds()[0];
                    if (wt.equals(Object.class)) {
                        return "*";
                    }
                    return "+" + vms(wt);
                }
            }
        }
        throw new IllegalArgumentException("Illegal vms arg " + t);
    }
    
    private static String fqcn(final Type t) {
        if (t instanceof Class) {
            final Class c = (Class)t;
            if (c.getDeclaringClass() == null) {
                return c.getName().replace('.', '/');
            }
            return fqcn(c.getDeclaringClass()) + "$" + c.getSimpleName();
        }
        else {
            if (!(t instanceof ParameterizedType)) {
                throw new IllegalArgumentException("Illegal fqcn arg = " + t);
            }
            final ParameterizedType p = (ParameterizedType)t;
            if (p.getOwnerType() == null) {
                return fqcn(p.getRawType()) + args(p);
            }
            assert p.getRawType() instanceof Class;
            return fqcn(p.getOwnerType()) + "." + ((Class)p.getRawType()).getSimpleName() + args(p);
        }
    }
    
    private static String args(final ParameterizedType p) {
        final StringBuilder sig = new StringBuilder("<");
        for (final Type t : p.getActualTypeArguments()) {
            sig.append(vms(t));
        }
        return sig.append(">").toString();
    }
}
