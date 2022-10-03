package org.apache.commons.collections4.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.MapIterator;

public class EntrySetToMapIteratorAdapter<K, V> implements MapIterator<K, V>, ResettableIterator<K>
{
    Set<Map.Entry<K, V>> entrySet;
    transient Iterator<Map.Entry<K, V>> iterator;
    transient Map.Entry<K, V> entry;
    
    public EntrySetToMapIteratorAdapter(final Set<Map.Entry<K, V>> entrySet) {
        this.entrySet = entrySet;
        this.reset();
    }
    
    @Override
    public K getKey() {
        return this.current().getKey();
    }
    
    @Override
    public V getValue() {
        return this.current().getValue();
    }
    
    @Override
    public V setValue(final V value) {
        return this.current().setValue(value);
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    @Override
    public K next() {
        this.entry = this.iterator.next();
        return this.getKey();
    }
    
    @Override
    public synchronized void reset() {
        this.iterator = this.entrySet.iterator();
    }
    
    @Override
    public void remove() {
        this.iterator.remove();
        this.entry = null;
    }
    
    protected synchronized Map.Entry<K, V> current() {
        if (this.entry == null) {
            throw new IllegalStateException();
        }
        return this.entry;
    }
}
