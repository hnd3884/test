package org.apache.commons.math3.fitting;

import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

@Deprecated
public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric>
{
    public PolynomialFitter(final MultivariateVectorOptimizer optimizer) {
        super(optimizer);
    }
    
    public double[] fit(final int maxEval, final double[] guess) {
        return this.fit(maxEval, new PolynomialFunction.Parametric(), guess);
    }
    
    public double[] fit(final double[] guess) {
        return this.fit(new PolynomialFunction.Parametric(), guess);
    }
}
