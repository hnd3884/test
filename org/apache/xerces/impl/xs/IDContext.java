package org.apache.xerces.impl.xs;

final class IDContext
{
    private static final int INITIAL_MATCH_SIZE = 16;
    private int[] fElementIDTable;
    private int fIDDepth;
    private int fIDScope;
    private int fElementScope;
    private int fElementDepth;
    private final int fTableSize = 101;
    private final Entry[] fBuckets;
    private int fNum;
    
    IDContext() {
        this.fElementIDTable = new int[16];
        this.fIDDepth = 0;
        this.fIDScope = 0;
        this.fElementScope = -1;
        this.fElementDepth = 0;
        this.fNum = 0;
        this.fBuckets = new Entry[101];
    }
    
    void clear() {
        if (this.fNum > 0) {
            for (int i = 0; i < 101; ++i) {
                this.fBuckets[i] = null;
            }
        }
        final int fElementDepth = 0;
        this.fNum = fElementDepth;
        this.fIDDepth = fElementDepth;
        this.fElementDepth = fElementDepth;
        final int n = -1;
        this.fIDScope = n;
        this.fElementScope = n;
    }
    
    void setCurrentScopeToParent() {
        if (this.fElementScope > 0) {
            final int[] fElementIDTable = this.fElementIDTable;
            final int fElementScope = this.fElementScope - 1;
            this.fElementScope = fElementScope;
            this.fIDScope = fElementIDTable[fElementScope];
        }
        else {
            final int n = -1;
            this.fIDScope = n;
            this.fElementScope = n;
        }
    }
    
    void popContext() {
        --this.fElementDepth;
    }
    
    void pushContext() {
        if (this.fElementDepth == this.fElementIDTable.length) {
            this.resizeElementDepthIDTable();
        }
        this.fElementScope = this.fElementDepth++;
        this.fElementIDTable[this.fElementScope] = (this.fIDScope = 0);
    }
    
    private void resizeElementDepthIDTable() {
        final int[] fElementIDTable = new int[this.fElementDepth << 1];
        System.arraycopy(this.fElementIDTable, 0, fElementIDTable, 0, this.fElementDepth);
        this.fElementIDTable = fElementIDTable;
    }
    
    boolean isDeclared(final String s) {
        final int value = this.get(s);
        if (value == -1) {
            return this.fIDScope == -1;
        }
        return value != this.fIDScope;
    }
    
    boolean containsID(final String s) {
        for (Entry next = this.fBuckets[(s.hashCode() & Integer.MAX_VALUE) % 101]; next != null; next = next.next) {
            if (s.equals(next.key)) {
                return true;
            }
        }
        return false;
    }
    
    private int get(final String s) {
        for (Entry next = this.fBuckets[(s.hashCode() & Integer.MAX_VALUE) % 101]; next != null; next = next.next) {
            if (s.equals(next.key)) {
                return next.value;
            }
        }
        return -1;
    }
    
    void add(final String s) {
        final int n = (s.hashCode() & Integer.MAX_VALUE) % 101;
        if (this.search(s, n) == null) {
            if (this.fElementIDTable[this.fElementScope] == 0) {
                this.fElementIDTable[this.fElementScope] = (this.fIDScope = ++this.fIDDepth);
            }
            this.fBuckets[n] = new Entry(s, this.fIDScope, this.fBuckets[n]);
            ++this.fNum;
        }
    }
    
    private Entry search(final String s, final int n) {
        for (Entry next = this.fBuckets[n]; next != null; next = next.next) {
            if (s.equals(next.key)) {
                return next;
            }
        }
        return null;
    }
    
    private static final class Entry
    {
        public String key;
        public int value;
        public Entry next;
        
        public Entry() {
            this.key = null;
            this.value = -1;
            this.next = null;
        }
        
        public Entry(final String key, final int value, final Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
