package org.apache.lucene.rangetree;

final class HeapSliceWriter implements SliceWriter
{
    final long[] values;
    final int[] docIDs;
    final long[] ords;
    private int nextWrite;
    private boolean closed;
    
    public HeapSliceWriter(final int count) {
        this.values = new long[count];
        this.docIDs = new int[count];
        this.ords = new long[count];
    }
    
    @Override
    public void append(final long value, final long ord, final int docID) {
        this.values[this.nextWrite] = value;
        this.ords[this.nextWrite] = ord;
        this.docIDs[this.nextWrite] = docID;
        ++this.nextWrite;
    }
    
    @Override
    public SliceReader getReader(final long start) {
        assert this.closed;
        return new HeapSliceReader(this.values, this.ords, this.docIDs, (int)start, this.values.length);
    }
    
    @Override
    public void close() {
        this.closed = true;
        if (this.nextWrite != this.values.length) {
            throw new IllegalStateException("only wrote " + this.nextWrite + " values, but expected " + this.values.length);
        }
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String toString() {
        return "HeapSliceWriter(count=" + this.values.length + ")";
    }
}
