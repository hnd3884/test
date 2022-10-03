package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;

public interface ParameterJacobianProvider extends Parameterizable
{
    void computeParameterJacobian(final double p0, final double[] p1, final double[] p2, final String p3, final double[] p4) throws DimensionMismatchException, MaxCountExceededException, UnknownParameterException;
}
