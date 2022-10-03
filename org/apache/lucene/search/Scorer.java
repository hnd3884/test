package org.apache.lucene.search;

import java.util.Collections;
import java.util.Collection;
import java.io.IOException;

public abstract class Scorer
{
    protected final Weight weight;
    
    protected Scorer(final Weight weight) {
        this.weight = weight;
    }
    
    public abstract int docID();
    
    public abstract float score() throws IOException;
    
    public abstract int freq() throws IOException;
    
    public Weight getWeight() {
        return this.weight;
    }
    
    public Collection<ChildScorer> getChildren() {
        return (Collection<ChildScorer>)Collections.emptyList();
    }
    
    public abstract DocIdSetIterator iterator();
    
    public TwoPhaseIterator twoPhaseIterator() {
        return null;
    }
    
    public static class ChildScorer
    {
        public final Scorer child;
        public final String relationship;
        
        public ChildScorer(final Scorer child, final String relationship) {
            this.child = child;
            this.relationship = relationship;
        }
    }
}
