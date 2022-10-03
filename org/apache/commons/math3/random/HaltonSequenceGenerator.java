package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.OutOfRangeException;

public class HaltonSequenceGenerator implements RandomVectorGenerator
{
    private static final int[] PRIMES;
    private static final int[] WEIGHTS;
    private final int dimension;
    private int count;
    private final int[] base;
    private final int[] weight;
    
    public HaltonSequenceGenerator(final int dimension) throws OutOfRangeException {
        this(dimension, HaltonSequenceGenerator.PRIMES, HaltonSequenceGenerator.WEIGHTS);
    }
    
    public HaltonSequenceGenerator(final int dimension, final int[] bases, final int[] weights) throws NullArgumentException, OutOfRangeException, DimensionMismatchException {
        this.count = 0;
        MathUtils.checkNotNull(bases);
        if (dimension < 1 || dimension > bases.length) {
            throw new OutOfRangeException(dimension, 1, HaltonSequenceGenerator.PRIMES.length);
        }
        if (weights != null && weights.length != bases.length) {
            throw new DimensionMismatchException(weights.length, bases.length);
        }
        this.dimension = dimension;
        this.base = bases.clone();
        this.weight = (int[])((weights == null) ? null : ((int[])weights.clone()));
        this.count = 0;
    }
    
    public double[] nextVector() {
        final double[] v = new double[this.dimension];
        for (int i = 0; i < this.dimension; ++i) {
            int index = this.count;
            double f = 1.0 / this.base[i];
            final int j = 0;
            while (index > 0) {
                final int digit = this.scramble(i, j, this.base[i], index % this.base[i]);
                final double[] array = v;
                final int n = i;
                array[n] += f * digit;
                index /= this.base[i];
                f /= this.base[i];
            }
        }
        ++this.count;
        return v;
    }
    
    protected int scramble(final int i, final int j, final int b, final int digit) {
        return (this.weight != null) ? (this.weight[i] * digit % b) : digit;
    }
    
    public double[] skipTo(final int index) throws NotPositiveException {
        this.count = index;
        return this.nextVector();
    }
    
    public int getNextIndex() {
        return this.count;
    }
    
    static {
        PRIMES = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173 };
        WEIGHTS = new int[] { 1, 2, 3, 3, 8, 11, 12, 14, 7, 18, 12, 13, 17, 18, 29, 14, 18, 43, 41, 44, 40, 30, 47, 65, 71, 28, 40, 60, 79, 89, 56, 50, 52, 61, 108, 56, 66, 63, 60, 66 };
    }
}
