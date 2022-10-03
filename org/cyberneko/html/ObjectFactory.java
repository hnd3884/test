package org.cyberneko.html;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;

class ObjectFactory
{
    private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static Properties fXercesProperties;
    private static long fLastModified;
    static /* synthetic */ Class class$org$cyberneko$html$ObjectFactory;
    
    static Object createObject(final String factoryId, final String fallbackClassName) throws ConfigurationError {
        return createObject(factoryId, null, fallbackClassName);
    }
    
    static Object createObject(final String factoryId, String propertiesFilename, final String fallbackClassName) throws ConfigurationError {
        final SecuritySupport ss = SecuritySupport.getInstance();
        final ClassLoader cl = findClassLoader();
        try {
            final String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, cl, true);
            }
        }
        catch (final SecurityException ex) {}
        String factoryClassName = null;
        if (propertiesFilename == null) {
            File propertiesFile = null;
            boolean propertiesFileExists = false;
            try {
                final String javah = ss.getSystemProperty("java.home");
                propertiesFilename = javah + File.separator + "lib" + File.separator + "xerces.properties";
                propertiesFile = new File(propertiesFilename);
                propertiesFileExists = ss.getFileExists(propertiesFile);
            }
            catch (final SecurityException e) {
                ObjectFactory.fLastModified = -1L;
                ObjectFactory.fXercesProperties = null;
            }
            Class class$;
            Class class$org$cyberneko$html$ObjectFactory;
            if (ObjectFactory.class$org$cyberneko$html$ObjectFactory == null) {
                class$org$cyberneko$html$ObjectFactory = (ObjectFactory.class$org$cyberneko$html$ObjectFactory = (class$ = class$("org.cyberneko.html.ObjectFactory")));
            }
            else {
                class$ = (class$org$cyberneko$html$ObjectFactory = ObjectFactory.class$org$cyberneko$html$ObjectFactory);
            }
            final Class clazz = class$org$cyberneko$html$ObjectFactory;
            synchronized (class$) {
                boolean loadProperties = false;
                try {
                    if (ObjectFactory.fLastModified >= 0L) {
                        if (propertiesFileExists && ObjectFactory.fLastModified < (ObjectFactory.fLastModified = ss.getLastModified(propertiesFile))) {
                            loadProperties = true;
                        }
                        else if (!propertiesFileExists) {
                            ObjectFactory.fLastModified = -1L;
                            ObjectFactory.fXercesProperties = null;
                        }
                    }
                    else if (propertiesFileExists) {
                        loadProperties = true;
                        ObjectFactory.fLastModified = ss.getLastModified(propertiesFile);
                    }
                    if (loadProperties) {
                        ObjectFactory.fXercesProperties = new Properties();
                        final FileInputStream fis = ss.getFileInputStream(propertiesFile);
                        ObjectFactory.fXercesProperties.load(fis);
                        fis.close();
                    }
                }
                catch (final Exception x) {
                    ObjectFactory.fXercesProperties = null;
                    ObjectFactory.fLastModified = -1L;
                }
            }
            if (ObjectFactory.fXercesProperties != null) {
                factoryClassName = ObjectFactory.fXercesProperties.getProperty(factoryId);
            }
        }
        else {
            try {
                final FileInputStream fis2 = ss.getFileInputStream(new File(propertiesFilename));
                final Properties props = new Properties();
                props.load(fis2);
                fis2.close();
                factoryClassName = props.getProperty(factoryId);
            }
            catch (final Exception ex2) {}
        }
        if (factoryClassName != null) {
            return newInstance(factoryClassName, cl, true);
        }
        final Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }
        if (fallbackClassName == null) {
            throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(fallbackClassName, cl, true);
    }
    
    private static void debugPrintln(final String msg) {
    }
    
    static ClassLoader findClassLoader() throws ConfigurationError {
        SecuritySupport ss;
        ClassLoader context;
        ClassLoader chain;
        ClassLoader system;
        for (ss = SecuritySupport.getInstance(), context = ss.getContextClassLoader(), system = (chain = ss.getSystemClassLoader()); context != chain; chain = ss.getParentClassLoader(chain)) {
            if (chain == null) {
                return context;
            }
        }
        ClassLoader current;
        for (current = ObjectFactory.class.getClassLoader(), chain = system; current != chain; chain = ss.getParentClassLoader(chain)) {
            if (chain == null) {
                return current;
            }
        }
        return system;
    }
    
    static Object newInstance(final String className, final ClassLoader cl, final boolean doFallback) throws ConfigurationError {
        try {
            final Class providerClass = findProviderClass(className, cl, doFallback);
            final Object instance = providerClass.newInstance();
            return instance;
        }
        catch (final ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + className + " not found", x);
        }
        catch (final Exception x2) {
            throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static Class findProviderClass(final String className, ClassLoader cl, final boolean doFallback) throws ClassNotFoundException, ConfigurationError {
        final SecurityManager security = System.getSecurityManager();
        try {
            if (security != null) {
                final int lastDot = className.lastIndexOf(".");
                String packageName = className;
                if (lastDot != -1) {
                    packageName = className.substring(0, lastDot);
                }
                security.checkPackageAccess(packageName);
            }
        }
        catch (final SecurityException e) {
            throw e;
        }
        Class providerClass;
        if (cl == null) {
            providerClass = Class.forName(className);
        }
        else {
            try {
                providerClass = cl.loadClass(className);
            }
            catch (final ClassNotFoundException x) {
                if (!doFallback) {
                    throw x;
                }
                final ClassLoader current = ObjectFactory.class.getClassLoader();
                if (current == null) {
                    providerClass = Class.forName(className);
                }
                else {
                    if (cl == current) {
                        throw x;
                    }
                    cl = current;
                    providerClass = cl.loadClass(className);
                }
            }
        }
        return providerClass;
    }
    
    private static Object findJarServiceProvider(final String factoryId) throws ConfigurationError {
        final SecuritySupport ss = SecuritySupport.getInstance();
        final String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;
        ClassLoader cl = findClassLoader();
        is = ss.getResourceAsStream(cl, serviceId);
        if (is == null) {
            final ClassLoader current = ObjectFactory.class.getClassLoader();
            if (cl != current) {
                cl = current;
                is = ss.getResourceAsStream(cl, serviceId);
            }
        }
        if (is == null) {
            return null;
        }
        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 80);
        }
        catch (final UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is), 80);
        }
        String factoryClassName = null;
        try {
            factoryClassName = rd.readLine();
            rd.close();
        }
        catch (final IOException x) {
            return null;
        }
        if (factoryClassName != null && !"".equals(factoryClassName)) {
            return newInstance(factoryClassName, cl, false);
        }
        return null;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (final ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        ObjectFactory.fXercesProperties = null;
        ObjectFactory.fLastModified = -1L;
    }
    
    static class ConfigurationError extends Error
    {
        private Exception exception;
        
        ConfigurationError(final String msg, final Exception x) {
            super(msg);
            this.exception = x;
        }
        
        Exception getException() {
            return this.exception;
        }
    }
}
