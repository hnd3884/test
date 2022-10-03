package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class GumbelDistribution extends AbstractRealDistribution
{
    private static final long serialVersionUID = 20141003L;
    private static final double EULER = 0.5778636748954609;
    private final double mu;
    private final double beta;
    
    public GumbelDistribution(final double mu, final double beta) {
        this(new Well19937c(), mu, beta);
    }
    
    public GumbelDistribution(final RandomGenerator rng, final double mu, final double beta) {
        super(rng);
        if (beta <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, beta);
        }
        this.beta = beta;
        this.mu = mu;
    }
    
    public double getLocation() {
        return this.mu;
    }
    
    public double getScale() {
        return this.beta;
    }
    
    public double density(final double x) {
        final double z = (x - this.mu) / this.beta;
        final double t = FastMath.exp(-z);
        return FastMath.exp(-z - t) / this.beta;
    }
    
    public double cumulativeProbability(final double x) {
        final double z = (x - this.mu) / this.beta;
        return FastMath.exp(-FastMath.exp(-z));
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0.0, 1.0);
        }
        if (p == 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        if (p == 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        return this.mu - FastMath.log(-FastMath.log(p)) * this.beta;
    }
    
    public double getNumericalMean() {
        return this.mu + 0.5778636748954609 * this.beta;
    }
    
    public double getNumericalVariance() {
        return 1.6449340668482264 * (this.beta * this.beta);
    }
    
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
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
