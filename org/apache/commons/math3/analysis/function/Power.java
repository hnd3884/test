package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Power implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double p;
    
    public Power(final double p) {
        this.p = p;
    }
    
    public double value(final double x) {
        return FastMath.pow(x, this.p);
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    public DerivativeStructure value(final DerivativeStructure t) {
        return t.pow(this.p);
    }
}
