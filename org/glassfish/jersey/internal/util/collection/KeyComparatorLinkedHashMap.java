package org.glassfish.jersey.internal.util.collection;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

public class KeyComparatorLinkedHashMap<K, V> extends KeyComparatorHashMap<K, V> implements Map<K, V>
{
    private static final long serialVersionUID = 3801124242820219131L;
    private transient Entry<K, V> header;
    private final boolean accessOrder;
    
    public KeyComparatorLinkedHashMap(final int initialCapacity, final float loadFactor, final KeyComparator<K> keyComparator) {
        super(initialCapacity, loadFactor, keyComparator);
        this.accessOrder = false;
    }
    
    public KeyComparatorLinkedHashMap(final int initialCapacity, final KeyComparator<K> keyComparator) {
        super(initialCapacity, keyComparator);
        this.accessOrder = false;
    }
    
    public KeyComparatorLinkedHashMap(final KeyComparator<K> keyComparator) {
        super(keyComparator);
        this.accessOrder = false;
    }
    
    public KeyComparatorLinkedHashMap(final Map<? extends K, ? extends V> m, final KeyComparator<K> keyComparator) {
        super(m, keyComparator);
        this.accessOrder = false;
    }
    
    public KeyComparatorLinkedHashMap(final int initialCapacity, final float loadFactor, final boolean accessOrder, final KeyComparator<K> keyComparator) {
        super(initialCapacity, loadFactor, keyComparator);
        this.accessOrder = accessOrder;
    }
    
    @Override
    void init() {
        this.header = new Entry<K, V>(-1, null, null, null);
        final Entry<K, V> header = this.header;
        final Entry<K, V> header2 = this.header;
        final Entry<K, V> header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    @Override
    void transfer(final KeyComparatorHashMap.Entry[] newTable) {
        final int newCapacity = newTable.length;
        for (Entry<K, V> e = this.header.after; e != this.header; e = e.after) {
            final int index = KeyComparatorHashMap.indexFor(e.hash, newCapacity);
            e.next = newTable[index];
            newTable[index] = e;
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (Entry e = this.header.after; e != this.header; e = e.after) {
                if (e.value == null) {
                    return true;
                }
            }
        }
        else {
            for (Entry e = this.header.after; e != this.header; e = e.after) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public V get(final Object key) {
        final Entry<K, V> e = (Entry)this.getEntry((K)key);
        if (e == null) {
            return null;
        }
        e.recordAccess(this);
        return e.value;
    }
    
    @Override
    public void clear() {
        super.clear();
        final Entry<K, V> header = this.header;
        final Entry<K, V> header2 = this.header;
        final Entry<K, V> header3 = this.header;
        header2.after = header3;
        header.before = header3;
    }
    
    @Override
    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }
    
    @Override
    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }
    
    @Override
    Iterator<Map.Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }
    
    @Override
    void addEntry(final int hash, final K key, final V value, final int bucketIndex) {
        this.createEntry(hash, key, value, bucketIndex);
        final Entry<K, V> eldest = this.header.after;
        if (this.removeEldestEntry(eldest)) {
            this.removeEntryForKey(eldest.key);
        }
        else if (this.size >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
    
    @Override
    void createEntry(final int hash, final K key, final V value, final int bucketIndex) {
        final KeyComparatorHashMap.Entry<K, V> old = this.table[bucketIndex];
        final Entry<K, V> e = new Entry<K, V>(hash, key, value, old);
        ((Entry<Object, Object>)(this.table[bucketIndex] = e)).addBefore((Entry<Object, Object>)this.header);
        ++this.size;
    }
    
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return false;
    }
    
    private static class Entry<K, V> extends KeyComparatorHashMap.Entry<K, V>
    {
        Entry<K, V> before;
        Entry<K, V> after;
        
        Entry(final int hash, final K key, final V value, final KeyComparatorHashMap.Entry<K, V> next) {
            super(hash, key, value, next);
        }
        
        private void remove() {
            this.before.after = this.after;
            this.after.before = this.before;
        }
        
        private void addBefore(final Entry<K, V> existingEntry) {
            this.after = existingEntry;
            this.before = existingEntry.before;
            this.before.after = this;
            this.after.before = this;
        }
        
        @Override
        void recordAccess(final KeyComparatorHashMap<K, V> m) {
            final KeyComparatorLinkedHashMap<K, V> lm = (KeyComparatorLinkedHashMap)m;
            if (((KeyComparatorLinkedHashMap<Object, Object>)lm).accessOrder) {
                final KeyComparatorLinkedHashMap<K, V> keyComparatorLinkedHashMap = lm;
                ++keyComparatorLinkedHashMap.modCount;
                this.remove();
                this.addBefore(((KeyComparatorLinkedHashMap<Object, Object>)lm).header);
            }
        }
        
        @Override
        void recordRemoval(final KeyComparatorHashMap<K, V> m) {
            this.remove();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
    
    private abstract class LinkedHashIterator<T> implements Iterator<T>
    {
        Entry<K, V> nextEntry;
        Entry<K, V> lastReturned;
        int expectedModCount;
        
        private LinkedHashIterator() {
            this.nextEntry = KeyComparatorLinkedHashMap.this.header.after;
            this.lastReturned = null;
            this.expectedModCount = KeyComparatorLinkedHashMap.this.modCount;
        }
        
        @Override
        public boolean hasNext() {
            return this.nextEntry != KeyComparatorLinkedHashMap.this.header;
        }
        
        @Override
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            KeyComparatorLinkedHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
            this.expectedModCount = KeyComparatorLinkedHashMap.this.modCount;
        }
        
        Entry<K, V> nextEntry() {
            if (KeyComparatorLinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.nextEntry == KeyComparatorLinkedHashMap.this.header) {
                throw new NoSuchElementException();
            }
            final Entry<K, V> nextEntry = this.nextEntry;
            this.lastReturned = nextEntry;
            final Entry<K, V> e = nextEntry;
            this.nextEntry = e.after;
            return e;
        }
    }
    
    private class KeyIterator extends LinkedHashIterator<K>
    {
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
    }
    
    private class ValueIterator extends LinkedHashIterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }
    
    private class EntryIterator extends LinkedHashIterator<Map.Entry<K, V>>
    {
        @Override
        public Map.Entry<K, V> next() {
            return this.nextEntry();
        }
    }
}
