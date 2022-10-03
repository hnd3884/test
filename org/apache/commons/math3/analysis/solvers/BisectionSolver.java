package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class BisectionSolver extends AbstractUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public BisectionSolver() {
        this(1.0E-6);
    }
    
    public BisectionSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public BisectionSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException {
        double min = this.getMin();
        double max = this.getMax();
        this.verifyInterval(min, max);
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        do {
            final double m = UnivariateSolverUtils.midpoint(min, max);
            final double fmin = this.computeObjectiveValue(min);
            final double fm = this.computeObjectiveValue(m);
            if (fm * fmin > 0.0) {
                min = m;
            }
            else {
                max = m;
            }
        } while (FastMath.abs(max - min) > absoluteAccuracy);
        final double m = UnivariateSolverUtils.midpoint(min, max);
        return m;
    }
}
