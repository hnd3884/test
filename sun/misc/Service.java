package sun.misc;

import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.Enumeration;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.io.BufferedReader;
import java.net.URL;

public final class Service<S>
{
    private static final String prefix = "META-INF/services/";
    
    private Service() {
    }
    
    private static void fail(final Class<?> clazz, final String s, final Throwable t) throws ServiceConfigurationError {
        final ServiceConfigurationError serviceConfigurationError = new ServiceConfigurationError(clazz.getName() + ": " + s);
        serviceConfigurationError.initCause(t);
        throw serviceConfigurationError;
    }
    
    private static void fail(final Class<?> clazz, final String s) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(clazz.getName() + ": " + s);
    }
    
    private static void fail(final Class<?> clazz, final URL url, final int n, final String s) throws ServiceConfigurationError {
        fail(clazz, url + ":" + n + ": " + s);
    }
    
    private static int parseLine(final Class<?> clazz, final URL url, final BufferedReader bufferedReader, final int n, final List<String> list, final Set<String> set) throws IOException, ServiceConfigurationError {
        String s = bufferedReader.readLine();
        if (s == null) {
            return -1;
        }
        final int index = s.indexOf(35);
        if (index >= 0) {
            s = s.substring(0, index);
        }
        final String trim = s.trim();
        final int length = trim.length();
        if (length != 0) {
            if (trim.indexOf(32) >= 0 || trim.indexOf(9) >= 0) {
                fail(clazz, url, n, "Illegal configuration-file syntax");
            }
            final int codePoint = trim.codePointAt(0);
            if (!Character.isJavaIdentifierStart(codePoint)) {
                fail(clazz, url, n, "Illegal provider-class name: " + trim);
            }
            int codePoint2;
            for (int i = Character.charCount(codePoint); i < length; i += Character.charCount(codePoint2)) {
                codePoint2 = trim.codePointAt(i);
                if (!Character.isJavaIdentifierPart(codePoint2) && codePoint2 != 46) {
                    fail(clazz, url, n, "Illegal provider-class name: " + trim);
                }
            }
            if (!set.contains(trim)) {
                list.add(trim);
                set.add(trim);
            }
        }
        return n + 1;
    }
    
    private static Iterator<String> parse(final Class<?> clazz, final URL url, final Set<String> set) throws ServiceConfigurationError {
        InputStream openStream = null;
        BufferedReader bufferedReader = null;
        final ArrayList list = new ArrayList();
        try {
            openStream = url.openStream();
            bufferedReader = new BufferedReader(new InputStreamReader(openStream, "utf-8"));
            int line = 1;
            while ((line = parseLine(clazz, url, bufferedReader, line, list, set)) >= 0) {}
        }
        catch (final IOException ex) {
            fail(clazz, ": " + ex);
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (openStream != null) {
                    openStream.close();
                }
            }
            catch (final IOException ex2) {
                fail(clazz, ": " + ex2);
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (openStream != null) {
                    openStream.close();
                }
            }
            catch (final IOException ex3) {
                fail(clazz, ": " + ex3);
            }
        }
        return list.iterator();
    }
    
    public static <S> Iterator<S> providers(final Class<S> clazz, final ClassLoader classLoader) throws ServiceConfigurationError {
        return new LazyIterator<S>((Class)clazz, classLoader);
    }
    
    public static <S> Iterator<S> providers(final Class<S> clazz) throws ServiceConfigurationError {
        return providers(clazz, Thread.currentThread().getContextClassLoader());
    }
    
    public static <S> Iterator<S> installedProviders(final Class<S> clazz) throws ServiceConfigurationError {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        ClassLoader classLoader2 = null;
        while (classLoader != null) {
            classLoader2 = classLoader;
            classLoader = classLoader.getParent();
        }
        return providers(clazz, classLoader2);
    }
    
    private static class LazyIterator<S> implements Iterator<S>
    {
        Class<S> service;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<String> pending;
        Set<String> returned;
        String nextName;
        
        private LazyIterator(final Class<S> service, final ClassLoader loader) {
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
                    final String string = "META-INF/services/" + this.service.getName();
                    if (this.loader == null) {
                        this.configs = ClassLoader.getSystemResources(string);
                    }
                    else {
                        this.configs = this.loader.getResources(string);
                    }
                }
                catch (final IOException ex) {
                    fail(this.service, ": " + ex);
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
        public S next() throws ServiceConfigurationError {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final String nextName = this.nextName;
            this.nextName = null;
            Class<?> forName = null;
            try {
                forName = Class.forName(nextName, false, this.loader);
            }
            catch (final ClassNotFoundException ex) {
                fail(this.service, "Provider " + nextName + " not found");
            }
            if (!this.service.isAssignableFrom(forName)) {
                fail(this.service, "Provider " + nextName + " not a subtype");
            }
            try {
                return this.service.cast(forName.newInstance());
            }
            catch (final Throwable t) {
                fail(this.service, "Provider " + nextName + " could not be instantiated", t);
                return null;
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
