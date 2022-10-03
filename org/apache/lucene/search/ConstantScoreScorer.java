package org.apache.lucene.search;

import java.io.IOException;

public final class ConstantScoreScorer extends Scorer
{
    private final float score;
    private final TwoPhaseIterator twoPhaseIterator;
    private final DocIdSetIterator disi;
    
    public ConstantScoreScorer(final Weight weight, final float score, final DocIdSetIterator disi) {
        super(weight);
        this.score = score;
        this.twoPhaseIterator = null;
        this.disi = disi;
    }
    
    public ConstantScoreScorer(final Weight weight, final float score, final TwoPhaseIterator twoPhaseIterator) {
        super(weight);
        this.score = score;
        this.twoPhaseIterator = twoPhaseIterator;
        this.disi = TwoPhaseIterator.asDocIdSetIterator(twoPhaseIterator);
    }
    
    @Override
    public DocIdSetIterator iterator() {
        return this.disi;
    }
    
    @Override
    public TwoPhaseIterator twoPhaseIterator() {
        return this.twoPhaseIterator;
    }
    
    @Override
    public int docID() {
        return this.disi.docID();
    }
    
    @Override
    public float score() throws IOException {
        return this.score;
    }
    
    @Override
    public int freq() throws IOException {
        return 1;
    }
}
