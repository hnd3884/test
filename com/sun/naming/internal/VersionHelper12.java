package com.sun.naming.internal;

import javax.naming.NamingException;
import java.util.NoSuchElementException;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import javax.naming.NamingEnumeration;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

final class VersionHelper12 extends VersionHelper
{
    private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
    private static final String trustURLCodebase;
    
    @Override
    public Class<?> loadClass(final String s) throws ClassNotFoundException {
        return this.loadClass(s, this.getContextClassLoader());
    }
    
    @Override
    Class<?> loadClass(final String s, final ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(s, true, classLoader);
    }
    
    @Override
    public Class<?> loadClass(final String s, final String s2) throws ClassNotFoundException, MalformedURLException {
        if ("true".equalsIgnoreCase(VersionHelper12.trustURLCodebase)) {
            return this.loadClass(s, URLClassLoader.newInstance(VersionHelper.getUrlArray(s2), this.getContextClassLoader()));
        }
        return null;
    }
    
    @Override
    String getJndiProperty(final int n) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return System.getProperty(VersionHelper.PROPS[n]);
                }
                catch (final SecurityException ex) {
                    return null;
                }
            }
        });
    }
    
    @Override
    String[] getJndiProperties() {
        final Properties properties = AccessController.doPrivileged((PrivilegedAction<Properties>)new PrivilegedAction<Properties>() {
            @Override
            public Properties run() {
                try {
                    return System.getProperties();
                }
                catch (final SecurityException ex) {
                    return null;
                }
            }
        });
        if (properties == null) {
            return null;
        }
        final String[] array = new String[VersionHelper12.PROPS.length];
        for (int i = 0; i < VersionHelper12.PROPS.length; ++i) {
            array[i] = properties.getProperty(VersionHelper12.PROPS[i]);
        }
        return array;
    }
    
    @Override
    InputStream getResourceAsStream(final Class<?> clazz, final String s) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            @Override
            public InputStream run() {
                return clazz.getResourceAsStream(s);
            }
        });
    }
    
    @Override
    InputStream getJavaHomeLibStream(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
            @Override
            public InputStream run() {
                try {
                    final String property = System.getProperty("java.home");
                    if (property == null) {
                        return null;
                    }
                    return new FileInputStream(property + File.separator + "lib" + File.separator + s);
                }
                catch (final Exception ex) {
                    return null;
                }
            }
        });
    }
    
    @Override
    NamingEnumeration<InputStream> getResources(final ClassLoader classLoader, final String s) throws IOException {
        Enumeration enumeration;
        try {
            enumeration = AccessController.doPrivileged((PrivilegedExceptionAction<Enumeration>)new PrivilegedExceptionAction<Enumeration<URL>>() {
                @Override
                public Enumeration<URL> run() throws IOException {
                    return (classLoader == null) ? ClassLoader.getSystemResources(s) : classLoader.getResources(s);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        return new InputStreamEnumeration(enumeration);
    }
    
    @Override
    ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                return classLoader;
            }
        });
    }
    
    static {
        trustURLCodebase = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                try {
                    return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
                }
                catch (final SecurityException ex) {
                    return "false";
                }
            }
        });
    }
    
    class InputStreamEnumeration implements NamingEnumeration<InputStream>
    {
        private final Enumeration<URL> urls;
        private InputStream nextElement;
        
        InputStreamEnumeration(final Enumeration<URL> urls) {
            this.nextElement = null;
            this.urls = urls;
        }
        
        private InputStream getNextElement() {
            return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    while (InputStreamEnumeration.this.urls.hasMoreElements()) {
                        try {
                            return InputStreamEnumeration.this.urls.nextElement().openStream();
                        }
                        catch (final IOException ex) {
                            continue;
                        }
                        break;
                    }
                    return null;
                }
            });
        }
        
        @Override
        public boolean hasMore() {
            if (this.nextElement != null) {
                return true;
            }
            this.nextElement = this.getNextElement();
            return this.nextElement != null;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.hasMore();
        }
        
        @Override
        public InputStream next() {
            if (this.hasMore()) {
                final InputStream nextElement = this.nextElement;
                this.nextElement = null;
                return nextElement;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public InputStream nextElement() {
            return this.next();
        }
        
        @Override
        public void close() {
        }
    }
}
