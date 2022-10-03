package org.glassfish.jersey.message.internal;

import java.util.Collection;
import org.glassfish.jersey.internal.PropertiesDelegate;

public final class TracingAwarePropertiesDelegate implements PropertiesDelegate
{
    private final PropertiesDelegate propertiesDelegate;
    private TracingLogger tracingLogger;
    
    public TracingAwarePropertiesDelegate(final PropertiesDelegate propertiesDelegate) {
        this.propertiesDelegate = propertiesDelegate;
    }
    
    @Override
    public void removeProperty(final String name) {
        if (TracingLogger.PROPERTY_NAME.equals(name)) {
            this.tracingLogger = null;
        }
        this.propertiesDelegate.removeProperty(name);
    }
    
    @Override
    public void setProperty(final String name, final Object object) {
        if (TracingLogger.PROPERTY_NAME.equals(name)) {
            this.tracingLogger = (TracingLogger)object;
        }
        this.propertiesDelegate.setProperty(name, object);
    }
    
    @Override
    public Object getProperty(final String name) {
        if (this.tracingLogger != null && TracingLogger.PROPERTY_NAME.equals(name)) {
            return this.tracingLogger;
        }
        return this.propertiesDelegate.getProperty(name);
    }
    
    @Override
    public Collection<String> getPropertyNames() {
        return this.propertiesDelegate.getPropertyNames();
    }
}
