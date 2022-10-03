package org.apache.commons.collections.keyvalue;

import java.io.Serializable;
import org.apache.commons.collections.KeyValue;
import java.util.Map;

public class TiedMapEntry implements Map.Entry, KeyValue, Serializable
{
    private static final long serialVersionUID = -8453869361373831205L;
    private final Map map;
    private final Object key;
    
    public TiedMapEntry(final Map map, final Object key) {
        this.map = map;
        this.key = key;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.map.get(this.key);
    }
    
    public Object setValue(final Object value) {
        if (value == this) {
            throw new IllegalArgumentException("Cannot set value to this map entry");
        }
        return this.map.put(this.key, value);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry other = (Map.Entry)obj;
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
    
    public int hashCode() {
        final Object value = this.getValue();
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((value == null) ? 0 : value.hashCode());
    }
    
    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}
