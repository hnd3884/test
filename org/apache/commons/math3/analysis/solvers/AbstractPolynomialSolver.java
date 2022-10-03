package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public abstract class AbstractPolynomialSolver extends BaseAbstractUnivariateSolver<PolynomialFunction> implements PolynomialSolver
{
    private PolynomialFunction polynomialFunction;
    
    protected AbstractPolynomialSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    protected AbstractPolynomialSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    protected AbstractPolynomialSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }
    
    @Override
    protected void setup(final int maxEval, final PolynomialFunction f, final double min, final double max, final double startValue) {
        super.setup(maxEval, f, min, max, startValue);
        this.polynomialFunction = f;
    }
    
    protected double[] getCoefficients() {
        return this.polynomialFunction.getCoefficients();
    }
}
