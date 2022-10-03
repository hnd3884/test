package org.apache.lucene.bkdtree;

import org.apache.lucene.util.ArrayUtil;

final class GrowingHeapLatLonWriter implements LatLonWriter
{
    int[] latEncs;
    int[] lonEncs;
    int[] docIDs;
    long[] ords;
    private int nextWrite;
    final int maxSize;
    
    public GrowingHeapLatLonWriter(final int maxSize) {
        this.latEncs = new int[16];
        this.lonEncs = new int[16];
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
    public void append(final int latEnc, final int lonEnc, final long ord, final int docID) {
        assert ord == this.nextWrite;
        if (this.latEncs.length == this.nextWrite) {
            final int nextSize = Math.min(this.maxSize, ArrayUtil.oversize(this.nextWrite + 1, 4));
            assert nextSize > this.nextWrite : "nextSize=" + nextSize + " vs nextWrite=" + this.nextWrite;
            this.latEncs = this.growExact(this.latEncs, nextSize);
            this.lonEncs = this.growExact(this.lonEncs, nextSize);
            this.ords = this.growExact(this.ords, nextSize);
            this.docIDs = this.growExact(this.docIDs, nextSize);
        }
        this.latEncs[this.nextWrite] = latEnc;
        this.lonEncs[this.nextWrite] = lonEnc;
        this.ords[this.nextWrite] = ord;
        this.docIDs[this.nextWrite] = docID;
        ++this.nextWrite;
    }
    
    @Override
    public LatLonReader getReader(final long start) {
        return new HeapLatLonReader(this.latEncs, this.lonEncs, this.ords, this.docIDs, (int)start, this.nextWrite);
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String toString() {
        return "GrowingHeapLatLonWriter(count=" + this.nextWrite + " alloc=" + this.latEncs.length + ")";
    }
}
