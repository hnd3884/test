package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

class GillStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final double ONE_MINUS_INV_SQRT_2;
    private static final double ONE_PLUS_INV_SQRT_2;
    private static final long serialVersionUID = 20111120L;
    
    public GillStepInterpolator() {
    }
    
    GillStepInterpolator(final GillStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new GillStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double twoTheta = 2.0 * theta;
        final double fourTheta2 = twoTheta * twoTheta;
        final double coeffDot1 = theta * (twoTheta - 3.0) + 1.0;
        final double cDot23 = twoTheta * (1.0 - theta);
        final double coeffDot2 = cDot23 * GillStepInterpolator.ONE_MINUS_INV_SQRT_2;
        final double coeffDot3 = cDot23 * GillStepInterpolator.ONE_PLUS_INV_SQRT_2;
        final double coeffDot4 = theta * (twoTheta - 1.0);
        if (this.previousState != null && theta <= 0.5) {
            final double s = theta * this.h / 6.0;
            final double c23 = s * (6.0 * theta - fourTheta2);
            final double coeff1 = s * (6.0 - 9.0 * theta + fourTheta2);
            final double coeff2 = c23 * GillStepInterpolator.ONE_MINUS_INV_SQRT_2;
            final double coeff3 = c23 * GillStepInterpolator.ONE_PLUS_INV_SQRT_2;
            final double coeff4 = s * (-3.0 * theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                this.interpolatedState[i] = this.previousState[i] + coeff1 * yDot1 + coeff2 * yDot2 + coeff3 * yDot3 + coeff4 * yDot4;
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4;
            }
        }
        else {
            final double s = oneMinusThetaH / 6.0;
            final double c23 = s * (2.0 + twoTheta - fourTheta2);
            final double coeff1 = s * (1.0 - 5.0 * theta + fourTheta2);
            final double coeff2 = c23 * GillStepInterpolator.ONE_MINUS_INV_SQRT_2;
            final double coeff3 = c23 * GillStepInterpolator.ONE_PLUS_INV_SQRT_2;
            final double coeff4 = s * (1.0 + theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                this.interpolatedState[i] = this.currentState[i] - coeff1 * yDot1 - coeff2 * yDot2 - coeff3 * yDot3 - coeff4 * yDot4;
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4;
            }
        }
    }
    
    static {
        ONE_MINUS_INV_SQRT_2 = 1.0 - FastMath.sqrt(0.5);
        ONE_PLUS_INV_SQRT_2 = 1.0 + FastMath.sqrt(0.5);
    }
}
