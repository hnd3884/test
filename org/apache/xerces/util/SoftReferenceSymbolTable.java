package org.apache.xerces.util;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

public class SoftReferenceSymbolTable extends SymbolTable
{
    protected SREntry[] fBuckets;
    private final ReferenceQueue fReferenceQueue;
    
    public SoftReferenceSymbolTable(int fTableSize, final float fLoadFactor) {
        super(1, fLoadFactor);
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
        this.fBuckets = new SREntry[this.fTableSize];
        this.fThreshold = (int)(this.fTableSize * fLoadFactor);
        this.fCount = 0;
        this.fReferenceQueue = new ReferenceQueue();
    }
    
    public SoftReferenceSymbolTable(final int n) {
        this(n, 0.75f);
    }
    
    public SoftReferenceSymbolTable() {
        this(101, 0.75f);
    }
    
    public String addSymbol(final String s) {
        this.clean();
        int n = 0;
        final int n2 = this.hash(s) % this.fTableSize;
        for (SREntry next = this.fBuckets[n2]; next != null; next = next.next) {
            final SREntryData srEntryData = next.get();
            if (srEntryData != null) {
                if (srEntryData.symbol.equals(s)) {
                    return srEntryData.symbol;
                }
                ++n;
            }
        }
        return this.addSymbol0(s, n2, n);
    }
    
    private String addSymbol0(String intern, int n, final int n2) {
        if (this.fCount >= this.fThreshold) {
            this.rehash();
            n = this.hash(intern) % this.fTableSize;
        }
        else if (n2 >= this.fCollisionThreshold) {
            this.rebalance();
            n = this.hash(intern) % this.fTableSize;
        }
        intern = intern.intern();
        this.fBuckets[n] = new SREntry(intern, this.fBuckets[n], n, this.fReferenceQueue);
        ++this.fCount;
        return intern;
    }
    
    public String addSymbol(final char[] array, final int n, final int n2) {
        this.clean();
        int n3 = 0;
        final int n4 = this.hash(array, n, n2) % this.fTableSize;
    Label_0110:
        for (SREntry next = this.fBuckets[n4]; next != null; next = next.next) {
            final SREntryData srEntryData = next.get();
            if (srEntryData != null) {
                if (n2 == srEntryData.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (array[n + i] != srEntryData.characters[i]) {
                            ++n3;
                            continue Label_0110;
                        }
                    }
                    return srEntryData.symbol;
                }
                ++n3;
            }
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
        final String intern = new String(array, n, n2).intern();
        this.fBuckets[n3] = new SREntry(intern, array, n, n2, this.fBuckets[n3], n3, this.fReferenceQueue);
        ++this.fCount;
        return intern;
    }
    
    protected void rehash() {
        this.rehashCommon(this.fBuckets.length * 2 + 1);
    }
    
    protected void compact() {
        this.rehashCommon((int)(this.fCount / this.fLoadFactor) * 2 + 1);
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
        final SREntry[] fBuckets = this.fBuckets;
        final SREntry[] fBuckets2 = new SREntry[n];
        this.fThreshold = (int)(n * this.fLoadFactor);
        this.fBuckets = fBuckets2;
        this.fTableSize = this.fBuckets.length;
        int n2 = length;
        while (n2-- > 0) {
            SREntry next = fBuckets[n2];
            while (next != null) {
                final SREntry prev = next;
                next = next.next;
                final SREntryData srEntryData = prev.get();
                if (srEntryData != null) {
                    final int bucket = this.hash(srEntryData.symbol) % n;
                    if (fBuckets2[bucket] != null) {
                        fBuckets2[bucket].prev = prev;
                    }
                    prev.bucket = bucket;
                    prev.next = fBuckets2[bucket];
                    fBuckets2[bucket] = prev;
                }
                else {
                    prev.bucket = -1;
                    prev.next = null;
                    --this.fCount;
                }
                prev.prev = null;
            }
        }
    }
    
    public boolean containsSymbol(final String s) {
        final int n = this.hash(s) % this.fTableSize;
        final int length = s.length();
    Label_0094:
        for (SREntry next = this.fBuckets[n]; next != null; next = next.next) {
            final SREntryData srEntryData = next.get();
            if (srEntryData != null) {
                if (length == srEntryData.characters.length) {
                    for (int i = 0; i < length; ++i) {
                        if (s.charAt(i) != srEntryData.characters[i]) {
                            continue Label_0094;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsSymbol(final char[] array, final int n, final int n2) {
    Label_0093:
        for (SREntry next = this.fBuckets[this.hash(array, n, n2) % this.fTableSize]; next != null; next = next.next) {
            final SREntryData srEntryData = next.get();
            if (srEntryData != null) {
                if (n2 == srEntryData.characters.length) {
                    for (int i = 0; i < n2; ++i) {
                        if (array[n + i] != srEntryData.characters[i]) {
                            continue Label_0093;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private void removeEntry(final SREntry srEntry) {
        final int bucket = srEntry.bucket;
        if (bucket >= 0) {
            if (srEntry.next != null) {
                srEntry.next.prev = srEntry.prev;
            }
            if (srEntry.prev != null) {
                srEntry.prev.next = srEntry.next;
            }
            else {
                this.fBuckets[bucket] = srEntry.next;
            }
            --this.fCount;
        }
    }
    
    private void clean() {
        SREntry srEntry = (SREntry)this.fReferenceQueue.poll();
        if (srEntry != null) {
            do {
                this.removeEntry(srEntry);
                srEntry = (SREntry)this.fReferenceQueue.poll();
            } while (srEntry != null);
            if (this.fCount < this.fThreshold >> 2) {
                this.compact();
            }
        }
    }
    
    protected static final class SREntry extends SoftReference
    {
        public SREntry next;
        public SREntry prev;
        public int bucket;
        
        public SREntry(final String s, final SREntry srEntry, final int n, final ReferenceQueue referenceQueue) {
            super(new SREntryData(s), referenceQueue);
            this.initialize(srEntry, n);
        }
        
        public SREntry(final String s, final char[] array, final int n, final int n2, final SREntry srEntry, final int n3, final ReferenceQueue referenceQueue) {
            super(new SREntryData(s, array, n, n2), referenceQueue);
            this.initialize(srEntry, n3);
        }
        
        private void initialize(final SREntry next, final int bucket) {
            this.next = next;
            if (next != null) {
                next.prev = this;
            }
            this.prev = null;
            this.bucket = bucket;
        }
    }
    
    protected static final class SREntryData
    {
        public final String symbol;
        public final char[] characters;
        
        public SREntryData(final String symbol) {
            this.symbol = symbol;
            this.characters = new char[this.symbol.length()];
            this.symbol.getChars(0, this.characters.length, this.characters, 0);
        }
        
        public SREntryData(final String symbol, final char[] array, final int n, final int n2) {
            this.symbol = symbol;
            System.arraycopy(array, n, this.characters = new char[n2], 0, n2);
        }
    }
}
