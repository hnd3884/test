package java.net;

import java.security.AccessControlContext;

final class FactoryURLClassLoader extends URLClassLoader
{
    FactoryURLClassLoader(final URL[] array, final ClassLoader classLoader, final AccessControlContext accessControlContext) {
        super(array, classLoader, accessControlContext);
    }
    
    FactoryURLClassLoader(final URL[] array, final AccessControlContext accessControlContext) {
        super(array, accessControlContext);
    }
    
    public final Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final int lastIndex = s.lastIndexOf(46);
            if (lastIndex != -1) {
                securityManager.checkPackageAccess(s.substring(0, lastIndex));
            }
        }
        return super.loadClass(s, b);
    }
    
    static {
        ClassLoader.registerAsParallelCapable();
    }
}
