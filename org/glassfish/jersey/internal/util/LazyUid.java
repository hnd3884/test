package org.glassfish.jersey.internal.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.io.Serializable;

public class LazyUid implements Serializable
{
    private static final long serialVersionUID = 4618609413877136867L;
    private final AtomicReference<String> uid;
    
    public LazyUid() {
        this.uid = new AtomicReference<String>();
    }
    
    public String value() {
        if (this.uid.get() == null) {
            this.uid.compareAndSet(null, UUID.randomUUID().toString());
        }
        return this.uid.get();
    }
    
    @Override
    public boolean equals(final Object that) {
        if (that == null) {
            return false;
        }
        if (this.getClass() != that.getClass()) {
            return false;
        }
        final LazyUid other = (LazyUid)that;
        return this.value().equals(other.value());
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.value().hashCode();
        return hash;
    }
    
    @Override
    public String toString() {
        return this.value();
    }
}
