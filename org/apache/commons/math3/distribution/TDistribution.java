package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class TDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = -5852615386664158222L;
    private final double degreesOfFreedom;
    private final double solverAbsoluteAccuracy;
    private final double factor;
    
    public TDistribution(final double degreesOfFreedom) throws NotStrictlyPositiveException {
        this(degreesOfFreedom, 1.0E-9);
    }
    
    public TDistribution(final double degreesOfFreedom, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), degreesOfFreedom, inverseCumAccuracy);
    }
    
    public TDistribution(final RandomGenerator rng, final double degreesOfFreedom) throws NotStrictlyPositiveException {
        this(rng, degreesOfFreedom, 1.0E-9);
    }
    
    public TDistribution(final RandomGenerator rng, final double degreesOfFreedom, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (degreesOfFreedom <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, degreesOfFreedom);
        }
        this.degreesOfFreedom = degreesOfFreedom;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
        final double n = degreesOfFreedom;
        final double nPlus1Over2 = (n + 1.0) / 2.0;
        this.factor = Gamma.logGamma(nPlus1Over2) - 0.5 * (FastMath.log(3.141592653589793) + FastMath.log(n)) - Gamma.logGamma(n / 2.0);
    }
    
    public double getDegreesOfFreedom() {
        return this.degreesOfFreedom;
    }
    
    public double density(final double x) {
        return FastMath.exp(this.logDensity(x));
    }
    
    @Override
    public double logDensity(final double x) {
        final double n = this.degreesOfFreedom;
        final double nPlus1Over2 = (n + 1.0) / 2.0;
        return this.factor - nPlus1Over2 * FastMath.log(1.0 + x * x / n);
    }
    
    public double cumulativeProbability(final double x) {
        double ret;
        if (x == 0.0) {
            ret = 0.5;
        }
        else {
            final double t = Beta.regularizedBeta(this.degreesOfFreedom / (this.degreesOfFreedom + x * x), 0.5 * this.degreesOfFreedom, 0.5);
            if (x < 0.0) {
                ret = 0.5 * t;
            }
            else {
                ret = 1.0 - 0.5 * t;
            }
        }
        return ret;
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        final double df = this.getDegreesOfFreedom();
        if (df > 1.0) {
            return 0.0;
        }
        return Double.NaN;
    }
    
    public double getNumericalVariance() {
        final double df = this.getDegreesOfFreedom();
        if (df > 2.0) {
            return df / (df - 2.0);
        }
        if (df > 1.0 && df <= 2.0) {
            return Double.POSITIVE_INFINITY;
        }
        return Double.NaN;
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
