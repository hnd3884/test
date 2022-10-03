package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.slf4j.internal.LoggerFactory;
import com.sun.org.slf4j.internal.Logger;

final class ClassLoaderUtils
{
    private static final Logger LOG;
    
    private ClassLoaderUtils() {
    }
    
    static Class<?> loadClass(final String s, final Class<?> clazz) throws ClassNotFoundException {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(s);
            }
        }
        catch (final ClassNotFoundException ex) {
            ClassLoaderUtils.LOG.debug(ex.getMessage(), ex);
        }
        return loadClass2(s, clazz);
    }
    
    private static Class<?> loadClass2(final String s, final Class<?> clazz) throws ClassNotFoundException {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            try {
                if (ClassLoaderUtils.class.getClassLoader() != null) {
                    return ClassLoaderUtils.class.getClassLoader().loadClass(s);
                }
            }
            catch (final ClassNotFoundException ex2) {
                if (clazz != null && clazz.getClassLoader() != null) {
                    return clazz.getClassLoader().loadClass(s);
                }
            }
            ClassLoaderUtils.LOG.debug(ex.getMessage(), ex);
            throw ex;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ClassLoaderUtils.class);
    }
}
