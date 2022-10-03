package org.apache.commons.collections4;

import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.map.EntrySetToMapIteratorAdapter;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections4.set.UnmodifiableSet;
import org.apache.commons.collections4.map.UnmodifiableEntrySet;
import java.util.Set;
import java.util.Map;

public class SplitMapUtils
{
    private SplitMapUtils() {
    }
    
    public static <K, V> IterableMap<K, V> readableMap(final Get<K, V> get) {
        if (get == null) {
            throw new NullPointerException("Get must not be null");
        }
        if (get instanceof Map) {
            return (get instanceof IterableMap) ? ((IterableMap)get) : MapUtils.iterableMap((Map<K, V>)(Map)get);
        }
        return new WrappedGet<K, V>((Get)get);
    }
    
    public static <K, V> Map<K, V> writableMap(final Put<K, V> put) {
        if (put == null) {
            throw new NullPointerException("Put must not be null");
        }
        if (put instanceof Map) {
            return (Map)put;
        }
        return new WrappedPut<K, V>((Put)put);
    }
    
    private static class WrappedGet<K, V> implements IterableMap<K, V>, Unmodifiable
    {
        private final Get<K, V> get;
        
        private WrappedGet(final Get<K, V> get) {
            this.get = get;
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.get.containsKey(key);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.get.containsValue(value);
        }
        
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return UnmodifiableEntrySet.unmodifiableEntrySet(this.get.entrySet());
        }
        
        @Override
        public boolean equals(final Object arg0) {
            return arg0 == this || (arg0 instanceof WrappedGet && ((WrappedGet)arg0).get.equals(this.get));
        }
        
        @Override
        public V get(final Object key) {
            return this.get.get(key);
        }
        
        @Override
        public int hashCode() {
            return "WrappedGet".hashCode() << 4 | this.get.hashCode();
        }
        
        @Override
        public boolean isEmpty() {
            return this.get.isEmpty();
        }
        
        @Override
        public Set<K> keySet() {
            return UnmodifiableSet.unmodifiableSet((Set<? extends K>)this.get.keySet());
        }
        
        @Override
        public V put(final K key, final V value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V remove(final Object key) {
            return this.get.remove(key);
        }
        
        @Override
        public int size() {
            return this.get.size();
        }
        
        @Override
        public Collection<V> values() {
            return UnmodifiableCollection.unmodifiableCollection((Collection<? extends V>)this.get.values());
        }
        
        @Override
        public MapIterator<K, V> mapIterator() {
            MapIterator<K, V> it;
            if (this.get instanceof IterableGet) {
                it = ((IterableGet)this.get).mapIterator();
            }
            else {
                it = new EntrySetToMapIteratorAdapter<K, V>(this.get.entrySet());
            }
            return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)it);
        }
    }
    
    private static class WrappedPut<K, V> implements Map<K, V>, Put<K, V>
    {
        private final Put<K, V> put;
        
        private WrappedPut(final Put<K, V> put) {
            this.put = put;
        }
        
        @Override
        public void clear() {
            this.put.clear();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean containsValue(final Object value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof WrappedPut && ((WrappedPut)obj).put.equals(this.put));
        }
        
        @Override
        public V get(final Object key) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int hashCode() {
            return "WrappedPut".hashCode() << 4 | this.put.hashCode();
        }
        
        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Set<K> keySet() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V put(final K key, final V value) {
            return (V)this.put.put(key, value);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> t) {
            this.put.putAll(t);
        }
        
        @Override
        public V remove(final Object key) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Collection<V> values() {
            throw new UnsupportedOperationException();
        }
    }
}
