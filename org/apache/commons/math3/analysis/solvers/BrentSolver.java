package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.util.FastMath;

public class BrentSolver extends AbstractUnivariateSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    
    public BrentSolver() {
        this(1.0E-6);
    }
    
    public BrentSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }
    
    public BrentSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }
    
    public BrentSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }
    
    @Override
    protected double doSolve() throws NoBracketingException, TooManyEvaluationsException, NumberIsTooLargeException {
        final double min = this.getMin();
        final double max = this.getMax();
        final double initial = this.getStartValue();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        this.verifySequence(min, initial, max);
        final double yInitial = this.computeObjectiveValue(initial);
        if (FastMath.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }
        final double yMin = this.computeObjectiveValue(min);
        if (FastMath.abs(yMin) <= functionValueAccuracy) {
            return min;
        }
        if (yInitial * yMin < 0.0) {
            return this.brent(min, initial, yMin, yInitial);
        }
        final double yMax = this.computeObjectiveValue(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            return max;
        }
        if (yInitial * yMax < 0.0) {
            return this.brent(initial, max, yInitial, yMax);
        }
        throw new NoBracketingException(min, max, yMin, yMax);
    }
    
    private double brent(final double lo, final double hi, final double fLo, final double fHi) {
        double a = lo;
        double fa = fLo;
        double b = hi;
        double fb = fHi;
        double c = a;
        double fc = fa;
        double e;
        double d = e = b - a;
        final double t = this.getAbsoluteAccuracy();
        final double eps = this.getRelativeAccuracy();
        while (true) {
            if (FastMath.abs(fc) < FastMath.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }
            final double tol = 2.0 * eps * FastMath.abs(b) + t;
            final double m = 0.5 * (c - b);
            if (FastMath.abs(m) <= tol || Precision.equals(fb, 0.0)) {
                break;
            }
            if (FastMath.abs(e) < tol || FastMath.abs(fa) <= FastMath.abs(fb)) {
                d = (e = m);
            }
            else {
                double s = fb / fa;
                double p;
                double q;
                if (a == c) {
                    p = 2.0 * m * s;
                    q = 1.0 - s;
                }
                else {
                    q = fa / fc;
                    final double r = fb / fc;
                    p = s * (2.0 * m * q * (q - r) - (b - a) * (r - 1.0));
                    q = (q - 1.0) * (r - 1.0) * (s - 1.0);
                }
                if (p > 0.0) {
                    q = -q;
                }
                else {
                    p = -p;
                }
                s = e;
                e = d;
                if (p >= 1.5 * m * q - FastMath.abs(tol * q) || p >= FastMath.abs(0.5 * s * q)) {
                    d = (e = m);
                }
                else {
                    d = p / q;
                }
            }
            a = b;
            fa = fb;
            if (FastMath.abs(d) > tol) {
                b += d;
            }
            else if (m > 0.0) {
                b += tol;
            }
            else {
                b -= tol;
            }
            fb = this.computeObjectiveValue(b);
            if ((fb <= 0.0 || fc <= 0.0) && (fb > 0.0 || fc > 0.0)) {
                continue;
            }
            c = a;
            fc = fa;
            d = (e = b - a);
        }
        return b;
    }
}
