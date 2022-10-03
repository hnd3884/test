package com.sun.corba.se.impl.ior;

public class ByteBuffer
{
    protected byte[] elementData;
    protected int elementCount;
    protected int capacityIncrement;
    
    public ByteBuffer(final int n, final int capacityIncrement) {
        if (n < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + n);
        }
        this.elementData = new byte[n];
        this.capacityIncrement = capacityIncrement;
    }
    
    public ByteBuffer(final int n) {
        this(n, 0);
    }
    
    public ByteBuffer() {
        this(200);
    }
    
    public void trimToSize() {
        if (this.elementCount < this.elementData.length) {
            System.arraycopy(this.elementData, 0, this.elementData = new byte[this.elementCount], 0, this.elementCount);
        }
    }
    
    private void ensureCapacityHelper(final int n) {
        final int length = this.elementData.length;
        if (n > length) {
            final byte[] elementData = this.elementData;
            int n2 = (this.capacityIncrement > 0) ? (length + this.capacityIncrement) : (length * 2);
            if (n2 < n) {
                n2 = n;
            }
            System.arraycopy(elementData, 0, this.elementData = new byte[n2], 0, this.elementCount);
        }
    }
    
    public int capacity() {
        return this.elementData.length;
    }
    
    public int size() {
        return this.elementCount;
    }
    
    public boolean isEmpty() {
        return this.elementCount == 0;
    }
    
    public void append(final byte b) {
        this.ensureCapacityHelper(this.elementCount + 1);
        this.elementData[this.elementCount++] = b;
    }
    
    public void append(final int n) {
        this.ensureCapacityHelper(this.elementCount + 4);
        this.doAppend(n);
    }
    
    private void doAppend(final int n) {
        int n2 = n;
        for (int i = 0; i < 4; ++i) {
            this.elementData[this.elementCount + i] = (byte)(n2 & 0xFF);
            n2 >>= 8;
        }
        this.elementCount += 4;
    }
    
    public void append(final String s) {
        final byte[] bytes = s.getBytes();
        this.ensureCapacityHelper(this.elementCount + bytes.length + 4);
        this.doAppend(bytes.length);
        System.arraycopy(bytes, 0, this.elementData, this.elementCount, bytes.length);
        this.elementCount += bytes.length;
    }
    
    public byte[] toArray() {
        return this.elementData;
    }
}
