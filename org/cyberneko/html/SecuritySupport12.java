package org.cyberneko.html;

import java.io.InputStream;
import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecuritySupport12 extends SecuritySupport
{
    ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
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
            public Object run() {
                return System.getProperty(propName);
            }
        });
    }
    
    FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction() {
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
            public Object run() {
                return f.exists();
            }
        });
    }
    
    long getLastModified(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            public Object run() {
                return f.lastModified();
            }
        });
    }
}
