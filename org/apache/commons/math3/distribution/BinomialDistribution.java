package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class BinomialDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = 6751309484392813623L;
    private final int numberOfTrials;
    private final double probabilityOfSuccess;
    
    public BinomialDistribution(final int trials, final double p) {
        this(new Well19937c(), trials, p);
    }
    
    public BinomialDistribution(final RandomGenerator rng, final int trials, final double p) {
        super(rng);
        if (trials < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_TRIALS, trials);
        }
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        this.probabilityOfSuccess = p;
        this.numberOfTrials = trials;
    }
    
    public int getNumberOfTrials() {
        return this.numberOfTrials;
    }
    
    public double getProbabilityOfSuccess() {
        return this.probabilityOfSuccess;
    }
    
    public double probability(final int x) {
        final double logProbability = this.logProbability(x);
        return (logProbability == Double.NEGATIVE_INFINITY) ? 0.0 : FastMath.exp(logProbability);
    }
    
    @Override
    public double logProbability(final int x) {
        if (this.numberOfTrials == 0) {
            return (x == 0) ? 0.0 : Double.NEGATIVE_INFINITY;
        }
        double ret;
        if (x < 0 || x > this.numberOfTrials) {
            ret = Double.NEGATIVE_INFINITY;
        }
        else {
            ret = SaddlePointExpansion.logBinomialProbability(x, this.numberOfTrials, this.probabilityOfSuccess, 1.0 - this.probabilityOfSuccess);
        }
        return ret;
    }
    
    public double cumulativeProbability(final int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        }
        else if (x >= this.numberOfTrials) {
            ret = 1.0;
        }
        else {
            ret = 1.0 - Beta.regularizedBeta(this.probabilityOfSuccess, x + 1.0, this.numberOfTrials - x);
        }
        return ret;
    }
    
    public double getNumericalMean() {
        return this.numberOfTrials * this.probabilityOfSuccess;
    }
    
    public double getNumericalVariance() {
        final double p = this.probabilityOfSuccess;
        return this.numberOfTrials * p * (1.0 - p);
    }
    
    public int getSupportLowerBound() {
        return (this.probabilityOfSuccess < 1.0) ? 0 : this.numberOfTrials;
    }
    
    public int getSupportUpperBound() {
        return (this.probabilityOfSuccess > 0.0) ? this.numberOfTrials : 0;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
