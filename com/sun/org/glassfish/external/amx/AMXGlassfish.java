package com.sun.org.glassfish.external.amx;

import javax.management.MBeanServer;
import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public final class AMXGlassfish
{
    public static final String DEFAULT_JMX_DOMAIN = "amx";
    public static final AMXGlassfish DEFAULT;
    private final String mJMXDomain;
    private final ObjectName mDomainRoot;
    
    public AMXGlassfish(final String jmxDomain) {
        this.mJMXDomain = jmxDomain;
        this.mDomainRoot = this.newObjectName("", "domain-root", null);
    }
    
    public static String getGlassfishVersion() {
        final String version = System.getProperty("glassfish.version");
        return version;
    }
    
    public String amxJMXDomain() {
        return this.mJMXDomain;
    }
    
    public String amxSupportDomain() {
        return this.amxJMXDomain() + "-support";
    }
    
    public String dasName() {
        return "server";
    }
    
    public String dasConfig() {
        return this.dasName() + "-config";
    }
    
    public ObjectName domainRoot() {
        return this.mDomainRoot;
    }
    
    public ObjectName monitoringRoot() {
        return this.newObjectName("/", "mon", null);
    }
    
    public ObjectName serverMon(final String serverName) {
        return this.newObjectName("/mon", "server-mon", serverName);
    }
    
    public ObjectName serverMonForDAS() {
        return this.serverMon("server");
    }
    
    public ObjectName newObjectName(final String pp, final String type, final String name) {
        String props = prop("pp", pp) + "," + prop("type", type);
        if (name != null) {
            props = props + "," + prop("name", name);
        }
        return this.newObjectName(props);
    }
    
    public ObjectName newObjectName(final String s) {
        String name = s;
        if (!name.startsWith(this.amxJMXDomain())) {
            name = this.amxJMXDomain() + ":" + name;
        }
        return AMXUtil.newObjectName(name);
    }
    
    private static String prop(final String key, final String value) {
        return key + "=" + value;
    }
    
    public ObjectName getBootAMXMBeanObjectName() {
        return AMXUtil.newObjectName(this.amxSupportDomain() + ":type=boot-amx");
    }
    
    public void invokeBootAMX(final MBeanServerConnection conn) {
        try {
            conn.invoke(this.getBootAMXMBeanObjectName(), "bootAMX", null, null);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static void invokeWaitAMXReady(final MBeanServerConnection conn, final ObjectName objectName) {
        try {
            conn.invoke(objectName, "waitAMXReady", null, null);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public <T extends MBeanListener.Callback> MBeanListener<T> listenForDomainRoot(final MBeanServerConnection server, final T callback) {
        final MBeanListener<T> listener = new MBeanListener<T>(server, this.domainRoot(), callback);
        listener.startListening();
        return listener;
    }
    
    public ObjectName waitAMXReady(final MBeanServerConnection server) {
        final WaitForDomainRootListenerCallback callback = new WaitForDomainRootListenerCallback(server);
        this.listenForDomainRoot(server, callback);
        callback.await();
        return callback.getRegistered();
    }
    
    public <T extends MBeanListener.Callback> MBeanListener<T> listenForBootAMX(final MBeanServerConnection server, final T callback) {
        final MBeanListener<T> listener = new MBeanListener<T>(server, this.getBootAMXMBeanObjectName(), callback);
        listener.startListening();
        return listener;
    }
    
    public ObjectName bootAMX(final MBeanServerConnection conn) throws IOException {
        final ObjectName domainRoot = this.domainRoot();
        if (!conn.isRegistered(domainRoot)) {
            final BootAMXCallback callback = new BootAMXCallback(conn);
            this.listenForBootAMX(conn, callback);
            callback.await();
            this.invokeBootAMX(conn);
            final WaitForDomainRootListenerCallback drCallback = new WaitForDomainRootListenerCallback(conn);
            this.listenForDomainRoot(conn, drCallback);
            drCallback.await();
            invokeWaitAMXReady(conn, domainRoot);
        }
        else {
            invokeWaitAMXReady(conn, domainRoot);
        }
        return domainRoot;
    }
    
    public ObjectName bootAMX(final MBeanServer server) {
        try {
            return this.bootAMX((MBeanServerConnection)server);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        DEFAULT = new AMXGlassfish("amx");
    }
    
    private static final class WaitForDomainRootListenerCallback extends MBeanListener.CallbackImpl
    {
        private final MBeanServerConnection mConn;
        
        public WaitForDomainRootListenerCallback(final MBeanServerConnection conn) {
            this.mConn = conn;
        }
        
        @Override
        public void mbeanRegistered(final ObjectName objectName, final MBeanListener listener) {
            super.mbeanRegistered(objectName, listener);
            invokeWaitAMXReady(this.mConn, objectName);
            this.mLatch.countDown();
        }
    }
    
    public static class BootAMXCallback extends MBeanListener.CallbackImpl
    {
        private final MBeanServerConnection mConn;
        
        public BootAMXCallback(final MBeanServerConnection conn) {
            this.mConn = conn;
        }
        
        @Override
        public void mbeanRegistered(final ObjectName objectName, final MBeanListener listener) {
            super.mbeanRegistered(objectName, listener);
            this.mLatch.countDown();
        }
    }
}
