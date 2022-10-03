package com.sun.org.apache.xalan.internal.utils;

public class ObjectFactory
{
    private static final String JAXP_INTERNAL = "com.sun.org.apache";
    private static final String STAX_INTERNAL = "com.sun.xml.internal";
    private static final boolean DEBUG = false;
    
    private static void debugPrintln(final String msg) {
    }
    
    public static ClassLoader findClassLoader() {
        if (System.getSecurityManager() != null) {
            return null;
        }
        ClassLoader context;
        ClassLoader chain;
        ClassLoader system;
        for (context = SecuritySupport.getContextClassLoader(), system = (chain = SecuritySupport.getSystemClassLoader()); context != chain; chain = SecuritySupport.getParentClassLoader(chain)) {
            if (chain == null) {
                return context;
            }
        }
        ClassLoader current;
        for (current = ObjectFactory.class.getClassLoader(), chain = system; current != chain; chain = SecuritySupport.getParentClassLoader(chain)) {
            if (chain == null) {
                return current;
            }
        }
        return system;
    }
    
    public static Object newInstance(final String className, final boolean doFallback) throws ConfigurationError {
        if (System.getSecurityManager() != null) {
            return newInstance(className, null, doFallback);
        }
        return newInstance(className, findClassLoader(), doFallback);
    }
    
    static Object newInstance(final String className, final ClassLoader cl, final boolean doFallback) throws ConfigurationError {
        try {
            final Class providerClass = findProviderClass(className, cl, doFallback);
            final Object instance = providerClass.newInstance();
            return instance;
        }
        catch (final ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + className + " not found", x);
        }
        catch (final Exception x2) {
            throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    public static Class<?> findProviderClass(final String className, final boolean doFallback) throws ClassNotFoundException, ConfigurationError {
        return findProviderClass(className, findClassLoader(), doFallback);
    }
    
    private static Class<?> findProviderClass(final String className, ClassLoader cl, final boolean doFallback) throws ClassNotFoundException, ConfigurationError {
        final SecurityManager security = System.getSecurityManager();
        try {
            if (security != null) {
                if (className.startsWith("com.sun.org.apache") || className.startsWith("com.sun.xml.internal")) {
                    cl = null;
                }
                else {
                    final int lastDot = className.lastIndexOf(".");
                    String packageName = className;
                    if (lastDot != -1) {
                        packageName = className.substring(0, lastDot);
                    }
                    security.checkPackageAccess(packageName);
                }
            }
        }
        catch (final SecurityException e) {
            throw e;
        }
        Class<?> providerClass;
        if (cl == null) {
            providerClass = Class.forName(className, false, ObjectFactory.class.getClassLoader());
        }
        else {
            try {
                providerClass = cl.loadClass(className);
            }
            catch (final ClassNotFoundException x) {
                if (!doFallback) {
                    throw x;
                }
                final ClassLoader current = ObjectFactory.class.getClassLoader();
                if (current == null) {
                    providerClass = Class.forName(className);
                }
                else {
                    if (cl == current) {
                        throw x;
                    }
                    cl = current;
                    providerClass = cl.loadClass(className);
                }
            }
        }
        return providerClass;
    }
}
