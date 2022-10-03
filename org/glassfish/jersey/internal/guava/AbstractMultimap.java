package org.glassfish.jersey.internal.guava;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;

abstract class AbstractMultimap<K, V> implements Multimap<K, V>
{
    private transient Collection<Map.Entry<K, V>> entries;
    private transient Set<K> keySet;
    private transient Collection<V> values;
    private transient Map<K, Collection<V>> asMap;
    
    @Override
    public boolean containsValue(final Object value) {
        for (final Collection<V> collection : this.asMap().values()) {
            if (collection.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean containsEntry(final Object key, final Object value) {
        final Collection<V> collection = this.asMap().get(key);
        return collection != null && collection.contains(value);
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        final Collection<V> collection = this.asMap().get(key);
        return collection != null && collection.remove(value);
    }
    
    @Override
    public boolean put(final K key, final V value) {
        return this.get(key).add(value);
    }
    
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        Preconditions.checkNotNull(values);
        if (values instanceof Collection) {
            final Collection<? extends V> valueCollection = (Collection)values;
            return !valueCollection.isEmpty() && this.get(key).addAll(valueCollection);
        }
        final Iterator<? extends V> valueItr = values.iterator();
        return valueItr.hasNext() && Iterators.addAll(this.get(key), valueItr);
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        final Collection<Map.Entry<K, V>> result = this.entries;
        return (result == null) ? (this.entries = this.createEntries()) : result;
    }
    
    private Collection<Map.Entry<K, V>> createEntries() {
        if (this instanceof SetMultimap) {
            return (Collection<Map.Entry<K, V>>)new EntrySet();
        }
        return (Collection<Map.Entry<K, V>>)new Entries();
    }
    
    abstract Iterator<Map.Entry<K, V>> entryIterator();
    
    @Override
    public Set<K> keySet() {
        final Set<K> result = this.keySet;
        return (result == null) ? (this.keySet = this.createKeySet()) : result;
    }
    
    Set<K> createKeySet() {
        return (Set<K>)new Maps.KeySet((Map<Object, Object>)this.asMap());
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> result = this.values;
        return (result == null) ? (this.values = this.createValues()) : result;
    }
    
    private Collection<V> createValues() {
        return new Values();
    }
    
    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entries().iterator());
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        final Map<K, Collection<V>> result = this.asMap;
        return (result == null) ? (this.asMap = this.createAsMap()) : result;
    }
    
    abstract Map<K, Collection<V>> createAsMap();
    
    @Override
    public boolean equals(final Object object) {
        return Multimaps.equalsImpl(this, object);
    }
    
    @Override
    public int hashCode() {
        return this.asMap().hashCode();
    }
    
    @Override
    public String toString() {
        return this.asMap().toString();
    }
    
    private class Entries extends Multimaps.Entries<K, V>
    {
        @Override
        Multimap<K, V> multimap() {
            return AbstractMultimap.this;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }
    }
    
    private class EntrySet extends Entries implements Set<Map.Entry<K, V>>
    {
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return Sets.equalsImpl(this, obj);
        }
    }
    
    private class Values extends AbstractCollection<V>
    {
        @Override
        public Iterator<V> iterator() {
            return AbstractMultimap.this.valueIterator();
        }
        
        @Override
        public int size() {
            return AbstractMultimap.this.size();
        }
        
        @Override
        public boolean contains(final Object o) {
            return AbstractMultimap.this.containsValue(o);
        }
        
        @Override
        public void clear() {
            AbstractMultimap.this.clear();
        }
    }
}
