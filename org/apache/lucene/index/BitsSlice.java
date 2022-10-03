package org.apache.lucene.index;

import org.apache.lucene.util.Bits;

final class BitsSlice implements Bits
{
    private final Bits parent;
    private final int start;
    private final int length;
    
    public BitsSlice(final Bits parent, final ReaderSlice slice) {
        this.parent = parent;
        this.start = slice.start;
        this.length = slice.length;
        assert this.length >= 0 : "length=" + this.length;
    }
    
    @Override
    public boolean get(final int doc) {
        if (doc >= this.length) {
            throw new RuntimeException("doc " + doc + " is out of bounds 0 .. " + (this.length - 1));
        }
        assert doc < this.length : "doc=" + doc + " length=" + this.length;
        return this.parent.get(doc + this.start);
    }
    
    @Override
    public int length() {
        return this.length;
    }
}
