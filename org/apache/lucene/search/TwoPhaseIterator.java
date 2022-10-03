package org.apache.lucene.search;

import java.io.IOException;
import java.util.Objects;

public abstract class TwoPhaseIterator
{
    protected final DocIdSetIterator approximation;
    
    protected TwoPhaseIterator(final DocIdSetIterator approximation) {
        this.approximation = Objects.requireNonNull(approximation);
    }
    
    public static DocIdSetIterator asDocIdSetIterator(final TwoPhaseIterator twoPhaseIterator) {
        final DocIdSetIterator approximation = twoPhaseIterator.approximation();
        return new DocIdSetIterator() {
            @Override
            public int docID() {
                return approximation.docID();
            }
            
            @Override
            public int nextDoc() throws IOException {
                return this.doNext(approximation.nextDoc());
            }
            
            @Override
            public int advance(final int target) throws IOException {
                return this.doNext(approximation.advance(target));
            }
            
            private int doNext(int doc) throws IOException {
                while (doc != Integer.MAX_VALUE) {
                    if (twoPhaseIterator.matches()) {
                        return doc;
                    }
                    doc = approximation.nextDoc();
                }
                return Integer.MAX_VALUE;
            }
            
            @Override
            public long cost() {
                return approximation.cost();
            }
        };
    }
    
    public DocIdSetIterator approximation() {
        return this.approximation;
    }
    
    public abstract boolean matches() throws IOException;
    
    public abstract float matchCost();
}
