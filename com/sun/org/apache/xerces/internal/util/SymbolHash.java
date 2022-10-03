package com.sun.org.apache.xerces.internal.util;

public class SymbolHash
{
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected int fTableSize;
    protected Entry[] fBuckets;
    protected int fNum;
    protected int[] fHashMultipliers;
    
    public SymbolHash() {
        this(101);
    }
    
    public SymbolHash(final int size) {
        this.fNum = 0;
        this.fTableSize = size;
        this.fBuckets = new Entry[this.fTableSize];
    }
    
    public void put(final Object key, final Object value) {
        int collisionCount = 0;
        final int hash = this.hash(key);
        int bucket = hash % this.fTableSize;
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (key.equals(entry.key)) {
                entry.value = value;
                return;
            }
            ++collisionCount;
        }
        if (this.fNum >= this.fTableSize) {
            this.rehash();
            bucket = hash % this.fTableSize;
        }
        else if (collisionCount >= 40 && key instanceof String) {
            this.rebalance();
            bucket = this.hash(key) % this.fTableSize;
        }
        Entry entry = new Entry(key, value, this.fBuckets[bucket]);
        this.fBuckets[bucket] = entry;
        ++this.fNum;
    }
    
    public Object get(final Object key) {
        final int bucket = this.hash(key) % this.fTableSize;
        final Entry entry = this.search(key, bucket);
        if (entry != null) {
            return entry.value;
        }
        return null;
    }
    
    public int getLength() {
        return this.fNum;
    }
    
    public int getValues(final Object[] elements, final int from) {
        for (int i = 0, j = 0; i < this.fTableSize && j < this.fNum; ++i) {
            for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
                elements[from + j] = entry.value;
                ++j;
            }
        }
        return this.fNum;
    }
    
    public Object[] getEntries() {
        final Object[] entries = new Object[this.fNum << 1];
        for (int i = 0, j = 0; i < this.fTableSize && j < this.fNum << 1; ++i) {
            for (Entry entry = this.fBuckets[i]; entry != null; entry = entry.next) {
                entries[j] = entry.key;
                entries[++j] = entry.value;
                ++j;
            }
        }
        return entries;
    }
    
    public SymbolHash makeClone() {
        final SymbolHash newTable = new SymbolHash(this.fTableSize);
        newTable.fNum = this.fNum;
        newTable.fHashMultipliers = (int[])((this.fHashMultipliers != null) ? ((int[])this.fHashMultipliers.clone()) : null);
        for (int i = 0; i < this.fTableSize; ++i) {
            if (this.fBuckets[i] != null) {
                newTable.fBuckets[i] = this.fBuckets[i].makeClone();
            }
        }
        return newTable;
    }
    
    public void clear() {
        for (int i = 0; i < this.fTableSize; ++i) {
            this.fBuckets[i] = null;
        }
        this.fNum = 0;
        this.fHashMultipliers = null;
    }
    
    protected Entry search(final Object key, final int bucket) {
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (key.equals(entry.key)) {
                return entry;
            }
        }
        return null;
    }
    
    protected int hash(final Object key) {
        if (this.fHashMultipliers == null || !(key instanceof String)) {
            return key.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0((String)key);
    }
    
    private int hash0(final String symbol) {
        int code = 0;
        final int length = symbol.length();
        final int[] multipliers = this.fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            code = code * multipliers[i & 0x1F] + symbol.charAt(i);
        }
        return code & Integer.MAX_VALUE;
    }
    
    protected void rehash() {
        this.rehashCommon((this.fBuckets.length << 1) + 1);
    }
    
    protected void rebalance() {
        if (this.fHashMultipliers == null) {
            this.fHashMultipliers = new int[32];
        }
        PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
        this.rehashCommon(this.fBuckets.length);
    }
    
    private void rehashCommon(final int newCapacity) {
        final int oldCapacity = this.fBuckets.length;
        final Entry[] oldTable = this.fBuckets;
        final Entry[] newTable = new Entry[newCapacity];
        this.fBuckets = newTable;
        this.fTableSize = this.fBuckets.length;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry e;
            int index;
            for (Entry old = oldTable[i]; old != null; old = old.next, index = this.hash(e.key) % newCapacity, e.next = newTable[index], newTable[index] = e) {
                e = old;
            }
        }
    }
    
    protected static final class Entry
    {
        public Object key;
        public Object value;
        public Entry next;
        
        public Entry() {
            this.key = null;
            this.value = null;
            this.next = null;
        }
        
        public Entry(final Object key, final Object value, final Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        public Entry makeClone() {
            final Entry entry = new Entry();
            entry.key = this.key;
            entry.value = this.value;
            if (this.next != null) {
                entry.next = this.next.makeClone();
            }
            return entry;
        }
    }
}
