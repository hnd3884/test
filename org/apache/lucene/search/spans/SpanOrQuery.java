package org.apache.lucene.search.spans;

import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.DisjunctionDISIApproximation;
import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.search.DisiWrapper;
import org.apache.lucene.search.DisiPriorityQueue;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Set;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import org.apache.lucene.search.Weight;
import java.util.Collection;
import org.apache.lucene.search.IndexSearcher;
import java.util.Iterator;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import java.util.ArrayList;
import java.util.List;

public final class SpanOrQuery extends SpanQuery
{
    private List<SpanQuery> clauses;
    private String field;
    
    public SpanOrQuery(final SpanQuery... clauses) {
        this.clauses = new ArrayList<SpanQuery>(clauses.length);
        for (final SpanQuery seq : clauses) {
            this.addClause(seq);
        }
    }
    
    @Deprecated
    public final void addClause(final SpanQuery clause) {
        if (this.field == null) {
            this.field = clause.getField();
        }
        else if (clause.getField() != null && !clause.getField().equals(this.field)) {
            throw new IllegalArgumentException("Clauses must have same field.");
        }
        this.clauses.add(clause);
    }
    
    public SpanQuery[] getClauses() {
        return this.clauses.toArray(new SpanQuery[this.clauses.size()]);
    }
    
    @Override
    public String getField() {
        return this.field;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        final SpanOrQuery rewritten = new SpanOrQuery(new SpanQuery[0]);
        boolean actuallyRewritten = false;
        for (int i = 0; i < this.clauses.size(); ++i) {
            final SpanQuery c = this.clauses.get(i);
            final SpanQuery query = (SpanQuery)c.rewrite(reader);
            actuallyRewritten |= (query != c);
            rewritten.addClause(query);
        }
        if (actuallyRewritten) {
            return rewritten;
        }
        return super.rewrite(reader);
    }
    
    @Override
    public String toString(final String field) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("spanOr([");
        final Iterator<SpanQuery> i = this.clauses.iterator();
        while (i.hasNext()) {
            final SpanQuery clause = i.next();
            buffer.append(clause.toString(field));
            if (i.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("])");
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final SpanOrQuery that = (SpanOrQuery)o;
        return this.clauses.equals(that.clauses);
    }
    
    @Override
    public int hashCode() {
        int h = super.hashCode();
        h = (h * 7 ^ this.clauses.hashCode());
        return h;
    }
    
    @Override
    public SpanWeight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
        final List<SpanWeight> subWeights = new ArrayList<SpanWeight>(this.clauses.size());
        for (final SpanQuery q : this.clauses) {
            subWeights.add(q.createWeight(searcher, false));
        }
        return new SpanOrWeight(searcher, needsScores ? SpanQuery.getTermContexts(subWeights) : null, subWeights);
    }
    
    public class SpanOrWeight extends SpanWeight
    {
        final List<SpanWeight> subWeights;
        
        public SpanOrWeight(final IndexSearcher searcher, final Map<Term, TermContext> terms, final List<SpanWeight> subWeights) throws IOException {
            super(SpanOrQuery.this, searcher, terms);
            this.subWeights = subWeights;
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            for (final SpanWeight w : this.subWeights) {
                w.extractTerms(terms);
            }
        }
        
        @Override
        public void extractTermContexts(final Map<Term, TermContext> contexts) {
            for (final SpanWeight w : this.subWeights) {
                w.extractTermContexts(contexts);
            }
        }
        
