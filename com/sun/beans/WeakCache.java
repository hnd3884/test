package com.sun.beans;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.lang.ref.Reference;
import java.util.Map;

public final class WeakCache<K, V>
{
    private final Map<K, Reference<V>> map;
    
    public WeakCache() {
        this.map = new WeakHashMap<K, Reference<V>>();
    }
    
    public V get(final K k) {
        final Reference reference = this.map.get(k);
        if (reference == null) {
            return null;
        }
        final Object value = reference.get();
        if (value == null) {
            this.map.remove(k);
        }
        return (V)value;
    }
    
    public void put(final K k, final V v) {
        if (v != null) {
            this.map.put(k, new WeakReference<V>(v));
        }
        else {
            this.map.remove(k);
        }
    }
    
    public void clear() {
        this.map.clear();
    }
}
