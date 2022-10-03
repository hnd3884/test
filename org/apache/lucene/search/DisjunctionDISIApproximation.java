package org.apache.lucene.search;

import java.io.IOException;
import java.util.Iterator;

public class DisjunctionDISIApproximation extends DocIdSetIterator
{
    final DisiPriorityQueue subIterators;
    final long cost;
    
    public DisjunctionDISIApproximation(final DisiPriorityQueue subIterators) {
        this.subIterators = subIterators;
        long cost = 0L;
        for (final DisiWrapper w : subIterators) {
            cost += w.cost;
        }
        this.cost = cost;
    }
    
    @Override
    public long cost() {
        return this.cost;
    }
    
    @Override
    public int docID() {
        return this.subIterators.top().doc;
    }
    
    @Override
    public int nextDoc() throws IOException {
        DisiWrapper top = this.subIterators.top();
        final int doc = top.doc;
        do {
            top.doc = top.approximation.nextDoc();
            top = this.subIterators.updateTop();
        } while (top.doc == doc);
        return top.doc;
    }
    
    @Override
    public int advance(final int target) throws IOException {
        DisiWrapper top = this.subIterators.top();
        do {
            top.doc = top.approximation.advance(target);
            top = this.subIterators.updateTop();
        } while (top.doc < target);
        return top.doc;
    }
}
