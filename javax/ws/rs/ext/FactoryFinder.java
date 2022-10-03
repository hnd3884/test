package javax.ws.rs.ext;

import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;

final class FactoryFinder
{
    private static final Logger LOGGER;
    
    private FactoryFinder() {
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(() -> {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread().getContextClassLoader();
            }
            catch (final SecurityException ex) {
                FactoryFinder.LOGGER.log(Level.WARNING, "Unable to get context classloader instance.", ex);
            }
            return cl;
        });
    }
    
    private static Object newInstance(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class<?> spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            }
            else {
                try {
                    spiClass = Class.forName(className, false, classLoader);
                }
                catch (final ClassNotFoundException ex) {
                    FactoryFinder.LOGGER.log(Level.FINE, "Unable to load provider class " + className + " using custom classloader " + classLoader.getClass().getName() + " trying again with current classloader.", ex);
                    spiClass = Class.forName(className);
                }
            }
            return spiClass.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (final ClassNotFoundException x) {
            throw x;
        }
        catch (final Exception x2) {
            throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + x2, x2);
        }
    }
    
    static <T> Object find(final String factoryId, final String fallbackClassName, final Class<T> service) throws ClassNotFoundException {
        final ClassLoader classLoader = getContextClassLoader();
        try {
            final Iterator<T> iterator = ServiceLoader.load(service, getContextClassLoader()).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        catch (final Exception | ServiceConfigurationError ex) {
            FactoryFinder.LOGGER.log(Level.FINER, "Failed to load service " + factoryId + ".", ex);
        }
        try {
            final Iterator<T> iterator = ServiceLoader.load(service, FactoryFinder.class.getClassLoader()).iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        catch (final Exception | ServiceConfigurationError ex) {
            FactoryFinder.LOGGER.log(Level.FINER, "Failed to load service " + factoryId + ".", ex);
        }
        FileInputStream inputStream = null;
        String configFile = null;
        try {
            final String javah = System.getProperty("java.home");
            configFile = javah + File.separator + "lib" + File.separator + "jaxrs.properties";
            final File f = new File(configFile);
            if (f.exists()) {
                final Properties props = new Properties();
                inputStream = new FileInputStream(f);
                props.load(inputStream);
                final String factoryClassName = props.getProperty(factoryId);
                return newInstance(factoryClassName, classLoader);
            }
        }
        catch (final Exception ex2) {
            FactoryFinder.LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from $java.home/lib/jaxrs.properties", ex2);
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex3) {
                    FactoryFinder.LOGGER.log(Level.FINER, String.format("Error closing %s file.", configFile), ex3);
                }
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex4) {
                    FactoryFinder.LOGGER.log(Level.FINER, String.format("Error closing %s file.", configFile), ex4);
                }
            }
        }
        try {
            final String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return newInstance(systemProp, classLoader);
            }
        }
        catch (final SecurityException se) {
            FactoryFinder.LOGGER.log(Level.FINER, "Failed to load service " + factoryId + " from a system property", se);
        }
        if (fallbackClassName == null) {
            throw new ClassNotFoundException("Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(fallbackClassName, classLoader);
    }
    
    static {
        LOGGER = Logger.getLogger(FactoryFinder.class.getName());
    }
}
