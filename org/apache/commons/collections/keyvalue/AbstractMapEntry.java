package org.apache.commons.collections.keyvalue;

import java.util.Map;

public abstract class AbstractMapEntry extends AbstractKeyValue implements Map.Entry
{
    protected AbstractMapEntry(final Object key, final Object value) {
        super(key, value);
    }
    
    public Object setValue(final Object value) {
        final Object answer = this.value;
        this.value = value;
        return answer;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry other = (Map.Entry)obj;
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
