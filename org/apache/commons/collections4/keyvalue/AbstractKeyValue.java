package org.apache.commons.collections4.keyvalue;

import org.apache.commons.collections4.KeyValue;

public abstract class AbstractKeyValue<K, V> implements KeyValue<K, V>
{
    private K key;
    private V value;
    
    protected AbstractKeyValue(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public K getKey() {
        return this.key;
    }
    
    protected K setKey(final K key) {
        final K old = this.key;
        this.key = key;
        return old;
    }
    
    @Override
    public V getValue() {
        return this.value;
    }
    
    protected V setValue(final V value) {
        final V old = this.value;
        this.value = value;
        return old;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append(this.getKey()).append('=').append(this.getValue()).toString();
    }
}
