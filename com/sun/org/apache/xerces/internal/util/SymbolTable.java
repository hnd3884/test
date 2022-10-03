package com.sun.org.apache.xerces.internal.util;

public class SymbolTable
{
    protected static final int TABLE_SIZE = 101;
    protected static final int MAX_HASH_COLLISIONS = 40;
    protected static final int MULTIPLIERS_SIZE = 32;
    protected static final int MULTIPLIERS_MASK = 31;
    protected Entry[] fBuckets;
    protected int fTableSize;
    protected transient int fCount;
    protected int fThreshold;
    protected float fLoadFactor;
    protected final int fCollisionThreshold;
    protected int[] fHashMultipliers;
    
    public SymbolTable(int initialCapacity, final float loadFactor) {
        this.fBuckets = null;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.fLoadFactor = loadFactor;
        this.fTableSize = initialCapacity;
        this.fBuckets = new Entry[this.fTableSize];
        this.fThreshold = (int)(this.fTableSize * loadFactor);
        this.fCollisionThreshold = (int)(40.0f * loadFactor);
        this.fCount = 0;
    }
    
    public SymbolTable(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public SymbolTable() {
        this(101, 0.75f);
    }
    
    public String addSymbol(final String symbol) {
        int collisionCount = 0;
        final int bucket = this.hash(symbol) % this.fTableSize;
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (entry.symbol.equals(symbol)) {
                return entry.symbol;
            }
            ++collisionCount;
        }
        return this.addSymbol0(symbol, bucket, collisionCount);
    }
    
    private String addSymbol0(final String symbol, int bucket, final int collisionCount) {
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            bucket = this.hash(symbol) % this.fTableSize;
        }
        else if (collisionCount >= this.fCollisionThreshold) {
            this.rebalance();
            bucket = this.hash(symbol) % this.fTableSize;
        }
        final Entry entry = new Entry(symbol, this.fBuckets[bucket]);
        this.fBuckets[bucket] = entry;
        ++this.fCount;
        return entry.symbol;
    }
    
    public String addSymbol(final char[] buffer, final int offset, final int length) {
        int collisionCount = 0;
        final int bucket = this.hash(buffer, offset, length) % this.fTableSize;
    Label_0088:
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; ++i) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        ++collisionCount;
                        continue Label_0088;
                    }
                }
                return entry.symbol;
            }
            ++collisionCount;
        }
        return this.addSymbol0(buffer, offset, length, bucket, collisionCount);
    }
    
    private String addSymbol0(final char[] buffer, final int offset, final int length, int bucket, final int collisionCount) {
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            bucket = this.hash(buffer, offset, length) % this.fTableSize;
        }
        else if (collisionCount >= this.fCollisionThreshold) {
            this.rebalance();
            bucket = this.hash(buffer, offset, length) % this.fTableSize;
        }
        final Entry entry = new Entry(buffer, offset, length, this.fBuckets[bucket]);
        this.fBuckets[bucket] = entry;
        ++this.fCount;
        return entry.symbol;
    }
    
    public int hash(final String symbol) {
        if (this.fHashMultipliers == null) {
            return symbol.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0(symbol);
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
    
    public int hash(final char[] buffer, final int offset, final int length) {
        if (this.fHashMultipliers == null) {
            int code = 0;
            for (int i = 0; i < length; ++i) {
                code = code * 31 + buffer[offset + i];
            }
            return code & Integer.MAX_VALUE;
        }
        return this.hash0(buffer, offset, length);
    }
    
    private int hash0(final char[] buffer, final int offset, final int length) {
        int code = 0;
        final int[] multipliers = this.fHashMultipliers;
        for (int i = 0; i < length; ++i) {
            code = code * multipliers[i & 0x1F] + buffer[offset + i];
        }
        return code & Integer.MAX_VALUE;
    }
    
    protected void rehash() {
        this.rehashCommon(this.fBuckets.length * 2 + 1);
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
        this.fThreshold = (int)(newCapacity * this.fLoadFactor);
        this.fBuckets = newTable;
        this.fTableSize = this.fBuckets.length;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry e;
            int index;
            for (Entry old = oldTable[i]; old != null; old = old.next, index = this.hash(e.symbol) % newCapacity, e.next = newTable[index], newTable[index] = e) {
                e = old;
            }
        }
    }
    
    public boolean containsSymbol(final String symbol) {
        final int bucket = this.hash(symbol) % this.fTableSize;
        final int length = symbol.length();
    Label_0076:
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; ++i) {
                    if (symbol.charAt(i) != entry.characters[i]) {
                        continue Label_0076;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean containsSymbol(final char[] buffer, final int offset, final int length) {
        final int bucket = this.hash(buffer, offset, length) % this.fTableSize;
    Label_0075:
        for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
            if (length == entry.characters.length) {
                for (int i = 0; i < length; ++i) {
                    if (buffer[offset + i] != entry.characters[i]) {
                        continue Label_0075;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    protected static final class Entry
    {
        public final String symbol;
        public final char[] characters;
        public Entry next;
        
        public Entry(final String symbol, final Entry next) {
            this.symbol = symbol.intern();
            this.characters = new char[symbol.length()];
            symbol.getChars(0, this.characters.length, this.characters, 0);
            this.next = next;
        }
        
        public Entry(final char[] ch, final int offset, final int length, final Entry next) {
            System.arraycopy(ch, offset, this.characters = new char[length], 0, length);
            this.symbol = new String(this.characters).intern();
            this.next = next;
        }
    }
}
