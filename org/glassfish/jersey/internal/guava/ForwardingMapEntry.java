package org.glassfish.jersey.internal.guava;

import java.util.Objects;
import java.util.Map;

public abstract class ForwardingMapEntry<K, V> extends ForwardingObject implements Map.Entry<K, V>
{
    ForwardingMapEntry() {
    }
    
    @Override
    protected abstract Map.Entry<K, V> delegate();
    
    @Override
    public K getKey() {
        return this.delegate().getKey();
    }
    
    @Override
    public V getValue() {
        return this.delegate().getValue();
    }
    
    @Override
    public V setValue(final V value) {
        return this.delegate().setValue(value);
    }
    
    @Override
    public boolean equals(final Object object) {
        return this.delegate().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    boolean standardEquals(final Object object) {
        if (object instanceof Map.Entry) {
            final Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
            return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
        }
        return false;
    }
}
