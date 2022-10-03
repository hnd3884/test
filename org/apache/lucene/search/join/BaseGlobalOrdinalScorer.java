package org.apache.lucene.search.join;

import org.apache.lucene.search.TwoPhaseIterator;
import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.Scorer;

abstract class BaseGlobalOrdinalScorer extends Scorer
{
    final SortedDocValues values;
    final DocIdSetIterator approximation;
    float score;
    
    public BaseGlobalOrdinalScorer(final Weight weight, final SortedDocValues values, final DocIdSetIterator approximationScorer) {
        super(weight);
        this.values = values;
        this.approximation = approximationScorer;
    }
    
    public float score() throws IOException {
        return this.score;
    }
    
    public int docID() {
        return this.approximation.docID();
    }
    
    public DocIdSetIterator iterator() {
        return TwoPhaseIterator.asDocIdSetIterator(this.twoPhaseIterator());
    }
    
    public TwoPhaseIterator twoPhaseIterator() {
        return this.createTwoPhaseIterator(this.approximation);
    }
    
    public int freq() throws IOException {
        return 1;
    }
    
    protected abstract TwoPhaseIterator createTwoPhaseIterator(final DocIdSetIterator p0);
}
