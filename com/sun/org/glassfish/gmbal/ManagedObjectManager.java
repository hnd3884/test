package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.Closeable;

public interface ManagedObjectManager extends Closeable
{
    void suspendJMXRegistration();
    
    void resumeJMXRegistration();
    
    boolean isManagedObject(final Object p0);
    
    GmbalMBean createRoot();
    
    GmbalMBean createRoot(final Object p0);
    
    GmbalMBean createRoot(final Object p0, final String p1);
    
    Object getRoot();
    
    GmbalMBean register(final Object p0, final Object p1, final String p2);
    
    GmbalMBean register(final Object p0, final Object p1);
    
    GmbalMBean registerAtRoot(final Object p0, final String p1);
    
    GmbalMBean registerAtRoot(final Object p0);
    
    void unregister(final Object p0);
    
    ObjectName getObjectName(final Object p0);
    
    AMXClient getAMXClient(final Object p0);
    
    Object getObject(final ObjectName p0);
    
    void stripPrefix(final String... p0);
    
    void stripPackagePrefix();
    
    String getDomain();
    
    void setMBeanServer(final MBeanServer p0);
    
    MBeanServer getMBeanServer();
    
    void setResourceBundle(final ResourceBundle p0);
    
    ResourceBundle getResourceBundle();
    
    void addAnnotation(final AnnotatedElement p0, final Annotation p1);
    
    void setRegistrationDebug(final RegistrationDebugLevel p0);
    
    void setRuntimeDebug(final boolean p0);
    
    void setTypelibDebug(final int p0);
    
    void setJMXRegistrationDebug(final boolean p0);
    
    String dumpSkeleton(final Object p0);
    
    void suppressDuplicateRootReport(final boolean p0);
    
    public enum RegistrationDebugLevel
    {
        NONE, 
        NORMAL, 
        FINE;
    }
}
