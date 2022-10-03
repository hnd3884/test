package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;

@Deprecated
public class NewtonSolver extends AbstractDifferentiableUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public NewtonSolver() {
        this(1.0E-6);
    }
    
    public NewtonSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    @Override
    public double solve(final int maxEval, final DifferentiableUnivariateFunction f, final double min, final double max) throws TooManyEvaluationsException {
        return super.solve(maxEval, f, UnivariateSolverUtils.midpoint(min, max));
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException {
        final double startValue = this.getStartValue();
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        double x0 = startValue;
        double x2;
        while (true) {
            x2 = x0 - this.computeObjectiveValue(x0) / this.computeDerivativeObjectiveValue(x0);
            if (FastMath.abs(x2 - x0) <= absoluteAccuracy) {
                break;
            }
            x0 = x2;
        }
        return x2;
    }
}
