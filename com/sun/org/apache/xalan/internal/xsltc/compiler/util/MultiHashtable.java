package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public final class MultiHashtable<K, V>
{
    static final long serialVersionUID = -6151608290510033572L;
    private final Map<K, Set<V>> map;
    private boolean modifiable;
    
    public MultiHashtable() {
        this.map = new HashMap<K, Set<V>>();
        this.modifiable = true;
    }
    
    public Set<V> put(final K key, final V value) {
        if (this.modifiable) {
            Set<V> set = this.map.get(key);
            if (set == null) {
                set = new HashSet<V>();
                this.map.put(key, set);
            }
            set.add(value);
            return set;
        }
        throw new UnsupportedOperationException("The MultiHashtable instance is not modifiable.");
    }
    
    public V maps(final K key, final V value) {
        if (key == null) {
            return null;
        }
        final Set<V> set = this.map.get(key);
        if (set != null) {
            for (final V v : set) {
                if (v.equals(value)) {
                    return v;
                }
            }
        }
        return null;
    }
    
    public void makeUnmodifiable() {
        this.modifiable = false;
    }
}
