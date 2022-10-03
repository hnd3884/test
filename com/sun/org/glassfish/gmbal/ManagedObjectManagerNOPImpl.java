package com.sun.org.glassfish.gmbal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class ManagedObjectManagerNOPImpl implements ManagedObjectManager
{
    static final ManagedObjectManager self;
    private static final GmbalMBean gmb;
    
    private ManagedObjectManagerNOPImpl() {
    }
    
    @Override
    public void suspendJMXRegistration() {
    }
    
    @Override
    public void resumeJMXRegistration() {
    }
    
    @Override
    public boolean isManagedObject(final Object obj) {
        return false;
    }
    
    @Override
    public GmbalMBean createRoot() {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public GmbalMBean createRoot(final Object root) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public GmbalMBean createRoot(final Object root, final String name) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public Object getRoot() {
        return null;
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj, final String name) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj, final String name) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj) {
        return ManagedObjectManagerNOPImpl.gmb;
    }
    
    @Override
    public void unregister(final Object obj) {
    }
    
    @Override
    public ObjectName getObjectName(final Object obj) {
        return null;
    }
    
    @Override
    public Object getObject(final ObjectName oname) {
        return null;
    }
    
    @Override
    public void stripPrefix(final String... str) {
    }
    
    @Override
    public String getDomain() {
        return null;
    }
    
    @Override
    public void setMBeanServer(final MBeanServer server) {
    }
    
    @Override
    public MBeanServer getMBeanServer() {
        return null;
    }
    
    @Override
    public void setResourceBundle(final ResourceBundle rb) {
    }
    
    @Override
    public ResourceBundle getResourceBundle() {
        return null;
    }
    
    @Override
    public void addAnnotation(final AnnotatedElement element, final Annotation annotation) {
    }
    
    @Override
    public void setRegistrationDebug(final RegistrationDebugLevel level) {
    }
    
    @Override
    public void setRuntimeDebug(final boolean flag) {
    }
    
    @Override
    public String dumpSkeleton(final Object obj) {
        return "";
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void setTypelibDebug(final int level) {
    }
    
    @Override
    public void stripPackagePrefix() {
    }
    
    @Override
    public void suppressDuplicateRootReport(final boolean suppressReport) {
    }
    
    @Override
    public AMXClient getAMXClient(final Object obj) {
        return null;
    }
    
    @Override
    public void setJMXRegistrationDebug(final boolean flag) {
    }
    
    static {
        self = new ManagedObjectManagerNOPImpl();
        gmb = new GmbalMBeanNOPImpl();
    }
}
