package org.apache.commons.collections4.keyvalue;

import java.util.Map;

public abstract class AbstractMapEntry<K, V> extends AbstractKeyValue<K, V> implements Map.Entry<K, V>
{
    protected AbstractMapEntry(final K key, final V value) {
        super(key, value);
    }
    
    @Override
    public V setValue(final V value) {
        return super.setValue(value);
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
        if (this.getKey() == null) {
            if (other.getKey() != null) {
                return false;
            }
        }
        else if (!this.getKey().equals(other.getKey())) {
            return false;
        }
        if ((this.getValue() != null) ? this.getValue().equals(other.getValue()) : (other.getValue() == null)) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
    }
}
