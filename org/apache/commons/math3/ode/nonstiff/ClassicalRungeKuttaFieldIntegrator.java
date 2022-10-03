package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class ClassicalRungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T>
{
    public ClassicalRungeKuttaFieldIntegrator(final Field<T> field, final T step) {
        super(field, "classical Runge-Kutta", step);
    }
    
    public T[] getC() {
        final T[] c = MathArrays.buildArray(this.getField(), 3);
        c[1] = (c[0] = this.getField().getOne().multiply(0.5));
        c[2] = this.getField().getOne();
        return c;
    }
    
    public T[][] getA() {
        final T[][] a = MathArrays.buildArray(this.getField(), 3, -1);
        for (int i = 0; i < a.length; ++i) {
            a[i] = MathArrays.buildArray(this.getField(), i + 1);
        }
        a[0][0] = this.fraction(1, 2);
        a[1][0] = this.getField().getZero();
        a[1][1] = a[0][0];
        a[2][0] = this.getField().getZero();
        a[2][1] = this.getField().getZero();
        a[2][2] = this.getField().getOne();
        return a;
    }
    
    public T[] getB() {
        final T[] b = MathArrays.buildArray(this.getField(), 4);
        b[0] = this.fraction(1, 6);
        b[2] = (b[1] = this.fraction(1, 3));
        b[3] = b[0];
        return b;
    }
    
    @Override
    protected ClassicalRungeKuttaFieldStepInterpolator<T> createInterpolator(final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> mapper) {
        return new ClassicalRungeKuttaFieldStepInterpolator<T>(this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
