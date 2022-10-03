package org.apache.commons.collections.keyvalue;

import org.apache.commons.collections.KeyValue;
import java.util.Map;

public abstract class AbstractMapEntryDecorator implements Map.Entry, KeyValue
{
    protected final Map.Entry entry;
    
    public AbstractMapEntryDecorator(final Map.Entry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Map Entry must not be null");
        }
        this.entry = entry;
    }
    
    protected Map.Entry getMapEntry() {
        return this.entry;
    }
    
    public Object getKey() {
        return this.entry.getKey();
    }
    
    public Object getValue() {
        return this.entry.getValue();
    }
    
    public Object setValue(final Object object) {
        return this.entry.setValue(object);
    }
    
    public boolean equals(final Object object) {
        return object == this || this.entry.equals(object);
    }
    
    public int hashCode() {
        return this.entry.hashCode();
    }
    
    public String toString() {
        return this.entry.toString();
    }
}
