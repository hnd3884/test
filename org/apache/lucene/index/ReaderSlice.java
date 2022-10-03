package org.apache.lucene.index;

public final class ReaderSlice
{
    public static final ReaderSlice[] EMPTY_ARRAY;
    public final int start;
    public final int length;
    public final int readerIndex;
    
    public ReaderSlice(final int start, final int length, final int readerIndex) {
        this.start = start;
        this.length = length;
        this.readerIndex = readerIndex;
    }
    
    @Override
    public String toString() {
        return "slice start=" + this.start + " length=" + this.length + " readerIndex=" + this.readerIndex;
    }
    
    static {
        EMPTY_ARRAY = new ReaderSlice[0];
    }
}
