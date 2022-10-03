package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.LineSearch;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public class PowellOptimizer extends MultivariateOptimizer
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
        this.line = new LineSearch(this, lineRel, lineAbs, 1.0);
    }
    
    public PowellOptimizer(final double rel, final double abs) {
        this(rel, abs, null);
    }
    
    public PowellOptimizer(final double rel, final double abs, final double lineRel, final double lineAbs) {
        this(rel, abs, lineRel, lineAbs, null);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        this.checkParameters();
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
        double fX;
        PointValuePair current;
        while (true) {
            this.incrementIterationCount();
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
                stop = checker.converged(this.getIterations(), previous, current);
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
    
    private void checkParameters() {
        if (this.getLowerBound() != null || this.getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
    
    static {
        MIN_RELATIVE_TOLERANCE = 2.0 * FastMath.ulp(1.0);
    }
}
