package org.apache.lucene.search.similarities;

public class IndependenceSaturated extends Independence
{
    @Override
    public float score(final float freq, final float expected) {
        return (freq - expected) / expected;
    }
    
    @Override
    public String toString() {
        return "Saturated";
    }
}
