package org.apache.lucene.search.spans;

import java.util.Set;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.ToStringUtils;
import java.util.Objects;

public final class SpanNotQuery extends SpanQuery
{
    private SpanQuery include;
    private SpanQuery exclude;
    private final int pre;
    private final int post;
    
    public SpanNotQuery(final SpanQuery include, final SpanQuery exclude) {
        this(include, exclude, 0, 0);
    }
    
    public SpanNotQuery(final SpanQuery include, final SpanQuery exclude, final int dist) {
        this(include, exclude, dist, dist);
    }
    
    public SpanNotQuery(final SpanQuery include, final SpanQuery exclude, final int pre, final int post) {
        this.include = Objects.requireNonNull(include);
        this.exclude = Objects.requireNonNull(exclude);
        this.pre = ((pre >= 0) ? pre : 0);
        this.post = ((post >= 0) ? post : 0);
        if (include.getField() != null && exclude.getField() != null && !include.getField().equals(exclude.getField())) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
    }
    
    public SpanQuery getInclude() {
        return this.include;
    }
    
    public SpanQuery getExclude() {
        return this.exclude;
    }
    
    @Override
    public String getField() {
        return this.include.getField();
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanNot(");
        buffer.append(this.include.toString(field));
        buffer.append(", ");
        buffer.append(this.exclude.toString(field));
        buffer.append(", ");
        buffer.append(Integer.toString(this.pre));
        buffer.append(", ");
        buffer.append(Integer.toString(this.post));
        buffer.append(")");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final SpanWeight includeWeight = this.include.createWeight(searcher, false);
        final SpanWeight excludeWeight = this.exclude.createWeight(searcher, false);
        return new SpanNotWeight(searcher, needsScores ? SpanQuery.getTermContexts(includeWeight, excludeWeight) : null, includeWeight, excludeWeight);
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final SpanQuery rewrittenInclude = (SpanQuery)this.include.rewrite(reader);
        final SpanQuery rewrittenExclude = (SpanQuery)this.exclude.rewrite(reader);
        if (rewrittenInclude != this.include || rewrittenExclude != this.exclude) {
            return new SpanNotQuery(rewrittenInclude, rewrittenExclude, this.pre, this.post);
        }
        return super.rewrite(reader);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanNotQuery other = (SpanNotQuery)o;
        return this.include.equals(other.include) && this.exclude.equals(other.exclude) && this.pre == other.pre && this.post == other.post;
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = Integer.rotateLeft(h, 1);
        h ^= this.include.hashCode();
        h = Integer.rotateLeft(h, 1);
        h ^= this.exclude.hashCode();
        h = Integer.rotateLeft(h, 1);
        h ^= this.pre;
        h = Integer.rotateLeft(h, 1);
        h ^= this.post;
        return h;
    }
    
    public class SpanNotWeight extends SpanWeight
    {
        final SpanWeight includeWeight;
        final SpanWeight excludeWeight;
        
        public SpanNotWeight(final IndexSearcher searcher, final Map<Term, TermContext> terms, final SpanWeight includeWeight, final SpanWeight excludeWeight) throws IOException {
            super(SpanNotQuery.this, searcher, terms);
            this.includeWeight = includeWeight;
            this.excludeWeight = excludeWeight;
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            this.includeWeight.extractTermContexts(contexts);
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            final Spans includeSpans = this.includeWeight.getSpans(context, requiredPostings);
            if (includeSpans == null) {
                return null;
            }
            final Spans excludeSpans = this.excludeWeight.getSpans(context, requiredPostings);
            if (excludeSpans == null) {
                return new ScoringWrapperSpans(includeSpans, this.getSimScorer(context));
            }
            final TwoPhaseIterator excludeTwoPhase = excludeSpans.asTwoPhaseIterator();
            final DocIdSetIterator excludeApproximation = (excludeTwoPhase == null) ? null : excludeTwoPhase.approximation();
            return new FilterSpans(includeSpans) {
                int lastApproxDoc = -1;
                boolean lastApproxResult = false;
                
                @Override
                protected AcceptStatus accept(final Spans candidate) throws IOException {
                    final int doc = candidate.docID();
                    if (doc > excludeSpans.docID()) {
                        if (excludeTwoPhase != null) {
                            if (excludeApproximation.advance(doc) == doc) {
                                this.lastApproxDoc = doc;
                                this.lastApproxResult = excludeTwoPhase.matches();
                            }
                        }
                        else {
                            excludeSpans.advance(doc);
                        }
                    }
                    else if (excludeTwoPhase != null && doc == excludeSpans.docID() && doc != this.lastApproxDoc) {
                        this.lastApproxDoc = doc;
                        this.lastApproxResult = excludeTwoPhase.matches();
                    }
                    if (doc != excludeSpans.docID() || (doc == this.lastApproxDoc && !this.lastApproxResult)) {
                        return AcceptStatus.YES;
                    }
                    if (excludeSpans.startPosition() == -1) {
                        excludeSpans.nextStartPosition();
                    }
                    while (excludeSpans.endPosition() <= candidate.startPosition() - SpanNotQuery.this.pre) {
                        if (excludeSpans.nextStartPosition() == Integer.MAX_VALUE) {
                            return AcceptStatus.YES;
                        }
                    }
                    if (candidate.endPosition() + SpanNotQuery.this.post <= excludeSpans.startPosition()) {
                        return AcceptStatus.YES;
                    }
                    return AcceptStatus.NO;
                }
            };
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            this.includeWeight.extractTerms(terms);
        }
    }
}
