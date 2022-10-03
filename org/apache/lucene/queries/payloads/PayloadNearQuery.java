package org.apache.lucene.queries.payloads;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.spans.FilterSpans;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.search.spans.SpanScorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.IndexSearcher;
import java.util.Objects;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanNearQuery;

@Deprecated
public class PayloadNearQuery extends SpanNearQuery
{
    protected String fieldName;
    protected PayloadFunction function;
    
    public PayloadNearQuery(final SpanQuery[] clauses, final int slop, final boolean inOrder) {
        this(clauses, slop, inOrder, new AveragePayloadFunction());
    }
    
    public PayloadNearQuery(final SpanQuery[] clauses, final int slop, final boolean inOrder, final PayloadFunction function) {
        super(clauses, slop, inOrder);
        this.fieldName = Objects.requireNonNull(clauses[0].getField(), "all clauses must have same non null field");
        this.function = Objects.requireNonNull(function);
    }
    
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final List<SpanWeight> subWeights = new ArrayList<SpanWeight>();
        for (final SpanQuery q : this.clauses) {
            subWeights.add(q.createWeight(searcher, false));
        }
        return (SpanWeight)new PayloadNearSpanWeight(subWeights, searcher, needsScores ? getTermContexts((Collection)subWeights) : null);
    }
    
    public PayloadNearQuery clone() {
        final int sz = this.clauses.size();
        final SpanQuery[] newClauses = new SpanQuery[sz];
        for (int i = 0; i < sz; ++i) {
            newClauses[i] = (SpanQuery)this.clauses.get(i).clone();
        }
        final PayloadNearQuery boostingNearQuery = new PayloadNearQuery(newClauses, this.slop, this.inOrder, this.function);
        boostingNearQuery.setBoost(this.getBoost());
        return boostingNearQuery;
    }
    
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("payloadNear([");
        final Iterator<SpanQuery> i = this.clauses.iterator();
        while (i.hasNext()) {
            final SpanQuery clause = i.next();
            buffer.append(clause.toString(field));
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("], ");
        buffer.append(this.slop);
        buffer.append(", ");
        buffer.append(this.inOrder);
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + this.fieldName.hashCode();
        result = 31 * result + this.function.hashCode();
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final PayloadNearQuery other = (PayloadNearQuery)obj;
        return this.fieldName.equals(other.fieldName) && this.function.equals(other.function);
    }
    
    private class PayloadNearSpanWeight extends SpanNearQuery.SpanNearWeight
    {
        public PayloadNearSpanWeight(final List<SpanWeight> subWeights, final IndexSearcher searcher, final Map<Term, TermContext> terms) throws IOException {
            super((SpanNearQuery)PayloadNearQuery.this, (List)subWeights, searcher, (Map)terms);
        }
        
        public SpanScorer scorer(final LeafReaderContext context) throws IOException {
            final Spans spans = super.getSpans(context, SpanWeight.Postings.PAYLOADS);
            if (spans == null) {
                return null;
            }
            final Similarity.SimScorer simScorer = this.getSimScorer(context);
            final PayloadSpans payloadSpans = new PayloadSpans(spans, simScorer);
            return new PayloadNearSpanScorer(payloadSpans, (SpanWeight)this, simScorer);
        }
        
        public Explanation explain(final LeafReaderContext context, final int doc) throws IOException {
            final PayloadNearSpanScorer scorer = (PayloadNearSpanScorer)this.scorer(context);
            if (scorer != null) {
                final int newDoc = scorer.iterator().advance(doc);
                if (newDoc == doc) {
                    final float freq = (float)scorer.freq();
                    final Explanation freqExplanation = Explanation.match(freq, "phraseFreq=" + freq, new Explanation[0]);
                    final Similarity.SimScorer docScorer = this.similarity.simScorer(this.simWeight, context);
                    final Explanation scoreExplanation = docScorer.explain(doc, freqExplanation);
                    final Explanation expl = Explanation.match(scoreExplanation.getValue(), "weight(" + this.getQuery() + " in " + doc + ") [" + this.similarity.getClass().getSimpleName() + "], result of:", new Explanation[] { scoreExplanation });
                    final String field = ((SpanQuery)this.getQuery()).getField();
                    final Explanation payloadExpl = PayloadNearQuery.this.function.explain(doc, field, scorer.spans.payloadsSeen, scorer.spans.payloadScore);
                    return Explanation.match(expl.getValue() * payloadExpl.getValue(), "PayloadNearQuery, product of:", new Explanation[] { expl, payloadExpl });
                }
            }
            return Explanation.noMatch("no matching term", new Explanation[0]);
        }
    }
    
    private class PayloadSpans extends FilterSpans implements SpanCollector
    {
        private final Similarity.SimScorer docScorer;
        public int payloadsSeen;
        public float payloadScore;
        private final List<byte[]> payloads;
        BytesRef scratch;
        
        private PayloadSpans(final Spans in, final Similarity.SimScorer docScorer) {
            super(in);
            this.payloads = new ArrayList<byte[]>();
            this.scratch = new BytesRef();
            this.docScorer = docScorer;
        }
        
        protected FilterSpans.AcceptStatus accept(final Spans candidate) throws IOException {
            return FilterSpans.AcceptStatus.YES;
        }
        
        public void collectLeaf(final PostingsEnum postings, final int position, final Term term) throws IOException {
            final BytesRef payload = postings.getPayload();
            if (payload == null) {
                return;
            }
            final byte[] bytes = new byte[payload.length];
            System.arraycopy(payload.bytes, payload.offset, bytes, 0, payload.length);
            this.payloads.add(bytes);
        }
        
        public void reset() {
            this.payloads.clear();
        }
        
        protected void doStartCurrentDoc() throws IOException {
            this.payloadScore = 0.0f;
            this.payloadsSeen = 0;
        }
        
        protected void doCurrentSpans() throws IOException {
            this.reset();
            this.collect((SpanCollector)this);
            this.processPayloads(this.payloads, this.startPosition(), this.endPosition());
        }
        
        protected void processPayloads(final Collection<byte[]> payLoads, final int start, final int end) {
            for (final byte[] thePayload : payLoads) {
                this.scratch.bytes = thePayload;
                this.scratch.offset = 0;
                this.scratch.length = thePayload.length;
                this.payloadScore = PayloadNearQuery.this.function.currentScore(this.docID(), PayloadNearQuery.this.fieldName, start, end, this.payloadsSeen, this.payloadScore, this.docScorer.computePayloadFactor(this.docID(), this.startPosition(), this.endPosition(), this.scratch));
                ++this.payloadsSeen;
            }
        }
    }
    
    private class PayloadNearSpanScorer extends SpanScorer
    {
        PayloadSpans spans;
        
        protected PayloadNearSpanScorer(final PayloadSpans spans, final SpanWeight weight, final Similarity.SimScorer docScorer) throws IOException {
            super(weight, (Spans)spans, docScorer);
            this.spans = spans;
        }
        
        public float scoreCurrentDoc() throws IOException {
            return super.scoreCurrentDoc() * PayloadNearQuery.this.function.docScore(this.docID(), PayloadNearQuery.this.fieldName, this.spans.payloadsSeen, this.spans.payloadScore);
        }
    }
}
