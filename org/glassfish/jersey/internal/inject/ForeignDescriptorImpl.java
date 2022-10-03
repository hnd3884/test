package org.glassfish.jersey.internal.inject;

import java.util.function.Consumer;

public class ForeignDescriptorImpl implements ForeignDescriptor
{
    private static final Consumer<Object> NOOP_DISPOSE_INSTANCE;
    private final Object foreignDescriptor;
    private final Consumer<Object> disposeInstance;
    
    public ForeignDescriptorImpl(final Object foreignDescriptor) {
        this(foreignDescriptor, ForeignDescriptorImpl.NOOP_DISPOSE_INSTANCE);
    }
    
    public ForeignDescriptorImpl(final Object foreignDescriptor, final Consumer<Object> disposeInstance) {
        this.foreignDescriptor = foreignDescriptor;
        this.disposeInstance = disposeInstance;
    }
    
    @Override
    public Object get() {
        return this.foreignDescriptor;
    }
    
    @Override
    public void dispose(final Object instance) {
        this.disposeInstance.accept(instance);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForeignDescriptorImpl)) {
            return false;
        }
        final ForeignDescriptorImpl that = (ForeignDescriptorImpl)o;
        return this.foreignDescriptor.equals(that.foreignDescriptor);
    }
    
    @Override
    public int hashCode() {
        return this.foreignDescriptor.hashCode();
    }
    
    static {
        NOOP_DISPOSE_INSTANCE = (instance -> {});
    }
}
