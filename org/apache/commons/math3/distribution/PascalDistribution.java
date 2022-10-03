package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class PascalDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = 6751309484392813623L;
    private final int numberOfSuccesses;
    private final double probabilityOfSuccess;
    private final double logProbabilityOfSuccess;
    private final double log1mProbabilityOfSuccess;
    
    public PascalDistribution(final int r, final double p) throws NotStrictlyPositiveException, OutOfRangeException {
        this(new Well19937c(), r, p);
    }
    
    public PascalDistribution(final RandomGenerator rng, final int r, final double p) throws NotStrictlyPositiveException, OutOfRangeException {
        super(rng);
        if (r <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES, r);
        }
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        this.numberOfSuccesses = r;
        this.probabilityOfSuccess = p;
        this.logProbabilityOfSuccess = FastMath.log(p);
        this.log1mProbabilityOfSuccess = FastMath.log1p(-p);
    }
    
    public int getNumberOfSuccesses() {
        return this.numberOfSuccesses;
    }
    
    public double getProbabilityOfSuccess() {
        return this.probabilityOfSuccess;
    }
    
    public double probability(final int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        }
        else {
            ret = CombinatoricsUtils.binomialCoefficientDouble(x + this.numberOfSuccesses - 1, this.numberOfSuccesses - 1) * FastMath.pow(this.probabilityOfSuccess, this.numberOfSuccesses) * FastMath.pow(1.0 - this.probabilityOfSuccess, x);
        }
        return ret;
    }
    
    @Override
    public double logProbability(final int x) {
        double ret;
        if (x < 0) {
            ret = Double.NEGATIVE_INFINITY;
        }
        else {
            ret = CombinatoricsUtils.binomialCoefficientLog(x + this.numberOfSuccesses - 1, this.numberOfSuccesses - 1) + this.logProbabilityOfSuccess * this.numberOfSuccesses + this.log1mProbabilityOfSuccess * x;
        }
        return ret;
    }
    
    public double cumulativeProbability(final int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        }
        else {
            ret = Beta.regularizedBeta(this.probabilityOfSuccess, this.numberOfSuccesses, x + 1.0);
        }
        return ret;
    }
    
    public double getNumericalMean() {
        final double p = this.getProbabilityOfSuccess();
        final double r = this.getNumberOfSuccesses();
        return r * (1.0 - p) / p;
    }
    
    public double getNumericalVariance() {
        final double p = this.getProbabilityOfSuccess();
        final double r = this.getNumberOfSuccesses();
        return r * (1.0 - p) / (p * p);
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
}
