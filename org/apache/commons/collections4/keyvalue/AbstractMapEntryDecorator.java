package org.apache.commons.collections4.keyvalue;

import org.apache.commons.collections4.KeyValue;
import java.util.Map;

public abstract class AbstractMapEntryDecorator<K, V> implements Map.Entry<K, V>, KeyValue<K, V>
{
    private final Map.Entry<K, V> entry;
    
    public AbstractMapEntryDecorator(final Map.Entry<K, V> entry) {
        if (entry == null) {
            throw new NullPointerException("Map Entry must not be null.");
        }
        this.entry = entry;
    }
    
    protected Map.Entry<K, V> getMapEntry() {
        return this.entry;
    }
    
    @Override
    public K getKey() {
        return this.entry.getKey();
    }
    
    @Override
    public V getValue() {
        return this.entry.getValue();
    }
    
    @Override
    public V setValue(final V object) {
        return this.entry.setValue(object);
    }
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.entry.equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.entry.hashCode();
    }
    
    @Override
    public String toString() {
        return this.entry.toString();
    }
}
