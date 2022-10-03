package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

final class SecureClassLoaderRepository implements ClassLoaderRepository
{
    private final ClassLoaderRepository clr;
    
    public SecureClassLoaderRepository(final ClassLoaderRepository clr) {
        this.clr = clr;
    }
    
    @Override
    public final Class<?> loadClass(final String s) throws ClassNotFoundException {
        return this.clr.loadClass(s);
    }
    
    @Override
    public final Class<?> loadClassWithout(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        return this.clr.loadClassWithout(classLoader, s);
    }
    
    @Override
    public final Class<?> loadClassBefore(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        return this.clr.loadClassBefore(classLoader, s);
    }
}
