package org.apache.tomcat.jdbc.pool.jmx;

import org.apache.juli.logging.LogFactory;
import javax.management.MalformedObjectNameException;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;

public class JmxUtil
{
    private static final Log log;
    
    public static ObjectName registerJmx(final ObjectName base, final String keyprop, final Object obj) {
        ObjectName oname = null;
        try {
            oname = getObjectName(base, keyprop);
            if (oname != null) {
                ManagementFactory.getPlatformMBeanServer().registerMBean(obj, oname);
            }
        }
        catch (final Exception e) {
            JmxUtil.log.error((Object)"Jmx registration failed.", (Throwable)e);
        }
        return oname;
    }
    
    public static void unregisterJmx(final ObjectName oname) {
        if (oname == null) {
            return;
        }
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(oname);
        }
        catch (final Exception e) {
            JmxUtil.log.error((Object)"Jmx unregistration failed.", (Throwable)e);
        }
    }
    
    private static ObjectName getObjectName(final ObjectName base, final String keyprop) throws MalformedObjectNameException {
        if (base == null) {
            return null;
        }
        final StringBuilder OnameStr = new StringBuilder(base.toString());
        if (keyprop != null) {
            OnameStr.append(keyprop);
        }
        final ObjectName oname = new ObjectName(OnameStr.toString());
        return oname;
    }
    
    static {
        log = LogFactory.getLog((Class)JmxUtil.class);
    }
}
