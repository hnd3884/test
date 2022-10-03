package org.apache.xerces.util;

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
    
    public SymbolTable(int fTableSize, final float fLoadFactor) {
        this.fBuckets = null;
        if (fTableSize < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + fTableSize);
        }
        if (fLoadFactor <= 0.0f || Float.isNaN(fLoadFactor)) {
            throw new IllegalArgumentException("Illegal Load: " + fLoadFactor);
        }
        if (fTableSize == 0) {
            fTableSize = 1;
        }
        this.fLoadFactor = fLoadFactor;
        this.fTableSize = fTableSize;
        this.fBuckets = new Entry[this.fTableSize];
        this.fThreshold = (int)(this.fTableSize * fLoadFactor);
        this.fCollisionThreshold = (int)(40.0f * fLoadFactor);
        this.fCount = 0;
    }
    
    public SymbolTable(final int n) {
        this(n, 0.75f);
    }
    
    public SymbolTable() {
        this(101, 0.75f);
    }
    
    public String addSymbol(final String s) {
        int n = 0;
        final int n2 = this.hash(s) % this.fTableSize;
        for (Entry next = this.fBuckets[n2]; next != null; next = next.next) {
            if (next.symbol.equals(s)) {
                return next.symbol;
            }
            ++n;
        }
        return this.addSymbol0(s, n2, n);
    }
    
    private String addSymbol0(final String s, int n, final int n2) {
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n = this.hash(s) % this.fTableSize;
        }
        else if (n2 >= this.fCollisionThreshold) {
            this.rebalance();
            n = this.hash(s) % this.fTableSize;
        }
        final Entry entry = new Entry(s, this.fBuckets[n]);
        this.fBuckets[n] = entry;
        ++this.fCount;
        return entry.symbol;
    }
    
    public String addSymbol(final char[] array, final int n, final int n2) {
        int n3 = 0;
        final int n4 = this.hash(array, n, n2) % this.fTableSize;
    Label_0088:
        for (Entry next = this.fBuckets[n4]; next != null; next = next.next) {
            if (n2 == next.characters.length) {
                for (int i = 0; i < n2; ++i) {
                    if (array[n + i] != next.characters[i]) {
                        ++n3;
                        continue Label_0088;
                    }
                }
                return next.symbol;
            }
            ++n3;
        }
        return this.addSymbol0(array, n, n2, n4, n3);
    }
    
    private String addSymbol0(final char[] array, final int n, final int n2, int n3, final int n4) {
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n3 = this.hash(array, n, n2) % this.fTableSize;
        }
        else if (n4 >= this.fCollisionThreshold) {
            this.rebalance();
            n3 = this.hash(array, n, n2) % this.fTableSize;
        }
        final Entry entry = new Entry(array, n, n2, this.fBuckets[n3]);
        this.fBuckets[n3] = entry;
        ++this.fCount;
        return entry.symbol;
    }
    
    public int hash(final String s) {
        if (this.fHashMultipliers == null) {
            return s.hashCode() & Integer.MAX_VALUE;
        }
        return this.hash0(s);
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
    
    public int hash(final char[] array, final int n, final int n2) {
        if (this.fHashMultipliers == null) {
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                n3 = n3 * 31 + array[n + i];
            }
            return n3 & Integer.MAX_VALUE;
        }
        return this.hash0(array, n, n2);
    }
    
    private int hash0(final char[] array, final int n, final int n2) {
        int n3 = 0;
        final int[] fHashMultipliers = this.fHashMultipliers;
        for (int i = 0; i < n2; ++i) {
            n3 = n3 * fHashMultipliers[i & 0x1F] + array[n + i];
        }
        return n3 & Integer.MAX_VALUE;
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
    
    private void rehashCommon(final int n) {
        final int length = this.fBuckets.length;
        final Entry[] fBuckets = this.fBuckets;
        final Entry[] fBuckets2 = new Entry[n];
        this.fThreshold = (int)(n * this.fLoadFactor);
        this.fBuckets = fBuckets2;
        this.fTableSize = this.fBuckets.length;
        int n2 = length;
        while (n2-- > 0) {
            Entry entry;
            int n3;
            for (Entry next = fBuckets[n2]; next != null; next = next.next, n3 = this.hash(entry.symbol) % n, entry.next = fBuckets2[n3], fBuckets2[n3] = entry) {
                entry = next;
            }
        }
    }
    
    public boolean containsSymbol(final String s) {
        final int n = this.hash(s) % this.fTableSize;
        final int length = s.length();
    Label_0076:
        for (Entry next = this.fBuckets[n]; next != null; next = next.next) {
            if (length == next.characters.length) {
                for (int i = 0; i < length; ++i) {
                    if (s.charAt(i) != next.characters[i]) {
                        continue Label_0076;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean containsSymbol(final char[] array, final int n, final int n2) {
    Label_0075:
        for (Entry next = this.fBuckets[this.hash(array, n, n2) % this.fTableSize]; next != null; next = next.next) {
            if (n2 == next.characters.length) {
                for (int i = 0; i < n2; ++i) {
                    if (array[n + i] != next.characters[i]) {
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
        
        public Entry(final String s, final Entry next) {
            this.symbol = s.intern();
            this.characters = new char[s.length()];
            s.getChars(0, this.characters.length, this.characters, 0);
            this.next = next;
        }
        
        public Entry(final char[] array, final int n, final int n2, final Entry next) {
            System.arraycopy(array, n, this.characters = new char[n2], 0, n2);
            this.symbol = new String(this.characters).intern();
            this.next = next;
        }
    }
}
