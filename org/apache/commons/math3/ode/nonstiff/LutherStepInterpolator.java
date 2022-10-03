package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

class LutherStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20140416L;
    private static final double Q;
    
    public LutherStepInterpolator() {
    }
    
    LutherStepInterpolator(final LutherStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new LutherStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double coeffDot1 = 1.0 + theta * (-10.8 + theta * (36.0 + theta * (-47.0 + theta * 21.0)));
        final double coeffDot2 = 0.0;
        final double coeffDot3 = theta * (-13.866666666666667 + theta * (106.66666666666667 + theta * (-202.66666666666666 + theta * 112.0)));
        final double coeffDot4 = theta * (12.96 + theta * (-97.2 + theta * (194.4 + theta * -567.0 / 5.0)));
        final double coeffDot5 = theta * ((833.0 + 343.0 * LutherStepInterpolator.Q) / 150.0 + theta * ((-637.0 - 357.0 * LutherStepInterpolator.Q) / 30.0 + theta * ((392.0 + 287.0 * LutherStepInterpolator.Q) / 15.0 + theta * (-49.0 - 49.0 * LutherStepInterpolator.Q) / 5.0)));
        final double coeffDot6 = theta * ((833.0 - 343.0 * LutherStepInterpolator.Q) / 150.0 + theta * ((-637.0 + 357.0 * LutherStepInterpolator.Q) / 30.0 + theta * ((392.0 - 287.0 * LutherStepInterpolator.Q) / 15.0 + theta * (-49.0 + 49.0 * LutherStepInterpolator.Q) / 5.0)));
        final double coeffDot7 = theta * (0.6 + theta * (-3.0 + theta * 3.0));
        if (this.previousState != null && theta <= 0.5) {
            final double coeff1 = 1.0 + theta * (-5.4 + theta * (12.0 + theta * (-11.75 + theta * 21.0 / 5.0)));
            final double coeff2 = 0.0;
            final double coeff3 = theta * (-6.933333333333334 + theta * (35.55555555555556 + theta * (-50.666666666666664 + theta * 112.0 / 5.0)));
            final double coeff4 = theta * (6.48 + theta * (-32.4 + theta * (48.6 + theta * -567.0 / 25.0)));
            final double coeff5 = theta * ((833.0 + 343.0 * LutherStepInterpolator.Q) / 300.0 + theta * ((-637.0 - 357.0 * LutherStepInterpolator.Q) / 90.0 + theta * ((392.0 + 287.0 * LutherStepInterpolator.Q) / 60.0 + theta * (-49.0 - 49.0 * LutherStepInterpolator.Q) / 25.0)));
            final double coeff6 = theta * ((833.0 - 343.0 * LutherStepInterpolator.Q) / 300.0 + theta * ((-637.0 + 357.0 * LutherStepInterpolator.Q) / 90.0 + theta * ((392.0 - 287.0 * LutherStepInterpolator.Q) / 60.0 + theta * (-49.0 + 49.0 * LutherStepInterpolator.Q) / 25.0)));
            final double coeff7 = theta * (0.3 + theta * (-1.0 + theta * 0.75));
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                final double yDot5 = this.yDotK[4][i];
                final double yDot6 = this.yDotK[5][i];
                final double yDot7 = this.yDotK[6][i];
                this.interpolatedState[i] = this.previousState[i] + theta * this.h * (coeff1 * yDot1 + 0.0 * yDot2 + coeff3 * yDot3 + coeff4 * yDot4 + coeff5 * yDot5 + coeff6 * yDot6 + coeff7 * yDot7);
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + 0.0 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4 + coeffDot5 * yDot5 + coeffDot6 * yDot6 + coeffDot7 * yDot7;
            }
        }
        else {
            final double coeff1 = -0.05 + theta * (0.95 + theta * (-4.45 + theta * (7.55 + theta * -21.0 / 5.0)));
            final double coeff2 = 0.0;
            final double coeff3 = -0.35555555555555557 + theta * (-0.35555555555555557 + theta * (-7.288888888888889 + theta * (28.266666666666666 + theta * -112.0 / 5.0)));
            final double coeff4 = theta * (theta * (6.48 + theta * (-25.92 + theta * 567.0 / 25.0)));
            final double coeff5 = -0.2722222222222222 + theta * (-0.2722222222222222 + theta * ((2254.0 + 1029.0 * LutherStepInterpolator.Q) / 900.0 + theta * ((-1372.0 - 847.0 * LutherStepInterpolator.Q) / 300.0 + theta * (49.0 + 49.0 * LutherStepInterpolator.Q) / 25.0)));
            final double coeff6 = -0.2722222222222222 + theta * (-0.2722222222222222 + theta * ((2254.0 - 1029.0 * LutherStepInterpolator.Q) / 900.0 + theta * ((-1372.0 + 847.0 * LutherStepInterpolator.Q) / 300.0 + theta * (49.0 - 49.0 * LutherStepInterpolator.Q) / 25.0)));
            final double coeff7 = -0.05 + theta * (-0.05 + theta * (0.25 + theta * -0.75));
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot1 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[1][i];
                final double yDot3 = this.yDotK[2][i];
                final double yDot4 = this.yDotK[3][i];
                final double yDot5 = this.yDotK[4][i];
                final double yDot6 = this.yDotK[5][i];
                final double yDot7 = this.yDotK[6][i];
                this.interpolatedState[i] = this.currentState[i] + oneMinusThetaH * (coeff1 * yDot1 + 0.0 * yDot2 + coeff3 * yDot3 + coeff4 * yDot4 + coeff5 * yDot5 + coeff6 * yDot6 + coeff7 * yDot7);
                this.interpolatedDerivatives[i] = coeffDot1 * yDot1 + 0.0 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4 + coeffDot5 * yDot5 + coeffDot6 * yDot6 + coeffDot7 * yDot7;
            }
        }
    }
    
    static {
        Q = FastMath.sqrt(21.0);
    }
}
