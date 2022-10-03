package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class GillFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    private final T one_minus_inv_sqrt_2;
    private final T one_plus_inv_sqrt_2;
    
    GillFieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        final T sqrt = ((RealFieldElement<RealFieldElement<T>>)field.getZero()).add(0.5).sqrt();
        this.one_minus_inv_sqrt_2 = field.getOne().subtract(sqrt);
        this.one_plus_inv_sqrt_2 = field.getOne().add(sqrt);
    }
    
    @Override
    protected GillFieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new GillFieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        final T one = time.getField().getOne();
        final T twoTheta = theta.multiply(2);
        final T fourTheta2 = twoTheta.multiply(twoTheta);
        final T coeffDot1 = theta.multiply(twoTheta.subtract(3.0)).add(1.0);
        final T cDot23 = twoTheta.multiply(one.subtract(theta));
        final T coeffDot2 = cDot23.multiply(this.one_minus_inv_sqrt_2);
        final T coeffDot3 = cDot23.multiply(this.one_plus_inv_sqrt_2);
        final T coeffDot4 = theta.multiply(twoTheta.subtract(1.0));
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T s = thetaH.divide(6.0);
            final T c23 = s.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(6).subtract(fourTheta2));
            final T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(9)).add(6.0));
            final T coeff2 = c23.multiply(this.one_minus_inv_sqrt_2);
            final T coeff3 = c23.multiply(this.one_plus_inv_sqrt_2);
            final T coeff4 = s.multiply(fourTheta2.subtract(theta.multiply(3)));
            interpolatedState = this.previousStateLinearCombination(coeff1, coeff2, coeff3, coeff4);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot3, coeffDot4);
        }
        else {
            final T s = oneMinusThetaH.divide(-6.0);
            final T c23 = s.multiply(((RealFieldElement<RealFieldElement<T>>)twoTheta).add(2.0).subtract(fourTheta2));
            final T coeff1 = s.multiply(fourTheta2.subtract(theta.multiply(5)).add(1.0));
            final T coeff2 = c23.multiply(this.one_minus_inv_sqrt_2);
            final T coeff3 = c23.multiply(this.one_plus_inv_sqrt_2);
            final T coeff4 = s.multiply(fourTheta2.add(theta).add(1.0));
            interpolatedState = this.currentStateLinearCombination(coeff1, coeff2, coeff3, coeff4);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot1, coeffDot2, coeffDot3, coeffDot4);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
