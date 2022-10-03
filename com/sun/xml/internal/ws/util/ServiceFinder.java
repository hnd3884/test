package com.sun.xml.internal.ws.util;

import java.util.TreeSet;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Set;
import java.io.BufferedReader;
import java.net.URL;
import java.util.List;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.Component;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.ComponentEx;
import com.sun.istack.internal.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.WeakHashMap;

public final class ServiceFinder<T> implements Iterable<T>
{
    private static final String prefix = "META-INF/services/";
    private static WeakHashMap<ClassLoader, ConcurrentHashMap<String, ServiceName[]>> serviceNameCache;
    private final Class<T> serviceClass;
    @Nullable
    private final ClassLoader classLoader;
    @Nullable
    private final ComponentEx component;
    
    public static <T> ServiceFinder<T> find(@NotNull final Class<T> service, @Nullable final ClassLoader loader, final Component component) {
        return new ServiceFinder<T>(service, loader, component);
    }
    
    public static <T> ServiceFinder<T> find(@NotNull final Class<T> service, final Component component) {
        return find(service, Thread.currentThread().getContextClassLoader(), component);
    }
    
    public static <T> ServiceFinder<T> find(@NotNull final Class<T> service, @Nullable final ClassLoader loader) {
        return find(service, loader, ContainerResolver.getInstance().getContainer());
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service) {
        return find(service, Thread.currentThread().getContextClassLoader());
    }
    
    private ServiceFinder(final Class<T> service, final ClassLoader loader, final Component component) {
        this.serviceClass = service;
        this.classLoader = loader;
        this.component = getComponentEx(component);
    }
    
    private static ServiceName[] serviceClassNames(final Class serviceClass, final ClassLoader classLoader) {
        final ArrayList<ServiceName> l = new ArrayList<ServiceName>();
        final Iterator<ServiceName> it = new ServiceNameIterator(serviceClass, classLoader);
        while (it.hasNext()) {
            l.add(it.next());
        }
        return l.toArray(new ServiceName[l.size()]);
    }
    
    @Override
    public Iterator<T> iterator() {
        final Iterator<T> it = new LazyIterator<T>((Class)this.serviceClass, this.classLoader);
        return (this.component != null) ? new CompositeIterator<T>((Iterator<T>[])new Iterator[] { this.component.getIterableSPI(this.serviceClass).iterator(), it }) : it;
    }
    
    public T[] toArray() {
        final List<T> result = new ArrayList<T>();
        for (final T t : this) {
            result.add(t);
        }
        return result.toArray((T[])Array.newInstance(this.serviceClass, result.size()));
    }
    
    private static void fail(final Class service, final String msg, final Throwable cause) throws ServiceConfigurationError {
        final ServiceConfigurationError sce = new ServiceConfigurationError(service.getName() + ": " + msg);
        sce.initCause(cause);
        throw sce;
    }
    
    private static void fail(final Class service, final String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }
    
