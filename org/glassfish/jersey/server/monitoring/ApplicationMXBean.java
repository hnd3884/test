package org.glassfish.jersey.server.monitoring;

import java.util.Set;
import java.util.Date;
import java.util.Map;

public interface ApplicationMXBean
{
    String getApplicationName();
    
    String getApplicationClass();
    
    Map<String, String> getProperties();
    
    Date getStartTime();
    
    Set<String> getRegisteredClasses();
    
    Set<String> getRegisteredInstances();
    
    Set<String> getProviderClasses();
}
