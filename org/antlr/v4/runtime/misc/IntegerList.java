package org.antlr.v4.runtime.misc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Collection;

public class IntegerList
{
    private static int[] EMPTY_DATA;
    private static final int INITIAL_SIZE = 4;
    private static final int MAX_ARRAY_SIZE = 2147483639;
    private int[] _data;
    private int _size;
    
    public IntegerList() {
        this._data = IntegerList.EMPTY_DATA;
    }
    
    public IntegerList(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }
        if (capacity == 0) {
            this._data = IntegerList.EMPTY_DATA;
        }
        else {
            this._data = new int[capacity];
        }
    }
    
    public IntegerList(final IntegerList list) {
        this._data = list._data.clone();
        this._size = list._size;
    }
    
    public IntegerList(final Collection<Integer> list) {
        this(list.size());
        for (final Integer value : list) {
            this.add(value);
        }
    }
    
    public final void add(final int value) {
        if (this._data.length == this._size) {
            this.ensureCapacity(this._size + 1);
        }
        this._data[this._size] = value;
        ++this._size;
    }
    
    public final void addAll(final int[] array) {
        this.ensureCapacity(this._size + array.length);
        System.arraycopy(array, 0, this._data, this._size, array.length);
        this._size += array.length;
    }
    
    public final void addAll(final IntegerList list) {
        this.ensureCapacity(this._size + list._size);
        System.arraycopy(list._data, 0, this._data, this._size, list._size);
        this._size += list._size;
    }
    
    public final void addAll(final Collection<Integer> list) {
        this.ensureCapacity(this._size + list.size());
        int current = 0;
        for (final int x : list) {
            this._data[this._size + current] = x;
            ++current;
        }
        this._size += list.size();
    }
    
    public final int get(final int index) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException();
        }
        return this._data[index];
    }
    
    public final boolean contains(final int value) {
        for (int i = 0; i < this._size; ++i) {
            if (this._data[i] == value) {
                return true;
            }
        }
        return false;
    }
    
    public final int set(final int index, final int value) {
        if (index < 0 || index >= this._size) {
            throw new IndexOutOfBoundsException();
        }
        final int previous = this._data[index];
        this._data[index] = value;
        return previous;
    }
    
    public final int removeAt(final int index) {
        final int value = this.get(index);
        System.arraycopy(this._data, index + 1, this._data, index, this._size - index - 1);
        this._data[this._size - 1] = 0;
        --this._size;
        return value;
    }
    
    public final void removeRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        System.arraycopy(this._data, toIndex, this._data, fromIndex, this._size - toIndex);
        Arrays.fill(this._data, this._size - (toIndex - fromIndex), this._size, 0);
        this._size -= toIndex - fromIndex;
    }
    
    public final boolean isEmpty() {
        return this._size == 0;
    }
    
    public final int size() {
        return this._size;
    }
    
    public final void trimToSize() {
        if (this._data.length == this._size) {
            return;
        }
        this._data = Arrays.copyOf(this._data, this._size);
    }
    
    public final void clear() {
        Arrays.fill(this._data, 0, this._size, 0);
        this._size = 0;
    }
    
    public final int[] toArray() {
        if (this._size == 0) {
            return IntegerList.EMPTY_DATA;
        }
        return Arrays.copyOf(this._data, this._size);
    }
    
    public final void sort() {
        Arrays.sort(this._data, 0, this._size);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntegerList)) {
            return false;
        }
        final IntegerList other = (IntegerList)o;
        if (this._size != other._size) {
            return false;
        }
        for (int i = 0; i < this._size; ++i) {
            if (this._data[i] != other._data[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (int i = 0; i < this._size; ++i) {
            hashCode = 31 * hashCode + this._data[i];
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.toArray());
    }
    
    public final int binarySearch(final int key) {
        return Arrays.binarySearch(this._data, 0, this._size, key);
    }
    
    public final int binarySearch(final int fromIndex, final int toIndex, final int key) {
        if (fromIndex < 0 || toIndex < 0 || fromIndex > this._size || toIndex > this._size) {
            throw new IndexOutOfBoundsException();
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        return Arrays.binarySearch(this._data, fromIndex, toIndex, key);
    }
    
    private void ensureCapacity(final int capacity) {
        if (capacity < 0 || capacity > 2147483639) {
            throw new OutOfMemoryError();
        }
        int newLength;
        if (this._data.length == 0) {
            newLength = 4;
        }
        else {
            newLength = this._data.length;
        }
        while (newLength < capacity) {
            newLength *= 2;
            if (newLength < 0 || newLength > 2147483639) {
                newLength = 2147483639;
            }
        }
        this._data = Arrays.copyOf(this._data, newLength);
    }
    
    static {
        IntegerList.EMPTY_DATA = new int[0];
    }
}
