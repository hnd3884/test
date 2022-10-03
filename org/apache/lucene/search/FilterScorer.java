package org.apache.lucene.search;

import java.io.IOException;

public abstract class FilterScorer extends Scorer
{
    protected final Scorer in;
    
    public FilterScorer(final Scorer in) {
        super(in.weight);
        this.in = in;
    }
    
    public FilterScorer(final Scorer in, final Weight weight) {
        super(weight);
        if (in == null) {
            throw new NullPointerException("wrapped Scorer must not be null");
        }
        this.in = in;
    }
    
    @Override
    public float score() throws IOException {
        return this.in.score();
    }
    
    @Override
    public int freq() throws IOException {
        return this.in.freq();
    }
    
    @Override
    public final int docID() {
        return this.in.docID();
    }
    
    @Override
    public final DocIdSetIterator iterator() {
        return this.in.iterator();
    }
    
    @Override
    public final TwoPhaseIterator twoPhaseIterator() {
        return this.in.twoPhaseIterator();
    }
}
