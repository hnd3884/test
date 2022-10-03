package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public abstract class KeyIntMap
{
    public static final int NOT_PRESENT = -1;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int MAXIMUM_CAPACITY = 1048576;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    int _readOnlyMapSize;
    int _size;
    int _capacity;
    int _threshold;
    final float _loadFactor;
    
    public KeyIntMap(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalInitialCapacity", new Object[] { initialCapacity }));
        }
        if (initialCapacity > 1048576) {
            initialCapacity = 1048576;
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalLoadFactor", new Object[] { loadFactor }));
        }
        if (initialCapacity != 16) {
            this._capacity = 1;
            while (this._capacity < initialCapacity) {
                this._capacity <<= 1;
            }
            this._loadFactor = loadFactor;
            this._threshold = (int)(this._capacity * this._loadFactor);
        }
        else {
            this._capacity = 16;
            this._loadFactor = 0.75f;
            this._threshold = 12;
        }
    }
    
    public KeyIntMap(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public KeyIntMap() {
        this._capacity = 16;
        this._loadFactor = 0.75f;
        this._threshold = 12;
    }
    
    public final int size() {
        return this._size + this._readOnlyMapSize;
    }
    
    public abstract void clear();
    
    public abstract void setReadOnlyMap(final KeyIntMap p0, final boolean p1);
    
    public static final int hashHash(int h) {
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    public static final int indexFor(final int h, final int length) {
        return h & length - 1;
    }
    
    static class BaseEntry
    {
        final int _hash;
        final int _value;
        
        public BaseEntry(final int hash, final int value) {
            this._hash = hash;
            this._value = value;
        }
    }
}
