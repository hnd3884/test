package org.apache.commons.math3.analysis;

public interface ParametricUnivariateFunction
{
    double value(final double p0, final double... p1);
    
    double[] gradient(final double p0, final double... p1);
}
