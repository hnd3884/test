package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

class DormandPrince54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T>
{
    private final T a70;
    private final T a72;
    private final T a73;
    private final T a74;
    private final T a75;
    private final T d0;
    private final T d2;
    private final T d3;
    private final T d4;
    private final T d5;
    private final T d6;
    
    DormandPrince54FieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        final T one = field.getOne();
        this.a70 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(35.0).divide(384.0);
        this.a72 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(500.0).divide(1113.0);
        this.a73 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(125.0).divide(192.0);
        this.a74 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(-2187.0).divide(6784.0);
        this.a75 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(11.0).divide(84.0);
        this.d0 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(-1.2715105075E10).divide(1.1282082432E10);
        this.d2 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(8.74874797E10).divide(3.2700410799E10);
        this.d3 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(-1.0690763975E10).divide(1.880347072E9);
        this.d4 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(7.01980252875E11).divide(1.99316789632E11);
        this.d5 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(-1.453857185E9).divide(8.22651844E8);
        this.d6 = ((RealFieldElement<RealFieldElement<T>>)one).multiply(6.9997945E7).divide(2.9380423E7);
    }
    
    @Override
    protected DormandPrince54FieldStepInterpolator<T> create(final Field<T> newField, final boolean newForward, final T[][] newYDotK, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return new DormandPrince54FieldStepInterpolator<T>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    @Override
    protected FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(final FieldEquationsMapper<T> mapper, final T time, final T theta, final T thetaH, final T oneMinusThetaH) {
        final T one = time.getField().getOne();
        final T eta = one.subtract(theta);
        final T twoTheta = theta.multiply(2);
        final T dot2 = one.subtract(twoTheta);
        final T dot3 = theta.multiply(((FieldElement<RealFieldElement<T>>)theta).multiply(-3).add(2.0));
        final T dot4 = twoTheta.multiply(theta.multiply(twoTheta.subtract(3.0)).add(1.0));
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        if (this.getGlobalPreviousState() != null && theta.getReal() <= 0.5) {
            final T f1 = thetaH;
            final T f2 = f1.multiply(eta);
            final T f3 = f2.multiply(theta);
            final T f4 = f3.multiply(eta);
            final T coeff0 = f1.multiply(this.a70).subtract(f2.multiply(this.a70.subtract(1.0))).add(f3.multiply(((FieldElement<RealFieldElement<T>>)this.a70).multiply(2).subtract(1.0))).add(f4.multiply(this.d0));
            final T coeff2 = time.getField().getZero();
            final T coeff3 = f1.multiply(this.a72).subtract(f2.multiply(this.a72)).add(f3.multiply(this.a72.multiply(2))).add(f4.multiply(this.d2));
            final T coeff4 = f1.multiply(this.a73).subtract(f2.multiply(this.a73)).add(f3.multiply(this.a73.multiply(2))).add(f4.multiply(this.d3));
            final T coeff5 = f1.multiply(this.a74).subtract(f2.multiply(this.a74)).add(f3.multiply(this.a74.multiply(2))).add(f4.multiply(this.d4));
            final T coeff6 = f1.multiply(this.a75).subtract(f2.multiply(this.a75)).add(f3.multiply(this.a75.multiply(2))).add(f4.multiply(this.d5));
            final T coeff7 = f4.multiply(this.d6).subtract(f3);
            final T coeffDot0 = this.a70.subtract(dot2.multiply(this.a70.subtract(1.0))).add(dot3.multiply(((FieldElement<RealFieldElement<T>>)this.a70).multiply(2).subtract(1.0))).add(dot4.multiply(this.d0));
            final T coeffDot2 = time.getField().getZero();
            final T coeffDot3 = this.a72.subtract(dot2.multiply(this.a72)).add(dot3.multiply(this.a72.multiply(2))).add(dot4.multiply(this.d2));
            final T coeffDot4 = this.a73.subtract(dot2.multiply(this.a73)).add(dot3.multiply(this.a73.multiply(2))).add(dot4.multiply(this.d3));
            final T coeffDot5 = this.a74.subtract(dot2.multiply(this.a74)).add(dot3.multiply(this.a74.multiply(2))).add(dot4.multiply(this.d4));
            final T coeffDot6 = this.a75.subtract(dot2.multiply(this.a75)).add(dot3.multiply(this.a75.multiply(2))).add(dot4.multiply(this.d5));
            final T coeffDot7 = dot4.multiply(this.d6).subtract(dot3);
            interpolatedState = this.previousStateLinearCombination(coeff0, coeff2, coeff3, coeff4, coeff5, coeff6, coeff7);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot0, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6, coeffDot7);
        }
        else {
            final T f1 = oneMinusThetaH.negate();
            final T f2 = oneMinusThetaH.multiply(theta);
            final T f3 = f2.multiply(theta);
            final T f4 = f3.multiply(eta);
            final T coeff0 = f1.multiply(this.a70).subtract(f2.multiply(this.a70.subtract(1.0))).add(f3.multiply(((FieldElement<RealFieldElement<T>>)this.a70).multiply(2).subtract(1.0))).add(f4.multiply(this.d0));
            final T coeff2 = time.getField().getZero();
            final T coeff3 = f1.multiply(this.a72).subtract(f2.multiply(this.a72)).add(f3.multiply(this.a72.multiply(2))).add(f4.multiply(this.d2));
            final T coeff4 = f1.multiply(this.a73).subtract(f2.multiply(this.a73)).add(f3.multiply(this.a73.multiply(2))).add(f4.multiply(this.d3));
            final T coeff5 = f1.multiply(this.a74).subtract(f2.multiply(this.a74)).add(f3.multiply(this.a74.multiply(2))).add(f4.multiply(this.d4));
            final T coeff6 = f1.multiply(this.a75).subtract(f2.multiply(this.a75)).add(f3.multiply(this.a75.multiply(2))).add(f4.multiply(this.d5));
            final T coeff7 = f4.multiply(this.d6).subtract(f3);
            final T coeffDot0 = this.a70.subtract(dot2.multiply(this.a70.subtract(1.0))).add(dot3.multiply(((FieldElement<RealFieldElement<T>>)this.a70).multiply(2).subtract(1.0))).add(dot4.multiply(this.d0));
            final T coeffDot2 = time.getField().getZero();
            final T coeffDot3 = this.a72.subtract(dot2.multiply(this.a72)).add(dot3.multiply(this.a72.multiply(2))).add(dot4.multiply(this.d2));
            final T coeffDot4 = this.a73.subtract(dot2.multiply(this.a73)).add(dot3.multiply(this.a73.multiply(2))).add(dot4.multiply(this.d3));
            final T coeffDot5 = this.a74.subtract(dot2.multiply(this.a74)).add(dot3.multiply(this.a74.multiply(2))).add(dot4.multiply(this.d4));
            final T coeffDot6 = this.a75.subtract(dot2.multiply(this.a75)).add(dot3.multiply(this.a75.multiply(2))).add(dot4.multiply(this.d5));
            final T coeffDot7 = dot4.multiply(this.d6).subtract(dot3);
            interpolatedState = this.currentStateLinearCombination(coeff0, coeff2, coeff3, coeff4, coeff5, coeff6, coeff7);
            interpolatedDerivatives = this.derivativeLinearCombination(coeffDot0, coeffDot2, coeffDot3, coeffDot4, coeffDot5, coeffDot6, coeffDot7);
        }
        return new FieldODEStateAndDerivative<T>(time, interpolatedState, interpolatedDerivatives);
    }
}
