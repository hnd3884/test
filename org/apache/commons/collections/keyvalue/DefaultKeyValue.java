package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;

public class DefaultKeyValue extends AbstractKeyValue
{
    public DefaultKeyValue() {
        super(null, null);
    }
    
    public DefaultKeyValue(final Object key, final Object value) {
        super(key, value);
    }
    
    public DefaultKeyValue(final KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }
    
    public DefaultKeyValue(final Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }
    
    public Object setKey(final Object key) {
        if (key == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
        }
        final Object old = this.key;
        this.key = key;
        return old;
    }
    
    public Object setValue(final Object value) {
        if (value == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
        }
        final Object old = this.value;
        this.value = value;
        return old;
    }
    
    public Map.Entry toMapEntry() {
        return new DefaultMapEntry(this);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultKeyValue)) {
            return false;
        }
        final DefaultKeyValue other = (DefaultKeyValue)obj;
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
    
    public int hashCode() {
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
    }
}
