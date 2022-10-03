package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringIntMap extends KeyIntMap
{
    protected static final Entry NULL_ENTRY;
    protected StringIntMap _readOnlyMap;
    protected Entry _lastEntry;
    protected Entry[] _table;
    protected int _index;
    protected int _totalCharacterCount;
    
    public StringIntMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this._lastEntry = StringIntMap.NULL_ENTRY;
        this._table = new Entry[this._capacity];
    }
    
    public StringIntMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public StringIntMap() {
        this(16, 0.75f);
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._lastEntry = StringIntMap.NULL_ENTRY;
        this._size = 0;
        this._index = this._readOnlyMapSize;
        this._totalCharacterCount = 0;
    }
    
    @Override
    public void setReadOnlyMap(final KeyIntMap readOnlyMap, final boolean clear) {
        if (!(readOnlyMap instanceof StringIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyMap }));
        }
        this.setReadOnlyMap((StringIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(final StringIntMap readOnlyMap, final boolean clear) {
        this._readOnlyMap = readOnlyMap;
        if (this._readOnlyMap != null) {
            this._readOnlyMapSize = this._readOnlyMap.size();
            this._index = this._size + this._readOnlyMapSize;
            if (clear) {
                this.clear();
            }
        }
        else {
            this._readOnlyMapSize = 0;
            this._index = this._size;
        }
    }
    
    public final int getNextIndex() {
        return this._index++;
    }
    
    public final int getIndex() {
        return this._index;
    }
    
    public final int obtainIndex(final String key) {
        final int hash = KeyIntMap.hashHash(key.hashCode());
        if (this._readOnlyMap != null) {
            final int index = this._readOnlyMap.get(key, hash);
            if (index != -1) {
                return index;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && this.eq(key, e._key)) {
                return e._value;
            }
        }
        this.addEntry(key, hash, tableIndex);
        return -1;
    }
    
    public final void add(final String key) {
        final int hash = KeyIntMap.hashHash(key.hashCode());
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        this.addEntry(key, hash, tableIndex);
    }
    
    public final int get(final String key) {
        if (key == this._lastEntry._key) {
            return this._lastEntry._value;
        }
        return this.get(key, KeyIntMap.hashHash(key.hashCode()));
    }
    
    public final int getTotalCharacterCount() {
        return this._totalCharacterCount;
    }
    
    private final int get(final String key, final int hash) {
        if (this._readOnlyMap != null) {
            final int i = this._readOnlyMap.get(key, hash);
            if (i != -1) {
                return i;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && this.eq(key, e._key)) {
                this._lastEntry = e;
                return e._value;
            }
        }
        return -1;
    }
    
    private final void addEntry(final String key, final int hash, final int bucketIndex) {
        final Entry e = this._table[bucketIndex];
        this._table[bucketIndex] = new Entry(key, hash, this._index++, e);
        this._totalCharacterCount += key.length();
        if (this._size++ >= this._threshold) {
            this.resize(2 * this._table.length);
        }
    }
    
    protected final void resize(final int newCapacity) {
        this._capacity = newCapacity;
        final Entry[] oldTable = this._table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity == 1048576) {
            this._threshold = Integer.MAX_VALUE;
            return;
        }
        final Entry[] newTable = new Entry[this._capacity];
        this.transfer(newTable);
        this._table = newTable;
        this._threshold = (int)(this._capacity * this._loadFactor);
    }
    
    private final void transfer(final Entry[] newTable) {
        final Entry[] src = this._table;
        final int newCapacity = newTable.length;
        for (int j = 0; j < src.length; ++j) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    final Entry next = e._next;
                    final int i = KeyIntMap.indexFor(e._hash, newCapacity);
                    e._next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
    
    private final boolean eq(final String x, final String y) {
        return x == y || x.equals(y);
    }
    
    static {
        NULL_ENTRY = new Entry(null, 0, -1, null);
    }
    
    protected static class Entry extends BaseEntry
    {
        final String _key;
        Entry _next;
        
        public Entry(final String key, final int hash, final int value, final Entry next) {
            super(hash, value);
            this._key = key;
            this._next = next;
        }
    }
}
