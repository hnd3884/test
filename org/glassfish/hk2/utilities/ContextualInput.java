package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ActiveDescriptor;

public class ContextualInput<T>
{
    private final ActiveDescriptor<T> descriptor;
    private final ServiceHandle<?> root;
    
    public ContextualInput(final ActiveDescriptor<T> descriptor, final ServiceHandle<?> root) {
        this.descriptor = descriptor;
        this.root = root;
    }
    
    public ActiveDescriptor<T> getDescriptor() {
        return this.descriptor;
    }
    
    public ServiceHandle<?> getRoot() {
        return this.root;
    }
    
    @Override
    public int hashCode() {
        return this.descriptor.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ContextualInput)) {
            return false;
        }
        final ContextualInput<T> other = (ContextualInput<T>)o;
        return this.descriptor.equals(other.descriptor);
    }
    
    @Override
    public String toString() {
        return "ContextualInput(" + this.descriptor.getImplementation() + "," + this.root + "," + System.identityHashCode(this) + ")";
    }
}
