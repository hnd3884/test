package org.glassfish.jersey.model.internal;

import javax.ws.rs.core.Configurable;
import java.util.Map;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import javax.ws.rs.core.FeatureContext;

public class FeatureContextWrapper implements FeatureContext, InjectionManagerSupplier
{
    private final FeatureContext context;
    private final InjectionManager injectionManager;
    
    public FeatureContextWrapper(final FeatureContext context, final InjectionManager injectionManager) {
        this.context = context;
        this.injectionManager = injectionManager;
    }
    
    public Configuration getConfiguration() {
        return this.context.getConfiguration();
    }
    
    public FeatureContext property(final String name, final Object value) {
        return (FeatureContext)this.context.property(name, value);
    }
    
    public FeatureContext register(final Class<?> componentClass) {
        return (FeatureContext)this.context.register((Class)componentClass);
    }
    
    public FeatureContext register(final Class<?> componentClass, final int priority) {
        return (FeatureContext)this.context.register((Class)componentClass, priority);
    }
    
    public FeatureContext register(final Class<?> componentClass, final Class<?>... contracts) {
        return (FeatureContext)this.context.register((Class)componentClass, (Class[])contracts);
    }
    
    public FeatureContext register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        return (FeatureContext)this.context.register((Class)componentClass, (Map)contracts);
    }
    
    public FeatureContext register(final Object component) {
        return (FeatureContext)this.context.register(component);
    }
    
    public FeatureContext register(final Object component, final int priority) {
        return (FeatureContext)this.context.register(component, priority);
    }
    
    public FeatureContext register(final Object component, final Class<?>... contracts) {
        return (FeatureContext)this.context.register(component, (Class[])contracts);
    }
    
    public FeatureContext register(final Object component, final Map<Class<?>, Integer> contracts) {
        return (FeatureContext)this.context.register(component, (Map)contracts);
    }
    
    public InjectionManager getInjectionManager() {
        return this.injectionManager;
    }
}
