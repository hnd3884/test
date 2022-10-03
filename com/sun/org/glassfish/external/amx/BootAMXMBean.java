package com.sun.org.glassfish.external.amx;

import javax.management.remote.JMXServiceURL;
import javax.management.ObjectName;
import com.sun.org.glassfish.external.arc.Stability;
import com.sun.org.glassfish.external.arc.Taxonomy;

@Taxonomy(stability = Stability.UNCOMMITTED)
public interface BootAMXMBean
{
    public static final String BOOT_AMX_OPERATION_NAME = "bootAMX";
    
    ObjectName bootAMX();
    
    JMXServiceURL[] getJMXServiceURLs();
}
