package org.apache.lucene.search.spans;

import java.util.ArrayList;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.util.Objects;

abstract class SpanContainQuery extends SpanQuery implements Cloneable
{
    SpanQuery big;
    SpanQuery little;
    
    SpanContainQuery(final SpanQuery big, final SpanQuery little) {
        this.big = Objects.requireNonNull(big);
        this.little = Objects.requireNonNull(little);
        Objects.requireNonNull(big.getField());
        Objects.requireNonNull(little.getField());
        if (!big.getField().equals(little.getField())) {
            throw new IllegalArgumentException("big and little not same field");
        }
    }
    
    @Override
    public String getField() {
        return this.big.getField();
    }
    
    String toString(final String field, final String name) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        buffer.append("(");
        buffer.append(this.big.toString(field));
        buffer.append(", ");
        buffer.append(this.little.toString(field));
        buffer.append(")");
        return buffer.toString();
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final SpanQuery rewrittenBig = (SpanQuery)this.big.rewrite(reader);
        final SpanQuery rewrittenLittle = (SpanQuery)this.little.rewrite(reader);
        if (this.big != rewrittenBig || this.little != rewrittenLittle) {
            final SpanContainQuery clone = (SpanContainQuery)super.clone();
            clone.big = rewrittenBig;
            clone.little = rewrittenLittle;
            return clone;
        }
        return super.rewrite(reader);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanContainQuery other = (SpanContainQuery)o;
        return this.big.equals(other.big) && this.little.equals(other.little);
    }
    
    @Override
    public int hashCode() {
        int h = Integer.rotateLeft(super.hashCode(), 1);
        h ^= this.big.hashCode();
        h = Integer.rotateLeft(h, 1);
        h ^= this.little.hashCode();
        return h;
    }
    
    public abstract class SpanContainWeight extends SpanWeight
    {
        final SpanWeight bigWeight;
        final SpanWeight littleWeight;
        
        public SpanContainWeight(final IndexSearcher searcher, final Map<Term, TermContext> terms, final SpanWeight bigWeight, final SpanWeight littleWeight) throws IOException {
            super(SpanContainQuery.this, searcher, terms);
            this.bigWeight = bigWeight;
            this.littleWeight = littleWeight;
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            this.bigWeight.extractTerms(terms);
            this.littleWeight.extractTerms(terms);
        }
        
        ArrayList<Spans> prepareConjunction(final LeafReaderContext context, final Postings postings) throws IOException {
            final Spans bigSpans = this.bigWeight.getSpans(context, postings);
            if (bigSpans == null) {
                return null;
            }
            final Spans littleSpans = this.littleWeight.getSpans(context, postings);
            if (littleSpans == null) {
                return null;
            }
            final ArrayList<Spans> bigAndLittle = new ArrayList<Spans>();
            bigAndLittle.add(bigSpans);
            bigAndLittle.add(littleSpans);
            return bigAndLittle;
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            this.bigWeight.extractTermContexts(contexts);
            this.littleWeight.extractTermContexts(contexts);
        }
    }
}
