package org.apache.commons.collections4.keyvalue;

import java.util.Map;
import org.apache.commons.collections4.KeyValue;

public class DefaultKeyValue<K, V> extends AbstractKeyValue<K, V>
{
    public DefaultKeyValue() {
        super(null, null);
    }
    
    public DefaultKeyValue(final K key, final V value) {
        super(key, value);
    }
    
    public DefaultKeyValue(final KeyValue<? extends K, ? extends V> pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public DefaultKeyValue(final Map.Entry<? extends K, ? extends V> entry) {
        super(entry.getKey(), entry.getValue());
    }
    
    public K setKey(final K key) {
        if (key == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
        }
        return super.setKey(key);
    }
    
    public V setValue(final V value) {
        if (value == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
        }
        return super.setValue(value);
    }
    
    public Map.Entry<K, V> toMapEntry() {
        return new DefaultMapEntry<K, V>((KeyValue<? extends K, ? extends V>)this);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultKeyValue)) {
            return false;
        }
        final DefaultKeyValue<?, ?> other = (DefaultKeyValue<?, ?>)obj;
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
