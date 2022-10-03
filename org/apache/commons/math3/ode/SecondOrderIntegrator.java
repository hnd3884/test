package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;

public interface SecondOrderIntegrator extends ODEIntegrator
{
    void integrate(final SecondOrderDifferentialEquations p0, final double p1, final double[] p2, final double[] p3, final double p4, final double[] p5, final double[] p6) throws MathIllegalStateException, MathIllegalArgumentException;
}
