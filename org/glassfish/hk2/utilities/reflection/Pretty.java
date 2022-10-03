package org.glassfish.hk2.utilities.reflection;

import java.util.Collection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class Pretty
{
    private static final String DOT = ".";
    private static final String NULL_STRING = "null";
    private static final String CONSTRUCTOR_NAME = "<init>";
    
    public static String clazz(final Class<?> clazz) {
        if (clazz == null) {
            return "null";
        }
        final String cn = clazz.getName();
        final int index = cn.lastIndexOf(".");
        if (index < 0) {
            return cn;
        }
        return cn.substring(index + 1);
    }
    
    public static String pType(final ParameterizedType pType) {
        final StringBuffer sb = new StringBuffer();
        sb.append(clazz(ReflectionHelper.getRawClass(pType)) + "<");
        boolean first = true;
        for (final Type t : pType.getActualTypeArguments()) {
            if (first) {
                first = false;
                sb.append(type(t));
            }
            else {
                sb.append("," + type(t));
            }
        }
        sb.append(">");
        return sb.toString();
    }
    
    public static String type(final Type t) {
        if (t == null) {
            return "null";
        }
        if (t instanceof Class) {
            return clazz((Class<?>)t);
        }
        if (t instanceof ParameterizedType) {
            return pType((ParameterizedType)t);
        }
        return t.toString();
    }
    
    public static String constructor(final Constructor<?> constructor) {
        if (constructor == null) {
            return "null";
        }
        return "<init>" + prettyPrintParameters(constructor.getParameterTypes());
    }
    
    public static String method(final Method method) {
        if (method == null) {
            return "null";
        }
        return method.getName() + prettyPrintParameters(method.getParameterTypes());
    }
    
    public static String field(final Field field) {
        if (field == null) {
            return "null";
        }
        final Type t = field.getGenericType();
        String baseString;
        if (t instanceof Class) {
            baseString = clazz((Class<?>)t);
        }
        else {
            baseString = type(t);
        }
        return "field(" + baseString + " " + field.getName() + " in " + field.getDeclaringClass().getName() + ")";
    }
    
    public static String array(final Object[] array) {
        if (array == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer("{");
        boolean first = true;
        for (Object item : array) {
            if (item != null && item instanceof Class) {
                item = clazz((Class<?>)item);
            }
            if (first) {
                first = false;
                sb.append((item == null) ? "null" : item.toString());
            }
            else {
                sb.append("," + ((item == null) ? "null" : item.toString()));
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public static String collection(final Collection<?> collection) {
        if (collection == null) {
            return "null";
        }
        return array(collection.toArray(new Object[collection.size()]));
    }
    
    private static String prettyPrintParameters(final Class<?>[] params) {
        if (params == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer("(");
        boolean first = true;
        for (final Class<?> param : params) {
            if (first) {
                sb.append(clazz(param));
                first = false;
            }
            else {
                sb.append("," + clazz(param));
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
