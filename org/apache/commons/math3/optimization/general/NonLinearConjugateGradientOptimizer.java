package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;

@Deprecated
public class NonLinearConjugateGradientOptimizer extends AbstractScalarDifferentiableOptimizer
{
    private final ConjugateGradientFormula updateFormula;
    private final Preconditioner preconditioner;
    private final UnivariateSolver solver;
    private double initialStep;
    private double[] point;
    
    @Deprecated
    public NonLinearConjugateGradientOptimizer(final ConjugateGradientFormula updateFormula) {
        this(updateFormula, new SimpleValueChecker());
    }
    
    public NonLinearConjugateGradientOptimizer(final ConjugateGradientFormula updateFormula, final ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula, checker, new BrentSolver(), new IdentityPreconditioner());
    }
    
    public NonLinearConjugateGradientOptimizer(final ConjugateGradientFormula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver) {
        this(updateFormula, checker, lineSearchSolver, new IdentityPreconditioner());
    }
    
    public NonLinearConjugateGradientOptimizer(final ConjugateGradientFormula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver, final Preconditioner preconditioner) {
        super(checker);
        this.updateFormula = updateFormula;
        this.solver = lineSearchSolver;
        this.preconditioner = preconditioner;
        this.initialStep = 1.0;
    }
    
    public void setInitialStep(final double initialStep) {
        if (initialStep <= 0.0) {
            this.initialStep = 1.0;
        }
        else {
            this.initialStep = initialStep;
        }
    }
    
    @Override
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        this.point = this.getStartPoint();
        final GoalType goal = this.getGoalType();
        final int n = this.point.length;
        double[] r = this.computeObjectiveGradient(this.point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; ++i) {
                r[i] = -r[i];
            }
        }
        double[] steepestDescent = this.preconditioner.precondition(this.point, r);
        double[] searchDirection = steepestDescent.clone();
        double delta = 0.0;
        for (int j = 0; j < n; ++j) {
            delta += r[j] * searchDirection[j];
        }
        PointValuePair current = null;
        int iter = 0;
        int maxEval = this.getMaxEvaluations();
        while (true) {
            ++iter;
            final double objective = this.computeObjectiveValue(this.point);
            final PointValuePair previous = current;
            current = new PointValuePair(this.point, objective);
            if (previous != null && checker.converged(iter, previous, current)) {
                break;
            }
            final UnivariateFunction lsf = new LineSearchFunction(searchDirection);
            final double uB = this.findUpperBound(lsf, 0.0, this.initialStep);
            final double step = this.solver.solve(maxEval, lsf, 0.0, uB, 1.0E-15);
            maxEval -= this.solver.getEvaluations();
            for (int k = 0; k < this.point.length; ++k) {
                final double[] point = this.point;
                final int n2 = k;
                point[n2] += step * searchDirection[k];
            }
            r = this.computeObjectiveGradient(this.point);
            if (goal == GoalType.MINIMIZE) {
                for (int k = 0; k < n; ++k) {
                    r[k] = -r[k];
                }
            }
            final double deltaOld = delta;
            final double[] newSteepestDescent = this.preconditioner.precondition(this.point, r);
            delta = 0.0;
            for (int l = 0; l < n; ++l) {
                delta += r[l] * newSteepestDescent[l];
            }
            double beta;
            if (this.updateFormula == ConjugateGradientFormula.FLETCHER_REEVES) {
                beta = delta / deltaOld;
            }
            else {
                double deltaMid = 0.0;
                for (int m = 0; m < r.length; ++m) {
                    deltaMid += r[m] * steepestDescent[m];
                }
                beta = (delta - deltaMid) / deltaOld;
            }
            steepestDescent = newSteepestDescent;
            if (iter % n == 0 || beta < 0.0) {
                searchDirection = steepestDescent.clone();
            }
            else {
                for (int i2 = 0; i2 < n; ++i2) {
                    searchDirection[i2] = steepestDescent[i2] + beta * searchDirection[i2];
                }
            }
        }
        return current;
    }
    
    private double findUpperBound(final UnivariateFunction f, final double a, final double h) {
        double yB;
        for (double yA = yB = f.value(a), step = h; step < Double.MAX_VALUE; step *= FastMath.max(2.0, yA / yB)) {
            final double b = a + step;
            yB = f.value(b);
            if (yA * yB <= 0.0) {
                return b;
            }
        }
        throw new MathIllegalStateException(LocalizedFormats.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH, new Object[0]);
    }
    
    public static class IdentityPreconditioner implements Preconditioner
    {
        public double[] precondition(final double[] variables, final double[] r) {
            return r.clone();
        }
    }
    
    private class LineSearchFunction implements UnivariateFunction
    {
        private final double[] searchDirection;
        
        LineSearchFunction(final double[] searchDirection) {
            this.searchDirection = searchDirection;
        }
        
        public double value(final double x) {
            final double[] shiftedPoint = NonLinearConjugateGradientOptimizer.this.point.clone();
            for (int i = 0; i < shiftedPoint.length; ++i) {
                final double[] array = shiftedPoint;
                final int n = i;
                array[n] += x * this.searchDirection[i];
            }
            final double[] gradient = NonLinearConjugateGradientOptimizer.this.computeObjectiveGradient(shiftedPoint);
            double dotProduct = 0.0;
            for (int j = 0; j < gradient.length; ++j) {
                dotProduct += gradient[j] * this.searchDirection[j];
            }
            return dotProduct;
        }
    }
}
