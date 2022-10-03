package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;

public class CacheTable
{
    private boolean noReverseMap;
    static final int INITIAL_SIZE = 16;
    static final int MAX_SIZE = 1073741824;
    int size;
    int entryCount;
    private Entry[] map;
    private Entry[] rmap;
    private ORB orb;
    private ORBUtilSystemException wrapper;
    
    private CacheTable() {
    }
    
    public CacheTable(final ORB orb, final boolean noReverseMap) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.encoding");
        this.noReverseMap = noReverseMap;
        this.size = 16;
        this.entryCount = 0;
        this.initTables();
    }
    
    private void initTables() {
        this.map = new Entry[this.size];
        this.rmap = (Entry[])(this.noReverseMap ? null : new Entry[this.size]);
    }
    
    private void grow() {
        if (this.size == 1073741824) {
            return;
        }
        final Entry[] map = this.map;
        final int size = this.size;
        this.size <<= 1;
        this.initTables();
        for (Entry next : map) {
            while (next != null) {
                this.put_table(next.key, next.val);
                next = next.next;
            }
        }
    }
    
    private int moduloTableSize(int n) {
        n += ~(n << 9);
        n ^= n >>> 14;
        n += n << 4;
        n ^= n >>> 10;
        return n & this.size - 1;
    }
    
    private int hash(final Object o) {
        return this.moduloTableSize(System.identityHashCode(o));
    }
    
    private int hash(final int n) {
        return this.moduloTableSize(n);
    }
    
    public final void put(final Object o, final int n) {
        if (this.put_table(o, n)) {
            ++this.entryCount;
            if (this.entryCount > this.size * 3 / 4) {
                this.grow();
            }
        }
    }
    
    private boolean put_table(final Object o, final int n) {
        final int hash = this.hash(o);
        Entry next = this.map[hash];
        while (next != null) {
            if (next.key == o) {
                if (next.val != n) {
                    throw this.wrapper.duplicateIndirectionOffset();
                }
                return false;
            }
            else {
                next = next.next;
            }
        }
        final Entry entry = new Entry(o, n);
        entry.next = this.map[hash];
        this.map[hash] = entry;
        if (!this.noReverseMap) {
            final int hash2 = this.hash(n);
            entry.rnext = this.rmap[hash2];
            this.rmap[hash2] = entry;
        }
        return true;
    }
    
    public final boolean containsKey(final Object o) {
        return this.getVal(o) != -1;
    }
    
    public final int getVal(final Object o) {
        for (Entry next = this.map[this.hash(o)]; next != null; next = next.next) {
            if (next.key == o) {
                return next.val;
            }
        }
        return -1;
    }
    
    public final boolean containsVal(final int n) {
        return this.getKey(n) != null;
    }
    
    public final boolean containsOrderedVal(final int n) {
        return this.containsVal(n);
    }
    
    public final Object getKey(final int n) {
        for (Entry rnext = this.rmap[this.hash(n)]; rnext != null; rnext = rnext.rnext) {
            if (rnext.val == n) {
                return rnext.key;
            }
        }
        return null;
    }
    
    public void done() {
        this.map = null;
        this.rmap = null;
    }
    
    class Entry
    {
        Object key;
        int val;
        Entry next;
        Entry rnext;
        
        public Entry(final Object key, final int val) {
            this.key = key;
            this.val = val;
            this.next = null;
            this.rnext = null;
        }
    }
}
