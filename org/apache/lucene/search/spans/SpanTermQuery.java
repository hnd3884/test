package org.apache.lucene.search.spans;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.ToStringUtils;
import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.index.IndexReaderContext;
import java.util.Collections;
import org.apache.lucene.search.IndexSearcher;
import java.util.Objects;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;

public class SpanTermQuery extends SpanQuery
{
    protected final Term term;
    protected final TermContext termContext;
    private static final float PHRASE_TO_SPAN_TERM_POSITIONS_COST = 4.0f;
    private static final int TERM_POSNS_SEEK_OPS_PER_DOC = 128;
    private static final int TERM_OPS_PER_POS = 7;
    
    public SpanTermQuery(final Term term) {
        this.term = Objects.requireNonNull(term);
        this.termContext = null;
    }
    
    public SpanTermQuery(final Term term, final TermContext context) {
        this.term = Objects.requireNonNull(term);
        this.termContext = context;
    }
    
    public Term getTerm() {
        return this.term;
    }
    
    @Override
    public String getField() {
        return this.term.field();
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final IndexReaderContext topContext = searcher.getTopReaderContext();
        TermContext context;
        if (this.termContext == null || this.termContext.topReaderContext != topContext) {
            context = TermContext.build(topContext, this.term);
        }
        else {
            context = this.termContext;
        }
        return new SpanTermWeight(context, searcher, needsScores ? Collections.singletonMap(this.term, context) : null);
    }
    
    static float termPositionsCost(final TermsEnum termsEnum) throws IOException {
        final int docFreq = termsEnum.docFreq();
        assert docFreq > 0;
        final long totalTermFreq = termsEnum.totalTermFreq();
        final float expOccurrencesInMatchingDoc = (totalTermFreq < docFreq) ? 1.0f : (totalTermFreq / (float)docFreq);
        return 128.0f + expOccurrencesInMatchingDoc * 7.0f;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        if (this.term.field().equals(field)) {
            buffer.append(this.term.text());
        }
        else {
            buffer.append(this.term.toString());
        }
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.term.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final SpanTermQuery other = (SpanTermQuery)obj;
        return this.term.equals(other.term);
    }
    
    public class SpanTermWeight extends SpanWeight
    {
        final TermContext termContext;
        
        public SpanTermWeight(final TermContext termContext, final IndexSearcher searcher, final Map<Term, TermContext> terms) throws IOException {
            super(SpanTermQuery.this, searcher, terms);
            this.termContext = termContext;
            assert termContext != null : "TermContext must not be null";
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            terms.add(SpanTermQuery.this.term);
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            contexts.put(SpanTermQuery.this.term, this.termContext);
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            assert this.termContext.topReaderContext == ReaderUtil.getTopLevelContext(context) : "The top-reader used to create Weight (" + this.termContext.topReaderContext + ") is not the same as the current reader's top-reader (" + ReaderUtil.getTopLevelContext(context);
            final TermState state = this.termContext.get(context.ord);
            if (state == null) {
                assert context.reader().docFreq(SpanTermQuery.this.term) == 0 : "no termstate found but term exists in reader term=" + SpanTermQuery.this.term;
                return null;
            }
            else {
                final Terms terms = context.reader().terms(SpanTermQuery.this.term.field());
                if (terms == null) {
                    return null;
                }
                if (!terms.hasPositions()) {
                    throw new IllegalStateException("field \"" + SpanTermQuery.this.term.field() + "\" was indexed without position data; cannot run SpanTermQuery (term=" + SpanTermQuery.this.term.text() + ")");
                }
                final TermsEnum termsEnum = terms.iterator();
                termsEnum.seekExact(SpanTermQuery.this.term.bytes(), state);
                final PostingsEnum postings = termsEnum.postings(null, requiredPostings.getRequiredPostings());
                final float positionsCost = SpanTermQuery.termPositionsCost(termsEnum) * 4.0f;
                return new TermSpans(this.getSimScorer(context), postings, SpanTermQuery.this.term, positionsCost);
            }
        }
    }
}
