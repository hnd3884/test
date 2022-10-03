package org.apache.xerces.impl.dv;

import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;

final class ObjectFactory
{
    private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
    private static final boolean DEBUG;
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static Properties fXercesProperties;
    private static long fLastModified;
    static /* synthetic */ Class class$org$apache$xerces$impl$dv$ObjectFactory;
    
    static Object createObject(final String s, final String s2) throws ConfigurationError {
        return createObject(s, null, s2);
    }
    
    static Object createObject(final String s, String string, final String s2) throws ConfigurationError {
        if (ObjectFactory.DEBUG) {
            debugPrintln("debug is on");
        }
        final ClassLoader classLoader = findClassLoader();
        try {
            final String systemProperty = SecuritySupport.getSystemProperty(s);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (ObjectFactory.DEBUG) {
                    debugPrintln("found system property, value=" + systemProperty);
                }
                return newInstance(systemProperty, classLoader, true);
            }
        }
        catch (final SecurityException ex) {}
        String s3 = null;
        if (string == null) {
            File file = null;
            boolean fileExists = false;
            try {
                string = SecuritySupport.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "xerces.properties";
                file = new File(string);
                fileExists = SecuritySupport.getFileExists(file);
            }
            catch (final SecurityException ex2) {
                ObjectFactory.fLastModified = -1L;
                ObjectFactory.fXercesProperties = null;
            }
            Class class$;
            Class class$org$apache$xerces$impl$dv$ObjectFactory;
            if (ObjectFactory.class$org$apache$xerces$impl$dv$ObjectFactory == null) {
                class$org$apache$xerces$impl$dv$ObjectFactory = (ObjectFactory.class$org$apache$xerces$impl$dv$ObjectFactory = (class$ = class$("org.apache.xerces.impl.dv.ObjectFactory")));
            }
            else {
                class$ = (class$org$apache$xerces$impl$dv$ObjectFactory = ObjectFactory.class$org$apache$xerces$impl$dv$ObjectFactory);
            }
            final Class clazz = class$org$apache$xerces$impl$dv$ObjectFactory;
            synchronized (class$) {
                boolean b = false;
                FileInputStream fileInputStream = null;
                try {
                    if (ObjectFactory.fLastModified >= 0L) {
                        if (fileExists && ObjectFactory.fLastModified < (ObjectFactory.fLastModified = SecuritySupport.getLastModified(file))) {
                            b = true;
                        }
                        else if (!fileExists) {
                            ObjectFactory.fLastModified = -1L;
                            ObjectFactory.fXercesProperties = null;
                        }
                    }
                    else if (fileExists) {
                        b = true;
                        ObjectFactory.fLastModified = SecuritySupport.getLastModified(file);
                    }
                    if (b) {
                        ObjectFactory.fXercesProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream(file);
                        ObjectFactory.fXercesProperties.load(fileInputStream);
                    }
                }
                catch (final Exception ex3) {
                    ObjectFactory.fXercesProperties = null;
                    ObjectFactory.fLastModified = -1L;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (final IOException ex4) {}
                    }
                }
            }
            if (ObjectFactory.fXercesProperties != null) {
                s3 = ObjectFactory.fXercesProperties.getProperty(s);
            }
        }
        else {
            FileInputStream fileInputStream2 = null;
            try {
                fileInputStream2 = SecuritySupport.getFileInputStream(new File(string));
                final Properties properties = new Properties();
                properties.load(fileInputStream2);
                s3 = properties.getProperty(s);
            }
            catch (final Exception ex5) {}
            finally {
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    }
                    catch (final IOException ex6) {}
                }
            }
        }
        if (s3 != null) {
            if (ObjectFactory.DEBUG) {
                debugPrintln("found in " + string + ", value=" + s3);
            }
            return newInstance(s3, classLoader, true);
        }
        final Object jarServiceProvider = findJarServiceProvider(s);
        if (jarServiceProvider != null) {
            return jarServiceProvider;
        }
        if (s2 == null) {
            throw new ConfigurationError("Provider for " + s + " cannot be found", null);
        }
        if (ObjectFactory.DEBUG) {
            debugPrintln("using fallback, value=" + s2);
        }
        return newInstance(s2, classLoader, true);
    }
    
    private static boolean isDebugEnabled() {
        try {
            final String systemProperty = SecuritySupport.getSystemProperty("xerces.debug");
            return systemProperty != null && !"false".equals(systemProperty);
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    private static void debugPrintln(final String s) {
        if (ObjectFactory.DEBUG) {
            System.err.println("XERCES: " + s);
        }
    }
    
    static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader contextClassLoader;
        ClassLoader classLoader2;
        ClassLoader classLoader;
        for (contextClassLoader = SecuritySupport.getContextClassLoader(), classLoader = (classLoader2 = SecuritySupport.getSystemClassLoader()); contextClassLoader != classLoader2; classLoader2 = SecuritySupport.getParentClassLoader(classLoader2)) {
            if (classLoader2 == null) {
                return contextClassLoader;
            }
        }
        for (ClassLoader classLoader3 = ObjectFactory.class.getClassLoader(), parentClassLoader = classLoader; classLoader3 != parentClassLoader; parentClassLoader = SecuritySupport.getParentClassLoader(parentClassLoader)) {
            if (parentClassLoader == null) {
                return classLoader3;
            }
        }
        return classLoader;
    }
    
    static Object newInstance(final String s, final ClassLoader classLoader, final boolean b) throws ConfigurationError {
        try {
            final Class providerClass = findProviderClass(s, classLoader, b);
            final Object instance = providerClass.newInstance();
            if (ObjectFactory.DEBUG) {
                debugPrintln("created new instance of " + providerClass + " using ClassLoader: " + classLoader);
            }
            return instance;
        }
        catch (final ClassNotFoundException ex) {
            throw new ConfigurationError("Provider " + s + " not found", ex);
        }
        catch (final Exception ex2) {
            throw new ConfigurationError("Provider " + s + " could not be instantiated: " + ex2, ex2);
        }
    }
    
    static Class findProviderClass(final String s, ClassLoader classLoader, final boolean b) throws ClassNotFoundException, ConfigurationError {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            final int lastIndex = s.lastIndexOf(46);
            String substring = s;
            if (lastIndex != -1) {
                substring = s.substring(0, lastIndex);
            }
            securityManager.checkPackageAccess(substring);
        }
        Class<?> clazz;
        if (classLoader == null) {
            clazz = Class.forName(s);
        }
        else {
            try {
                clazz = classLoader.loadClass(s);
            }
            catch (final ClassNotFoundException ex) {
                if (!b) {
                    throw ex;
                }
                final ClassLoader classLoader2 = ObjectFactory.class.getClassLoader();
                if (classLoader2 == null) {
                    clazz = Class.forName(s);
                }
                else {
                    if (classLoader == classLoader2) {
                        throw ex;
                    }
                    classLoader = classLoader2;
                    clazz = classLoader.loadClass(s);
                }
            }
        }
        return clazz;
    }
    
    private static Object findJarServiceProvider(final String s) throws ConfigurationError {
        final String string = "META-INF/services/" + s;
        ClassLoader classLoader = findClassLoader();
        InputStream inputStream = SecuritySupport.getResourceAsStream(classLoader, string);
        if (inputStream == null) {
            final ClassLoader classLoader2 = ObjectFactory.class.getClassLoader();
            if (classLoader != classLoader2) {
                classLoader = classLoader2;
                inputStream = SecuritySupport.getResourceAsStream(classLoader, string);
            }
        }
        if (inputStream == null) {
            return null;
        }
        if (ObjectFactory.DEBUG) {
            debugPrintln("found jar resource=" + string + " using ClassLoader: " + classLoader);
        }
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
        }
        catch (final UnsupportedEncodingException ex) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
        }
        String line = null;
        try {
            line = bufferedReader.readLine();
        }
        catch (final IOException ex2) {
            return null;
        }
        finally {
            try {
                bufferedReader.close();
            }
            catch (final IOException ex3) {}
        }
        if (line != null && !"".equals(line)) {
            if (ObjectFactory.DEBUG) {
                debugPrintln("found in resource, value=" + line);
            }
            return newInstance(line, classLoader, false);
        }
        return null;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError().initCause(ex);
        }
    }
    
    static {
        DEBUG = isDebugEnabled();
        ObjectFactory.fXercesProperties = null;
        ObjectFactory.fLastModified = -1L;
    }
    
    static final class ConfigurationError extends Error
    {
        static final long serialVersionUID = 8521878292694272124L;
        private Exception exception;
        
        ConfigurationError(final String s, final Exception exception) {
            super(s);
            this.exception = exception;
        }
        
        Exception getException() {
            return this.exception;
        }
    }
}
