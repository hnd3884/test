package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class RiddersSolver extends AbstractUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public RiddersSolver() {
        this(1.0E-6);
    }
    
    public RiddersSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public RiddersSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException, NoBracketingException {
        final double min = this.getMin();
        final double max = this.getMax();
        double x1 = min;
        double y1 = this.computeObjectiveValue(x1);
        double x2 = max;
        double y2 = this.computeObjectiveValue(x2);
        if (y1 == 0.0) {
            return min;
        }
        if (y2 == 0.0) {
            return max;
        }
        this.verifyBracketing(min, max);
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        final double relativeAccuracy = this.getRelativeAccuracy();
        double oldx = Double.POSITIVE_INFINITY;
        while (true) {
            final double x3 = 0.5 * (x1 + x2);
            final double y3 = this.computeObjectiveValue(x3);
            if (FastMath.abs(y3) <= functionValueAccuracy) {
                return x3;
            }
            final double delta = 1.0 - y1 * y2 / (y3 * y3);
            final double correction = FastMath.signum(y2) * FastMath.signum(y3) * (x3 - x1) / FastMath.sqrt(delta);
            final double x4 = x3 - correction;
            final double y4 = this.computeObjectiveValue(x4);
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x4), absoluteAccuracy);
            if (FastMath.abs(x4 - oldx) <= tolerance) {
                return x4;
            }
            if (FastMath.abs(y4) <= functionValueAccuracy) {
                return x4;
            }
            if (correction > 0.0) {
                if (FastMath.signum(y1) + FastMath.signum(y4) == 0.0) {
                    x2 = x4;
                    y2 = y4;
                }
                else {
                    x1 = x4;
                    x2 = x3;
                    y1 = y4;
                    y2 = y3;
                }
            }
            else if (FastMath.signum(y2) + FastMath.signum(y4) == 0.0) {
                x1 = x4;
                y1 = y4;
            }
            else {
                x1 = x3;
                x2 = x4;
                y1 = y3;
                y2 = y4;
            }
            oldx = x4;
        }
    }
}
