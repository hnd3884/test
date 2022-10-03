package org.apache.commons.collections.keyvalue;

import org.apache.commons.collections.KeyValue;

public abstract class AbstractKeyValue implements KeyValue
{
    protected Object key;
    protected Object value;
    
    protected AbstractKeyValue(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    public Object getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public String toString() {
        return new StringBuffer().append(this.getKey()).append('=').append(this.getValue()).toString();
    }
}
