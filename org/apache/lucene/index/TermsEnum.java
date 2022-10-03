package org.apache.lucene.index;

import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRefIterator;

public abstract class TermsEnum implements BytesRefIterator
{
    private AttributeSource atts;
    public static final TermsEnum EMPTY;
    
    protected TermsEnum() {
        this.atts = null;
    }
    
    public AttributeSource attributes() {
        if (this.atts == null) {
            this.atts = new AttributeSource();
        }
        return this.atts;
    }
    
    public boolean seekExact(final BytesRef text) throws IOException {
        return this.seekCeil(text) == SeekStatus.FOUND;
    }
    
    public abstract SeekStatus seekCeil(final BytesRef p0) throws IOException;
    
    public abstract void seekExact(final long p0) throws IOException;
    
    public void seekExact(final BytesRef term, final TermState state) throws IOException {
        if (!this.seekExact(term)) {
            throw new IllegalArgumentException("term=" + term + " does not exist");
        }
    }
    
    public abstract BytesRef term() throws IOException;
    
    public abstract long ord() throws IOException;
    
    public abstract int docFreq() throws IOException;
    
    public abstract long totalTermFreq() throws IOException;
    
    public final PostingsEnum postings(final PostingsEnum reuse) throws IOException {
        return this.postings(reuse, 8);
    }
    
    public abstract PostingsEnum postings(final PostingsEnum p0, final int p1) throws IOException;
    
    public TermState termState() throws IOException {
        return new TermState() {
            @Override
            public void copyFrom(final TermState other) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Deprecated
    public final DocsEnum docs(final Bits liveDocs, final DocsEnum reuse) throws IOException {
        return this.docs(liveDocs, reuse, 1);
    }
    
    @Deprecated
    public final DocsEnum docs(final Bits liveDocs, final DocsEnum reuse, final int flags) throws IOException {
        int newFlags;
        if (flags == 1) {
            newFlags = 8;
        }
        else {
            if (flags != 0) {
                throw new IllegalArgumentException("Invalid legacy docs flags: " + flags);
            }
            newFlags = 0;
        }
        final PostingsEnum actualReuse = DocsAndPositionsEnum.unwrap(reuse);
        final PostingsEnum postings = this.postings(actualReuse, newFlags);
        if (postings == null) {
            throw new AssertionError();
        }
        if (postings == actualReuse && liveDocs == DocsAndPositionsEnum.unwrapliveDocs(reuse)) {
            return reuse;
        }
        return DocsAndPositionsEnum.wrap(postings, liveDocs);
    }
    
    @Deprecated
    public final DocsAndPositionsEnum docsAndPositions(final Bits liveDocs, final DocsAndPositionsEnum reuse) throws IOException {
        return this.docsAndPositions(liveDocs, reuse, 3);
    }
    
    @Deprecated
    public final DocsAndPositionsEnum docsAndPositions(final Bits liveDocs, final DocsAndPositionsEnum reuse, final int flags) throws IOException {
        int newFlags;
        if (flags == 3) {
            newFlags = 120;
        }
        else if (flags == 1) {
            newFlags = 56;
        }
        else if (flags == 2) {
            newFlags = 88;
        }
        else {
            if (flags != 0) {
                throw new IllegalArgumentException("Invalid legacy docsAndPositions flags: " + flags);
            }
            newFlags = 24;
        }
        final PostingsEnum actualReuse = DocsAndPositionsEnum.unwrap(reuse);
        final PostingsEnum postings = this.postings(actualReuse, newFlags | 0x4000);
        if (postings == null) {
            return null;
        }
        if (postings == actualReuse && liveDocs == DocsAndPositionsEnum.unwrapliveDocs(reuse)) {
            return reuse;
        }
        return DocsAndPositionsEnum.wrap(postings, liveDocs);
    }
    
    static {
        EMPTY = new TermsEnum() {
            @Override
            public SeekStatus seekCeil(final BytesRef term) {
                return SeekStatus.END;
            }
            
            @Override
            public void seekExact(final long ord) {
            }
            
            @Override
            public BytesRef term() {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public int docFreq() {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public long totalTermFreq() {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public long ord() {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public PostingsEnum postings(final PostingsEnum reuse, final int flags) {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public BytesRef next() {
                return null;
            }
            
            @Override
            public synchronized AttributeSource attributes() {
                return super.attributes();
            }
            
            @Override
            public TermState termState() {
                throw new IllegalStateException("this method should never be called");
            }
            
            @Override
            public void seekExact(final BytesRef term, final TermState state) {
                throw new IllegalStateException("this method should never be called");
            }
        };
    }
    
    public enum SeekStatus
    {
        END, 
        FOUND, 
        NOT_FOUND;
    }
}
