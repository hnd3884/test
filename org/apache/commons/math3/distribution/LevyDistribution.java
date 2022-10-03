package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class LevyDistribution extends AbstractRealDistribution
{
    private static final long serialVersionUID = 20130314L;
    private final double mu;
    private final double c;
    private final double halfC;
    
    public LevyDistribution(final double mu, final double c) {
        this(new Well19937c(), mu, c);
    }
    
    public LevyDistribution(final RandomGenerator rng, final double mu, final double c) {
        super(rng);
        this.mu = mu;
        this.c = c;
        this.halfC = 0.5 * c;
    }
    
    public double density(final double x) {
        if (x < this.mu) {
            return Double.NaN;
        }
        final double delta = x - this.mu;
        final double f = this.halfC / delta;
        return FastMath.sqrt(f / 3.141592653589793) * FastMath.exp(-f) / delta;
    }
    
    @Override
    public double logDensity(final double x) {
        if (x < this.mu) {
            return Double.NaN;
        }
        final double delta = x - this.mu;
        final double f = this.halfC / delta;
        return 0.5 * FastMath.log(f / 3.141592653589793) - f - FastMath.log(delta);
    }
    
    public double cumulativeProbability(final double x) {
        if (x < this.mu) {
            return Double.NaN;
        }
        return Erf.erfc(FastMath.sqrt(this.halfC / (x - this.mu)));
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        final double t = Erf.erfcInv(p);
        return this.mu + this.halfC / (t * t);
    }
    
    public double getScale() {
        return this.c;
    }
    
    public double getLocation() {
        return this.mu;
    }
    
    public double getNumericalMean() {
        return Double.POSITIVE_INFINITY;
    }
    
    public double getNumericalVariance() {
        return Double.POSITIVE_INFINITY;
    }
    
    public double getSupportLowerBound() {
        return this.mu;
    }
    
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return false;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
