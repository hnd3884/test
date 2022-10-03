package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public interface RealDistribution
{
    double probability(final double p0);
    
    double density(final double p0);
    
    double cumulativeProbability(final double p0);
    
    @Deprecated
    double cumulativeProbability(final double p0, final double p1) throws NumberIsTooLargeException;
    
    double inverseCumulativeProbability(final double p0) throws OutOfRangeException;
    
    double getNumericalMean();
    
    double getNumericalVariance();
    
    double getSupportLowerBound();
    
    double getSupportUpperBound();
    
    @Deprecated
    boolean isSupportLowerBoundInclusive();
    
    @Deprecated
    boolean isSupportUpperBoundInclusive();
    
    boolean isSupportConnected();
    
    void reseedRandomGenerator(final long p0);
    
    double sample();
    
    double[] sample(final int p0);
}
