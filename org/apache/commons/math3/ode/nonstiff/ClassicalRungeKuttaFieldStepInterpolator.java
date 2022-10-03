package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class ClassicalRungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    ClassicalRungeKuttaFieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
    }
    
    @Override
    protected ClassicalRungeKuttaFieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new ClassicalRungeKuttaFieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        final T one = time.getField().getOne();
        final T oneMinusTheta = one.subtract(theta);
        final T oneMinus2Theta = one.subtract(theta.multiply(2));
        final T coeffDot1 = oneMinusTheta.multiply(oneMinus2Theta);
        final T coeffDot2 = theta.multiply(oneMinusTheta).multiply(2);
        final T coeffDot3 = theta.multiply(oneMinus2Theta).negate();
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T fourTheta2 = theta.multiply(theta).multiply(4);
            final T s = thetaH.divide(6.0);
            final T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(9)).add(6.0));
            final T coeff2 = s.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(6).subtract(fourTheta2));
            final T coeff3 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
            interpolatedState = this.previousStateLinearCombination(coeff1, coeff2, coeff2, coeff3);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot2, coeffDot3);
        }
        else {
            final T fourTheta3 = theta.multiply(4);
            final T s = oneMinusThetaH.divide(6.0);
            final T coeff1 = s.multiply(theta.multiply(((FieldElement<RealFieldElement<T>>)fourTheta3).negate().add(5.0)).subtract(1.0));
            final T coeff2 = s.multiply(theta.multiply(fourTheta3.subtract(2.0)).subtract(2.0));
            final T coeff3 = s.multiply(theta.multiply(((FieldElement<RealFieldElement<T>>)fourTheta3).negate().subtract(1.0)).subtract(1.0));
            interpolatedState = this.currentStateLinearCombination(coeff1, coeff2, coeff2, coeff3);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot2, coeffDot3);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
