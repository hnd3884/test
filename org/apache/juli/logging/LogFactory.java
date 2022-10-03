package org.apache.juli.logging;

import java.util.logging.LogManager;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.nio.file.FileSystems;
import java.lang.reflect.Constructor;

public class LogFactory
{
    private static final LogFactory singleton;
    private final Constructor<? extends Log> discoveredLogConstructor;
    
    private LogFactory() {
        FileSystems.getDefault();
        final ServiceLoader<Log> logLoader = ServiceLoader.load(Log.class);
        Constructor<? extends Log> m = null;
        final Iterator i$ = logLoader.iterator();
        if (i$.hasNext()) {
            final Log log = i$.next();
            final Class<? extends Log> c = log.getClass();
            try {
                m = c.getConstructor(String.class);
            }
            catch (final NoSuchMethodException | SecurityException e) {
                throw new Error(e);
            }
        }
        this.discoveredLogConstructor = m;
    }
    
    public Log getInstance(final String name) throws LogConfigurationException {
        if (this.discoveredLogConstructor == null) {
            return DirectJDKLog.getInstance(name);
        }
        try {
            return (Log)this.discoveredLogConstructor.newInstance(name);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException e) {
            throw new LogConfigurationException(e);
        }
    }
    
    public Log getInstance(final Class<?> clazz) throws LogConfigurationException {
        return this.getInstance(clazz.getName());
    }
    
    public static LogFactory getFactory() throws LogConfigurationException {
        return LogFactory.singleton;
    }
    
    public static Log getLog(final Class<?> clazz) throws LogConfigurationException {
        return getFactory().getInstance(clazz);
    }
    
    public static Log getLog(final String name) throws LogConfigurationException {
        return getFactory().getInstance(name);
    }
    
    public static void release(final ClassLoader classLoader) {
        if (!LogManager.getLogManager().getClass().getName().equals("java.util.logging.LogManager")) {
            LogManager.getLogManager().reset();
        }
    }
    
    static {
        singleton = new LogFactory();
    }
}
