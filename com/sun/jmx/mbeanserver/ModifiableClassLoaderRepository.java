package com.sun.jmx.mbeanserver;

import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;

public interface ModifiableClassLoaderRepository extends ClassLoaderRepository
{
    void addClassLoader(final ClassLoader p0);
    
    void removeClassLoader(final ClassLoader p0);
    
    void addClassLoader(final ObjectName p0, final ClassLoader p1);
    
    void removeClassLoader(final ObjectName p0);
    
    ClassLoader getClassLoader(final ObjectName p0);
}
