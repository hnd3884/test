package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optim.ConvergenceChecker;

public class BrentOptimizer extends UnivariateOptimizer
{
    private static final double GOLDEN_SECTION;
    private static final double MIN_RELATIVE_TOLERANCE;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    
    public BrentOptimizer(final double rel, final double abs, final ConvergenceChecker<UnivariatePointValuePair> checker) {
        super(checker);
        if (rel < BrentOptimizer.MIN_RELATIVE_TOLERANCE) {
            throw new NumberIsTooSmallException(rel, BrentOptimizer.MIN_RELATIVE_TOLERANCE, true);
        }
        if (abs <= 0.0) {
            throw new NotStrictlyPositiveException(abs);
        }
        this.relativeThreshold = rel;
        this.absoluteThreshold = abs;
    }
    
    public BrentOptimizer(final double rel, final double abs) {
        this(rel, abs, null);
    }
    
    @Override
    protected UnivariatePointValuePair doOptimize() {
        final boolean isMinim = this.getGoalType() == GoalType.MINIMIZE;
        final double lo = this.getMin();
        final double mid = this.getStartValue();
        final double hi = this.getMax();
        final ConvergenceChecker<UnivariatePointValuePair> checker = this.getConvergenceChecker();
        double a;
        double b;
        if (lo < hi) {
            a = lo;
            b = hi;
        }
        else {
            a = hi;
            b = lo;
        }
        double v;
        double w;
        double x = w = (v = mid);
        double d = 0.0;
        double e = 0.0;
        double fx = this.computeObjectiveValue(x);
        if (!isMinim) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;
        UnivariatePointValuePair previous = null;
        UnivariatePointValuePair best;
        UnivariatePointValuePair current = best = new UnivariatePointValuePair(x, isMinim ? fx : (-fx));
        while (true) {
            final double m = 0.5 * (a + b);
            final double tol1 = this.relativeThreshold * FastMath.abs(x) + this.absoluteThreshold;
            final double tol2 = 2.0 * tol1;
            final boolean stop = FastMath.abs(x - m) <= tol2 - 0.5 * (b - a);
            if (stop) {
                return this.best(best, this.best(previous, current, isMinim), isMinim);
            }
            double p = 0.0;
            double q = 0.0;
            double r = 0.0;
            double u = 0.0;
            if (FastMath.abs(e) > tol1) {
                r = (x - w) * (fx - fv);
                q = (x - v) * (fx - fw);
                p = (x - v) * q - (x - w) * r;
                q = 2.0 * (q - r);
                if (q > 0.0) {
                    p = -p;
                }
                else {
                    q = -q;
                }
                r = e;
                e = d;
                if (p > q * (a - x) && p < q * (b - x) && FastMath.abs(p) < FastMath.abs(0.5 * q * r)) {
                    d = p / q;
                    u = x + d;
                    if (u - a < tol2 || b - u < tol2) {
                        if (x <= m) {
                            d = tol1;
                        }
                        else {
                            d = -tol1;
                        }
                    }
                }
                else {
                    if (x < m) {
                        e = b - x;
                    }
                    else {
                        e = a - x;
                    }
                    d = BrentOptimizer.GOLDEN_SECTION * e;
                }
            }
            else {
                if (x < m) {
                    e = b - x;
                }
                else {
                    e = a - x;
                }
                d = BrentOptimizer.GOLDEN_SECTION * e;
            }
            if (FastMath.abs(d) < tol1) {
                if (d >= 0.0) {
                    u = x + tol1;
                }
                else {
                    u = x - tol1;
                }
            }
            else {
                u = x + d;
            }
            double fu = this.computeObjectiveValue(u);
            if (!isMinim) {
                fu = -fu;
            }
            previous = current;
            current = new UnivariatePointValuePair(u, isMinim ? fu : (-fu));
            best = this.best(best, this.best(previous, current, isMinim), isMinim);
            if (checker != null && checker.converged(this.getIterations(), previous, current)) {
                return best;
            }
            if (fu <= fx) {
                if (u < x) {
                    b = x;
                }
                else {
                    a = x;
                }
                v = w;
                fv = fw;
                w = x;
                fw = fx;
                x = u;
                fx = fu;
            }
            else {
                if (u < x) {
                    a = u;
                }
                else {
                    b = u;
                }
                if (fu <= fw || Precision.equals(w, x)) {
                    v = w;
                    fv = fw;
                    w = u;
                    fw = fu;
                }
                else if (fu <= fv || Precision.equals(v, x) || Precision.equals(v, w)) {
                    v = u;
                    fv = fu;
                }
            }
            this.incrementIterationCount();
        }
    }
    
    private UnivariatePointValuePair best(final UnivariatePointValuePair a, final UnivariatePointValuePair b, final boolean isMinim) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (isMinim) {
            return (a.getValue() <= b.getValue()) ? a : b;
        }
        return (a.getValue() >= b.getValue()) ? a : b;
    }
    
    static {
        GOLDEN_SECTION = 0.5 * (3.0 - FastMath.sqrt(5.0));
        MIN_RELATIVE_TOLERANCE = 2.0 * FastMath.ulp(1.0);
    }
}
