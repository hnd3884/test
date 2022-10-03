package org.apache.lucene.search.similarities;

public class NormalizationH3 extends Normalization
{
    private final float mu;
    
    public NormalizationH3() {
        this(800.0f);
    }
    
    public NormalizationH3(final float mu) {
        this.mu = mu;
    }
    
    @Override
    public float tfn(final BasicStats stats, final float tf, final float len) {
        return (tf + this.mu * ((stats.getTotalTermFreq() + 1.0f) / (stats.getNumberOfFieldTokens() + 1.0f))) / (len + this.mu) * this.mu;
    }
    
    @Override
    public String toString() {
        return "3(" + this.mu + ")";
    }
    
    public float getMu() {
        return this.mu;
    }
}
