package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.InputStream;

public final class ClasspathResourceLoader implements ResourceLoader
{
    private final Class<?> clazz;
    private final ClassLoader loader;
    
    public ClasspathResourceLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }
    
    public ClasspathResourceLoader(final ClassLoader loader) {
        this(null, loader);
    }
    
    public ClasspathResourceLoader(final Class<?> clazz) {
        this(clazz, clazz.getClassLoader());
    }
    
    private ClasspathResourceLoader(final Class<?> clazz, final ClassLoader loader) {
        this.clazz = clazz;
        this.loader = loader;
    }
    
    @Override
    public InputStream openResource(final String resource) throws IOException {
        final InputStream stream = (this.clazz != null) ? this.clazz.getResourceAsStream(resource) : this.loader.getResourceAsStream(resource);
        if (stream == null) {
            throw new IOException("Resource not found: " + resource);
        }
        return stream;
    }
    
    @Override
    public <T> Class<? extends T> findClass(final String cname, final Class<T> expectedType) {
        try {
            return Class.forName(cname, true, this.loader).asSubclass(expectedType);
        }
        catch (final Exception e) {
            throw new RuntimeException("Cannot load class: " + cname, e);
        }
    }
    
    @Override
    public <T> T newInstance(final String cname, final Class<T> expectedType) {
        final Class<? extends T> clazz = this.findClass(cname, expectedType);
        try {
            return (T)clazz.newInstance();
        }
        catch (final Exception e) {
            throw new RuntimeException("Cannot create instance: " + cname, e);
        }
    }
}
