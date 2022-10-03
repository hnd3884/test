package org.apache.lucene.search;

import java.util.Collection;

final class FakeScorer extends Scorer
{
    float score;
    int doc;
    int freq;
    
    public FakeScorer() {
        super(null);
        this.doc = -1;
        this.freq = 1;
    }
    
    @Override
    public int docID() {
        return this.doc;
    }
    
    @Override
    public int freq() {
        return this.freq;
    }
    
    @Override
    public float score() {
        return this.score;
    }
    
    @Override
    public DocIdSetIterator iterator() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Weight getWeight() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<ChildScorer> getChildren() {
        throw new UnsupportedOperationException();
    }
}
