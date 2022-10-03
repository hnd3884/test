package org.apache.commons.lang;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils
{
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    public static final String PACKAGE_SEPARATOR;
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    public static final String INNER_CLASS_SEPARATOR;
    
    public static String getShortClassName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass().getName());
    }
    
    public static String getShortClassName(final Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        return getShortClassName(cls.getName());
    }
    
    public static String getShortClassName(final String className) {
        if (StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException("The class name must not be empty");
        }
        final char[] chars = className.toCharArray();
        int lastDot = 0;
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '.') {
                lastDot = i + 1;
            }
            else if (chars[i] == '$') {
                chars[i] = '.';
            }
        }
        return new String(chars, lastDot, chars.length - lastDot);
    }
    
    public static String getPackageName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass().getName());
    }
    
    public static String getPackageName(final Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        return getPackageName(cls.getName());
    }
    
    public static String getPackageName(final String className) {
        if (StringUtils.isEmpty(className)) {
            throw new IllegalArgumentException("The class name must not be empty");
        }
        final int i = className.lastIndexOf(46);
        if (i == -1) {
            return "";
        }
        return className.substring(0, i);
    }
    
    public static List getAllSuperclasses(final Class cls) {
        if (cls == null) {
            return null;
        }
        final List classes = new ArrayList();
        for (Class superclass = cls.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            classes.add(superclass);
        }
        return classes;
    }
    
    public static List getAllInterfaces(Class cls) {
        if (cls == null) {
            return null;
        }
        final List list = new ArrayList();
        while (cls != null) {
            final Class[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; ++i) {
                if (!list.contains(interfaces[i])) {
                    list.add(interfaces[i]);
                }
                final List superInterfaces = getAllInterfaces(interfaces[i]);
                final Iterator it = superInterfaces.iterator();
                while (it.hasNext()) {
                    final Class intface = it.next();
                    if (!list.contains(intface)) {
                        list.add(intface);
                    }
                }
            }
            cls = cls.getSuperclass();
        }
        return list;
    }
    
    public static List convertClassNamesToClasses(final List classNames) {
        if (classNames == null) {
            return null;
        }
        final List classes = new ArrayList(classNames.size());
        final Iterator it = classNames.iterator();
        while (it.hasNext()) {
            final String className = it.next();
            try {
                classes.add(Class.forName(className));
            }
            catch (final Exception ex) {
                classes.add(null);
            }
        }
        return classes;
    }
    
    public static List convertClassesToClassNames(final List classes) {
        if (classes == null) {
            return null;
        }
        final List classNames = new ArrayList(classes.size());
        final Iterator it = classes.iterator();
        while (it.hasNext()) {
            final Class cls = it.next();
            if (cls == null) {
                classNames.add(null);
            }
            else {
                classNames.add(cls.getName());
            }
        }
        return classNames;
    }
    
    public static boolean isAssignable(Class[] classArray, Class[] toClassArray) {
        if (!ArrayUtils.isSameLength(classArray, toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; ++i) {
            if (!isAssignable(classArray[i], toClassArray[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAssignable(final Class cls, final Class toClass) {
        if (toClass == null) {
            return false;
        }
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (!cls.isPrimitive()) {
            return toClass.isAssignableFrom(cls);
        }
        if (!toClass.isPrimitive()) {
            return false;
        }
        if (Integer.TYPE.equals(cls)) {
            return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
        }
        if (Long.TYPE.equals(cls)) {
            return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
        }
        if (Boolean.TYPE.equals(cls)) {
            return false;
        }
        if (Double.TYPE.equals(cls)) {
            return false;
        }
        if (Float.TYPE.equals(cls)) {
            return Double.TYPE.equals(toClass);
        }
        if (Character.TYPE.equals(cls)) {
            return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
        }
        if (Short.TYPE.equals(cls)) {
            return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
        }
        return Byte.TYPE.equals(cls) && (Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass));
    }
    
    public static boolean isInnerClass(final Class cls) {
        return cls != null && cls.getName().indexOf(36) >= 0;
    }
    
    static {
        PACKAGE_SEPARATOR = String.valueOf('.');
        INNER_CLASS_SEPARATOR = String.valueOf('$');
    }
}
