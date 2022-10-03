package org.glassfish.jersey.server.internal.monitoring;

import java.util.Set;
import java.util.Date;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;

final class ApplicationInfoImpl implements ApplicationInfo
{
    private final ResourceConfig resourceConfig;
    private final Date startTime;
    private final Set<Class<?>> registeredClasses;
    private final Set<Object> registeredInstances;
    private final Set<Class<?>> providers;
    
    ApplicationInfoImpl(final ResourceConfig resourceConfig, final Date startTime, final Set<Class<?>> registeredClasses, final Set<Object> registeredInstances, final Set<Class<?>> providers) {
        this.resourceConfig = resourceConfig;
        this.startTime = startTime;
        this.registeredClasses = registeredClasses;
        this.registeredInstances = registeredInstances;
        this.providers = providers;
    }
    
    @Override
    public ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }
    
    @Override
    public Date getStartTime() {
        return this.startTime;
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
    public ApplicationInfo snapshot() {
        return this;
    }
}
