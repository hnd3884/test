package org.apache.lucene.search;

import java.util.Collections;
import java.util.Collection;
import java.io.IOException;

class ReqExclScorer extends Scorer
{
    private final Scorer reqScorer;
    private final DocIdSetIterator reqApproximation;
    private final DocIdSetIterator exclApproximation;
    private final TwoPhaseIterator reqTwoPhaseIterator;
    private final TwoPhaseIterator exclTwoPhaseIterator;
    private static final int ADVANCE_COST = 10;
    
    public ReqExclScorer(final Scorer reqScorer, final Scorer exclScorer) {
        super(reqScorer.weight);
        this.reqScorer = reqScorer;
        this.reqTwoPhaseIterator = reqScorer.twoPhaseIterator();
        if (this.reqTwoPhaseIterator == null) {
            this.reqApproximation = reqScorer.iterator();
        }
        else {
            this.reqApproximation = this.reqTwoPhaseIterator.approximation();
        }
        this.exclTwoPhaseIterator = exclScorer.twoPhaseIterator();
        if (this.exclTwoPhaseIterator == null) {
            this.exclApproximation = exclScorer.iterator();
        }
        else {
            this.exclApproximation = this.exclTwoPhaseIterator.approximation();
        }
    }
    
    private static boolean matchesOrNull(final TwoPhaseIterator it) throws IOException {
        return it == null || it.matches();
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return TwoPhaseIterator.asDocIdSetIterator(this.twoPhaseIterator());
    }
    
    @Override
    public int docID() {
        return this.reqApproximation.docID();
    }
    
    @Override
    public int freq() throws IOException {
        return this.reqScorer.freq();
    }
    
    @Override
    public float score() throws IOException {
        return this.reqScorer.score();
    }
    
    @Override
    public Collection<ChildScorer> getChildren() {
        return Collections.singleton(new ChildScorer(this.reqScorer, "MUST"));
    }
    
    private static float matchCost(final DocIdSetIterator reqApproximation, final TwoPhaseIterator reqTwoPhaseIterator, final DocIdSetIterator exclApproximation, final TwoPhaseIterator exclTwoPhaseIterator) {
        float matchCost = 2.0f;
        if (reqTwoPhaseIterator != null) {
            matchCost += reqTwoPhaseIterator.matchCost();
        }
        final float exclMatchCost = 10.0f + ((exclTwoPhaseIterator == null) ? 0.0f : exclTwoPhaseIterator.matchCost());
        float ratio;
        if (reqApproximation.cost() <= 0L) {
            ratio = 1.0f;
        }
        else if (exclApproximation.cost() <= 0L) {
            ratio = 0.0f;
        }
        else {
            ratio = Math.min(reqApproximation.cost(), exclApproximation.cost()) / (float)reqApproximation.cost();
        }
        matchCost += ratio * exclMatchCost;
        return matchCost;
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        final float matchCost = matchCost(this.reqApproximation, this.reqTwoPhaseIterator, this.exclApproximation, this.exclTwoPhaseIterator);
        if (this.reqTwoPhaseIterator == null || (this.exclTwoPhaseIterator != null && this.reqTwoPhaseIterator.matchCost() <= this.exclTwoPhaseIterator.matchCost())) {
            return new TwoPhaseIterator(this.reqApproximation) {
                @Override
                public boolean matches() throws IOException {
                    final int doc = ReqExclScorer.this.reqApproximation.docID();
                    int exclDoc = ReqExclScorer.this.exclApproximation.docID();
                    if (exclDoc < doc) {
                        exclDoc = ReqExclScorer.this.exclApproximation.advance(doc);
                    }
                    if (exclDoc != doc) {
                        return matchesOrNull(ReqExclScorer.this.reqTwoPhaseIterator);
                    }
                    return matchesOrNull(ReqExclScorer.this.reqTwoPhaseIterator) && !matchesOrNull(ReqExclScorer.this.exclTwoPhaseIterator);
                }
                
                @Override
                public float matchCost() {
                    return matchCost;
                }
            };
        }
        return new TwoPhaseIterator(this.reqApproximation) {
            @Override
            public boolean matches() throws IOException {
                final int doc = ReqExclScorer.this.reqApproximation.docID();
                int exclDoc = ReqExclScorer.this.exclApproximation.docID();
                if (exclDoc < doc) {
                    exclDoc = ReqExclScorer.this.exclApproximation.advance(doc);
                }
                if (exclDoc != doc) {
                    return matchesOrNull(ReqExclScorer.this.reqTwoPhaseIterator);
                }
                return !matchesOrNull(ReqExclScorer.this.exclTwoPhaseIterator) && matchesOrNull(ReqExclScorer.this.reqTwoPhaseIterator);
            }
            
            @Override
            public float matchCost() {
                return matchCost;
            }
        };
    }
}
