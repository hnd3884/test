package org.apache.lucene.util;

import java.util.Iterator;
import java.util.Set;
import java.util.ServiceConfigurationError;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.Map;

public final class NamedSPILoader<S extends NamedSPI> implements Iterable<S>
{
    private volatile Map<String, S> services;
    private final Class<S> clazz;
    
    public NamedSPILoader(final Class<S> clazz) {
        this(clazz, Thread.currentThread().getContextClassLoader());
    }
    
    public NamedSPILoader(final Class<S> clazz, ClassLoader classloader) {
        this.services = Collections.emptyMap();
        this.clazz = clazz;
        final ClassLoader clazzClassloader = clazz.getClassLoader();
        if (classloader == null) {
            classloader = clazzClassloader;
        }
        if (clazzClassloader != null && !SPIClassIterator.isParentClassLoader(clazzClassloader, classloader)) {
            this.reload(clazzClassloader);
        }
        this.reload(classloader);
    }
    
    public void reload(final ClassLoader classloader) {
        Objects.requireNonNull(classloader, "classloader");
        final LinkedHashMap<String, S> services = new LinkedHashMap<String, S>((Map<? extends String, ? extends S>)this.services);
        final SPIClassIterator<S> loader = SPIClassIterator.get(this.clazz, classloader);
        while (loader.hasNext()) {
            final Class<? extends S> c = loader.next();
            try {
                final S service = (S)c.newInstance();
                final String name = service.getName();
                if (services.containsKey(name)) {
                    continue;
                }
                checkServiceName(name);
                services.put(name, service);
            }
            catch (final Exception e) {
                throw new ServiceConfigurationError("Cannot instantiate SPI class: " + c.getName(), e);
            }
        }
        this.services = Collections.unmodifiableMap((Map<? extends String, ? extends S>)services);
    }
    
    public static void checkServiceName(final String name) {
        if (name.length() >= 128) {
            throw new IllegalArgumentException("Illegal service name: '" + name + "' is too long (must be < 128 chars).");
        }
        for (int i = 0, len = name.length(); i < len; ++i) {
            final char c = name.charAt(i);
            if (!isLetterOrDigit(c)) {
                throw new IllegalArgumentException("Illegal service name: '" + name + "' must be simple ascii alphanumeric.");
            }
        }
    }
    
    private static boolean isLetterOrDigit(final char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9');
    }
    
    public S lookup(final String name) {
        final S service = this.services.get(name);
        if (service != null) {
            return service;
        }
        throw new IllegalArgumentException("An SPI class of type " + this.clazz.getName() + " with name '" + name + "' does not exist." + "  You need to add the corresponding JAR file supporting this SPI to your classpath." + "  The current classpath supports the following names: " + this.availableServices());
    }
    
    public Set<String> availableServices() {
        return this.services.keySet();
    }
    
    @Override
    public Iterator<S> iterator() {
        return this.services.values().iterator();
    }
    
    public interface NamedSPI
    {
        String getName();
    }
}
