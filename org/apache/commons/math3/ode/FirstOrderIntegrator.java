package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;

public interface FirstOrderIntegrator extends ODEIntegrator
{
    double integrate(final FirstOrderDifferentialEquations p0, final double p1, final double[] p2, final double p3, final double[] p4) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException;
}
