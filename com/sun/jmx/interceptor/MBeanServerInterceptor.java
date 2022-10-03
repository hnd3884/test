package com.sun.jmx.interceptor;

import javax.management.loading.ClassLoaderRepository;
import javax.management.OperationsException;
import java.io.ObjectInputStream;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.MBeanServer;

public interface MBeanServerInterceptor extends MBeanServer
{
    Object instantiate(final String p0) throws ReflectionException, MBeanException;
    
    Object instantiate(final String p0, final ObjectName p1) throws ReflectionException, MBeanException, InstanceNotFoundException;
    
    Object instantiate(final String p0, final Object[] p1, final String[] p2) throws ReflectionException, MBeanException;
    
    Object instantiate(final String p0, final ObjectName p1, final Object[] p2, final String[] p3) throws ReflectionException, MBeanException, InstanceNotFoundException;
    
    @Deprecated
    ObjectInputStream deserialize(final ObjectName p0, final byte[] p1) throws InstanceNotFoundException, OperationsException;
    
    @Deprecated
    ObjectInputStream deserialize(final String p0, final byte[] p1) throws OperationsException, ReflectionException;
    
    @Deprecated
    ObjectInputStream deserialize(final String p0, final ObjectName p1, final byte[] p2) throws InstanceNotFoundException, OperationsException, ReflectionException;
    
    ClassLoaderRepository getClassLoaderRepository();
}
