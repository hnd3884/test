package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Set;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;

public class ApplicationEventImpl implements ApplicationEvent
{
    private final Type type;
    private final ResourceConfig resourceConfig;
    private final Set<Class<?>> providers;
    private final Set<Class<?>> registeredClasses;
    private final Set<Object> registeredInstances;
    private final ResourceModel resourceModel;
    
    public ApplicationEventImpl(final Type type, final ResourceConfig resourceConfig, final Set<Class<?>> providers, final Set<Class<?>> registeredClasses, final Set<Object> registeredInstances, final ResourceModel resourceModel) {
        this.type = type;
        this.resourceConfig = resourceConfig;
        this.providers = providers;
        this.registeredClasses = registeredClasses;
        this.registeredInstances = registeredInstances;
        this.resourceModel = resourceModel;
    }
    
    @Override
    public ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public Set<Class<?>> getRegisteredClasses() {
        return this.registeredClasses;
    }
    
    @Override
    public Set<Object> getRegisteredInstances() {
        return this.registeredInstances;
    }
    
    @Override
    public Set<Class<?>> getProviders() {
        return this.providers;
    }
    
    @Override
    public ResourceModel getResourceModel() {
        return this.resourceModel;
    }
}
