package javax.xml.xpath;

import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.io.FileNotFoundException;
import java.security.PrivilegedExceptionAction;
import java.io.FileInputStream;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class SecuritySupport
{
    private SecuritySupport() {
    }
    
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
    
    static InputStream getURLInputStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return url.openStream();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    static URL getResourceAsURL(final ClassLoader classLoader, final String s) {
        return AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction() {
            public Object run() {
                URL url;
                if (classLoader == null) {
                    url = ClassLoader.getSystemResource(s);
                }
                else {
                    url = classLoader.getResource(s);
                }
                return url;
            }
        });
    }
    
    static Enumeration getResources(final ClassLoader classLoader, final String s) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Enumeration>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    Enumeration<URL> enumeration;
                    if (classLoader == null) {
                        enumeration = ClassLoader.getSystemResources(s);
                    }
                    else {
                        enumeration = classLoader.getResources(s);
                    }
                    return enumeration;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
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
    
    static boolean doesFileExist(final File file) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return file.exists() ? Boolean.TRUE : Boolean.FALSE;
            }
        });
    }
}
