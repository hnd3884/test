package org.apache.commons.math3.analysis;

public interface MultivariateMatrixFunction
{
    double[][] value(final double[] p0) throws IllegalArgumentException;
}
