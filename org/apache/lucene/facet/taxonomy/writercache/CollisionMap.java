package org.apache.lucene.facet.taxonomy.writercache;

import java.util.NoSuchElementException;
import java.util.Iterator;
import org.apache.lucene.facet.taxonomy.FacetLabel;

public class CollisionMap
{
    private int capacity;
    private float loadFactor;
    private int size;
    private int threshold;
    private CharBlockArray labelRepository;
    private Entry[] entries;
    
    CollisionMap(final CharBlockArray labelRepository) {
        this(16384, 0.75f, labelRepository);
    }
    
    CollisionMap(final int initialCapacity, final CharBlockArray labelRepository) {
        this(initialCapacity, 0.75f, labelRepository);
    }
    
    private CollisionMap(final int initialCapacity, final float loadFactor, final CharBlockArray labelRepository) {
        this.labelRepository = labelRepository;
        this.loadFactor = loadFactor;
        this.capacity = CompactLabelToOrdinal.determineCapacity(2, initialCapacity);
        this.entries = new Entry[this.capacity];
        this.threshold = (int)(this.capacity * this.loadFactor);
    }
    
    public int size() {
        return this.size;
    }
    
    public int capacity() {
        return this.capacity;
    }
    
    private void grow() {
        final int newCapacity = this.capacity * 2;
        final Entry[] newEntries = new Entry[newCapacity];
        final Entry[] src = this.entries;
        for (int j = 0; j < src.length; ++j) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    final Entry next = e.next;
                    final int hash = e.hash;
                    final int i = indexFor(hash, newCapacity);
                    e.next = newEntries[i];
                    newEntries[i] = e;
                    e = next;
                } while (e != null);
            }
        }
        this.capacity = newCapacity;
        this.entries = newEntries;
        this.threshold = (int)(this.capacity * this.loadFactor);
    }
    
    public int get(final FacetLabel label, final int hash) {
        final int bucketIndex = indexFor(hash, this.capacity);
        Entry e;
        for (e = this.entries[bucketIndex]; e != null && (hash != e.hash || !CategoryPathUtils.equalsToSerialized(label, this.labelRepository, e.offset)); e = e.next) {}
        if (e == null) {
            return -2;
        }
        return e.cid;
    }
    
    public int addLabel(final FacetLabel label, final int hash, final int cid) {
        final int bucketIndex = indexFor(hash, this.capacity);
        for (Entry e = this.entries[bucketIndex]; e != null; e = e.next) {
            if (e.hash == hash && CategoryPathUtils.equalsToSerialized(label, this.labelRepository, e.offset)) {
                return e.cid;
            }
        }
        final int offset = this.labelRepository.length();
        CategoryPathUtils.serialize(label, this.labelRepository);
        this.addEntry(offset, cid, hash, bucketIndex);
        return cid;
    }
    
    public void addLabelOffset(final int hash, final int offset, final int cid) {
        final int bucketIndex = indexFor(hash, this.capacity);
        this.addEntry(offset, cid, hash, bucketIndex);
    }
    
    private void addEntry(final int offset, final int cid, final int hash, final int bucketIndex) {
        final Entry e = this.entries[bucketIndex];
        this.entries[bucketIndex] = new Entry(offset, cid, hash, e);
        if (this.size++ >= this.threshold) {
            this.grow();
        }
    }
    
    Iterator<Entry> entryIterator() {
        return new EntryIterator(this.entries, this.size);
    }
    
    static int indexFor(final int h, final int length) {
        return h & length - 1;
    }
    
    int getMemoryUsage() {
        int memoryUsage = 0;
        if (this.entries != null) {
            for (final Entry e : this.entries) {
                if (e != null) {
                    memoryUsage += 16;
                    for (Entry ee = e.next; ee != null; ee = ee.next) {
                        memoryUsage += 16;
                    }
                }
            }
        }
        return memoryUsage;
    }
    
    static class Entry
    {
        int offset;
        int cid;
        Entry next;
        int hash;
        
        Entry(final int offset, final int cid, final int h, final Entry e) {
            this.offset = offset;
            this.cid = cid;
            this.next = e;
            this.hash = h;
        }
    }
    
    private class EntryIterator implements Iterator<Entry>
    {
        Entry next;
        int index;
        Entry[] ents;
        
        EntryIterator(final Entry[] entries, final int size) {
            this.ents = entries;
            final Entry[] t = entries;
            int i = t.length;
            Entry n = null;
            if (size != 0) {
                while (i > 0 && (n = t[--i]) == null) {}
            }
            this.next = n;
            this.index = i;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        @Override
        public Entry next() {
            final Entry e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            Entry n;
            Entry[] t;
            int i;
            for (n = e.next, t = this.ents, i = this.index; n == null && i > 0; n = t[--i]) {}
            this.index = i;
            this.next = n;
            return e;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
