package org.apache.lucene.queries.function;

import java.io.IOException;
import org.apache.lucene.search.Weight;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.TwoPhaseIterator;
import org.apache.lucene.search.Scorer;

public abstract class ValueSourceScorer extends Scorer
{
    protected final FunctionValues values;
    private final TwoPhaseIterator twoPhaseIterator;
    private final DocIdSetIterator disi;
    
    protected ValueSourceScorer(final IndexReader reader, final FunctionValues values) {
        super((Weight)null);
        this.values = values;
        final DocIdSetIterator approximation = DocIdSetIterator.all(reader.maxDoc());
        this.twoPhaseIterator = new TwoPhaseIterator(approximation) {
            public boolean matches() throws IOException {
                return ValueSourceScorer.this.matches(this.approximation.docID());
            }
            
            public float matchCost() {
                return 100.0f;
            }
        };
        this.disi = TwoPhaseIterator.asDocIdSetIterator(this.twoPhaseIterator);
    }
    
    public abstract boolean matches(final int p0);
    
    public DocIdSetIterator iterator() {
        return this.disi;
    }
    
    public TwoPhaseIterator twoPhaseIterator() {
        return this.twoPhaseIterator;
    }
    
    public int docID() {
        return this.disi.docID();
    }
    
    public float score() throws IOException {
        final float score = this.values.floatVal(this.disi.docID());
        return (score > Float.NEGATIVE_INFINITY) ? score : -3.4028235E38f;
    }
    
    public int freq() throws IOException {
        return 1;
    }
}
