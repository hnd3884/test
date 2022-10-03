package org.apache.catalina.util;

import java.util.Collection;
import java.util.Set;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.apache.tomcat.util.res.StringManager;
import java.io.Serializable;
import java.util.Map;

public final class ParameterMap<K, V> implements Map<K, V>, Serializable
{
    private static final long serialVersionUID = 2L;
    private final Map<K, V> delegatedMap;
    private final Map<K, V> unmodifiableDelegatedMap;
    private boolean locked;
    private static final StringManager sm;
    
    public ParameterMap() {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap<K, V>();
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.delegatedMap);
    }
    
    public ParameterMap(final int initialCapacity) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap<K, V>(initialCapacity);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.delegatedMap);
    }
    
    public ParameterMap(final int initialCapacity, final float loadFactor) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap<K, V>(initialCapacity, loadFactor);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.delegatedMap);
    }
    
    public ParameterMap(final Map<K, V> map) {
        this.locked = false;
        this.delegatedMap = new LinkedHashMap<K, V>((Map<? extends K, ? extends V>)map);
        this.unmodifiableDelegatedMap = Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.delegatedMap);
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    @Override
    public void clear() {
        this.checkLocked();
        this.delegatedMap.clear();
    }
    
    @Override
    public V put(final K key, final V value) {
        this.checkLocked();
        return this.delegatedMap.put(key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.checkLocked();
        this.delegatedMap.putAll(map);
    }
    
    @Override
    public V remove(final Object key) {
        this.checkLocked();
        return this.delegatedMap.remove(key);
    }
    
    private void checkLocked() {
        if (this.locked) {
            throw new IllegalStateException(ParameterMap.sm.getString("parameterMap.locked"));
        }
    }
    
    @Override
    public int size() {
        return this.delegatedMap.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegatedMap.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.delegatedMap.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.delegatedMap.containsValue(value);
    }
    
    @Override
    public V get(final Object key) {
        return this.delegatedMap.get(key);
    }
    
    @Override
    public Set<K> keySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.keySet();
        }
        return this.delegatedMap.keySet();
    }
    
    @Override
    public Collection<V> values() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.values();
        }
        return this.delegatedMap.values();
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        if (this.locked) {
            return this.unmodifiableDelegatedMap.entrySet();
        }
        return this.delegatedMap.entrySet();
    }
    
    static {
        sm = StringManager.getManager("org.apache.catalina.util");
    }
}
