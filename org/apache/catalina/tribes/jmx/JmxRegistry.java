package org.apache.catalina.tribes.jmx;

import org.apache.juli.logging.LogFactory;
import java.util.Iterator;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.InstanceNotFoundException;
import javax.management.NotCompliantMBeanException;
import javax.management.MalformedObjectNameException;
import org.apache.catalina.tribes.JmxChannel;
import org.apache.catalina.tribes.Channel;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;

public class JmxRegistry
{
    private static final Log log;
    protected static final StringManager sm;
    private static ConcurrentHashMap<String, JmxRegistry> registryCache;
    private MBeanServer mbserver;
    private ObjectName baseOname;
    
    private JmxRegistry() {
        this.mbserver = ManagementFactory.getPlatformMBeanServer();
        this.baseOname = null;
    }
    
    public static JmxRegistry getRegistry(final Channel channel) {
        if (channel == null || channel.getName() == null) {
            return null;
        }
        JmxRegistry registry = JmxRegistry.registryCache.get(channel.getName());
        if (registry != null) {
            return registry;
        }
        if (!(channel instanceof JmxChannel)) {
            return null;
        }
        final JmxChannel jmxChannel = (JmxChannel)channel;
        if (!jmxChannel.isJmxEnabled()) {
            return null;
        }
        final ObjectName baseOn = createBaseObjectName(jmxChannel.getJmxDomain(), jmxChannel.getJmxPrefix(), channel.getName());
        if (baseOn == null) {
            return null;
        }
        registry = new JmxRegistry();
        registry.baseOname = baseOn;
        JmxRegistry.registryCache.put(channel.getName(), registry);
        return registry;
    }
    
    public static void removeRegistry(final Channel channel, final boolean clear) {
        final JmxRegistry registry = JmxRegistry.registryCache.get(channel.getName());
        if (registry == null) {
            return;
        }
        if (clear) {
            registry.clearMBeans();
        }
        JmxRegistry.registryCache.remove(channel.getName());
    }
    
    private static ObjectName createBaseObjectName(final String domain, final String prefix, final String name) {
        if (domain == null) {
            JmxRegistry.log.warn((Object)JmxRegistry.sm.getString("jmxRegistry.no.domain"));
            return null;
        }
        ObjectName on = null;
        final StringBuilder sb = new StringBuilder(domain);
        sb.append(':');
        sb.append(prefix);
        sb.append("type=Channel,channel=");
        sb.append(name);
        try {
            on = new ObjectName(sb.toString());
        }
        catch (final MalformedObjectNameException e) {
            JmxRegistry.log.error((Object)JmxRegistry.sm.getString("jmxRegistry.objectName.failed", sb.toString()), (Throwable)e);
        }
        return on;
    }
    
    public ObjectName registerJmx(final String keyprop, final Object bean) {
        final String oNameStr = this.baseOname.toString() + keyprop;
        ObjectName oName = null;
        try {
            oName = new ObjectName(oNameStr);
            if (this.mbserver.isRegistered(oName)) {
                this.mbserver.unregisterMBean(oName);
            }
            this.mbserver.registerMBean(bean, oName);
        }
        catch (final NotCompliantMBeanException e) {
            JmxRegistry.log.warn((Object)JmxRegistry.sm.getString("jmxRegistry.registerJmx.notCompliant", bean), (Throwable)e);
            return null;
        }
        catch (final MalformedObjectNameException e2) {
            JmxRegistry.log.error((Object)JmxRegistry.sm.getString("jmxRegistry.objectName.failed", oNameStr), (Throwable)e2);
            return null;
        }
        catch (final Exception e3) {
            JmxRegistry.log.error((Object)JmxRegistry.sm.getString("jmxRegistry.registerJmx.failed", bean, oNameStr), (Throwable)e3);
            return null;
        }
        return oName;
    }
    
    public void unregisterJmx(final ObjectName oname) {
        if (oname == null) {
            return;
        }
        try {
            this.mbserver.unregisterMBean(oname);
        }
        catch (final InstanceNotFoundException e) {
            JmxRegistry.log.warn((Object)JmxRegistry.sm.getString("jmxRegistry.unregisterJmx.notFound", oname), (Throwable)e);
        }
        catch (final Exception e2) {
            JmxRegistry.log.warn((Object)JmxRegistry.sm.getString("jmxRegistry.unregisterJmx.failed", oname), (Throwable)e2);
        }
    }
    
    private void clearMBeans() {
        final String query = this.baseOname.toString() + ",*";
        try {
            final ObjectName name = new ObjectName(query);
            final Set<ObjectName> onames = this.mbserver.queryNames(name, null);
            for (final ObjectName objectName : onames) {
                this.unregisterJmx(objectName);
            }
        }
        catch (final MalformedObjectNameException e) {
            JmxRegistry.log.error((Object)JmxRegistry.sm.getString("jmxRegistry.objectName.failed", query), (Throwable)e);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)JmxRegistry.class);
        sm = StringManager.getManager(JmxRegistry.class);
        JmxRegistry.registryCache = new ConcurrentHashMap<String, JmxRegistry>();
    }
}
