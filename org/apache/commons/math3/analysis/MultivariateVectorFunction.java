package org.apache.commons.math3.analysis;

public interface MultivariateVectorFunction
{
    double[] value(final double[] p0) throws IllegalArgumentException;
}
