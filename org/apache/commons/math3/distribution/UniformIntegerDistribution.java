package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class UniformIntegerDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = 20120109L;
    private final int lower;
    private final int upper;
    
    public UniformIntegerDistribution(final int lower, final int upper) throws NumberIsTooLargeException {
        this(new Well19937c(), lower, upper);
    }
    
    public UniformIntegerDistribution(final RandomGenerator rng, final int lower, final int upper) throws NumberIsTooLargeException {
        super(rng);
        if (lower > upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, true);
        }
        this.lower = lower;
        this.upper = upper;
    }
    
    public double probability(final int x) {
        if (x < this.lower || x > this.upper) {
            return 0.0;
        }
        return 1.0 / (this.upper - this.lower + 1);
    }
    
    public double cumulativeProbability(final int x) {
        if (x < this.lower) {
            return 0.0;
        }
        if (x > this.upper) {
            return 1.0;
        }
        return (x - this.lower + 1.0) / (this.upper - this.lower + 1.0);
    }
    
    public double getNumericalMean() {
        return 0.5 * (this.lower + this.upper);
    }
    
    public double getNumericalVariance() {
        final double n = this.upper - this.lower + 1;
        return (n * n - 1.0) / 12.0;
    }
    
    public int getSupportLowerBound() {
        return this.lower;
    }
    
    public int getSupportUpperBound() {
        return this.upper;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public int sample() {
        final int max = this.upper - this.lower + 1;
        if (max <= 0) {
            int r;
            do {
                r = this.random.nextInt();
            } while (r < this.lower || r > this.upper);
            return r;
        }
        return this.lower + this.random.nextInt(max);
    }
}
