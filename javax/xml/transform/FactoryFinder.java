package javax.xml.transform;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.File;
import java.util.Properties;

final class FactoryFinder
{
    private static boolean debug;
    private static Properties cacheProps;
    private static boolean firstTime;
    private static final int DEFAULT_LINE_LENGTH = 80;
    
    private FactoryFinder() {
    }
    
    private static void dPrint(final String s) {
        if (FactoryFinder.debug) {
            System.err.println("JAXP: " + s);
        }
    }
    
    static Object newInstance(final String s, ClassLoader classLoader, final boolean b) throws ConfigurationError {
        try {
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
                    classLoader = FactoryFinder.class.getClassLoader();
                    if (classLoader != null) {
                        clazz = classLoader.loadClass(s);
                    }
                    else {
                        clazz = Class.forName(s);
                    }
                }
            }
            final Object instance = clazz.newInstance();
            if (FactoryFinder.debug) {
                dPrint("created new instance of " + clazz + " using ClassLoader: " + classLoader);
            }
            return instance;
        }
        catch (final ClassNotFoundException ex2) {
            throw new ConfigurationError("Provider " + s + " not found", ex2);
        }
        catch (final Exception ex3) {
            throw new ConfigurationError("Provider " + s + " could not be instantiated: " + ex3, ex3);
        }
    }
    
    static Object find(final String s, final String s2) throws ConfigurationError {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = FactoryFinder.class.getClassLoader();
        }
        if (FactoryFinder.debug) {
            dPrint("find factoryId =" + s);
        }
        try {
            final String systemProperty = SecuritySupport.getSystemProperty(s);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (FactoryFinder.debug) {
                    dPrint("found system property, value=" + systemProperty);
                }
                return newInstance(systemProperty, classLoader, true);
            }
        }
        catch (final SecurityException ex) {}
        try {
            final String string = SecuritySupport.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            if (FactoryFinder.firstTime) {
                synchronized (FactoryFinder.cacheProps) {
                    if (FactoryFinder.firstTime) {
                        final File file = new File(string);
                        FactoryFinder.firstTime = false;
                        if (SecuritySupport.doesFileExist(file)) {
                            if (FactoryFinder.debug) {
                                dPrint("Read properties file " + file);
                            }
                            FactoryFinder.cacheProps.load(SecuritySupport.getFileInputStream(file));
                        }
                    }
                }
            }
            final String property = FactoryFinder.cacheProps.getProperty(s);
            if (property != null) {
                if (FactoryFinder.debug) {
                    dPrint("found in $java.home/jaxp.properties, value=" + property);
                }
                return newInstance(property, classLoader, true);
            }
        }
        catch (final Exception ex2) {
            if (FactoryFinder.debug) {
                ex2.printStackTrace();
            }
        }
        final Object jarServiceProvider = findJarServiceProvider(s);
        if (jarServiceProvider != null) {
            return jarServiceProvider;
        }
        if (s2 == null) {
            throw new ConfigurationError("Provider for " + s + " cannot be found", null);
        }
        if (FactoryFinder.debug) {
            dPrint("loaded from fallback value: " + s2);
        }
        return newInstance(s2, classLoader, true);
    }
    
    private static Object findJarServiceProvider(final String s) throws ConfigurationError {
        final String string = "META-INF/services/" + s;
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        InputStream inputStream;
        if (classLoader != null) {
            inputStream = SecuritySupport.getResourceAsStream(classLoader, string);
            if (inputStream == null) {
                classLoader = FactoryFinder.class.getClassLoader();
                inputStream = SecuritySupport.getResourceAsStream(classLoader, string);
            }
        }
        else {
            classLoader = FactoryFinder.class.getClassLoader();
            inputStream = SecuritySupport.getResourceAsStream(classLoader, string);
        }
        if (inputStream == null) {
            return null;
        }
        if (FactoryFinder.debug) {
            dPrint("found jar resource=" + string + " using ClassLoader: " + classLoader);
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
            if (FactoryFinder.debug) {
                dPrint("found in resource, value=" + line);
            }
            return newInstance(line, classLoader, false);
        }
        return null;
    }
    
    static {
        FactoryFinder.debug = false;
        FactoryFinder.cacheProps = new Properties();
        FactoryFinder.firstTime = true;
        try {
            final String systemProperty = SecuritySupport.getSystemProperty("jaxp.debug");
            FactoryFinder.debug = (systemProperty != null && !"false".equals(systemProperty));
        }
        catch (final SecurityException ex) {
            FactoryFinder.debug = false;
        }
    }
    
    static class ConfigurationError extends Error
    {
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
