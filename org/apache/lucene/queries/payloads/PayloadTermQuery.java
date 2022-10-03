package org.apache.lucene.queries.payloads;

import org.apache.lucene.search.spans.FilterSpans;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.Weight;
import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.IndexSearcher;
import java.util.Objects;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanTermQuery;

@Deprecated
public class PayloadTermQuery extends SpanTermQuery
{
    protected PayloadFunction function;
    private boolean includeSpanScore;
    
    public PayloadTermQuery(final Term term, final PayloadFunction function) {
        this(term, function, true);
    }
    
    public PayloadTermQuery(final Term term, final PayloadFunction function, final boolean includeSpanScore) {
        super(term);
        this.function = Objects.requireNonNull(function);
        this.includeSpanScore = includeSpanScore;
    }
    
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final TermContext context = TermContext.build(searcher.getTopReaderContext(), this.term);
        return (SpanWeight)new PayloadTermWeight(context, searcher, needsScores ? Collections.singletonMap(this.term, context) : null);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.function.hashCode();
        result = 31 * result + (this.includeSpanScore ? 1231 : 1237);
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final PayloadTermQuery other = (PayloadTermQuery)obj;
        return this.includeSpanScore == other.includeSpanScore && this.function.equals(other.function);
    }
    
    private static class PayloadTermCollector implements SpanCollector
    {
        BytesRef payload;
        
        public void collectLeaf(final PostingsEnum postings, final int position, final Term term) throws IOException {
            this.payload = postings.getPayload();
        }
        
        public void reset() {
            this.payload = null;
        }
    }
    
    private class PayloadTermWeight extends SpanTermQuery.SpanTermWeight
    {
        public PayloadTermWeight(final TermContext context, final IndexSearcher searcher, final Map<Term, TermContext> terms) throws IOException {
            super((SpanTermQuery)PayloadTermQuery.this, context, searcher, (Map)terms);
        }
        
        public PayloadTermSpanScorer scorer(final LeafReaderContext context) throws IOException {
            final Spans spans = super.getSpans(context, SpanWeight.Postings.PAYLOADS);
            if (spans == null) {
                return null;
            }
            final Similarity.SimScorer simScorer = this.getSimScorer(context);
            final PayloadSpans payloadSpans = new PayloadSpans(spans, simScorer);
            return new PayloadTermSpanScorer(payloadSpans, (SpanWeight)this, simScorer);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final PayloadTermSpanScorer scorer = this.scorer(context);
            if (scorer != null) {
                final int newDoc = scorer.iterator().advance(doc);
                if (newDoc == doc) {
                    final float freq = scorer.sloppyFreq();
                    final Explanation freqExplanation = Explanation.match(freq, "phraseFreq=" + freq, new Explanation[0]);
                    final Similarity.SimScorer docScorer = this.similarity.simScorer(this.simWeight, context);
                    final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    final Explanation expl = Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", new Explanation[] { scoreExplanation });
                    final String field = ((SpanQuery)this.getQuery()).getField();
                    final Explanation payloadExpl = PayloadTermQuery.this.function.explain(doc, field, scorer.spans.payloadsSeen, scorer.spans.payloadScore);
                    if (PayloadTermQuery.this.includeSpanScore) {
                        return Explanation.match(expl.getValue() * payloadExpl.getValue(), "btq, product of:", new Explanation[] { expl, payloadExpl });
                    }
                    return Explanation.match(payloadExpl.getValue(), "btq(includeSpanScore=false), result of:", new Explanation[] { payloadExpl });
                }
            }
            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
        
        private class PayloadSpans extends FilterSpans
        {
            private final PayloadTermCollector payloadCollector;
            private final Similarity.SimScorer docScorer;
            float payloadScore;
            int payloadsSeen;
            
            protected PayloadSpans(final Spans in, final Similarity.SimScorer docScorer) {
                super(in);
                this.payloadCollector = new PayloadTermCollector();
                this.docScorer = docScorer;
            }
            
            protected FilterSpans.AcceptStatus accept(final Spans candidate) throws IOException {
                return FilterSpans.AcceptStatus.YES;
            }
            
            protected void doStartCurrentDoc() throws IOException {
                this.payloadScore = 0.0f;
                this.payloadsSeen = 0;
            }
            
            protected void doCurrentSpans() throws IOException {
                this.payloadCollector.reset();
                this.collect((SpanCollector)this.payloadCollector);
                this.processPayload();
            }
            
            protected void processPayload() throws IOException {
                final float payloadFactor = (this.payloadCollector.payload == null) ? 1.0f : this.docScorer.computePayloadFactor(this.docID(), this.startPosition(), this.endPosition(), this.payloadCollector.payload);
                this.payloadScore = PayloadTermQuery.this.function.currentScore(this.docID(), PayloadTermQuery.this.term.field(), this.startPosition(), this.endPosition(), this.payloadsSeen, this.payloadScore, payloadFactor);
                ++this.payloadsSeen;
            }
        }
        
        protected class PayloadTermSpanScorer extends SpanScorer
        {
            private final PayloadSpans spans;
            
            public PayloadTermSpanScorer(final PayloadSpans spans, final SpanWeight weight, final Similarity.SimScorer docScorer) throws IOException {
                super(weight, (Spans)spans, docScorer);
                this.spans = spans;
            }
            
            public float scoreCurrentDoc() throws IOException {
                return PayloadTermQuery.this.includeSpanScore ? (this.getSpanScore() * this.getPayloadScore()) : this.getPayloadScore();
            }
            
            protected float getSpanScore() throws IOException {
                return super.scoreCurrentDoc();
            }
            
            protected float getPayloadScore() {
                return PayloadTermQuery.this.function.docScore(this.docID(), PayloadTermQuery.this.term.field(), this.spans.payloadsSeen, this.spans.payloadScore);
            }
        }
    }
}
