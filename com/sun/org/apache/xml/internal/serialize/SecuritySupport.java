package com.sun.org.apache.xml.internal.serialize;

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
    private static final SecuritySupport securitySupport;
    
    static SecuritySupport getInstance() {
        return SecuritySupport.securitySupport;
    }
    
    ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ex) {}
                return cl;
            }
        });
    }
    
    ClassLoader getSystemClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (final SecurityException ex) {}
                return cl;
            }
        });
    }
    
    ClassLoader getParentClassLoader(final ClassLoader cl) {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                ClassLoader parent = null;
                try {
                    parent = cl.getParent();
                }
                catch (final SecurityException ex) {}
                return (parent == cl) ? null : parent;
            }
        });
    }
    
    String getSystemProperty(final String propName) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(propName);
            }
        });
    }
    
    FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws FileNotFoundException {
                    return new FileInputStream(file);
                }
            });
        }
        catch (final PrivilegedActionException e) {
            throw (FileNotFoundException)e.getException();
        }
    }
    
    InputStream getResourceAsStream(final ClassLoader cl, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
            @Override
            public Object run() {
                InputStream ris;
                if (cl == null) {
                    ris = ClassLoader.getSystemResourceAsStream(name);
                }
                else {
                    ris = cl.getResourceAsStream(name);
                }
                return ris;
            }
        });
    }
    
    boolean getFileExists(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new Boolean(f.exists());
            }
        });
    }
    
    long getLastModified(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new Long(f.lastModified());
            }
        });
    }
    
    private SecuritySupport() {
    }
    
    static {
        securitySupport = new SecuritySupport();
    }
}
