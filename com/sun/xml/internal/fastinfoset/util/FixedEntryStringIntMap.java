package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class FixedEntryStringIntMap extends StringIntMap
{
    private Entry _fixedEntry;
    
    public FixedEntryStringIntMap(final String fixedEntry, final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        final int hash = KeyIntMap.hashHash(fixedEntry.hashCode());
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        this._table[tableIndex] = (this._fixedEntry = new Entry(fixedEntry, hash, this._index++, null));
        if (this._size++ >= this._threshold) {
            this.resize(2 * this._table.length);
        }
    }
    
    public FixedEntryStringIntMap(final String fixedEntry, final int initialCapacity) {
        this(fixedEntry, initialCapacity, 0.75f);
    }
    
    public FixedEntryStringIntMap(final String fixedEntry) {
        this(fixedEntry, 16, 0.75f);
    }
    
    @Override
    public final void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._lastEntry = FixedEntryStringIntMap.NULL_ENTRY;
        if (this._fixedEntry != null) {
            final int tableIndex = KeyIntMap.indexFor(this._fixedEntry._hash, this._table.length);
            this._table[tableIndex] = this._fixedEntry;
            this._fixedEntry._next = null;
            this._size = 1;
            this._index = this._readOnlyMapSize + 1;
        }
        else {
            this._size = 0;
            this._index = this._readOnlyMapSize;
        }
    }
    
    @Override
    public final void setReadOnlyMap(final KeyIntMap readOnlyMap, final boolean clear) {
        if (!(readOnlyMap instanceof FixedEntryStringIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyMap }));
        }
        this.setReadOnlyMap((FixedEntryStringIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(final FixedEntryStringIntMap readOnlyMap, final boolean clear) {
        this._readOnlyMap = readOnlyMap;
        if (this._readOnlyMap != null) {
            readOnlyMap.removeFixedEntry();
            this._readOnlyMapSize = readOnlyMap.size();
            this._index = this._readOnlyMapSize + this._size;
            if (clear) {
                this.clear();
            }
        }
        else {
            this._readOnlyMapSize = 0;
        }
    }
    
    private final void removeFixedEntry() {
        if (this._fixedEntry != null) {
            final int tableIndex = KeyIntMap.indexFor(this._fixedEntry._hash, this._table.length);
            final Entry firstEntry = this._table[tableIndex];
            if (firstEntry == this._fixedEntry) {
                this._table[tableIndex] = this._fixedEntry._next;
            }
            else {
                Entry previousEntry;
                for (previousEntry = firstEntry; previousEntry._next != this._fixedEntry; previousEntry = previousEntry._next) {}
                previousEntry._next = this._fixedEntry._next;
            }
            this._fixedEntry = null;
            --this._size;
        }
    }
}
