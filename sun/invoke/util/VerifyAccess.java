package sun.invoke.util;

import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.Reflection;
import java.lang.reflect.Modifier;

public class VerifyAccess
{
    private static final int PACKAGE_ONLY = 0;
    private static final int PACKAGE_ALLOWED = 8;
    private static final int PROTECTED_OR_PACKAGE_ALLOWED = 12;
    private static final int ALL_ACCESS_MODES = 7;
    private static final boolean ALLOW_NESTMATE_ACCESS = false;
    
    private VerifyAccess() {
    }
    
    public static boolean isMemberAccessible(final Class<?> clazz, final Class<?> clazz2, final int n, final Class<?> clazz3, final int n2) {
        if (n2 == 0) {
            return false;
        }
        assert (n2 & 0x1) != 0x0 && (n2 & 0xFFFFFFF0) == 0x0;
        if (!isClassAccessible(clazz, clazz3, n2)) {
            return false;
        }
        if (clazz2 == clazz3 && (n2 & 0x2) != 0x0) {
            return true;
        }
        switch (n & 0x7) {
            case 1: {
                return true;
            }
            case 4: {
                assert !clazz2.isInterface();
                return ((n2 & 0xC) != 0x0 && isSamePackage(clazz2, clazz3)) || ((n2 & 0x4) != 0x0 && ((n & 0x8) == 0x0 || isRelatedClass(clazz, clazz3)) && ((n2 & 0x4) != 0x0 && isSubClass(clazz3, clazz2)));
            }
            case 0: {
                assert !clazz2.isInterface();
                return (n2 & 0x8) != 0x0 && isSamePackage(clazz2, clazz3);
            }
            case 2: {
                return false;
            }
            default: {
                throw new IllegalArgumentException("bad modifiers: " + Modifier.toString(n));
            }
        }
    }
    
    static boolean isRelatedClass(final Class<?> clazz, final Class<?> clazz2) {
        return clazz == clazz2 || isSubClass(clazz, clazz2) || isSubClass(clazz2, clazz);
    }
    
    static boolean isSubClass(final Class<?> clazz, final Class<?> clazz2) {
        return clazz2.isAssignableFrom(clazz) && !clazz.isInterface();
    }
    
    static int getClassModifiers(final Class<?> clazz) {
        if (clazz.isArray() || clazz.isPrimitive()) {
            return clazz.getModifiers();
        }
        return Reflection.getClassAccessFlags(clazz);
    }
    
    public static boolean isClassAccessible(final Class<?> clazz, final Class<?> clazz2, final int n) {
        if (n == 0) {
            return false;
        }
        assert (n & 0x1) != 0x0 && (n & 0xFFFFFFF0) == 0x0;
        return Modifier.isPublic(getClassModifiers(clazz)) || ((n & 0x8) != 0x0 && isSamePackage(clazz2, clazz));
    }
    
    public static boolean isTypeVisible(Class<?> componentType, final Class<?> clazz) {
        if (componentType == clazz) {
            return true;
        }
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        if (componentType.isPrimitive() || componentType == Object.class) {
            return true;
        }
        final ClassLoader classLoader = componentType.getClassLoader();
        final ClassLoader classLoader2 = clazz.getClassLoader();
        return classLoader == classLoader2 || ((classLoader2 != null || classLoader == null) && ((classLoader == null && componentType.getName().startsWith("java.")) || componentType == AccessController.doPrivileged((PrivilegedAction<Class>)new PrivilegedAction<Class>() {
            final /* synthetic */ String val$name = componentType.getName();
            
            @Override
            public Class<?> run() {
                try {
                    return Class.forName(this.val$name, false, classLoader2);
                }
                catch (final ClassNotFoundException | LinkageError classNotFoundException | LinkageError) {
                    return null;
                }
            }
        })));
    }
    
    public static boolean isTypeVisible(final MethodType methodType, final Class<?> clazz) {
        for (int i = -1; i < methodType.parameterCount(); ++i) {
            if (!isTypeVisible((i < 0) ? methodType.returnType() : methodType.parameterType(i), clazz)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isSamePackage(final Class<?> clazz, final Class<?> clazz2) {
        assert !clazz.isArray() && !clazz2.isArray();
        if (clazz == clazz2) {
            return true;
        }
        if (clazz.getClassLoader() != clazz2.getClassLoader()) {
            return false;
        }
        final String name = clazz.getName();
        final String name2 = clazz2.getName();
        final int lastIndex = name.lastIndexOf(46);
        if (lastIndex != name2.lastIndexOf(46)) {
            return false;
        }
        for (int i = 0; i < lastIndex; ++i) {
            if (name.charAt(i) != name2.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public static String getPackageName(final Class<?> clazz) {
        assert !clazz.isArray();
        final String name = clazz.getName();
        final int lastIndex = name.lastIndexOf(46);
        if (lastIndex < 0) {
            return "";
        }
        return name.substring(0, lastIndex);
    }
    
    public static boolean isSamePackageMember(final Class<?> clazz, final Class<?> clazz2) {
        return clazz == clazz2 || (isSamePackage(clazz, clazz2) && getOutermostEnclosingClass(clazz) == getOutermostEnclosingClass(clazz2));
    }
    
    private static Class<?> getOutermostEnclosingClass(final Class<?> clazz) {
        Class<?> clazz2 = clazz;
        Class<?> enclosingClass = clazz;
        while ((enclosingClass = enclosingClass.getEnclosingClass()) != null) {
            clazz2 = enclosingClass;
        }
        return clazz2;
    }
    
    private static boolean loadersAreRelated(final ClassLoader classLoader, final ClassLoader classLoader2, final boolean b) {
        if (classLoader == classLoader2 || classLoader == null || (classLoader2 == null && !b)) {
            return true;
        }
        for (ClassLoader parent = classLoader2; parent != null; parent = parent.getParent()) {
            if (parent == classLoader) {
                return true;
            }
        }
        if (b) {
            return false;
        }
        for (ClassLoader parent2 = classLoader; parent2 != null; parent2 = parent2.getParent()) {
            if (parent2 == classLoader2) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean classLoaderIsAncestor(final Class<?> clazz, final Class<?> clazz2) {
        return loadersAreRelated(clazz.getClassLoader(), clazz2.getClassLoader(), true);
    }
}
