package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public interface IntegerDistribution
{
    double probability(final int p0);
    
    double cumulativeProbability(final int p0);
    
    double cumulativeProbability(final int p0, final int p1) throws NumberIsTooLargeException;
    
    int inverseCumulativeProbability(final double p0) throws OutOfRangeException;
    
    double getNumericalMean();
    
    double getNumericalVariance();
    
    int getSupportLowerBound();
    
    int getSupportUpperBound();
    
    boolean isSupportConnected();
    
    void reseedRandomGenerator(final long p0);
    
    int sample();
    
    int[] sample(final int p0);
}
