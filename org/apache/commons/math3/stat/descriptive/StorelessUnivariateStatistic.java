package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface StorelessUnivariateStatistic extends UnivariateStatistic
{
    void increment(final double p0);
    
    void incrementAll(final double[] p0) throws MathIllegalArgumentException;
    
    void incrementAll(final double[] p0, final int p1, final int p2) throws MathIllegalArgumentException;
    
    double getResult();
    
    long getN();
    
    void clear();
    
    StorelessUnivariateStatistic copy();
}
