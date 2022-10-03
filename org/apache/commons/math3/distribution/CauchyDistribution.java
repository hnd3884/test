package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class CauchyDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 8589540077390120676L;
    private final double median;
    private final double scale;
    private final double solverAbsoluteAccuracy;
    
    public CauchyDistribution() {
        this(0.0, 1.0);
    }
    
    public CauchyDistribution(final double median, final double scale) {
        this(median, scale, 1.0E-9);
    }
    
    public CauchyDistribution(final double median, final double scale, final double inverseCumAccuracy) {
        this(new Well19937c(), median, scale, inverseCumAccuracy);
    }
    
    public CauchyDistribution(final RandomGenerator rng, final double median, final double scale) {
        this(rng, median, scale, 1.0E-9);
    }
    
    public CauchyDistribution(final RandomGenerator rng, final double median, final double scale, final double inverseCumAccuracy) {
        super(rng);
        if (scale <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, scale);
        }
        this.scale = scale;
        this.median = median;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double cumulativeProbability(final double x) {
        return 0.5 + FastMath.atan((x - this.median) / this.scale) / 3.141592653589793;
    }
    
    public double getMedian() {
        return this.median;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public double density(final double x) {
        final double dev = x - this.median;
        return 0.3183098861837907 * (this.scale / (dev * dev + this.scale * this.scale));
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        double ret;
        if (p == 0.0) {
            ret = Double.NEGATIVE_INFINITY;
        }
        else if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        }
        else {
            ret = this.median + this.scale * FastMath.tan(3.141592653589793 * (p - 0.5));
        }
        return ret;
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        return Double.NaN;
    }
    
    public double getNumericalVariance() {
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
