package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;

@Deprecated
public abstract class AbstractDifferentiableUnivariateSolver extends BaseAbstractUnivariateSolver<DifferentiableUnivariateFunction> implements DifferentiableUnivariateSolver
{
    private UnivariateFunction functionDerivative;
    
    protected AbstractDifferentiableUnivariateSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    protected AbstractDifferentiableUnivariateSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }
    
    protected double computeDerivativeObjectiveValue(final double point) throws TooManyEvaluationsException {
        this.incrementEvaluationCount();
        return this.functionDerivative.value(point);
    }
    
    @Override
    protected void setup(final int maxEval, final DifferentiableUnivariateFunction f, final double min, final double max, final double startValue) {
        super.setup(maxEval, f, min, max, startValue);
        this.functionDerivative = f.derivative();
    }
}
