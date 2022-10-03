package org.apache.lucene.search.similarities;

public class NormalizationH2 extends Normalization
{
    private final float c;
    
    public NormalizationH2(final float c) {
        this.c = c;
    }
    
    public NormalizationH2() {
        this(1.0f);
    }
    
    @Override
    public final float tfn(final BasicStats stats, final float tf, final float len) {
        return (float)(tf * SimilarityBase.log2(1.0f + this.c * stats.getAvgFieldLength() / len));
    }
    
    @Override
    public String toString() {
        return "2";
    }
    
    public float getC() {
        return this.c;
    }
}
