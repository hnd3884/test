package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class MullerSolver extends AbstractUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public MullerSolver() {
        this(1.0E-6);
    }
    
    public MullerSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public MullerSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        final double min = this.getMin();
        final double max = this.getMax();
        final double initial = this.getStartValue();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        this.verifySequence(min, initial, max);
        final double fMin = this.computeObjectiveValue(min);
        if (FastMath.abs(fMin) < functionValueAccuracy) {
            return min;
        }
        final double fMax = this.computeObjectiveValue(max);
        if (FastMath.abs(fMax) < functionValueAccuracy) {
            return max;
        }
        final double fInitial = this.computeObjectiveValue(initial);
        if (FastMath.abs(fInitial) < functionValueAccuracy) {
            return initial;
        }
        this.verifyBracketing(min, max);
        if (this.isBracketing(min, initial)) {
            return this.solve(min, initial, fMin, fInitial);
        }
        return this.solve(initial, max, fInitial, fMax);
    }
    
    private double solve(final double min, final double max, final double fMin, final double fMax) throws TooManyEvaluationsException {
        final double relativeAccuracy = this.getRelativeAccuracy();
        final double absoluteAccuracy = this.getAbsoluteAccuracy();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        double x0 = min;
        double y0 = fMin;
        double x2 = max;
        double y2 = fMax;
        double x3 = 0.5 * (x0 + x2);
        double y3 = this.computeObjectiveValue(x3);
        double oldx = Double.POSITIVE_INFINITY;
        double x4;
        while (true) {
            final double d01 = (y3 - y0) / (x3 - x0);
            final double d2 = (y2 - y3) / (x2 - x3);
            final double d3 = (d2 - d01) / (x2 - x0);
            final double c1 = d01 + (x3 - x0) * d3;
            final double delta = c1 * c1 - 4.0 * y3 * d3;
            final double xplus = x3 + -2.0 * y3 / (c1 + FastMath.sqrt(delta));
            final double xminus = x3 + -2.0 * y3 / (c1 - FastMath.sqrt(delta));
            x4 = (this.isSequence(x0, xplus, x2) ? xplus : xminus);
            final double y4 = this.computeObjectiveValue(x4);
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x4), absoluteAccuracy);
            if (FastMath.abs(x4 - oldx) <= tolerance || FastMath.abs(y4) <= functionValueAccuracy) {
                break;
            }
            final boolean bisect = (x4 < x3 && x3 - x0 > 0.95 * (x2 - x0)) || (x4 > x3 && x2 - x3 > 0.95 * (x2 - x0)) || x4 == x3;
            if (!bisect) {
                x0 = ((x4 < x3) ? x0 : x3);
                y0 = ((x4 < x3) ? y0 : y3);
                x2 = ((x4 > x3) ? x2 : x3);
                y2 = ((x4 > x3) ? y2 : y3);
                x3 = x4;
                y3 = y4;
                oldx = x4;
            }
            else {
                final double xm = 0.5 * (x0 + x2);
                final double ym = this.computeObjectiveValue(xm);
                if (FastMath.signum(y0) + FastMath.signum(ym) == 0.0) {
                    x2 = xm;
                    y2 = ym;
                }
                else {
                    x0 = xm;
                    y0 = ym;
                }
                x3 = 0.5 * (x0 + x2);
                y3 = this.computeObjectiveValue(x3);
                oldx = Double.POSITIVE_INFINITY;
            }
        }
        return x4;
    }
}
