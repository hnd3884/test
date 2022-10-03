package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.RandomGenerator;

public class ConstantRealDistribution extends AbstractRealDistribution
{
    private static final long serialVersionUID = -4157745166772046273L;
    private final double value;
    
    public ConstantRealDistribution(final double value) {
        super(null);
        this.value = value;
    }
    
    public double density(final double x) {
        return (x == this.value) ? 1.0 : 0.0;
    }
    
    public double cumulativeProbability(final double x) {
        return (x < this.value) ? 0.0 : 1.0;
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        return this.value;
    }
    
    public double getNumericalMean() {
        return this.value;
    }
    
    public double getNumericalVariance() {
        return 0.0;
    }
    
    public double getSupportLowerBound() {
        return this.value;
    }
    
    public double getSupportUpperBound() {
        return this.value;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public double sample() {
        return this.value;
    }
    
    @Override
    public void reseedRandomGenerator(final long seed) {
    }
}
