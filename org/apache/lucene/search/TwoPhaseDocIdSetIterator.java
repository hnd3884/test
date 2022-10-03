package org.apache.lucene.search;

import java.io.IOException;

public abstract class TwoPhaseDocIdSetIterator
{
    public static DocIdSetIterator asDocIdSetIterator(final TwoPhaseDocIdSetIterator twoPhaseIterator) {
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
    
    public abstract DocIdSetIterator approximation();
    
    public abstract boolean matches() throws IOException;
}
