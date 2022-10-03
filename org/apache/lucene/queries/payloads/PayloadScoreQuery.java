package org.apache.lucene.queries.payloads;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.spans.FilterSpans;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.Explanation;
import java.util.Set;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.Weight;
import java.io.IOException;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spans.SpanQuery;

public class PayloadScoreQuery extends SpanQuery
{
    private final SpanQuery wrappedQuery;
    private final PayloadFunction function;
    private final boolean includeSpanScore;
    
    public PayloadScoreQuery(final SpanQuery wrappedQuery, final PayloadFunction function, final boolean includeSpanScore) {
        this.wrappedQuery = wrappedQuery;
        this.function = function;
        this.includeSpanScore = includeSpanScore;
    }
    
    public PayloadScoreQuery(final SpanQuery wrappedQuery, final PayloadFunction function) {
        this(wrappedQuery, function, true);
    }
    
    public String getField() {
        return this.wrappedQuery.getField();
    }
    
    public String toString(final String field) {
        return "PayloadSpanQuery[" + this.wrappedQuery.toString(field) + "; " + this.function.toString() + "]";
    }
    
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight innerWeight = this.wrappedQuery.createWeight(searcher, needsScores);
        if (!needsScores) {
            return innerWeight;
        }
        return new PayloadSpanWeight(searcher, innerWeight);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PayloadScoreQuery)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PayloadScoreQuery that = (PayloadScoreQuery)o;
        Label_0064: {
            if (this.wrappedQuery != null) {
                if (this.wrappedQuery.equals((Object)that.wrappedQuery)) {
                    break Label_0064;
                }
            }
            else if (that.wrappedQuery == null) {
                break Label_0064;
            }
            return false;
        }
        if (this.function != null) {
            if (!this.function.equals(that.function)) {
                return false;
            }
        }
        else if (that.function != null) {
            return false;
        }
        return true;
        b = false;
        return b;
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.wrappedQuery != null) ? this.wrappedQuery.hashCode() : 0);
        result = 31 * result + ((this.function != null) ? this.function.hashCode() : 0);
        return result;
    }
    
    private class PayloadSpanWeight extends SpanWeight
    {
        private final SpanWeight innerWeight;
        
        public PayloadSpanWeight(final IndexSearcher searcher, final SpanWeight innerWeight) throws IOException {
            super((SpanQuery)PayloadScoreQuery.this, searcher, (Map)null);
            this.innerWeight = innerWeight;
        }
        
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            this.innerWeight.extractTermContexts((Map)contexts);
        }
        
        public Spans getSpans(final LeafReaderContext ctx, final SpanWeight.Postings requiredPostings) throws IOException {
            return this.innerWeight.getSpans(ctx, requiredPostings.atLeast(SpanWeight.Postings.PAYLOADS));
        }
        
        public PayloadSpanScorer scorer(final LeafReaderContext context) throws IOException {
            final Spans spans = this.getSpans(context, SpanWeight.Postings.PAYLOADS);
            if (spans == null) {
                return null;
            }
            final Similarity.SimScorer docScorer = this.innerWeight.getSimScorer(context);
            final PayloadSpans payloadSpans = new PayloadSpans(spans, docScorer);
            return new PayloadSpanScorer((SpanWeight)this, payloadSpans, docScorer);
        }
        
        public void extractTerms(final Set<Term> terms) {
            this.innerWeight.extractTerms((Set)terms);
        }
        
        public float getValueForNormalization() throws IOException {
            return this.innerWeight.getValueForNormalization();
        }
        
        public void normalize(final float queryNorm, final float topLevelBoost) {
            this.innerWeight.normalize(queryNorm, topLevelBoost);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final PayloadSpanScorer scorer = this.scorer(context);
            if (scorer == null || scorer.iterator().advance(doc) != doc) {
                return Explanation.noMatch("No match", new Explanation[0]);
            }
            scorer.freq();
            final Explanation payloadExpl = scorer.getPayloadExplanation();
            if (PayloadScoreQuery.this.includeSpanScore) {
                final SpanWeight innerWeight = ((PayloadSpanWeight)scorer.getWeight()).innerWeight;
                final Explanation innerExpl = innerWeight.explain(context, doc);
                return Explanation.match(scorer.scoreCurrentDoc(), "PayloadSpanQuery, product of:", new Explanation[] { innerExpl, payloadExpl });
            }
            return scorer.getPayloadExplanation();
        }
    }
    
    private class PayloadSpans extends FilterSpans implements SpanCollector
    {
        private final Similarity.SimScorer docScorer;
        public int payloadsSeen;
        public float payloadScore;
        
        private PayloadSpans(final Spans in, final Similarity.SimScorer docScorer) {
            super(in);
            this.docScorer = docScorer;
        }
        
        protected FilterSpans.AcceptStatus accept(final Spans candidate) throws IOException {
            return FilterSpans.AcceptStatus.YES;
        }
        
        protected void doStartCurrentDoc() {
            this.payloadScore = 0.0f;
            this.payloadsSeen = 0;
        }
        
        public void collectLeaf(final PostingsEnum postings, final int position, final Term term) throws IOException {
            final BytesRef payload = postings.getPayload();
            if (payload == null) {
                return;
            }
            final float payloadFactor = this.docScorer.computePayloadFactor(this.docID(), this.in.startPosition(), this.in.endPosition(), payload);
            this.payloadScore = PayloadScoreQuery.this.function.currentScore(this.docID(), PayloadScoreQuery.this.getField(), this.in.startPosition(), this.in.endPosition(), this.payloadsSeen, this.payloadScore, payloadFactor);
            ++this.payloadsSeen;
        }
        
        public void reset() {
        }
        
        protected void doCurrentSpans() throws IOException {
            this.in.collect((SpanCollector)this);
        }
    }
    
    private class PayloadSpanScorer extends SpanScorer
    {
        private final PayloadSpans spans;
        
        private PayloadSpanScorer(final SpanWeight weight, final PayloadSpans spans, final Similarity.SimScorer docScorer) throws IOException {
            super(weight, (Spans)spans, docScorer);
            this.spans = spans;
        }
        
        protected float getPayloadScore() {
            return PayloadScoreQuery.this.function.docScore(this.docID(), PayloadScoreQuery.this.getField(), this.spans.payloadsSeen, this.spans.payloadScore);
        }
        
        protected Explanation getPayloadExplanation() {
            return PayloadScoreQuery.this.function.explain(this.docID(), PayloadScoreQuery.this.getField(), this.spans.payloadsSeen, this.spans.payloadScore);
        }
        
        protected float getSpanScore() throws IOException {
            return super.scoreCurrentDoc();
        }
        
        protected float scoreCurrentDoc() throws IOException {
            if (PayloadScoreQuery.this.includeSpanScore) {
                return this.getSpanScore() * this.getPayloadScore();
            }
            return this.getPayloadScore();
        }
    }
}
