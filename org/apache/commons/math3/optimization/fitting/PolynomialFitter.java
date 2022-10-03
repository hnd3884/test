package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

@Deprecated
public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric>
{
    @Deprecated
    private final int degree;
    
    @Deprecated
    public PolynomialFitter(final int degree, final DifferentiableMultivariateVectorOptimizer optimizer) {
        super(optimizer);
        this.degree = degree;
    }
    
    public PolynomialFitter(final DifferentiableMultivariateVectorOptimizer optimizer) {
        super(optimizer);
        this.degree = -1;
    }
    
    @Deprecated
    public double[] fit() {
        return this.fit(new PolynomialFunction.Parametric(), new double[this.degree + 1]);
    }
    
    public double[] fit(final int maxEval, final double[] guess) {
        return this.fit(maxEval, new PolynomialFunction.Parametric(), guess);
    }
    
    public double[] fit(final double[] guess) {
        return this.fit(new PolynomialFunction.Parametric(), guess);
    }
}
