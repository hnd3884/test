package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class NormalDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 8589540077390120676L;
    private static final double SQRT2;
    private final double mean;
    private final double standardDeviation;
    private final double logStandardDeviationPlusHalfLog2Pi;
    private final double solverAbsoluteAccuracy;
    
    public NormalDistribution() {
        this(0.0, 1.0);
    }
    
    public NormalDistribution(final double mean, final double sd) throws NotStrictlyPositiveException {
        this(mean, sd, 1.0E-9);
    }
    
    public NormalDistribution(final double mean, final double sd, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), mean, sd, inverseCumAccuracy);
    }
    
    public NormalDistribution(final RandomGenerator rng, final double mean, final double sd) throws NotStrictlyPositiveException {
        this(rng, mean, sd, 1.0E-9);
    }
    
    public NormalDistribution(final RandomGenerator rng, final double mean, final double sd, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (sd <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, sd);
        }
        this.mean = mean;
        this.standardDeviation = sd;
        this.logStandardDeviationPlusHalfLog2Pi = FastMath.log(sd) + 0.5 * FastMath.log(6.283185307179586);
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getMean() {
        return this.mean;
    }
    
    public double getStandardDeviation() {
        return this.standardDeviation;
    }
    
    public double density(final double x) {
        return FastMath.exp(this.logDensity(x));
    }
    
    @Override
    public double logDensity(final double x) {
        final double x2 = x - this.mean;
        final double x3 = x2 / this.standardDeviation;
        return -0.5 * x3 * x3 - this.logStandardDeviationPlusHalfLog2Pi;
    }
    
    public double cumulativeProbability(final double x) {
        final double dev = x - this.mean;
        if (FastMath.abs(dev) > 40.0 * this.standardDeviation) {
            return (dev < 0.0) ? 0.0 : 1.0;
        }
        return 0.5 * Erf.erfc(-dev / (this.standardDeviation * NormalDistribution.SQRT2));
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        return this.mean + this.standardDeviation * NormalDistribution.SQRT2 * Erf.erfInv(2.0 * p - 1.0);
    }
    
    @Deprecated
    @Override
    public double cumulativeProbability(final double x0, final double x1) throws NumberIsTooLargeException {
        return this.probability(x0, x1);
    }
    
    @Override
    public double probability(final double x0, final double x1) throws NumberIsTooLargeException {
        if (x0 > x1) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1, true);
        }
        final double denom = this.standardDeviation * NormalDistribution.SQRT2;
        final double v0 = (x0 - this.mean) / denom;
        final double v2 = (x1 - this.mean) / denom;
        return 0.5 * Erf.erf(v0, v2);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        return this.getMean();
    }
    
    public double getNumericalVariance() {
        final double s = this.getStandardDeviation();
        return s * s;
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
    
    @Override
    public double sample() {
        return this.standardDeviation * this.random.nextGaussian() + this.mean;
    }
    
    static {
        SQRT2 = FastMath.sqrt(2.0);
    }
}
