package org.apache.lucene.index;

import java.util.Arrays;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.BytesRef;

public final class TermContext
{
    public final IndexReaderContext topReaderContext;
    private final TermState[] states;
    private int docFreq;
    private long totalTermFreq;
    
    public TermContext(final IndexReaderContext context) {
        assert context != null && context.isTopLevel;
        this.topReaderContext = context;
        this.docFreq = 0;
        this.totalTermFreq = 0L;
        int len;
        if (context.leaves() == null) {
            len = 1;
        }
        else {
            len = context.leaves().size();
        }
        this.states = new TermState[len];
    }
    
    public TermContext(final IndexReaderContext context, final TermState state, final int ord, final int docFreq, final long totalTermFreq) {
        this(context);
        this.register(state, ord, docFreq, totalTermFreq);
    }
    
    public static TermContext build(final IndexReaderContext context, final Term term) throws IOException {
        assert context != null && context.isTopLevel;
        final String field = term.field();
        final BytesRef bytes = term.bytes();
        final TermContext perReaderTermState = new TermContext(context);
        for (final LeafReaderContext ctx : context.leaves()) {
            final Terms terms = ctx.reader().terms(field);
            if (terms != null) {
                final TermsEnum termsEnum = terms.iterator();
                if (!termsEnum.seekExact(bytes)) {
                    continue;
                }
                final TermState termState = termsEnum.termState();
                perReaderTermState.register(termState, ctx.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
            }
        }
        return perReaderTermState;
    }
    
    public void clear() {
        this.docFreq = 0;
        this.totalTermFreq = 0L;
        Arrays.fill(this.states, null);
    }
    
    public void register(final TermState state, final int ord, final int docFreq, final long totalTermFreq) {
        this.register(state, ord);
        this.accumulateStatistics(docFreq, totalTermFreq);
    }
    
    public void register(final TermState state, final int ord) {
        assert state != null : "state must not be null";
        assert ord >= 0 && ord < this.states.length;
        assert this.states[ord] == null : "state for ord: " + ord + " already registered";
        this.states[ord] = state;
    }
    
    public void accumulateStatistics(final int docFreq, final long totalTermFreq) {
        this.docFreq += docFreq;
        if (this.totalTermFreq >= 0L && totalTermFreq >= 0L) {
            this.totalTermFreq += totalTermFreq;
        }
        else {
            this.totalTermFreq = -1L;
        }
    }
    
    public TermState get(final int ord) {
        assert ord >= 0 && ord < this.states.length;
        return this.states[ord];
    }
    
    public int docFreq() {
        return this.docFreq;
    }
    
    public long totalTermFreq() {
        return this.totalTermFreq;
    }
    
    public boolean hasOnlyRealTerms() {
        for (final TermState termState : this.states) {
            if (termState != null && !termState.isRealTerm()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TermContext\n");
        for (final TermState termState : this.states) {
            sb.append("  state=");
            sb.append(termState.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
