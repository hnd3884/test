package com.me.ems.framework.common.api.v1.model;

public class LiteFilterValue<K, V>
{
    private K key;
    private V displayName;
    
    public LiteFilterValue() {
    }
    
    public LiteFilterValue(final K key, final V displayName) {
        this.key = key;
        this.displayName = displayName;
    }
    
    public K getKey() {
        return this.key;
    }
    
    public void setKey(final K key) {
        this.key = key;
    }
    
    public V getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final V displayName) {
        this.displayName = displayName;
    }
}
