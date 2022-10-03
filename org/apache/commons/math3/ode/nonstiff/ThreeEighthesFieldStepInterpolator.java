package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class ThreeEighthesFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    ThreeEighthesFieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
    }
    
    @Override
    protected ThreeEighthesFieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new ThreeEighthesFieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        final T coeffDot3 = theta.multiply(0.75);
        final T coeffDot4 = coeffDot3.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(4).subtract(5.0)).add(1.0);
        final T coeffDot5 = coeffDot3.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(-6).add(5.0));
        final T coeffDot6 = coeffDot3.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(2).subtract(1.0));
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T s = thetaH.divide(8.0);
            final T fourTheta2 = theta.multiply(theta).multiply(4);
            final T coeff1 = s.multiply((T)((FieldElement<RealFieldElement<RealFieldElement>>)fourTheta2).multiply(2).subtract(((FieldElement<RealFieldElement>)theta).multiply(15)).add(8.0));
            final T coeff2 = s.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(5).subtract(fourTheta2)).multiply(3);
            final T coeff3 = s.multiply(theta).multiply(3);
            final T coeff4 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
            interpolatedState = this.previousStateLinearCombination(coeff1, coeff2, coeff3, coeff4);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot4, coeffDot5, coeffDot3, coeffDot6);
        }
        else {
            final T s = oneMinusThetaH.divide(-8.0);
            final T fourTheta2 = theta.multiply(theta).multiply(4);
            final T thetaPlus1 = theta.add(1.0);
            final T coeff5 = s.multiply((T)((FieldElement<RealFieldElement<RealFieldElement>>)fourTheta2).multiply(2).subtract(((FieldElement<RealFieldElement>)theta).multiply(7)).add(1.0));
            final T coeff6 = s.multiply(thetaPlus1.subtract(fourTheta2)).multiply(3);
            final T coeff7 = s.multiply(thetaPlus1).multiply(3);
            final T coeff8 = s.multiply(thetaPlus1.add(fourTheta2));
            interpolatedState = this.currentStateLinearCombination(coeff5, coeff6, coeff7, coeff8);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot4, coeffDot5, coeffDot3, coeffDot6);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
