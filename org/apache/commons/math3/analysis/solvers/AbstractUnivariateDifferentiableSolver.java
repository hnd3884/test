package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public abstract class AbstractUnivariateDifferentiableSolver extends BaseAbstractUnivariateSolver<UnivariateDifferentiableFunction> implements UnivariateDifferentiableSolver
{
    private UnivariateDifferentiableFunction function;
    
    protected AbstractUnivariateDifferentiableSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    protected AbstractUnivariateDifferentiableSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }
    
    protected DerivativeStructure computeObjectiveValueAndDerivative(final double point) throws TooManyEvaluationsException {
        this.incrementEvaluationCount();
        return this.function.value(new DerivativeStructure(1, 1, 0, point));
    }
    
    @Override
    protected void setup(final int maxEval, final UnivariateDifferentiableFunction f, final double min, final double max, final double startValue) {
        super.setup(maxEval, f, min, max, startValue);
        this.function = f;
    }
}
