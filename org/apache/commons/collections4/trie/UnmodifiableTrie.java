package org.apache.commons.collections4.trie;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.Unmodifiable;
import java.io.Serializable;
import org.apache.commons.collections4.Trie;

public class UnmodifiableTrie<K, V> implements Trie<K, V>, Serializable, Unmodifiable
{
    private static final long serialVersionUID = -7156426030315945159L;
    private final Trie<K, V> delegate;
    
    public static <K, V> Trie<K, V> unmodifiableTrie(final Trie<K, ? extends V> trie) {
        if (trie instanceof Unmodifiable) {
            final Trie<K, V> tmpTrie = (Trie<K, V>)trie;
            return tmpTrie;
        }
        return new UnmodifiableTrie<K, V>(trie);
    }
    
    public UnmodifiableTrie(final Trie<K, ? extends V> trie) {
        if (trie == null) {
            throw new NullPointerException("Trie must not be null");
        }
        final Trie<K, V> tmpTrie = (Trie<K, V>)trie;
        this.delegate = tmpTrie;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<K, V>>)this.delegate.entrySet());
    }
    
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }
    
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(this.delegate.values());
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.delegate.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.delegate.containsValue(value);
    }
    
    @Override
    public V get(final Object key) {
        return this.delegate.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public V put(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public K firstKey() {
        return this.delegate.firstKey();
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return Collections.unmodifiableSortedMap(this.delegate.headMap(toKey));
    }
    
    @Override
    public K lastKey() {
        return this.delegate.lastKey();
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return Collections.unmodifiableSortedMap(this.delegate.subMap(fromKey, toKey));
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return Collections.unmodifiableSortedMap(this.delegate.tailMap(fromKey));
    }
    
    @Override
    public SortedMap<K, V> prefixMap(final K key) {
        return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>)this.delegate.prefixMap(key));
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.delegate.comparator();
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        final OrderedMapIterator<K, V> it = this.delegate.mapIterator();
        return UnmodifiableOrderedMapIterator.unmodifiableOrderedMapIterator((OrderedMapIterator<K, ? extends V>)it);
    }
    
    @Override
    public K nextKey(final K key) {
        return this.delegate.nextKey(key);
    }
    
    @Override
    public K previousKey(final K key) {
        return this.delegate.previousKey(key);
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.delegate.equals(obj);
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
}
