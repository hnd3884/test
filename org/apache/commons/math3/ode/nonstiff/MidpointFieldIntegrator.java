package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class MidpointFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T>
{
    public MidpointFieldIntegrator(final Field<T> field, final T step) {
        super(field, "midpoint", step);
    }
    
    public T[] getC() {
        final T[] c = MathArrays.buildArray(this.getField(), 1);
        c[0] = this.getField().getOne().multiply(0.5);
        return c;
    }
    
    public T[][] getA() {
        final T[][] a = MathArrays.buildArray(this.getField(), 1, 1);
        a[0][0] = this.fraction(1, 2);
        return a;
    }
    
    public T[] getB() {
        final T[] b = MathArrays.buildArray(this.getField(), 2);
        b[0] = this.getField().getZero();
        b[1] = this.getField().getOne();
        return b;
    }
    
    @Override
    protected MidpointFieldStepInterpolator<T> createInterpolator(final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> mapper) {
        return new MidpointFieldStepInterpolator<T>(this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
