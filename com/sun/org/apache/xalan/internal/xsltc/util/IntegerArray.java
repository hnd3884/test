package com.sun.org.apache.xalan.internal.xsltc.util;

import java.io.PrintStream;

public final class IntegerArray
{
    private static final int InitialSize = 32;
    private int[] _array;
    private int _size;
    private int _free;
    
    public IntegerArray() {
        this(32);
    }
    
    public IntegerArray(final int size) {
        this._free = 0;
        this._size = size;
        this._array = new int[size];
    }
    
    public IntegerArray(final int[] array) {
        this(array.length);
        System.arraycopy(array, 0, this._array, 0, this._free = this._size);
    }
    
    public void clear() {
        this._free = 0;
    }
    
    public Object clone() {
        final IntegerArray clone = new IntegerArray((this._free > 0) ? this._free : 1);
        System.arraycopy(this._array, 0, clone._array, 0, this._free);
        clone._free = this._free;
        return clone;
    }
    
    public int[] toIntArray() {
        final int[] result = new int[this.cardinality()];
        System.arraycopy(this._array, 0, result, 0, this.cardinality());
        return result;
    }
    
    public final int at(final int index) {
        return this._array[index];
    }
    
    public final void set(final int index, final int value) {
        this._array[index] = value;
    }
    
    public int indexOf(final int n) {
        for (int i = 0; i < this._free; ++i) {
            if (n == this._array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public final void add(final int value) {
        if (this._free == this._size) {
            this.growArray(this._size * 2);
        }
        this._array[this._free++] = value;
    }
    
    public void addNew(final int value) {
        for (int i = 0; i < this._free; ++i) {
            if (this._array[i] == value) {
                return;
            }
        }
        this.add(value);
    }
    
    public void reverse() {
        int temp;
        for (int left = 0, right = this._free - 1; left < right; this._array[left++] = this._array[right], this._array[right--] = temp) {
            temp = this._array[left];
        }
    }
    
    public void merge(final IntegerArray other) {
        final int newSize = this._free + other._free;
        final int[] newArray = new int[newSize];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < this._free && j < other._free) {
            final int x = this._array[i];
            final int y = other._array[j];
            if (x < y) {
                newArray[k] = x;
                ++i;
            }
            else if (x > y) {
                newArray[k] = y;
                ++j;
            }
            else {
                newArray[k] = x;
                ++i;
                ++j;
            }
            ++k;
        }
        if (i >= this._free) {
            while (j < other._free) {
                newArray[k++] = other._array[j++];
            }
        }
        else {
            while (i < this._free) {
                newArray[k++] = this._array[i++];
            }
        }
        this._array = newArray;
        final int n = newSize;
        this._size = n;
        this._free = n;
    }
    
    public void sort() {
        quicksort(this._array, 0, this._free - 1);
    }
    
    private static void quicksort(final int[] array, final int p, final int r) {
        if (p < r) {
            final int q = partition(array, p, r);
            quicksort(array, p, q);
            quicksort(array, q + 1, r);
        }
    }
    
    private static int partition(final int[] array, final int p, final int r) {
        final int x = array[p + r >>> 1];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x < array[--j]) {
                continue;
            }
            while (x > array[++i]) {}
            if (i >= j) {
                break;
            }
            final int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return j;
    }
    
    private void growArray(final int size) {
        this._size = size;
        final int[] newArray = new int[size];
        System.arraycopy(this._array, 0, newArray, 0, this._free);
        this._array = newArray;
    }
    
    public int popLast() {
        final int[] array = this._array;
        final int free = this._free - 1;
        this._free = free;
        return array[free];
    }
    
    public int last() {
        return this._array[this._free - 1];
    }
    
    public void setLast(final int n) {
        this._array[this._free - 1] = n;
    }
    
    public void pop() {
        --this._free;
    }
    
    public void pop(final int n) {
        this._free -= n;
    }
    
    public final int cardinality() {
        return this._free;
    }
    
    public void print(final PrintStream out) {
        if (this._free > 0) {
            for (int i = 0; i < this._free - 1; ++i) {
                out.print(this._array[i]);
                out.print(' ');
            }
            out.println(this._array[this._free - 1]);
        }
        else {
            out.println("IntegerArray: empty");
        }
    }
}
