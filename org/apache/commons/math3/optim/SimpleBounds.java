package org.apache.commons.math3.optim;

import java.util.Arrays;

public class SimpleBounds implements OptimizationData
{
    private final double[] lower;
    private final double[] upper;
    
    public SimpleBounds(final double[] lB, final double[] uB) {
        this.lower = lB.clone();
        this.upper = uB.clone();
    }
    
    public double[] getLower() {
        return this.lower.clone();
    }
    
    public double[] getUpper() {
        return this.upper.clone();
    }
    
    public static SimpleBounds unbounded(final int dim) {
        final double[] lB = new double[dim];
        Arrays.fill(lB, Double.NEGATIVE_INFINITY);
        final double[] uB = new double[dim];
        Arrays.fill(uB, Double.POSITIVE_INFINITY);
        return new SimpleBounds(lB, uB);
    }
}
