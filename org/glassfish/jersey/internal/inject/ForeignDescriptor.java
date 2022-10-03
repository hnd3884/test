package org.glassfish.jersey.internal.inject;

import java.util.function.Consumer;

public interface ForeignDescriptor
{
    Object get();
    
    void dispose(final Object p0);
    
    default ForeignDescriptor wrap(final Object descriptor) {
        return new ForeignDescriptorImpl(descriptor);
    }
    
    default ForeignDescriptor wrap(final Object descriptor, final Consumer<Object> disposeInstance) {
        return new ForeignDescriptorImpl(descriptor, disposeInstance);
    }
}
