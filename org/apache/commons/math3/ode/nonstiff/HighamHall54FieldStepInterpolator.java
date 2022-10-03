package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class HighamHall54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    HighamHall54FieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
    }
    
    @Override
    protected HighamHall54FieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new HighamHall54FieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        final T bDot0 = theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-10.0).add(16.0)).add(-7.5)).add(1.0);
        final T bDot2 = time.getField().getZero();
        final T bDot3 = theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(67.5).add(-91.125)).add(28.6875));
        final T bDot4 = theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-120.0).add(152.0)).add(-44.0));
        final T bDot5 = theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(62.5).add(-78.125)).add(23.4375));
        final T bDot6 = ((RealFieldElement<RealFieldElement<T>>)theta).multiply(0.625).multiply((T)((FieldElement<RealFieldElement<Object>>)theta).multiply(2).subtract(1.0));
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T b0 = thetaH.multiply(theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-2.5).add(5.333333333333333)).add(-3.75)).add(1.0));
            final T b2 = time.getField().getZero();
            final T b3 = thetaH.multiply(theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(16.875).add(-30.375)).add(14.34375)));
            final T b4 = thetaH.multiply(theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-30.0).add(50.666666666666664)).add(-22.0)));
            final T b5 = thetaH.multiply(theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(15.625).add(-26.041666666666668)).add(11.71875)));
            final T b6 = thetaH.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(0.4166666666666667).add(-0.3125)));
            interpolatedState = this.previousStateLinearCombination(b0, b2, b3, b4, b5, b6);
            interpolatedDerivatives = this.derivativeLinearCombination(bDot0, bDot2, bDot3, bDot4, bDot5, bDot6);
        }
        else {
            final T theta2 = theta.multiply(theta);
            final T h = thetaH.divide(theta);
            final T b7 = h.multiply(theta.multiply(theta.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-2.5).add(5.333333333333333)).add(-3.75)).add(1.0)).add(-0.08333333333333333));
            final T b8 = time.getField().getZero();
            final T b9 = h.multiply(theta2.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(16.875).add(-30.375)).add(14.34375)).add(-0.84375));
            final T b10 = h.multiply(theta2.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(-30.0).add(50.666666666666664)).add(-22.0)).add(1.3333333333333333));
            final T b11 = h.multiply(theta2.multiply(theta.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(15.625).add(-26.041666666666668)).add(11.71875)).add(-1.3020833333333333));
            final T b12 = h.multiply(theta2.multiply(((RealFieldElement<RealFieldElement<T>>)theta).multiply(0.4166666666666667).add(-0.3125)).add(-0.10416666666666667));
            interpolatedState = this.currentStateLinearCombination(b7, b8, b9, b10, b11, b12);
            interpolatedDerivatives = this.derivativeLinearCombination(bDot0, bDot2, bDot3, bDot4, bDot5, bDot6);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
