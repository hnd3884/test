package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomDataImpl;
import java.io.Serializable;

public abstract class AbstractRealDistribution implements RealDistribution, Serializable
{
    public static final double SOLVER_DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    private static final long serialVersionUID = -38038050983108802L;
    @Deprecated
    protected RandomDataImpl randomData;
    protected final RandomGenerator random;
    private double solverAbsoluteAccuracy;
    
    @Deprecated
    protected AbstractRealDistribution() {
        this.randomData = new RandomDataImpl();
        this.solverAbsoluteAccuracy = 1.0E-6;
        this.random = null;
    }
    
    protected AbstractRealDistribution(final RandomGenerator rng) {
        this.randomData = new RandomDataImpl();
        this.solverAbsoluteAccuracy = 1.0E-6;
        this.random = rng;
    }
    
    @Deprecated
    public double cumulativeProbability(final double x0, final double x1) throws NumberIsTooLargeException {
        return this.probability(x0, x1);
    }
    
    public double probability(final double x0, final double x1) {
        if (x0 > x1) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1, true);
        }
        return this.cumulativeProbability(x1) - this.cumulativeProbability(x0);
    }
    
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        double lowerBound = this.getSupportLowerBound();
        if (p == 0.0) {
            return lowerBound;
        }
        double upperBound = this.getSupportUpperBound();
        if (p == 1.0) {
            return upperBound;
        }
        final double mu = this.getNumericalMean();
        final double sig = FastMath.sqrt(this.getNumericalVariance());
        final boolean chebyshevApplies = !Double.isInfinite(mu) && !Double.isNaN(mu) && !Double.isInfinite(sig) && !Double.isNaN(sig);
        if (lowerBound == Double.NEGATIVE_INFINITY) {
            if (chebyshevApplies) {
                lowerBound = mu - sig * FastMath.sqrt((1.0 - p) / p);
            }
            else {
                for (lowerBound = -1.0; this.cumulativeProbability(lowerBound) >= p; lowerBound *= 2.0) {}
            }
        }
        if (upperBound == Double.POSITIVE_INFINITY) {
            if (chebyshevApplies) {
                upperBound = mu + sig * FastMath.sqrt(p / (1.0 - p));
            }
            else {
                for (upperBound = 1.0; this.cumulativeProbability(upperBound) < p; upperBound *= 2.0) {}
            }
        }
        final UnivariateFunction toSolve = new UnivariateFunction() {
            public double value(final double x) {
                return AbstractRealDistribution.this.cumulativeProbability(x) - p;
            }
        };
        final double x = UnivariateSolverUtils.solve(toSolve, lowerBound, upperBound, this.getSolverAbsoluteAccuracy());
        if (!this.isSupportConnected()) {
            final double dx = this.getSolverAbsoluteAccuracy();
            if (x - dx >= this.getSupportLowerBound()) {
                final double px = this.cumulativeProbability(x);
                if (this.cumulativeProbability(x - dx) == px) {
                    upperBound = x;
                    while (upperBound - lowerBound > dx) {
                        final double midPoint = 0.5 * (lowerBound + upperBound);
                        if (this.cumulativeProbability(midPoint) < px) {
                            lowerBound = midPoint;
                        }
                        else {
                            upperBound = midPoint;
                        }
                    }
                    return upperBound;
                }
            }
        }
        return x;
    }
    
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public void reseedRandomGenerator(final long seed) {
        this.random.setSeed(seed);
        this.randomData.reSeed(seed);
    }
    
    public double sample() {
        return this.inverseCumulativeProbability(this.random.nextDouble());
    }
    
    public double[] sample(final int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
        }
        final double[] out = new double[sampleSize];
        for (int i = 0; i < sampleSize; ++i) {
            out[i] = this.sample();
        }
        return out;
    }
    
    public double probability(final double x) {
        return 0.0;
    }
    
    public double logDensity(final double x) {
        return FastMath.log(this.density(x));
    }
}
