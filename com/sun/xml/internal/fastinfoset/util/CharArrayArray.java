package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class CharArrayArray extends ValueArray
{
    private CharArray[] _array;
    private CharArrayArray _readOnlyArray;
    
    public CharArrayArray(final int initialCapacity, final int maximumCapacity) {
        this._array = new CharArray[initialCapacity];
        this._maximumCapacity = maximumCapacity;
    }
    
    public CharArrayArray() {
        this(10, Integer.MAX_VALUE);
    }
    
    @Override
    public final void clear() {
        for (int i = 0; i < this._size; ++i) {
            this._array[i] = null;
        }
        this._size = 0;
    }
    
    public final CharArray[] getArray() {
        if (this._array == null) {
            return null;
        }
        final CharArray[] clonedArray = new CharArray[this._array.length];
        System.arraycopy(this._array, 0, clonedArray, 0, this._array.length);
        return clonedArray;
    }
    
    @Override
    public final void setReadOnlyArray(final ValueArray readOnlyArray, final boolean clear) {
        if (!(readOnlyArray instanceof CharArrayArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyArray }));
        }
        this.setReadOnlyArray((CharArrayArray)readOnlyArray, clear);
    }
    
    public final void setReadOnlyArray(final CharArrayArray readOnlyArray, final boolean clear) {
        if (readOnlyArray != null) {
            this._readOnlyArray = readOnlyArray;
            this._readOnlyArraySize = readOnlyArray.getSize();
            if (clear) {
                this.clear();
            }
        }
    }
    
    public final CharArray get(final int i) {
        if (this._readOnlyArray == null) {
            return this._array[i];
        }
        if (i < this._readOnlyArraySize) {
            return this._readOnlyArray.get(i);
        }
        return this._array[i - this._readOnlyArraySize];
    }
    
    public final void add(final CharArray s) {
        if (this._size == this._array.length) {
            this.resize();
        }
        this._array[this._size++] = s;
    }
    
    protected final void resize() {
        if (this._size == this._maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }
        int newSize = this._size * 3 / 2 + 1;
        if (newSize > this._maximumCapacity) {
            newSize = this._maximumCapacity;
        }
        final CharArray[] newArray = new CharArray[newSize];
        System.arraycopy(this._array, 0, newArray, 0, this._size);
        this._array = newArray;
    }
}
