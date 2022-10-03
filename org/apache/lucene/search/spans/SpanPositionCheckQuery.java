package org.apache.lucene.search.spans;

import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import java.util.Objects;

public abstract class SpanPositionCheckQuery extends SpanQuery implements Cloneable
{
    protected SpanQuery match;
    
    public SpanPositionCheckQuery(final SpanQuery match) {
        this.match = Objects.requireNonNull(match);
    }
    
    public SpanQuery getMatch() {
        return this.match;
    }
    
    @Override
    public String getField() {
        return this.match.getField();
    }
    
    protected abstract FilterSpans.AcceptStatus acceptPosition(final Spans p0) throws IOException;
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight matchWeight = this.match.createWeight(searcher, false);
        return new SpanPositionCheckWeight(matchWeight, searcher, needsScores ? SpanQuery.getTermContexts(matchWeight) : null);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final SpanQuery rewritten = (SpanQuery)this.match.rewrite(reader);
        if (rewritten != this.match) {
            final SpanPositionCheckQuery clone = (SpanPositionCheckQuery)this.clone();
            clone.match = rewritten;
            return clone;
        }
        return super.rewrite(reader);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanPositionCheckQuery spcq = (SpanPositionCheckQuery)o;
        return this.match.equals(spcq.match);
    }
    
    @Override
    public int hashCode() {
        return this.match.hashCode() ^ super.hashCode();
    }
    
    public class SpanPositionCheckWeight extends SpanWeight
    {
        final SpanWeight matchWeight;
        
        public SpanPositionCheckWeight(final SpanWeight matchWeight, final IndexSearcher searcher, final Map<Term, TermContext> terms) throws IOException {
            super(SpanPositionCheckQuery.this, searcher, terms);
            this.matchWeight = matchWeight;
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            this.matchWeight.extractTerms(terms);
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            this.matchWeight.extractTermContexts(contexts);
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            final Spans matchSpans = this.matchWeight.getSpans(context, requiredPostings);
            return (matchSpans == null) ? null : new FilterSpans(matchSpans) {
                @Override
                protected AcceptStatus accept(final Spans candidate) throws IOException {
                    return SpanPositionCheckQuery.this.acceptPosition(candidate);
                }
            };
        }
    }
}
