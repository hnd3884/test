package org.apache.commons.math3.ode.nonstiff;

public class EulerIntegrator extends RungeKuttaIntegrator
{
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    
    public EulerIntegrator(final double step) {
        super("Euler", EulerIntegrator.STATIC_C, EulerIntegrator.STATIC_A, EulerIntegrator.STATIC_B, new EulerStepInterpolator(), step);
    }
    
    static {
        STATIC_C = new double[0];
        STATIC_A = new double[0][];
        STATIC_B = new double[] { 1.0 };
    }
}
