package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.RealFieldElement;

abstract class RungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>> extends AbstractFieldStepInterpolator<T>
{
    private final Field<T> field;
    private final T[][] yDotK;
    
    protected RungeKuttaFieldStepInterpolator(final Field<T> field, final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldODEStateAndDerivative<T> softPreviousState, final FieldODEStateAndDerivative<T> softCurrentState, final FieldEquationsMapper<T> mapper) {
        super(forward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        this.field = field;
        this.yDotK = MathArrays.buildArray(field, yDotK.length, -1);
        for (int i = 0; i < yDotK.length; ++i) {
            this.yDotK[i] = yDotK[i].clone();
        }
    }
    
    @Override
    protected RungeKuttaFieldStepInterpolator<T> create(final boolean newForward, final FieldODEStateAndDerivative<T> newGlobalPreviousState, final FieldODEStateAndDerivative<T> newGlobalCurrentState, final FieldODEStateAndDerivative<T> newSoftPreviousState, final FieldODEStateAndDerivative<T> newSoftCurrentState, final FieldEquationsMapper<T> newMapper) {
        return this.create(this.field, newForward, this.yDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }
    
    protected abstract RungeKuttaFieldStepInterpolator<T> create(final Field<T> p0, final boolean p1, final T[][] p2, final FieldODEStateAndDerivative<T> p3, final FieldODEStateAndDerivative<T> p4, final FieldODEStateAndDerivative<T> p5, final FieldODEStateAndDerivative<T> p6, final FieldEquationsMapper<T> p7);
    
    protected final T[] previousStateLinearCombination(final T... coefficients) {
        return this.combine(this.getPreviousState().getState(), coefficients);
    }
    
    protected T[] currentStateLinearCombination(final T... coefficients) {
        return this.combine(this.getCurrentState().getState(), coefficients);
    }
    
    protected T[] derivativeLinearCombination(final T... coefficients) {
        return this.combine(MathArrays.buildArray(this.field, this.yDotK[0].length), coefficients);
    }
    
    private T[] combine(final T[] a, final T... coefficients) {
        for (int i = 0; i < a.length; ++i) {
            for (int k = 0; k < coefficients.length; ++k) {
                a[i] = a[i].add(coefficients[k].multiply(this.yDotK[k][i]));
            }
        }
        return a;
    }
}
