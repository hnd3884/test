package org.apache.commons.chain.web;

import java.util.Map;

public class MapEntry implements Map.Entry
{
    private Object key;
    private Object value;
    private boolean modifiable;
    
    public MapEntry(final Object key, final Object value, final boolean modifiable) {
        this.modifiable = false;
        this.key = key;
        this.value = value;
        this.modifiable = modifiable;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public Object setValue(final Object val) {
        if (this.modifiable) {
            final Object oldVal = this.value;
            this.value = val;
            return oldVal;
        }
        throw new UnsupportedOperationException();
    }
    
    public boolean equals(final Object o) {
        if (o != null && o instanceof Map.Entry) {
            final Map.Entry entry = (Map.Entry)o;
            if (this.getKey() == null) {
                if (entry.getKey() != null) {
                    return false;
                }
            }
            else if (!this.getKey().equals(entry.getKey())) {
                return false;
            }
            if ((this.getValue() != null) ? this.getValue().equals(entry.getValue()) : (entry.getValue() == null)) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    public int hashCode() {
        return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
    }
}
