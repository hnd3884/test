package org.apache.lucene.search.similarities;

public class IndependenceStandardized extends Independence
{
    @Override
    public float score(final float freq, final float expected) {
        return (freq - expected) / (float)Math.sqrt(expected);
    }
    
    @Override
    public String toString() {
        return "Standardized";
    }
}
