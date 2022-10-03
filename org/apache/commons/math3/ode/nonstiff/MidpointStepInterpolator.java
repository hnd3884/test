package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class MidpointStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20111120L;
    
    public MidpointStepInterpolator() {
    }
    
    MidpointStepInterpolator(final MidpointStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new MidpointStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double coeffDot2 = 2.0 * theta;
        final double coeffDot3 = 1.0 - coeffDot2;
        if (this.previousState != null && theta <= 0.5) {
            final double coeff1 = theta * oneMinusThetaH;
            final double coeff2 = theta * theta * this.h;
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                this.interpolatedState[i] = this.previousState[i] + coeff1 * yDot1 + coeff2 * yDot2;
                this.interpolatedDerivatives[i] = coeffDot3 * yDot1 + coeffDot2 * yDot2;
            }
        }
        else {
            final double coeff1 = oneMinusThetaH * theta;
            final double coeff2 = oneMinusThetaH * (1.0 + theta);
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                this.interpolatedState[i] = this.currentState[i] + coeff1 * yDot1 - coeff2 * yDot2;
                this.interpolatedDerivatives[i] = coeffDot3 * yDot1 + coeffDot2 * yDot2;
            }
        }
    }
}
