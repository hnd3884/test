package java.rmi.server;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.rmi.server.LoaderHandler;
import java.security.Permission;
import java.net.URL;
import java.net.MalformedURLException;

public class RMIClassLoader
{
    private static final RMIClassLoaderSpi defaultProvider;
    private static final RMIClassLoaderSpi provider;
    
    private RMIClassLoader() {
    }
    
    @Deprecated
    public static Class<?> loadClass(final String s) throws MalformedURLException, ClassNotFoundException {
        return loadClass((String)null, s);
    }
    
    public static Class<?> loadClass(final URL url, final String s) throws MalformedURLException, ClassNotFoundException {
        return RMIClassLoader.provider.loadClass((url != null) ? url.toString() : null, s, null);
    }
    
    public static Class<?> loadClass(final String s, final String s2) throws MalformedURLException, ClassNotFoundException {
        return RMIClassLoader.provider.loadClass(s, s2, null);
    }
    
    public static Class<?> loadClass(final String s, final String s2, final ClassLoader classLoader) throws MalformedURLException, ClassNotFoundException {
        return RMIClassLoader.provider.loadClass(s, s2, classLoader);
    }
    
    public static Class<?> loadProxyClass(final String s, final String[] array, final ClassLoader classLoader) throws ClassNotFoundException, MalformedURLException {
        return RMIClassLoader.provider.loadProxyClass(s, array, classLoader);
    }
    
    public static ClassLoader getClassLoader(final String s) throws MalformedURLException, SecurityException {
        return RMIClassLoader.provider.getClassLoader(s);
    }
    
    public static String getClassAnnotation(final Class<?> clazz) {
        return RMIClassLoader.provider.getClassAnnotation(clazz);
    }
    
    public static RMIClassLoaderSpi getDefaultProviderInstance() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new RuntimePermission("setFactory"));
        }
        return RMIClassLoader.defaultProvider;
    }
    
    @Deprecated
    public static Object getSecurityContext(final ClassLoader classLoader) {
        return LoaderHandler.getSecurityContext(classLoader);
    }
    
    private static RMIClassLoaderSpi newDefaultProviderInstance() {
        return new RMIClassLoaderSpi() {
            @Override
            public Class<?> loadClass(final String s, final String s2, final ClassLoader classLoader) throws MalformedURLException, ClassNotFoundException {
                return LoaderHandler.loadClass(s, s2, classLoader);
            }
            
            @Override
            public Class<?> loadProxyClass(final String s, final String[] array, final ClassLoader classLoader) throws MalformedURLException, ClassNotFoundException {
                return LoaderHandler.loadProxyClass(s, array, classLoader);
            }
            
            @Override
            public ClassLoader getClassLoader(final String s) throws MalformedURLException {
                return LoaderHandler.getClassLoader(s);
            }
            
            @Override
            public String getClassAnnotation(final Class<?> clazz) {
                return LoaderHandler.getClassAnnotation(clazz);
            }
        };
    }
    
    private static RMIClassLoaderSpi initializeProvider() {
        final String property = System.getProperty("java.rmi.server.RMIClassLoaderSpi");
        if (property != null) {
            if (property.equals("default")) {
                return RMIClassLoader.defaultProvider;
            }
            try {
                return (RMIClassLoaderSpi)Class.forName(property, false, ClassLoader.getSystemClassLoader()).asSubclass(RMIClassLoaderSpi.class).newInstance();
            }
            catch (final ClassNotFoundException ex) {
                throw new NoClassDefFoundError(ex.getMessage());
            }
            catch (final IllegalAccessException ex2) {
                throw new IllegalAccessError(ex2.getMessage());
            }
            catch (final InstantiationException ex3) {
                throw new InstantiationError(ex3.getMessage());
            }
            catch (final ClassCastException ex4) {
                final LinkageError linkageError = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
                linkageError.initCause(ex4);
                throw linkageError;
            }
        }
        final Iterator<RMIClassLoaderSpi> iterator = ServiceLoader.load(RMIClassLoaderSpi.class, ClassLoader.getSystemClassLoader()).iterator();
        if (iterator.hasNext()) {
            try {
                return iterator.next();
            }
            catch (final ClassCastException ex5) {
                final LinkageError linkageError2 = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
                linkageError2.initCause(ex5);
                throw linkageError2;
            }
        }
        return RMIClassLoader.defaultProvider;
    }
    
    static {
        defaultProvider = newDefaultProviderInstance();
        provider = AccessController.doPrivileged((PrivilegedAction<RMIClassLoaderSpi>)new PrivilegedAction<RMIClassLoaderSpi>() {
            @Override
            public RMIClassLoaderSpi run() {
                return initializeProvider();
            }
        });
    }
}
