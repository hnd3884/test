package org.apache.lucene.search.spans;

import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import org.apache.lucene.search.ConjunctionDISI;
import java.util.List;
import org.apache.lucene.search.DocIdSetIterator;

abstract class ConjunctionSpans extends Spans
{
    final Spans[] subSpans;
    final DocIdSetIterator conjunction;
    boolean atFirstInCurrentDoc;
    boolean oneExhaustedInCurrentDoc;
    
    ConjunctionSpans(final List<Spans> subSpans) {
        if (subSpans.size() < 2) {
            throw new IllegalArgumentException("Less than 2 subSpans.size():" + subSpans.size());
        }
        this.subSpans = subSpans.toArray(new Spans[subSpans.size()]);
        this.conjunction = ConjunctionDISI.intersectSpans(subSpans);
        this.atFirstInCurrentDoc = true;
    }
    
    @Override
    public int docID() {
        return this.conjunction.docID();
    }
    
    @Override
    public long cost() {
        return this.conjunction.cost();
    }
    
    @Override
    public int nextDoc() throws IOException {
        return (this.conjunction.nextDoc() == Integer.MAX_VALUE) ? Integer.MAX_VALUE : this.toMatchDoc();
    }
    
    @Override
    public int advance(final int target) throws IOException {
        return (this.conjunction.advance(target) == Integer.MAX_VALUE) ? Integer.MAX_VALUE : this.toMatchDoc();
    }
    
    int toMatchDoc() throws IOException {
        this.oneExhaustedInCurrentDoc = false;
        while (!this.twoPhaseCurrentDocMatches()) {
            if (this.conjunction.nextDoc() == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
        }
        return this.docID();
    }
    
    abstract boolean twoPhaseCurrentDocMatches() throws IOException;
    
    @Override
    public TwoPhaseIterator asTwoPhaseIterator() {
        float totalMatchCost = 0.0f;
        for (final Spans spans : this.subSpans) {
            final TwoPhaseIterator tpi = spans.asTwoPhaseIterator();
            if (tpi != null) {
                totalMatchCost += tpi.matchCost();
            }
            else {
                totalMatchCost += spans.positionsCost();
            }
        }
        final float matchCost = totalMatchCost;
        return new TwoPhaseIterator(this.conjunction) {
            @Override
            public boolean matches() throws IOException {
                return ConjunctionSpans.this.twoPhaseCurrentDocMatches();
            }
            
            @Override
            public float matchCost() {
                return matchCost;
            }
        };
    }
    
    @Override
    public float positionsCost() {
        throw new UnsupportedOperationException();
    }
    
    public Spans[] getSubSpans() {
        return this.subSpans;
    }
}
