package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class PoissonDistribution extends AbstractIntegerDistribution
{
    public static final int DEFAULT_MAX_ITERATIONS = 10000000;
    public static final double DEFAULT_EPSILON = 1.0E-12;
    private static final long serialVersionUID = -3349935121172596109L;
    private final NormalDistribution normal;
    private final ExponentialDistribution exponential;
    private final double mean;
    private final int maxIterations;
    private final double epsilon;
    
    public PoissonDistribution(final double p) throws NotStrictlyPositiveException {
        this(p, 1.0E-12, 10000000);
    }
    
    public PoissonDistribution(final double p, final double epsilon, final int maxIterations) throws NotStrictlyPositiveException {
        this(new Well19937c(), p, epsilon, maxIterations);
    }
    
    public PoissonDistribution(final RandomGenerator rng, final double p, final double epsilon, final int maxIterations) throws NotStrictlyPositiveException {
        super(rng);
        if (p <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, p);
        }
        this.mean = p;
        this.epsilon = epsilon;
        this.maxIterations = maxIterations;
        this.normal = new NormalDistribution(rng, p, FastMath.sqrt(p), 1.0E-9);
        this.exponential = new ExponentialDistribution(rng, 1.0, 1.0E-9);
    }
    
    public PoissonDistribution(final double p, final double epsilon) throws NotStrictlyPositiveException {
        this(p, epsilon, 10000000);
    }
    
    public PoissonDistribution(final double p, final int maxIterations) {
        this(p, 1.0E-12, maxIterations);
    }
    
    public double getMean() {
        return this.mean;
    }
    
    public double probability(final int x) {
        final double logProbability = this.logProbability(x);
        return (logProbability == Double.NEGATIVE_INFINITY) ? 0.0 : FastMath.exp(logProbability);
    }
    
    @Override
    public double logProbability(final int x) {
        double ret;
        if (x < 0 || x == Integer.MAX_VALUE) {
            ret = Double.NEGATIVE_INFINITY;
        }
        else if (x == 0) {
            ret = -this.mean;
        }
        else {
            ret = -SaddlePointExpansion.getStirlingError(x) - SaddlePointExpansion.getDeviancePart(x, this.mean) - 0.5 * FastMath.log(6.283185307179586) - 0.5 * FastMath.log(x);
        }
        return ret;
    }
    
    public double cumulativeProbability(final int x) {
        if (x < 0) {
            return 0.0;
        }
        if (x == Integer.MAX_VALUE) {
            return 1.0;
        }
        return Gamma.regularizedGammaQ(x + 1.0, this.mean, this.epsilon, this.maxIterations);
    }
    
    public double normalApproximateProbability(final int x) {
        return this.normal.cumulativeProbability(x + 0.5);
    }
    
    public double getNumericalMean() {
        return this.getMean();
    }
    
    public double getNumericalVariance() {
        return this.getMean();
    }
    
    public int getSupportLowerBound() {
        return 0;
    }
    
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public int sample() {
        return (int)FastMath.min(this.nextPoisson(this.mean), 2147483647L);
    }
    
    private long nextPoisson(final double meanPoisson) {
        final double pivot = 40.0;
        if (meanPoisson < 40.0) {
            final double p = FastMath.exp(-meanPoisson);
            long n = 0L;
            double r = 1.0;
            double rnd = 1.0;
            while (n < 1000.0 * meanPoisson) {
                rnd = this.random.nextDouble();
                r *= rnd;
                if (r < p) {
                    return n;
                }
                ++n;
            }
            return n;
        }
        final double lambda = FastMath.floor(meanPoisson);
        final double lambdaFractional = meanPoisson - lambda;
        final double logLambda = FastMath.log(lambda);
        final double logLambdaFactorial = CombinatoricsUtils.factorialLog((int)lambda);
        final long y2 = (lambdaFractional < Double.MIN_VALUE) ? 0L : this.nextPoisson(lambdaFractional);
        final double delta = FastMath.sqrt(lambda * FastMath.log(32.0 * lambda / 3.141592653589793 + 1.0));
        final double halfDelta = delta / 2.0;
        final double twolpd = 2.0 * lambda + delta;
        final double a1 = FastMath.sqrt(3.141592653589793 * twolpd) * FastMath.exp(1.0 / (8.0 * lambda));
        final double a2 = twolpd / delta * FastMath.exp(-delta * (1.0 + delta) / twolpd);
        final double aSum = a1 + a2 + 1.0;
        final double p2 = a1 / aSum;
        final double p3 = a2 / aSum;
        final double c1 = 1.0 / (8.0 * lambda);
        double x = 0.0;
        double y3 = 0.0;
        double v = 0.0;
        int a3 = 0;
        double t = 0.0;
        double qr = 0.0;
        double qa = 0.0;
        while (true) {
            final double u = this.random.nextDouble();
            if (u <= p2) {
                final double n2 = this.random.nextGaussian();
                x = n2 * FastMath.sqrt(lambda + halfDelta) - 0.5;
                if (x > delta) {
                    continue;
                }
                if (x < -lambda) {
                    continue;
                }
                y3 = ((x < 0.0) ? FastMath.floor(x) : FastMath.ceil(x));
                final double e = this.exponential.sample();
                v = -e - n2 * n2 / 2.0 + c1;
            }
            else {
                if (u > p2 + p3) {
                    y3 = lambda;
                    break;
                }
                x = delta + twolpd / delta * this.exponential.sample();
                y3 = FastMath.ceil(x);
                v = -this.exponential.sample() - delta * (x + 1.0) / twolpd;
            }
            a3 = ((x < 0.0) ? 1 : 0);
            t = y3 * (y3 + 1.0) / (2.0 * lambda);
            if (v < -t && a3 == 0) {
                y3 += lambda;
                break;
            }
            qr = t * ((2.0 * y3 + 1.0) / (6.0 * lambda) - 1.0);
            qa = qr - t * t / (3.0 * (lambda + a3 * (y3 + 1.0)));
            if (v < qa) {
                y3 += lambda;
                break;
            }
            if (v > qr) {
                continue;
            }
            if (v < y3 * logLambda - CombinatoricsUtils.factorialLog((int)(y3 + lambda)) + logLambdaFactorial) {
                y3 += lambda;
                break;
            }
        }
        return y2 + (long)y3;
    }
}
