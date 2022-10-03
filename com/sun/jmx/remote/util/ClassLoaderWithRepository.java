package com.sun.jmx.remote.util;

import javax.management.loading.ClassLoaderRepository;

public class ClassLoaderWithRepository extends ClassLoader
{
    private ClassLoaderRepository repository;
    private ClassLoader cl2;
    
    public ClassLoaderWithRepository(final ClassLoaderRepository repository, final ClassLoader cl2) {
        if (repository == null) {
            throw new IllegalArgumentException("Null ClassLoaderRepository object.");
        }
        this.repository = repository;
        this.cl2 = cl2;
    }
    
    @Override
    protected Class<?> findClass(final String s) throws ClassNotFoundException {
        Class<?> loadClass;
        try {
            loadClass = this.repository.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {
            if (this.cl2 != null) {
                return this.cl2.loadClass(s);
            }
            throw ex;
        }
        if (loadClass.getName().equals(s)) {
            return loadClass;
        }
        if (this.cl2 != null) {
            return this.cl2.loadClass(s);
        }
        throw new ClassNotFoundException(s);
    }
}
