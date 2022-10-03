package com.lowagie.text.pdf.hyphenation;

import java.io.Serializable;

public class ByteVector implements Serializable
{
    private static final long serialVersionUID = -1096301185375029343L;
    private static final int DEFAULT_BLOCK_SIZE = 2048;
    private int blockSize;
    private byte[] array;
    private int n;
    
    public ByteVector() {
        this(2048);
    }
    
    public ByteVector(final int capacity) {
        if (capacity > 0) {
            this.blockSize = capacity;
        }
        else {
            this.blockSize = 2048;
        }
        this.array = new byte[this.blockSize];
        this.n = 0;
    }
    
    public ByteVector(final byte[] a) {
        this.blockSize = 2048;
        this.array = a;
        this.n = 0;
    }
    
    public ByteVector(final byte[] a, final int capacity) {
        if (capacity > 0) {
            this.blockSize = capacity;
        }
        else {
            this.blockSize = 2048;
        }
        this.array = a;
        this.n = 0;
    }
    
    public byte[] getArray() {
        return this.array;
    }
    
    public int length() {
        return this.n;
    }
    
    public int capacity() {
        return this.array.length;
    }
    
    public void put(final int index, final byte val) {
        this.array[index] = val;
    }
    
    public byte get(final int index) {
        return this.array[index];
    }
    
    public int alloc(final int size) {
        final int index = this.n;
        final int len = this.array.length;
        if (this.n + size >= len) {
            final byte[] aux = new byte[len + this.blockSize];
            System.arraycopy(this.array, 0, aux, 0, len);
            this.array = aux;
        }
        this.n += size;
        return index;
    }
    
    public void trimToSize() {
        if (this.n < this.array.length) {
            final byte[] aux = new byte[this.n];
            System.arraycopy(this.array, 0, aux, 0, this.n);
            this.array = aux;
        }
    }
}
