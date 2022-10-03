package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;

public class HighamHall54Integrator extends EmbeddedRungeKuttaIntegrator
{
    private static final String METHOD_NAME = "Higham-Hall 5(4)";
    private static final double[] STATIC_C;
    private static final double[][] STATIC_A;
    private static final double[] STATIC_B;
    private static final double[] STATIC_E;
    
    public HighamHall54Integrator(final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) {
        super("Higham-Hall 5(4)", false, HighamHall54Integrator.STATIC_C, HighamHall54Integrator.STATIC_A, HighamHall54Integrator.STATIC_B, new HighamHall54StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }
    
    public HighamHall54Integrator(final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) {
        super("Higham-Hall 5(4)", false, HighamHall54Integrator.STATIC_C, HighamHall54Integrator.STATIC_A, HighamHall54Integrator.STATIC_B, new HighamHall54StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }
    
    @Override
    public int getOrder() {
        return 5;
    }
    
    @Override
    protected double estimateError(final double[][] yDotK, final double[] y0, final double[] y1, final double h) {
        double error = 0.0;
        for (int j = 0; j < this.mainSetDimension; ++j) {
            double errSum = HighamHall54Integrator.STATIC_E[0] * yDotK[0][j];
            for (int l = 1; l < HighamHall54Integrator.STATIC_E.length; ++l) {
                errSum += HighamHall54Integrator.STATIC_E[l] * yDotK[l][j];
            }
            final double yScale = FastMath.max(FastMath.abs(y0[j]), FastMath.abs(y1[j]));
            final double tol = (this.vecAbsoluteTolerance == null) ? (this.scalAbsoluteTolerance + this.scalRelativeTolerance * yScale) : (this.vecAbsoluteTolerance[j] + this.vecRelativeTolerance[j] * yScale);
            final double ratio = h * errSum / tol;
            error += ratio * ratio;
        }
        return FastMath.sqrt(error / this.mainSetDimension);
    }
    
    static {
        STATIC_C = new double[] { 0.2222222222222222, 0.3333333333333333, 0.5, 0.6, 1.0, 1.0 };
        STATIC_A = new double[][] { { 0.2222222222222222 }, { 0.08333333333333333, 0.25 }, { 0.125, 0.0, 0.375 }, { 0.182, -0.27, 0.624, 0.064 }, { -0.55, 1.35, 2.4, -7.2, 5.0 }, { 0.08333333333333333, 0.0, 0.84375, -1.3333333333333333, 1.3020833333333333, 0.10416666666666667 } };
        STATIC_B = new double[] { 0.08333333333333333, 0.0, 0.84375, -1.3333333333333333, 1.3020833333333333, 0.10416666666666667, 0.0 };
        STATIC_E = new double[] { -0.05, 0.0, 0.50625, -1.2, 0.78125, 0.0625, -0.1 };
    }
}
