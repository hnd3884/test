package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BitSetIterator;
import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.PostingsEnum;

class BitSetPostingsEnum extends PostingsEnum
{
    private final BitSet bits;
    private DocIdSetIterator in;
    
    BitSetPostingsEnum(final BitSet bits) {
        this.bits = bits;
        this.reset();
    }
    
    @Override
    public int freq() throws IOException {
        return 1;
    }
    
    @Override
    public int docID() {
        if (this.in == null) {
            return -1;
        }
        return this.in.docID();
    }
    
    @Override
    public int nextDoc() throws IOException {
        if (this.in == null) {
            this.in = new BitSetIterator(this.bits, 0L);
        }
        return this.in.nextDoc();
    }
    
    @Override
    public int advance(final int target) throws IOException {
        return this.in.advance(target);
    }
    
    @Override
    public long cost() {
        return this.in.cost();
    }
    
    void reset() {
        this.in = null;
    }
    
    @Override
    public BytesRef getPayload() {
        return null;
    }
    
    @Override
    public int nextPosition() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int startOffset() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int endOffset() {
        throw new UnsupportedOperationException();
    }
}
