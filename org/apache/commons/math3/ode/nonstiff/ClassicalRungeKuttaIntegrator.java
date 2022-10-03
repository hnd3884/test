package org.apache.commons.math3.ode.nonstiff;

public class ClassicalRungeKuttaIntegrator extends RungeKuttaIntegrator
{
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    
    public ClassicalRungeKuttaIntegrator(final double step) {
        super("classical Runge-Kutta", ClassicalRungeKuttaIntegrator.STATIC_C, ClassicalRungeKuttaIntegrator.STATIC_A, ClassicalRungeKuttaIntegrator.STATIC_B, new ClassicalRungeKuttaStepInterpolator(), step);
    }
    
    static {
        STATIC_C = new double[] { 0.5, 0.5, 1.0 };
        STATIC_A = new double[][] { { 0.5 }, { 0.0, 0.5 }, { 0.0, 0.0, 1.0 } };
        STATIC_B = new double[] { 0.16666666666666666, 0.3333333333333333, 0.3333333333333333, 0.16666666666666666 };
    }
}
