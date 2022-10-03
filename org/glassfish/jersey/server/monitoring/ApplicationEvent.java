package org.glassfish.jersey.server.monitoring;

import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Set;
import org.glassfish.jersey.server.ResourceConfig;

public interface ApplicationEvent
{
    Type getType();
    
    ResourceConfig getResourceConfig();
    
    Set<Class<?>> getRegisteredClasses();
    
    Set<Object> getRegisteredInstances();
    
    Set<Class<?>> getProviders();
    
    ResourceModel getResourceModel();
    
    public enum Type
    {
        INITIALIZATION_START, 
        INITIALIZATION_APP_FINISHED, 
        INITIALIZATION_FINISHED, 
        DESTROY_FINISHED, 
        RELOAD_FINISHED;
    }
}
