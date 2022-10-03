package org.apache.commons.math3.ode.nonstiff;

public class ThreeEighthesIntegrator extends RungeKuttaIntegrator
{
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    
    public ThreeEighthesIntegrator(final double step) {
        super("3/8", ThreeEighthesIntegrator.STATIC_C, ThreeEighthesIntegrator.STATIC_A, ThreeEighthesIntegrator.STATIC_B, new ThreeEighthesStepInterpolator(), step);
    }
    
    static {
        STATIC_C = new double[] { 0.3333333333333333, 0.6666666666666666, 1.0 };
        STATIC_A = new double[][] { { 0.3333333333333333 }, { -0.3333333333333333, 1.0 }, { 1.0, -1.0, 1.0 } };
        STATIC_B = new double[] { 0.125, 0.375, 0.375, 0.125 };
    }
}
