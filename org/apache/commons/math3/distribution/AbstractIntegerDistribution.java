package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomDataImpl;
import java.io.Serializable;

public abstract class AbstractIntegerDistribution implements IntegerDistribution, Serializable
{
    private static final long serialVersionUID = -1146319659338487221L;
    @Deprecated
    protected final RandomDataImpl randomData;
    protected final RandomGenerator random;
    
    @Deprecated
    protected AbstractIntegerDistribution() {
        this.randomData = new RandomDataImpl();
        this.random = null;
    }
    
    protected AbstractIntegerDistribution(final RandomGenerator rng) {
        this.randomData = new RandomDataImpl();
        this.random = rng;
    }
    
    public double cumulativeProbability(final int x0, final int x1) throws NumberIsTooLargeException {
        if (x1 < x0) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1, true);
        }
        return this.cumulativeProbability(x1) - this.cumulativeProbability(x0);
    }
    
    public int inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        int lower = this.getSupportLowerBound();
        if (p == 0.0) {
            return lower;
        }
        if (lower == Integer.MIN_VALUE) {
            if (this.checkedCumulativeProbability(lower) >= p) {
                return lower;
            }
        }
        else {
            --lower;
        }
        int upper = this.getSupportUpperBound();
        if (p == 1.0) {
            return upper;
        }
        final double mu = this.getNumericalMean();
        final double sigma = FastMath.sqrt(this.getNumericalVariance());
        final boolean chebyshevApplies = !Double.isInfinite(mu) && !Double.isNaN(mu) && !Double.isInfinite(sigma) && !Double.isNaN(sigma) && sigma != 0.0;
        if (chebyshevApplies) {
            double k = FastMath.sqrt((1.0 - p) / p);
            double tmp = mu - k * sigma;
            if (tmp > lower) {
                lower = (int)FastMath.ceil(tmp) - 1;
            }
            k = 1.0 / k;
            tmp = mu + k * sigma;
            if (tmp < upper) {
                upper = (int)FastMath.ceil(tmp) - 1;
            }
        }
        return this.solveInverseCumulativeProbability(p, lower, upper);
    }
    
    protected int solveInverseCumulativeProbability(final double p, int lower, int upper) {
        while (lower + 1 < upper) {
            int xm = (lower + upper) / 2;
            if (xm < lower || xm > upper) {
                xm = lower + (upper - lower) / 2;
            }
            final double pm = this.checkedCumulativeProbability(xm);
            if (pm >= p) {
                upper = xm;
            }
            else {
                lower = xm;
            }
        }
        return upper;
    }
    
    public void reseedRandomGenerator(final long seed) {
        this.random.setSeed(seed);
        this.randomData.reSeed(seed);
    }
    
    public int sample() {
        return this.inverseCumulativeProbability(this.random.nextDouble());
    }
    
    public int[] sample(final int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
        }
        final int[] out = new int[sampleSize];
        for (int i = 0; i < sampleSize; ++i) {
            out[i] = this.sample();
        }
        return out;
    }
    
    private double checkedCumulativeProbability(final int argument) throws MathInternalError {
        double result = Double.NaN;
        result = this.cumulativeProbability(argument);
        if (Double.isNaN(result)) {
            throw new MathInternalError(LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, new Object[] { argument });
        }
        return result;
    }
    
    public double logProbability(final int x) {
        return FastMath.log(this.probability(x));
    }
}
