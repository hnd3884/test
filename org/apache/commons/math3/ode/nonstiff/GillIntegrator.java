package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;

public class GillIntegrator extends RungeKuttaIntegrator
{
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    
    public GillIntegrator(final double step) {
        super("Gill", GillIntegrator.STATIC_C, GillIntegrator.STATIC_A, GillIntegrator.STATIC_B, new GillStepInterpolator(), step);
    }
    
    static {
        STATIC_C = new double[] { 0.5, 0.5, 1.0 };
        STATIC_A = new double[][] { { 0.5 }, { (FastMath.sqrt(2.0) - 1.0) / 2.0, (2.0 - FastMath.sqrt(2.0)) / 2.0 }, { 0.0, -FastMath.sqrt(2.0) / 2.0, (2.0 + FastMath.sqrt(2.0)) / 2.0 } };
        STATIC_B = new double[] { 0.16666666666666666, (2.0 - FastMath.sqrt(2.0)) / 6.0, (2.0 + FastMath.sqrt(2.0)) / 6.0, 0.16666666666666666 };
    }
}
