package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;

public class EulerFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T>
{
    public EulerFieldIntegrator(final Field<T> field, final T step) {
        super(field, "Euler", step);
    }
    
    public T[] getC() {
        return MathArrays.buildArray(this.getField(), 0);
    }
    
    public T[][] getA() {
        return MathArrays.buildArray(this.getField(), 0, 0);
    }
    
    public T[] getB() {
        final T[] b = MathArrays.buildArray(this.getField(), 1);
        b[0] = this.getField().getOne();
        return b;
    }
    
    @Override
    protected EulerFieldStepInterpolator<T> createInterpolator(final boolean forward, final T[][] yDotK, final FieldODEStateAndDerivative<T> globalPreviousState, final FieldODEStateAndDerivative<T> globalCurrentState, final FieldEquationsMapper<T> mapper) {
        return new EulerFieldStepInterpolator<T>(this.getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
