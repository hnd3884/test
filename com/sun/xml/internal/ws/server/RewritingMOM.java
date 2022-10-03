package com.sun.xml.internal.ws.server;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import com.sun.org.glassfish.gmbal.AMXClient;
import javax.management.ObjectName;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;

class RewritingMOM implements ManagedObjectManager
{
    private final ManagedObjectManager mom;
    private static final String gmbalQuotingCharsRegex = "\n|\\|\"|\\*|\\?|:|=|,";
    private static final String replacementChar = "-";
    
    RewritingMOM(final ManagedObjectManager mom) {
        this.mom = mom;
    }
    
    private String rewrite(final String x) {
        return x.replaceAll("\n|\\|\"|\\*|\\?|:|=|,", "-");
    }
    
    @Override
    public void suspendJMXRegistration() {
        this.mom.suspendJMXRegistration();
    }
    
    @Override
    public void resumeJMXRegistration() {
        this.mom.resumeJMXRegistration();
    }
    
    @Override
    public GmbalMBean createRoot() {
        return this.mom.createRoot();
    }
    
    @Override
    public GmbalMBean createRoot(final Object root) {
        return this.mom.createRoot(root);
    }
    
    @Override
    public GmbalMBean createRoot(final Object root, final String name) {
        return this.mom.createRoot(root, this.rewrite(name));
    }
    
    @Override
    public Object getRoot() {
        return this.mom.getRoot();
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj, final String name) {
        return this.mom.register(parent, obj, this.rewrite(name));
    }
    
    @Override
    public GmbalMBean register(final Object parent, final Object obj) {
        return this.mom.register(parent, obj);
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj, final String name) {
        return this.mom.registerAtRoot(obj, this.rewrite(name));
    }
    
    @Override
    public GmbalMBean registerAtRoot(final Object obj) {
        return this.mom.registerAtRoot(obj);
    }
    
    @Override
    public void unregister(final Object obj) {
        this.mom.unregister(obj);
    }
    
    @Override
    public ObjectName getObjectName(final Object obj) {
        return this.mom.getObjectName(obj);
    }
    
    @Override
    public AMXClient getAMXClient(final Object obj) {
        return this.mom.getAMXClient(obj);
    }
    
    @Override
    public Object getObject(final ObjectName oname) {
        return this.mom.getObject(oname);
    }
    
    @Override
    public void stripPrefix(final String... str) {
        this.mom.stripPrefix(str);
    }
    
    @Override
    public void stripPackagePrefix() {
        this.mom.stripPackagePrefix();
    }
    
    @Override
    public String getDomain() {
        return this.mom.getDomain();
    }
    
    @Override
    public void setMBeanServer(final MBeanServer server) {
        this.mom.setMBeanServer(server);
    }
    
    @Override
    public MBeanServer getMBeanServer() {
        return this.mom.getMBeanServer();
    }
    
    @Override
    public void setResourceBundle(final ResourceBundle rb) {
        this.mom.setResourceBundle(rb);
    }
    
    @Override
    public ResourceBundle getResourceBundle() {
        return this.mom.getResourceBundle();
    }
    
    @Override
    public void addAnnotation(final AnnotatedElement element, final Annotation annotation) {
        this.mom.addAnnotation(element, annotation);
    }
    
    @Override
    public void setRegistrationDebug(final RegistrationDebugLevel level) {
        this.mom.setRegistrationDebug(level);
    }
    
    @Override
    public void setRuntimeDebug(final boolean flag) {
        this.mom.setRuntimeDebug(flag);
    }
    
    @Override
    public void setTypelibDebug(final int level) {
        this.mom.setTypelibDebug(level);
    }
    
    @Override
    public String dumpSkeleton(final Object obj) {
        return this.mom.dumpSkeleton(obj);
    }
    
    @Override
    public void suppressDuplicateRootReport(final boolean suppressReport) {
        this.mom.suppressDuplicateRootReport(suppressReport);
    }
    
    @Override
    public void close() throws IOException {
        this.mom.close();
    }
    
    @Override
    public void setJMXRegistrationDebug(final boolean x) {
        this.mom.setJMXRegistrationDebug(x);
    }
    
    @Override
    public boolean isManagedObject(final Object x) {
        return this.mom.isManagedObject(x);
    }
}
