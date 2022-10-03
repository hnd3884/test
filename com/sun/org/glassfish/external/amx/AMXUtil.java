package com.sun.org.glassfish.external.amx;

import javax.management.ObjectName;
import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;

@Taxonomy(stability = Stability.UNCOMMITTED)
public final class AMXUtil
{
    private AMXUtil() {
    }
    
    public static ObjectName newObjectName(final String s) {
        try {
            return new ObjectName(s);
        }
        catch (final Exception e) {
            throw new RuntimeException("bad ObjectName", e);
        }
    }
    
    public static ObjectName newObjectName(final String domain, final String props) {
        return newObjectName(domain + ":" + props);
    }
    
    public static ObjectName getMBeanServerDelegateObjectName() {
        return newObjectName("JMImplementation:type=MBeanServerDelegate");
    }
    
    public static String prop(final String key, final String value) {
        return key + "=" + value;
    }
}
