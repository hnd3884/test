package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class GeometricDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = 20130507L;
    private final double probabilityOfSuccess;
    private final double logProbabilityOfSuccess;
    private final double log1mProbabilityOfSuccess;
    
    public GeometricDistribution(final double p) {
        this(new Well19937c(), p);
    }
    
    public GeometricDistribution(final RandomGenerator rng, final double p) {
        super(rng);
        if (p <= 0.0 || p > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT, p, 0, 1);
        }
        this.probabilityOfSuccess = p;
        this.logProbabilityOfSuccess = FastMath.log(p);
        this.log1mProbabilityOfSuccess = FastMath.log1p(-p);
    }
    
    public double getProbabilityOfSuccess() {
        return this.probabilityOfSuccess;
    }
    
    public double probability(final int x) {
        if (x < 0) {
            return 0.0;
        }
        return FastMath.exp(this.log1mProbabilityOfSuccess * x) * this.probabilityOfSuccess;
    }
    
    @Override
    public double logProbability(final int x) {
        if (x < 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return x * this.log1mProbabilityOfSuccess + this.logProbabilityOfSuccess;
    }
    
    public double cumulativeProbability(final int x) {
        if (x < 0) {
            return 0.0;
        }
        return -FastMath.expm1(this.log1mProbabilityOfSuccess * (x + 1));
    }
    
    public double getNumericalMean() {
        return (1.0 - this.probabilityOfSuccess) / this.probabilityOfSuccess;
    }
    
    public double getNumericalVariance() {
        return (1.0 - this.probabilityOfSuccess) / (this.probabilityOfSuccess * this.probabilityOfSuccess);
    }
    
    public int getSupportLowerBound() {
        return 0;
    }
    
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public int inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        if (p == 1.0) {
            return Integer.MAX_VALUE;
        }
        if (p == 0.0) {
            return 0;
        }
        return Math.max(0, (int)Math.ceil(FastMath.log1p(-p) / this.log1mProbabilityOfSuccess - 1.0));
    }
}
