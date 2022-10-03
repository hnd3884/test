package org.apache.lucene.search.similarities;

public class NormalizationH1 extends Normalization
{
    private final float c;
    
    public NormalizationH1(final float c) {
        this.c = c;
    }
    
    public NormalizationH1() {
        this(1.0f);
    }
    
    @Override
    public final float tfn(final BasicStats stats, final float tf, final float len) {
        return tf * this.c * stats.getAvgFieldLength() / len;
    }
    
    @Override
    public String toString() {
        return "1";
    }
    
    public float getC() {
        return this.c;
    }
}
