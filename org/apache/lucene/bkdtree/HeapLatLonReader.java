package org.apache.lucene.bkdtree;

final class HeapLatLonReader implements LatLonReader
{
    private int curRead;
    final int[] latEncs;
    final int[] lonEncs;
    final long[] ords;
    final int[] docIDs;
    final int end;
    
    HeapLatLonReader(final int[] latEncs, final int[] lonEncs, final long[] ords, final int[] docIDs, final int start, final int end) {
        this.latEncs = latEncs;
        this.lonEncs = lonEncs;
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
    public int latEnc() {
        return this.latEncs[this.curRead];
    }
    
    @Override
    public int lonEnc() {
        return this.lonEncs[this.curRead];
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
