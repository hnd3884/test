package org.apache.harmony.beans;

import java.lang.reflect.Method;
import java.util.Arrays;

public class BeansUtils
{
    public static final Object[] EMPTY_OBJECT_ARRAY;
    public static final String NEW = "new";
    public static final String NEWINSTANCE = "newInstance";
    public static final String NEWARRAY = "newArray";
    public static final String FORNAME = "forName";
    public static final String GET = "get";
    public static final String IS = "is";
    public static final String SET = "set";
    public static final String ADD = "add";
    public static final String PUT = "put";
    public static final String NULL = "null";
    public static final String QUOTE = "\"\"";
    private static final String EQUALS_METHOD = "equals";
    private static final Class<?>[] EQUALS_PARAMETERS;
    
    public static final int getHashCode(final Object obj) {
        return (obj != null) ? obj.hashCode() : 0;
    }
    
    public static final int getHashCode(final boolean bool) {
        return bool ? 1 : 0;
    }
    
    public static String toASCIILowerCase(final String string) {
        final char[] charArray = string.toCharArray();
        final StringBuilder sb = new StringBuilder(charArray.length);
        for (int index = 0; index < charArray.length; ++index) {
            if ('A' <= charArray[index] && charArray[index] <= 'Z') {
                sb.append((char)(charArray[index] + ' '));
            }
            else {
                sb.append(charArray[index]);
            }
        }
        return sb.toString();
    }
    
    public static String toASCIIUpperCase(final String string) {
        final char[] charArray = string.toCharArray();
        final StringBuilder sb = new StringBuilder(charArray.length);
        for (int index = 0; index < charArray.length; ++index) {
            if ('a' <= charArray[index] && charArray[index] <= 'z') {
                sb.append((char)(charArray[index] - ' '));
            }
            else {
                sb.append(charArray[index]);
            }
        }
        return sb.toString();
    }
    
    public static boolean isPrimitiveWrapper(final Class<?> wrapper, final Class<?> base) {
        return (base == Boolean.TYPE && wrapper == Boolean.class) || (base == Byte.TYPE && wrapper == Byte.class) || (base == Character.TYPE && wrapper == Character.class) || (base == Short.TYPE && wrapper == Short.class) || (base == Integer.TYPE && wrapper == Integer.class) || (base == Long.TYPE && wrapper == Long.class) || (base == Float.TYPE && wrapper == Float.class) || (base == Double.TYPE && wrapper == Double.class);
    }
    
    public static boolean declaredEquals(final Class<?> clazz) {
        for (final Method declaredMethod : clazz.getDeclaredMethods()) {
            if ("equals".equals(declaredMethod.getName()) && Arrays.equals(declaredMethod.getParameterTypes(), BeansUtils.EQUALS_PARAMETERS)) {
                return true;
            }
        }
        return false;
    }
    
    public static String idOfClass(final Class<?> clazz) {
        Class<?> theClass = clazz;
        final StringBuilder sb = new StringBuilder();
        if (theClass.isArray()) {
            do {
                sb.append("Array");
                theClass = theClass.getComponentType();
            } while (theClass.isArray());
        }
        String clazzName = theClass.getName();
        clazzName = clazzName.substring(clazzName.lastIndexOf(46) + 1);
        return clazzName + sb.toString();
    }
    
    static {
        EMPTY_OBJECT_ARRAY = new Object[0];
        EQUALS_PARAMETERS = new Class[] { Object.class };
    }
}
