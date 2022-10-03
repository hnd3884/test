package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface MultivariateRealDistribution
{
    double density(final double[] p0);
    
    void reseedRandomGenerator(final long p0);
    
    int getDimension();
    
    double[] sample();
    
    double[][] sample(final int p0) throws NotStrictlyPositiveException;
}
