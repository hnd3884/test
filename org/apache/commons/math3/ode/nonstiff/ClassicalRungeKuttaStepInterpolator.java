package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class ClassicalRungeKuttaStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20111120L;
    
    public ClassicalRungeKuttaStepInterpolator() {
    }
    
    ClassicalRungeKuttaStepInterpolator(final ClassicalRungeKuttaStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new ClassicalRungeKuttaStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double oneMinusTheta = 1.0 - theta;
        final double oneMinus2Theta = 1.0 - 2.0 * theta;
        final double coeffDot1 = oneMinusTheta * oneMinus2Theta;
        final double coeffDot2 = 2.0 * theta * oneMinusTheta;
        final double coeffDot3 = -theta * oneMinus2Theta;
        if (this.previousState != null && theta <= 0.5) {
            final double fourTheta2 = 4.0 * theta * theta;
            final double s = theta * this.h / 6.0;
            final double coeff1 = s * (6.0 - 9.0 * theta + fourTheta2);
            final double coeff2 = s * (6.0 * theta - fourTheta2);
            final double coeff3 = s * (-3.0 * theta + fourTheta2);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i] + this.yDotK[2][i];
                final double yDot3 = this.yDotK[3][i];
                this.interpolatedState[i] = this.previousState[i] + coeff1 * yDot1 + coeff2 * yDot2 + coeff3 * yDot3;
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3;
            }
        }
        else {
            final double fourTheta3 = 4.0 * theta;
            final double s = oneMinusThetaH / 6.0;
            final double coeff1 = s * ((-fourTheta3 + 5.0) * theta - 1.0);
            final double coeff2 = s * ((fourTheta3 - 2.0) * theta - 2.0);
            final double coeff3 = s * ((-fourTheta3 - 1.0) * theta - 1.0);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i] + this.yDotK[2][i];
                final double yDot3 = this.yDotK[3][i];
                this.interpolatedState[i] = this.currentState[i] + coeff1 * yDot1 + coeff2 * yDot2 + coeff3 * yDot3;
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3;
            }
        }
    }
}