        @Override
        public Spans getSpans(final LeafReaderContext context, final Postings requiredPostings) throws IOException {
            final ArrayList<Spans> subSpans = new ArrayList<Spans>(SpanOrQuery.this.clauses.size());
            for (final SpanWeight w : this.subWeights) {
                final Spans spans = w.getSpans(context, requiredPostings);
                if (spans != null) {
                    subSpans.add(spans);
                }
            }
            if (subSpans.size() == 0) {
                return null;
            }
            if (subSpans.size() == 1) {
                return new ScoringWrapperSpans(subSpans.get(0), this.getSimScorer(context));
            }
            final DisiPriorityQueue byDocQueue = new DisiPriorityQueue(subSpans.size());
            final Iterator i$2 = subSpans.iterator();
            while (i$2.hasNext()) {
                final Spans spans = i$2.next();
                byDocQueue.add(new DisiWrapper(spans));
            }
            final SpanPositionQueue byPositionQueue = new SpanPositionQueue(subSpans.size());
            return new Spans() {
                Spans topPositionSpans = null;
                float positionsCost = -1.0f;
                int lastDocTwoPhaseMatched = -1;
                long cost = -1L;
                
                @Override
                public int nextDoc() throws IOException {
                    this.topPositionSpans = null;
                    DisiWrapper topDocSpans = byDocQueue.top();
                    final int currentDoc = topDocSpans.doc;
                    do {
                        topDocSpans.doc = topDocSpans.iterator.nextDoc();
                        topDocSpans = byDocQueue.updateTop();
                    } while (topDocSpans.doc == currentDoc);
                    return topDocSpans.doc;
                }
                
                @Override
                public int advance(final int target) throws IOException {
                    this.topPositionSpans = null;
                    DisiWrapper topDocSpans = byDocQueue.top();
                    do {
                        topDocSpans.doc = topDocSpans.iterator.advance(target);
                        topDocSpans = byDocQueue.updateTop();
                    } while (topDocSpans.doc < target);
                    return topDocSpans.doc;
                }
                
                @Override
                public int docID() {
                    final DisiWrapper topDocSpans = byDocQueue.top();
                    return topDocSpans.doc;
                }
                
                @Override
                public TwoPhaseIterator asTwoPhaseIterator() {
                    float sumMatchCost = 0.0f;
                    long sumApproxCost = 0L;
                    for (final DisiWrapper w : byDocQueue) {
                        if (w.twoPhaseView != null) {
                            final long costWeight = (w.cost <= 1L) ? 1L : w.cost;
                            sumMatchCost += w.twoPhaseView.matchCost() * costWeight;
                            sumApproxCost += costWeight;
                        }
                    }
                    if (sumApproxCost == 0L) {
                        this.computePositionsCost();
                        return null;
                    }
                    final float matchCost = sumMatchCost / sumApproxCost;
                    return new TwoPhaseIterator(new DisjunctionDISIApproximation(byDocQueue)) {
                        @Override
                        public boolean matches() throws IOException {
                            return Spans.this.twoPhaseCurrentDocMatches();
                        }
                        
                        @Override
                        public float matchCost() {
                            return matchCost;
                        }
                    };
                }
                
                void computePositionsCost() {
                    float sumPositionsCost = 0.0f;
                    long sumCost = 0L;
                    for (final DisiWrapper w : byDocQueue) {
                        final long costWeight = (w.cost <= 1L) ? 1L : w.cost;
                        sumPositionsCost += w.spans.positionsCost() * costWeight;
                        sumCost += costWeight;
                    }
                    this.positionsCost = sumPositionsCost / sumCost;
                }
                
                @Override
                public float positionsCost() {
                    assert this.positionsCost > 0.0f;
                    return this.positionsCost;
                }
                
                boolean twoPhaseCurrentDocMatches() throws IOException {
                    DisiWrapper listAtCurrentDoc = byDocQueue.topList();
                    final int currentDoc = listAtCurrentDoc.doc;
                    while (listAtCurrentDoc.twoPhaseView != null) {
                        if (listAtCurrentDoc.twoPhaseView.matches()) {
                            listAtCurrentDoc.lastApproxMatchDoc = currentDoc;
                            break;
                        }
                        listAtCurrentDoc.lastApproxNonMatchDoc = currentDoc;
                        listAtCurrentDoc = listAtCurrentDoc.next;
                        if (listAtCurrentDoc == null) {
                            return false;
                        }
                    }
                    this.lastDocTwoPhaseMatched = currentDoc;
                    this.topPositionSpans = null;
                    return true;
                }
                
                void fillPositionQueue() throws IOException {
                    assert byPositionQueue.size() == 0;
                    for (DisiWrapper listAtCurrentDoc = byDocQueue.topList(); listAtCurrentDoc != null; listAtCurrentDoc = listAtCurrentDoc.next) {
                        Spans spansAtDoc = listAtCurrentDoc.spans;
                        if (this.lastDocTwoPhaseMatched == listAtCurrentDoc.doc && listAtCurrentDoc.twoPhaseView != null) {
                            if (listAtCurrentDoc.lastApproxNonMatchDoc == listAtCurrentDoc.doc) {
                                spansAtDoc = null;
                            }
                            else if (listAtCurrentDoc.lastApproxMatchDoc != listAtCurrentDoc.doc && !listAtCurrentDoc.twoPhaseView.matches()) {
                                spansAtDoc = null;
                            }
                        }
                        if (spansAtDoc != null) {
                            assert spansAtDoc.docID() == listAtCurrentDoc.doc;
                            assert spansAtDoc.startPosition() == -1;
                            spansAtDoc.nextStartPosition();
                            assert spansAtDoc.startPosition() != Integer.MAX_VALUE;
                            byPositionQueue.add(spansAtDoc);
                        }
                    }
                    assert byPositionQueue.size() > 0;
                }
                
                @Override
                public int nextStartPosition() throws IOException {
                    if (this.topPositionSpans == null) {
                        byPositionQueue.clear();
                        this.fillPositionQueue();
                        this.topPositionSpans = byPositionQueue.top();
                    }
                    else {
                        this.topPositionSpans.nextStartPosition();
                        this.topPositionSpans = byPositionQueue.updateTop();
                    }
                    return this.topPositionSpans.startPosition();
                }
                
                @Override
                public int startPosition() {
                    return (this.topPositionSpans == null) ? -1 : this.topPositionSpans.startPosition();
                }
                
                @Override
                public int endPosition() {
                    return (this.topPositionSpans == null) ? -1 : this.topPositionSpans.endPosition();
                }
                
                @Override
                public int width() {
                    return this.topPositionSpans.width();
                }
                
                @Override
                public void collect(final SpanCollector collector) throws IOException {
                    this.topPositionSpans.collect(collector);
                }
                
                @Override
                public String toString() {
                    return "spanOr(" + SpanOrQuery.this + ")@" + this.docID() + ": " + this.startPosition() + " - " + this.endPosition();
                }
                
                @Override
                public long cost() {
                    if (this.cost == -1L) {
                        this.cost = 0L;
                        for (final Spans spans : subSpans) {
                            this.cost += spans.cost();
                        }
                    }
                    return this.cost;
                }
            };
        }
    }
}
