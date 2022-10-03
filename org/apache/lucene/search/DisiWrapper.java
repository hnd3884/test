package org.apache.lucene.search;

import org.apache.lucene.search.spans.Spans;

public class DisiWrapper
{
    public final DocIdSetIterator iterator;
    public final Scorer scorer;
    public final long cost;
    public final float matchCost;
    public int doc;
    public DisiWrapper next;
    public final DocIdSetIterator approximation;
    public final TwoPhaseIterator twoPhaseView;
    public final Spans spans;
    public int lastApproxMatchDoc;
    public int lastApproxNonMatchDoc;
    
    public DisiWrapper(final Scorer scorer) {
        this.scorer = scorer;
        this.spans = null;
        this.iterator = scorer.iterator();
        this.cost = this.iterator.cost();
        this.doc = -1;
        this.twoPhaseView = scorer.twoPhaseIterator();
        if (this.twoPhaseView != null) {
            this.approximation = this.twoPhaseView.approximation();
            this.matchCost = this.twoPhaseView.matchCost();
        }
        else {
            this.approximation = this.iterator;
            this.matchCost = 0.0f;
        }
    }
    
    public DisiWrapper(final Spans spans) {
        this.scorer = null;
        this.spans = spans;
        this.iterator = spans;
        this.cost = this.iterator.cost();
        this.doc = -1;
        this.twoPhaseView = spans.asTwoPhaseIterator();
        if (this.twoPhaseView != null) {
            this.approximation = this.twoPhaseView.approximation();
            this.matchCost = this.twoPhaseView.matchCost();
        }
        else {
            this.approximation = this.iterator;
            this.matchCost = 0.0f;
        }
        this.lastApproxNonMatchDoc = -2;
        this.lastApproxMatchDoc = -2;
    }
}
