package sun.invoke.util;

import java.util.Iterator;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BytecodeDescriptor
{
    private BytecodeDescriptor() {
    }
    
    public static List<Class<?>> parseMethod(final String s, final ClassLoader classLoader) {
        return parseMethod(s, 0, s.length(), classLoader);
    }
    
    static List<Class<?>> parseMethod(final String s, final int n, final int n2, ClassLoader systemClassLoader) {
        if (systemClassLoader == null) {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        }
        final int[] array = { n };
        final ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        if (array[0] < n2 && s.charAt(array[0]) == '(') {
            final int[] array2 = array;
            final int n3 = 0;
            ++array2[n3];
            while (array[0] < n2 && s.charAt(array[0]) != ')') {
                final Class<?> sig = parseSig(s, array, n2, systemClassLoader);
                if (sig == null || sig == Void.TYPE) {
                    parseError(s, "bad argument type");
                }
                list.add(sig);
            }
            final int[] array3 = array;
            final int n4 = 0;
            ++array3[n4];
        }
        else {
            parseError(s, "not a method type");
        }
        final Class<?> sig2 = parseSig(s, array, n2, systemClassLoader);
        if (sig2 == null || array[0] != n2) {
            parseError(s, "bad return type");
        }
        list.add(sig2);
        return list;
    }
    
    private static void parseError(final String s, final String s2) {
        throw new IllegalArgumentException("bad signature: " + s + ": " + s2);
    }
    
    private static Class<?> parseSig(final String s, final int[] array, final int n, final ClassLoader classLoader) {
        if (array[0] == n) {
            return null;
        }
        final char char1 = s.charAt(array[0]++);
        if (char1 == 'L') {
            final int n2 = array[0];
            final int index = s.indexOf(59, n2);
            if (index < 0) {
                return null;
            }
            array[0] = index + 1;
            final String replace = s.substring(n2, index).replace('/', '.');
            try {
                return classLoader.loadClass(replace);
            }
            catch (final ClassNotFoundException ex) {
                throw new TypeNotPresentException(replace, ex);
            }
        }
        if (char1 == '[') {
            Class<?> clazz = parseSig(s, array, n, classLoader);
            if (clazz != null) {
                clazz = Array.newInstance(clazz, 0).getClass();
            }
            return clazz;
        }
        return Wrapper.forBasicType(char1).primitiveType();
    }
    
    public static String unparse(final Class<?> clazz) {
        final StringBuilder sb = new StringBuilder();
        unparseSig(clazz, sb);
        return sb.toString();
    }
    
    public static String unparse(final MethodType methodType) {
        return unparseMethod(methodType.returnType(), methodType.parameterList());
    }
    
    public static String unparse(final Object o) {
        if (o instanceof Class) {
            return unparse((Class<?>)o);
        }
        if (o instanceof MethodType) {
            return unparse((MethodType)o);
        }
        return (String)o;
    }
    
    public static String unparseMethod(final Class<?> clazz, final List<Class<?>> list) {
        final StringBuilder sb = new StringBuilder();
        sb.append('(');
        final Iterator<Class<?>> iterator = list.iterator();
        while (iterator.hasNext()) {
            unparseSig(iterator.next(), sb);
        }
        sb.append(')');
        unparseSig(clazz, sb);
        return sb.toString();
    }
    
    private static void unparseSig(final Class<?> clazz, final StringBuilder sb) {
        final char basicTypeChar = Wrapper.forBasicType(clazz).basicTypeChar();
        if (basicTypeChar != 'L') {
            sb.append(basicTypeChar);
        }
        else {
            final boolean b = !clazz.isArray();
            if (b) {
                sb.append('L');
            }
            sb.append(clazz.getName().replace('.', '/'));
            if (b) {
                sb.append(';');
            }
        }
    }
}
