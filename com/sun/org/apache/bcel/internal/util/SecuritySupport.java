package com.sun.org.apache.bcel.internal.util;

import java.io.FilenameFilter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.ListResourceBundle;
import java.io.InputStream;
import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class SecuritySupport
{
    private static final SecuritySupport securitySupport;
    
    public static SecuritySupport getInstance() {
        return SecuritySupport.securitySupport;
    }
    
    static ClassLoader getContextClassLoader() {
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
    
    static ClassLoader getSystemClassLoader() {
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
    
    static ClassLoader getParentClassLoader(final ClassLoader cl) {
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
    
    public static String getSystemProperty(final String propName) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty(propName);
            }
        });
    }
    
    static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
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
    
    public static InputStream getResourceAsStream(final String name) {
        if (System.getSecurityManager() != null) {
            return getResourceAsStream(null, name);
        }
        return getResourceAsStream(findClassLoader(), name);
    }
    
    public static InputStream getResourceAsStream(final ClassLoader cl, final String name) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
            @Override
            public Object run() {
                InputStream ris;
                if (cl == null) {
                    ris = Object.class.getResourceAsStream("/" + name);
                }
                else {
                    ris = cl.getResourceAsStream(name);
                }
                return ris;
            }
        });
    }
    
    public static ListResourceBundle getResourceBundle(final String bundle) {
        return getResourceBundle(bundle, Locale.getDefault());
    }
    
    public static ListResourceBundle getResourceBundle(final String bundle, final Locale locale) {
        return AccessController.doPrivileged((PrivilegedAction<ListResourceBundle>)new PrivilegedAction<ListResourceBundle>() {
            @Override
            public ListResourceBundle run() {
                try {
                    return (ListResourceBundle)ResourceBundle.getBundle(bundle, locale);
                }
                catch (final MissingResourceException e) {
                    try {
                        return (ListResourceBundle)ResourceBundle.getBundle(bundle, new Locale("en", "US"));
                    }
                    catch (final MissingResourceException e2) {
                        throw new MissingResourceException("Could not load any resource bundle by " + bundle, bundle, "");
                    }
                }
            }
        });
    }
    
    public static String[] getFileList(final File f, final FilenameFilter filter) {
        return AccessController.doPrivileged((PrivilegedAction<String[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                return f.list(filter);
            }
        });
    }
    
    public static boolean getFileExists(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            @Override
            public Object run() {
                return f.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
    
    static long getLastModified(final File f) {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new Long(f.lastModified());
            }
        });
    }
    
    public static ClassLoader findClassLoader() {
        if (System.getSecurityManager() != null) {
            return null;
        }
        return SecuritySupport.class.getClassLoader();
    }
    
    private SecuritySupport() {
    }
    
    static {
        securitySupport = new SecuritySupport();
    }
}
