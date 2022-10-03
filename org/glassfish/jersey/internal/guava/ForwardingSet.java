package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.util.Set;

public abstract class ForwardingSet<E> extends ForwardingCollection<E> implements Set<E>
{
    ForwardingSet() {
    }
    
    @Override
    protected abstract Set<E> delegate();
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.delegate().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
}
