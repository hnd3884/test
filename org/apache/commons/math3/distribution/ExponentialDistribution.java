package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.ResizableDoubleArray;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class ExponentialDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = 2401296428283614780L;
    private static final double[] EXPONENTIAL_SA_QI;
    private final double mean;
    private final double logMean;
    private final double solverAbsoluteAccuracy;
    
    public ExponentialDistribution(final double mean) {
        this(mean, 1.0E-9);
    }
    
    public ExponentialDistribution(final double mean, final double inverseCumAccuracy) {
        this(new Well19937c(), mean, inverseCumAccuracy);
    }
    
    public ExponentialDistribution(final RandomGenerator rng, final double mean) throws NotStrictlyPositiveException {
        this(rng, mean, 1.0E-9);
    }
    
    public ExponentialDistribution(final RandomGenerator rng, final double mean, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (mean <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, mean);
        }
        this.mean = mean;
        this.logMean = FastMath.log(mean);
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double getMean() {
        return this.mean;
    }
    
    public double density(final double x) {
        final double logDensity = this.logDensity(x);
        return (logDensity == Double.NEGATIVE_INFINITY) ? 0.0 : FastMath.exp(logDensity);
    }
    
    @Override
    public double logDensity(final double x) {
        if (x < 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        return -x / this.mean - this.logMean;
    }
    
    public double cumulativeProbability(final double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        }
        else {
            ret = 1.0 - FastMath.exp(-x / this.mean);
        }
        return ret;
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0.0, 1.0);
        }
        double ret;
        if (p == 1.0) {
            ret = Double.POSITIVE_INFINITY;
        }
        else {
            ret = -this.mean * FastMath.log(1.0 - p);
        }
        return ret;
    }
    
    @Override
    public double sample() {
        double a = 0.0;
        double u;
        for (u = this.random.nextDouble(); u < 0.5; u *= 2.0) {
            a += ExponentialDistribution.EXPONENTIAL_SA_QI[0];
        }
        u += u - 1.0;
        if (u <= ExponentialDistribution.EXPONENTIAL_SA_QI[0]) {
            return this.mean * (a + u);
        }
        int i = 0;
        double umin;
        double u2 = umin = this.random.nextDouble();
        do {
            ++i;
            u2 = this.random.nextDouble();
            if (u2 < umin) {
                umin = u2;
            }
        } while (u > ExponentialDistribution.EXPONENTIAL_SA_QI[i]);
        return this.mean * (a + umin * ExponentialDistribution.EXPONENTIAL_SA_QI[0]);
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        return this.getMean();
    }
    
    public double getNumericalVariance() {
        final double m = this.getMean();
        return m * m;
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
    
    static {
        final double LN2 = FastMath.log(2.0);
        double qi = 0.0;
        int i = 1;
        final ResizableDoubleArray ra = new ResizableDoubleArray(20);
        while (qi < 1.0) {
            qi += FastMath.pow(LN2, i) / CombinatoricsUtils.factorial(i);
            ra.addElement(qi);
            ++i;
        }
        EXPONENTIAL_SA_QI = ra.getElements();
    }
}
