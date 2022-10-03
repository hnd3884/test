package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class ClassUtil
{
    public static Class loadClass(final Class clazz, final String s) {
        try {
            final ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(s);
            }
            return AccessController.doPrivileged((PrivilegedAction<Class>)new PrivilegedAction() {
                public Object run() {
                    try {
                        return Class.forName(s);
                    }
                    catch (final Exception ex) {
                        return null;
                    }
                }
            });
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
    }
}
