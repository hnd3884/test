package org.apache.commons.collections4.multimap;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.io.Serializable;
import org.apache.commons.collections4.MultiValuedMap;

public abstract class AbstractMultiValuedMapDecorator<K, V> implements MultiValuedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 20150612L;
    private final MultiValuedMap<K, V> map;
    
    protected AbstractMultiValuedMapDecorator(final MultiValuedMap<K, V> map) {
        if (map == null) {
            throw new NullPointerException("MultiValuedMap must not be null.");
        }
        this.map = map;
    }
    
    protected MultiValuedMap<K, V> decorated() {
        return this.map;
    }
    
    @Override
    public int size() {
        return this.decorated().size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.decorated().isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.decorated().containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.decorated().containsValue(value);
    }
    
    @Override
    public boolean containsMapping(final Object key, final Object value) {
        return this.decorated().containsMapping(key, value);
    }
    
    @Override
    public Collection<V> get(final K key) {
        return this.decorated().get(key);
    }
    
    @Override
    public Collection<V> remove(final Object key) {
        return this.decorated().remove(key);
    }
    
    @Override
    public boolean removeMapping(final Object key, final Object item) {
        return this.decorated().removeMapping(key, item);
    }
    
    @Override
    public void clear() {
        this.decorated().clear();
    }
    
    @Override
    public boolean put(final K key, final V value) {
        return this.decorated().put(key, value);
    }
    
    @Override
    public Set<K> keySet() {
        return this.decorated().keySet();
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return this.decorated().entries();
    }
    
    @Override
    public MultiSet<K> keys() {
        return this.decorated().keys();
    }
    
    @Override
    public Collection<V> values() {
        return this.decorated().values();
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return this.decorated().asMap();
    }
    
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        return this.decorated().putAll(key, values);
    }
    
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        return this.decorated().putAll(map);
    }
    
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        return this.decorated().putAll(map);
    }
    
    @Override
    public MapIterator<K, V> mapIterator() {
        return this.decorated().mapIterator();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.decorated().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.decorated().hashCode();
    }
    
    @Override
    public String toString() {
        return this.decorated().toString();
    }
}
