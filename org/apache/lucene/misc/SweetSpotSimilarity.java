package org.apache.lucene.misc;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class SweetSpotSimilarity extends DefaultSimilarity
{
    private int ln_min;
    private int ln_max;
    private float ln_steep;
    private float tf_base;
    private float tf_min;
    private float tf_hyper_min;
    private float tf_hyper_max;
    private double tf_hyper_base;
    private float tf_hyper_xoffset;
    
    public SweetSpotSimilarity() {
        this.ln_min = 1;
        this.ln_max = 1;
        this.ln_steep = 0.5f;
        this.tf_base = 0.0f;
        this.tf_min = 0.0f;
        this.tf_hyper_min = 0.0f;
        this.tf_hyper_max = 2.0f;
        this.tf_hyper_base = 1.3;
        this.tf_hyper_xoffset = 10.0f;
    }
    
    public void setBaselineTfFactors(final float base, final float min) {
        this.tf_min = min;
        this.tf_base = base;
    }
    
    public void setHyperbolicTfFactors(final float min, final float max, final double base, final float xoffset) {
        this.tf_hyper_min = min;
        this.tf_hyper_max = max;
        this.tf_hyper_base = base;
        this.tf_hyper_xoffset = xoffset;
    }
    
    public void setLengthNormFactors(final int min, final int max, final float steepness, final boolean discountOverlaps) {
        this.ln_min = min;
        this.ln_max = max;
        this.ln_steep = steepness;
        this.discountOverlaps = discountOverlaps;
    }
    
    public float lengthNorm(final FieldInvertState state) {
        int numTokens;
        if (this.discountOverlaps) {
            numTokens = state.getLength() - state.getNumOverlap();
        }
        else {
            numTokens = state.getLength();
        }
        return state.getBoost() * this.computeLengthNorm(numTokens);
    }
    
    public float computeLengthNorm(final int numTerms) {
        final int l = this.ln_min;
        final int h = this.ln_max;
        final float s = this.ln_steep;
        return (float)(1.0 / Math.sqrt(s * (Math.abs(numTerms - l) + Math.abs(numTerms - h) - (h - l)) + 1.0f));
    }
    
    public float tf(final float freq) {
        return this.baselineTf(freq);
    }
    
    public float baselineTf(final float freq) {
        if (0.0f == freq) {
            return 0.0f;
        }
        return (freq <= this.tf_min) ? this.tf_base : ((float)Math.sqrt(freq + this.tf_base * this.tf_base - this.tf_min));
    }
    
    public float hyperbolicTf(final float freq) {
        if (0.0f == freq) {
            return 0.0f;
        }
        final float min = this.tf_hyper_min;
        final float max = this.tf_hyper_max;
        final double base = this.tf_hyper_base;
        final float xoffset = this.tf_hyper_xoffset;
        final double x = freq - xoffset;
        final float result = min + (float)((max - min) / 2.0f * ((Math.pow(base, x) - Math.pow(base, -x)) / (Math.pow(base, x) + Math.pow(base, -x)) + 1.0));
        return Float.isNaN(result) ? max : result;
    }
}
