package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.HK2Loader;

public class HK2LoaderImpl implements HK2Loader
{
    private final ClassLoader loader;
    
    public HK2LoaderImpl() {
        this(ClassLoader.getSystemClassLoader());
    }
    
    public HK2LoaderImpl(final ClassLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException();
        }
        this.loader = loader;
    }
    
    @Override
    public Class<?> loadClass(final String className) throws MultiException {
        try {
            return this.loader.loadClass(className);
        }
        catch (final Exception e) {
            throw new MultiException(e);
        }
    }
    
    @Override
    public String toString() {
        return "HK2LoaderImpl(" + this.loader + ")";
    }
}
