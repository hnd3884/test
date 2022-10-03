package org.apache.commons.collections;

import java.util.Map;

public class DefaultMapEntry implements Map.Entry, KeyValue
{
    private Object key;
    private Object value;
    
    public DefaultMapEntry() {
    }
    
    public DefaultMapEntry(final Map.Entry entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }
    
    public DefaultMapEntry(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public void setKey(final Object key) {
        this.key = key;
    }
    
    public Object getValue() {
        return this.value;
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
    
    public String toString() {
        return "" + this.getKey() + "=" + this.getValue();
    }
}
