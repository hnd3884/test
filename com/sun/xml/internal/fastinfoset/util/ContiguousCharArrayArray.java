package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class ContiguousCharArrayArray extends ValueArray
{
    public static final int INITIAL_CHARACTER_SIZE = 512;
    public static final int MAXIMUM_CHARACTER_SIZE = Integer.MAX_VALUE;
    protected int _maximumCharacterSize;
    public int[] _offset;
    public int[] _length;
    public char[] _array;
    public int _arrayIndex;
    public int _readOnlyArrayIndex;
    private String[] _cachedStrings;
    public int _cachedIndex;
    private ContiguousCharArrayArray _readOnlyArray;
    
    public ContiguousCharArrayArray(final int initialCapacity, final int maximumCapacity, final int initialCharacterSize, final int maximumCharacterSize) {
        this._offset = new int[initialCapacity];
        this._length = new int[initialCapacity];
        this._array = new char[initialCharacterSize];
        this._maximumCapacity = maximumCapacity;
        this._maximumCharacterSize = maximumCharacterSize;
    }
    
    public ContiguousCharArrayArray() {
        this(10, Integer.MAX_VALUE, 512, Integer.MAX_VALUE);
    }
    
    @Override
    public final void clear() {
        this._arrayIndex = this._readOnlyArrayIndex;
        this._size = this._readOnlyArraySize;
        if (this._cachedStrings != null) {
            for (int i = this._readOnlyArraySize; i < this._cachedStrings.length; ++i) {
                this._cachedStrings[i] = null;
            }
        }
    }
    
    public final int getArrayIndex() {
        return this._arrayIndex;
    }
    
    @Override
    public final void setReadOnlyArray(final ValueArray readOnlyArray, final boolean clear) {
        if (!(readOnlyArray instanceof ContiguousCharArrayArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { readOnlyArray }));
        }
        this.setReadOnlyArray((ContiguousCharArrayArray)readOnlyArray, clear);
    }
    
    public final void setReadOnlyArray(final ContiguousCharArrayArray readOnlyArray, final boolean clear) {
        if (readOnlyArray != null) {
            this._readOnlyArray = readOnlyArray;
            this._readOnlyArraySize = readOnlyArray.getSize();
            this._readOnlyArrayIndex = readOnlyArray.getArrayIndex();
            if (clear) {
                this.clear();
            }
            this._array = this.getCompleteCharArray();
            this._offset = this.getCompleteOffsetArray();
            this._length = this.getCompleteLengthArray();
            this._size = this._readOnlyArraySize;
            this._arrayIndex = this._readOnlyArrayIndex;
        }
    }
    
    public final char[] getCompleteCharArray() {
        if (this._readOnlyArray != null) {
            final char[] ra = this._readOnlyArray.getCompleteCharArray();
            final char[] a = new char[this._readOnlyArrayIndex + this._array.length];
            System.arraycopy(ra, 0, a, 0, this._readOnlyArrayIndex);
            return a;
        }
        if (this._array == null) {
            return null;
        }
        final char[] clonedArray = new char[this._array.length];
        System.arraycopy(this._array, 0, clonedArray, 0, this._array.length);
        return clonedArray;
    }
    
    public final int[] getCompleteOffsetArray() {
        if (this._readOnlyArray != null) {
            final int[] ra = this._readOnlyArray.getCompleteOffsetArray();
            final int[] a = new int[this._readOnlyArraySize + this._offset.length];
            System.arraycopy(ra, 0, a, 0, this._readOnlyArraySize);
            return a;
        }
        if (this._offset == null) {
            return null;
        }
        final int[] clonedArray = new int[this._offset.length];
        System.arraycopy(this._offset, 0, clonedArray, 0, this._offset.length);
        return clonedArray;
    }
    
    public final int[] getCompleteLengthArray() {
        if (this._readOnlyArray != null) {
            final int[] ra = this._readOnlyArray.getCompleteLengthArray();
            final int[] a = new int[this._readOnlyArraySize + this._length.length];
            System.arraycopy(ra, 0, a, 0, this._readOnlyArraySize);
            return a;
        }
        if (this._length == null) {
            return null;
        }
        final int[] clonedArray = new int[this._length.length];
        System.arraycopy(this._length, 0, clonedArray, 0, this._length.length);
        return clonedArray;
    }
    
    public final String getString(final int i) {
        if (this._cachedStrings != null && i < this._cachedStrings.length) {
            final String s = this._cachedStrings[i];
            return (s != null) ? s : (this._cachedStrings[i] = new String(this._array, this._offset[i], this._length[i]));
        }
        final String[] newCachedStrings = new String[this._offset.length];
        if (this._cachedStrings != null && i >= this._cachedStrings.length) {
            System.arraycopy(this._cachedStrings, 0, newCachedStrings, 0, this._cachedStrings.length);
        }
        this._cachedStrings = newCachedStrings;
        return this._cachedStrings[i] = new String(this._array, this._offset[i], this._length[i]);
    }
    
    public final void ensureSize(final int l) {
        if (this._arrayIndex + l >= this._array.length) {
            this.resizeArray(this._arrayIndex + l);
        }
    }
    
    public final void add(final int l) {
        if (this._size == this._offset.length) {
            this.resize();
        }
        this._cachedIndex = this._size;
        this._offset[this._size] = this._arrayIndex;
        this._length[this._size++] = l;
        this._arrayIndex += l;
    }
    
    public final int add(final char[] c, final int l) {
        if (this._size == this._offset.length) {
            this.resize();
        }
        final int oldArrayIndex = this._arrayIndex;
        final int arrayIndex = oldArrayIndex + l;
        this._cachedIndex = this._size;
        this._offset[this._size] = oldArrayIndex;
        this._length[this._size++] = l;
        if (arrayIndex >= this._array.length) {
            this.resizeArray(arrayIndex);
        }
        System.arraycopy(c, 0, this._array, oldArrayIndex, l);
        this._arrayIndex = arrayIndex;
        return oldArrayIndex;
    }
    
    protected final void resize() {
        if (this._size == this._maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }
        int newSize = this._size * 3 / 2 + 1;
        if (newSize > this._maximumCapacity) {
            newSize = this._maximumCapacity;
        }
        final int[] offset = new int[newSize];
        System.arraycopy(this._offset, 0, offset, 0, this._size);
        this._offset = offset;
        final int[] length = new int[newSize];
        System.arraycopy(this._length, 0, length, 0, this._size);
        this._length = length;
    }
    
    protected final void resizeArray(final int requestedSize) {
        if (this._arrayIndex == this._maximumCharacterSize) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.maxNumberOfCharacters"));
        }
        int newSize = requestedSize * 3 / 2 + 1;
        if (newSize > this._maximumCharacterSize) {
            newSize = this._maximumCharacterSize;
        }
        final char[] array = new char[newSize];
        System.arraycopy(this._array, 0, array, 0, this._arrayIndex);
        this._array = array;
    }
}
