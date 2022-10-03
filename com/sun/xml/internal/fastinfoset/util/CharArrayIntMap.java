package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayIntMap extends KeyIntMap
{
    private CharArrayIntMap _readOnlyMap;
    protected int _totalCharacterCount;
    private Entry[] _table;
    
    public CharArrayIntMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this._table = new Entry[this._capacity];
    }
    
    public CharArrayIntMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public CharArrayIntMap() {
        this(16, 0.75f);
    }
    
    @Override
    public final void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._size = 0;
        this._totalCharacterCount = 0;
    }
    
    @Override
    public final void setReadOnlyMap(final KeyIntMap readOnlyMap, final boolean clear) {
        if (!(readOnlyMap instanceof CharArrayIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyMap }));
        }
        this.setReadOnlyMap((CharArrayIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(final CharArrayIntMap readOnlyMap, final boolean clear) {
        this._readOnlyMap = readOnlyMap;
        if (this._readOnlyMap != null) {
            this._readOnlyMapSize = this._readOnlyMap.size();
            if (clear) {
                this.clear();
            }
        }
        else {
            this._readOnlyMapSize = 0;
        }
    }
    
    public final int get(final char[] ch, final int start, final int length) {
        final int hash = KeyIntMap.hashHash(CharArray.hashCode(ch, start, length));
        return this.get(ch, start, length, hash);
    }
    
    public final int obtainIndex(char[] ch, int start, final int length, final boolean clone) {
        final int hash = KeyIntMap.hashHash(CharArray.hashCode(ch, start, length));
        if (this._readOnlyMap != null) {
            final int index = this._readOnlyMap.get(ch, start, length, hash);
            if (index != -1) {
                return index;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
                return e._value;
            }
        }
        if (clone) {
            final char[] chClone = new char[length];
            System.arraycopy(ch, start, chClone, 0, length);
            ch = chClone;
            start = 0;
        }
        this.addEntry(ch, start, length, hash, this._size + this._readOnlyMapSize, tableIndex);
        return -1;
    }
    
    public final int getTotalCharacterCount() {
        return this._totalCharacterCount;
    }
    
    private final int get(final char[] ch, final int start, final int length, final int hash) {
        if (this._readOnlyMap != null) {
            final int i = this._readOnlyMap.get(ch, start, length, hash);
            if (i != -1) {
                return i;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
                return e._value;
            }
        }
        return -1;
    }
    
    private final void addEntry(final char[] ch, final int start, final int length, final int hash, final int value, final int bucketIndex) {
        final Entry e = this._table[bucketIndex];
        this._table[bucketIndex] = new Entry(ch, start, length, hash, value, e);
        this._totalCharacterCount += length;
        if (this._size++ >= this._threshold) {
            this.resize(2 * this._table.length);
        }
    }
    
    private final void resize(final int newCapacity) {
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
    
    static class Entry extends BaseEntry
    {
        final char[] _ch;
        final int _start;
        final int _length;
        Entry _next;
        
        public Entry(final char[] ch, final int start, final int length, final int hash, final int value, final Entry next) {
            super(hash, value);
            this._ch = ch;
            this._start = start;
            this._length = length;
            this._next = next;
        }
        
        public final boolean equalsCharArray(final char[] ch, final int start, final int length) {
            if (this._length == length) {
                int n = this._length;
                int i = this._start;
                int j = start;
                while (n-- != 0) {
                    if (this._ch[i++] != ch[j++]) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}
