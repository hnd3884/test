package org.glassfish.jersey.internal.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.List;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class ImmutableMultivaluedMap<K, V> implements MultivaluedMap<K, V>
{
    private final MultivaluedMap<K, V> delegate;
    
    public static <K, V> ImmutableMultivaluedMap<K, V> empty() {
        return new ImmutableMultivaluedMap<K, V>((javax.ws.rs.core.MultivaluedMap<K, V>)new MultivaluedHashMap());
    }
    
    public ImmutableMultivaluedMap(final MultivaluedMap<K, V> delegate) {
        if (delegate == null) {
            throw new NullPointerException("ImmutableMultivaluedMap delegate must not be 'null'.");
        }
        this.delegate = delegate;
    }
    
    public boolean equalsIgnoreValueOrder(final MultivaluedMap<K, V> otherMap) {
        return this.delegate.equalsIgnoreValueOrder((MultivaluedMap)otherMap);
    }
    
    public void putSingle(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public void add(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public V getFirst(final K key) {
        return (V)this.delegate.getFirst((Object)key);
    }
    
    public void addAll(final K key, final V... newValues) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public void addAll(final K key, final List<V> valueList) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public void addFirst(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public int size() {
        return this.delegate.size();
    }
    
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    public boolean containsKey(final Object key) {
        return this.delegate.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        return this.delegate.containsValue(value);
    }
    
    public List<V> get(final Object key) {
        return (List)this.delegate.get(key);
    }
    
    public List<V> put(final K key, final List<V> value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public List<V> remove(final Object key) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public void putAll(final Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public void clear() {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }
    
    public Set<K> keySet() {
        return Collections.unmodifiableSet((Set<? extends K>)this.delegate.keySet());
    }
    
    public Collection<List<V>> values() {
        return Collections.unmodifiableCollection((Collection<? extends List<V>>)this.delegate.values());
    }
    
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<K, List<V>>>)this.delegate.entrySet());
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImmutableMultivaluedMap)) {
            return false;
        }
        final ImmutableMultivaluedMap that = (ImmutableMultivaluedMap)o;
        return this.delegate.equals(that.delegate);
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
}
