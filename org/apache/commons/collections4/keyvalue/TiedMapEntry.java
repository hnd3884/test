package org.apache.commons.collections4.keyvalue;

import java.io.Serializable;
import org.apache.commons.collections4.KeyValue;
import java.util.Map;

public class TiedMapEntry<K, V> implements Map.Entry<K, V>, KeyValue<K, V>, Serializable
{
    private static final long serialVersionUID = -8453869361373831205L;
    private final Map<K, V> map;
    private final K key;
    
    public TiedMapEntry(final Map<K, V> map, final K key) {
        this.map = map;
        this.key = key;
    }
    
    @Override
    public K getKey() {
        return this.key;
    }
    
    @Override
    public V getValue() {
        return this.map.get(this.key);
    }
    
    @Override
    public V setValue(final V value) {
        if (value == this) {
            throw new IllegalArgumentException("Cannot set value to this map entry");
        }
        return this.map.put(this.key, value);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry<?, ?> other = (Map.Entry<?, ?>)obj;
        final Object value = this.getValue();
        if (this.key == null) {
            if (other.getKey() != null) {
                return false;
            }
        }
        else if (!this.key.equals(other.getKey())) {
            return false;
        }
        if ((value != null) ? value.equals(other.getValue()) : (other.getValue() == null)) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final Object value = this.getValue();
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((value == null) ? 0 : value.hashCode());
    }
    
    @Override
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
