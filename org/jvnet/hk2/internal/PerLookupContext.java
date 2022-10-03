package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Context;

public class PerLookupContext implements Context<PerLookup>
{
    public Class<? extends Annotation> getScope() {
        return (Class<? extends Annotation>)PerLookup.class;
    }
    
    public <T> T findOrCreate(final ActiveDescriptor<T> activeDescriptor, final ServiceHandle<?> root) {
        return (T)activeDescriptor.create((ServiceHandle)root);
    }
    
    public boolean containsKey(final ActiveDescriptor<?> descriptor) {
        return false;
    }
    
    public boolean isActive() {
        return true;
    }
    
    public boolean supportsNullCreation() {
        return true;
    }
    
    public void shutdown() {
    }
    
    public void destroyOne(final ActiveDescriptor<?> descriptor) {
    }
}
