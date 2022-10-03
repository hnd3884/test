package org.apache.commons.compress.utils;

public class OsgiUtils
{
    private static final boolean inOsgiEnvironment;
    
    private static boolean isBundleReference(final Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if (c.getName().equals("org.osgi.framework.BundleReference")) {
                return true;
            }
            for (final Class<?> ifc : c.getInterfaces()) {
                if (isBundleReference(ifc)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isRunningInOsgiEnvironment() {
        return OsgiUtils.inOsgiEnvironment;
    }
    
    static {
        final Class<?> classloaderClass = OsgiUtils.class.getClassLoader().getClass();
        inOsgiEnvironment = isBundleReference(classloaderClass);
    }
}
