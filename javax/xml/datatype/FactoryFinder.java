package javax.xml.datatype;

import java.net.URL;
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
    private static final String CLASS_NAME = "javax.xml.datatype.FactoryFinder";
    private static boolean debug;
    private static Properties cacheProps;
    private static boolean firstTime;
    private static final int DEFAULT_LINE_LENGTH = 80;
    
    private FactoryFinder() {
    }
    
    private static void debugPrintln(final String s) {
        if (FactoryFinder.debug) {
            System.err.println("javax.xml.datatype.FactoryFinder:" + s);
        }
    }
    
    private static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (FactoryFinder.debug) {
            debugPrintln("Using context class loader: " + classLoader);
        }
        if (classLoader == null) {
            classLoader = FactoryFinder.class.getClassLoader();
            if (FactoryFinder.debug) {
                debugPrintln("Using the class loader of FactoryFinder: " + classLoader);
            }
        }
        return classLoader;
    }
    
    static Object newInstance(final String s, final ClassLoader classLoader) throws ConfigurationError {
        try {
            Class<?> clazz;
            if (classLoader == null) {
                clazz = Class.forName(s);
            }
            else {
                clazz = classLoader.loadClass(s);
            }
            if (FactoryFinder.debug) {
                debugPrintln("Loaded " + s + " from " + which(clazz));
            }
            return clazz.newInstance();
        }
        catch (final ClassNotFoundException ex) {
            throw new ConfigurationError("Provider " + s + " not found", ex);
        }
        catch (final Exception ex2) {
            throw new ConfigurationError("Provider " + s + " could not be instantiated: " + ex2, ex2);
        }
    }
    
    static Object find(final String s, final String s2) throws ConfigurationError {
        final ClassLoader classLoader = findClassLoader();
        try {
            final String systemProperty = SecuritySupport.getSystemProperty(s);
            if (systemProperty != null && systemProperty.length() > 0) {
                if (FactoryFinder.debug) {
                    debugPrintln("found " + systemProperty + " in the system property " + s);
                }
                return newInstance(systemProperty, classLoader);
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
                                debugPrintln("Read properties file " + file);
                            }
                            FactoryFinder.cacheProps.load(SecuritySupport.getFileInputStream(file));
                        }
                    }
                }
            }
            final String property = FactoryFinder.cacheProps.getProperty(s);
            if (FactoryFinder.debug) {
                debugPrintln("found " + property + " in $java.home/jaxp.properties");
            }
            if (property != null) {
                return newInstance(property, classLoader);
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
            debugPrintln("loaded from fallback value: " + s2);
        }
        return newInstance(s2, classLoader);
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
            if (FactoryFinder.debug) {
                debugPrintln("found in resource, value=" + line);
            }
            return newInstance(line, classLoader);
        }
        return null;
    }
    
    private static String which(final Class clazz) {
        try {
            final String string = clazz.getName().replace('.', '/') + ".class";
            final ClassLoader classLoader = clazz.getClassLoader();
            URL url;
            if (classLoader != null) {
                url = classLoader.getResource(string);
            }
            else {
                url = ClassLoader.getSystemResource(string);
            }
            if (url != null) {
                return url.toString();
            }
        }
        catch (final VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (final ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (final Throwable t) {
            if (FactoryFinder.debug) {
                t.printStackTrace();
            }
        }
        return "unknown location";
    }
    
    static {
        FactoryFinder.debug = false;
        FactoryFinder.cacheProps = new Properties();
        FactoryFinder.firstTime = true;
        try {
            final String systemProperty = SecuritySupport.getSystemProperty("jaxp.debug");
            FactoryFinder.debug = (systemProperty != null && !"false".equals(systemProperty));
        }
        catch (final Exception ex) {
            FactoryFinder.debug = false;
        }
    }
    
    static class ConfigurationError extends Error
    {
        private static final long serialVersionUID = -3644413026244211347L;
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
