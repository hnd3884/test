package javax.activation;

import java.util.Enumeration;
import java.util.Vector;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

class SecuritySupport12 extends SecuritySupport
{
    public ClassLoader getContextClassLoader() {
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
    
    public InputStream getResourceAsStream(final Class clazz, final String s) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                private final /* synthetic */ Class val$c = val$c;
                
                public Object run() throws IOException {
                    return this.val$c.getResourceAsStream(s);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public URL[] getResources(final ClassLoader classLoader, final String s) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction() {
            private final /* synthetic */ ClassLoader val$cl = val$cl;
            
            public Object run() {
                Object[] array = null;
                try {
                    final Vector vector = new Vector();
                    final Enumeration<URL> resources = this.val$cl.getResources(s);
                    while (resources != null && resources.hasMoreElements()) {
                        final URL url = resources.nextElement();
                        if (url != null) {
                            vector.addElement(url);
                        }
                    }
                    if (vector.size() > 0) {
                        array = new URL[vector.size()];
                        vector.copyInto(array);
                    }
                }
                catch (final IOException ex) {}
                catch (final SecurityException ex2) {}
                return array;
            }
        });
    }
    
    public URL[] getSystemResources(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<URL[]>)new PrivilegedAction() {
            private final /* synthetic */ String val$name = val$name;
            
            public Object run() {
                Object[] array = null;
                try {
                    final Vector vector = new Vector();
                    final Enumeration<URL> systemResources = ClassLoader.getSystemResources(this.val$name);
                    while (systemResources != null && systemResources.hasMoreElements()) {
                        final URL url = systemResources.nextElement();
                        if (url != null) {
                            vector.addElement(url);
                        }
                    }
                    if (vector.size() > 0) {
                        array = new URL[vector.size()];
                        vector.copyInto(array);
                    }
                }
                catch (final IOException ex) {}
                catch (final SecurityException ex2) {}
                return array;
            }
        });
    }
    
    public InputStream openStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                private final /* synthetic */ URL val$url = val$url;
                
                public Object run() throws IOException {
                    return this.val$url.openStream();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
}
