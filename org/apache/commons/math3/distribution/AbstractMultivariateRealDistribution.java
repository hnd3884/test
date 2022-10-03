package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

public abstract class AbstractMultivariateRealDistribution implements MultivariateRealDistribution
{
    protected final RandomGenerator random;
    private final int dimension;
    
    protected AbstractMultivariateRealDistribution(final RandomGenerator rng, final int n) {
        this.random = rng;
        this.dimension = n;
    }
    
    public void reseedRandomGenerator(final long seed) {
        this.random.setSeed(seed);
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public abstract double[] sample();
    
    public double[][] sample(final int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
        }
        final double[][] out = new double[sampleSize][this.dimension];
        for (int i = 0; i < sampleSize; ++i) {
            out[i] = this.sample();
        }
        return out;
    }
}
