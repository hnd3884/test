package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class GillFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T>
{
    public GillFieldIntegrator(final Field<T> field, final T step) {
        super(field, "Gill", step);
    }
    
    public T[] getC() {
        final T[] c = MathArrays.buildArray(this.getField(), 3);
        c[1] = (c[0] = this.fraction(1, 2));
        c[2] = this.getField().getOne();
        return c;
    }
    
    public T[][] getA() {
        final T two = this.getField().getZero().add(2.0);
        final T sqrtTwo = two.sqrt();
        final T[][] a = MathArrays.buildArray(this.getField(), 3, -1);
        for (int i = 0; i < a.length; ++i) {
            a[i] = MathArrays.buildArray(this.getField(), i + 1);
        }
        a[0][0] = this.fraction(1, 2);
        a[1][0] = ((RealFieldElement<RealFieldElement<T>>)sqrtTwo).subtract(1.0).multiply(0.5);
        a[1][1] = ((RealFieldElement<RealFieldElement<T>>)sqrtTwo).subtract(2.0).multiply(-0.5);
        a[2][0] = this.getField().getZero();
        a[2][1] = sqrtTwo.multiply(-0.5);
        a[2][2] = ((RealFieldElement<RealFieldElement<T>>)sqrtTwo).add(2.0).multiply(0.5);
        return a;
    }
    
    public T[] getB() {
        final T two = this.getField().getZero().add(2.0);
        final T sqrtTwo = two.sqrt();
        final T[] b = MathArrays.buildArray(this.getField(), 4);
        b[0] = this.fraction(1, 6);
        b[1] = ((RealFieldElement<RealFieldElement<T>>)sqrtTwo).subtract(2.0).divide(-6.0);
        b[2] = ((RealFieldElement<RealFieldElement<T>>)sqrtTwo).add(2.0).divide(6.0);
        b[3] = b[0];
        return b;
    }
    
    @Override
    protected GillFieldStepInterpolator<T> createInterpolator(final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> mapper) {
        return new GillFieldStepInterpolator<T>(this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
