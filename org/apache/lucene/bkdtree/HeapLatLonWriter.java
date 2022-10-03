package org.apache.lucene.bkdtree;

final class HeapLatLonWriter implements LatLonWriter
{
    final int[] latEncs;
    final int[] lonEncs;
    final int[] docIDs;
    final long[] ords;
    private int nextWrite;
    private boolean closed;
    
    public HeapLatLonWriter(final int count) {
        this.latEncs = new int[count];
        this.lonEncs = new int[count];
        this.docIDs = new int[count];
        this.ords = new long[count];
    }
    
    @Override
    public void append(final int latEnc, final int lonEnc, final long ord, final int docID) {
        this.latEncs[this.nextWrite] = latEnc;
        this.lonEncs[this.nextWrite] = lonEnc;
        this.ords[this.nextWrite] = ord;
        this.docIDs[this.nextWrite] = docID;
        ++this.nextWrite;
    }
    
    @Override
    public LatLonReader getReader(final long start) {
        assert this.closed;
        return new HeapLatLonReader(this.latEncs, this.lonEncs, this.ords, this.docIDs, (int)start, this.latEncs.length);
    }
    
    @Override
    public void close() {
        this.closed = true;
        if (this.nextWrite != this.latEncs.length) {
            throw new IllegalStateException("only wrote " + this.nextWrite + " values, but expected " + this.latEncs.length);
        }
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String toString() {
        return "HeapLatLonWriter(count=" + this.latEncs.length + ")";
    }
}
