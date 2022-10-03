package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Cosh implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    public double value(final double x) {
        return FastMath.cosh(x);
    }
    
    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Sinh();
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        return t.cosh();
    }
}
