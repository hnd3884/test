package javax.xml.soap;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

class FactoryFinder
{
    private static Object newInstance(final String className, final ClassLoader classLoader) throws SOAPException {
        try {
            final Class spiClass = safeLoadClass(className, classLoader);
            return spiClass.newInstance();
        }
        catch (final ClassNotFoundException x) {
            throw new SOAPException("Provider " + className + " not found", x);
        }
        catch (final Exception x2) {
            throw new SOAPException("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static Object find(final String factoryId) throws SOAPException {
        return find(factoryId, null, false);
    }
    
    static Object find(final String factoryId, final String fallbackClassName) throws SOAPException {
        return find(factoryId, fallbackClassName, true);
    }
    
    static Object find(final String factoryId, final String defaultClassName, final boolean tryFallback) throws SOAPException {
        ClassLoader classLoader;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        catch (final Exception x) {
            throw new SOAPException(x.toString(), x);
        }
        try {
            final String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, classLoader);
            }
        }
        catch (final SecurityException ex) {}
        try {
            final String javah = System.getProperty("java.home");
            final String configFile = javah + File.separator + "lib" + File.separator + "jaxm.properties";
            final File f = new File(configFile);
            if (f.exists()) {
                final Properties props = new Properties();
                props.load(new FileInputStream(f));
                final String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryClassName, classLoader);
            }
        }
        catch (final Exception ex2) {}
        final String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is = null;
            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            }
            else {
                is = classLoader.getResourceAsStream(serviceId);
            }
            if (is != null) {
                final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                final String factoryClassName2 = rd.readLine();
                rd.close();
                if (factoryClassName2 != null && !"".equals(factoryClassName2)) {
                    return newInstance(factoryClassName2, classLoader);
                }
            }
        }
        catch (final Exception ex3) {}
        if (!tryFallback) {
            return null;
        }
        if (defaultClassName == null) {
            throw new SOAPException("Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(defaultClassName, classLoader);
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
            if (isDefaultImplementation(className)) {
                return Class.forName(className);
            }
            throw se;
        }
    }
    
    private static boolean isDefaultImplementation(final String className) {
        return "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl".equals(className) || "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl".equals(className) || "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory".equals(className) || "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl".equals(className);
    }
}
