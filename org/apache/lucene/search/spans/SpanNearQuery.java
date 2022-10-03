package org.apache.lucene.search.spans;

import java.util.Set;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import java.util.Objects;
import java.util.LinkedList;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.search.IndexSearcher;
import java.util.Iterator;
import org.apache.lucene.util.ToStringUtils;
import java.util.ArrayList;
import java.util.List;

public class SpanNearQuery extends SpanQuery implements Cloneable
{
    protected List<SpanQuery> clauses;
    protected int slop;
    protected boolean inOrder;
    protected String field;
    
    public static Builder newOrderedNearQuery(final String field) {
        return new Builder(field, true);
    }
    
    public static Builder newUnorderedNearQuery(final String field) {
        return new Builder(field, false);
    }
    
    public SpanNearQuery(final SpanQuery[] clauses, final int slop, final boolean inOrder) {
        this(clauses, slop, inOrder, true);
    }
    
    @Deprecated
    public SpanNearQuery(final SpanQuery[] clausesIn, final int slop, final boolean inOrder, final boolean collectPayloads) {
        this.clauses = new ArrayList<SpanQuery>(clausesIn.length);
        for (final SpanQuery clause : clausesIn) {
            if (this.field == null) {
                this.field = clause.getField();
            }
            else if (clause.getField() != null && !clause.getField().equals(this.field)) {
                throw new IllegalArgumentException("Clauses must have same field.");
            }
            this.clauses.add(clause);
        }
        this.slop = slop;
        this.inOrder = inOrder;
    }
    
    public SpanQuery[] getClauses() {
        return this.clauses.toArray(new SpanQuery[this.clauses.size()]);
    }
    
    public int getSlop() {
        return this.slop;
    }
    
    public boolean isInOrder() {
        return this.inOrder;
    }
    
    @Override
    public String getField() {
        return this.field;
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanNear([");
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
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final List<SpanWeight> subWeights = new ArrayList<SpanWeight>();
        for (final SpanQuery q : this.clauses) {
            subWeights.add(q.createWeight(searcher, false));
        }
        return new SpanNearWeight(subWeights, searcher, needsScores ? SpanQuery.getTermContexts(subWeights) : null);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        boolean actuallyRewritten = false;
        final List<SpanQuery> rewrittenClauses = new ArrayList<SpanQuery>();
        for (int i = 0; i < this.clauses.size(); ++i) {
            final SpanQuery c = this.clauses.get(i);
            final SpanQuery query = (SpanQuery)c.rewrite(reader);
            actuallyRewritten |= (query != c);
            rewrittenClauses.add(query);
        }
        if (actuallyRewritten) {
            final SpanNearQuery rewritten = (SpanNearQuery)this.clone();
            rewritten.clauses = rewrittenClauses;
            return rewritten;
        }
        return super.rewrite(reader);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanNearQuery spanNearQuery = (SpanNearQuery)o;
        return this.inOrder == spanNearQuery.inOrder && this.slop == spanNearQuery.slop && this.clauses.equals(spanNearQuery.clauses);
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result ^= this.clauses.hashCode();
        result += this.slop;
        final int fac = 1 + (this.inOrder ? 8 : 4);
        return fac * result;
    }
    
    public static class Builder
    {
        private final boolean ordered;
        private final String field;
        private final List<SpanQuery> clauses;
        private int slop;
        
        public Builder(final String field, final boolean ordered) {
            this.clauses = new LinkedList<SpanQuery>();
            this.field = field;
            this.ordered = ordered;
        }
        
        public Builder addClause(final SpanQuery clause) {
            if (!Objects.equals(clause.getField(), this.field)) {
                throw new IllegalArgumentException("Cannot add clause " + clause + " to SpanNearQuery for field " + this.field);
            }
            this.clauses.add(clause);
            return this;
        }
        
        public Builder addGap(final int width) {
            if (!this.ordered) {
                throw new IllegalArgumentException("Gaps can only be added to ordered near queries");
            }
            this.clauses.add(new SpanGapQuery(this.field, width));
            return this;
        }
        
        public Builder setSlop(final int slop) {
            this.slop = slop;
            return this;
        }
        
        public SpanNearQuery build() {
            return new SpanNearQuery(this.clauses.toArray(new SpanQuery[this.clauses.size()]), this.slop, this.ordered);
        }
    }
    
    public class SpanNearWeight extends SpanWeight
    {
        final List<SpanWeight> subWeights;
        
        public SpanNearWeight(final List<SpanWeight> subWeights, final IndexSearcher searcher, final Map<Term, TermContext> terms) throws IOException {
            super(SpanNearQuery.this, searcher, terms);
            this.subWeights = subWeights;
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            for (final SpanWeight w : this.subWeights) {
                w.extractTermContexts(contexts);
            }
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            final Terms terms = context.reader().terms(this.field);
            if (terms == null) {
                return null;
            }
            final ArrayList<Spans> subSpans = new ArrayList<Spans>(SpanNearQuery.this.clauses.size());
            for (final SpanWeight w : this.subWeights) {
                final Spans subSpan = w.getSpans(context, requiredPostings);
                if (subSpan == null) {
                    return null;
                }
                subSpans.add(subSpan);
            }
            return SpanNearQuery.this.inOrder ? new NearSpansOrdered(SpanNearQuery.this.slop, subSpans) : new NearSpansUnordered(SpanNearQuery.this.slop, subSpans);
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            for (final SpanWeight w : this.subWeights) {
                w.extractTerms(terms);
            }
        }
    }
    
    private static class SpanGapQuery extends SpanQuery
    {
        private final String field;
        private final int width;
        
        public SpanGapQuery(final String field, final int width) {
            this.field = field;
            this.width = width;
        }
        
        @Override
        public String getField() {
            return this.field;
        }
        
        @Override
        public String toString(final String field) {
            return "SpanGap(" + field + ":" + this.width + ")";
        }
        
        @Override
        public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            return new SpanGapWeight(searcher);
        }
        
        private class SpanGapWeight extends SpanWeight
        {
            SpanGapWeight(final IndexSearcher searcher) throws IOException {
                super(SpanGapQuery.this, searcher, null);
            }
            
            @Override
            public void extractTermContexts(final Map<Term, TermContext> contexts) {
            }
            
            @Override
            public Spans getSpans(final LeafReaderContext ctx, final Postings requiredPostings) throws IOException {
                return new GapSpans(SpanGapQuery.this.width);
            }
            
            @Override
            public void extractTerms(final Set<Term> terms) {
            }
        }
    }
    
    static class GapSpans extends Spans
    {
        int doc;
        int pos;
        final int width;
        
        GapSpans(final int width) {
            this.doc = -1;
            this.pos = -1;
            this.width = width;
        }
        
        @Override
        public int nextStartPosition() throws IOException {
            return ++this.pos;
        }
        
        public int skipToPosition(final int position) throws IOException {
            return this.pos = position;
        }
        
        @Override
        public int startPosition() {
            return this.pos;
        }
        
        @Override
        public int endPosition() {
            return this.pos + this.width;
        }
        
        @Override
        public int width() {
            return this.width;
        }
        
        @Override
        public void collect(final SpanCollector collector) throws IOException {
        }
        
        @Override
        public int docID() {
            return this.doc;
        }
        
        @Override
        public int nextDoc() throws IOException {
            this.pos = -1;
            return ++this.doc;
        }
        
        @Override
        public int advance(final int target) throws IOException {
            this.pos = -1;
            return this.doc = target;
        }
        
        @Override
        public long cost() {
            return 0L;
        }
        
        @Override
        public float positionsCost() {
            return 0.0f;
        }
    }
}
