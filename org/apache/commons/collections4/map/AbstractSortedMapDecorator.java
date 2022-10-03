package org.apache.commons.collections4.map;

import java.util.ListIterator;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import java.util.Set;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.collections4.IterableSortedMap;

public abstract class AbstractSortedMapDecorator<K, V> extends AbstractMapDecorator<K, V> implements IterableSortedMap<K, V>
{
    protected AbstractSortedMapDecorator() {
    }
    
    public AbstractSortedMapDecorator(final SortedMap<K, V> map) {
        super(map);
    }
    
    @Override
    protected SortedMap<K, V> decorated() {
        return (SortedMap)super.decorated();
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.decorated().comparator();
    }
    
    @Override
    public K firstKey() {
        return this.decorated().firstKey();
    }
    
    @Override
    public K lastKey() {
        return this.decorated().lastKey();
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return this.decorated().subMap(fromKey, toKey);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return this.decorated().headMap(toKey);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return this.decorated().tailMap(fromKey);
    }
    
    @Override
    public K previousKey(final K key) {
        final SortedMap<K, V> headMap = this.headMap(key);
        return headMap.isEmpty() ? null : headMap.lastKey();
    }
    
    @Override
    public K nextKey(final K key) {
        final Iterator<K> it = this.tailMap(key).keySet().iterator();
        it.next();
        return it.hasNext() ? it.next() : null;
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new SortedMapIterator<K, V>(this.entrySet());
    }
    
    protected static class SortedMapIterator<K, V> extends EntrySetToMapIteratorAdapter<K, V> implements OrderedMapIterator<K, V>
    {
        protected SortedMapIterator(final Set<Map.Entry<K, V>> entrySet) {
            super(entrySet);
        }
        
        @Override
        public synchronized void reset() {
            super.reset();
            this.iterator = (Iterator<Map.Entry<K, V>>)new ListIteratorWrapper<Map.Entry<K, V>>((Iterator<? extends Map.Entry<K, V>>)this.iterator);
        }
        
        @Override
        public boolean hasPrevious() {
            return ((ListIterator)this.iterator).hasPrevious();
        }
        
        @Override
        public K previous() {
            this.entry = (Map.Entry<K, V>)((ListIterator)this.iterator).previous();
            return this.getKey();
        }
    }
}
