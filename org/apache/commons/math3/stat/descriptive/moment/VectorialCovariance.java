package org.apache.commons.math3.stat.descriptive.moment;

import java.util.Arrays;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;

public class VectorialCovariance implements Serializable
{
    private static final long serialVersionUID = 4118372414238930270L;
    private final double[] sums;
    private final double[] productsSums;
    private final boolean isBiasCorrected;
    private long n;
    
    public VectorialCovariance(final int dimension, final boolean isBiasCorrected) {
        this.sums = new double[dimension];
        this.productsSums = new double[dimension * (dimension + 1) / 2];
        this.n = 0L;
        this.isBiasCorrected = isBiasCorrected;
    }
    
    public void increment(final double[] v) throws DimensionMismatchException {
        if (v.length != this.sums.length) {
            throw new DimensionMismatchException(v.length, this.sums.length);
        }
        int k = 0;
        for (int i = 0; i < v.length; ++i) {
            final double[] sums = this.sums;
            final int n = i;
            sums[n] += v[i];
            for (int j = 0; j <= i; ++j) {
                final double[] productsSums = this.productsSums;
                final int n2 = k++;
                productsSums[n2] += v[i] * v[j];
            }
        }
        ++this.n;
    }
    
    public RealMatrix getResult() {
        final int dimension = this.sums.length;
        final RealMatrix result = MatrixUtils.createRealMatrix(dimension, dimension);
        if (this.n > 1L) {
            final double c = 1.0 / (this.n * (this.isBiasCorrected ? (this.n - 1L) : this.n));
            int k = 0;
            for (int i = 0; i < dimension; ++i) {
                for (int j = 0; j <= i; ++j) {
                    final double e = c * (this.n * this.productsSums[k++] - this.sums[i] * this.sums[j]);
                    result.setEntry(i, j, e);
                    result.setEntry(j, i, e);
                }
            }
        }
        return result;
    }
    
    public long getN() {
        return this.n;
    }
    
    public void clear() {
        this.n = 0L;
        Arrays.fill(this.sums, 0.0);
        Arrays.fill(this.productsSums, 0.0);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (this.isBiasCorrected ? 1231 : 1237);
        result = 31 * result + (int)(this.n ^ this.n >>> 32);
        result = 31 * result + Arrays.hashCode(this.productsSums);
        result = 31 * result + Arrays.hashCode(this.sums);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VectorialCovariance)) {
            return false;
        }
        final VectorialCovariance other = (VectorialCovariance)obj;
        return this.isBiasCorrected == other.isBiasCorrected && this.n == other.n && Arrays.equals(this.productsSums, other.productsSums) && Arrays.equals(this.sums, other.sums);
    }
}
