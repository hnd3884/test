package org.apache.lucene.search.similarities;

public class IndependenceChiSquared extends Independence
{
    @Override
    public float score(final float freq, final float expected) {
        return (freq - expected) * (freq - expected) / expected;
    }
    
    @Override
    public String toString() {
        return "ChiSquared";
    }
}
