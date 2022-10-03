package org.apache.lucene.search;

import org.apache.lucene.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

abstract class DisjunctionScorer extends Scorer
{
    private final boolean needsScores;
    private final DisiPriorityQueue subScorers;
    private final DisjunctionDISIApproximation approximation;
    private final TwoPhase twoPhase;
    
    protected DisjunctionScorer(final Weight weight, final List<Scorer> subScorers, final boolean needsScores) {
        super(weight);
        if (subScorers.size() <= 1) {
            throw new IllegalArgumentException("There must be at least 2 subScorers");
        }
        this.subScorers = new DisiPriorityQueue(subScorers.size());
        for (final Scorer scorer : subScorers) {
            final DisiWrapper w = new DisiWrapper(scorer);
            this.subScorers.add(w);
        }
        this.needsScores = needsScores;
        this.approximation = new DisjunctionDISIApproximation(this.subScorers);
        boolean hasApproximation = false;
        float sumMatchCost = 0.0f;
        long sumApproxCost = 0L;
        for (final DisiWrapper w2 : this.subScorers) {
            final long costWeight = (w2.cost <= 1L) ? 1L : w2.cost;
            sumApproxCost += costWeight;
            if (w2.twoPhaseView != null) {
                hasApproximation = true;
                sumMatchCost += w2.matchCost * costWeight;
            }
        }
        if (!hasApproximation) {
            this.twoPhase = null;
        }
        else {
            final float matchCost = sumMatchCost / sumApproxCost;
            this.twoPhase = new TwoPhase((DocIdSetIterator)this.approximation, matchCost);
        }
    }
    
    @Override
    public DocIdSetIterator iterator() {
        if (this.twoPhase != null) {
            return TwoPhaseIterator.asDocIdSetIterator(this.twoPhase);
        }
        return this.approximation;
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return this.twoPhase;
    }
    
    @Override
    public final int docID() {
        return this.subScorers.top().doc;
    }
    
    DisiWrapper getSubMatches() throws IOException {
        if (this.twoPhase == null) {
            return this.subScorers.topList();
        }
        return this.twoPhase.getSubMatches();
    }
    
    @Override
    public final int freq() throws IOException {
        final DisiWrapper subMatches = this.getSubMatches();
        int freq = 1;
        for (DisiWrapper w = subMatches.next; w != null; w = w.next) {
            ++freq;
        }
        return freq;
    }
    
    @Override
    public final float score() throws IOException {
        return this.score(this.getSubMatches());
    }
    
    protected abstract float score(final DisiWrapper p0) throws IOException;
    
    @Override
    public final Collection<ChildScorer> getChildren() {
        final ArrayList<ChildScorer> children = new ArrayList<ChildScorer>();
        for (final DisiWrapper scorer : this.subScorers) {
            children.add(new ChildScorer(scorer.scorer, "SHOULD"));
        }
        return children;
    }
    
    private class TwoPhase extends TwoPhaseIterator
    {
        private final float matchCost;
        DisiWrapper verifiedMatches;
        final PriorityQueue<DisiWrapper> unverifiedMatches;
        
        private TwoPhase(final DocIdSetIterator approximation, final float matchCost) {
            super(approximation);
            this.matchCost = matchCost;
            this.unverifiedMatches = new PriorityQueue<DisiWrapper>(DisjunctionScorer.this.subScorers.size()) {
                @Override
                protected boolean lessThan(final DisiWrapper a, final DisiWrapper b) {
                    return a.matchCost < b.matchCost;
                }
            };
        }
        
        DisiWrapper getSubMatches() throws IOException {
            for (final DisiWrapper w : this.unverifiedMatches) {
                if (w.twoPhaseView.matches()) {
                    w.next = this.verifiedMatches;
                    this.verifiedMatches = w;
                }
            }
            this.unverifiedMatches.clear();
            return this.verifiedMatches;
        }
        
        @Override
        public boolean matches() throws IOException {
            this.verifiedMatches = null;
            this.unverifiedMatches.clear();
            DisiWrapper next;
            for (DisiWrapper w = DisjunctionScorer.this.subScorers.topList(); w != null; w = next) {
                next = w.next;
                if (w.twoPhaseView == null) {
                    w.next = this.verifiedMatches;
                    this.verifiedMatches = w;
                    if (!DisjunctionScorer.this.needsScores) {
                        return true;
                    }
                }
                else {
                    this.unverifiedMatches.add(w);
                }
            }
            if (this.verifiedMatches != null) {
                return true;
            }
            while (this.unverifiedMatches.size() > 0) {
                final DisiWrapper w = this.unverifiedMatches.pop();
                if (w.twoPhaseView.matches()) {
                    w.next = null;
                    this.verifiedMatches = w;
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public float matchCost() {
            return this.matchCost;
        }
    }
}
