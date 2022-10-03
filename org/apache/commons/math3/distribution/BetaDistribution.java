package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class BetaDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = -1221965979403477668L;
    private final double alpha;
    private final double beta;
    private double z;
    private final double solverAbsoluteAccuracy;
    
    public BetaDistribution(final double alpha, final double beta) {
        this(alpha, beta, 1.0E-9);
    }
    
    public BetaDistribution(final double alpha, final double beta, final double inverseCumAccuracy) {
        this(new Well19937c(), alpha, beta, inverseCumAccuracy);
    }
    
    public BetaDistribution(final RandomGenerator rng, final double alpha, final double beta) {
        this(rng, alpha, beta, 1.0E-9);
    }
    
    public BetaDistribution(final RandomGenerator rng, final double alpha, final double beta, final double inverseCumAccuracy) {
        super(rng);
        this.alpha = alpha;
        this.beta = beta;
        this.z = Double.NaN;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getAlpha() {
        return this.alpha;
    }
    
    public double getBeta() {
        return this.beta;
    }
    
    private void recomputeZ() {
        if (Double.isNaN(this.z)) {
            this.z = Gamma.logGamma(this.alpha) + Gamma.logGamma(this.beta) - Gamma.logGamma(this.alpha + this.beta);
        }
    }
    
    public double density(final double x) {
        final double logDensity = this.logDensity(x);
        return (logDensity == Double.NEGATIVE_INFINITY) ? 0.0 : FastMath.exp(logDensity);
    }
    
    @Override
    public double logDensity(final double x) {
        this.recomputeZ();
        if (x < 0.0 || x > 1.0) {
            return Double.NEGATIVE_INFINITY;
        }
        if (x == 0.0) {
            if (this.alpha < 1.0) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, this.alpha, 1, false);
            }
            return Double.NEGATIVE_INFINITY;
        }
        else {
            if (x != 1.0) {
                final double logX = FastMath.log(x);
                final double log1mX = FastMath.log1p(-x);
                return (this.alpha - 1.0) * logX + (this.beta - 1.0) * log1mX - this.z;
            }
            if (this.beta < 1.0) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, this.beta, 1, false);
            }
            return Double.NEGATIVE_INFINITY;
        }
    }
    
    public double cumulativeProbability(final double x) {
        if (x <= 0.0) {
            return 0.0;
        }
        if (x >= 1.0) {
            return 1.0;
        }
        return Beta.regularizedBeta(x, this.alpha, this.beta);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        final double a = this.getAlpha();
        return a / (a + this.getBeta());
    }
    
    public double getNumericalVariance() {
        final double a = this.getAlpha();
        final double b = this.getBeta();
        final double alphabetasum = a + b;
        return a * b / (alphabetasum * alphabetasum * (alphabetasum + 1.0));
    }
    
    public double getSupportLowerBound() {
        return 0.0;
    }
    
    public double getSupportUpperBound() {
        return 1.0;
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
        return ChengBetaSampler.sample(this.random, this.alpha, this.beta);
    }
    
    private static final class ChengBetaSampler
    {
        static double sample(final RandomGenerator random, final double alpha, final double beta) {
            final double a = FastMath.min(alpha, beta);
            final double b = FastMath.max(alpha, beta);
            if (a > 1.0) {
                return algorithmBB(random, alpha, a, b);
            }
            return algorithmBC(random, alpha, b, a);
        }
        
        private static double algorithmBB(final RandomGenerator random, final double a0, final double a, final double b) {
            final double alpha = a + b;
            final double beta = FastMath.sqrt((alpha - 2.0) / (2.0 * a * b - alpha));
            final double gamma = a + 1.0 / beta;
            double r;
            double w;
            double t;
            do {
                final double u1 = random.nextDouble();
                final double u2 = random.nextDouble();
                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                final double z = u1 * u1 * u2;
                r = gamma * v - 1.3862944;
                final double s = a + r - w;
                if (s + 2.609438 >= 5.0 * z) {
                    break;
                }
                t = FastMath.log(z);
                if (s >= t) {
                    break;
                }
            } while (r + alpha * (FastMath.log(alpha) - FastMath.log(b + w)) < t);
            w = FastMath.min(w, Double.MAX_VALUE);
            return Precision.equals(a, a0) ? (w / (b + w)) : (b / (b + w));
        }
        
        private static double algorithmBC(final RandomGenerator random, final double a0, final double a, final double b) {
            final double alpha = a + b;
            final double beta = 1.0 / b;
            final double delta = 1.0 + a - b;
            final double k1 = delta * (0.0138889 + 0.0416667 * b) / (a * beta - 0.777778);
            final double k2 = 0.25 + (0.5 + 0.25 / delta) * b;
            double w;
            while (true) {
                final double u1 = random.nextDouble();
                final double u2 = random.nextDouble();
                final double y = u1 * u2;
                final double z = u1 * y;
                if (u1 < 0.5) {
                    if (0.25 * u2 + z - y >= k1) {
                        continue;
                    }
                }
                else {
                    if (z <= 0.25) {
                        final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                        w = a * FastMath.exp(v);
                        break;
                    }
                    if (z >= k2) {
                        continue;
                    }
                }
                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                if (alpha * (FastMath.log(alpha) - FastMath.log(b + w) + v) - 1.3862944 >= FastMath.log(z)) {
                    break;
                }
            }
            w = FastMath.min(w, Double.MAX_VALUE);
            return Precision.equals(a, a0) ? (w / (b + w)) : (b / (b + w));
        }
    }
}