    private static void fail(final Class service, final URL u, final int line, final String msg) throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }
    
    private static int parseLine(final Class service, final URL u, final BufferedReader r, final int lc, final List<String> names, final Set<String> returned) throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        final int ci = ln.indexOf(35);
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        final int n = ln.length();
        if (n != 0) {
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
                fail(service, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                    fail(service, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            if (!returned.contains(ln)) {
                names.add(ln);
                returned.add(ln);
            }
        }
        return lc + 1;
    }
    
    private static Iterator<String> parse(final Class service, final URL u, final Set<String> returned) throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        final ArrayList<String> names = new ArrayList<String>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names, returned)) >= 0) {}
        }
        catch (final IOException x) {
            fail(service, ": " + x);
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException y) {
                fail(service, ": " + y);
            }
        }
        finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException y2) {
                fail(service, ": " + y2);
            }
        }
        return names.iterator();
    }
    
    private static ComponentEx getComponentEx(final Component component) {
        if (component instanceof ComponentEx) {
            return (ComponentEx)component;
        }
        return (component != null) ? new ComponentExWrapper(component) : null;
    }
    
    static {
        ServiceFinder.serviceNameCache = new WeakHashMap<ClassLoader, ConcurrentHashMap<String, ServiceName[]>>();
    }
    
    private static class ServiceName
    {
        final String className;
        final URL config;
        
        public ServiceName(final String className, final URL config) {
            this.className = className;
            this.config = config;
        }
    }
    
    private static class ComponentExWrapper implements ComponentEx
    {
        private final Component component;
        
        public ComponentExWrapper(final Component component) {
            this.component = component;
        }
        
        @Override
        public <S> S getSPI(final Class<S> spiType) {
            return this.component.getSPI(spiType);
        }
        
        @Override
        public <S> Iterable<S> getIterableSPI(final Class<S> spiType) {
            final S item = this.getSPI(spiType);
            if (item != null) {
                final Collection<S> c = Collections.singletonList(item);
                return c;
            }
            return (Iterable<S>)Collections.emptySet();
        }
    }
    
    private static class CompositeIterator<T> implements Iterator<T>
    {
        private final Iterator<Iterator<T>> it;
        private Iterator<T> current;
        
        public CompositeIterator(final Iterator<T>... iterators) {
            this.current = null;
            this.it = Arrays.asList(iterators).iterator();
        }
        
        @Override
        public boolean hasNext() {
            if (this.current != null && this.current.hasNext()) {
                return true;
            }
            while (this.it.hasNext()) {
                this.current = this.it.next();
                if (this.current.hasNext()) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.current.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class ServiceNameIterator implements Iterator<ServiceName>
    {
        Class service;
        @Nullable
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<String> pending;
        Set<String> returned;
        String nextName;
        URL currentConfig;
        
        private ServiceNameIterator(final Class service, final ClassLoader loader) {
            this.configs = null;
            this.pending = null;
            this.returned = new TreeSet<String>();
            this.nextName = null;
            this.currentConfig = null;
            this.service = service;
            this.loader = loader;
        }
        
        @Override
        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            if (this.configs == null) {
                try {
                    final String fullName = "META-INF/services/" + this.service.getName();
                    if (this.loader == null) {
                        this.configs = ClassLoader.getSystemResources(fullName);
                    }
                    else {
                        this.configs = this.loader.getResources(fullName);
                    }
                }
                catch (final IOException x) {
                    fail(this.service, ": " + x);
                }
            }
            while (this.pending == null || !this.pending.hasNext()) {
                if (!this.configs.hasMoreElements()) {
                    return false;
                }
                this.currentConfig = this.configs.nextElement();
                this.pending = parse(this.service, this.currentConfig, this.returned);
            }
            this.nextName = this.pending.next();
            return true;
        }
        
        @Override
        public ServiceName next() throws ServiceConfigurationError {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String cn = this.nextName;
            this.nextName = null;
            return new ServiceName(cn, this.currentConfig);
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class LazyIterator<T> implements Iterator<T>
    {
        Class<T> service;
        @Nullable
        ClassLoader loader;
        ServiceName[] names;
        int index;
        
        private LazyIterator(final Class<T> service, final ClassLoader loader) {
            this.service = service;
            this.loader = loader;
            this.names = null;
            this.index = 0;
        }
        
        @Override
        public boolean hasNext() {
            if (this.names == null) {
                ConcurrentHashMap<String, ServiceName[]> nameMap = null;
                synchronized (ServiceFinder.serviceNameCache) {
                    nameMap = ServiceFinder.serviceNameCache.get(this.loader);
                }
                this.names = (ServiceName[])((nameMap != null) ? ((ServiceName[])nameMap.get(this.service.getName())) : null);
                if (this.names == null) {
                    this.names = serviceClassNames(this.service, this.loader);
                    if (nameMap == null) {
                        nameMap = new ConcurrentHashMap<String, ServiceName[]>();
                    }
                    nameMap.put(this.service.getName(), this.names);
                    synchronized (ServiceFinder.serviceNameCache) {
                        ServiceFinder.serviceNameCache.put(this.loader, nameMap);
                    }
                }
            }
            return this.index < this.names.length;
        }
        
        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final ServiceName sn = this.names[this.index++];
            final String cn = sn.className;
            final URL currentConfig = sn.config;
            try {
                return this.service.cast(Class.forName(cn, true, this.loader).newInstance());
            }
            catch (final ClassNotFoundException x) {
                fail(this.service, "Provider " + cn + " is specified in " + currentConfig + " but not found");
            }
            catch (final Exception x2) {
                fail(this.service, "Provider " + cn + " is specified in " + currentConfig + "but could not be instantiated: " + x2, x2);
            }
            return null;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
