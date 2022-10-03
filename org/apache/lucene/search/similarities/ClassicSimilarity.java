package org.apache.lucene.search.similarities;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.util.SmallFloat;

public class ClassicSimilarity extends TFIDFSimilarity
{
    private static final float[] NORM_TABLE;
    protected boolean discountOverlaps;
    
    public ClassicSimilarity() {
        this.discountOverlaps = true;
    }
    
    @Override
    public float coord(final int overlap, final int maxOverlap) {
        return overlap / (float)maxOverlap;
    }
    
    @Override
    public float queryNorm(final float sumOfSquaredWeights) {
        return (float)(1.0 / Math.sqrt(sumOfSquaredWeights));
    }
    
    @Override
    public final long encodeNormValue(final float f) {
        return SmallFloat.floatToByte315(f);
    }
    
    @Override
    public final float decodeNormValue(final long norm) {
        return ClassicSimilarity.NORM_TABLE[(int)(norm & 0xFFL)];
    }
    
    @Override
    public float lengthNorm(final FieldInvertState state) {
        int numTerms;
        if (this.discountOverlaps) {
            numTerms = state.getLength() - state.getNumOverlap();
        }
        else {
            numTerms = state.getLength();
        }
        return state.getBoost() * (float)(1.0 / Math.sqrt(numTerms));
    }
    
    @Override
    public float tf(final float freq) {
        return (float)Math.sqrt(freq);
    }
    
    @Override
    public float sloppyFreq(final int distance) {
        return 1.0f / (distance + 1);
    }
    
    @Override
    public float scorePayload(final int doc, final int start, final int end, final BytesRef payload) {
        return 1.0f;
    }
    
    @Override
    public float idf(final long docFreq, final long numDocs) {
        return (float)(Math.log(numDocs / (double)(docFreq + 1L)) + 1.0);
    }
    
    public void setDiscountOverlaps(final boolean v) {
        this.discountOverlaps = v;
    }
    
    public boolean getDiscountOverlaps() {
        return this.discountOverlaps;
    }
    
    @Override
    public String toString() {
        return "DefaultSimilarity";
    }
    
    static {
        NORM_TABLE = new float[256];
        for (int i = 0; i < 256; ++i) {
            ClassicSimilarity.NORM_TABLE[i] = SmallFloat.byte315ToFloat((byte)i);
        }
    }
}
