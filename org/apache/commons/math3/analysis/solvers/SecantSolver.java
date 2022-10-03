package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class SecantSolver extends AbstractUnivariateSolver
{
    protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public SecantSolver() {
        super(1.0E-6);
    }
    
    public SecantSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public SecantSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    @Override
    protected final double doSolve() throws TooManyEvaluationsException, NoBracketingException {
        double x0 = this.getMin();
        double x2 = this.getMax();
        double f0 = this.computeObjectiveValue(x0);
        double f2 = this.computeObjectiveValue(x2);
        if (f0 == 0.0) {
            return x0;
        }
        if (f2 == 0.0) {
            return x2;
        }
        this.verifyBracketing(x0, x2);
        final double ftol = this.getFunctionValueAccuracy();
        final double atol = this.getAbsoluteAccuracy();
        final double rtol = this.getRelativeAccuracy();
        while (true) {
            final double x3 = x2 - f2 * (x2 - x0) / (f2 - f0);
            final double fx = this.computeObjectiveValue(x3);
            if (fx == 0.0) {
                return x3;
            }
            x0 = x2;
            f0 = f2;
            x2 = x3;
            f2 = fx;
            if (FastMath.abs(f2) <= ftol) {
                return x2;
            }
            if (FastMath.abs(x2 - x0) < FastMath.max(rtol * FastMath.abs(x2), atol)) {
                return x2;
            }
        }
    }
}
