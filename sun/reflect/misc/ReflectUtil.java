package sun.reflect.misc;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Permission;
import sun.security.util.SecurityConstants;
import java.lang.reflect.Member;
import sun.reflect.Reflection;
import java.lang.reflect.Modifier;

public final class ReflectUtil
{
    public static final String PROXY_PACKAGE = "com.sun.proxy";
    
    private ReflectUtil() {
    }
    
    public static Class<?> forName(final String s) throws ClassNotFoundException {
        checkPackageAccess(s);
        return Class.forName(s);
    }
    
    public static Object newInstance(final Class<?> clazz) throws InstantiationException, IllegalAccessException {
        checkPackageAccess(clazz);
        return clazz.newInstance();
    }
    
    public static void ensureMemberAccess(final Class<?> clazz, final Class<?> clazz2, final Object o, final int n) throws IllegalAccessException {
        if (o == null && Modifier.isProtected(n)) {
            final int n2 = (n & 0xFFFFFFFB) | 0x1;
            Reflection.ensureMemberAccess(clazz, clazz2, o, n2);
            try {
                Reflection.ensureMemberAccess(clazz, clazz2, o, n2 & 0xFFFFFFFE);
                return;
            }
            catch (final IllegalAccessException ex) {
                if (isSubclassOf(clazz, clazz2)) {
                    return;
                }
                throw ex;
            }
        }
        Reflection.ensureMemberAccess(clazz, clazz2, o, n);
    }
    
    private static boolean isSubclassOf(Class<?> superclass, final Class<?> clazz) {
        while (superclass != null) {
            if (superclass == clazz) {
                return true;
            }
            superclass = superclass.getSuperclass();
        }
        return false;
    }
    
    public static void conservativeCheckMemberAccess(final Member member) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        final Class<?> declaringClass = member.getDeclaringClass();
        checkPackageAccess(declaringClass);
        if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(declaringClass.getModifiers())) {
            return;
        }
        securityManager.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
    }
    
    public static void checkPackageAccess(final Class<?> clazz) {
        checkPackageAccess(clazz.getName());
        if (isNonPublicProxyClass(clazz)) {
            checkProxyPackageAccess(clazz);
        }
    }
    
    public static void checkPackageAccess(final String s) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            String s2 = s.replace('/', '.');
            if (s2.startsWith("[")) {
                final int n = s2.lastIndexOf(91) + 2;
                if (n > 1 && n < s2.length()) {
                    s2 = s2.substring(n);
                }
            }
            final int lastIndex = s2.lastIndexOf(46);
            if (lastIndex != -1) {
                securityManager.checkPackageAccess(s2.substring(0, lastIndex));
            }
        }
    }
    
    public static boolean isPackageAccessible(final Class<?> clazz) {
        try {
            checkPackageAccess(clazz);
        }
        catch (final SecurityException ex) {
            return false;
        }
        return true;
    }
    
    private static boolean isAncestor(final ClassLoader classLoader, final ClassLoader classLoader2) {
        ClassLoader parent = classLoader2;
        do {
            parent = parent.getParent();
            if (classLoader == parent) {
                return true;
            }
        } while (parent != null);
        return false;
    }
    
    public static boolean needsPackageAccessCheck(final ClassLoader classLoader, final ClassLoader classLoader2) {
        return classLoader != null && classLoader != classLoader2 && (classLoader2 == null || !isAncestor(classLoader, classLoader2));
    }
    
    public static void checkProxyPackageAccess(final Class<?> clazz) {
        if (System.getSecurityManager() != null && Proxy.isProxyClass(clazz)) {
            final Class[] interfaces = clazz.getInterfaces();
            for (int length = interfaces.length, i = 0; i < length; ++i) {
                checkPackageAccess(interfaces[i]);
            }
        }
    }
    
    public static void checkProxyPackageAccess(final ClassLoader classLoader, final Class<?>... array) {
        if (System.getSecurityManager() != null) {
            for (final Class<?> clazz : array) {
                if (needsPackageAccessCheck(classLoader, clazz.getClassLoader())) {
                    checkPackageAccess(clazz);
                }
            }
        }
    }
    
    public static boolean isNonPublicProxyClass(final Class<?> clazz) {
        final String name = clazz.getName();
        final int lastIndex = name.lastIndexOf(46);
        final String s = (lastIndex != -1) ? name.substring(0, lastIndex) : "";
        return Proxy.isProxyClass(clazz) && !s.equals("com.sun.proxy");
    }
    
    public static void checkProxyMethod(final Object o, final Method method) {
        if (o == null || !Proxy.isProxyClass(o.getClass())) {
            throw new IllegalArgumentException("Not a Proxy instance");
        }
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalArgumentException("Can't handle static method");
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass == Object.class) {
            final String name = method.getName();
            if (name.equals("hashCode") || name.equals("equals") || name.equals("toString")) {
                return;
            }
        }
        if (isSuperInterface(o.getClass(), declaringClass)) {
            return;
        }
        throw new IllegalArgumentException("Can't handle: " + method);
    }
    
    private static boolean isSuperInterface(final Class<?> clazz, final Class<?> clazz2) {
        for (final Class clazz3 : clazz.getInterfaces()) {
            if (clazz3 == clazz2) {
                return true;
            }
            if (isSuperInterface(clazz3, clazz2)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isVMAnonymousClass(final Class<?> clazz) {
        return clazz.getName().indexOf("/") > -1;
    }
}
