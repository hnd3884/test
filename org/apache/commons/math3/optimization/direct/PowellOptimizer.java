package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.univariate.SimpleUnivariateValueChecker;
import org.apache.commons.math3.optimization.univariate.BracketFinder;
import org.apache.commons.math3.optimization.univariate.BrentOptimizer;
import org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.MultivariateOptimizer;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class PowellOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateFunction> implements MultivariateOptimizer
{
    private static final double MIN_RELATIVE_TOLERANCE;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    private final LineSearch line;
    
    public PowellOptimizer(final double rel, final double abs, final ConvergenceChecker<PointValuePair> checker) {
        this(rel, abs, FastMath.sqrt(rel), FastMath.sqrt(abs), checker);
    }
    
    public PowellOptimizer(final double rel, final double abs, final double lineRel, final double lineAbs, final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
        if (rel < PowellOptimizer.MIN_RELATIVE_TOLERANCE) {
            throw new NumberIsTooSmallException(rel, PowellOptimizer.MIN_RELATIVE_TOLERANCE, true);
        }
        if (abs <= 0.0) {
            throw new NotStrictlyPositiveException(abs);
        }
        this.relativeThreshold = rel;
        this.absoluteThreshold = abs;
        this.line = new LineSearch(lineRel, lineAbs);
    }
    
    public PowellOptimizer(final double rel, final double abs) {
        this(rel, abs, null);
    }
    
    public PowellOptimizer(final double rel, final double abs, final double lineRel, final double lineAbs) {
        this(rel, abs, lineRel, lineAbs, null);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        final GoalType goal = this.getGoalType();
        final double[] guess = this.getStartPoint();
        final int n = guess.length;
        final double[][] direc = new double[n][n];
        for (int i = 0; i < n; ++i) {
            direc[i][i] = 1.0;
        }
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        double[] x = guess;
        double fVal = this.computeObjectiveValue(x);
        double[] x2 = x.clone();
        int iter = 0;
        double fX;
        PointValuePair current;
        while (true) {
            ++iter;
            fX = fVal;
            double fX2 = 0.0;
            double delta = 0.0;
            int bigInd = 0;
            double alphaMin = 0.0;
            for (int j = 0; j < n; ++j) {
                final double[] d = MathArrays.copyOf(direc[j]);
                fX2 = fVal;
                final UnivariatePointValuePair optimum = this.line.search(x, d);
                fVal = optimum.getValue();
                alphaMin = optimum.getPoint();
                final double[][] result = this.newPointAndDirection(x, d, alphaMin);
                x = result[0];
                if (fX2 - fVal > delta) {
                    delta = fX2 - fVal;
                    bigInd = j;
                }
            }
            boolean stop = 2.0 * (fX - fVal) <= this.relativeThreshold * (FastMath.abs(fX) + FastMath.abs(fVal)) + this.absoluteThreshold;
            final PointValuePair previous = new PointValuePair(x2, fX);
            current = new PointValuePair(x, fVal);
            if (!stop && checker != null) {
                stop = checker.converged(iter, previous, current);
            }
            if (stop) {
                break;
            }
            final double[] d2 = new double[n];
            final double[] x3 = new double[n];
            for (int k = 0; k < n; ++k) {
                d2[k] = x[k] - x2[k];
                x3[k] = 2.0 * x[k] - x2[k];
            }
            x2 = x.clone();
            fX2 = this.computeObjectiveValue(x3);
            if (fX <= fX2) {
                continue;
            }
            double t = 2.0 * (fX + fX2 - 2.0 * fVal);
            double temp = fX - fVal - delta;
            t *= temp * temp;
            temp = fX - fX2;
            t -= delta * temp * temp;
            if (t >= 0.0) {
                continue;
            }
            final UnivariatePointValuePair optimum2 = this.line.search(x, d2);
            fVal = optimum2.getValue();
            alphaMin = optimum2.getPoint();
            final double[][] result2 = this.newPointAndDirection(x, d2, alphaMin);
            x = result2[0];
            final int lastInd = n - 1;
            direc[bigInd] = direc[lastInd];
            direc[lastInd] = result2[1];
        }
        if (goal == GoalType.MINIMIZE) {
            final PointValuePair previous;
            return (fVal < fX) ? current : previous;
        }
        PointValuePair previous;
        return (fVal > fX) ? current : previous;
    }
    
    private double[][] newPointAndDirection(final double[] p, final double[] d, final double optimum) {
        final int n = p.length;
        final double[] nP = new double[n];
        final double[] nD = new double[n];
        for (int i = 0; i < n; ++i) {
            nD[i] = d[i] * optimum;
            nP[i] = p[i] + nD[i];
        }
        final double[][] result = { nP, nD };
        return result;
    }
    
    static {
        MIN_RELATIVE_TOLERANCE = 2.0 * FastMath.ulp(1.0);
    }
    
    private class LineSearch extends BrentOptimizer
    {
        private static final double REL_TOL_UNUSED = 1.0E-15;
        private static final double ABS_TOL_UNUSED = Double.MIN_VALUE;
        private final BracketFinder bracket;
        
        LineSearch(final double rel, final double abs) {
            super(1.0E-15, Double.MIN_VALUE, new SimpleUnivariateValueChecker(rel, abs));
            this.bracket = new BracketFinder();
        }
        
        public UnivariatePointValuePair search(final double[] p, final double[] d) {
            final int n = p.length;
            final UnivariateFunction f = new UnivariateFunction() {
                public double value(final double alpha) {
                    final double[] x = new double[n];
                    for (int i = 0; i < n; ++i) {
                        x[i] = p[i] + alpha * d[i];
                    }
                    final double obj = PowellOptimizer.this.computeObjectiveValue(x);
                    return obj;
                }
            };
            final GoalType goal = PowellOptimizer.this.getGoalType();
            this.bracket.search(f, goal, 0.0, 1.0);
            return this.optimize(Integer.MAX_VALUE, f, goal, this.bracket.getLo(), this.bracket.getHi(), this.bracket.getMid());
        }
    }
}
