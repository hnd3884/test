package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class LocalNameQualifiedNamesMap extends KeyIntMap
{
    private LocalNameQualifiedNamesMap _readOnlyMap;
    private int _index;
    private Entry[] _table;
    
    public LocalNameQualifiedNamesMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this._table = new Entry[this._capacity];
    }
    
    public LocalNameQualifiedNamesMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public LocalNameQualifiedNamesMap() {
        this(16, 0.75f);
    }
    
    @Override
    public final void clear() {
        for (int i = 0; i < this._table.length; ++i) {
            this._table[i] = null;
        }
        this._size = 0;
        if (this._readOnlyMap != null) {
            this._index = this._readOnlyMap.getIndex();
        }
        else {
            this._index = 0;
        }
    }
    
    @Override
    public final void setReadOnlyMap(final KeyIntMap readOnlyMap, final boolean clear) {
        if (!(readOnlyMap instanceof LocalNameQualifiedNamesMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyMap }));
        }
        this.setReadOnlyMap((LocalNameQualifiedNamesMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(final LocalNameQualifiedNamesMap readOnlyMap, final boolean clear) {
        this._readOnlyMap = readOnlyMap;
        if (this._readOnlyMap != null) {
            this._readOnlyMapSize = this._readOnlyMap.size();
            this._index = this._readOnlyMap.getIndex();
            if (clear) {
                this.clear();
            }
        }
        else {
            this._readOnlyMapSize = 0;
            this._index = 0;
        }
    }
    
    public final boolean isQNameFromReadOnlyMap(final QualifiedName name) {
        return this._readOnlyMap != null && name.index <= this._readOnlyMap.getIndex();
    }
    
    public final int getNextIndex() {
        return this._index++;
    }
    
    public final int getIndex() {
        return this._index;
    }
    
    public final Entry obtainEntry(final String key) {
        final int hash = KeyIntMap.hashHash(key.hashCode());
        if (this._readOnlyMap != null) {
            final Entry entry = this._readOnlyMap.getEntry(key, hash);
            if (entry != null) {
                return entry;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && this.eq(key, e._key)) {
                return e;
            }
        }
        return this.addEntry(key, hash, tableIndex);
    }
    
    public final Entry obtainDynamicEntry(final String key) {
        final int hash = KeyIntMap.hashHash(key.hashCode());
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && this.eq(key, e._key)) {
                return e;
            }
        }
        return this.addEntry(key, hash, tableIndex);
    }
    
    private final Entry getEntry(final String key, final int hash) {
        if (this._readOnlyMap != null) {
            final Entry entry = this._readOnlyMap.getEntry(key, hash);
            if (entry != null) {
                return entry;
            }
        }
        final int tableIndex = KeyIntMap.indexFor(hash, this._table.length);
        for (Entry e = this._table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && this.eq(key, e._key)) {
                return e;
            }
        }
        return null;
    }
    
    private final Entry addEntry(final String key, final int hash, final int bucketIndex) {
        Entry e = this._table[bucketIndex];
        this._table[bucketIndex] = new Entry(key, hash, e);
        e = this._table[bucketIndex];
        if (this._size++ >= this._threshold) {
            this.resize(2 * this._table.length);
        }
        return e;
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
    
    private final boolean eq(final String x, final String y) {
        return x == y || x.equals(y);
    }
    
    public static class Entry
    {
        final String _key;
        final int _hash;
        public QualifiedName[] _value;
        public int _valueIndex;
        Entry _next;
        
        public Entry(final String key, final int hash, final Entry next) {
            this._key = key;
            this._hash = hash;
            this._next = next;
            this._value = new QualifiedName[1];
        }
        
        public void addQualifiedName(final QualifiedName name) {
            if (this._valueIndex < this._value.length) {
                this._value[this._valueIndex++] = name;
            }
            else if (this._valueIndex == this._value.length) {
                final QualifiedName[] newValue = new QualifiedName[this._valueIndex * 3 / 2 + 1];
                System.arraycopy(this._value, 0, newValue, 0, this._valueIndex);
                (this._value = newValue)[this._valueIndex++] = name;
            }
        }
    }
}
