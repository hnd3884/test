package org.apache.lucene.search.grouping;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Scorer;

class FakeScorer extends Scorer
{
    float score;
    int doc;
    int freq;
    
    FakeScorer() {
        super((Weight)null);
        this.doc = -1;
        this.freq = 1;
    }
    
    public int docID() {
        return this.doc;
    }
    
    public DocIdSetIterator iterator() {
        throw new UnsupportedOperationException();
    }
    
    public int freq() throws IOException {
        return this.freq;
    }
    
    public float score() throws IOException {
        return this.score;
    }
}
