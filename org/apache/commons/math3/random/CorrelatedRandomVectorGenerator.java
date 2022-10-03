package org.apache.commons.math3.random;

import org.apache.commons.math3.linear.RectangularCholeskyDecomposition;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;

public class CorrelatedRandomVectorGenerator implements RandomVectorGenerator
{
    private final double[] mean;
    private final NormalizedRandomGenerator generator;
    private final double[] normalized;
    private final RealMatrix root;
    
    public CorrelatedRandomVectorGenerator(final double[] mean, final RealMatrix covariance, final double small, final NormalizedRandomGenerator generator) {
        final int order = covariance.getRowDimension();
        if (mean.length != order) {
            throw new DimensionMismatchException(mean.length, order);
        }
        this.mean = mean.clone();
        final RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
        this.root = decomposition.getRootMatrix();
        this.generator = generator;
        this.normalized = new double[decomposition.getRank()];
    }
    
    public CorrelatedRandomVectorGenerator(final RealMatrix covariance, final double small, final NormalizedRandomGenerator generator) {
        final int order = covariance.getRowDimension();
        this.mean = new double[order];
        for (int i = 0; i < order; ++i) {
            this.mean[i] = 0.0;
        }
        final RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
        this.root = decomposition.getRootMatrix();
        this.generator = generator;
        this.normalized = new double[decomposition.getRank()];
    }
    
    public NormalizedRandomGenerator getGenerator() {
        return this.generator;
    }
    
    public int getRank() {
        return this.normalized.length;
    }
    
    public RealMatrix getRootMatrix() {
        return this.root;
    }
    
    public double[] nextVector() {
        for (int i = 0; i < this.normalized.length; ++i) {
            this.normalized[i] = this.generator.nextNormalizedDouble();
        }
        final double[] correlated = new double[this.mean.length];
        for (int j = 0; j < correlated.length; ++j) {
            correlated[j] = this.mean[j];
            for (int k = 0; k < this.root.getColumnDimension(); ++k) {
                final double[] array = correlated;
                final int n = j;
                array[n] += this.root.getEntry(j, k) * this.normalized[k];
            }
        }
        return correlated;
    }
}
