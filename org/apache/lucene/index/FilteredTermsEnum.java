package org.apache.lucene.index;

import org.apache.lucene.util.AttributeSource;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;

public abstract class FilteredTermsEnum extends TermsEnum
{
    private BytesRef initialSeekTerm;
    private boolean doSeek;
    protected BytesRef actualTerm;
    protected final TermsEnum tenum;
    
    protected abstract AcceptStatus accept(final BytesRef p0) throws IOException;
    
    public FilteredTermsEnum(final TermsEnum tenum) {
        this(tenum, true);
    }
    
    public FilteredTermsEnum(final TermsEnum tenum, final boolean startWithSeek) {
        assert tenum != null;
        this.tenum = tenum;
        this.doSeek = startWithSeek;
    }
    
    protected final void setInitialSeekTerm(final BytesRef term) {
        this.initialSeekTerm = term;
    }
    
    protected BytesRef nextSeekTerm(final BytesRef currentTerm) throws IOException {
        final BytesRef t = this.initialSeekTerm;
        this.initialSeekTerm = null;
        return t;
    }
    
    @Override
    public AttributeSource attributes() {
        return this.tenum.attributes();
    }
    
    @Override
    public BytesRef term() throws IOException {
        return this.tenum.term();
    }
    
    @Override
    public int docFreq() throws IOException {
        return this.tenum.docFreq();
    }
    
    @Override
    public long totalTermFreq() throws IOException {
        return this.tenum.totalTermFreq();
    }
    
    @Override
    public boolean seekExact(final BytesRef term) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }
    
    @Override
    public SeekStatus seekCeil(final BytesRef term) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }
    
    @Override
    public void seekExact(final long ord) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }
    
    @Override
    public long ord() throws IOException {
        return this.tenum.ord();
    }
    
    @Override
    public PostingsEnum postings(final PostingsEnum reuse, final int flags) throws IOException {
        return this.tenum.postings(reuse, flags);
    }
    
    @Override
    public void seekExact(final BytesRef term, final TermState state) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not support seeking");
    }
    
    @Override
    public TermState termState() throws IOException {
        assert this.tenum != null;
        return this.tenum.termState();
    }
    
    @Override
    public BytesRef next() throws IOException {
    Label_0193:
        while (true) {
            if (this.doSeek) {
                this.doSeek = false;
                final BytesRef t = this.nextSeekTerm(this.actualTerm);
                assert t.compareTo(this.actualTerm) > 0 : "curTerm=" + this.actualTerm + " seekTerm=" + t;
                if (t == null || this.tenum.seekCeil(t) == SeekStatus.END) {
                    return null;
                }
                this.actualTerm = this.tenum.term();
            }
            else {
                this.actualTerm = this.tenum.next();
                if (this.actualTerm == null) {
                    return null;
                }
            }
            switch (this.accept(this.actualTerm)) {
                case YES_AND_SEEK: {
                    this.doSeek = true;
                }
                case YES: {
                    break Label_0193;
                }
                case NO_AND_SEEK: {
                    this.doSeek = true;
                    continue;
                }
                case END: {
                    return null;
                }
            }
        }
        return this.actualTerm;
    }
    
    protected enum AcceptStatus
    {
        YES, 
        YES_AND_SEEK, 
        NO, 
        NO_AND_SEEK, 
        END;
    }
}
