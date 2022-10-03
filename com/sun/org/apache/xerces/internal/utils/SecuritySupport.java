package com.sun.org.apache.xerces.internal.utils;

import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.InputStream;
import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

public final class SecuritySupport
{
    private static final SecuritySupport securitySupport;
    static final Properties cacheProps;
    static volatile boolean firstTime;
    
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
        return getResourceAsStream(ObjectFactory.findClassLoader(), name);
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
    
    public static ResourceBundle getResourceBundle(final String bundle) {
        return getResourceBundle(bundle, Locale.getDefault());
    }
    
    public static ResourceBundle getResourceBundle(final String bundle, final Locale locale) {
        return AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                try {
                    return ResourceBundle.getBundle(bundle, locale);
                }
                catch (final MissingResourceException e) {
                    try {
                        return ResourceBundle.getBundle(bundle, new Locale("en", "US"));
                    }
                    catch (final MissingResourceException e2) {
                        throw new MissingResourceException("Could not load any resource bundle by " + bundle, bundle, "");
                    }
                }
            }
        });
    }
    
    static boolean getFileExists(final File f) {
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
    
    public static String sanitizePath(final String uri) {
        if (uri == null) {
            return "";
        }
        final int i = uri.lastIndexOf("/");
        if (i > 0) {
            return uri.substring(i + 1, uri.length());
        }
        return uri;
    }
    
    public static String checkAccess(final String systemId, final String allowedProtocols, final String accessAny) throws IOException {
        if (systemId == null || (allowedProtocols != null && allowedProtocols.equalsIgnoreCase(accessAny))) {
            return null;
        }
        String protocol;
        if (systemId.indexOf(":") == -1) {
            protocol = "file";
        }
        else {
            final URL url = new URL(systemId);
            protocol = url.getProtocol();
            if (protocol.equalsIgnoreCase("jar")) {
                final String path = url.getPath();
                protocol = path.substring(0, path.indexOf(":"));
            }
        }
        if (isProtocolAllowed(protocol, allowedProtocols)) {
            return null;
        }
        return protocol;
    }
    
    private static boolean isProtocolAllowed(final String protocol, final String allowedProtocols) {
        if (allowedProtocols == null) {
            return false;
        }
        final String[] split;
        final String[] temp = split = allowedProtocols.split(",");
        for (String t : split) {
            t = t.trim();
            if (t.equalsIgnoreCase(protocol)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getJAXPSystemProperty(final String sysPropertyId) {
        String accessExternal = getSystemProperty(sysPropertyId);
        if (accessExternal == null) {
            accessExternal = readJAXPProperty(sysPropertyId);
        }
        return accessExternal;
    }
    
    static String readJAXPProperty(final String propertyId) {
        String value = null;
        InputStream is = null;
        try {
            if (SecuritySupport.firstTime) {
                synchronized (SecuritySupport.cacheProps) {
                    if (SecuritySupport.firstTime) {
                        final String configFile = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
                        final File f = new File(configFile);
                        if (getFileExists(f)) {
                            is = getFileInputStream(f);
                            SecuritySupport.cacheProps.load(is);
                        }
                        SecuritySupport.firstTime = false;
                    }
                }
            }
            value = SecuritySupport.cacheProps.getProperty(propertyId);
        }
        catch (final Exception ex) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex2) {}
            }
        }
        return value;
    }
    
    private SecuritySupport() {
    }
    
    static {
        securitySupport = new SecuritySupport();
        cacheProps = new Properties();
        SecuritySupport.firstTime = true;
    }
}
