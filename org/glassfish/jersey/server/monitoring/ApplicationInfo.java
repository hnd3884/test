package org.glassfish.jersey.server.monitoring;

import java.util.Set;
import java.util.Date;
import org.glassfish.jersey.server.ResourceConfig;

public interface ApplicationInfo
{
    ResourceConfig getResourceConfig();
    
    Date getStartTime();
    
    Set<Class<?>> getRegisteredClasses();
    
    Set<Object> getRegisteredInstances();
    
    Set<Class<?>> getProviders();
    
    ApplicationInfo snapshot();
}
