package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class NewtonRaphsonSolver extends AbstractUnivariateDifferentiableSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public NewtonRaphsonSolver() {
        this(1.0E-6);
    }
    
    public NewtonRaphsonSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    @Override
    public double solve(final int maxEval, final UnivariateDifferentiableFunction f, final double min, final double max) throws TooManyEvaluationsException {
        return super.solve(maxEval, f, UnivariateSolverUtils.midpoint(min, max));
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException {
        final double startValue = this.getStartValue();
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        double x0 = startValue;
        double x2;
        while (true) {
            final DerivativeStructure y0 = this.computeObjectiveValueAndDerivative(x0);
            x2 = x0 - y0.getValue() / y0.getPartialDerivative(1);
            if (FastMath.abs(x2 - x0) <= absoluteAccuracy) {
                break;
            }
            x0 = x2;
        }
        return x2;
    }
}
