package org.apache.lucene.rangetree;

import org.apache.lucene.util.ArrayUtil;

final class GrowingHeapSliceWriter implements SliceWriter
{
    long[] values;
    int[] docIDs;
    long[] ords;
    private int nextWrite;
    final int maxSize;
    
    public GrowingHeapSliceWriter(final int maxSize) {
        this.values = new long[16];
        this.docIDs = new int[16];
        this.ords = new long[16];
        this.maxSize = maxSize;
    }
    
    private int[] growExact(final int[] arr, final int size) {
        assert size > arr.length;
        final int[] newArr = new int[size];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }
    
    private long[] growExact(final long[] arr, final int size) {
        assert size > arr.length;
        final long[] newArr = new long[size];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }
    
    @Override
    public void append(final long value, final long ord, final int docID) {
        assert ord == this.nextWrite;
        if (this.values.length == this.nextWrite) {
            final int nextSize = Math.min(this.maxSize, ArrayUtil.oversize(this.nextWrite + 1, 4));
            assert nextSize > this.nextWrite : "nextSize=" + nextSize + " vs nextWrite=" + this.nextWrite;
            this.values = this.growExact(this.values, nextSize);
            this.ords = this.growExact(this.ords, nextSize);
            this.docIDs = this.growExact(this.docIDs, nextSize);
        }
        this.values[this.nextWrite] = value;
        this.ords[this.nextWrite] = ord;
        this.docIDs[this.nextWrite] = docID;
        ++this.nextWrite;
    }
    
    @Override
    public SliceReader getReader(final long start) {
        return new HeapSliceReader(this.values, this.ords, this.docIDs, (int)start, this.nextWrite);
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String toString() {
        return "GrowingHeapSliceWriter(count=" + this.nextWrite + " alloc=" + this.values.length + ")";
    }
}
