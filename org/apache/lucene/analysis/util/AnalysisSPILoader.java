package org.apache.lucene.analysis.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.ServiceConfigurationError;
import java.util.Arrays;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.apache.lucene.util.SPIClassIterator;
import java.util.Collections;
import java.util.Map;

public final class AnalysisSPILoader<S extends AbstractAnalysisFactory>
{
    private volatile Map<String, Class<? extends S>> services;
    private final Class<S> clazz;
    private final String[] suffixes;
    
    public AnalysisSPILoader(final Class<S> clazz) {
        this(clazz, new String[] { clazz.getSimpleName() });
    }
    
    public AnalysisSPILoader(final Class<S> clazz, final ClassLoader loader) {
        this(clazz, new String[] { clazz.getSimpleName() }, loader);
    }
    
    public AnalysisSPILoader(final Class<S> clazz, final String[] suffixes) {
        this(clazz, suffixes, Thread.currentThread().getContextClassLoader());
    }
    
    public AnalysisSPILoader(final Class<S> clazz, final String[] suffixes, ClassLoader classloader) {
        this.services = Collections.emptyMap();
        this.clazz = clazz;
        this.suffixes = suffixes;
        final ClassLoader clazzClassloader = clazz.getClassLoader();
        if (classloader == null) {
            classloader = clazzClassloader;
        }
        if (clazzClassloader != null && !SPIClassIterator.isParentClassLoader(clazzClassloader, classloader)) {
            this.reload(clazzClassloader);
        }
        this.reload(classloader);
    }
    
    public synchronized void reload(final ClassLoader classloader) {
        Objects.requireNonNull(classloader, "classloader");
        final LinkedHashMap<String, Class<? extends S>> services = new LinkedHashMap<String, Class<? extends S>>(this.services);
        final SPIClassIterator<S> loader = (SPIClassIterator<S>)SPIClassIterator.get((Class)this.clazz, classloader);
        while (loader.hasNext()) {
            final Class<? extends S> service = loader.next();
            final String clazzName = service.getSimpleName();
            String name = null;
            for (final String suffix : this.suffixes) {
                if (clazzName.endsWith(suffix)) {
                    name = clazzName.substring(0, clazzName.length() - suffix.length()).toLowerCase(Locale.ROOT);
                    break;
                }
            }
            if (name == null) {
                throw new ServiceConfigurationError("The class name " + service.getName() + " has wrong suffix, allowed are: " + Arrays.toString(this.suffixes));
            }
            if (services.containsKey(name)) {
                continue;
            }
            services.put(name, service);
        }
        this.services = Collections.unmodifiableMap((Map<? extends String, ? extends Class<? extends S>>)services);
    }
    
    public S newInstance(final String name, final Map<String, String> args) {
        final Class<? extends S> service = this.lookupClass(name);
        return newFactoryClassInstance((Class<S>)service, args);
    }
    
    public Class<? extends S> lookupClass(final String name) {
        final Class<? extends S> service = this.services.get(name.toLowerCase(Locale.ROOT));
        if (service != null) {
            return service;
        }
        throw new IllegalArgumentException("A SPI class of type " + this.clazz.getName() + " with name '" + name + "' does not exist. " + "You need to add the corresponding JAR file supporting this SPI to your classpath. " + "The current classpath supports the following names: " + this.availableServices());
    }
    
    public Set<String> availableServices() {
        return this.services.keySet();
    }
    
    public static <T extends AbstractAnalysisFactory> T newFactoryClassInstance(final Class<T> clazz, final Map<String, String> args) {
        try {
            return clazz.getConstructor(Map.class).newInstance(args);
        }
        catch (final InvocationTargetException ite) {
            final Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new RuntimeException("Unexpected checked exception while calling constructor of " + clazz.getName(), cause);
        }
        catch (final ReflectiveOperationException e) {
            throw new UnsupportedOperationException("Factory " + clazz.getName() + " cannot be instantiated. This is likely due to missing Map<String,String> constructor.", e);
        }
    }
}
