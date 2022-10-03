package org.apache.lucene.queries.payloads;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.spans.FilterSpans;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.search.Weight;
import java.util.Iterator;
import java.io.IOException;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import java.util.List;
import org.apache.lucene.search.spans.SpanQuery;

public class SpanPayloadCheckQuery extends SpanQuery
{
    protected final List<BytesRef> payloadToMatch;
    protected final SpanQuery match;
    
    public SpanPayloadCheckQuery(final SpanQuery match, final List<BytesRef> payloadToMatch) {
        this.match = match;
        this.payloadToMatch = payloadToMatch;
    }
    
    public String getField() {
        return this.match.getField();
    }
    
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight matchWeight = this.match.createWeight(searcher, false);
        return new SpanPayloadCheckWeight(searcher, needsScores ? getTermContexts(new SpanWeight[] { matchWeight }) : null, matchWeight);
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanPayCheck(");
        buffer.append(this.match.toString(field));
        buffer.append(", payloadRef: ");
        for (final BytesRef bytes : this.payloadToMatch) {
            buffer.append(Term.toString(bytes));
            buffer.append(';');
        }
        buffer.append(")");
        return buffer.toString();
    }
    
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanPayloadCheckQuery other = (SpanPayloadCheckQuery)o;
        return this.payloadToMatch.equals(other.payloadToMatch);
    }
    
    public int hashCode() {
        int h = super.hashCode();
        h = (h * 63 ^ this.payloadToMatch.hashCode());
        return h;
    }
    
    public class SpanPayloadCheckWeight extends SpanWeight
    {
        final SpanWeight matchWeight;
        
        public SpanPayloadCheckWeight(final IndexSearcher searcher, final Map<Term, TermContext> termContexts, final SpanWeight matchWeight) throws IOException {
            super((SpanQuery)SpanPayloadCheckQuery.this, searcher, (Map)termContexts);
            this.matchWeight = matchWeight;
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.matchWeight.extractTerms((Set)terms);
        }
        
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            this.matchWeight.extractTermContexts((Map)contexts);
        }
        
        public Spans getSpans(final LeafReaderContext context, final SpanWeight.Postings requiredPostings) throws IOException {
            final PayloadChecker collector = new PayloadChecker();
            final Spans matchSpans = this.matchWeight.getSpans(context, requiredPostings.atLeast(SpanWeight.Postings.PAYLOADS));
            return (Spans)((matchSpans == null) ? null : new FilterSpans(matchSpans) {
                protected FilterSpans.AcceptStatus accept(final Spans candidate) throws IOException {
                    collector.reset();
                    candidate.collect((SpanCollector)collector);
                    return collector.match();
                }
            });
        }
        
        public SpanScorer scorer(final LeafReaderContext context) throws IOException {
            if (this.field == null) {
                return null;
            }
            final Terms terms = context.reader().terms(this.field);
            if (terms != null && !terms.hasPositions()) {
                throw new IllegalStateException("field \"" + this.field + "\" was indexed without position data; cannot run SpanQuery (query=" + this.parentQuery + ")");
            }
            final Spans spans = this.getSpans(context, SpanWeight.Postings.PAYLOADS);
            if (spans == null) {
                return null;
            }
            final Similarity.SimScorer docScorer = this.getSimScorer(context);
            return new SpanScorer((SpanWeight)this, spans, docScorer);
        }
    }
    
    private class PayloadChecker implements SpanCollector
    {
        int upto;
        boolean matches;
        
        private PayloadChecker() {
            this.upto = 0;
            this.matches = true;
        }
        
        public void collectLeaf(final PostingsEnum postings, final int position, final Term term) throws IOException {
            if (!this.matches) {
                return;
            }
            if (this.upto >= SpanPayloadCheckQuery.this.payloadToMatch.size()) {
                this.matches = false;
                return;
            }
            final BytesRef payload = postings.getPayload();
            if (SpanPayloadCheckQuery.this.payloadToMatch.get(this.upto) == null) {
                this.matches = (payload == null);
                ++this.upto;
                return;
            }
            if (payload == null) {
                this.matches = false;
                ++this.upto;
                return;
            }
            this.matches = SpanPayloadCheckQuery.this.payloadToMatch.get(this.upto).bytesEquals(payload);
            ++this.upto;
        }
        
        FilterSpans.AcceptStatus match() {
            return (this.matches && this.upto == SpanPayloadCheckQuery.this.payloadToMatch.size()) ? FilterSpans.AcceptStatus.YES : FilterSpans.AcceptStatus.NO;
        }
        
        public void reset() {
            this.upto = 0;
            this.matches = true;
        }
    }
}
