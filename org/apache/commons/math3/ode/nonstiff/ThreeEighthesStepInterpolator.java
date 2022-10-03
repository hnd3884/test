package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class ThreeEighthesStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20111120L;
    
    public ThreeEighthesStepInterpolator() {
    }
    
    ThreeEighthesStepInterpolator(final ThreeEighthesStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new ThreeEighthesStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double coeffDot3 = 0.75 * theta;
        final double coeffDot4 = coeffDot3 * (4.0 * theta - 5.0) + 1.0;
        final double coeffDot5 = coeffDot3 * (5.0 - 6.0 * theta);
        final double coeffDot6 = coeffDot3 * (2.0 * theta - 1.0);
        if (this.previousState != null && theta <= 0.5) {
            final double s = theta * this.h / 8.0;
            final double fourTheta2 = 4.0 * theta * theta;
            final double coeff1 = s * (8.0 - 15.0 * theta + 2.0 * fourTheta2);
            final double coeff2 = 3.0 * s * (5.0 * theta - fourTheta2);
            final double coeff3 = 3.0 * s * theta;
            final double coeff4 = s * (-3.0 * theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                this.interpolatedState[i] = this.previousState[i] + coeff1 * yDot1 + coeff2 * yDot2 + coeff3 * yDot3 + coeff4 * yDot4;
                this.interpolatedDerivatives[i] = coeffDot4 * yDot1 + coeffDot5 * yDot2 + coeffDot3 * yDot3 + coeffDot6 * yDot4;
            }
        }
        else {
            final double s = oneMinusThetaH / 8.0;
            final double fourTheta2 = 4.0 * theta * theta;
            final double coeff1 = s * (1.0 - 7.0 * theta + 2.0 * fourTheta2);
            final double coeff2 = 3.0 * s * (1.0 + theta - fourTheta2);
            final double coeff3 = 3.0 * s * (1.0 + theta);
            final double coeff4 = s * (1.0 + theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                this.interpolatedState[i] = this.currentState[i] - coeff1 * yDot1 - coeff2 * yDot2 - coeff3 * yDot3 - coeff4 * yDot4;
                this.interpolatedDerivatives[i] = coeffDot4 * yDot1 + coeffDot5 * yDot2 + coeffDot3 * yDot3 + coeffDot6 * yDot4;
            }
        }
    }
}
