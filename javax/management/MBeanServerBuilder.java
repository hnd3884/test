package javax.management;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

public class MBeanServerBuilder
{
    public MBeanServerDelegate newMBeanServerDelegate() {
        return JmxMBeanServer.newMBeanServerDelegate();
    }
    
    public MBeanServer newMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate) {
        return JmxMBeanServer.newMBeanServer(s, mBeanServer, mBeanServerDelegate, false);
    }
}
