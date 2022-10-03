package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;

class SortedSetDocValuesTermsEnum extends TermsEnum
{
    private final SortedSetDocValues values;
    private long currentOrd;
    private final BytesRefBuilder scratch;
    
    public SortedSetDocValuesTermsEnum(final SortedSetDocValues values) {
        this.currentOrd = -1L;
        this.values = values;
        this.scratch = new BytesRefBuilder();
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef text) throws IOException {
        final long ord = this.values.lookupTerm(text);
        if (ord >= 0L) {
            this.currentOrd = ord;
            this.scratch.copyBytes(text);
            return SeekStatus.FOUND;
        }
        this.currentOrd = -ord - 1L;
        if (this.currentOrd == this.values.getValueCount()) {
            return SeekStatus.END;
        }
        this.scratch.copyBytes(this.values.lookupOrd(this.currentOrd));
        return SeekStatus.NOT_FOUND;
    }
    
    @Override
    public boolean seekExact(final BytesRef text) throws IOException {
        final long ord = this.values.lookupTerm(text);
        if (ord >= 0L) {
            this.currentOrd = ord;
            this.scratch.copyBytes(text);
            return true;
        }
        return false;
    }
    
    @Override
    public void seekExact(final long ord) throws IOException {
        assert ord >= 0L && ord < this.values.getValueCount();
        this.currentOrd = (int)ord;
        this.scratch.copyBytes(this.values.lookupOrd(this.currentOrd));
    }
    
    @Override
    public BytesRef next() throws IOException {
        ++this.currentOrd;
        if (this.currentOrd >= this.values.getValueCount()) {
            return null;
        }
        this.scratch.copyBytes(this.values.lookupOrd(this.currentOrd));
        return this.scratch.get();
    }
    
    @Override
    public BytesRef term() throws IOException {
        return this.scratch.get();
    }
    
    @Override
    public long ord() throws IOException {
        return this.currentOrd;
    }
    
    @Override
    public int docFreq() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long totalTermFreq() {
        return -1L;
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void seekExact(final BytesRef term, final TermState state) throws IOException {
        assert state != null && state instanceof OrdTermState;
        this.seekExact(((OrdTermState)state).ord);
    }
    
    @Override
    public TermState termState() throws IOException {
        final OrdTermState state = new OrdTermState();
        state.ord = this.currentOrd;
        return state;
    }
}
