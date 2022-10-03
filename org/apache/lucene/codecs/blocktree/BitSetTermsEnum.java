package org.apache.lucene.codecs.blocktree;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.TermsEnum;

class BitSetTermsEnum extends TermsEnum
{
    private final BitSetPostingsEnum postingsEnum;
    
    public BitSetTermsEnum(final BitSet docs) {
        this.postingsEnum = new BitSetPostingsEnum(docs);
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef text) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void seekExact(final long ord) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BytesRef term() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public BytesRef next() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long ord() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int docFreq() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long totalTermFreq() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) {
        if (flags != 0) {
            return null;
        }
        this.postingsEnum.reset();
        return this.postingsEnum;
    }
}
