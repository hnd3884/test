package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class UniformRealDistribution extends AbstractRealDistribution
{
    @Deprecated
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 20120109L;
    private final double lower;
    private final double upper;
    
    public UniformRealDistribution() {
        this(0.0, 1.0);
    }
    
    public UniformRealDistribution(final double lower, final double upper) throws NumberIsTooLargeException {
        this(new Well19937c(), lower, upper);
    }
    
    @Deprecated
    public UniformRealDistribution(final double lower, final double upper, final double inverseCumAccuracy) throws NumberIsTooLargeException {
        this(new Well19937c(), lower, upper);
    }
    
    @Deprecated
    public UniformRealDistribution(final RandomGenerator rng, final double lower, final double upper, final double inverseCumAccuracy) {
        this(rng, lower, upper);
    }
    
    public UniformRealDistribution(final RandomGenerator rng, final double lower, final double upper) throws NumberIsTooLargeException {
        super(rng);
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
        }
        this.lower = lower;
        this.upper = upper;
    }
    
    public double density(final double x) {
        if (x < this.lower || x > this.upper) {
            return 0.0;
        }
        return 1.0 / (this.upper - this.lower);
    }
    
    public double cumulativeProbability(final double x) {
        if (x <= this.lower) {
            return 0.0;
        }
        if (x >= this.upper) {
            return 1.0;
        }
        return (x - this.lower) / (this.upper - this.lower);
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        return p * (this.upper - this.lower) + this.lower;
    }
    
    public double getNumericalMean() {
        return 0.5 * (this.lower + this.upper);
    }
    
    public double getNumericalVariance() {
        final double ul = this.upper - this.lower;
        return ul * ul / 12.0;
    }
    
    public double getSupportLowerBound() {
        return this.lower;
    }
    
    public double getSupportUpperBound() {
        return this.upper;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public double sample() {
        final double u = this.random.nextDouble();
        return u * this.upper + (1.0 - u) * this.lower;
    }
}
