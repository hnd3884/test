package org.apache.lucene.rangetree;

final class HeapSliceReader implements SliceReader
{
    private int curRead;
    final long[] values;
    final long[] ords;
    final int[] docIDs;
    final int end;
    
    HeapSliceReader(final long[] values, final long[] ords, final int[] docIDs, final int start, final int end) {
        this.values = values;
        this.ords = ords;
        this.docIDs = docIDs;
        this.curRead = start - 1;
        this.end = end;
    }
    
    @Override
    public boolean next() {
        ++this.curRead;
        return this.curRead < this.end;
    }
    
    @Override
    public long value() {
        return this.values[this.curRead];
    }
    
    @Override
    public int docID() {
        return this.docIDs[this.curRead];
    }
    
    @Override
    public long ord() {
        return this.ords[this.curRead];
    }
    
    @Override
    public void close() {
    }
}
