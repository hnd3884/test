package org.apache.commons.math3.random;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class UncorrelatedRandomVectorGenerator implements RandomVectorGenerator
{
    private final NormalizedRandomGenerator generator;
    private final double[] mean;
    private final double[] standardDeviation;
    
    public UncorrelatedRandomVectorGenerator(final double[] mean, final double[] standardDeviation, final NormalizedRandomGenerator generator) {
        if (mean.length != standardDeviation.length) {
            throw new DimensionMismatchException(mean.length, standardDeviation.length);
        }
        this.mean = mean.clone();
        this.standardDeviation = standardDeviation.clone();
        this.generator = generator;
    }
    
    public UncorrelatedRandomVectorGenerator(final int dimension, final NormalizedRandomGenerator generator) {
        this.mean = new double[dimension];
        Arrays.fill(this.standardDeviation = new double[dimension], 1.0);
        this.generator = generator;
    }
    
    public double[] nextVector() {
        final double[] random = new double[this.mean.length];
        for (int i = 0; i < random.length; ++i) {
            random[i] = this.mean[i] + this.standardDeviation[i] * this.generator.nextNormalizedDouble();
        }
        return random;
    }
}
