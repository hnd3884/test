package org.apache.lucene.search.similarities;

public class NormalizationZ extends Normalization
{
    final float z;
    
    public NormalizationZ() {
        this(0.3f);
    }
    
    public NormalizationZ(final float z) {
        this.z = z;
    }
    
    @Override
    public float tfn(final BasicStats stats, final float tf, final float len) {
        return (float)(tf * Math.pow(stats.avgFieldLength / len, this.z));
    }
    
    @Override
    public String toString() {
        return "Z(" + this.z + ")";
    }
    
    public float getZ() {
        return this.z;
    }
}
