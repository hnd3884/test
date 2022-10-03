package org.apache.commons.math3.ode.nonstiff;

public class MidpointIntegrator extends RungeKuttaIntegrator
{
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    
    public MidpointIntegrator(final double step) {
        super("midpoint", MidpointIntegrator.STATIC_C, MidpointIntegrator.STATIC_A, MidpointIntegrator.STATIC_B, new MidpointStepInterpolator(), step);
    }
    
    static {
        STATIC_C = new double[] { 0.5 };
        STATIC_A = new double[][] { { 0.5 } };
        STATIC_B = new double[] { 0.0, 1.0 };
    }
}
