package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class LogNormalDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 20120112L;
    private static final double SQRT2PI;
    private static final double SQRT2;
    private final double scale;
    private final double shape;
    private final double logShapePlusHalfLog2Pi;
    private final double solverAbsoluteAccuracy;
    
    public LogNormalDistribution() {
        this(0.0, 1.0);
    }
    
    public LogNormalDistribution(final double scale, final double shape) throws NotStrictlyPositiveException {
        this(scale, shape, 1.0E-9);
    }
    
    public LogNormalDistribution(final double scale, final double shape, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), scale, shape, inverseCumAccuracy);
    }
    
    public LogNormalDistribution(final RandomGenerator rng, final double scale, final double shape) throws NotStrictlyPositiveException {
        this(rng, scale, shape, 1.0E-9);
    }
    
    public LogNormalDistribution(final RandomGenerator rng, final double scale, final double shape, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (shape <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
        }
        this.scale = scale;
        this.shape = shape;
        this.logShapePlusHalfLog2Pi = FastMath.log(shape) + 0.5 * FastMath.log(6.283185307179586);
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public double getShape() {
        return this.shape;
    }
    
    public double density(final double x) {
        if (x <= 0.0) {
            return 0.0;
        }
        final double x2 = FastMath.log(x) - this.scale;
        final double x3 = x2 / this.shape;
        return FastMath.exp(-0.5 * x3 * x3) / (this.shape * LogNormalDistribution.SQRT2PI * x);
    }
    
    @Override
    public double logDensity(final double x) {
        if (x <= 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        final double logX = FastMath.log(x);
        final double x2 = logX - this.scale;
        final double x3 = x2 / this.shape;
        return -0.5 * x3 * x3 - (this.logShapePlusHalfLog2Pi + logX);
    }
    
    public double cumulativeProbability(final double x) {
        if (x <= 0.0) {
            return 0.0;
        }
        final double dev = FastMath.log(x) - this.scale;
        if (FastMath.abs(dev) > 40.0 * this.shape) {
            return (dev < 0.0) ? 0.0 : 1.0;
        }
        return 0.5 + 0.5 * Erf.erf(dev / (this.shape * LogNormalDistribution.SQRT2));
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
        if (x0 <= 0.0 || x1 <= 0.0) {
            return super.probability(x0, x1);
        }
        final double denom = this.shape * LogNormalDistribution.SQRT2;
        final double v0 = (FastMath.log(x0) - this.scale) / denom;
        final double v2 = (FastMath.log(x1) - this.scale) / denom;
        return 0.5 * Erf.erf(v0, v2);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        final double s = this.shape;
        return FastMath.exp(this.scale + s * s / 2.0);
    }
    
    public double getNumericalVariance() {
        final double s = this.shape;
        final double ss = s * s;
        return FastMath.expm1(ss) * FastMath.exp(2.0 * this.scale + ss);
    }
    
    public double getSupportLowerBound() {
        return 0.0;
    }
    
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public double sample() {
        final double n = this.random.nextGaussian();
        return FastMath.exp(this.scale + this.shape * n);
    }
    
    static {
        SQRT2PI = FastMath.sqrt(6.283185307179586);
        SQRT2 = FastMath.sqrt(2.0);
    }
}
