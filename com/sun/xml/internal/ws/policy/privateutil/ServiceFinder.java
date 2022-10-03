package com.sun.xml.internal.ws.policy.privateutil;

import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Set;
import java.io.BufferedReader;
import java.net.URL;
import java.util.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

final class ServiceFinder<T> implements Iterable<T>
{
    private static final PolicyLogger LOGGER;
    private static final String prefix = "META-INF/services/";
    private final Class<T> serviceClass;
    private final ClassLoader classLoader;
    
    static <T> ServiceFinder<T> find(final Class<T> service, final ClassLoader loader) {
        if (null == service) {
            throw ServiceFinder.LOGGER.logSevereException(new NullPointerException(LocalizationMessages.WSP_0032_SERVICE_CAN_NOT_BE_NULL()));
        }
        return new ServiceFinder<T>(service, loader);
    }
    
    public static <T> ServiceFinder<T> find(final Class<T> service) {
        return find(service, Thread.currentThread().getContextClassLoader());
    }
    
    private ServiceFinder(final Class<T> service, final ClassLoader loader) {
        this.serviceClass = service;
        this.classLoader = loader;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new LazyIterator<T>((Class)this.serviceClass, this.classLoader);
    }
    
    public T[] toArray() {
        final List<T> result = new ArrayList<T>();
        for (final T t : this) {
            result.add(t);
        }
        return result.toArray((T[])Array.newInstance(this.serviceClass, result.size()));
    }
    
    private static void fail(final Class service, final String msg, final Throwable cause) throws ServiceConfigurationError {
        final ServiceConfigurationError sce = new ServiceConfigurationError(LocalizationMessages.WSP_0025_SPI_FAIL_SERVICE_MSG(service.getName(), msg));
        if (null != cause) {
            sce.initCause(cause);
        }
        throw ServiceFinder.LOGGER.logSevereException(sce);
    }
    
    private static void fail(final Class service, final URL u, final int line, final String msg, final Throwable cause) throws ServiceConfigurationError {
        fail(service, LocalizationMessages.WSP_0024_SPI_FAIL_SERVICE_URL_LINE_MSG(u, line, msg), cause);
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
                fail(service, u, lc, LocalizationMessages.WSP_0067_ILLEGAL_CFG_FILE_SYNTAX(), null);
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(service, u, lc, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(ln), null);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && cp != 46) {
                    fail(service, u, lc, LocalizationMessages.WSP_0066_ILLEGAL_PROVIDER_CLASSNAME(ln), null);
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
            fail(service, ": " + x, x);
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException y) {
                fail(service, ": " + y, y);
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
                fail(service, ": " + y2, y2);
            }
        }
        return names.iterator();
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(ServiceFinder.class);
    }
    
    private static class LazyIterator<T> implements Iterator<T>
    {
        Class<T> service;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<String> pending;
        Set<String> returned;
        String nextName;
        
        private LazyIterator(final Class<T> service, final ClassLoader loader) {
            this.configs = null;
            this.pending = null;
            this.returned = new TreeSet<String>();
            this.nextName = null;
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
                    fail(this.service, ": " + x, x);
                }
            }
            while (this.pending == null || !this.pending.hasNext()) {
                if (!this.configs.hasMoreElements()) {
                    return false;
                }
                this.pending = parse(this.service, this.configs.nextElement(), this.returned);
            }
            this.nextName = this.pending.next();
            return true;
        }
        
        @Override
        public T next() throws ServiceConfigurationError {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String cn = this.nextName;
            this.nextName = null;
            try {
                return this.service.cast(Class.forName(cn, true, this.loader).newInstance());
            }
            catch (final ClassNotFoundException x) {
                fail(this.service, LocalizationMessages.WSP_0027_SERVICE_PROVIDER_NOT_FOUND(cn), x);
            }
            catch (final Exception x2) {
                fail(this.service, LocalizationMessages.WSP_0028_SERVICE_PROVIDER_COULD_NOT_BE_INSTANTIATED(cn), x2);
            }
            return null;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
