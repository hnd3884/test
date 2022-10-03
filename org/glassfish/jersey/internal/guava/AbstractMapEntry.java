package org.glassfish.jersey.internal.guava;

import java.util.Objects;
import java.util.Map;

abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V>
{
    @Override
    public abstract K getKey();
    
    @Override
    public abstract V getValue();
    
    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object instanceof Map.Entry) {
            final Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
            return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final K k = this.getKey();
        final V v = this.getValue();
        return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
    }
    
    @Override
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
