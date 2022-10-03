package org.apache.commons.collections4.map;

import java.util.Iterator;
import org.apache.commons.collections4.collection.CompositeCollection;
import org.apache.commons.collections4.set.CompositeSet;
import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Map;
import java.io.Serializable;

public class CompositeMap<K, V> extends AbstractIterableMap<K, V> implements Serializable
{
    private static final long serialVersionUID = -6096931280583808322L;
    private Map<K, V>[] composite;
    private MapMutator<K, V> mutator;
    
    public CompositeMap() {
        this(new Map[0], null);
    }
    
    public CompositeMap(final Map<K, V> one, final Map<K, V> two) {
        this(new Map[] { one, two }, null);
    }
    
    public CompositeMap(final Map<K, V> one, final Map<K, V> two, final MapMutator<K, V> mutator) {
        this(new Map[] { one, two }, mutator);
    }
    
    public CompositeMap(final Map<K, V>... composite) {
        this(composite, null);
    }
    
    public CompositeMap(final Map<K, V>[] composite, final MapMutator<K, V> mutator) {
        this.mutator = mutator;
        this.composite = new Map[0];
        for (int i = composite.length - 1; i >= 0; --i) {
            this.addComposited(composite[i]);
        }
    }
    
    public void setMutator(final MapMutator<K, V> mutator) {
        this.mutator = mutator;
    }
    
    public synchronized void addComposited(final Map<K, V> map) throws IllegalArgumentException {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            final Collection<K> intersect = CollectionUtils.intersection((Iterable<? extends K>)this.composite[i].keySet(), (Iterable<? extends K>)map.keySet());
            if (intersect.size() != 0) {
                if (this.mutator == null) {
                    throw new IllegalArgumentException("Key collision adding Map to CompositeMap");
                }
                this.mutator.resolveCollision(this, this.composite[i], map, intersect);
            }
        }
        final Map<K, V>[] temp = new Map[this.composite.length + 1];
        System.arraycopy(this.composite, 0, temp, 0, this.composite.length);
        temp[temp.length - 1] = map;
        this.composite = temp;
    }
    
    public synchronized Map<K, V> removeComposited(final Map<K, V> map) {
        for (int size = this.composite.length, i = 0; i < size; ++i) {
            if (this.composite[i].equals(map)) {
                final Map<K, V>[] temp = new Map[size - 1];
                System.arraycopy(this.composite, 0, temp, 0, i);
                System.arraycopy(this.composite, i + 1, temp, i, size - i - 1);
                this.composite = temp;
                return map;
            }
        }
        return null;
    }
    
    @Override
    public void clear() {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            this.composite[i].clear();
        }
    }
    
    @Override
    public boolean containsKey(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsValue(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final CompositeSet<Map.Entry<K, V>> entries = new CompositeSet<Map.Entry<K, V>>();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            entries.addComposited(this.composite[i].entrySet());
        }
        return entries;
    }
    
    @Override
    public V get(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].get(key);
            }
        }
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (!this.composite[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Set<K> keySet() {
        final CompositeSet<K> keys = new CompositeSet<K>();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            keys.addComposited(this.composite[i].keySet());
        }
        return keys;
    }
    
    @Override
    public V put(final K key, final V value) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        return this.mutator.put(this, this.composite, key, value);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("No mutator specified");
        }
        this.mutator.putAll(this, this.composite, map);
    }
    
    @Override
    public V remove(final Object key) {
        for (int i = this.composite.length - 1; i >= 0; --i) {
            if (this.composite[i].containsKey(key)) {
                return this.composite[i].remove(key);
            }
        }
        return null;
    }
    
    @Override
    public int size() {
        int size = 0;
        for (int i = this.composite.length - 1; i >= 0; --i) {
            size += this.composite[i].size();
        }
        return size;
    }
    
    @Override
    public Collection<V> values() {
        final CompositeCollection<V> values = new CompositeCollection<V>();
        for (int i = this.composite.length - 1; i >= 0; --i) {
            values.addComposited(this.composite[i].values());
        }
        return values;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>)obj;
            return this.entrySet().equals(map.entrySet());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            code += entry.hashCode();
        }
        return code;
    }
    
    public interface MapMutator<K, V> extends Serializable
    {
        void resolveCollision(final CompositeMap<K, V> p0, final Map<K, V> p1, final Map<K, V> p2, final Collection<K> p3);
        
        V put(final CompositeMap<K, V> p0, final Map<K, V>[] p1, final K p2, final V p3);
        
        void putAll(final CompositeMap<K, V> p0, final Map<K, V>[] p1, final Map<? extends K, ? extends V> p2);
    }
}
