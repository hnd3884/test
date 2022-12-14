package org.apache.tomcat.util.modeler;

import javax.management.ObjectName;
import java.util.List;

public interface RegistryMBean
{
    void invoke(final List<ObjectName> p0, final String p1, final boolean p2) throws Exception;
    
    void registerComponent(final Object p0, final String p1, final String p2) throws Exception;
    
    void unregisterComponent(final String p0);
    
    int getId(final String p0, final String p1);
    
    void stop();
}
