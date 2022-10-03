package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.util.FastMath;

public class MullerSolver2 extends AbstractUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public MullerSolver2() {
        this(1.0E-6);
    }
    
    public MullerSolver2(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public MullerSolver2(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        final double min = this.getMin();
        final double max = this.getMax();
        this.verifyInterval(min, max);
        final double relativeAccuracy = this.getRelativeAccuracy();
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        double x0 = min;
        double y0 = this.computeObjectiveValue(x0);
        if (FastMath.abs(y0) < functionValueAccuracy) {
            return x0;
        }
        double x2 = max;
        double y2 = this.computeObjectiveValue(x2);
        if (FastMath.abs(y2) < functionValueAccuracy) {
            return x2;
        }
        if (y0 * y2 > 0.0) {
            throw new NoBracketingException(x0, x2, y0, y2);
        }
        double x3 = 0.5 * (x0 + x2);
        double y3 = this.computeObjectiveValue(x3);
        double oldx = Double.POSITIVE_INFINITY;
        double x4;
        while (true) {
            final double q = (x3 - x2) / (x2 - x0);
            final double a = q * (y3 - (1.0 + q) * y2 + q * y0);
            final double b = (2.0 * q + 1.0) * y3 - (1.0 + q) * (1.0 + q) * y2 + q * q * y0;
            final double c = (1.0 + q) * y3;
            final double delta = b * b - 4.0 * a * c;
            double denominator;
            if (delta >= 0.0) {
                final double dplus = b + FastMath.sqrt(delta);
                final double dminus = b - FastMath.sqrt(delta);
                denominator = ((FastMath.abs(dplus) > FastMath.abs(dminus)) ? dplus : dminus);
            }
            else {
                denominator = FastMath.sqrt(b * b - delta);
            }
            if (denominator != 0.0) {
                for (x4 = x3 - 2.0 * c * (x3 - x2) / denominator; x4 == x2 || x4 == x3; x4 += absoluteAccuracy) {}
            }
            else {
                x4 = min + FastMath.random() * (max - min);
                oldx = Double.POSITIVE_INFINITY;
            }
            final double y4 = this.computeObjectiveValue(x4);
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x4), absoluteAccuracy);
            if (FastMath.abs(x4 - oldx) <= tolerance || FastMath.abs(y4) <= functionValueAccuracy) {
                break;
            }
            x0 = x2;
            y0 = y2;
            x2 = x3;
            y2 = y3;
            x3 = x4;
            y3 = y4;
            oldx = x4;
        }
        return x4;
    }
}
