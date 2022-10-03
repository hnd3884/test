package org.apache.xerces.impl.xs;

import java.io.InputStream;
import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class SecuritySupport
{
    static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                Object contextClassLoader = null;
                try {
                    contextClassLoader = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ex) {}
                return contextClassLoader;
            }
        });
    }
    
    static ClassLoader getSystemClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                Object systemClassLoader = null;
                try {
                    systemClassLoader = ClassLoader.getSystemClassLoader();
                }
                catch (final SecurityException ex) {}
                return systemClassLoader;
            }
        });
    }
    
    static ClassLoader getParentClassLoader(final ClassLoader classLoader) {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            public Object run() {
                ClassLoader parent = null;
                try {
                    parent = classLoader.getParent();
                }
                catch (final SecurityException ex) {}
                return (parent == classLoader) ? null : parent;
            }
        });
    }
    
    static String getSystemProperty(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty(s);
            }
        });
    }
    
    static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction() {
                public Object run() throws FileNotFoundException {
                    return new FileInputStream(file);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (FileNotFoundException)ex.getException();
        }
    }
    
    static InputStream getResourceAsStream(final ClassLoader classLoader, final String s) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
            public Object run() {
                InputStream inputStream;
                if (classLoader == null) {
                    inputStream = ClassLoader.getSystemResourceAsStream(s);
                }
                else {
                    inputStream = classLoader.getResourceAsStream(s);
                }
                return inputStream;
            }
        });
    }
    
    static boolean getFileExists(final File file) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return file.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
    
    static long getLastModified(final File file) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            public Object run() {
                return new Long(file.lastModified());
            }
        });
    }
    
    private SecuritySupport() {
    }
}
