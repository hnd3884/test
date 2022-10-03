package com.adventnet.persistence.cache;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.Map;

public class SyncMap<K, V> implements Map<K, V>
{
    private Map<K, V> map;
    private Lock readLock;
    private Lock writeLock;
    
    public SyncMap(final Map<K, V> map, final ReadWriteLock rwl) {
        this.map = map;
        this.readLock = rwl.readLock();
        this.writeLock = rwl.writeLock();
    }
    
    public Lock readLock() {
        return this.readLock;
    }
    
    public Lock writeLock() {
        return this.writeLock;
    }
    
    @Override
    public int size() {
        this.readLock.lock();
        try {
            return this.map.size();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean isEmpty() {
        this.readLock.lock();
        try {
            return this.map.isEmpty();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.readLock.lock();
        try {
            return this.map.containsKey(key);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.readLock.lock();
        try {
            return this.map.containsValue(value);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public V get(final Object key) {
        this.readLock.lock();
        try {
            return this.map.get(key);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public V put(final K key, final V value) {
        this.writeLock.lock();
        try {
            return this.map.put(key, value);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public V remove(final Object key) {
        this.writeLock.lock();
        try {
            return this.map.remove(key);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> t) {
        this.writeLock.lock();
        try {
            this.map.putAll(t);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            this.map.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Set<K> keySet() {
        this.readLock.lock();
        try {
            return this.map.keySet();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Collection<V> values() {
        this.readLock.lock();
        try {
            return this.map.values();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        this.readLock.lock();
        try {
            return this.map.entrySet();
        }
        finally {
            this.readLock.unlock();
        }
    }
}
