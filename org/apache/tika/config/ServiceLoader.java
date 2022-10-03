package org.apache.tika.config;

import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.tika.exception.TikaConfigException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.net.URL;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.Map;

public class ServiceLoader
{
    private static final Map<Object, RankedService> services;
    private static final Pattern COMMENT;
    private static final Pattern WHITESPACE;
    private static volatile ClassLoader contextClassLoader;
    private final ClassLoader loader;
    private final LoadErrorHandler handler;
    private final InitializableProblemHandler initializableProblemHandler;
    private final boolean dynamic;
    
    public ServiceLoader(final ClassLoader loader, final LoadErrorHandler handler, final InitializableProblemHandler initializableProblemHandler, final boolean dynamic) {
        this.loader = loader;
        this.handler = handler;
        this.initializableProblemHandler = initializableProblemHandler;
        this.dynamic = dynamic;
    }
    
    public ServiceLoader(final ClassLoader loader, final LoadErrorHandler handler, final boolean dynamic) {
        this(loader, handler, InitializableProblemHandler.WARN, dynamic);
    }
    
    public ServiceLoader(final ClassLoader loader, final LoadErrorHandler handler) {
        this(loader, handler, false);
    }
    
    public ServiceLoader(final ClassLoader loader) {
        this(loader, Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE);
    }
    
    public ServiceLoader() {
        this(getContextClassLoader(), Boolean.getBoolean("org.apache.tika.service.error.warn") ? LoadErrorHandler.WARN : LoadErrorHandler.IGNORE, true);
    }
    
    static ClassLoader getContextClassLoader() {
        ClassLoader loader = ServiceLoader.contextClassLoader;
        if (loader == null) {
            loader = ServiceLoader.class.getClassLoader();
        }
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader;
    }
    
    public static void setContextClassLoader(final ClassLoader loader) {
        ServiceLoader.contextClassLoader = loader;
    }
    
    static void addService(final Object reference, final Object service, final int rank) {
        synchronized (ServiceLoader.services) {
            ServiceLoader.services.put(reference, new RankedService(service, rank));
        }
    }
    
    static Object removeService(final Object reference) {
        synchronized (ServiceLoader.services) {
            return ServiceLoader.services.remove(reference);
        }
    }
    
    public boolean isDynamic() {
        return this.dynamic;
    }
    
    public LoadErrorHandler getLoadErrorHandler() {
        return this.handler;
    }
    
    public InitializableProblemHandler getInitializableProblemHandler() {
        return this.initializableProblemHandler;
    }
    
    public InputStream getResourceAsStream(final String name) {
        if (this.loader != null) {
            return this.loader.getResourceAsStream(name);
        }
        return null;
    }
    
    public ClassLoader getLoader() {
        return this.loader;
    }
    
    public <T> Class<? extends T> getServiceClass(final Class<T> iface, final String name) throws ClassNotFoundException {
        if (this.loader == null) {
            throw new ClassNotFoundException("Service class " + name + " is not available");
        }
        final Class<?> klass = Class.forName(name, true, this.loader);
        if (klass.isInterface()) {
            throw new ClassNotFoundException("Service class " + name + " is an interface");
        }
        if (!iface.isAssignableFrom(klass)) {
            throw new ClassNotFoundException("Service class " + name + " does not implement " + iface.getName());
        }
        return (Class<? extends T>)klass;
    }
    
    public Enumeration<URL> findServiceResources(final String filePattern) {
        try {
            return this.loader.getResources(filePattern);
        }
        catch (final IOException ignore) {
            final List<URL> empty = Collections.emptyList();
            return Collections.enumeration(empty);
        }
    }
    
    public <T> List<T> loadServiceProviders(final Class<T> iface) {
        final List<T> providers = new ArrayList<T>();
        providers.addAll((Collection<? extends T>)this.loadDynamicServiceProviders((Class<Object>)iface));
        providers.addAll((Collection<? extends T>)this.loadStaticServiceProviders((Class<Object>)iface));
        return providers;
    }
    
    public <T> List<T> loadDynamicServiceProviders(final Class<T> iface) {
        if (this.dynamic) {
            synchronized (ServiceLoader.services) {
                final List<RankedService> list = new ArrayList<RankedService>(ServiceLoader.services.values());
                Collections.sort(list);
                final List<T> providers = new ArrayList<T>(list.size());
                for (final RankedService service : list) {
                    if (service.isInstanceOf(iface)) {
                        providers.add((T)service.service);
                    }
                }
                return providers;
            }
        }
        return new ArrayList<T>(0);
    }
    
    protected <T> List<String> identifyStaticServiceProviders(final Class<T> iface) {
        final List<String> names = new ArrayList<String>();
        if (this.loader != null) {
            final String serviceName = iface.getName();
            final Enumeration<URL> resources = this.findServiceResources("META-INF/services/" + serviceName);
            for (final URL resource : Collections.list(resources)) {
                try {
                    this.collectServiceClassNames(resource, names);
                }
                catch (final IOException e) {
                    this.handler.handleLoadError(serviceName, e);
                }
            }
        }
        return names;
    }
    
    public <T> List<T> loadStaticServiceProviders(final Class<T> iface) {
        return this.loadStaticServiceProviders(iface, Collections.EMPTY_SET);
    }
    
    public <T> List<T> loadStaticServiceProviders(final Class<T> iface, final Collection<Class<? extends T>> excludes) {
        final List<T> providers = new ArrayList<T>();
        if (this.loader != null) {
            final List<String> names = this.identifyStaticServiceProviders(iface);
            for (final String name : names) {
                try {
                    final Class<?> klass = this.loader.loadClass(name);
                    if (!iface.isAssignableFrom(klass)) {
                        throw new TikaConfigException("Class " + name + " is not of type: " + iface);
                    }
                    boolean shouldExclude = false;
                    for (final Class<? extends T> ex : excludes) {
                        if (ex.isAssignableFrom(klass)) {
                            shouldExclude = true;
                            break;
                        }
                    }
                    if (shouldExclude) {
                        continue;
                    }
                    final T instance = (T)klass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                    if (instance instanceof Initializable) {
                        ((Initializable)instance).initialize(Collections.EMPTY_MAP);
                        ((Initializable)instance).checkInitialization(this.initializableProblemHandler);
                    }
                    providers.add(instance);
                }
                catch (final Throwable t) {
                    this.handler.handleLoadError(name, t);
                }
            }
        }
        return providers;
    }
    
    private void collectServiceClassNames(final URL resource, final Collection<String> names) throws IOException {
        try (final InputStream stream = resource.openStream()) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = ServiceLoader.COMMENT.matcher(line).replaceFirst("");
                line = ServiceLoader.WHITESPACE.matcher(line).replaceAll("");
                if (line.length() > 0) {
                    names.add(line);
                }
            }
        }
    }
    
    static {
        services = new HashMap<Object, RankedService>();
        COMMENT = Pattern.compile("#.*");
        WHITESPACE = Pattern.compile("\\s+");
        ServiceLoader.contextClassLoader = null;
    }
    
    private static class RankedService implements Comparable<RankedService>
    {
        private final Object service;
        private final int rank;
        
        public RankedService(final Object service, final int rank) {
            this.service = service;
            this.rank = rank;
        }
        
        public boolean isInstanceOf(final Class<?> iface) {
            return iface.isAssignableFrom(this.service.getClass());
        }
        
        @Override
        public int compareTo(final RankedService that) {
            return that.rank - this.rank;
        }
    }
}
