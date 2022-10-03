package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class GammaDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 20120524L;
    private final double shape;
    private final double scale;
    private final double shiftedShape;
    private final double densityPrefactor1;
    private final double logDensityPrefactor1;
    private final double densityPrefactor2;
    private final double logDensityPrefactor2;
    private final double minY;
    private final double maxLogY;
    private final double solverAbsoluteAccuracy;
    
    public GammaDistribution(final double shape, final double scale) throws NotStrictlyPositiveException {
        this(shape, scale, 1.0E-9);
    }
    
    public GammaDistribution(final double shape, final double scale, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), shape, scale, inverseCumAccuracy);
    }
    
    public GammaDistribution(final RandomGenerator rng, final double shape, final double scale) throws NotStrictlyPositiveException {
        this(rng, shape, scale, 1.0E-9);
    }
    
    public GammaDistribution(final RandomGenerator rng, final double shape, final double scale, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (shape <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, shape);
        }
        if (scale <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, scale);
        }
        this.shape = shape;
        this.scale = scale;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
        this.shiftedShape = shape + 4.7421875 + 0.5;
        final double aux = 2.718281828459045 / (6.283185307179586 * this.shiftedShape);
        this.densityPrefactor2 = shape * FastMath.sqrt(aux) / Gamma.lanczos(shape);
        this.logDensityPrefactor2 = FastMath.log(shape) + 0.5 * FastMath.log(aux) - FastMath.log(Gamma.lanczos(shape));
        this.densityPrefactor1 = this.densityPrefactor2 / scale * FastMath.pow(this.shiftedShape, -shape) * FastMath.exp(shape + 4.7421875);
        this.logDensityPrefactor1 = this.logDensityPrefactor2 - FastMath.log(scale) - FastMath.log(this.shiftedShape) * shape + shape + 4.7421875;
        this.minY = shape + 4.7421875 - FastMath.log(Double.MAX_VALUE);
        this.maxLogY = FastMath.log(Double.MAX_VALUE) / (shape - 1.0);
    }
    
    @Deprecated
    public double getAlpha() {
        return this.shape;
    }
    
    public double getShape() {
        return this.shape;
    }
    
    @Deprecated
    public double getBeta() {
        return this.scale;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public double density(final double x) {
        if (x < 0.0) {
            return 0.0;
        }
        final double y = x / this.scale;
        if (y <= this.minY || FastMath.log(y) >= this.maxLogY) {
            final double aux1 = (y - this.shiftedShape) / this.shiftedShape;
            final double aux2 = this.shape * (FastMath.log1p(aux1) - aux1);
            final double aux3 = -y * 5.2421875 / this.shiftedShape + 4.7421875 + aux2;
            return this.densityPrefactor2 / x * FastMath.exp(aux3);
        }
        return this.densityPrefactor1 * FastMath.exp(-y) * FastMath.pow(y, this.shape - 1.0);
    }
    
    @Override
    public double logDensity(final double x) {
        if (x < 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        final double y = x / this.scale;
        if (y <= this.minY || FastMath.log(y) >= this.maxLogY) {
            final double aux1 = (y - this.shiftedShape) / this.shiftedShape;
            final double aux2 = this.shape * (FastMath.log1p(aux1) - aux1);
            final double aux3 = -y * 5.2421875 / this.shiftedShape + 4.7421875 + aux2;
            return this.logDensityPrefactor2 - FastMath.log(x) + aux3;
        }
        return this.logDensityPrefactor1 - y + FastMath.log(y) * (this.shape - 1.0);
    }
    
    public double cumulativeProbability(final double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        }
        else {
            ret = Gamma.regularizedGammaP(this.shape, x / this.scale);
        }
        return ret;
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        return this.shape * this.scale;
    }
    
    public double getNumericalVariance() {
        return this.shape * this.scale * this.scale;
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
        if (this.shape < 1.0) {
            while (true) {
                final double u = this.random.nextDouble();
                final double bGS = 1.0 + this.shape / 2.718281828459045;
                final double p = bGS * u;
                if (p <= 1.0) {
                    final double x = FastMath.pow(p, 1.0 / this.shape);
                    final double u2 = this.random.nextDouble();
                    if (u2 > FastMath.exp(-x)) {
                        continue;
                    }
                    return this.scale * x;
                }
                else {
                    final double x = -1.0 * FastMath.log((bGS - p) / this.shape);
                    final double u2 = this.random.nextDouble();
                    if (u2 > FastMath.pow(x, this.shape - 1.0)) {
                        continue;
                    }
                    return this.scale * x;
                }
            }
        }
        else {
            final double d = this.shape - 0.3333333333333333;
            final double c = 1.0 / (3.0 * FastMath.sqrt(d));
            while (true) {
                final double x2 = this.random.nextGaussian();
                final double v = (1.0 + c * x2) * (1.0 + c * x2) * (1.0 + c * x2);
                if (v <= 0.0) {
                    continue;
                }
                final double x3 = x2 * x2;
                final double u3 = this.random.nextDouble();
                if (u3 < 1.0 - 0.0331 * x3 * x3) {
                    return this.scale * d * v;
                }
                if (FastMath.log(u3) < 0.5 * x3 + d * (1.0 - v + FastMath.log(v))) {
                    return this.scale * d * v;
                }
            }
        }
    }
}
