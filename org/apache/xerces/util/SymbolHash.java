package org.apache.xerces.util;

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
    
    public SymbolHash(final int fTableSize) {
        this.fNum = 0;
        this.fTableSize = fTableSize;
        this.fBuckets = new Entry[this.fTableSize];
    }
    
    public void put(final Object o, final Object value) {
        int n = 0;
        final int hash = this.hash(o);
        int n2 = hash % this.fTableSize;
        for (Entry next = this.fBuckets[n2]; next != null; next = next.next) {
            if (o.equals(next.key)) {
                next.value = value;
                return;
            }
            ++n;
        }
        if (this.fNum >= this.fTableSize) {
            this.rehash();
            n2 = hash % this.fTableSize;
        }
        else if (n >= 40 && o instanceof String) {
            this.rebalance();
            n2 = this.hash(o) % this.fTableSize;
        }
        this.fBuckets[n2] = new Entry(o, value, this.fBuckets[n2]);
        ++this.fNum;
    }
    
    public Object get(final Object o) {
        final Entry search = this.search(o, this.hash(o) % this.fTableSize);
        if (search != null) {
            return search.value;
        }
        return null;
    }
    
    public int getLength() {
        return this.fNum;
    }
    
    public int getValues(final Object[] array, final int n) {
        for (int n2 = 0, n3 = 0; n2 < this.fTableSize && n3 < this.fNum; ++n2) {
            for (Entry next = this.fBuckets[n2]; next != null; next = next.next) {
                array[n + n3] = next.value;
                ++n3;
            }
        }
        return this.fNum;
    }
    
    public Object[] getEntries() {
        final Object[] array = new Object[this.fNum << 1];
        for (int n = 0, n2 = 0; n < this.fTableSize && n2 < this.fNum << 1; ++n) {
            for (Entry next = this.fBuckets[n]; next != null; next = next.next) {
                array[n2] = next.key;
                array[++n2] = next.value;
                ++n2;
            }
        }
        return array;
    }
    
    public SymbolHash makeClone() {
        final SymbolHash symbolHash = new SymbolHash(this.fTableSize);
        symbolHash.fNum = this.fNum;
        symbolHash.fHashMultipliers = (int[])((this.fHashMultipliers != null) ? ((int[])this.fHashMultipliers.clone()) : null);
        for (int i = 0; i < this.fTableSize; ++i) {
            if (this.fBuckets[i] != null) {
                symbolHash.fBuckets[i] = this.fBuckets[i].makeClone();
            }
        }
        return symbolHash;
    }
    
    public void clear() {
        for (int i = 0; i < this.fTableSize; ++i) {
            this.fBuckets[i] = null;
        }
        this.fNum = 0;
        this.fHashMultipliers = null;
    }
    
    protected Entry search(final Object o, final int n) {
        for (Entry next = this.fBuckets[n]; next != null; next = next.next) {
            if (o.equals(next.key)) {
                return next;
            }
        }
        return null;
    }
    
    protected int hash(final Object o) {
        if (this.fHashMultipliers == null || !(o instanceof String)) {
            return o.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0((String)o);
    }
    
    private int hash0(final String s) {
        int n = 0;
        final int length = s.length();
        final int[] fHashMultipliers = this.fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            n = n * fHashMultipliers[i & 0x1F] + s.charAt(i);
        }
        return n & Integer.MAX_VALUE;
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
    
    private void rehashCommon(final int n) {
        final int length = this.fBuckets.length;
        final Entry[] fBuckets = this.fBuckets;
        final Entry[] fBuckets2 = new Entry[n];
        this.fBuckets = fBuckets2;
        this.fTableSize = this.fBuckets.length;
        int n2 = length;
        while (n2-- > 0) {
            Entry entry;
            int n3;
            for (Entry next = fBuckets[n2]; next != null; next = next.next, n3 = this.hash(entry.key) % n, entry.next = fBuckets2[n3], fBuckets2[n3] = entry) {
                entry = next;
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
