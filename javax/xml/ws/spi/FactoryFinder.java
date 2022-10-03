package javax.xml.ws.spi;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.io.Closeable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.ws.WebServiceException;

class FactoryFinder
{
    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader";
    
    private static Object newInstance(final String className, final ClassLoader classLoader) {
        try {
            final Class spiClass = safeLoadClass(className, classLoader);
            return spiClass.newInstance();
        }
        catch (final ClassNotFoundException x) {
            throw new WebServiceException("Provider " + className + " not found", x);
        }
        catch (final Exception x2) {
            throw new WebServiceException("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static Object find(final String factoryId, final String fallbackClassName) {
        if (isOsgi()) {
            return lookupUsingOSGiServiceLoader(factoryId);
        }
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (final Exception x) {
            throw new WebServiceException(x.toString(), x);
        }
        final String serviceId = "META-INF/services/" + factoryId;
        BufferedReader rd = null;
        try {
            InputStream is;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            }
            else {
                is = classLoader.getResourceAsStream(serviceId);
            }
            if (is != null) {
                rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                final String factoryClassName = rd.readLine();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return newInstance(factoryClassName, classLoader);
                }
            }
        }
        catch (final Exception ex) {}
        finally {
            close(rd);
        }
        FileInputStream inStream = null;
        try {
            final String javah = System.getProperty("java.home");
            final String configFile = javah + File.separator + "lib" + File.separator + "jaxws.properties";
            final File f = new File(configFile);
            if (f.exists()) {
                final Properties props = new Properties();
                inStream = new FileInputStream(f);
                props.load(inStream);
                final String factoryClassName2 = props.getProperty(factoryId);
                return newInstance(factoryClassName2, classLoader);
            }
        }
        catch (final Exception ex2) {}
        finally {
            close(inStream);
        }
        try {
            final String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, classLoader);
            }
        }
        catch (final SecurityException ex3) {}
        if (fallbackClassName == null) {
            throw new WebServiceException("Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(fallbackClassName, classLoader);
    }
    
    private static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private static Class safeLoadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        try {
            final SecurityManager s = System.getSecurityManager();
            if (s != null) {
                final int i = className.lastIndexOf(46);
                if (i != -1) {
                    s.checkPackageAccess(className.substring(0, i));
                }
            }
            if (classLoader == null) {
                return Class.forName(className);
            }
            return classLoader.loadClass(className);
        }
        catch (final SecurityException se) {
            if ("com.sun.xml.internal.ws.spi.ProviderImpl".equals(className)) {
                return Class.forName(className);
            }
            throw se;
        }
    }
    
    private static boolean isOsgi() {
        try {
            Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
            return true;
        }
        catch (final ClassNotFoundException ex) {
            return false;
        }
    }
    
    private static Object lookupUsingOSGiServiceLoader(final String factoryId) {
        try {
            final Class serviceClass = Class.forName(factoryId);
            final Class[] args = { serviceClass };
            final Class target = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
            final Method m = target.getMethod("lookupProviderInstances", Class.class);
            final Iterator iter = ((Iterable)m.invoke(null, (Object[])args)).iterator();
            return iter.hasNext() ? iter.next() : null;
        }
        catch (final Exception ignored) {
            return null;
        }
    }
}
