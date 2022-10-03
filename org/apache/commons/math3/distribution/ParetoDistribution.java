package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class ParetoDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 20130424L;
    private final double scale;
    private final double shape;
    private final double solverAbsoluteAccuracy;
    
    public ParetoDistribution() {
        this(1.0, 1.0);
    }
    
    public ParetoDistribution(final double scale, final double shape) throws NotStrictlyPositiveException {
        this(scale, shape, 1.0E-9);
    }
    
    public ParetoDistribution(final double scale, final double shape, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), scale, shape, inverseCumAccuracy);
    }
    
    public ParetoDistribution(final RandomGenerator rng, final double scale, final double shape) throws NotStrictlyPositiveException {
        this(rng, scale, shape, 1.0E-9);
    }
    
    public ParetoDistribution(final RandomGenerator rng, final double scale, final double shape, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (scale <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, scale);
        }
        if (shape <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
        }
        this.scale = scale;
        this.shape = shape;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public double getShape() {
        return this.shape;
    }
    
    public double density(final double x) {
        if (x < this.scale) {
            return 0.0;
        }
        return FastMath.pow(this.scale, this.shape) / FastMath.pow(x, this.shape + 1.0) * this.shape;
    }
    
    @Override
    public double logDensity(final double x) {
        if (x < this.scale) {
            return Double.NEGATIVE_INFINITY;
        }
        return FastMath.log(this.scale) * this.shape - FastMath.log(x) * (this.shape + 1.0) + FastMath.log(this.shape);
    }
    
    public double cumulativeProbability(final double x) {
        if (x <= this.scale) {
            return 0.0;
        }
        return 1.0 - FastMath.pow(this.scale / x, this.shape);
    }
    
    @Deprecated
    @Override
    public double cumulativeProbability(final double x0, final double x1) throws NumberIsTooLargeException {
        return this.probability(x0, x1);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        if (this.shape <= 1.0) {
            return Double.POSITIVE_INFINITY;
        }
        return this.shape * this.scale / (this.shape - 1.0);
    }
    
    public double getNumericalVariance() {
        if (this.shape <= 2.0) {
            return Double.POSITIVE_INFINITY;
        }
        final double s = this.shape - 1.0;
        return this.scale * this.scale * this.shape / (s * s) / (this.shape - 2.0);
    }
    
    public double getSupportLowerBound() {
        return this.scale;
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
        final double n = this.random.nextDouble();
        return this.scale / FastMath.pow(n, 1.0 / this.shape);
    }
}
