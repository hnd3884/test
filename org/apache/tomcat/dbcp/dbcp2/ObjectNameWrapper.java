package org.apache.tomcat.dbcp.dbcp2;

import org.apache.juli.logging.LogFactory;
import java.util.Objects;
import javax.management.MalformedObjectNameException;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import org.apache.juli.logging.Log;

class ObjectNameWrapper
{
    private static final Log log;
    private static final MBeanServer MBEAN_SERVER;
    private final ObjectName objectName;
    
    private static MBeanServer getPlatformMBeanServer() {
        try {
            return ManagementFactory.getPlatformMBeanServer();
        }
        catch (final LinkageError | Exception e) {
            ObjectNameWrapper.log.debug((Object)"Failed to get platform MBeanServer", e);
            return null;
        }
    }
    
    public static ObjectName unwrap(final ObjectNameWrapper wrapper) {
        return (wrapper == null) ? null : wrapper.unwrap();
    }
    
    public static ObjectNameWrapper wrap(final ObjectName objectName) {
        return new ObjectNameWrapper(objectName);
    }
    
    public static ObjectNameWrapper wrap(final String name) throws MalformedObjectNameException {
        return wrap(new ObjectName(name));
    }
    
    public ObjectNameWrapper(final ObjectName objectName) {
        this.objectName = objectName;
    }
    
    public void registerMBean(final Object object) {
        if (ObjectNameWrapper.MBEAN_SERVER == null || this.objectName == null) {
            return;
        }
        try {
            ObjectNameWrapper.MBEAN_SERVER.registerMBean(object, this.objectName);
        }
        catch (final LinkageError | Exception e) {
            ObjectNameWrapper.log.warn((Object)("Failed to complete JMX registration for " + this.objectName), e);
        }
    }
    
    @Override
    public String toString() {
        return Objects.toString(this.objectName);
    }
    
    public void unregisterMBean() {
        if (ObjectNameWrapper.MBEAN_SERVER == null || this.objectName == null) {
            return;
        }
        if (ObjectNameWrapper.MBEAN_SERVER.isRegistered(this.objectName)) {
            try {
                ObjectNameWrapper.MBEAN_SERVER.unregisterMBean(this.objectName);
            }
            catch (final LinkageError | Exception e) {
                ObjectNameWrapper.log.warn((Object)("Failed to complete JMX unregistration for " + this.objectName), e);
            }
        }
    }
    
    public ObjectName unwrap() {
        return this.objectName;
    }
    
    static {
        log = LogFactory.getLog((Class)ObjectNameWrapper.class);
        MBEAN_SERVER = getPlatformMBeanServer();
    }
}
