package sun.reflect;

import java.lang.annotation.Annotation;
import sun.misc.VM;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Reflection
{
    private static volatile Map<Class<?>, String[]> fieldFilterMap;
    private static volatile Map<Class<?>, String[]> methodFilterMap;
    
    @CallerSensitive
    public static native Class<?> getCallerClass();
    
    @Deprecated
    public static native Class<?> getCallerClass(final int p0);
    
    public static native int getClassAccessFlags(final Class<?> p0);
    
    public static boolean quickCheckMemberAccess(final Class<?> clazz, final int n) {
        return Modifier.isPublic(getClassAccessFlags(clazz) & n);
    }
    
    public static void ensureMemberAccess(final Class<?> clazz, final Class<?> clazz2, final Object o, final int n) throws IllegalAccessException {
        if (clazz == null || clazz2 == null) {
            throw new InternalError();
        }
        if (!verifyMemberAccess(clazz, clazz2, o, n)) {
            throw new IllegalAccessException("Class " + clazz.getName() + " can not access a member of class " + clazz2.getName() + " with modifiers \"" + Modifier.toString(n) + "\"");
        }
    }
    
    public static boolean verifyMemberAccess(final Class<?> clazz, final Class<?> clazz2, final Object o, final int n) {
        int n2 = 0;
        boolean b = false;
        if (clazz == clazz2) {
            return true;
        }
        if (!Modifier.isPublic(getClassAccessFlags(clazz2))) {
            b = isSameClassPackage(clazz, clazz2);
            n2 = 1;
            if (!b) {
                return false;
            }
        }
        if (Modifier.isPublic(n)) {
            return true;
        }
        int n3 = 0;
        if (Modifier.isProtected(n) && isSubclassOf(clazz, clazz2)) {
            n3 = 1;
        }
        if (n3 == 0 && !Modifier.isPrivate(n)) {
            if (n2 == 0) {
                b = isSameClassPackage(clazz, clazz2);
                n2 = 1;
            }
            if (b) {
                n3 = 1;
            }
        }
        if (n3 == 0) {
            return false;
        }
        if (Modifier.isProtected(n)) {
            final Class<?> clazz3 = (o == null) ? clazz2 : o.getClass();
            if (clazz3 != clazz) {
                if (n2 == 0) {
                    b = isSameClassPackage(clazz, clazz2);
                }
                if (!b && !isSubclassOf(clazz3, clazz)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isSameClassPackage(final Class<?> clazz, final Class<?> clazz2) {
        return isSameClassPackage(clazz.getClassLoader(), clazz.getName(), clazz2.getClassLoader(), clazz2.getName());
    }
    
    private static boolean isSameClassPackage(final ClassLoader classLoader, final String s, final ClassLoader classLoader2, final String s2) {
        if (classLoader != classLoader2) {
            return false;
        }
        final int lastIndex = s.lastIndexOf(46);
        final int lastIndex2 = s2.lastIndexOf(46);
        if (lastIndex == -1 || lastIndex2 == -1) {
            return lastIndex == lastIndex2;
        }
        int n = 0;
        int n2 = 0;
        if (s.charAt(n) == '[') {
            do {
                ++n;
            } while (s.charAt(n) == '[');
            if (s.charAt(n) != 'L') {
                throw new InternalError("Illegal class name " + s);
            }
        }
        if (s2.charAt(n2) == '[') {
            do {
                ++n2;
            } while (s2.charAt(n2) == '[');
            if (s2.charAt(n2) != 'L') {
                throw new InternalError("Illegal class name " + s2);
            }
        }
        final int n3 = lastIndex - n;
        return n3 == lastIndex2 - n2 && s.regionMatches(false, n, s2, n2, n3);
    }
    
    static boolean isSubclassOf(Class<?> superclass, final Class<?> clazz) {
        while (superclass != null) {
            if (superclass == clazz) {
                return true;
            }
            superclass = superclass.getSuperclass();
        }
        return false;
    }
    
    public static synchronized void registerFieldsToFilter(final Class<?> clazz, final String... array) {
        Reflection.fieldFilterMap = registerFilter(Reflection.fieldFilterMap, clazz, array);
    }
    
    public static synchronized void registerMethodsToFilter(final Class<?> clazz, final String... array) {
        Reflection.methodFilterMap = registerFilter(Reflection.methodFilterMap, clazz, array);
    }
    
    private static Map<Class<?>, String[]> registerFilter(final Map<Class<?>, String[]> map, final Class<?> clazz, final String... array) {
        if (map.get(clazz) != null) {
            throw new IllegalArgumentException("Filter already registered: " + clazz);
        }
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)map);
        hashMap.put(clazz, array);
        return hashMap;
    }
    
    public static Field[] filterFields(final Class<?> clazz, final Field[] array) {
        if (Reflection.fieldFilterMap == null) {
            return array;
        }
        return (Field[])filter(array, Reflection.fieldFilterMap.get(clazz));
    }
    
    public static Method[] filterMethods(final Class<?> clazz, final Method[] array) {
        if (Reflection.methodFilterMap == null) {
            return array;
        }
        return (Method[])filter(array, Reflection.methodFilterMap.get(clazz));
    }
    
    private static Member[] filter(final Member[] array, final String[] array2) {
        if (array2 == null || array.length == 0) {
            return array;
        }
        int n = 0;
        for (final Member member : array) {
            boolean b = false;
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                if (member.getName() == array2[j]) {
                    b = true;
                    break;
                }
            }
            if (!b) {
                ++n;
            }
        }
        final Member[] array3 = (Member[])Array.newInstance(array[0].getClass(), n);
        int n2 = 0;
        for (final Member member2 : array) {
            boolean b2 = false;
            for (int length4 = array2.length, l = 0; l < length4; ++l) {
                if (member2.getName() == array2[l]) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                array3[n2++] = member2;
            }
        }
        return array3;
    }
    
    public static boolean isCallerSensitive(final Method method) {
        final ClassLoader classLoader = method.getDeclaringClass().getClassLoader();
        return (VM.isSystemDomainLoader(classLoader) || isExtClassLoader(classLoader)) && method.isAnnotationPresent(CallerSensitive.class);
    }
    
    private static boolean isExtClassLoader(final ClassLoader classLoader) {
        for (ClassLoader classLoader2 = ClassLoader.getSystemClassLoader(); classLoader2 != null; classLoader2 = classLoader2.getParent()) {
            if (classLoader2.getParent() == null && classLoader2 == classLoader) {
                return true;
            }
        }
        return false;
    }
    
    static {
        final HashMap fieldFilterMap = new HashMap();
        fieldFilterMap.put(Reflection.class, new String[] { "fieldFilterMap", "methodFilterMap" });
        fieldFilterMap.put(System.class, new String[] { "security" });
        fieldFilterMap.put(Class.class, new String[] { "classLoader" });
        Reflection.fieldFilterMap = fieldFilterMap;
        Reflection.methodFilterMap = new HashMap<Class<?>, String[]>();
    }
}
